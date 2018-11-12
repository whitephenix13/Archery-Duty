package effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import conditions.Condition;
import deplacement.Deplace;
import fleches.Fleche;
import partie.AbstractModelPartie;
import partie.PartieTimer;
import types.Entitie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Vent_effect extends Effect{

	double FRICTION_UPDATE_TIME = 0.01 ; // ~4 frames
	double lastFrictionUpdate = 0;

	double DUREE_EJECT = -1;

	public static double[] SQRT_EJECT_SPEED={30,22, 18,15};//square of desired speed at distance < sqrt(EJECT_DISTANCE) per animation 
	private int getEJECT_DISTANCE()
	{
		return xtaille.get(anim) * ytaille.get(anim);
	}
	float xalign = 1; // 0 , 1/2 , 1. Set 1 for right align 
	float yalign = 0.5f; // 0 , 1/2 , 1. Set 1 for bottom align 
	
	private Collidable collidedObject=null;
	private Vitesse collidedEjectSpeed=null;
	
	

	public Vent_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean fromCenter)
	{
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,typeEffect,false);
		if(fromCenter)
			xalign = 0.5f;
		anim=_anim;

		
		xtaille =  Arrays.asList(164,178,259,400);
		ytaille =  Arrays.asList(159,191,349,400);
		//hitbox = Hitbox.createSquareHitboxes(Arrays.asList(125,123,74,0),Arrays.asList(109,110,0,0),Arrays.asList(291,298,336,400),Arrays.asList(274,313,349,400));
		hitbox = Hitbox.createSquareHitboxes(Arrays.asList(0,0,0,0),Arrays.asList(0,0,0,0),Arrays.asList(164,178,259,400),Arrays.asList(159,191,349,400));

		int start_index =0;
		int end_index =4;
		animation.start(Arrays.asList(4,8,12,16), current_frame, start_index, end_index);
		maxnumberloops = 1;
		
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
	}
	public void SetCollidedObject(Collidable _collidedObject,Vitesse _collidedEjectSpeed)
	{
		collidedObject=_collidedObject;
		collidedEjectSpeed=_collidedEjectSpeed;
	}
	
	@Override
	public int getMaxBoundingSquare()
	{
		return 400;
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
			//System.out.println("Vent effect" + x_vit+" "+ y_vit);
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

		return computeProjectSpeed(partie,obj_mid,super.getArrowTip(partie),getEJECT_DISTANCE(),anim);
	}

	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(Collision.testcollisionObjects(partie, this, attacher,true)){
			Vitesse init_vit = computeProjectSpeed(partie,attacher); 
			attacher.conditions.addNewCondition(Condition.MOTION, DUREE_EJECT,init_vit,System.identityHashCode(this));
		}
	}

	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		updatePos(partie);
		int prev_anim =anim;
		anim=animation.update(anim,partie.getFrame(),1);
		if(prev_anim != anim)
			onAnimChanged(prev_anim,anim);
		//doit deplace, change anim
		boolean[] res = {true,false};
		return res;
	}
	private void onAnimChanged(int prevAnim, int anim)
	{
			//We want the point of interest to stay the same: pos += (previous center - new center)
			int deltaX = (int)((xtaille.get(prevAnim)-xtaille.get(anim)) * xalign * Math.cos(rotation) - 
					(ytaille.get(prevAnim)-ytaille.get(anim)) * yalign * Math.sin(rotation));

			int deltaY = (int)((xtaille.get(prevAnim)-xtaille.get(anim)) * yalign * Math.sin(rotation) +
					(ytaille.get(prevAnim)-ytaille.get(anim)) * yalign * Math.cos(rotation)); 
			pxpos_sync(deltaX);
			pypos_sync(deltaY);
		
	}


	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie, Collidable obj) {
		return new Vitesse();
	}


	public void setFirstPos(AbstractModelPartie partie) {

		boolean worldCollision = pointCollision!=null;
		//get the middle bottom of the effect
		int x_eff_center = (int) (xtaille.get(anim)* xalign * Math.cos(rotation) - (ytaille.get(anim)*yalign) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)* xalign * Math.sin(rotation) + (ytaille.get(anim)*yalign) * Math.cos(rotation));

		Point firstPos = new Point();
		if(worldCollision)
			firstPos = super.setFirstPos(partie,new Point(x_eff_center,y_eff_center));
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip(partie);

			firstPos=new Point(arrowTip.x-x_eff_center,arrowTip.y-y_eff_center);

		}
		xpos_sync(firstPos.x);
		ypos_sync(firstPos.y);
	}

	@Override
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		return super.computeTransformDrawRotated(partie);
	}

	@Override
	public void onDestroy(AbstractModelPartie partie)
	{
		if(ref_fleche != null)
			ref_fleche.OnFlecheEffectDestroy(partie, true);
	}
}
