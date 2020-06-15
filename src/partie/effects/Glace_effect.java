package partie.effects;

import java.awt.Point;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.conditions.Condition.ConditionEnum;
import partie.entitie.Entity;
import partie.modelPartie.ModelPartie;
import partie.mouvement.effect.Glace_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Glace_effect extends Effect{
	
	double DUREE_LENTEUR=3;
	double damage = 0;
	double DUREE_EJECT = -1;
	double eject_vit_norm = 20;
	final double PULSE_EVERY = 0.5;

	public Glace_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundCollision, int _damage)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,groundCollision,groundCollision);
		damage = _damage;
		isProjectile=true;
		consequenceUpdateTime = PULSE_EVERY;//s => consequence applied every X while in the effect

		this.setCollideWithout(Arrays.asList(ObjectType.GLACE_EFF,ObjectType.FLECHE));
		
		subTypeMouv = groundEffect?EffectCollisionEnum.GROUND:EffectCollisionEnum.ENTITY;
		setMouvement(new Glace_idle(subTypeMouv,ModelPartie.me.getFrame()));
		
		ModelPartie.me.arrowsEffects.add(this);
		setFirstPos();
	}

	@Override
	public void applyConsequence(Entity attacher,boolean isFirstApplication)
	{
		if(!groundEffect)
			attacher.conditions.addNewCondition(ConditionEnum.LENTEUR, DUREE_LENTEUR,System.identityHashCode(this));
	}

	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}

	public void setFirstPos() {
		//get the middle bottom of the effect
		Point eff_center = groundEffect? getBottomOfTaille() : getCenterOfTaille();
		
		Point firstPos = new Point();
		if(groundEffect)
			firstPos = super.setFirstPos(eff_center);
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip();

			firstPos=new Point(arrowTip.x-eff_center.x,arrowTip.y-eff_center.y);

		}
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}

	@Override
	public void handleWorldCollision(Vector2d normal,Collidable collidedObject,boolean stuck)
	{
		if(!groundEffect)
			return;
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		//boolean collision_bas = normal.y<0;	
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		localVit= new Vitesse(0,0);
		this.setCollideWithNone();
		((Glace_idle)getMouvement()).setDestroyAnimation(ModelPartie.me.getFrame());
	}
	
	@Override
	public void handleObjectCollision(Collidable collider,Vector2d normal)
	{
		if(!groundEffect)
			return;
		//If collide with the heros, gain seyeri
		if(collider instanceof Entity){
			//get middle of collider
			Vector2d colliderMid = Hitbox.getObjMid(collider);
			//get middle of effect 
			Vector2d effectMid = Hitbox.getObjMid(this);
			// get rotation based on that previous value 
			double deltaX= (colliderMid.x - effectMid.x);
			double deltaY= (colliderMid.y - effectMid.y);

			double distance = Math.sqrt(deltaX*deltaX+deltaY*deltaY);
			Vitesse init_vit = new Vitesse(deltaX*eject_vit_norm/distance,deltaY*eject_vit_norm/distance);
			Entity ent = (Entity)collider;
			ent.conditions.addNewCondition(ConditionEnum.MOTION, DUREE_EJECT,init_vit,System.identityHashCode(this));
			ent.addLife(damage);
			this.setCollideWithNone();
			((Glace_idle)getMouvement()).setDestroyAnimation(ModelPartie.me.getFrame());
		}
	}
	@Override
	public void handleStuck() {
		if(!groundEffect)
			return;
		handleWorldCollision( new Vector2d(),null,true );
	}


}
