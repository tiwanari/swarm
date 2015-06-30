import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;

/**
 * ModelSwarmがBugとFoodSpaceからなる世界のモデルの詳細を決める。ここで行うのは次の3点。<BR>
 * <BR>
 * 1. buildObjects：モデル中の様々なオブジェクトを生成する。
 * これはかつてはメソッドmainで行っていたことである<BR>
 * <BR>
 * 2. buildActions：オブジェクトに送るメッセージを並べる。ただしこれはfor文などで行うのではなく、
 * 送りたいメッセージを並べておくためのデータ構造を用いて行う。<BR>
 * <BR>
 * 3. activateIn：buildActionsによって作ったスケジュールを、
 * ModelSwarmの上位で作られたスケジュールやswarmとひとつにする。
 * この例では、ModelSwarmがトップレベルであるから、ほかで作られたものはなく、activateInはnullを引数にして呼び出す。<BR>
 */

public class ModelSwarm extends SwarmImpl{
	int worldXSize, worldYSize;
	int xPos, yPos;
	double seedProb;
	FoodSpace foodSpace;
	Bug aBug;
	Schedule modelSchedule;
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		// Fill in various simulation parameters with default values.
		worldXSize = 80;
		worldYSize = 80;
		seedProb = 0.1;
		xPos = 40;
		yPos = 40;
	}
	
	/**
	 * モデルの中にあるオブジェクトを生成する。
	 */
	public Object buildObjects(){
		foodSpace=new FoodSpace(Globals.env.globalZone,worldXSize,worldYSize);
		foodSpace.seedFoodWithProb(seedProb);
		
		aBug=new Bug(Globals.env.globalZone);
		aBug.setFoodSpace(foodSpace);
		aBug.setWorldSizeX$Y(worldXSize,worldYSize);
		aBug.setX$Y(xPos,yPos);
		return this;
	}
	
	/**
	 * メッセージを送りたい順番で格納する。
	 * ここで作るスケジュールは、Bugにstepというメッセージを送るということを繰り返すだけのものである。
	 * 繰り返し間隔を1とすることで、このスケジュールは永遠に繰り返されることになる。
	 * プログラムはCtrl-Cで停止させることになる。
	 */
	public Object buildActions(){
		modelSchedule=new ScheduleImpl(this,1);
		try {
			modelSchedule.at$createActionTo$message(0,aBug,
			new Selector(Class.forName("Bug"),"step",false));// Objective-Cのメソッドの場合、true
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		return this;
	}
	
	/**
	 * このクラスで作られたスケジュールを、より上位のスケジュールと結合する。
	 * activateInが呼び出されると、自分をactivateし、自分が持つスケジュールをactivateする
	 * このような作業によって、クラス階層の中で定義されたスケジュールを横断していくことができる。
	 */
	public Activity activateIn(Swarm context){
    	super.activateIn (context);
    	modelSchedule.activateIn(this);
		return getActivity ();
	}
}
