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
import partie.PartieTimer;
import types.Entitie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Feu_effect extends Effect{

	Vector2d normalCollision=null;
	boolean type0 ;
	int shift ;
	double DUREE_BRULURE = 5;
	double UPDATE_TIME = 0.05 ; //s
	double damage = -5;
	
	public Feu_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,int typeEffect,int shift)
	{
		super.init();
		this.shift=shift;
		this.typeEffect=typeEffect;
		type0 = typeEffect==0;
		anim=_anim;

		ref_fleche = _ref_fleche;
		if(type0){
			xtaille =  Arrays.asList(40,40,40,40,40,40);
			ytaille =  Arrays.asList(300,300,300,300,300,300);
			hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(0 ,0  ,0  ,0  ,0  ,0  ),
					Arrays.asList(232,160,86 ,32 ,0  ,0  ),
					Arrays.asList(40,40,40,40,40,40),
					Arrays.asList(300,300,300,300,300,300));

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

		rotation =  type0?effectRotation:ref_fleche.rotation;
		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index = type0? 6 : 4;
		List<Integer> animTimes= type0? Arrays.asList(4,8,12,16,20,24):Arrays.asList(4,8,12,16);
		animation.start(animTimes, current_frame, start_index, end_index);
		maxnumberloops = 1;

		localVit= new Vitesse();
		normalCollision=_normalCollision;
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}

	@Override
	public int getMaxBoundingSquare()
	{
		if(type0)
			return 300;
		else
			return 100;
	}

	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(!type0){
			if(Collision.testcollisionObjects(partie, this, attacher,true))
				attacher.conditions.addNewCondition(Condition.BRULURE, DUREE_BRULURE);}
		else
			if((PartieTimer.me.getElapsedNano() - attacher.last_feu_effect_update)>UPDATE_TIME*Math.pow(10, 9) && Collision.testcollisionObjects(partie, this, attacher,true)){
				attacher.addLife(damage);
				attacher.last_feu_effect_update=PartieTimer.me.getElapsedNano();
			}
	}

	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}


	public void setFirstPos(AbstractModelPartie partie) {

		//get the middle bottom of the effect
		int adjustBottom = type0? -5 : 0;
		int divider = type0? 1:2;

		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - (ytaille.get(anim)/divider+adjustBottom) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + (ytaille.get(anim)/divider+adjustBottom) * Math.cos(rotation));

		//get the tip of the arrow
		Hitbox fHitbox = ref_fleche.getHitbox(partie.INIT_RECT,partie.getScreenDisp());

		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(ref_fleche.rotation-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(ref_fleche.rotation+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		int x_tip_fleche =  (int) ((v1.x+v2.x)/2);
		int y_tip_fleche= (int) ((v1.y+v2.y)/2);

		int xshift = type0? (int) (shift * Math.cos(rotation)):0;
		int yshift = type0? (int) (shift * Math.sin(rotation)):0;
		
		xpos_sync(x_tip_fleche-x_eff_center+xshift);
		ypos_sync( y_tip_fleche-y_eff_center+yshift);
	}
	@Override
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		if(!type0)
			return super.computeTransformDraw(partie);

		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr2 = new AffineTransform();

		tr2.translate(transl.x, transl.y);
		tr2.rotate(rotation);

		return tr2;
	}




}
