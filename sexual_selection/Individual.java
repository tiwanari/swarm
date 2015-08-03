import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;

// 雌雄共通の個体クラス
public class Individual extends SwarmObjectImpl {
	private int tGene, pGene; // tGene: T遺伝子（発現形式）	pGene: p遺伝子（好み)
	protected double deathProb; // deathProb: 死亡確率
	private int year; // 年齢

	// デフォルトコンストラクタ
	public Individual(Zone aZone){
		super(aZone);

		tGene = 0;
		pGene = 0;
		deathProb = 0.0;
		year = 0;
	}

	// tGene，pGene指定コンストラクタ
	public Individual(Zone aZone, int t, int p){
		super(aZone);

		tGene = t;
		pGene = p;
		deathProb = 0.0;
		year = 0;
	}

	// tGeneの値を返す
	public int getT(){
		return tGene;
	}

	// pGeneの値を返す
	public int getP() {
		return pGene;
	}


	// yearの値を返す
	public int getAge() {
		return year;
	}



	// sProbの値を変更する
	public void setDeathProb(double prob) {
		deathProb = prob;
	}


	// 年をとる
	public void age() {
		year++;
	}
}
