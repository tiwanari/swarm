import swarm.*;
import swarm.space.*;
import swarm.defobj.*;

public class TrafficWorld extends DblBuffer2dImpl {
	private int xsize, ysize;
	
	//�z��̏㉺���E�̗v�f���w��
	//�����m�����C���o�m�����C�J�ڊm��p�C�������z���w��
	public double probability = 0.9;
	public double distribution = 0;
	public double alpha = 0.1;
	public double beta = 0.1;
	
	public TrafficWorld(Zone aZone, int x, int y){
		super(aZone, x, y);
	}
	
	public Object setSizeX$Y(int x, int y){
		xsize = x;
		ysize = y;
		return this;
	}

	public void setASEPParam(double p, double a, double b, double d){
		probability = p;
		alpha = a;
		beta = b;
		distribution = d;
	}
	
	public Object swapColorAtX$Y(int x, int y){
		int oldState = this.getValueAtX$Y(x,y);
		
		int newState = (oldState == 1) ? 0 : 1;
		this.putValue$atX$Y(newState,x,y);
				
		return this;
	}
	
	public Object eraseAll(){
		for(int i=0;i<xsize;i++){
			for(int j=0;j<ysize;j++){
				this.putValue$atX$Y(0,i,j);
			}
		}
		return this;
	}

	public Object init(){
		//������
		//�������z�萔�����ƂɎԂ������_���ɔz�u
		for(int i=0;i<xsize;i++){
			int j = ysize-1;
			if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < distribution){
				this.putValue$atX$Y(1, i,j);
			}else{
				this.putValue$atX$Y(0, i,j);
			}
		}
		this.updateLattice();
		return this;
	}

	public Object stepRule(){
		//�X�e�b�v���Ԃ��Ƃɔz��������ړ�
		for(int x=0; x<xsize; x++){
			for(int y=0;y<ysize-1;y++){
				// north
				int south = this.getValueAtX$Y(x, y+1);
				this.putValue$atX$Y(south, x, y);
			
			}
		}
		
		calcNextState();
		this.updateLattice();
		return this;
	}
	
	private void calcNextState(){
		int x;
		int y = ysize-1;
		
		// �Ōォ��k���Čv�Z����
		calcOutflow();
		for(x=xsize-2;x>=0;x--){
			int east = getValueAtX$Y(x+1, y);
			int here = getValueAtX$Y(x, y);

			if(here == 0){
				putValue$atX$Y(0, x, y);
			}else{
				if(east == 0){
					if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) <= probability){
						putValue$atX$Y(1, x+1, y);
						putValue$atX$Y(0, x, y);
					}else{
						putValue$atX$Y(1, x, y);
					}
				}else{
					putValue$atX$Y(1, x, y);
				}
			}
		}
		calcInflow();
	}
	
	private void calcInflow(){
		int x = 0;
		int y = ysize-1;
		if(getValueAtX$Y(x,y) == 0){
			if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < alpha){
				putValue$atX$Y(1, x, y);
			}else{
				putValue$atX$Y(0, x, y);
			}
		}
	}
	
	private void calcOutflow(){
		// beta
		int x = xsize-1;
		int y = ysize-1;
		if(getValueAtX$Y(x,y) == 1){
			if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < beta){
				putValue$atX$Y(0, x, y);
			}else{
				putValue$atX$Y(1, x, y);
			}
		}else{
			putValue$atX$Y(0, x, y);
		}
	}
}
