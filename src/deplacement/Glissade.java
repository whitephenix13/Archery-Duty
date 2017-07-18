package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.TypeObject;

public class Glissade extends Mouvement_perso
{
	public static int glissade_gauche = 0;
	public static int glissade_droite = 1;
	

	//constructeur monstre
	public Glissade(String type,int _type_mouv,int current_frame) 
	{
		super();
		type_mouv=_type_mouv;
		if(type==TypeObject.heros)
		{
			xtaille =  Arrays.asList(49,49);
			ytaille =  Arrays.asList(89,89);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(9,0);
			List<Integer> xd = Arrays.asList(47,39);
			List<Integer> yh = Arrays.asList(5,5);
			List<Integer> yb = Arrays.asList(88,88);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			

			//animation frame, current_frame, start_index, end_index
			int start_index =type_mouv==glissade_gauche ? 0 : 1;
			int end_index =type_mouv==glissade_gauche ? 1 : 2;
			animation.start(Arrays.asList(2,2), current_frame, start_index, end_index);

		}
	}
	public Glissade(String type,int _type_mouv, int current_frame,Animation _animation){
		this(type,_type_mouv,current_frame);
		animation = _animation;
	}
	public Mouvement Copy(String type) {
		return new Glissade(type,type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim) {
		if(type.equals(TypeObject.heros))
		{
			//nothing
		}
		else if(type.equals(TypeObject.m_spirel))
		{
			//nothing
		}
	}
	@Override
	public String droite_gauche(String type,int anim) {
		if(type.equals(TypeObject.heros))
			if(anim<1)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}
}
