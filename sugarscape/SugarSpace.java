import swarm.defobj.Zone;
import swarm.objectbase.SwarmObjectImpl;
import swarm.space.Discrete2d;
import swarm.space.Discrete2dImpl;
import swarm.space.Grid2d;
import swarm.space.Grid2dImpl;

public class SugarSpace extends SwarmObjectImpl{
	int xsize, ysize;

	int dbg=0;

	Discrete2d sugar;
	Discrete2d spice;
	Discrete2d sugarAndSpice; // for display
	int sugarGrowRate;
	int spiceGrowRate;
	int season;
	int seasonRatio=8;
	int count=0;
	int seasonLength=50;

	String maxSugarDataFile;
	String maxSpiceDataFile;
	Discrete2d maxSugar;
	Discrete2d maxSpice;
	int globalMaxSugar;
	int globalMaxSpice;

	Discrete2d pollution;
	double takePollution=1.0;
	double consumePollution=1.0;
	int pollute;

	Grid2d agentGrid;

	public SugarSpace(Zone aZone,int x,int y,String datafile,String datafile2,int alpha,int pol,int ssn){
		super(aZone);

		xsize=x;
		ysize=y;
		maxSugarDataFile=datafile;
		maxSpiceDataFile=datafile2;
		sugarGrowRate=1;
		spiceGrowRate=1;
		pollute=pol;
		season=ssn;

		if(xsize <= 0 || ysize <= 0){
			if(dbg==1) System.out.println("SugarSpace was created with an invalid size");
			System.exit(1);
		}

		if(maxSugarDataFile == null){
			if(dbg==1) System.out.println("SugarSpace was created without a data file for max sugar.");
			System.exit(1);
		}
		if(maxSpiceDataFile == null){
			if(dbg==1) System.out.println("SugarSpace was created without a data file for max spice.");
			System.exit(1);
		}

		sugar=new Discrete2dImpl(getZone(),xsize,ysize);
		spice=new Discrete2dImpl(getZone(),xsize,ysize);
		sugarAndSpice=new Discrete2dImpl(getZone(),xsize,ysize);

		pollution=new Discrete2dImpl(getZone(),xsize,ysize);

		maxSugar = new Discrete2dImpl(getZone(),xsize,ysize);
		maxSpice = new Discrete2dImpl(getZone(),xsize,ysize);

		globalMaxSugar=maxSugar.setDiscrete2d$toFile(maxSugar,maxSugarDataFile);
		globalMaxSpice=maxSpice.setDiscrete2d$toFile(maxSpice,maxSpiceDataFile);

		maxSugar.copyDiscrete2d$toDiscrete2d(maxSugar,sugar);
		maxSpice.copyDiscrete2d$toDiscrete2d(maxSpice,spice);

		for(x=0; x<xsize; x++) for(y=0; y<ysize; y++)
			sugarAndSpice.putValue$atX$Y(spice.getValueAtX$Y(x,y)*8+sugar.getValueAtX$Y(x,y),x,y);

		agentGrid=new Grid2dImpl(getZone(), xsize, ysize);

	}

	public Object updateSugar(){
		if(dbg==1) System.out.println("---updateSugar()in---\n");

		int x,y,grow=sugarGrowRate;
		count++;

		if(dbg==1) System.out.println("SugarSpace.updateSugar()");
		for(x=0;x<xsize;x++){
			for(y=0;y<ysize;y++){
				int sugarHere=sugar.getValueAtX$Y(x,y);
				int maxSugarHere=maxSugar.getValueAtX$Y(x,y);
				int spiceHere=spice.getValueAtX$Y(x,y);
				int maxSpiceHere=maxSpice.getValueAtX$Y(x,y);

				if(season>=1) {
					if((count%(seasonLength*2))>seasonLength){
						if(y<(ysize/2)){
							if((count%seasonRatio)==0)
								grow=sugarGrowRate;
							else grow=0;
						}
						else grow=sugarGrowRate;
					}
					else {
						if(y>(ysize/2)){
							if((count%seasonRatio)==0)
								grow=sugarGrowRate;
							else grow=0;
						}
						else grow=sugarGrowRate;
					}
				}

				int gsgr=grow;
				int gspc=grow;

				if(season==2) gspc=spiceGrowRate; // spice grow whole year
				if(season==3) gsgr=sugarGrowRate; // sugar grow whole year

				if(sugarHere+gsgr < maxSugarHere)
					sugarHere=sugarHere+gsgr;
				else
					sugarHere=maxSugarHere;
				sugar.putValue$atX$Y(sugarHere,x,y);

				if(spiceHere+gspc < maxSpiceHere)
					spiceHere=spiceHere+gspc;
				else
					spiceHere=maxSpiceHere;
				spice.putValue$atX$Y(spiceHere,x,y);

			}
		}

		if(pollute==1){
			int pollutionHere=0;
			if(count>100){

				for(x=0; x<xsize; x++){
					for(y=0; y<ysize; y++){
						pollutionHere=pollution.getValueAtX$Y(x,y);
						int pollutionNorth=0,pollutionSouth=0,pollutionWest=0,pollutionEast=0;
						if(x>0) pollutionWest=pollution.getValueAtX$Y(x-1,y);
						if(y>0) pollutionNorth=pollution.getValueAtX$Y(x,y-1);
						if(x<xsize-1) pollutionEast=pollution.getValueAtX$Y(x+1,y);
						if(y<ysize-1) pollutionSouth=pollution.getValueAtX$Y(x,y+1);
						pollutionHere = (pollutionWest+pollutionEast
								+pollutionNorth+pollutionSouth)/4;
					}
				}
		
				for(x=0; x<xsize; x++){
					for(y=0; y<ysize; y++){
						pollution.putValue$atX$Y(pollutionHere,x,y);
					}
				}

			}
		}

		for(x=0; x<xsize; x++) for(y=0; y<ysize; y++)
			sugarAndSpice.putValue$atX$Y(spice.getValueAtX$Y(x,y)*8+sugar.getValueAtX$Y(x,y),x,y);

		if(dbg==1) System.out.println("---updateSugar()out---\n");

		return this;
	}

