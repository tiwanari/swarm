import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.collections.*;


// ���̃N���X
public class Female extends Individual {
	// �f�t�H���g�R���X�g���N�^
	public Female(Zone aZone){
		super(aZone);
	}

	// tGene�CpGene�w��R���X�g���N�^
	public Female(Zone aZone, int t, int p){
		super(aZone, t, p);
	}
}
