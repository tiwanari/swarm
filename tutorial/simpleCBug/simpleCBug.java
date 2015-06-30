import swarm.*;

/**
 * 最初のチュートリアル<BR>
 * <BR>
 * ここには特にSwarmらしいことはない（Swarmの乱数生成ルーチンを使ってはいるが）<BR>
 * simpleCBugのCはC言語のこと。C言語のようにオブジェクト指向でない書き方をしているから<BR>
 * <BR>
 * <IMG src="../simpleCBug.png" border="0"><BR>
 * <BR>
 * 次はsimpleObjcBug
 * @author YABUKI Taro
 * @version 0.4
 */

public class simpleCBug{
	public static void main(String[] args) {
		int worldXSize = 80; // Maximum X value
		int worldYSize = 80; // Maximum Y value
		
		int xPos = 40; // Bug's starting position
		int yPos = 40;
		
		int i;
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);  // Always first in Swarm main
		
		System.out.println("I started at X = " + xPos + " Y = " + yPos + "\n");
		
		for(i = 0; i < 100; ++i){
			// Random movement in X and Y (possibly 0)
			
			xPos = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
			yPos = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
			
			// Take move modulo maximum coordinate values
			xPos = (xPos + worldXSize) % worldXSize;
			yPos = (yPos + worldYSize) % worldYSize;
			
			System.out.println("I moved to X = " + xPos + " Y = " + yPos);
		}
	}
}
