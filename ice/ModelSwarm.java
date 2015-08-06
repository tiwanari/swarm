import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl {
    public int worldXSize, worldYSize, cellNum;

    PatternSpace patternSpace;

    Array cellVector;
    ActionGroup modelActions;
    Schedule modelSchedule;

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
        Cell aCell;
        System.out.println("model swarm");
        patternSpace = new PatternSpace(this, worldXSize, worldYSize);
        System.out.println("model swarm");

        cellNum = worldXSize * worldYSize;
        cellVector = new ArrayImpl(this, cellNum);
        patternSpace.setCellVector(cellVector);

        for (int i = 0; i < cellNum; i++){
            aCell = new Cell(this, i, cellVector);
            cellVector.atOffset$put(i, aCell);
        }
        initializeCellVector();
        return this;
    }

    public void stepCellVector()
    {
        for (int i = 0; i < cellNum; i++){
            ((Cell) cellVector.atOffset(i)).next();
        }
    }

    public Object buildActions()
    {
        modelActions = new ActionGroupImpl(this);
        try{
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
        Cell aCell;
        for(int i = 0; i < cellNum; i++) {
            aCell = (Cell) cellVector.atOffset(i);
            aCell.setParams(worldXSize, worldYSize, true, 0.1, 0.2, 0.3);
            aCell.initialize();
        }
    }
}
