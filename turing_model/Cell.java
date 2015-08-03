import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import swarm.collections.*;

public class Cell extends SwarmObjectImpl {
	public double u,v,u1,u2,v1,v2,meanV2,nu;
	
	int ru,rv;
	double w1,w2,m0,m1,p,d,e,initProb;
	
	int offset,width;
	Array cellVector;
	
	public Cell(Zone aZone,int Offset,int Width,Array aVector){
		super(aZone);
		offset=Offset;
		width=Width;
		cellVector=aVector;
	}
	
	public double getU(){
		return u;
	}
	
	public void initialize(){
		if(Globals.env.uniformDblRand.getDoubleWithMin$withMax
			(0.0,1.0)<initProb)
			u=1;
		else
			u=0;
		v=0;
		u1=0;
		u2=0;
		v1=0;
		v2=0;
		meanV2=0;
		nu=0;
	}
	
	public void setParams(int Ru,int Rv,
		double W1,double W2,double M0,double M1,double P,
		double D,double E,double InitProb){
		ru=Ru;
		rv=Rv;
		w1=W1;
		w2=W2;
		m0=M0;
		m1=M1;
		p=P;
		d=D;
		e=E;
		initProb=InitProb;
	}
	
	public void step1(){
		if(v>=1) v1=java.lang.Math.round(v*(1-d)-e);
		else v1=0;
	}
		
	public void step2(){
		if(u==0){
			if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)<p)
				u1=1;
			else u1=0;// ‚±‚Ì•”•ªA˜_•¶‚É‘‚¢‚Ä‚È‚¢‚¯‚Ç‘åŽ–
		} else u1=u;
	}
		
	public void step3(){
		if(u1==1) v2=v1+w1;
		else v2=v1;
	}
		
	public void step4(){
		nu=0;
		for(int Ru=-ru;Ru<=ru;++Ru){
			nu+=((Cell)cellVector.atOffset((offset+Ru+width)%width)).getU();
		}
		if(u1==0 && nu>java.lang.Math.round(m0+m1*v2)) u2=1;
		else u2=u1;
	}
	
	public void step5(){
		meanV2=0;
		for(int Rv=-rv;Rv<=rv;++Rv){
			meanV2+=((Cell)cellVector.atOffset((offset+Rv+width)%width)).getV2();
		}
		meanV2/=(2.*rv+1.);
		v=(int)java.lang.Math.round(meanV2);
	}
	
	public void step6(){
		if(v>=w2)
			u=0;
		else
			u=u2;
	}
	
	public double getV2(){
		return v2;
	}
}
