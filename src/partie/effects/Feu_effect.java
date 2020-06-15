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
import partie.modelPartie.ModelPartie;
import partie.modelPartie.PartieTimer;
import partie.mouvement.effect.Feu_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Feu_effect extends Effect{

	int shift ;
	double DUREE_BRULURE = 5;
	double damage = -5;
	final double GROUND_PULSE_EVERY = 0.06;
	final double ENTITY_PULSE_EVERY = 0.5;
	
	public Feu_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundEffect,int shift)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,groundEffect,groundEffect);
		this.setCollideWithout(Arrays.asList(ObjectType.PROJECTILE));
		
		assert getScaling().x == getScaling().y;
		
		this.shift=(int)Math.round(shift*getScaling().x);
		this.groundEffect = groundEffect;
		
		consequenceUpdateTime = groundEffect?GROUND_PULSE_EVERY:ENTITY_PULSE_EVERY;
		
		subTypeMouv = groundEffect?EffectCollisionEnum.GROUND:EffectCollisionEnum.ENTITY;
		setMouvement(new Feu_idle(subTypeMouv,ModelPartie.me.getFrame()));
		
		ModelPartie.me.arrowsEffects.add(this);
		setFirstPos();
		System.out.println(System.nanoTime());
	}

	@Override
	public void applyConsequence(Entity attacher,boolean isFirstApplication)
	{
		if(!groundEffect){
			attacher.conditions.addNewCondition(ConditionEnum.BRULURE, DUREE_BRULURE,System.identityHashCode(this));
		}
		else{
			attacher.addLife(damage);
			attacher.last_feu_effect_update=PartieTimer.me.getElapsedNano();
		}
	}

	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}


	public void setFirstPos() {
		
		Point eff_center = groundEffect? getBottomOfTaille() : getCenterOfTaille();
			
		Point firstPos = new Point();
		if(groundEffect){
			firstPos = super.setFirstPos(eff_center);
			firstPos = new Point(firstPos.x+(int) (shift * Math.cos(getRotation())),firstPos.y +(int) (shift * Math.sin(getRotation())) );
		}
		else
		{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip();
			firstPos=new Point(arrowTip.x-eff_center.x,arrowTip.y-eff_center.y);
		}

		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}
	

}
