import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
    public int randomSeed;
    public int worldXSize, worldYSize;
    public double t1Rate, p1Rate; // 初期個体中のT1、P1の割合
    public int initialBugSum;
    /*
     * bugA0、bugA1はそれぞれ、
     * 雌がbugP=0の好み、bugP=1の好みを持っていた場合に
     * 好みではない形質の雄であったとしても
     * 生殖相手として許容する確率
     * A0 < A1　であれば、雌は生き残りやすい雄をより好むといえる
     */
    public double bugA0, bugA1;
    public double extinctProbability; // 絶滅する確率
    public int visibility; // 雌の視界（雄を探す、子供を産む範囲）半径
    public int bugSumLimit;
    
    Grid2d world;
    
    List bugList;
    int stepCount;
    int bugSum;
    int numT1, numT0, numP1, numP0;
    
    ActionGroup modelActions;
    Schedule modelSchedule;
    
    public ModelSwarm(Zone aZone){
	super(aZone);
	
	randomSeed = 5;
	worldXSize = 80;
	worldYSize = 80;
	t1Rate = 0.2;
	p1Rate = 0.8;
	initialBugSum = 200;
	bugA0 = 0.8;
	bugA1 = 0.9;
	extinctProbability = 0.05;
	visibility = 2;
	bugSumLimit = 200;
	stepCount = 0;
	bugSum = 0;
	numT1 = 0;
	numT0 = 0;
	numP1 = 0;
	numP0 = 0;

    }
    
    public Object buildObjects(){
	Bug aBug;
	int x,y;
	
	world=new Grid2dImpl(this,worldXSize,worldYSize);
	world.fillWithObject(null);
	
	bugList=new ListImpl(this);

	for (y = 0; y < worldYSize; y++){
	    for (x = 0; x < worldXSize; x++){
		aBug = new Bug(this, this);

		// 必ずしも初期個体の総数はinitialBugSumとはならない
		boolean live;
		if (Globals.env.uniformDblRand.getDoubleWithMin$withMax
		    (0.0,1.0) < (double)initialBugSum / (worldXSize*worldYSize)){
		    live = true;
		    bugSum++;
		} else {
		    live = false; 
		}
		// 1/2の確率で雌雄を決める
		int bugSex = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1);
		// 確率でT（形質）とP（好み）の遺伝子を決める
		int bugT = ( t1Rate > Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) ) ? 1 : 0 ;
		int bugP = ( p1Rate > Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) ) ? 1 : 0 ;
		
		// 虫を作る
		aBug.setWorld$List$StepCount
		    (world, bugList, stepCount, live);
		aBug.setX$Y(x,y);
		aBug.setParameter(bugA0, bugA1, extinctProbability, visibility, bugSumLimit);
		aBug.setSex$Gene(bugSex, bugT, bugP);
		if (live){
		    if (bugSex == 0){
			if (bugT == 1) numT1 ++;
			else numT0 ++;
		    } else {
			if (bugP == 1) numP1 ++;
			else numP0 ++;
		    }
		}
		world.putObject$atX$Y(aBug,x,y);
		
		// リストに加える
		if (live) bugList.addFirst(aBug);
		else bugList.addLast(aBug);
		
	    }
	}
	Globals.env.randomGenerator.setStateFromSeed(randomSeed);
	return this;
    }
    
    public Object buildActions(){
	modelActions=new ActionGroupImpl(this);
	try{
	    modelActions.createActionForEach$message(bugList,
						     new Selector(Class.forName("Bug"),"step",false));
	} catch (Exception e) {
	    e.printStackTrace (System.err);
	    System.exit(1);
	}
	
	modelSchedule=new ScheduleImpl(this,1);
	modelSchedule.at$createAction(0,modelActions);
	return this;
    }
    
    public Activity activateIn(Swarm context){
    	super.activateIn (context);
    	modelSchedule.activateIn(this);
	return getActivity();
    }
    
    public Grid2d getWorld(){
	return world;
    }
    
    public List getBugList(){
	return bugList;
    }
    
    public int getNumT1(){
	return numT1;
    }

    public int getNumT0(){
	return numT0;
    }
    
    public int getNumP1(){
	return numP1;
    }
    
    public int getNumP0(){
	return numP0;
    }

}

