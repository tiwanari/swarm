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
	int displayFrequency;
	int zoomFactor;

	ActionGroup displayActions;
	Schedule displaySchedule;

	ModelSwarm modelSwarm;

	EZGraph numGraph;

	Colormap colorMap;
	ZoomRaster worldRaster;
	Object2dDisplay worldDisplay;

	public ObserverSwarm(Zone aZone){
		super(aZone);
		displayFrequency=1;
		zoomFactor=4;
	}

	public Object buildObjects(){
		super.buildObjects();
		modelSwarm = (ModelSwarm)
			Globals.env.lispAppArchiver.getWithZone$key(this,"modelSwarm");
		Globals.env.createArchivedProbeDisplay (modelSwarm,
                                            "modelSwarm");

		getControlPanel().setStateStopped();
		modelSwarm.buildObjects();
		colorMap=new ColormapImpl(this);
		colorMap.setColor$ToName((byte)0,"blue");
		colorMap.setColor$ToName((byte)1,"orange");
		colorMap.setColor$ToName((byte)2,"yellow");
		colorMap.setColor$ToName((byte)3,"green");

		worldRaster=new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(zoomFactor);
		worldRaster.setWidth$Height(
			modelSwarm.getSize(),
			modelSwarm.getSize());
		worldRaster.setWindowTitle("Spatial PD");
		for(int x=0;x<modelSwarm.getSize();++x){
			for(int y=0;y<modelSwarm.getSize();++y){
				worldRaster.drawPointX$Y$Color(x,y,(byte)0);
			}
		}
		worldRaster.pack();

		try {
			worldDisplay = new Object2dDisplayImpl(
				getZone(),
				worldRaster,
				modelSwarm.getWorld(),
				new Selector(Class.forName("Player"), "drawSelfOn", false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		worldDisplay.setObjectCollection(modelSwarm.getPlayers());

		try {
			worldRaster.setButton$Client$Message(
				3,worldDisplay,new Selector(worldDisplay.getClass(),
                                          "makeProbeAtX$Y",true));
        } catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}

		numGraph=new EZGraphImpl(
			this,"Number of all-C","Time","Number","numGraph");
		try {
			numGraph.createSequence$withFeedFrom$andSelector(
				"all-C",modelSwarm,new Selector(
				modelSwarm.getClass(),"getNum0", false));
			numGraph.createSequence$withFeedFrom$andSelector(
				"TFT",modelSwarm,new Selector(
				modelSwarm.getClass(),"getNum1", false));
			numGraph.createSequence$withFeedFrom$andSelector(
				"aTFT",modelSwarm,new Selector(
				modelSwarm.getClass(),"getNum2", false));
			numGraph.createSequence$withFeedFrom$andSelector(
				"all-D",modelSwarm,new Selector(
				modelSwarm.getClass(),"getNum3", false));
		} catch (Exception e) {
			System.err.println ("Exception: " + e.getMessage());
		}

		getControlPanel().setStateStopped();
		return this;
	}

	public Object buildActions(){
		super.buildActions();
		displayActions=new ActionGroupImpl(this);
		try {
			displayActions.createActionTo$message(modelSwarm,
				new Selector(modelSwarm.getClass(),"step",false));
			displayActions.createActionTo$message(worldDisplay,
				new Selector(worldDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(worldRaster,
				new Selector(worldRaster.getClass(),"drawSelf",false));
			displayActions.createActionTo$message(numGraph,
				new Selector(numGraph.getClass(),"step",false));
			displayActions.createActionTo$message(
				getActionCache(),
				new Selector(getActionCache().getClass(),"doTkEvents",true));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}

		displaySchedule = new ScheduleImpl(this,1);
		displaySchedule.at$createAction(0,displayActions);

		return this;
    }

	public Activity activateIn(Swarm context){
    	super.activateIn(context);
		displaySchedule.activateIn(this);
		return getActivity();
	}
}
