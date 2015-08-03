import java.util.ArrayList;

//
// The interface of directed path 
//

public interface IPath
{
	int GetTotalPathDistance();
	int GetDistanceToFrontCar(Car car);
	EdgeLocation GetEdgeLocation(int location);
	boolean IsEdgeContain(Edge edge);
	ArrayList<Edge> GetEdges();
	int GetEdgeIndex(Edge edge);
}
