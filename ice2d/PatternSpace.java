import swarm.space.*;
import swarm.*;
import swarm.defobj.*;
import swarm.collections.*;

public class PatternSpace extends Discrete2dImpl {
    int worldXSize, worldYSize;
    Array cellVector;

    public PatternSpace(Zone zone,int x,int y)
    {
        super(zone, x, y);
        worldXSize = x;
        worldYSize = y;
    }

    public void setCellVector(Array vector)
    {
        cellVector = vector;
    }

    public void update()
    {
        for(int y = 0; y < worldYSize; y++)
            for(int x = 0; x < worldXSize; x++) {
                Cell cell = (Cell) cellVector.atOffset(y * worldXSize + x);
                int currentState = cell.getState();
                putValue$atX$Y(currentState, x, y);
            }
    }

}
