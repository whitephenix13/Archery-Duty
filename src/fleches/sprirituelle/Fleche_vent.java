package fleches.sprirituelle;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import effects.Effect;
import effects.Roche_effect;
import effects.Vent_effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Projectile;
import types.Vitesse;

public class Fleche_vent extends Spirituelle{

	private boolean arrowExploded = false; // do not collide with more than one object 
	public Fleche_vent(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor)
	{
		super(tabFleche,current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
	}
	@Override
	protected void onPlanted(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee&& stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck)
		{
			this.destroy(partie,false);
			return;
		}
		if(arrowExploded)
			return;

		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Vent_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision);
			MusicBruitage.startBruitage("vent_effect");
		}
		for(Entitie obj : objects)
		{
			obj.registerEffect(flecheEffect);
			obj.localVit= new Vitesse(0,0);
		}

		boolean stickToHeros =false;
		//If the arrow is planted on the ground and collide with the heros hitbox, attach it to the heros
		if(Collision.testcollisionObjects(partie, this, partie.heros,true)){
			partie.heros.addSynchroSpeed(this);
			partie.heros.addSynchroSpeed(flecheEffect);
			Vent_effect eff = (Vent_effect) flecheEffect;
			eff.stickedCollidable=this.shooter;
			stickToHeros=true;
		}
		if(!stickToHeros && (collidedObject instanceof Roche_effect))
		{
			Roche_effect eff = (Roche_effect) collidedObject;
			if(eff.isWorldCollider){
				eff.addSynchroSpeed(this);
				eff.addSynchroSpeed(flecheEffect);
			}
		}
		this.doitDeplace=false;
		this.setCollideWithNone();
		arrowExploded=true;
	}

	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);

		if(arrowExploded)
			return false;

		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Vent_effect(partie,this,0,partie.getFrame(),normal,null,null);
			MusicBruitage.startBruitage("vent_effect");
		}
		for(Entitie obj : objects)
		{
			obj.registerEffect(flecheEffect);
			obj.localVit= new Vitesse(0,0);
		}
		Vent_effect eff = (Vent_effect) flecheEffect;
		eff.stickedCollidable=collider;
		collider.addSynchroSpeed(this);
		this.doitDeplace=false;
		this.setCollideWithNone();
		//need destroy after collision : false, it will be destroy after the end of the effect 
		arrowExploded=true;
		return false;

	}

	@Override
	public void beforeFlecheDestroyed(AbstractModelPartie partie)
	{
		//nothing todo with ref_fleche before destruction
	}
}
