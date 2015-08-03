import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.simtoolsgui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

public class ObserverSwarm extends GUISwarmImpl{
	public int displayFrequency; // one parameter: update freq
	
	ActionGroup displayActions; // schedule data structs
	Schedule displaySchedule;
	
	ModelSwarm modelSwarm; // the Swarm we're observing
	
	// Lots of display objects. First, widgets
	
	Colormap colorMap; // allocate colours
	ZoomRaster worldRaster; // 2d display widget
	
	// Now, higher order display and data objects
	
	Value2dDisplay obstacleDisplay; // display the heat
	Object2dDisplay bugDisplay; // display the heatbugs
	
	public ObserverSwarm(Zone aZone){
		
		// Superclass createBegin to allocate ourselves.
		
		super(aZone);
		
		// Fill in the relevant parameters (only one, in this case).
		
		displayFrequency=1;
		
		// Also, build a customized probe map. Without a probe map, the default
		// is to show all variables and messages. Here we choose to
		// customize the appearance of the probe, give a nicer interface.
		
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		// Add in a bunch of variables, one per simulation parameters
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("displayFrequency",this.getClass()));
        
      	// Now install our custom probeMap into the probeLibrary.
        
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
	}
	
	public Object buildObjects(){
		super.buildObjects();
		
		
		modelSwarm = (ModelSwarm)
			Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm"); //エラーだった場合の次の処理は未実装
		//raiseEvent(InvalidOperation,"Can't find the modelSwarm parameters");
		
		Globals.env.createArchivedProbeDisplay (modelSwarm,
                                            "modelSwarm");
		Globals.env.createArchivedProbeDisplay (this, "observerSwarm");
		
		
		getControlPanel().setStateStopped();
		
		
		modelSwarm.buildObjects();
		
		
		colorMap=new ColormapImpl(this);
		colorMap.setColor$ToName((byte)0,"black");//キャストが必要
		colorMap.setColor$ToName((byte)1,"red");
		colorMap.setColor$ToName((byte)2,"green");
		
		
		worldRaster=new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(4);
		worldRaster.setWidth$Height(
			modelSwarm.getWorld().getSizeX(),
			modelSwarm.getWorld().getSizeY());
		worldRaster.setWindowTitle("Obstacle Space");
		worldRaster.pack(); // draw the window.
		
		// Now create a Value2dDisplay: this is a special object that will
		// display arbitrary 2d value arrays on a given Raster widget.
		
		obstacleDisplay=new Value2dDisplayImpl(
			this,worldRaster,colorMap,modelSwarm.getObstacle());
		
		// And also create an Object2dDisplay: this object draws bugs on
		// the worldRaster widget for us.
		
		try {
			bugDisplay = new Object2dDisplayImpl(
				this,
				worldRaster,
				modelSwarm.getWorld(),
				new Selector(Class.forName("Bug"), "drawSelfOn", false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		bugDisplay.setObjectCollection(modelSwarm.getBugList());
		
		// Also, tell the world raster to send mouse clicks to the heatbugDisplay
		// this allows the user to right-click on the display to probe the bugs.
		
		try {
			worldRaster.setButton$Client$Message(
				3,bugDisplay,new Selector(bugDisplay.getClass(),
                                          "makeProbeAtX$Y",true));
        } catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
        
		return this;
	}
	
	/**
	 * Create the actions necessary for the simulation.
	 */
	public Object buildActions(){
		super.buildActions();
		
		// First, let our model swarm build its own schedule.
		
		modelSwarm.buildActions();
		
		// Create an ActionGroup for display.
		
		displayActions=new ActionGroupImpl(this);
		
		// Schedule up the methods to draw the display of the world
		
		try {
			displayActions.createActionTo$message(obstacleDisplay,
				new Selector(obstacleDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(bugDisplay,
				new Selector(bugDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(worldRaster,
				new Selector(worldRaster.getClass(),"drawSelf",false));
			displayActions.createActionTo$message(
				getActionCache(),
				new Selector(getActionCache().getClass(),"doTkEvents",true));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		// And the display schedule. Note the repeat interval is set from our
		// own Swarm data structure. Display is frequently the slowest part of a
		// simulation, so redrawing less frequently can be a help.
		
		displaySchedule = new ScheduleImpl(this,displayFrequency);
		displaySchedule.at$createAction(0,displayActions);
		
		return this;
    }
	
	/**
	 * activateIn: - activate the schedules so they're ready to run.
	 */
	public Activity activateIn(Swarm context){
    	super.activateIn(context);
    	
    	// Activate the model swarm in ourselves. The model swarm is a
    	// subswarm of the observer swarm.
    	
		modelSwarm.activateIn(this);
		
		// Now activate our schedule in ourselves. This arranges for the
		// execution of the schedule we built.
		
		displaySchedule.activateIn(this);
		
		// Activate returns the swarm activity - the thing that's ready to run.
		
		return getActivity();
	}
}
