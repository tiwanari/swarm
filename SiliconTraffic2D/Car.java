import java.util.Comparator;

import swarm.objectbase.*;

public class Car 
	extends SwarmObjectImpl 
{
	int m_speed;
	int m_location;		// Location from path top
	Edge m_edge;
	int m_edgeLocation;
	IPath m_path;	
	CarList m_list;
	SignalLightMap m_signal;
	static final int V_MAX = 6;
	int m_vMax;
	Configure m_cfg;
	
	public Car(CarList list, IPath path, SignalLightMap signal, Configure cfg)
	{
		m_cfg = cfg;
		m_list = list;
		m_signal = signal;
		m_speed = 0;
		m_location = 0;
		m_edgeLocation = 0;
		m_path = path;

		m_vMax = (int)((V_MAX+1) * (0.8 + 0.2*Math.random()));
		if(m_vMax > V_MAX)m_vMax = V_MAX;
		
		list.add(this);
	}
	
	int AdjustSpeed(int speed)
	{
		if(speed > m_vMax)
			return m_vMax;
		else if(speed < 0) 
			return 0;
		else
			return speed;
	}
	
	// Speed
	public void StepSpeed()
	{
		
		int distance = m_path.GetDistanceToFrontCar( this );
		if( !m_signal.IsEdgeSgnalGreen( GetEdge() ) ){
			int signalDistance =
				GetEdge().GetDistance() - m_edgeLocation;
			if(signalDistance < distance)
				distance = signalDistance;
		}
		
		// Acceleration or Slow down 
		if( distance > m_speed + 1){
			m_speed++;
		}
		else if(distance <= m_speed){
			m_speed = distance - 1;
		}
		m_speed = AdjustSpeed(m_speed);
		
		// Random Slow down
		if( Math.random() < m_cfg.carRandomSlowDownRate ){
			m_speed--;
			m_speed = AdjustSpeed(m_speed);
		}
		
		// Slow down at curve
		/*if(GetEdge().GetDistance() - m_edgeLocation < V_MAX*2 && m_speed > V_MAX/2){
			m_speed--;
			m_speed = AdjustSpeed(m_speed);
		}*/
	}
	
	// Move
	public boolean StepMove()
	{
		m_location = m_location + m_speed;
	
		Edge oldEdge = m_edge;
		EdgeLocation edgeLocation = m_path.GetEdgeLocation( m_location ); 
		m_edge = edgeLocation.m_edge;
		m_edgeLocation = edgeLocation.m_edgeRelativeLocation;

		m_list.UpdateCarEdge(this, oldEdge, m_edge);
		
		return (m_location >= m_path.GetTotalPathDistance());
	}
	
	
	// Comparator for TreeSet
	// Car is ordered by its location in current path
	public static class CarComparator implements Comparator<Car>
	{
		IPath m_path;
		public CarComparator(IPath path)
		{
			m_path = path;
		}
		
		public int compare(Car a, Car b)
		{
			int aEdgeIndex = m_path.GetEdgeIndex(a.GetEdge());
			int bEdgeIndex = m_path.GetEdgeIndex(b.GetEdge());
			
			if(aEdgeIndex != bEdgeIndex){
				return aEdgeIndex - bEdgeIndex;
			}
			return a.m_edgeLocation - b.m_edgeLocation;
		}

	}

	
	// ----
	// Accessor
	
	public int GetSpeed()
	{
		return m_speed;
	};
	
	public void SetSpeed(int speed)
	{
		m_speed = speed;
	};

	public int GetLocation()
	{
		return m_location;
	}

	public void SetLocation(int location)
	{
		m_location = location;
	}
	
	public IPath GetPath()
	{
		return m_path;
	}
	
	public Edge GetEdge()
	{
		if(m_edge == null){
			m_edge = m_path.GetEdgeLocation(m_location).m_edge;
		}
		return m_edge;
	}
	
	public int GetEdgeRelativeLocation()
	{
		return 	m_edgeLocation;
	}

}
