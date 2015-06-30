import swarm.*;

/**
 * Kirkpatrickが提案したモデルによる性選択のシミュレーション<BR>
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author 神田敦
 **/
public class SexualSelection{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("SexualSelection", "1.0", "Iba Lab.", args);
		
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}