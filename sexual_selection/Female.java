import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.collections.*;


// 雌個体クラス
public class Female extends Individual {
	// デフォルトコンストラクタ
	public Female(Zone aZone){
		super(aZone);
	}

	// tGene，pGene指定コンストラクタ
	public Female(Zone aZone, int t, int p){
		super(aZone, t, p);
	}
}
