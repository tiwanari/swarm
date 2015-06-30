import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.simtoolsgui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.analysis.*;
import swarm.*;

public class ObserverSwarm extends GUISwarmImpl{
	public int displayFrequency;
	int zoomFactor;

	ActionGroup displayActions;
	Schedule displaySchedule;

	ModelSwarm modelSwarm;

	EZGraph numGraph, ratioGraph;

	public ObserverSwarm(Zone aZone){
		super(aZone);
		displayFrequency=4;
		zoomFactor=20;
	}

	public Object buildObjects(){
		super.buildObjects();
		modelSwarm = (ModelSwarm)
			Globals.env.lispAppArchiver.getWithZone$key(this,"modelSwarm");

		modelSwarm.buildObjects();

		Globals.env.createArchivedProbeDisplay (modelSwarm,
			"modelSwarm");
		Globals.env.createArchivedProbeDisplay (this, "observerSwarm");

		numGraph=new EZGraphImpl(
			this,"Number of genes","Time","Number","numGraph");
		try {
			numGraph.createSequence$withFeedFrom$andSelector(
				"Gene P0",modelSwarm,new Selector(
				modelSwarm.getClass(),"countP0Gene", false));
			numGraph.createSequence$withFeedFrom$andSelector(
				"Gene P1",modelSwarm,new Selector(
				modelSwarm.getClass(),"countP1Gene", false));
			numGraph.createSequence$withFeedFrom$andSelector(
				"Gene T0",modelSwarm,new Selector(
				modelSwarm.getClass(),"countT0Gene", false));
			numGraph.createSequence$withFeedFrom$andSelector(
				"Gene T1",modelSwarm,new Selector(
				modelSwarm.getClass(),"countT1Gene", false));
		} catch (Exception e) {
			System.err.println ("Exception: " + e.getMessage());
		}

		ratioGraph = new EZGraphImpl(
			this,"Ratio of genes","Time","ratio","ratioGraph");
		try {
			ratioGraph.createSequence$withFeedFrom$andSelector(
				"Ratio P1",modelSwarm,new Selector(
				modelSwarm.getClass(),"getP1Ratio", false));
			ratioGraph.createSequence$withFeedFrom$andSelector(
				"Ratio T1",modelSwarm,new Selector(
				modelSwarm.getClass(),"getT1Ratio", false));
			ratioGraph.createSequence$withFeedFrom$andSelector(
				"Ratio T1 at Equilibrium",modelSwarm,new Selector(
				modelSwarm.getClass(),"getEquilibriumValue", false));
		} catch (Exception e) {
			System.err.println ("Exception: " + e.getMessage());
		}

		getControlPanel().setStateStopped();
		return this;
	}

	public Object buildActions(){
		super.buildActions();
		displayActions=new ActionGroupImpl(this);
		try {
			displayActions.createActionTo$message(modelSwarm,
				new Selector(modelSwarm.getClass(),"step",false));
			displayActions.createActionTo$message(numGraph,
				new Selector(numGraph.getClass(),"step",false));
			displayActions.createActionTo$message(ratioGraph,
				new Selector(numGraph.getClass(),"step",false));
			displayActions.createActionTo$message(this,
				new Selector(this.getClass(),"checkToStop",false));
			displayActions.createActionTo$message(
				getActionCache(),
				new Selector(getActionCache().getClass(),"doTkEvents",true));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}

		displaySchedule = new ScheduleImpl(this,1);
		displaySchedule.at$createAction(0,displayActions);

		return this;
	}

	public Activity activateIn(Swarm context){
		super.activateIn(context);
		displaySchedule.activateIn(this);
		return getActivity();
	}

	// 次の世代まで進めるか判断．進める場合は世代を進める．
	public void checkToStop(){
		// 終了条件を満たした場合
		if(!modelSwarm.checkToStop())
			getControlPanel().setStateStopped();
	}
}
