//
// 
//


import swarm.Globals;
import swarm.Selector;
import swarm.simtoolsgui.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.gui.Colormap;
import swarm.gui.ColormapImpl;
import swarm.gui.ZoomRaster;
import swarm.gui.ZoomRasterImpl;
import swarm.objectbase.*;

public class TrafficOvserver extends GUISwarmImpl
{

	TrafficModel m_trafficModel;
	PathDisplay m_pathDisplay;
	
	ActionGroup	m_displayActions;
	Schedule	m_displaySchedule;
	
	Colormap	m_colorMap;
	ZoomRaster	m_raster;
	Configure	m_cfg;

	
	public TrafficOvserver(Zone zone)
	{
		super(zone);
		m_cfg = new Configure();
		m_trafficModel = new TrafficModel(zone, m_cfg);
	}
	
	public Object buildObjects()
	{
		super.buildObjects();

		// Probe
		Globals.env.createArchivedProbeDisplay(m_cfg, "SIlicon Traffic");
		getControlPanel().setStateStopped();

		m_trafficModel.buildObjects();

		// Color map
		m_colorMap = new ColormapImpl(this);
		ColorScheme.InitColorScheme(m_colorMap);
		
		// Raster
		m_raster = new ZoomRasterImpl(this);
		m_raster.setColormap( m_colorMap );
		m_raster.setZoomFactor(1);
		m_raster.setWidth$Height( 
			m_trafficModel.GetTrafficMap().GetMapWidth() + 10,
			m_trafficModel.GetTrafficMap().GetMapHeight() + 10 );
		m_raster.setWindowTitle("Traffic");
		m_raster.pack();
		
		// Path Display
		m_pathDisplay = new PathDisplay( m_trafficModel, m_raster, m_cfg );

		return this;
	}
	
	public Object buildActions()
	{
		m_trafficModel.buildActions();

		
		m_displayActions = new ActionGroupImpl(this);
		try{
			
			// Console
			//m_displayActions.createActionTo$message(
			//	new PathConsoleDisplay( m_trafficModel, m_trafficModel.GetPath() ),
			//	new Selector( Class.forName("PathConsoleDisplay"), "Step", false) );

			// Display window
			m_displayActions.createActionTo$message(
				m_pathDisplay,
				new Selector( Class.forName("PathDisplay"), "Step", false) );
			
			m_displayActions.createActionTo$message(
				m_raster,
				new Selector( m_raster.getClass(), "drawSelf", false) );
			
			
			// This action is for updating events of Tk panel(GUI) 
			m_displayActions.createActionTo$message(
				getActionCache(),
				new Selector( getActionCache().getClass(), "doTkEvents", false) );
		}
		catch(Exception e){
			System.out.println("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		m_displaySchedule = new ScheduleImpl(this, 1);
		m_displaySchedule.at$createAction(0, m_displayActions);
		return this;
	}

	public Activity activateIn(Swarm context)
	{
		super.activateIn(context);
    	m_trafficModel.activateIn(this);
		m_displaySchedule.activateIn(this);
		return getActivity();
	}
}
