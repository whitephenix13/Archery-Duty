package partie.effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import partie.collision.Collidable;
import partie.collision.Collision;
import partie.conditions.Condition;
import partie.deplacement.effect.Mouvement_effect.TypeMouvEffect;
import partie.deplacement.effect.Ombre_idle;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Ombre_effect extends Effect{
	
	double LENTEUR_DUREE = 3;
	
	public Ombre_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		boolean _typeEffect = false;//doesn't matter

		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,_typeEffect,true);

		setDeplacement(new Ombre_idle(TypeMouvEffect.Ombre,partie.getFrame()));
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
	}

	
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		if(Collision.testcollisionObjects(partie, this, attacher,true))
			attacher.conditions.addNewCondition(Condition.LENTEUR, LENTEUR_DUREE,System.identityHashCode(this));
	}
	
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}

	
	public void setFirstPos(AbstractModelPartie partie) {

		boolean worldCollision = (pointCollision !=null);
		int x_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/2 * Math.cos(getRotation()) - getDeplacement().ytaille.get(getAnim())/1 * Math.sin(getRotation()));
		int y_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/2 * Math.sin(getRotation()) + getDeplacement().ytaille.get(getAnim())/1 * Math.cos(getRotation()));
		
		Point firstPos = new Point();
		if(worldCollision)
			firstPos = super.setFirstPos(partie,new Point(x_eff_center,y_eff_center));
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip(partie);
			firstPos=new Point(arrowTip.x-x_eff_center,arrowTip.y-y_eff_center);

		}
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
		
	}

}
