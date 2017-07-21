package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//il y a 4 animations de deux cotés 
import java.util.List;

import collision.Collidable;
import option.Config;
import types.TypeObject;

public class Marche extends Mouvement_perso{
	
	public static int marche_gauche = 0;
	public static int marche_droite = 1;

	//constructeur 
	public Marche(String type,int _type_mouv,int current_frame){
		super();
		type_mouv=_type_mouv;
		if(type.equals(TypeObject.heros))
		{

			xtaille =  Arrays.asList(75,75,75,75,75,75,75,75);
			ytaille =  Arrays.asList(101,100,98,100,101,100,98,100);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(27,14,37,38,26,39,16,15);
			List<Integer> xd = Arrays.asList(48,35,58,59,47,60,37,36);
			List<Integer> yh = Arrays.asList(10,9,7,9,10,9,7,9);
			List<Integer> yb = Arrays.asList(100,99,97,99,100,99,97,99);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int start_index =type_mouv==marche_gauche ? 0 : 4;
			int end_index =type_mouv==marche_gauche ? 4 : 8;
			animation.start(Arrays.asList(10,20,30,40,10,20,30,40), current_frame, start_index, end_index);

		}
		else if(type.equals(TypeObject.m_spirel))
		{
			xtaille =  Arrays.asList(56,56,56,56,-1,-1,-1,-1);
			ytaille =  Arrays.asList(75,75,75,75,-1,-1,-1,-1);
		
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0 ,0 ,0 ,0 ,-1,-1,-1,-1);
			List<Integer> xd = Arrays.asList(56,56,56,56,-1,-1,-1,-1);
			List<Integer> yh = Arrays.asList(0 ,0 ,0 ,0 ,-1,-1,-1,-1);
			List<Integer> yb = Arrays.asList(74,74,74,74,-1,-1,-1,-1);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			
			//animation frame, current_frame, start_index, end_index
			int start_index =type_mouv==marche_gauche ? 0 : 2;
			int end_index =type_mouv==marche_gauche? 2 : 4;
			animation.start(Arrays.asList(5,10,5,10), current_frame, start_index, end_index);
		}
	}

	public Marche(String type,int _type_mouv, int current_frame,Animation _animation){
		this(type,_type_mouv,current_frame);
		animation = _animation;
	}
	public Mouvement Copy(String type) {
		return new Marche(type,type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim) {
		if(type.equals(TypeObject.heros))
		{
			int speed_norm = (int)(3.0 / Config.ratio_fps());

			assert (anim>=0 && anim <8);
			object.localVit.x=(speed_norm * ((anim<4)? -1 : 1 ));//20 for old deplace
		}
		else if(type.equals(TypeObject.m_spirel))
		{
			int speed_norm = (int)(3.0 / Config.ratio_fps());
			if(anim<2)
				object.localVit.x=(-1*speed_norm);
			
			else
				object.localVit.x=( speed_norm);
		}
	}
	@Override
	public String droite_gauche(String type,int anim) {
		if(type.equals(TypeObject.heros))
			if(anim<4)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else if(type.equals(TypeObject.m_spirel))
			if(anim<2)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}
}
