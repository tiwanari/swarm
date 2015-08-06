import swarm.*;
import java.util.*;

/**
 * Snowflake simulator on Swarm
 * @author Tatsuya Iwanari
 **/
public class Snowflake2d {
    public static void main(String[] args) 
    {
        ObserverSwarm observerSwarm;

        Globals.env.initSwarm("snowflake_2d", "0.1", "Iba Lab.", args);
        observerSwarm = new ObserverSwarm(Globals.env.globalZone);
        System.out.println("buildObjects");
        observerSwarm.buildObjects();
        System.out.println("buildActions");
        observerSwarm.buildActions();
        System.out.println("activateIn");
        observerSwarm.activateIn(null);
        System.out.println("go");
        observerSwarm.go();
    }
}
