import swarm.*;
import java.util.*;

/**
 * �i�q�{���c�}���@�ɂ�闬�̂̃V�~�����[�V����<BR>
 * �ɒ�Ďu, "���G�n�̃V�~�����[�V���� -Swarm�ɂ��}���`�G�[�W�F���g�E�V�X�e��-", �R���i��, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author �z�q�i�q
 **/
public class LBM{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("lbm", "0.0", "Iba Lab.", args);
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
