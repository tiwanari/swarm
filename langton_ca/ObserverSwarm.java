import swarm.Globals;
import swarm.Selector;
import swarm.activity.ActionGroup;
import swarm.activity.ActionGroupImpl;
import swarm.activity.Activity;
import swarm.activity.Schedule;
import swarm.activity.ScheduleImpl;
import swarm.defobj.Zone;
import swarm.gui.Colormap;
import swarm.gui.ColormapImpl;
import swarm.gui.ZoomRaster;
import swarm.gui.ZoomRasterImpl;
import swarm.objectbase.EmptyProbeMap;
import swarm.objectbase.EmptyProbeMapImpl;
import swarm.objectbase.Swarm;
import swarm.simtoolsgui.GUISwarmImpl;
import swarm.space.Value2dDisplay;
import swarm.space.Value2dDisplayImpl;

public class ObserverSwarm extends GUISwarmImpl
{
	public int displayFrequency;
	public int worldSizeX; 	// width of the world
	public int worldSizeY;	// height of the world

	private ActionGroup displayActions;		// schedule data structs
	private Schedule displaySchedules; 		// 
	private Colormap colorMap; 						// colors of states
	private ZoomRaster worldRaster; 			// 
	private LangtonWorld langtonWorld; 		// cell Automaton world
	private Value2dDisplay valueDisplay; 	// 

	public ObserverSwarm(Zone aZone)
	{
		// Superclass createBegin to allocate ourselves.
		super(aZone);
		
		// Fill in the relevant parameters
		displayFrequency = 1;
		worldSizeX = 200;
		worldSizeY = 200;
		
		EmptyProbeMap probeMap;
		probeMap = new EmptyProbeMapImpl(aZone, this.getClass());
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("displayFrequency", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("worldSizeX", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("worldSizeY", this.getClass()));
		
		// Now install our custom proveMap into the probeLibrary.
		Globals.env.probeLibrary.setProbeMap$For(probeMap, this.getClass());
	}

	public Object buildObjects()
	{
		super.buildObjects();
		Globals.env.createArchivedProbeDisplay(this, "observerSwarm");
		getControlPanel().setStateStopped();

		// First, create a colormap: this is a global resource, the information
		colorMap = new ColormapImpl(this);
		colorMap.setColor$ToName((byte)0,"black");
		colorMap.setColor$ToName((byte)1,"red");
		colorMap.setColor$ToName((byte)2,"green");
		colorMap.setColor$ToName((byte)3,"red");
		colorMap.setColor$ToName((byte)4,"red");
		colorMap.setColor$ToName((byte)5,"red");
		colorMap.setColor$ToName((byte)6,"red");
		colorMap.setColor$ToName((byte)7,"red");
		colorMap.setColor$ToName((byte)8,"black");
		
		// Next, create a 2d window for display, set its size, zoom factor, title
		worldRaster = new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(2);
		worldRaster.setWidth$Height(worldSizeX, worldSizeY);
		worldRaster.setWindowTitle("Langton's Cell Automaton");
		worldRaster.pack();
		
		langtonWorld = new LangtonWorld(this, worldSizeX, worldSizeY);
		langtonWorld.setObserver(this);
		langtonWorld.init(); // init must be called after setObserver(**).
		
		try	{
			valueDisplay = new Value2dDisplayImpl(this, worldRaster, colorMap, langtonWorld);
		} catch (Exception e)	{
			System.out.println("Exception: " + e.getMessage());
			System.exit(1);
		}
		try	{
			worldRaster.setButton$Client$Message(3, langtonWorld,
					new Selector(langtonWorld.getClass(), "swapColorAtX$Y",	false));
		} catch (Exception e)	{
			System.out.println("Exception: " + e.getMessage());
			System.exit(1);
		}
		
		return this;
	}

	public Object buildActions()
	{
		// Create an ActionGroup for display
		displayActions = new ActionGroupImpl(this);
		
		// Schedule up the methods to draw the display of the world
		try	{
			displayActions.createActionTo$message(valueDisplay, new Selector(valueDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(worldRaster, new Selector(worldRaster.getClass(),"drawSelf",false));
			displayActions.createActionTo$message(langtonWorld, new Selector(langtonWorld.getClass(), "stepRule", true));
			displayActions.createActionTo$message(getActionCache(), new Selector(getActionCache().getClass(), "doTkEvents",
					true));
		} catch (Exception e)	{
			System.out.println("Exception: " + e.getMessage());
			System.exit(1);
		}
		displaySchedules = new ScheduleImpl(this, displayFrequency);
		displaySchedules.at$createAction(0, displayActions);
		
		return this;
	}

	public Activity activateIn(Swarm context)
	{
		super.activateIn(context);
				
		// Now activate our schedule in ourselves.
		// This arranges for the excution of the schedule we built.
		displaySchedules.activateIn(this);
		
		// Activate returns the swarm activity
		return getActivity();
	}
	
	// returns valueDisplay object
	public Value2dDisplay getValueDisplay()
	{
		return valueDisplay;
	}
	// returns worldRaster object
	public ZoomRaster getWorldRaster()
	{
		return worldRaster;
	}
}