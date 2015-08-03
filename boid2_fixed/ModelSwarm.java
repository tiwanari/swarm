import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
  public int worldXSize, worldYSize;
  public double obstacleProb;
  public int obstacleSize;
  public double bugDensity;
  
  ObstacleSpace obstacle;
  Grid2d world;
  
  List bugList;
  ActionGroup modelActions;
  Schedule modelSchedule;
  
  public ModelSwarm(Zone aZone){
    super(aZone);
    
    // Now fill in various simulation parameters with default values.B
    
    worldXSize = 80;
    worldYSize = 80;
    obstacleProb   = 0.0;
    obstacleSize = 1;
    bugDensity = 0.1;
    
    // And build a customized probe map. Without a probe map, the default
    // is to show all variables and messages. Here we choose to
    // customize the appearance of the probe, give a nicer interface.
    
    EmptyProbeMap probeMap;
    probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
    
    // Add in a bunch of variables, one per simulation parameter
    
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		      ("worldXSize",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		      ("worldYSize",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		      ("obstacleProb",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		      ("obstacleSize",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		      ("bugDensity",this.getClass()));
    
    // Now install our custom probeMap into the probeLibrary.
    
    Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
  }
  
  public Object buildObjects(){
    Bug aBug;
    int x,y;
    
    
    // Here, we create the objects in the model
    
    // Then, create the obstacle space and initialize it
		
    obstacle=new ObstacleSpace(this,worldXSize,worldYSize);
    obstacle.setObstacle(obstacleProb, obstacleSize);
    
    // Now set up the grid used to represent agent position
    // Grid2d enforces only 1 bug per site
    
    world=new Grid2dImpl(this,worldXSize,worldYSize);
    world.fillWithObject(null);
    
    // Now, create a bunch of bugs to live in the world
    
    bugList=new ListImpl(this);
    
    for (y = 0; y < worldYSize; y++){
      for (x = 0; x < worldXSize; x++){
	if (Globals.env.uniformDblRand.getDoubleWithMin$withMax
	    (0.0,1.0) < bugDensity){
	  aBug=new Bug(this);
	  aBug.setWorld(world);
	  aBug.setX$Y(x,y);
	  aBug.setObstacle(obstacle);
	  bugList.addLast(aBug);
	}
      }
    }
    return this;
  }
	
  public Object buildActions(){
    modelActions=new ActionGroupImpl(this);
    try{
      modelActions.createActionForEach$message
	(bugList, new Selector(Class.forName("Bug"),"step",false));
    } catch (Exception e) {
      e.printStackTrace (System.err);
      System.exit(1);
    }
    
    modelSchedule=new ScheduleImpl(this,1);
    modelSchedule.at$createAction(0,modelActions);
    return this;
  }
  
  public Activity activateIn(Swarm context){
    super.activateIn (context);
    modelSchedule.activateIn(this);
    return getActivity();
  }
	
  public Grid2d getWorld(){
    return world;
  }
  
  public ObstacleSpace getObstacle(){
    return obstacle;
  }
  
  public List getBugList(){
    return bugList;
  }
}
