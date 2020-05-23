package partie.effects;

import java.awt.Point;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.conditions.Condition.ConditionEnum;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.PartieTimer;
import partie.mouvement.effect.Feu_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Feu_effect extends Effect{

	int shift ;
	double DUREE_BRULURE = 5;
	double UPDATE_TIME = 0.05 ; //s
	double damage = -3;
	
	public Feu_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundEffect,int shift)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,groundEffect,groundEffect);
		this.setCollideWithout(Arrays.asList(ObjectType.PROJECTILE));
		
		assert getScaling().x == getScaling().y;
		
		this.shift=(int)Math.round(shift*getScaling().x);
		this.groundEffect = groundEffect;
		
		subTypeMouv = groundEffect?EffectCollisionEnum.GROUND:EffectCollisionEnum.ENTITY;
		setMouvement(new Feu_idle(subTypeMouv,partie.getFrame()));
		
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		//Destroy if it is colliding in the ground
		/*if(groundEffect && Collision.isWorldCollision(partie, this, true)){//
			this.destroy(partie, true);
		}*/
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
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}


	public void setFirstPos(AbstractModelPartie partie) {
		
		Point eff_center = groundEffect? getBottomOfTaille() : getCenterOfTaille();
			
		Point firstPos = new Point();
		if(groundEffect){
			firstPos = super.setFirstPos(partie,eff_center);
			firstPos = new Point(firstPos.x+(int) (shift * Math.cos(getRotation())),firstPos.y +(int) (shift * Math.sin(getRotation())) );
		}
		else
		{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip(partie);
			firstPos=new Point(arrowTip.x-eff_center.x,arrowTip.y-eff_center.y);
		}

		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}
	

}
