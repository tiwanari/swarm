/*----------------------------------------------------------------------------
 * import
 *----------------------------------------------------------------------------*/
import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.simtoolsgui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

// 追加 (unhappiness視覚化のため)
import swarm.analysis.EZGraph;
import swarm.analysis.EZGraphImpl;

/*----------------------------------------------------------------------------
 * class   : SegregatedObserverSwarm
 * comment : ObserverSwarm.javaの転用
 *----------------------------------------------------------------------------*/
public class SegregatedObserverSwarm extends GUISwarmImpl{
  public int displayFrequency; // one parameter: update freq
                               // probeで操作できるようにするためにはpublicでなければならない
  
  ActionGroup displayActions; // schedule data structs
  Schedule displaySchedule;
  
  SegregatedModelSwarm modelSwarm; // the Swarm we're observing
  
  // Lots of display objects. First, widgets
  
  Colormap colorMap; // allocate colours
  ZoomRaster worldRaster; // 2d display widget
  
  // Now, higher order display and data objects
  
  Value2dDisplay churchDisplay; // display the heat
  Object2dDisplay personDisplay; // display the heatpeople

  public EZGraph unhappinessGraph; // "unhappiness"の視覚化

  public SegregatedObserverSwarm(Zone aZone){
    super(aZone);
    
    displayFrequency=1;

    EmptyProbeMap probeMap;
    probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
    
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("displayFrequency",this.getClass()));
        
    Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
  }
  
  public Object buildObjects(){
    super.buildObjects();
    
    modelSwarm = (SegregatedModelSwarm)
      Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm"); //エラーだった場合の次の処理は未実装

    //raiseEvent(InvalidOperation,"Can't find the modelSwarm parameters");
    
    Globals.env.createArchivedProbeDisplay (modelSwarm,
                                            "modelSwarm");
    Globals.env.createArchivedProbeDisplay (this, "observerSwarm");
    
    getControlPanel().setStateStopped();
    
    modelSwarm.buildObjects();
    
    /* 表示色定義 */
    colorMap=new ColormapImpl(this);
    colorMap.setColor$ToName((byte)0,"black");//キャストが必要

    // 民族色 (非博愛)
    colorMap.setColor$ToName((byte)1,"red4");
    colorMap.setColor$ToName((byte)2,"green4");
    colorMap.setColor$ToName((byte)3,"blue4");

    // 民族色 (博愛主義者)
    colorMap.setColor$ToName((byte)4,"red");
    colorMap.setColor$ToName((byte)5,"green");
    colorMap.setColor$ToName((byte)6,"blue");

    // 教会色 (民族毎)
    colorMap.setColor$ToName((byte)7,"magenta");
    colorMap.setColor$ToName((byte)8,"lightblue");
    colorMap.setColor$ToName((byte)9,"cyan");

    // 未使用
    colorMap.setColor$ToName((byte)10,"orange");
    colorMap.setColor$ToName((byte)11,"yellow");
    colorMap.setColor$ToName((byte)12,"cyan4");

    colorMap.setColor$ToName((byte)13,"white");
    
    worldRaster=new ZoomRasterImpl(this);
    worldRaster.setColormap(colorMap);
    worldRaster.setZoomFactor(4);
    worldRaster.setWidth$Height(
      modelSwarm.getWorld().getSizeX(),
      modelSwarm.getWorld().getSizeY());
    worldRaster.setWindowTitle("Segregated Residence");
    worldRaster.pack(); // draw the window.
    
    churchDisplay=new Value2dDisplayImpl(
      this,worldRaster,colorMap,modelSwarm.getChurch());
    
    try {
      personDisplay = new Object2dDisplayImpl(
        this,
        worldRaster,
        modelSwarm.getWorld(),
        new Selector(Class.forName("SegregatedAgent"), "drawSelfOn", false));
    } catch (Exception e) {
      System.out.println ("Exception: " + e.getMessage ());
      System.exit(1);
    }
    
    personDisplay.setObjectCollection(modelSwarm.getAgentList());
    
    try {
      worldRaster.setButton$Client$Message(
        3,personDisplay,new Selector(personDisplay.getClass(),
                                          "makeProbeAtX$Y",true));
          } catch (Exception e) {
      System.out.println ("Exception: " + e.getMessage ());
      System.exit(1);
    }
        
    /* "unhappiness"視覚化のため追加 (ここから) */
    unhappinessGraph = new EZGraphImpl
      (getZone(), "unhappiness of people vs. time", "time",
      "unhappiness", "unhappinessGraph");

    try {
      unhappinessGraph.enableDestroyNotification$notificationMethod
        (this, new Selector (getClass(), "_unhappinessGraphDeath_", false));
    } catch (Exception e) {
      System.out.println("Exception _unhappinessGraphDeath_: " + e.getMessage());
    }

    try {
      unhappinessGraph.createAverageSequence$withFeedFrom$andSelector
        ("unhappiness", modelSwarm.getAgentList(), 
        new Selector (Class.forName("SegregatedAgent"), "getUnhappiness", true));
      
    } catch (Exception e) {
      System.out.println("Exception getUnhappiness: " + e.getMessage());
    }
    /* "unhappiness"視覚化のため追加 (ここまで) */

    return this;
  }
  
  /* "unhappiness"視覚化のため追加 (終了関数?) */
  public Object _unhappinessGraphDeath_ (Object caller) {
    unhappinessGraph.drop();
    unhappinessGraph = null;
    return this;
  }

  /**
   * Create the actions necessary for the simulation.
   */
  public Object buildActions(){
    super.buildActions();
    
    modelSwarm.buildActions();
    
    displayActions=new ActionGroupImpl(this);
    
    try {
      displayActions.createActionTo$message(churchDisplay,
        new Selector(churchDisplay.getClass(),"display",false));
      displayActions.createActionTo$message(personDisplay,
        new Selector(personDisplay.getClass(),"display",false));
      displayActions.createActionTo$message(worldRaster,
        new Selector(worldRaster.getClass(),"drawSelf",false));
      displayActions.createActionTo$message(
        getActionCache(),
        new Selector(getActionCache().getClass(),"doTkEvents",true));

      /* "unhappiness"視覚化のため追加 (表示処理登録?) */
      displayActions.createActionTo$message(unhappinessGraph,
        new Selector(unhappinessGraph.getClass(), "step", false));
    } catch (Exception e) {
      System.out.println ("Exception: " + e.getMessage ());
      System.exit(1);
    }
    
    displaySchedule = new ScheduleImpl(this,displayFrequency);
    displaySchedule.at$createAction(0,displayActions);
    
    return this;
    }
  
  /**
   * activateIn: - activate the schedules so they're ready to run.
   */
  public Activity activateIn(Swarm context){
        super.activateIn(context);
      
    modelSwarm.activateIn(this);
    
    displaySchedule.activateIn(this);
    
    return getActivity();
  }
}

/*----------------------------------------------------------------------------
 * end of file
 *----------------------------------------------------------------------------*/
