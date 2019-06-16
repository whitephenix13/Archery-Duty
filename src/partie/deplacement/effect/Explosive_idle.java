package partie.deplacement.effect;

import java.util.Arrays;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.deplacement.effect.Mouvement_effect.TypeMouvEffect;
import partie.effects.Electrique_effect;
import utils.Vitesse;

public class Explosive_idle extends Mouvement_effect{
	public Explosive_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;

		xtaille =  Arrays.asList(400,400,400,400,400,400,400,400,400,400,400,400);
		ytaille =  Arrays.asList(400,400,400,400,400,400,400,400,400,400,400,400);
		hitbox = Hitbox.createSquareHitboxes(
				Arrays.asList(185,170,142,103,105,80,62,110,110,133,137,144),
				Arrays.asList(185,166,138,113,87,77,73,180,150,195,225,238),
				Arrays.asList(213,237,263,300,283,294,306,280,262,260,255,220),
				Arrays.asList(213,238,265,318,306,318,325,328,340,346,340,371));


		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index =12;
		animation.start(Arrays.asList(3,6,9,12,15,18,21,24,27,30,33,36), current_frame, start_index, end_index);
		animation.setMaxNumAnim(1);
		
		vit = new Vitesse();
	}
	@Override
	public Mouvement Copy(Object obj) {
		return new Explosive_idle(type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		return vit;
	}

}
