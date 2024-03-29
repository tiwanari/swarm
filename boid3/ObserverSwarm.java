import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

public class ObserverSwarm extends GUISwarmImpl {
	public int displayFrequency;

	ActionGroup displayActions;
	Schedule displaySchedule;

	ModelSwarm modelSwarm;

	Colormap colorMap;
	ZoomRaster worldRaster;

	Value2dDisplay foodDisplay;
	Object2dDisplay bugDisplay;

	public ObserverSwarm(Zone aZone) {
		super(aZone);

		displayFrequency = 1;

		EmptyProbeMap probeMap;
		probeMap = new EmptyProbeMapImpl(aZone, this.getClass());

		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"displayFrequency",
				this.getClass()));

		Globals.env.probeLibrary.setProbeMap$For(probeMap, this.getClass());
	}

	public Object buildObjects() {
		super.buildObjects();

		modelSwarm =
			(ModelSwarm) Globals.env.lispAppArchiver.getWithZone$key(
				Globals.env.globalZone,
				"modelSwarm");
		//エラーだった場合の次の処理は未実装
		//raiseEvent(InvalidOperation,"Can't find the modelSwarm parameters");

		Globals.env.createArchivedProbeDisplay(modelSwarm, "modelSwarm");
		Globals.env.createArchivedProbeDisplay(this, "observerSwarm");

		getControlPanel().setStateStopped();

		modelSwarm.buildObjects();

		colorMap = new ColormapImpl(this);
		colorMap.setColor$ToName((byte) 0, "gray");
		colorMap.setColor$ToName((byte) 1, "black");
		colorMap.setColor$ToName((byte) 2, "cyan");
		colorMap.setColor$ToName((byte) 3, "blue");
		colorMap.setColor$ToName((byte) 4, "orange");
		colorMap.setColor$ToName((byte) 5, "red");


		worldRaster = new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(4);
		worldRaster.setWidth$Height(
			modelSwarm.getWorld().getSizeX(),
			modelSwarm.getWorld().getSizeY());
		worldRaster.setWindowTitle("Food Space");
		worldRaster.pack();

		foodDisplay =
			new Value2dDisplayImpl(
				this,
				worldRaster,
				colorMap,
				modelSwarm.getFood());

		try {
			bugDisplay =
				new Object2dDisplayImpl(
					this,
					worldRaster,
					modelSwarm.getWorld(),
					new Selector(Class.forName("Bug"), "drawSelfOn", false));
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			System.exit(1);
		}

		bugDisplay.setObjectCollection(modelSwarm.getBugList());

		try {
			worldRaster.setButton$Client$Message(
				3,
				bugDisplay,
				new Selector(bugDisplay.getClass(), "makeProbeAtX$Y", true));
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			System.exit(1);
		}

		return this;
	}

	public Object buildActions() {
		super.buildActions();

		modelSwarm.buildActions();
		displayActions = new ActionGroupImpl(this);
		try {
			displayActions.createActionTo$message(
				foodDisplay,
				new Selector(foodDisplay.getClass(), "display", false));
			displayActions.createActionTo$message(
				bugDisplay,
				new Selector(bugDisplay.getClass(), "display", false));
			displayActions.createActionTo$message(
				worldRaster,
				new Selector(worldRaster.getClass(), "drawSelf", false));
			displayActions.createActionTo$message(
				getActionCache(),
				new Selector(getActionCache().getClass(), "doTkEvents", true));
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			System.exit(1);
		}

		displaySchedule = new ScheduleImpl(this, displayFrequency);
		displaySchedule.at$createAction(0, displayActions);

		return this;
	}

	public Activity activateIn(Swarm context) {
		super.activateIn(context);
		modelSwarm.activateIn(this);
		displaySchedule.activateIn(this);
		return getActivity();
	}
}
