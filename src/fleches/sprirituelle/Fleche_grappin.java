package fleches.sprirituelle;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Deplace;
import effects.Effect;
import effects.Grappin_effect;
import effects.Roche_effect;
import fleches.Fleche;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Projectile;
import types.Vitesse;

public class Fleche_grappin extends Spirituelle {

	private boolean destroy_next_frame=false;
	public Entitie collider = null;
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
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		
		if(collider!=null)
			if(collider.getNeedDestroy()){
				collider=null;
				destroy(partie,false);
			}
		//doitDeplace, animationChanged
		max_speed_norm = -1;
		boolean[] res = super.deplace(partie, deplace);
		double speedNorm = this.getGlobalVit(partie).norm();
		if(generatedEffect && (this.tempsDetruit==0) && (!this.getNeedDestroy()) )
		{
			Grappin_effect eff = (Grappin_effect) this.flecheEffect;

			double rope_remaining_length = eff.getRemainingLength();
			//there is a 1-1 correspondance between speed and displacement
			if(rope_remaining_length<=0 || eff.isEnded() || eff.tempsDetruit>0 || destroy_next_frame)
			{
				res[0]=false;
				if(this.tempsDetruit<=0)
				{
					eff.reached_max_length=true;
					//eff.destroy(partie, false);
					destroy(partie,false);
					doitDeplace=false;
				}
			}
			else if(speedNorm>rope_remaining_length)
			{
				max_speed_norm=rope_remaining_length;
				destroy_next_frame=true;
			} 
		}
		return res;
	}

	@Override
	protected void onPlanted(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		//Handle case where effect is destroyed but last compute is true just for display purpose
		if(flecheEffect ==null)
			return;
		
		if(this.afterDecochee && stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck){
			destroy(partie,false);
			return;
		}

		if((!this.needDestroy || this.tempsDetruit>0) && !dragSomething)
		{
			//planted is only called if the arrow collide with the world hence the grappin applies on the shooter
			this.setCollideWithNone();
			this.doitDeplace=false;
			if(!stuck)
			{
				shooter.registerEffect(flecheEffect);
				shooter.localVit= new Vitesse(0,0);
				Grappin_effect grap = ((Grappin_effect)flecheEffect);
				grap.isDragging=true;
				grap.shooterDragged=true;
				dragSomething=true;
				if(collidedObject instanceof Roche_effect)
				{
					Roche_effect eff = (Roche_effect) collidedObject;
					if(eff.isWorldCollider){
						eff.addSynchroSpeed(this);
						eff.addSynchroSpeed(flecheEffect);
					}
				}
			}
			else
			{
				this.destroy(partie, false);
			}
		}
	}


	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);

		boolean isColliderEntitie = collider instanceof Entitie;
		Entitie colliderEntitie = isColliderEntitie?  (Entitie) collider : null;

		if(colliderEntitie != null && colliderEntitie.draggable && (!this.needDestroy || this.tempsDetruit>0) && !dragSomething )
		{
			this.collider= colliderEntitie;
			this.setCollideWithNone();
			this.doitDeplace=false;
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
	public void OnShoot(AbstractModelPartie partie,Deplace deplace)
	{
		super.OnShoot(partie, deplace);
		if(!generatedEffect){
			generatedEffect=true;
			flecheEffect=new Grappin_effect(partie,this,0,partie.getFrame(),shooter);
			//TODO: sound grappin
			MusicBruitage.startBruitage("arc");
		}

	}
	@Override
	public boolean OnArrowReshot(AbstractModelPartie partie, Fleche firstFleche)
	{
		if(!getNeedDestroy() && (tempsDetruit==0))
			destroy(partie,true);
		return false;
	}
	@Override
	public void beforeFlecheDestroyed(AbstractModelPartie partie)
	{
		Grappin_effect grap = ((Grappin_effect)flecheEffect);
		if(grap != null){
			grap.isDragging=false;

			//unregister shooter and collider 
			if(shooter!=null)
				shooter.unregisterEffect(partie, grap);
			if(collider!=null)
				collider.unregisterEffect(partie, grap);
		}


	}

}
