import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
    public int randomSeed;
    public int worldXSize, worldYSize;
    public double t1Rate, p1Rate; // $B=i4|8DBNCf$N(BT1$B!"(BP1$B$N3d9g(B
    public int initialBugSum;
    /*
     * bugA0$B!"(BbugA1$B$O$=$l$>$l!"(B
     * $B;s$,(BbugP=0$B$N9%$_!"(BbugP=1$B$N9%$_$r;}$C$F$$$?>l9g$K(B
     * $B9%$_$G$O$J$$7A<A$NM:$G$"$C$?$H$7$F$b(B
     * $B@8?#Aj<j$H$7$F5vMF$9$k3NN((B
     * A0 < A1$B!!$G$"$l$P!";s$O@8$-;D$j$d$9$$M:$r$h$j9%$`$H$$$($k(B
     */
    public double bugA0, bugA1;
    public double extinctProbability; // $B@dLG$9$k3NN((B
    public int visibility; // $B;s$N;k3&!JM:$rC5$9!";R6!$r;:$`HO0O!KH>7B(B
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

		// $BI,$:$7$b=i4|8DBN$NAm?t$O(BinitialBugSum$B$H$O$J$i$J$$(B
		boolean live;
		if (Globals.env.uniformDblRand.getDoubleWithMin$withMax
		    (0.0,1.0) < (double)initialBugSum / (worldXSize*worldYSize)){
		    live = true;
		    bugSum++;
		} else {
		    live = false; 
		}
		// 1/2$B$N3NN($G;sM:$r7h$a$k(B
		int bugSex = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1);
		// $B3NN($G(BT$B!J7A<A!K$H(BP$B!J9%$_!K$N0dEA;R$r7h$a$k(B
		int bugT = ( t1Rate > Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) ) ? 1 : 0 ;
		int bugP = ( p1Rate > Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) ) ? 1 : 0 ;
		
		// $BCn$r:n$k(B
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
		
		// $B%j%9%H$K2C$($k(B
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

