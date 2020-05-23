package partie.mouvement.effect;

import java.util.Arrays;
import java.util.List;

import partie.collision.Hitbox;
import partie.effects.Effect.EffectCollisionEnum;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;
import utils.Vitesse;

public class Feu_idle extends Mouvement_effect{
	
	public Feu_idle(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.FEU_IDLE;
		sub_type_mouv=_sub_type_mouv;
		if(sub_type_mouv.equals(EffectCollisionEnum.GROUND)){
			xtaille =  Arrays.asList(40,40,40,40,40,40);
			ytaille =  Arrays.asList(300,300,300,300,300,300);
			this.hitbox = Hitbox.createSquareHitboxes(
					Arrays.asList(0 ,0  ,0  ,0  ,0  ,0  ),
					Arrays.asList(232,160,86 ,32 ,0  ,0  ),
					Arrays.asList(40,40,40,40,40,40),
					Arrays.asList(300,300,300,300,300,300));
		}
		else{
			xtaille =  Arrays.asList(100,100,100,100);
			ytaille =  Arrays.asList(100,100,100,100);
			this.hitbox = Hitbox.createSquareHitboxes(0,0,100,100,4);
		}
		
		int start_index =0;
		int end_index = sub_type_mouv.equals(EffectCollisionEnum.GROUND)? 6 : 4;
		List<Integer> animationFrame=  sub_type_mouv.equals(EffectCollisionEnum.GROUND)? Arrays.asList(4,8,12,16,20,24):Arrays.asList(4,8,12,16);
		animation.start(animationFrame, current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);

		vit = new Vitesse();
	}
	
	@Override
	public Mouvement Copy() {
		return new Feu_idle(sub_type_mouv,animation.getStartFrame());
	}
	@Override
	public boolean isInterruptible(int currentAnim) {
		return true;
	}
}
