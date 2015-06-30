import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import java.io.*;

public class SumOfSpaces extends Discrete2dImpl{

    int colonySize;
    FoodSpace foodSpace;
    PheromoneSpace pheromoneSpace;
    int stepCounter;
    boolean isNotOver;
    int r1,r2,r3,r4,r5,r6,r7,r8,r9,r0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y0;
    boolean isNotOver1, isNotOver2, isNotOver3, isNotOver4, isNotOver5, isNotOver6, isNotOver7, isNotOver8, isNotOver9, isNotOver0;
    int step1, step2, step3, step4, step5, step6, step7, step8, step9, step0;
		
    
    public SumOfSpaces(Zone aZone,int x,int y){
	super(aZone,x,y);
    }

    public Object setSpaces(FoodSpace f,PheromoneSpace h){
	foodSpace = f;
	pheromoneSpace = h;
	return this;
    }

    public void setStepCounter(int counter){
	stepCounter = counter;
	isNotOver = true;
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
	
	if (r1 > 0) isNotOver1 = true;
	else isNotOver1 = false;
	if (r2 > 0) isNotOver2 = true;
	else isNotOver2 = false;
	if (r3 > 0) isNotOver3 = true;
	else isNotOver3 = false;
	if (r4 > 0) isNotOver4 = true;
	else isNotOver4 = false;
	if (r5 > 0) isNotOver5 = true;
	else isNotOver5 = false;
	if (r6 > 0) isNotOver6 = true;
	else isNotOver6 = false;
	if (r7 > 0) isNotOver7 = true;
	else isNotOver7 = false;
	if (r8 > 0) isNotOver8 = true;
	else isNotOver8 = false;
	if (r9 > 0) isNotOver9 = true;
	else isNotOver9 = false;
	if (r0 > 0) isNotOver0 = true;
	else isNotOver0 = false;
	
	step1 = 0;
	step2 = 0;
	step3 = 0;
	step4 = 0;
	step5 = 0;
	step6 = 0;
	step7 = 0;
	step8 = 0;
	step9 = 0;
	step0 = 0;
	
    }
    
    public void setColonySize(int co){
	colonySize = co;
    } 

    public Object getFoodMap(){
	int x,y;
	int xsize,ysize;
	
	xsize=this.getSizeX();
	ysize=this.getSizeY();

	for (y = 0; y < ysize; y++)
	    {
		for (x = 0; x < xsize; x++)
		    {
			int z = foodSpace.getValueAtX$Y(x,y);
			this.putValue$atX$Y(z,x,y);
			if ((x-xsize/2)*(x-xsize/2)+(y-ysize/2)*(y-ysize/2) < colonySize*colonySize)
			    this.putValue$atX$Y(3,x,y);
		    }
	    }
	
	return this;
    }

    public void step(){
	int xsize=this.getSizeX();
	int ysize=this.getSizeY();
	int numOfFood = 0;
	
	for(int x = 0; x < xsize; x++){
	    for(int y = 0; y < ysize; y++){
		this.putValue$atX$Y(0,x,y);
		if (foodSpace.getValueAtX$Y(x,y) == 1){
		    this.putValue$atX$Y(1,x,y);
		    numOfFood++;
		} else {
		    if (pheromoneSpace.getValueAtX$Y(x,y)>0){
			int z = pheromoneSpace.getValueAtX$Y(x,y);
			if (z > 232){
			    this.putValue$atX$Y(242,x,y);
			} else {
			    this.putValue$atX$Y(z+10,x,y);
			    // 表示の関係で、10プラス
			}
		    }	    
		}
		if ((x-xsize/2)*(x-xsize/2)+(y-ysize/2)*(y-ysize/2) < colonySize*colonySize)
		    this.putValue$atX$Y(3,x,y);
	    }
	}
	
	stepCounter++;
	
	//output();
	
	if(numOfFood == 0 && isNotOver) {
	    System.out.println("All Food are searched : " + stepCounter + "step");
	    isNotOver = false;
	}
	
	
    }

