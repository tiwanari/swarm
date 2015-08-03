import java.lang.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import swarm.defobj.Zone;
import swarm.space.DblBuffer2dImpl;

public class LangtonWorld extends DblBuffer2dImpl
{
	public static int[][] INIT_VALUE = {
		{ 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0 },
		{ 2, 1, 7, 0, 1, 4, 0, 1, 4, 2, 0, 0, 0, 0, 0 },
		{ 2, 0, 2, 2, 2, 2, 2, 2, 0, 2, 0, 0, 0, 0, 0 },
		{ 2, 7, 2, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0 },
		{ 2, 1, 2, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0 },
		{ 2, 0, 2, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0 },
		{ 2, 7, 2, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0 },
		{ 2, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 0 },
		{ 2, 0, 7, 1, 0, 7, 1, 0, 7, 1, 1, 1, 1, 1, 2 },
		{ 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0 }
		};
	
	public static HashMap transitionRules; 
	
	private int xsize, ysize;
	private ObserverSwarm observer;
	
	public LangtonWorld(Zone aZone, int x, int y)
	{
		super(aZone, x, y);
		transitionRules = new HashMap();
		try {
			BufferedReader reader = new BufferedReader( new FileReader(new File("transitionRules.txt")) );
			String str = new String();
			String[] elements = new String [2];
			while((str = reader.readLine()) != null)
			{
				System.out.println(str);
				StringTokenizer st = new StringTokenizer(str, " ");
				for(int i=0;i<2;i++)	elements[i] = st.nextToken();
				int state = Integer.valueOf( elements[0] ).intValue();
				int nextState = Integer.valueOf( elements[1] ).intValue();
				transitionRules.put(new Integer( state), new Integer(nextState) );
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if( e instanceof FileNotFoundException)
				System.out.println("File not found Exception");
			else if (e instanceof IOException )
				System.out.println("Rule File IO Exception");
			System.exit(1);
		}
		xsize = x;
		ysize = y;
	}
	
	// initializes world state
	public void init()
	{
		eraseAll();
		int x = getSizeX()/2;
		int y = getSizeY()/2;
		for(int i=0; i < INIT_VALUE.length; i++)
			for(int j=0; j < INIT_VALUE[0].length; j++)
				putValue$atX$Y( INIT_VALUE[i][j], x + j, y + i );
		updateLattice();
	}
	
	public Object setObserver(ObserverSwarm anObject)
	{
		observer = anObject;
		return this;
	}
	
	public Object setSizeX$Y(int x, int y)
	{
		xsize = x;
		ysize = y;
		return this;
	}

	public Object swapColorAtX$Y(int x, int y)
	{
		int newState;
		int oldState = this.getValueAtX$Y(x, y);

		newState = ( oldState + 1 ) % 8;
		this.putValue$atX$Y(newState, x, y);
		return this;
	}

	public Object eraseAll()
	{
		int i, j;
		for (i = 0; i < xsize; i++)
		{
			for (j = 0; j < ysize; j++)
			{
				this.putValue$atX$Y(0, i, j);
			}
		}
		return this;
	}

	public Object stepRule()
	{
		int[] proximity = new int[5];
		
		for (int x = 0; x < xsize; x++)
		{
			for (int y = 0; y < ysize; y++)
			{
				proximity[0] = getValueAtX$Y( x, y);
				proximity[1] = getValueAtX$Y( x, (y + ysize - 1) % ysize);
				proximity[2] = getValueAtX$Y((x + 1) % xsize, y);
				proximity[3] = getValueAtX$Y( x, (y + 1) % ysize);
				proximity[4] = getValueAtX$Y((x + xsize - 1) % xsize, y);
				
				int states = 0;
				states = proximity[1] * 1000 + proximity[2] * 100 + proximity[3] * 10 + proximity[4];
				if( states == 0 ) continue; // if all 0
				int roundedStates = proximity[2] * 1000 + proximity[3] * 100 + proximity[4] * 10 + proximity[1];
				if( roundedStates < states ) states = roundedStates;
				roundedStates = proximity[3] * 1000 + proximity[4] * 100 + proximity[1] * 10 + proximity[2];
				if( roundedStates < states ) states = roundedStates;
				roundedStates = proximity[4] * 1000 + proximity[1] * 100 + proximity[2] * 10 + proximity[3];
				if( roundedStates < states ) states = roundedStates;
				
				states += 10000 * proximity[0];
				int nextState = 0;
				if( transitionRules.containsKey( new Integer(states) ) )
					nextState = ((Integer)transitionRules.get( new Integer(states) )).intValue();
				
				//set state
				if( nextState != proximity[0] )
					this.putValue$atX$Y( nextState, x, y );
			}
		}
		updateLattice();
		return this;
	}
}