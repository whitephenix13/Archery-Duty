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

public class Ombre_effect extends Effect{

	Vector2d normalCollision=null;
	double LENTEUR_DUREE = 3;
	
	public Ombre_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision)
	{
		super.init();
		anim=_anim;

		ref_fleche = _ref_fleche;
		xtaille =  Arrays.asList(92,92,92,92);
		ytaille =  Arrays.asList(79,79,79,79);
		hitbox= Hitbox.createSquareHitboxes(0,0,92,79,4);


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
		int end_index =4;
		animation.start(Arrays.asList(4,8,12,16), current_frame, start_index, end_index);
		maxnumberloops = 1;

		localVit=new Vitesse();
		
		normalCollision=_normalCollision;
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}

	@Override
	public int getMaxBoundingSquare()
	{
		return 92;
	}
	
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(Collision.testcollisionObjects(partie, this, attacher,true))
			attacher.conditions.addNewCondition(Condition.LENTEUR, LENTEUR_DUREE);
	}
	
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}

	
	public void setFirstPos(AbstractModelPartie partie) {

		//get the middle bottom of the effect
		int adjustBottom = -5;
		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - (ytaille.get(anim)+adjustBottom) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + (ytaille.get(anim)+adjustBottom) * Math.cos(rotation));

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
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr2 = new AffineTransform();
	
		tr2.translate(transl.x, transl.y);
		tr2.rotate(rotation);
		
		return tr2;
	}
	


}
