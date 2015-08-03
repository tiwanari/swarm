import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

public class ObserverSwarm extends GUISwarmImpl{
	ActionGroup displayActions;
	Schedule displaySchedule;
	ModelSwarm modelSwarm;
	Colormap colorMap;
	ZoomRaster patternRaster;
	Value2dDisplay patternDisplay;
		
	public ObserverSwarm(Zone aZone){
		super(aZone);
	}
	
	public Object buildObjects(){
		super.buildObjects();
		modelSwarm = (ModelSwarm)
			Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm");
		Globals.env.createArchivedProbeDisplay (modelSwarm,"modelSwarm");
		getControlPanel().setStateStopped();
		modelSwarm.buildObjects();
		
		colorMap=new ColormapImpl(this);
		// background color
		colorMap.setColor$ToRed$Green$Blue((byte) 127, 0, 0, 0); 
		//í·ë¨ÇÃê‘Ç©ÇÁçÇë¨ÇÃóŒÇ÷Ç∆ë¨ìxÇ…î‰ó·ÇµÇƒïœâªÇ∑ÇÈ
		double k = 1.0/modelSwarm.vmax;
		for(int i=0;i<=modelSwarm.vmax && i < 127;i++){
			colorMap.setColor$ToRed$Green$Blue((byte) i, k*(modelSwarm.vmax - i), k*i, 0.0);
		}
		
		patternRaster=new ZoomRasterImpl(this);
		patternRaster.setColormap(colorMap);
		patternRaster.setZoomFactor(3);
		patternRaster.setWidth$Height(
			modelSwarm.getWidth(),
			modelSwarm.getHistory());
		patternRaster.setWindowTitle("Generated Pattern");
		patternRaster.pack();
		
		patternDisplay=new Value2dDisplayImpl(
			this,patternRaster,colorMap,modelSwarm.getPattern());
		
		return this;
	}
	
	public Object buildActions(){
		super.buildActions();
		modelSwarm.buildActions();
		displayActions=new ActionGroupImpl(this);
		
		try {
			displayActions.createActionTo$message(patternDisplay,
				new Selector(patternDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(patternRaster,
				new Selector(patternRaster.getClass(),"drawSelf",false));
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
		modelSwarm.activateIn(this);
		displaySchedule.activateIn(this);
		return getActivity();
	}
}
