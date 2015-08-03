import swarm.*;
import java.util.*;

/**
 * Swarm��p�����X�щ΍Ђ̃V�~�����[�V����<BR>
 * �ɒ�Ďu, "���G�n�̃V�~�����[�V���� -Swarm�ɂ��}���`�G�[�W�F���g�E�V�X�e��-", �R���i��, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author ��J�C
 **/
 public class ForestFire {
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("forest_fire", "0.1", "Iba Lab.", args);
		observerSwarm = new ObserverSwarm(Globals.env.globalZone);
        System.out.println("buildObjects");
		observerSwarm.buildObjects();
        System.out.println("buildActions");
		observerSwarm.buildActions();
        System.out.println("activateIn");
		observerSwarm.activateIn(null);
        System.out.println("go");
		observerSwarm.go();
	}
}
