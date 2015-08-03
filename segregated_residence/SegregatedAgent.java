/*----------------------------------------------------------------------------
 * import
 *----------------------------------------------------------------------------*/
import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;

/*----------------------------------------------------------------------------
 * class   : SegregatedAgent
 * comment : Bug.javaの転用 (Agentとすべきだろうか)
 *----------------------------------------------------------------------------*/
public class SegregatedAgent extends SwarmObjectImpl {
  int xPos, yPos;
  int worldXSize, worldYSize;
  Grid2d world;

  /* 追加 */
  SegregatedChurchSpace churchSpace;  // 教会空間オブジェクト
  int race;                 // 民族値
  int color;                // 表示色 (民族毎, 博愛主義者, 教会在中で異なる)
  int searchSpace;          // 不満値に関連する
  int raceNum;              // 民族種数
  double unhappiness;       // 不満値
  boolean philanthropism;   // 博愛主義フラグ
  boolean inChurch;         // 教会内/外フラグ
  
  /**
   * コンストラクタ */
  public SegregatedAgent(Zone aZone, int raceNum, double seedPhilan){
    super(aZone);

    // 民族をランダムに決定
    color = race = Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,raceNum);

    // 博愛主義/非博愛主義をランダムに決定
    if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < seedPhilan) {
      // 色も変える (明るくする)
      color += 3;
      philanthropism = true;
    } else {
      philanthropism = false;
    }

    searchSpace = 1;
    unhappiness = 0;
    inChurch=false;
  }
  
  /**
   * 空間と教会空間, 更にそれらの広さを取得 */
  public Object setWorld$Space(Grid2d w,SegregatedChurchSpace c){
    world=w;
    churchSpace=c;
    worldXSize = world.getSizeX();//とりあえずここに書いておく。
    worldYSize = world.getSizeY();
    return this;
  }
  
  /**
   * 初期位置設定 */
  public Object setX$Y(int x, int y){
    xPos = x;
    yPos = y;
    return this;
  }

  /**
   * 1ステップ毎の処理 */
  public void step(){
    int newX, newY;     // 新しい位置
    int churchValue=0;  // 教会値 (ある/なし, あるならどの民族の教会か)
    double newUnhappiness = 0; // 不満値 (負なら満足)
    SegregatedAgent aSegregatedAgent;
    
    // 移動するマスをランダムに決定
    do {
      newX = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
      newY = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
    } while( ( (xPos == newX) && (yPos == newY) ) && inChurch);
    
    // 空間を越えた場合は反対側に移動
    newX = (newX + worldXSize) % worldXSize;
    newY = (newY + worldYSize) % worldYSize;
    
    // 移動先マスの周囲8マスを探索
    for (int i = ( newX - searchSpace ); i<= ( newX + searchSpace ); i++) {
      int sx = (i + worldXSize) % worldXSize;
      for (int j = ( newY - searchSpace ); j<= ( newY + searchSpace ); j++) {
        int sy = (j + worldYSize) % worldYSize;

        // 不満値計算 (人)
        if( ( i != xPos ) || ( j != yPos ) ) { // 今いるマス以外 (自分を含めない)

          // 人がいる
          if( ( aSegregatedAgent = (SegregatedAgent)world.getObjectAtX$Y( sx, sy ) ) != null) {

            float fValue = 1;

            // プロパティ毎に処理
            if ( philanthropism == true ) {           // 博愛主義
              newUnhappiness -= fValue;
            } else if ( aSegregatedAgent.getRace() != race ) { // 異民族
              newUnhappiness += fValue;
            } else if ( aSegregatedAgent.getRace() == race ) { // 同じ民族
              newUnhappiness -= fValue;
            }
          }
        }

        // 不満値計算 (教会)
        if( ( i != newX ) || ( j != newY ) ) { // 移動先以外

          if ( (churchValue = churchSpace.getValueAtX$Y( sx, sy )) != 0) {
            float fValue = 1;
            churchValue %= 3;
            newUnhappiness += (float)((churchValue == (race % 3)) ? -1: 1);
          }
        } else { // 移動先

          // 教会がある
          if ( (churchValue = churchSpace.getValueAtX$Y(newX, newY)) != 0) {
            churchValue %= 3;
            newUnhappiness += (churchValue == (race % 3)) ? -3: +3;
          }
        }
      }
    }

    // 誰もいなければ移動
    if (world.getObjectAtX$Y(newX, newY) == null){

      // 不満値が少ない場合, または教会にいる場合
      if( (newUnhappiness <= unhappiness)  || inChurch ) {
        world.putObject$atX$Y(null,xPos,yPos);
        xPos = newX;
        yPos = newY;
        unhappiness = newUnhappiness;
        world.putObject$atX$Y(this,newX,newY);
      }
    }

    // 教会にいる場合といない場合で色を変化させる
    if ( churchSpace.getValueAtX$Y(xPos, yPos) != 0) {
      inChurch=true;
      color=13;  // white
    } else {
      inChurch=false;
      color=race + (philanthropism ? 3: 0); // 規定色
    }
  }

  /**
   * 民族種を取得 */
  public int getRace() {
    return race;
  }
  
  /**
   * 不満値を取得 */
  public double getUnhappiness() {
    return unhappiness;
  }
  
  public Object drawSelfOn(Raster r){
    r.drawPointX$Y$Color(xPos,yPos,(byte)color);//キャストが必要
    return this;
  }
}

/*----------------------------------------------------------------------------
 * end of file
 *----------------------------------------------------------------------------*/
