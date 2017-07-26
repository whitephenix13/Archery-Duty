package fleches;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import conditions.Condition;
import effects.Lumiere_effect;
import effects.Vent_effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Vitesse;

public class Fleche_lumiere extends Fleche {

	double VITESSE_DUREE = 10;

	public Fleche_lumiere(List<Fleche> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		type_fleche=SPIRITUELLE.LUMIERE;
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
	}

	void applyArrowEffect(List<Collidable> objects,AbstractModelPartie partie,Collidable collider)
	{
		if(generatedEffect)
			return;

		generatedEffect=true;

		flecheEffect=new Lumiere_effect(partie,this,0,partie.getFrame());
		MusicBruitage.startBruitage("arc");

		for(Collidable obj : objects)
		{
			if(obj instanceof Entitie)
				if(Collision.testcollisionObjects(partie, this, obj)){
					obj.conditions.addNewCondition(Condition.VITESSE, VITESSE_DUREE);
				}
		}
		this.doitDeplace=false;
		this.checkCollision=false;


	}
	@Override
	protected void onPlanted(List<Collidable> objects, AbstractModelPartie partie,boolean stuck)
	{
		if(stuck)
			destroy(partie,false);
		else
			applyArrowEffect(objects,partie,null);
	}
	@Override
	protected boolean OnObjectsCollision(List<Collidable> objects,AbstractModelPartie partie,Collidable collider,Vector2d normal)
	{
		applyArrowEffect(objects,partie,collider);
		return false;
	}

}
