import swarm.*;

/**
 * ModelSwarm��ύX���邱�Ƃɂ���āABug�̐��𕡐��ɂ���<BR>
 * <BR>
 * <IMG src="../simpleSwarmBug2.png" border="0"><BR>
 * <BR>
 * ����simpleSwarmBug3
 * @author YABUKI Taro
 * @version 0.4
 */
public class simpleSwarmBug2{
	public static void main(String[] args) {
		ModelSwarm modelSwarm;
		
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		modelSwarm=new ModelSwarm(Globals.env.globalZone);
		modelSwarm.buildObjects();
		modelSwarm.buildActions();
		modelSwarm.activateIn(null);
		modelSwarm.getActivity().run();
	}
}
