import swarm.space.*;
import swarm.*;
import swarm.defobj.*;
import swarm.collections.*;

public class PatternSpace extends Discrete2dImpl {
    int worldXSize,worldYSize;
    Array cellVector;

    public PatternSpace(Zone aZone,int x,int y)
    {
        super(aZone, x, y);
        worldXSize = x;
        worldYSize = y;
    }

    public void setCellVector(Array aVector)
    {
        cellVector = aVector;
    }

    public void update()
    {
        int x,y;
        for(y = 0; y < worldYSize; y++)
            for(x = 0; x < worldXSize; x++) {
                Cell cell = (Cell) cellVector.atOffset(y * worldXSize + x);
                int currentState = cell.getState();
                putValue$atX$Y(currentState, x, y);
            }
    }

}
