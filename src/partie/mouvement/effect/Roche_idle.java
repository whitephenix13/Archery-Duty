package partie.mouvement.effect;

import java.util.Arrays;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.effects.Effect.EffectCollisionEnum;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;
import utils.Vitesse;

public class Roche_idle extends Mouvement_effect{
	
	boolean isBeingDestroyed = false;
	public Roche_idle(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.ROCHE_IDLE;
		sub_type_mouv = _sub_type_mouv;
		
		if(sub_type_mouv.equals(EffectCollisionEnum.GROUND)){
			xtaille =  Arrays.asList(100,100,100,100,100);
			ytaille =  Arrays.asList(99,100,100,100,100);
			hitbox = Hitbox.createSquareHitboxes(
					Arrays.asList(35 ,35 ,27 ,10 ,0),
					Arrays.asList(0,0,0,0,0),
					Arrays.asList(63 ,63 ,76 ,91 ,99),
					Arrays.asList(1,1,1,1,1));
		}
		else{
			xtaille =  Arrays.asList(100,100,100,100);
			ytaille =  Arrays.asList(100,100,100,100);
			hitbox = Hitbox.createSquareHitboxes(0,0,99,99,4);
		}
		int start_index =0;
		int end_index = sub_type_mouv.equals(EffectCollisionEnum.GROUND)? 1 : 4;
		List<Integer> animationFrame= sub_type_mouv.equals(EffectCollisionEnum.GROUND)? Arrays.asList(10,4,8,12,16):Arrays.asList(4,8,12,16);
		animation.start(animationFrame, current_frame, start_index, end_index);
		if(sub_type_mouv.equals(EffectCollisionEnum.GROUND))
			animation.setMaxNumAnim(-1);
		else
			animation.setMaxNumAnim(1);


		vit = new Vitesse();
	}
	
	public void setDestroyAnimation(int frame)
	{
		if(!sub_type_mouv.equals(EffectCollisionEnum.GROUND))
			return;
		int start_index =1;
		int end_index =5;
		animation.restart(animation.getAnimationFrame(), frame, start_index, end_index);
		animation.setMaxNumAnim(1);
		isBeingDestroyed=true;
	}
	@Override
	public Mouvement Copy() {
		return new Roche_idle(sub_type_mouv,animation.getStartFrame());
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
