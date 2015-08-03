import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.simtoolsgui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.analysis.*;
import swarm.*;

public class ObserverSwarm extends GUISwarmImpl{
    public int displayFrequency; // one parameter: update freq
    // probeで操作できるようにするためにはpublicでなければならない
    
    ActionGroup displayActions; // schedule data structs
    Schedule displaySchedule;
    
    ModelSwarm modelSwarm; // the Swarm we're observing

    EZGraph numGraph;
    
    Colormap colorMap; // allocate colours
    ZoomRaster worldRaster; // 2d display widget
    
    Object2dDisplay bugDisplay; // display the heatbugs
    
    public ObserverSwarm(Zone aZone){
	
	super(aZone);
	
	displayFrequency=1;
	
	EmptyProbeMap probeMap;
	probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
	
	probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
			  ("displayFrequency",this.getClass()));
        
	Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
    }
    
    public Object buildObjects(){
	super.buildObjects();
	
	modelSwarm = (ModelSwarm)Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm");
	Globals.env.createArchivedProbeDisplay (modelSwarm, "modelSwarm");
	Globals.env.createArchivedProbeDisplay (this, "observerSwarm");
	getControlPanel().setStateStopped();
	
	modelSwarm.buildObjects();
	
	colorMap=new ColormapImpl(this);
	colorMap.setColor$ToName((byte)0,"black");//キャストが必要
	colorMap.setColor$ToName((byte)1,"yellow");
	colorMap.setColor$ToName((byte)2,"blue");
	colorMap.setColor$ToName((byte)3,"green");
	colorMap.setColor$ToName((byte)4,"orange");
	
	worldRaster=new ZoomRasterImpl(this);
	worldRaster.setColormap(colorMap);
	worldRaster.setZoomFactor(4);
	worldRaster.setWidth$Height(modelSwarm.getWorld().getSizeX(),
				    modelSwarm.getWorld().getSizeY());
	worldRaster.setWindowTitle("Sexual Selection");
	worldRaster.pack(); // draw the window.
	
	try {
	    bugDisplay = new Object2dDisplayImpl
		(this,
		 worldRaster,
		 modelSwarm.getWorld(),
		 new Selector(Class.forName("Bug"), "drawSelfOn", false));
	} catch (Exception e) {
	    System.out.println ("Exception: " + e.getMessage ());
	    System.exit(1);
	}
	
	bugDisplay.setObjectCollection(modelSwarm.getBugList());
	
	try {
	    worldRaster.setButton$Client$Message
		(3,bugDisplay,new Selector(bugDisplay.getClass(),
					   "makeProbeAtX$Y",true));
        } catch (Exception e) {
	    System.out.println ("Exception: " + e.getMessage ());
	    System.exit(1);
	}
        
	numGraph=new EZGraphImpl
	    (this,"Number of bug","Time","Number","numGraph");
	try {
	    numGraph.createSequence$withFeedFrom$andSelector
		("male_t1",modelSwarm,new Selector
		    (modelSwarm.getClass(),"getNumT1", false));
	    numGraph.createSequence$withFeedFrom$andSelector
		("male_t0",modelSwarm,new Selector
		    (modelSwarm.getClass(),"getNumT0", false));
	    numGraph.createSequence$withFeedFrom$andSelector
		("female_p1",modelSwarm,new Selector
		    (modelSwarm.getClass(),"getNumP1", false));
	    numGraph.createSequence$withFeedFrom$andSelector
		("female_p0",modelSwarm,new Selector
		    (modelSwarm.getClass(),"getNumP0", false));
	} catch (Exception e) {
	    System.err.println ("Exception: " + e.getMessage());
	}

	return this;
    }
    
    public Object buildActions(){
	super.buildActions();
	
	modelSwarm.buildActions();
	
	displayActions=new ActionGroupImpl(this);
	
	try {
	    displayActions.createActionTo$message
		(bugDisplay,
		 new Selector(bugDisplay.getClass(),"display",false));
	    displayActions.createActionTo$message
		(worldRaster,
		 new Selector(worldRaster.getClass(),"drawSelf",false));
	    displayActions.createActionTo$message
		(numGraph,
		 new Selector(numGraph.getClass(),"step",false));
	    displayActions.createActionTo$message
		(getActionCache(),
		 new Selector(getActionCache().getClass(),"doTkEvents",true));
	} catch (Exception e) {
	    System.out.println ("Exception: " + e.getMessage ());
	    System.exit(1);
	}
	
	displaySchedule = new ScheduleImpl(this,displayFrequency);
	displaySchedule.at$createAction(0,displayActions);
	
	return this;
    }
    
    public Activity activateIn(Swarm context){
    	super.activateIn(context);
    	
	modelSwarm.activateIn(this);
	
	displaySchedule.activateIn(this);
	
	return getActivity();
    }
}
