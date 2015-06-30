import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import swarm.activity.*;
import swarm.Selector;
import swarm.collections.*;

public class Bug extends SwarmObjectImpl {
    int tendX, tendY; // アリが前回進んだ方向
    int xPos, yPos;
    int worldXSize, worldYSize;
    FoodSpace foodSpace;
    PheromoneSpace pheromoneSpace;
    PheromoneOnGround pheromoneOnGround;
    Grid2d world;
    int haveEaten; // haveEaten == 1（アリは餌を持っている）haveEaten == 0（アリは餌を持っていない）

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
	 * 餌を更新した際に、餌とアリが重なった場合
	 * 餌を持っていない状態にして、巣に戻す
	 */
	turnToColony();		
	/*
	 * アリが餌を持っていない場合
	 */
	if ( haveEaten == 0 )
	    {   
		/*
		 * 全方向を見回して、餌があれば餌を食べる
		 */
		eatFood();
		if ( haveEaten == 1 ) {
		    pheromoneOnGround.putValue$atX$Y
			(pheromoneOnGround.getValueAtX$Y(xPos,yPos)
			 + amountOfReleasingPheromone, xPos, yPos);
		    
		/*
		 * 餌に接していない場合
		 */
		} else {
		    /*
		     * フェロモン上にいるアリは
		     * 全方向を見回して、フェロモンが濃い方向に移動する
		     *
		     * 巣から離れた方向には進みやすい
		     */
		    if (pheromoneSpace.getValueAtX$Y(xPos,yPos) > 0) {
			proceedOnPheromone();
		    /*
		     * アリがフェロモン上にいない場合
		     */
		    } else {
			proceedNotOnPheromone();
		    }
		}
	    }
	
	/*
	 * アリが餌を持っている場合
	 * 餌に近づいた（であろう）時に、フェロモンを出す
	 */
	else
	    {
		/*
		 * 巣に隣接していれば餌を運び終えたと判断
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
	 * 巣から遠方に向う方向（４方向）のフェロモンは大きく感応する
	 * （遠方と判断する２方向は、awayFromColonyRate倍、フェロモンを大きくする
	 * もう２方向は、awayFromColonyRate^2倍、フェロモンを大きくする）
	 * 一歩前（後方）のフェロモンは小さく感応する
	 * （来た方向は、1/awayFromCokony^2倍、フェロモンを小さくする）
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
	 * 行きたい場所にアリがいる場合、巣から遠ざかる方向に進むように試みる
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

	// 餌を持っておらずフェロモン上にいない場合は、(1-turnRate)(1-turnRate)の確率で直進する
	
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
	 * 巣に近づく２方向にランダムに移動
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
	 * 行こうとした場所に餌、または、他のアリがあれば
	 * 餌のない方向へランダムに移動
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












