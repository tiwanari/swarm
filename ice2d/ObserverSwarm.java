import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.simtoolsgui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

public class ObserverSwarm extends GUISwarmImpl {
    ActionGroup displayActions;
    Schedule displaySchedule;
    ModelSwarm modelSwarm;
    Colormap colorMap;
    ZoomRaster patternRaster;
    Value2dDisplay patternDisplay;

    public ObserverSwarm(Zone zone)
    {
        super(zone);
    }

    public Object buildObjects()
    {
        super.buildObjects();
        
        modelSwarm 
            = (ModelSwarm) Globals.env.lispAppArchiver.
                getWithZone$key(Globals.env.globalZone, "modelSwarm");
        
        Globals.env.createArchivedProbeDisplay(modelSwarm, "modelSwarm");
        getControlPanel().setStateStopped();
        modelSwarm.buildObjects();

        colorMap = new ColormapImpl(this);
        colorMap.setColor$ToName((byte)0, "black");
        colorMap.setColor$ToName((byte)1, "blue");

        patternRaster = new ZoomRasterImpl(this);
        patternRaster.setColormap(colorMap);
        patternRaster.setZoomFactor(1);
        patternRaster.setWidth$Height(
                modelSwarm.getWorldSizeX(),
                modelSwarm.getWorldSizeY());
        patternRaster.setWindowTitle("snowflake 2D model");
        patternRaster.pack();

        patternDisplay = new Value2dDisplayImpl(
                this, patternRaster, colorMap, modelSwarm.getPattern());
        return this;
    }

    public Object buildActions() 
    {
        super.buildActions();
        modelSwarm.buildActions();
        displayActions = new ActionGroupImpl(this);

        try {
            displayActions.createActionTo$message(patternDisplay,
                    new Selector(patternDisplay.getClass(), "display", false));
            displayActions.createActionTo$message(patternRaster,
                    new Selector(patternRaster.getClass(), "drawSelf", false));
            displayActions.createActionTo$message(
                    getActionCache(),
                    new Selector(getActionCache().getClass(), "doTkEvents", true));
        } catch (Exception e) {
            System.out.println ("Exception: " + e.getMessage());
            System.exit(1);
        }

        displaySchedule = new ScheduleImpl(this, 1);
        displaySchedule.at$createAction(0, displayActions);

        return this;
    }

    public Activity activateIn(Swarm context) 
    {
        super.activateIn(context);
        modelSwarm.activateIn(this);
        displaySchedule.activateIn(this);
        return getActivity();
    }
}
