package partie.effects;

import java.awt.Point;

import javax.vecmath.Vector2d;

import partie.collision.Collidable;
import partie.collision.Collision;
import partie.conditions.Condition.ConditionEnum;
import partie.deplacement.effect.Feu_idle;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.PartieTimer;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Feu_effect extends Effect{

	int shift ;
	double DUREE_BRULURE = 5;
	double UPDATE_TIME = 0.05 ; //s
	double damage = -3;
	
	public Feu_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundEffect,int shift)
	{
		super(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,groundEffect,groundEffect);
		this.shift=shift;
		this.groundEffect = groundEffect;
		
		subTypeMouv = groundEffect?EffectCollisionEnum.GROUND:EffectCollisionEnum.ENTITY;
		setDeplacement(new Feu_idle(subTypeMouv,partie.getFrame()));
		
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
	}

	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		if(!groundEffect){
			if(Collision.testcollisionObjects(partie, this, attacher,true))
				attacher.conditions.addNewCondition(ConditionEnum.BRULURE, DUREE_BRULURE,System.identityHashCode(this));}
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
		int divider = groundEffect? 1:2;

		int x_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/2 * Math.cos(getRotation()) - (getDeplacement().ytaille.get(getAnim())/divider) * Math.sin(getRotation()));
		int y_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/2 * Math.sin(getRotation()) + (getDeplacement().ytaille.get(getAnim())/divider) * Math.cos(getRotation()));
		
		Point firstPos = new Point();
		if(groundEffect){
			firstPos = super.setFirstPos(partie,new Point(x_eff_center,y_eff_center));
			firstPos = new Point(firstPos.x+(int) (shift * Math.cos(getRotation())),firstPos.y +(int) (shift * Math.sin(getRotation())) );
		}
		else
		{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip(partie);
			firstPos=new Point(arrowTip.x-x_eff_center,arrowTip.y-y_eff_center);
		}

		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}
	

}
