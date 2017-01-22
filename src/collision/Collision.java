package collision;
/*
 * 
 * 
 * probleme: on doit initialiser a et b pour gérer le cas ou le point est exactement sur le bord du carré
 * le probleme de collision vient du problème de décallage des sprites en x et du fait que la hitbox se décalle 
 * GERER Les xdeplacementECRAN !!!!! : il faut tester les collisions en ajoutant le depalcement de l'ecran
 * 
 *Travailler en variable décallée : xperso-xdecallEcran et yperso-ydecallEcran
 * 
 * */
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import deplacement.Deplace;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Bloc;
import types.Hitbox;
import types.Monde;
import types.Vitesse;

public class Collision implements InterfaceConstantes{

	public static List<Bloc> getMondeBlocs(Monde monde,Hitbox objectHitbox, Point INIT_RECT, int TAILLE_BLOC)
	{
		List<Bloc> mondeBlocs = new ArrayList<Bloc>();
		Polygon poly = objectHitbox.polygon;

		assert (poly.npoints>0);
		Point p0 = new Point(poly.xpoints[0],poly.ypoints[0]);
		Point min = p0;
		Point max = p0;

		for(int i=0; i< poly.npoints; ++i)
		{
			Point p = new Point(poly.xpoints[i],poly.ypoints[i]);
			min= new Point ( Math.min(min.x, p.x),Math.min(min.y, p.y) );
			max= new Point ( Math.max(max.x, p.x),Math.max(max.y, p.y) );
		}
		min.x-=2;//deal with case of touching objects
		min.y-=2;//deal with case of touching objects
		max.x+=2;//deal with case of touching objects
		max.y+=2;//deal with case of touching objects

		for(int x = min.x; x<(max.x+TAILLE_BLOC) ; x+=TAILLE_BLOC)
			for(int y = min.y; y<(max.y+TAILLE_BLOC); y+=TAILLE_BLOC)
			{
				Bloc bloc = monde.niveau[(x + INIT_RECT.x)/TAILLE_BLOC][(y + INIT_RECT.y)/TAILLE_BLOC];
				if(bloc.getHitbox(INIT_RECT)!=null)
				{
					mondeBlocs.add(bloc);
				}

			}

		return mondeBlocs;
	}
	/**
	 * 
	 * @return True if the object is colliding with the world
	 */
	public boolean isWorldCollision(AbstractModelPartie partie,Deplace deplace, Collidable object)
	{
		List<Bloc> mondeBlocs = null;
		Hitbox objectHitbox = null;
		//translate all object hitboxes
		Point CompScreenMove1 = new Point(0,0);

		Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
									  partie.ydeplaceEcran+partie.ydeplaceEcranBloc);

		if(object.fixedWhenScreenMoves)
			CompScreenMove1= new Point(deplaceEcran.x,deplaceEcran.y);

		Point p = new Point(CompScreenMove1.x,CompScreenMove1.y);
		objectHitbox= Hitbox.minusPoint(object.getHitbox(partie.INIT_RECT),p,false);

		Vitesse speed= object.vit;
		Vitesse minSpeed= new Vitesse(-1*speed.x,-1*speed.y);
		
		mondeBlocs = getMondeBlocs(partie.monde,objectHitbox, partie.INIT_RECT,
				partie.TAILLE_BLOC);
		for(Bloc mondeBloc : mondeBlocs)
		{
			Hitbox mondeBox = mondeBloc.getHitbox(partie.INIT_RECT);
		
			Vector2d supp1 = GJK_EPA.support(mondeBox.polygon,minSpeed.vect2d() );//fixed one
			Vector2d supp2 = GJK_EPA.support(objectHitbox.polygon, speed.vect2d());//mobile one
			Vector2d firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);
	
			List<Vector2d> simplex = GJK_EPA.intersects(mondeBox.polygon,objectHitbox.polygon ,firstDir);
			List<Vector2d> normals = new ArrayList<Vector2d>();

			int collision_type=-1;
			boolean dNull= true;
			Double dInter=0.0d;
			if(simplex!=null)
			{
				dNull=false;
				dInter= GJK_EPA.EPA(mondeBox.polygon, objectHitbox.polygon, simplex, minSpeed.vect2d(), normals);
			}
			collision_type =  GJK_EPA.isIntersect(dInter,dNull);

