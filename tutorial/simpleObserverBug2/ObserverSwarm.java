import swarm.simtoolsgui.*;
import swarm.gui.*;
import swarm.space.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.*;

/**
 * ������Swarm�̃I�u�W�F�N�g�ɃA�N�Z�X�\��Probe���쐬����B
 * Probe�̓I�u�W�F�N�g�̃t�B�[���h�̒l��\���E�ύX������A
 * ���\�b�h���Ăяo�����肷�邽�߂�GUI�ł���B
 * ���ӓ_�Ƃ��āAProbe�ň�����t�B�[���h�⃁�\�b�h��public�Ȃ��̂łȂ���΂Ȃ�Ȃ��B<BR>
 * <BR>
 * �����ł́AOberverSwarm��ModelSwarm�ւ�Probe���N�����ɐ��������悤�ɂ���B
 * ����Ƀ}�E�X�ŃN���b�N���邱�Ƃɂ����Bug�ւ�Probe�����������悤�ɂ���B<BR>
 * <BR>
 * Probe��Globals.env.createArchivedProbeDisplay�ō쐬���邱�Ƃ��ł���B
 * ����ō����Probe�ɂ�public�ȃt�B�[���h�����ׂĂ̂��Ă���B
 * �\������t�B�[���h��I�т����ꍇ��A���\�b�h���̂������ꍇ�ɂ͎��̂悤�ɂ���΂悢�B<BR>
 * 1. ProbeMap�Ƀt�B�[���h�⃁�\�b�h��ǉ�����<BR>
 * 2. ProbeLibrary��ProbeMap��o�^����<BR>
 * 3. Globals.env.createArchivedProbeDisplay<BR>
 * �����ł�ObserverSwarm�ւ�Probe�̓f�t�H���g�̂܂܁A
 * ModelSwarm�ւ�Probe�̓J�X�^�}�C�Y���Ă���B<BR>
 * <BR>
 * �v���O������2��Probe�𐶐��������_�Œ�~����B
 * �����ŁA�V�~�����[�V�����̃p�����[�^�iModelSwarm�̃t�B�[���h�j
 * ���C�����Ă���Start�{�^���������΁A�p�����[�^�t�@�C�������������邱�ƂȂ��A
 * ���܂��܂Ȑݒ���V�~�����[�g���邱�Ƃ��ł���
 * �i�t�B�[���h�̐��l��������������Enter�L�[���������Ɓj�B<BR>
 * <BR>
 * ����2�_���m�F���Ăق����B<BR>
 * 1. Probe��̐��̃I�u�W�F�N�g�����N���b�N����ƁApublic�ȃ��\�b�h�����ׂĕ\�������B<BR>
 * 2. �����ŁA�E��̃{�^�����N���b�N����ƁA�X�[�p�[�N���X��Probe�����������B<BR>
 */
public class ObserverSwarm extends GUISwarmImpl{
	/**
	 * probe�ő���ł���悤�ɂ��邽�߂ɂ�public�łȂ���΂Ȃ�Ȃ�
	 */
	public int displayFrequency; 
	
	ActionGroup displayActions; // schedule data structs
	Schedule displaySchedule;
	ModelSwarm modelSwarm; // the Swarm we're observing
	Colormap colorMap; // allocate colours
	ZoomRaster worldRaster; // 2d display widget
	Value2dDisplay foodDisplay; // display the heat
	Object2dDisplay bugDisplay; // display the heatbugs
	
	/**
	 * �R���X�g���N�^�BObserverSwarm��Probe�̓f�t�H���g�̂܂�
	 * �i���ׂĂ�public�ȃt�B�[���h���\�������j�ł悢���߁A�Ǝ���probeMap�����K�v�͂Ȃ��B
	 * �f�t�H���g��Probe���Č�����悤�ȃR�[�h���R�����g���Ɏ����Ă��邩��Q�l�ɂ��Ăق����B
	 * Probe�̍����́AModelSwarm�̃R���X�g���N�^�����������킩��₷�����낤�B
	 */
	public ObserverSwarm(Zone aZone){
		super(aZone);
		displayFrequency=1;
		
/*		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		// Add in a bunch of variables, one per simulation parameters
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("displayFrequency",this.getClass()));
        
      	// Now install our custom probeMap into the probeLibrary.
        
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
*/	}
	
