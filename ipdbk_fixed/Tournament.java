import swarm.defobj.*;
import swarm.objectbase.*;

public class Tournament extends SwarmObjectImpl{
	Player player1,player2;
	int numIter;

	static final int matrix[][]={{100,0},{165,0}};

	public Tournament(Zone aZone){
		super(aZone);
	}

	public Object setPlayer1$Player2(Player p1,Player p2){
		player1 = p1;
		player2 = p2;
		player1.setOtherPlayer(player2);
		player2.setOtherPlayer(player1);
		return this;
	}

	public Object updateMemories(){
		player1.remember();
		player2.remember();
		return this;
	}

	public Object distrPayoffs(){
		int action1, action2;

		action1 = player1.getNewAction();
		action2 = player2.getNewAction();
		player1.setPayoff(player1.getPayoff()+matrix[action1][action2]);
		player2.setPayoff(player2.getPayoff()+matrix[action2][action1]);
		return this;
	}

	public Object run(){
		int t;

		numIter = 4;

		for (t=0; t<numIter; t++) {
			updateMemories();
			player1.step(t);
			player2.step(t);
			distrPayoffs();
		}
		return this;
	}
}
