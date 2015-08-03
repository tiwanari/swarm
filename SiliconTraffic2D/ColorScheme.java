import swarm.gui.Colormap;


public class ColorScheme
{
	static final byte BACKGOUNRD	= (byte)0;
	static final byte ROAD			= (byte)1;
	static final byte CENTER_LINE	= (byte)2;
	static final byte SIGNAL_FRAME	= (byte)4;
	static final byte SIGNAL_GREEN	= (byte)5;
	static final byte SIGNAL_RED	= (byte)6;
	static final byte USER_BEGIN	= (byte)7;
	
	public static void InitColorScheme(Colormap colorMap)
	{
		colorMap.setColor$ToRed$Green$Blue( ColorScheme.BACKGOUNRD, 0, 0, 0);
		colorMap.setColor$ToRed$Green$Blue( ColorScheme.ROAD, 0.7, 0.7, 0.7);
		colorMap.setColor$ToRed$Green$Blue( ColorScheme.CENTER_LINE, 1.0, 1.0, 1.0);
		
		colorMap.setColor$ToRed$Green$Blue( ColorScheme.SIGNAL_FRAME, 1.0, 1.0, 1.0);
		colorMap.setColor$ToRed$Green$Blue( ColorScheme.SIGNAL_GREEN, 0.0, 1.0, 0.2);
		colorMap.setColor$ToRed$Green$Blue( ColorScheme.SIGNAL_RED, 1.0, 0.2, 0.0);

		for(int i = 0;i <= Car.V_MAX;i++){
			byte curColId = (byte)i; 
			curColId += USER_BEGIN;
			
			double cp = (double)i / (double)Car.V_MAX; 
			if(cp < 1.0/4.0){
				cp -= 0.0/4.0; cp *= 4.0;
				colorMap.setColor$ToRed$Green$Blue( curColId, 1.0, cp, 0.0);
			}
			else if(cp < 2.0/4.0){
				cp -= 1.0/4.0; cp *= 4.0;
				colorMap.setColor$ToRed$Green$Blue( curColId, 1.0 - cp, 1.0, 0.0);
			}
			else if(cp < 3.0/4.0){
				cp -= 2.0/4.0; cp *= 4.0;
				colorMap.setColor$ToRed$Green$Blue( curColId, 0.0, 1.0, cp);
			}
			else{
				cp -= 3.0/4.0; cp *= 4.0;
				colorMap.setColor$ToRed$Green$Blue( curColId, 0.0, 1.0 - cp, 1.0);
			}
		}
		
	}
	
	public static byte GetColorFromSpeed(int speed)
	{
		return (byte)(speed + USER_BEGIN);
	}

}
