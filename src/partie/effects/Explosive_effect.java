package partie.effects;

import java.awt.Point;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.mouvement.effect.Explosive_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Explosive_effect extends Effect{
	
	final double PULSE_EVERY = 0.05;
	public Explosive_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,false,true);
		this.setCollideWithout(Arrays.asList(ObjectType.PROJECTILE));
		
		consequenceUpdateTime = PULSE_EVERY;//s => damage every X while in the effect  
		
		subTypeMouv = null;
		setMouvement(new Explosive_idle(subTypeMouv,ModelPartie.me.getFrame()));
		ModelPartie.me.arrowsEffects.add(this);
		setFirstPos();
	}

	public void setFirstPos() {

		//get the middle of the effect
		boolean worldCollision = (pointCollision !=null);
		Point eff_center = worldCollision? getCenterOfTaille() :getCenterOfTaille();//worldCollision? getBottomOfTaille() :getCenterOfTaille()
		
		Point firstPos = super.setFirstPos(eff_center);
	
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}
	
	@Override
	public void applyConsequence(Entity attacher,boolean isFirstApplication)
	{
		
	}
	
	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}
	
}
