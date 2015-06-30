import swarm.gui.ZoomRaster;
import swarm.objectbase.SwarmObjectImpl;


public class PathDisplay extends SwarmObjectImpl
{
	TrafficModel m_model;
	TrafficMap m_trafficMap;
	ZoomRaster m_raster;
	Configure m_cfg;
	
	public PathDisplay(
		TrafficModel model,
		ZoomRaster raster,
		Configure cfg )
	{
		m_model = model;
		m_trafficMap = model.GetTrafficMap();
		m_raster = raster;
		m_cfg = cfg;
	}
	
	void View()
	{
		
		Vertex topLeft = new Vertex( 
			(m_raster.getWidth() - m_trafficMap.GetMapWidth()) / 2, 
			(m_raster.getHeight() - m_trafficMap.GetMapHeight()) / 2 );
		
		// Draw road
		for( Edge edge: m_trafficMap.GetEdges() ){ 
			m_raster.lineX0$Y0$X1$Y1$Width$Color(
					edge.GetBeginVertex().x + topLeft.x,
					edge.GetBeginVertex().y + topLeft.y,
					edge.GetEndVertex().x + topLeft.x,
					edge.GetEndVertex().y + topLeft.y,
					12,	// line width
					ColorScheme.ROAD );
		}

		for( Edge edge: m_trafficMap.GetEdges() ){ 
			m_raster.lineX0$Y0$X1$Y1$Width$Color(
					edge.GetBeginVertex().x + topLeft.x,
					edge.GetBeginVertex().y + topLeft.y,
					edge.GetEndVertex().x + topLeft.x,
					edge.GetEndVertex().y + topLeft.y,
					1,	// line width
					ColorScheme.CENTER_LINE );
		}
	

		for( Car car : m_model.GetCarList().GetListBody() ){
			Edge edge = car.GetEdge();
			Vertex begin = edge.GetBeginVertex();
			Vertex end   = edge.GetEndVertex();
			int relLocation = car.GetEdgeRelativeLocation();
			int x = begin.x + topLeft.x;
			int y = begin.y + topLeft.y;
			
			if(relLocation == 0)relLocation++;
			int sx = (end.x - begin.x) * relLocation / edge.GetDistance(); 
			int sy = (end.y - begin.y) * relLocation / edge.GetDistance();
			
			int dx = 3*(end.y - begin.y) / (edge.GetDistance() - 1);
			int dy = 3*-(end.x - begin.x) / (edge.GetDistance() - 1);
			
			m_raster.fillRectangleX0$Y0$X1$Y1$Color(
				x + sx + dx - 2, 
				y + sy + dy - 2,
				x + sx + dx + 2,
				y + sy + dy + 2,
				ColorScheme.GetColorFromSpeed( car.GetSpeed() ));
		}
		
		for( SignalLightMap.Signal signal : m_model.GetSignalLightMap().GetSignalList() ){
			for( Edge edge : signal.m_inEdges){
				Vertex begin = edge.GetBeginVertex();
				Vertex end   = edge.GetEndVertex();
				
				int x = end.x + topLeft.x;
				int y = end.y + topLeft.y;
				
				double sx = 12 * (end.x - begin.x) / edge.GetDistance(); 
				double sy = 12 * (end.y - begin.y) / edge.GetDistance();
				
				double c = 1;
				double s = 0;
				int dx = (int)(sx*c + sy*-s);
				int dy = (int)(sx*s + sy*c);
				
				boolean signalGreen = m_model.GetSignalLightMap().IsEdgeSgnalGreen( edge );
				byte col = signalGreen ? ColorScheme.SIGNAL_GREEN : ColorScheme.SIGNAL_RED;
				
				m_raster.fillRectangleX0$Y0$X1$Y1$Color(
					x + dx - 3,
					y + dy - 3,
					x + dx + 3,
					y + dy + 3,
					ColorScheme.SIGNAL_FRAME );
				m_raster.fillRectangleX0$Y0$X1$Y1$Color(
					x + dx - 2, 
					y + dy - 2,
					x + dx + 2,
					y + dy + 2,
					col );
			}
		}
	}
	
	public void Step()
	{
		View();
	}
	
}
