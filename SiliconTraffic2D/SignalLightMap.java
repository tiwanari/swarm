import java.util.ArrayList;
import java.util.HashMap;


public class SignalLightMap 
{
	TrafficMap m_trafficMap;
	ArrayList<Vertex> m_vertices;
	ArrayList<Signal> m_signalList;
	HashMap<Edge, Signal> m_signalMap;
	Configure m_cfg;
	
	public class Signal
	{
		public Vertex m_vertex;
		public ArrayList<Edge> m_inEdges;
		public int m_greenIndex;
		public int m_greenIndexFacing;
		public int m_timer;
	}
	
	public SignalLightMap(TrafficMap trafficMap, Configure cfg)
	{
		m_cfg = cfg;
		m_trafficMap = trafficMap;
		m_vertices = m_trafficMap.GetVertices();
		
		m_signalList = new ArrayList<Signal>();
		m_signalMap = new HashMap<Edge, Signal>();
		for( Vertex i : m_vertices ){
			if( m_trafficMap.GetInEdges(i).size() >= 3){
				Signal signal = new Signal();
				
				signal.m_vertex = i;
				signal.m_inEdges = m_trafficMap.GetInEdges(i);
				signal.m_greenIndex = 0;
				signal.m_greenIndexFacing = -1;
				signal.m_timer = (int)(Math.random()*60);
				
				m_signalList.add(signal);
				for( Edge in : signal.m_inEdges ){
					m_signalMap.put( in, signal );
				}
			}
		}
	}
	
	public int CalcFacingIndex(ArrayList<Edge> edges, int index)
	{
		if(index == -1)
			return -1;
		
		ArrayList<Vertex> normalizedVertices = new ArrayList<Vertex>();
		for( Edge edge : edges ){
			Vertex begin = edge.GetBeginVertex();
			Vertex end   = edge.GetEndVertex();
			int distance = edge.GetDistance();
			normalizedVertices.add(  
				new Vertex(
					(begin.x - end.x) * 256 / distance,
					(begin.y - end.y) * 256 / distance ) );
		}
		
		
		Vertex vBegin = normalizedVertices.get(index);
		int maxDistanceP2 = 0;
		Vertex maxDistanceVertex = null;
		for( Vertex v : normalizedVertices ){
			int distanceP2 = (v.x - vBegin.x)*(v.x - vBegin.x) + (v.y - vBegin.y)*(v.y - vBegin.y);
			if(distanceP2 > maxDistanceP2){
				maxDistanceP2 = distanceP2;
				maxDistanceVertex = v;
			}
		}
		
		if(maxDistanceVertex != null)
			return normalizedVertices.indexOf( maxDistanceVertex );
		else
			return -1;
	}
	
	public void Step()
	{
		if(!m_cfg.signalLightEnabled)
			return;
		
		for( Signal i : m_signalList ){
			i.m_timer++;
			if(i.m_timer < m_cfg.signalLightChangeInterval)
				continue;
			i.m_timer = 0;

			
			i.m_greenIndex++;
			if(i.m_greenIndex >= i.m_inEdges.size())
				i.m_greenIndex = 0;

			int indexFacing = CalcFacingIndex(i.m_inEdges, i.m_greenIndex);
			if(i.m_greenIndex == CalcFacingIndex(i.m_inEdges, indexFacing))
				i.m_greenIndexFacing = indexFacing;
			else
				i.m_greenIndexFacing = -1;
		}
		
	}
	
	public boolean IsEdgeSgnalGreen(Edge edge)
	{
		if(!m_cfg.signalLightEnabled)
			return true;

		Signal signal = m_signalMap.get(edge);
		if(signal == null)
			return true;
		
		ArrayList<Edge> inEdges = signal.m_inEdges;
		int greenIndex = signal.m_greenIndex;
		int greenIndexFacing = signal.m_greenIndexFacing;
		
		if( inEdges.get( greenIndex ) == edge )
			return true;
		else if( greenIndexFacing != -1 && inEdges.get( greenIndexFacing ) == edge )
			return true;
		else
			return false;
	}
	
	public ArrayList<Signal> GetSignalList()
	{
		return m_signalList;
	}
}
