package fleches.materielle;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import effects.Effect;
import effects.Roche_effect;
import fleches.Fleche;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Bloc;
import types.Entitie;
import types.Projectile;

public class Fleche_roche extends Materielle {

	public Fleche_roche(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		this.MAX_NUMBER_INSTANCE=3;//3
		damage=0*damageMult;//TODO:
	}

	@Override
	protected void onPlanted(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck){
			Collidable[] collidedObjects = {new Bloc()};
			stuck  = !ejectArrow(partie,unprojectedSpeed,collidedObjects);
			if(!(collidedObjects[0] instanceof Bloc) || !((Bloc) collidedObjects[0]).isVide())
				collidedObject=collidedObjects[0];
		}
		if(stuck){
			destroy(partie,false);
			return;
		}
		if(!generatedEffect){
			generatedEffect=true;
			flecheEffect=new Roche_effect(partie,this,0,partie.getFrame(),this.normCollision,this.pointCollision,this.correctedPointCollision,0);
			if(collidedObject instanceof Roche_effect)
			{
				Roche_effect eff = (Roche_effect) collidedObject;
				if(eff.isWorldCollider){
					eff.addSynchroSpeed(this);
					//Only add as synchro speed if the effect is prependicular
					double deltaRot = Math.abs(eff.rotation-flecheEffect.rotation);
					if((deltaRot == Math.PI/2)|| (deltaRot == 3* Math.PI/2) )
						eff.addSynchroSpeed(flecheEffect);
				}
			}
			if(!flecheEffect.getNeedDestroy())
				this.isVisible=false;
			MusicBruitage.startBruitage("arc");
		}
	}

	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{

		if(this.afterDecochee && (collider instanceof Effect)){
			if(((Effect)collider).isWorldCollider){
				Collidable[] collidedObjects = {new Bloc()};
				ejectArrow(partie,unprojectedSpeed,collidedObjects);
				if(!(collidedObjects[0] instanceof Bloc) || !((Bloc) collidedObjects[0]).isVide())
					collider=collidedObjects[0];
			}
		}

		if(!generatedEffect){

			generatedEffect=true;

			flecheEffect=new Roche_effect(partie,this,0,partie.getFrame(),normal,null,null,1);
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

	@Override
	public boolean shouldCountArrow(Fleche f)
	{
		if(f instanceof Fleche_roche){
			Fleche_roche f_roche = (Fleche_roche) f;
			if(f_roche.flecheEffect != null)
			{
				Roche_effect e_effect = (Roche_effect) f_roche.flecheEffect;
				if(e_effect.startDestroyAnim)
					return false;
			}
		}

		return true;
	}
	@Override
	public boolean OnArrowReshot(AbstractModelPartie partie, Fleche firstFleche)
	{
		if(firstFleche.flecheEffect!=null && (firstFleche.flecheEffect.typeEffect==0))
			((Roche_effect)firstFleche.flecheEffect).startDestroyAnim(partie.getFrame(), true);
		else
			firstFleche.destroy(partie, true);
		return true;
	}
}
