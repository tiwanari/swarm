import swarm.*;

/**
 * �a���܂��ꂽ2�����O���b�h���Bug���������<BR>
 * <BR>
 * Bug��FoodSpace�Ƃ���2�����O���b�h�������B
 * FoodSpace�̊e�O���b�h�ɂ�seedProb�̊m���ŉa���܂���Ă���B
 * Bug�͉a��������Ƃ����H�ׂ�B<BR>
 * <BR>
 * FoodSpace���I�u�W�F�N�g�ł���AsimpleObjCBug2����new�ɂ���č����B
 * �������ꂽFoodSpace�ɂ́A���\�b�hseedFoodWithProb�ɂ���ĉa���܂����B<BR>
 * <BR>
 * Bug��simpleObjCBug�̎��Ɠ��l�ɍ����B
 * �������ꂽBug�́A���\�b�hsetFoodSpace�ɂ���Ď�����FoodSpace��m��B
 * Bug�̃��C���E���\�b�h��step�ł��邪�A���̃��\�b�h��FoodSpace�ɃA�N�Z�X����悤�ɏC������Ă���B<BR>
 * <BR>
 * <IMG src="../simpleObjCBug2.png" border="0"><BR>
 * <BR>
 * ����simpleSwarmBug
 * @author YABUKI Taro
 * @version 0.4
 */

public class simpleObjCBug2{
	public static void main(String[] args) {
		int worldXSize = 80;
		int worldYSize = 80;
		
		int xPos = 40;
		int yPos = 40;
		
		double seedProb=0.5; // Density of distribution of "food"
		
		int i;
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		// Create and initialize a "food" space
		
		FoodSpace foodSpace=new FoodSpace(Globals.env.globalZone,worldXSize,worldYSize);
		
		foodSpace.seedFoodWithProb(seedProb);
		
		Bug aBug=new Bug(Globals.env.globalZone);
		aBug.setFoodSpace(foodSpace);
		aBug.setWorldSizeX$Y(worldXSize,worldYSize);
		
		aBug.setX$Y(xPos,yPos);
		
		for(i = 0; i < 100; ++i)
			aBug.step();
	}
}
