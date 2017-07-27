package fleches;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import effects.Roche_effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;

public class Fleche_roche extends Fleche {

	public Fleche_roche(List<Fleche> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		type_fleche=MATERIELLE.ROCHE;
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;//TODO:
	}
	@Override
	protected void onPlanted(List<Entitie> objects, AbstractModelPartie partie,boolean stuck)
	{
		if(stuck){
			destroy(partie,false);
			return;
		}
		
		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Roche_effect(partie,this,0,partie.getFrame(),this.normCollision,0);
			MusicBruitage.startBruitage("arc");
		}
	}
	
	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d normal)
	{
		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Roche_effect(partie,this,0,partie.getFrame(),normal,1);
			MusicBruitage.startBruitage("arc");
			for(Entitie obj : objects)
			{
				obj.currentEffects.add(this.flecheEffect);
			}
			this.doitDeplace=false;
			this.checkCollision=false;
		}
		return true;
	}
}
