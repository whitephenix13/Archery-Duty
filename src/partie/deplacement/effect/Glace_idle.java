package partie.deplacement.effect;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Effect;
import utils.Vitesse;

public class Glace_idle extends Mouvement_effect{
	int speedNorm = 5;
	boolean isBeingDestroyed = false;
	public Glace_idle(TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;
		
		if(type_mouv.equals(TypeMouvEffect.GlaceGround)){
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
		int end_index = type_mouv.equals(TypeMouvEffect.GlaceGround)? 1 : 4; //5:4
		List<Integer> animTimes= type_mouv.equals(TypeMouvEffect.GlaceGround)? Arrays.asList(6,12,18,24,30):Arrays.asList(4,8,12,16);
		if(!type_mouv.equals(TypeMouvEffect.GlaceGround)){
			animation.setMaxNumAnim(1);
		}
		else{
			animation.setMaxNumAnim(-1);
		}
		animation.start(animTimes, current_frame, start_index, end_index);

		vit = new Vitesse();
	}
	
	public void setDestroyAnimation(int frame)
	{
		if(!type_mouv.equals(TypeMouvEffect.GlaceGround))
			return;
		int start_index =1;
		int end_index =5;
		animation.restart(animation.getAnimationFrame(), frame, start_index, end_index);
		animation.setMaxNumAnim(1);
		
		isBeingDestroyed=true;
	}
	
	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		if(!type_mouv.equals(TypeMouvEffect.GlaceGround))
			return vit;
		
		if(isBeingDestroyed)
			vit = new Vitesse();
		else
			vit = object.convertSpeed(speedNorm,object.getRotation()-Math.PI/2);
		return vit;
	}
	
	@Override
	public Mouvement Copy(Object obj) {
		return new Glace_idle(type_mouv,animation.getStartFrame());
	}
	
	@Override
	public String droite_gauche(Object obj, int anim) {
		//Watch out, the direction are reversed since the stalactite moves the other way 
		if( (((Effect)obj).getRotation() <= Math.PI/2) && (((Effect)obj).getRotation() >= 3*Math.PI/2) )
			return Mouvement.DROITE; 
		else
			return Mouvement.GAUCHE;
	}
}
