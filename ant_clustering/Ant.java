// Ant.java
// Defines the class for ant agents
import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import java.util.Random;

public class Ant extends SwarmObjectImpl
{

    DataSpace myDataSpace;
    Grid2dImpl mySpace;
    int xPos;
    int yPos;
    int antID;

    int worldXSize;
    int worldYSize;

    DataUnit heldData; // pointer if the ant is holding some data

    double kp; // pick constant
    double kd; // drop constant
    double kc; // crowd constant

    int sight;

    Random dice;

    // Constructor for the ant
    public Ant(Zone aZone, DataSpace dSpace, Grid2dImpl aSpace,
	       int X, int Y, int ID, double pick, double drop, double crowd, int range, Random d)
    {
     // Call the constructor for the bug's parent class.
     super(aZone);

     // Record the bug's foodspace, bugspace, initial position and
     // id number.
     myDataSpace = dSpace;
     mySpace = aSpace;
     worldXSize = myDataSpace.getSizeX();
     worldYSize = myDataSpace.getSizeY();

     xPos = X;
     yPos = Y;

     antID = ID;

     heldData = null;

     kp = pick;
     kd = drop;
     kc = crowd;

     sight = range;
     dice = d;

    }

    public void randomWalk()
    {
     int newX, newY;

     if (heldData == null)
	 return;

     // Decide where to move.
     newX = xPos +
	 (dice.nextInt(3)-1);
     newY = yPos +
	 (dice.nextInt(3)-1);
     newX = (newX + worldXSize) % worldXSize;
     newY = (newY + worldYSize) % worldYSize;

     // Is there a bug at the new position already? If not, put a
     // null at this bug's current position and put this bug at the
     // new position.
     if (mySpace.getObjectAtX$Y(newX, newY) == null)
	 {
	  mySpace.putObject$atX$Y(null, xPos, yPos);
	  xPos = newX;
	  yPos = newY;
	  mySpace.putObject$atX$Y(this, xPos, yPos);
	 }

    }

    // Method to report the bug's position to the console.
    public void pickData()
    {
	double chance;
	double[] disp;
	DataUnit candidate;
	int cx, cy;

	// do nothing if hands are full or there is no object in the ground
	if (heldData != null)
	    return;
	   
	do {
	    candidate = myDataSpace.getPickCandidate();
	    cx = candidate.getX();
	    cy = candidate.getY();
	} while (mySpace.getObjectAtX$Y(cx, cy) != null);

	mySpace.putObject$atX$Y(null, xPos, yPos);
	xPos = cx;
	yPos = cy;
	mySpace.putObject$atX$Y(this, xPos, yPos);

	// getDisparity returns two values, the first is the 
	// average disparity, and the second is the number of neighbours
	disp = myDataSpace.getDisparity(candidate,
					xPos,yPos,sight);

	// Calculation of Pick chance
	// Based on "Swarms on Continuous Data", Vitorino Ramos et al, 2005

	// Basic pick chance is calculated based on disparity
	chance = Math.pow(disp[0]/(kp+disp[0]),2);

	// Pick chance is affected by number of neighbours
	// The more neighbours, less is the pick chance
	chance = chance*
	    (1-(Math.pow(disp[1],2)/(Math.pow(disp[1],2)+Math.pow(kc,2))));

	if (dice.nextDouble()
	    //Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) 
	    <= chance)
	    {
		heldData = (DataUnit) myDataSpace.getObjectAtX$Y(xPos,yPos);
		heldData.setCarried(true);

		myDataSpace.putObject$atX$Y(null,xPos,yPos);
		
	    }


    }
    
    public void dropData()
    {

	double[] disp;
	double chance;


	// do nothing if ant has no object, or if the space is not empty
	if (heldData == null || myDataSpace.getObjectAtX$Y(xPos,yPos) != null)
	    {
		// System.out.println("   Ant has no object");
		return;
	    }
      
	// getDisparity returns two values, the first is the 
	// average disparity, and the second is the number of neighbours
	disp = myDataSpace.getDisparity(heldData,xPos,yPos,sight);

	// Calculation of Drop chance
	// Based on "Swarms on Continuous Data", Vitorino Ramos et al, 2005

	// Basic drop chance is calculated based on disparity
	chance = Math.pow(kd/(kd+disp[0]),2);

	// Drop chance is affected by number of neighbours
	// The more neighbours, more is the drop chance
	chance = chance*
	    (Math.pow(disp[1],2)/(Math.pow(disp[1],2)+Math.pow(kc,2)));



	//if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) 
	if (dice.nextDouble()
	    <= chance)
	    {
		myDataSpace.putObject$atX$Y(heldData,xPos,yPos);
		heldData.setCarried(false);
		heldData.setPosition(xPos,yPos);

		heldData = null;
	    }

    }

    public Object drawSelfOn(Raster r)
    {
    	r.drawPointX$Y$Color(xPos,yPos,(byte) (heldData==null?1:2));
    	return this;
    }


}
