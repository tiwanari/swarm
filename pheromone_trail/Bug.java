import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import swarm.activity.*;
import swarm.Selector;
import swarm.collections.*;

public class Bug extends SwarmObjectImpl {
    int tendX, tendY; // $B%"%j$,A02s?J$s$@J}8~(B
    int xPos, yPos;
    int worldXSize, worldYSize;
    FoodSpace foodSpace;
    PheromoneSpace pheromoneSpace;
    PheromoneOnGround pheromoneOnGround;
    Grid2d world;
    int haveEaten; // haveEaten == 1$B!J%"%j$O1B$r;}$C$F$$$k!K(BhaveEaten == 0$B!J%"%j$O1B$r;}$C$F$$$J$$!K(B

    int colonySize;
    int amountOfReleasingPheromone;
    double awayFromColonyRate;
    double turnRate;
    
    public Bug(Zone aZone){
	super(aZone);
    }
    
    public Object setWorld$Food(Grid2d w,FoodSpace f,PheromoneSpace h,PheromoneOnGround p){
	haveEaten = 0;
	world = w;
	foodSpace = f;
	pheromoneSpace = h;
	pheromoneOnGround = p;
	tendX = 0;
	tendY = 0;
	worldXSize = world.getSizeX();
	worldYSize = world.getSizeY();
	return this;
    }

    public void setBugParametar(int co, int am, double aw, double tu){
	colonySize = co;
	amountOfReleasingPheromone = am;
	awayFromColonyRate = aw;
	turnRate = tu;
    }
	
    
    public Object setX$Y(int x, int y){
	xPos = x;
	yPos = y;
	return this;
    }
    
    public Object setWorldSizeX$Y(int xSize, int ySize){
	worldXSize=xSize;
	worldYSize=ySize;
	return this;
    }
			
    public void step(){

	int newX,newY;

	/*
	 * $B1B$r99?7$7$?:]$K!"1B$H%"%j$,=E$J$C$?>l9g(B
	 * $B1B$r;}$C$F$$$J$$>uBV$K$7$F!"Ac$KLa$9(B
	 */
	turnToColony();		
	/*
	 * $B%"%j$,1B$r;}$C$F$$$J$$>l9g(B
	 */
	if ( haveEaten == 0 )
	    {   
		/*
		 * $BA4J}8~$r8+2s$7$F!"1B$,$"$l$P1B$r?)$Y$k(B
		 */
		eatFood();
		if ( haveEaten == 1 ) {
		    pheromoneOnGround.putValue$atX$Y
			(pheromoneOnGround.getValueAtX$Y(xPos,yPos)
			 + amountOfReleasingPheromone, xPos, yPos);
		    
		/*
		 * $B1B$K@\$7$F$$$J$$>l9g(B
		 */
		} else {
		    /*
		     * $B%U%'%m%b%s>e$K$$$k%"%j$O(B
		     * $BA4J}8~$r8+2s$7$F!"%U%'%m%b%s$,G;$$J}8~$K0\F0$9$k(B
		     *
		     * $BAc$+$iN%$l$?J}8~$K$O?J$_$d$9$$(B
		     */
		    if (pheromoneSpace.getValueAtX$Y(xPos,yPos) > 0) {
			proceedOnPheromone();
		    /*
		     * $B%"%j$,%U%'%m%b%s>e$K$$$J$$>l9g(B
		     */
		    } else {
			proceedNotOnPheromone();
		    }
		}
	    }
	
	/*
	 * $B%"%j$,1B$r;}$C$F$$$k>l9g(B
	 * $B1B$K6a$E$$$?!J$G$"$m$&!K;~$K!"%U%'%m%b%s$r=P$9(B
	 */
	else
	    {
		/*
		 * $BAc$KNY@\$7$F$$$l$P1B$r1?$S=*$($?$HH=CG(B
		 */
		if ((xPos - worldXSize/2)*(xPos - worldXSize/2) + (yPos - worldYSize/2)*(yPos - worldYSize/2) < (colonySize+1)*(colonySize+1))
		    {
			haveEaten = 0;
			tendX = - tendX;
			tendY = - tendY;
		    }
		else
		    {
			returnToHome();
		    }
	    }
    }
    
