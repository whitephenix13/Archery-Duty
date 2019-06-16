package partie.deplacement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Vent_effect;
import utils.Vitesse;

public class Vent_idle extends Mouvement_effect{
	
	public Vent_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;

		xtaille =  Arrays.asList(164,178,259,400);
		ytaille =  Arrays.asList(159,191,349,400);
		//hitbox = Hitbox.createSquareHitboxes(Arrays.asList(125,123,74,0),Arrays.asList(109,110,0,0),Arrays.asList(291,298,336,400),Arrays.asList(274,313,349,400));
		hitbox  = Hitbox.createSquareHitboxes(Arrays.asList(0,0,0,0),Arrays.asList(0,0,0,0),Arrays.asList(164,178,259,400),Arrays.asList(159,191,349,400));

		int start_index =0;
		int end_index =4;
		animation.start(Arrays.asList(4,8,12,16), current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);
		
		vit = new Vitesse();
	}
	
	@Override
	public Mouvement Copy(Object obj) {
		return new Vent_idle(type_mouv,animation.getStartFrame());
	}
	
	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		return vit;
	}

}
