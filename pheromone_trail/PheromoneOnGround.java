import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;

public class PheromoneOnGround extends Discrete2dImpl{

    PheromoneSpace pheromoneSpace;
    double evaporationRate;

    public void setEvaporationRate(double e){
	evaporationRate = e; //蒸発係数
    }
    
    public PheromoneOnGround(Zone aZone,int x,int y){
	super(aZone,x,y);
    }

    public void setPheromoneSpace(PheromoneSpace p){
	pheromoneSpace = p;
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

	/*
	 * 地上に残されたフェロモン（ground）は蒸発し、
	 * 蒸発したフェロモン（pheromone）に、アリは反応する
	 */

	for(int x = 0; x < xsize; x++){
	    for(int y = 0; y < ysize; y++){
		int ground = this.getValueAtX$Y(x,y);
		if(ground>0){
		    int pheromone = pheromoneSpace.getValueAtX$Y(x,y);
		    pheromone = (int)(pheromone + evaporationRate * ground);
		    pheromoneSpace.putValue$atX$Y(pheromone, x, y);
		    this.putValue$atX$Y( (int)(ground - evaporationRate * ground), x, y );
		}
	    }
	}
    }
}
