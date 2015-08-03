public class Vector2D {
  private float x;
  private float y;

  public Vector2D(float x, float y) {
    this.x = x;
    this.y = y;
  }

  //最低・最高速度制限をかける
  public void checkSpeed(float maxSpeed, float minSpeed) {
    //速度が０のときはランダムな方向に
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

  //ベクトルをランダムにする
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

  //単位ベクトル化する
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

  //右９０度回転したベクトルを返す
  public Vector2D getRightVec() {
    return new Vector2D(y, -x);
  }

  //左９０度回転したベクトルを返す
  public Vector2D getLeftVec() {
    return new Vector2D(-y, x);
  }
  
  //ベクトルの角度を返す
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