			return collision_type == GJK_EPA.INTER;
		}
		return false;
	}
	/** @return false if object is stuck into environment */
	public boolean ejectWorldCollision(AbstractModelPartie partie,Deplace deplace, Collidable object)
	{
		//on calcul l'endroit où serait le nouvel objet
		int xDeplacement=0;
		int yDeplacement=0;
		List<Bloc> mondeBlocs = null;
		Hitbox objectHitbox = null;

		Vitesse speed= object.vit;
		Vitesse minSpeed= new Vitesse(-1*speed.x,-1*speed.y);

		if(partie.slowDown)
		{
			xDeplacement=(int) (speed.x*T/1000/object.slowDownFactor);
			yDeplacement=(int) (speed.y*T/1000/object.slowDownFactor);
		}
		else
		{
			xDeplacement=(int) (speed.x*T/1000);
			yDeplacement=(int) (speed.y*T/1000);
		}

		boolean noIntersection = false;
		//distance which must move the object to avoid any collisions
		Point totalInterDist = new Point(0,0);

		//variables pour connaitre la direction d'intersection
		Hitbox intersectedHitbox=null; // hitbox intersectée par l'objet mobile
		Vector2d EPA_normal = null; //normal du coté intersecté
		while(!noIntersection)
		{
			//intersectedHitbox= null;
			//EPA_normal=null;
			noIntersection=true;
			//distance which must move the object to avoid actual collision with the world
			Point maxInterDist = new Point(0,0);

			//translate all object hitboxes
			int xCompScreenMove=0; 
			int yCompScreenMove=0;
			Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
					partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
			if(object.fixedWhenScreenMoves)
			{
				xCompScreenMove=deplaceEcran.x;
				yCompScreenMove=deplaceEcran.y;
			}

			Point p = new Point(xCompScreenMove-totalInterDist.x-xDeplacement,
					yCompScreenMove-totalInterDist.y-yDeplacement);
			objectHitbox= Hitbox.minusPoint(object.getHitbox(partie.INIT_RECT),p,false);

			mondeBlocs = getMondeBlocs(partie.monde,objectHitbox, partie.INIT_RECT,
					partie.TAILLE_BLOC);
			for(Bloc mondeBloc : mondeBlocs)
			{
				Hitbox mondeBox = mondeBloc.getHitbox(partie.INIT_RECT);
				//p = new Point( (mondeBloc.fixedWhenScreenMoves? 0 : deplaceEcran.x)+totalInterDist.x,
				//	(mondeBloc.fixedWhenScreenMoves? 0 : deplaceEcran.y)+totalInterDist.y);
				//mondeBox= Hitbox.minusPoint(mondeBox, p);

				Vector2d supp1 = GJK_EPA.support(mondeBox.polygon,minSpeed.vect2d() );//fixed one
				Vector2d supp2 = GJK_EPA.support(objectHitbox.polygon, speed.vect2d());//mobile one
				Vector2d firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);

				List<Vector2d> simplex = GJK_EPA.intersects(mondeBox.polygon,objectHitbox.polygon ,firstDir);


				List<Vector2d> normals = new ArrayList<Vector2d>();

				int collision_type=-1;
				boolean dNull= true;
				Double dInter=0.0d;
				if(simplex!=null)
				{
					dNull=false;
					dInter= GJK_EPA.EPA(mondeBox.polygon, objectHitbox.polygon, simplex, minSpeed.vect2d(), normals);
				}
				collision_type =  GJK_EPA.isIntersect(dInter,dNull);

				boolean inTouch= collision_type == GJK_EPA.TOUCH;
				boolean inCollision = collision_type==GJK_EPA.INTER; 
				if(inTouch || inCollision)
				{
					//Object is stuck in environment
					if(speed.x==0 && speed.y==0)
						return false; //stuck

					Vector2d vectOut = new Vector2d(-1*speed.x,-1*speed.y);
					//floor: the largest (closest to positive infinity) floating-point value that less than or equal to the argument 
					//expected : x>0, out = -2.5 ,floor: -3  ,value expected = -3  
					//expected : x>0, out = -3 ,floor: -3 ,value expected = -4  
					//expected : x<0, out = 2.5 ,floor: 2 ,value expected = 3  
					//expected : x<0, out = 3 ,floor: 3 ,value expected = 4  

					int x = (speed.x<0? 1 : 0);
					int y = (speed.y<0? 1 : 0);
					//avoid that the ejected object is exactly on the collided object
					double xequ = (speed.x>0 && speed.x==(int)speed.x)? -1 : 0;
					double yequ = (speed.y>0 && speed.y==(int)speed.y)? -1 : 0;
					vectOut.normalize();
					//vectOut= new Vector2d(Math.round(vectOut.x*dInter),Math.round(vectOut.y*dInter));
					vectOut= new Vector2d(Math.floor(vectOut.x*dInter)+x+xequ,Math.floor(vectOut.y*dInter)+y+yequ);
					intersectedHitbox=mondeBox;
					if((Math.abs(maxInterDist.x)-Math.abs((int)vectOut.x))<0 || 
							((Math.abs(maxInterDist.y)-Math.abs((int)vectOut.y))<0 ))
					{
						if(normals.size()>0)
						{
							EPA_normal=normals.get(0);
						}
						maxInterDist.x =(int)vectOut.x;
						maxInterDist.y =(int)vectOut.y;
					}
					noIntersection=false;
				}

			}
			totalInterDist.x += maxInterDist.x;
			totalInterDist.y += maxInterDist.y;

		}
		boolean xlimExceeded = (Math.abs(totalInterDist.x)> Math.abs(xDeplacement));
		boolean ylimExceeded = (Math.abs(totalInterDist.y)> Math.abs(yDeplacement));
		object.xpos+= xlimExceeded ? 0 : (xDeplacement+totalInterDist.x);
		object.ypos+= ylimExceeded ? 0 : (yDeplacement+totalInterDist.y);	

		//last collision test to check if the object is now out 
		if(xlimExceeded || ylimExceeded)
		{
			if(isWorldCollision(partie,deplace, object))
				return false; // object is stuck
		}

		//on calcul la direction de collision 
		if(intersectedHitbox!=null)
		{
			//on appelle la fonction qui gère les collisions en fonction de la normale
			object.handleWorldCollision(EPA_normal,partie,deplace);
		}
		return true; //not stuck
	}
	/**
	 * Test collision between two object without moving them
	 * @param partie
	 * @param deplace
	 * @param object1
	 * @param object2
	 * @return True if the two objects are colliding
	 */
	public boolean collisionObjects(AbstractModelPartie partie,Deplace deplace, Collidable object1,Collidable object2)
	{
		
		Hitbox objectHitbox1 = null;
		Hitbox objectHitbox2 = null;


		//translate all object hitboxes
		Point CompScreenMove1 = new Point(0,0);
		Point CompScreenMove2 = new Point(0,0);

		Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
									  partie.ydeplaceEcran+partie.ydeplaceEcranBloc);

		if(object1.fixedWhenScreenMoves)
			CompScreenMove1= new Point(deplaceEcran.x,deplaceEcran.y);

		if(object2.fixedWhenScreenMoves)
			CompScreenMove2= new Point(deplaceEcran.x,deplaceEcran.y);


		Point p1 = new Point(CompScreenMove1.x,CompScreenMove1.y);
		objectHitbox1= Hitbox.minusPoint(object1.getHitbox(partie.INIT_RECT),p1,false);

		Point p2 = new Point(CompScreenMove2.x,CompScreenMove2.y);
		objectHitbox2= Hitbox.minusPoint(object2.getHitbox(partie.INIT_RECT),p2,false);

		Vector2d deltaSpeed = new Vector2d(object1.vit.x-object2.vit.x,object1.vit.y-object2.vit.y);
		Vector2d m_deltaSpeed= new Vector2d(-deltaSpeed.x,-deltaSpeed.y);
		
		Vector2d supp1 = GJK_EPA.support(objectHitbox1.polygon,deltaSpeed );//fixed one
		Vector2d supp2 = GJK_EPA.support(objectHitbox2.polygon, m_deltaSpeed);//mobile one
		Vector2d firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);

		List<Vector2d> simplex = GJK_EPA.intersects(objectHitbox1.polygon,objectHitbox2.polygon ,firstDir);
		if(simplex!=null){
			object1.handleObjectCollision(partie, deplace);
			object2.handleObjectCollision(partie, deplace);
			return true;
		}
		else
			return false;
	}
}


