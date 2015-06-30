import swarm.*;
/**
 * Cohenらによる繰り返し囚人のジレンマのシミュレーション<BR>
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author YABUKI Taro
 **/
public class GridIPD{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("GridIPD", "0.1", "Iba Lab.", args);
		
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
