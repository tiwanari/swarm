import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;

/**
 * 餌を食べたらそのことをFoodSpaceに知らせる
 * （FoodSpaceのメソッドbugAteを呼ぶことによって）。
 */
public final class Bug extends SwarmObjectImpl {
	int xPos, yPos;
	int worldXSize, worldYSize;
	FoodSpace foodSpace;
	Grid2d world;
	int haveEaten;

	public Bug(Zone aZone){
		super(aZone);
	}

	public Object setWorld$Food(Grid2d w,FoodSpace f){
		world=w;
		foodSpace=f;
		setWorldSizeX$Y(world.getSizeX(),world.getSizeY());
		return this;
	}

	public Object setX$Y(int x, int y){
		xPos = x;
		yPos = y;
		return this;
	}

	public Object setWorldSizeX$Y(int xSize, int ySize){
		worldXSize=xSize;
		worldYSize=ySize;
		return this;
	}

	public void step(){
		int newX,newY;

		newX = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		newY = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		newX = (newX + worldXSize) % worldXSize;
		newY = (newY + worldYSize) % worldYSize;

		if (world.getObjectAtX$Y(newX,newY) == null){
			world.putObject$atX$Y(null,xPos,yPos);
			setX$Y(newX, newY);
		}

		if (foodSpace.getValueAtX$Y(xPos,yPos) == 1){
			foodSpace.putValue$atX$Y(0,xPos,yPos);
			foodSpace.bugAte();
		}
	}
}
