import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

public class FoodSpace extends Discrete2dImpl{
    
    int r1,r2,r3,r4,r5,r6,r7,r8,r9,r0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y0;
        
    public FoodSpace(Zone aZone,int x,int y){
	super(aZone,x,y);
    }
	
    public void setParams(int ra, int rb, int rc, int rd, int re,
			  int rf, int rg, int rh, int ri, int rj,
			  int xa, int xb, int xc, int xd, int xe,
			  int xf, int xg, int xh, int xi, int xj,
			  int ya, int yb, int yc, int yd, int ye,
			  int yf, int yg, int yh, int yi, int yj){
	r1=ra;
	r2=rb;
	r3=rc;
	r4=rd;
	r5=re;
	r6=rf;
	r7=rg;
	r8=rh;
	r9=ri;
	r0=rj;
	x1=xa;
	x2=xb;
	x3=xc;
	x4=xd;
	x5=xe;
	x6=xf;
	x7=xg;
	x8=xh;
	x9=xi;
	x0=xj;
	y1=ya;
	y2=yb;
	y3=yc;
	y4=yd;
	y5=ye;
	y6=yf;
	y7=yg;
	y8=yh;
	y9=yi;
	y0=yj;
	
    }
    
    public Object giveFoodSpace(){
	int xsize,ysize;	
	xsize=this.getSizeX();
	ysize=this.getSizeY();
	
	for (int y = 0; y < ysize; y++)
	    {
		for (int x = 0; x < xsize; x++)
		    {
			// delete food
			if (r1 < 0 && (x - x1%xsize)*(x - x1%xsize) + (y - y1%ysize)*(y - y1%ysize) < r1*r1 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r2 < 0 && (x - x2%xsize)*(x - x2%xsize) + (y - y2%ysize)*(y - y2%ysize) < r2*r2 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r3 < 0 && (x - x3%xsize)*(x - x3%xsize) + (y - y3%ysize)*(y - y3%ysize) < r3*r3 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r4 < 0 && (x - x4%xsize)*(x - x4%xsize) + (y - y4%ysize)*(y - y4%ysize) < r4*r4 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r5 < 0 && (x - x5%xsize)*(x - x5%xsize) + (y - y5%ysize)*(y - y5%ysize) < r5*r5 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r6 < 0 && (x - x6%xsize)*(x - x6%xsize) + (y - y6%ysize)*(y - y6%ysize) < r6*r6 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r7 < 0 && (x - x7%xsize)*(x - x7%xsize) + (y - y7%ysize)*(y - y7%ysize) < r7*r7 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r8 < 0 && (x - x8%xsize)*(x - x8%xsize) + (y - y8%ysize)*(y - y8%ysize) < r8*r8 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r9 < 0 && (x - x9%xsize)*(x - x9%xsize) + (y - y9%ysize)*(y - y9%ysize) < r9*r9 ){
			    this.putValue$atX$Y(0,x,y);
			}
			if (r0 < 0 && (x - x0%xsize)*(x - x0%xsize) + (y - y0%ysize)*(y - y0%ysize) < r0*r0 ){
			    this.putValue$atX$Y(0,x,y);
			}			

			// add food
			if (r1 > 0 && (x - x1%xsize)*(x - x1%xsize) + (y - y1%ysize)*(y - y1%ysize) < r1*r1 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r2 > 0 && (x - x2%xsize)*(x - x2%xsize) + (y - y2%ysize)*(y - y2%ysize) < r2*r2 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r3 > 0 && (x - x3%xsize)*(x - x3%xsize) + (y - y3%ysize)*(y - y3%ysize) < r3*r3 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r4 > 0 && (x - x4%xsize)*(x - x4%xsize) + (y - y4%ysize)*(y - y4%ysize) < r4*r4 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r5 > 0 && (x - x5%xsize)*(x - x5%xsize) + (y - y5%ysize)*(y - y5%ysize) < r5*r5 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r6 > 0 && (x - x6%xsize)*(x - x6%xsize) + (y - y6%ysize)*(y - y6%ysize) < r6*r6 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r7 > 0 && (x - x7%xsize)*(x - x7%xsize) + (y - y7%ysize)*(y - y7%ysize) < r7*r7 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r8 > 0 && (x - x8%xsize)*(x - x8%xsize) + (y - y8%ysize)*(y - y8%ysize) < r8*r8 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r9 > 0 && (x - x9%xsize)*(x - x9%xsize) + (y - y9%ysize)*(y - y9%ysize) < r9*r9 ){
			    this.putValue$atX$Y(1,x,y);
			}
			if (r0 > 0 && (x - x0%xsize)*(x - x0%xsize) + (y - y0%ysize)*(y - y0%ysize) < r0*r0 ){
			    this.putValue$atX$Y(1,x,y);
			}
		    }
	    }
	return this;
    }
}


