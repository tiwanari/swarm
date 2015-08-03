import swarm.*;

/**
 * パラメータのロード・セーブ<BR>
 * <BR>
 * パラメータをファイルから読み込むようする。
 * これにより、設定が変わるたびにコンパイルし直す必要がなくなる。<BR>
 * <BR>
 * ファイルからパラメータを読み込むにはlispAppArchiverを用いる。
 * lispAppArchiverは<appname>.scmというファイルをカレント・ディレクトリから探す。
 * ここでは<appname>はbugであるから、探索されるファイルはbug.scmということになる。
 * 他のファイル名を用いる方法はjmousetrapを参考にしてほしい。<BR>
 * <BR>
 * パラメータ・ファイルの内容は次のようなLispのS式である。<BR>
 * <BR>
 * <PRE>(list 
 * (cons 'modelSwarm
 *       (make-instance 'ModelSwarm
 *                      #:worldXSize 80
 *                      #:worldYSize 80
 *                      #:seedProb 0.9
 *                      #:bugDensity 0.01)))</PRE>
 * <BR>
 * ModelSwarmのインスタンスをこのように生成する場合、指定されているフィールドはpublicでなければならないことに注意。<BR>
 * <BR>
 * ここでは説明しないが、lispAppArchiverはputShallowやputDeepによってファイルにデータを書き出すこともできる。<BR>
 * <BR>
 * 次はsimpleObserverSwarm
 * @author YABUKI Taro
 * @version 0.4
 */

public class simpleSwarmBug3{
	public static void main(String[] args) {
		ModelSwarm modelSwarm;
		
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		// bug.scmからModelSwarmのインスタンスを生成する。
		modelSwarm = (ModelSwarm)Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm");
		
		// インスタンスが生成されなかった場合
		if(modelSwarm==null){
			System.out.println("Can't find the modelSwarm parameters.");
			System.exit(1);
		}
		// Objective-Cの場合の次の処理は未実装
		//raiseEvent(InvalidOperation,"Can't find the modelSwarm parameters");
		
		modelSwarm.buildObjects();
		modelSwarm.buildActions();
		modelSwarm.activateIn(null);
		modelSwarm.getActivity().run();
	}
}
