import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import java.util.*;


public class ModelSwarm extends SwarmImpl{
	List<Player> playerList;
	int time;
	public int randomSeed;
	int numPlayers;
	int num[]=new int[4];
	public double pALLC, pTFT, pATFT, pALLD;
	int numNeighbors;
	Discrete2d world;
	public int worldSize;

	public int[] bigbanX = {48,49,50,51,48,49,50,51,48,48,48,49,50,51,48,49,50,51,51,51};
	public int[] bigbanY = {2,2,2,2,3,3,3,3,1,1,90,90,90,90,91,91,91,91,92,92};
	
	public int[] kaleidoscopeX = {50};
	public int[] kaleidoscopeY = {50};
	
	public int isBigbang;
	public int isKaleidoscope;
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass
				("isBigbang",this.getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass
				("isKaleidoscope",this.getClass()));



		Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());

	}

	public Object setBigbang(){

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

		playerList=new ArrayList<Player>();
		playerID=0;
		for(x=0;x<worldSize;x++){
			for(y=0;y<worldSize;y++){
				playerID++;
				aPlayer=new Player(this);
				aPlayer.initPlayer$type(playerID,1);
				aPlayer.setNeighborhood(new ArrayList<Player>());
				playerList.add(aPlayer);
				world.putObject$atX$Y(aPlayer,x,y);
				aPlayer.setX$Y(x,y);
			}
		}
		int cntx = 0;
		int cnty = 0;
		for(x=0;x<20;x++){
			Player temp = (Player) world.getObjectAtX$Y(bigbanX[cntx++], bigbanY[cnty++]);
			temp.setPlayerType(0);
		}
		setNeighborhoods();
		return this;
		
	}
	
	public Object setKaleidoscope(){
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

		playerList=new ArrayList<Player>();
		playerID=0;
		for(x=0;x<worldSize;x++){
			for(y=0;y<worldSize;y++){
				playerID++;
				aPlayer=new Player(this);
				
				if(x==50&&y==50){
					aPlayer.initPlayer$type(playerID,1);
				}else{
					aPlayer.initPlayer$type(playerID,0);
				}
				aPlayer.setNeighborhood(new ArrayList<Player>());
				playerList.add(aPlayer);
				world.putObject$atX$Y(aPlayer,x,y);
				aPlayer.setX$Y(x,y);
			}
		}
		setNeighborhoods();
		return this;

	}
	
	public Object setRandom(){
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

		playerList=new ArrayList<Player>();
		playerID=0;
		for(pt=0;pt<4;pt++)
			for(i=0;i<num[pt];i++){
				playerID++;
				aPlayer=new Player(this);
				aPlayer.setIsNormal(1);
				aPlayer.initPlayer$type(playerID,pt);
				aPlayer.setNeighborhood(new ArrayList<Player>());
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
	public Object buildObjects(){
		if(isBigbang==1&&isKaleidoscope==1){
			return setBigbang();
		}
		else if(isKaleidoscope == 1){
			return setKaleidoscope();
		}else if(isBigbang == 1){
			return setBigbang();
		}
		return null;
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
			setNeighborhood$atDX$DY(aPlayer,-1,-1);
			setNeighborhood$atDX$DY(aPlayer,1,-1);
			setNeighborhood$atDX$DY(aPlayer,1,1);
			setNeighborhood$atDX$DY(aPlayer,-1,1);
		}
		return this;
	}

	public Object setNeighborhood$atDX$DY(Player player,int dx,int dy){
		int x,y;

		x=player.getX();
		y=player.getY();

		if(validX$Y(x+dx,y+dy)){
			player.getNeighborhood().add((Player)world.getObjectAtX$Y(x+dx,y+dy));
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
		/*
		for(pt=0;pt<4;pt++){
			if(num[pt]==0)
				System.out.print(" "+0.0);
			else
				System.out.print(" "+avPayoff[pt] / num[pt]);
		}
		System.out.print("\n");
		 */

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

	public Object shuffle(List<Player> list){
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
