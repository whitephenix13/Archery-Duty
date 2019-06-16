package partie.deplacement.effect;

import java.util.Arrays;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Roche_effect;
import utils.Vitesse;

public class Roche_idle extends Mouvement_effect{
	
	boolean isBeingDestroyed = false;
	public Roche_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;

		if(type_mouv.equals(TypeMouvEffect.RocheGround)){
			xtaille =  Arrays.asList(100,100,100,100,100);
			ytaille =  Arrays.asList(99,100,100,100,100);
		}
		else{
			xtaille =  Arrays.asList(100,100,100,100);
			ytaille =  Arrays.asList(100,100,100,100);
			hitbox = Hitbox.createSquareHitboxes(0,0,99,99,4);
		}
		int start_index =0;
		int end_index = type_mouv.equals(TypeMouvEffect.RocheGround)? 1 : 4;
		List<Integer> animTimes= type_mouv.equals(TypeMouvEffect.RocheGround)? Arrays.asList(10,4,8,12,16):Arrays.asList(4,8,12,16);
		animation.start(animTimes, current_frame, start_index, end_index);
		if(type_mouv.equals(TypeMouvEffect.RocheGround))
			animation.setMaxNumAnim(-1);
		else
			animation.setMaxNumAnim(1);


		vit = new Vitesse();
	}
	
	public void setDestroyAnimation(int frame)
	{
		if(!type_mouv.equals(TypeMouvEffect.RocheGround))
			return;
		int start_index =1;
		int end_index =5;
		animation.restart(animation.getAnimationFrame(), frame, start_index, end_index);
		animation.setMaxNumAnim(1);
		
		isBeingDestroyed=true;
	}
	@Override
	public Mouvement Copy(Object obj) {
		return new Roche_idle(type_mouv,animation.getStartFrame());
	}

	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		return vit;
	}

}
