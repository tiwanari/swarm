import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;

/**
 * ここではBugに新しいフィールド（内部変数）FoodSpaceを追加する。
 * メソッドsetFoodSpaceによって、Bugは自分がアクセスするFoodSpaceを知ることができる。
 * setFoodSpace(f)によって、FoodSpaceそのものが与えられるわけではなく、
 * FoodSpaceの参照が与えられることに注意（C++ならsetFoodSpace(&f)と書くところである）。<BR>
 * <BR>
 * メソッドstepは、FoorSpaceにアクセスし、自分がいる場所に餌があるなら（つまりFoosSpaceの値が1）それを食べ、
 * メッセージを表示し、FoodSpaceの値を0にする。
 */

public class Bug extends SwarmObjectImpl {
	int xPos, yPos;
	int worldXSize, worldYSize;
	FoodSpace foodSpace;
	
	public Bug(Zone aZone){
		super(aZone);
	}
	
	public Object setFoodSpace(FoodSpace f){
		foodSpace=f;
		return this;
	}
	
	public Object setX$Y(int x, int y){
		xPos = x;
		yPos = y;
		System.out.println("I started at X = " + xPos + " Y = " + yPos + "\n");
		return this;
	}
	
	public Object setWorldSizeX$Y(int xSize, int ySize){
		worldXSize=xSize;
		worldYSize=ySize;
		return this;
	}
	
	public Object step(){
		xPos = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		yPos = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		
		xPos = (xPos + worldXSize) % worldXSize;
		yPos = (yPos + worldYSize) % worldYSize;
		
		if (foodSpace.getValueAtX$Y(xPos,yPos) == 1){
			foodSpace.putValue$atX$Y(0,xPos,yPos);
			System.out.println("I found food at X = " + xPos + " Y = " + yPos +"!");
		}
		return this;
	}
}
