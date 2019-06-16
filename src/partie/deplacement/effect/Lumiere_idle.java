package partie.deplacement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Lumiere_effect;
import utils.Vitesse;

public class Lumiere_idle extends Mouvement_effect{
	public Lumiere_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;

		xtaille =  Arrays.asList(100,100,100,100,100);
		ytaille =  Arrays.asList(100,100,100,100,100);
		hitbox = Hitbox.createSquareHitboxes(0,0,100,100,5);

		int start_index =0;
		int end_index =5;
		animation.start(Arrays.asList(4,8,12,16,20), current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);
		
		vit = new Vitesse();
	}
	@Override
	public Mouvement Copy(Object obj) {
		return new Lumiere_idle(type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		return vit;
	}

}
