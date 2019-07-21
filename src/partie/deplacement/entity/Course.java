package partie.deplacement.entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//il y a 4 animations de deux cot�s 
import java.util.List;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import option.Config;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Animation;
import partie.deplacement.Mouvement;
import utils.Vitesse;

public class Course extends Mouvement_entity{

	//constructeur monstre
	public Course(ObjectType objType,SubTypeMouv _sub_type_mouv,int current_frame){
		super();
		type_mouv= MouvEntityEnum.COURSE;
		sub_type_mouv=_sub_type_mouv;
		this.objType=objType;
		
		if(objType.equals(ObjectType.HEROS))
		{
			xtaille =  Arrays.asList(55,79,75,82,55,79,75,82);
			ytaille =  Arrays.asList(89,85,89,94,89,85,89,94);

			// 78,78,76,72,78,78,76,72 

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(9,8,14,10,9,34,24,35);
			List<Integer> xd = Arrays.asList(45,44,50,46,45,70,60,71);
			List<Integer> yh = Arrays.asList(8,4,8,13,8,4,8,13);
			List<Integer> yb = Arrays.asList(88,84,88,93,88,84,88,93);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			int start_index =sub_type_mouv.equals(DirSubTypeMouv.GAUCHE) ? 0 : 4;
			int end_index =sub_type_mouv.equals(DirSubTypeMouv.GAUCHE) ? 4 : 8;
			//animation frame, current_frame, start_index, end_index
			animation.start(Arrays.asList(6,12,18,24,6,12,18,24), current_frame, start_index, end_index);

		}
	}

	public Course(ObjectType objType,SubTypeMouv _sub_type_mouv, int current_frame,Animation _animation){
		this(objType,_sub_type_mouv,current_frame);
		animation = _animation;
	}

	/*@Override
	public int getMaxBoundingSquare(Object obj)
	{
		return 100;
	}*/

	public Mouvement Copy() {
		return new Course(objType,sub_type_mouv,animation.getStartFrame(),animation);
	}

	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		if(ObjectTypeHelper.isTypeOf(object, ObjectType.HEROS))
		{
			assert (anim>=0 && anim <8);

			int speed_norm = (int)(8.0 / Config.ratio_fps());
			if(object.getDeplacement().droite_gauche(object.getAnim(),object.getRotation()).equals(DirSubTypeMouv.GAUCHE))
			{if(object.last_colli_left){speed_norm = 0;}}
			else
			{if(object.last_colli_right){speed_norm = 0;}}
			return new Vitesse((speed_norm * ((anim<4)? -1 : 1 )),object.localVit.y);
		}
		return null;
	}
	@Override
	public DirSubTypeMouv droite_gauche(int anim,double rotation) {
		if(objType.equals(ObjectType.HEROS))
			if(anim<4)
				return (DirSubTypeMouv.GAUCHE);
			else 
				return(DirSubTypeMouv.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return DirSubTypeMouv.GAUCHE;
		}
	}
}
