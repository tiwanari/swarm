// Sugarscape.java

import swarm.*;

/**
 * Joshua M. Epstein��Robert Axtell����Ă����l���Љ�̂��߂̎�����<BR>
 * �ɒ�Ďu, "���G�n�̃V�~�����[�V���� -Swarm�ɂ��}���`�G�[�W�F���g�E�V�X�e��-", �R���i��, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author ���F�V
 **/
public class Sugarscape{
	public static void main(String[] args){
		ObserverSwarm observerSwarm;

		Globals.env.initSwarm("Sugarscape","0.9","Iba Lab.", args);

		observerSwarm = new ObserverSwarm(Globals.env.globalZone);
		Globals.env.setWindowGeometryRecordName(observerSwarm, "observerSwarm");

		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}

