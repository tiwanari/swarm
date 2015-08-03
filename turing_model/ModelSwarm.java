import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
	public int ru,rv;
	public double w1,w2,m0,m1,p,d,e,initProb;
	public int width,history;
	
	PatternSpace patternSpace;
	
	Array cellVector;
	ActionGroup modelActions;
	Schedule modelSchedule;
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		ru=1;
		rv=17;
		w1=1.;
		w2=1.;
		m0=0.;
		m1=0.;
		p=0.002;
		d=0.;
		e=1.;
		initProb=0.;
		
		width=150;
		history=100;
		
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());
		
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("ru",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("rv",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("w1",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("w2",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("m0",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("m1",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("p",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("d",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("e",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForVariable$inClass
          ("initProb",getClass()));
		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass
          ("initializeCellVector",getClass()));
		
        Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
	}
	
	public Object buildObjects(){
		Cell aCell;
		patternSpace=new PatternSpace(this,width,history);
		
		cellVector=new ArrayImpl(this,width);
		patternSpace.setCellVector(cellVector);
		
		for (int i=0; i<width; i++){
			aCell=new Cell(this,i,width,cellVector);
			cellVector.atOffset$put(i,aCell);
		}
		initializeCellVector();
		return this;
	}
	
	public void stepCellVector(){
		for (int i=0; i<width; i++){
			((Cell)cellVector.atOffset(i)).step1();
		}
		for (int i=0; i<width; i++){
			((Cell)cellVector.atOffset(i)).step2();
		}
		for (int i=0; i<width; i++){
			((Cell)cellVector.atOffset(i)).step3();
		}
		for (int i=0; i<width; i++){
			((Cell)cellVector.atOffset(i)).step4();
		}
		for (int i=0; i<width; i++){
			((Cell)cellVector.atOffset(i)).step5();
		}
		for (int i=0; i<width; i++){
			((Cell)cellVector.atOffset(i)).step6();
		}
	}
	
	public Object buildActions(){
		modelActions=new ActionGroupImpl(this);
		try{
			modelActions.createActionTo$message(this,
				new Selector(getClass(),"stepCellVector",false));
			modelActions.createActionTo$message(patternSpace,
				new Selector(patternSpace.getClass(),"update",false));
		} catch (Exception e) {
			e.printStackTrace (System.err);
			System.exit(1);
		}
		
		modelSchedule=new ScheduleImpl(this,1);
		modelSchedule.at$createAction(0,modelActions);
		return this;
	}
	
	public Activity activateIn(Swarm context){
    	super.activateIn (context);
    	modelSchedule.activateIn(this);
		return getActivity();
	}
	
	public PatternSpace getPattern(){
		return patternSpace;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHistory(){
		return history;
	}
	
	public void initializeCellVector(){
		Cell aCell;
		for(int i=0;i<width;++i){
			aCell=(Cell)cellVector.atOffset(i);
			aCell.setParams(ru,rv,w1,w2,m0,m1,p,d,e,initProb);
			aCell.initialize();
		}
	}
}
