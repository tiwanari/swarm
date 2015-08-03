import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
	/** lispAppArchiverÇÃÇΩÇﬂÇ…ÇÕpublicÇ≈Ç»ÇØÇÍÇŒÇ»ÇÁÇ»Ç¢ÅB */
	public int worldXSize, worldYSize;
	/** lispAppArchiverÇÃÇΩÇﬂÇ…ÇÕpublicÇ≈Ç»ÇØÇÍÇŒÇ»ÇÁÇ»Ç¢ÅB */
	public double seedProb;
	/** lispAppArchiverÇÃÇΩÇﬂÇ…ÇÕpublicÇ≈Ç»ÇØÇÍÇŒÇ»ÇÁÇ»Ç¢ÅB */
	public double bugDensity;
	
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
		
		reportBug=(Bug)bugList.removeFirst();
		bugList.addFirst(reportBug);
		
		return this;
	}
	
	public Object buildActions(){
		modelActions=new ActionGroupImpl(this);
		try{
			modelActions.createActionForEach$message(bugList,
				new Selector(Class.forName("Bug"),"step",false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		try{
			modelActions.createActionTo$message(reportBug,
				new Selector(Class.forName("Bug"),"report",false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		modelSchedule=new ScheduleImpl(this,1);
		modelSchedule.at$createAction(0,modelActions);
		return this;
	}
	
	public Activity activateIn(Swarm context){
    	super.activateIn(context);
    	modelSchedule.activateIn(this);
		return getActivity();
	}
}
