import swarm.simtoolsgui.*;
import swarm.*;
import swarm.activity.*;
import swarm.analysis.*;
import swarm.objectbase.*;
import swarm.defobj.*;

/**
 * ExperSwarm�͕����̃��f��(ModelSwarm)���V�~�����[�g����B<BR>
 * <BR>
 * ���f���̃p�����[�^��ݒ肷�镔����ParameterManager�Ƃ��ĕʃN���X�ɕ������Ă���B
 */
public class ExperSwarm extends GUISwarmImpl{
	int modelTime; // model run time
	public int numModelsRun; // number of models run
	
	ActionGroup experActions; // schedule data structs
	Schedule experSchedule;
	
	ModelSwarm modelSwarm; // the Swarm we're iterating
	
	ParameterManager parameterManager; // An object to manage model parameters
	
	// ���|��
	//File logFile; // File to log run results
	
	// Display objects, widgets, etc.
	
	EZGraph resultGraph; // graphing widget
	EmptyProbeMap modelProbeMap; // the ProbeMap for the modelSwarm
	
	public ExperSwarm(Zone aZone){
		super(aZone);
		EmptyProbeMap theProbeMap;
		
		numModelsRun=0;
		
		theProbeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		theProbeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"numModelsRun",this.getClass()));
		Globals.env.probeLibrary.setProbeMap$For(theProbeMap,this.getClass());
	}
	
	public Object _resultGraphDeath_(Object caller){
		resultGraph.drop();
		resultGraph = null;
		return this;
	}
	
	/**
	 * �܂��A����܂łƈقȂ�AbuildObjects��ModelSwarm�̃I�u�W�F�N�g��
	 * �������Ȃ����Ƃɒ��ӁB	 * �����ōs���̂́AProbe�̐����A  EZGraph�̐����A
	 * ���ʂ��o�͂��邽�߂̃t�@�C��"log.file"�̐����i����͖��|��j�ł���B<BR>
	 * <BR>
	 * ModelSwarm�̓V�~�����[�V�����Ő����E�p�����J��Ԃ����߁A
	 * ���������\�b�hbuildModel�ɂ܂Ƃ߂��B
	 */
	public Object buildObjects(){
		super.buildObjects();
		
		// Build the parameter manager, using the parameterManager data stored in
		// the `bug.scm' datafile.
		
		parameterManager = (ParameterManager)
			Globals.env.lispAppArchiver.getWithZone$key(this,"parameterManager");
		if(parameterManager==null){
			System.out.println("Can't find the parameterManager parameters.");
			System.exit(1);
		}
		parameterManager.initializeParameters(this);
		
		// Build a probeDisplay on ourself
		
		Globals.env.createArchivedProbeDisplay (this, "experSwarm");
		
		// build the EZGraph for model results
		
		resultGraph=new EZGraphImpl(
			this,"Model Run Times","Model #","Run Time","resultGraph");
		
		try {
			resultGraph.enableDestroyNotification$notificationMethod
			  (this, new Selector(getClass(),"_resultGraphDeath_",false));
		} catch (Exception e) {
			System.err.println ("Exception _resultGraphDeath_: "+e.getMessage());
		}
		
		// Create a sequence to track model run times.
		// Since we keep changing models, we feed from our own method
		// "getModelTime" which will probe the correct instance of 
		// ModelSwarm
		
		try {
			resultGraph.createSequence$withFeedFrom$andSelector(
				"runTime",this,new Selector(
				this.getClass(),"getModelTime", false));
		} catch (Exception e) {
			System.err.println ("Exception: " + e.getMessage());
		}
		
		// Allow the user to alter experiment parameters
		
		getControlPanel().setStateStopped();
		
		// Create the OutFile object to log the runs
		//���|��
		//logFile = [OutFile create: self setName: "log.file"];
		
		return this;
	}
	
	/**
	 * ActionGroup�����A�����̃A�N�V���������Ԃɓo�^����B
	 * ActionGroup���X�P�W���[���ɓo�^���邱�ƂŁA���s�����������B
	 * ���̂悤�ɁA�A�N�V�����̓O���[�v�����ĊǗ����邱�Ƃ��ł���B
	 */
	public Object buildActions(){
		super.buildActions();
		experActions=new ActionGroupImpl(this);
		
		try {
			experActions.createActionTo$message(this,
				new Selector(this.getClass(),"buildModel",false));
			experActions.createActionTo$message(this,
				new Selector(this.getClass(),"runModel",false));
			experActions.createActionTo$message(this,
				new Selector(this.getClass(),"doStats",false));
			experActions.createActionTo$message(this,
				new Selector(this.getClass(),"showStats",false));
			experActions.createActionTo$message(this,
				new Selector(this.getClass(),"logResults",false));
			experActions.createActionTo$message(this,
				new Selector(this.getClass(),"dropModel",false));
			
			// Check to see if the experiment has ended (all the models have been run).
			
			experActions.createActionTo$message(this,
				new Selector(this.getClass(),"checkToStop",false));
			
			// Schedule the update of the probe display
			
			experActions.createActionTo$message(Globals.env.probeDisplayManager,
				new Selector(Globals.env.probeDisplayManager.getClass(),"update",false));
			
			// Finally, schedule an update for the whole user interface code.
			
			experActions.createActionTo$message(
				getActionCache(),
				new Selector(getActionCache().getClass(),"doTkEvents",true));
				
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		experSchedule = new ScheduleImpl(this,1);
		experSchedule.at$createAction(0,experActions);
		
		return this;
	}


	public Activity activateIn(Swarm context){
		super.activateIn(context);
		experSchedule.activateIn(this);
		return getActivity();
	}
	
	/**
	 * ModelSwarm�𐶐�����B<BR>
	 * �v���O��������ModelSwarm�͉��x����������邪�A
	 * ModelSwarm�̂��߂�Probe�͍ŏ��Ɉ�񐶐�����΂悢�B<BR>
	 * <BR>
	 * ModelSwarm��activate�����̂́AExperSwarm���ł͂Ȃ��Anull�łł���B
	 * ����́AExperSwarm�̃^�C���X�e�b�v��ModelSwarm�̃^�C���X�e�b�v���Ⴄ���߂ł���B
	 * ���̂��߁AExperSwarm�̃X�P�W���[����ModelSwarm�̃X�P�W���[����
	 * �}�[�W���邱�Ƃ͂ł��Ȃ��B
	 */
	public Object buildModel(){
		modelSwarm=new ModelSwarm(this);
		
		// If this is the first model, create a custom probeMap for modelSwarm
		// and construct a graph displaying model results
		
		if(numModelsRun==0){
			modelProbeMap=new EmptyProbeMapImpl(this,modelSwarm.getClass());
			
			modelProbeMap.addProbe(
				Globals.env.probeLibrary.getProbeForVariable$inClass(
					"worldXSize",modelSwarm.getClass()));
			modelProbeMap.addProbe(
				Globals.env.probeLibrary.getProbeForVariable$inClass(
					"worldYSize",modelSwarm.getClass()));
			modelProbeMap.addProbe(
				Globals.env.probeLibrary.getProbeForVariable$inClass(
					"seedProb",modelSwarm.getClass()));
			modelProbeMap.addProbe(
				Globals.env.probeLibrary.getProbeForVariable$inClass(
					"bugDensity",modelSwarm.getClass()));
			
			Globals.env.probeLibrary.setProbeMap$For(
				modelProbeMap,modelSwarm.getClass());
		}
		
		// Now, we invoke the parameterManager to initialize the model
		
		parameterManager.initializeModel(modelSwarm);
		
		modelSwarm.buildObjects();
		modelSwarm.buildActions();
		modelSwarm.activateIn(null);
		
		return this;
	}
	
	/**
	 * ModelSwarm�����Aactivate������A������ModelSwarm�𑖂点��B
	 * ModelSwarm��terminate������A����͂����ɖ߂��Ă���B<BR>
	 */
	public Object runModel(){
		System.out.println("\nStarting model "+(numModelsRun+1)+"\n");
		
		modelSwarm.getActivity().run();
		
		System.out.println("\nModel "+(numModelsRun+1)+" is done\n");
		
		numModelsRun++; // increment count of models
		
		return this;
	}
	

	/**
	 * ModelSwarm��time���擾���A�\������B
	 */
	public Object doStats(){
		modelTime=modelSwarm.getTime();
		System.out.println("Length of this run = "+modelTime+"\n");
		return this;
	}
	
	/**
	 * EZGraph�Ƀf�[�^��n�����߂̃��\�b�h�B
	 * EZGraph��ModelSwarm���璼��time���擾���邱�Ƃ͂ł��Ȃ��B
	 * �Ȃ��Ȃ�AModelSwarm�̃C���X�^���X����������Ă��Ȃ�������A
	 * �C���X�^���X���ς�����肷�邩��ł���B
	 * @return ModelSwarm��time��Ԃ� 
	 */
	public int getModelTime(){
		return modelSwarm.getTime();
	}
	
	/**
	 * ���|��
	 * This uses the OutFile object to log the parameters 
	 * and results of a run to the file "log.file".<BR>
	 * <BR>
	 *   This makes use of an OutFile object to archive the parameter settings and 
	 * results for each run of the model. It asks the ParameterManager to print
	 * out its state into the file as well, so the specific parameters for
	 * each run will be recorded together with the results of the run.
	 */
	public Object logResults(){
		/* ���|��
		// This uses the OutFile object to log the parameters 
		// and results of a run to the file "log.file"
		
		[logFile putString: "--------------------------------\n\n"];
		
		[logFile putString: "Model # "]; 
		[logFile putInt: numModelsRun];
		
		[logFile putNewLine];
		
		// have the parameterManager log its state
		[parameterManager printParameters: logFile];
		
		[logFile putNewLine];
		
		[logFile putString: "Time for this run = "];
		[logFile putInt: modelTime];
		
		[logFile putNewLine];
		*/
		return this;
	}
	
	/**
	 * resultGraph��`�悷��B���̑����ExperSwarm�̃X�P�W���[���ɓo�^���邱�Ƃ��ł���B
	 * �������Ȃ��̂́A�ʂ̃O���t���������ꍇ�ɁA������step�������邾���ł����悤��
	 * ���邽�߂ł���B����ɂ���āA�X�P�W���[�����V���v���ɕۂ��Ƃ��ł���B
	 */
	public Object showStats(){
		if(resultGraph!=null)
			resultGraph.step(); // step the result Graph
		return this;
	}
	
	/**
	 * ModelSwam�̖����I�ȃf�X�g���N�^�B
	 * ModelSwarm��FoodSpace��World��drop������B
	 * �i�K�x�b�W�E�R���N�^������Java�ɂ��̂悤�ȑ���͕K�v�Ȃ��悤�Ɏv���B
	 * ���ہA���ꂪ�Ȃ��Ă��v���O�����͓��삷��B�j
	 */
	public Object dropModel(){
		modelSwarm.drop();
		return this;
	}
	
	/**
	 * ���ׂẴ��f�������s������������I������B<BR>
	 * <BR>
	 * parameterManager�̃��\�b�hstepParametersIf���Ăяo���B
	 * ���ׂẴ��f�������s���I����Ă���ꍇ�Anull���Ԃ邽�߁A
	 * �����̏I�����킩��B
	 */
	public Object checkToStop(){
		if(parameterManager.stepParameters()==null){
			Globals.env.probeDisplayManager.update();
			getActionCache().doTkEvents();
			
			System.out.println("\n All the models have run!\n");
			
			//���|��
			//logFile.drop();
			getControlPanel().setStateStopped();
		}
		return this;
	}
}
