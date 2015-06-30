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
	public int Rule;
	public double Alpha;
	public String Signal1;
	public String Signal2;
	
	ActionGroup displayActions; // schedule data structs
	Schedule displaySchedules;
	
	Colormap colorMap;
	ZoomRaster worldRaster;
	CAWorld caWorld;
	
	Value2dDisplay valueDisplay;
	
	public ObserverSwarm(Zone aZone){
		// Superclass createBegin to allocate ourselves.
		super(aZone);
		
		// Fill in the relevant parameters
		displayFrequency=1;
		worldSizeX = 80;
		worldSizeY = 80;
		Alpha = 1.0;
		Signal1="1";
		Signal2="1";
		
		EmptyProbeMap probeMap;
		probeMap = new EmptyProbeMapImpl(aZone, this.getClass());
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("displayFrequency",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("worldSizeX",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("worldSizeY",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("Alpha",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("Signal1",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("Signal2",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("applyRule",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("randomize",getClass()));
		
		// Now install our custom proveMap into the probeLibrary.
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
		
	}
	public Object buildObjects(){
		super.buildObjects();
		
		Globals.env.createArchivedProbeDisplay(this, "observerSwarm");
		
		getControlPanel().setStateStopped();
		
		// First, create a colormap: this is a global resource, the information
		
		colorMap = new ColormapImpl(this);
		colorMap.setColor$ToName((byte)10,"blue");
		colorMap.setColor$ToName((byte)1,"red");
		colorMap.setColor$ToName((byte)0,"white");
		
		System.out.println("colormap");
		// Next, create a 2d window for display, set its size, zoom factor, title
		
		worldRaster = new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(4);
		worldRaster.setWidth$Height(worldSizeX, worldSizeY);
		worldRaster.setWindowTitle("CA world");
		worldRaster.pack();

		
		caWorld = new CAWorld(this, worldSizeX, worldSizeY);
		// CAWorld(this) cannnot initialize DblBuffer2d correctly.
				
		caWorld.setSizeX$Y(worldSizeX,worldSizeY);
		caWorld.setObserver(this);
		randomize();
		applyRule();
		
		
		try{
			valueDisplay = new Value2dDisplayImpl(this, worldRaster, colorMap, caWorld);
		}catch(Exception e){
			System.out.println("Exception: " + e.getMessage());
			System.exit(1);
		}

		try{
			worldRaster.setButton$Client$Message(3, caWorld, new Selector(caWorld.getClass(), "swapColorAtX$Y",false));
		}catch(Exception e){
			System.out.println("Exception: " + e.getMessage());
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
			displayActions.createActionTo$message(caWorld, new Selector(caWorld.getClass(),"stepRule",false));
			displayActions.createActionTo$message(getActionCache(), new Selector(getActionCache().getClass(),"doTkEvents",true));
		}catch (Exception e){
			System.out.println("Exception: " + e.getMessage());
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
	public void randomize(){
		caWorld.randomize();
	}
	
	public void applyRule(){
		caWorld.setRule(Alpha,Signal1,Signal2);
	}
	
	public void applyAlpha(){
		caWorld.setAlpha(Alpha);
	}
	
	public void applySignal1(){
		caWorld.setSignal1(Signal1);
	}
	
	public void applySignal2(){
		caWorld.setSignal2(Signal2);
	}
	
	public Object eraseCA(){
		if(caWorld.getClass()!=null)
			caWorld.eraseAll();
		return this;
	}
}