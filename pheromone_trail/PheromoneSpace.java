import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;

public class PheromoneSpace extends Discrete2dImpl{

    double diffusionRate;
    
    public void setDiffusionRate(double d){
	diffusionRate = d; // 拡散係数 0 < diffusionRate < 1/5
    }
    
    public PheromoneSpace(Zone aZone,int x,int y){
	super(aZone,x,y);
    }
    
    public Object nullMap(){
	int x,y;
	int xsize,ysize;
	
	xsize=this.getSizeX();
	ysize=this.getSizeY();
	for (y = 0; y < ysize; y++)
	    {
		for (x = 0; x < xsize; x++)
		    {
			this.putValue$atX$Y(0,x,y);
		    }
	    }
	return this;
    }

    public void step(){
	int xsize=this.getSizeX();
	int ysize=this.getSizeY();
	int[][] nextPheromone;
	nextPheromone = new int[xsize][ysize];
	for(int x = 0; x < xsize; x++){
	    for(int y = 0; y < ysize; y++){
		nextPheromone[x][y] =
		    (int)(this.getValueAtX$Y(x,y)
			  + diffusionRate * (this.getValueAtX$Y((x-1+xsize)%xsize,y)
					     + this.getValueAtX$Y(x,(y-1+ysize)%ysize)
					     + this.getValueAtX$Y(x, (y+1)%ysize)
					     + this.getValueAtX$Y((x+1)%xsize, y)
					     - 5 * this.getValueAtX$Y(x,y)));
	    }
	}
	for(int x = 0; x < xsize; x++){
	    for(int y = 0; y < ysize; y++){
		this.putValue$atX$Y( nextPheromone[x][y], x, y );
	    }
	}
    }
}





