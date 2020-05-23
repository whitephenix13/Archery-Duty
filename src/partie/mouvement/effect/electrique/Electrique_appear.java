package partie.mouvement.effect.electrique;

import java.util.Arrays;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.effects.Effect.EffectCollisionEnum;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect;
import utils.Vitesse;

public class Electrique_appear extends Mouvement_effect{
	private Vitesse zeroVit = new Vitesse();
	
	public Electrique_appear(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.ELECTRIQUE_APPEAR;
		sub_type_mouv = _sub_type_mouv; 
		if(sub_type_mouv.equals(EffectCollisionEnum.ENTITY)){
			xtaille =  Arrays.asList(72);
			ytaille =  Arrays.asList(66);
			this.hitbox = Hitbox.createSquareHitboxes(
					Arrays.asList(15),
					Arrays.asList(19),
					Arrays.asList(56),
					Arrays.asList(49));
		}
		
		int start_index =0;
		int end_index = 1;
		List<Integer> animationFrame= Arrays.asList(4);
		animation.start(animationFrame, current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);

		vit = new Vitesse();
	}
	
	@Override
	public Mouvement Copy() {
		return new Electrique_appear(sub_type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int mouv_index) {
		return zeroVit;
	}
	@Override
	public boolean isInterruptible(int currentAnim) {
		return false;//Can be interrupted, have to wait for end of appear
	}

}
