// Sugarscape.java

import swarm.*;

/**
 * Joshua M. EpsteinとRobert Axtellが提案した人口社会のための実験環境<BR>
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author 岩野孝之
 **/
public class Sugarscape{
	public static void main(String[] args){
		ObserverSwarm observerSwarm;

		Globals.env.initSwarm("Sugarscape","0.9","Iba Lab.", args);

		observerSwarm = new ObserverSwarm(Globals.env.globalZone);
		Globals.env.setWindowGeometryRecordName(observerSwarm, "observerSwarm");

		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}

