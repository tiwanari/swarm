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
 * comment : ModelSwarm.java�̓]�p
 *----------------------------------------------------------------------------*/
public class SegregatedModelSwarm extends SwarmImpl{
  public int worldXSize, worldYSize;
  public double personDensity;
  Grid2d world;
  List personList;
  ActionGroup modelActions;
  Schedule modelSchedule;
  
  /* �ǉ� */
  public int raceNum;       // �����퐔
  public double seedChurch; // ��Ԃɑ΂��鋳��̑��ݖ��x
  public double seedPhilan; // �l���ɑ΂��锎����`�҂̖��x
  SegregatedChurchSpace church;       // �����ԃI�u�W�F�N�g (FoodSpace��]�p)

  /**
   * �R���X�g���N�^ */
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
    
    // �z�u���閯���퐔�������̋����z�u
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
          // �����퐔��, ������`�Җ��x�������œn��
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
