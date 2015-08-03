import swarm.*;

/**
 * �ŏ��̃`���[�g���A��<BR>
 * <BR>
 * �����ɂ͓���Swarm�炵�����Ƃ͂Ȃ��iSwarm�̗����������[�`�����g���Ă͂��邪�j<BR>
 * simpleCBug��C��C����̂��ƁBC����̂悤�ɃI�u�W�F�N�g�w���łȂ������������Ă��邩��<BR>
 * <BR>
 * <IMG src="../simpleCBug.png" border="0"><BR>
 * <BR>
 * ����simpleObjcBug
 * @author YABUKI Taro
 * @version 0.4
 */

public class simpleCBug{
	public static void main(String[] args) {
		int worldXSize = 80; // Maximum X value
		int worldYSize = 80; // Maximum Y value
		
		int xPos = 40; // Bug's starting position
		int yPos = 40;
		
		int i;
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);  // Always first in Swarm main
		
		System.out.println("I started at X = " + xPos + " Y = " + yPos + "\n");
		
		for(i = 0; i < 100; ++i){
			// Random movement in X and Y (possibly 0)
			
			xPos = xPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
			yPos = yPos + Globals.env.uniformIntRand.getIntegerWithMin$withMax(-1,1);
			
			// Take move modulo maximum coordinate values
			xPos = (xPos + worldXSize) % worldXSize;
			yPos = (yPos + worldYSize) % worldYSize;
			
			System.out.println("I moved to X = " + xPos + " Y = " + yPos);
		}
	}
}
