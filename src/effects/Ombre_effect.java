package effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.GJK_EPA;
import deplacement.Deplace;
import fleches.Fleche;
import partie.AbstractModelPartie;
import types.Hitbox;
import types.Vitesse;

public class Ombre_effect extends Effect{

	Vector2d normalCollision=null;
	double effectRotation = 0;
	public Ombre_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision)
	{
		anim=_anim;

		ref_fleche = _ref_fleche;
		name = Fleche.SPIRITUELLE.OMBRE;
		xtaille =  Arrays.asList(92,92,92,92);
		ytaille =  Arrays.asList(79,79,79,79);
		rotation = _ref_fleche.rotation;//arrow's rotation
		//AXIS FOR ANGLE IS (1,0) 
		_normalCollision = GJK_EPA.projectVectorTo90(_normalCollision,false,0);
		double crossProdNorm = _normalCollision.y; // axis.x * _normalCollision.y  -axis.y * _normalCollision.x
		double dotProd = _normalCollision.x;
		effectRotation = Math.atan(crossProdNorm/dotProd) ;
		boolean minus_zero = ((Double)effectRotation).equals(new Double(-0.0));
		effectRotation += minus_zero? -Math.PI/2 : Math.PI/2;
		
		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index =4;
		animation.start(Arrays.asList(4,8,12,16), current_frame, start_index, end_index);
		maxnumberloops = 1;

		normalCollision=_normalCollision;
		partie.arrowsEffects.add(this);
	}


	@Override
	public void onUpdate(AbstractModelPartie partie, boolean last) {
		// TODO Auto-generated method stub

	}

	@Override
	public Point getTranslationFromTranformDraw(AbstractModelPartie partie) {

		//get the middle bottom of the effect
		int adjustBottom = -5;
		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(effectRotation) - (ytaille.get(anim)+adjustBottom) * Math.sin(effectRotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(effectRotation) + (ytaille.get(anim)+adjustBottom) * Math.cos(effectRotation));

		//get the tip of the arrow
		Hitbox fHitbox = ref_fleche.getHitbox(partie.INIT_RECT);
		
		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(ref_fleche.rotation-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(ref_fleche.rotation+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		int x_tip_fleche =  (int) ((v1.x+v2.x)/2);
		int y_tip_fleche= (int) ((v1.y+v2.y)/2);

		return new Point(x_tip_fleche-x_eff_center+partie.xScreendisp, +y_tip_fleche-y_eff_center+partie.yScreendisp);
	}
	@Override
	public AffineTransform getTransformDraw(AbstractModelPartie partie) {
		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr=new AffineTransform(ref_fleche.draw_tr);
		AffineTransform tr2 = new AffineTransform();
		double[] flatmat = new double[6];
		tr.getMatrix(flatmat);
		tr.translate(-flatmat[4], -flatmat[5]);
		tr.setTransform(flatmat[0], flatmat[1], flatmat[2], flatmat[3], transl.x, transl.y);
	
		tr2.translate(transl.x, transl.y);
		tr2.rotate(effectRotation);
		
		return tr2;
	}
	@Override
	public Image applyFilter(AbstractModelPartie partie, Image im) {
		return im;
	}

	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}

}
