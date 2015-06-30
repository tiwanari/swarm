import swarm.*;

/**
 * Swarm�ɂ��Ǘ�<BR>
 * <BR>
 * Swarm�ƌĂԓ���ȃI�u�W�F�N�g��Bug��FoodSpace���Ǘ�������B
 * Swarm��Bug��FoodSpace����Ȃ鐢�E�̃��f���ł���B
 * ����ɂ���āA���f���̏ڍׂ̓��\�b�hmain�����菜�����B
 * main�̖����́A���̃��f������邱�Ƃ����ɂȂ�B<BR>
 * <BR>
 * Swarm��Bug��FoodSpace�Ȃǂ��������łȂ��Aactivity�ɂ���āA�����̓�������䂷��B
 * Activity�̓I�u�W�F�N�g�ɑ����郁�b�Z�[�W�̈ꗗ�ł���B
 * �ڍׂ�ModelSwarm.java�����Ăق����B<BR>
 * <BR>
 * �����ł͒P����ModelSwarm�𐶐����AModelSwarm�ɃI�u�W�F�N�g��activity�𐶐�������B
 * �g�b�v���x����activity�i�����ł�ModelSwarm��activity�j��run���邱�ƂŁA�V�~�����[�V���������s������B<BR>
 * <BR>
 * �N���X�}<BR>
 * <IMG src="../simpleSwarmBug.png" border="0"><BR>
 * <BR>
 * �����̗���
 * <OL>
 * <LI>simpleSwarmBug��ModelSwarm�����iModelSwarm��<A href="http://www.santafe.edu/projects/swarm/swarmdocs/refbook-java/swarm/objectbase/SwarmImpl.html">SwarmImpl</A>�ł���j
 * <LI>ModelSwarm��buildObjects��Bug��FoodSpace�����
 * <LI>ModelSwarm��buildActions���X�P�W���[���iBug�����ԊԊu1��step����j�����
 * <LI>ModelSwarm��activateIn���X�P�W���[�����m�肷��
 * <LI>simpleSwarmBug���g�b�v���x���iModelSwarm�j��activity��getActivity�Ŏ擾���A���s����irun�j
 * </OL>
 * <BR>
 * �V�[�P���X�}<BR>
 * <IMG src="../simpleSwarmBugSequence.png" border="0"><BR>
 * <BR>
 * ����simpleSwarmBug2
 * @author YABUKI Taro
 * @version 0.4
 */

public class simpleSwarmBug{
	public static void main(String[] args) {
		ModelSwarm modelSwarm;
		
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		// Make the model swarm
		modelSwarm=new ModelSwarm(Globals.env.globalZone);
		
		// Now send messages to the newly created swarm telling it
		// to build its internal objects and its activities.
		// Then activate the swarm.
		modelSwarm.buildObjects();
		modelSwarm.buildActions();
		modelSwarm.activateIn(null); // Top-level swarm is activated in nil
		
		// Now the swarm is built, activated, and ready to go...
		// so "run" it.
		modelSwarm.getActivity().run();
	}
}
