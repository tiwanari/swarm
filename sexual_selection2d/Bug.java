import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import swarm.collections.*;

public class Bug extends SwarmObjectImpl {
    public int xPos, yPos;
    public int worldXSize, worldYSize;
    public int bugSex; // $BM:!'(B0$B!!;s!'(B1
    /*
     * bugT$B$O7A<A!"(BbugP$B$O9%$_(B
     * T1$B!J(BbugT=1$B!K$NM:$O(BT0$B!J(BbugT=0$B!K$NM:$h$j@8B8$7Fq$$(B
     */
    public int bugT, bugP; 
    
    public boolean live;
    
    double bugA0, bugA1;
    double extinctProbability; // $B@dLG$9$k3NN((B
    int visibility; // $B;s$N;k3&H>7B(B
    int bugSumLimit;
    
    ModelSwarm modelSwarm;
    
    Grid2d world;
    
    List bugList;
    int stepCount;
    
    public Bug(Zone aZone, ModelSwarm m){
	super(aZone);
	modelSwarm = m;
    }

    public Object setWorld$List$StepCount(Grid2d w, List bL, int c, boolean l){
	world = w;
	bugList = bL;
	stepCount = c;
	live = l;
	worldXSize = world.getSizeX();
	worldYSize = world.getSizeY();
	return this;
    }

    public Object setParameter(double A0, double A1, double eP, int v, int bSL){
	bugA0 = A0;
	bugA1 = A1;
	extinctProbability = eP;
	visibility = v;
	bugSumLimit = bSL;
	return this;
    }
    
    public Object setSex$Gene(int sex, int t, int p){
	bugSex = sex;
	bugT = t;
	bugP = p;
	return this;
    }
    
    public Object setX$Y(int x, int y){
	xPos = x;
	yPos = y;
	return this;
    }

    public Object setWorldSizeX$Y(int xSize, int ySize){
	worldXSize = xSize;
	worldYSize = ySize;
	return this;
    }
    
    public void step(){
	if (live) { 
	    // $B7A<A(BT1$B!J(BbugT=1$B!K$r;}$DM:$O0lDj$N3NN((Bs$B$G;`$L(B
	    // $B$^$?!"(BBug$B$N?t$K$h$C$F!"4V0z$-$5$l$k(B
	    if (stepCount%3 == 1) exterminate();
	    // $B;s$O<~0O$K$$$kM:$NCf$+$i@8?#Aj<j$rA*$S!";R6!$r:n$k(B
	    if (stepCount%3 == 2 && bugSex == 1) reproduce();
	    // $B%i%s%@%`$K0\F0(B
	    if (stepCount%3 == 0) move();
	}
	stepCount++;
    }

