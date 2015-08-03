import swarm.*;

/**
 * オブジェクト指向による記述<BR>
 * <BR>
 * simpleCBugをオブジェクト指向で書き直すとこのようになる。
 * simpleObjCBugのObjCはObjective-Cのこと（C言語をオブジェクト言語として拡張したのがObjective-C。
 * ここで用いているのはJavaなのだが、古い命名規則を残している）。
 * オブジェクトBugを定義するのは、Bug.javaである。<BR>
 * <BR>
 * main methodにはオブジェクトBugを生成するための文new Bugがある。
 * これは、クラスBugのインスタンス（オブジェクト）を生成するためのものである。<BR>
 * <BR>
 * Bugのメソッド呼び出し<BR>
 *              aBug.setX$Y(xPos,yPos);<BR>
 * は、Bugのフィールド（内部変数）を設定するためのものである。<BR>
 * <BR>
 * <IMG src="../simpleObjCBug.png" border="0"><BR>
 * <BR>
 * 次はsimpleObjCBug2
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
