package partie.projectile.fleches.materielle;

import java.util.List;

import javax.vecmath.Vector2d;

import music.MusicBruitage;
import partie.collision.Collidable;
import partie.effects.Effect;
import partie.effects.Feu_effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Deplace;
import partie.projectile.Projectile;

public class Fleche_feu extends Materielle {
	// WARNING : effect moves with 
	//	-colliding entity        			YES 
	//  -colliding ground (ie roche_effect) YES
	
	public Fleche_feu(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=-10*damageMult;
		seyeri_cost = -10;
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
			Vector2d arrowDir = Deplace.angleToVector(getRotation());
			int sign = ((this.normCollision.x<0 && arrowDir.y<0) || (this.normCollision.y<0 && arrowDir.x<0))?-1 : 1;
			//creating the effect registers it to arrowsEffects from partie 
			Feu_effect flecheEffect=new Feu_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision,true,-1*sign*40);
			Feu_effect flecheEffect2=new Feu_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision,true,0);
			Feu_effect flecheEffect3=new Feu_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision,true,sign*40);
			
			
			Roche_effect.synchroniseMovementWithRocheEffectMovement(collidedObject, new Collidable[] {this,flecheEffect,flecheEffect2,flecheEffect3});

			this.simulateDestroy();
			MusicBruitage.me.startBruitage("arc");

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

			flecheEffect=new Feu_effect(partie,this,0,partie.getFrame(),normal,null,null,false,0);
			MusicBruitage.me.startBruitage("arc");
			
			for(Entity obj : objects)
			{
				obj.registerEffect(this.flecheEffect);
			}
			
			//Hide the arrow but don't destroy it otherwise the effect position is no longer updated
			this.simulateDestroy();
			return false;
		}
		return true;
	}
}
