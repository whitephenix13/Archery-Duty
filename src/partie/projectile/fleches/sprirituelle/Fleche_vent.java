package partie.projectile.fleches.sprirituelle;

import java.awt.Polygon;
import java.util.List;

import javax.vecmath.Vector2d;

import music.MusicBruitage;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.effects.Effect;
import partie.effects.Roche_effect;
import partie.effects.Vent_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.ModelPartie;
import partie.projectile.Projectile;
import utils.PointHelper;
import utils.Vitesse;

public class Fleche_vent extends Spirituelle{
	
	// WARNING : effect moves with 
	//	-colliding entity        			NO 
	//  -colliding ground (ie roche_effect) YES
	
	private boolean arrowExploded = false; // do not collide with more than one object 
	public Fleche_vent(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor)
	{
		super(tabFleche,current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
		seyeri_cost = -5;
	}
	@Override
	protected void onPlanted(List<Entity> objects,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee&& stuck)
			ejectArrow(unprojectedSpeed);
		if(stuck)
		{
			this.destroy(false);
			return;
		}
		if(arrowExploded)
			return;

		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Vent_effect(this,0,ModelPartie.me.getFrame(),normCollision,pointCollision,correctedPointCollision);
			MusicBruitage.me.startBruitage("vent_effect");
		}
		/*for(Entitie obj : objects)
		{
			obj.registerEffect(flecheEffect);
			obj.localVit= new Vitesse(0,0);
		}*/

		Roche_effect.synchroniseMovementWithRocheEffectMovement(collidedObject, new Collidable[] {this,flecheEffect});

		this.simulateDestroy();
		arrowExploded=true;
	}

	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(unprojectedSpeed);

		if(arrowExploded)
			return false;

		//Compute speed for collider: specific case because collision point is not exact 
		Polygon arrowPol = this.getHitbox(ModelPartie.me.INIT_RECT, ModelPartie.me.getScreenDisp()).polygon;
		Hitbox colliderHitbox = collider.getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp());
		Vector2d intersectPoint = null;
		//project each border to the hitbox to find one point of collision
		for(int i=0; i<arrowPol.npoints;++i)
		{
			Vector2d p = new Vector2d(arrowPol.xpoints[i],arrowPol.ypoints[i]);
			intersectPoint = Hitbox.projectOnHitbox(colliderHitbox, p, getGlobalVit().negated());
			if(intersectPoint != null)
				break;
		}

		if(!generatedEffect){
			generatedEffect=true;
			flecheEffect=new Vent_effect(this,0,ModelPartie.me.getFrame(),normal,null,null);
			Vent_effect ventEffect = (Vent_effect) flecheEffect;

			Vector2d objMid = Hitbox.getObjMid( collider) ;
			
			//Compute the speed with respect to the intersection point and the middle of the collider hitbox
			Vitesse projectionSpeed = ventEffect.computeProjectSpeed(objMid,
					PointHelper.VecToPoint(intersectPoint),1000000,ventEffect.getMouvIndex());
			((Vent_effect)flecheEffect).setCollidedObject(collider,projectionSpeed);
			MusicBruitage.me.startBruitage("vent_effect");
		}
		for(Entity obj : objects)
		{
			obj.registerEffect(flecheEffect);
			obj.localVit= new Vitesse(0,0);

		}

		this.simulateDestroy();
		//need destroy after collision : false, it will be destroy after the end of the effect 
		arrowExploded=true;
		return false;

	}

	@Override
	public void beforeFlecheDestroyed()
	{
		//nothing to do with ref_fleche before destruction
	}
}