    public void exterminate(){
	// $B@8B8$KITMx$J7A<A$NM:$r;&$9(B
	if ( bugSex == 0 && bugT == 1){
	    if ( Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < extinctProbability ){
		live = false;
		modelSwarm.bugSum--;
		modelSwarm.numT1 --;
	    }
	}
	// Bug$B$NAm?t$K$h$k4V0z$-(B
	if (live && bugSumLimit < modelSwarm.bugSum){
	    double p = (double)bugSumLimit / modelSwarm.bugSum;
	    if ( Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) > p ){
		live = false;
		modelSwarm.bugSum--;
		if (bugSex == 0){
		    if (bugT == 1) modelSwarm.numT1 --;
		    else modelSwarm.numT0 --;
		} else {
		    if (bugP == 1) modelSwarm.numP1 --;
		    else modelSwarm.numP0 --;
		}
	    }
	}
    }

    public void reproduce(){
	// $B<~$j$K$$$kM:$+$i@8?#Aj<j$rA*$V(B

	/*
	 * $B<~$j$K$$$kM:$rC5$9(B
	 * $BM:$,$$$?>l9g!"7A<A$K1~$8$F!"$=$NAm?t$H0LCV$r5-21(B
	 *
	 * $B$^$?!"8e$GMQ$$$k$?$a!"6uGr$N>l=j$rC5$7$F!"$=$N0LCV$r5-21(B
	 */
	int[] maleSum = new int[2];
	maleSum[0] = 0; // $B7A<A$,(BT0$B$G$"$kM:$NAm?t(B
	maleSum[1] = 0; // $B7A<A$,(BT1$B$G$"$kM:$NAm?t(B
	int blankSum = 0; // $B6uGr$NAm?t(B
	int[][] maleT0Pos = new int[(visibility*2+1)*(visibility*2+1)-1][2];
	int[][] maleT1Pos = new int[(visibility*2+1)*(visibility*2+1)-1][2];
	int[][] blankPos = new int[(visibility*2+1)*(visibility*2+1)-1][2];
	for (int x = - visibility; x < visibility+1; x++){
	    for (int y = - visibility; y < visibility+1; y++){
		int tmpX = (xPos + x + worldXSize) % worldXSize;
		int tmpY = (yPos + y + worldYSize) % worldYSize;
		Bug aBug = (Bug)world.getObjectAtX$Y(tmpX,tmpY);
		if (aBug.live){
		    if (aBug.bugSex == 0){
			if (aBug.bugT == 0){
			    maleT0Pos[maleSum[0]][0] = tmpX;
			    maleT0Pos[maleSum[0]][1] = tmpY;
			    maleSum[0]++;
			} else {
			    maleT1Pos[maleSum[1]][0] = tmpX;
			    maleT1Pos[maleSum[1]][1] = tmpY;
			    maleSum[1]++;
			}
		    }
		} else {
		    blankPos[blankSum][0] = tmpX;
		    blankPos[blankSum][1] = tmpY;
		    blankSum++;
		}
	    }
	}

	// $B;s$N9%$_$K1~$8$F!"@8?#Aj<j$r7hDj(B
	Bug pairBug = null;
	
	if (maleSum[0]+maleSum[1]>0){
	    // $BM:$,$$$?>l9g!"$=$NCf$N7A<A$NIQEY$K0MB8$7$F@8?#Aj<j$r7hDj$9$k(B
	    if (maleSum[(bugP+1)%2] == 0
		|| (double)(bugP==0?bugA0:bugA1)*maleSum[bugP]
		/(maleSum[(bugP+1)%2]+(bugP==0?bugA0:bugA1)*maleSum[bugP])
		> Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)){
		//$B%i%s%@%`$KM:$rA*Br(B
		int m = Globals.env.uniformIntRand.getIntegerWithMin$withMax
		    (0, maleSum[bugP]-1);
		if (bugP == 0) {
		    pairBug = (Bug)world.getObjectAtX$Y
			(maleT0Pos[m][0], maleT0Pos[m][1]);
		} else {
		    pairBug = (Bug)world.getObjectAtX$Y
			(maleT1Pos[m][0], maleT1Pos[m][1]);
		}
		
	    } else {
		//$B%i%s%@%`$KM:$rA*Br(B
		int m = Globals.env.uniformIntRand.getIntegerWithMin$withMax
		    (0, maleSum[(bugP+1)%2]-1);
		if (bugP == 0) {
		    pairBug = (Bug)world.getObjectAtX$Y
			(maleT1Pos[m][0], maleT1Pos[m][1]);
		} else {
		    pairBug = (Bug)world.getObjectAtX$Y
			(maleT0Pos[m][0], maleT0Pos[m][1]);
		}
	    }
	}
	
	// pairBug$B$H$N4V$G;R6!$r:n$k!J$^$?$O!";R6!$r:n$i$:$K=*$o$k!K(B
	if (pairBug != null && blankSum > 0) {
	    Bug newBug;
	    
	    // $B;R6!$r;:$`0LCV$r7h$a$k(B
	    int n = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0, blankSum-1);
	    int newX = blankPos[n][0];
	    int newY = blankPos[n][1];
	    
	    // $B;sM:!J(B1/2$B$N3NN($G%i%s%@%`!K$H0dEA;R$r7h$a$k(B
	    int newSex = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1);
	    int newT = 
		(Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1) == 0) ? bugT : pairBug.bugT;
	    int newP = 
		(Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1) == 0) ? bugP : pairBug.bugT;
	    
	    // $BCn$r:n$k(B
	    newBug = (Bug)world.getObjectAtX$Y(newX,newY);
	    newBug.setSex$Gene(newSex, newT, newP);
	    newBug.live = true;
	    newBug.stepCount = stepCount+1;
	    
	    // $B%j%9%H$r@0M}(B
	    bugList.remove(newBug);
	    bugList.addFirst(newBug);

	    // $BCn$NAm?t$r99?7$9$k(B
	    modelSwarm.bugSum++;
	    
	    if (newSex == 0){
		if (newT == 1) modelSwarm.numT1 ++;
		else modelSwarm.numT0 ++;
	    } else {
		if (newP == 1) modelSwarm.numP1 ++;
		else modelSwarm.numP0 ++;
	    }
	    
	}
    }
    
    public void move(){
	int newX, newY;
	
	newX = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
	newY = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
	newX = (newX + worldXSize) % worldXSize;
	newY = (newY + worldYSize) % worldYSize;
	
	Bug aBug = (Bug)world.getObjectAtX$Y(newX,newY);
	if (aBug.live == false){
	    world.putObject$atX$Y(null,xPos,yPos);
	    world.putObject$atX$Y(null,newX,newY);
	    aBug.xPos = xPos;
	    aBug.yPos = yPos;
	    world.putObject$atX$Y(aBug,xPos,yPos);
	    xPos = newX;
	    yPos = newY;
	    world.putObject$atX$Y(this,newX,newY);
	}
    }

    /*
     *   $B(#7A<A(BT1 - $B?'(B2
     * $BM:("(B
     *   $B(&7A<A(BT0 - $B?'(B4
     *
     *   $B(#9%$_(BP1 - $B?'(B1
     * $B;s("(B
     *   $B(&9%$_(BP0 - $B?'(B3
     */
    public Object drawSelfOn(Raster r){
	if (live){
	    if (bugSex == 0){
		if (bugT == 1) r.drawPointX$Y$Color(xPos,yPos,(byte)2);
		else r.drawPointX$Y$Color(xPos,yPos,(byte)4);
	    } else {
		if (bugP == 1) r.drawPointX$Y$Color(xPos,yPos,(byte)1);
		else r.drawPointX$Y$Color(xPos,yPos,(byte)3);
	    }
	} else {
	    r.drawPointX$Y$Color(xPos,yPos,(byte)0);
	}
	return this;
    }
}








