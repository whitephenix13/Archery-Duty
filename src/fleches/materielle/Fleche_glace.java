package fleches.materielle;

import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import effects.Glace_effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Projectile;

public class Fleche_glace extends Materielle {

	public Fleche_glace(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
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

			flecheEffect=new Glace_effect(partie,this,0,partie.getFrame(),this.normCollision,0);
			MusicBruitage.startBruitage("arc");
		}
	}
	
	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d normal)
	{
		if(!generatedEffect){
			generatedEffect=true;

			flecheEffect=new Glace_effect(partie,this,0,partie.getFrame(),normal,1);
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
