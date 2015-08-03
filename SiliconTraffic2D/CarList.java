import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

//
// List of all cars
//

public class CarList 
{
	ArrayList<Car> m_listBody;
	HashMap<Edge, ArrayList<Car>> m_edgeCarMap;
	
	private static final long serialVersionUID = 1L;
	
	public CarList()
	{
		m_listBody = new ArrayList<Car>();
		m_edgeCarMap = new HashMap<Edge, ArrayList<Car>>(); 
	}
	
	// Retrieve car set which is ordered by location in specified path.  
	public TreeSet<Car> GetCarSetFromPath( IPath path, ArrayList<Edge> edges )
	{
		TreeSet<Car> set = new TreeSet<Car>(new Car.CarComparator(path));
		
		for( Edge i : edges ){
			ArrayList<Car> list = m_edgeCarMap.get(i);
			if(list == null)
				continue;
			
			for( Car car : list ){
				set.add(car);
			}
		}
								
		return set;
	}

	public ArrayList<Car> GetListBody()
	{
		return m_listBody;
	}
	
	public void UpdateCarEdge(Car car, Edge oldEdge, Edge newEdge)
	{
		if(oldEdge != newEdge){
			if(oldEdge != null){
				ArrayList<Car> oldEdgeCarList = m_edgeCarMap.get(oldEdge);
				if(oldEdgeCarList != null){
					oldEdgeCarList.remove(car);
				}
			}
			
			if(newEdge != null){
				ArrayList<Car> newEdgeCarList = m_edgeCarMap.get(newEdge);
				if(newEdgeCarList == null){
					newEdgeCarList = new ArrayList<Car>();
					m_edgeCarMap.put(newEdge, newEdgeCarList);
				}
				newEdgeCarList.add(car);
			}
		}
	}
	
	public int size()
	{
		return m_listBody.size();
	}
	
	public void add(Car car)
	{
		UpdateCarEdge( car, null, car.GetEdge() );
		m_listBody.add(car);
	}

	public boolean remove(Car car)
	{
		UpdateCarEdge( car, car.GetEdge(), null );
		boolean ret = m_listBody.remove(car);
		return ret;
	}
	
}
