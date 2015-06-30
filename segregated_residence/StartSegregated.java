/*----------------------------------------------------------------------------
 * import
 *----------------------------------------------------------------------------*/
import swarm.*;

/*----------------------------------------------------------------------------
 * class   : StartSegregated
 * comment : simpleObserverBug2��]�p
 *----------------------------------------------------------------------------*/
/**
 * �g�[�}�X�E�V�F�����O�̕������f���Ɂu����v�������Ċg���������f��<BR>
 * �ɒ�Ďu, "���G�n�̃V�~�����[�V���� -Swarm�ɂ��}���`�G�[�W�F���g�E�V�X�e��-", �R���i��, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author ���c�ǉ�
 **/
public class StartSegregated {
  public static void main(String[] args) {
    Globals.env.initSwarm("seg", "2.2", "Iba Lab.", args);
    
    if(Globals.env.guiFlag) {
      SegregatedObserverSwarm topLevelSwarm = 
        new SegregatedObserverSwarm(Globals.env.globalZone);
      Globals.env.setWindowGeometryRecordName (topLevelSwarm, "topLevelSwarm");
      if (topLevelSwarm.buildObjects() != null) {
        topLevelSwarm.buildActions();
        topLevelSwarm.activateIn(null);
        topLevelSwarm.go();
      }
      topLevelSwarm.drop();
    } else {

      SegregatedBatchSwarm topLevelSwarm =
        new SegregatedBatchSwarm (Globals.env.globalZone);
      topLevelSwarm.buildObjects ();
      topLevelSwarm.buildActions ();
      topLevelSwarm.activateIn (null);
      topLevelSwarm.go ();
      topLevelSwarm.drop ();
    }
  }
}

/*----------------------------------------------------------------------------
 * end of file
 *----------------------------------------------------------------------------*/
