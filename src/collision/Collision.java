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

import monstre.Monstre;
import monstre.TirMonstre;
import partie.AbstractModelPartie;
import personnage.Fleche;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Bloc;
import types.Hitbox;
import types.Monde;
import types.Point_float;
import types.Vitesse;
import deplacement.Deplace;

public class Collision implements InterfaceConstantes{
	//variables communes 
	//{{

	//variable pour savoir de combien il va se deplacer

	//Deplace deplace ;
	int xDeplacement = 0;
	int yDeplacement = 0;

	//variable de parcours 
	int i;
	int j;

	static int AUCUNE = 0;
	static int HAUT = 1;
	static int DROITE = 2;
	static int BAS = 3;
	static  int GAUCHE = 4;
	static int HAUTDROITE = 5;
	static int BASDROITE = 6;
	static int BASGAUCHE = 7;
	static int HAUTGAUCHE = 8;

	//variable pour savoir ou on se trouve sur le coté du carré

	boolean bordGauche = false;
	boolean bordHaut = false;
	boolean bordBas= false;
	boolean bordDroite = false;

	//connaitre la position fictive de l'objet quand on le deplace
	float x;
	float y;

	//calcul le point suivant pour savoir si on est allé trop loin ou non
	Point_float pointsuivant;

	//variables permetant de savoir jusqu'a ou on doit explorer
	//si il y a collision, cette variable prend la valeur de la ou il y a eu collision 
	int xlim;
	int ylim;
	//position initiale du point de la hitbox 
	int xori;
	int yori;
	//booleen pour savoir si on a fini d'étudier un point 
	boolean stop;
	//connaitre la direction ou on va :
	int direction;
	//indique la direction d'ou on vient
	int ancienDirection;
	//tolérance due aux erreurs de calcul (tangente) lors du test point suivant 
	float epsilon = 0.001f; 

	//}}
	//variables pour heros 
	//{{
	//variable pour stocker les variables de sauts lors d'un test
	boolean testFinSaut;
	boolean testPeutSauter;
	boolean testGlisse;

	// }}
	//variables pour fleche
	//{{
	int xHG =0;
	int xHD =0;
	int xBG =0;
	int xBD= 0;
	int yHG =0;
	int yHD=0;
	int yBG =0;
	int yBD=0;
	//}}
	//variables pour monstres
	//{{
	//variable pour stocker les variables de sauts lors d'un test
	boolean testMFinSaut;
	boolean testMPeutSauter;
	boolean testMGlisse;
	//}}
	//variables pour TirMonstre 
	//{{
	int xTMG ;
	int xTMD;
	int yTMH;
	int yTMB;
	//}}
	//Fonctions communes
	//{{
	public Collision(){
	}
	public int initI() 
	{
		if (direction ==AUCUNE)
		{return(3);}
		else if (direction <=4)//direction == HAUT,DROITE,BAS,GAUCHE
		{return(1);}
		else if (direction <=8){return(0);}
		else {throw new IllegalArgumentException("ERREUR direction Collision/initI: ,"+ direction);}
	}
	public void pointSuivant( float alphalim, float alpha)
	{
		if(direction==AUCUNE){
			throw new IllegalArgumentException("PointSuivant: direction = aucune");
		}
		else if(direction==HAUT)
		{
			pointsuivant.x=x;
			pointsuivant.y=divisionGaucheHaut(y)- (bordHaut ? 100 : 0);
			ancienDirection= BAS;
			//bordGauche inchangé
			bordHaut=true;


		}
		else if(direction == HAUTDROITE)
		{
			alphalim= (float) Math.atan( (divisionGaucheHaut(y) - (bordHaut ? 100 : 0)-y)/((divisionGaucheHaut(x)+100)-x)); // alphalim est négatif aussi
			if(Math.abs(alpha)<Math.abs(alphalim))
			{
				// on va a droite : 
				pointsuivant.y=(float) (y-Math.tan(alpha)*(x-(divisionGaucheHaut(x)+100))); // Math.tan(alpha)*(x-x/100*100+100) est bien positif
				pointsuivant.x=divisionGaucheHaut(x)+100;
				ancienDirection= GAUCHE;

				bordGauche=true;
				bordHaut= (divisionGaucheHaut(pointsuivant.y)==pointsuivant.y);//=false sauf si alpha=alphalim
			}
			else if (Math.abs(alpha)>Math.abs(alphalim))
			{
				//on va en haut

				pointsuivant.x= (float) (x-(float)(y-divisionGaucheHaut(y)+ (bordHaut ? 100 : 0))/Math.tan(alpha)); // le signe est negatif
				pointsuivant.y=divisionGaucheHaut(y)- (bordGauche ? 100 : 0);
				ancienDirection= BAS;
				bordGauche= (divisionGaucheHaut(pointsuivant.x) == pointsuivant.x);//=false sauf si alpha=alphalim
				bordHaut=true;
			}
			else //alpha=alphalim
			{
				pointsuivant.x=divisionGaucheHaut(x)+100;
				pointsuivant.y=divisionGaucheHaut(y)- (bordHaut ? 100 : 0);
				ancienDirection= BASGAUCHE;
				bordGauche=true;
				bordHaut=true;//dans la direction haut gauche, cette valeur vaut vraie si le point est en bas du carré qu'on veut 
			}
		}
		else if(direction == DROITE)
		{
			pointsuivant.y=y;
			pointsuivant.x=divisionGaucheHaut(x)+100;
			ancienDirection= GAUCHE;
			//bordHaut ne change pas 
			bordGauche= true;
		}
		else if(direction == BASDROITE) //aucun cas particulier 
		{
			alphalim= (float) Math.atan( (float)((divisionGaucheHaut(y)+100)-y)/((divisionGaucheHaut(x)+100)-x)); // alpha >0
			if(Math.abs(alpha)<Math.abs(alphalim))
			{
				// on va a droite : 
				pointsuivant.y=(float) (y+(Math.tan(alpha)*(divisionGaucheHaut(x)+100-x))); 
				pointsuivant.x=divisionGaucheHaut(x)+100;
				ancienDirection= GAUCHE;
				bordGauche=true;
				bordHaut= (divisionGaucheHaut(pointsuivant.y)==pointsuivant.y);//=false sauf si alpha=alphalim

			}
			else if (Math.abs(alpha)>Math.abs(alphalim))
			{
				//on va en bas
				pointsuivant.x=(float) (x+((float)(divisionGaucheHaut(y)+100-y)/Math.tan(alpha))); 
				pointsuivant.y=divisionGaucheHaut(y)+100;
				ancienDirection= HAUT;
				bordGauche=(divisionGaucheHaut(pointsuivant.x)==pointsuivant.x);//=false sauf si alpha=alphalim
				bordHaut=true; 
			}
			else //alpha=alphalim
			{
				pointsuivant.x=divisionGaucheHaut(x)+100;
				pointsuivant.y=divisionGaucheHaut(y)+100;
				ancienDirection= HAUTGAUCHE;
				bordGauche=true;
				bordHaut= true;
			}
		}
		else if(direction == BAS)
		{
			pointsuivant.x=x;
			pointsuivant.y=divisionGaucheHaut(y)+100;
			ancienDirection= HAUT;
			//bordGauche reste inchangé 
			bordHaut= true;
		}
		else if(direction == BASGAUCHE)
		{
			//ici le cas limite est a=100 

			alphalim= (float) Math.atan( (float)(divisionGaucheHaut(y)+100 -y)/((divisionGaucheHaut(x)- (bordGauche ? 100 : 0))-x)); // alphalim est négatif aussi 
			if(Math.abs(alpha)<Math.abs(alphalim))
			{
				// on va a gauche 
				pointsuivant.y=(float) (y-Math.tan(alpha)*(x-divisionGaucheHaut(x)+ (bordGauche ? 100 : 0))); //tan alpha negatif donc on ajoute qqch de positif à y 
				pointsuivant.x=divisionGaucheHaut(x)- (bordGauche ? 100 : 0);
				ancienDirection= DROITE;
				bordGauche=true;
				bordHaut= (divisionGaucheHaut(pointsuivant.y)==pointsuivant.y);//=false sauf si alpha=alphalim
			}
			else if (Math.abs(alpha)>Math.abs(alphalim))
			{
				// on va en bas
				pointsuivant.x=(float) (x-(float)(y-(divisionGaucheHaut(y)+100))/Math.tan(alpha)); // le signe est positif donc on ajoute bien qqch de negatif a y
				pointsuivant.y=divisionGaucheHaut(y)+100;
				ancienDirection= HAUT;
				bordGauche=(divisionGaucheHaut(pointsuivant.x)==pointsuivant.x);//=false sauf si alpha=alphalim
				bordHaut=true; 
			}
			else //alpha=alphalim
			{
				pointsuivant.x=divisionGaucheHaut(x)- (bordGauche ? 100 : 0);
				pointsuivant.y=divisionGaucheHaut(y)+100;
				ancienDirection= HAUTDROITE;
				bordGauche=true;
				bordHaut= true;
			}

		}
		else if(direction ==GAUCHE)
		{
			pointsuivant.y=y; 
			pointsuivant.x=divisionGaucheHaut(x)- (bordGauche ? 100 : 0);
			ancienDirection= DROITE;
			//bordHaut ne change pas 
			bordGauche=true;
			bordHaut= (divisionGaucheHaut(pointsuivant.y)==pointsuivant.y);//=false sauf si alpha=alphalim
		}
		else if(direction == HAUTGAUCHE)
		{
			alphalim= (float) Math.atan( (float)(y-(divisionGaucheHaut(y)- (bordHaut ? 100 : 0)))/(x-(divisionGaucheHaut(x)- (bordGauche? 100 : 0)))); // alphalim est positif 
			if(Math.abs(alpha)<Math.abs(alphalim))
			{
				// on va a gauche 
				pointsuivant.y=(float) (y-Math.tan(alpha)*(x-divisionGaucheHaut(x)+ (bordGauche ? 100 : 0))); //tan alpha negatif donc on ajoute qqch de positif à y 
				pointsuivant.x=divisionGaucheHaut(x)- (bordGauche ? 100 : 0);
				ancienDirection= DROITE;
				bordGauche= true; 

			}
			else if (Math.abs(alpha)>Math.abs(alphalim))
			{
				//on va en haut

				pointsuivant.x=(float) (x-(float)(y-(divisionGaucheHaut(y)-(bordHaut ? 100 : 0)))/Math.tan(alpha)); // le signe est positif donc on soustrait bien qqch de positif à x 
				pointsuivant.y=divisionGaucheHaut(y)-(bordHaut ? 100 : 0);
				ancienDirection= BAS;
				bordHaut=true;
				bordGauche=(divisionGaucheHaut(pointsuivant.x)==pointsuivant.x);//=false sauf si alpha=alphalim
			}
			else //alpha=alphalim
			{
				pointsuivant.x=divisionGaucheHaut(x)- (bordGauche ? 100 : 0);
				pointsuivant.y=divisionGaucheHaut(y)-(bordHaut ? 100 : 0);
				ancienDirection= BASDROITE;
				bordHaut=true;
				bordGauche=true;
			}
		}
		else {throw new IllegalArgumentException("ERREUR Collision/pointSuivant: direction inconnue: "+direction);}

	}
	public int divisionGaucheHaut(float nombre)
	{
		int resultat =0;
		if(nombre >= 0 ||((int)nombre/100* 100 == nombre) )
		{
			resultat= ((int)nombre/100*100);
		}
		else
		{
			resultat=((int)nombre/100 * 100 -100) ;
		}

		return(resultat);
	}
	public boolean testPointSuivant(){//renvoie true si on a pas dépasser les limites
		if((int)Math.abs(pointsuivant.x-xori)>(int)(Math.abs(xlim)) || (int)Math.abs(pointsuivant.y-yori) > (int)(Math.abs(ylim)) )//on a depassé les limites 
			return(false);

		else 
			return(true);
	}

