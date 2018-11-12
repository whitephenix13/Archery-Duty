package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//il y a 4 animations de deux cotés 
import java.util.List;

import collision.Collidable;
import option.Config;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Course extends Mouvement_perso{

	public enum TypeCourse implements TypeMouv {CourseGauche,CourseDroite };

	//constructeur monstre
	public Course(Object obj,TypeMouv _type_mouv,int current_frame){
		super();
		type_mouv=_type_mouv;
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
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
			int start_index =type_mouv.equals(TypeCourse.CourseGauche) ? 0 : 4;
			int end_index =type_mouv.equals(TypeCourse.CourseGauche) ? 4 : 8;
			//animation frame, current_frame, start_index, end_index
			animation.start(Arrays.asList(6,12,18,24,6,12,18,24), current_frame, start_index, end_index);

		}
	}

	public Course(Object obj,TypeMouv _type_mouv, int current_frame,Animation _animation){
		this(obj,_type_mouv,current_frame);
		animation = _animation;
	}

	@Override
	public int getMaxBoundingSquare(Object obj)
	{
		return 100;
	}

	public Mouvement Copy(Object obj) {
		return new Course(obj,type_mouv,animation.getStartFrame(),animation);
	}

	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		if(TypeObject.isTypeOf(object, TypeObject.HEROS))
		{
			assert (anim>=0 && anim <8);

			int speed_norm = (int)(8.0 / Config.ratio_fps());
			if(object.deplacement.droite_gauche(object, object.anim).equals(Mouvement.GAUCHE))
			{if(object.last_colli_left){speed_norm = 0;}}
			else
			{if(object.last_colli_right){speed_norm = 0;}}
			return new Vitesse((speed_norm * ((anim<4)? -1 : 1 )),object.localVit.y);
		}
		return null;
	}
	@Override
	public String droite_gauche(Object obj,int anim) {
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
			if(anim<4)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}
}
