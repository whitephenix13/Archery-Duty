package partie.mouvement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;
import utils.Vitesse;

public class Lumiere_idle extends Mouvement_effect{
	public Lumiere_idle(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.LUMIERE_IDLE;
		sub_type_mouv=_sub_type_mouv;
		
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
	public Mouvement Copy() {
		return new Lumiere_idle(sub_type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse getSpeed(Collidable object, int animationFrame) {
		return vit;
	}
	@Override
	public boolean isInterruptible(int currentAnim) {
		return true;
	}

}
