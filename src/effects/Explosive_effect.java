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

	Vector2d normalCollision=null;
	
	public Explosive_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision)
	{
		super.init();
		anim=_anim;

		ref_fleche = _ref_fleche;
		xtaille =  Arrays.asList(400,400,400,400,400,400,400,400,400,400,400,400);
		ytaille =  Arrays.asList(400,400,400,400,400,400,400,400,400,400,400,400);
		hitbox= Hitbox.createSquareHitboxes(
				Arrays.asList(185,170,142,103,105,80,62,110,110,133,137,144),
				Arrays.asList(185,166,138,113,87,77,73,180,150,195,225,238),
				Arrays.asList(213,237,263,300,283,294,306,280,262,260,255,220),
				Arrays.asList(213,238,265,318,306,318,325,328,340,346,340,371));


		//AXIS FOR ANGLE IS (1,0) 
		_normalCollision = GJK_EPA.projectVectorTo90(_normalCollision,false,0);
		double crossProdNorm = _normalCollision.y; // axis.x * _normalCollision.y  -axis.y * _normalCollision.x
		double dotProd = _normalCollision.x;
		double effectRotation = Math.atan(crossProdNorm/dotProd) ;
		boolean minus_zero = ((Double)effectRotation).equals(new Double(-0.0));
		effectRotation += minus_zero? -Math.PI/2 : Math.PI/2;
		
		rotation =  effectRotation;

		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index =12;
		animation.start(Arrays.asList(3,6,9,12,15,18,21,24,27,30,33,36), current_frame, start_index, end_index);
		maxnumberloops = 1;

		localVit=new Vitesse();
		
		normalCollision=_normalCollision;
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}

	public void setFirstPos(AbstractModelPartie partie) {

		//get the middle of the effect
		int adjustBottom = -5;
		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - (ytaille.get(anim)/2+adjustBottom) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + (ytaille.get(anim)/2+adjustBottom) * Math.cos(rotation));

		//get the tip of the arrow
		Hitbox fHitbox = ref_fleche.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
		
		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(ref_fleche.rotation-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(ref_fleche.rotation+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		int x_tip_fleche =  (int) ((v1.x+v2.x)/2);
		int y_tip_fleche= (int) ((v1.y+v2.y)/2);

		xpos_sync(x_tip_fleche-x_eff_center);
		ypos_sync(y_tip_fleche-y_eff_center);
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
		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr2 = new AffineTransform();
	
		tr2.translate(transl.x, transl.y);
		tr2.rotate(rotation);
		
		return tr2;
	}
	


}
