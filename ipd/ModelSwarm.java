import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import java.util.*;

public class ModelSwarm extends SwarmImpl{
	List playerList;
	int time;
	public int randomSeed;
	int numPlayers;
	int num[]=new int[4];
	public double pALLC, pTFT, pATFT, pALLD;
	int numNeighbors;
	Discrete2d world;
	public int worldSize;

	public ModelSwarm(Zone aZone){
		super(aZone);
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("randomSeed",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("pALLC",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("pTFT",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("pATFT",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("pALLD",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("worldSize",this.getClass()));
		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());

	}

	public Object buildObjects(){
		Player aPlayer;
		Iterator index;
		int pt,i,playerID;
		int x,y;

		Globals.env.randomGenerator.setStateFromSeed(randomSeed);

		world=new Discrete2dImpl(this,worldSize,worldSize);
		world.fillWithObject(null);

		time=0;

		numPlayers=worldSize*worldSize;
		num[0] = (int) (numPlayers * pALLC);
		num[1] = (int) (numPlayers * pTFT);
		num[2] = (int) (numPlayers * pATFT);
		num[3] = (int) (numPlayers * pALLD);

		playerList=new ArrayList();
		playerID=0;
		for(pt=0;pt<4;pt++)
			for(i=0;i<num[pt];i++){
				playerID++;
				aPlayer=new Player(this);
				aPlayer.initPlayer$type(playerID,pt);
				aPlayer.setNeighborhood(new ArrayList());
				playerList.add(aPlayer);
			}

		shuffle(playerList);

		index=playerList.iterator();
		for(x=0;x<worldSize;x++){
			for(y=0;y<worldSize;y++){
				aPlayer=(Player)index.next();
				world.putObject$atX$Y(aPlayer,x,y);
				aPlayer.setX$Y(x,y);
			}
		}

		setNeighborhoods();

		return this;
	}

	public Object setNeighborhoods(){
		Iterator index;
		Player aPlayer;

		index=playerList.iterator();
		while(index.hasNext()){
			aPlayer=(Player)index.next();
			setNeighborhood$atDX$DY(aPlayer,-1,0);
			setNeighborhood$atDX$DY(aPlayer,1,0);
			setNeighborhood$atDX$DY(aPlayer,0,-1);
			setNeighborhood$atDX$DY(aPlayer,0,1);
		}
		return this;
	}

	public Object setNeighborhood$atDX$DY(Player player,int dx,int dy){
		int x,y;

		x=player.getX();
		y=player.getY();

		if(validX$Y(x+dx,y+dy)){
			player.getNeighborhood().add(world.getObjectAtX$Y(x+dx,y+dy));
		}
		return this;
	}

	public boolean validX$Y(int x,int y){
		return(((x >= 0) && (x < worldSize)) && ((y >= 0) && (y < worldSize)));
	}

	public Discrete2d getWorld(){
		return world;
	}

	public int getSize(){
		return worldSize;
	}

	public List getPlayers(){
		return playerList;
	}

	public Object resetPlayers(){
		Iterator index;
		Player player;

		index=playerList.iterator();
		while(index.hasNext()){
			player=(Player)index.next();
			player.resetPlayer();
		}
		return this;
	}

	public int getNum0(){
		return num[0];
	}

	public int getNum1(){
		return num[1];
	}

	public int getNum2(){
		return num[2];
	}

	public int getNum3(){
		return num[3];
	}

	public Object runTournaments(){
		Iterator index,nIndex;
		Player player,neigh;

		index=playerList.iterator();
		while(index.hasNext()){
			player=(Player)index.next();
			nIndex=player.getNeighborhood().iterator();
			while(nIndex.hasNext()){
				neigh=(Player)nIndex.next();
				runTournament$against(player,neigh);
			}
		}
		return this;
	}

	public Object runTournament$against(Player player1,Player player2){
		Tournament tournament;
		tournament=new Tournament(this);
		tournament.setPlayer1$Player2(player1,player2);
		tournament.run();
		tournament.drop();
		return this;
	}

	public Object adaptPlayers(){
		Iterator index;
		Player player;

		index=playerList.iterator();
		while(index.hasNext()){
			player=(Player)index.next();
			player.adaptType();
		}

		index=playerList.iterator();
		while(index.hasNext()){
			player=(Player)index.next();
			player.updateType();
		}
		return this;
	}

	public Object reportResults(){
		Iterator index;
		Player aPlayer;
		int pt;
		double avPayoff[]=new double[4];

		for(pt=0;pt<4;pt++){
			num[pt]=0;
			avPayoff[pt]=0.0;
		}

		index=playerList.iterator();
		while(index.hasNext()){
			aPlayer=(Player)index.next();
			pt=aPlayer.getPlayerType();
			num[pt]++;
			avPayoff[pt]=avPayoff[pt]+aPlayer.getAveragePayoff();
		}

		System.out.print("Time: "+time+" Num: "+num[0]+" "+num[1]+" "+num[2]+" "+num[3]+"   Payoff:");

		for(pt=0;pt<4;pt++){
			if(num[pt]==0)
				System.out.print(" "+0.0);
			else
				System.out.print(" "+avPayoff[pt] / num[pt]);
		}
		System.out.print("\n");


		return this;
	}

	public void step(){
		if(time>0){
			resetPlayers();
			runTournaments();
			adaptPlayers();
		}
		reportResults();
		time++;
	}

	public Object shuffle(List list){
		int j,k;
		Player temp;

		j=list.size();
		while(j>1){
			k=Globals.env.uniformIntRand.getIntegerWithMin$withMax(0,j-1);
			j--;
			temp=(Player)list.get(k);
			list.set(k,list.get(j));
			list.set(j,temp);
		}
		return this;
	}
}
