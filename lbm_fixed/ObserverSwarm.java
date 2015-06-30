import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.simtoolsgui.*;
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
		double range;
		
		super.buildObjects();
		modelSwarm = (ModelSwarm)
			Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm");
		Globals.env.createArchivedProbeDisplay (modelSwarm,"modelSwarm");
		getControlPanel().setStateStopped();
		modelSwarm.buildObjects();
		
		colorMap=new ColormapImpl(this);
		range=50.0;
		for(int i=0; i<range; i++){
			colorMap.setColor$ToRed$Green$Blue((byte)i, 0.0, 0.0, (double)i/range );
		}
		for(int i=(int)range; i<2*range; i++){
			colorMap.setColor$ToRed$Green$Blue((byte)i, 0.0, ((double)i - range)/range, 1.0 );//(range - 0.5*((double)i-range))/range);
		}
		for(int i=2*(int)range; i<3*range; i++){
			colorMap.setColor$ToRed$Green$Blue((byte)i, ((double)i - 2*range)/range, 1.0, (3*range - (double)i)/range);//(range - 0.5*((double)i-range))/range);// );
		}
		for(int i=3*(int)range; i<4*range; i++){
			colorMap.setColor$ToRed$Green$Blue((byte)i, 1.0, (4*range - (double)i)/range, ((double)i - 3*range)/range );
		}
		for(int i=4*(int)range; i<5*range; i++){
			colorMap.setColor$ToRed$Green$Blue((byte)i, 1.0, ((double)i - 4*range)/range, 1.0 );
		}
		colorMap.setColor$ToName((byte)250,"gray");
		
		patternRaster=new ZoomRasterImpl(this);
		patternRaster.setColormap(colorMap);
		patternRaster.setZoomFactor(1);
		patternRaster.setWidth$Height(
			modelSwarm.getWidth(),
			modelSwarm.getheight());
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
