/*----------------------------------------------------------------------------
 * import
 *----------------------------------------------------------------------------*/
import swarm.*;

/*----------------------------------------------------------------------------
 * class   : StartSegregated
 * comment : simpleObserverBug2を転用
 *----------------------------------------------------------------------------*/
/**
 * トーマス・シェリングの分居モデルに「教会」を加えて拡張したモデル<BR>
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author 金田良介
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