    public void output(){

	int xsize=this.getSizeX();
	int ysize=this.getSizeY();
	int numOfFood = 0;
	
	int numOfFood1 = 0;
	int numOfFood2 = 0;
	int numOfFood3 = 0;
	int numOfFood4 = 0;
	int numOfFood5 = 0;
	int numOfFood6 = 0;
	int numOfFood7 = 0;
	int numOfFood8 = 0;
	int numOfFood9 = 0;
	int numOfFood0 = 0;
	
	for (int y = 0; y < ysize; y++) {
	    for (int x = 0; x < xsize; x++) {
		if (foodSpace.getValueAtX$Y(x,y) == 1){
		    numOfFood++;
		    if (r1 > 0 && (x - x1%xsize)*(x - x1%xsize) + (y - y1%ysize)*(y - y1%ysize) < r1*r1 ) numOfFood1++;
		    if (r2 > 0 && (x - x2%xsize)*(x - x2%xsize) + (y - y2%ysize)*(y - y2%ysize) < r2*r2 ) numOfFood2++;
		    if (r3 > 0 && (x - x3%xsize)*(x - x3%xsize) + (y - y3%ysize)*(y - y3%ysize) < r3*r3 ) numOfFood3++;
		    if (r4 > 0 && (x - x4%xsize)*(x - x4%xsize) + (y - y4%ysize)*(y - y4%ysize) < r4*r4 ) numOfFood4++;
		    if (r5 > 0 && (x - x5%xsize)*(x - x5%xsize) + (y - y5%ysize)*(y - y5%ysize) < r5*r5 ) numOfFood5++;
		    if (r6 > 0 && (x - x6%xsize)*(x - x6%xsize) + (y - y6%ysize)*(y - y6%ysize) < r6*r6 ) numOfFood6++;
		    if (r7 > 0 && (x - x7%xsize)*(x - x7%xsize) + (y - y7%ysize)*(y - y7%ysize) < r7*r7 ) numOfFood7++;
		    if (r8 > 0 && (x - x8%xsize)*(x - x8%xsize) + (y - y8%ysize)*(y - y8%ysize) < r8*r8 ) numOfFood8++;
		    if (r9 > 0 && (x - x9%xsize)*(x - x9%xsize) + (y - y9%ysize)*(y - y9%ysize) < r9*r9 ) numOfFood9++;
		    if (r0 > 0 && (x - x0%xsize)*(x - x0%xsize) + (y - y0%ysize)*(y - y0%ysize) < r0*r0 ) numOfFood0++;
		}
	    }
	}
	
	if(numOfFood1 == 0 && isNotOver1) {
	    step1 = stepCounter;
	    isNotOver1 = false;
	}
	if(numOfFood2 == 0 && isNotOver2) {
	    step2 = stepCounter;
	    isNotOver2 = false;
	}
	if(numOfFood3 == 0 && isNotOver3) {
	    step3 = stepCounter;
	    isNotOver3 = false;
	}
	if(numOfFood4 == 0 && isNotOver4) {
	    step4 = stepCounter;
	    isNotOver4 = false;
	}
	if(numOfFood5 == 0 && isNotOver5) {
	    step5 = stepCounter;
	    isNotOver5 = false;
	}
	if(numOfFood6 == 0 && isNotOver6) {
	    step6 = stepCounter;
	    isNotOver6 = false;
	}
	if(numOfFood7 == 0 && isNotOver7) {
	    step7 = stepCounter;
	    isNotOver7 = false;
	}
	if(numOfFood8 == 0 && isNotOver8) {
	    step8 = stepCounter;
	    isNotOver8 = false;
	}
	if(numOfFood9 == 0 && isNotOver9) {
	    step9 = stepCounter;
	    isNotOver9 = false;
	}
	if(numOfFood0 == 0 && isNotOver0) {
	    step0 = stepCounter;
	    isNotOver0 = false;
	}
	
	if(numOfFood == 0 && isNotOver) {
	    
	    FileWriter fw;
	    try{
		fw = new FileWriter("result.txt");
	    } catch (IOException e) {
		System.out.println("write File can not open");
		fw = null;
	    }
	    
	    try{
		
		PrintWriter out = new PrintWriter(fw);
		
		out.print(stepCounter + " ");
		out.print(step1 + " ");
		out.print(step2 + " ");
		out.print(step3 + " ");
		out.print(step4 + " ");
		out.print(step5 + " ");
		out.print(step6 + " ");
		out.print(step7 + " ");
		out.print(step8 + " ");
		out.print(step9 + " ");
		out.print(step0 + " ");
		
		fw.close();
				
	    } catch (Exception e) {}
	
	    // isNotOver == false;
	    
	}
    }
}














