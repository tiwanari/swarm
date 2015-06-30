import swarm.*;
import swarm.space.*;
import swarm.defobj.*;

public class CAWorld extends DblBuffer2dImpl {
	ObserverSwarm observer;
	int xsize, ysize;
	int rule;
	int[] binary_rule;
	
	public CAWorld(Zone aZone, int x, int y){
		super(aZone, x, y);
		binary_rule = new int [8];
	}
	
	public Object setRule(int num){
		rule = num;
		if(rule > 255) rule = 255;
		if(rule < 0 ) rule = 0;
		makeBinaryRule();
		return this;
	}
	public Object randomize(){
		int i,v;
		for(i=0;i<xsize;i++){
			v = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1);
			this.putValue$atX$Y(v, i,0);
		}
		this.updateLattice();
		return this;
	}
	public void makeBinaryRule(){
		int i;
		int tmp = rule;
		for(i=0;i<8;i++){
			binary_rule[i] = tmp%2;
			tmp/=2;
		}
		System.out.print("Decimal:" + rule + ", Binary: ");
		for(i=7;i>-1;i--){
			System.out.print(binary_rule[i]);
		}
		System.out.println();
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
		int sx;
		int xm1, xp1, ym1, yp1;
		int i,j;
		int[] v = new int [3];
		int index;
		
		for(x=0; x<xsize; x++){
			y=0;
			for(i=-1;i<2;i++){
				sx = (x+i+xsize)%xsize;
				v[i+1] = this.getValueAtX$Y(sx, y);
			}
			index = v[0]*4 + v[1]*2 + v[2];
			
			if( binary_rule[index] == 1) newState = 1;
			else newState = 0;
			this.putValue$atX$Y(newState,x,y);
			
			for(y=1; y<ysize; y++){
				newState = this.getValueAtX$Y(x,y-1);
				this.putValue$atX$Y(newState,x,y);
			}
		}
		this.updateLattice();
		return this;
	}
}