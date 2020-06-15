package partie.effects;

import java.awt.Point;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.mouvement.effect.Trou_noir_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Trou_noir_effect extends Effect{
	final double PULSE_EVERY = 1;

	public Trou_noir_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,false,true);
		this.setCollideWithout(Arrays.asList(ObjectType.PROJECTILE));
		subTypeMouv = null;
		setMouvement(new Trou_noir_idle(subTypeMouv,ModelPartie.me.getFrame()));
		
		consequenceUpdateTime = PULSE_EVERY;//s => consequence applied every X while in the effect

		ModelPartie.me.arrowsEffects.add(this);
		setFirstPos();
	}

	@Override
	public void applyConsequence(Entity attacher,boolean isFirstApplication)
	{

	}

	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}

	public void setFirstPos() {
		
		Point eff_center = getCenterOfTaille();
		Point firstPos = new Point();
		if(pointCollision!=null)
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
