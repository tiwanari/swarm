import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.collections.*;

// �Y�̃N���X
public class Male extends Individual {
	public boolean married; // married: ���̑�ł����ɂȂ������ǂ���

	// �f�t�H���g�R���X�g���N�^
	public Male(Zone aZone){
		super(aZone);
	}

	// tGene�CpGene, deadList�w��R���X�g���N�^
	public Male(Zone aZone, int t, int p){
		super(aZone, t, p);

		married = false;
	}


	// �N���Ƃ�ƂƂ��ɁCmarried��false�ɖ߂�
	public void age(){
		super.age();
		married = false;
	}

}
