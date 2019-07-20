package partie.deplacement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.effect.Mouvement_effect.MouvEffectEnum;
import utils.Vitesse;

public class Grappin_idle extends Mouvement_effect{
	public Grappin_idle(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.GRAPPIN_IDLE;
		sub_type_mouv=_sub_type_mouv;
		
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
	public Mouvement Copy() {
		return new Grappin_idle(sub_type_mouv,animation.getStartFrame());
	}
	
	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		return vit;
	}

}
