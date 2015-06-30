import swarm.*;

/**
 * ���f���{GUI<BR>
 * <BR>
 * �V�~�����[�V�����̃��f���Ƃ����\�����邽�߂̃C���^�[�t�F�[�X�𕹂�����
 * ObserverSwarm�����B
 * ����ɂ���ăV�X�e���́AObserverSwarm��ModelSwarm�����A
 * ModelSwarm��Bug��FoodSpace�����Ƃ����K�w�\���ɂȂ�B
 * ���̂悤�ȊK�w�\�����ł���ƁA�V�~�����[�V�������K�w�I�Ɏ��s���邱�ƂɂȂ�B<BR>
 * <BR>
 * �����ŗp����swarm�͂���܂łƈႢ�AGUISwarm�ł���B
 * ����ɂ���āAswarm�̓��[�U��GUI��p���đΘb���邱�Ƃ��ł���B
 * �Ƃ͂����Ă��A�����̎d���Ȃǂ��傫���ς��킯�ł͂Ȃ��B<BR>
 * <BR>
 * �ЂƂς�����̂́A�V�~�����[�V�������J�n����̂ɁAswarm��activity��
 * �擾����run������K�v���Ȃ��Ȃ������Ƃł���B����ɑ�������̂́AControlPanel��
 * start�{�^���������ꂽ�Ƃ��ɍs���B<BR>
 * <BR>
 * <IMG src="../simpleObserverBug.png" border="0"><BR>
 * <BR>
 * ����simpleObserverBug2
 * @author YABUKI Taro
 * @version 0.4
 */
public class simpleObserverBug{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		
		// We tell the swarm itself to go, instead of an activity
		// because the observerSwarm is a GUI-swarm, and has its
		// own controlPanel that we can talk to.
		observerSwarm.go();
	}
}
