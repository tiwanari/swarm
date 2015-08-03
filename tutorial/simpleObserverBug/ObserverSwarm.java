import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

/**
 * ObserverSwarmはModelSwarmとworldRasterを持つ。
 * ModelSwarmはシミュレーション本体、worldRasterはシミュレーションの様子
 * を表示するGUIである。<BR>
 * <BR>
 * メソッドbuildObjectのなかで、ModelSwarmとworldRasterは作られるが、
 * どちらもObserverSwarmと同じZoneで作られるため、ObserverSwarmが無くなれば
 * 無くなる。
 * <BR>
 * ModelSwarmを作った後、control panelを停止させる。
 * これによって、ModelSwarmが内部のオブジェクトを作る前に、
 * パラメータを修正することができる（これは次のチュートリアルで試みる）。<BR>
 * <BR>
 * Startボタンが押されると、プログラムの実行は再開され、
 * ModelSwarmのbuildObjectsが呼び出される。<BR>
 * <BR>
 * シミュレーションの本体（ModelSwarm）を作った後で、それを観察するための
 * オブジェクトを作る。これについてはbuildObjectsの説明を参照。<BR>
 * <BR>
 * スケジュールについては、buildActionsの説明を参照。
 */
public class ObserverSwarm extends GUISwarmImpl{
	int displayFrequency; // one parameter: update freq
	
	ActionGroup displayActions; // schedule data structs
	Schedule displaySchedule;
	
	ModelSwarm modelSwarm; // the Swarm we're observing
	
	// Lots of display objects. First, widgets
	Colormap colorMap; // allocate colours
	ZoomRaster worldRaster; // 2d display widget
	
	// Now, higher order display and data objects
	Value2dDisplay foodDisplay; // display the heat
	Object2dDisplay bugDisplay; // display the heatbugs
	
	public ObserverSwarm(Zone aZone){
		super(aZone);
		displayFrequency=1;
	}
	
	/** 
	 * まず、simpleSwarmBug3と同様にして、パラメータ・ファイルbug.scm
	 * をもとに、ModelSwarmを作り、ModelSwarmのbuildObjectsを呼ぶ。<BR>
	 * <BR>
	 * 他に、色と数字の対応（colorMap）、実際に描くウィンドウ（worldRaster）、
	 * FoodSpaceからworldRasterへの写像（foodDisplay）、
	 * BugのcollectionからworldRasterへの写像（bugDisplay）
	 * もここで生成する。 
	 */
	public Object buildObjects(){
		super.buildObjects();
		modelSwarm = (ModelSwarm)Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm");
		if(modelSwarm==null){
			System.out.println("Can't find the modelSwarm parameters.");
			System.exit(1);
		}
		
		// Instruct the control panel to wait for a button event.
		// We halt here until someone hits a control panel button.
		
		getControlPanel().setStateStopped();
		
		// OK - the user said "go" so we're ready to start
		
		modelSwarm.buildObjects();

		// Now get down to building our own display objects.
		// First, create a colormap: this is a global resource, the information
		// here is used by lots of different objects.
		
		colorMap=new ColormapImpl(this);
		colorMap.setColor$ToName((byte)0,"black"); //キャストが必要
		colorMap.setColor$ToName((byte)1,"red");
		colorMap.setColor$ToName((byte)2,"green");
		
		// Next, create a 2d window for display, set its size, zoom factor, title.
		
		worldRaster=new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(4);
		worldRaster.setWidth$Height(
			modelSwarm.getWorld().getSizeX(),
			modelSwarm.getWorld().getSizeY());
		worldRaster.setWindowTitle("Food Space");
		worldRaster.pack(); // draw the window.
		
		// Now create a Value2dDisplay: this is a special object that will
		// display arbitrary 2d value arrays on a given Raster widget.
		
		foodDisplay=new Value2dDisplayImpl(this,worldRaster,colorMap,modelSwarm.getFood());
		
		// And also create an Object2dDisplay: this object draws bugs on
		// the worldRaster widget for us.
		
		try {
			bugDisplay = new Object2dDisplayImpl(
				this,
				worldRaster,
				modelSwarm.getWorld(),
				new Selector(Class.forName("Bug"), "drawSelfOn", false));
		} catch (Exception e) {
			System.exit(1);
		}
		bugDisplay.setObjectCollection(modelSwarm.getBugList());
		
		return this;
	}
	
	/**
	 * スケジュールを生成する。<BR>
	 * <BR>
	 * まず下位にあるModelSwarmのスケジュールを生成する。
	 * 次に自身のスケジュールを作る。<BR>
	 * <BR>
	 * タスクはまず、displayActionsとしてまとめられ、displayActionsが
	 * displayScheduleに登録される。displayActionsには、foodDisplay・bugDisplay
	 * がそれぞれworldRasterに書き込む(display)ことと、worldRasterの描画(drawSelf)
	 * である。
	 */
	public Object buildActions(){
		
		// First, let our model swarm build its own schedule.
		
		modelSwarm.buildActions();
		
		// Create an ActionGroup for display: a bunch of things that occur in
		// a specific order, but at one step of simulation time. Some of these
		// actions could be executed in parallel, but we don't explicitly
		// notate that here.
		
		displayActions=new ActionGroupImpl(this);
		
		// Schedule up the methods to draw the display of the world
		
		try {
			displayActions.createActionTo$message(foodDisplay,
				new Selector(foodDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(bugDisplay,
				new Selector(bugDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(worldRaster,
				new Selector(worldRaster.getClass(),"drawSelf",false));
			displayActions.createActionTo$message(getActionCache(),
				new Selector(getActionCache().getClass(),"doTkEvents",false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		// And the display schedule. Note the repeat interval is set from our
		// own Swarm data structure. Display is frequently the slowest part of a
		// simulation, so redrawing less frequently can be a help.
		
		displaySchedule = new ScheduleImpl(this,displayFrequency);
		displaySchedule.at$createAction(0,displayActions);
		
		return this;
    }

	/**
	 * スケジュールをactivateする（activateされたスケジュールはrunできる）。
	 * ObserverSwarmはトップ・レベルであるから、nullの中でactivateされる
	 * （simpleObserverBugの中で呼び出される）。
	 * 下位レベルにあるもの（ModelSwarmとdisplaySchedule）はObserverSwarm
	 * （つまりthis）の中でactivateされる。
	 */
	public Activity activateIn(Swarm context){
    	super.activateIn(context);
		
		// Activate the model swarm in ourselves. The model swarm is a
		// subswarm of the observer swarm.
		
		modelSwarm.activateIn(this);
		
		// Now activate our schedule in ourselves. This arranges for the
		// execution of the schedule we built.
		
		displaySchedule.activateIn(this);
		
		// Activate returns the swarm activity - the thing that's ready to run.
		
		return getActivity();
	}
}
