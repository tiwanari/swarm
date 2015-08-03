import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import swarm.collections.*;

public class Cell extends SwarmObjectImpl {
	
	int offset,width, height;
	int ioff, joff;
	Array cellVector;
	
	int iMax, jMax;
	double reyn; //Reynols number
	double f[], ftmp[];
	double rho, u, ux, uy;
	double rho0, ux0, uy0;
	double tau, nu, invtau, one_minus_invtau;
	double w_rho, ichi_minus_hayasa, ux_times_3, uy_times_3, ux2_times_45, uy2_times_45, uplus_times_45, uminus_times_45;

	
	double f0[];
	double w[];
	double dt; //delta t
	double mach;
	int boardLen;//=6;
	int near[][]={{0,0}, {1,0}, {0,1}, {-1,0}, {0,-1}, {1,1}, {-1,1}, {-1,-1}, {1,-1}};
	public int board[][];
	
	public Cell(Zone aZone,int Offset, int IMax, int JMax, int Board[][], int BoardLen, Array aVector){
		super(aZone);
		offset=Offset;
		cellVector=aVector;
		
		jMax = JMax;
		iMax = IMax;
		ioff = offset % (iMax + 2);
		joff = offset / (iMax + 2);
		board = Board;
		boardLen = BoardLen;
		
		f = new double[9];
		ftmp = new double[9];
		f0 = new double[9];
		
		w = new double[9];  //d2q9 model
		w[0] = 4.0/9.0;
		for( int i=1; i<=4; i++ ){
			w[i] = 1.0 / 9.0;
		}
		for( int i=5; i<=8; i++ ){
			w[i] = 1.0 / 36.0;
		}
		mach = 0.1; //Mach number

	}
	public int moff( int ioff, int joff ){
		return (iMax + 2) * joff + ioff;
	}
	
	public double getU(){
	 	return Math.sqrt(ux*ux*2500000 + uy*uy*2500000);
	}
	public double getRho(){
		return rho;
	}
	
	public void initialize(){
		
		double w_rho, ichi_minus_hayasa, ux_times_3, uy_times_3, ux2_times_45, uy2_times_45, uplus_times_45, uminus_times_45;

		dt = mach * (1.0 / Math.sqrt(3.0));  //delta t
		u = dt; // =1.0 * dt / a;
		tau = 3.0 * (double)boardLen * dt / reyn + 0.5;         //ŠÉ˜aŽžŠÔƒÑ
		invtau = 1.0 / tau;
		one_minus_invtau = 1.0 - invtau;
		
		rho0 = 1.0;
		ux0 = u; //+x•ûŒü‚É—¬‚ê
		uy0 = 0.0; //0.0*u
		ux = ux0;
		uy = uy0;
		
		ux_times_3 = 3.0 * ux0;
		uy_times_3 = 3.0 * uy0;
		ux2_times_45 = 4.5 * ux0 * ux0;
		uy2_times_45 = 4.5 * uy0 * uy0;
		ichi_minus_hayasa = 1.0 - 1.5 * (ux0*ux0 + uy0*uy0);
		for( int k=0; k<9; k++ ){
			f0[k] = w[k]*rho0 
				* ( ichi_minus_hayasa + (double)near[k][0]*ux_times_3 + (double)near[k][1]*uy_times_3 
					+ (double)4.5*Math.pow( near[k][0]*ux0 + (double)near[k][1]*uy0, 2 ) );
		}
		
		for( int k=0; k<9; k++ ){
			f[k] = f0[k];
		}
	}
	
	public void setParams( double Reyn ){
		reyn = Reyn;
	}
	
	public void step1(){
		double inv_rho;
		//–§“x
		rho = 0.0;
		for( int k=0; k<9; k++ ){
			rho += f[k];
		}
		
		//‘¬“xê		
		inv_rho = 1.0 / rho;
		ux = inv_rho * ( f[1] - f[3] + f[5] - f[6] - f[7] + f[8] );
		uy = inv_rho * ( f[2] - f[4] + f[5] + f[6] - f[7] - f[8] );
	}
		
