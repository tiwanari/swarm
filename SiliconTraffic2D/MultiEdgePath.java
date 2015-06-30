import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class MultiEdgePath implements IPath
{
	CarList m_carList;
	ArrayList<Edge> m_edges;
	int m_pathDistance;
	Configure m_cfg;
	
	public MultiEdgePath(CarList carList, ArrayList<Edge> edges, Configure cfg)
	{
		m_cfg = cfg;
		m_carList = carList;
		m_edges = edges;
		CalcPathDistance();
	}


	// Retrieve car in front
	public Car GetFrontCarFromPath( Car car )
	{
		ArrayList<Edge> viewEdges = new ArrayList<Edge>();
		viewEdges.add( car.GetEdge() );
		
		int carEdgeIndex = GetEdgeIndex(car.GetEdge()) + 1;
		int viewDistance = 0;
		while(carEdgeIndex < m_edges.size() && viewDistance < Car.V_MAX + 2){
			Edge edge = m_edges.get(carEdgeIndex);
			viewEdges.add( edge );
			viewDistance += edge.GetDistance();
			carEdgeIndex++;
		}
		
		TreeSet<Car> totalSet = m_carList.GetCarSetFromPath(this, viewEdges);
		SortedSet<Car> tail = totalSet.tailSet(car);
		Iterator<Car> i = tail.iterator();
		
		if(i.hasNext()){
			i.next();
			if(i.hasNext())
				return i.next();
			else
				return totalSet.last();
		}
		else{
			return null;
		}
	}

	// ----
	// IPath
	
	public int GetDistanceToFrontCar(Car car)
	{
		Car frontCar = GetFrontCarFromPath( car );
		if(frontCar == car)
			frontCar = null;
		
		int distanceFromFrontCar = 0;
		if(frontCar != null){

			int frontCarEdgeIndex = GetEdgeIndex(frontCar.GetEdge());
			int carEdgeIndex = GetEdgeIndex(car.GetEdge());
			while(frontCarEdgeIndex > carEdgeIndex && distanceFromFrontCar < Car.V_MAX + 2 ){
				distanceFromFrontCar +=	m_edges.get(carEdgeIndex).GetDistance() + 1;
				carEdgeIndex++;
			}
			distanceFromFrontCar += 
				frontCar.GetEdgeRelativeLocation() - car.GetEdgeRelativeLocation();
			
		}
		else{
			// There is no car in front
			distanceFromFrontCar = Car.V_MAX + 2;
		}

		if(distanceFromFrontCar < 0)
			distanceFromFrontCar += GetTotalPathDistance();
		
		return distanceFromFrontCar;
	}

	void CalcPathDistance()
	{
		int distance = 0;
		for( Edge i : m_edges ){
			distance += i.GetDistance();
		}
		m_pathDistance = distance;
	}
	
	public int GetTotalPathDistance()
	{
		return m_pathDistance; 
	}

	public EdgeLocation GetEdgeLocation(int location)
	{
		int distance = 0;
		for( Edge i : m_edges ){
			if(distance <= location && location < distance + i.GetDistance()){
				return new EdgeLocation( i, location - distance );
			}
			distance += i.GetDistance();
		}
		return new EdgeLocation( null, 0 );
	}
	

	public boolean IsEdgeContain(Edge edge)
	{
		return m_edges.contains(edge);
	}

	public ArrayList<Edge> GetEdges()
	{
		return m_edges;
	}

	public int GetEdgeIndex(Edge edge)
	{
		return m_edges.indexOf(edge);
	}
}
