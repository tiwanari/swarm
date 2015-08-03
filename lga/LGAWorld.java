import swarm.*;
import swarm.space.*;
import swarm.defobj.*;

public class LGAWorld extends DblBuffer2dImpl {
	ObserverSwarm observer;
	int xsize, ysize;
	
	public final static int EAST = 3;
	public final static int WEST = 2;
	public final static int SOUTH = 1;
	public final static int NORTH = 0;
	
	public LGAWorld(Zone aZone, int x, int y){
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

	public Object init(){
		int i,j;
		int tmp;
		int mx, my;
		mx = xsize/2;
		my = ysize/2;
		int r = 20;
		int p;
		
		for(i=0;i<xsize;i++){
			for(j=0;j<ysize;j++){
				p = (i-mx)*(i-mx) + (j-my)*(j-my);
				if( p > r*r ){
					tmp = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,15);
					this.putValue$atX$Y(tmp, i,j);
				}
			}
		}
		this.updateLattice();
		return this;
	}
	private void getBinaryNumber(int in, int out[]){
		int tmp = in;
		for(int i=0; i<4;i++){
			out[i] = tmp%2;
			tmp/=2;
		}
	}
	private int getDecimalNumber(int in[]){
		int base = 1;
		int out = 0;
		for(int i=0; i<4;i++){
			out += in[i] * base;
			base*=2;
		}
		return out;
	}
	public Object stepRule(){
		int newState;
		int x,y;
		int tmp;
		int xm1, xp1, ym1, yp1;
		int east, west, south, north ;
		int[] eb = new int [4];
		int[] wb = new int [4];
		int[] sb = new int [4];
		int[] nb = new int [4];
		int[] newStateBinary = new int [4];
		
		for(x=0; x<xsize; x++){
			for(y=0; y<ysize; y++){
				xm1 = (x + xsize - 1) % xsize;
				xp1 = (x + 1) % xsize;
				
				ym1 = (y + ysize - 1) % ysize;
				yp1 = (y + 1) % ysize;
				
				// east
				east = this.getValueAtX$Y(xp1,y);
				getBinaryNumber(east, eb);
				
				// west
				west = this.getValueAtX$Y(xm1, y);
				getBinaryNumber(west, wb);
				
				// south
				south = this.getValueAtX$Y(x,yp1);
				getBinaryNumber(south, sb);
				
				// north
				north = this.getValueAtX$Y(x, ym1);
				getBinaryNumber(north, nb);
				
				if(eb[WEST] == 1 && wb[EAST] == 1 && sb[NORTH] == 0 && nb[SOUTH] == 0){
					newStateBinary[EAST] = 0;
					newStateBinary[WEST] = 0;
					newStateBinary[SOUTH] = 1;
					newStateBinary[NORTH] = 1;
				}else if(eb[WEST] == 0 && wb[EAST] == 0 && sb[NORTH] == 1 && nb[SOUTH] == 1){
					newStateBinary[EAST] = 1;
					newStateBinary[WEST] = 1;
					newStateBinary[SOUTH] = 0;
					newStateBinary[NORTH] = 0;
				}else{
					newStateBinary[EAST] = wb[EAST];
					newStateBinary[WEST] = eb[WEST];
					newStateBinary[SOUTH] = nb[SOUTH];
					newStateBinary[NORTH] = sb[NORTH];
				}
				newState = getDecimalNumber(newStateBinary);
				this.putValue$atX$Y(newState,x,y);
			}
		}
		this.updateLattice();
		return this;
	}
}
