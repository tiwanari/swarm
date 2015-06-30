import swarm.*;
import swarm.space.*;
import swarm.defobj.*;

public class ConwayWorld extends ConwayLife2dImpl {
	ObserverSwarm observer;
	int xsize, ysize;
	
	public ConwayWorld(Zone aZone, int x, int y){
		super(aZone, x, y);
	}
	
	public Object setSizeX$Y(int x, int y){
		xsize = x;
		ysize = y;
		return this;
	}
	public Object setObserver(ObserverSwarm anObject){
		observer = anObject;
		return this;
	}
	
	public Object swapColorAtX$Y(int x, int y){
		int newState;
		int oldState = this.getValueAtX$Y(x,y);
		
		newState = (oldState == 1) ? 0 : 1;
		this.putValue$atX$Y(newState,x,y);
				
		return this;
	}
	
	public Object eraseAll(){
		int i,j;
		for(i=0;i<xsize;i++){
			for(j=0;j<ysize;j++){
				this.putValue$atX$Y(0,i,j);
			}
		}
		return this;
	}

	public Object stepRule(){
		int newState;
		int x,y;
		int sum;
		int xm1, xp1, ym1, yp1;
		
		for(x=0; x<xsize; x++){
			for(y=0; y<ysize; y++){
				sum = 0;
				xm1 = (x + xsize - 1) % xsize;
				xp1 = (x + 1) % xsize;
				
				ym1 = (y + ysize - 1) % ysize;
				yp1 = (y + 1) % ysize;
				
				sum += this.getValueAtX$Y(xm1, ym1);
				sum += this.getValueAtX$Y(x,ym1);
				sum += this.getValueAtX$Y(xp1,ym1);
				
				sum += this.getValueAtX$Y(xm1,y);
				sum += this.getValueAtX$Y(xp1, y);
				
				sum += this.getValueAtX$Y(xm1, yp1);
				sum += this.getValueAtX$Y(x, yp1);
				sum += this.getValueAtX$Y(xp1, yp1);
				
				if(this.getValueAtX$Y(x,y)==1)
					newState = (sum==2 || sum==3) ? 1 : 0;
				else
					newState = (sum==3) ? 1 :0;
				this.putValue$atX$Y(newState,x,y);
			}
		}
		this.updateLattice();
		return this;
	}
}