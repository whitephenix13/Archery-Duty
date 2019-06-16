package partie.deplacement.effect;

import java.util.Arrays;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Electrique_effect;
import utils.Vitesse;

public class Electrique_idle extends Mouvement_effect{
	int speedNorm = 30;

	public Electrique_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;

		if(type_mouv.equals(TypeMouvEffect.ElectriqueGround)){
			xtaille =  Arrays.asList(200,200,200,200,200,200);
			ytaille =  Arrays.asList(200,200,200,200,200,200);
			this.hitbox = Hitbox.createSquareHitboxes(
					Arrays.asList(43 ,0  ,0  ,0  ,0  ,0  ),
					Arrays.asList(145,72 ,17 ,0  ,0  ,0  ),
					Arrays.asList(159,175,200,200,200,200),
					Arrays.asList(200,200,200,200,200,200));
		}
		else{
			xtaille =  Arrays.asList(120,120,120,120);
			ytaille =  Arrays.asList(60,60,60,60);
			this.hitbox = Hitbox.createSquareHitboxes(
					Arrays.asList(60 ,0,0,0),
					Arrays.asList(0,0,0,0),
					Arrays.asList(120,120,120,120),
					Arrays.asList(60,60,60,60));
		}
		
		int start_index =0;
		int end_index = type_mouv.equals(TypeMouvEffect.ElectriqueGround)? 6 : 4;
		List<Integer> animTimes= type_mouv.equals(TypeMouvEffect.ElectriqueGround)? Arrays.asList(6,12,18,24,30,36):Arrays.asList(4,8,12,16);
		animation.start(animTimes, current_frame, start_index, end_index);
		if(type_mouv.equals(TypeMouvEffect.ElectriqueGround)){
			animation.setMaxNumAnim(3);
		}
		else
		{
			animation.setMaxNumAnim(-1);//infinite loop
		}

		vit = new Vitesse();
	}
	
	@Override
	public Mouvement Copy(Object obj) {
		return new Electrique_idle(type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		if(type_mouv.equals(TypeMouvEffect.ElectriqueGround))
			return vit;
		
		vit = object.convertSpeed(speedNorm,object.getRotation());
		return vit;
	}

}
