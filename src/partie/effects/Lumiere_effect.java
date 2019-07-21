package partie.effects;

import java.awt.Point;

import javax.vecmath.Vector2d;

import partie.collision.Collidable;
import partie.collision.Collision;
import partie.conditions.Condition.ConditionEnum;
import partie.deplacement.effect.Lumiere_idle;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Lumiere_effect extends Effect{
	
	
	double DUREE_VITESSE = 10;

	public Lumiere_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision)
	{
		super(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,false,true);
		
		subTypeMouv = null;
		setDeplacement(new Lumiere_idle(subTypeMouv,partie.getFrame()));

		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
	}


	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		if(Collision.testcollisionObjects(partie, this, attacher,true))
			attacher.conditions.addNewCondition(ConditionEnum.VITESSE, DUREE_VITESSE,System.identityHashCode(this));
	}
	
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}
	
	public void setFirstPos(AbstractModelPartie partie) {
		//get the middle of the effect
		boolean worldCollision = (pointCollision !=null);
		int yDivider = worldCollision? 1 : 2 ;
		int x_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/2 * Math.cos(getRotation()) - getDeplacement().ytaille.get(getAnim())/yDivider * Math.sin(getRotation()));
		int y_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/2 * Math.sin(getRotation()) + getDeplacement().ytaille.get(getAnim())/yDivider * Math.cos(getRotation()));
		
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