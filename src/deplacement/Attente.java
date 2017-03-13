package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.TypeObject;
import types.Vitesse;

//il y a 1 animations de deux cotés 

public class Attente extends Mouvement_perso{

	public static int attente_gauche = 0;
	public static int attente_droite = 1;

	//constructeur des monstres 
	public Attente(String type,int _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;
		if(type.equals(TypeObject.heros))
		{
			xtaille =  Arrays.asList(85,84,85,84);
			ytaille =  Arrays.asList(100,100,100,100);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(40,40,13,12);
			List<Integer> xd = Arrays.asList(71,71,44,43);
			List<Integer> yh = Arrays.asList(13,13,13,13);
			List<Integer> yb = Arrays.asList(99,99,99,99);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int start_index =type_mouv==attente_gauche ? 0 : 2;
			int end_index =type_mouv==attente_gauche ? 2 : 4;
			animation.start(Arrays.asList(80,160,80,160), current_frame, start_index, end_index);
		}
		else if(type.equals(TypeObject.m_spirel))
		{
			xtaille =  Arrays.asList(56,56,-1,-1,-1,-1,-1,-1);
			ytaille =  Arrays.asList(75,75,-1,-1,-1,-1,-1,-1);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0,0);
			List<Integer> xd = Arrays.asList(56,56);
			List<Integer> yh = Arrays.asList(0,0);
			List<Integer> yb = Arrays.asList(74,74);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			int start_index =type_mouv==attente_gauche ? 0 : 1;
			int end_index =type_mouv==attente_gauche ? 1 : 2;
			//animation frame, current_frame, start_index, end_index
			animation.start(Arrays.asList(20,20), current_frame, start_index, end_index);

		}
	}

	public Attente(String type,int _type_mouv, int current_frame,Animation _animation){
		this(type,_type_mouv,current_frame);
		animation = _animation;
	}
	
	public Mouvement Copy(String type) {
		return new Attente(type,type_mouv,animation.getStartFrame(),animation);
	}
	
	@Override
	public void setSpeed(String type, Collidable object, int anim) {
		if(type.equals(TypeObject.heros))
			object.vit=new Vitesse(0,0);
		
		else if(type.equals(TypeObject.m_spirel))
			object.vit=new Vitesse(0,0);
	}

	@Override
	public String droite_gauche(String type,int anim) {
		if(type.equals(TypeObject.heros))
			if(anim<2)
				return ("Gauche");
			else 
				return("Droite");
		else if(type.equals(TypeObject.m_spirel))
			if(anim<1)
				return ("Gauche");
			else 
				return("Droite");
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}

}
