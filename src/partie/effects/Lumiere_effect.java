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
import partie.mouvement.effect.Lumiere_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Lumiere_effect extends Effect{
	
	
	double DUREE_VITESSE = 10;
	final double PULSE_EVERY = 0.5;

	public Lumiere_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,false,true);
		this.setCollideWithout(Arrays.asList(ObjectType.PROJECTILE));
		
		consequenceUpdateTime = PULSE_EVERY;//s => consequence applied every X while in the effect

		subTypeMouv = null;
		setMouvement(new Lumiere_idle(subTypeMouv,ModelPartie.me.getFrame()));

		ModelPartie.me.arrowsEffects.add(this);
		setFirstPos();
	}


	@Override
	public void applyConsequence(Entity attacher,boolean isFirstApplication)
	{
		attacher.conditions.addNewCondition(ConditionEnum.VITESSE, DUREE_VITESSE,System.identityHashCode(this));
	}
	
	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}
	
	public void setFirstPos() {
		//get the middle of the effect
		boolean worldCollision = (pointCollision !=null);
		Point eff_center = worldCollision? getBottomOfTaille() :getCenterOfTaille();
		
		Point firstPos = new Point();
		if(worldCollision)
			firstPos = super.setFirstPos(eff_center);
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip();
			firstPos=new Point(arrowTip.x-eff_center.x,arrowTip.y-eff_center.y);

		}
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}

	

}
