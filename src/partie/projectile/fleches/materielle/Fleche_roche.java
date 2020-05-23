package partie.projectile.fleches.materielle;

import java.util.List;

import javax.vecmath.Vector2d;

import menu.menuPrincipal.ModelPrincipal;
import music.MusicBruitage;
import partie.bloc.Bloc;
import partie.collision.Collidable;
import partie.effects.Effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;

public class Fleche_roche extends Materielle {
	
	// WARNING : effect moves with 
	//	-colliding entity        			YES 
	//  -colliding ground (ie roche_effect) YES
	public Fleche_roche(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		this.MAX_NUMBER_INSTANCE=3;//3
		damage=-17*damageMult;
		seyeri_cost= -23;
	}
	
	@Override
	protected void onPlanted(List<Entity> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		ModelPrincipal.debugTime.startElapsedForVerbose();
		if(this.afterDecochee && stuck){
			Collidable[] collidedObjects = {new Bloc()};
			stuck  = !ejectArrow(partie,unprojectedSpeed,collidedObjects);
			if(!(collidedObjects[0] instanceof Bloc) || collidedObjects==null)
				collidedObject=collidedObjects[0];
		}
		ModelPrincipal.debugTime.elapsed("Fleche roche: After eject arrow");
		if(stuck){
			destroy(partie,false);
			return;
		}
		if(!generatedEffect){
			generatedEffect=true;
			flecheEffect=new Roche_effect(partie,this,0,partie.getFrame(),this.normCollision,this.pointCollision,this.correctedPointCollision,true);
			ModelPrincipal.debugTime.elapsed("Fleche roche: After generating roche effect");
			if(collidedObject instanceof Roche_effect)
			{
				Roche_effect eff = (Roche_effect) collidedObject;
				if(eff.isWorldCollider){
					eff.addSynchroSpeed(this);
					//Only add as synchro speed if the effect is prependicular
					double deltaRot = Math.abs(eff.getRotation()-flecheEffect.getRotation());
					if((deltaRot == Math.PI/2)|| (deltaRot == 3* Math.PI/2) )
						eff.addSynchroSpeed(flecheEffect);
				}
			}
			ModelPrincipal.debugTime.elapsed("Fleche roche: After synchro speed with other roche");
			if(!flecheEffect.getNeedDestroy())
				this.simulateDestroy();
			ModelPrincipal.debugTime.elapsed("Fleche roche: After set fleche invisible");
			MusicBruitage.me.startBruitage("arc");
			ModelPrincipal.debugTime.elapsed("Fleche roche: After start bruitage");
		}
	}

	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{

		if(this.afterDecochee && (collider instanceof Effect)){
			if(((Effect)collider).isWorldCollider){
				Collidable[] collidedObjects = {new Bloc()};
				ejectArrow(partie,unprojectedSpeed,collidedObjects);
				if(!(collidedObjects[0] instanceof Bloc) || collidedObjects[0]==null)
					collider=collidedObjects[0];
			}
		}

		if(!generatedEffect){

			generatedEffect=true;

			flecheEffect=new Roche_effect(partie,this,0,partie.getFrame(),normal,null,null,false);
			MusicBruitage.me.startBruitage("arc");

			//Hide the arrow but don't destroy it otherwise the effect position is no longer updated
			this.simulateDestroy();
			return false;
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
		if(firstFleche.flecheEffect!=null && (firstFleche.flecheEffect.groundEffect))
			((Roche_effect)firstFleche.flecheEffect).startDestroyAnim(partie.getFrame(), true);
		else
			firstFleche.destroy(partie, true);
		return true;
	}
}
