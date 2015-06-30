// SimpleBug.java
// Defines the class for our SimpleBug agents/

import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;


public class DataUnit extends SwarmObjectImpl
{
    DataSpace myDataSpace;
    int xPos;
    int yPos;
    int objID;

    double[] value;
    
    // is the object being carried by an ant now?
    boolean isCarried;

    // Constructor to create a SimpleBug object in Zone aZone and to
    // place it in the foodspace and bugspace, fSpace and bSpace, at
    // the specified X,Y location. The bug is also given a numeric id,
    // bNum.
    public DataUnit(Zone aZone, DataSpace fSpace,
		    int X, int Y, int ID, double[] v)
    {
     // Call the constructor for the bug's parent class.
     super(aZone);

     // Record the bug's foodspace, bugspace, initial position and
     // id number.
     myDataSpace = fSpace;
     xPos = X;
     yPos = Y;
     objID = ID;

     
     isCarried = false;
     value = v;

    }

    public double getValue(int i)
    {
	return(value[i]);
    }

    public int getX()
    {
	return(xPos);
    }
    public int getY()
    {
	return(yPos);
    }

    public void setCarried(boolean value)
    {
	isCarried = value;
    }
    
    public boolean getCarried()
    {
	return(isCarried);
    }

    public void setPosition(int x, int y)
    {
	xPos = x;
	yPos = y;
    }

    public double calcDistance(DataUnit oData)
    {
	int i;
	double sum;

	sum = 0.0;

	// adding the squared difference between the features 
	for(i=0;i<value.length; i++)
	    sum += (Math.pow(value[i] - oData.getValue(i),2));

	// dividing the result by the total number of features, and 
	// taking its square root
	sum = Math.pow(sum/value.length, 0.5);

	return (sum);
		   
    }

    public Object drawSelfOn(Raster r)
    {
	//FIXME: Better colloring scheme
	if (isCarried == false)
	    r.drawPointX$Y$Color(xPos,yPos,(byte) (value[2]>0.5?3:4));
	return this;
    }

}
