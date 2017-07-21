package fleches;

import java.util.List;

import collision.Collidable;
import deplacement.Deplace;
import effects.Effect;
import effects.Grappin_effect;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Vitesse;

public class Fleche_grappin extends Fleche {

	private boolean destroy_next_frame=false;
	public Collidable collider = null;
	private boolean dragSomething = false; // boolean to make sure that at most one object is dragged
	
	public Fleche_grappin(List<Fleche> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor)
	{
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		no_more_than_one=true;
		destroy_on_click=true;
		type_fleche=SPIRITUELLE.GRAPPIN;
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.5 sec 
		damage=0*damageMult;
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
		if(generatedEffect)
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
					eff.destroy(partie, false);
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
	protected void onPlanted(List<Collidable> objects,AbstractModelPartie partie)
	{
		if((!this.needDestroy || this.tempsDetruit>0) && !dragSomething)
		{
			//planted is only called if the arrow collide with the world hence the grappin applies on the shooter
			this.checkCollision=false;
			this.doitDeplace=false;
			shooter.registerEffect(flecheEffect);
			shooter.localVit= new Vitesse(0,0);
			Grappin_effect grap = ((Grappin_effect)flecheEffect);
			grap.isDragging=true;
			grap.shooterDragged=true;
			dragSomething=true;
		}
	}


	@Override
	protected boolean OnObjectsCollision(List<Collidable> objects,AbstractModelPartie partie,Collidable collider)
	{
		if(collider.draggable && (!this.needDestroy || this.tempsDetruit>0) && !dragSomething )
		{
			this.collider= collider;
			this.checkCollision=false;
			this.doitDeplace=false;
			collider.addSynchroSpeed(this);
			//both arrow and object are pulled toward the hero
			collider.registerEffect(flecheEffect);
			collider.localVit= new Vitesse(0,0);
			Grappin_effect grap = ((Grappin_effect)flecheEffect);
			grap.shooterDragged=false;
			grap.isDragging=true;
			dragSomething=true;
			return false;
		}
		else if(!dragSomething)
			return true;
		else
			return false;
	}


	@Override
	public void flecheDecochee(AbstractModelPartie partie,Deplace deplace)
	{
		super.flecheDecochee(partie, deplace);
		if(!generatedEffect){
			generatedEffect=true;
			flecheEffect=new Grappin_effect(partie,this,0,partie.getFrame(),shooter);
			//TODO: sound grappin
			//MusicBruitage.me.startBruitage("vent_effect");
		}

	}
	@Override
	public void beforeFlecheDestroyed(AbstractModelPartie partie)
	{
		Grappin_effect grap = ((Grappin_effect)flecheEffect);
		grap.isDragging=false;

		//unregister shooter and collider 
		if(shooter!=null)
			shooter.unregisterEffect(partie, grap);
		if(collider!=null)
			collider.unregisterEffect(partie, grap);



	}

}
