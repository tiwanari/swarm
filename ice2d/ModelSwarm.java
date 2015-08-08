import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;
import java.util.*;
import java.util.concurrent.*;

public class ModelSwarm extends SwarmImpl {
    public double rho;
    public int worldXSize, worldYSize, cellNum;

    private PatternSpace patternSpace;

    private Array cellVector;
    private ActionGroup modelActions;
    private Schedule modelSchedule;

    public ModelSwarm(Zone zone)
    {
        super(zone);

        worldXSize = 500;
        worldYSize = 500;

        EmptyProbeMap probeMap;
        probeMap = new EmptyProbeMapImpl(zone, this.getClass());
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
        
        // Cell initialization
        rho = 0.8;
        Cell.kappa = 0.05;
        Cell.mu = 0.015;
        Cell.gamma = 0.0001;
        Cell.alpha = 0.006;
        Cell.beta = 2.9;
        Cell.theta = 0.004;
        Cell.sigma = 0.0000;
        initializeCellVector();
        
        return this;
    }

    static int iter = 0;
    static int div = 10;
    public void stepCellVector()
    {
        ++iter;
        if (iter % div == 0) {
            System.out.println("#cell = " + cellNum + ", #iter = " + iter);
            div *= 10;
        }
        
        int numCores = Runtime.getRuntime().availableProcessors();
        /* int numCores = 4; */

        for (int s = 0; s < Cell.Step.values().length; s++) {
            {
                for (int i = 0; i < cellNum; i++) {
                    final Cell cell 
                        = ((Cell) cellVector.atOffset(i));
                    cell.copyOldNeigborState();
                }
            }

            {
                for (int i = 0; i < cellNum; i++) {
                    final Cell cell 
                        = ((Cell) cellVector.atOffset(i));
                    cell.next();
                }
            }
        }
        /* for (int s = 0; s < Cell.Step.values().length; s++) { */
        /*     { */
        /*         ExecutorService threadPool  */
        /*             = Executors.newFixedThreadPool(numCores); */
        /*         java.util.Collection<Callable<Void>> processes  */
        /*             = new java.util.LinkedList<Callable<Void>>(); */
        /*  */
        /*         for (int i = 0; i < cellNum; i++) { */
        /*             final Cell cell  */
        /*                 = ((Cell) cellVector.atOffset(i)); */
        /*             processes.add(new Callable<Void>() { */
        /*                 @Override */
        /*                 public Void call() { */
        /*                     cell.copyOldNeigborState(); */
        /*                     return null; */
        /*                 } */
        /*             }); */
        /*         } */
        /*  */
        /*         try { */
        /*             threadPool.invokeAll(processes); */
        /*         } catch (InterruptedException e) { */
        /*             throw new RuntimeException(e); */
        /*         } finally { */
        /*             threadPool.shutdown(); */
        /*         } */
        /*     } */
        /*  */
        /*     { */
        /*         ExecutorService threadPool  */
        /*             = Executors.newFixedThreadPool(numCores); */
        /*         java.util.Collection<Callable<Void>> processes  */
        /*             = new java.util.LinkedList<Callable<Void>>(); */
        /*  */
        /*         for (int i = 0; i < cellNum; i++) { */
        /*             final Cell cell  */
        /*                 = ((Cell) cellVector.atOffset(i)); */
        /*             processes.add(new Callable<Void>() { */
        /*                 @Override */
        /*                 public Void call() { */
        /*                     cell.next(); */
        /*                     return null; */
        /*                 } */
        /*             }); */
        /*         } */
        /*  */
        /*         try { */
        /*             threadPool.invokeAll(processes); */
        /*         } catch (InterruptedException e) { */
        /*             throw new RuntimeException(e); */
        /*         } finally { */
        /*             threadPool.shutdown(); */
        /*         } */
        /*     } */
        /* } */
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
        for (int i = 0; i < cellNum; i++) {
            Cell cell = (Cell) cellVector.atOffset(i);
            cell.setParams(worldXSize, worldYSize, false, 0.0, 0.0, rho);
            cell.initialize();
        }
        // center cell
        Cell center = (Cell) cellVector.atOffset(cellNum / 2 + worldXSize / 2);
        center.setParams(worldXSize, worldYSize, true, 0.0, 1.0, 0.0);
        center.initialize();
    }
}
