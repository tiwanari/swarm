import swarm.*;

/**
 * ����܂ł͈�̃��f�����V�~�����[�g���邾���������̂ɑ΂��A
 * �����ł̓p�����[�^��ς��Ȃ��畡���̃��f�����V�~�����[�g����B<BR>
 * <BR>
 * �����̃��f�����Ǘ����邽�߂�ExperSwarm���g���B
 * ����܂ł�ObserverSwarm�ɑ�������N���X�ł���B<BR>
 * <BR>
 * <IMG src="../simpleExperBug.png" border="0"><BR>
 * <BR>
 * �ȏ�Ń`���[�g���A���͊����ł���B����ɗ����������΁A
 * jheatbugs��jmousetrap�Ȃǂ�����BSwarm�ł̊J���́A
 * ���̃`���[�g���A���̃V�~�����[�V�����̘g�g�݁iObserver�������āAModel������j
 * �ɂƂ������̂ł͂Ȃ��B
 * @author YABUKI Taro
 * @version 0.4
 */
public class simpleExperBug{
	public static void main(String[] args) {
		ExperSwarm experSwarm;
		
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		experSwarm=new ExperSwarm(Globals.env.globalZone);
		Globals.env.setWindowGeometryRecordName(experSwarm,"experSwarm");
		experSwarm.buildObjects();
		experSwarm.buildActions();
		experSwarm.activateIn(null);
		experSwarm.go();
	}
}
