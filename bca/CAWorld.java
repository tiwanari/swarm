import swarm.*;
import swarm.space.*;
import swarm.defobj.*;

public class CAWorld extends DblBuffer2dImpl {
	ObserverSwarm observer;
	int xsize, ysize;
	int rule;
	double Alpha;
	String Signal1,Signal2;
	int sig1ct,sig2ct;
	int[] binary_rule;
	
	public CAWorld(Zone aZone, int x, int y){
		super(aZone, x, y);
		binary_rule = new int [8];
	}
	
	public Object setRule(double num,String sig1, String sig2){
		Alpha = num;
		Signal1 = sig1;
		Signal2 = sig2;
		return this;
	}
	
	public Object setAlpha(double num){
		Alpha = num;
		return this;
	}
	
	public Object setSignal1(String sig1){
		Signal1 = sig1;
		return this;
	}
	
	public Object setSignal2(String sig2){
		Signal2 = sig2;
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
		int[] m = new int [xsize];
		int[] v = new int [3];
		int index;
		//makeBinaryRule();
		
		for(x=0; x<xsize; x++){
		if( Math.random() < Alpha )m[x]=1;
		else m[x]=0;
		}
		
		sig1ct++;
		sig2ct++;
		if(sig1ct>=Signal1.length())sig1ct=0;
		if(sig2ct>=Signal2.length())sig2ct=0;

		m[xsize/3]= (Signal1.charAt(sig1ct))-48;
		m[xsize*2/3]= (Signal2.charAt(sig2ct))-48;
		
		for(x=0; x<xsize; x++){
			
			int l=1;
			int[] binary = new int[3];

			for(i=0;i<8;i++){
				int tmp = i;
				for(j=0;j<3;j++){
					binary[j] = tmp%2;
					tmp/=2;
				}
				
				if(x>0){
				binary_rule[i]=binary[1] + Math.min(m[x],Math.min(binary[0],l-binary[1]))
				-Math.min(m[x-1],Math.min(binary[1], l-binary[2]));
				}
				else {
				binary_rule[i]=binary[1] + Math.min(m[0],Math.min(binary[0],l-binary[1]))
					-Math.min(m[xsize-1],Math.min(binary[1], l-binary[2]));
				}
			}
				
				
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