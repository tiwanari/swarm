import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

public class ObserverSwarm extends GUISwarmImpl{
	public int displayFrequency;
	public int worldSizeX;
	public int worldSizeY;
	
	ActionGroup displayActions; // schedule data structs
	Schedule displaySchedules;
	
	Colormap colorMap;
	ZoomRaster worldRaster;
	LGAWorld world;
	
	Value2dDisplay valueDisplay;
	
	public ObserverSwarm(Zone aZone){
		// Superclass createBegin to allocate ourselves.
		super(aZone);
		
		// Fill in the relevant parameters
		displayFrequency=1;
		worldSizeX = 300;
		worldSizeY = 300;
		
		EmptyProbeMap probeMap;
		probeMap = new EmptyProbeMapImpl(aZone, this.getClass());
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("displayFrequency",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("worldSizeX",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("worldSizeY",this.getClass()));
		
		// Now install our custom proveMap into the probeLibrary.
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
		
	}
	public Object buildObjects(){
		super.buildObjects();
		
		Globals.env.createArchivedProbeDisplay(this, "observerSwarm");
		
		getControlPanel().setStateStopped();
		
		// First, create a colormap: this is a global resource, the information
		
		colorMap = new ColormapImpl(this);

		colorMap.setColor$ToName((byte)0,"#000000");

		colorMap.setColor$ToName((byte)1,"#333333");
		colorMap.setColor$ToName((byte)2,"#333333");
		colorMap.setColor$ToName((byte)3,"#666666");
		colorMap.setColor$ToName((byte)4,"#333333");
		colorMap.setColor$ToName((byte)5,"#666666");
		colorMap.setColor$ToName((byte)6,"#666666");
		colorMap.setColor$ToName((byte)7,"#999999");
		colorMap.setColor$ToName((byte)8,"#333333");
		colorMap.setColor$ToName((byte)9,"#666666");
		colorMap.setColor$ToName((byte)10,"#666666");
		colorMap.setColor$ToName((byte)11,"#999999");
		colorMap.setColor$ToName((byte)12,"#666666");
		colorMap.setColor$ToName((byte)13,"#999999");
		colorMap.setColor$ToName((byte)14,"#999999");
		colorMap.setColor$ToName((byte)15,"#CCCCCC");
		
//		System.out.println("colormap");
		// Next, create a 2d window for display, set its size, zoom factor, title
		
		worldRaster = new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(1);
		worldRaster.setWidth$Height(worldSizeX, worldSizeY);
		worldRaster.setWindowTitle("LGA's world");
		worldRaster.pack();

		
		world = new LGAWorld(this, worldSizeX, worldSizeY);
		// LGAWorld(this) cannnot initialize DblBuffer2d correctly.
				
		world.setSizeX$Y(worldSizeX,worldSizeY);
		world.eraseAll();
		world.init();
		world.setObserver(this);
		
		try{
			valueDisplay = new Value2dDisplayImpl(this, worldRaster, colorMap, world);
		}catch(Exception e){
			System.err.println("Exception: " + e.getMessage());
			System.exit(1);
		}

		try{
			worldRaster.setButton$Client$Message(3, world, new Selector(world.getClass(), "swapColorAtX$Y",false));
		}catch(Exception e){
			System.err.println("Exception: " + e.getMessage());
			System.exit(1);
		}
		return this;
	}
	public Object buildActions(){
		
		// Create an ActionGroup for display
		displayActions = new ActionGroupImpl(this);
		
		// Schedule up the methods to draw the display of the world
		try{
			displayActions.createActionTo$message(valueDisplay, new Selector(valueDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(worldRaster, new Selector(worldRaster.getClass(),"drawSelf",false));
			displayActions.createActionTo$message(world, new Selector(world.getClass(),"stepRule",false));
			displayActions.createActionTo$message(getActionCache(), new Selector(getActionCache().getClass(),"doTkEvents",true));
		}catch (Exception e){
			System.err.println("Exception: " + e.getMessage());
			System.exit(1);
		}
		displaySchedules = new ScheduleImpl(this,displayFrequency);
		displaySchedules.at$createAction(0,displayActions);
		
		return this;
	}
	
	public Activity activateIn(Swarm context){
		super.activateIn(context);
		
		// Now activate our schedule in ourselves.
		// This arranges for the excution of the schedule we built.
		
		displaySchedules.activateIn(this);
		
		// Activate returns the swarm activity
		
		return getActivity();
	}
	
	public Object eraseLGA(){
		if(world.getClass()!=null)
			world.eraseAll();
		return this;
	}
}