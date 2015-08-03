import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import java.util.*;

public class Player extends SwarmObjectImpl {
	int playerID, type, newType;
	int posX, posY;
	int cumulPayoff;
	int numPlays;
	int memory;
	int newAction;
	int color = 0;
	
	int preType;
	Player other;
	List<Player> neighbors;

	int isNormal=0;
	
	static final int iParam[] = {1, 1, 0, 0};
	static final int pParam[] = {1, 1, 0, 0};
	static final int qParam[] = {1, 0, 1, 0};

	public Player(Zone aZone){
		super(aZone);
	}

	public Object initPlayer$type(int idnum,int playerType){
		playerID=idnum;
		type=playerType;
		return this;
	}

	public int getID(){
		return playerID;
	}

	public Object resetPlayer(){
		cumulPayoff=0;
		numPlays=0;
		return this;
	}

	public Object setPlayerType(int playerType){
		type=playerType;
		return this;
	}

	public void setIsNormal(int normal){
		isNormal = normal;
	}
	public int getPlayerType(){
		return type;
	}

	public Object setX$Y(int x,int y){
		posX=x;
		posY=y;
		return this;
	}

	public int getX(){
		return posX;
	}

	public int getY(){
		return posY;
	}

	public Object setNeighborhood(List<Player> nl){
		neighbors=nl;
		cumulPayoff=0;
		numPlays=0;
		return this;
	}

	public List<Player> getNeighborhood(){
		return neighbors;
	}

	public Object setOtherPlayer(Player player){
		other=player;
		return this;
	}

	public Object setPayoff(int payoff){
		cumulPayoff = payoff;
		return this;
	}

	public int getPayoff(){
		return cumulPayoff;
	}

	public double getAveragePayoff(){
		if(numPlays==0)
			return 0.0;
		else
			return (double) cumulPayoff / (double) numPlays;
	}

	public int getNewAction(){
		return newAction;
	}

	public Object remember(){
		memory = other.getNewAction();
		return this;
	}

	public void step(int t){
	
		numPlays++;
		if(isNormal == 0){
			newAction = type;
			if(preType == 1&& newAction == 0){
				color = 1;
			}else if(preType == 0&& newAction == 1){
				color = 0;
			}else if(preType == 1&& newAction == 1){
				color = 2;
			}else if(preType == 0&& newAction == 0){
				color = 3;
			}
		}else{
			if(t==0){
				newAction=iParam[type];
			}else if(memory==1){
				newAction=pParam[type];
			}else{
				newAction=qParam[type];
			}
		}
		
	}

	public Object adaptType(){
		Player neigh;
		Iterator index;
		int bestType;
		double bestPayoff,currentPayoff;

		bestPayoff = -1.0;
		bestType = type;
		newType = type;

		index=neighbors.iterator();
		while(index.hasNext()){
			neigh=(Player)index.next();
			currentPayoff=neigh.getAveragePayoff();
			if(currentPayoff>bestPayoff){
				bestPayoff=currentPayoff;
				bestType=neigh.getPlayerType();
			}
			else if(currentPayoff==bestPayoff)
				if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0,1.0)<0.5){
					bestPayoff=currentPayoff;
					bestType=neigh.getPlayerType();
				}
		}

		if(bestPayoff>getAveragePayoff())
			newType=bestType;
		
		return this;
	}

	public Object updateType(){
		preType = type;
		setPlayerType(newType);
		return this;
	}

	public Object drawSelfOn(Raster raster){
		raster.drawPointX$Y$Color(posX,posY,(byte)color);
		return this;
	}
}
