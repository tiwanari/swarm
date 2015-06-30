/*----------------------------------------------------------------------------
 * import
 *----------------------------------------------------------------------------*/
import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

/*----------------------------------------------------------------------------
 * class   : SegregatedChurchSpace
 * comment : FoodSpace��]�p
 *----------------------------------------------------------------------------*/
public class SegregatedChurchSpace extends Discrete2dImpl{

  /**
   * �R���X�g���N�^ */
  public SegregatedChurchSpace(Zone aZone,int x,int y){
    super(aZone,x,y);
  }

  /**
   * 2������Ԃɋ����z�u���� */
  public Object seedChurchWithProb(double seedChurch, int a, int z){
    int x,y;
    int xsize,ysize;
    
    xsize=this.getSizeX();
    ysize=this.getSizeY();
    
    for (y = 0; y < ysize; y++)
      for (x = 0; x < xsize; x++)
        if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)
        <seedChurch)
          this.putValue$atX$Y(
            Globals.env.uniformIntRand.getIntegerWithMin$withMax(a,z),
            x,y);
    
    return this;
  }
}

/*----------------------------------------------------------------------------
 * end of file
 *----------------------------------------------------------------------------*/
