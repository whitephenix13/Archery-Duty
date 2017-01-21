package deplacement;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.Hitbox;
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
			xtaille =  Arrays.asList(70,70,-1,-1,-1,-1,-1,-1);
			ytaille =  Arrays.asList(94,94,-1,-1,-1,-1,-1,-1);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(21,16);
			List<Integer> xd = Arrays.asList(54,49);
			List<Integer> yh = Arrays.asList(25,25);
			List<Integer> yb = Arrays.asList(94,94);

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
