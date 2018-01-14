package fleches.destructrice;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import effects.Roche_effect;
import effects.Trou_noir_effect;
import fleches.Fleche;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Hitbox;
import types.Projectile;
import types.Vitesse;

public class Fleche_trou_noir extends Destructrice {

	public Fleche_trou_noir(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;//TODO:
	}
	
	@Override
	protected void onPlanted(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck){
			destroy(partie,false);
			return;
		}
		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Trou_noir_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision);
			MusicBruitage.startBruitage("arc");
			if(collidedObject instanceof Roche_effect)
			{
				Roche_effect eff = (Roche_effect) collidedObject;
				if(eff.isWorldCollider){
					eff.addSynchroSpeed(this);
					eff.addSynchroSpeed(flecheEffect);
				}
			}
		}

	}


}
