import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import java.lang.*;
import java.util.*;

public class Bug extends SwarmObjectImpl {
  public int xPos, yPos;
  int worldXSize, worldYSize;
  Grid2d world;

  public Vector2D moveVec;

  //各種限界値
  public float maxDirectionChange = (float)(Math.PI/8.0);
 
  public int searchLength = 10; //視界の距離
  public float searchDeg = (float)Math.PI / 4.0f; //視界の角度（片側）
  public int separationLength = 5; //反発が働く距離
  public int obstacleLength = 10; //避ける障害物の範囲

  public float maxSpeed = 5.0f; //最大移動速度
  public float minSpeed = 1.0f; //最低移動速度

  //各種重み
  public float separationWeight = 0.1f;
  public float alignmentWeight = 0.2f;
  public float cohesionWeight = 0.7f;
  public float obstacleWeight = 0.05f;

  ObstacleSpace obstacleSpace;

  public Bug(Zone aZone){
    super(aZone);
    moveVec = new Vector2D(0, 0);
  }
  
  public Vector2D getMoveVec() {
    return moveVec;
  }

  public Object setWorld(Grid2d w){
    world=w;
    worldXSize = world.getSizeX();
    worldYSize = world.getSizeY();
    return this;
  }

  public Object setX$Y(int x, int y){
    xPos = x;
    yPos = y;
    return this;
  }
  
  public Object setObstacle(ObstacleSpace o) {
    obstacleSpace = o;
    return this;
  }

  public void step(){
    int i, j;
    int width;
    ArrayList<Object> visibleBugs = new ArrayList<Object>(); //可視な仲間達
    Bug foundBug; //見つかった仲間の一時格納場所
    boolean existNearBug = false; //近くに仲間がいるかを示すフラグ
    boolean existNearObs = false; //近くに障害物があるかを示すフラグ
    ArrayList<Object> nearBugs = new ArrayList<Object>(); //近すぎる仲間達
    ArrayList<Object> nearObss = new ArrayList<Object>(); //近すぎる障害物

    int nearestBugDistance = searchLength * searchLength; //最も近い仲間の距離
    int nearestObsDistance = nearestBugDistance; //最も近い障害物の距離
    int gravityX = 0; int gravityY = 0; //重心計算用
    
    //可視な仲間及び障害物の探索
    for(i = xPos - searchLength; i <= xPos + searchLength; i++) {
      width = (int)(Math.sqrt(searchLength*searchLength - (i-xPos)*(i-xPos)));
      for(j = yPos - width; j <= yPos + width; j++) {
	if( i != xPos || j != xPos ) {
	  int adjustedX = adjust(i, worldXSize);
	  int adjustedY = adjust(j, worldYSize);
	  
	  //仲間の探索
	  foundBug = (Bug)world.getObjectAtX$Y(adjustedX, adjustedY);
	  if( foundBug != null ) {
	    visibleBugs.add( foundBug ); //可視な仲間に追加
	    gravityX += i - xPos; 
	    gravityY += j - yPos;
	    
	    //最も近い仲間達を登録
	    int tmpDistance = (i-xPos)*(i-xPos)+(j-yPos)*(j-yPos);
	    if( tmpDistance < nearestBugDistance ) {
	      existNearBug = true;
	      nearBugs.clear();
	      nearBugs.add(foundBug);
	      nearestBugDistance = tmpDistance;
	    }
	    else if( tmpDistance == nearestBugDistance ) {
	      nearBugs.add(foundBug);
	    }
	  }
	  //障害物の探索
	  double tmpDeg = (new Vector2D(i-xPos, j-yPos)).getDeg();
	  double moveDeg = moveVec.getDeg();
	  //視界に入る障害物だけよける
	  if( Math.abs(degDiff(tmpDeg, moveDeg)) < searchDeg &&
	      obstacleSpace.getValueAtX$Y(adjustedX, adjustedY) == 1) {
	    int tmpDistance = (i-xPos)*(i-xPos)+(j-yPos)*(j-yPos);
	    if( tmpDistance < nearestObsDistance ) {
	      existNearObs = true;
	      nearObss.clear();
	      nearObss.add(new Coordinate(i, j));
	      nearestObsDistance = tmpDistance;
	    }
	    else if( tmpDistance == nearestObsDistance ) {
	      nearObss.add(new Coordinate(i, j));
	    }
	  }
	}
      }
    }
    
    Vector2D separationVec; //近くの仲間から離れるベクトル
    Vector2D alignmentVec;  //視界に入る仲間の移動方向の平均ベクトル
    Vector2D cohesionVec;   //視界に入る仲間の重心の方向のベクトル
    Vector2D obstacleVec;   //障害物から離れるベクトル

    //障害物の回避を最優先する
    if( existNearObs ) {
      moveVec.add( calcObstacleVec(existNearObs, nearObss) );
    }
    else {
      separationVec = calcSeparationVec(existNearBug, nearBugs);
      alignmentVec = calcAlignmentVec(visibleBugs);
      cohesionVec = calcCohesionVec(visibleBugs, gravityX, gravityY);

      moveVec.add(separationVec);
      moveVec.add(alignmentVec);
      moveVec.add(cohesionVec);
    }

    //速度制限
    moveVec.checkSpeed(maxSpeed, minSpeed);

    int newXPos = adjust(xPos + (int)moveVec.getX(), worldXSize);
    int newYPos = adjust(yPos + (int)moveVec.getY(), worldYSize);

    //行き先に仲間も障害物も無かったら移動
    if( world.getObjectAtX$Y(newXPos, newYPos) == null &&
	obstacleSpace.getValueAtX$Y(newXPos, newYPos) == 0) {
      world.putObject$atX$Y(null, xPos, yPos);
      xPos = newXPos;
      yPos = newYPos;
      world.putObject$atX$Y(this, xPos, yPos);
    }
    else {
      Vector2D randomVec = new Vector2D(0, 0);
      randomVec.toRandomVec(maxSpeed, minSpeed);
      moveVec.add(randomVec);
    }
      
  }

