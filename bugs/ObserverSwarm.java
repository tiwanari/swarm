import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.analysis.EZBin;
import swarm.analysis.EZBinC;
import swarm.analysis.EZBinCImpl;
import swarm.analysis.EZBinImpl;
import swarm.collections.List;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

/**
 * あらゆるSwarmのオブジェクトにアクセス可能なProbeを作成する。
 * Probeはオブジェクトのフィールドの値を表示・変更したり、
 * メソッドを呼び出したりするためのGUIである。
 * 注意点として、Probeで扱えるフィールドやメソッドはpublicなものでなければならない。<BR>
 * <BR>
 * ここでは、OberverSwarmとModelSwarmへのProbeが起動時に生成されるようにする。
 * さらにマウスでクリックすることによってBugへのProbeも生成されるようにする。<BR>
 * <BR>
 * ProbeはGlobals.env.createArchivedProbeDisplayで作成することができる。
 * これで作ったProbeにはpublicなフィールドがすべてのっている。
 * 表示するフィールドを選びたい場合や、メソッドものせたい場合には次のようにすればよい。<BR>
 * 1. ProbeMapにフィールドやメソッドを追加する<BR>
 * 2. ProbeLibraryにProbeMapを登録する<BR>
 * 3. Globals.env.createArchivedProbeDisplay<BR>
 * ここではObserverSwarmへのProbeはデフォルトのまま、
 * ModelSwarmへのProbeはカスタマイズしている。<BR>
 * <BR>
 * プログラムは2つのProbeを生成した時点で停止する。
 * ここで、シミュレーションのパラメータ（ModelSwarmのフィールド）
 * を修正してからStartボタンを押せば、パラメータファイルを書き換えることなく、
 * さまざまな設定をシミュレートすることができる
 * （フィールドの数値を書き換えたらEnterキーを押すこと）。<BR>
 * <BR>
 * 次の2点も確認してほしい。<BR>
 * 1. Probe上の青字のオブジェクト名をクリックすると、publicなメソッドもすべて表示される。<BR>
 * 2. そこで、右上のボタンをクリックすると、スーパークラスのProbeが生成される。<BR>
 */
public class ObserverSwarm extends GUISwarmImpl{
	/**
	 * probeで操作できるようにするためにはpublicでなければならない
	 */
	public int displayFrequency; 
	
	ActionGroup displayActions; // schedule data structs
	Schedule displaySchedule;
	ModelSwarm modelSwarm; // the Swarm we're observing
	Colormap colorMap; // allocate colours
	ZoomRaster worldRaster; // 2d display widget
	Value2dDisplay foodDisplay; // display the heat
	Object2dDisplay bugDisplay; // display the heatbugs
	EZBin ageHistogram;
	EZBinC ageHistogramC;
	EZBin energyHistogram;
	EZBinC energyHistogramC;
	Zone aZone;
	
	/**
	 * コンストラクタ。ObserverSwarmのProbeはデフォルトのまま
	 * （すべてのpublicなフィールドが表示される）でよいため、独自のprobeMapを作る必要はない。
	 * デフォルトのProbeを再現するようなコードをコメント中に示してあるから参考にしてほしい。
	 * Probeの作り方は、ModelSwarmのコンストラクタを見た方がわかりやすいだろう。
	 */
	public ObserverSwarm(Zone aZone){
		super(aZone);
		this.aZone=aZone;
		displayFrequency=1;
		
/*		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		// Add in a bunch of variables, one per simulation parameters
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("displayFrequency",this.getClass()));
        
      	// Now install our custom probeMap into the probeLibrary.
        
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
*/	}
	
	public Object _ageHistogramDeath_(Object caller){ 
		ageHistogram.drop();
		ageHistogram = null;
		return this;
	}
	
	public Object _energyHistogramDeath_(Object caller){ 
		energyHistogram.drop();
		energyHistogram = null;
		return this;
	}
	
