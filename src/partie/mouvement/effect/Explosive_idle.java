package partie.mouvement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;
import utils.Vitesse;

public class Explosive_idle extends Mouvement_effect{
	public Explosive_idle(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.EXPLOSIVE_IDLE;
		sub_type_mouv=_sub_type_mouv;

		xtaille =  Arrays.asList(400,400,400,400,400,400,400,400,400,400,400,400);
		ytaille =  Arrays.asList(400,400,400,400,400,400,400,400,400,400,400,400);
		hitbox = Hitbox.createSquareHitboxes(
				Arrays.asList(185,170,152,127,100,90 ,86 ,100,110,140,150,155),
				Arrays.asList(185,170,137,112,90 ,70 ,70,111,134,140,160,150),
				Arrays.asList(213,233,265,285,300,310,300,308,267,256,230,200),
				Arrays.asList(213,233,260,270,300,300,310,295,293,276,270,200));


		int start_index =0;
		int end_index =12;
		animation.start(Arrays.asList(3,6,9,12,15,18,21,24,27,30,33,36), current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);
		
		vit = new Vitesse();
	}
	@Override
	public Mouvement Copy() {
		return new Explosive_idle(sub_type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int mouv_index) {
		return vit;
	}
	@Override
	public boolean isInterruptible(int currentAnim) {
		return true;
	}
}
