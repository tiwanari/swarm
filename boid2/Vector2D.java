public class Vector2D {
  private float x;
  private float y;

  public Vector2D(float x, float y) {
    this.x = x;
    this.y = y;
  }

  //$B:GDc!&:G9bB.EY@)8B$r$+$1$k(B
  public void checkSpeed(float maxSpeed, float minSpeed) {
    //$BB.EY$,#0$N$H$-$O%i%s%@%`$JJ}8~$K(B
    if( x==0.0f && y==0.0f ) {
      toRandomVec(maxSpeed, minSpeed);
    }
    
    float nowSpeed = x*x + y*y;
    
    if(nowSpeed < minSpeed) {
      toUnitVec();
      times(minSpeed);
    }
    else if(nowSpeed > maxSpeed) {
      toUnitVec();
      times(maxSpeed);
    }
  }

  //$B%Y%/%H%k$r%i%s%@%`$K$9$k(B
  public void toRandomVec(float maxSpeed, float minSpeed) {
    x = (float)Math.random() * (maxSpeed - minSpeed) + minSpeed;
    y = (float)Math.random() * (maxSpeed - minSpeed) + minSpeed;

    if( Math.random() > 0.5 ) {
      x = -x;
    }
    if( Math.random() > 0.5 ) {
      y = -y;
    }
  }

  //$BC10L%Y%/%H%k2=$9$k(B
  public void toUnitVec() {
    if( x == 0 ) {
      y = 1.0f;
    }
    else if ( y == 0 ) {
      x = 1.0f;
    }
    else {
      float length = (float)Math.sqrt(x*x + y*y);
      x /= length;
      y /= length;
    }
  }

  //$B1&#9#0EY2sE>$7$?%Y%/%H%k$rJV$9(B
  public Vector2D getRightVec() {
    return new Vector2D(y, -x);
  }

  //$B:8#9#0EY2sE>$7$?%Y%/%H%k$rJV$9(B
  public Vector2D getLeftVec() {
    return new Vector2D(-y, x);
  }
  
  //$B%Y%/%H%k$N3QEY$rJV$9(B
  public float getDeg() {
    double deg = Math.atan((double)(y/x));
    if( x < 0 ) {
      if( y > 0 ) {
	deg = Math.PI + deg;
      }
      else {
	deg = -Math.PI - deg;
      }
    }
    return (float)deg;
  }

  public void add(Vector2D anotherVec) {
    this.x += anotherVec.getX();
    this.y += anotherVec.getY();
  }
  
  public void times(float value) {
    this.x *= value;
    this.y *= value;
  }
  
  public float getX() {
    if( Float.isNaN(x) ) {
      x = 0.0f;
    }
    return x;
  }

  public float getY() {
    if( Float.isNaN(y) ) {
      y = 0.0f;
    }
    return y;
  }

  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
