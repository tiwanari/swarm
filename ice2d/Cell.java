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
    public static double theta;
    public static double sigma;
    public static double alpha;
    public static double beta;
    
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
    private int prevNumOfCrystal = 0;
    private double prevDiffusiveMass = 0;
    
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
        this.step = Step.Diffusion;
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
    
    public double boundaryMass()
    {
        return b;
    }
    
    public double crystalMass()
    {
        return c;
    }
    
    public double diffusiveMass()
    {
        return d;
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
        
        // top-left
        if (offset / worldXSize != 0) {
            list.add((Cell)cellVector.atOffset(offset + 0 - worldXSize));
            // top-right
            if ( (offset + 1) % worldXSize != 0)
                list.add((Cell)cellVector.atOffset(offset + 1 - worldXSize));
        }
        
        // left
        if (offset % worldXSize != 0) 
            list.add((Cell)cellVector.atOffset(offset - 1));
        
        // right
        if ( (offset + 1) % worldXSize != 0)
            list.add((Cell)cellVector.atOffset(offset + 1));
        
        // bottom-right
        if (offset / worldXSize != worldYSize - 1) {
            list.add((Cell)cellVector.atOffset(offset + 0 + worldXSize));
            // bottom-left
            if (offset % worldXSize != 0) 
                list.add((Cell)cellVector.atOffset(offset - 1 + worldXSize));
        }
        
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
    
    public double calcTotalDiffusiveMassOnNeighbors() {
        java.util.List<Cell> cells = getNeighbors();
        double total = 0.0;
        for (Cell cell : cells) {
            if (cell.isAttached())
                total += this.diffusiveMass();
            else
                total += cell.diffusiveMass();
        }
        return total;
    }

    public void copyOldNeigborState()
    {
        prevNumOfCrystal = numOfCrystalOnNeighbors();
        prevDiffusiveMass = calcTotalDiffusiveMassOnNeighbors();
    }
    
    public void next()
    {
        // this cell is already attached
        if (a) return;
        
        switch (step) {
            case Diffusion:
            {
                if (numOfCrystalOnNeighbors() != 0) break;
                d = 1 / 7 * prevDiffusiveMass;
                break;
            }
            case Freezing:
            {
                b += (1 - kappa) * d;
                c += kappa * d;
                d = 0;
                break;
            }
            case Attachment:
            {
                int count = prevNumOfCrystal;
                if (count == 1 || count == 2) {
                    if (b >= beta) a = true;
                }
                else if (count == 3) {
                    if (b >= beta 
                        || (prevDiffusiveMass < theta && b >= alpha))
                        a = true;
                }
                else if (count >= 4) {
                    a = true;
                }
                
                if (a) {
                    c += b;
                    b = 0;
                }
                break;
            }
            case Melting:
            {
                d = d + mu * b + gamma * c;
                b *= (1 - mu);
                c *= (1 - gamma);
                break;
            }
            case Noise:
            {
                if (Globals.env.uniformDblRand.getDoubleWithMin$withMax(0.0, 1.0) < 0.5)
                    d *= (1 + sigma);
                else 
                    d *= (1 - sigma);
                break;
            }
        }
        if (prevNumOfCrystal != 0)
            System.out.println("(a, b, c, d) = " + a + ", " + b + ", " + c + ", " + d);
        /* System.out.println("kappa = " + kappa); */
        /* System.out.println("mu = " + mu); */
        /* System.out.println("mu = " + mu); */
        /* System.out.println("gamma = " + gamma); */
        /* System.out.println("alpha = " + alpha); */
        /* System.out.println("beta = " + beta); */
        /* System.out.println("theta = " + theta); */
        /* System.out.println("sigma = " + sigma); */
        if (a) System.out.println("changed!");
        
        step = step.next();
    }

}