	public int getSugarAtX$Y(int x,int y){
		x=xnorm(x);
		y=ynorm(y);
		return sugar.getValueAtX$Y(x,y);
	}

	public int getSpiceAtX$Y(int x,int y){
		x=xnorm(x);
		y=ynorm(y);
		return spice.getValueAtX$Y(x,y);
	}

	public int getPollutionAtX$Y(int x,int y){
		x=xnorm(x);
		y=ynorm(y);
		return pollution.getValueAtX$Y(x,y);
	}

	public int takeSugarAtX$Y(int x,int y){
		if(dbg==1) System.out.println("---takeSugar()in---\n");

		int sugarHere, pollutionHere;

		x=xnorm(x);
		y=ynorm(y);
		sugarHere=sugar.getValueAtX$Y(x,y);
		sugar.putValue$atX$Y(0,x,y);

		if(pollute==1){
			if(count>50){
				pollutionHere=pollution.getValueAtX$Y(x,y);
				pollution.putValue$atX$Y(pollutionHere+(int)(sugarHere*takePollution),x,y);
			}
		}

		if(dbg==1) System.out.println("---takeSugar()out---\n");

		return sugarHere;
	}

	public int takeSpiceAtX$Y(int x,int y){
		if(dbg==1) System.out.println("---takeSugar()in---\n");

		int spiceHere;

		x=xnorm(x);
		y=ynorm(y);
		spiceHere=spice.getValueAtX$Y(x,y);
		spice.putValue$atX$Y(0,x,y);

		if(dbg==1) System.out.println("---takeSugar()out---\n");

		return spiceHere;
	}

	public int consumeSugar$atX$Y(int m,int x,int y){
		if(dbg==1) System.out.println("---consumeSugar()in---\n");
		int pollutionHere;

		x=xnorm(x);
		y=ynorm(y);

		if(pollute==1){
			if(count>50){
				pollutionHere=pollution.getValueAtX$Y(x,y);
				pollution.putValue$atX$Y(pollutionHere+(int)(m*consumePollution),x,y);
			}
		}
		
		if(dbg==1) System.out.println("---consumeSugar()out---\n");
		return m;
	}

	public Object getAgentAtX$Y(int x,int y){
		return agentGrid.getObjectAtX$Y(xnorm(x),ynorm(y));
	}

	public Object addAgent$atX$Y(SugarAgent agent, int x, int y){
		if(dbg==1) System.out.println("SugarSpace.addAgent()");
		x=xnorm(x);
		y=ynorm(y);
		agent.x=x;
		agent.y=y;
		agentGrid.putObject$atX$Y(agent,x,y);

		return this;
	}

	public Object removeAgent(SugarAgent agent){
		int x,y;
		if(dbg==1) System.out.println("SugarSpace.removeAgent()in");
		x=xnorm(agent.x);
		y=ynorm(agent.y);
		if(getAgentAtX$Y(x,y)==agent)
			agentGrid.putObject$atX$Y(null,x,y);
		
		if(dbg==1) System.out.println("SugarSpace.removeAgent()out");

		return this;
	}

	public Object moveAgent$toX$Y(SugarAgent agent,int x, int y){
		if(dbg==1) System.out.println("SugarSpace.moveAgent()in");
		removeAgent(agent);
		addAgent$atX$Y(agent,x,y);
		if(dbg==1) System.out.println("SugarSpace.moveAgent()out");

		return this;
	}

	public Object setSizeX$Y(int x, int y){
		xsize=x;
		ysize=y;

		return this;
	}

	public int getSizeX(){
		return xsize;
	}

	public int getSizeY(){
		return ysize;
	}

	public int getGlobalMaxSugar(){
		return globalMaxSugar;
	}

	public int getGlobalMaxSpice(){
		return globalMaxSpice;
	}

	public Grid2d getAgentGrid(){
		return agentGrid;
	}

	public Discrete2d getSugarValues(){
		return sugar;
	}

	public Discrete2d getSpiceValues(){
		return spice;
	}

	public Discrete2d getSugarAndSpiceValues(){
		return sugarAndSpice;
	}

	public Object setSugarGrowRate(int r){
		sugarGrowRate=r;
		return this;
	}

	public int getSugarGrowRate(){
		return sugarGrowRate;
	}

	public Object setMaxSugarDataFile(String s){
		maxSugarDataFile = s;
		return this;
	}

	public Object setMaxSpiceDataFile(String s){
		maxSpiceDataFile = s;
		return this;
	}

	public int xnorm(int x){
		if(x < 0)
			return (x + xsize * 128) % xsize;
		else if (x >= (int)xsize)
			return x % xsize;
		else
			return x;
	}

	public int ynorm(int y){
		if(y < 0)
			return (y + ysize * 128) % ysize;
		else if (y >= (int)ysize)
			return y % ysize;
		else
			return y;
	}

}

