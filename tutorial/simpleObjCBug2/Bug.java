import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;

/**
 * �����ł�Bug�ɐV�����t�B�[���h�i�����ϐ��jFoodSpace��ǉ�����B
 * ���\�b�hsetFoodSpace�ɂ���āABug�͎������A�N�Z�X����FoodSpace��m�邱�Ƃ��ł���B
 * setFoodSpace(f)�ɂ���āAFoodSpace���̂��̂��^������킯�ł͂Ȃ��A
 * FoodSpace�̎Q�Ƃ��^�����邱�Ƃɒ��ӁiC++�Ȃ�setFoodSpace(&f)�Ə����Ƃ���ł���j�B<BR>
 * <BR>
 * ���\�b�hstep�́AFoorSpace�ɃA�N�Z�X���A����������ꏊ�ɉa������Ȃ�i�܂�FoosSpace�̒l��1�j�����H�ׁA
 * ���b�Z�[�W��\�����AFoodSpace�̒l��0�ɂ���B
 */

public class Bug extends SwarmObjectImpl {
	int xPos, yPos;
	int worldXSize, worldYSize;
	FoodSpace foodSpace;
	
	public Bug(Zone aZone){
		super(aZone);
	}
	
	public Object setFoodSpace(FoodSpace f){
		foodSpace=f;
		return this;
	}
	
	public Object setX$Y(int x, int y){
		xPos = x;
		yPos = y;
		System.out.println("I started at X = " + xPos + " Y = " + yPos + "\n");
		return this;
	}
	
	public Object setWorldSizeX$Y(int xSize, int ySize){
		worldXSize=xSize;
		worldYSize=ySize;
		return this;
	}
	
	public Object step(){
		xPos = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		yPos = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		
		xPos = (xPos + worldXSize) % worldXSize;
		yPos = (yPos + worldYSize) % worldYSize;
		
		if (foodSpace.getValueAtX$Y(xPos,yPos) == 1){
			foodSpace.putValue$atX$Y(0,xPos,yPos);
			System.out.println("I found food at X = " + xPos + " Y = " + yPos +"!");
		}
		return this;
	}
}