	//TODO
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
					//System.out.println("Hitbox in "+ (x + INIT_RECT.x)/TAILLE_BLOC + " "+(y + INIT_RECT.y)/TAILLE_BLOC );
					mondeBlocs.add(bloc);
				}
				//else 
					//System.out.println("No hitbox for bloc "+ (x + INIT_RECT.x)/TAILLE_BLOC + " "+(y + INIT_RECT.y)/TAILLE_BLOC );
				
			}

		return mondeBlocs;
	}
	/** @return false if object is stuck into environment */
	public boolean collisionGenerale(AbstractModelPartie partie,Deplace deplace, Collidable object)
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
			objectHitbox= Hitbox.minusPoint(object.getHitbox(partie.INIT_RECT),p);

			mondeBlocs = getMondeBlocs(partie.monde,objectHitbox, partie.INIT_RECT,
					partie.TAILLE_BLOC);
			System.out.println("\t\t test for " + objectHitbox.toString() );

			for(Bloc mondeBloc : mondeBlocs)
			{
				Hitbox mondeBox = mondeBloc.getHitbox(partie.INIT_RECT);
				//p = new Point( (mondeBloc.fixedWhenScreenMoves? 0 : deplaceEcran.x)+totalInterDist.x,
				//	(mondeBloc.fixedWhenScreenMoves? 0 : deplaceEcran.y)+totalInterDist.y);
				//mondeBox= Hitbox.minusPoint(mondeBox, p);
				
				Vector2d supp1 = GJK_EPA.support(mondeBox.polygon,minSpeed.vect2d() );//fixed one
				Vector2d supp2 = GJK_EPA.support(objectHitbox.polygon, speed.vect2d());//mobile one
				Vector2d firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);
				
				System.out.println("\t\tColli for " + objectHitbox.toString() +" // "+ mondeBloc.getHitbox(partie.INIT_RECT).toString());


				
				List<Vector2d> simplex = GJK_EPA.intersects(mondeBox.polygon,objectHitbox.polygon ,firstDir);
				

				List<Vector2d> normals = new ArrayList<Vector2d>();

				int collision_type= GJK_EPA.isIntersect(simplex);
				boolean inCollision =collision_type==GJK_EPA.INTER; //=0
				System.out.println("\t\t\t/"+ inCollision+":"+collision_type+"/ ");
				if(inCollision)
				{
					//Object is stuck in environment
					if(speed.x==0 && speed.y==0)
						return false;
					
					Double dInter= GJK_EPA.EPA(mondeBox.polygon, objectHitbox.polygon, simplex, minSpeed.vect2d(), normals);

					Vector2d vectOut = new Vector2d(-1*speed.x,-1*speed.y);
					//int x = (speed.x>0? -1 : 0);
					//int y = (speed.y>0? -1 : 0);
					
					vectOut.normalize();
					vectOut= new Vector2d(Math.round(vectOut.x*dInter),Math.round(vectOut.y*dInter));
					intersectedHitbox=mondeBox;
					if((Math.abs(maxInterDist.x)-Math.abs((int)vectOut.x))<0 || 
							((Math.abs(maxInterDist.y)-Math.abs((int)vectOut.y))<0 ))
					{
						if(normals.size()>0)
						{
							EPA_normal=normals.get(0);
							System.out.println("\t\t\t\tEPA_NORMAL " +EPA_normal.x +" "+ EPA_normal.y );
						}
						maxInterDist.x =(int)vectOut.x;
						maxInterDist.y =(int)vectOut.y;
						System.out.println("\t\t\t\tinter dist " + maxInterDist.x+" "+ maxInterDist.y);
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

		//on calcul la direction de collision 
		if(intersectedHitbox!=null)
		{
			//on appelle la fonction qui gère les collisions en fonction de la normale
			object.handleCollision(EPA_normal,partie,deplace);
		}
		return true;
	}
	//}}
	//fonctions pour heros 
	//{{
	public void collision(AbstractModelPartie partie,Deplace deplace, Heros heros)
	{
		boolean slowDown = partie.slowDown;
		int slowDownFactor = heros.slowDownFactor;
		int xdeplaceEcran = partie.xdeplaceEcran;
		int ydeplaceEcran = partie.ydeplaceEcran;
		int xdeplaceEcranBloc = partie.xdeplaceEcranBloc;
		int ydeplaceEcranBloc = partie.ydeplaceEcranBloc;

		if(slowDown)
		{
			xDeplacement=(int) (heros.vit.x*T/1000/slowDownFactor);
			yDeplacement=(int) (heros.vit.y*T/1000/slowDownFactor);
		}
		else
		{
			xDeplacement=(int) (heros.vit.x*T/1000);
			yDeplacement=(int) (heros.vit.y*T/1000);
		}

		float alpha;
		if(xDeplacement == 0 && yDeplacement>=0)
		{
			alpha=(float) (Math.PI/2);
		}
		else if(xDeplacement == 0 && yDeplacement<0)
		{
			alpha=(float) (-1*Math.PI/2);
		}
		else {
			alpha=(float) Math.atan((double)yDeplacement / xDeplacement);
		} 
		float alphalim=-1;


		bordGauche=false; //Ne pas les initialiser à leur vraie valeur permet de tester le point la ou est le joueur
		bordHaut = false;

		testFinSaut=partie.finSaut;
		testPeutSauter= partie.peutSauter;
		testGlisse= false;
		//variables permetant de savoir jusqu'a ou on doit explorer
		//si il y a collision, cette variable prend la valeur de la ou il y a eu collision 
		xlim=xDeplacement;
		ylim=yDeplacement;
		//position initiale du point de la hitbox 
		xori= -1;
		yori= -1;
		//variable de parcours: reperer le personnage a chaque moment si il bouge
		x = heros.xpos + heros.deplacement.xdecallsprite.get(heros.anim)-xdeplaceEcran-xdeplaceEcranBloc;
		y= heros.ypos+ heros.deplacement.ydecallsprite.get(heros.anim)-ydeplaceEcran-ydeplaceEcranBloc;
		pointsuivant = new Point_float(-1,-1);
		//absuivant= new Points(-1,-1);
		//booleen pour savoir si on a fini d'étudier un point 
		stop = false;
		//connaitre la direction ou on va :
		choixDirection(heros);

		i=initI();
		while(i<3)
		{
			//initialisation des valeurs :
			stop =false;
			bordGauche=false; 
			bordHaut = false;
			testFinSaut=partie.finSaut;
			testPeutSauter= partie.peutSauter;
			testGlisse=false;
			x = heros.xpos + heros.deplacement.xdecallsprite.get(heros.anim)-xdeplaceEcran-xdeplaceEcranBloc;
			y= heros.ypos+ heros.deplacement.ydecallsprite.get(heros.anim)-ydeplaceEcran-ydeplaceEcranBloc;
			choixDirection(heros);

			stop=testBlocage(partie,heros);
			if(stop)
			{
				//le heros est bloqué sur place

				partie.finSaut=testFinSaut;
				partie.peutSauter=testPeutSauter;
				deplace.glisse=testGlisse;

				xlim=0;
				ylim=0;
			}
			while(stop == false)
			{
				//on calcule le nouveau point , on vérifie si on est allé trop loin 
				//si on est allé trop loin , on ajoute xlim a la position, xlim ne doit pas changer, on arrete la boucle
				//si on est pas allé trop loin on test si le point calculé est dans un carré bloquant
				//si il est bloquant, on change x,y,xlim, on arrete la boucle, on ajoute xlim a la position 
				//sinon , on modifie x,y et on continue

				//on calcul le point suivant
				pointSuivant(alphalim,alpha);
				if(testPointSuivant())//on a pas depasse les limites 
				{
					x=pointsuivant.x;
					y=pointsuivant.y;
					stop=testBlocage(partie,heros);//modifie,x,y,la vitesse du heros,le fait qu'il peut sauter
					//xlim et ylim changent que si on est bloqué 
					if(stop)
					{
						partie.finSaut=testFinSaut;
						partie.peutSauter=testPeutSauter;
						deplace.glisse=testGlisse;

						xlim=(int) Math.min(Math.abs(x-xori),Math.abs(xlim))* (xlim>0 ? 1 : -1);
						ylim=(int) Math.min(Math.abs(y-yori),Math.abs(ylim))* (ylim>0 ? 1 : -1);
					}
					else{}
				}
				else // si on a depasse les limites 
				{ 
					//x et y ne changent pas
					stop = true;
					//xlim et ylim ne changent pas 

				}
			}
			//fin boucle while i 
			i++;

		}
		//il reste à déplacer le heros, on utilise les variables x lim et y lim qui ont stocké les positions 
		heros.xpos=heros.xpos+ xlim;
		heros.ypos=heros.ypos+ ylim;
	}

	public boolean testBlocage( AbstractModelPartie partie, Heros heros)
	{
		Monde monde = partie.monde;
		Point INIT_RECT = partie.INIT_RECT;

		if(ancienDirection== BASDROITE) // on vient d'en bas à droite 
		{
			if(monde.niveau[(int) ((x-1)+INIT_RECT.x)/100][(int) ((y-1)+INIT_RECT.y)/100].getBloquer()==true)
			{
				testGlisse=true;
				// x= x;
				// y= y;
				heros.vit.x=0;
				heros.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== HAUTDROITE )// en vient d'en haut à droite 
		{

			if(monde.niveau[(int) ((x-1)+INIT_RECT.x)/100][(int) (y+INIT_RECT.y)/100].getBloquer()==true)
			{
				testGlisse=true;
				//x=x 
				y=y-1;
				if(!partie.peutSauter)
				{ 
					testFinSaut=true;
					testPeutSauter=true;
				}
				//on arrete la vitesse que si on est pas allé trop loin
				heros.vit.x=0;
				heros.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== BASGAUCHE)// basgauche
		{
			if(monde.niveau[(int) (x+INIT_RECT.x)/100][(int) ((y-1)+INIT_RECT.y)/100].getBloquer()==true)
			{
				testGlisse=true;
				x= (x-1);
				//y=y 
				//on arrete la vitesse que si on est pas allé trop loin
				heros.vit.x=0;
				heros.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== HAUTGAUCHE)// on vient d'en haut à gauche 
		{
			if(monde.niveau[(int) (x+INIT_RECT.x)/100][(int) (y+INIT_RECT.y)/100].getBloquer()==true)
			{
				testGlisse=true;
				x= (x-1);
				y=(y-1);
				if(!partie.peutSauter){ 
					testFinSaut=true;
					testPeutSauter=true;
				}
				//on arrete la vitesse que si on est pas allé trop loin
				heros.vit.x=0;
				heros.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		//on est sur la ligne en bas du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(ancienDirection== HAUT) // on vient d'en haut 
		{
			if(monde.niveau[(int) (x+INIT_RECT.x)/100][(int) (y+INIT_RECT.y)/100].getBloquer()==true)
			{
				//x=x
				y=y-1;
				if(!partie.peutSauter){ 
					testFinSaut=true;
					testPeutSauter=true;
				}
				//on arrete la vitesse que si on est pas allé trop loin
				heros.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== BAS)// on vient d'en bas
		{
			if(monde.niveau[(int) (x+INIT_RECT.x)/100][(int) ((y-1)+INIT_RECT.y)/100].getBloquer()==true)
			{
				//x= x;
				//y=y
				//on arrete la vitesse que si on est pas allé trop loin
				heros.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		//on est sur la ligne à droite du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(ancienDirection== DROITE)// on vient de droite 
		{

			if(monde.niveau[(int) ((x-1)+INIT_RECT.x)/100][(int) (y+INIT_RECT.y)/100].getBloquer()==true)
			{
				testGlisse=true;
				//x=x;
				//y= y;
				//on arrete la vitesse que si on est pas allé trop loin
				heros.vit.x=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== GAUCHE)// on vient de gauche 
		{
			if(monde.niveau[(int) (x+INIT_RECT.x)/100][(int) (y+INIT_RECT.y)/100].getBloquer()==true)
			{
				testGlisse=true;
				x= (x-1);
				//y = y
				//on arrete la vitesse que si on est pas allé trop loin
				heros.vit.x=0;
				return(true);
			}
			else {return(false);}
		}

		else // tous les cas ont été traité, c'est donc un erreur 
		{
			return(true);

		}

	}

	public void choixDirection(Heros heros)
	{
		//initalisation des points de départ 
		if(xDeplacement==0 && yDeplacement==0)
		{
			// haut ou haut droite 
			direction =AUCUNE;
			ancienDirection=AUCUNE;
			//aucun i 
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement==0 && yDeplacement<0)
		{
			//i va de 1 à 2 
			direction =HAUT;
			ancienDirection=BAS;
			x =x +(i==1 ? 0 : heros.deplacement.xhitbox.get(heros.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement<0)
		{
			//i va de 0 a 2
			direction =HAUTDROITE;
			ancienDirection=BASGAUCHE;
			x =x + (i==0 ? 0 : heros.deplacement.xhitbox.get(heros.anim));
			y= y + (i<2 ? 0 : heros.deplacement.yhitbox.get(heros.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement==0)
		{
			//i va de 1 a 2
			direction =DROITE;
			ancienDirection=GAUCHE;
			x =x + heros.deplacement.xhitbox.get(heros.anim);
			y= y + (i==1 ? 0 : heros.deplacement.yhitbox.get(heros.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement>0)
		{
			// i va de 0 a 2
			direction = BASDROITE;
			ancienDirection=HAUTGAUCHE;
			x = x+ (i==0 ? 0 : heros.deplacement.xhitbox.get(heros.anim));
			y= y + (i==2 ? 0 : heros.deplacement.yhitbox.get(heros.anim)); 
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;

		}
		else if(xDeplacement==0 && yDeplacement>0)
		{ 
			// i va de 1 a 2
			direction =BAS;
			ancienDirection=HAUT;
			x =x + (i==1 ? 0 : heros.deplacement.xhitbox.get(heros.anim));
			y= y + heros.deplacement.yhitbox.get(heros.anim);
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement>0)
		{
			// i va de 0 a 2
			direction = BASGAUCHE;
			ancienDirection=HAUTDROITE;
			x = x + (i<=1 ? 0 : heros.deplacement.xhitbox.get(heros.anim));
			y= y + (i==0 ? 0 : heros.deplacement.yhitbox.get(heros.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement==0)
		{
			// i va de 1 a 2
			direction =GAUCHE;
			ancienDirection=DROITE;
			y= y + (i==1 ? 0 : heros.deplacement.yhitbox.get(heros.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement<0)
		{
			// i va de 0 a 2
			direction = HAUTGAUCHE;
			ancienDirection=BASDROITE;
			x = x + (i<=1 ? 0 : heros.deplacement.xhitbox.get(heros.anim));
			y= y + (i>=1 ? 0 : heros.deplacement.yhitbox.get(heros.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else 
		{
			throw new IllegalArgumentException("ERREUR Collision/collision: cas vitesse non géré donc probleme direction ");
		}
	}

	//}}
	//fonctions pour fleches 
	//{{

	public void collision(AbstractModelPartie partie, Fleche fleche)
	{

		xHG =fleche.xpos+ fleche.deplacement.xHdecallsprite.get(fleche.anim);
		xHD =fleche.xpos + fleche.deplacement.xHdecallsprite.get(fleche.anim) + fleche.deplacement.xHdecall2.get(fleche.anim);
		xBG =fleche.xpos + fleche.deplacement.xBdecallsprite.get(fleche.anim);
		xBD= fleche.xpos + fleche.deplacement.xBdecallsprite.get(fleche.anim) + fleche.deplacement.xBdecall2.get(fleche.anim);
		yHG =fleche.ypos + fleche.deplacement.yHdecallsprite.get(fleche.anim);
		yHD=fleche.ypos + fleche.deplacement.yHdecallsprite.get(fleche.anim) + fleche.deplacement.yHdecall2.get(fleche.anim);
		yBG =fleche.ypos + fleche.deplacement.yBdecallsprite.get(fleche.anim);
		yBD=fleche.ypos + fleche.deplacement.yBdecallsprite.get(fleche.anim) + fleche.deplacement.yBdecall2.get(fleche.anim);

		if(partie.slowDown)
		{
			xDeplacement=(int) (fleche.vit.x*T/1000/partie.slowDownTir); 
			yDeplacement=(int) (fleche.vit.y*T/1000/partie.slowDownTir);
		}
		else 
		{
			xDeplacement=(int) (fleche.vit.x*T/1000); 
			yDeplacement=(int) (fleche.vit.y*T/1000);
		}

		float alpha;
		if(xDeplacement == 0 && yDeplacement>=0)
		{
			alpha=(float) (Math.PI/2);
		}
		else if(xDeplacement == 0 && yDeplacement<0)
		{
			alpha=(float) (-1*Math.PI/2);
		}
		else {
			alpha=(float) Math.atan((double)yDeplacement / xDeplacement);
		} 
		float alphalim=-1;


		bordGauche=false; //Ne pas les initialiser à leur vraie valeur permet de tester le point la ou est le joueur
		bordHaut = false;

		//variables permetant de savoir jusqu'a ou on doit explorer
		//si il y a collision, cette variable prend la valeur de la ou il y a eu collision 
		xlim=xDeplacement;
		ylim=yDeplacement;
		//position initiale du point de la hitbox 
		xori= -1;
		yori= -1;
		//variable de parcours: reperer l'objet a chaque moment si il bouge


		x = xHG ;
		y = yHG ;
		pointsuivant = new Point_float(-1,-1);
		//absuivant= new Points(-1,-1);
		//booleen pour savoir si on a fini d'étudier un point 
		stop = false;

		//connaitre la direction ou on va :
		choixDirection(fleche);
		i=initI();
		while(i<3)
		{
			//initialisation des valeurs :
			stop =false;
			bordGauche=false; 
			bordHaut = false;


			choixDirection(fleche);

			//on test si la fleche est deja bloquée 
			stop=testBlocage(partie,fleche);
			//si c'est le cas la fleche ne doit pas bouger
			if (stop)
			{
				xlim=0;
				ylim=0;
				fleche.doitDeplace=false;
				fleche.doitDetruire=true;
				//fleche.timer();
			}
			while(stop == false)
			{
				//on calcule le nouveau point , on vérifie si on est allé trop loin 
				//si on est allé trop loin , on ajoute xlim a la position, xlim ne doit pas changer, on arrete la boucle
				//si on est pas allé trop loin on test si le point calculé est dans un carré bloquant
				//si il est bloquant, on change x,y,xlim, on arrete la boucle, on ajoute xlim a la position 
				//sinon , on modifie x,y et on continue

				//on calcul le point suivant
				pointSuivant(alphalim,alpha);
				if(testPointSuivant())//on a pas depasse les limites 
				{
					x=pointsuivant.x;
					y=pointsuivant.y;
					stop=testBlocage(partie,fleche);//modifie,x,y,la vitesse du heros,le fait qu'il peut sauter
					//xlim et ylim changent que si on est bloqué 
					if(stop)
					{

						xlim=(int) Math.min(Math.abs(x-xori),Math.abs(xlim))* (xlim>0 ? 1 : -1);
						ylim=(int) Math.min(Math.abs(y-yori),Math.abs(ylim))* (ylim>0 ? 1 : -1);
						fleche.doitDeplace=false;
						fleche.doitDetruire=true;
						//fleche.timer();
					}
					else{}
				}
				else // si on a depasse les limites 
				{ 
					//x et y ne changent pas
					stop = true;
					//xlim et ylim ne changent pas 

				}
			}
			//fin boucle while i 
			i++;

		}
		//il reste à déplacer le heros, on utilise les variables x lim et y lim qui ont stocké les positions 
		fleche.xpos=fleche.xpos+ xlim;
		fleche.ypos=fleche.ypos+ ylim;
	}

	public boolean testBlocage( AbstractModelPartie partie, Fleche fleche )
	{
		Monde monde = partie.monde;
		int INIT_ABS_RECT = partie.INIT_RECT.x;
		int INIT_ORD_RECT = partie.INIT_RECT.y;

		if(ancienDirection== BASDROITE) // on vient d'en bas à droite 
		{
			if(monde.niveau[(int) ((x-1)+INIT_ABS_RECT)/100][(int) ((y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				// x= x;
				// y= y;
				fleche.vit.x=0;
				fleche.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== HAUTDROITE )// en vient d'en haut à droite 
		{

			if(monde.niveau[(int) ((x-1)+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				//x=x 
				y=y-1;
				//on arrete la vitesse que si on est pas allé trop loin
				fleche.vit.x=0;
				fleche.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== BASGAUCHE)// basgauche
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) ((y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				x= (x-1);
				//y=y 
				//on arrete la vitesse que si on est pas allé trop loin
				fleche.vit.x=0;
				fleche.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== HAUTGAUCHE)// on vient d'en haut à gauche 
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				x= (x-1);
				y=(y-1);

				//on arrete la vitesse que si on est pas allé trop loin
				fleche.vit.x=0;
				fleche.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		//on est sur la ligne en bas du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(ancienDirection== HAUT) // on vient d'en haut 
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				//x=x
				y=y-1;
				//on arrete la vitesse que si on est pas allé trop loin
				fleche.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== BAS)// on vient d'en bas
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) ((y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				//x= x;
				//y=y
				//on arrete la vitesse que si on est pas allé trop loin
				fleche.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		//on est sur la ligne à droite du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(ancienDirection== DROITE)// on vient de droite 
		{

			if(monde.niveau[(int) ((x-1)+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				//x=x;
				//y= y;
				//on arrete la vitesse que si on est pas allé trop loin
				fleche.vit.x=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== GAUCHE)// on vient de gauche 
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				x= (x-1);
				//y = y
				//on arrete la vitesse que si on est pas allé trop loin
				fleche.vit.x=0;
				return(true);
			}
			else {return(false);}
		}

		else // tous les cas ont été traité, c'est donc un erreur 
		{
			return(true);

		}

	}


	public void choixDirection(Fleche fleche)
	{
		//initalisation des points de départ 
		if(xDeplacement==0 && yDeplacement==0)
		{
			direction =AUCUNE;
			ancienDirection=AUCUNE;
			//aucun i 
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement==0 && yDeplacement<0)
		{
			//i va de 1 à 2 
			direction =HAUT;
			ancienDirection=BAS;
			switch(i)
			{
			case 1: x=xHG; y=yHG; break;
			case 2: x=xHD; y=yHD; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement<0)
		{
			//i va de 0 a 2
			direction =HAUTDROITE;
			ancienDirection=BASGAUCHE;
			switch(i)
			{
			case 0: x=xHG; y=yHG; break;
			case 1: x=xHD; y=yHD; break;
			case 2: x=xBD; y=yBD; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement==0)
		{
			//i va de 1 a 2
			direction =DROITE;
			ancienDirection=GAUCHE;
			switch(i)
			{
			case 1: x=xHD; y=yHD; break;
			case 2: x=xBD; y=yBD; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement>0)
		{
			// i va de 0 a 2
			direction = BASDROITE;
			ancienDirection=HAUTGAUCHE;
			switch(i)
			{
			case 0: x=xHD; y=yHD; break;
			case 1: x=xBD; y=yBD; break;
			case 2: x=xBG; y=yBG; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;

		}
		else if(xDeplacement==0 && yDeplacement>0)
		{ 
			// i va de 1 a 2
			direction =BAS;
			ancienDirection=HAUT;
			switch(i)
			{
			case 1: x=xBG; y=yBG; break;
			case 2: x=xBD; y=yBD; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement>0)
		{
			// i va de 0 a 2
			direction = BASGAUCHE;
			ancienDirection= HAUTDROITE;
			switch(i)
			{
			case 0: x=xHG; y=yHG; break;
			case 1: x=xBG; y=yBG; break;
			case 2: x=xBD; y=yBD; break; 
			}

			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement==0)
		{
			// i va de 1 a 2
			direction =GAUCHE;
			ancienDirection= DROITE;
			switch(i)
			{
			case 1: x=xHG; y=yHG; break;
			case 2: x=xBG; y=yBG; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement<0)
		{
			// i va de 0 a 2
			direction = HAUTGAUCHE;
			ancienDirection=BASDROITE;
			switch(i)
			{
			case 0: x=xBG; y=yBG; break;
			case 1: x=xHG; y=yHG; break;
			case 2: x=xHD; y=yHD; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else 
		{
			throw new IllegalArgumentException("ERREUR Collision/collision: cas vitesse non géré donc probleme direction ");
		}
	}

	//}}
	//fonctions pour monstre
	//{{

	public void collision(AbstractModelPartie partie, Monstre monstre)
	{
		// x=pos + decall sprite+ ecran +ecran bloc
		if(partie.slowDown)
		{
			xDeplacement=(int) (monstre.vit.x*T/1000/monstre.slowDownFactor);
			yDeplacement=(int) (monstre.vit.y*T/1000/monstre.slowDownFactor);
		}
		else 
		{
			xDeplacement=(int) (monstre.vit.x*T/1000); 
			yDeplacement=(int) (monstre.vit.y*T/1000);
		}

		float alpha;
		if(xDeplacement == 0 && yDeplacement>=0)
		{
			alpha=(float) (Math.PI/2);
		}
		else if(xDeplacement == 0 && yDeplacement<0)
		{
			alpha=(float) (-1*Math.PI/2);
		}
		else {
			alpha=(float) Math.atan((double)yDeplacement / xDeplacement);
		} 
		float alphalim=-1;


		bordGauche=false; //Ne pas les initialiser à leur vraie valeur permet de tester le point la ou est le joueur
		bordHaut = false;

		//variables permetant de savoir jusqu'a ou on doit explorer
		//si il y a collision, cette variable prend la valeur de la ou il y a eu collision 
		xlim=xDeplacement;
		ylim=yDeplacement;
		//position initiale du point de la hitbox 
		xori= -1;
		yori= -1;
		//variable de parcours: reperer l'objet a chaque moment si il bouge

		x = monstre.xPos+monstre.deplacement.xdecallsprite.get(monstre.anim);
		y = monstre.yPos+monstre.deplacement.ydecallsprite.get(monstre.anim);

		pointsuivant = new Point_float(-1,-1);
		//absuivant= new Points(-1,-1);
		//booleen pour savoir si on a fini d'étudier un point 
		stop = false;

		//connaitre la direction ou on va :
		choixDirection(monstre);
		i=initI();
		while(i<3)
		{
			//initialisation des valeurs :
			stop =false;
			bordGauche=false; 
			bordHaut = false;
			testMFinSaut=monstre.finSaut;
			testMPeutSauter= monstre.peutSauter;

			x = monstre.xPos+monstre.deplacement.xdecallsprite.get(monstre.anim);
			y = monstre.yPos+monstre.deplacement.ydecallsprite.get(monstre.anim);

			choixDirection(monstre);

			//on test si la fleche est deja bloquée 
			stop=testBlocage(partie,monstre,x,y,ancienDirection);
			//si c'est le cas la fleche ne doit pas bouger
			if (stop)
			{
				xlim=0;
				ylim=0;
				monstre.finSaut=testMFinSaut;
				monstre.peutSauter=testMPeutSauter;
				monstre.glisse=testMGlisse;
			}
			while(stop == false)
			{
				//on calcule le nouveau point , on vérifie si on est allé trop loin 
				//si on est allé trop loin , on ajoute xlim a la position, xlim ne doit pas changer, on arrete la boucle
				//si on est pas allé trop loin on test si le point calculé est dans un carré bloquant
				//si il est bloquant, on change x,y,xlim, on arrete la boucle, on ajoute xlim a la position 
				//sinon , on modifie x,y et on continue

				//on calcul le point suivant
				pointSuivant(alphalim,alpha);
				if(testPointSuivant())//on a pas depasse les limites 
				{
					x=pointsuivant.x;
					y=pointsuivant.y;
					stop=testBlocage(partie,monstre,x,y,ancienDirection);//modifie,x,y,la vitesse du heros,le fait qu'il peut sauter
					//xlim et ylim changent que si on est bloqué 
					if(stop)
					{

						xlim=(int) Math.min(Math.abs(x-xori),Math.abs(xlim))* (xlim>0 ? 1 : -1);
						ylim=(int) Math.min(Math.abs(y-yori),Math.abs(ylim))* (ylim>0 ? 1 : -1);

						monstre.finSaut=testMFinSaut;
						monstre.peutSauter=testMPeutSauter;
						monstre.glisse=testMGlisse;


					}
					else{}
				}
				else // si on a depasse les limites 
				{ 
					//x et y ne changent pas
					stop = true;
					//xlim et ylim ne changent pas 

				}
			}
			//fin boucle while i 
			i++;

		}
		//il reste à déplacer le heros, on utilise les variables x lim et y lim qui ont stocké les positions 
		monstre.xPos=monstre.xPos+ xlim;
		monstre.yPos=monstre.yPos+ ylim;

	}

	public static boolean isBloque(AbstractModelPartie partie, Monstre monstre, float _x, float _y, int _ancienDirection)
	{
		Monde monde = partie.monde;
		int INIT_ABS_RECT = partie.INIT_RECT.x;
		int INIT_ORD_RECT = partie.INIT_RECT.y;


		if(_ancienDirection== BASDROITE) // on vient d'en bas à droite 
			if(monde.niveau[(int) ((_x-1)+INIT_ABS_RECT)/100][(int) ((_y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
				return(true);
			else 
				return false;

		else if(_ancienDirection== HAUTDROITE )// en vient d'en haut à droite 
			if(monde.niveau[(int) ((_x-1)+INIT_ABS_RECT)/100][(int) (_y+INIT_ORD_RECT)/100].getBloquer()==true)
				return(true);
			else 
				return false;

		else if(_ancienDirection== BASGAUCHE)// basgauche
			if(monde.niveau[(int) (_x+INIT_ABS_RECT)/100][(int) ((_y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
				return(true);
			else 
				return false;

		else if(_ancienDirection== HAUTGAUCHE)// on vient d'en haut à gauche 
			if(monde.niveau[(int) (_x+INIT_ABS_RECT)/100][(int) (_y+INIT_ORD_RECT)/100].getBloquer()==true)
				return(true);
			else 
				return false;

		//on est sur la ligne en bas du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(_ancienDirection== HAUT) // on vient d'en haut 
			if(monde.niveau[(int) (_x+INIT_ABS_RECT)/100][(int) (_y+INIT_ORD_RECT)/100].getBloquer()==true)
				return(true);
			else 
				return false;

		else if(_ancienDirection== BAS)// on vient d'en bas
			if(monde.niveau[(int) (_x+INIT_ABS_RECT)/100][(int) ((_y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
				return(true);
			else 
				return false;

		//on est sur la ligne à droite du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(_ancienDirection== DROITE)// on vient de droite 
			if(monde.niveau[(int) ((_x-1)+INIT_ABS_RECT)/100][(int) (_y+INIT_ORD_RECT)/100].getBloquer()==true)
				return(true);
			else 
				return false;
		else if(_ancienDirection== GAUCHE)// on vient de gauche 
			if(monde.niveau[(int) (_x+INIT_ABS_RECT)/100][(int) (_y+INIT_ORD_RECT)/100].getBloquer()==true)
				return(true);
			else 
				return false;

		else // tous les cas ont été traité, c'est donc un erreur 
			throw new IllegalArgumentException("testBlocage: ancienne direction inconnue");

	}

	public boolean testBlocage( AbstractModelPartie partie, Monstre monstre, float _x, float _y, int _ancienDirection)
	{
		boolean isBloque = isBloque(partie,monstre,_x,_y,_ancienDirection) ;
		if(_ancienDirection== BASDROITE && isBloque) // on vient d'en bas à droite 
		{
			testMGlisse=true;
			monstre.vit.x=0;
			monstre.vit.y=0;
		}
		else if(_ancienDirection== HAUTDROITE&& isBloque )// en vient d'en haut à droite 
		{
			//x=x 
			testGlisse=true;
			y=y-1;
			if(!monstre.peutSauter)
			{ 
				testMFinSaut=true;
				testMPeutSauter=true;
			}
			//on arrete la vitesse que si on est pas allé trop loin
			monstre.vit.x=0;
			monstre.vit.y=0;
		}
		else if(_ancienDirection== BASGAUCHE&& isBloque)// basgauche
		{

			testMGlisse=true;
			x= (x-1);
			//y=y 
			//on arrete la vitesse que si on est pas allé trop loin
			monstre.vit.x=0;
			monstre.vit.y=0;
		}
		else if(_ancienDirection== HAUTGAUCHE&& isBloque)// on vient d'en haut à gauche 
		{
			testMGlisse=true;
			if(!monstre.peutSauter){ 
				testMFinSaut=true;
				testMPeutSauter=true;
			}
			x= (x-1);
			y=(y-1);

			//on arrete la vitesse que si on est pas allé trop loin
			monstre.vit.x=0;
			monstre.vit.y=0;
		}
		//on est sur la ligne en bas du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(_ancienDirection== HAUT&& isBloque) // on vient d'en haut 
		{
			y=y-1;
			if(!monstre.peutSauter){ 
				testMFinSaut=true;
				testMPeutSauter=true;
			}
			//on arrete la vitesse que si on est pas allé trop loin
			monstre.vit.y=0;
		}
		else if(_ancienDirection== BAS&& isBloque)// on vient d'en bas
		{
			//x= x;
			//y=y
			//on arrete la vitesse que si on est pas allé trop loin
			monstre.vit.y=0;
		}
		//on est sur la ligne à droite du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(_ancienDirection== DROITE&& isBloque)// on vient de droite 
		{
			//x=x;
			//y= y;
			testMGlisse=true;
			//on arrete la vitesse que si on est pas allé trop loin
			monstre.vit.x=0;
		}
		else if(_ancienDirection== GAUCHE&& isBloque)// on vient de gauche 
		{
			x= (x-1);
			//y = y
			testMGlisse=true;
			//on arrete la vitesse que si on est pas allé trop loin
			monstre.vit.x=0;
		}

		else if(isBloque)// tous les cas ont été traité, c'est donc un erreur 
			throw new IllegalArgumentException("testBlocage: ancienne direction inconnue " + _ancienDirection);

		return(isBloque);
	}


	public void choixDirection(Monstre monstre)
	{//initalisation des points de départ 
		if(xDeplacement==0 && yDeplacement==0)
		{
			// haut ou haut droite 
			direction =AUCUNE;
			ancienDirection=AUCUNE;
			//aucun i 
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement==0 && yDeplacement<0)
		{
			//i va de 1 à 2 
			direction =HAUT;
			ancienDirection=BAS;
			x =x +(i==1 ? 0 : monstre.deplacement.xhitbox.get(monstre.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement<0)
		{
			//i va de 0 a 2
			direction =HAUTDROITE;
			ancienDirection=BASGAUCHE;
			x =x + (i==0 ? 0 : monstre.deplacement.xhitbox.get(monstre.anim));
			y= y + (i<2 ? 0 : monstre.deplacement.yhitbox.get(monstre.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement==0)
		{
			//i va de 1 a 2
			direction =DROITE;
			ancienDirection=GAUCHE;
			x =x + monstre.deplacement.xhitbox.get(monstre.anim);
			y= y + (i==1 ? 0 : monstre.deplacement.yhitbox.get(monstre.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement>0)
		{
			// i va de 0 a 2
			direction = BASDROITE;
			ancienDirection=HAUTGAUCHE;
			x = x+ (i==0 ? 0 : monstre.deplacement.xhitbox.get(monstre.anim));
			y= y + (i==2 ? 0 : monstre.deplacement.yhitbox.get(monstre.anim)); 
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;

		}
		else if(xDeplacement==0 && yDeplacement>0)
		{ 
			// i va de 1 a 2
			direction =BAS;
			ancienDirection=HAUT;
			x =x + (i==1 ? 0 : monstre.deplacement.xhitbox.get(monstre.anim));
			y= y + monstre.deplacement.yhitbox.get(monstre.anim);
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement>0)
		{
			// i va de 0 a 2
			direction = BASGAUCHE;
			ancienDirection=HAUTDROITE;
			x = x + (i<=1 ? 0 : monstre.deplacement.xhitbox.get(monstre.anim));
			y= y + (i==0 ? 0 : monstre.deplacement.yhitbox.get(monstre.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement==0)
		{
			// i va de 1 a 2
			direction =GAUCHE;
			ancienDirection=DROITE;
			y= y + (i==1 ? 0 : monstre.deplacement.yhitbox.get(monstre.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement<0)
		{
			// i va de 0 a 2
			direction = HAUTGAUCHE;
			ancienDirection=BASDROITE;
			x = x + (i<=1 ? 0 : monstre.deplacement.xhitbox.get(monstre.anim));
			y= y + (i>=1 ? 0 : monstre.deplacement.yhitbox.get(monstre.anim));
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else 
		{
			throw new IllegalArgumentException("ERREUR Collision/collision: cas vitesse non géré donc probleme direction ");
		}
	}
	//}}
	//fonctions pour TirMonstre
	//{{

	public void collision(AbstractModelPartie partie, TirMonstre tir)
	{
		xTMG= tir.xpos+ tir.xdecallsprite.get(tir.anim);
		xTMD=xTMG+ tir.xhitbox.get(tir.anim);
		yTMH= tir.ypos+ tir.ydecallsprite.get(tir.anim);
		yTMB=yTMH+tir.yhitbox.get(tir.anim);
		if(partie.slowDown)
		{
			xDeplacement=(int) (tir.vit.x*T/1000/partie.slowDownTir); 
			yDeplacement=(int) (tir.vit.y*T/1000/partie.slowDownTir);
		}
		else 
		{
			xDeplacement=(int) (tir.vit.x*T/1000); 
			yDeplacement=(int) (tir.vit.y*T/1000);
		}

		float alpha;
		if(xDeplacement == 0 && yDeplacement>=0)
		{
			alpha=(float) (Math.PI/2);
		}
		else if(xDeplacement == 0 && yDeplacement<0)
		{
			alpha=(float) (-1*Math.PI/2);
		}
		else {
			alpha=(float) Math.atan((double)yDeplacement / xDeplacement);
		} 
		float alphalim=-1;


		bordGauche=false; //Ne pas les initialiser à leur vraie valeur permet de tester le point la ou est le joueur
		bordHaut = false;

		//variables permetant de savoir jusqu'a ou on doit explorer
		//si il y a collision, cette variable prend la valeur de la ou il y a eu collision 
		xlim=xDeplacement;
		ylim=yDeplacement;
		//position initiale du point de la hitbox 
		xori= -1;
		yori= -1;
		//variable de parcours: reperer l'objet a chaque moment si il bouge


		x = xTMG ;
		y = yTMH ;
		pointsuivant = new Point_float(-1,-1);
		//absuivant= new Points(-1,-1);
		//booleen pour savoir si on a fini d'étudier un point 
		stop = false;

		//connaitre la direction ou on va :
		choixDirection(tir);
		i=initI();
		while(i<3)
		{
			//initialisation des valeurs :
			stop =false;
			bordGauche=false; 
			bordHaut = false;

			choixDirection(tir);

			//on test si la fleche est deja bloquée 
			stop=testBlocage(partie,tir);
			//si c'est le cas la fleche ne doit pas bouger
			if (stop)
			{
				xlim=0;
				ylim=0;
				tir.doitDetruire=true;
			}
			while(stop == false)
			{
				//on calcule le nouveau point , on vérifie si on est allé trop loin 
				//si on est allé trop loin , on ajoute xlim a la position, xlim ne doit pas changer, on arrete la boucle
				//si on est pas allé trop loin on test si le point calculé est dans un carré bloquant
				//si il est bloquant, on change x,y,xlim, on arrete la boucle, on ajoute xlim a la position 
				//sinon , on modifie x,y et on continue

				//on calcul le point suivant
				pointSuivant(alphalim,alpha);
				if(testPointSuivant())//on a pas depasse les limites 
				{
					x=pointsuivant.x;
					y=pointsuivant.y;
					stop=testBlocage(partie,tir);//modifie,x,y,la vitesse du heros,le fait qu'il peut sauter
					//xlim et ylim changent que si on est bloqué 
					if(stop)
					{

						xlim=(int) Math.min(Math.abs(x-xori),Math.abs(xlim))* (xlim>0 ? 1 : -1);
						ylim=(int) Math.min(Math.abs(y-yori),Math.abs(ylim))* (ylim>0 ? 1 : -1);
						tir.doitDetruire=true;
					}
					else{}
				}
				else // si on a depasse les limites 
				{ 
					//x et y ne changent pas
					stop = true;
					//xlim et ylim ne changent pas 

				}
			}
			//fin boucle while i 
			i++;

		}
		//il reste à déplacer le heros, on utilise les variables x lim et y lim qui ont stocké les positions 
		tir.xpos=tir.xpos+ xlim;
		tir.ypos=tir.ypos+ ylim;
	}

	public boolean testBlocage( AbstractModelPartie partie, TirMonstre tir )
	{
		Monde monde = partie.monde;
		int INIT_ABS_RECT = partie.INIT_RECT.x;
		int INIT_ORD_RECT = partie.INIT_RECT.y;

		if(ancienDirection== BASDROITE) // on vient d'en bas à droite 
		{
			if(monde.niveau[(int) ((x-1)+INIT_ABS_RECT)/100][(int) ((y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				// x= x;
				// y= y;
				tir.vit.x=0;
				tir.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== HAUTDROITE )// en vient d'en haut à droite 
		{

			if(monde.niveau[(int) ((x-1)+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				//x=x 
				y=y-1;
				//on arrete la vitesse que si on est pas allé trop loin
				tir.vit.x=0;
				tir.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== BASGAUCHE)// basgauche
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) ((y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				x= (x-1);
				//y=y 
				//on arrete la vitesse que si on est pas allé trop loin
				tir.vit.x=0;
				tir.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== HAUTGAUCHE)// on vient d'en haut à gauche 
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				x= (x-1);
				y=(y-1);

				//on arrete la vitesse que si on est pas allé trop loin
				tir.vit.x=0;
				tir.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		//on est sur la ligne en bas du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(ancienDirection== HAUT) // on vient d'en haut 
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				//x=x
				y=y-1;
				//on arrete la vitesse que si on est pas allé trop loin
				tir.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== BAS)// on vient d'en bas
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) ((y-1)+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				//x= x;
				//y=y
				//on arrete la vitesse que si on est pas allé trop loin
				tir.vit.y=0;
				return(true);
			}
			else {return(false);}
		}
		//on est sur la ligne à droite du carré mais cette ligne n'appartient pas au carré qu'on veut tester, elle est sur le bord
		else if(ancienDirection== DROITE)// on vient de droite 
		{

			if(monde.niveau[(int) ((x-1)+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				//x=x;
				//y= y;
				//on arrete la vitesse que si on est pas allé trop loin
				tir.vit.x=0;
				return(true);
			}
			else {return(false);}
		}
		else if(ancienDirection== GAUCHE)// on vient de gauche 
		{
			if(monde.niveau[(int) (x+INIT_ABS_RECT)/100][(int) (y+INIT_ORD_RECT)/100].getBloquer()==true)
			{
				x= (x-1);
				//y = y
				//on arrete la vitesse que si on est pas allé trop loin
				tir.vit.x=0;
				return(true);
			}
			else {return(false);}
		}

		else // tous les cas ont été traité, c'est donc un erreur 
		{
			throw new IllegalArgumentException("testBlocage: ancienne direction inconnue");
		}

	}


	public void choixDirection(TirMonstre tir)
	{
		//initalisation des points de départ 
		if(xDeplacement==0 && yDeplacement==0)
		{
			direction =AUCUNE;
			ancienDirection=AUCUNE;
			//aucun i 
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement==0 && yDeplacement<0)
		{
			//i va de 1 à 2 
			direction =HAUT;
			ancienDirection=BAS;
			switch(i)
			{
			case 1: x=xTMG; y=yTMH; break;
			case 2: x=xTMD; y=yTMH; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement<0)
		{
			//i va de 0 a 2
			direction =HAUTDROITE;
			ancienDirection=BASGAUCHE;
			switch(i)
			{
			case 0: x=xTMG; y=yTMH; break;
			case 1: x=xTMD; y=yTMH; break;
			case 2: x=xTMD; y=yTMB; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement==0)
		{
			//i va de 1 a 2
			direction =DROITE;
			ancienDirection=GAUCHE;
			switch(i)
			{
			case 1: x=xTMD; y=yTMH; break;
			case 2: x=xTMD; y=yTMB; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement>0 && yDeplacement>0)
		{
			// i va de 0 a 2
			direction = BASDROITE;
			ancienDirection=HAUTGAUCHE;
			switch(i)
			{
			case 0: x=xTMD; y=yTMH; break;
			case 1: x=xTMD; y=yTMB; break;
			case 2: x=xTMG; y=yTMB; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;

		}
		else if(xDeplacement==0 && yDeplacement>0)
		{ 
			// i va de 1 a 2
			direction =BAS;
			ancienDirection=HAUT;
			switch(i)
			{
			case 1: x=xTMG; y=yTMB; break;
			case 2: x=xTMD; y=yTMB; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement>0)
		{
			// i va de 0 a 2
			direction = BASGAUCHE;
			ancienDirection= HAUTDROITE;
			switch(i)
			{
			case 0: x=xTMG; y=yTMH; break;
			case 1: x=xTMG; y=yTMB; break;
			case 2: x=xTMD; y=yTMB; break; 
			}

			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement==0)
		{
			// i va de 1 a 2
			direction =GAUCHE;
			ancienDirection= DROITE;
			switch(i)
			{
			case 1: x=xTMG; y=yTMH; break;
			case 2: x=xTMG; y=yTMB; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else if(xDeplacement<0 && yDeplacement<0)
		{
			// i va de 0 a 2
			direction = HAUTGAUCHE;
			ancienDirection=BASDROITE;
			switch(i)
			{
			case 0: x=xTMG; y=yTMB; break;
			case 1: x=xTMG; y=yTMH; break;
			case 2: x=xTMD; y=yTMH; break; 
			}
			xori=(int) x;
			yori=(int) y;
			pointsuivant.x= x;
			pointsuivant.y= y;
		}
		else 
		{
			throw new IllegalArgumentException("ERREUR Collision/collision: cas vitesse non géré donc probleme direction ");
		}
	}
	//}}


}


