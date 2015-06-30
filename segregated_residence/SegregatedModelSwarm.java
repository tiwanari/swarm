/*----------------------------------------------------------------------------
 * import
 *----------------------------------------------------------------------------*/
import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

/*----------------------------------------------------------------------------
 * class   : SegregatedModelSwarm
 * comment : ModelSwarm.javaの転用
 *----------------------------------------------------------------------------*/
public class SegregatedModelSwarm extends SwarmImpl{
  public int worldXSize, worldYSize;
  public double personDensity;
  Grid2d world;
  List personList;
  ActionGroup modelActions;
  Schedule modelSchedule;
  
  /* 追加 */
  public int raceNum;       // 民族種数
  public double seedChurch; // 空間に対する教会の存在密度
  public double seedPhilan; // 人口に対する博愛主義者の密度
  SegregatedChurchSpace church;       // 教会空間オブジェクト (FoodSpaceを転用)

  /**
   * コンストラクタ */
  public SegregatedModelSwarm(Zone aZone){
    super(aZone);
    
    // Now fill in various simulation parameters with default values.
    
    worldXSize = 80;
    worldYSize = 80;
    seedChurch = 0.5;
    seedPhilan = 0.01;
    personDensity = 0.1;
    raceNum = 2;
    
    
    EmptyProbeMap probeMap;
    probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
    
    // Add in a bunch of variables, one per simulation parameter
    
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("worldXSize",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("worldYSize",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("seedChurch",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("raceNum",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("seedPhilan",this.getClass()));
    probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("personDensity",this.getClass()));
        
    // Now install our custom probeMap into the probeLibrary.
        
        Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
  }
  
  public Object buildObjects(){
    SegregatedAgent aSegregatedAgent;
    int x,y;
    
    // 配置する民族種数分だけの教会を配置
    church=new SegregatedChurchSpace(this,worldXSize,worldYSize);
    church.seedChurchWithProb(seedChurch, 7, 6 + raceNum);
    
    world=new Grid2dImpl(this,worldXSize,worldYSize);
    world.fillWithObject(null);
    
    // Now, create a bunch of people to live in the world
    
    personList=new ListImpl(this);
    
    for (y = 0; y < worldYSize; y++){
      for (x = 0; x < worldXSize; x++){
        if (Globals.env.uniformDblRand.getDoubleWithMin$withMax
        (0.0,1.0) < personDensity){
          // 民族種数と, 博愛主義者密度を引数で渡す
          aSegregatedAgent=new SegregatedAgent(this, raceNum, seedPhilan);
          aSegregatedAgent.setWorld$Space(world,church);
          aSegregatedAgent.setX$Y(x,y);
          personList.addLast(aSegregatedAgent);
        }
      }
    }
    return this;
  }
  
  public Object buildActions(){
    modelActions=new ActionGroupImpl(this);
    try{
      modelActions.createActionForEach$message(personList,
        new Selector(Class.forName("SegregatedAgent"),"step",false));
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
  
  public SegregatedChurchSpace getChurch(){
    return church;
  }
  
  public List getAgentList(){
    return personList;
  }
}

/*----------------------------------------------------------------------------
 * end of file
 *----------------------------------------------------------------------------*/
