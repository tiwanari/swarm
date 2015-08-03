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

  //�e����E�l
  public float maxDirectionChange = (float)(Math.PI/8.0);
 
  public int searchLength = 10; //���E�̋���
  public float searchDeg = (float)Math.PI / 4.0f; //���E�̊p�x�i�Б��j
  public int separationLength = 5; //��������������
  public int obstacleLength = 10; //�������Q���͈̔�

  public float maxSpeed = 5.0f; //�ő�ړ����x
  public float minSpeed = 1.0f; //�Œ�ړ����x

  //�e��d��
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
    ArrayList<Object> visibleBugs = new ArrayList<Object>(); //���Ȓ��ԒB
    Bug foundBug; //�����������Ԃ̈ꎞ�i�[�ꏊ
    boolean existNearBug = false; //�߂��ɒ��Ԃ����邩�������t���O
    boolean existNearObs = false; //�߂��ɏ�Q�������邩�������t���O
    ArrayList<Object> nearBugs = new ArrayList<Object>(); //�߂����钇�ԒB
    ArrayList<Object> nearObss = new ArrayList<Object>(); //�߂������Q��

    int nearestBugDistance = searchLength * searchLength; //�ł��߂����Ԃ̋���
    int nearestObsDistance = nearestBugDistance; //�ł��߂���Q���̋���
    int gravityX = 0; int gravityY = 0; //�d�S�v�Z�p
    
    //���Ȓ��ԋy�я�Q���̒T��
    for(i = xPos - searchLength; i <= xPos + searchLength; i++) {
      width = (int)(Math.sqrt(searchLength*searchLength - (i-xPos)*(i-xPos)));
      for(j = yPos - width; j <= yPos + width; j++) {
	if( i != xPos || j != xPos ) {
	  int adjustedX = adjust(i, worldXSize);
	  int adjustedY = adjust(j, worldYSize);
	  
	  //���Ԃ̒T��
	  foundBug = (Bug)world.getObjectAtX$Y(adjustedX, adjustedY);
	  if( foundBug != null ) {
	    visibleBugs.add( foundBug ); //���Ȓ��Ԃɒǉ�
	    gravityX += i - xPos; 
	    gravityY += j - yPos;
	    
	    //�ł��߂����ԒB��o�^
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
	  //��Q���̒T��
	  double tmpDeg = (new Vector2D(i-xPos, j-yPos)).getDeg();
	  double moveDeg = moveVec.getDeg();
	  //���E�ɓ����Q�������悯��
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
    
    Vector2D separationVec; //�߂��̒��Ԃ��痣���x�N�g��
    Vector2D alignmentVec;  //���E�ɓ��钇�Ԃ̈ړ������̕��σx�N�g��
    Vector2D cohesionVec;   //���E�ɓ��钇�Ԃ̏d�S�̕����̃x�N�g��
    Vector2D obstacleVec;   //��Q�����痣���x�N�g��

    //��Q���̉�����ŗD�悷��
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

    //���x����
    moveVec.checkSpeed(maxSpeed, minSpeed);

    int newXPos = adjust(xPos + (int)moveVec.getX(), worldXSize);
    int newYPos = adjust(yPos + (int)moveVec.getY(), worldYSize);

    //�s����ɒ��Ԃ���Q��������������ړ�
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
    r.drawPointX$Y$Color(xPos,yPos,(byte)2);//�L���X�g���K�v
    return this;
  }
 
  //�߂����钇�Ԃ��痣��悤�Ƃ���x�N�g�����v�Z
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

  //��Q�����痣��悤�Ƃ���x�N�g�����v�Z
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
  
  //���Ȓ��ԒB�̕��ψړ��x�N�g�����v�Z
  private Vector2D calcAlignmentVec(ArrayList visibleBugs) {
    Vector2D alignmentVec = new Vector2D(0, 0);

    int i;
    for(i=0; i<visibleBugs.size(); i++) {    
      alignmentVec.add( ((Bug)visibleBugs.get(i)).getMoveVec() );
    }

    alignmentVec.times( alignmentWeight / visibleBugs.size() );
    return alignmentVec;
  }

  //���Ȓ��ԒB�̏d�S�֌������x�N�g�����v�Z
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

  //�͈͂���O��Ȃ����W�l��Ԃ�
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
