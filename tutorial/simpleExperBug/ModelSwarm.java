import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

/**
 * ExperSwarm�i���m�ɂ�ParameterManager�j���烂�f���̃p�����[�^��ݒ�ł���悤�ɁA
 * ���\�b�h���������ǉ������B
 * ����ɁA�a�����H�אs�����ꂽ���𒲂ׂ邽�߂ɁA���Ԃ��v��悤�ɂ����B
 */
public class ModelSwarm extends SwarmImpl{
	public int worldXSize, worldYSize;
	
	public double seedProb;
	public double bugDensity;
	
	int time;
	
	FoodSpace foodSpace;
	Grid2d world;
	
	List bugList;
	ActionGroup modelActions;
	Schedule modelSchedule;
	
	public List bugList(){
		return bugList;
	}
	
	public Grid2d getWorld(){
		return world;
	}
	
	public FoodSpace getFoodSpace(){
		return foodSpace;
	}

	/**
	 * @return �^�C���X�e�b�v��
	 */
	public int getTime(){
		return time;
	}
	
	public Object setWorldXSize$YSize(int x,int y){
		worldXSize=x;
		worldYSize=y;
		return this;
	}
	
	/**
	 * ExperSwarm����p�����[�^��ݒ肷�邽�߂̃��\�b�h
	 * @param s �a�̖��xseedProb
	 * @param b Bug�̖��xbugDensity
	 */
	public Object setSeedProb$bugDensity(double s,double b){
		seedProb=s;
		bugDensity=b;
		return this;
	}
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		worldXSize = 80;
		worldYSize = 80;
		seedProb   = 0.5;
		bugDensity = 0.1;
	}
	
	public Object buildObjects(){
		Bug aBug;
		int x,y;
		
		foodSpace=new FoodSpace(this,worldXSize,worldYSize);
		foodSpace.seedFoodWithProb(seedProb);
		
		world=new Grid2dImpl(this,worldXSize,worldYSize);
		world.fillWithObject(null);
		
		bugList=new ListImpl(this);
		
		for (y = 0; y < worldYSize; y++){
			for (x = 0; x < worldXSize; x++){
				if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0) < bugDensity){
					aBug=new Bug(this);
					aBug.setWorld$Food(world,foodSpace);
					aBug.setX$Y(x,y);
					bugList.addLast(aBug);
				}
			}
		}
		
		time=0;
		
		return this;
	}
	
	public Object buildActions(){
		System.out.println("creating Actions\n");
		modelActions=new ActionGroupImpl(this);
		try{
			modelActions.createActionForEach$message(bugList,
				new Selector(Class.forName("Bug"),"step",false));
			modelActions.createActionTo$message(this,
				new Selector(this.getClass(),"checkToStop",false));
		} catch (Exception e) {
			e.printStackTrace (System.err);
			System.exit(1);
		}
		
		modelSchedule=new ScheduleImpl(this,1);
		modelSchedule.at$createAction(0,modelActions);
		return this;
	}
	
	public Activity activateIn(Swarm context){
		super.activateIn(context);
    	modelSchedule.activateIn(this);
		return getActivity();
	}
	
	/**
	 * �a�̐��𒲂ׁA�H�אs������Ă�����ModelSwarm���~����B
	 */
	public Object checkToStop(){
		
		// If the bugs have eaten all the food, the model run is over.
		
		if(foodSpace.getFood()<=0){
			this.getActivity().terminate();
			System.out.println("ModelSwarm terminated.");
		}
		
		// if not, increment time and continue running the model
		
		time++;
		return this;
	}
	
	/**
	 * ModelSwarm�̃X�[�p�[�N���X�ł���SwarmImpl�̃C���^�[�t�F�[�Xswarm.defobj.Drop
	 * �̃��\�b�h<a href="http://www.santafe.edu/projects/swarm/swarmdocs/refbook-java/swarm/defobj/Drop.html#drop()">
	 * drop<\a>���I�[�o�[���[�h����B
	 */
	public void drop(){
		foodSpace.drop();
		world.drop();
		super.drop();
	}
}
