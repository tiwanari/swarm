//SugarAgent.java

import swarm.Globals;
import swarm.defobj.Zone;
import swarm.gui.ZoomRaster;
import swarm.objectbase.SwarmObjectImpl;
import java.lang.Math;

public class SugarAgent extends SwarmObjectImpl{
	int currentSugar;
	int currentSpice;
	int initialSugar;
	int initialSpice;
	int metabolism;
	int metabolism2;
	int vision;
	int age;
	int deathAge;
	int sex;
	int[] tag;
	int stop;
	double price=1.0;

	ModelSwarm modelSwarm;
	SugarSpace sugarSpace;

	public int x, y;

	public SugarAgent(Zone aZone){
		super(aZone);
		stop=0;
	}

	public void step(){

		if(stop==1) return;

		moveToBestOpenSpot();

		currentSugar+=sugarSpace.takeSugarAtX$Y(x,y);
		currentSugar-=metabolism*10/modelSwarm.alpha;
		sugarSpace.consumeSugar$atX$Y(metabolism,x,y);

		if(modelSwarm.spice==1){
			currentSpice+=sugarSpace.takeSpiceAtX$Y(x,y);
			currentSpice-=metabolism2*10/modelSwarm.alpha;
		}

		age++;

		if(currentSugar <= 0 || (modelSwarm.spice==1 && currentSpice<=0) || age >= deathAge){

			sugarSpace.removeAgent(this);

			modelSwarm.agentDeath(this);

		}

		if(modelSwarm.mating==1)
				mateWithNeighbor();

		if(modelSwarm.propagation==1) propagateCulture();

		if(modelSwarm.trade>0) tradeFoods();

		return;
	}

	public int intabs(int a){
		if(a<0)
			return -a;
		else
			return a;
	}

