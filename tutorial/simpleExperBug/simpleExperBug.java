import swarm.*;

/**
 * これまでは一つのモデルをシミュレートするだけだったのに対し、
 * ここではパラメータを変えながら複数のモデルをシミュレートする。<BR>
 * <BR>
 * 複数のモデルを管理するためにExperSwarmを使う。
 * これまでのObserverSwarmに相当するクラスである。<BR>
 * <BR>
 * <IMG src="../simpleExperBug.png" border="0"><BR>
 * <BR>
 * 以上でチュートリアルは完了である。さらに例を見たければ、
 * jheatbugsやjmousetrapなどがある。Swarmでの開発は、
 * このチュートリアルのシミュレーションの枠組み（Observerがあって、Modelがある）
 * にとらわれるものではない。
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
