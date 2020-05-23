package partie.mouvement.effect;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.effects.Effect;
import partie.effects.Effect.EffectCollisionEnum;
import partie.mouvement.Mouvement;
import partie.mouvement.effect.Mouvement_effect.MouvEffectEnum;
import utils.Vitesse;

public class Glace_idle extends Mouvement_effect{
	int speedNorm = 8;
	boolean isBeingDestroyed = false;
	public Glace_idle(SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv = MouvEffectEnum.GLACE_IDLE;
		sub_type_mouv=_sub_type_mouv;
		
		if(sub_type_mouv.equals(EffectCollisionEnum.GROUND)){
			xtaille =  Arrays.asList(90,90,90,90,90);
			ytaille =  Arrays.asList(72,72,72,72,72);
			this.hitbox =Hitbox.createQuadriHitboxes(
					Arrays.asList(new Point(10,70),new Point(10,70),new Point(2,62),new Point(36,56),new Point(38,42)),
					Arrays.asList(new Point(40,0),new Point(40,0),new Point(44,1),new Point(7,3),new Point(4,5)),
					Arrays.asList(new Point(54,0),new Point(54,0),new Point(89,25),new Point(89,6),new Point(89,5)),
					Arrays.asList(new Point(80,70),new Point(80,70),new Point(89,55),new Point(89,33),new Point(89,29))
					);
			/*hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(15 ,15  ,15  ,15 ,15 ),
					Arrays.asList(0 ,0  ,0  ,0 ,0 ),
					Arrays.asList(75,75,75,75,75),
					Arrays.asList(72,72,70,57,43));*/
		}
		else{
			xtaille =  Arrays.asList(100,100,100,100);
			ytaille =  Arrays.asList(100,100,100,100);
			this.hitbox =Hitbox.createSquareHitboxes(0,0,100,100,4);
		}
		
	
		int start_index =0;
		int end_index = sub_type_mouv.equals(EffectCollisionEnum.GROUND)? 1 : 4; //5:4
		List<Integer> animationFrame= sub_type_mouv.equals(EffectCollisionEnum.GROUND)? Arrays.asList(6,12,18,24,30):Arrays.asList(4,8,12,16);
		if(!sub_type_mouv.equals(EffectCollisionEnum.GROUND)){
			animation.setMaxNumAnim(1);
		}
		else{
			animation.setMaxNumAnim(-1);
		}
		animation.start(animationFrame, current_frame, start_index, end_index);

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
	public Vitesse getSpeed(Collidable object, int mouv_index) {
		if(!sub_type_mouv.equals(EffectCollisionEnum.GROUND))
			return vit;
		
		if(isBeingDestroyed)
			vit = new Vitesse();
		else
			vit = object.convertSpeed(speedNorm,object.getRotation()-Math.PI/2);
		return vit;
	}
	
	@Override
	public Mouvement Copy() {
		return new Glace_idle(sub_type_mouv,animation.getStartFrame());
	}
	
	@Override
	public DirSubTypeMouv droite_gauche(int animationFrame,double rotation) {
		//Watch out, the direction are reversed since the stalactite moves the other way 
		if( rotation <= Math.PI/2 && (rotation >= 3*Math.PI/2) )
			return DirSubTypeMouv.DROITE; 
		else
			return DirSubTypeMouv.GAUCHE;
	}
	@Override
	public boolean isInterruptible(int currentAnim) {
		return !isBeingDestroyed;
	}
}