	public Object buildObjects(){
		super.buildObjects();
		
		modelSwarm = (ModelSwarm)
			Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm"); //�G���[�������ꍇ�̎��̏����͖�����
		if(modelSwarm==null){
			System.out.println("Can't find the modelSwarm parameters.");
			System.exit(1);
		}
		
		// Now create probe objects on the model and ourselves. This gives a
		// simple user interface to let the user change parameters.
		
		Globals.env.createArchivedProbeDisplay (modelSwarm, "modelSwarm");
		Globals.env.createArchivedProbeDisplay (this, "observerSwarm");
		
		// Instruct the control panel to wait for a button event.
		// We halt here until someone hits a control panel button.
		
		// Now that we're using Probes, the user can set the parameters
		// in the ModelSwarm probe window - we halt here to allow
		// the user to change parameters.
		
		getControlPanel().setStateStopped();
		
		modelSwarm.buildObjects();
		
		colorMap=new ColormapImpl(this);
		colorMap.setColor$ToName((byte)0,"black");//�L���X�g���K�v
		colorMap.setColor$ToName((byte)1,"red");
		colorMap.setColor$ToName((byte)2,"green");
		
		worldRaster=new ZoomRasterImpl(this);
		worldRaster.setColormap(colorMap);
		worldRaster.setZoomFactor(4);
		worldRaster.setWidth$Height(
			modelSwarm.getWorld().getSizeX(),
			modelSwarm.getWorld().getSizeY());
		worldRaster.setWindowTitle("Food Space");
		worldRaster.pack(); // draw the window.
		
		foodDisplay=new Value2dDisplayImpl(this,worldRaster,colorMap,modelSwarm.getFood());
		
		try {
			bugDisplay = new Object2dDisplayImpl(
				this,
				worldRaster,
				modelSwarm.getWorld(),
				new Selector(Class.forName("Bug"), "drawSelfOn", false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		bugDisplay.setObjectCollection(modelSwarm.getBugList());
		
		// worldRaster�̏�Ń}�E�X���N���b�N���ꂽ�ꍇ�A
		// bugDisplay�̃��\�b�hmakeProbeAtX$Y���Ăяo���B
		// makeProbeAtX$Y�͂����ɂ���I�u�W�F�N�g�i�܂�Bug�j��Probe�𐶐�����B
		
		try {
			worldRaster.setButton$Client$Message(
				3,bugDisplay,new Selector(bugDisplay.getClass(), "makeProbeAtX$Y",true));
        } catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
        
		return this;
	}
	
	public Object buildActions(){
		super.buildActions();
		modelSwarm.buildActions();
		displayActions=new ActionGroupImpl(this);
		
		try {
			displayActions.createActionTo$message(foodDisplay,
				new Selector(foodDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(bugDisplay,
				new Selector(bugDisplay.getClass(),"display",false));
			displayActions.createActionTo$message(worldRaster,
				new Selector(worldRaster.getClass(),"drawSelf",false));
			displayActions.createActionTo$message(
				getActionCache(),
				new Selector(getActionCache().getClass(),"doTkEvents",false));
		} catch (Exception e) {
			System.out.println ("Exception: " + e.getMessage ());
			System.exit(1);
		}
		
		displaySchedule = new ScheduleImpl(this,displayFrequency);
		displaySchedule.at$createAction(0,displayActions);
		
		return this;
    }
	
	public Activity activateIn(Swarm context){
    	super.activateIn(context);
		modelSwarm.activateIn(this);
		displaySchedule.activateIn(this);
		return getActivity();
	}
}
