import swarm.*;
/**
 * Swarmを用いた1次元2状態3近傍のセルオートマトン<BR>
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author 柳瀬利彦
 **/
public class CA {

	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("bug","0.1","Iba Lab.",args);
		
		observerSwarm = new ObserverSwarm(Globals.env.globalZone);
		System.out.println("buildObjects");
		observerSwarm.buildObjects();
		System.out.println("buildActions");
		observerSwarm.buildActions();
		System.out.println("activateIn");
		observerSwarm.activateIn(null);
		System.out.println("go");
		observerSwarm.go();		
	}
}
