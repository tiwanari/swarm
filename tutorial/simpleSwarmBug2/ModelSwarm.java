import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

/**
 * ������Bug���V�~�����[�g����悤�Ƀ��f�����g������B<BR>
 * <BR>
 * ���̂��߂ɂ܂��A
 * 1. Bug���������world�����iGrid2d�j�B<BR>
 * 2. Bug�̃R���N�V����List�����B<BR>
 * <BR>
 * Grid2d��Swarm ���C�u�����̈ꕔ�ŁA�I�u�W�F�N�g���i�[����i�q�ł���B
 * Bug�����Ȃ��ꍇ�A�Z���̒l��null�ł���B
 * Bug������ꍇ�ɂ́A�Z����Bug�̎Q�Ƃ��i�[�����B<BR>
 * <BR>
 * ������Bug�̂����A1�C��������ʂ�reportBug�Ƃ���B
 * reportBug�͉a��������Ƃ����񍐂���B
 * �N���X�}�ł�reportBug��Bug�̌p���̂悤�ɕ`���Ă��邪�A�����ł͂Ȃ��A�P���ȋ�ʂ����Ă��邾���ł���B<BR>
 * <BR>
 * ���f���̃X�P�W���[���͈��ActionGroup�i�A�N�V�����̏W���j����Ȃ�B
 * ActionGroup��bugList���̊e�I�u�W�F�N�g�̃��\�b�hstep���ĂԂ��ƂƁA
 * reportBug�̃��\�b�hreport���ĂԂ��Ƃ���\�������B
 * �����āA���̃X�P�W���[���͎���0�ɊJ�n����A���ԊԊu1�ŌJ��Ԃ����B
 */
public class ModelSwarm extends SwarmImpl{
	int worldXSize, worldYSize;
	double seedProb;
	double bugDensity;
	
	FoodSpace food;
	Grid2d world;
	Bug reportBug;
	
	List bugList;
	ActionGroup modelActions;
	Schedule modelSchedule;
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		worldXSize = 80;
		worldYSize = 80;
		seedProb = 0.8;
		bugDensity=0.1;
	}
	
	public Object buildObjects(){
		Bug aBug;
		int x,y;
		
		food=new FoodSpace(this,worldXSize,worldYSize);
		food.seedFoodWithProb(seedProb);
		
		// Now set up the grid used to represent agent position
		world=new Grid2dImpl(this,worldXSize,worldYSize);
		world.fillWithObject(null);
		
		// Now, create a bunch of bugs to live in the world
		
		// First, we create a List object to manage the bugs
		// for us.
		
		bugList=new ListImpl(this);
		
		// Then, we iterate over the possible sites in the world,
		// with a certain probability of creating a bug at 
		// each site.
		
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
		
		// enlist a "reporter" bug to let us know how things are going
		// We just pop the first bug we created and then return it
		reportBug=(Bug)bugList.removeFirst();
		bugList.addFirst(reportBug);
		
		return this;
	}
	
	public Object buildActions(){
		// Create an ActionGroup to hold the messages over the bugs
		modelActions=new ActionGroupImpl(this);
		try{
			modelActions.createActionForEach$message(bugList,
				new Selector(Class.forName("Bug"),"step",false));
			modelActions.createActionTo$message(reportBug,
				new Selector(Class.forName("Bug"),"report",false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		// Make a schedule and insert the ActionGroup as the only action
		modelSchedule=new ScheduleImpl(this,1);
		modelSchedule.at$createAction(0,modelActions);
		return this;
	}
	
	public Activity activateIn(Swarm context){
    	super.activateIn (context);
    	modelSchedule.activateIn(this);
		return getActivity();
	}
}
