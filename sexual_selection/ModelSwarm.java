import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{	
	public int N; // N: �S�̐�
	public double male_femaleRatio; // male_femaleRatio: ���Y��(�S�̐��ɑ΂���Y�̂̊���)
	public double tGeneRatio; // tGeneRatio: �S�̐��ɑ΂���T1��`�q�̂̊���
	public double pGeneRatio; // pGeneRatio: �S�̐��ɑ΂���P1��`�q�̂̊���
	
	public double a0Coeff, a1Coeff; // a0Coeff, a1Coeff: Kirkpatrick�̃��f���̗Y�I�����ɂ�����m�����̌W��
	public double maleDeathProb; // �Y�̌�z�O���Ŋm��(���ׂĂ̗Y�̌�z�O���Ŋm���̃x�[�X)
	public double femaleDeathProb; // FemaleProb:���̌�z�O���Ŋm��
	public double sRatio; // T��`�q�Y�̌�z�O���Ŋm���̔�DT1��`�q�Y��T0��`�q�Y�ɔ�ׂ�(1 - sRatio)�{�̐������D
	private double tMaleDeathProb[]; // tMaleDeathProb:T��`�q���Ƃ̗Y�̌�z�O���Ŋm���D

	private double[] BreedProb; // BreedProb: ���Y�������m���iBreedProb[0] = P(T0|P0), BreedProb[1] = P(T1|P1)�j
	private int[][][] figures; // figures: �̂̓��v���Dfigures[���ʁi0:�Y�C1:���j][T��`�q][P��`�q]

	public int time; // time: ����
	public int EndTime; // EndTime: �I�������ƂȂ鐢��

	public int lifetime; // lifetime: �̂̎���
	public int numChild; // numChild: ��x�̌�z�Ő��ގq���̐� 
	
	List[] tMaleList; // tMaleList: T��`�q���Ƃ̗Y�̃��X�g
    List femaleList; // femaleList: ���̃��X�g

	double v1, v2; // v1, v2: Kirkpatrick�̃��f���̕��t��Ԃł̈�`�q�p�x�̊֌W���̂�����V1, V2

	// �f�t�H���g�R���X�g���N�^
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
	
	// ������
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

		// �̂̐���
		for (x = 0; x < N; x++){

			// T��`�q����
			if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < tGeneRatio) {
				tGene = 1;
			} else {
				tGene = 0;
			}

			// P��`�q����
			if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < pGeneRatio) {
				pGene = 1;
			} else {
				pGene = 0;
			}
			
			// ���ʌ���
			if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < male_femaleRatio) {
			
				// �Y�̂̐���
				aMale = new Male(this, tGene, pGene);
				aMale.setDeathProb(tMaleDeathProb[tGene]);
				tMaleList[tGene].addLast(aMale);
				addFigures(0, tGene, pGene);

			} else {

				// ���̂̐���
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
	
	// T��`�q�Y�̂̑�����Ԃ�
	public int countTMale(int tGene){
		return tMaleList[tGene].getCount();
	}

	// ���̂̑�����Ԃ�
	public int countFemale(){
		return femaleList.getCount();
	}


	// T0��`�q�̌̐���Ԃ�
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

	// T1��`�q�̌̐���Ԃ�
	public int countT1Gene() {
		int x, y;
		int numT = 0;

		for(x = 0; x <= 1; x++)
			for(y = 0; y <= 1; y++)
				numT += figures[x][1][y];

		return numT;
	}

	// P0��`�q�̌̐���Ԃ�
	public int countP0Gene() {
		int x, y;
		int numP = 0;

		for(x = 0; x <= 1; x++)
			for(y = 0; y <= 1; y++)
				numP += figures[x][y][0];

		return numP;
	}

	// P1��`�q�̌̐���Ԃ�
	public int countP1Gene() {
		int x, y;
		int numP = 0;

		for(x = 0; x <= 1; x++)
			for(y = 0; y <= 1; y++)
				numP += figures[x][y][1];

		return numP;
	}


	// �̂̑�����Ԃ�
	public int countTotal() {
		return countTMale(0) + countTMale(1) + countFemale();
	}


	// ���݂�T1��`�q�̊�����Ԃ�
	public double getT1Ratio(){
		return (double)countT1Gene() / countTotal();
	}

	// ���݂�P1��`�q�̊�����Ԃ�
	public double getP1Ratio(){
		return (double)countP1Gene() / countTotal();
	}


	// ���̐���܂Ői�߂邩���f�D�i�߂�ꍇ�͐����i�߂�D
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


	// ���݂�T0��`�q�CT1��`�q�CP0��`�q�CP1��`�q�̌̐���\��
	public void showCount(){
		System.out.println("\t (T0, T1) = (" + countT0Gene() + ", " + countT1Gene() + ")\t"
			+ "(P0, P1) = (" + countP0Gene() + ", " + countP1Gene() + ")");	}

	// ���݂�T0��`�q�Y�CT1��`�q�Y�CP0��`�q���CP1��`�q���̌̐���\��
	public void showRatio(){
		System.out.println("[Generation:" + time + "] T1: " + getT1Ratio() + "\tP1: " + getP1Ratio());
	}

	// ���Y�������m�����v�Z����
	private void calcBreedProb(){
		int t0Male = tMaleList[0].getCount(),
			t1Male = tMaleList[1].getCount();

		BreedProb[0] = (a0Coeff * t0Male) / (t1Male + a0Coeff * t0Male);
		BreedProb[1] = (a1Coeff * t1Male) / (t0Male + a1Coeff * t1Male);
	}

	// �����������z������
	private void coupling() {
		Female aFemale;
		ListShuffler shuffler;
		int femaleCount;
		int x;

		// tMaleList[0], tMaleList[1], femaleList���V���b�t������
		shuffler = new ListShufflerImpl(this.getZone());
		shuffler.shuffleWholeList(tMaleList[0]);
		shuffler.shuffleWholeList(tMaleList[1]);
		shuffler.shuffleWholeList(femaleList);

		femaleCount = countFemale();
		
		for(x = 0; x < femaleCount; x++){

			// �����X�g�̐擪�̎������o��
			aFemale = (Female)femaleList.removeFirst();

			if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < BreedProb[aFemale.getP()]){
		
				// P0��T0�ƁCP1��T1�ƌ�z����

				select(aFemale.getT(), aFemale.getP(), aFemale.getP());

			} else {

				// P0��T1�ƁCP1��T0�ƌ�z����

				select(aFemale.getT(), aFemale.getP(), (aFemale.getP() + 1)%2);
			}

			// ���o�����������X�g�̍Ō���ɖ߂�
			femaleList.addLast(aFemale);

		}

	}

	// �����ɂȂ�Y��I��
	private void select(int femaleT, int femaleP, int maleT){
		Male aMale;
		int maleTCount;
		int x, y;

		// �����̑���ƂȂ�T��`�q�Y�̑������擾����
		maleTCount = countTMale(maleT);

		// �����̑���ƂȂ�T��`�q�Y�����݂��Ȃ��Ƃ��͌�z���Ȃ�
		if(maleTCount <= 0) return;

		for(x = 0; x < maleTCount; x++) {
			// ���̗Y���Ƃ肾��
			aMale = (Male)tMaleList[maleT].removeFirst();
					
			// �܂������ɂȂ��Ă��Ȃ���΁C��z
			if(!aMale.married) {
				aMale.married = true;
				for(y = 0; y < numChild; y++){
					breed(aMale.getT(), aMale.getP(), femaleT, femaleP);
				}
				// �Ƃ肾�����Y�����X�g�̍Ō���ɖ߂�
				tMaleList[maleT].addLast(aMale);
				break;
			}
			// �Ƃ肾�����Y�����X�g�̍Ō���ɖ߂�
			tMaleList[maleT].addLast(aMale);

		}
				
	}

	// ���̐���̎q���𐶐����C�e�̃��X�g�ɒǉ�����
	private void breed(int maleT, int maleP, int femaleT, int femaleP){
		int tGene, pGene;
		Male aMale;
		Female aFemale;

		// �q����T��`�q������
		if(maleT == femaleT) {
			tGene = maleT;
		} else {
			if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < 0.5)
				tGene = maleT;
			else
				tGene = femaleT;		
		}

		// �q����P��`�q������
		if(maleP == femaleP) {
			pGene = maleP;
		} else {
			if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < 0.5)
				pGene = maleP;
			else
				pGene = femaleP;		
		}
		
		// ���ʂ̌���
		if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < 0.5){
			
			// �Y�𐶐�
			aMale = new Male(this, tGene, pGene);
			aMale.setDeathProb(tMaleDeathProb[tGene]);
			tMaleList[tGene].addLast(aMale);
			addFigures(0, tGene, pGene);

		} else {

			// ���𐶐�
			aFemale = new Female(this, tGene, pGene);
			aFemale.setDeathProb(femaleDeathProb);
			femaleList.addLast(aFemale);
			addFigures(1, tGene, pGene);
		}
	}

	// ���v���figures�ɐV���Ȍ̂̃f�[�^�𔽉f
	private void addFigures(int sex, int tGene, int pGene){
		figures[sex][tGene][pGene]++;
	}

	// ���v���figures�Ɍ̂̎��S�𔽉f
	private void removeFigures(int sex, int tGene, int pGene){
		figures[sex][tGene][pGene]--;
	}

	// ���m���Ō̂����S������ƂƂ��Ɏ����ɂȂ����̂����S������
	private void die(){
		Male aMale;
		Female aFemale;
		int count;
		int x, y;

		// �Y�̐�������
		for(x = 0; x < 2; x++) {

			// Tx��`�q�Y�̑������擾����
			count = countTMale(x);

			for(y = 0; y < count; y++) {
				// ���X�g�̐擪����Ƃ肾��
				aMale = (Male)tMaleList[x].removeFirst();

				if(aMale.getAge() >= lifetime || Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < tMaleDeathProb[x]){
					// ���S
					removeFigures(0, x, aMale.getP());
				} else {
					// �Ƃ肾�����̂��E�������X�g�̍Ō���ɖ߂�
					tMaleList[x].addLast(aMale);
				}
			}
		}

		// ���̐�������

		// ���̑������擾����
		count = countFemale();

		for(x = 0; x < count; x++) {
			// ���X�g�̐擪����Ƃ肾��
			aFemale = (Female)femaleList.removeFirst();

			if(aFemale.getAge() >= lifetime || Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < femaleDeathProb){
				// ���S
				removeFigures(1, aFemale.getT(), aFemale.getP());
			} else {
				// �Ƃ肾�����̂��E�������X�g�̍Ō���ɖ߂�
				femaleList.addLast(aFemale);
			}
		}
	}


	// �N���1���₵�C�Y��married��false�ɂ���D
	private void nextAge(){
		Male aMale;
		Female aFemale;
		int count;
		int x, y;

		// �Y
		for(x = 0; x < 2; x++) {

			// Tx��`�q�Y�̑������擾����
			count = countTMale(x);

			for(y = 0; y < count; y++) {
				aMale = (Male)tMaleList[x].removeFirst();
				aMale.age();
				tMaleList[x].addLast(aMale);
			}
		}

		// ��

		// Tx��`�q�Y�̑������擾����
		count = countFemale();

		for(x = 0; x < count; x++) {
			aFemale = (Female)femaleList.removeFirst();
			aFemale.age();
			femaleList.addLast(aFemale);
		}
	}

	// Kirkpatrick�̃��f���ɂ����镽�t��Ԃ�T1��`�q�p�x��Ԃ�
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

	// �e����ł̏���
	public void step() {

		// ���m���Ō̂����S������ƂƂ��Ɏ����ɂȂ����̂����S������
		die();

		// ��`�q���Ƃ̎��Y�̂����m�����v�Z
		calcBreedProb();

		
		// �N���1���₵�C�Y��married��false�ɂ���
		nextAge();

		// ��z
		coupling();

		// ���݂̏󋵂�\��
		showRatio();
		showCount();
	}
}
