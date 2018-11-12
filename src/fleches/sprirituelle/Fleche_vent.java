package fleches.sprirituelle;

import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import effects.Effect;
import effects.Roche_effect;
import effects.Vent_effect;
import fleches.Fleche;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Hitbox;
import types.Projectile;
import types.Vitesse;

public class Fleche_vent extends Spirituelle{

	private boolean arrowExploded = false; // do not collide with more than one object 
	public Fleche_vent(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor)
	{
		super(tabFleche,current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
		seyeri_cost = -5;
	}
	@Override
	protected void onPlanted(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee&& stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck)
		{
			this.destroy(partie,false);
			return;
		}
		if(arrowExploded)
			return;

		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Vent_effect(partie,this,0,partie.getFrame(),normCollision,pointCollision,correctedPointCollision,false);
			MusicBruitage.startBruitage("vent_effect");
		}
		for(Entitie obj : objects)
		{
			obj.registerEffect(flecheEffect);
			obj.localVit= new Vitesse(0,0);
		}

		if((collidedObject instanceof Roche_effect))
		{
			Roche_effect eff = (Roche_effect) collidedObject;
			if(eff.isWorldCollider){
				eff.addSynchroSpeed(this);
				eff.addSynchroSpeed(flecheEffect);
			}
		}
		this.doitDeplace=false;
		this.setCollideWithNone();
		arrowExploded=true;
	}

	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);

		if(arrowExploded)
			return false;

		//Compute speed for collider: specific case because collision point is not exact 
		Polygon arrowPol = this.getHitbox(partie.INIT_RECT, partie.getScreenDisp()).polygon;
		Hitbox colliderHitbox = collider.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
		Vector2d intersectPoint = null;
		//project each border to the hitbox to find one point of collision
		for(int i=0; i<arrowPol.npoints;++i)
		{
			Vector2d p = new Vector2d(arrowPol.xpoints[i],arrowPol.ypoints[i]);
			intersectPoint = Hitbox.projectOnHitbox(colliderHitbox, p, getGlobalVit(partie).negated().vect2d());
			if(intersectPoint != null)
				break;
		}

		if(!generatedEffect){
			generatedEffect=true;
			flecheEffect=new Vent_effect(partie,this,0,partie.getFrame(),normal,null,null,true);
			Vent_effect ventEffect = (Vent_effect) flecheEffect;

			Vector2d objMid = Hitbox.getObjMid(partie, collider) ;
			
			//Compute the speed with respect to the intersection point and the middle of the collider hitbox
			Vitesse projectionSpeed = ventEffect.computeProjectSpeed(partie,objMid,
					new Point((int)intersectPoint.x,(int)intersectPoint.y),1000000,ventEffect.anim);
			((Vent_effect)flecheEffect).SetCollidedObject(collider,projectionSpeed);
			MusicBruitage.startBruitage("vent_effect");
		}
		for(Entitie obj : objects)
		{
			obj.registerEffect(flecheEffect);
			obj.localVit= new Vitesse(0,0);

		}

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
