import swarm.space.*;
import swarm.*;
import swarm.defobj.*;
import swarm.collections.*;

public class PatternSpace extends Discrete2dImpl{
	int width,height;
	Array cellVector;
	int iMax, jMax, masu;
	int boardP[]={20,23}, boardQ[]={20,27}, boardLen=10, boardColor=250;
	public int board[][];

	public PatternSpace(Zone aZone,int x,int y){
		super(aZone,x,y);
		width=x;
		height=y;
		//
		iMax = 100;
		jMax =  50;
		masu =  2;
	}
	public void init( int IMax, int JMax, int Masu, int Board[][], int BoardLen ){
		board = Board;
		boardLen = BoardLen;
	}

	public void setCellVector(Array aVector){
		cellVector=aVector;
	}
	
	public int m2(int i, int j){
		return j * (iMax + 2) + i+1;
	}

	public void update(){
		int tmp;

		for( int j=0; j<height; j++ ){
			for( int i=0; i<width; i++ ){
				tmp=(int)java.lang.Math.round(((Cell)cellVector.atOffset(m2(i/masu+1,j/masu+1))).getU());
				if( tmp < 0 || tmp > 255 ) tmp=255;
				putValue$atX$Y( tmp, i, j );
			}
		}
		for( int l=0; l<boardLen; l++ ){
			for( int m=0; m<masu; m++ ){
				for( int n=0; n<masu; n++ ){
					putValue$atX$Y(boardColor, board[l][0]*masu + m, board[l][1]*masu + n );
				}
			}
		}
	}
}
