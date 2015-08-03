import swarm.*;

/**
 * モデル＋GUI<BR>
 * <BR>
 * シミュレーションのモデルとそれを表示するためのインターフェースを併せ持つ
 * ObserverSwarmを作る。
 * これによってシステムは、ObserverSwarmがModelSwarmを作り、
 * ModelSwarmがBugやFoodSpaceを作るという階層構造になる。
 * このような階層構造ができると、シミュレーションも階層的に実行することになる。<BR>
 * <BR>
 * ここで用いるswarmはこれまでと違い、GUISwarmである。
 * これによって、swarmはユーザとGUIを用いて対話することができる。
 * とはいっても、生成の仕方などが大きく変わるわけではない。<BR>
 * <BR>
 * ひとつ変わったのは、シミュレーションを開始するのに、swarmのactivityを
 * 取得してrunさせる必要がなくなったことである。これに相当するのは、ControlPanelが
 * startボタンを押されたときに行う。<BR>
 * <BR>
 * <IMG src="../simpleObserverBug.png" border="0"><BR>
 * <BR>
 * 次はsimpleObserverBug2
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
