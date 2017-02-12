package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.Vitesse;

//il y a 1 animations de deux cotés 

public class Attente extends Mouvement_perso{
	//constructeur du personnage
	public Attente(){
		this(Mouvement_perso.heros);
	}

	//constructeur des monstres 
	public Attente(String type ){
		super();
		if(type.equals(Mouvement_perso.heros))
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
			
		}
		else if(type.equals(Mouvement_perso.m_spirel))
		{
			xtaille =  Arrays.asList(56,56,-1,-1,-1,-1,-1,-1);
			ytaille =  Arrays.asList(75,75,-1,-1,-1,-1,-1,-1);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0,0);
			List<Integer> xd = Arrays.asList(56,56);
			List<Integer> yh = Arrays.asList(0,0);
			List<Integer> yb = Arrays.asList(75,75);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);

		}
	}

	public Mouvement Copy(String type) {
		return new Attente(type);
	}
	
	@Override
	public void setSpeed(String type, Collidable object, int anim,Deplace deplace) {
		if(type.equals(Mouvement_perso.heros))
			object.vit=new Vitesse(0,0);
		
		else if(type.equals(Mouvement_perso.m_spirel))
			object.vit=new Vitesse(0,0);
	}
}
