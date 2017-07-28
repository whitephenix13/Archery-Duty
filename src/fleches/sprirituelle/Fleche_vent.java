package fleches.sprirituelle;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
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
	protected void onPlanted(List<Entitie> objects, AbstractModelPartie partie,boolean stuck)
	{
		if(stuck)
		{
			this.destroy(partie,false);
			return;
		}
		if(arrowExploded)
			return;

		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Vent_effect(partie,this,0,partie.getFrame());
			MusicBruitage.startBruitage("vent_effect");
		}
		for(Entitie obj : objects)
		{
			obj.registerEffect(flecheEffect);
			obj.localVit= new Vitesse(0,0);
		}
		//If the arrow is planted on the ground and collide with the heros hitbox, attach it to the heros
		if(Collision.testcollisionObjects(partie, this, partie.heros,true)){
			partie.heros.addSynchroSpeed(this);
			Vent_effect eff = (Vent_effect) flecheEffect;
			eff.stickedCollidable=this.shooter;
		}
		this.doitDeplace=false;
		this.setCollideWithNone();
		arrowExploded=true;
	}
	
	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d normal)
	{
		if(arrowExploded)
			return false;

		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Vent_effect(partie,this,0,partie.getFrame());
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