	public void step2(){ //collision
		
		ux_times_3 = 3.0 * ux;
		uy_times_3 = 3.0 * uy;
		ux2_times_45 = 4.5 * ux * ux;
		uy2_times_45 = 4.5 * uy * uy;
		ichi_minus_hayasa = 1.0 - 1.5 * (ux*ux + uy*uy);
		
		f[0]    = one_minus_invtau * f[0] + w[0] * rho * ichi_minus_hayasa * invtau;
		for( int k=1; k<9; k++ ){
			ftmp[k] = one_minus_invtau * f[k] + w[k] * rho * invtau
				* ( ichi_minus_hayasa + (double)near[k][0]*ux_times_3 + (double)near[k][1]*uy_times_3 
					+ 4.5*Math.pow( (double)near[k][0]*ux + (double)near[k][1]*uy, 2 ) );
		}
	}
	
		
	public void step3(){

		for( int k=1; k<9; k++ ){
			if( ioff < board[0][0]-1 || ioff > board[9][0]+1 ){
					((Cell)cellVector.atOffset( moff(ioff+near[k][0], joff+near[k][1]) )).putF(k, ftmp[k]);
			}else{
				for( int l=0; l<10; l++ ){
					if( ioff+near[k][0] == board[l][0] && joff+near[k][1] == board[l][1] && near[k][0]!=0 ){
						if( (l==0 && k!=8) || (l==4 && k!=5) || (l==5 && k!=6) || (l==9 && k!=7) )
							((Cell)cellVector.atOffset( moff(ioff+near[k][0], joff+near[k][1]) )).putF(k, ftmp[k]);
						else ((Cell)cellVector.atOffset( moff( board[l][0],  board[l][1]) )).f[k] += ftmp[k];
					}
					else if( ioff == board[l][0] && joff == board[l][1] 
						&& (l<5 && near[k][0]>0 || l>=5 && near[k][0] < 0) 
						&& ((l!=0 || k!=8) && (l!=4 || k!=5) && (l!=5 || k!=6) && (l!=9 || k!=7)) ){
							
						continue;
					}
					else{
						((Cell)cellVector.atOffset( moff(ioff+near[k][0], joff+near[k][1]) )).putF(k, ftmp[k]);
					}
				}
			}
		}
	}
		
	public void step4(){ //periodic array  (ioff, 1)
		f[2] = ((Cell)cellVector.atOffset( moff(ioff, jMax+1) )).getF(2);
		((Cell)cellVector.atOffset( moff(ioff+1, 1))).putF(5,  ((Cell)cellVector.atOffset( moff(ioff+1, jMax+1) )).getF(5));
		((Cell)cellVector.atOffset( moff(ioff-1, 1))).putF(6,  ((Cell)cellVector.atOffset( moff(ioff-1, jMax+1) )).getF(6));		

		((Cell)cellVector.atOffset( moff(ioff,   jMax) )).putF(4,  ((Cell)cellVector.atOffset( moff(ioff,   0) )).getF(4));
		((Cell)cellVector.atOffset( moff(ioff-1, jMax) )).putF(7,  ((Cell)cellVector.atOffset( moff(ioff-1, 0) )).getF(7));
		((Cell)cellVector.atOffset( moff(ioff+1, jMax) )).putF(8,  ((Cell)cellVector.atOffset( moff(ioff+1, 0) )).getF(8));		
	}
	
	public void step5(){ //inflow   ioff=1
		
		for( int k=0; k<9; k++ ){
			f[k] = f0[k];
		}
	}
	public void step6(){  // outflow  ioff=iMax
		
		step1();
		ux=((Cell)cellVector.atOffset( moff(ioff-1, joff) )).ux;
		
		ux_times_3 = 3.0 * ux;
		uy_times_3 = 3.0 * uy;
		ux2_times_45 = 4.5 * ux * ux;
		uy2_times_45 = 4.5 * uy * uy;
		ichi_minus_hayasa = 1.0 - 1.5 * (ux*ux + uy*uy);

		for( int k=0; k<9; k++ ){
			f[k] = w[k] * rho
				* ( ichi_minus_hayasa + (double)near[k][0]*ux_times_3 + (double)near[k][1]*uy_times_3 
					+ 4.5*Math.pow( (double)near[k][0]*ux + (double)near[k][1]*uy, 2 ) );
		}
		
	}
	public void step7(){ //•½”Â@”S’…ðŒ
		
		step1();
				
		ux=0.0; //—¬‘¬‚Í•½”Â‚ÌˆÚ“®‘¬“x‚Æ“™‚µ‚¢‚Æ‚·‚é
		//uy=0.0;
		
		ux_times_3 = 3.0 * ux;
		uy_times_3 = 3.0 * uy;
		ux2_times_45 = 4.5 * ux * ux;
		uy2_times_45 = 4.5 * uy * uy;
		ichi_minus_hayasa = 1.0 - 1.5 * (ux*ux + uy*uy);

		for( int k=0; k<9; k++ ){
			f[k] = w[k] * rho
				* ( ichi_minus_hayasa + (double)near[k][0]*ux_times_3 + (double)near[k][1]*uy_times_3 
					+ 4.5*Math.pow( (double)near[k][0]*ux + (double)near[k][1]*uy, 2 ) );
		}

	}
	
	public void putF( int k, double val ){
		f[k] = val;
	}
	public double getF( int k ){
		return f[k];
	}
}
