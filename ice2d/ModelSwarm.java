import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl {
    public double cool1, cool2, cool3, grow1, grow2, grow3, fire, diffuse;
    public int worldXSize, worldYSize, cellNum;

    private PatternSpace patternSpace;

    private Array cellVector;
    private ActionGroup modelActions;
    private Schedule modelSchedule;

    public ModelSwarm(Zone aZone)
    {
        super(aZone);

        worldXSize = 100;
        worldYSize = 100;

        EmptyProbeMap probeMap;
        probeMap = new EmptyProbeMapImpl(aZone, this.getClass());
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("worldXSize", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("worldYSize", getClass()));

        Globals.env.probeLibrary.setProbeMap$For(probeMap, this.getClass());
    }
    public Object buildObjects()
    {
        patternSpace = new PatternSpace(this, worldXSize, worldYSize);

        cellNum = worldXSize * worldYSize;
        cellVector = new ArrayImpl(this, cellNum);
        patternSpace.setCellVector(cellVector);

        for (int i = 0; i < cellNum; i++) {
            Cell cell = new Cell(this, i, cellVector);
            cellVector.atOffset$put(i, cell);
        }
        initializeCellVector();
        return this;
    }

    public void stepCellVector()
    {
        for (int i = 0; i < cellNum; i++) {
            ((Cell) cellVector.atOffset(i)).next();
        }
    }

    public Object buildActions()
    {
        modelActions = new ActionGroupImpl(this);
        try {
            modelActions.createActionTo$message(this,
                    new Selector(getClass(), "stepCellVector", false));
            modelActions.createActionTo$message(patternSpace,
                    new Selector(patternSpace.getClass(), "update", false));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        modelSchedule = new ScheduleImpl(this, 1);
        modelSchedule.at$createAction(0, modelActions);
        return this;
    }

    public Activity activateIn(Swarm context)
    {
        super.activateIn(context);
        modelSchedule.activateIn(this);
        return getActivity();
    }

    public PatternSpace getPattern()
    {
        return patternSpace;
    }

    public int getWorldSizeX()
    {
        return worldXSize;
    }

    public int getWorldSizeY()
    {
        return worldYSize;
    }

    public void initializeCellVector()
    {
        for(int i = 0; i < cellNum; i++) {
            Cell cell = (Cell) cellVector.atOffset(i);
            cell.setParams(worldXSize, worldYSize, false, 0.0, 0.0, 0.3);
            cell.initialize();
        }
    }
}
