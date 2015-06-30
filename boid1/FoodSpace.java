import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

public class FoodSpace extends Discrete2dImpl{
	public FoodSpace(Zone aZone,int x,int y){
		super(aZone,x,y);
	}
	public Object seedFoodWithProb(double seedProb){
		int x,y;
		int xsize,ysize;
		
		xsize=this.getSizeX();
		ysize=this.getSizeY();
		
		for (y = 0; y < ysize; y++)
			for (x = 0; x < xsize; x++)
				if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)
				<seedProb)
					this.putValue$atX$Y(1,x,y);
		return this;
	}
}
