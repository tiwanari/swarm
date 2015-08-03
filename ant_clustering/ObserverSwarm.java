import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.simtoolsgui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

public class ObserverSwarm extends GUISwarmImpl{
	int displayFrequency; // one parameter: update freq
	
	ActionGroup displayActions; // schedule data structs
	Schedule displaySchedule;
	
	ModelSwarm modelSwarm; // the Swarm we're observing
	
	// Lots of display objects. First, widgets
	
	Colormap colorMap; // allocate colours
	ZoomRaster worldRaster; // 2d display widget
	
	// Now, higher order display and data objects
	
	Object2dDisplay dataDisplay; 
	Object2dDisplay antDisplay; 	

	public ObserverSwarm(Zone aZone){
		// Superclass createBegin to allocate ourselves.
		super(aZone);
		modelSwarm = new ModelSwarm(aZone);
		// Fill in the relevant parameters (only one, in this case).
		displayFrequency=1;
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("resizeWindow",this.getClass()));
		// Now install our custom proveMap into the probeLibrary.
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
	}
	public void resizeWindow(){
		worldRaster.setWidth$Height(
				modelSwarm.worldXSize,
				modelSwarm.worldYSize);
		worldRaster.pack(); // draw the window.
//		modelSwarm.resetWorld();
	}
	public Object buildObjects(){
		super.buildObjects();
		

                Globals.env.createArchivedProbeDisplay (modelSwarm,
                                            "modelSwarm");
                Globals.env.createArchivedProbeDisplay (this, "observerSwarm");

		// Instruct the control panel to wait for a button event.
		// We halt here until someone hits a control panel button.

		getControlPanel().setStateStopped();
		
		// OK - the user said "go" so we're ready to start

		
		
		modelSwarm.buildObjects();
		
		// FIXME: DEFINE COLORS ACCORDING TO THE OBJECTS BUILT
		
		colorMap=new ColormapImpl(this);
		// Background
		colorMap.setColor$ToRed$Green$Blue((byte) 0, 0.0, 0.0, 0.0);
		// Loaded and unloaded ants:
		colorMap.setColor$ToRed$Green$Blue((byte) 1, 0.7, 0.7, 0.7);
		colorMap.setColor$ToRed$Green$Blue((byte) 2, 0.45, 0.45, 0.45);
		// Objects in cluster 1 and 2:
		colorMap.setColor$ToRed$Green$Blue((byte) 3, 1.0, 0.0, 0.0);
		colorMap.setColor$ToRed$Green$Blue((byte) 4, 0.0, 0.0, 1.0);

		// FIXME: END - fix colors

		// Next, create a 2d window for display, set its size, zoom factor, title.
		
		worldRaster=new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(10);
		worldRaster.setWidth$Height(
			modelSwarm.worldXSize,
			modelSwarm.worldYSize);
		worldRaster.setWindowTitle("Ant Clustering");
		worldRaster.pack(); // draw the window.

		try {
			antDisplay = new Object2dDisplayImpl(
				this,
				worldRaster,
				modelSwarm.getAntSpace(),
				new Selector(Class.forName("Ant"), "drawSelfOn", false));
		} catch (Exception e) {
			System.exit(1);
		}
		antDisplay.setObjectCollection(modelSwarm.getAntList());
	
		try {
			dataDisplay = new Object2dDisplayImpl(
				this,
				worldRaster,
				modelSwarm.getDataSpace(),
				new Selector(Class.forName("DataUnit"), "drawSelfOn", false));
		} catch (Exception e) {
			System.exit(1);
		}
		dataDisplay.setObjectCollection(modelSwarm.getDataList());
	
		return this;
	}
	
	public Object buildActions(){
		
		modelSwarm.buildActions();
		
		displayActions=new ActionGroupImpl(this);
		
		// Schedule up the methods to draw the display of the world
		
		try {
		    displayActions.createActionTo$message(worldRaster,
				new Selector(worldRaster.getClass(),"erase",false));
		    displayActions.createActionTo$message(dataDisplay,
				new Selector(dataDisplay.getClass(),"display",false));
		    displayActions.createActionTo$message(antDisplay,
				new Selector(antDisplay.getClass(),"display",false));
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
