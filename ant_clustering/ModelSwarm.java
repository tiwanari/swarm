// ModelSwarm.java
// The top-level ModelSwarm

import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.Selector;
import swarm.space.*;
import swarm.collections.*;
import java.util.Random;

public class ModelSwarm extends SwarmImpl
{
    // Declare the model parameters and their default values.
    public int worldXSize = 30, worldYSize = 30;
        
    public int nAnts = 40;
    public int nObjs = 100; //FIXME! : Objects currently limited by color number
    public double k_pick = 0.45;
    public double k_drop = 0.2;
    public double k_crowd = 6;
    
    public int sight_range = 1;

    public int random_seed = 1;

    // Declare some other needed variables.
    DataSpace dataSpace;
    Grid2dImpl antSpace;
    ListImpl antList;
    ListImpl dataList;

    Random rGen;

    ScheduleImpl modelSchedule;

    // This is the constructor for a new ModelSwarm. All we do is to
    // use the contructor for ModelSwarm's parent class.
    public ModelSwarm(Zone azone)
    {
    	// Use the parent class to create a top-level swarm.
    	super(azone);
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(azone,this.getClass());
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("worldXSize",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("worldYSize",this.getClass()));
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("nAnts",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("nObjs",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("k_pick",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("k_drop",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("k_crowd",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("sight_range",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("random_seed",this.getClass()));
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("resetWorld",this.getClass()));
		// Now install our custom proveMap into the probeLibrary.
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
    }

    // This is the method for building the model's objects: the food
    // space, the two-dimensional positioning grid, and the host of
    // bugs.
    public Object buildObjects()
    {
    	// use the parent class buildObject() method to initialize the
    	// process
    	super.buildObjects();

    	rGen = new Random(random_seed);

    	// Create the DataSpace
    	dataSpace = new DataSpace(Globals.env.globalZone,
			       worldXSize, worldYSize);

    	// Create the list of objects, and put them into the dataspace
    	dataList = new ListImpl(Globals.env.globalZone);
    	buildDataList();
    	
    	// FIXME: Read data from file

    	// Create the 2d grid where the ants will walk
    	antSpace = new Grid2dImpl(Globals.env.globalZone,
    			worldXSize, worldYSize);
    	antSpace.fastFillWithObject(null);

    	// Now create a List object to manage all the bugs we are
    	// about to create.
    	antList = new ListImpl(Globals.env.globalZone);

    	buildAntList();
    	return this;
    }
    
    // This is the method a) for building the list of actions for
    // these objects to accomplish and b) for scheduling these actions
    // in simulated time.
    public Object buildActions()
    {
    	Selector sel;
    	ActionGroupImpl modelActions;

    	// First, use the parent class to initialize the process.
    	super.buildActions();

    	// Creat and Action group
    	// Messages - for ants - pickData, dropData, randomWalk

    	modelActions = new ActionGroupImpl(getZone());

    	try
    	{
    		sel = new Selector(Class.forName("Ant"),
			     "pickData", false);
    		modelActions.createActionForEach$message(antList, sel);
    	} catch (Exception e)
    	{
    		System.err.println("Exception pickData: " +
			      e.getMessage ());
    		System.exit(1);
    	}
    	try
    	{
    		sel = new Selector(Class.forName("Ant"),
				"randomWalk", false);
    		modelActions.createActionForEach$message(antList, sel);
    	} catch (Exception e)
	    {
    		System.err.println("Exception randomWalk: " +
				    e.getMessage ());
    		System.exit(1);
	     }
    	try
    	{
    		sel = new Selector(Class.forName("Ant"),
				"dropData", false);
    		modelActions.createActionForEach$message(antList, sel);
    	} catch (Exception e)
	    {
    		System.err.println("Exception dropData: " +
				    e.getMessage ());
    		System.exit(1);
	    }


    	// Now create the schedule and set the repeat interval to unity.
    	modelSchedule = new ScheduleImpl(getZone(), 1);

    	// Finally, insert the action list into the schedule at period zero
    	modelSchedule.at$createAction(0, modelActions);

    	return this;
    }

    // This method specifies the context in which the model is to be run.
    public Activity activateIn(Swarm swarmContext)
    {
     // Use the parent class to activate ourselves in the context
     // passed to us.
    	super.activateIn(swarmContext);

     // Then activate the schedule in ourselves.
    	modelSchedule.activateIn(this);

     // Finally, return the activity we have built.
    	return getActivity();
    }

    public DataSpace getDataSpace()
    {
    	return dataSpace;
    }

    public Grid2dImpl getAntSpace()
    {
    	return antSpace;
    }

    public ListImpl getAntList()
    {
    	return antList;
    }

    public ListImpl getDataList()
    {
    	return dataList;
    }
	private void buildAntList(){
	     int i, num;
	     int px, py;
	     Ant aAnt;
	     num = 0;
	     for (i = 0; i < nAnts; i++)
		 {
		     do {
			 px = rGen.nextInt(worldXSize);
			 py = rGen.nextInt(worldYSize);

		     } while (antSpace.getObjectAtX$Y(px, py) != null);
		     aAnt = new Ant(Globals.env.globalZone, dataSpace, antSpace,
				    px,py,++num,k_pick, k_drop, k_crowd, sight_range,rGen);
		     antSpace.putObject$atX$Y(aAnt, px, py);
		     antList.addLast(aAnt);

		 }
	}
	private void buildDataList(){
    	int i, num=0;
    	int px, py;
    	DataUnit aData;
    	double [] v;

    	for (i=0; i<nObjs/2; i++)
    	{
    		do {
    			px = rGen.nextInt(worldXSize);
    			py = rGen.nextInt(worldYSize);

    		} while (dataSpace.getObjectAtX$Y(px, py) != null);

    		v = new double[] { 0.1 + rGen.nextDouble()*0.15,
    				0.1 + rGen.nextDouble()*0.15,
    				0.1 + rGen.nextDouble()*0.15};

    		aData = new DataUnit(Globals.env.globalZone, dataSpace, px, py, ++num, v);

    		dataSpace.putObject$atX$Y(aData, px, py);
    		dataList.addLast(aData);
    	}

    	for (i=(nObjs/2)+1; i<nObjs; i++)
    	{
    		do {
    			px = rGen.nextInt(worldXSize);
    			py = rGen.nextInt(worldYSize);
    		} while (dataSpace.getObjectAtX$Y(px, py) != null);

    		v = new double[] { 0.7 + rGen.nextDouble()*0.15,
    				0.7 + rGen.nextDouble()*0.15,
    				0.7 + rGen.nextDouble()*0.15};

    		aData = new DataUnit(Globals.env.globalZone, dataSpace, px, py, ++num, v);

    		dataSpace.putObject$atX$Y(aData, px, py);
    		dataList.addLast(aData);
    	}

    	dataSpace.setList(dataList);
	
	}
	private void removeDataList(){
		dataList.removeAll();
		dataSpace.fastFillWithObject(null);
	}
	// add by yanase
	private void removeAntList(){
		antList.removeAll();
		antSpace.fastFillWithObject(null);
	}
	/*
	public void resetAntAndData(){
    	rGen = new Random(random_seed);
		removeAntList();
		removeDataList();
		buildDataList();
		buildAntList();
	}
	*/
	public void resetWorld(){
    	rGen = new Random(random_seed);
		removeAntList();
		removeDataList();
    	// Create the DataSpace
    	dataSpace = new DataSpace(Globals.env.globalZone,
			       worldXSize, worldYSize);

    	antSpace = new Grid2dImpl(Globals.env.globalZone,
    			worldXSize, worldYSize);
    	antSpace.fastFillWithObject(null);
		buildDataList();
		buildAntList();
	}
}
