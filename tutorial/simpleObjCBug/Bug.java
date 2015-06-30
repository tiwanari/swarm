import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;

/**
 * クラスはその名前のついた1つのjavaファイルの中で定義される（ここではBug.java）<BR>
 */

public class Bug extends SwarmObjectImpl {
	int xPos, yPos;
	int worldXSize, worldYSize;
	
	/**
	 * @param aZone SwarmのオブジェクトはZoneに生成される
	 */
	public Bug(Zone aZone){
		super(aZone);
	}
	
	/**
	 * 位置を設定するメソッド
	 * @param x x座標
	 * @param y y座標
	 */
	public Object setX$Y(int x, int y){
		xPos = x;
		yPos = y;
		System.out.println("I started at X = " + xPos + " Y = " + yPos + "\n");
		return this;
	}
	
	/**
	 * Bugが住む世界のサイズを設定するメソッド。本来Bugがこれを知る必要はないはず
	 * @param xSize x方向のサイズ
	 * @param ySize y方向のサイズ
	 */
	public Object setWorldSizeX$Y(int xSize, int ySize){
		worldXSize=xSize;
		worldYSize=ySize;
		return this;
	}
	
	/**
	 * ランダムウォークする
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
