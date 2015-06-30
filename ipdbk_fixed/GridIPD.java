import swarm.*;
public class GridIPD{
	public static void main(String[] args) {
		ObserverSwarm observerSwarm;
		
		Globals.env.initSwarm("GridIPD", "0.1", "Iba Lab.", args);
		
		observerSwarm=new ObserverSwarm(Globals.env.globalZone);
		observerSwarm.buildObjects();
		observerSwarm.buildActions();
		observerSwarm.activateIn(null);
		observerSwarm.go();
	}
}
