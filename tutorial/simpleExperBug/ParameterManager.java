import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.*;
import java.io.*;

/**
 * モデルのパラメータを設定するためのクラス。
 * ExperSwarmがすべきことの一部をここに分離した。
 */
public class ParameterManager extends SwarmImpl{
	public int worldXSize,worldYSize;
	
	public double seedProb;
	public double bugDensity;
	
	public double seedProbInc;
	public double bugDensityInc;
	
	public double seedProbMax;
	public double bugDensityMax;
	
	EmptyProbeMap pmProbeMap;
		
	public Object initializeParameters(Zone aZone){
		
		pmProbeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		pmProbeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
        	("worldXSize",this.getClass()));
		pmProbeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
        	("worldYSize",this.getClass()));
		pmProbeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
        	("seedProb",this.getClass()));
		pmProbeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
        	("bugDensity",this.getClass()));
		pmProbeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
        	("seedProbInc",this.getClass()));
		pmProbeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
        	("bugDensityInc",this.getClass()));
		pmProbeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
        	("seedProbMax",this.getClass()));
		pmProbeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
        	("bugDensityMax",this.getClass()));
		
        Globals.env.probeLibrary.setProbeMap$For(pmProbeMap,this.getClass());
        
		Globals.env.createArchivedProbeDisplay (this,"parameterManager");
		return this;
	}
	
	public Object initializeModel(ModelSwarm theModel){
		theModel.setWorldXSize$YSize(worldXSize,worldYSize);
		theModel.setSeedProb$bugDensity(seedProb,bugDensity);
		return this;
	}
	
	public Object stepParameters(){
		seedProb+=seedProbInc;
		bugDensity+= bugDensityInc;
		if ((seedProb > seedProbMax) || (bugDensity > bugDensityMax))
			return null;
		return this;
	}
	
	public Object Parameters(File anOutFile){
		try{
			FileWriter fw=new FileWriter(anOutFile);
			fw.write("\n");
			// 未翻訳
			//[ObjectSaver save: self to: anOutFile withTemplate: pmProbeMap];
			fw.write("\n");
		} catch(Exception e) {
			System.out.println("Exception: "+e.getMessage());
		}
		return this;
	}
}
