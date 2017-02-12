package deplacement_tir;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import deplacement.Deplace;
import deplacement.Mouvement;
import types.Vitesse;

public class T_normal extends Mouvement_tir{


	//constructeur du personnage
	public T_normal(){
		this(Mouvement_tir.fleche);
	}

	//constructeur des monstres 
	public T_normal(String type ){
		super();
		if(type.equals(Mouvement_tir.fleche))
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
		}
		else if(type.equals(Mouvement_tir.tir_spirel))
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
		}
	}

	@Override
	public Mouvement Copy(String type) {
		return new T_normal(type);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim,Deplace deplace) {
		if(type.equals(Mouvement_tir.fleche))
		{
			int norm_speed = 30;
			Vitesse vit = object.convertSpeed(norm_speed,object.rotation);
			object.vit.x=vit.x;
			object.vit.y=vit.y;
		}
		else if(type.equals(Mouvement_tir.tir_spirel))
		{
			int vitesse=10;
			switch(anim)
			{
			case 0 : object.vit.x= 1*vitesse;break;
			case 1 : object.vit.x= -1*vitesse;break;
			case 2 : object.vit.y=-1*vitesse;break;
			}
		}
	}
}
