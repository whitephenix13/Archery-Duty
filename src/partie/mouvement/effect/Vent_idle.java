package partie.mouvement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;
import utils.Vitesse;

public class Vent_idle extends Mouvement_effect{
	
	public Vent_idle(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.VENT_IDLE;
		sub_type_mouv = _sub_type_mouv;
		
		xtaille =  Arrays.asList(164,178,259,400);
		ytaille =  Arrays.asList(159,191,349,400);
		hitbox  = Hitbox.createSquareHitboxes(Arrays.asList(0,0,0,0),Arrays.asList(0,0,0,0),Arrays.asList(164,178,259,400),Arrays.asList(159,191,349,400));

		int start_index =0;
		int end_index =4;
		animation.start(Arrays.asList(4,8,12,16), current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);
		
		vit = new Vitesse();
	}
	
	@Override
	public Mouvement Copy() {
		return new Vent_idle(sub_type_mouv,animation.getStartFrame());
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
