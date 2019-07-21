package partie.projectile.fleches.materielle;

import java.util.List;

import javax.vecmath.Vector2d;

import music.MusicBruitage;
import partie.collision.Collidable;
import partie.effects.Effect;
import partie.effects.Electrique_effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.Projectile;

public class Fleche_electrique extends Materielle {
	// WARNING : effect moves with 
	//	-colliding entity        			NO 
	//  -colliding ground (ie roche_effect) YES
	
	int nb_effect = 5;
	int numberExplosion = 3;

	public Fleche_electrique(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=-5*damageMult;
		seyeri_cost=-15;
	}
	

	@Override
	protected void onPlanted(List<Entity> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck){
			destroy(partie,false);
			return;
		}
		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Electrique_effect(partie,this,0,partie.getFrame(),this.normCollision,this.pointCollision,this.correctedPointCollision,true,numberExplosion);
			MusicBruitage.me.startBruitage("arc");

			Roche_effect.synchroniseMovementWithRocheEffectMovement(collidedObject, new Collidable[] {this,flecheEffect});

			this.doitDeplace=false;
			this.setCollideWithNone();
			this.isVisible=false;
		}
	}

	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);
		if(!generatedEffect){
			generatedEffect=true;

			for(int i =0; i< nb_effect; i++){
				new Electrique_effect(partie,this,0,partie.getFrame(),normal,null,null,false,numberExplosion,collider);
			}
			
			MusicBruitage.me.startBruitage("arc");
			
			//Hide the arrow but don't destroy it otherwise the effect position is no longer updated
			this.isVisible=false;
			return false;

		}
		return true;
	}
}