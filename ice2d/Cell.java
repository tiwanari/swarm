import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import swarm.collections.*;
import java.util.*;

public class Cell extends SwarmObjectImpl {
    
    public static double kappa;
    public static double mu;
    public static double gamma;
    
    public enum Step {
        Diffusion,
        Freezing,
        Attachment,
        Melting,
        Noise;
        
        Step next() {
            Step[] vals = values();
            int nextOrd = ordinal() + 1;
            return nextOrd < vals.length ? vals[nextOrd] : vals[0];
        }
        
        Step prev() {
            Step[] vals = values();
            int prevOrd = ordinal() - 1;
            return prevOrd > -1 ? vals[prevOrd] : vals[vals.length-1];
        }
    };
    
    private Step step;
    private boolean a;
    private double b, c, d;
    
    int offset;
    int worldXSize, worldYSize;
    Array cellVector;

    /** 
     * HoneyComb Cell which has 6 neighbors
     */
    public Cell(Zone zone, int offset, Array vector)
    {
        super(zone);
        this.offset = offset;
        this.cellVector = vector;
    }

    public void initialize() 
    {
        this.step = Step.Noise;
    }

    public void setParams(int worldXSize, int worldYSize, 
        boolean a, double b, double c, double d)
    {
        this.worldXSize = worldXSize;
        this.worldYSize = worldYSize;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    
    public boolean isAttached()
    {
        return a;
    }
    
    public int getState()
    {
        if (a) return 1;
        else return 0;
    }
    
    public java.util.List<Cell> getNeighbors()
    {
        // add neighbors including it self
        java.util.List<Cell> list = new java.util.ArrayList<Cell>();
        list.add(this);
        
        if (offset / worldXSize == 0)
            list.add((Cell)cellVector.atOffset(offset + 0 + worldXSize * (worldYSize - 1)));
        else
            list.add((Cell)cellVector.atOffset(offset + 0 - worldXSize));
        
        // list.add((Cell)cellVector.atOffset(offset + 1 - worldXSize));
        
        if (offset % worldXSize == 0) 
            list.add((Cell)cellVector.atOffset(offset - 1 + worldXSize));
        else
            list.add((Cell)cellVector.atOffset(offset - 1));
        
        if ( (offset + 1) % worldXSize == 0)
            list.add((Cell)cellVector.atOffset(offset + 1 - worldXSize));
        else
            list.add((Cell)cellVector.atOffset(offset + 1));
        
        if (offset / worldXSize == worldYSize - 1) 
            list.add((Cell)cellVector.atOffset(offset + 0 - worldXSize * (worldYSize - 1)));
        else
            list.add((Cell)cellVector.atOffset(offset + 0 + worldXSize));
        
        // list.add((Cell)cellVector.atOffset(offset + 1 + worldXSize));
        return list;
    }
    
    public int numOfCrystalOnNeighbors()
    {
        java.util.List<Cell> list = getNeighbors();
        int count = 0;
        for (Cell cell : list) {
            if (cell.isAttached()) count++;
        }
        return count;
    }

    public void next()
    {
        switch (step) {
            case Diffusion:
            {
                /* if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < grow1) state = 1; */
                break;
            }
            case Freezing:
            {
                /* if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < grow2) state = 2; */
                break;
            }
            case Attachment:
            {
                /* if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < grow3) state = 3; */
                break;
            }
            case Melting:
            {
                /* if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < fire  */
                /*         || ( */
                /*                (oldNeiState_1 == 4 || oldNeiState_2 == 4 || oldNeiState_3 == 4 || oldNeiState_4 == 4) */
                /*                     && Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < diffuse */
                /*            ) */
                /*     )  */
                /*         state = 4; */
                break;
            }
            case Noise:
            {
                /* if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < cool1) state = 5; */
                break;
            }
        }
        
        step = step.next();
    }

}
