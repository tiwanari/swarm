import swarm.*;
import swarm.activity.*;
import swarm.defobj.*;
import swarm.objectbase.*;
import swarm.space.*;
import swarm.collections.*;

public class ModelSwarm extends SwarmImpl{
	
	public int width,height;
	public int iMax, jMax, masu;
	public int boardLen=6;
	public int board[][]={
		{20,22},{20,23},{20,24},{20,25},{20,26},{20,27},
		{21,27},{21,26},{21,25},{21,24},{21,23},{21,22}};
	public double reyn;
	int lenCellVector;
	
	PatternSpace patternSpace;
	
	Array cellVector;
	ActionGroup modelActions;
	Schedule modelSchedule;
	
	public ModelSwarm(Zone aZone){
		super(aZone);
		
		width=200; height=100;
		iMax=100;   jMax=50;
		lenCellVector = (iMax + 2)*(jMax + 2);
		
		EmptyProbeMap probeMap;
		probeMap=new EmptyProbeMapImpl(aZone,this.getClass());

		probeMap.addProbe(Globals.env.probeLibrary.getProbeForMessage$inClass
          ("reyn",getClass()));
		
        Globals.env.probeLibrary.setProbeMap$For(probeMap,this.getClass());
	}
	
	public Object buildObjects(){
		Cell aCell;
		patternSpace=new PatternSpace(this,width,height);
		
		cellVector=new ArrayImpl(this, lenCellVector);
		patternSpace.setCellVector(cellVector);
		patternSpace.init(iMax, jMax, masu, board, boardLen);

		for (int i=0; i<lenCellVector; i++){
			aCell=new Cell(this, i, iMax, jMax, board, boardLen, cellVector);
			cellVector.atOffset$put(i,aCell);
		}
		initializeCellVector();
		return this;
	}
	
	public int m(int i, int j){
		return j * (iMax + 2) + i;
	}
	
	public void stepCellVector(){
		int skip=10;
		for(int j=1; j<=jMax; j++ ){
			for(int i=1; i<=iMax; i++ ){
				((Cell)cellVector.atOffset(m(i,j))).step1();
			}
		}
		for(int j=1; j<=jMax; j++ ){
			for(int i=1; i<=iMax; i++ ){
				((Cell)cellVector.atOffset(m(i,j))).step2();
			}
		}
		for(int j=1; j<=jMax; j++ ){
			for(int i=1; i<=iMax; i++ ){
				((Cell)cellVector.atOffset(m(i,j))).step3();
			}
		}
		//‹«ŠEðŒ
		for(int i=1; i<=iMax; i++ ){
			((Cell)cellVector.atOffset(m(i,1))).step4();
		}
		for(int j=1; j<=jMax; j++ ){
			((Cell)cellVector.atOffset(m(1,j))).step5();
		}
		for(int j=1; j<=jMax; j++ ){
			((Cell)cellVector.atOffset(m(iMax,j))).step6();
		}
		for(int l=0; l<2*boardLen; l++ ){
			((Cell)cellVector.atOffset(m(board[l][0], board[l][1]))).step7();
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
	
	public int getheight(){
		return height;
	}
	
	public void initializeCellVector(){
		Cell aCell;
		
		for( int j=1; j<=jMax; j++ ){
			for( int i=1; i<=iMax; i++ ){
				aCell=(Cell)cellVector.atOffset(m(i,j));
				aCell.setParams( reyn );
				aCell.initialize();
			}
		}
	}
}
