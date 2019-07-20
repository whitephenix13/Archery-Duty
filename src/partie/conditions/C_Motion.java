package partie.conditions;


import java.util.ArrayList;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import partie.conditions.Condition.ConditionEnum;
import partie.modelPartie.PartieTimer;
import utils.Vitesse;

public class C_Motion extends Condition{

	public boolean attacherCollided = false;
	double FRICTION_UPDATE_TIME = 0.01 ; // ~4 frames
	double lastFrictionUpdate = 0;
	double speedThreshold = 10e-2;
	double FRICTION_VAL = 0.20f;
	
	float MAX_SPEED_NORM =200;
	Vitesse init_speed;
	Vitesse speed= null;
	private ArrayList<Integer> ids = new ArrayList<Integer>();
	public C_Motion(Vitesse init_speed)
	{
		type=ConditionEnum.MOTION;
		DUREE= -1 ; // no timer  
		startTime=PartieTimer.me.getElapsedNano();
		speed=correctSpeed(init_speed);
	}
	
	public Vitesse getModifiedVitesse()
	{
		return speed;
	}
	
	private Vitesse correctSpeed(Vitesse vit)
	{
		if(vit.norm()>MAX_SPEED_NORM)
		{
			Vector2d temp = new Vector2d();
			temp.normalize(vit);

			return new Vitesse(temp.x * MAX_SPEED_NORM, temp.y * MAX_SPEED_NORM);
		}
		return vit;
	}
	
	@Override
	public void Update()
	{			
		boolean update_frict = (PartieTimer.me.getElapsedNano() - lastFrictionUpdate)>FRICTION_UPDATE_TIME*Math.pow(10, 9) ;
		if(update_frict)
		{
			speed=Vitesse.applyFriction(speed, FRICTION_VAL, 0);
			lastFrictionUpdate= PartieTimer.me.getElapsedNano();
		}
	}
	@Override
	public void OnAttacherCollided()
	{
		attacherCollided=true;
	}
	@Override
	public boolean ended()
	{
		if(speed.norm()<speedThreshold || attacherCollided)
			return true;
		else
			return false;
	}
	
	
	@Override
	public void onAddCondition(double _duree,Vitesse _initSpeed,int _id)
	{
		if(!ids.contains(_id))
		{
			super.onAddCondition(_duree,_initSpeed,_id);
			speed= correctSpeed(speed.add(_initSpeed));
			attacherCollided=false;
			ids.add(_id);
		}
	}
}
