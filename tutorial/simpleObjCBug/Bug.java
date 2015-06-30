import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;

/**
 * �N���X�͂��̖��O�̂���1��java�t�@�C���̒��Œ�`�����i�����ł�Bug.java�j<BR>
 */

public class Bug extends SwarmObjectImpl {
	int xPos, yPos;
	int worldXSize, worldYSize;
	
	/**
	 * @param aZone Swarm�̃I�u�W�F�N�g��Zone�ɐ��������
	 */
	public Bug(Zone aZone){
		super(aZone);
	}
	
	/**
	 * �ʒu��ݒ肷�郁�\�b�h
	 * @param x x���W
	 * @param y y���W
	 */
	public Object setX$Y(int x, int y){
		xPos = x;
		yPos = y;
		System.out.println("I started at X = " + xPos + " Y = " + yPos + "\n");
		return this;
	}
	
	/**
	 * Bug���Z�ސ��E�̃T�C�Y��ݒ肷�郁�\�b�h�B�{��Bug�������m��K�v�͂Ȃ��͂�
	 * @param xSize x�����̃T�C�Y
	 * @param ySize y�����̃T�C�Y
	 */
	public Object setWorldSizeX$Y(int xSize, int ySize){
		worldXSize=xSize;
		worldYSize=ySize;
		return this;
	}
	
	/**
	 * �����_���E�H�[�N����
	 */
	public Object step(){
		xPos = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		yPos = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
		
		xPos = (xPos + worldXSize) % worldXSize;
		yPos = (yPos + worldYSize) % worldYSize;
		
		System.out.println("I moved to X = " + xPos + " Y = " + yPos);
		return this;
	}
}
