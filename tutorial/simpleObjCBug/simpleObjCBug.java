import swarm.*;

/**
 * �I�u�W�F�N�g�w���ɂ��L�q<BR>
 * <BR>
 * simpleCBug���I�u�W�F�N�g�w���ŏ��������Ƃ��̂悤�ɂȂ�B
 * simpleObjCBug��ObjC��Objective-C�̂��ƁiC������I�u�W�F�N�g����Ƃ��Ċg�������̂�Objective-C�B
 * �����ŗp���Ă���̂�Java�Ȃ̂����A�Â������K�����c���Ă���j�B
 * �I�u�W�F�N�gBug���`����̂́ABug.java�ł���B<BR>
 * <BR>
 * main method�ɂ̓I�u�W�F�N�gBug�𐶐����邽�߂̕�new Bug������B
 * ����́A�N���XBug�̃C���X�^���X�i�I�u�W�F�N�g�j�𐶐����邽�߂̂��̂ł���B<BR>
 * <BR>
 * Bug�̃��\�b�h�Ăяo��<BR>
 *              aBug.setX$Y(xPos,yPos);<BR>
 * �́ABug�̃t�B�[���h�i�����ϐ��j��ݒ肷�邽�߂̂��̂ł���B<BR>
 * <BR>
 * <IMG src="../simpleObjCBug.png" border="0"><BR>
 * <BR>
 * ����simpleObjCBug2
 * @author YABUKI Taro
 * @version 0.4
 */

public class simpleObjCBug{
	public static void main(String[] args) {
		int worldXSize = 80;
		int worldYSize = 80;
		
		int xPos = 40;
		int yPos = 40;
		
		int i;
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		// Make us aBug please!
		Bug aBug=new Bug(Globals.env.globalZone);
		
		// and initialize it
		aBug.setX$Y(xPos,yPos);
		aBug.setWorldSizeX$Y(worldXSize,worldYSize);
		
		for(i = 0; i < 100; ++i)
			aBug.step(); // Tell it to act
	}
}
