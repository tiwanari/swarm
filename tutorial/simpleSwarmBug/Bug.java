import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;

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
	
	public void step(){
		xPos = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		yPos = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		
		xPos = (xPos + worldXSize) % worldXSize;
		yPos = (yPos + worldYSize) % worldYSize;
		
		if (foodSpace.getValueAtX$Y(xPos,yPos) == 1){
			foodSpace.putValue$atX$Y(0,xPos,yPos);
			System.out.println("I found food at X = " + xPos + " Y = " + yPos +"!");
		}
	}
}
