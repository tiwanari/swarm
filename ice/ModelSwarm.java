import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
    public double cool1,cool2,cool3,grow1,grow2,grow3,fire,diffuse;
    public int worldXSize,worldYSize,cellNum;

    PatternSpace patternSpace;

    Array cellVector;
    ActionGroup modelActions;
    Schedule modelSchedule;

    public ModelSwarm(Zone aZone){
        super(aZone);

        worldXSize = 100;
        worldYSize = 100;

        cool1 = 1.0;
        cool2 = 0.9;
        cool3 = 0.8;
        grow1 = 0.2;
        grow2 = 0.05;
        grow3 = 0.05;
        fire = 0.00002;
        diffuse = 0.8;

        EmptyProbeMap probeMap;
        probeMap = new EmptyProbeMapImpl(aZone, this.getClass());

        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("worldXSize", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("worldYSize", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("cool1", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("cool2", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("cool3", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("grow1", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("grow2", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("grow3", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("fire", getClass()));
        probeMap.addProbe(
                Globals.env.probeLibrary.getProbeForVariable$inClass("diffuse", getClass()));

        Globals.env.probeLibrary.setProbeMap$For(probeMap, this.getClass());
    }

    public Object buildObjects(){
        Cell aCell;
        patternSpace = new PatternSpace(this, worldXSize, worldYSize);

        cellNum = worldXSize*worldYSize;
        cellVector = new ArrayImpl(this, cellNum);
        patternSpace.setCellVector(cellVector);

        for (int i=0; i<cellNum; i++){
            aCell = new Cell(this, i, cellVector);
            cellVector.atOffset$put(i, aCell);
        }
        initializeCellVector();
        return this;
    }

    public void stepCellVector(){
        for (int i=0; i<cellNum; i++){
            ((Cell) cellVector.atOffset(i)).copyOldState();
        }
        for (int i=0; i<cellNum; i++){
            ((Cell) cellVector.atOffset(i)).newState();
        }
    }

    public Object buildActions(){
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

    public Activity activateIn(Swarm context){
        super.activateIn(context);
        modelSchedule.activateIn(this);
        return getActivity();
    }

    public PatternSpace getPattern(){
        return patternSpace;
    }

    public int getWorldSizeX(){
        return worldXSize;
    }

    public int getWorldSizeY(){
        return worldYSize;
    }

    public void initializeCellVector(){
        Cell aCell;
        for(int i=0;i<cellNum;i++){
            aCell = (Cell) cellVector.atOffset(i);
            aCell.setParams(worldXSize, worldYSize, cool1, cool2, cool3, grow1, grow2, grow3, fire, diffuse);
            aCell.initialize();
        }
    }
}
