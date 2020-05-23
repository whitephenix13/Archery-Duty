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
import partie.mouvement.effect.Lumiere_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Lumiere_effect extends Effect{
	
	
	double DUREE_VITESSE = 10;

	public Lumiere_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,false,true);
		this.setCollideWithout(Arrays.asList(ObjectType.PROJECTILE));
		
		subTypeMouv = null;
		setMouvement(new Lumiere_idle(subTypeMouv,partie.getFrame()));

		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}


	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		if(Collision.testcollisionObjects(partie, this, attacher,true))
			attacher.conditions.addNewCondition(ConditionEnum.VITESSE, DUREE_VITESSE,System.identityHashCode(this));
	}
	
	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}
	
	public void setFirstPos(AbstractModelPartie partie) {
		//get the middle of the effect
		boolean worldCollision = (pointCollision !=null);
		Point eff_center = worldCollision? getBottomOfTaille() :getCenterOfTaille();
		
		Point firstPos = new Point();
		if(worldCollision)
			firstPos = super.setFirstPos(partie,eff_center);
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip(partie);
			firstPos=new Point(arrowTip.x-eff_center.x,arrowTip.y-eff_center.y);

		}
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}

	

}
