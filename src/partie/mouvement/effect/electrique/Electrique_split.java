package partie.mouvement.effect.electrique;

import java.util.Arrays;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.effects.Effect.EffectCollisionEnum;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect;
import utils.Vitesse;

public class Electrique_split extends Mouvement_effect{
	private Vitesse zeroVit = new Vitesse();
	
	public Electrique_split(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.ELECTRIQUE_SPLIT;
		sub_type_mouv = _sub_type_mouv; 
		if(sub_type_mouv.equals(EffectCollisionEnum.ENTITY)){
			xtaille =  Arrays.asList(72,72,72,72);
			ytaille =  Arrays.asList(66,66,66,66);
			this.hitbox = Hitbox.createSquareHitboxes(
					Arrays.asList(11  ,7  ,5  ,0  ),
					Arrays.asList(11 ,13 ,23 ,21  ),
					Arrays.asList(67 ,68 ,69 ,72 ),
					Arrays.asList(57 ,59 ,45 ,49 ));
		}
		
		int start_index =0;
		int end_index = 4;
		List<Integer> animationFrame= Arrays.asList(4,8,12,16);
		animation.start(animationFrame, current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);

		vit = new Vitesse();
	}
	
	@Override
	public Mouvement Copy() {
		return new Electrique_split(sub_type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int mouv_index) {
		return zeroVit;
	}
	@Override
	public boolean isInterruptible(int currentAnim) {
		return false;
	}

}
