package partie.mouvement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;
import utils.Vitesse;

public class Trou_noir_idle extends Mouvement_effect{
	public Trou_noir_idle(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.TROU_NOIR_IDLE;
		sub_type_mouv=_sub_type_mouv;
		
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
	public Mouvement Copy() {
		return new Trou_noir_idle(sub_type_mouv,animation.getStartFrame());
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
