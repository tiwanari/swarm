//ObserverSwarm.java

import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;
import swarm.analysis.*;
import swarm.collections.*;
import java.lang.String;

public class ObserverSwarm extends GUISwarmImpl{
	public ModelSwarm modelSwarm;

	public String parameterFile;
	public String dataArchiveName;
	public int displayFrequency;
	public int drawPopulationGraph;
	public int drawAttributeGraph;
	public int drawBirthGraph;
	public int drawMRSGraph;
	public int drawPriceGraph;
	public int drawTradeGraph;
	public int drawWealthGraph;
	public int drawWealthMaxGraph;
	public int drawWealthHistogram;
	public int drawAgeHistogram;
	public int stopPeriod;
	public int step;
	public int period;

	Colormap colormap;
	ZoomRaster worldRaster;
	Value2dDisplay sugarDisplay;
	Value2dDisplay sugarAndSpiceDisplay;
	Object2dDisplay agentDisplay;
	EZGraph populationGraph;
	EZGraph attributeGraph;
	EZGraph birthGraph;
	EZGraph mrsGraph;
	EZGraph priceGraph;
	EZGraph tradeGraph;
	EZGraph wealthGraph;
	EZGraph wealthMaxGraph;
	EZBin wealthHistogram;
	EZBinC wealthHistogramC;
	EZBin ageHistogram;
	EZBinC ageHistogramC;
	Zone aZone;

	ActionGroup displayActions;
	Schedule displaySchedule;

	public ObserverSwarm(Zone aZone){
		super(aZone);
		this.aZone=aZone;
		
		EmptyProbeMap probeMap;


		displayFrequency=1;
		drawPopulationGraph=1;
		drawAttributeGraph=1;
		drawBirthGraph=1;
		drawMRSGraph=0;
		drawPriceGraph=0;
		drawTradeGraph=0;
		drawWealthGraph=1;
		drawWealthMaxGraph=1;
		drawWealthHistogram=0;
		drawAgeHistogram=0;
		parameterFile=null;
		stopPeriod=100;
		step=-2;
		period=0;

		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());

		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("parameterFile",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass("dataArchiveName",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawPopulationGraph",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawAttributeGraph",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawBirthGraph",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawMRSGraph",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawPriceGraph",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawTradeGraph",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawWealthGraph",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawWealthMaxGraph",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawWealthHistogram",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("drawAgeHistogram",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("displayFrequency",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass("stopPeriod",this.getClass()));

		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass()); 
	}

	public Object _wealthHistogramDeath_(Object caller){ 
		wealthHistogram.drop();
		wealthHistogram = null;
		return this;
	}

	public Object _ageHistogramDeath_(Object caller){ 
		ageHistogram.drop();
		ageHistogram = null;
		return this;
	}

	public Object _worldRasterDeath_(Object caller){ 
		worldRaster.drop();
		worldRaster=null;
		return this;
	}

