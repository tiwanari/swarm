import swarm.*;

/**
 * フィールドを変更したりメソッドを呼び出すためのGUIであるProbeを追加する。
 * シミュレーションの本体に変更はない。<BR>
 * <BR>
 * <IMG src="../simpleObserverBug2.png" border="0"><BR>
 * <BR>
 * 次はsimpleExperBug
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
