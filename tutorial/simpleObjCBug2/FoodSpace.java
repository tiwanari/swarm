import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

/**
 * FoodSpace�̎����͊ȒP�ł��邪�A�����Swarm���C�u�����̈ꕔDiscrete2dimpl���p�����Ă��邽�߂ł���B
 * �p���́A����1�s�ɂ��B<BR>
 * <BR>
 * public class FoodSpace extends Discrete2dImpl<BR>
 * <BR>
 * ����ɂ���āAFoodSpace��Discrete2dImpl�������ׂẴt�B�[���h�ƃ��\�b�h������邱�ƂɂȂ�B<BR>
 * <BR>
 * Discrete2dimpl�͊i�q�ł���A�e�Z���ɂ͐������i�[�����B
 * ���̃I�u�W�F�N�g�ɂ́A�T�C�Y���Z�b�g������A�Z���̒l��ǂݏ������邽�߂Ƀ��\�b�h��������Ă���B
 * �ڂ�����API���t�@�����X���Q�Ƃ��Ăق����B<BR>
 * <BR>
 * FoodSpace��Discrete2dimpl�ɐV�������\�b�hseedFoodWithProb�����������̂ł���B
 * ����͊e�Z���̒l���m����1�ɂ���Ƃ������̂ł���B
 * 1�Ƃ����l�͉a�����邱�Ƃ��Ӗ�����B�Z���̃f�t�H���g�l��0�ŁABug���a��H�ׂ����0�ɂȂ�B
 */

public class FoodSpace extends Discrete2dImpl{
	public FoodSpace(Zone aZone,int x,int y){
		super(aZone,x,y);
	}
	
	public Object seedFoodWithProb(double seedProb){
		int x,y;
		int xsize,ysize;
		
		xsize=this.getSizeX();
		ysize=this.getSizeY();
		
		for (y = 0; y < ysize; y++)
			for (x = 0; x < xsize; x++)
				if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)<seedProb)
					this.putValue$atX$Y(1,x,y);
		return this;
	}
}
