import swarm.*;

/**
 * Swarmによる管理<BR>
 * <BR>
 * Swarmと呼ぶ特殊なオブジェクトにBugとFoodSpaceを管理させる。
 * SwarmはBugとFoodSpaceからなる世界のモデルである。
 * これによって、モデルの詳細はメソッドmainから取り除かれる。
 * mainの役割は、このモデルを作ることだけになる。<BR>
 * <BR>
 * SwarmはBugやFoodSpaceなどを持つだけでなく、activityによって、それらの動作も制御する。
 * Activityはオブジェクトに送られるメッセージの一覧である。
 * 詳細はModelSwarm.javaを見てほしい。<BR>
 * <BR>
 * ここでは単純にModelSwarmを生成し、ModelSwarmにオブジェクトとactivityを生成させる。
 * トップレベルのactivity（ここではModelSwarmのactivity）をrunすることで、シミュレーションを実行させる。<BR>
 * <BR>
 * クラス図<BR>
 * <IMG src="../simpleSwarmBug.png" border="0"><BR>
 * <BR>
 * 処理の流れ
 * <OL>
 * <LI>simpleSwarmBugがModelSwarmを作る（ModelSwarmは<A href="http://www.santafe.edu/projects/swarm/swarmdocs/refbook-java/swarm/objectbase/SwarmImpl.html">SwarmImpl</A>である）
 * <LI>ModelSwarmのbuildObjectsがBugとFoodSpaceを作る
 * <LI>ModelSwarmのbuildActionsがスケジュール（Bugが時間間隔1でstepする）を作る
 * <LI>ModelSwarmのactivateInがスケジュールを確定する
 * <LI>simpleSwarmBugがトップレベル（ModelSwarm）のactivityをgetActivityで取得し、実行する（run）
 * </OL>
 * <BR>
 * シーケンス図<BR>
 * <IMG src="../simpleSwarmBugSequence.png" border="0"><BR>
 * <BR>
 * 次はsimpleSwarmBug2
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
