import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;

/**
 * 本来、変更の必要はないのだが、worldRaster上で右クリックしたときに現れる
 * BugのProbeに、Bugのフィールドの一部を表示するように変更した。
 * ここでは、xPosとyPosをpublicにしたため、これらはProbeに登場する。
 */
public class Bug extends SwarmObjectImpl {
	public int xPos, yPos, x2Pos, y2Pos, x3Pos, y3Pos;
	int worldXSize, worldYSize;
	FoodSpace foodSpace;
	Grid2d world;
	int haveEaten;

	final int[][] dirMOVE={{0,2},{2,1},{2,-1},{0,-2},{-2,-1},{-2,1}};
	final int FOOD_ENERGY=40;
	final int ENERGY_MAX=1500;
	final int INITIAL_ENERGY=5;
	final int MATURE_AGE=800;
	final int REPRODUCTION_ENERGY=1000;
	final int FOOD_INERVAL=200;
	final int AGE_MAX=1000;
	
	static int turn=19;
	
	private int age=0;
	private int energy=INITIAL_ENERGY;
	private int direction=(int)(6*Math.random());
	private int[] dirProb={
			(int)(10*Math.random()),
			(int)(10*Math.random()),
			(int)(10*Math.random()),
			(int)(10*Math.random()),
			(int)(10*Math.random()),
			(int)(10*Math.random())};
			
	
	public Bug(Zone aZone){
		super(aZone);
	}
	
	public Object setDirProb(int[] Prob){
		dirProb = Prob; 
		return this;
	}
	
	public int[] getDirProb(){
		return dirProb;
	}
 	
	public int getEnergy(){
		return energy;
	}
	
	public int getAge(){
		return age;
	}
	
	public Object setAge(int e){
		age= e;
		return this;
	}
	
	public Object setEnergy(int e){
		energy=e;
		return this;
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
		world.putObject$atX$Y(this,xPos,yPos);
		return this;
	}
	
	public Object setWorldSizeX$Y(int xSize, int ySize){
		worldXSize=xSize;
		worldYSize=ySize;
		return this;
	}
	
	public int decideDir(int[] dirProb){
		int[] sum= new int[6];
		sum[0] = dirProb[0];
		for(int i=1;i<6;i++){
			sum[i]=sum[i-1]+dirProb[i];
		}

		int val = (int)(sum[5]*(Math.random()));

		for(int i=0;i<6;i++){
			if(val<sum[i]){
				return i;
			}
		}
		
		return 0;
	}
	

	
	public void step(){
		x3Pos= x2Pos;
		y3Pos= y2Pos;	
		x2Pos= xPos;
		y2Pos= yPos;
		
		
		if(energy>ENERGY_MAX)energy=ENERGY_MAX;
		
		
		if(energy>0){
			
		int newX,newY;
		haveEaten=0;
		direction = (direction+decideDir(dirProb))%6;
		
		newX =xPos + dirMOVE[direction][0];
		newY =yPos + dirMOVE[direction][1]; 
		
		if(newX>=worldXSize || newX<=0)newX=xPos;
		if(newY>=worldYSize || newY<=0)newY=yPos;
		
		newX = (newX + worldXSize) % worldXSize;
		newY = (newY + worldYSize) % worldYSize;
		
		
		if (world.getObjectAtX$Y(newX,newY) == null){
			world.putObject$atX$Y(null,xPos,yPos);
			setX$Y(newX, newY);
		}
		
		for(int i=-1;i<1;i++){
			for(int j=-1;j<1;j++){
				if (foodSpace.getValueAtX$Y(xPos+i,yPos+j) == 1){
					foodSpace.putValue$atX$Y(0,xPos+i,yPos+j);
					haveEaten=1;
					energy+=40;
				}
			}
		}
		
		age++;
		if(age>AGE_MAX)age=AGE_MAX;
		energy--;
		}
	}
	
	

	
	
	public Object drawSelfOn(Raster r){
		if(age > 800){
			r.drawPointX$Y$Color(xPos,yPos,(byte)3);//キャストが必要
			r.drawPointX$Y$Color(x2Pos,y2Pos,(byte)3);
			r.drawPointX$Y$Color((int)((x2Pos+xPos)/2),(int)((y2Pos+yPos)/2),(byte)3);
			r.drawPointX$Y$Color(x3Pos,y3Pos,(byte)3);
			r.drawPointX$Y$Color((int)((x3Pos+x2Pos)/2),(int)((y2Pos+y3Pos)/2),(byte)3);
	
		}
		else if(age>500){
			r.drawPointX$Y$Color(xPos,yPos,(byte)2);//キャストが必要
			//r.drawPointX$Y$Color(x2Pos,y2Pos,(byte)2);
			r.drawPointX$Y$Color((int)((x2Pos+xPos)/2),(int)((y2Pos+yPos)/2),(byte)2);
			//r.drawPointX$Y$Color(x3Pos,y3Pos,(byte)2);
			r.drawPointX$Y$Color((int)((x3Pos+x2Pos)/2),(int)((y2Pos+y3Pos)/2),(byte)2);
	
		}
		else{
			r.drawPointX$Y$Color(xPos,yPos,(byte)2);//キャストが必要
			//r.drawPointX$Y$Color(x2Pos,y2Pos,(byte)2);
			//r.drawPointX$Y$Color((int)((x2Pos+xPos)/2),(int)((y2Pos+yPos)/2),(byte)2);
			//r.drawPointX$Y$Color(x3Pos,y3Pos,(byte)2);
			//r.drawPointX$Y$Color((int)((x3Pos+x2Pos)/2),(int)((y2Pos+y3Pos)/2),(byte)2);
	
		}
		return this;
	}
}
