import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;

/**
 * ModelSwarm��Bug��FoodSpace����Ȃ鐢�E�̃��f���̏ڍׂ����߂�B�����ōs���͎̂���3�_�B<BR>
 * <BR>
 * 1. buildObjects�F���f�����̗l�X�ȃI�u�W�F�N�g�𐶐�����B
 * ����͂��Ă̓��\�b�hmain�ōs���Ă������Ƃł���<BR>
 * <BR>
 * 2. buildActions�F�I�u�W�F�N�g�ɑ��郁�b�Z�[�W����ׂ�B�����������for���Ȃǂōs���̂ł͂Ȃ��A
 * ���肽�����b�Z�[�W����ׂĂ������߂̃f�[�^�\����p���čs���B<BR>
 * <BR>
 * 3. activateIn�FbuildActions�ɂ���č�����X�P�W���[�����A
 * ModelSwarm�̏�ʂō��ꂽ�X�P�W���[����swarm�ƂЂƂɂ���B
 * ���̗�ł́AModelSwarm���g�b�v���x���ł��邩��A�ق��ō��ꂽ���̂͂Ȃ��AactivateIn��null�������ɂ��ČĂяo���B<BR>
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
	 * ���f���̒��ɂ���I�u�W�F�N�g�𐶐�����B
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
	 * ���b�Z�[�W�𑗂肽�����ԂŊi�[����B
	 * �����ō��X�P�W���[���́ABug��step�Ƃ������b�Z�[�W�𑗂�Ƃ������Ƃ��J��Ԃ������̂��̂ł���B
	 * �J��Ԃ��Ԋu��1�Ƃ��邱�ƂŁA���̃X�P�W���[���͉i���ɌJ��Ԃ���邱�ƂɂȂ�B
	 * �v���O������Ctrl-C�Œ�~�����邱�ƂɂȂ�B
	 */
	public Object buildActions(){
		modelSchedule=new ScheduleImpl(this,1);
		try {
			modelSchedule.at$createActionTo$message(0,aBug,
			new Selector(Class.forName("Bug"),"step",false));// Objective-C�̃��\�b�h�̏ꍇ�Atrue
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		return this;
	}
	
	/**
	 * ���̃N���X�ō��ꂽ�X�P�W���[�����A����ʂ̃X�P�W���[���ƌ�������B
	 * activateIn���Ăяo�����ƁA������activate���A���������X�P�W���[����activate����
	 * ���̂悤�ȍ�Ƃɂ���āA�N���X�K�w�̒��Œ�`���ꂽ�X�P�W���[�������f���Ă������Ƃ��ł���B
	 */
	public Activity activateIn(Swarm context){
    	super.activateIn (context);
    	modelSchedule.activateIn(this);
		return getActivity ();
	}
}
