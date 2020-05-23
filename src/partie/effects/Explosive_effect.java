package partie.effects;

import java.awt.Point;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.effect.Explosive_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Explosive_effect extends Effect{
	
	public Explosive_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,false,true);
		this.setCollideWithout(Arrays.asList(ObjectType.PROJECTILE));
		
		subTypeMouv = null;
		setMouvement(new Explosive_idle(subTypeMouv,partie.getFrame()));
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}

	public void setFirstPos(AbstractModelPartie partie) {

		//get the middle of the effect
		boolean worldCollision = (pointCollision !=null);
		Point eff_center = worldCollision? getCenterOfTaille() :getCenterOfTaille();//worldCollision? getBottomOfTaille() :getCenterOfTaille()
		
		Point firstPos = super.setFirstPos(partie,eff_center);
	
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}
	
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		
	}
	
	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}
	
}
