package partie.effects;

import java.awt.Point;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.conditions.Condition.ConditionEnum;
import partie.deplacement.Mouvement.SubTypeMouv;
import partie.deplacement.effect.Electrique_idle;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Electrique_effect extends Effect{
	
	double DUREE_PARALYSIE= 2;
	
	int numberExplosion = 0;
	int lifeGained = 10;
	int damage = -5;
	
	Collidable previousCollider = null;
	public Electrique_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundCollision,int _numberExplosion)
	{
		this(partie,_ref_fleche,_anim, current_frame,_normalCollision,_pointCollision,
				_correctedPointCollision,groundCollision,_numberExplosion,null);
	}
	public Electrique_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundCollision,int _numberExplosion,Collidable prevCollider)
	{
		super(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,groundCollision,groundCollision);
		TEMPS_DESTRUCTION = 1*(long) Math.pow(10, 8);//nanos, 0.3sec 
		isProjectile =true; //to allow for collision with other projectile and entities
		this.setCollideWithout(Arrays.asList(ObjectType.ELECTRIQUE_EFF,ObjectType.FLECHE));
		numberExplosion=_numberExplosion;
		previousCollider=prevCollider;
		subTypeMouv = groundEffect?EffectCollisionEnum.GROUND:EffectCollisionEnum.ENTITY;
		setDeplacement(new Electrique_idle(subTypeMouv,partie.getFrame()));
		
		partie.arrowsEffects.add(this);
		if(!groundEffect){
			//Try random rotation at most 10 times to avoid this effect to be created in the ground 
			boolean isStuck =true;
			for(int i=0; i<10; ++i){
				if(!isStuck)
					break;
				
				//Is Stuck 
				setRotation(Math.random() * 2* Math.PI);
				setFirstPos(partie);
				onUpdate(partie, false); //update rotated hitbox and drawtr
				
				//extend the hitbox in the movement direction 
				Hitbox extendedHit = Hitbox.directionalExtend(this.getHitbox(partie.INIT_RECT, partie.getScreenDisp()),getRotation(),getDeplacement().getMaxBoundingSquare());
				isStuck = Collision.isWorldCollision(partie,extendedHit , false);
			}
			
			
		}else{
			setFirstPos(partie);
			this.onUpdate(partie, false); //update rotated hitbox and drawtr
		}
	}

	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		if(groundEffect)
			if(Collision.testcollisionObjects(partie, this, attacher,true))
				attacher.conditions.addNewCondition(ConditionEnum.PARALYSIE, DUREE_PARALYSIE,System.identityHashCode(this));
	}

	private void setFirstPos(AbstractModelPartie partie) {

		//get the middle bottom of the effect
		int xdivider = groundEffect? 2:1;

		int x_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/xdivider * Math.cos(getRotation()) - (getDeplacement().ytaille.get(getAnim())) * Math.sin(getRotation()));
		int y_eff_center = (int) (getDeplacement().xtaille.get(getAnim())/xdivider * Math.sin(getRotation()) + (getDeplacement().ytaille.get(getAnim())) * Math.cos(getRotation()));

		Point firstPos = new Point();
		if(groundEffect)
			firstPos = super.setFirstPos(partie,new Point(x_eff_center,y_eff_center));
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip(partie);
			
			firstPos=new Point(arrowTip.x-x_eff_center,arrowTip.y-y_eff_center);

		}
		setXpos_sync(firstPos.x);
		setYpos_sync(firstPos.y);
	}
	
	@Override
	public void handleWorldCollision(Vector2d normal,AbstractModelPartie partie,Collidable collidedObject,boolean stuck)
	{
		if(groundEffect)
			return;
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		//boolean collision_bas = normal.y<0;	
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		localVit= new Vitesse(0,0);
		this.setCollideWithNone();
		this.destroy(partie, stuck);
	}
	
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal)
	{
		if(groundEffect)
			return;
		if(collider == previousCollider)
			return;
		//If collide with the heros, gain seyeri
		if(collider instanceof Heros)
			((Heros)collider).addLife(lifeGained);
		//else exploded in other electrique effect 
		else 
		{
			for(int i=0; i<numberExplosion;++i)
			{
				new Electrique_effect(partie,ref_fleche,0,partie.getFrame(),normal,null,null,false,numberExplosion-1,collider);
			}
			if(collider instanceof Entity)
				((Entity)collider).addLife(damage);
		}
		destroy(partie,true);
	}
	@Override
	public void handleStuck(AbstractModelPartie partie) {
		if(groundEffect)
			return;
		handleWorldCollision( new Vector2d(), partie,null,true );
	}
}
