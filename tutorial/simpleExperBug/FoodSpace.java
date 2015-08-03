import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

/**
 * 餌の数を数えて、食べ尽くされたのがわかるようにする。
 * Bugは餌を食べると、FoodSpaceのbugAteを呼び出す。
 * また、ModelSwarmが餌の数を調べられるように、getFoodを追加している。
 */
public class FoodSpace extends Discrete2dImpl{
	int food;
	
	public FoodSpace(Zone aZone,int x,int y){
		super(aZone,x,y);
	}
	
	/**
	 * @return 餌の数
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
	 * 餌を食べたBugから呼ばれるこのメソッドは記録してある餌の数foodをデクリメントする。
	 */
	public Object bugAte(){
		food--;
		return this;
	}
}
