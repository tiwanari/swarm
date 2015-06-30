import swarm.simtoolsgui.*;
import swarm.*;
import swarm.activity.*;
import swarm.analysis.*;
import swarm.objectbase.*;
import swarm.defobj.*;

/**
 * ExperSwarmは複数のモデル(ModelSwarm)をシミュレートする。<BR>
 * <BR>
 * モデルのパラメータを設定する部分はParameterManagerとして別クラスに分離している。
 */
public class ExperSwarm extends GUISwarmImpl{
	int modelTime; // model run time
	public int numModelsRun; // number of models run
	
	ActionGroup experActions; // schedule data structs
	Schedule experSchedule;
	
	ModelSwarm modelSwarm; // the Swarm we're iterating
	
	ParameterManager parameterManager; // An object to manage model parameters
	
	// 未翻訳
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
	 * まず、これまでと異なり、buildObjectsはModelSwarmのオブジェクトを
	 * 生成しないことに注意。	 * ここで行うのは、Probeの生成、  EZGraphの生成、
	 * 結果を出力するためのファイル"log.file"の生成（これは未翻訳）である。<BR>
	 * <BR>
	 * ModelSwarmはシミュレーションで生成・廃棄を繰り返すため、
	 * 生成をメソッドbuildModelにまとめた。
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
		//未翻訳
		//logFile = [OutFile create: self setName: "log.file"];
		
		return this;
	}
	
	/**
	 * ActionGroupを作り、複数のアクションを順番に登録する。
	 * ActionGroupをスケジュールに登録することで、実行準備が整う。
	 * このように、アクションはグループ化して管理することができる。
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
	 * ModelSwarmを生成する。<BR>
	 * プログラム内でModelSwarmは何度も生成されるが、
	 * ModelSwarmのためのProbeは最初に一回生成すればよい。<BR>
	 * <BR>
	 * ModelSwarmがactivateされるのは、ExperSwarm内ではなく、nullでである。
	 * これは、ExperSwarmのタイムステップとModelSwarmのタイムステップが違うためである。
	 * そのため、ExperSwarmのスケジュールとModelSwarmのスケジュールを
	 * マージすることはできない。
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
	 * ModelSwarmを作り、activateした後、ここでModelSwarmを走らせる。
	 * ModelSwarmがterminateしたら、制御はここに戻ってくる。<BR>
	 */
	public Object runModel(){
		System.out.println("\nStarting model "+(numModelsRun+1)+"\n");
		
		modelSwarm.getActivity().run();
		
		System.out.println("\nModel "+(numModelsRun+1)+" is done\n");
		
		numModelsRun++; // increment count of models
		
		return this;
	}
	

	/**
	 * ModelSwarmのtimeを取得し、表示する。
	 */
	public Object doStats(){
		modelTime=modelSwarm.getTime();
		System.out.println("Length of this run = "+modelTime+"\n");
		return this;
	}
	
	/**
	 * EZGraphにデータを渡すためのメソッド。
	 * EZGraphはModelSwarmから直接timeを取得することはできない。
	 * なぜなら、ModelSwarmのインスタンスが生成されていなかったり、
	 * インスタンスが変わったりするからである。
	 * @return ModelSwarmのtimeを返す 
	 */
	public int getModelTime(){
		return modelSwarm.getTime();
	}
	
	/**
	 * 未翻訳
	 * This uses the OutFile object to log the parameters 
	 * and results of a run to the file "log.file".<BR>
	 * <BR>
	 *   This makes use of an OutFile object to archive the parameter settings and 
	 * results for each run of the model. It asks the ParameterManager to print
	 * out its state into the file as well, so the specific parameters for
	 * each run will be recorded together with the results of the run.
	 */
	public Object logResults(){
		/* 未翻訳
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
	 * resultGraphを描画する。この操作はExperSwarmのスケジュールに登録することもできる。
	 * そうしないのは、別のグラフがあった場合に、ここにstepを加えるだけでいいように
	 * するためである。これによって、スケジュールをシンプルに保つことができる。
	 */
	public Object showStats(){
		if(resultGraph!=null)
			resultGraph.step(); // step the result Graph
		return this;
	}
	
	/**
	 * ModelSwamの明示的なデストラクタ。
	 * ModelSwarmにFoodSpaceとWorldをdropさせる。
	 * （ガベッジ・コレクタがあるJavaにこのような操作は必要ないように思う。
	 * 実際、これがなくてもプログラムは動作する。）
	 */
	public Object dropModel(){
		modelSwarm.drop();
		return this;
	}
	
	/**
	 * すべてのモデルを実行したら実験を終了する。<BR>
	 * <BR>
	 * parameterManagerのメソッドstepParametersIfを呼び出す。
	 * すべてのモデルを実行し終わっている場合、nullが返るため、
	 * 実験の終了がわかる。
	 */
	public Object checkToStop(){
		if(parameterManager.stepParameters()==null){
			Globals.env.probeDisplayManager.update();
			getActionCache().doTkEvents();
			
			System.out.println("\n All the models have run!\n");
			
			//未翻訳
			//logFile.drop();
			getControlPanel().setStateStopped();
		}
		return this;
	}
}
