package fleches.destructrice;

import java.awt.Point;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import effects.Effect;
import effects.Explosive_effect;
import effects.Roche_effect;
import fleches.Fleche;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Projectile;

public class Fleche_explosive extends Destructrice {

	public Fleche_explosive(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);

		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
	}

	
	void applyArrowEffect(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d collisionNormal,Point _pointCollision,
			Point _correctedPointCollision)
	{
		if(generatedEffect)
			return;

		generatedEffect=true;

		flecheEffect=new Explosive_effect(partie,this,0,partie.getFrame(),collisionNormal,_pointCollision,_correctedPointCollision);
		MusicBruitage.startBruitage("arc");
		if(collidedObject instanceof Roche_effect)
		{
			Roche_effect eff = (Roche_effect) collidedObject;
			if(eff.isWorldCollider){
			eff.addSynchroSpeed(this);
			eff.addSynchroSpeed(flecheEffect);
			}
		}

		/*
		for(Entitie obj : objects)
		{
			obj.currentEffects.add(flecheEffect);
		}*/

		this.doitDeplace=false;
		this.setCollideWithNone();
	}
	@Override
	protected void onPlanted(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck)
			destroy(partie,false);
		else
			applyArrowEffect(objects,partie,collidedObject,this.normCollision,this.pointCollision,this.correctedPointCollision);
	}
	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);
		applyArrowEffect(objects,partie,collider,normal,null,null);
		return false;
	}
}
