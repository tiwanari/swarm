import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.simtools.QSort;
import swarm.simtools.QSortImpl;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
	public int width,history;
	
	public int num;
	public int vmax;
	public double pslow;
	public int seed;
	public int SlStrigger;
	public double troubletrigger;
	public double randomdown;
	
	PatternSpace patternSpace;
	
	Array cellVector;
	ActionGroup modelActions;
	Schedule modelSchedule;
	List posList;
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		num = 2;
		vmax = 10;
		pslow = 0.2;
		seed = 1;
		SlStrigger = 1;
		randomdown = 0;
		troubletrigger = 0;
		width=150;
		history=100;
		
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("num",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("vmax",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("seed",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("SlStrigger",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("pslow",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("randomdown",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("troubletrigger",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("width",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
		          ("history",getClass()));
		
        Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
	}
	
	public Object buildObjects(){
		Car aCell;
		Globals.env.randomGenerator.setStateFromSeed(seed);
		patternSpace=new PatternSpace(this,width,history,num);
		
		cellVector=new ArrayImpl(this,num);
		patternSpace.setCellVector(cellVector);

		posList=new ListImpl(this);
		for(int i=0;i<width;i++){
			posList.addLast(new Integer(i));
		}
		
		for (int i=0; i<num; i++){
			aCell=new Car(this, calcPos(), width,cellVector);
			cellVector.atOffset$put(i,aCell);
		}
		try {
			Selector sel = new Selector(Car.class, "compareCar", false);
			QSort sort = new QSortImpl(this.getZone());
			sort.sortObjectsIn$using(cellVector,sel);
		} catch (NonUniqueMethodSignatureException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SignatureNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		initializeCellVector();
		return this;
	}
	
	private int calcPos(){
		int k = Globals.env.uniformIntRand.getIntegerWithMin$withMax(0, posList.getCount()-1);
		Integer Index = (Integer)posList.atOffset(k);
		posList.remove(Index);
		return Index.intValue();
	}
	
	public void stepCellVector(){
		
		if(SlStrigger >= 1){
			for (int i=0; i<num; i++){
				((Car)cellVector.atOffset(i)).SlStrigger();
			}
			
			for (int i=0; i<num; i++){
				((Car)cellVector.atOffset(i)).SlowtoStart();
			}	
		}
			
		for (int i=0; i<num; i++){
				((Car)cellVector.atOffset(i)).accelerate();
			}
				
		for (int i=0; i<num; i++){
				((Car)cellVector.atOffset(i)).slowDown();
			}
		
		for (int i=0; i<num; i++){
			if(randomdown > Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0)){
				((Car)cellVector.atOffset(i)).randomslowdown();
		 	}
		}
		
		for (int i=0; i<num; i++){
			((Car)cellVector.atOffset(i)).recovery();
		}
		
		for (int i=0; i<num; i++){
			if(troubletrigger > Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0)){
				((Car)cellVector.atOffset(i)).trouble();
		 	}
		}
		
		if(pslow > Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0)){
			for (int i=0; i<num; i++){
				((Car)cellVector.atOffset(i)).decrementVelocity();
			}
		}
		
		for (int i=0; i<num; i++){
			((Car)cellVector.atOffset(i)).move();
		}
		
	}
	
	public Object buildActions(){
		modelActions=new ActionGroupImpl(this);
		try{
			modelActions.createActionTo$message(this,
				new Selector(getClass(),"stepCellVector",false));
			modelActions.createActionTo$message(patternSpace,
				new Selector(patternSpace.getClass(),"update",false));
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
	
	public PatternSpace getPattern(){
		return patternSpace;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHistory(){
		return history;
	}
	
	public void initializeCellVector(){
		Car aCell;
		for(int i=0;i<num;++i){
			aCell=(Car)cellVector.atOffset(i);
			aCell.setParams(i, vmax, num);
//			System.out.println(i + ", " + aCell.getX());
			aCell.initialize();
		}
	}
}
