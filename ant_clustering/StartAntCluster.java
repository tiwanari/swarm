// StartSimpleBug.java
// Java SimpleBug application.

import swarm.*;

/**
 * ACO(Ant Colony Optimization)を応用したクラスタリング<BR>
 * 伊庭斉志, "複雑系のシミュレーション -Swarmによるマルチエージェント・システム-", コロナ社, 2007<BR>
 * http://www.iba.k.u-tokyo.ac.jp/software/Swarm_Software/<BR>
 * @author Claus Aranha
 **/
public class StartAntCluster
{
    public static void main (String[] args)
    {
     ObserverSwarm observerSwarm;
     // Swarm initialization: all Swarm apps must call this first.
     Globals.env.initSwarm ("AntCluster", "0.1", "Iba Lab.", args);

     // Create a top-level Swarm object and build its internal
     // objects and activities.
     observerSwarm = new ObserverSwarm(Globals.env.globalZone);
     observerSwarm.buildObjects();
     observerSwarm.buildActions();
     observerSwarm.activateIn(null);

     // Now activate the swarm.
     observerSwarm.go();
    }
}
