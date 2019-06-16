package partie.effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import partie.collision.Collidable;
import partie.deplacement.effect.Explosive_idle;
import partie.deplacement.effect.Mouvement_effect.TypeMouvEffect;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Explosive_effect extends Effect{
	
	public Explosive_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		boolean _typeEffect = false;//doesn't matter
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,_typeEffect,true);

		setDeplacement(new Explosive_idle(TypeMouvEffect.Explosive,partie.getFrame()));
		
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
	}

	public void setFirstPos(AbstractModelPartie partie) {

		//get the middle of the effect
		int x_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/2 * Math.cos(getRotation()) - (getDeplacement().ytaille.get(getAnim())/2) * Math.sin(getRotation()));
		int y_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/2 * Math.sin(getRotation()) + (getDeplacement().ytaille.get(getAnim())/2) * Math.cos(getRotation()));

		Point firstPos = super.setFirstPos(partie,new Point(x_eff_center,y_eff_center));
	
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}
	
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		
	}
	
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}
	
}
