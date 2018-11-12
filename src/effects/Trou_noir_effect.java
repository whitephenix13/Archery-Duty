package effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import collision.Collidable;
import fleches.Fleche;
import partie.AbstractModelPartie;
import types.Entitie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Trou_noir_effect extends Effect{

	public Trou_noir_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,typeEffect,true);

		xtaille =  Arrays.asList(800,800,800,800,800);
		ytaille =  Arrays.asList(800,800,800,800,800);
		hitbox= Hitbox.createSquareHitboxes(0,0,800,800,5);

		int start_index =0;
		int end_index =5;
		animation.start(Arrays.asList(4,8,12,16,20), current_frame, start_index, end_index);
		maxnumberloops = 10;

		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
	}
	@Override
	public int getMaxBoundingSquare()
	{
		return 800;
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

	public void setFirstPos(AbstractModelPartie partie) {

		//get the middle bottom of the effect
		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - (ytaille.get(anim)/2) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + (ytaille.get(anim)/2) * Math.cos(rotation));

		Point firstPos = new Point();
		if(pointCollision!=null)
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




}
