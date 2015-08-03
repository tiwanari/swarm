import swarm.gui.Raster;
import swarm.defobj.Zone;

public class BugA extends Bug {
	public BugA(Zone aZone) {
		super(aZone);
		// TODO Auto-generated constructor stub
		maxSpeed = 5.0f;
		minSpeed = 2.0f;
		searchSpace = 10;
		bugType = 1;
	}

	public Object drawSelfOn(Raster r) {
		r.drawPointX$Y$Color(xPos, yPos, (byte) 3);
		r.drawPointX$Y$Color(lastX, lastY, (byte) 2);
		return this;
	}

}