	public Object buildObjects(){
		int i,j;
		int maxsgr,maxspc;
		SugarSpace sugarSpace;
		List agentList;
		
		super.buildObjects();

		Globals.env.createArchivedProbeDisplay(this, "ObserverSwarm");


		if(parameterFile!=null){
			LispArchiver archiver=new LispArchiverImpl(aZone,parameterFile);

			if((modelSwarm=(ModelSwarm)archiver.getWithZone$key(aZone,"model"))==null){
				System.out.println("Can't find the parameters to create modelSwarm");
				System.exit(1);
			}
			archiver.drop();
		}
		else{
			modelSwarm =new ModelSwarm(aZone);
		}


		Globals.env.createArchivedProbeDisplay(modelSwarm,"modelSwarm");

		getControlPanel().setStateStopped();

		modelSwarm.buildObjects();

		sugarSpace=modelSwarm.getSugarSpace();
		agentList=modelSwarm.getAgentList();

		maxsgr=modelSwarm.getSugarSpace().getGlobalMaxSugar();
		maxspc=modelSwarm.getSugarSpace().getGlobalMaxSpice();


		colormap=new ColormapImpl(aZone);

		
		// colors for sugar and spice
		for(i = 0; i < maxspc; i++)
			for(j=0; j<maxsgr; j++)
				colormap.setColor$ToRed$Green$Blue((byte)(i*8+j), j/(maxsgr-1.0)/2+i/(maxspc-1.0), j/(maxsgr-1.0), 0.0);

		// colors for agents
		for(i = 0; i < 8; i++)
			for(j=0; j<8; j++)
				colormap.setColor$ToRed$Green$Blue((byte)(128+i+j*8), (double) i/7, (double) j/7, 1.0);

		
		for(i=0; i<8; i++)
			colormap.setColor$ToRed$Green$Blue((byte)(192+i), ((double)(i&4)+1.0)/2, ((double)(i&2)+1.0)/2, ((double)(i&1)+1.0)/2);


		colormap.setColor$ToName((byte)100,"red");
		colormap.setColor$ToName((byte)101,"blue");
		colormap.setColor$ToName((byte)102,"green");

		worldRaster=new ZoomRasterImpl(aZone);
		Globals.env.setWindowGeometryRecordName(worldRaster,"worldRaster");
		try{
			worldRaster.enableDestroyNotification$notificationMethod(this,new Selector(this.getClass(),"_worldRasterDeath_",false)); 
		} catch(Exception e){
			System.err.println ("Exception11: " + e.getMessage());
		}

		worldRaster.setColormap(colormap);
		worldRaster.setZoomFactor(6);
		worldRaster.setWidth$Height(sugarSpace.getAgentGrid().getSizeX(),sugarSpace.getAgentGrid().getSizeY());
		worldRaster.setWindowTitle("SugarScape");
		worldRaster.pack();

		sugarDisplay=new Value2dDisplayImpl(aZone, worldRaster, colormap, modelSwarm.getSugarSpace().getSugarValues());
		sugarAndSpiceDisplay=new Value2dDisplayImpl(aZone, worldRaster, colormap, modelSwarm.getSugarSpace().getSugarAndSpiceValues());
		
		try{
			agentDisplay=new Object2dDisplayImpl(aZone,worldRaster,sugarSpace.getAgentGrid(),new Selector(Class.forName("SugarAgent"),"drawSelfOn",false));
		} catch(Exception e) {
			System.err.println ("Exception14: " + e.getMessage());
		}
			
		agentDisplay.setObjectCollection(modelSwarm.getAgentList());

		try{
			worldRaster.setButton$Client$Message(3,agentDisplay,new Selector(agentDisplay.getClass(), "makeProbeAtX$Y",false)); 
		} catch(Exception e) {
			System.err.println ("Exception15: " + e.getMessage());
		}


		if(drawPopulationGraph!=0){
			populationGraph=new EZGraphImpl(aZone,"Population over time","time","population","populationGraph");
			try{
				populationGraph.createSequence$withFeedFrom$andSelector("total",agentList,new Selector(agentList.getClass(),"getCount",false)); 
			} catch(Exception e) {
				System.err.println ("Exception16: " + e.getMessage());
			}

			try{
				populationGraph.createTotalSequence$withFeedFrom$andSelector("blue",agentList,new Selector(Class.forName("SugarAgent"),"isBlue",false)); 
			} catch(Exception e) {
				System.err.println ("Exception16: " + e.getMessage());
			}

			try{
				populationGraph.createTotalSequence$withFeedFrom$andSelector(" ",agentList,new Selector(Class.forName("SugarAgent"),"dummy",false)); 
			} catch(Exception e) {
				System.err.println ("Exception16: " + e.getMessage());
			}

			try{
				populationGraph.createTotalSequence$withFeedFrom$andSelector("red",agentList,new Selector(Class.forName("SugarAgent"),"isRed",false)); 
			} catch(Exception e) {
				System.err.println ("Exception16: " + e.getMessage());
			}

		}

		if(drawAttributeGraph!=0){
			attributeGraph=new EZGraphImpl(aZone,"Agent attributes over time","time","attribute","attributeGraph");
			try{
				attributeGraph.createAverageSequence$withFeedFrom$andSelector("vision",agentList,new Selector(Class.forName("SugarAgent"),"getVision",false));
			} catch(Exception e) {
				System.err.println ("Exception17: " + e.getMessage());
			}

			try{
				attributeGraph.createStdDevSequence$withFeedFrom$andSelector("vision sd",agentList,new Selector(Class.forName("SugarAgent"),"getVision",false));
			} catch(Exception e) {
				System.err.println ("Exception17: " + e.getMessage());
			}

			try{
				attributeGraph.createAverageSequence$withFeedFrom$andSelector("metabo",agentList,new Selector(Class.forName("SugarAgent"),"getMetabolism",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				attributeGraph.createStdDevSequence$withFeedFrom$andSelector("metabo sd",agentList,new Selector(Class.forName("SugarAgent"),"getMetabolism",false));
			} catch(Exception e) {
				System.err.println ("Exception17: " + e.getMessage());
			}

			if(modelSwarm.spice==1){
				try{
					attributeGraph.createAverageSequence$withFeedFrom$andSelector("metabo2",agentList,new Selector(Class.forName("SugarAgent"),"getMetabolism2",false));
				} catch(Exception e) {
					System.err.println ("Exception18: " + e.getMessage());
				}

				try{
					attributeGraph.createStdDevSequence$withFeedFrom$andSelector("metabo2 sd",agentList,new Selector(Class.forName("SugarAgent"),"getMetabolism2",false));
				} catch(Exception e) {
					System.err.println ("Exception17: " + e.getMessage());
				}
			}

		}

		if(drawBirthGraph!=0){
			birthGraph=new EZGraphImpl(aZone,"Birth number over time","time","number","birthGraph");
			try{
				birthGraph.createSequence$withFeedFrom$andSelector("birth",modelSwarm,new Selector(modelSwarm.getClass(),"getNumBirth",false));
			} catch(Exception e) {
				System.err.println ("Exception31: " + e.getMessage());
			}

			try{
				birthGraph.createSequence$withFeedFrom$andSelector("death",modelSwarm,new Selector(modelSwarm.getClass(),"getNumDeath",false));
			} catch(Exception e) {
				System.err.println ("Exception32: " + e.getMessage());
			}

			try{
				birthGraph.createTotalSequence$withFeedFrom$andSelector(" ",agentList,new Selector(Class.forName("SugarAgent"),"dummy",false)); 
			} catch(Exception e) {
				System.err.println ("Exception16: " + e.getMessage());
			}

			try{
				birthGraph.createSequence$withFeedFrom$andSelector("killed",modelSwarm,new Selector(modelSwarm.getClass(),"getNumKilled",false));
			} catch(Exception e) {
				System.err.println ("Exception33: " + e.getMessage());
			}

		}
		
		if(drawMRSGraph!=0){
			mrsGraph=new EZGraphImpl(aZone,"MRS over time","time","log MRS","mrsGraph");
			try{
				mrsGraph.createAverageSequence$withFeedFrom$andSelector("ave MRS",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentLogMRS",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				mrsGraph.createMaxSequence$withFeedFrom$andSelector("max MRS",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentLogMRS",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				mrsGraph.createMinSequence$withFeedFrom$andSelector("min MRS",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentLogMRS",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				mrsGraph.createStdDevSequence$withFeedFrom$andSelector("MRS sd",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentLogMRS",false));
			} catch(Exception e) {
				System.err.println ("Exception17: " + e.getMessage());
			}
		}
		
		if(drawPriceGraph!=0){
			priceGraph=new EZGraphImpl(aZone,"Price over time","time","price","priceGraph");
			try{
				priceGraph.createAverageSequence$withFeedFrom$andSelector("ave price",agentList,new Selector(Class.forName("SugarAgent"),"getLastLogPrice",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				priceGraph.createMaxSequence$withFeedFrom$andSelector("max price",agentList,new Selector(Class.forName("SugarAgent"),"getLastLogPrice",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				priceGraph.createMinSequence$withFeedFrom$andSelector("min price",agentList,new Selector(Class.forName("SugarAgent"),"getLastLogPrice",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				priceGraph.createStdDevSequence$withFeedFrom$andSelector("price sd",agentList,new Selector(Class.forName("SugarAgent"),"getLastLogPrice",false));
			} catch(Exception e) {
				System.err.println ("Exception17: " + e.getMessage());
			}

		}
		
		if(drawTradeGraph!=0){
			tradeGraph=new EZGraphImpl(aZone,"Trade amount over time","time","amount","tradeGraph");
			try{
				tradeGraph.createSequence$withFeedFrom$andSelector("amount",modelSwarm,new Selector(modelSwarm.getClass(),"getTradeAmount",false));
			} catch(Exception e) {
				System.err.println ("Exception31: " + e.getMessage());
			}

		}

		if(drawWealthGraph!=0){
			wealthGraph=new EZGraphImpl(aZone,"Wealth over time","time","wealth","wealthGraph");
			try{
				wealthGraph.createAverageSequence$withFeedFrom$andSelector("ave sugar",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentSugar",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				wealthGraph.createStdDevSequence$withFeedFrom$andSelector("sugar sd",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentSugar",false));
			} catch(Exception e) {
				System.err.println ("Exception17: " + e.getMessage());
			}

			if(modelSwarm.spice==1){

			try{
				wealthGraph.createAverageSequence$withFeedFrom$andSelector("ave spice",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentSpice",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				wealthGraph.createStdDevSequence$withFeedFrom$andSelector("spice sd",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentSpice",false));
			} catch(Exception e) {
				System.err.println ("Exception17: " + e.getMessage());
			}

			try{
				wealthGraph.createAverageSequence$withFeedFrom$andSelector("ave welfare",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentWelfare",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				wealthGraph.createStdDevSequence$withFeedFrom$andSelector("welfare sd",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentWelfare",false));
			} catch(Exception e) {
				System.err.println ("Exception17: " + e.getMessage());
			}

			}
		}

		if(drawWealthMaxGraph!=0){
			wealthMaxGraph=new EZGraphImpl(aZone,"Max wealth over time","time","maxwealth","wealthMaxGraph");
			try{
				wealthMaxGraph.createMaxSequence$withFeedFrom$andSelector("max sugar",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentSugar",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			if(modelSwarm.spice==1){

			try{
				wealthMaxGraph.createMaxSequence$withFeedFrom$andSelector("max spice",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentSpice",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			try{
				wealthMaxGraph.createMaxSequence$withFeedFrom$andSelector("max welfare",agentList,new Selector(Class.forName("SugarAgent"),"getCurrentWelfare",false));
			} catch(Exception e) {
				System.err.println ("Exception18: " + e.getMessage());
			}

			}

		}

		if(drawWealthHistogram!=0){	
			EZBinC wealthHistogramC=new EZBinCImpl(new EZBinImpl());
			wealthHistogramC.createBegin(aZone);
			wealthHistogramC.setTitle("Agent wealth distribution");
			wealthHistogramC.setAxisLabelsX$Y("wealth","number of agents");
			wealthHistogramC.setBinCount(10);
			wealthHistogramC.setLowerBound(0);
			wealthHistogramC.setUpperBound(50);
			wealthHistogramC.setCollection(agentList);
			try{
				wealthHistogramC.setProbedSelector(new Selector(Class.forName("SugarAgent"),"getCurrentWelfare",false));
			} catch(Exception e) {
				System.err.println ("Exception29: " + e.getMessage());
			}
			wealthHistogram = (EZBin)wealthHistogramC.createEnd();
			Globals.env.setWindowGeometryRecordName(wealthHistogram,"wealthHistogram");
			try{
				wealthHistogram.enableDestroyNotification$notificationMethod(this,new Selector(this.getClass(),"_wealthHistogramDeath_",false)); 
			} catch(Exception e) {
				System.err.println ("Exception19: " + e.getMessage());
			}
		}

		if(drawAgeHistogram!=0){
			EZBinC ageHistogramC=new EZBinCImpl(new EZBinImpl());
			ageHistogramC.createBegin(aZone);
			ageHistogramC.setTitle("Agent age distribution");
			ageHistogramC.setAxisLabelsX$Y("age","number of agents");
			ageHistogramC.setBinCount(10);
			ageHistogramC.setLowerBound(0);
			ageHistogramC.setUpperBound(50);
			ageHistogramC.setCollection(agentList);
			try{
				ageHistogramC.setProbedSelector(new Selector(Class.forName("SugarAgent"),"getAge",false));
			} catch(Exception e) {
				System.err.println ("Exception29: " + e.getMessage());
			}
			ageHistogram = (EZBin)ageHistogramC.createEnd();
			Globals.env.setWindowGeometryRecordName(ageHistogram,"ageHistogram");
			try{
				ageHistogram.enableDestroyNotification$notificationMethod(this,new Selector(this.getClass(),"_ageHistogramDeath_",false)); 
			} catch(Exception e) {
				System.err.println ("Exception19: " + e.getMessage());
			}
		}
			
		return this;
	}

	public Object _updateHistogram_(){
		if(wealthHistogram!=null){
			wealthHistogram.reset();
			wealthHistogram.update();
			wealthHistogram.output();
		}
		return this;
	}

	public Object _updateAgeHistogram_(){
		if(ageHistogram!=null){
			ageHistogram.reset();
			ageHistogram.update();
			ageHistogram.output();
		}
		return this;
	}

	public Object _updateDisplay_(){
		if(worldRaster!=null){
			if(modelSwarm.spice==1)
				sugarAndSpiceDisplay.display();
			else sugarDisplay.display();
			agentDisplay.display();
			worldRaster.drawSelf();
		}
		step+=displayFrequency;
		if(step>=stopPeriod) {getControlPanel().setStateStopped(); step=0;}
		return this;
	}

	public String setParameterFile(String aString){
		parameterFile="parameters/"+aString+".scm";
		return parameterFile;
	}


	public Object saveParameters(String aString){
		dataArchiveName="parameters/"+aString+".scm";
		LispArchiver dataArchiver=new LispArchiverImpl(aZone,dataArchiveName);

		dataArchiver.putShallow$object("model",modelSwarm);
		dataArchiver.sync();
		dataArchiver.drop();
		return this;
	}

	public Object buildActions(){
		super.buildActions();
	
		modelSwarm.buildActions();


		

		displayActions=new ActionGroupImpl(aZone);

		try{
			displayActions.createActionTo$message(this, new Selector(this.getClass(),"_updateDisplay_",false));
		} catch(Exception e) {
			System.err.println ("Exception20: " + e.getMessage());
		}


		try{
			if(drawPopulationGraph!=0)
				displayActions.createActionTo$message(populationGraph, new Selector(populationGraph.getClass(),"step",false));
		} catch(Exception e) {
			System.err.println ("Exception22: " + e.getMessage());
		}

		try{
			if(drawAttributeGraph!=0)
				displayActions.createActionTo$message(attributeGraph, new Selector(attributeGraph.getClass(),"step",false));
		} catch(Exception e) {
			System.err.println ("Exception21: " + e.getMessage());
		}

		try{
			if(drawBirthGraph!=0)
				displayActions.createActionTo$message(birthGraph, new Selector(birthGraph.getClass(),"step",false));
		} catch(Exception e) {
			System.err.println ("Exception23: " + e.getMessage());
		}

		try{
			if(drawMRSGraph!=0)
				displayActions.createActionTo$message(mrsGraph, new Selector(mrsGraph.getClass(),"step",false));
		} catch(Exception e) {
			System.err.println ("Exception24: " + e.getMessage());
		}

		try{
			if(drawPriceGraph!=0)
				displayActions.createActionTo$message(priceGraph, new Selector(priceGraph.getClass(),"step",false));
		} catch(Exception e) {
			System.err.println ("Exception24: " + e.getMessage());
		}

		try{
			if(drawTradeGraph!=0)
				displayActions.createActionTo$message(tradeGraph, new Selector(tradeGraph.getClass(),"step",false));
		} catch(Exception e) {
			System.err.println ("Exception24: " + e.getMessage());
		}

		try{
			if(drawWealthGraph!=0)
				displayActions.createActionTo$message(wealthGraph, new Selector(wealthGraph.getClass(),"step",false));
		} catch(Exception e) {
			System.err.println ("Exception24: " + e.getMessage());
		}

		try{
			if(drawWealthMaxGraph!=0)
				displayActions.createActionTo$message(wealthMaxGraph, new Selector(wealthMaxGraph.getClass(),"step",false));
		} catch(Exception e) {
			System.err.println ("Exception24: " + e.getMessage());
		}

		try{
			if(drawWealthHistogram!=0)
				displayActions.createActionTo$message(this, new Selector(this.getClass(),"_updateHistogram_",false));
		} catch(Exception e) {
			System.err.println ("Exception23: " + e.getMessage());
		}

		try{
			if(drawAgeHistogram!=0)
				displayActions.createActionTo$message(this, new Selector(this.getClass(),"_updateAgeHistogram_",false));
		} catch(Exception e) {
			System.err.println ("Exception23: " + e.getMessage());
		}

		try{
			displayActions.createActionTo$message(Globals.env.probeDisplayManager, new Selector(Globals.env.probeDisplayManager.getClass(),"update",false));
		} catch(Exception e) {
			System.err.println ("Exception24: " + e.getMessage());
		}

		try{
			displayActions.createActionTo$message(getActionCache(), new Selector(getActionCache().getClass(),"doTkEvents",false));
		} catch(Exception e) {
			System.err.println ("Exception25: " + e.getMessage());
		}



		displaySchedule=new ScheduleImpl(aZone,displayFrequency);
		displaySchedule.at$createAction(0,displayActions);

		return this;
	}

	public Activity activateIn(Swarm swarmContext){
		super.activateIn(swarmContext);
		modelSwarm.activateIn(this);
		displaySchedule.activateIn(this);
		return getActivity();
	}


}


