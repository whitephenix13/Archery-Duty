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

public class Roche_effect extends Effect{

	Vector2d normalCollision=null;

	boolean type0 ;
	
	double DUREE_DEFAILLANCE = 3;
	public Roche_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,int typeEffect)
	{
		this.typeEffect=typeEffect;
		type0 = typeEffect==0;
		anim=_anim;

		ref_fleche = _ref_fleche;
		if(type0){
			xtaille =  Arrays.asList(100,100,100,100,100,100,100,100,100,100);
			ytaille =  Arrays.asList(100,100,100,100,100,100,100,100,100,100);
			hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(40 ,40 ,40 ,40 ,40 ,34 ,23 ,20 ,10 ,4  ),
					Arrays.asList(80 ,60 ,40 ,20 ,0  ,0  ,8  ,32 ,51 ,60 ),
					Arrays.asList(60 ,60 ,60 ,60 ,60 ,70 ,73 ,74 ,86 ,91 ),
					Arrays.asList(100,100,100,100,100,100,100,100,100,100));
		}
		else{
			xtaille =  Arrays.asList(100,100,100,100);
			ytaille =  Arrays.asList(100,100,100,100);
			hitbox= Hitbox.createSquareHitboxes(0,0,100,100,4);
		}

		//AXIS FOR ANGLE IS (1,0) 
		_normalCollision = GJK_EPA.projectVectorTo90(_normalCollision,false,0);
		double crossProdNorm = _normalCollision.y; // axis.x * _normalCollision.y  -axis.y * _normalCollision.x
		double dotProd = _normalCollision.x;
		double effectRotation = Math.atan(crossProdNorm/dotProd) ;
		boolean minus_zero = ((Double)effectRotation).equals(new Double(-0.0));
		effectRotation += minus_zero? -Math.PI/2 : Math.PI/2;

		rotation = type0?effectRotation:ref_fleche.rotation;
		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index = type0? 10 : 4;
		List<Integer> animTimes= type0? Arrays.asList(3,6,9,12,15,21,27,33,39,45):Arrays.asList(4,8,12,16);
		animation.start(animTimes, current_frame, start_index, end_index);
		maxnumberloops = 1;

		localVit= new Vitesse();
		normalCollision=_normalCollision;
		partie.arrowsEffects.add(this);
	}

	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(!type0)
			if(Collision.testcollisionObjects(partie, this, attacher,true))
				attacher.conditions.addNewCondition(Condition.DEFAILLANCE, DUREE_DEFAILLANCE);
	}
	
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}
	
	@Override
	public Point getTranslationFromTranformDraw(AbstractModelPartie partie) {

		//get the middle bottom of the effect
		int adjustBottom = type0? -5 : 0;
		int divider = type0? 1:2;

		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - (ytaille.get(anim)/divider+adjustBottom) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + (ytaille.get(anim)/divider+adjustBottom) * Math.cos(rotation));

		//get the tip of the arrow
		Hitbox fHitbox = ref_fleche.getHitbox(partie.INIT_RECT);

		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(ref_fleche.rotation-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(ref_fleche.rotation+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		int x_tip_fleche =  (int) ((v1.x+v2.x)/2);
		int y_tip_fleche= (int) ((v1.y+v2.y)/2);

		return new Point(x_tip_fleche-x_eff_center+partie.xScreendisp, +y_tip_fleche-y_eff_center+partie.yScreendisp);
	}
	@Override
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		if(!type0)
			return super.computeTransformDraw(partie);
		
		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr=new AffineTransform(ref_fleche.draw_tr);
		AffineTransform tr2 = new AffineTransform();
		double[] flatmat = new double[6];
		tr.getMatrix(flatmat);
		tr.translate(-flatmat[4], -flatmat[5]);
		tr.setTransform(flatmat[0], flatmat[1], flatmat[2], flatmat[3], transl.x, transl.y);
		
		tr2.translate(transl.x, transl.y);
		tr2.rotate(rotation);

		return tr2;
	}
	

	

}
