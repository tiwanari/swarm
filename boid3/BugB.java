import swarm.gui.Raster;
import swarm.defobj.Zone;

public class BugB extends Bug {
	public BugB(Zone aZone) {
		super(aZone);
		// TODO Auto-generated constructor stub
		maxSpeed = 3.0f;
		minSpeed = 2.0f;
		searchSpace = 15;
		bugType = 2;
	}
	
	public Object drawSelfOn(Raster r) {
		r.drawPointX$Y$Color(xPos, yPos, (byte) 5);
		r.drawPointX$Y$Color(lastX, lastY, (byte) 4);
		return this;
	}
}