	public Object buildObjects(){
		List agentList;
		
		
		super.buildObjects();
		
		modelSwarm = (ModelSwarm)
			Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm"); //エラーだった場合の次の処理は未実装
		if(modelSwarm==null){
			System.out.println("Can't find the modelSwarm parameters.");
			System.exit(1);
		}
		
		// Now create probe objects on the model and ourselves. This gives a
		// simple user interface to let the user change parameters.
		
		Globals.env.createArchivedProbeDisplay (modelSwarm, "modelSwarm");
		Globals.env.createArchivedProbeDisplay (this, "observerSwarm");
		
		// Instruct the control panel to wait for a button event.
		// We halt here until someone hits a control panel button.
		
		// Now that we're using Probes, the user can set the parameters
		// in the ModelSwarm probe window - we halt here to allow
		// the user to change parameters.
		
		getControlPanel().setStateStopped();
		
		modelSwarm.buildObjects();
		
		agentList=modelSwarm.getBugList();
		
		colorMap=new ColormapImpl(this);
		colorMap.setColor$ToName((byte)0,"black");//キャストが必要
		colorMap.setColor$ToName((byte)1,"red");
		colorMap.setColor$ToName((byte)2,"green");
		colorMap.setColor$ToName((byte)3,"yellow");
		
		worldRaster=new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(4);
		worldRaster.setWidth$Height(
			modelSwarm.getWorld().getSizeX(),
			modelSwarm.getWorld().getSizeY());
		worldRaster.setWindowTitle("Food Space");
		worldRaster.pack(); // draw the window.
		
		foodDisplay=new Value2dDisplayImpl(this,worldRaster,colorMap,modelSwarm.getFood());
		
		try {
			bugDisplay = new Object2dDisplayImpl(
				this,
				worldRaster,
				modelSwarm.getWorld(),
				new Selector(Class.forName("Bug"), "drawSelfOn", false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		bugDisplay.setObjectCollection(modelSwarm.getBugList());
		
		// worldRasterの上でマウスがクリックされた場合、
		// bugDisplayのメソッドmakeProbeAtX$Yを呼び出す。
		// makeProbeAtX$Yはそこにあるオブジェクト（つまりBug）のProbeを生成する。
		
		try {
			worldRaster.setButton$Client$Message(
				3,bugDisplay,new Selector(bugDisplay.getClass(), "makeProbeAtX$Y",true));
        } catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
        

        EZBinC ageHistogramC=new EZBinCImpl(new EZBinImpl());
        ageHistogramC.createBegin(aZone);
        ageHistogramC.setTitle("Bug age distribution");
        ageHistogramC.setAxisLabelsX$Y("age","number of bugs");
        ageHistogramC.setBinCount(10);
        ageHistogramC.setLowerBound(0);
        ageHistogramC.setUpperBound(1000);
        ageHistogramC.setCollection(agentList);
        try{
        	ageHistogramC.setProbedSelector(new Selector(Class.forName("Bug"),"getAge",false));
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

        EZBinC energyHistogramC=new EZBinCImpl(new EZBinImpl());
        energyHistogramC.createBegin(aZone);
        energyHistogramC.setTitle("Bug energy distribution");
        energyHistogramC.setAxisLabelsX$Y("energy","number of bugs");
        energyHistogramC.setBinCount(10);
        energyHistogramC.setLowerBound(0);
        energyHistogramC.setUpperBound(1500);
        energyHistogramC.setCollection(agentList);
        try{
        	energyHistogramC.setProbedSelector(new Selector(Class.forName("Bug"),"getEnergy",false));
        } catch(Exception e) {
        	System.err.println ("Exception30: " + e.getMessage());
        }
        energyHistogram = (EZBin)energyHistogramC.createEnd();
        Globals.env.setWindowGeometryRecordName(energyHistogram,"energyHistogram");
        try{
        	energyHistogram.enableDestroyNotification$notificationMethod(this,new Selector(this.getClass(),"_energyHistogramDeath_",false)); 
        } catch(Exception e) {
        	System.err.println ("Exception19: " + e.getMessage());
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
	
	public Object _updateEnergyHistogram_(){
		if(energyHistogram!=null){
			energyHistogram.reset();
			energyHistogram.update();
			energyHistogram.output();
		}
		return this;
	}
	
	public Object buildActions(){
		super.buildActions();
		modelSwarm.buildActions();
		displayActions=new ActionGroupImpl(this);
		
		try {
			displayActions.createActionTo$message(foodDisplay,
				new Selector(foodDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(bugDisplay,
				new Selector(bugDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(worldRaster,
				new Selector(worldRaster.getClass(),"drawSelf",false));
			displayActions.createActionTo$message(
				getActionCache(),
				new Selector(getActionCache().getClass(),"doTkEvents",false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		try{
			//if(drawAgeHistogram!=0)
				displayActions.createActionTo$message(this, new Selector(this.getClass(),"_updateAgeHistogram_",false));
				displayActions.createActionTo$message(this, new Selector(this.getClass(),"_updateEnergyHistogram_",false));
		} catch(Exception e) {
			System.err.println ("Exception23: " + e.getMessage());
		}
		
		displaySchedule = new ScheduleImpl(this,displayFrequency);
		displaySchedule.at$createAction(0,displayActions);
		
		return this;
    }
	
	public Activity activateIn(Swarm context){
    	super.activateIn(context);
		modelSwarm.activateIn(this);
		displaySchedule.activateIn(this);
		return getActivity();
	}
}
