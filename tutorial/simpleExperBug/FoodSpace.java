import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

/**
 * �a�̐��𐔂��āA�H�אs�����ꂽ�̂��킩��悤�ɂ���B
 * Bug�͉a��H�ׂ�ƁAFoodSpace��bugAte���Ăяo���B
 * �܂��AModelSwarm���a�̐��𒲂ׂ���悤�ɁAgetFood��ǉ����Ă���B
 */
public class FoodSpace extends Discrete2dImpl{
	int food;
	
	public FoodSpace(Zone aZone,int x,int y){
		super(aZone,x,y);
	}
	
	/**
	 * @return �a�̐�
	 */
	public int getFood(){
		return food;
	}
	
	public Object seedFoodWithProb(double seedProb){
		int x,y;
		int xsize,ysize;
		
		food=0;
		xsize=this.getSizeX();
		ysize=this.getSizeY();
		
		for (y = 0; y < ysize; y++)
			for (x = 0; x < xsize; x++)
				if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)
					<seedProb){
					this.putValue$atX$Y(1,x,y);
					food++;
				}
		return this;
	}
	
	/**
	 * �a��H�ׂ�Bug����Ă΂�邱�̃��\�b�h�͋L�^���Ă���a�̐�food���f�N�������g����B
	 */
	public Object bugAte(){
		food--;
		return this;
	}
}
