package partie.mouvement.projectile.t_normal;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.mouvement.Animation;
import partie.mouvement.Mouvement;
import partie.mouvement.projectile.Mouvement_projectile;
import partie.mouvement.projectile.Mouvement_projectile.MouvProjectileEnum;
import utils.Vitesse;

public class T_normal_creation extends Mouvement_projectile{

	public T_normal_creation(ObjectType objType,SubTypeMouv _sub_type_mouv,int current_frame){
		super();
		type_mouv=MouvProjectileEnum.T_normal_creation;
		sub_type_mouv=_sub_type_mouv;
		this.objType=objType;
		
		if(objType.equals(ObjectType.FLECHE))
		{                     
			try {throw new Exception("Not implemented");} catch (Exception e) {e.printStackTrace();}

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
	public T_normal_creation(ObjectType _typeObj,SubTypeMouv _sub_type_mouv, int current_frame,Animation _animation){
		this(_typeObj,_sub_type_mouv,current_frame);
		animation = _animation;
	}

	@Override
	public Mouvement Copy() {
		return new T_normal_creation(objType,sub_type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int mouv_index) {
		if(ObjectTypeHelper.isTypeOf(object, ObjectType.FLECHE))
		{
			try {throw new Exception("Not implemented");} catch (Exception e) {e.printStackTrace();}
			return new Vitesse();
		}
		else if(ObjectTypeHelper.isTypeOf(object, ObjectType.TIR_SPIREL))
		{
			return new Vitesse();
		}
		else{
			System.err.println("Unkown type "+ object.getClass().getName());
			return null;}
	}
	@Override
	public DirSubTypeMouv droite_gauche(int mouv_index,double rotation) {
		if(objType.equals(ObjectType.FLECHE))
		{
			try {throw new Exception("Not implemented");} catch (Exception e) {e.printStackTrace();}
			return DirSubTypeMouv.GAUCHE; 
		}
		else if(objType.equals(ObjectType.TIR_SPIREL))
			if( (rotation >= Math.PI/2) && (rotation <= 3*Math.PI/2) )
				return DirSubTypeMouv.GAUCHE; 
			else
				return DirSubTypeMouv.DROITE;
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return DirSubTypeMouv.GAUCHE;
		}
	}
	@Override
	public boolean isInterruptible(int currentAnim) {
		return false;
	}
}
