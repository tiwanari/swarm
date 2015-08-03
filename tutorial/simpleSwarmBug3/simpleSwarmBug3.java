import swarm.*;

/**
 * �p�����[�^�̃��[�h�E�Z�[�u<BR>
 * <BR>
 * �p�����[�^���t�@�C������ǂݍ��ނ悤����B
 * ����ɂ��A�ݒ肪�ς�邽�тɃR���p�C���������K�v���Ȃ��Ȃ�B<BR>
 * <BR>
 * �t�@�C������p�����[�^��ǂݍ��ނɂ�lispAppArchiver��p����B
 * lispAppArchiver��<appname>.scm�Ƃ����t�@�C�����J�����g�E�f�B���N�g������T���B
 * �����ł�<appname>��bug�ł��邩��A�T�������t�@�C����bug.scm�Ƃ������ƂɂȂ�B
 * ���̃t�@�C������p������@��jmousetrap���Q�l�ɂ��Ăق����B<BR>
 * <BR>
 * �p�����[�^�E�t�@�C���̓��e�͎��̂悤��Lisp��S���ł���B<BR>
 * <BR>
 * <PRE>(list 
 * (cons 'modelSwarm
 *       (make-instance 'ModelSwarm
 *                      #:worldXSize 80
 *                      #:worldYSize 80
 *                      #:seedProb 0.9
 *                      #:bugDensity 0.01)))</PRE>
 * <BR>
 * ModelSwarm�̃C���X�^���X�����̂悤�ɐ�������ꍇ�A�w�肳��Ă���t�B�[���h��public�łȂ���΂Ȃ�Ȃ����Ƃɒ��ӁB<BR>
 * <BR>
 * �����ł͐������Ȃ����AlispAppArchiver��putShallow��putDeep�ɂ���ăt�@�C���Ƀf�[�^�������o�����Ƃ��ł���B<BR>
 * <BR>
 * ����simpleObserverSwarm
 * @author YABUKI Taro
 * @version 0.4
 */

public class simpleSwarmBug3{
	public static void main(String[] args) {
		ModelSwarm modelSwarm;
		
		Globals.env.initSwarm("bug", "0.4", "YABUKI Taro", args);
		
		// bug.scm����ModelSwarm�̃C���X�^���X�𐶐�����B
		modelSwarm = (ModelSwarm)Globals.env.lispAppArchiver.getWithZone$key(Globals.env.globalZone,"modelSwarm");
		
		// �C���X�^���X����������Ȃ������ꍇ
		if(modelSwarm==null){
			System.out.println("Can't find the modelSwarm parameters.");
			System.exit(1);
		}
		// Objective-C�̏ꍇ�̎��̏����͖�����
		//raiseEvent(InvalidOperation,"Can't find the modelSwarm parameters");
		
		modelSwarm.buildObjects();
		modelSwarm.buildActions();
		modelSwarm.activateIn(null);
		modelSwarm.getActivity().run();
	}
}
