package partie.projectile.fleches.destructrice;

import java.awt.Point;
import java.util.List;

import javax.vecmath.Vector2d;

import music.MusicBruitage;
import partie.collision.Collidable;
import partie.effects.Effect;
import partie.effects.Explosive_effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.Projectile;

public class Fleche_explosive extends Destructrice {
	
	// WARNING : effect moves with 
	//	-colliding entity        			YES 
	//  -colliding ground (ie roche_effect) YES
	
	public Fleche_explosive(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
	}

	
	void applyArrowEffect(List<Entity> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d collisionNormal,Point _pointCollision,
			Point _correctedPointCollision)
	{
		if(generatedEffect)
			return;

		generatedEffect=true;

		flecheEffect=new Explosive_effect(partie,this,0,partie.getFrame(),collisionNormal,_pointCollision,_correctedPointCollision);
		MusicBruitage.me.startBruitage("arc");
		
		Roche_effect.synchroniseMovementWithRocheEffectMovement(collidedObject, new Collidable[] {this,flecheEffect});

		this.doitDeplace=false;
		this.setCollideWithNone();
	}
	@Override
	protected void onPlanted(List<Entity> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck)
			destroy(partie,false);
		else
			applyArrowEffect(objects,partie,collidedObject,this.normCollision,this.pointCollision,this.correctedPointCollision);
		this.isVisible=false;
	}
	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);
		applyArrowEffect(objects,partie,collider,normal,null,null);
		//Hide the arrow but don't destroy it otherwise the effect position is no longer updated
		this.isVisible=false;
		collider.addSynchroSpeed(flecheEffect);
		return false;
	}
}
