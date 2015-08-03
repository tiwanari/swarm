//import swarm.*;
/**
 * 交通流シミュレーション<BR>
 * @author Iba Lab.
 **/

import swarm.Globals;

public class SiliconTraffic {

	
	
	public static void main(String[] args) 
	{
		
		Globals.env.initSwarm("Silicon Traffic", "0.0", "Iba Lab.", args);
		
		TrafficOvserver trafficOvserver = 
			new TrafficOvserver(Globals.env.globalZone);
		
		trafficOvserver.buildObjects();
		trafficOvserver.buildActions();
		trafficOvserver.activateIn(null);
		
		trafficOvserver.go();
		
	}

}
