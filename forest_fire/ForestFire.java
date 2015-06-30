import swarm.*;
import java.util.*;

/**
 * Swarmを用いた森林火災のシミュレーション<BR>
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author 大谷佑
 **/
 public class ForestFire{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("forest_fire", "0.1", "Iba Lab.", args);
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
