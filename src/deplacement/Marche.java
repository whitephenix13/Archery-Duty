package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//il y a 4 animations de deux cotés 
import java.util.List;

import collision.Collidable;

public class Marche extends Mouvement_perso{
	//pour le sprite tourné vers la droite: xdecallsprite=xtaille - xdecallsprite(gauche)-xhitbox
	//constructeur personnage
	public Marche()
	{
		this(Mouvement_perso.heros);
	}
	//constructeur monstre
	public Marche(String type){
		super();
		if(type.equals(Mouvement_perso.heros))
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
		}
		else if(type.equals(Mouvement_perso.m_spirel))
		{
			xtaille =  Arrays.asList(56,56,56,56,-1,-1,-1,-1);
			ytaille =  Arrays.asList(75,75,75,75,-1,-1,-1,-1);
		
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0 ,0 ,0 ,0 ,-1,-1,-1,-1);
			List<Integer> xd = Arrays.asList(56,56,56,56,-1,-1,-1,-1);
			List<Integer> yh = Arrays.asList(0 ,0 ,0 ,0 ,-1,-1,-1,-1);
			List<Integer> yb = Arrays.asList(75,75,75,75,-1,-1,-1,-1);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);


		}
	}


	public Mouvement Copy(String type) {
		return new Marche(type);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim,Deplace deplace) {
		if(type.equals(Mouvement_perso.heros))
		{
			assert (anim>=0 && anim <8);
			object.vit.x= 2 * ((anim<4)? -1 : 1 );//20 for old deplace
		}
		else if(type.equals(Mouvement_perso.m_spirel))
		{
			int speed=10;//4000
			if(anim<2)
				object.vit.x=-1*speed;
			
			else
				object.vit.x= speed;
		}
	}
}
