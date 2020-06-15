package partie.effects;

import java.awt.Point;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.conditions.Condition.ConditionEnum;
import partie.entitie.Entity;
import partie.modelPartie.ModelPartie;
import partie.mouvement.effect.Vent_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Vent_effect extends Effect{
	
	double DUREE_EJECT = -1;
	final double PULSE_EVERY = 1;

	public static double[] SQRT_EJECT_SPEED={30,22, 18,15};//square of desired speed at distance < sqrt(EJECT_DISTANCE) per animation 
	private int getEJECT_DISTANCE()
	{
		return getCurrentXtaille() * getCurrentYtaille();
	}
	
	private Collidable collidedObject=null;
	private Vitesse collidedEjectSpeed=null;
	
	

	public Vent_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,false,false);
		this.setCollideWithout(Arrays.asList(ObjectType.PROJECTILE));
		
		consequenceUpdateTime = PULSE_EVERY;//s => consequence applied every X while in the effect

		subTypeMouv = null;
		setMouvement(new Vent_idle(subTypeMouv,ModelPartie.me.getFrame()));
		ModelPartie.me.arrowsEffects.add(this);
		setFirstPos();
	}
	public void setCollidedObject(Collidable _collidedObject,Vitesse _collidedEjectSpeed)
	{
		collidedObject=_collidedObject;
		collidedEjectSpeed=_collidedEjectSpeed;
	}
	

	public Vitesse computeProjectSpeed(Vector2d objPoint,Point flechePoint,int EJECT_DISTANCE,int mouv_index)
	{

		double deltaX= (objPoint.x - flechePoint.x);
		double deltaY= (objPoint.y - flechePoint.y);

		double distance = deltaX*deltaX+deltaY*deltaY;

		double x_vit=0;
		double y_vit=0;
		//calculate projected speed
		if(distance<EJECT_DISTANCE)
		{
			double sqrt_distance = Math.sqrt(distance);
			double sqrt_eject_speed = SQRT_EJECT_SPEED[mouv_index];
			//The normalized vector is deltaX/sqrt_distance, deltaY/sqrt_distance. We want this vector to have sqrt_eject_speed as a norm
			x_vit = deltaX * sqrt_eject_speed/sqrt_distance;
			y_vit = deltaY * sqrt_eject_speed/sqrt_distance;
		}
		return new Vitesse(x_vit,y_vit);
	}
	

	Vitesse computeProjectSpeed(Collidable obj)
	{
		if(obj == collidedObject)
			System.out.println("\tSpecial case: " + collidedEjectSpeed);
		if(obj == collidedObject )
			return collidedEjectSpeed;
		
		//else compute vitesse for the first time 
	
		//find where object is precisely using the middle of the hitbox
		Vector2d obj_mid = Hitbox.getObjMid(obj);

		return computeProjectSpeed(obj_mid,super.getArrowTip(),getEJECT_DISTANCE(),getMouvIndex());
	}

	@Override
	public void applyConsequence(Entity attacher,boolean isFirstApplication)
	{
		Vitesse init_vit = computeProjectSpeed(attacher); 
		attacher.conditions.addNewCondition(ConditionEnum.MOTION, DUREE_EJECT,init_vit,System.identityHashCode(this));
	}
	
	private void alignBasedOnMouvIndex(int mouvIndex, int nextMouvIndex)
	{
		try {
			this.alignNextMouvement(getMouvement(), nextMouvIndex, XAlignmentType.CENTER, YAlignmentType.CENTER, false, true);
		} catch (Exception e) {} //this happens if we couldn't align the movement. We don't care in that case
	}
	@Override
	protected boolean updateMouvementBasedOnAnimation() {
		int nextMouvIndex = getMouvement().updateAnimation(getMouvIndex(), ModelPartie.me.getFrame(),1);
		if(getMouvIndex()!=nextMouvIndex){
			boolean success = true;//no need to hitbox alignment check 
			if(success){
				alignBasedOnMouvIndex(getMouvIndex(),nextMouvIndex);
				setMouvIndex(nextMouvIndex);
				return true;
			}
		}
		return false;
	}

	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}


	public void setFirstPos() {

		boolean worldCollision = pointCollision!=null;
		Vector2d eff_center = Hitbox.getObjMid(this);//.getCenterOfTaille();
		Point p_eff_center = new Point((int)Math.round(eff_center.x),(int)Math.round(eff_center.y));
		
		Point firstPos = new Point();
		if(worldCollision)
			firstPos = super.setFirstPos(p_eff_center);
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip();

			firstPos=new Point(arrowTip.x-p_eff_center.x,arrowTip.y-p_eff_center.y);

		}
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}

	@Override
	public void onDestroy()
	{
		if(ref_fleche != null)
			ref_fleche.OnFlecheEffectDestroy(true);
	}
}
