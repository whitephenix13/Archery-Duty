package partie.effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.conditions.Condition;
import partie.deplacement.Deplace;
import partie.deplacement.effect.Mouvement_effect.TypeMouvEffect;
import partie.deplacement.effect.Vent_idle;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Vent_effect extends Effect{
	
	double FRICTION_UPDATE_TIME = 0.01 ; // ~4 frames
	double lastFrictionUpdate = 0;

	double DUREE_EJECT = -1;

	public static double[] SQRT_EJECT_SPEED={30,22, 18,15};//square of desired speed at distance < sqrt(EJECT_DISTANCE) per animation 
	private int getEJECT_DISTANCE()
	{
		return getDeplacement().xtaille.get(getAnim()) * getDeplacement().ytaille.get(getAnim());
	}
	float xalign = 1; // 0 , 1/2 , 1. Set 1 for right align 
	float yalign = 0.5f; // 0 , 1/2 , 1. Set 1 for bottom align 
	
	private Collidable collidedObject=null;
	private Vitesse collidedEjectSpeed=null;
	
	

	public Vent_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean fromCenter)
	{
		boolean _typeEffect = false;//doesn't matter

		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,_typeEffect,false);
		if(fromCenter)
			xalign = 0.5f;

		setDeplacement(new Vent_idle(TypeMouvEffect.Vent,partie.getFrame()));
		
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
	}
	public void SetCollidedObject(Collidable _collidedObject,Vitesse _collidedEjectSpeed)
	{
		collidedObject=_collidedObject;
		collidedEjectSpeed=_collidedEjectSpeed;
	}
	

	public Vitesse computeProjectSpeed(AbstractModelPartie partie,Vector2d objPoint,Point flechePoint,int EJECT_DISTANCE,int anim)
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
			double sqrt_eject_speed = SQRT_EJECT_SPEED[anim];
			//The normalized vector is deltaX/sqrt_distance, deltaY/sqrt_distance. We want this vector to have sqrt_eject_speed as a norm
			x_vit = deltaX * sqrt_eject_speed/sqrt_distance;
			y_vit = deltaY * sqrt_eject_speed/sqrt_distance;
		}
		return new Vitesse(x_vit,y_vit);
	}
	

	Vitesse computeProjectSpeed(AbstractModelPartie partie,Collidable obj)
	{
		boolean isAffected = Collision.testcollisionObjects(partie, this, obj,false);
		if(!isAffected)
			return new Vitesse();
		if(obj == collidedObject)
			System.out.println("\tSpecial case: " + collidedEjectSpeed);
		if(obj == collidedObject )
			return collidedEjectSpeed;
		
		//else compute vitesse for the first time 
	
		//find where object is precisely using the middle of the hitbox
		Vector2d obj_mid = Hitbox.getObjMid(partie,obj);

		return computeProjectSpeed(partie,obj_mid,super.getArrowTip(partie),getEJECT_DISTANCE(),getAnim());
	}

	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		if(Collision.testcollisionObjects(partie, this, attacher,true)){
			Vitesse init_vit = computeProjectSpeed(partie,attacher); 
			attacher.conditions.addNewCondition(Condition.MOTION, DUREE_EJECT,init_vit,System.identityHashCode(this));
		}
	}

	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		updatePos(partie);
		int prev_anim =getAnim();
		setAnim(getDeplacement().updateAnimation(this,getAnim(),partie.getFrame(),1));
		if(prev_anim != getAnim())
			onAnimChanged(prev_anim,getAnim());
		//doit deplace, change anim
		boolean[] res = {true,false};
		return res;
	}
	private void onAnimChanged(int prevAnim, int anim)
	{
			//We want the point of interest to stay the same: pos += (previous center - new center)
			int deltaX = (int)((getDeplacement().xtaille.get(prevAnim)-getDeplacement().xtaille.get(anim)) * xalign * Math.cos(getRotation()) - 
					(getDeplacement().ytaille.get(prevAnim)-getDeplacement().ytaille.get(anim)) * yalign * Math.sin(getRotation()));

			int deltaY = (int)((getDeplacement().xtaille.get(prevAnim)-getDeplacement().xtaille.get(anim)) * yalign * Math.sin(getRotation()) +
					(getDeplacement().ytaille.get(prevAnim)-getDeplacement().ytaille.get(anim)) * yalign * Math.cos(getRotation())); 
			addXpos_sync(deltaX);
			addYpos_sync(deltaY);
		
	}


	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie, Collidable obj) {
		return new Vitesse();
	}


	public void setFirstPos(AbstractModelPartie partie) {

		boolean worldCollision = pointCollision!=null;
		//get the middle bottom of the effect
		int x_eff_center = (int) (getDeplacement().xtaille.get(getAnim())* xalign * Math.cos(getRotation()) - (getDeplacement().ytaille.get(getAnim())*yalign) * Math.sin(getRotation()));
		int y_eff_center = (int) (getDeplacement().xtaille.get(getAnim())* xalign * Math.sin(getRotation()) + (getDeplacement().ytaille.get(getAnim())*yalign) * Math.cos(getRotation()));

		Point firstPos = new Point();
		if(worldCollision)
			firstPos = super.setFirstPos(partie,new Point(x_eff_center,y_eff_center));
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip(partie);

			firstPos=new Point(arrowTip.x-x_eff_center,arrowTip.y-y_eff_center);

		}
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}

	@Override
	public void onDestroy(AbstractModelPartie partie)
	{
		if(ref_fleche != null)
			ref_fleche.OnFlecheEffectDestroy(partie, true);
	}
}
