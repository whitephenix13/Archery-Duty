package partie.projectile.fleches.sprirituelle;

import java.util.List;

import javax.vecmath.Vector2d;

import music.MusicBruitage;
import partie.collision.Collidable;
import partie.effects.Effect;
import partie.effects.Grappin_effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.mouvement.Deplace;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Fleche_grappin extends Spirituelle {
	
	// WARNING : effect moves with 
	//	-colliding entity        			YES 
	//  -colliding ground (ie roche_effect) YES
	
	private boolean destroy_next_frame=false;
	public Entity collider = null;
	private boolean dragSomething = false; // boolean to make sure that at most one object is dragged

	public Fleche_grappin(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor)
	{
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		MAX_NUMBER_INSTANCE=1;
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.5 sec 
		damage=0*damageMult;
		seyeri_cost = -10;
	}
	

	//only move arrow if the grappin length is long enough
	@Override
	public boolean updateMouvementBasedOnAnimation() {
		if(collider!=null)
			if(collider.getNeedDestroy()){
				collider=null;
				destroy(false);
			}
		//doitDeplace, animationChanged
		max_speed_norm = -1;
		
		Grappin_effect eff = (Grappin_effect) this.flecheEffect;
		if(eff!= null && eff.shooterDragged){
			this.setLocalVit(eff.getModifiedVitesse(this));
		}
		boolean updated = super.updateMouvementBasedOnAnimation();
		double speedNorm = this.getGlobalVit().norm();
		if(generatedEffect && (this.tempsDetruit==0) && (!this.getNeedDestroy()) )
		{
			double rope_remaining_length = eff.getRemainingLength();
			//there is a 1-1 correspondance between speed and displacement
			if(rope_remaining_length<=0 || eff.isEnded() || eff.tempsDetruit>0 || destroy_next_frame)
			{
				shouldMove=false;
				if(this.tempsDetruit<=0)
				{
					eff.reached_max_length=true;
					destroy(false);
					shouldMove=false;
				}
			}
			else if(speedNorm>rope_remaining_length)
			{
				max_speed_norm=rope_remaining_length;
				destroy_next_frame=true;
			} 
		}
		return updated;
	}

	@Override
	protected void onPlanted(List<Entity> objects,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		//Handle case where effect is destroyed but last compute is true just for display purpose
		if(flecheEffect ==null)
			return;
		
		if(this.afterDecochee && stuck)
			ejectArrow(unprojectedSpeed);
		if(stuck){
			destroy(false);
			return;
		}

		if((!this.needDestroy || this.tempsDetruit>0) && !dragSomething)
		{
			//planted is only called if the arrow collide with the world hence the grappin applies on the shooter
			this.setCollideWithNone();
			this.getMouvement().stopSpeed=true;
			this.shouldMove=true; //force this to keep the 
			if(!stuck)
			{
				shooter.registerEffect(flecheEffect);
				shooter.localVit= new Vitesse(0,0);
				Grappin_effect grap = ((Grappin_effect)flecheEffect);
				grap.isDragging=true;
				grap.shooterDragged=true;
				dragSomething=true;
				Roche_effect.synchroniseMovementWithRocheEffectMovement(collidedObject, new Collidable[] {this});
			}
			else
			{
				this.destroy( false);
			}
		}
	}


	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(unprojectedSpeed);

		boolean isColliderEntitie = collider instanceof Entity;
		Entity colliderEntitie = isColliderEntitie?  (Entity) collider : null;

		if(colliderEntitie != null && colliderEntitie.draggable && (!this.needDestroy || this.tempsDetruit>0) && !dragSomething )
		{
			this.collider= colliderEntitie;
			this.setCollideWithNone();
			//this.doitDeplace=false; keep moving the arrow towards the hero
			collider.addSynchroSpeed(this);
			//both arrow and object are pulled toward the hero
			colliderEntitie.registerEffect(flecheEffect);
			collider.localVit= new Vitesse(0,0);
			Grappin_effect grap = ((Grappin_effect)flecheEffect);
			grap.shooterDragged=false;
			grap.isDragging=true;
			dragSomething=true;
			return false;
		}
		if(!dragSomething)
			return true;
		else
			return false;
	}


	@Override
	public void OnShoot()
	{
		super.OnShoot();
		if(!generatedEffect){
			generatedEffect=true;
			flecheEffect=new Grappin_effect(this,0,ModelPartie.me.getFrame(),shooter);
			//TODO: sound grappin
			MusicBruitage.me.startBruitage("arc");
		}

	}
	@Override
	public boolean OnArrowReshot( Fleche firstFleche)
	{
		if(!getNeedDestroy() && (tempsDetruit==0))
			destroy(true);
		return false;
	}
	@Override
	public void beforeFlecheDestroyed()
	{
		Grappin_effect grap = ((Grappin_effect)flecheEffect);
		if(grap != null){
			grap.isDragging=false;
		}


	}
}
