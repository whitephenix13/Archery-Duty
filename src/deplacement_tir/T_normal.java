package deplacement_tir;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import deplacement.Attente;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;

public class T_normal extends Mouvement_tir{


	//constructeur du personnage
	public T_normal(){
		this(Mouvement_tir.fleche);
	}

	//constructeur des monstres 
	public T_normal(String type ){
		super();
		if(type.equals(Mouvement_tir.fleche))
		{                     
			xtaille=Arrays.asList(46,37,10,37,46,37,10,37);
			ytaille=Arrays.asList(10,37,46,37,10,37,46,37);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xgh = Arrays.asList(0 ,6 ,0 ,30,0 ,6 ,0 ,30);
			List<Integer> ygh = Arrays.asList(0 ,0 ,0 ,0 ,0 ,0 ,0, 0 );

			List<Integer> xgb = Arrays.asList(0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 );
			List<Integer> ygb = Arrays.asList(10,6 ,46,30,10,6 ,46,30);

			List<Integer> xdb = Arrays.asList(46,30,10,6 ,46,30,10,6 );
			List<Integer> ydb = Arrays.asList(10,36,46,36,10,36,46,36);

			List<Integer> xdh = Arrays.asList(46,36,10,36,46,36,10,36);
			List<Integer> ydh = Arrays.asList(0 ,30,0 ,6 ,0 ,30,0, 6 );



			hitboxCreation.add(asListPoint(xgh,ygh));
			hitboxCreation.add(asListPoint(xgb,ygb));
			hitboxCreation.add(asListPoint(xdb,ydb));
			hitboxCreation.add(asListPoint(xdh,ydh));

			hitbox = createHitbox(hitboxCreation);
		}
		else if(type.equals(Mouvement_tir.tir_spirel))
		{

			xtaille= Arrays.asList(114,114,34 );
			ytaille= Arrays.asList(34 ,34 ,114);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0,0,0);
			List<Integer> xd = Arrays.asList(114,114,34);
			List<Integer> yh = Arrays.asList(0,0,0);
			List<Integer> yb = Arrays.asList(34,34,114);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
		}
	}

	@Override
	public Mouvement Copy(String type) {
		return new T_normal(type);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim,Deplace deplace) {
		if(type.equals(Mouvement_tir.fleche))
		{
			int vitesse = 40000;
			int vitesseReduite= (int) (40000f/(2));
			switch(anim)
			{
			case 0 : 
				object.vit.x= vitesse;
				object.vit.y=0;
				break;
			case 1 : 
				object.vit.x= vitesseReduite;
				object.vit.y= vitesseReduite;
				break;
			case 2 : 
				object.vit.x=0;
				object.vit.y=vitesse;
				break;
			case 3 : 
				object.vit.x= -1 * vitesseReduite;
				object.vit.y= vitesseReduite;
				break;
			case 4 : 
				object.vit.x= -1 * vitesse;
				object.vit.y= 0;
				break;
			case 5 : 
				object.vit.x= -1 * vitesseReduite;
				object.vit.y= -1 * vitesseReduite;
				break;
			case 6 : 
				object.vit.x=0;
				object.vit.y= -1 *vitesse;
				break;
			case 7 : 
				object.vit.x= vitesseReduite;
				object.vit.y= -1 *vitesseReduite;
				break;
			default : 	throw new IllegalArgumentException("ERREUR: set position fleche: anim inconnue ");
			}
		}
		else if(type.equals(Mouvement_tir.tir_spirel))
		{
			int vitesse=10000;
			switch(anim)
			{
			case 0 : object.vit.x= 1*vitesse;break;
			case 1 : object.vit.x= -1*vitesse;break;
			case 2 : object.vit.y=-1*vitesse;break;
			}
		}
	}
}
