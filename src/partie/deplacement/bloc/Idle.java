package partie.deplacement.bloc;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.deplacement.entity.Mouvement_entity;
import utils.Vitesse;

public class Idle extends Mouvement_entity{
	private static Vitesse vit;
	public static enum TypeMouvBloc implements TypeMouv {Idle};

	public Idle(){
		super();
		type_mouv = TypeMouvBloc.Idle;
		xtaille =  Arrays.asList(99);
		ytaille =  Arrays.asList(99);
		
		hitbox = Hitbox.createSquareHitboxes(0, 0, 99, 99, 1);
		vit = new Vitesse();
		//no animation needed
		
	}

	@Override
	public Mouvement Copy(Object obj) {
		return new Idle();
	}

	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		return vit;
	}

	@Override
	public String droite_gauche(Object obj, int anim) {
		return Mouvement.GAUCHE;//default 
	}
}
