

import java.util.ArrayList;

import swarm.Selector;
import swarm.activity.*;
import swarm.defobj.Zone;
import swarm.objectbase.*;

public class TrafficModel extends SwarmImpl
{
	ActionGroup m_actions;
	Schedule m_schedule;
	CarList m_carList;
	TrafficMap m_trafficMap;
	SignalLightMap m_signalLightMap;
	Configure m_cfg;

	public TrafficMap GetTrafficMap()
	{
		return m_trafficMap;
	}

	public SignalLightMap GetSignalLightMap()
	{
		return m_signalLightMap;
	}
	

	public CarList GetCarList()
	{
		return m_carList;
	}

	public void Step()
	{
		m_signalLightMap.Step();
		
		for( Car i : m_carList.GetListBody() ){
			i.StepSpeed();
		}
		
		ArrayList<Car> disposeCarList = new ArrayList<Car>();
		for( Car curCar : m_carList.GetListBody() ){
			boolean dispose = curCar.StepMove();
			if(dispose){
				disposeCarList.add(curCar);
			}
		}
		for( Car i : disposeCarList ){
			m_carList.remove(i);
		}
		
		// Add car
		int count = 0;
		while(m_carList.size() < (m_trafficMap.GetTotalEdgeDistance() * m_cfg.carDensityInRoad) ){
			//IPath path = new SingleEdgePath(m_carList);
			//path = new MultiEdgePath(m_carList);
			IPath path = m_trafficMap.CreatePath();
			
			Car car = new Car( m_carList, path, m_signalLightMap, m_cfg );
			car.SetLocation( 0 );
			car.SetSpeed( Car.V_MAX );
			
			count++;
			if(count > 20)
				break;
		}
	}
	
	// ----
	public TrafficModel(Zone aZone, Configure cfg)
	{
		super(aZone);
		m_cfg = cfg;
	}
	
	public Object buildObjects()
	{
		m_carList = new CarList();
		m_trafficMap = new TrafficMap( m_carList, m_cfg );
		m_signalLightMap = new SignalLightMap( m_trafficMap, m_cfg );
		return this;
	}

	public Object buildActions()
	{
		m_actions = new ActionGroupImpl(this);
		try{
			m_actions.createActionTo$message(
					this,
					new Selector(Class.forName("TrafficModel"), "Step", false) );
		} 
		catch(Exception e){
			e.printStackTrace( System.err );
			System.exit(1);
		}
		
		m_schedule = new ScheduleImpl(this, 1);		// '1' is step interval
		m_schedule.at$createAction(0, m_actions);	// actions start at time '0'

		return this;
	}
	
	public Activity activateIn(Swarm context)
	{
    	super.activateIn( context );
    	m_schedule.activateIn( this );
		return getActivity();
	}
}
