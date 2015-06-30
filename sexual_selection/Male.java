import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.collections.*;

// 雄個体クラス
public class Male extends Individual {
	public boolean married; // married: 今の代でつがいになったかどうか

	// デフォルトコンストラクタ
	public Male(Zone aZone){
		super(aZone);
	}

	// tGene，pGene, deadList指定コンストラクタ
	public Male(Zone aZone, int t, int p){
		super(aZone, t, p);

		married = false;
	}


	// 年をとるとともに，marriedをfalseに戻す
	public void age(){
		super.age();
		married = false;
	}

}