	public Object tradeFoods(){
		SugarAgent agent=null;

		if(x-1>=0 && sugarSpace.getAgentAtX$Y(x-1,y)!=null){
			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x-1,y);
		}
		else if(x+1<sugarSpace.getSizeX() && sugarSpace.getAgentAtX$Y(x+1,y)!=null){
			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x+1,y);
		}
		else if(y-1>=0 && sugarSpace.getAgentAtX$Y(x,y-1)!=null){
			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x,y-1);
		}
		else if(y+1<sugarSpace.getSizeX() && sugarSpace.getAgentAtX$Y(x,y+1)!=null){
			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x,y+1);
		}

		
		if(agent!=null){
			if(modelSwarm.trade==2){
				if(isBlue()==0 || agent.isBlue()==0) return this;
			}
			else if(modelSwarm.trade==3){
				if(isRed()==0 || agent.isRed()==0) return this;
			}

			double mrsA=getCurrentMRS(), mrsB=agent.getCurrentMRS();
			if(mrsA==mrsB) return this;
			price=java.lang.Math.sqrt(mrsA*mrsB);
			int p;
			if(mrsA>mrsB){
				if(price>=1){
					p=(int)price;
					if(currentSpice<=2) return this;
					if(currentSpice<p) p=currentSpice;
					while(calculateMRS(currentSugar+1,currentSpice-p) > 
						agent.calculateMRS(agent.currentSugar-1,agent.currentSpice+p)){
						currentSugar+=1;
						currentSpice-=p;
						agent.currentSugar-=1;
						agent.currentSpice+=p;
						modelSwarm.amount+=p;
					}
				}
				else {
					p=(int)(1/price);
					if(agent.currentSugar<=1) return this;
					if(agent.currentSugar<p) p=agent.currentSugar;
					if(p==0) p=1;
					while(calculateMRS(currentSugar+p,currentSpice-1) > 
						agent.calculateMRS(agent.currentSugar-p,agent.currentSpice+1)){
						currentSugar+=p;
						currentSpice-=1;
						agent.currentSugar-=p;
						agent.currentSpice+=1;
						modelSwarm.amount+=p;
					}
				}
			}
			else {
				if(price>=1){
					p=(int)price;
					if(agent.currentSpice<=1) return this;
					if(agent.currentSpice<p) p=agent.currentSpice;
					while(calculateMRS(currentSugar-1,currentSpice+p) < 
						agent.calculateMRS(agent.currentSugar+1,agent.currentSpice-p)){
						currentSugar-=1;
						currentSpice+=p;
						agent.currentSugar+=1;
						agent.currentSpice-=p;
						modelSwarm.amount+=p;
					}
				}
				else {
					p=(int)(1/price);
					if(currentSugar<=1) return this;
					if(currentSugar<p) p=currentSugar;
					if(p==0) p=1;
					while(calculateMRS(currentSugar-p,currentSpice+1) < 
						agent.calculateMRS(agent.currentSugar+p,agent.currentSpice-1)){
						currentSugar-=p;
						currentSpice+=1;
						agent.currentSugar+=p;
						agent.currentSpice-=1;
						modelSwarm.amount+=p;
					}
				}
			}
		}
				
		return this;
	}

	public Object mateWithNeighbor(){
		SugarAgent agent=null, child=null;
		int openSpotsX[] = new int[4];
		int openSpotsY[] = new int[4];
		int openSpots;

		if(modelSwarm.dbg==1) System.out.println("---mateWithNeighbor()in---\n");

		if(x-1>=0 && sugarSpace.getAgentAtX$Y(x-1,y)!=null){
			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x-1,y);
		}
		else if(x+1<sugarSpace.getSizeX() && sugarSpace.getAgentAtX$Y(x+1,y)!=null){
			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x+1,y);
		}
		else if(y-1>=0 && sugarSpace.getAgentAtX$Y(x,y-1)!=null){
			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x,y-1);
		}
		else if(y+1<sugarSpace.getSizeX() && sugarSpace.getAgentAtX$Y(x,y+1)!=null){
			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x,y+1);
		}

		if(agent!=null && 
			agent.sex!=sex && 
			initialSugar/2 < currentSugar && agent.initialSugar/2 < agent.currentSugar &&
			(modelSwarm.spice==0 || initialSpice/2 < currentSpice && agent.initialSpice/2 < agent.currentSpice))
			{

			if(modelSwarm.dbg==1) System.out.println("---mateWithNeighbor()1---\n");

			sugarSpace.getAgentGrid().setOverwriteWarnings(false);

			openSpots = 0;
			if(sugarSpace.getAgentAtX$Y(x+1,y)==null && x+1<sugarSpace.getSizeX()){
				openSpotsX[openSpots]=x+1;
				openSpotsY[openSpots]=y;
				openSpots++;
			}
			if(sugarSpace.getAgentAtX$Y(x,y-1)==null && y-1>=0) {
				openSpotsX[openSpots]=x;
				openSpotsY[openSpots]=y-1;
				openSpots++;
			}
			if(sugarSpace.getAgentAtX$Y(x,y+1)==null && y+1<sugarSpace.getSizeY()) {
				openSpotsX[openSpots]=x;
				openSpotsY[openSpots]=y+1;
				openSpots++;
			}
			if(sugarSpace.getAgentAtX$Y(x-1,y)==null && x-1>=0) {
				openSpotsX[openSpots]=x-1;
				openSpotsY[openSpots]=y;
				openSpots++;
			}
				


			if(openSpots!=0){
				if(modelSwarm.dbg==1) System.out.println("---mateWithNeighbor()2---\n");
				int chosenSpot;

				if(openSpots==1)
					chosenSpot=0;
				else
					chosenSpot=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,openSpots-1);

				int newX = openSpotsX[chosenSpot];
				int newY = openSpotsY[chosenSpot];

				child=new SugarAgent(modelSwarm.aZone);
				child.setModelSwarm(modelSwarm);

				sugarSpace.addAgent$atX$Y(child,newX,newY);

				child.setInitialSugar((initialSugar+agent.initialSugar)/2);
				child.setCurrentSugar(child.getInitialSugar());
				currentSugar-=initialSugar/2;
				agent.currentSugar-=agent.initialSugar/2;

				if(modelSwarm.spice==1){
					child.setInitialSpice((initialSpice+agent.initialSpice)/2);
					child.setCurrentSpice(child.getInitialSpice());
					currentSpice-=initialSpice/2;
					agent.currentSpice-=agent.initialSpice/2;
				}

				int r;
				r=Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,2);
				if(r==1) child.setMetabolism(metabolism);
				else child.setMetabolism(agent.metabolism);

				r=Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,2);
				if(r==1) child.setMetabolism2(metabolism2);
				else child.setMetabolism2(agent.metabolism2);

				r=Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,2);
				if(r==1) child.setVision(vision);
				else child.setVision(agent.vision);

				child.setDeathAge(
					Globals.env.uniformIntRand.getIntegerWithMin$withMax(
					modelSwarm.deathAgeMin,modelSwarm.deathAgeMax));

				child.setSex(Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,2));

				if(modelSwarm.dbg==1) System.out.println("---mateWithNeighbor()3---\n");

				child.tag=new int[11];
				for(int i=0; i<modelSwarm.tagSize; i++){
					r=Globals.env.uniformIntRand.getIntegerWithMin$withMax(1,2);
					if(r==1) child.setTag$at(getTagAt(i),i);
					else child.setTag$at(agent.getTagAt(i),i);
				}

				if(modelSwarm.dbg==1) System.out.println("---mateWithNeighbor()4---\n");

				modelSwarm.childList.addLast(child);

				sugarSpace.getAgentGrid().setOverwriteWarnings(true);
			}
		}

		if(modelSwarm.dbg==1) System.out.println("---mateWithNeighbor()out---\n");

		return this;
	}

	public Object propagateCulture(){
		SugarAgent agent=null;

		if(x-1>=0) agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x-1,y);
		if(agent!=null) flipTag(agent);

		if(x+1<modelSwarm.worldXSize) agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x+1,y);
		if(agent!=null) flipTag(agent);

		if(y-1>=0) agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x,y-1);
		if(agent!=null) flipTag(agent);

		if(y+1<modelSwarm.worldYSize) agent = (SugarAgent)sugarSpace.getAgentAtX$Y(x,y+1);
		if(agent!=null) flipTag(agent);

		return this;
	}

	public Object flipTag(SugarAgent agent){
		int i=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0, modelSwarm.tagSize-1);
		agent.setTag$at(getTagAt(i), i);
		
		return this;
	}

	public Object moveToBestOpenSpot(){
		int xLook, yLook;
		int bestSugar;
		int bestDistance;
		int goodSpots;
		int[] goodX=new int[16];
		int[] goodY=new int[16];
		int chosenSpot, newX, newY;
		SugarAgent agent;
		
		if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()in---");
		bestSugar=-1;
		goodSpots=0;
		bestDistance=999999;

		
		int xMin=x-vision, xMax=x+vision, yMin=y-vision, yMax=y+vision;
		
		if(xMin < 0) xMin = 0;
		if(xMax >= modelSwarm.worldXSize) xMax = modelSwarm.worldXSize-1;
		if(yMin < 0) yMin = 0;
		if(yMax >= modelSwarm.worldYSize) yMax = modelSwarm.worldYSize-1;

		int sugarHere,pollutionHere,spiceHere;

		if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()1---");

		yLook=y;
		for(xLook = xMin; xLook <= xMax; xLook++){
			if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()1-0---");
			agent=(SugarAgent)sugarSpace.getAgentAtX$Y(xLook, yLook);
			if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()1-1---");

			int flag=0;
			if(agent==null && (modelSwarm.battle==0 || revenge(xLook,yLook)==0)) flag=1;
			else if(modelSwarm.battle==1 && xLook!=x && agent!=null && agent.isBlue()!=isBlue()
				&& agent.currentSugar < currentSugar	 && revenge(xLook,yLook)==0) flag=1;
			else if(modelSwarm.battle==2  && xLook!=x && agent!=null && agent.isBlue()!=isBlue()
				&& agent.currentSugar < currentSugar	) flag=1;
			else if(modelSwarm.battle==3  && xLook!=x && agent!=null && agent.isBlue()!=isBlue()
				&& agent.currentSugar < currentSugar && (isBlue()==1 || revenge(xLook,yLook)==0)) flag=1;
			else if(modelSwarm.battle==4  && xLook!=x && agent!=null && agent.isBlue()!=isBlue()
				&& agent.currentSugar < currentSugar && (isRed()==1 || revenge(xLook,yLook)==0)) flag=1;

			if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()1-1-2---");
			if(flag==1)
			{
				if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()1-2---");
				sugarHere = sugarSpace.getSugarAtX$Y(xLook,yLook);
				if(modelSwarm.spice==1){
					spiceHere = sugarSpace.getSpiceAtX$Y(xLook,yLook);
					sugarHere = welfare(currentSugar+sugarHere,currentSpice+spiceHere);
				}
				pollutionHere = sugarSpace.getPollutionAtX$Y(xLook,yLook);
				if(modelSwarm.battle==1 && agent!=null) sugarHere+=agent.currentSugar;
				if(modelSwarm.pollute==1) sugarHere=sugarHere*1000/(pollutionHere+1);

				if(sugarHere > bestSugar){
					bestSugar=sugarHere;
					bestDistance=intabs(x-xLook);
					goodSpots=0;
					goodX[0]=xLook;
					goodY[0]=yLook;
					goodSpots++;
				}
				else if(sugarHere==bestSugar){
					if(intabs(x-xLook) < bestDistance){
						bestDistance=intabs(x-xLook);
						goodSpots=0;
						goodX[0]=xLook;
						goodY[0]=yLook;
						goodSpots++;
					}
					else if(intabs(x-xLook)==bestDistance){
						goodX[goodSpots]=xLook;
						goodY[goodSpots]=yLook;
						goodSpots++;
					}
				}
			}
			if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()1-4---");
		}

		if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()2---");

		xLook=x;
		for(yLook = yMin; yLook <= yMax; yLook++){
			if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()2-0---");
			agent=(SugarAgent)sugarSpace.getAgentAtX$Y(xLook, yLook);
			if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()2-1---");

			int flag=0;
			if(agent==null && revenge(xLook,yLook)==0) flag=1;
			else if(modelSwarm.battle==1 && xLook!=x && agent!=null && agent.isBlue()!=isBlue()
				&& agent.currentSugar < currentSugar	 && revenge(xLook,yLook)==0) flag=1;
			else if(modelSwarm.battle==2  && xLook!=x && agent!=null && agent.isBlue()!=isBlue()
				&& agent.currentSugar < currentSugar	) flag=1;
			else if(modelSwarm.battle==3  && xLook!=x && agent!=null && agent.isBlue()!=isBlue()
				&& agent.currentSugar < currentSugar && (isBlue()==1 || revenge(xLook,yLook)==0)) flag=1;
			else if(modelSwarm.battle==4  && xLook!=x && agent!=null && agent.isBlue()!=isBlue()
				&& agent.currentSugar < currentSugar && (isRed()==1 || revenge(xLook,yLook)==0)) flag=1;

			if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()2-1-2---");
			if(flag==1)
			{
				if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()2-2---");

				sugarHere = sugarSpace.getSugarAtX$Y(xLook,yLook);
				if(modelSwarm.spice==1){
					spiceHere = sugarSpace.getSpiceAtX$Y(xLook,yLook);
					sugarHere = welfare(currentSugar+sugarHere,currentSpice+spiceHere);
				}
				pollutionHere = sugarSpace.getPollutionAtX$Y(xLook,yLook);
				if(modelSwarm.battle==1 && agent!=null) sugarHere+=agent.currentSugar;
				if(modelSwarm.pollute==1) sugarHere=sugarHere*1000/(pollutionHere+1);

				if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()2-3---");
				if(sugarHere > bestSugar){
					bestSugar=sugarHere;
					bestDistance=intabs(y-yLook);
					goodSpots=0;
					goodX[0]=xLook;
					goodY[0]=yLook;
					goodSpots++;
				}
				else if(sugarHere==bestSugar){
					if(intabs(y-yLook) < bestDistance){
						bestDistance=intabs(y-yLook);
						goodSpots=0;
						goodX[0]=xLook;
						goodY[0]=yLook;
						goodSpots++;
					}
					else if(intabs(y-yLook)==bestDistance){
						goodX[goodSpots]=xLook;
						goodY[goodSpots]=yLook;
						goodSpots++;
					}
				}
			}
			if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()2-4---");
		}

		if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()3---");

		if(goodSpots==0)
			;
		else{
			if(goodSpots==1)
				chosenSpot=0;
			else
				chosenSpot=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,goodSpots-1);

			newX=goodX[chosenSpot];
			newY=goodY[chosenSpot];

			agent = (SugarAgent)sugarSpace.getAgentAtX$Y(newX, newY);
			if(agent!=null && (newX!=x || newY!=y)){
				if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot() 5---");

				if(agent.currentSugar > modelSwarm.maxPlunder)
					currentSugar+=modelSwarm.maxPlunder;
				else
					currentSugar+=agent.currentSugar;

				sugarSpace.removeAgent(agent);
				modelSwarm.agentDeath(agent);
				agent.stop=1;
				modelSwarm.numKilled++;
			}

			sugarSpace.moveAgent$toX$Y(this,newX,newY);
		}


		if(modelSwarm.dbg==1) System.out.println("---SugarAgent.moveToBestOpenSpot()out---");

		return this;
	}

	public double getLastLogPrice(){
		return java.lang.Math.log(price);
	}

	public int welfare(int sugar, int spice){
		double w1,w2,m1,m2,wel;

		w1=(double)sugar;
		w2=(double)spice;
		m1=(double)metabolism*10/modelSwarm.alpha;
		m2=(double)metabolism2*10/modelSwarm.alpha;

		wel=Math.pow(w1,m1/(m1+m2))*Math.pow(w2,m2/(m1+m2));

		return (int)wel;
	}

	public double calculateMRS(int sugar, int spice){
		double w1,w2,m1,m2,mrs;
	

		w1=(double)sugar;
		w2=(double)spice;
		m1=(double)metabolism;
		m2=(double)metabolism2;

		if(w1<=0) w1=1;
		if(w2 <=0) w2=1;
		mrs=m1*w2/(m2*w1);

		return mrs;
	}

	public double getCurrentMRS(){
		return calculateMRS(currentSugar, currentSpice);
	}

	public double getCurrentLogMRS(){
		return java.lang.Math.log(calculateMRS(currentSugar, currentSpice));
	}

	public int revenge(int x, int y){
		if(modelSwarm.dbg==1) System.out.println("---SugarAgent.revenge()in---");
		int xLook, yLook;
		int xMin=x-vision, xMax=x+vision, yMin=y-vision, yMax=y+vision;
		SugarAgent agent;
		
		if(xMin < 0) xMin = 0;
		if(xMax >= modelSwarm.worldXSize) xMax = modelSwarm.worldXSize-1;
		if(yMin < 0) yMin = 0;
		if(yMax >= modelSwarm.worldYSize) yMax = modelSwarm.worldYSize-1;

		yLook=y;
		for(xLook = xMin; xLook <= xMax; xLook++){
			agent=(SugarAgent)sugarSpace.getAgentAtX$Y(xLook, yLook);
			if(agent!=null && agent.isBlue()!=isBlue() && agent.currentSugar > currentSugar){
				if(modelSwarm.dbg==1) System.out.println("---SugarAgent.revenge()out1---");
				return 1;
			}
		}
		xLook=x;
		for(yLook = yMin; yLook <= yMax; yLook++){
			agent=(SugarAgent)sugarSpace.getAgentAtX$Y(xLook, yLook);
			if(agent!=null && agent.isBlue()!=isBlue() && agent.currentSugar > currentSugar){
				if(modelSwarm.dbg==1) System.out.println("---SugarAgent.revenge()out1---");
				return 1;
			}
		}

		if(modelSwarm.dbg==1) System.out.println("---SugarAgent.revenge()out0---");
		return 0;
	}

	public Object setModelSwarm(ModelSwarm s){
		modelSwarm=s;
		sugarSpace=s.getSugarSpace();
		return this;
	}

	public int getCurrentSugar(){
		return currentSugar;
	}

	public Object setCurrentSugar(int cs){
		currentSugar=cs;
		return this;
	}

	public int getInitialSugar(){
		return initialSugar;
	}

	public Object setInitialSugar(int is){
		initialSugar=is;
		return this;
	}

	public int getCurrentSpice(){
		return currentSpice;
	}

	public Object setCurrentSpice(int cs){
		currentSpice=cs;
		return this;
	}

	public int getInitialSpice(){
		return initialSpice;
	}

	public Object setInitialSpice(int is){
		initialSpice=is;
		return this;
	}

	public int getCurrentWelfare(){
		if(modelSwarm.spice==1) return welfare(currentSugar, currentSpice);
		else return currentSugar;
	}

	public int getMetabolism(){
		return metabolism;
	}

	public int getMetabolism2(){
		return metabolism2;
	}

	public Object setMetabolism(int m){
		metabolism=m;
		return this;
	}

	public Object setMetabolism2(int m){
		metabolism2=m;
		return this;
	}

	public int getVision(){
		return vision;
	}

	public Object setVision(int v){
		vision=v;
		return this;
	}

	public int getAge(){
		return age;
	}

	public Object setDeathAge(int s){
		deathAge=s;
		return this;
	}

	public Object setSex(int s){
		sex=s;
		return this;
	}

	public int getSex(){
		return sex;
	}

	public Object setTag$at(int t,int i){
		tag[i]=t;
		return this;
	}

	public int getTagAt(int i){
		return tag[i];
	}

	public int isBlue(){
		int ones=0;
		for(int i=0; i<modelSwarm.tagSize; i++) ones+=tag[i];
		if(ones > modelSwarm.tagSize/2) return 1;
		else return 0;
	}

	public int isRed(){
		int ones=0;
		for(int i=0; i<modelSwarm.tagSize; i++) ones+=tag[i];
		if(ones <= modelSwarm.tagSize/2) return 1;
		else return 0;
	}

	public int dummy(){
		return 0;
	}

	public Object drawSelfOn(ZoomRaster r){

		if(modelSwarm.color==100){ // 8x8-color by place
			r.drawPointX$Y$Color(x,y,
			(byte)(128+(8*x/modelSwarm.worldXSize)*8+(8*y/modelSwarm.worldYSize)));
		}

		else if(modelSwarm.color==200){ // 8x8-color by place
			if((8*x/modelSwarm.worldXSize)<4) r.drawPointX$Y$Color(x,y,(byte)(128+32+(8*x/modelSwarm.worldXSize)*8));
			else r.drawPointX$Y$Color(x,y,(byte)(128+55+(8*x/modelSwarm.worldXSize)));
		}

		else if(modelSwarm.color==1){ // 8-color by vision
			if(vision >= 8)
				r.drawPointX$Y$Color(x,y,(byte)(128+7*9));
			else 
				r.drawPointX$Y$Color(x,y,(byte)(128+(vision/1)*9));
		}

		else if(modelSwarm.color==2){ // 8-color by metabolism
			int metabo;
			if(modelSwarm.spice==1) metabo=metabolism+metabolism2;
			else metabo=metabolism;

			if(metabo >= 8)
				r.drawPointX$Y$Color(x,y,(byte)(128+7*9));
			else 
				r.drawPointX$Y$Color(x,y,(byte)(128+(metabo/1)*9));
		}

		else if(modelSwarm.color==3){ // 8-color by wealth
			int wealth;
			if(modelSwarm.spice==1) wealth=currentSugar+currentSpice;
			else wealth=currentSugar;

			if(wealth >= 40)
				r.drawPointX$Y$Color(x,y,(byte)(128+7*9));
			else 
				r.drawPointX$Y$Color(x,y,(byte)(128+(wealth/5)*9));
		}

		else if(modelSwarm.color==4){ // 8-color by age
			if(age >= 80)
				r.drawPointX$Y$Color(x,y,(byte)(128+7*9));
			else 
				r.drawPointX$Y$Color(x,y,(byte)(128+(age/10)*9));
		}

		else if(modelSwarm.color==5){ // 8x8-color by vision and metabolism
			int metabo;
			if(modelSwarm.spice==1) metabo=metabolism+metabolism2;
			else metabo=metabolism;

			r.drawPointX$Y$Color(x,y,
			(byte)(128+(vision)*8+(metabo)));
		}

		else if(modelSwarm.color==6){ // 8x8-color by vision and wealth
			int wealth;
			if(modelSwarm.spice==1) wealth=currentSugar+currentSpice;
			else wealth=currentSugar;

			if(wealth>=40)
				r.drawPointX$Y$Color(x,y,
				(byte)(128+(vision)*8+7));
			else
				r.drawPointX$Y$Color(x,y,
				(byte)(128+(vision)*8+(wealth/5)));
		}

		
		else if(modelSwarm.color==7){ // 8-color by tag
			int ones=0;
			for(int i=0; i<modelSwarm.tagSize; i++) ones+=tag[i];
			r.drawPointX$Y$Color(x,y,(byte)(128+(7*ones/modelSwarm.tagSize)*7+7));
		}

		else if(modelSwarm.color==8){ // 2-color by tag
			int ones=0;
			for(int i=0; i<modelSwarm.tagSize; i++) ones+=tag[i];
			if(ones > modelSwarm.tagSize/2) r.drawPointX$Y$Color(x,y,(byte)101);
			else r.drawPointX$Y$Color(x,y,(byte)100);
		}

		else if(modelSwarm.color==9){ // 8-color by MRS
			int mrs = (int)getCurrentLogMRS();
			if(mrs<-4) mrs=-4;
			if(mrs>3) mrs=3;
			r.drawPointX$Y$Color(x,y,(byte)(128+(7*(mrs+4)/7)*7+7));
		}

		else if(modelSwarm.color==10){ // 8-color by tag permutation
			int type=tag[2]*4+tag[1]*2+tag[0];
			r.drawPointX$Y$Color(x,y,(byte)(192+type));
		}

		else if(modelSwarm.color==11){ // 14-color by tag and wealth
			int wealth;
			if(modelSwarm.spice==1) wealth=currentSugar+currentSpice;
			else wealth=currentSugar;
			wealth=wealth/5;
			if(wealth>5) wealth=5;

			if(isRed()==1)
				r.drawPointX$Y$Color(x,y,(byte)(128+7+wealth*8));
			else
				if(wealth<4) r.drawPointX$Y$Color(x,y,(byte)(128+32+wealth*8));
				else r.drawPointX$Y$Color(x,y,(byte)(128+55+wealth));
		}

		else r.drawPointX$Y$Color(x,y,(byte)100);


		return this;
	}

}
