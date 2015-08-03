import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

public class ObstacleSpace extends Discrete2dImpl{
  public ObstacleSpace(Zone aZone,int x,int y){
    super(aZone,x,y);
  }
  public Object setObstacle(double obstacleProb, int obstacleSize){
    int x,y;
    int i,j;
    int xsize,ysize;
    
    xsize=this.getSizeX();
    ysize=this.getSizeY();
    
    for (y = 0; y < ysize; y++) {
      for (x = 0; x < xsize; x++) {
	if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)
	   <obstacleProb) {
	  for(i=-obstacleSize; i<=obstacleSize; i++) {
	    int width = (int)(Math.sqrt(obstacleSize*obstacleSize - i*i));
	    for(j=-width; j<=width; j++) {
	      this.putValue$atX$Y(1,x+i,y+j);
	    }
	  }
	}
      }
    }
    return this;
  }
}
