
public class Configure
{
	public double carRandomSlowDownRate;
	public double carDensityInRoad;
	
	public boolean 	signalLightEnabled;
	public int	 	signalLightChangeInterval;
	
	public double pathTrafficWeightFactor;
	
	public int mapGridSize;
	public int mapHorizontalGridNum;
	public int mapVerticalGridNum;
	public double mapGridRandomRate;
	public double mapHolizontalEdgeZapRate;
	public double mapVerticalEdgeZapRate;
	
	
	public Configure()
	{
		carDensityInRoad = 0.05;
		carRandomSlowDownRate = 0.1;

		signalLightEnabled = true;
		signalLightChangeInterval = 30;
		
		pathTrafficWeightFactor = 5;
		
		mapGridSize = 120;
		mapGridRandomRate = 1.0;
		mapHorizontalGridNum = 10;
		mapVerticalGridNum = 7;
		mapHolizontalEdgeZapRate = 0.15;
		mapVerticalEdgeZapRate = 0.2;
	}
}