    private void turnToColony(){
	if (foodSpace.getValueAtX$Y(xPos,yPos) == 1){
	    haveEaten = 0;
	    world.putObject$atX$Y(null,xPos,yPos);
	    xPos = worldXSize/2;
	    yPos = worldYSize/2;
	    tendX = tendY = 0;
	    world.putObject$atX$Y(this,xPos,yPos);
	}
    }

    private void eatFood(){
	
	int newX, newY;
	
	if (foodSpace.getValueAtX$Y((xPos+1+worldXSize)%worldXSize,(yPos+1+worldYSize)%worldYSize) == 1) {
	    
	    tendX = 1;
	    tendY = 1;
	    
	    newX = (xPos+1 + worldXSize) % worldXSize;
	    newY = (yPos+1 + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
		foodSpace.putValue$atX$Y(0,xPos,yPos);
		haveEaten=1;
	    }
	} else if (foodSpace.getValueAtX$Y((xPos+1+worldXSize)%worldXSize,yPos) == 1) {
	    
	    tendX = 1;
	    tendY = 0;
	    
	    newX = (xPos+1 + worldXSize) % worldXSize;
	    newY = (yPos + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
		foodSpace.putValue$atX$Y(0,xPos,yPos);
		haveEaten=1;
	    }
	} else if (foodSpace.getValueAtX$Y((xPos+1+worldXSize)%worldXSize,(yPos-1+worldYSize)%worldYSize) == 1) {
	    
	    tendX = 1;
	    tendY = -1;
	    
	    newX = (xPos+1 + worldXSize) % worldXSize;
	    newY = (yPos-1 + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
		foodSpace.putValue$atX$Y(0,xPos,yPos);
		haveEaten=1;
	    }
	} else if (foodSpace.getValueAtX$Y(xPos,(yPos+1+worldYSize)%worldYSize) == 1) {
	    
	    tendX = 0;
	    tendY = 1;
	    
	    newX = (xPos + worldXSize) % worldXSize;
	    newY = (yPos+1 + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
		foodSpace.putValue$atX$Y(0,xPos,yPos);
		haveEaten=1;
	    }
	} else if (foodSpace.getValueAtX$Y(xPos,(yPos-1+worldYSize)%worldYSize) == 1) {
	    
	    tendX = 0;
	    tendY = -1;
	    
	    newX = (xPos + worldXSize) % worldXSize;
	    newY = (yPos-1 + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
		foodSpace.putValue$atX$Y(0,xPos,yPos);
		haveEaten=1;
	    }
	} else if (foodSpace.getValueAtX$Y((xPos-1+worldXSize)%worldXSize,(yPos+1+worldYSize)%worldYSize) == 1) {
	    
	    tendX = -1;
	    tendY = 1;
	    
	    newX = (xPos-1 + worldXSize) % worldXSize;
	    newY = (yPos+1 + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
		foodSpace.putValue$atX$Y(0,xPos,yPos);
		haveEaten=1;
	    }
	} else if (foodSpace.getValueAtX$Y((xPos-1+worldXSize)%worldXSize,yPos) == 1) {
	    
	    tendX = -1;
	    tendY = 0;
	    
	    newX = (xPos-1 + worldXSize) % worldXSize;
	    newY = (yPos + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
		foodSpace.putValue$atX$Y(0,xPos,yPos);
		haveEaten=1;
	    }
	} else if (foodSpace.getValueAtX$Y((xPos-1+worldXSize)%worldXSize,(yPos-1+worldYSize)%worldYSize) == 1) {
	    
	    tendX = -1;
	    tendY = -1;
	    
	    newX = (xPos-1 + worldXSize) % worldXSize;
	    newY = (yPos-1 + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
		foodSpace.putValue$atX$Y(0,xPos,yPos);
		haveEaten=1;
	    }
	}
    }

