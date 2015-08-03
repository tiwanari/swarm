import swarm.*;

/**
 * 餌がまかれた2次元グリッド上をBugが歩き回る<BR>
 * <BR>
 * BugはFoodSpaceという2次元グリッド上を歩く。
 * FoodSpaceの各グリッドにはseedProbの確率で餌がまかれている。
 * Bugは餌を見つけるとそれを食べる。<BR>
 * <BR>
 * FoodSpaceもオブジェクトであり、simpleObjCBug2中でnewによって作られる。
 * 生成されたFoodSpaceには、メソッドseedFoodWithProbによって餌がまかれる。<BR>
 * <BR>
 * BugもsimpleObjCBugの時と同様に作られる。
 * 生成されたBugは、メソッドsetFoodSpaceによって自分のFoodSpaceを知る。
 * Bugのメイン・メソッドはstepであるが、このメソッドはFoodSpaceにアクセスするように修正されている。<BR>
 * <BR>
 * <IMG src="../simpleObjCBug2.png" border="0"><BR>
 * <BR>
 * 次はsimpleSwarmBug
 * @author YABUKI Taro
 * @version 0.4
 */

public class simpleObjCBug2{
	public static void main(String[] args) {
		int worldXSize = 80;
		int worldYSize = 80;
		
		int xPos = 40;
		int yPos = 40;
		
		double seedProb=0.5; // Density of distribution of "food"
		
		int i;
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		// Create and initialize a "food" space
		
		FoodSpace foodSpace=new FoodSpace(Globals.env.globalZone,worldXSize,worldYSize);
		
		foodSpace.seedFoodWithProb(seedProb);
		
		Bug aBug=new Bug(Globals.env.globalZone);
		aBug.setFoodSpace(foodSpace);
		aBug.setWorldSizeX$Y(worldXSize,worldYSize);
		
		aBug.setX$Y(xPos,yPos);
		
		for(i = 0; i < 100; ++i)
			aBug.step();
	}
}
