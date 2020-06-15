package partie.projectile.fleches.sprirituelle;

import java.awt.Point;
import java.util.List;

import javax.vecmath.Vector2d;

import music.MusicBruitage;
import partie.collision.Collidable;
import partie.effects.Effect;
import partie.effects.Lumiere_effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.ModelPartie;
import partie.projectile.Projectile;

public class Fleche_lumiere extends Spirituelle {
	
	// WARNING : effect moves with 
	//	-colliding entity        			YES 
	//  -colliding ground (ie roche_effect) YES
	
	public Fleche_lumiere(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
		seyeri_cost = -15;
	}

	void applyArrowEffect(List<Entity> objects,Collidable collidedObject,Vector2d normal,Point pColli, Point correctedPColli)
	{
		if(generatedEffect)
			return;

		generatedEffect=true;

		flecheEffect=new Lumiere_effect(this,0,ModelPartie.me.getFrame(),normal,pColli,correctedPColli);
		MusicBruitage.me.startBruitage("arc");

		for(Entity obj : objects)
		{
			obj.registerEffect(this.flecheEffect);
		}
		Roche_effect.synchroniseMovementWithRocheEffectMovement(collidedObject, new Collidable[] {this,flecheEffect});

		this.simulateDestroy();

	}
	@Override
	protected void onPlanted(List<Entity> objects,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(unprojectedSpeed);
		if(stuck)
			destroy(false);
		else
			applyArrowEffect(objects,collidedObject,normCollision,pointCollision,correctedPointCollision);
	}
	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(unprojectedSpeed);

		applyArrowEffect(objects,collider,normal,null,null);
		return false;
	}

}
