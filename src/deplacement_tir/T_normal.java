package deplacement_tir;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import deplacement.Animation;
import deplacement.Mouvement;
import deplacement.TypeMouv;
import fleches.Fleche;
import option.Config;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class T_normal extends Mouvement_tir{

	public enum TypeTirNormal implements TypeMouv {Tir};

	//constructeur des monstres 
	public T_normal(Object obj,TypeMouv _type_mouv,int current_frame){
		super();
		type_mouv=_type_mouv;
		if(TypeObject.isTypeOf(obj, TypeObject.FLECHE))
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
		else if(TypeObject.isTypeOf(obj, TypeObject.TIR_SPIREL))
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
			animation.setLoop(false);
			animation.start(Arrays.asList(delta,2*delta,3*delta), current_frame, 0, 3);
		}
		else
			System.err.println("Unkown type "+ obj.getClass().getName());
	}
	public T_normal(Object obj,TypeMouv _type_mouv, int current_frame,Animation _animation){
		this(obj,_type_mouv,current_frame);
		animation = _animation;
	}
	@Override
	public int getMaxBoundingSquare(Object obj)
	{
		if(TypeObject.isTypeOf(obj, TypeObject.FLECHE))
			return 45;
		else if(TypeObject.isTypeOf(obj, TypeObject.TIR_SPIREL))
			return 114;
		else
			return 0;
	}
	@Override
	public Mouvement Copy(Object obj) {
		return new T_normal(obj,type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		if(TypeObject.isTypeOf(object, TypeObject.FLECHE))
		{
			Fleche f = (Fleche)object;
			int speed_norm = (int)(30.0 / Config.ratio_fps());
			if(TypeObject.isTypeOf(object, TypeObject.GRAPPIN))
				speed_norm = (int)(60.0 / Config.ratio_fps());//60
			return object.convertSpeed(speed_norm,object.rotation);
		}
		else if(TypeObject.isTypeOf(object, TypeObject.TIR_SPIREL))
		{
			int speed_norm = (int)(10.0 / Config.ratio_fps());
			return object.convertSpeed(speed_norm,object.rotation);
		}
		else{
			System.err.println("Unkown type "+ object.getClass().getName());
			return null;}
	}
	@Override
	public String droite_gauche(Object obj,int anim) {
		if(TypeObject.isTypeOf(obj, TypeObject.FLECHE))
			if(anim<2)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		//ARBITRARY
		else if(TypeObject.isTypeOf(obj, TypeObject.TIR_SPIREL))
			if(anim<2)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}
	@Override
	public int updateAnimation(Object obj,int anim,int current_frame,double speedFactor) {
		if(TypeObject.isTypeOf(obj, TypeObject.FLECHE))
			return animation.update(anim,current_frame,speedFactor);
		else  if(TypeObject.isTypeOf(obj, TypeObject.TIR_SPIREL))
			return animation.update(0,current_frame,speedFactor);
		else 
			return -1;
	}
}
