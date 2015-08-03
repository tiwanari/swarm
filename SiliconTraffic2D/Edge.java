

public class Edge
{
	Vertex m_begin;
	Vertex m_end;
	int m_distance;
	
	public Edge(Vertex begin, Vertex end)
	{
		m_begin = begin;
		m_end = end;
		m_distance = CalcDistance();
	}
	
	public Edge()
	{
		m_begin = new Vertex(0,0);
		m_end = new Vertex(0,0);
		m_distance = CalcDistance();
	}

	public void SetBeginVertex(Vertex begin)
	{
		m_begin = begin;
		m_distance = CalcDistance();
	}

	public void SetEndVertex(Vertex end)
	{
		m_end = end;
		m_distance = CalcDistance();
	}

	public Vertex GetBeginVertex()
	{
		return m_begin;
	}

	public Vertex GetEndVertex()
	{
		return m_end;
	}

	int CalcDistance()
	{
		int xs = m_begin.x - m_end.x; 
		int ys = m_begin.y - m_end.y; 
		double dis = Math.sqrt(xs*xs + ys*ys);
		return (int)dis;
	}

	public int GetDistance()
	{
		return m_distance;
	}
	

}
