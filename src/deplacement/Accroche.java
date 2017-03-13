package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.TypeObject;
import types.Vitesse;

//il y a 1 animations de deux cotés 

public class Accroche extends Mouvement_perso{

	public static int accroche_gauche = 0;
	public static int grimpe_gauche = 1;
	public static int accroche_droite = 2;
	public static int grimpe_droite = 3;

	//constructeur des monstres 
	public Accroche(String type,int _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;
		if(type.equals(TypeObject.heros))
		{
			xtaille =  Arrays.asList(83,44,83,44);
			ytaille =  Arrays.asList(99,82,99,82);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(42,4 ,11,4);
			List<Integer> xd = Arrays.asList(71,28,40,39);
			List<Integer> yh = Arrays.asList(12,4,12,13);
			List<Integer> yb = Arrays.asList(94,67,94,67);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int start_index=0;int end_index=0;
			if(type_mouv==accroche_gauche){
				start_index=0;end_index=1;}
			else if(type_mouv==grimpe_gauche){
				start_index=1;end_index=2;}
			else if(type_mouv==accroche_droite){
				start_index=2;end_index=3;}
			else if(type_mouv==grimpe_droite){
				start_index=3;end_index=4;}
			animation.start(Arrays.asList(10,4,10,4), current_frame, start_index, end_index);
		}
	}
	public Accroche(String type,int _type_mouv, int current_frame,Animation _animation){
		this(type,_type_mouv,current_frame);
		animation = _animation;
	}
	public Mouvement Copy(String type) {
		return new Accroche(type,type_mouv,animation.getStartFrame(),animation);
	}
	
	@Override
	public void setSpeed(String type, Collidable object, int anim) {
		if(type.equals(TypeObject.heros))
			object.vit=new Vitesse(0,0);
		
		}

	@Override
	public String droite_gauche(String type,int anim) {
		if(type.equals(TypeObject.heros))
			if(anim<2)
				return ("Gauche");
			else 
				return("Droite");
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}

}
