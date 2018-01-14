package fleches.materielle;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Deplace;
import effects.Effect;
import effects.Feu_effect;
import effects.Roche_effect;
import fleches.Fleche;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Projectile;

public class Fleche_feu extends Materielle {

	public Fleche_feu(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=-10*damageMult;
		seyeri_cost = -10;
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
			Vector2d arrowDir = Deplace.angleToVector(rotation);
			int sign = ((this.normCollision.x<0 && arrowDir.y<0) || (this.normCollision.y<0 && arrowDir.x<0))?-1 : 1;
			Feu_effect flecheEffect=new Feu_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision,0,0);
			Feu_effect flecheEffect2=new Feu_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision,0,sign*40);
			Feu_effect flecheEffect3=new Feu_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision,0,sign*80);

			for(Entitie obj : objects)
			{
				obj.currentEffects.add(flecheEffect);
				obj.currentEffects.add(flecheEffect2);
				obj.currentEffects.add(flecheEffect3);
			}

			if(collidedObject instanceof Roche_effect)
			{
				Roche_effect eff = (Roche_effect) collidedObject;
				if(eff.isWorldCollider){
					eff.addSynchroSpeed(this);
					eff.addSynchroSpeed(flecheEffect);
					eff.addSynchroSpeed(flecheEffect2);
					eff.addSynchroSpeed(flecheEffect3);
				}

			}
			MusicBruitage.startBruitage("arc");

		}
	}

	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);
		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Feu_effect(partie,this,0,partie.getFrame(),normal,null,null,1,0);
			MusicBruitage.startBruitage("arc");
			for(Entitie obj : objects)
			{
				obj.currentEffects.add(this.flecheEffect);
			}
			this.doitDeplace=false;
			this.setCollideWithNone();
		}
		return true;
	}
}
