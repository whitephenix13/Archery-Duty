package partie.deplacement.effect;

import java.util.Arrays;
import java.util.List;

import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.deplacement.effect.Mouvement_effect.TypeMouvEffect;
import partie.effects.Electrique_effect;
import partie.effects.Feu_effect;
import utils.Vitesse;

public class Feu_idle extends Mouvement_effect{
	
	public Feu_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;

		if(type_mouv.equals(TypeMouvEffect.FeuGround)){
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
		int end_index = type_mouv.equals(TypeMouvEffect.FeuGround)? 6 : 4;
		List<Integer> animTimes=  type_mouv.equals(TypeMouvEffect.FeuGround)? Arrays.asList(4,8,12,16,20,24):Arrays.asList(4,8,12,16);
		animation.start(animTimes, current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);

		vit = new Vitesse();
	}
	
	@Override
	public Mouvement Copy(Object obj) {
		return new Feu_idle(type_mouv,animation.getStartFrame());
	}
	
}
