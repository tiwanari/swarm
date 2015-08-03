import swarm.*;

/**
 * 交通流シミュレーション<BR>
 * @author Iba Lab.
 **/
public class TrafficSimulator {

	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("TrafficSimulator","0.1","Iba Lab.",args);
		
		observerSwarm = new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();		
	}
}
