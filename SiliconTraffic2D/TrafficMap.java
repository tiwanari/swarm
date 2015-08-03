import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;


public class TrafficMap 
{
	
	ArrayList<Vertex> m_vertices;
	ArrayList<Vertex> m_endPoints;
	ArrayList<Edge> m_edges;
	CarList m_carList;
	int m_mapWidth;
	int m_mapHeight;
	Configure m_cfg;
	
	public int GetMapWidth()
	{
		return m_mapWidth;
	}
	
	public int GetMapHeight()
	{
		return m_mapHeight;
	}
	
	public int GetTotalEdgeDistance()
	{
		int distance = 0;
		for( Edge i : m_edges ){
			distance += i.GetDistance();
		}
		return distance;
	}
	
	public ArrayList<Edge> GetEdges()
	{
		return m_edges;
	}
	
	public ArrayList<Vertex> GetVertices()
	{
		return m_vertices;
	}

	public TrafficMap( CarList carList, Configure cfg )
	{
		m_carList = carList;
		m_cfg = cfg;
		BuildMap();
	}
	
	public void BuildMap()
	{

		
		int w = m_cfg.mapHorizontalGridNum;
		int h = m_cfg.mapVerticalGridNum;
		int gridSize = m_cfg.mapGridSize;
		
		// Generate vertices
		Vector<Vertex> vertices = new Vector<Vertex>();
		double gridRandomRate = 3.0;
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				if(Math.random() < 0.1){
					gridRandomRate = 1.0 + Math.random()*2;
				}
				
				double factor = gridSize / gridRandomRate * m_cfg.mapGridRandomRate;
				int randX = (int)((Math.random() - 0.5) * factor);
				int randY = (int)((Math.random() - 0.5) * factor);
				
				Vertex v = new Vertex( 
					x*gridSize + randX,
					y*gridSize + randY );
				vertices.add(v);
			}
		}

		// Correct top-left of vertices to (0,0)
		int minX = (w - 1) * gridSize;
		int maxX = 0;
		int minY = (h - 1) * gridSize;
		int maxY = 0;
		for( Vertex v : vertices ){
			if(minX > v.x) minX = v.x;
			if(maxX < v.x) maxX = v.x;
			if(minY > v.y) minY = v.y;
			if(maxY < v.y) maxY = v.y;
		}
		m_mapWidth = maxX - minX;
		m_mapHeight = maxY - minY;
		for( Vertex v : vertices ){
			v.x -= minX;
			v.y -= minY;
		}
		
		m_edges = new ArrayList<Edge>();
		// Horizontal edge
		for(int y = 0; y < h; y++){
			int hCutNum = 0;
			for(int x = 0; x < w - 1; x++){
				Vertex v1 = vertices.elementAt(x + y * w);
				Vertex v2 = vertices.elementAt(x + y * w + 1);
				
				if(y == 0 || y == h - 1){
					continue;
				}
				
				if(/*hCutNum < h/2 && */Math.random() < m_cfg.mapHolizontalEdgeZapRate){
					hCutNum++;
					continue;
				}
				
				m_edges.add( new Edge( v1, v2 ) );
				m_edges.add( new Edge( v2, v1 ) );
			}
		}

		// Vertical edge
		for(int y = 0; y < h - 1; y++){
			int vCutNum = 0;
			for(int x = 0; x < w; x++){
				Vertex v1 = vertices.elementAt(x + y * w);
				Vertex v2 = vertices.elementAt(x + (y + 1) * w);
				
				if(x == 0 || x == (w - 1))
					continue;
				
				if(/*vCutNum < w/2 && */Math.random() < m_cfg.mapVerticalEdgeZapRate){
					vCutNum++;
					continue;
				}

				m_edges.add( new Edge( v1, v2 ) );
				m_edges.add( new Edge( v2, v1 ) );
			}
		}
		
		// End point
		m_vertices = new ArrayList<Vertex>();
		m_endPoints = new ArrayList<Vertex>();
		for( Vertex v : vertices ){
			if(GetOutEdges(v).size() == 1){
				m_endPoints.add(v);
			}
			
			if(GetOutEdges(v).size() > 0){
				m_vertices.add(v);
			}
		}
	}
	
	public IPath CreatePath()
	{
		
		int startEndPoint = (int)(m_endPoints.size() * Math.random());  
		int goalEndPoint  = (int)(m_endPoints.size() * Math.random());
		if(startEndPoint == goalEndPoint)
			return CreatePath();	// Retry
		
		Vertex start = m_endPoints.get(startEndPoint);
		Vertex goal  = m_endPoints.get(goalEndPoint);
		
		// Calculate density of each edge
		HashMap<Edge, Double> carDensity = new HashMap<Edge, Double>();
		for( Edge i : m_edges ){
			carDensity.put( i, 0.0 );
		}
		for( Car i : m_carList.GetListBody() ){
			if(i.GetSpeed() < Car.V_MAX / 2){
				carDensity.put(i.GetEdge(), 
					carDensity.get(i.GetEdge()) + 1.0 / i.GetEdge().GetDistance());	
			}
		}
		
		// Calculate minimum path by Dijkstra's algorithm
		class Node
		{
			public Vertex vertex;
			public Vertex from;
			public int distance;
			public boolean fixed;
			public Node()
			{
				distance = -1;	// -1 is means 'undefined'
				fixed = false;
			}
		};
		
		// Initialize node map
		HashMap<Vertex, Node> nodeMap = new HashMap<Vertex, Node>();
		for( Vertex v : m_vertices ){
			Node node = new Node();
			node.vertex = v;
			nodeMap.put(v, node);
		}
	
		// start vertex
		nodeMap.get(start).distance = 0;

		while(true){
			// Determine Non-fixed and minimum distance node 
			Node minNode = null;
			for( Vertex v : m_vertices ){
				Node curNode = nodeMap.get(v); 
				if(curNode.fixed || curNode.distance == -1)
					continue;
				
				if(minNode == null || minNode.distance > curNode.distance)
					minNode = curNode;
			}
			if(minNode == null)
				break;
			
			// Node is fixed
			minNode.fixed = true;
			
			// Update neighboring nodes
			for( Edge i : GetOutEdges(minNode.vertex) ){
				Node nextNode = nodeMap.get( i.GetEndVertex() );
				double weight = 1.0 + carDensity.get(i)*m_cfg.pathTrafficWeightFactor;  
				int addDistance = (int)( i.GetDistance() * weight );
				int distance = minNode.distance + addDistance;
				if(nextNode.distance == -1 || distance < nextNode.distance){
					nextNode.distance = distance;
					nextNode.from = minNode.vertex;
				}
			}
		}
		
		// Traverse minimum path
		// (this path is reversed
		LinkedList<Edge> pathEdgeList = new LinkedList<Edge>();
		Vertex cur = goal;
		while(cur != start){
			Node curNode = nodeMap.get(cur);
			Edge edge = GetEdge( curNode.from, cur );
			pathEdgeList.addFirst(edge);
			cur = curNode.from;
			if(cur == null)
				return CreatePath(); //outer island,retry
		}

		ArrayList<Edge> pathEdge = new ArrayList<Edge>();
		for( Edge i : pathEdgeList ){
			pathEdge.add(i);
		}

		return new MultiEdgePath( m_carList, pathEdge, m_cfg );
	}
	
	ArrayList<Edge> GetOutEdges(Vertex from)
	{
		ArrayList<Edge> outEdges = new ArrayList<Edge>();  
		for( Edge i :m_edges ){
			if(i.GetBeginVertex() == from )
				outEdges.add(i);
		}
		return outEdges;
	}

	ArrayList<Edge> GetInEdges(Vertex to)
	{
		ArrayList<Edge> inEdges = new ArrayList<Edge>();  
		for( Edge i :m_edges ){
			if(i.GetEndVertex() == to )
				inEdges.add(i);
		}
		return inEdges;
	}

	Edge GetEdge(Vertex begin, Vertex end)
	{
		for( Edge i :m_edges ){
			if(i.GetBeginVertex() == begin && i.GetEndVertex() == end)
				return i;
		}
		return null;
	}
}
