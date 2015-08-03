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
    
    ActionGroup displayActions; 
    Schedule displaySchedule;
    
    ModelSwarm modelSwarm; // the Swarm we're observing
    
    
    Colormap colorMap; // allocate colours
    ZoomRaster worldRaster; // 2d display widget
    
        
    Value2dDisplay sumOfSpacesDisplay; // display the food and pheromone
    Object2dDisplay bugDisplay; // display the ants
    
    public ObserverSwarm(Zone aZone){
	// Superclass createBegin to allocate ourselves.
	super(aZone);
	
	// Fill in the relevant parameters (only one, in this case).
	displayFrequency=1;
    }
    
    public Object buildObjects(){
	super.buildObjects();
	
	modelSwarm = (ModelSwarm)
	    Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm"); //エラーだった場合の次の処理は未実装
	//raiseEvent(InvalidOperation,"Can't find the modelSwarm parameters");
	
	Globals.env.createArchivedProbeDisplay (modelSwarm,"modelSwarm");	
	getControlPanel().setStateStopped();
	
	modelSwarm.buildObjects();
	
	colorMap=new ColormapImpl(this);
	colorMap.setColor$ToName((byte)0,"black"); //キャストが必要
	colorMap.setColor$ToName((byte)1,"red");
	colorMap.setColor$ToName((byte)2,"green");
	colorMap.setColor$ToName((byte)3,"blue");
	for ( int k = 0; k < 243; k++ ){
	    colorMap.setColor$ToRed$Green$Blue
		((byte)(11+k), (242+k)/484.0, (242+k)/484.0, (242+k)/484.0);
	}	

	
	worldRaster=new ZoomRasterImpl(this);
	worldRaster.setColormap(colorMap);
	worldRaster.setZoomFactor(4);
	worldRaster.setWidth$Height(
				    modelSwarm.getWorld().getSizeX(),
				    modelSwarm.getWorld().getSizeY());
	worldRaster.setWindowTitle("Pheromone Trail");
	worldRaster.pack();
    	
	sumOfSpacesDisplay=new Value2dDisplayImpl(this,worldRaster,colorMap,modelSwarm.getSumOfSpaces());

	try {
	    bugDisplay = new Object2dDisplayImpl(this,
						 worldRaster,
						 modelSwarm.getWorld(),
						 new Selector(Class.forName("Bug"), "drawSelfOn", false));
	} catch (Exception e) {
	    System.exit(1);
	}
	bugDisplay.setObjectCollection(modelSwarm.getBugList());
	
	return this;
    }
    
    public Object buildActions(){
	
	// First, let our model swarm build its own schedule.
	
	modelSwarm.buildActions();
	
	displayActions=new ActionGroupImpl(this);
	
	// Schedule up the methods to draw the display of the world
	
	try {
	    displayActions.createActionTo$message(sumOfSpacesDisplay,
	    				  new Selector(sumOfSpacesDisplay.getClass(),"display",false));
	    displayActions.createActionTo$message(bugDisplay,
						  new Selector(bugDisplay.getClass(),"display",false));
	    displayActions.createActionTo$message(worldRaster,
						  new Selector(worldRaster.getClass(),"drawSelf",false));
	    displayActions.createActionTo$message(getActionCache(),
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
