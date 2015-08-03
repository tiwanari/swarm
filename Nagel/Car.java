import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.collections.*;

public class Car extends SwarmObjectImpl {
	private int vmax;
	private int vmin;
	private int velocity;
	private int SlS;
	private int trouble;
	private int x;
	private int index;
	private int num;
	private int width;
	Array cellVector;
	
	
	public int compareCar(Car c){
		return x - c.x;
	}
	
	public Car(Zone aZone,int pos,int Width,Array aVector){
		super(aZone);
		width=Width;
		cellVector=aVector;
		
		num = 1;
		vmax = 10;
		vmin = 1;
		velocity = 1;
		x = pos;
		SlS = 0;
		trouble = 0;
		this.index = 1;
	}
	
	public int getVelocity(){
		return velocity;
	}
	
	public int getX(){
		return x;
	}
	//それぞれ次の車までの距離。
	private int getDistanceFromNextCar(){
		int target = index + 1;
		if(target >= num) target -= num;
		int target_x = ((Car) cellVector.atOffset(target)).getX();
		int distance = 0;
		if(target_x > x) distance = target_x - x;
		else distance = target_x + width - x;
		return distance;
	}
	
	
	//停止スタート
	public void SlStrigger(){
		if(getDistanceFromNextCar() <= vmin){
			SlS = 2;
			velocity = 0;
		}
	}
	
	
	//距離に余裕があるならば速度を1増加
	public void accelerate(){
		if(velocity < vmax){
			if(velocity+1 < getDistanceFromNextCar() && SlS <= 0 && trouble <=0){
				velocity++;
			}
		}
	}
	//速度を前方との車間距離ぎりぎりまで減速。
	public void slowDown(){
		if(velocity >= getDistanceFromNextCar() && SlS <= 0 && trouble <=0){
			velocity = getDistanceFromNextCar()-1;
		}
	}
		
    //ランダムで全体が減速。
	public void decrementVelocity(){
		if(velocity > vmin && SlS <= 0 && trouble <=0)
			velocity--;
	}
	//各車が一定確率で減速。
	public void randomslowdown(){
		if(velocity >= vmin && SlS <=0 && trouble <=0)
			velocity--;
    }	
	public void recovery(){
		if(trouble >= 1)
			trouble--;
	}
	//緊急事態による急停止
	public void trouble(){
		velocity = 0;
		trouble = 50;//プローブで数値を設定できたほうが良い？
	}
	//車の移動、終端を超えたら一番前へループ
	public void move(){
		if(SlS <= 0 && trouble <=0) x+=velocity;
		if(SlS <= 0 && trouble <=0 && x>=width) {
			x-=width;
		}
	}
	//前方の空きを確認して加速を始める段階
	public void SlowtoStart(){
		if(getDistanceFromNextCar() > vmin && SlS >= 1){
			SlS--;
		}	
	}
	//速度初期化
	public void initialize(){
		velocity=vmin;
	}
	
	public void setParams(int index, int vmax, int num){
		this.index = index;
		this.vmax = vmax;
		this.num = num;
	}
}
