package effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import deplacement.Deplace;
import fleches.Fleche;
import partie.AbstractModelPartie;
import types.Entitie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Vent_effect extends Effect{

	private double EJECT_SPEED=1600;//square of desired speed at distance < sqrt(EJECT_DISTANCE)
	private int getEJECT_DISTANCE()
	{
		switch(this.anim)
		{
		case 0: return 80*80;
		case 1: return 102*102;
		case 2: return 150*150;
		default: return 200*200;
		}
	}
	private Vitesse modified_vitesse = null;

	public Collidable stickedCollidable = null; //collidable to which the arrow is sticked if any 
	private Integer stickXVit = null;
	private Integer stickYVit = null;

	public Vent_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,typeEffect,false);
		anim=_anim;

		xtaille =  Arrays.asList(400,400,400,400);
		ytaille =  Arrays.asList(400,400,400,400);
		hitbox = Hitbox.createSquareHitboxes(Arrays.asList(125,123,74,0),Arrays.asList(109,110,0,0),Arrays.asList(291,298,336,400),Arrays.asList(274,313,349,400));

		int start_index =0;
		int end_index =4;
		animation.start(Arrays.asList(4,8,12,16), current_frame, start_index, end_index);
		maxnumberloops = 1;

		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}
	@Override
	public int getMaxBoundingSquare()
	{
		return 400;
	}
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{

	}

	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie, Collidable obj) {

		boolean isStickedCollidable = stickedCollidable!=null && obj == stickedCollidable;
		if(isStickedCollidable)
			if(stickXVit!=null && stickYVit != null)
				return new Vitesse(stickXVit,stickYVit);
		if(modified_vitesse!=null)
		{
			modified_vitesse=Vitesse.applyFriction(modified_vitesse, obj.useGravity, 0);
			return modified_vitesse;
		}

		boolean isAffected = Collision.testcollisionObjects(partie, this, obj,false);
		if(!isAffected)
			return new Vitesse();
		//else compute vitesse for the first time 

		//get position of objects 
		Point _pos = new Point(obj.xpos(),obj.ypos());
		if(obj.fixedWhenScreenMoves)
		{
			_pos.x-=partie.xScreendisp;_pos.y-=partie.yScreendisp;
		}
		double deltaX=_pos.x-ref_fleche.xpos();
		double deltaY=_pos.y-ref_fleche.ypos();
		double distance = deltaX*deltaX+deltaY*deltaY;
		int EJECT_DISTANCE = getEJECT_DISTANCE();

		//find where object is precisely using the middle of the hitbox
		Hitbox obj_hit = obj.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
		Vector2d obj_left_up_hit = Hitbox.supportPoint(new Vector2d(-1,-1),obj_hit.polygon);
		Vector2d obj_right_down_hit = Hitbox.supportPoint(new Vector2d(1,1),obj_hit.polygon);
		double objXmiddle = (obj_left_up_hit.x + obj_right_down_hit.x)/2;
		double objYmiddle = (obj_left_up_hit.y + obj_right_down_hit.y)/2;

		//use the tip of the arrow 
		Hitbox fleche_hit = ref_fleche.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
		//Test if x is close to 0
		double[] XY = Deplace.angleToXY(ref_fleche.rotation);

		Vector2d fleche_tip = Hitbox.supportPoint(new Vector2d(XY[0],XY[1]),fleche_hit.polygon);
		double deltaX2= (objXmiddle - fleche_tip.x);
		double deltaY2= (objYmiddle - fleche_tip.y);

		double distance2 = deltaX2*deltaX2+deltaY2*deltaY2;
		double x_vit=0;
		double y_vit=0;
		//calculate projected speed
		if(distance2<EJECT_DISTANCE)
		{
			double sqrt_eject_speed = Math.sqrt(EJECT_SPEED);
			double sqrt_eject_distance = Math.sqrt(EJECT_DISTANCE);
			double sqrt_distance2 = Math.sqrt(distance2);

			double normSpeed = sqrt_eject_speed * ((sqrt_eject_distance-sqrt_distance2) / sqrt_eject_distance);
			x_vit = deltaX2 * normSpeed/sqrt_distance2;
			y_vit = deltaY2 * normSpeed/sqrt_distance2;
		}
		if(isStickedCollidable)
		{
			stickXVit=(int) x_vit;
			stickYVit=(int) y_vit;				
		}
		return new Vitesse(x_vit,y_vit);


	}

	public void setFirstPos(AbstractModelPartie partie) {

		boolean worldCollision = pointCollision!=null;
		//get the middle bottom of the effect
		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - (ytaille.get(anim)/2) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + (ytaille.get(anim)/2) * Math.cos(rotation));

		Point firstPos = new Point();
		if(worldCollision)
			firstPos = super.setFirstPos(new Point(x_eff_center,y_eff_center));
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