  public Object drawSelfOn(Raster r){
    r.drawPointX$Y$Color(xPos,yPos,(byte)2);//キャストが必要
    return this;
  }
 
  //近すぎる仲間から離れようとするベクトルを計算
  private Vector2D calcSeparationVec(boolean existNearBug,ArrayList nearBugs) {
    if( !existNearBug ) {
      return new Vector2D(0, 0);
    }
    
    int i;
    int x=0, y=0;
    for(i=0; i<nearBugs.size(); i++) {
      x += ( (Bug)nearBugs.get(i) ).xPos;
      y += ( (Bug)nearBugs.get(i) ).yPos;
    }
      
    Vector2D separationVec = new Vector2D(xPos-x, yPos-y);
    
    separationVec.times( separationWeight / nearBugs.size() ); 
    return separationVec;
  }

  //障害物から離れようとするベクトルを計算
  private Vector2D calcObstacleVec(boolean existNearObs, ArrayList nearObss) {
    if( !existNearObs ) {
      return new Vector2D(0, 0);
    }
    
    Vector2D obstacleVec = new Vector2D(0, 0);

    int i;
    for(i=0; i<nearObss.size(); i++) {
      int x = xPos - ((Coordinate)nearObss.get(i)).getX();
      int y = yPos - ((Coordinate)nearObss.get(i)).getY();
      
      Vector2D obsVec = new Vector2D(x, y);
      float degDiff = (float)(obsVec.getDeg() - moveVec.getDeg());
      
      if( degDiff == 0.0 ) {
	if( Math.random() > 0.5 ) {
	  Vector2D tmpVec = moveVec.getRightVec();
	  tmpVec.toUnitVec();
	  obstacleVec.add(tmpVec);
	}
	else {
	  Vector2D tmpVec = moveVec.getLeftVec();
	  tmpVec.toUnitVec();
	  obstacleVec.add(tmpVec);
	}
	continue;
      }

      else if( Math.abs(degDiff) > Math.PI ) {
	if( degDiff > 0 ) {
	  degDiff = degDiff - (float)Math.PI*2;
	}
	else {
	  degDiff = (float)Math.PI*2 + degDiff;
	}
      }
     
      if ( degDiff > 0 ) {
	Vector2D tmpVec = moveVec.getLeftVec();
	tmpVec.toUnitVec();
	obstacleVec.add(tmpVec);
	continue;
      }
      else {
	Vector2D tmpVec = moveVec.getRightVec();
	tmpVec.toUnitVec();
	obstacleVec.add(tmpVec);
      }
    }
    
    obstacleVec.toUnitVec();
    obstacleVec.times(obstacleWeight);
    
    return obstacleVec;
  }
  
  //可視な仲間達の平均移動ベクトルを計算
  private Vector2D calcAlignmentVec(ArrayList visibleBugs) {
    Vector2D alignmentVec = new Vector2D(0, 0);

    int i;
    for(i=0; i<visibleBugs.size(); i++) {    
      alignmentVec.add( ((Bug)visibleBugs.get(i)).getMoveVec() );
    }

    alignmentVec.times( alignmentWeight / visibleBugs.size() );
    return alignmentVec;
  }

  //可視な仲間達の重心へ向かうベクトルを計算
  private Vector2D calcCohesionVec(ArrayList visibleBugs,
				   int gravityX, int gravityY) {
    int size;
    if( (size = visibleBugs.size()) == 0 ) {
      return new Vector2D(0, 0);
    }
    
    Vector2D cohesionVec = new Vector2D(gravityX, gravityY);

    cohesionVec.times( cohesionWeight / visibleBugs.size() );
    return cohesionVec;
  }

  private double degDiff(double objDeg, double baseDeg) {
    double diff = objDeg - baseDeg;
    if( Math.abs(diff) > Math.PI ) {
      if( diff > 0.0 ) {
	return diff - Math.PI*2.0;
      }
      else {
	return diff + Math.PI*2.0;
      }
    }
    return diff;
  }

  //範囲から外れない座標値を返す
  private int adjust(int x, int width) {
    if( x >= 0 ) {
      return x % width;
    }
    else {
      return width + ( x % width );
    }
  }

  private void printList(List list) {
    int i;
    System.out.println("printList");
    for(i=0; i<list.size(); i++) {
      System.out.println(list.get(i));
    }
  }
}
