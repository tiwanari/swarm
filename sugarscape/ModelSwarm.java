//ModelSwarm.java

import swarm.Globals;
import swarm.Selector;
import swarm.activity.ActionGroup;
import swarm.activity.ActionGroupImpl;
import swarm.activity.Activity;
import swarm.activity.Schedule;
import swarm.activity.ScheduleImpl;
import swarm.collections.Index;
import swarm.collections.List;
import swarm.collections.ListImpl;
import swarm.collections.ListShuffler;
import swarm.collections.ListShufflerImpl;
import swarm.defobj.Zone;
import swarm.objectbase.EmptyProbeMap;
import swarm.objectbase.EmptyProbeMapImpl;
import swarm.objectbase.Swarm;
import swarm.objectbase.SwarmImpl;

public class ModelSwarm extends SwarmImpl{
	//Parameters for the model
	public int numAgents;
	public int alpha;
	public int pollute;
	public int season;
	public int color;
	public int mating;
	public int replacement;
	public int dbg;
	public int tagSize;
	public int propagation;
	public int boxstart;
	public int maxVision, maxMetabolism, maxMetabolism2;
	public int minInitialSugar, maxInitialSugar;
	public int minInitialSpice, maxInitialSpice;
	public int deathAgeMin, deathAgeMax;
	public int worldXSize, worldYSize;
	public java.lang.String datafile;
	public java.lang.String datafile2;
	public int battle;
	public int maxPlunder;
	public int spice;
	public int trade;

	//Objects in the list
	List agentList,childList;
	ListShuffler shuffler;
	SugarSpace sugarSpace;
	ListImpl reaperQueue;
	Zone aZone;
	int initial;
	int numBirth;
	int numDeath;
	int numKilled;
	int amount;

	//Schedule stuff
	Schedule modelSchedule;

