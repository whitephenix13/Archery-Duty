package partie.deplacement.effect;

import java.util.Arrays;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Ombre_effect;
import utils.Vitesse;

public class Ombre_idle extends Mouvement_effect{
		public Ombre_idle(TypeMouv _type_mouv, int current_frame){
			super();
			type_mouv=_type_mouv;

			xtaille =  Arrays.asList(92,92,92,92);
			ytaille =  Arrays.asList(79,79,79,79);
			hitbox = Hitbox.createSquareHitboxes(0,0,92,79,4);

			//(new Float(0.0)).equals(new Float(-0.0))
			int start_index =0;
			int end_index =4;
			animation.start(Arrays.asList(4,8,12,16), current_frame, start_index, end_index);
			animation.setMaxNumAnim(1);
			
			vit = new Vitesse();
		}
		@Override
		public Mouvement Copy(Object obj) {
			return new Ombre_idle(type_mouv,animation.getStartFrame());
		}

		@Override
		public Vitesse getSpeed(Collidable object, int anim) {
			return vit;
		}
}
