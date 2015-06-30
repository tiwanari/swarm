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
	public int seedNum;
	public int Eden;
	
	
	FoodSpace food;
	Grid2d world;
	
	List bugList;
	ActionGroup modelActions;
	Schedule modelSchedule;
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		worldXSize = 80;
		worldYSize = 80;
		seedProb   = 1;
		seedNum = 1;
		bugDensity = 0.1;
		Eden = 1;
		
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
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		  ("Eden",this.getClass()));
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
	
	public Object checkBugsAlive(){
		int bugNum=bugList.getCount();
		Bug bug;
		
		for(int i=0;i<bugNum;i++){
			bug = (Bug)bugList.removeFirst();
			if(bug.getEnergy()>0){
				bugList.addLast(bug);
			}
		}
			
		return this;
	}

	public void reproduction(){
		Bug parent1,parent2;
		int bugNum=bugList.getCount();

		for(int i=0;i<bugNum;i++){

			List copy=new ListImpl(this);

			for(int j=0;j<bugNum;j++){
				copy.addLast(bugList.removeFirst());
				bugList.addLast(copy.getLast());
			}

			parent1 = (Bug)bugList.removeFirst();
			int x1= parent1.xPos;
			int y1= parent1.yPos;
			int energy1=parent1.getEnergy();
			int age1=parent1.getAge();
			bugList.addLast(parent1);

			if(energy1 >= 1200 && age1 >=1000){
				if(world.getValueAtX$Y(parent1.xPos+1, parent1.yPos)==0 && 
						world.getValueAtX$Y(parent1.xPos-1, parent1.yPos)==0){
					Bug child1=new Bug(this);
					Bug child2=new Bug(this);
					child1.setWorld$Food(world,food);
					child1.setX$Y(parent1.xPos+1,parent1.yPos);
					child1.setEnergy((parent1.getEnergy()/2));
					child1.setAge(0);
					child2.setWorld$Food(world,food);
					child2.setX$Y(parent1.xPos-1,parent1.yPos);
					child2.setEnergy((parent1.getEnergy()/2));
					child1.setAge(0);
					child1.setDirProb(crossover(parent1.getDirProb(),parent1.getDirProb()));
					child2.setDirProb(crossover(parent1.getDirProb(),parent1.getDirProb()));
					bugList.removeLast();
					bugList.addLast(child1);
					bugList.addLast(child2);
					
				}
			}
			
			else{
			for(int j=0;j<i;j++){
				copy.removeFirst();
			}

			for(int j=i;j<bugNum;j++){
				parent2 = (Bug)copy.removeFirst();
				int x2 = parent2.xPos;
				int y2 = parent2.yPos;
				int energy2 = parent2.getEnergy();
				int age2= parent2.getAge();
				if(Math.abs(x1-x2) + Math.abs(y1-y2)<10 && energy1>1000 && energy2>1000&& age1>800 && age2>800){
					if(world.getValueAtX$Y(parent1.xPos+1, parent1.yPos)==0 && 
							world.getValueAtX$Y(parent2.xPos+1, parent2.yPos)==0){
						Bug child1=new Bug(this);
						Bug child2=new Bug(this);
						child1.setWorld$Food(world,food);
						child1.setX$Y(parent1.xPos+1,parent1.yPos);
						child1.setEnergy((parent1.getEnergy()+parent2.getEnergy())/4);
						child1.setAge(0);
						child2.setWorld$Food(world,food);
						child2.setX$Y(parent2.xPos+1,parent2.yPos);
						child2.setEnergy((parent1.getEnergy()+parent2.getEnergy())/4);
						child1.setAge(0);
						parent1.setEnergy(parent1.getEnergy()/2);
						parent2.setEnergy(parent2.getEnergy()/2);
						child1.setDirProb(crossover(parent1.getDirProb(),parent2.getDirProb()));
						child2.setDirProb(crossover(parent1.getDirProb(),parent2.getDirProb()));
						bugList.addLast(child1);
						bugList.addLast(child2);
					}
				}
			}
			}
		}
	}

	public int[] crossover(int[] dir1,int[] dir2){
		int[] child = new int[6];
		int[] flag = {0,0,0,0,0,0};
		
		for(int i=0;i<flag.length;i++){
			if(Math.random()<0.5){
				flag[i]=1;
			}
		}
		
		for(int i=0;i<flag.length;i++){
			if(flag[i]==0)child[i]=dir1[i];
			else child[i]=dir2[i];
		}
		
		//mutation
		for(int i=0;i<flag.length;i++){
			if(Math.random()<0.1){
				child[i]=(int)(10*Math.random());
			}
		}
		
		return child;
	}
	
	public void generateNewFood(){
		for(int i=0;i<seedNum;i++){
			if(Math.random()<0.3)
			food.putValue$atX$Y(1,(int)(worldXSize*Math.random()),(int)(worldYSize*Math.random()));
		}

		if(Eden != 0){
			for(int i=0;i<seedNum;i++){
				if(Math.random()<0.3)
				food.putValue$atX$Y(1,(int)((worldXSize/8)*Math.random()+(worldXSize*6/8)),
						(int)((worldYSize/8)*Math.random()+(worldYSize*6/8)));
			}
		}
	}
	
	public Object buildActions(){
		modelActions=new ActionGroupImpl(this);
		try{
			modelActions.createActionForEach$message(bugList,
				new Selector(Class.forName("Bug"),"step",false));

			modelActions.createActionTo$message(this,new Selector(Class.forName("ModelSwarm"),"checkBugsAlive",false));
			modelActions.createActionTo$message(this,new Selector(Class.forName("ModelSwarm"),"reproduction",false));
			modelActions.createActionTo$message(this,new Selector(Class.forName("ModelSwarm"),"generateNewFood",false));

			
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
