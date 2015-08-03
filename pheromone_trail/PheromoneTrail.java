import swarm.*;

/**
 * Swarm�ɂ��A���̍̐H�s���̃V�~�����[�V����<BR>
 * �ɒ�Ďu, "���G�n�̃V�~�����[�V���� -Swarm�ɂ��}���`�G�[�W�F���g�E�V�X�e��-", �R���i��, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author �ŏo�`��
 **/
public class PheromoneTrail{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
	
		Globals.env.initSwarm("bug", "0.3", "Iba Lab.", args);
	
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
	
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
