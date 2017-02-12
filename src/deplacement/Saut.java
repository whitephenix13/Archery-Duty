package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//il y a 3 animations de deux cotés 
import java.util.List;

import collision.Collidable;
import monstre.Spirel;
import personnage.Heros;

public class Saut extends Mouvement_perso{
	//Constructor personnage
	public Saut() {
		this(Mouvement_perso.heros);
	}
	//constructeur monstre
	public Saut(String type) {
		super();
		if(type.equals(Mouvement_perso.heros))
		{
			xtaille =  Arrays.asList(85,84,74,85,84,74);
			ytaille =  Arrays.asList(97,105,91,97,105,91); 
			
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(35,27,27,10,12,4);
			List<Integer> xd = Arrays.asList(74,71,69,49,56,46);
			List<Integer> yh = Arrays.asList(8,5,5,8,5,5);
			List<Integer> yb = Arrays.asList(96,98,90,96,98,90);

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
		return new Saut(type);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim,Deplace deplace) {
		if(type.equals(Mouvement_perso.heros))
		{
			Heros heros = null; 
			if (object instanceof Heros) {
				heros = (Heros) object;
			}
			//permet de déplacer le héros sur le cote 
			final int vitMax = (object.vit.x == 0) ? 8:  Math.abs(object.vit.x) ; 
			final int varVit = 8 ; 
			final int vitSaut = -15; //10000 normalement 

			if(heros.sautGlisse)
			{
				heros.sautGlisse=false;
				object.vit.x=varVit * ((heros.droite_gauche(anim)=="Gauche") ? -1 : 1);
				object.vit.y=vitSaut;
			}
			else
			{
				if(heros.debutSaut) 
				{
					object.vit.y=vitSaut;
					heros.debutSaut =false;
				}
				else if(heros.finSaut)
				{
					object.vit.y=0;
				}
				if (heros.deplaceSautDroit  && !heros.last_colli_right)
				{
					if(object.vit.x<0)//change direction in air
						heros.runBeforeJump=false;

					if(object.vit.x<(vitMax- varVit))
						object.vit.x+= varVit;
					else 
						object.vit.x= vitMax;

					//on attend que le joueur réappui sur la touche de direction pour redeplacer
					heros.deplaceSautDroit= false;
					return;
				}
				if (heros.deplaceSautGauche && ! heros.last_colli_left)
				{
					if(object.vit.x>0)//change direction in air
						heros.runBeforeJump=false;

					if(object.vit.x>(-1*vitMax+ varVit))
						object.vit.x-= varVit;
					else 
						object.vit.x= -1*vitMax;

					//on attend que le joueur réappui sur la touche de direction pour redeplacer
					heros.deplaceSautGauche= false;
					return;
				}
			}
		}
		else if(type.equals(Mouvement_perso.m_spirel))
		{
			Spirel spirel=null;
			if(object instanceof Spirel)
				spirel=(Spirel) object;
			
			int xspeed=10;//10000
			int yspeed=15;//15000

			if(spirel.peutSauter)
			{
				object.vit.y=-1*yspeed;
			}
			if(spirel.sautGauche && ! spirel.sautDroit)
			{
				object.vit.x= -1*xspeed;
			}
			if(spirel.sautDroit && ! spirel.sautGauche)
			{
				object.vit.x= xspeed;
			}
		}
	}
}
