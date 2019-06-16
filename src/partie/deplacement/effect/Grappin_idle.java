package partie.deplacement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Vent_effect;
import utils.Vitesse;

public class Grappin_idle extends Mouvement_effect{
	public Grappin_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;
		
		xtaille =  Arrays.asList(1657);
		ytaille =  Arrays.asList(30);
		hitbox  = Hitbox.createSquareHitboxes(0,0,1657,30,1); // 657


		int start_index =0;
		int end_index =1;
		animation.start(Arrays.asList(1), current_frame, start_index, end_index);
		//<=0 means endless loop
		animation.setMaxNumAnim(-1);

		
		vit = new Vitesse();
	}

	@Override
	public Mouvement Copy(Object obj) {
		return new Grappin_idle(type_mouv,animation.getStartFrame());
	}
	
	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		return vit;
	}

}
