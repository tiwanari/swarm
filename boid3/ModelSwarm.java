import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl {
	public int worldXSize, worldYSize;
	public double bugDensity;
	public double defaultSpeed;
	public double maxSpeed;
	public double minSpeed;
	public double accel;
	public double optDistance;
	public int searchSpace;
	public double gravityWeight;
	public double nearWeight;

	FoodSpace food;
	Grid2d world;

	List bugList;
	ActionGroup modelActions;
	Schedule modelSchedule;

	public ModelSwarm(Zone aZone) {
		super(aZone);

		worldXSize = 160;
		worldYSize = 160;
		bugDensity = 0.02;
		defaultSpeed = 3.0;
		maxSpeed = 4.0;
		minSpeed = 2.0;
		accel = 1.1;
		optDistance = 5.0;
		searchSpace = 10;
		gravityWeight = 0.05;
		nearWeight = 0.15;

		EmptyProbeMap probeMap;
		probeMap = new EmptyProbeMapImpl(aZone, this.getClass());

		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"worldXSize",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"worldYSize",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"bugDensity",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"defaultSpeed",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"maxSpeed",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"minSpeed",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"accel",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"optDistance",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"searchSpace",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"gravityWeight",
				this.getClass()));
		probeMap.addProbe(
			Globals.env.probeLibrary.getProbeForVariable$inClass(
				"nearWeight",
				this.getClass()));
		Globals.env.probeLibrary.setProbeMap$For(probeMap, this.getClass());
	}

	public Object buildObjects() {
		Bug aBug;
		int x, y;

		food = new FoodSpace(this, worldXSize, worldYSize);

		world = new Grid2dImpl(this, worldXSize, worldYSize);
		world.fillWithObject(null);

		bugList = new ListImpl(this);

		for (y = 0; y < worldYSize; y++) {
			for (x = 0; x < worldXSize; x++) {
				if (Globals
					.env
					.uniformDblRand
					.getDoubleWithMin$withMax(0.0, 1.0)
					< bugDensity) {
						if (Globals
							.env
							.uniformDblRand
							.getDoubleWithMin$withMax(0.0, 1.0)
							< 0.5) {
								aBug = (Bug)new BugA(this);
							}else{
								aBug = (Bug)new BugB(this);
							}
						

					aBug.setWorld$Food(world, food);
					aBug.setX$Y(x, y);
					aBug.setSpeed((float) defaultSpeed);
					aBug.setDirection(
						(float)Globals
								.env
								.uniformDblRand
								.getDoubleWithMin$withMax(0.0, 360.0));
					bugList.addLast(aBug);
				}
			}
		}
		return this;
	}

	public Object buildActions() {
		modelActions = new ActionGroupImpl(this);
		try {
			modelActions.createActionForEach$message(
				bugList,
				new Selector(Class.forName("Bug"), "step", false));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}

		modelSchedule = new ScheduleImpl(this, 1);
		modelSchedule.at$createAction(0, modelActions);
		return this;
	}

	public Activity activateIn(Swarm context) {
		super.activateIn(context);
		modelSchedule.activateIn(this);
		return getActivity();
	}

	public Grid2d getWorld() {
		return world;
	}

	public FoodSpace getFood() {
		return food;
	}

	public List getBugList() {
		return bugList;
	}
}
