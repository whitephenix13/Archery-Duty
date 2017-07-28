package fleches.sprirituelle;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import effects.Lumiere_effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Projectile;

public class Fleche_lumiere extends Spirituelle {

	public Fleche_lumiere(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
	}

	void applyArrowEffect(List<Entitie> objects,AbstractModelPartie partie,Collidable collider)
	{
		if(generatedEffect)
			return;

		generatedEffect=true;

		flecheEffect=new Lumiere_effect(partie,this,0,partie.getFrame());
		MusicBruitage.startBruitage("arc");

		for(Entitie obj : objects)
		{
			obj.currentEffects.add(this.flecheEffect);
		}
		this.doitDeplace=false;
		this.setCollideWithNone();


	}
	@Override
	protected void onPlanted(List<Entitie> objects, AbstractModelPartie partie,boolean stuck)
	{
		if(stuck)
			destroy(partie,false);
		else
			applyArrowEffect(objects,partie,null);
	}
	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d normal)
	{
		applyArrowEffect(objects,partie,collider);
		return false;
	}

}
