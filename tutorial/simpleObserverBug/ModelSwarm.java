import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.Selector;
import swarm.space.*;
import swarm.collections.*;

/**
 * ModelSwarm�ɑ傫�ȕύX�͂Ȃ����A�O������A�N�Z�X���₷���悤�ɁA���\�b�h���������ǉ����Ă���B
 * reportBug�͂��͂�K�v�Ȃ����ߎ������Ă��Ȃ��B
 */
public class ModelSwarm extends SwarmImpl{
	public int worldXSize, worldYSize;
	public double seedProb;
	public double bugDensity;
	
	FoodSpace food;
	Grid2d world;
	
	List bugList;
	ActionGroup modelActions;
	Schedule modelSchedule;
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		worldXSize = 80;
		worldYSize = 80;
		seedProb   = 0.5;
		bugDensity = 0.1;
	}
	
	public Object buildObjects(){
		Bug aBug;
		int x,y;
		
		food=new FoodSpace(this,worldXSize,worldYSize);
		food.seedFoodWithProb(seedProb);
		
		world=new Grid2dImpl(this,worldXSize,worldYSize);
		world.fillWithObject(null);
		
		bugList=new ListImpl(this);
		
		for (y = 0; y < worldYSize; y++){
			for (x = 0; x < worldXSize; x++){
				if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < bugDensity){
					aBug=new Bug(this);
					aBug.setWorld$Food(world,food);
					aBug.setX$Y(x,y);
					bugList.addLast(aBug);
				}
			}
		}
		return this;
	}
	
	public Object buildActions(){
		modelActions=new ActionGroupImpl(this);
		try{
			modelActions.createActionForEach$message(bugList,new Selector(Class.forName("Bug"),"step",false));
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
	
	// ModelSwarm�̓����ɃA�N�Z�X���邽�߂ɐV���Ɏ��������B
	public Grid2d getWorld(){
		return world;
	}
	
	// ModelSwarm�̓����ɃA�N�Z�X���邽�߂ɐV���Ɏ��������B
	public FoodSpace getFood(){
		return food;
	}
	
	// ModelSwarm�̓����ɃA�N�Z�X���邽�߂ɐV���Ɏ��������B
	public List getBugList(){
		return bugList;
	}
}
