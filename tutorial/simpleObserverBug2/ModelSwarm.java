import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

/**
 * ControlPanelのStartボタンを押す前に、すべてのモデルパラメータを変更可能
 * にするために、ModelSwarmのProbeを作る。
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
		
		// Probeにはデフォルトではすべてのpublicなフィールドがのる。
		// ここではさらにpublicなメッセージ呼び出しものせられることをみるために、
		// getBugListも追加する（実用上の意味はないが）。
		
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		// Add in a bunch of variables, one per simulation parameter
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("worldXSize",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("worldYSize",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("seedProb",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("bugDensity",this.getClass()));
        // ここまではデフォルトのProbeにも登場するもの。よって、これだけで十分な場合は、
        // このようにProbeMapを作る必要はない。ObserverSwarmのProbeを参照。
        
		// あらたにメッセージへのProbeも追加する。
		// Probeに追加できるメッセージはpublicなものに限られる。
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass
		  ("getBugList",this.getClass()));

		// Now install our custom probeMap into the probeLibrary.
        
        Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
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
				if (Globals.env.uniformDblRand.getDoubleWithMin$withMax
				(0.0,1.0) < bugDensity){
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
			modelActions.createActionForEach$message(bugList,
				new Selector(Class.forName("Bug"),"step",false));
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
	
	public FoodSpace getFood(){
		return food;
	}
	
	public List getBugList(){
		return bugList;
	}
}
