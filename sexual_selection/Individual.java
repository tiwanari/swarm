import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;

// ���Y���ʂ̌̃N���X
public class Individual extends SwarmObjectImpl {
	private int tGene, pGene; // tGene: T��`�q�i�����`���j	pGene: p��`�q�i�D��)
	protected double deathProb; // deathProb: ���S�m��
	private int year; // �N��

	// �f�t�H���g�R���X�g���N�^
	public Individual(Zone aZone){
		super(aZone);

		tGene = 0;
		pGene = 0;
		deathProb = 0.0;
		year = 0;
	}

	// tGene�CpGene�w��R���X�g���N�^
	public Individual(Zone aZone, int t, int p){
		super(aZone);

		tGene = t;
		pGene = p;
		deathProb = 0.0;
		year = 0;
	}

	// tGene�̒l��Ԃ�
	public int getT(){
		return tGene;
	}

	// pGene�̒l��Ԃ�
	public int getP() {
		return pGene;
	}


	// year�̒l��Ԃ�
	public int getAge() {
		return year;
	}



	// sProb�̒l��ύX����
	public void setDeathProb(double prob) {
		deathProb = prob;
	}


	// �N���Ƃ�
	public void age() {
		year++;
	}
}
