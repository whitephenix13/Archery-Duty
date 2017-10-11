package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.Hitbox;
import types.TypeObject;

public class Glissade extends Mouvement_perso
{
	public static int glissade_gauche = 0;
	public static int glissade_droite = 1;


	//constructeur monstre
	public Glissade(Object obj,int _type_mouv,int current_frame) 
	{
		super();
		type_mouv=_type_mouv;
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
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
			int start_index =type_mouv==glissade_gauche ? 0 : 1;
			int end_index =type_mouv==glissade_gauche ? 1 : 2;
			animation.start(Arrays.asList(2,2), current_frame, start_index, end_index);

		}
	}
	public Glissade(Object obj,int _type_mouv, int current_frame,Animation _animation){
		this(obj,_type_mouv,current_frame);
		animation = _animation;
	}
	@Override
	public int getMaxBoundingSquare(Object obj)
	{
		return 90;
	}
	public Mouvement Copy(Object obj) {
		return new Glissade(obj,type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public void setSpeed(Collidable object, int anim) {
		if(TypeObject.isTypeOf(object, TypeObject.HEROS))
		{
			//nothing
		}

	}
	@Override
	public String droite_gauche(Object obj,int anim) {
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
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
