import swarm.*;

/**
 * �t�B�[���h��ύX�����胁�\�b�h���Ăяo�����߂�GUI�ł���Probe��ǉ�����B
 * �V�~�����[�V�����̖{�̂ɕύX�͂Ȃ��B<BR>
 * <BR>
 * <IMG src="../simpleObserverBug2.png" border="0"><BR>
 * <BR>
 * ����simpleExperBug
 * @author YABUKI Taro
 * @version 0.4
 */
public class simpleObserverBug2{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("bug", "0.4", "WATANABE Akio", args);
		
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
