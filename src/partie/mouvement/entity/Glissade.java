package partie.mouvement.entity;

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
import utils.Vitesse;

public class Glissade extends Mouvement_entity
{
	//constructeur monstre
	public Glissade(ObjectType objType,SubTypeMouv _sub_type_mouv,int current_frame) 
	{
		super();
		type_mouv=EntityTypeMouv.GLISSADE;
		sub_type_mouv=_sub_type_mouv;
		this.objType=objType;
		if(objType.equals(ObjectType.HEROS))
		{
			xtaille =  Arrays.asList(49,49);
			ytaille =  Arrays.asList(89,89);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(9,0);
			List<Integer> xd = Arrays.asList(47,39);
			List<Integer> yh = Arrays.asList(5,5);
			List<Integer> yb = Arrays.asList(88,88);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);


			//animation frame, current_frame, start_index, end_index
			int start_index =sub_type_mouv.equals(DirSubTypeMouv.GAUCHE) ? 0 : 1;
			int end_index =sub_type_mouv.equals(DirSubTypeMouv.GAUCHE) ? 1 : 2;
			animation.start(Arrays.asList(2,2), current_frame, start_index, end_index);

		}
	}
	public Glissade(ObjectType _typeObj,SubTypeMouv _sub_type_mouv, int current_frame,Animation _animation){
		this(_typeObj,_sub_type_mouv,current_frame);
		animation = _animation;
	}
	/*@Override
	public int getMaxBoundingSquare(Object obj)
	{
		return 90;
	}*/
	public Mouvement Copy() {
		return new Glissade(objType,sub_type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int mouv_index) {
		if(ObjectTypeHelper.isTypeOf(object, ObjectType.HEROS))
		{
			return null; //nothing to set
		}
		return null;
	}
	@Override
	public DirSubTypeMouv droite_gauche(int mouv_index,double rotation) {
		if(objType.equals(ObjectType.HEROS))
			if(mouv_index<1)
				return (DirSubTypeMouv.GAUCHE);
			else 
				return(DirSubTypeMouv.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return DirSubTypeMouv.GAUCHE;
		}
	}
	@Override
	public boolean isInterruptible(int currentAnim) {
		return true;
	}
}