    private void proceedOnPheromone(){

	int newX, newY;
	int xProposed, yProposed; // xProposed, yProposed == -1, 0 or 1	
	int pheromoneDensity;
	int tempDensity;
	int x = 1;
	int y = 1;
	int x0 = 0;
	int y0 = 0;
	
	if ( xPos < worldXSize/2 ) x = 0;
	if ( yPos < worldYSize/2 ) y = 0;

	if ( (xPos-worldXSize/2)*(xPos-worldXSize/2) > (yPos-worldYSize/2)*(yPos-worldYSize/2) ) {
	    if ( xPos - worldXSize/2 < 0 ) x0 = -1;
	    else x0 = 1;
	}
	if ( (xPos-worldXSize/2)*(xPos-worldXSize/2) < (yPos-worldYSize/2)*(yPos-worldYSize/2) ) {
	    if ( yPos - worldYSize/2 < 0 ) y0 = -1;
	    else y0 = 1;
	}
	
	xProposed = tendX;
	yProposed = tendY;
	pheromoneDensity = pheromoneSpace.getValueAtX$Y(( xPos + xProposed + worldXSize ) % worldXSize, ( yPos + yProposed + worldYSize ) % worldYSize);
	
	/*
	 * $BAc$+$i1sJ}$K8~$&J}8~!J#4J}8~!K$N%U%'%m%b%s$OBg$-$/461~$9$k(B
	 * $B!J1sJ}$HH=CG$9$k#2J}8~$O!"(BawayFromColonyRate$BG\!"%U%'%m%b%s$rBg$-$/$9$k(B
	 * $B$b$&#2J}8~$O!"(BawayFromColonyRate^2$BG\!"%U%'%m%b%s$rBg$-$/$9$k!K(B
	 * $B0lJbA0!J8eJ}!K$N%U%'%m%b%s$O>.$5$/461~$9$k(B
	 * $B!JMh$?J}8~$O!"(B1/awayFromCokony^2$BG\!"%U%'%m%b%s$r>.$5$/$9$k!K(B
	 */
	    
	tempDensity = pheromoneSpace.getValueAtX$Y((xPos+1+worldXSize)%worldXSize,(yPos+1+worldYSize)%worldYSize);
	if ( tempDensity != 0 && ( x == 1 | x-1 == 1 ) && ( y == 1 | y-1 == 1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( tempDensity != 0 && ( x0 == 1 | y0 == 1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( - tendX == 1 && - tendY == 1 ) tempDensity = (int)(tempDensity / (awayFromColonyRate*awayFromColonyRate));
	if (pheromoneDensity < tempDensity) {
	    xProposed = 1;
	    yProposed = 1;
	    pheromoneDensity = tempDensity;
	} 
	    
	tempDensity = pheromoneSpace.getValueAtX$Y((xPos+1+worldXSize)%worldXSize,(yPos+worldYSize)%worldYSize);
	if ( tempDensity != 0 && ( x == 1 | x-1 == 1 ) && ( y == 0 | y-1 == 0 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( tempDensity != 0 && ( y0 == 1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( - tendX == 1 && - tendY == 0 ) tempDensity = (int)(tempDensity / (awayFromColonyRate*awayFromColonyRate));
	if (pheromoneDensity < tempDensity) {
	    xProposed = 1;
	    yProposed = 0;
	    pheromoneDensity = tempDensity;
	} 
	
	tempDensity = pheromoneSpace.getValueAtX$Y((xPos+1+worldXSize)%worldXSize,(yPos-1+worldYSize)%worldYSize);
	if ( tempDensity != 0 && ( x == 1 | x-1 == 1 ) && ( y == -1 | y-1 == -1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( tempDensity != 0 && ( x0 == 1 | y0 == -1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( - tendX == 1 && - tendY == -1 ) tempDensity = (int)(tempDensity / (awayFromColonyRate*awayFromColonyRate));
	if (pheromoneDensity < tempDensity) {
	    xProposed = 1;
	    yProposed = -1;
	    pheromoneDensity = tempDensity;
	} 
	
	tempDensity = pheromoneSpace.getValueAtX$Y((xPos+worldXSize)%worldXSize,(yPos-1+worldYSize)%worldYSize);
	if ( tempDensity != 0 && ( x == 0 | x-1 == 0 ) && ( y == -1 | y-1 == -1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( tempDensity != 0 && ( y0 == -1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( - tendX == 0 && - tendY == -1 ) tempDensity = (int)(tempDensity / (awayFromColonyRate*awayFromColonyRate));
	if (pheromoneDensity < tempDensity) {
	    xProposed = 0;
	    yProposed = -1;
	    pheromoneDensity = tempDensity;
	}
	
	tempDensity = pheromoneSpace.getValueAtX$Y((xPos-1+worldXSize)%worldXSize,(yPos-1+worldYSize)%worldYSize);
	if ( tempDensity != 0 && ( x == -1 | x-1 == -1 ) && ( y == -1 | y-1 == -1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( tempDensity != 0 && ( x0 == -1 | y0 == -1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( - tendX == -1 && - tendY == -1 ) tempDensity = (int)(tempDensity / (awayFromColonyRate*awayFromColonyRate));
	if (pheromoneDensity < tempDensity) {
	    xProposed = -1;
	    yProposed = -1;
	    pheromoneDensity = tempDensity;
	}
	
	tempDensity = pheromoneSpace.getValueAtX$Y((xPos-1+worldXSize)%worldXSize,(yPos+worldYSize)%worldYSize);
	if ( tempDensity != 0 && ( x == -1 | x-1 == -1 ) && ( y == 0 | y-1 == 0 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( tempDensity != 0 && ( x0 == -1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( - tendX == -1 && - tendY == 0 ) tempDensity = (int)(tempDensity / (awayFromColonyRate*awayFromColonyRate));
	if (pheromoneDensity < tempDensity) {
	    xProposed = -1;
	    yProposed = 0;
	    pheromoneDensity = tempDensity;
	}
	
	tempDensity = pheromoneSpace.getValueAtX$Y((xPos-1+worldXSize)%worldXSize,(yPos+1+worldYSize)%worldYSize);
	if ( tempDensity != 0 && ( x == -1 | x-1 == -1 ) && ( y == 1 | y-1 == 1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( tempDensity != 0 && ( x0 == -1 | y0 == 1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( - tendX == -1 && - tendY == 1 ) tempDensity = (int)(tempDensity / (awayFromColonyRate*awayFromColonyRate));
	if (pheromoneDensity < tempDensity) {
	    xProposed = -1;
	    yProposed = 1;
	    pheromoneDensity = tempDensity;
	}
	
	tempDensity = pheromoneSpace.getValueAtX$Y((xPos+worldXSize)%worldXSize,(yPos+1+worldYSize)%worldYSize);
	if ( tempDensity != 0 && ( x == 0 | x-1 == 0 ) && ( y == 1 | y-1 == 1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( tempDensity != 0 && ( y0 == 1 ) )
	    tempDensity = (int)(tempDensity * awayFromColonyRate);
	if ( - tendX == 0 && - tendY == 1 ) tempDensity = (int)(tempDensity / (awayFromColonyRate*awayFromColonyRate));
	if (pheromoneDensity < tempDensity) {
	    xProposed = 0;
	    yProposed = 1;
	    pheromoneDensity = tempDensity;
	}
	
	tendX = xProposed;
	tendY = yProposed;
	
	newX = xPos + tendX;
	newY = yPos + tendY;
	
	newX = (newX + worldXSize) % worldXSize;
	newY = (newY + worldYSize) % worldYSize;
	
	if (world.getObjectAtX$Y(newX,newY) == null){
	    world.putObject$atX$Y(null,xPos,yPos);
	    xPos = newX;
	    yPos = newY;
	    world.putObject$atX$Y(this,newX,newY);
	
	/*
	 * $B9T$-$?$$>l=j$K%"%j$,$$$k>l9g!"Ac$+$i1s$6$+$kJ}8~$K?J$`$h$&$K;n$_$k(B
	 */
	} else {
	    
	    tendX = Globals.env.uniformIntRand.getIntegerWithMin$withMax(x-1,x);
	    tendY = Globals.env.uniformIntRand.getIntegerWithMin$withMax(y-1,y);

	    newX = xPos + tendX;
	    newY = yPos + tendY;
	    
	    newX = (newX + worldXSize) % worldXSize;
	    newY = (newY + worldYSize) % worldYSize;
	    
	    if (world.getObjectAtX$Y(newX,newY) == null){
		world.putObject$atX$Y(null,xPos,yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
	    }
	}
	
    }
    
    private void proceedNotOnPheromone(){
	
	int newX, newY;

	// $B1B$r;}$C$F$*$i$:%U%'%m%b%s>e$K$$$J$$>l9g$O!"(B(1-turnRate)(1-turnRate)$B$N3NN($GD>?J$9$k(B
	
	if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < turnRate)
	    tendX = Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
	if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < turnRate)
	    tendY = Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
	
	newX = xPos + tendX;
	newY = yPos + tendY;
	
	newX = (newX + worldXSize) % worldXSize;
	newY = (newY + worldYSize) % worldYSize;
	
	if (world.getObjectAtX$Y(newX,newY) == null){
	    world.putObject$atX$Y(null,xPos,yPos);
	    xPos = newX;
	    yPos = newY;
	    world.putObject$atX$Y(this,newX,newY);
	}
	
    }

    private void returnToHome(){
	
	int newX, newY;
	int x = -1;
	int y = -1;
	int x0 = 0;
	int y0 = 0;
	
	/*
	 * $BAc$K6a$E$/#2J}8~$K%i%s%@%`$K0\F0(B
	 */
	if ( xPos < worldXSize/2 ) x = 0;
	if ( yPos < worldYSize/2 ) y = 0;
	
	if ( (xPos-worldXSize/2)*(xPos-worldXSize/2) > (yPos-worldYSize/2)*(yPos-worldYSize/2) ) {
	    if ( xPos - worldXSize/2 < 0 ) x0 = 1;
	    else x0 = -1;
	}
	if ( (xPos-worldXSize/2)*(xPos-worldXSize/2) < (yPos-worldYSize/2)*(yPos-worldYSize/2) ) {
	    if ( yPos - worldYSize/2 < 0 ) y0 = 1;
	    else y0 = -1;
	}
	
	tendX = Globals.env.uniformIntRand.getIntegerWithMin$withMax(x,x+1);
	tendY = Globals.env.uniformIntRand.getIntegerWithMin$withMax(y,y+1);
	
	if ( x0 != 0 && y0 == 0 ) tendX = x0;
	if ( x0 == 0 && y0 != 0 ) tendY = y0;

	newX = xPos + tendX;
	newY = yPos + tendY;
	
	newX = (newX + worldXSize) % worldXSize;
	newY = (newY + worldYSize) % worldYSize;
	
	
	if (world.getObjectAtX$Y(newX,newY) == null && foodSpace.getValueAtX$Y(newX,newY) == 0)
	    {
		world.putObject$atX$Y(null,xPos,yPos);
		pheromoneOnGround.putValue$atX$Y
		    (pheromoneOnGround.getValueAtX$Y(xPos,yPos)
		     + amountOfReleasingPheromone, xPos, yPos);
		xPos = newX;
		yPos = newY;
		world.putObject$atX$Y(this,newX,newY);
	    }
	
	/*
	 * $B9T$3$&$H$7$?>l=j$K1B!"$^$?$O!"B>$N%"%j$,$"$l$P(B
	 * $B1B$N$J$$J}8~$X%i%s%@%`$K0\F0(B
	 */
	
	else
	    {
		do {
		    tendX = Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		    tendY = Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		    
		    newX = xPos + tendX;
		    newY = yPos + tendY;
		    
		    newX = (newX + worldXSize) % worldXSize;
		    newY = (newY + worldYSize) % worldYSize;
		    
		}while (foodSpace.getValueAtX$Y(newX,newY) != 0 );
		
		if (world.getObjectAtX$Y(newX,newY) == null){
		    world.putObject$atX$Y(null,xPos,yPos);
		    xPos = newX;
		    yPos = newY;
		    world.putObject$atX$Y(this,newX,newY);
		}
	    }
	
    }
    
    public Object report(){
	if(haveEaten==1)
	    System.out.println("I found food at X = " + xPos + " Y = " + yPos +"!");
	return this;
    }
    
    public Object drawSelfOn(Raster r){
	r.drawPointX$Y$Color(xPos,yPos,(byte)2);
	return this;
    }
}












