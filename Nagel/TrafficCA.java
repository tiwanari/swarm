import swarm.*;

/**
 * Traffic CA�ɂ���ʏa�؂̃V�~�����[�V����<BR>
 * Turing������
 * �ɒ�Ďu, "���G�n�̃V�~�����[�V���� -Swarm�ɂ��}���`�G�[�W�F���g�E�V�X�e��-", �R���i��, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author Iba Lab.
 **/
public class TrafficCA{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("traffic", "0.0", "Iba Lab.", args);
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
