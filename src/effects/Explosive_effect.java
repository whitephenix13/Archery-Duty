package effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

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

public class Explosive_effect extends Effect{
	
	public Explosive_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,typeEffect,true);

		xtaille =  Arrays.asList(400,400,400,400,400,400,400,400,400,400,400,400);
		ytaille =  Arrays.asList(400,400,400,400,400,400,400,400,400,400,400,400);
		hitbox= Hitbox.createSquareHitboxes(
				Arrays.asList(185,170,142,103,105,80,62,110,110,133,137,144),
				Arrays.asList(185,166,138,113,87,77,73,180,150,195,225,238),
				Arrays.asList(213,237,263,300,283,294,306,280,262,260,255,220),
				Arrays.asList(213,238,265,318,306,318,325,328,340,346,340,371));


		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index =12;
		animation.start(Arrays.asList(3,6,9,12,15,18,21,24,27,30,33,36), current_frame, start_index, end_index);
		maxnumberloops = 1;
		
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}

	public void setFirstPos(AbstractModelPartie partie) {

		//get the middle of the effect
		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - (ytaille.get(anim)/2) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + (ytaille.get(anim)/2) * Math.cos(rotation));

		Point firstPos = super.setFirstPos(new Point(x_eff_center,y_eff_center));
	
		xpos_sync(firstPos.x);
		ypos_sync(firstPos.y);
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
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}

	@Override
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		return super.computeTransformDrawRotated(partie);
	}
	


}