	public ModelSwarm(Zone aZone){
		super(aZone);
		this.aZone=aZone;

		EmptyProbeMap probeMap;


		numAgents=400;
		alpha=5;
		pollute=0;
		season=0;
		color=6;
		mating=1;
		replacement=0;
		dbg=0;
		tagSize=3;
		propagation=0;
		boxstart=0;
		maxVision=6;
		maxMetabolism=4;
		maxMetabolism2=4;
		minInitialSugar=5;
		maxInitialSugar=25;
		minInitialSpice=5;
		maxInitialSpice=25;
		deathAgeMin=60;
		deathAgeMax=100;
		worldXSize=50;
		worldYSize=50;
		battle=0;
		maxPlunder=100;
		spice=0;
		trade=0;

		datafile="sugarspace.pgm";
		datafile2="spicespace.pgm";
		
		probeMap=new EmptyProbeMapImpl(aZone, this.getClass());
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("numAgents", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("alpha", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("pollute", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("season", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("color", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("mating", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("replacement", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("dbg", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("tagSize", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("propagation", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("boxstart", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("maxMetabolism", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("maxMetabolism2", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("maxVision", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("minInitialSugar", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("maxInitialSugar", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("minInitialSpice", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("maxInitialSpice", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("deathAgeMin", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("deathAgeMax", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("datafile", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("datafile2", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("battle", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("maxPlunder", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("spice", this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("trade", this.getClass()));
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass()); 

	}

	public Object buildObjects(){
		int i;
		Index index;
		
		super.buildObjects();


		sugarSpace=new SugarSpace(aZone,worldXSize,worldYSize,datafile,datafile2,alpha,pollute,season);

		agentList=new ListImpl(aZone);
		childList=new ListImpl(aZone);

		initial=1;
		for(i = 0; i < numAgents; i++)
			addNewRandomAgent();
		initial=0;

		reaperQueue=new ListImpl(aZone);

		shuffler=new ListShufflerImpl(aZone);

		return this;
	}

	public Object buildActions(){
		ActionGroup modelActions;

		super.buildActions();

		modelActions=new ActionGroupImpl(aZone);

		try{
			modelActions.createActionTo$message(this, new Selector(this.getClass(),"resetIndicator",false));

		} catch (Exception e){
			System.err.println ("Exception101: " + e.getMessage());
		}

		try{
			modelActions.createActionTo$message(sugarSpace, new Selector(sugarSpace.getClass(),"updateSugar",false));

		} catch (Exception e){
			System.err.println ("Exception101: " + e.getMessage());
		}

		try{
			modelActions.createActionTo$message(shuffler, new Selector(shuffler.getClass(),"shuffleWholeList",false), agentList);
		} catch (Exception e){
			System.err.println ("Exception102: " + e.getMessage());
		}

		try{
			modelActions.createActionForEach$message(agentList, new Selector(Class.forName("SugarAgent"),"step",false));
		} catch (Exception e){
			System.err.println ("Exception3: " + e.getMessage());
		}

		try{
			modelActions.createActionTo$message(this, new Selector(this.getClass(),"addChildren",false));
		} catch (Exception e){
			System.err.println ("Exception5: " + e.getMessage());
		}

		try{
			modelActions.createActionTo$message(this, new Selector(this.getClass(),"reapAgents",false));
		} catch (Exception e){
			System.err.println ("Exception4: " + e.getMessage());
		}

		modelSchedule=new ScheduleImpl(aZone,1);
		modelSchedule.at$createAction(0,modelActions);
		
		return this;
	}

	public Object resetIndicator(){
		numBirth=0;
		numDeath=0;
		numKilled=0;
		amount=0;
		return this;
	}

	public int getNumBirth(){
		return numBirth;
	}

	public int getNumDeath(){
		return numDeath;
	}

	public int getNumKilled(){
		return numKilled;
	}

	public int getTradeAmount(){
		return amount;
	}

	public Object addNewRandomAgent(){
		int x,y;
		SugarAgent agent;

		if(dbg==1) System.out.println("===ModelSwarm.addNewRandomAgent()===");


		sugarSpace.getAgentGrid().setOverwriteWarnings(false);

		agent=new SugarAgent(aZone);
		agent.setModelSwarm(this);

		agent.tag=new int[11];

		if(boxstart==1){
			int r=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1);
			if(r==1){
				x=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,20);
				y=Globals.env.uniformIntRand.getIntegerWithMin$withMax(sugarSpace.getSizeY()-21,sugarSpace.getSizeY()-1);
				for(int i=0; i<tagSize; i++){
					agent.setTag$at(0,i);
				}
			}
			else {
				x=Globals.env.uniformIntRand.getIntegerWithMin$withMax(sugarSpace.getSizeX()-21,sugarSpace.getSizeX()-1);
				y=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,20);
				for(int i=0; i<tagSize; i++){
					agent.setTag$at(1,i);
				}
			}
		}
		else{
			x=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,sugarSpace.getSizeX());
			y=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,sugarSpace.getSizeY());
			for(int i=0; i<tagSize; i++){
				int t=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,1);
				agent.setTag$at(t,i);
			}
		}
		
		sugarSpace.addAgent$atX$Y(agent,x,y);
		agent.setInitialSugar(Globals.env.uniformIntRand.getIntegerWithMin$withMax(minInitialSugar, maxInitialSugar));
		agent.setCurrentSugar(agent.getInitialSugar());
		agent.setInitialSpice(Globals.env.uniformIntRand.getIntegerWithMin$withMax(minInitialSpice, maxInitialSpice));
		agent.setCurrentSpice(agent.getInitialSpice());
		agent.setMetabolism(Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,maxMetabolism));
		agent.setMetabolism2(Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,maxMetabolism2));
		agent.setVision(Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,maxVision));
		agent.setDeathAge(Globals.env.uniformIntRand.getIntegerWithMin$withMax(deathAgeMin,deathAgeMax));
		agent.setSex(Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,2));
		
		agentBirth(agent);

		sugarSpace.getAgentGrid().setOverwriteWarnings(true);
		return this;
	}

	public Object agentBirth(SugarAgent agent){
		if(dbg==1) System.out.println("===agentBirth()in===");
		agentList.addLast(agent);
		numBirth++;
		return this;
	}

	public Object agentDeath(SugarAgent agent){
		if(dbg==1) System.out.println("ModelSwarm.agentDeath()in");
		reaperQueue.addLast(agent);
		if(replacement!=0)
			addNewRandomAgent();
		return this;
	}

	public Object reapAgents(){
		Index index;
		SugarAgent agent;

		index = reaperQueue.listBegin(aZone); 
		if(dbg==1) System.out.println("===reapAgents()===");
		while((agent=(SugarAgent)index.next())!=null){
			if(dbg==1) System.out.println("===reapAgents()2===");
			agentList.remove(agent);
			numDeath++;
			if(dbg==1) System.out.println("===reapAgents()3===");
			agent.drop();
			if(dbg==1) System.out.println("===reapAgents()4===");
		}
		if(dbg==1) System.out.println("===reapAgents()5===");
		reaperQueue.removeAll();
		if(dbg==1) System.out.println("===reapAgents()out===");
		return this;
	}

	public Object addToAgentList(SugarAgent agent){
		agentList.addLast(agent);
		numBirth++;
		return this;
	}

	public Object addChildren(){
		Index index;
		SugarAgent agent;

		index = childList.listBegin(aZone); 
		if(dbg==1) System.out.println("===addChildren()in===");
		while((agent=(SugarAgent)index.next())!=null){
			if(dbg==1) System.out.println("===addChildren()2===");
			agentList.addLast(agent);
			numBirth++;
			if(dbg==1) System.out.println("===addChildren()3===");
			if(dbg==1) System.out.println("===addChildren()4===");
		}
		if(dbg==1) System.out.println("===addChildren()5===");
		childList.removeAll();
		if(dbg==1) System.out.println("===addChildren()out===");
		return this;
	}


	public Activity activateIn(Swarm swarmContext){
		super.activateIn(swarmContext);
		modelSchedule.activateIn(this);
		return getActivity();
	}

	public SugarSpace getSugarSpace(){


		return sugarSpace;
	}

	public List getAgentList(){
		return agentList;
	}

}

