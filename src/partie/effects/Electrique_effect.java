package partie.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import debug.DebugDraw;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.conditions.Condition.ConditionEnum;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;
import partie.mouvement.effect.electrique.Electrique_appear;
import partie.mouvement.effect.electrique.Electrique_idle;
import partie.mouvement.effect.electrique.Electrique_split;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Electrique_effect extends Effect{
	
	final double DUREE_PARALYSIE= 2;
	final double PULSE_EVERY = 0.5;
	
	int explosionDepth; //number of time that an effect exploded. 0 at start
	int maxExplosionDepth = 0;
	int lifeGained = 10;
	int damage = -5;
	
	Collidable previousCollider = null;
	Collidable currentCollider = null;
	Point previousEffectPos = null;
	public Electrique_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundCollision,int _numberExplosion,int explosionDepth)
	{
		this(_ref_fleche,_mouv_index, current_frame,_normalCollision,_pointCollision,
				_correctedPointCollision,groundCollision,_numberExplosion,explosionDepth,null,null);
	}
	public Electrique_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundCollision,int max_explosion_depth,int explosionDepth,Collidable prevCollider,Point previousEffectPos)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,groundCollision,groundCollision);
		double newScaling = Math.pow(2, -1*explosionDepth);
		setScaling(new Vector2d(newScaling,newScaling));
		TEMPS_DESTRUCTION = 1*(long) Math.pow(10, 8);//nanos, 0.3sec 
		isProjectile =true; //to allow for collision with other projectile and entities
		consequenceUpdateTime = PULSE_EVERY;//s => consequence applied every X while in the effect
		this.setCollideWithout(Arrays.asList(ObjectType.ELECTRIQUE_EFF,ObjectType.FLECHE));
		this.maxExplosionDepth=max_explosion_depth;
		this.explosionDepth=explosionDepth;
		previousCollider=prevCollider;
		this.previousEffectPos = previousEffectPos;
		subTypeMouv = groundEffect?EffectCollisionEnum.GROUND:EffectCollisionEnum.ENTITY;
		if(groundEffect)
			setMouvement(new Electrique_idle(subTypeMouv,ModelPartie.me.getFrame()));
		else
			setMouvement(new Electrique_appear(subTypeMouv,ModelPartie.me.getFrame()));
				
		ModelPartie.me.arrowsEffects.add(this);
		if(!groundEffect){
			//Try random rotation at most 10 times to avoid this effect to be created in the ground 
			boolean isStuck =true;
			for(int i=0; i<10; ++i){
				if(!isStuck)
					break;
				
				//Is Stuck 
				setRotation(Math.random() * 2* Math.PI);
				//setRotation(0);
				setFirstPos();
				
				//extend the hitbox in the movement direction 
				Hitbox extendedHit = Hitbox.directionalExtend(this.getHitbox(ModelPartie.me.INIT_RECT, ModelPartie.me.getScreenDisp()),getRotation(),getMouvement().getMaxBoundingSquare());
				isStuck = Collision.isWorldCollision(extendedHit , false);
			}
			
			
		}else{
			this.setCollideWithout(Arrays.asList(ObjectType.ELECTRIQUE_EFF,ObjectType.PROJECTILE));
			setFirstPos();
		}
		
	}
	
	static void debugDraw(final Point leftPos,final Point rightPos)
	{
		if(ModelPartie.me.debugDraw==null){
			ModelPartie.me.debugDraw = new DebugDraw(){
				@Override
				public void draw(Graphics g){
					g.setColor(Color.red);
					g.fillRect(leftPos.x-5+ModelPartie.me.getScreenDisp().x, leftPos.y-5+ModelPartie.me.getScreenDisp().y, 10, 10);
					g.fillRect(rightPos.x-5+ModelPartie.me.getScreenDisp().x, rightPos.y-5+ModelPartie.me.getScreenDisp().y, 10, 10);
				}
			};
		}
	}
	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}
	@Override
	public void applyConsequence(Entity attacher,boolean isFirstApplication)
	{
		if(groundEffect)
			attacher.conditions.addNewCondition(ConditionEnum.PARALYSIE, DUREE_PARALYSIE,System.identityHashCode(this));
	}
	
	@Override
	protected void onAnimationEnded()
	{
		if(getMouvement().getTypeMouv().equals(MouvEffectEnum.ELECTRIQUE_APPEAR))
		{
			this.setMouvement(new Electrique_idle(EffectCollisionEnum.ENTITY,ModelPartie.me.getFrame()));
			this.setMouvIndex(0);
		}
		else if(getMouvement().getTypeMouv().equals(MouvEffectEnum.ELECTRIQUE_SPLIT))
		{
			//TODO: convert the point to world coordinate (instead of screen ones) ? 
			//TODO: compute the first pos based on those new points 
			//TODO: make it work with scaling 
			Vector2d mid = Hitbox.getObjMid(this);
			double center_offset = 0.45 * getCurrentXtaille();
			Point leftPos = new Point((int)Math.round(mid.x - center_offset*Math.cos(getRotation())) , 
									(int)Math.round(mid.y - center_offset*Math.sin(getRotation())));
			Point rightPos = new Point((int)Math.round(mid.x + center_offset*Math.cos(getRotation())) , 
					(int)Math.round(mid.y + center_offset*Math.sin(getRotation())));
			
			for(int i=0; i<2;++i)
			{
				Point lastPos = i%2==0? leftPos: rightPos;
				new Electrique_effect(ref_fleche,0,ModelPartie.me.getFrame(),normal,null,null,false,maxExplosionDepth,explosionDepth+1,currentCollider,lastPos);
			}
			 destroy(true);
		}
		 else
			destroy(true);
		
	}
	private void setFirstPos() {

		Point eff_center = groundEffect? getBottomOfTaille() : getCenterOfTaille();

		 Point firstPos = new Point();
		 if(groundEffect || (!groundEffect && previousEffectPos==null))
			 firstPos = super.setFirstPos(eff_center);
		 else{
			 firstPos = new Point(previousEffectPos.x-eff_center.x,previousEffectPos.y-eff_center.y);
		 }
		 setXpos_sync(firstPos.x);
		 setYpos_sync(firstPos.y);
	}
	
	@Override
	public void handleWorldCollision(Vector2d normal,Collidable collidedObject,boolean stuck)
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
		this.destroy(stuck);
	}
	
	@Override
	public void handleObjectCollision(Collidable collider,Vector2d normal)
	{
		if(groundEffect)
			return;
		if(collider == previousCollider)
			return;
		//If collide with the heros, gain seyeri
		if(collider instanceof Heros){
			((Heros)collider).addLife(lifeGained);
			this.destroy(true);
		}
		//else exploded in other electrique effect 
		else 
		{
			this.setCollideWithNone();
			if(explosionDepth < (maxExplosionDepth-1) ){
				this.setMouvIndex(0);
				this.setMouvement(new Electrique_split(EffectCollisionEnum.ENTITY,ModelPartie.me.getFrame()));
				currentCollider = collider;
			}
			else
				this.destroy(true);
			if(collider instanceof Entity)
				((Entity)collider).addLife(damage);
		}
	}
	@Override
	public void handleStuck() {
		if(groundEffect)
			return;
		handleWorldCollision( new Vector2d(), null,true );
	}
}
