// StartSimpleBug.java
// Java SimpleBug application.

import swarm.*;

/**
 * ACO(Ant Colony Optimization)�����p�����N���X�^�����O<BR>
 * �ɒ�Ďu, "���G�n�̃V�~�����[�V���� -Swarm�ɂ��}���`�G�[�W�F���g�E�V�X�e��-", �R���i��, 2007<BR>
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
