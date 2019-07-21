package partie.deplacement.projectile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import option.Config;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Animation;
import partie.deplacement.Mouvement;
import partie.effects.Electrique_effect;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class T_normal extends Mouvement_projectile{

	public T_normal(ObjectType objType,SubTypeMouv _sub_type_mouv,int current_frame){
		super();
		type_mouv=MouvProjectileEnum.T_normal;
		sub_type_mouv=_sub_type_mouv;
		this.objType=objType;
		
		if(objType.equals(ObjectType.FLECHE))
		{                     
			xtaille=Arrays.asList(45,45,45,45);
			ytaille=Arrays.asList(19,19,19,19);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(6,6,6,6);
			List<Integer> xd = Arrays.asList(37,37,37,37);
			List<Integer> yh = Arrays.asList(5,5,5,5);
			List<Integer> yb = Arrays.asList(11,11,11,11);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int delta= 5;//5
			animation.start(Arrays.asList(delta,2*delta,3*delta,4*delta), current_frame, 0, 4);

		}
		else if(objType.equals(ObjectType.TIR_SPIREL))
		{

			xtaille= Arrays.asList(36,63,114);
			ytaille= Arrays.asList(25,34,34);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0,0,0);
			List<Integer> xd = Arrays.asList(36,63,114);
			List<Integer> yh = Arrays.asList(0,0,0);
			List<Integer> yb = Arrays.asList(25,34,34);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int delta = 2;
			animation.setMaxNumAnim(1);
			animation.start(Arrays.asList(delta,2*delta,3*delta), current_frame, 0, 3);
		}
		else
			System.err.println("Unkown type "+ objType);
	}
	public T_normal(ObjectType _typeObj,SubTypeMouv _sub_type_mouv, int current_frame,Animation _animation){
		this(_typeObj,_sub_type_mouv,current_frame);
		animation = _animation;
	}
	/*@Override
	public int getMaxBoundingSquare(Object obj)
	{
		if(TypeObject.isTypeOf(obj, TypeObject.FLECHE))
			return 45;
		else if(TypeObject.isTypeOf(obj, TypeObject.TIR_SPIREL))
			return 114;
		else
			return 0;
	}*/
	@Override
	public Mouvement Copy() {
		return new T_normal(objType,sub_type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		if(ObjectTypeHelper.isTypeOf(object, ObjectType.FLECHE))
		{
			Fleche f = (Fleche)object;
			int speed_norm = (int)(30.0 / Config.ratio_fps());
			if(ObjectTypeHelper.isTypeOf(object, ObjectType.GRAPPIN))
				speed_norm = (int)(60.0 / Config.ratio_fps());//60
			return object.convertSpeed(speed_norm,object.getRotation());
		}
		else if(ObjectTypeHelper.isTypeOf(object, ObjectType.TIR_SPIREL))
		{
			int speed_norm = (int)(10.0 / Config.ratio_fps());
			return object.convertSpeed(speed_norm,object.getRotation());
		}
		else{
			System.err.println("Unkown type "+ object.getClass().getName());
			return null;}
	}
	@Override
	public DirSubTypeMouv droite_gauche(int anim,double rotation) {
		if(objType.equals(ObjectType.FLECHE))
		{
			if( (rotation <= Math.PI/2) && (rotation >= 3*Math.PI/2) )
				return DirSubTypeMouv.GAUCHE; 
			else
				return DirSubTypeMouv.DROITE;
		}
		else if(objType.equals(ObjectType.TIR_SPIREL))
			if(anim<2)
				return (DirSubTypeMouv.GAUCHE);
			else 
				return(DirSubTypeMouv.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return DirSubTypeMouv.GAUCHE;
		}
	}
	@Override
	public int updateAnimation(int anim,int current_frame,double speedFactor) {
		if(objType.equals(ObjectType.FLECHE))
			return animation.update(anim,current_frame,speedFactor);
		else if(objType.equals(ObjectType.TIR_SPIREL))
			return animation.update(0,current_frame,speedFactor);
		else 
			return -1;
	}
}
