import swarm.*;

/**
 * Swarmによるアリの採食行動のシミュレーション<BR>
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author 打出義尚
 **/
public class PheromoneTrail{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
	
		Globals.env.initSwarm("bug", "0.3", "Iba Lab.", args);
	
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
	
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
