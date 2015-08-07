import swarm.*;
import java.util.*;

/**
 * Snowflake simulator on Swarm
 * @author Tatsuya Iwanari
 **/
public class Snowflake2d {
    public static void main(String[] args) 
    {
        Globals.env.initSwarm("snowflake_2d", "0.1", "Iba Lab.", args);
        ObserverSwarm observerSwarm
            = new ObserverSwarm(Globals.env.globalZone);
        observerSwarm.buildObjects();
        observerSwarm.buildActions();
        observerSwarm.activateIn(null);
        observerSwarm.go();
    }
}
