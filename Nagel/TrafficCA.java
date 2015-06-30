import swarm.*;

/**
 * Traffic CAによる交通渋滞のシミュレーション<BR>
 * Turingを改変
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author Iba Lab.
 **/
public class TrafficCA{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("traffic", "0.0", "Iba Lab.", args);
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
