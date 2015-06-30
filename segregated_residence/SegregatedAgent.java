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
 * comment : Bug.java�̓]�p (Agent�Ƃ��ׂ����낤��)
 *----------------------------------------------------------------------------*/
public class SegregatedAgent extends SwarmObjectImpl {
  int xPos, yPos;
  int worldXSize, worldYSize;
  Grid2d world;

  /* �ǉ� */
  SegregatedChurchSpace churchSpace;  // �����ԃI�u�W�F�N�g
  int race;                 // �����l
  int color;                // �\���F (������, ������`��, ����ݒ��ňقȂ�)
  int searchSpace;          // �s���l�Ɋ֘A����
  int raceNum;              // �����퐔
  double unhappiness;       // �s���l
  boolean philanthropism;   // ������`�t���O
  boolean inChurch;         // �����/�O�t���O
  
  /**
   * �R���X�g���N�^ */
  public SegregatedAgent(Zone aZone, int raceNum, double seedPhilan){
    super(aZone);

    // �����������_���Ɍ���
    color = race = Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,raceNum);

    // ������`/�񔎈���`�������_���Ɍ���
    if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < seedPhilan) {
      // �F���ς��� (���邭����)
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
   * ��ԂƋ�����, �X�ɂ����̍L�����擾 */
  public Object setWorld$Space(Grid2d w,SegregatedChurchSpace c){
    world=w;
    churchSpace=c;
    worldXSize = world.getSizeX();//�Ƃ肠���������ɏ����Ă����B
    worldYSize = world.getSizeY();
    return this;
  }
  
  /**
   * �����ʒu�ݒ� */
  public Object setX$Y(int x, int y){
    xPos = x;
    yPos = y;
    return this;
  }

  /**
   * 1�X�e�b�v���̏��� */
  public void step(){
    int newX, newY;     // �V�����ʒu
    int churchValue=0;  // ����l (����/�Ȃ�, ����Ȃ�ǂ̖����̋��)
    double newUnhappiness = 0; // �s���l (���Ȃ疞��)
    SegregatedAgent aSegregatedAgent;
    
    // �ړ�����}�X�������_���Ɍ���
    do {
      newX = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
      newY = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
    } while( ( (xPos == newX) && (yPos == newY) ) && inChurch);
    
    // ��Ԃ��z�����ꍇ�͔��Α��Ɉړ�
    newX = (newX + worldXSize) % worldXSize;
    newY = (newY + worldYSize) % worldYSize;
    
    // �ړ���}�X�̎���8�}�X��T��
    for (int i = ( newX - searchSpace ); i<= ( newX + searchSpace ); i++) {
      int sx = (i + worldXSize) % worldXSize;
      for (int j = ( newY - searchSpace ); j<= ( newY + searchSpace ); j++) {
        int sy = (j + worldYSize) % worldYSize;

        // �s���l�v�Z (�l)
        if( ( i != xPos ) || ( j != yPos ) ) { // ������}�X�ȊO (�������܂߂Ȃ�)

          // �l������
          if( ( aSegregatedAgent = (SegregatedAgent)world.getObjectAtX$Y( sx, sy ) ) != null) {

            float fValue = 1;

            // �v���p�e�B���ɏ���
            if ( philanthropism == true ) {           // ������`
              newUnhappiness -= fValue;
            } else if ( aSegregatedAgent.getRace() != race ) { // �ٖ���
              newUnhappiness += fValue;
            } else if ( aSegregatedAgent.getRace() == race ) { // ��������
              newUnhappiness -= fValue;
            }
          }
        }

        // �s���l�v�Z (����)
        if( ( i != newX ) || ( j != newY ) ) { // �ړ���ȊO

          if ( (churchValue = churchSpace.getValueAtX$Y( sx, sy )) != 0) {
            float fValue = 1;
            churchValue %= 3;
            newUnhappiness += (float)((churchValue == (race % 3)) ? -1: 1);
          }
        } else { // �ړ���

          // �������
          if ( (churchValue = churchSpace.getValueAtX$Y(newX, newY)) != 0) {
            churchValue %= 3;
            newUnhappiness += (churchValue == (race % 3)) ? -3: +3;
          }
        }
      }
    }

    // �N�����Ȃ���Έړ�
    if (world.getObjectAtX$Y(newX, newY) == null){

      // �s���l�����Ȃ��ꍇ, �܂��͋���ɂ���ꍇ
      if( (newUnhappiness <= unhappiness)  || inChurch ) {
        world.putObject$atX$Y(null,xPos,yPos);
        xPos = newX;
        yPos = newY;
        unhappiness = newUnhappiness;
        world.putObject$atX$Y(this,newX,newY);
      }
    }

    // ����ɂ���ꍇ�Ƃ��Ȃ��ꍇ�ŐF��ω�������
    if ( churchSpace.getValueAtX$Y(xPos, yPos) != 0) {
      inChurch=true;
      color=13;  // white
    } else {
      inChurch=false;
      color=race + (philanthropism ? 3: 0); // �K��F
    }
  }

  /**
   * ��������擾 */
  public int getRace() {
    return race;
  }
  
  /**
   * �s���l���擾 */
  public double getUnhappiness() {
    return unhappiness;
  }
  
  public Object drawSelfOn(Raster r){
    r.drawPointX$Y$Color(xPos,yPos,(byte)color);//�L���X�g���K�v
    return this;
  }
}

/*----------------------------------------------------------------------------
 * end of file
 *----------------------------------------------------------------------------*/
