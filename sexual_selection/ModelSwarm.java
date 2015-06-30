import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{	
	public int N; // N: 全個体数
	public double male_femaleRatio; // male_femaleRatio: 雌雄比(全個体数に対する雄個体の割合)
	public double tGeneRatio; // tGeneRatio: 全個体数に対するT1遺伝子個体の割合
	public double pGeneRatio; // pGeneRatio: 全個体数に対するP1遺伝子個体の割合
	
	public double a0Coeff, a1Coeff; // a0Coeff, a1Coeff: Kirkpatrickのモデルの雄選択時における確率式の係数
	public double maleDeathProb; // 雄の交配前死滅確率(すべての雄の交配前死滅確率のベース)
	public double femaleDeathProb; // FemaleProb:雌の交配前死滅確率
	public double sRatio; // T遺伝子雄の交配前死滅確率の比．T1遺伝子雄はT0遺伝子雄に比べて(1 - sRatio)倍の生存率．
	private double tMaleDeathProb[]; // tMaleDeathProb:T遺伝子ごとの雄の交配前死滅確率．

	private double[] BreedProb; // BreedProb: 雌雄がつがう確率（BreedProb[0] = P(T0|P0), BreedProb[1] = P(T1|P1)）
	private int[][][] figures; // figures: 個体の統計情報．figures[性別（0:雄，1:雌）][T遺伝子][P遺伝子]

	public int time; // time: 世代
	public int EndTime; // EndTime: 終了条件となる世代

	public int lifetime; // lifetime: 個体の寿命
	public int numChild; // numChild: 一度の交配で生む子供の数 
	
	List[] tMaleList; // tMaleList: T遺伝子ごとの雄個体リスト
    List femaleList; // femaleList: 雌個体リスト

	double v1, v2; // v1, v2: Kirkpatrickのモデルの平衡状態での遺伝子頻度の関係式のおけるV1, V2

	// デフォルトコンストラクタ
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		N = 1000;
		male_femaleRatio = 0.5;
		tGeneRatio = 0.5;
		pGeneRatio = 0.5;

		maleDeathProb = 0.0;
		sRatio = 0.2;
		femaleDeathProb = 0.0;

		a0Coeff = 2.0;
		a1Coeff = 3.0;
		EndTime = 100;
		lifetime = 1;
		numChild = 2;
	}
	
	// 初期化
	public Object buildObjects(){
		Male aMale;
		Female aFemale;

		int x, y, z;
		int tGene, pGene;

		tMaleList = new List[2];
		tMaleList[0] = new ListImpl(this);
		tMaleList[1] = new ListImpl(this);
		femaleList = new ListImpl(this);

		tMaleDeathProb = new double[2];
		tMaleDeathProb[0] = maleDeathProb;
		tMaleDeathProb[1] = (double)1 - ((1 - maleDeathProb) * (1 - sRatio));

		BreedProb = new double[2];

		time = 0;

		figures = new int[2][2][2];
		for(x = 0; x < 2; x++)
			for(y = 0; y < 2; y++)
				for(z = 0; z < 2; z++)
					figures[x][y][z] = 0;

		v1 = (a0Coeff + sRatio -1) / (a0Coeff * a1Coeff - 1) / (1 - sRatio);
		v2 = a1Coeff * (a0Coeff + sRatio -1) / (a0Coeff * a1Coeff - 1);

		// 個体の生成
		for (x = 0; x < N; x++){

			// T遺伝子決定
			if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < tGeneRatio) {
				tGene = 1;
			} else {
				tGene = 0;
			}

			// P遺伝子決定
			if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < pGeneRatio) {
				pGene = 1;
			} else {
				pGene = 0;
			}
			
			// 性別決定
			if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < male_femaleRatio) {
			
				// 雄個体の生成
				aMale = new Male(this, tGene, pGene);
				aMale.setDeathProb(tMaleDeathProb[tGene]);
				tMaleList[tGene].addLast(aMale);
				addFigures(0, tGene, pGene);

			} else {

				// 雌個体の生成
				aFemale = new Female(this, tGene, pGene);
				aFemale.setDeathProb(femaleDeathProb);
				femaleList.addLast(aFemale);
				addFigures(1, tGene, pGene);
			}	
		}
		System.out.println("Death Probability\nT0Male: " + tMaleDeathProb[0]
								+ "\tT1Male: " + tMaleDeathProb[1]
								+ "\tFemale: " + femaleDeathProb);
		calcBreedProb();
		showRatio();
		showCount();
		time++;
		return this;
	}
	
	// T遺伝子雄個体の総数を返す
	public int countTMale(int tGene){
		return tMaleList[tGene].getCount();
	}

	// 雌個体の総数を返す
	public int countFemale(){
		return femaleList.getCount();
	}


	// T0遺伝子の個体数を返す
	public int countT0Gene() {
		int x, y;
		int numT = 0;

		for(x = 0; x <= 1; x++)
			for(y = 0; y <= 1; y++)
				numT += figures[x][0][y];

		if(numT < 0) {
			System.out.println("T0MaleCount:" + countTMale(0) + "\tT1MaleCount:" + countTMale(1) + "\tFemaleCount: " + countFemale());
		}

		return numT;
	}

	// T1遺伝子の個体数を返す
	public int countT1Gene() {
		int x, y;
		int numT = 0;

		for(x = 0; x <= 1; x++)
			for(y = 0; y <= 1; y++)
				numT += figures[x][1][y];

		return numT;
	}

	// P0遺伝子の個体数を返す
	public int countP0Gene() {
		int x, y;
		int numP = 0;

		for(x = 0; x <= 1; x++)
			for(y = 0; y <= 1; y++)
				numP += figures[x][y][0];

		return numP;
	}

	// P1遺伝子の個体数を返す
	public int countP1Gene() {
		int x, y;
		int numP = 0;

		for(x = 0; x <= 1; x++)
			for(y = 0; y <= 1; y++)
				numP += figures[x][y][1];

		return numP;
	}


	// 個体の総数を返す
	public int countTotal() {
		return countTMale(0) + countTMale(1) + countFemale();
	}


	// 現在のT1遺伝子の割合を返す
	public double getT1Ratio(){
		return (double)countT1Gene() / countTotal();
	}

	// 現在のP1遺伝子の割合を返す
	public double getP1Ratio(){
		return (double)countP1Gene() / countTotal();
	}


	// 次の世代まで進めるか判断．進める場合は世代を進める．
	public boolean checkToStop(){
		if(time >= EndTime) {
			System.out.println("Timeout");
			return false;
		} else if(countTMale(0) == 0 && countTMale(1) == 0) {
			System.out.println("Male dies out");
			return false;
		} else if(countFemale() == 0) {
			System.out.println("Female dies out");
			return false;
		} else if(countT0Gene() == 0){
			System.out.println("Gene T0 dies out");
			return false;
		} else if(countT1Gene() == 0){
			System.out.println("Gene T1 dies out");
			return false;
		} else if(countP0Gene() == 0){
			System.out.println("Gene P0 dies out");
			return false;
		} else if(countP1Gene() == 0){
			System.out.println("Gene P1 dies out");
			return false;
		}

		time++;
		return true;
	}


	// 現在のT0遺伝子，T1遺伝子，P0遺伝子，P1遺伝子の個体数を表示
	public void showCount(){
		System.out.println("\t (T0, T1) = (" + countT0Gene() + ", " + countT1Gene() + ")\t"
			+ "(P0, P1) = (" + countP0Gene() + ", " + countP1Gene() + ")");	}

	// 現在のT0遺伝子雄，T1遺伝子雄，P0遺伝子雌，P1遺伝子雌の個体数を表示
	public void showRatio(){
		System.out.println("[Generation:" + time + "] T1: " + getT1Ratio() + "\tP1: " + getP1Ratio());
	}

	// 雌雄がつがう確率を計算する
	private void calcBreedProb(){
		int t0Male = tMaleList[0].getCount(),
			t1Male = tMaleList[1].getCount();

		BreedProb[0] = (a0Coeff * t0Male) / (t1Male + a0Coeff * t0Male);
		BreedProb[1] = (a1Coeff * t1Male) / (t0Male + a1Coeff * t1Male);
	}

	// つがいをつくり交配させる
	private void coupling() {
		Female aFemale;
		ListShuffler shuffler;
		int femaleCount;
		int x;

		// tMaleList[0], tMaleList[1], femaleListをシャッフルする
		shuffler = new ListShufflerImpl(this.getZone());
		shuffler.shuffleWholeList(tMaleList[0]);
		shuffler.shuffleWholeList(tMaleList[1]);
		shuffler.shuffleWholeList(femaleList);

		femaleCount = countFemale();
		
		for(x = 0; x < femaleCount; x++){

			// 雌リストの先頭の雌を取り出す
			aFemale = (Female)femaleList.removeFirst();

			if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < BreedProb[aFemale.getP()]){
		
				// P0はT0と，P1はT1と交配する

				select(aFemale.getT(), aFemale.getP(), aFemale.getP());

			} else {

				// P0はT1と，P1はT0と交配する

				select(aFemale.getT(), aFemale.getP(), (aFemale.getP() + 1)%2);
			}

			// 取り出した雌をリストの最後尾に戻す
			femaleList.addLast(aFemale);

		}

	}

	// つがいになる雄を選ぶ
	private void select(int femaleT, int femaleP, int maleT){
		Male aMale;
		int maleTCount;
		int x, y;

		// つがいの相手となるT遺伝子雄の総数を取得する
		maleTCount = countTMale(maleT);

		// つがいの相手となるT遺伝子雄が存在しないときは交配しない
		if(maleTCount <= 0) return;

		for(x = 0; x < maleTCount; x++) {
			// 次の雄をとりだす
			aMale = (Male)tMaleList[maleT].removeFirst();
					
			// まだつがいになっていなければ，交配
			if(!aMale.married) {
				aMale.married = true;
				for(y = 0; y < numChild; y++){
					breed(aMale.getT(), aMale.getP(), femaleT, femaleP);
				}
				// とりだした雄をリストの最後尾に戻す
				tMaleList[maleT].addLast(aMale);
				break;
			}
			// とりだした雄をリストの最後尾に戻す
			tMaleList[maleT].addLast(aMale);

		}
				
	}

	// 次の世代の子供を生成し，各個体リストに追加する
	private void breed(int maleT, int maleP, int femaleT, int femaleP){
		int tGene, pGene;
		Male aMale;
		Female aFemale;

		// 子供のT遺伝子を決定
		if(maleT == femaleT) {
			tGene = maleT;
		} else {
			if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < 0.5)
				tGene = maleT;
			else
				tGene = femaleT;		
		}

		// 子供のP遺伝子を決定
		if(maleP == femaleP) {
			pGene = maleP;
		} else {
			if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < 0.5)
				pGene = maleP;
			else
				pGene = femaleP;		
		}
		
		// 性別の決定
		if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < 0.5){
			
			// 雄を生成
			aMale = new Male(this, tGene, pGene);
			aMale.setDeathProb(tMaleDeathProb[tGene]);
			tMaleList[tGene].addLast(aMale);
			addFigures(0, tGene, pGene);

		} else {

			// 雌を生成
			aFemale = new Female(this, tGene, pGene);
			aFemale.setDeathProb(femaleDeathProb);
			femaleList.addLast(aFemale);
			addFigures(1, tGene, pGene);
		}
	}

	// 統計情報figuresに新たな個体のデータを反映
	private void addFigures(int sex, int tGene, int pGene){
		figures[sex][tGene][pGene]++;
	}

	// 統計情報figuresに個体の死亡を反映
	private void removeFigures(int sex, int tGene, int pGene){
		figures[sex][tGene][pGene]--;
	}

	// 一定確率で個体を死亡させるとともに寿命になった個体を死亡させる
	private void die(){
		Male aMale;
		Female aFemale;
		int count;
		int x, y;

		// 雄の生死判定
		for(x = 0; x < 2; x++) {

			// Tx遺伝子雄の総数を取得する
			count = countTMale(x);

			for(y = 0; y < count; y++) {
				// リストの先頭からとりだす
				aMale = (Male)tMaleList[x].removeFirst();

				if(aMale.getAge() >= lifetime || Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < tMaleDeathProb[x]){
					// 死亡
					removeFigures(0, x, aMale.getP());
				} else {
					// とりだした個体を殺さずリストの最後尾に戻す
					tMaleList[x].addLast(aMale);
				}
			}
		}

		// 雌の生死判定

		// 雌の総数を取得する
		count = countFemale();

		for(x = 0; x < count; x++) {
			// リストの先頭からとりだす
			aFemale = (Female)femaleList.removeFirst();

			if(aFemale.getAge() >= lifetime || Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < femaleDeathProb){
				// 死亡
				removeFigures(1, aFemale.getT(), aFemale.getP());
			} else {
				// とりだした個体を殺さずリストの最後尾に戻す
				femaleList.addLast(aFemale);
			}
		}
	}


	// 年齢を1つ増やし，雄のmarriedをfalseにする．
	private void nextAge(){
		Male aMale;
		Female aFemale;
		int count;
		int x, y;

		// 雄
		for(x = 0; x < 2; x++) {

			// Tx遺伝子雄の総数を取得する
			count = countTMale(x);

			for(y = 0; y < count; y++) {
				aMale = (Male)tMaleList[x].removeFirst();
				aMale.age();
				tMaleList[x].addLast(aMale);
			}
		}

		// 雌

		// Tx遺伝子雄の総数を取得する
		count = countFemale();

		for(x = 0; x < count; x++) {
			aFemale = (Female)femaleList.removeFirst();
			aFemale.age();
			femaleList.addLast(aFemale);
		}
	}

	// Kirkpatrickのモデルにおける平衡状態のT1遺伝子頻度を返す
	public double getEquilibriumValue(){
		double tmp, p1Ratio;
		p1Ratio = getP1Ratio();

		tmp = 1 - sRatio;

		if(p1Ratio <= v1) {
			return 0;
		} else if (p1Ratio < v2) {
			return ( (a0Coeff * a1Coeff - 1) * tmp / (a0Coeff - tmp) * p1Ratio - 1 ) / (a1Coeff * tmp - 1) ;
		} else {
			return 1;
		}
	}

	// 各世代での処理
	public void step() {

		// 一定確率で個体を死亡させるとともに寿命になった個体を死亡させる
		die();

		// 遺伝子ごとの雌雄のつがう確率を計算
		calcBreedProb();

		
		// 年齢を1つ増やし，雄のmarriedをfalseにする
		nextAge();

		// 交配
		coupling();

		// 現在の状況を表示
		showRatio();
		showCount();
	}
}
