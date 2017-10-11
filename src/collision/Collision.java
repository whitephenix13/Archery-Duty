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

import fleches.Fleche;
import fleches.materielle.Fleche_feu;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Bloc;
import types.Hitbox;
import types.Monde;
import types.Vitesse;

public abstract class Collision implements InterfaceConstantes{

	public static List<Collidable> getMondeBlocs(Monde monde,Hitbox objectHitbox, Point INIT_RECT,Point screenDisp, int TAILLE_BLOC)
	{
		List<Collidable> mondeBlocs = new ArrayList<Collidable>();
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
				if(bloc.getHitbox(INIT_RECT,screenDisp)!=null)
				{
					mondeBlocs.add(bloc);
				}

			}

		return mondeBlocs;
	}

	public static double round(double val)
	{
		double tolerance = Math.pow(10, -9);
		return Math.round(val/tolerance)*tolerance;
	}
	/**Compute the eject point such that the object minus this is just still in collision*/
	private static Point pushLimitInside(Vector2d direction, Vector2d outVector)
	{
		//floor: the largest (closest to positive infinity) floating-point value that less than or equal to the argument 
		//expected : x>0, out = -2.5 ,floor: -3  ,value expected = -2  
		//expected : x>0, out = -3 ,floor: -3 ,value expected = -2 
		//expected : x<0, out = 2.5 ,floor: 2 ,value expected = 2  
		//expected : x<0, out = 3 ,floor: 3 ,value expected = 2  

		int xpos = (direction.x>0? 1 : 0);
		int ypos = (direction.y>0? 1 : 0);
		//avoid that the ejected object is exactly on the collided object
		int xneg = (direction.x<0 && round(outVector.x)==(int)round(outVector.x))? -1 : 0;
		int yneg = (direction.y<0 && round(outVector.y)==(int)round(outVector.y))? -1 : 0;

		return new Point(xneg + xpos,yneg + ypos);
	}


	/**The eject vector is such that the object are still touching, this function return a point such that the object is correctly ejected 
	 * @param direction: The speed of the object 
	 **/

	private static Point ejectFromTouch(Vector2d direction, Vector2d outVector)
	{
		//floor: the largest (closest to positive infinity) floating-point value that less than or equal to the argument 
		//expected : x>0, out = -2.5 ,floor: -3  ,value expected = -3  
		//expected : x>0, out = -3 ,floor: -3 ,value expected = -4  
		//expected : x<0, out = 2.5 ,floor: 2 ,value expected = 3  
		//expected : x<0, out = 3 ,floor: 3 ,value expected = 4  

		int xneg = (direction.x<0? 1 : 0);
		int yneg = (direction.y<0? 1 : 0);
		//avoid that the ejected object is exactly on the collided object
		int xpos = (direction.x>0 && round(outVector.x)==(int)round(outVector.x))? -1 : 0;
		int ypos = (direction.y>0 && round(outVector.y)==(int)round(outVector.y))? -1 : 0;
		//RES: vectOut= new Vector2d(Math.floor(outVector.x)+x+xequ,Math.floor(outVector.y)+y+yequ);
		return new Point(xneg+xpos,yneg+ypos);
	}

	/**
	 * 
	 * @param dir = speed
	 * @param dInter = dInter
	 * @param hit_eject= object 
	 * @param hit2= world
	 * @return {collisionPoint, minimumEjectPoint to apply to make sure that an object is out of collision}
	 */
	public static Point[] computeCollisionPoint(Vector2d dir, double dInter, Hitbox hit_eject, Hitbox hit2,boolean debug)
	{

		Vector2d ejectVect = new Vector2d();
		ejectVect.negate(dir);
		ejectVect.normalize();
		ejectVect.scale(dInter);
		Point beforeCol = new Point((int)Math.floor(round(ejectVect.x)),(int)Math.floor(round(ejectVect.y)));
		Hitbox hit1= Hitbox.plusPoint(hit_eject, beforeCol, true);

		Vector2d deltaStep = new Vector2d();
		deltaStep=new Vector2d(dir.x,dir.y);
		deltaStep.normalize();
		Polygon[] pols = {hit1.polygon,hit2.polygon};
		Point[] collisionPoints = new Point[2];
		Point[] minEject = new Point[2]; // by how much eject in order to separate hitboxes 

		int index = 0; //keep track of the size of collisionPoints

		int numb_restart = 0;//allow for restart with a push inside smaller (in very specific cases, push inside doesn't help)
		for(int j = 0; j<2; j++){
			int j2 = (j+1)%2;
			int sign = (j==0)?1:-1;//depending of which polygon is considered, we either add or remove delta step
			int smallestFactor=2; //keep track of the highest reached factor, if at some point found one higher than this one, it means that the new point 
			//is a better collision point than the one found, hence the previous ones should be remove 
			Polygon p1 = pols[j];
			Polygon p2 = pols[j2];
			for(int i=0;i<p1.npoints;i++)
			{
				Point ori_p = new Point(p1.xpoints[i],p1.ypoints[i]); 
				//Correct the vector by tranforming (-0.5,1.2) => (0,1), (-1.2,0.5) => (-1,0) otherwise negative switch to next int too fast
				
				int factor=2-numb_restart;
				
				Point pushInside = new Point((int)(sign*(factor*deltaStep.x)),(int)(sign*(factor*deltaStep.y)));
				Point p =  new Point((int)(ori_p.x+pushInside.x ),(int)(ori_p.y+pushInside.y));

				if(Hitbox.contains(p2,p)){//push the point inside 
					//Test if the push was necessary. It might not be because of floor function also ejecting the object 
						Point p_smaller = p;
					Point prev_p_smaller = p;
					do
					{
						prev_p_smaller = p_smaller;
						//Correct the vector by tranforming (-0.5,1.2) => (0,1), (-1.2,0.5) => (-1,0) otherwise negative switch to next int too fast
						Point point_shift = new Point((int)(sign*(factor*deltaStep.x)),(int)(sign*(factor*deltaStep.y)));
						p_smaller = new Point(ori_p.x +point_shift.x,ori_p.y+ sign*point_shift.y);
						factor--;
					}
					while(Hitbox.contains(p2, p_smaller));
					if(factor>smallestFactor)
						continue;
					if(factor<smallestFactor){
						smallestFactor=factor;
						//reset index since better collision point found 
						index=0;
					}
					collisionPoints[index]= (j==0)? prev_p_smaller : ori_p;//if j==1 the current hitbox is the when that should not moveqqqq
					minEject[index]= new Point(sign*(p_smaller.x-prev_p_smaller.x),sign*(p_smaller.y-prev_p_smaller.y));

					index+=1;
					//Special case: can happen with flat hitbox that is totally included in the other 
					if(index>=2)
						break;
				}
			}
			//if we found 1 or 2 points for iteration j=0, break, else look the reverse way (if points from hit2 are in hit 1 )
			if(index==1 || index==2)
				break;
			
			//restart if index = 0 and j= 1 => try with a smaller push inside 
			if(index==0 && j == 1 && (numb_restart<2)){
				numb_restart+=1;
				j=-1; //force restart
			}
				
		}
		//at most two points are inside, otherwise it means that the object was already stucked
		if(index==0 || index>2)
			return null;
		Point colliP_res = new Point();
		Point minEject_res = new Point();
		for(int k=0;k<index;k++)
		{
			colliP_res.x+=collisionPoints[k].x;
			colliP_res.y+=collisionPoints[k].y;
			minEject_res.x = Math.abs(minEject[k].x)>Math.abs(minEject_res.x) ? minEject[k].x : minEject_res.x ;
			minEject_res.y = Math.abs(minEject[k].y)>Math.abs(minEject_res.y) ? minEject[k].y : minEject_res.y ;
		}
		colliP_res.x/=index;
		colliP_res.y/=index;
		Point[] res = new Point[2];
		res[0]=colliP_res;
		res[1]=minEject_res;
		return res;

	}
	//////////////////////////////////////////////////////WORLD COLLISION///////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @return True if the object is colliding with the world
	 */
	public static boolean isWorldCollision(AbstractModelPartie partie, Collidable object,boolean considerTouch)
	{
		return isWorldCollision(partie,object,null,considerTouch,true);
	}
	public static boolean isWorldCollision(AbstractModelPartie partie, Collidable object,boolean considerTouch,boolean considerEffects)
	{
		return isWorldCollision(partie,object,null,considerTouch,considerEffects);
	}
	/**
	 * 
	 * @param partie
	 * @param object
	 * @param considerTouch
	 * @param motion: value by which the object has to be moved for the test
	 * @return
	 */
	public static boolean isWorldCollision(AbstractModelPartie partie, Collidable object,boolean considerTouch,Point motion)
	{
		object.pxpos(motion.x);
		object.pypos(motion.y);
		boolean res = isWorldCollision(partie,object,null,considerTouch,true);
		object.pxpos(-motion.x);
		object.pypos(-motion.y);
		return res;
	}
	/**
	 * 
	 * @return True if the object is colliding with the world
	 */
	public static boolean isWorldCollision(AbstractModelPartie partie, Hitbox objectHitbox,boolean considerTouch)
	{
		return isWorldCollision(partie,null,objectHitbox,considerTouch,true);
	}
	/**
	 * 
	 * @return True if the object is colliding with the world
	 */
	private static boolean isWorldCollision(AbstractModelPartie partie, Collidable object,Hitbox objectHitbox ,boolean considerTouch,boolean considerEffects)
	{
		List<Collidable> mondeBlocs = null;
		Vector2d firstDir =new Vector2d(1,0);
		Vector2d speed =new Vector2d(1,0);
		Vector2d minspeed =new Vector2d(-1,0);

		if(object != null)
		{
			if(object.checkCollideWithNone())
				return false;
			objectHitbox= object.getHitbox(partie.INIT_RECT, partie.getScreenDisp()).copy();

			speed= object.getGlobalVit(partie).vect2d();
			minspeed.negate(speed);
		}


		mondeBlocs = getMondeBlocs(partie.monde,objectHitbox, partie.INIT_RECT,partie.getScreenDisp(),
				partie.TAILLE_BLOC);
		List<Collidable> effectColli = Collidable.getAllCollidableEffect(partie, CustomBoundingSquare.getScreen());
		List<Collidable> allColli = new ArrayList<Collidable>();
		allColli.addAll(mondeBlocs);
		if((object == null || object.checkCollideWithEffect()) && considerEffects)
		{
			allColli.addAll(effectColli);
		}

		allColli.remove(object);


		for(Collidable col : allColli)
		{
			Hitbox box = col.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();
			//WARNING: fixedScreen not used for optimization
			if(object != null){
				Vector2d supp1 = GJK_EPA.support(box.polygon,minspeed);//fixed one
				Vector2d supp2 = GJK_EPA.support(objectHitbox.polygon, speed);//mobile one
				firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);
			}

			List<Vector2d> simplex = GJK_EPA.intersects(box.polygon,objectHitbox.polygon ,firstDir);
			List<Vector2d> normals = new ArrayList<Vector2d>();

			int collision_type=-1;
			boolean dNull= true;
			Double dInter=0.0d;
			if(simplex!=null)
			{
				dNull=false;
				dInter= GJK_EPA.EPA(objectHitbox.polygon,box.polygon, simplex, minspeed, normals);

			}
			collision_type =  GJK_EPA.isIntersect(dInter,dNull);

			if( (collision_type == GJK_EPA.INTER && !considerTouch) || (considerTouch && collision_type != GJK_EPA.NOT_INTER) ){
				return true;
			}
		}
		return false;
	}

	//////////////////////////////////////////////////////EJECT WORLD/OBJECT COLLISION///////////////////////////////////////////////////////////////////

	/** @return false if object is stuck into environment */
	/*public static boolean ejectWorldCollision(AbstractModelPartie partie, Collidable object)
	{
		//on calcul l'endroit où serait le nouvel objet
		int xDeplacement=0;
		int yDeplacement=0;
		List<Collidable> mondeBlocs = null;
		Hitbox objectHitbox = null;

		//Vitesse speed= object.getGlobalVit(partie,object.type.equals(TypeObject.heros));
		Vitesse speed= object.getGlobalVit(partie);
		//project the speed with respect to the last norm 
		speed = Vitesse.removePenetrationComponent(speed, object.getNormCollision());

		Vitesse minSpeed= new Vitesse(-1*speed.x,-1*speed.y);

		double corrected_norm=1;
		double speed_norm = speed.norm();
		if(object.max_speed_norm>0 && object.max_speed_norm < speed_norm)
		{
			corrected_norm= object.max_speed_norm/speed_norm;
		}

		xDeplacement=(int) Math.round(speed.x*corrected_norm  );
		yDeplacement=(int) Math.round(speed.y*corrected_norm  );

		boolean noIntersection = false;
		//distance which must move the object to avoid any collisions
		Point totalInterDist = new Point(0,0);

		List<Collidable> collidableEffects = Collidable.getAllCollidableEffect(partie,true);

		//variables pour connaitre la direction d'intersection
		Collidable intersectedCol=null; // hitbox intersectée par l'objet mobile
		Point intersectedCollision = null; //eject coordinate of collision 

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
			Point deplaceEcran =new Point(partie.xScreendisp,partie.yScreendisp);
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
			List<Collidable> allCollidables = new ArrayList<Collidable>();
			if(object == null || object.checkCollideWithEffect())
			{
				allCollidables.addAll(collidableEffects);
			}
			allCollidables.addAll(mondeBlocs);

			for(Collidable col : allCollidables)
			{
				Hitbox mondeBox = col.getHitbox(partie.INIT_RECT);

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
					if(speed.x==0 && speed.y==0){
						return false; //stuck
					}

					Vector2d vectOut = new Vector2d(-1*speed.x,-1*speed.y);
					vectOut.normalize();
					vectOut.scale(dInter);
					//avoid that the ejected object is exactly on the collided object
					Point correctEject = ejectFromTouch(speed.vect2d(), vectOut);
					//TODO= correct floor
					Vector2d vectOut_corrected= new Vector2d(Math.floor(vectOut.x)+correctEject.x,Math.floor(vectOut.y)+correctEject.y);		

					//Only remember the bigest vect out 
					if((Math.abs(maxInterDist.x))<Math.abs((int)vectOut_corrected.x) || 
							((Math.abs(maxInterDist.y))<Math.abs((int)vectOut_corrected.y) ))
					{
						intersectedCol=col;
						//TODO: compute intersectedCollision using vectOut
						intersectedCollision=computeCollisionPoint(speed.vect2d(),dInter,objectHitbox,mondeBox);
						if(normals.size()>0)
						{
							EPA_normal=normals.get(0);

						}
						maxInterDist.x =(int)vectOut_corrected.x;
						maxInterDist.y =(int)vectOut_corrected.y;
					}
					noIntersection=false;
				}

			}
			totalInterDist.x += maxInterDist.x;
			totalInterDist.y += maxInterDist.y;

		}
		boolean xlimExceeded = (Math.abs(totalInterDist.x)> Math.abs(xDeplacement));
		boolean ylimExceeded = (Math.abs(totalInterDist.y)> Math.abs(yDeplacement));
		int final_x_dep = xlimExceeded ? 0 : (xDeplacement+totalInterDist.x);
		int final_y_dep = ylimExceeded ? 0 : (yDeplacement+totalInterDist.y);	

		object.pxpos_sync(final_x_dep);
		object.pypos_sync(final_y_dep);

		//TODO: correct correctEject by testing polygon collision
		//last collision test to check if the object is now out 
		if(xlimExceeded || ylimExceeded)
		{
			if(isWorldCollision(partie, object,false)){
				return false; // object is stuck
			}
		}

		//on calcul la direction de collision 
		if(intersectedCol!=null)
		{
			object.setNormCollision(EPA_normal);
			//on appelle la fonction qui gère les collisions en fonction de la normale
			object.handleWorldCollision(EPA_normal,partie,intersectedCol,false);
		}
		else
			object.setNormCollision(null);


		return true; //not stuck
	}*/

	/** @return false if object is stuck into environment */
	public static boolean ejectWorldCollision(AbstractModelPartie partie, Collidable object)
	{
		Point applyMotion = null; //null to apply the motion
		boolean considerEffect = true;
		return ejectCollision(partie,object,null,null,considerEffect,applyMotion,true,true,true,null);
	}
	/**
	 * 
	 * @param partie
	 * @param object
	 * @param ejectVect
	 * @return
	 */
	public static boolean ejectWorldCollision(AbstractModelPartie partie, Collidable object,Vector2d ejectVect,boolean exact)
	{
		return ejectWorldCollision(partie,object,ejectVect,exact,true,true,null);
	}

	/**
	 * 
	 * @param partie
	 * @param object
	 * @param ejectVect
	 * @param exact
	 * @param setColliInfo
	 * @param warnCollision
	 * @return false if stuck
	 */
	public static boolean ejectWorldCollision(AbstractModelPartie partie, Collidable object,Vector2d ejectVect,boolean exact,boolean setColliInfo, 
			boolean warnCollision,Collidable[] resCollidedObject)
	{
		if(exact)
		{
			object.pxpos((int) -ejectVect.x);
			object.pypos((int) -ejectVect.y);
		}
		Point applyMotion = new Point(); //not null to avoid motion application
		boolean considerEffect = true;
		boolean res = ejectCollision(partie,object,null,ejectVect,considerEffect,applyMotion,true,setColliInfo,warnCollision,resCollidedObject);
		//res = res && (Math.abs(ejectVect.x)>=Math.abs(applyMotion.x)) && (Math.abs(ejectVect.y)>=Math.abs(applyMotion.y));
		if(exact)
		{
			object.pxpos((int) ejectVect.x);
			object.pypos((int) ejectVect.y);
		}

		if(res)
		{
			object.pxpos(applyMotion.x);
			object.pypos(applyMotion.y);
		}
		return res; 
	}
	/**
	 * 
	 * @param partie
	 * @param object
	 * @param objectToEject: eject from world but also from this object
	 * @param motion
	 * @param appliedMotion
	 * @param considerEffects
	 * @return
	 */
	public static boolean ejectWorldCollision(AbstractModelPartie partie, Collidable object,Collidable objectToEject,Point motion,Point appliedMotion,
			boolean considerEffects)
	{
		//appliedMotion not null => motion will not be applied, instead appliedMotion will be set to the motion that has to be apply after ejection
		//The object has to be moved by at most motion (just what it needs to be ejected). 
		//In order to satisfy this, move the object by motion, then try to move it by -motion and see where it get stucks 
		object.pxpos(motion.x);
		object.pypos(motion.y);
		boolean res = ejectCollision(partie,object,objectToEject,new Vector2d(-motion.x,-motion.y),considerEffects,appliedMotion,true,true,true,null);
		//applied Motion is now set to the eject value, ie the desired motion is -motion + appliedMotion 
		//correct applied Motion 
		object.pxpos(-motion.x);
		object.pypos(-motion.y);
		return res; 
	}

	/**Eject object 1(moves) from object 2(fixed) by at most ejectDeplacement(deplacement of object2) and test if after that object 1 collide with the world. 
	 * To do so we simulate that object 1 did ejectDeplacement (already) */
	public static boolean ejectObjectCollision(AbstractModelPartie partie, Collidable object1, Collidable object2,Vector2d _ejectDeplacement)
	{
		Point applyMotion = null; //null to apply the motion
		boolean considerEffect = true;
		return ejectCollision(partie,object1,object2,_ejectDeplacement,considerEffect,applyMotion,false,true,true,null);
	}

	/**
	 * 
	 * @param partie
	 * @param object1
	 * @param object2
	 * @param _ejectDeplacement
	 * @param considerEffects consider the effects as part of the collision is set to  "true" 
	 * @param appliedMotion: set it to null to apply motion, set it to new Point() to not apply motion and set this value to the applied one (so that it can be apply later) 
	 * @return false if stuck
	 */
	public static boolean ejectCollision(AbstractModelPartie partie, Collidable object1, Collidable object2,Vector2d _ejectDeplacement,boolean considerEffects,
			Point appliedMotion,boolean computeWorld,boolean setColliInfo, boolean warnCollision,Collidable[] resCollidedObject)
	{

		boolean shouldApplyMotion = (appliedMotion ==null);

		Hitbox object1Hitbox = null;
		Hitbox object2Hitbox = null;

		Vector2d ejectDeplacement ;
		Vector2d minEjectDeplacement= new Vector2d();
		if(computeWorld)
		{
			if(_ejectDeplacement != null)
				ejectDeplacement=_ejectDeplacement;
			else
			{
				Vitesse speed= object1.getGlobalVit(partie);
				//project the speed with respect to the last norm 
				speed = Vitesse.removePenetrationComponent(speed, object1.getNormCollision());

				double corrected_norm=1;
				double speed_norm = speed.norm();
				if(object1.max_speed_norm>0 && object1.max_speed_norm < speed_norm)
				{
					corrected_norm= object1.max_speed_norm/speed_norm;
				}

				ejectDeplacement=new Vector2d(speed.x*corrected_norm,speed.y*corrected_norm);
			}
		}
		else
			ejectDeplacement = new Vector2d(_ejectDeplacement.x,_ejectDeplacement.y);
		minEjectDeplacement.negate(ejectDeplacement);

		Point desired_dep = new Point((int) Math.round(ejectDeplacement.x ),(int) Math.round(ejectDeplacement.y ));

		List<Collidable> mondeBlocs = null;

		//Vitesse speed= object.getGlobalVit(partie,object.type.equals(TypeObject.heros));


		boolean noIntersection = false;
		//distance which must move the object to avoid any collisions
		Point totalInterDist = new Point(0,0);


		//variables pour connaitre la direction d'intersection
		Collidable intersectedCol=null; // hitbox intersectée par l'objet mobile
		Hitbox intersectedHit = null; //same as above but because the method is generic, computing the hitbox from the collidable would need computation that was already done
		Point ejectFromCollisionPoint = null; //value to use to eject an object from the collision point

		Point intersectedCollision = null; //eject coordinate of collision 
		Vector2d EPA_normal = null; //normal du coté intersecté

		List<Collidable> collidableEffects = null;
		if(computeWorld && considerEffects)
			collidableEffects=Collidable.getAllCollidableEffect(partie,CustomBoundingSquare.getScreen());

		Point prevtotalInterDist = new Point(-1,-1);
		boolean xlimExceeded = false;
		boolean ylimExceeded = false;
		Point maxInterDist=null;

		if(object2!=null)
		{
			object2Hitbox=object2.getHitbox(partie.INIT_RECT, partie.getScreenDisp()).copy();
		}

		while(!noIntersection && ((prevtotalInterDist.x!=totalInterDist.x ) || (prevtotalInterDist.y !=totalInterDist.y)) )
		{
			prevtotalInterDist= new Point(totalInterDist.x,totalInterDist.y);

			noIntersection=true;
			//distance which must move the object to avoid actual collision with the world
			maxInterDist = new Point(0,0);

			//translate all object hitboxes
			Point p;
			if(computeWorld)
				p=new Point(totalInterDist.x+desired_dep.x,
						totalInterDist.y+desired_dep.y);
			else
				p = new Point(); //do not substract x deplacement as usual because we assumed the object already moved by ejectDeplacement

			object1Hitbox= Hitbox.plusPoint(object1.getHitbox(partie.INIT_RECT,partie.getScreenDisp()),p,true);

			//WARNING: SPECIAL BREAK HERE Break here if lim exceed so that the hitbox 1 is computed correctly 
			if(xlimExceeded || ylimExceeded)
			{
				break;
			}

			List<Collidable> allCollidables = new ArrayList<Collidable>();

			if(object2!=null)
			{
				allCollidables.add(object2);
			}
			if(computeWorld)
			{
				mondeBlocs = getMondeBlocs(partie.monde,object1Hitbox, partie.INIT_RECT,partie.getScreenDisp(),
						partie.TAILLE_BLOC);

				if(collidableEffects!=null && object1.checkCollideWithEffect())
				{
					allCollidables.addAll(collidableEffects);
					allCollidables.remove(object1);
				}

				allCollidables.addAll(mondeBlocs);}

			for(Collidable col : allCollidables)
			{
				if(computeWorld)
					object2Hitbox = col.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();
				
				Vector2d supp1 = GJK_EPA.support(object2Hitbox.polygon,minEjectDeplacement );//fixed one
				Vector2d supp2 = GJK_EPA.support(object1Hitbox.polygon, ejectDeplacement);//mobile one
				Vector2d firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);

				List<Vector2d> simplex = GJK_EPA.intersects(object2Hitbox.polygon,object1Hitbox.polygon ,firstDir);
				List<Vector2d> normals = new ArrayList<Vector2d>();

				int collision_type=-1;
				boolean dNull= true;
				Double dInter=0.0d;

				if(simplex!=null)
				{
					dNull=false;
					dInter= GJK_EPA.EPA(object2Hitbox.polygon,object1Hitbox.polygon, simplex, new Vector2d(minEjectDeplacement.x,minEjectDeplacement.y), normals);
				}

				collision_type =  GJK_EPA.isIntersect(dInter,dNull);

				boolean inTouch= collision_type == GJK_EPA.TOUCH;
				boolean inCollision = collision_type==GJK_EPA.INTER; 

				if(inTouch || inCollision)
				{
					//Object is stuck in environment
					if(ejectDeplacement.x==0 && ejectDeplacement.y==0){
						return false; //stuck
					}

					Vector2d vectOut = new Vector2d(minEjectDeplacement.x,minEjectDeplacement.y);
					vectOut.normalize();
					vectOut.scale(dInter);

					//avoid that the ejected object is exactly on the collided object
					Point correctEject = ejectFromTouch(new Vector2d(ejectDeplacement.x,ejectDeplacement.y), vectOut);
					Vector2d vectOut_corrected= new Vector2d(Math.floor(round(vectOut.x))+correctEject.x,Math.floor(round(vectOut.y))+correctEject.y);
					//Only remember the bigest vect out 
					if((Math.abs(maxInterDist.x))<Math.abs((int)vectOut_corrected.x) || 
							((Math.abs(maxInterDist.y))<Math.abs((int)vectOut_corrected.y) ))
					{
						if(normals.size()>0)
						{
							EPA_normal=normals.get(0);

						}
						if(computeWorld)
						{
							intersectedCol=col;
							intersectedHit=object2Hitbox.copy();
							boolean debug = (object1 instanceof Fleche);
							Point[] res = computeCollisionPoint(new Vector2d(ejectDeplacement.x,ejectDeplacement.y),dInter,object1Hitbox,object2Hitbox,debug);
							intersectedCollision= res[0];
							Vector2d projectedEPA = GJK_EPA.projectVectorTo90(EPA_normal,false,0);
							ejectFromCollisionPoint=new Point((int) (projectedEPA.x),(int)(projectedEPA.y));
						}

						maxInterDist.x =(int)vectOut_corrected.x;
						maxInterDist.y =(int)vectOut_corrected.y;
					}
					noIntersection=false;
				}

			}
			totalInterDist.x += maxInterDist.x;
			totalInterDist.y += maxInterDist.y;


			xlimExceeded = (Math.abs(totalInterDist.x)> Math.abs(desired_dep.x));
			ylimExceeded = (Math.abs(totalInterDist.y)> Math.abs(desired_dep.y));

		}

		int final_x_dep = xlimExceeded ? 0 : (desired_dep.x+totalInterDist.x);
		int final_y_dep = ylimExceeded ? 0 : (desired_dep.y+totalInterDist.y);	

		if(shouldApplyMotion){
			object1.pxpos_sync(final_x_dep);
			object1.pypos_sync(final_y_dep);
		}
		else{
			appliedMotion.setLocation(new Point(xlimExceeded ?0 : (totalInterDist.x) ,
					ylimExceeded ? 0 : (totalInterDist.y)));
		}


		if(computeWorld)
		{
			//on calcul la direction de collision 
			if(intersectedCol!=null)
			{
				if(setColliInfo)
					object1.setCollisionInformation(EPA_normal, intersectedCollision,  ejectFromCollisionPoint);
				//on appelle la fonction qui gère les collisions en fonction de la normale

				if(setColliInfo && !warnCollision && resCollidedObject!=null){
					resCollidedObject[0]=intersectedCol;
				}
			}
			else
				if(setColliInfo){
					object1.setCollisionInformation(null, null, null);
				}
		}
		
		if(shouldApplyMotion && isWorldCollision(partie, object1,true)){
			object1.pxpos_sync(-final_x_dep);
			object1.pypos_sync(-final_y_dep);
			return false; // object is stuck
		}
		if(computeWorld && (intersectedCol!=null) && warnCollision)
			object1.handleWorldCollision(EPA_normal,partie,intersectedCol,false);
		
		return true; //not stuck
	}

	//////////////////////////////////////////////////////OBJECTS COLLISION///////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param partie
	 * @param object1
	 * @param object2
	 * @param computeDirWithSpeed
	 * @return true if collision
	 */
	public static boolean testcollisionObjects(AbstractModelPartie partie, Collidable object1,Collidable object2,boolean computeDirWithSpeed)
	{
		return collisionObjects(partie, object1,object2,null,null,false,computeDirWithSpeed);
	}
	/**
	 * 
	 * @param partie
	 * @param objectHitbox1
	 * @param objectHitbox2
	 * @return true if collision
	 */
	public static boolean testcollisionHitbox(AbstractModelPartie partie, Hitbox objectHitbox1,Hitbox objectHitbox2)
	{
		return  collisionObjects(partie, null,null,objectHitbox1,objectHitbox2,false,false);
	}
	/**
	 * 
	 * @param partie
	 * @param object1
	 * @param object2
	 * @param computeDirWithSpeed
	 * @return true if collision
	 */
	public static boolean collisionObjects(AbstractModelPartie partie, Collidable object1,Collidable object2,boolean computeDirWithSpeed)
	{
		return collisionObjects(partie, object1,object2,null,null,true,computeDirWithSpeed);
	}
	
	/**
	 * 
	 * @param partie
	 * @param object1
	 * @param object2
	 * @param objectHitbox1
	 * @param objectHitbox2
	 * @param warnCollision
	 * @param computeDirWithSpeed
	 * @return true if collision
	 */
	private static boolean collisionObjects(AbstractModelPartie partie, Collidable object1,Collidable object2,Hitbox objectHitbox1,Hitbox objectHitbox2,
			boolean warnCollision,boolean computeDirWithSpeed)
	{

		Vector2d firstDir=new Vector2d(1,0);

		if(object1!=null)
		{
			objectHitbox1= object1.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();
		}
		if(object2!=null)
		{
			objectHitbox2= object2.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();
		}

		if(computeDirWithSpeed && (object1!=null) && (object2!=null)){
			Vector2d deltaSpeed = new Vector2d(object1.getGlobalVit(partie).x-object2.getGlobalVit(partie).x,object1.getGlobalVit(partie).y-object2.getGlobalVit(partie).y);
			Vector2d m_deltaSpeed= new Vector2d(-deltaSpeed.x,-deltaSpeed.y);

			Vector2d supp1 = GJK_EPA.support(objectHitbox1.polygon,deltaSpeed );//fixed one
			Vector2d supp2 = GJK_EPA.support(objectHitbox2.polygon, m_deltaSpeed);//mobile one
			firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);
		}

		List<Vector2d> simplex = GJK_EPA.intersects(objectHitbox1.polygon,objectHitbox2.polygon ,firstDir);


		List<Vector2d> normals = new ArrayList<Vector2d>();

		Vector2d EPA_normal = null;

		if(simplex!=null){
			//fast case
			if(!warnCollision)
				return true; 
			//Longer case, even if the output is true, still compute usefull values to call handleObjectCollision
			GJK_EPA.EPA(objectHitbox1.polygon, objectHitbox2.polygon, simplex, firstDir, normals);
			if(normals.size()>0)
				EPA_normal=normals.get(0);
			if((object1!=null) && (object2!=null)){
				object1.handleObjectCollision(partie,object2,EPA_normal);
				EPA_normal.negate();
				object2.handleObjectCollision(partie,object1,EPA_normal);
			}
			return true;
		}
		else
			return false;
	}


	/////////////////////////////////////////// EFFECT ENTITIE COLLIDING //////////////////////////////////////////////////////////

	/**
	 * 
	 * @param partie
	 * @param object
	 * @param motion
	 * @return Returns all collidable that collide with object
	 */
	public static List<Collidable> getAllEffectEntitieCollision(AbstractModelPartie partie,Collidable object,Point motion )
	{
		List<Collidable> colliders = new ArrayList<Collidable>(); //return value
		List<Collidable> allCollidables = new ArrayList<Collidable>(); // all effect + entitie

		Hitbox objectHitbox =null;

		//compute object hitbox

		objectHitbox= object.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();

		//Retrieve all relevant Collidable

		//EFFECT COLLISION
		if(object.checkCollideWithEffect())
		{
			allCollidables.addAll(Collidable.getAllCollidableEffect(partie,CustomBoundingSquare.getScreen()));
		}

		//ENTITIE COLLISION
		if(object.checkCollideWithEntitie())
		{
			allCollidables.addAll(Collidable.getAllEntitiesCollidable(partie, object));
		}

		allCollidables.remove(object);
		for(Collidable col : allCollidables)
		{
			Hitbox box = col.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
			Vector2d supp1 = GJK_EPA.support(box.polygon,new Vector2d(-motion.x,-motion.y));//fixed one
			Vector2d supp2 = GJK_EPA.support(objectHitbox.polygon, new Vector2d(motion.x,motion.y));//mobile one
			Vector2d firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);


			List<Vector2d> simplex = GJK_EPA.intersects(box.polygon,objectHitbox.polygon ,firstDir);
			List<Vector2d> normals = new ArrayList<Vector2d>();

			int collision_type=-1;
			boolean dNull= true;
			Double dInter=0.0d;
			if(simplex!=null)
			{
				dNull=false;
				dInter= GJK_EPA.EPA(objectHitbox.polygon,box.polygon, simplex, new Vector2d(-motion.x,-motion.y), normals);

			}
			collision_type =  GJK_EPA.isIntersect(dInter,dNull);
			if( (collision_type != GJK_EPA.NOT_INTER) )
				colliders.add(col);
		}

		return colliders;
	}
}
