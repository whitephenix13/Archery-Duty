package partie.deplacement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Trou_noir_effect;
import utils.Vitesse;

public class Trou_noir_idle extends Mouvement_effect{
	public Trou_noir_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;

		xtaille =  Arrays.asList(800,800,800,800,800);
		ytaille =  Arrays.asList(800,800,800,800,800);
		hitbox = Hitbox.createSquareHitboxes(0,0,800,800,5);

		int start_index =0;
		int end_index =5;
		animation.start(Arrays.asList(4,8,12,16,20), current_frame, start_index, end_index);
		animation.setMaxNumAnim(10);
		
		vit = new Vitesse();
	}
	@Override
	public Mouvement Copy(Object obj) {
		return new Trou_noir_idle(type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		return vit;
	}

}
