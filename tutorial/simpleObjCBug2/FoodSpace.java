import swarm.space.*;
import swarm.*;
import swarm.defobj.*;

/**
 * FoodSpaceの実装は簡単であるが、それはSwarmライブラリの一部Discrete2dimplを継承しているためである。
 * 継承は、次の1行による。<BR>
 * <BR>
 * public class FoodSpace extends Discrete2dImpl<BR>
 * <BR>
 * これによって、FoodSpaceはDiscrete2dImplが持つすべてのフィールドとメソッドを備えることになる。<BR>
 * <BR>
 * Discrete2dimplは格子であり、各セルには整数が格納される。
 * このオブジェクトには、サイズをセットしたり、セルの値を読み書きするためにメソッドが備わっている。
 * 詳しくはAPIリファレンスを参照してほしい。<BR>
 * <BR>
 * FoodSpaceはDiscrete2dimplに新しいメソッドseedFoodWithProbを加えたものである。
 * これは各セルの値を確率で1にするというものである。
 * 1という値は餌があることを意味する。セルのデフォルト値は0で、Bugが餌を食べた後も0になる。
 */

public class FoodSpace extends Discrete2dImpl{
	public FoodSpace(Zone aZone,int x,int y){
		super(aZone,x,y);
	}
	
	public Object seedFoodWithProb(double seedProb){
		int x,y;
		int xsize,ysize;
		
		xsize=this.getSizeX();
		ysize=this.getSizeY();
		
		for (y = 0; y < ysize; y++)
			for (x = 0; x < xsize; x++)
				if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)<seedProb)
					this.putValue$atX$Y(1,x,y);
		return this;
	}
}
