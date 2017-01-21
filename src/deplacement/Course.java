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
			xtaille =  Arrays.asList(64,100,74,108,64,100,74,108);
			ytaille =  Arrays.asList(78,78,76,72,78,78,76,72);

			// 78,78,76,72,78,78,76,72 

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(10,30,10,16,15,32,33,52);
			List<Integer> xd = Arrays.asList(49,68,41,54,54,70,64,90);
			List<Integer> yh = Arrays.asList(20,20,19,16,20,20,19,16);
			List<Integer> yb = Arrays.asList(78,78,76,72,78,78,76,72);

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
			object.vit.x= 40000 * ((anim<4)? -1 : 1 );
		}
		else if(type.equals(Mouvement_perso.m_spirel))
		{
			
		}
	}
}
