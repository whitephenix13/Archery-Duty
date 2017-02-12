package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//il y a 4 animations de deux cotés 
import java.util.List;

import collision.Collidable;

public class Course extends Mouvement_perso{
	//constructeur personnage
	public Course(){
		this(Mouvement_perso.heros);
	}
	//constructeur monstre
	public Course(String type){
		super();
		if(type==Mouvement_perso.heros)
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

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);

		}
	}

	public Mouvement Copy(String type) {
		return new Course(type);
	}
	
	@Override
	public void setSpeed(String type, Collidable object, int anim,Deplace deplace) {
		if(type.equals(Mouvement_perso.heros))
		{
			assert (anim>=0 && anim <8);
			object.vit.x= 8 * ((anim<4)? -1 : 1 );//40 for old deplace
		}
		else if(type.equals(Mouvement_perso.m_spirel))
		{
			
		}
	}
}
