import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import swarm.collections.*;

public class Cell extends SwarmObjectImpl {
    public int state, oldState, oldNeiState_1, oldNeiState_2, oldNeiState_3, oldNeiState_4;
    double cool1, cool2, cool3, grow1, grow2, grow3, fire, diffuse;

    int offset;
    int worldXSize, worldYSize;
    Array cellVector;

    public Cell(Zone aZone, int Offset, Array aVector)
    {
        super(aZone);
        offset = Offset;
        cellVector = aVector;
    }

    public void initialize() 
    {
        state = 1;
    }

    public void setParams(int WorldXSize, int WorldYSize, double Cool1, double Cool2, double Cool3, double Grow1, double Grow2, double Grow3, double Fire, double Diffuse)
    {
        worldXSize = WorldXSize;
        worldYSize = WorldYSize;
        cool1 = Cool1;
        cool2 = Cool2;
        cool3 = Cool3;
        grow1 = Grow1;
        grow2 = Grow2;
        grow3 = Grow3;
        fire = Fire;
        diffuse = Diffuse;
    }

    public int getState()
    {
        return state;
    }

    public void copyOldState()
    {
        oldState=state;
        if((offset%worldXSize) == 0) oldNeiState_1=((Cell)cellVector.atOffset(offset-1+worldXSize)).getState();
        else oldNeiState_1 =((Cell)cellVector.atOffset(offset-1)).getState();
        
        if(((offset+1)%worldXSize)==0) oldNeiState_2=((Cell)cellVector.atOffset(offset+1-worldXSize)).getState();
        else oldNeiState_2 =((Cell)cellVector.atOffset(offset+1)).getState();
        
        if((offset/worldXSize)==0) oldNeiState_3=((Cell)cellVector.atOffset(offset+worldXSize*(worldYSize-1))).getState();
        else oldNeiState_3=((Cell)cellVector.atOffset(offset-worldXSize)).getState();
        
        if ((offset/worldXSize) == (worldYSize-1)) 
            oldNeiState_4 = ((Cell) cellVector.atOffset(offset - worldXSize * (worldYSize - 1))).getState();
        else 
            oldNeiState_4 = ((Cell) cellVector.atOffset(offset + worldXSize)).getState();
    }

    public void newState()
    {
        switch (state) {
            case 0:
                if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < grow1) state = 1;
                break;
            case 1:
                if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < grow2) state = 2;
                break;
            case 2:
                if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < grow3) state = 3;
                break;
            case 3:
                if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < fire ||
                        ((oldNeiState_1 == 4 || oldNeiState_2 == 4 || oldNeiState_3 == 4 || oldNeiState_4 == 4) &&
                    Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < diffuse)) state = 4;
                break;
            case 4:
                if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < cool1) state = 5;
                break;
            case 5:
                if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < cool2) state = 6;
                break;
            case 6:
                if(Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < cool3) state = 0;
                break;
        }
    }

}
