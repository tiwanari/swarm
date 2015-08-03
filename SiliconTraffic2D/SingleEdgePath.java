import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class SingleEdgePath implements IPath
{
	Edge m_edge;
	CarList m_carList;
	
	public SingleEdgePath(CarList carList)
	{
		m_carList = carList;
		
		
		Edge edge = new Edge();
		Vertex begin = new Vertex(0,0);
		Vertex end = new Vertex(200,0);
		edge.SetBeginVertex( begin );
		edge.SetEndVertex( end );
		m_edge = edge;
	}


	// Retrieve car in front
	public Car GetFrontCarFromPath( Car car )
	{
		TreeSet<Car> totalSet = m_carList.GetCarSetFromPath(this,GetEdges());
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
		int distanceFromFrontCar;
		if(frontCar == car)
			frontCar = null;
		
		if(frontCar != null){
			distanceFromFrontCar = frontCar.GetLocation() - car.GetLocation();
			if(distanceFromFrontCar < 0)
				distanceFromFrontCar += GetTotalPathDistance();
		}
		else{
			// There is no car in front
			distanceFromFrontCar = Car.V_MAX + 1;
		}
		
		return distanceFromFrontCar;
	}

	public int GetTotalPathDistance()
	{
		return m_edge.GetDistance();
	}

	public EdgeLocation GetEdgeLocation(int location)
	{
		return new EdgeLocation( m_edge, location );
	}

	public boolean IsEdgeContain(Edge edge)
	{
		return m_edge == edge;
	}

	public ArrayList<Edge> GetEdges()
	{
		ArrayList<Edge> list = new ArrayList<Edge>();
		list.add(m_edge);
		return list;
	}
	
	public int GetEdgeIndex(Edge edge)
	{
		return 0;
	}
}
