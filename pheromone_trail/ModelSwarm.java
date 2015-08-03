import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.Selector;
import swarm.space.*;
import swarm.collections.*;
import java.io.*;

public class ModelSwarm extends SwarmImpl{

    public int worldXSize, worldYSize;
    public int r1,r2,r3,r4,r5,r6,r7,r8,r9,r0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y0;
    public int bugSum;
    public double evaporationRate, diffusionRate;
    public int colonySize, amountOfReleasingPheromone;
    public double awayFromColonyRate, turnRate;
    public int stepCounter;
    public int randomSeed;
    
    FoodSpace food;
    PheromoneSpace pheromoneSpace;
    PheromoneOnGround pheromoneOnGround;
    SumOfSpaces sumOfSpaces;
    
    Grid2d world;
    
    List bugList;
    ActionGroup modelActions;
    Schedule modelSchedule;
    
    public ModelSwarm(Zone aZone){
	
	super(aZone);
	
	r1=4;
	x1=20;
	y1=20;
	r2=6;
	x2=70;
	y2=70;
	r3=7;
	x3=20;
	y3=60;
	r4=0;
	x4=0;
	y4=0;
	r5=0;
	x5=0;
	y5=0;
	r6=0;
	x6=0;
	y6=0;
	r7=0;
	x7=0;
	y7=0;
	r8=0;
	x8=0;
	y8=0;
	r9=0;
	x9=0;
	y9=0;
	r0=0;
	x0=0;
	y0=0;
	
	evaporationRate = 0.05; // 蒸発係数 0 < evaporationRate < 1
	diffusionRate = 0.05; // 拡散係数 0 < diffusionRate < 0.2

	colonySize = 2;
	amountOfReleasingPheromone = 600;
	awayFromColonyRate = 1.2; // awayFromColonyRate > 1
	turnRate = 0.1; // 0 < turnRate < 1
	
	randomSeed = 1;
	
	worldXSize = 100;
	worldYSize = 100;
	bugSum = 60;

	EmptyProbeMap probeMap;
	probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
	
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("randomSeed",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("evaporationRate",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("diffusionRate",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass
			  ("initializeEvaporationAndDiffusionRate",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("colonySize",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("amountOfReleasingPheromone",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("awayFromColonyRate",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("turnRate",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass
			  ("initializeBugAndColonySize",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r1",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x1",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y1",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r2",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x2",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y2",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r3",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x3",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y3",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r4",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x4",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y4",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r5",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x5",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y5",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r6",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x6",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y6",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r7",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x7",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y7",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r8",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x8",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y8",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r9",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x9",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y9",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("r0",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("x0",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("y0",getClass()));
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass
			  ("initializeFood",getClass()));
	
        Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
	
    }
    
    public Object buildObjects(){
	
	Bug aBug;
	
	// Here, we create the objects in the model

	// Create the food/pheromone space and initialize it
	
	food = new FoodSpace(this,worldXSize,worldYSize);
    
	pheromoneSpace = new PheromoneSpace(this,worldXSize,worldYSize);
	pheromoneSpace.nullMap();
	
	pheromoneOnGround = new PheromoneOnGround(this,worldXSize,worldYSize);
	pheromoneOnGround.setPheromoneSpace(pheromoneSpace);
	pheromoneOnGround.nullMap();

	sumOfSpaces = new SumOfSpaces(this,worldXSize,worldYSize);
	sumOfSpaces.setSpaces(food,pheromoneSpace);
	sumOfSpaces.getFoodMap();

	initializeFood();	
	initializeEvaporationAndDiffusionRate();
	
	// Now set up the grid used to represent agent position
	// Grid2d enforces only 1 bug per site
	
	world=new Grid2dImpl(this,worldXSize,worldYSize);
	world.fillWithObject(null);
	
	// Now, create a bunch of bugs to live in the world
	
	bugList=new ListImpl(this);
	
	for (int x = 0; x < bugSum; x++){
	    aBug = new Bug(this);
	    aBug.setWorld$Food(world,food,pheromoneSpace,pheromoneOnGround); //add
	    aBug.setX$Y(worldXSize/2,worldYSize/2);
	    bugList.addLast(aBug);
	}
	
	initializeBugAndColonySize();

	Globals.env.randomGenerator.setStateFromSeed(randomSeed);

	//output();

	return this;
	
    }
    
    public Object buildActions(){
	
	// Create the list of simulation actions.
	
	modelActions=new ActionGroupImpl(this);
	try{
	    modelActions.createActionForEach$message(bugList,
						     new Selector(Class.forName("Bug"),"step",false));
	    modelActions.createActionTo$message(pheromoneSpace,
						new Selector(Class.forName("PheromoneSpace"),"step",false));
	    modelActions.createActionTo$message(pheromoneOnGround,
						new Selector(Class.forName("PheromoneOnGround"),"step",false));
	    modelActions.createActionTo$message(sumOfSpaces,
						new Selector(Class.forName("SumOfSpaces"),"step",false));
	    
	} catch (Exception e) {
	    e.printStackTrace (System.err);
	    System.exit(1);
	}
	
	// Then we create a schedule that executes the modelActions.
	
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

    public SumOfSpaces getSumOfSpaces(){
	return sumOfSpaces;
    }
    
    public List getBugList(){
	return bugList;
    }

    public void initializeBugAndColonySize(){
	for (int x = 0; x < bugSum; x++){
	    Bug aBug = (Bug)(bugList.removeFirst());
	    aBug.setBugParametar
		( colonySize, amountOfReleasingPheromone, awayFromColonyRate, turnRate );
	    bugList.addLast(aBug);
	}
	sumOfSpaces.setColonySize(colonySize);
    }
    
    public void initializeFood(){
	food.setParams(r1,r2,r3,r4,r5,r6,r7,r8,r9,r0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y0);
	sumOfSpaces.setParams(r1,r2,r3,r4,r5,r6,r7,r8,r9,r0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y0);
	food.giveFoodSpace();
	sumOfSpaces.setStepCounter(0);
    }
    
    public void initializeEvaporationAndDiffusionRate(){
	pheromoneOnGround.setEvaporationRate(evaporationRate);
	pheromoneSpace.setDiffusionRate(diffusionRate);
    }

    public void output(){
	
	FileWriter fw;
	try{
	    fw = new FileWriter("environment.txt");
	} catch (IOException e) {
	    System.out.println("write File can not open");
	    fw = null;
	}
	
	try{

	    PrintWriter out = new PrintWriter(fw);
	    
	    out.print(worldXSize + " ");
	    out.print(worldYSize + " ");
	    out.print(bugSum + " ");

	    out.print(randomSeed + " ");
	    
	    out.print(evaporationRate + " ");
	    out.print(diffusionRate + " ");
	    
	    out.print(colonySize + " ");
	    out.print(amountOfReleasingPheromone + " ");
	    out.print(awayFromColonyRate + " ");
	    out.print(turnRate + " ");
	    
	    out.print(r1 + " ");
	    out.print(x1 + " ");
	    out.print(y1 + " ");
	    out.print(r2 + " ");
	    out.print(x2 + " ");
	    out.print(y2 + " ");
	    out.print(r3 + " ");
	    out.print(x3 + " ");
	    out.print(y3 + " ");
	    out.print(r4 + " ");
	    out.print(x4 + " ");
	    out.print(y4 + " ");
	    out.print(r5 + " ");
	    out.print(x5 + " ");
	    out.print(y5 + " ");
	    out.print(r6 + " ");
	    out.print(x6 + " ");
	    out.print(y6 + " ");
	    out.print(r7 + " ");
	    out.print(x7 + " ");
	    out.print(y7 + " ");
	    out.print(r8 + " ");
	    out.print(x8 + " ");
	    out.print(y8 + " ");
	    out.print(r9 + " ");
	    out.print(x9 + " ");
	    out.print(y9 + " ");
	    out.print(r0 + " ");
	    out.print(x0 + " ");
	    out.print(y0 + " ");
	    
	    fw.close();
	    
	} catch (Exception e) {}

    }
    
}


