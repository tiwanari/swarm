// FoodSpace.java
// Defines the FoodSpace class as a subclass of Discrete2dImpl.

import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.Selector;
import swarm.space.*;
import swarm.collections.*;

public class DataSpace extends Grid2dImpl
{

    ListImpl dataList;
    int listsize;
    int offset;
    
    public DataSpace(Zone aZone, int xSize, int ySize)
    {
	// Call the constructor for the parent class and then fill the
	// lattice with zeros.
	super(aZone, xSize, ySize);
	fastFillWithObject(null);
    }

    public void setList(ListImpl dlist)
    {
	dataList = dlist;
	listsize = dataList.getCount();
	offset = 0;
	
	return;
    }
    
    // Calculate the disparity of a region related to an object
    // TODO: Add radius per ant.    
    public double[] getDisparity(DataUnit data, int posx, int posy, int sight)
    {
	int i,j;
	DataUnit tmpData;

	

	double sum;
	int neigh;

	sum = 0.0;
	neigh = 0;

	for (i = sight*-1; i <= sight; i++)
	    for (j = sight*-1; j <= sight; j++)
		{
		    tmpData = (DataUnit) getObjectAtX$Y((posx+i+getSizeX())%
							getSizeX(), 
							(posy+j+getSizeY())%
							getSizeY());
		    if ((tmpData != null) && !(i == 0 && j == 0))
			{
			    neigh ++;
			    sum += data.calcDistance(tmpData);
			}
   
		}
	
	if (neigh == 0)
	    return(new double[] {1.0, 0});
	else
	    return(new double[] {sum/neigh, neigh});
	
    }

    public DataUnit getPickCandidate()
    {
	do {
	    offset = (offset + 1)%listsize;
	} while (((DataUnit)dataList.atOffset(offset)).getCarried()==true);
	return((DataUnit) dataList.atOffset(offset));
    }

}


















