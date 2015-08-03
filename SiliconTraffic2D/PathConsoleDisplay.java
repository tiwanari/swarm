//
// Display traffic state of single edge to console
//

import java.util.TreeSet;

import swarm.objectbase.SwarmObjectImpl;


public class PathConsoleDisplay extends SwarmObjectImpl 
{
	TrafficModel m_model;
	IPath m_path;
	
	public PathConsoleDisplay(TrafficModel model, IPath path)
	{
		m_model = model;
		m_path = path;
	}

	void View()
	{
		TreeSet<Car> set = m_model.GetCarList().GetCarSetFromPath(m_path, m_path.GetEdges());

		String line = "";
		int curLocation = 0;
		for( Car i : set ){
			for(; curLocation < i.GetLocation(); curLocation++){
				line += ".";
			}
			line += i.GetSpeed();
			curLocation++;
		}
		
		for(; curLocation < m_path.GetTotalPathDistance(); curLocation++){
			line += ".";
		}

		System.out.println( line );

	}
	
	public void Step()
	{
		View();
	}
	
}
