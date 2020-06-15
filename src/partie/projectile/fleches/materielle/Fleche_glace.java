package partie.projectile.fleches.materielle;

import java.util.List;

import javax.vecmath.Vector2d;

import music.MusicBruitage;
import partie.collision.Collidable;
import partie.effects.Effect;
import partie.effects.Glace_effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.ModelPartie;
import partie.projectile.Projectile;

public class Fleche_glace extends Materielle {
	// WARNING : effect moves with 
	//	-colliding entity        			YES 
	//  -colliding ground (ie roche_effect) YES
	
	int damage_init = -10;
	public Fleche_glace(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=damage_init*damageMult;
		seyeri_cost = -8;
	}
	
	@Override
	protected void onPlanted(List<Entity> objects,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(unprojectedSpeed);
		if(stuck){
			destroy(false);
			return;
		}
		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Glace_effect(this,0,ModelPartie.me.getFrame(),normCollision,pointCollision,correctedPointCollision,true,damage_init);
			MusicBruitage.me.startBruitage("arc");
			Roche_effect.synchroniseMovementWithRocheEffectMovement(collidedObject, new Collidable[] {this,flecheEffect});

			this.simulateDestroy();
		}
	}

	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(unprojectedSpeed);

		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Glace_effect(this,0,ModelPartie.me.getFrame(),normal,null,null,false,damage_init);
			MusicBruitage.me.startBruitage("arc");

			//Hide the arrow but don't destroy it otherwise the effect position is no longer updated
			this.simulateDestroy();
			return false;
		}
		return true;
	}
}
