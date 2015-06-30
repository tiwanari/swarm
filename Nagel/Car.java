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
	//���ꂼ�ꎟ�̎Ԃ܂ł̋����B
	private int getDistanceFromNextCar(){
		int target = index + 1;
		if(target >= num) target -= num;
		int target_x = ((Car) cellVector.atOffset(target)).getX();
		int distance = 0;
		if(target_x > x) distance = target_x - x;
		else distance = target_x + width - x;
		return distance;
	}
	
	
	//��~�X�^�[�g
	public void SlStrigger(){
		if(getDistanceFromNextCar() <= vmin){
			SlS = 2;
			velocity = 0;
		}
	}
	
	
	//�����ɗ]�T������Ȃ�Α��x��1����
	public void accelerate(){
		if(velocity < vmax){
			if(velocity+1 < getDistanceFromNextCar() && SlS <= 0 && trouble <=0){
				velocity++;
			}
		}
	}
	//���x��O���Ƃ̎Ԋԋ������肬��܂Ō����B
	public void slowDown(){
		if(velocity >= getDistanceFromNextCar() && SlS <= 0 && trouble <=0){
			velocity = getDistanceFromNextCar()-1;
		}
	}
		
    //�����_���őS�̂������B
	public void decrementVelocity(){
		if(velocity > vmin && SlS <= 0 && trouble <=0)
			velocity--;
	}
	//�e�Ԃ����m���Ō����B
	public void randomslowdown(){
		if(velocity >= vmin && SlS <=0 && trouble <=0)
			velocity--;
    }	
	public void recovery(){
		if(trouble >= 1)
			trouble--;
	}
	//�ً}���Ԃɂ��}��~
	public void trouble(){
		velocity = 0;
		trouble = 50;//�v���[�u�Ő��l��ݒ�ł����ق����ǂ��H
	}
	//�Ԃ̈ړ��A�I�[�𒴂������ԑO�փ��[�v
	public void move(){
		if(SlS <= 0 && trouble <=0) x+=velocity;
		if(SlS <= 0 && trouble <=0 && x>=width) {
			x-=width;
		}
	}
	//�O���̋󂫂��m�F���ĉ������n�߂�i�K
	public void SlowtoStart(){
		if(getDistanceFromNextCar() > vmin && SlS >= 1){
			SlS--;
		}	
	}
	//���x������
	public void initialize(){
		velocity=vmin;
	}
	
	public void setParams(int index, int vmax, int num){
		this.index = index;
		this.vmax = vmax;
		this.num = num;
	}
}
