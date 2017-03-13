package deplacement_tir;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import deplacement.Animation;
import deplacement.Mouvement;
import option.Config;
import types.TypeObject;
import types.Vitesse;

public class T_normal extends Mouvement_tir{

	public static int tir = 0;

	//constructeur des monstres 
	public T_normal(String type,int _type_mouv,int current_frame){
		super();
		type_mouv=_type_mouv;
		if(type.equals(TypeObject.fleche))
		{                     
			xtaille=Arrays.asList(42,45,42,44);
			ytaille=Arrays.asList(19,15,19,16);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(6,6,6,5);
			List<Integer> xd = Arrays.asList(37,37,37,36);
			List<Integer> yh = Arrays.asList(5,5,5,3);
			List<Integer> yb = Arrays.asList(11,11,11,9);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			animation.start(Arrays.asList(2,4,6,8), current_frame, 0, 4);

		}
		else if(type.equals(TypeObject.tir_spirel))
		{

			xtaille= Arrays.asList(114,114,34 );
			ytaille= Arrays.asList(34 ,34 ,114);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0,0,0);
			List<Integer> xd = Arrays.asList(114,114,34);
			List<Integer> yh = Arrays.asList(0,0,0);
			List<Integer> yb = Arrays.asList(34,34,114);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			animation.start(Arrays.asList(2), current_frame, 0, 1);
		}
	}
	public T_normal(String type,int _type_mouv, int current_frame,Animation _animation){
		this(type,_type_mouv,current_frame);
		animation = _animation;
	}
	@Override
	public Mouvement Copy(String type) {
		return new T_normal(type,type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim) {
		if(type.equals(TypeObject.fleche))
		{
			int speed_norm = (int)(30.0 / Config.ratio_fps());
			Vitesse vit = object.convertSpeed(speed_norm,object.rotation);
			object.vit.x=vit.x;
			object.vit.y=vit.y;
		}
		else if(type.equals(TypeObject.tir_spirel))
		{
			int speed_norm = (int)(10.0 / Config.ratio_fps());

			switch(anim)
			{
			case 0 : object.vit.x= 1*speed_norm;break;
			case 1 : object.vit.x= -1*speed_norm;break;
			case 2 : object.vit.y=-1*speed_norm;break;
			}
		}
	}
	@Override
	public String droite_gauche(String type,int anim) {
		if(type.equals(TypeObject.fleche))
			if(anim<2)
				return ("Gauche");
			else 
				return("Droite");
		//ARBITRARY
		else if(type.equals(TypeObject.tir_spirel))
			if(anim<2)
				return ("Gauche");
			else 
				return("Droite");
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}
	@Override
	public int updateAnimation(String type,int anim,int current_frame) {
		if(type.equals(TypeObject.fleche))
			return animation.update(anim,current_frame);
		else  if(type.equals(TypeObject.tir_spirel))
			return animation.update(0,current_frame);
		else 
			return -1;
	}
}
