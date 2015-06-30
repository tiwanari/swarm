import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import swarm.collections.*;

public class Bug extends SwarmObjectImpl {
    public int xPos, yPos;
    public int worldXSize, worldYSize;
    public int bugSex; // 雄：0　雌：1
    /*
     * bugTは形質、bugPは好み
     * T1（bugT=1）の雄はT0（bugT=0）の雄より生存し難い
     */
    public int bugT, bugP; 
    
    public boolean live;
    
    double bugA0, bugA1;
    double extinctProbability; // 絶滅する確率
    int visibility; // 雌の視界半径
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
	    // 形質T1（bugT=1）を持つ雄は一定の確率sで死ぬ
	    // また、Bugの数によって、間引きされる
	    if (stepCount%3 == 1) exterminate();
	    // 雌は周囲にいる雄の中から生殖相手を選び、子供を作る
	    if (stepCount%3 == 2 && bugSex == 1) reproduce();
	    // ランダムに移動
	    if (stepCount%3 == 0) move();
	}
	stepCount++;
    }

    public void exterminate(){
	// 生存に不利な形質の雄を殺す
	if ( bugSex == 0 && bugT == 1){
	    if ( Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < extinctProbability ){
		live = false;
		modelSwarm.bugSum--;
		modelSwarm.numT1 --;
	    }
	}
	// Bugの総数による間引き
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
	// 周りにいる雄から生殖相手を選ぶ

	/*
	 * 周りにいる雄を探す
	 * 雄がいた場合、形質に応じて、その総数と位置を記憶
	 *
	 * また、後で用いるため、空白の場所を探して、その位置を記憶
	 */
	int[] maleSum = new int[2];
	maleSum[0] = 0; // 形質がT0である雄の総数
	maleSum[1] = 0; // 形質がT1である雄の総数
	int blankSum = 0; // 空白の総数
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

	// 雌の好みに応じて、生殖相手を決定
	Bug pairBug = null;
	
	if (maleSum[0]+maleSum[1]>0){
	    // 雄がいた場合、その中の形質の頻度に依存して生殖相手を決定する
	    if (maleSum[(bugP+1)%2] == 0
		|| (double)(bugP==0?bugA0:bugA1)*maleSum[bugP]
		/(maleSum[(bugP+1)%2]+(bugP==0?bugA0:bugA1)*maleSum[bugP])
		> Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)){
		//ランダムに雄を選択
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
		//ランダムに雄を選択
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
	
	// pairBugとの間で子供を作る（または、子供を作らずに終わる）
	if (pairBug != null && blankSum > 0) {
	    Bug newBug;
	    
	    // 子供を産む位置を決める
	    int n = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0, blankSum-1);
	    int newX = blankPos[n][0];
	    int newY = blankPos[n][1];
	    
	    // 雌雄（1/2の確率でランダム）と遺伝子を決める
	    int newSex = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1);
	    int newT = 
		(Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1) == 0) ? bugT : pairBug.bugT;
	    int newP = 
		(Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1) == 0) ? bugP : pairBug.bugT;
	    
	    // 虫を作る
	    newBug = (Bug)world.getObjectAtX$Y(newX,newY);
	    newBug.setSex$Gene(newSex, newT, newP);
	    newBug.live = true;
	    newBug.stepCount = stepCount+1;
	    
	    // リストを整理
	    bugList.remove(newBug);
	    bugList.addFirst(newBug);

	    // 虫の総数を更新する
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
     *   ┌形質T1 - 色2
     * 雄│
     *   └形質T0 - 色4
     *
     *   ┌好みP1 - 色1
     * 雌│
     *   └好みP0 - 色3
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








