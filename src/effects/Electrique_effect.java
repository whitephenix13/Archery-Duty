package effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import collision.GJK_EPA;
import conditions.Condition;
import deplacement.Deplace;
import fleches.Fleche;
import partie.AbstractModelPartie;
import types.Entitie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Electrique_effect extends Effect{

	boolean type0 ;
	double DUREE_PARALYSIE= 2;

	public Electrique_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,int typeEffect)
	{
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,typeEffect,typeEffect == 0);
		type0 = typeEffect==0;

		if(type0){
			xtaille =  Arrays.asList(200,200,200,200,200,200);
			ytaille =  Arrays.asList(200,200,200,200,200,200);
			hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(43 ,0  ,0  ,0  ,0  ,0  ),
					Arrays.asList(145,72 ,17 ,0  ,0  ,0  ),
					Arrays.asList(159,175,200,200,200,200),
					Arrays.asList(200,200,200,200,200,200));
		}
		else{
			xtaille =  Arrays.asList(120,120,120,120);
			ytaille =  Arrays.asList(60,60,60,60);
			hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(60 ,0,0,0),
					Arrays.asList(0,0,0,0),
					Arrays.asList(120,120,120,120),
					Arrays.asList(60,60,60,60));
		}

		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index = type0? 6 : 4;
		List<Integer> animTimes= type0? Arrays.asList(6,12,18,24,30,36):Arrays.asList(4,8,12,16);
		animation.start(animTimes, current_frame, start_index, end_index);
		maxnumberloops = 1;
		
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}

	@Override
	public int getMaxBoundingSquare()
	{
		if(type0)
			return 200;
		else
			return 120;
	}
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(type0)
			if(Collision.testcollisionObjects(partie, this, attacher,true))
				attacher.conditions.addNewCondition(Condition.PARALYSIE, DUREE_PARALYSIE);
	}

	private void setFirstPos(AbstractModelPartie partie) {

		//get the middle bottom of the effect
		int xdivider = type0? 2:1;

		int x_eff_center = (int) (xtaille.get(anim)/xdivider * Math.cos(rotation) - (ytaille.get(anim)) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/xdivider * Math.sin(rotation) + (ytaille.get(anim)) * Math.cos(rotation));

		Point firstPos = new Point();
		if(type0)
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
		if(!type0)
			return super.computeTransformDraw(partie);
		else
			return super.computeTransformDrawRotated(partie);
	}

}
