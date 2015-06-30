import swarm.space.*;
import swarm.defobj.*;
import swarm.collections.*;

public class PatternSpace extends Discrete2dImpl{
	int width,history;
	int num;
	Array cellVector;

	public PatternSpace(Zone aZone,int x,int y,int num){
		super(aZone,x,y);
		
		width=x;
		history=y;
		this.num = num;
		for(y=0;y<history;++y)
			for(x=0;x<width;++x)
				putValue$atX$Y(127,x,y);

	}

	public void setCellVector(Array aVector){
		cellVector=aVector;
	}

	public void update(){
		int x,y;
		for(y=0;y<history-1;++y)
			for(x=0;x<width;++x)
				putValue$atX$Y(getValueAtX$Y(x,y+1),x,y);

		for(x=0;x<width;++x){
			putValue$atX$Y(127, x, history-1);
		}
		for(int i=0;i<num;i++){
			Car c = (Car) cellVector.atOffset(i);
			putValue$atX$Y(c.getVelocity(),c.getX(), history-1);
		}
//		System.out.println(c.getVelocity() + ", " + c.getX());
	}
}
