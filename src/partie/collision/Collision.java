package partie.collision;
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

import gameConfig.InterfaceConstantes;
import menu.menuPrincipal.ModelPrincipal;
import partie.bloc.Bloc;
import partie.bloc.Monde;
import partie.effects.Effect;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import utils.PointHelper;
import utils.Vitesse;

public abstract class Collision implements InterfaceConstantes{

	private final static double ROUND_TOLERANCE = Math.pow(10, -9);
	
	public static List<Collidable> getMondeBlocs(final Monde monde,final Hitbox objectHitbox,final Point INIT_RECT,final Point screenDisp,final int TAILLE_BLOC)
	{
		List<Collidable> mondeBlocs = new ArrayList<Collidable>();//default capacity is 10. 
		int xmin = objectHitbox.getXmin()-2;//-2: deal with case of touching objects
		int ymin = objectHitbox.getYmin()-2;//-2: deal with case of touching objects
		int xmax = objectHitbox.getXmax()+2;//-2: deal with case of touching objects
		int ymax = objectHitbox.getYmax()+2;//-2: deal with case of touching objects
		
		Bloc bloc;
		for(int x = xmin; x<(xmax+TAILLE_BLOC) ; x+=TAILLE_BLOC)
			for(int y = ymin; y<(ymax+TAILLE_BLOC); y+=TAILLE_BLOC)
			{
				int xIndex = (x + INIT_RECT.x)/TAILLE_BLOC;
				int yIndex = (y + INIT_RECT.y)/TAILLE_BLOC;
				if(xIndex>=0 && (yIndex>=0) && (xIndex<monde.niveau.length) && (yIndex<monde.niveau[0].length) )
				{
					bloc= monde.niveau[xIndex][yIndex];
					if(bloc != null && !bloc.getHitbox(INIT_RECT,screenDisp).isNull())
					{
						mondeBlocs.add(bloc);
					}
				}

			}
		return mondeBlocs;
	}

	public static double round(double val)
	{
		return Math.round(val/ROUND_TOLERANCE)*ROUND_TOLERANCE;
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
	private static Point ejectFromIntTouch( Vector2d outVector)
	{
		//floor: the largest (closest to positive infinity) floating-point value that less than or equal to the argument 
		//expected : x>0, out = -2.5 ,int: -2  ,value expected = -3  
		//expected : x>0, out = -3 ,int: -3 ,value expected = -4  
		//expected : x<0, out = 2.5 ,int: 2 ,value expected = 3  
		//expected : x<0, out = 3 ,int: 3 ,value expected = 4  
		return new Point((int)Math.signum(outVector.x),(int)Math.signum(outVector.y));
	}
	public static Point[] computeCollisionPoint(Vector2d dir, double dInter, Hitbox hit_eject, Hitbox hit2)
	{
		return computeCollisionPoint(dir, dInter, hit_eject, hit2,false);
	}
	/**
	 * 
	 * @param dir = speed
	 * @param dInter = dInter
	 * @param hit_eject= object 
	 * @param hit2= world
	 * @return {collisionPoint, minimumEjectPoint to apply to make sure that an object is out of collision}
	 */
	public static Point[] computeCollisionPoint(Vector2d dir, double dInter, Hitbox hit_eject, Hitbox hit2,boolean log)
	{

		//The two hitboxes are currently colliding. The goal is to find the points which are currently in the other hitbox
		//and that after one step are not longer inside
		Vector2d deltaStep =new Vector2d(-dir.x,-dir.y); //move in the direction of ejection
		deltaStep.normalize();
		Polygon[] pols = {hit_eject.polygon,hit2.polygon};
		Point[] collisionPoints = new Point[2];
		Point[] minEject = new Point[2]; // by how much eject in order to separate hitboxes 
		if(log)
			System.out.println(hit_eject +" "+ hit2+" "+ dInter +" "+ dir);
		int index = 0; //keep track of the size of collisionPoints
		int end_factor = (int) Math.ceil(dInter+2);
		int number_restart = 0; // sometime hitboxes are intersecting but no corner is inside the other box. The hope is that small displacement solve the problem
		for(int j = 0; j<2; j++){
			//j=0 : iterate over hit_eject and check if one of the point is in hit2
			//j = 1 the opposite
			int j2 = (j+1)%2;
			int sign = (j==0)?1:-1;//depending of which polygon is considered, we either add or remove delta step
			Polygon p1 = pols[j];
			Polygon p2 = pols[j2];
			for(int i=0;i<p1.npoints;i++)
			{
				Point ori_p = new Point(p1.xpoints[i],p1.ypoints[i]); 
				//Correct the vector by tranforming (-0.5,1.2) => (0,1), (-1.2,0.5) => (-1,0) otherwise negative switch to next int too fast

				int factor=number_restart;

				Vector2d exactPush = new Vector2d(sign*(factor*deltaStep.x),sign*(factor*deltaStep.y));
				//Correct the push outside to make sure that the rectangle (0,100),(200,200) and (200,100) + (0.5,0.5) are no longer colliding.
				//otherwise taking the int give: (200,100) which is still a problem
				Point correct = ejectFromIntTouch(exactPush);
				Point pushOutside = new Point((int)exactPush.x + correct.x,(int)exactPush.y + correct.y);

				Point p =  new Point((int)(ori_p.x+pushOutside.x ),(int)(ori_p.y+pushOutside.y));
				if(log)
					System.out.println(Hitbox.polyToString(p2)+ " contains " + p +" "+ Hitbox.contains(p2,p));
				if(Hitbox.contains(p2,p)){//push the point inside 
					//Test if the push was necessary. It might not be because of floor function also ejecting the object 
					Point p_smaller = p;
					Point prev_p_smaller = p;
					boolean contain = true;
					do
					{
						prev_p_smaller = p_smaller;
						//Correct the vector by tranforming (-0.5,1.2) => (0,1), (-1.2,0.5) => (-1,0) otherwise negative switch to next int too fast
						Vector2d exact_shift = new Vector2d(sign*(factor*deltaStep.x),sign*(factor*deltaStep.y));
						Point correct_shift = ejectFromIntTouch(exact_shift);
						Point point_shift = new Point((int)exact_shift.x+correct_shift.x,(int)exact_shift.y+correct_shift.y);
						p_smaller = new Point(ori_p.x +point_shift.x,ori_p.y+ point_shift.y);
						factor++;
						contain=Hitbox.contains(p2, p_smaller);
						if(log)
							System.out.println("\t"+Hitbox.polyToString(p2)+ " contains " + p_smaller +" "+ contain);
					}
					while(contain && factor<=end_factor);
					if(contain)
						continue;
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
			//Restart by ejecting a bit the object to get closer to the collision point 
			if(index==0 && j==1 && number_restart<=end_factor)
			{
				number_restart+=1;
				j=-1;
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
	public static boolean isWorldCollision(AbstractModelPartie partie, Collidable object,boolean touchingIsColliding)
	{
		return isWorldCollision(partie,object,null,touchingIsColliding,true);
	}
	public static boolean isWorldCollision(AbstractModelPartie partie, Collidable object,boolean touchingIsColliding,boolean considerEffects)
	{
		return isWorldCollision(partie,object,null,touchingIsColliding,considerEffects);
	}
	/**
	 * 
	 * @param partie
	 * @param object
	 * @param touchingIsColliding
	 * @param motion: value by which the object has to be moved for the test
	 * @return
	 */
	public static boolean isWorldCollision(AbstractModelPartie partie, Collidable object,boolean touchingIsColliding,Point motion)
	{
		object.addXpos(motion.x);
		object.addYpos(motion.y);
		boolean res = isWorldCollision(partie,object,null,touchingIsColliding,true);
		object.addXpos(-motion.x);
		object.addYpos(-motion.y);
		return res;
	}
	/**
	 * 
	 * @return True if the object is colliding with the world
	 */
	public static boolean isWorldCollision(AbstractModelPartie partie, Hitbox objectHitbox,boolean touchingIsColliding)
	{
		return isWorldCollision(partie,null,objectHitbox,touchingIsColliding,true);
	}
	/**
	 * 
	 * @return True if the object is colliding with the world
	 */
	private static boolean isWorldCollision(AbstractModelPartie partie, Collidable object,Hitbox objectHitbox ,boolean touchingIsColliding,boolean considerEffects)
	{
		if(object instanceof Effect){
			System.out.println("Is world colli ");
		}
		List<Collidable> mondeBlocs = null;
		Vector2d firstDir =new Vector2d(1,0);
		Vector2d speed =new Vector2d(1,0);
		Vector2d minspeed =new Vector2d(-1,0);

		if(object != null)
		{
			if(object.checkCollideWithNone())
				return false;
			objectHitbox= object.getHitbox(partie.INIT_RECT, partie.getScreenDisp()).copy();

			speed= object.getGlobalVit();
			minspeed.negate(speed);
		}


		mondeBlocs = getMondeBlocs(partie.monde,objectHitbox, partie.INIT_RECT,partie.getScreenDisp(),
				partie.TAILLE_BLOC);
		List<Collidable> effectColli = Collidable.getAllCollidableEffectOnScreen(partie);
		List<Collidable> allColli = new ArrayList<Collidable>();
		allColli.addAll(mondeBlocs);
		if(object instanceof Effect){
			System.out.println("Monde blocs "+ mondeBlocs+" for "+ objectHitbox+" "+objectHitbox.getXmin()+" "+objectHitbox.getXmax()+" "+objectHitbox.getYmin());
		}
		if((object == null || object.checkCollideWithEffect()) && considerEffects)
		{
			allColli.addAll(effectColli);
		}
		if(object instanceof Effect){
			System.out.println("Effect "+ effectColli);
		}
		allColli.remove(object);
		if(object instanceof Effect){
			System.out.println("remove  "+ object);
		}
		Double dInter=0.0d;
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
			if(object instanceof Effect){
				System.out.println("Check collision "+objectHitbox +" " + box +" : "+  simplex!= null);
			}
			List<Vector2d> normals = new ArrayList<Vector2d>();

			int collision_type=-1;
			boolean dNull= true;
			dInter=0.0d;
			if(simplex!=null)
			{
				dNull=false;
				if(Math.abs(minspeed.x) == 0 && Math.abs(minspeed.y)==0 ) 
					dInter = GJK_EPA.EPA(box.polygon,objectHitbox.polygon, simplex, normals);
				else
					dInter= GJK_EPA.directionalEPA(box.polygon,objectHitbox.polygon, simplex, minspeed, normals);

			}
			collision_type =  GJK_EPA.isIntersect(dInter,dNull);

			if( (collision_type == GJK_EPA.INTER && !touchingIsColliding) || (touchingIsColliding && collision_type != GJK_EPA.NOT_INTER) ){
				return true;
			}
		}
		return false;
	}

	//////////////////////////////////////////////////////EJECT WORLD/OBJECT COLLISION///////////////////////////////////////////////////////////////////


	/** @return false if object is stuck into environment */
	public static boolean ejectWorldCollision(AbstractModelPartie partie, Collidable object)
	{
		Point applyMotion = null; //null to apply the motion
		boolean considerEffect = true;
		boolean touchingIsColliding = true;
		boolean computeWorld = true;
		boolean setColliInfo = true;
		boolean warnCollision = true;
		
		Collidable[] resCollidedObject = null;
		Vector2d ejectVect = null;
		Vector2d closestEjectMaxDist = null;
		
		return ejectCollision(partie,object,null,ejectVect,considerEffect,touchingIsColliding,applyMotion,computeWorld,setColliInfo,warnCollision,
				resCollidedObject,closestEjectMaxDist);
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
		boolean touchingIsColliding = true;
		boolean computeWorld = true;

		Vector2d closestEjectMaxDist = null;
		
		if(exact)
		{
			object.addXpos((int) -ejectVect.x);
			object.addYpos((int) -ejectVect.y);
		}
		Point applyMotion = new Point(); //not null to avoid motion application
		boolean considerEffect = true;
		boolean res = ejectCollision(partie,object,null,ejectVect,considerEffect,touchingIsColliding,applyMotion,computeWorld,setColliInfo,
				warnCollision,resCollidedObject,closestEjectMaxDist);
		//res = res && (Math.abs(ejectVect.x)>=Math.abs(applyMotion.x)) && (Math.abs(ejectVect.y)>=Math.abs(applyMotion.y));
		if(exact)
		{
			object.addXpos((int) ejectVect.x);
			object.addYpos((int) ejectVect.y);
		}

		if(res)
		{
			object.addXpos(applyMotion.x);
			object.addYpos(applyMotion.y);
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
		boolean touchingIsColliding = true;
		boolean computeWorld = true;
		boolean setColliInfo = true;
		boolean warnCollision = true;
		
		Collidable[] resCollidedObject = null;
		Vector2d closestEjectMaxDist = null;
		
		//appliedMotion not null => motion will not be applied, instead appliedMotion will be set to the motion that has to be apply after ejection
		//The object has to be moved by at most motion (just what it needs to be ejected). 
		//In order to satisfy this, move the object by motion, then try to move it by -motion and see where it get stucks 
		object.addXpos(motion.x);
		object.addYpos(motion.y);
		boolean res = ejectCollision(partie,object,objectToEject,new Vector2d(-motion.x,-motion.y),considerEffects,touchingIsColliding,appliedMotion,
				computeWorld,setColliInfo,warnCollision,resCollidedObject,closestEjectMaxDist);
		//applied Motion is now set to the eject value, ie the desired motion is -motion + appliedMotion 
		//correct applied Motion 
		object.addXpos(-motion.x);
		object.addYpos(-motion.y);
		return res; 
	}

	/**Eject object 1(moves) from object 2(fixed) by at most ejectDeplacement(deplacement of object2) and test if after that object 1 collide with the world. 
	 * To do so we simulate that object 1 did ejectDeplacement (already) */
	public static boolean ejectObjectCollision(AbstractModelPartie partie, Collidable object1, Collidable object2,Vector2d _ejectDeplacement)
	{
		boolean considerEffect = true;
		boolean touchingIsColliding = true;
		boolean computeWorld = false;
		boolean setColliInfo = true;
		boolean warnCollision = true;
		
		Point applyMotion = new Point();
		Collidable[] resCollidedObject = null;
		Vector2d closestEjectMaxDist = null;
		
		return ejectCollision(partie,object1,object2,_ejectDeplacement,considerEffect,touchingIsColliding,applyMotion,computeWorld,setColliInfo,warnCollision
				,resCollidedObject,closestEjectMaxDist);
	}

	/***
	 * 
	 * @param partie
	 * @param object1 
	 * @param object2
	 * @param considerEffects
	 * @param closestEjectMaxDist
	 * @return Eject the object from any colliding objects using the shortest distance (no direction taken into account). 
	 */
	public static boolean ejectFromCollision(AbstractModelPartie partie, Collidable object1, Collidable object2,boolean considerEffects,
			boolean touchingIsColliding, Vector2d closestEjectMaxDist){
		boolean computeWorld = true;
		boolean setColliInfo = false;
		boolean warnCollision = false;
		
		Point applyMotion = null; //set this to null will apply the motion and check that the object is not colliding with the world
		Vector2d _ejectDeplacement = null;
		Collidable[] resCollidedObject = null;
		
		return ejectCollision(partie,object1,object2,_ejectDeplacement,considerEffects,touchingIsColliding,applyMotion,computeWorld,setColliInfo,
				warnCollision,resCollidedObject,closestEjectMaxDist);
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
			boolean touchingIsColliding, Point appliedMotion,boolean computeWorld,boolean setColliInfo, boolean warnCollision,Collidable[] resCollidedObject,
			Vector2d closestEjectMaxDist)
	{
		boolean ejectClosest =closestEjectMaxDist != null;
		ModelPrincipal.debugTime.startElapsedForVerbose();
		
		boolean shouldApplyMotion = (appliedMotion ==null);

		Hitbox object1Hitbox = null;
		Hitbox object2Hitbox = null;

		Vector2d ejectDeplacement ;
		Vector2d minEjectDeplacement= new Vector2d();
		if(computeWorld)
		{
			if(ejectClosest)
				ejectDeplacement = new Vector2d(); //initialize to avoid the rest of the code to crash. This should not be usefull anyway
			else if(_ejectDeplacement != null)
				ejectDeplacement=_ejectDeplacement;
			else
			{
				Vitesse speed= object1.getGlobalVit();
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
		
		ModelPrincipal.debugTime.elapsed("Eject deplacement computed");
		minEjectDeplacement.negate(ejectDeplacement);

		Point desired_dep = PointHelper.RoundVecToPoint(ejectDeplacement);

		List<Collidable> mondeBlocs = null;

		//Vitesse speed= object.getGlobalVit(partie,object.type.equals(TypeObject.heros));


		boolean noIntersection = false;
		//distance which must move the object to avoid any collisions
		Point totalInterDist = new Point(0,0);


		//variables pour connaitre la direction d'intersection
		Collidable intersectedCol=null; // hitbox intersectée par l'objet mobile
		Point ejectFromCollisionPoint = null; //value to use to eject an object from the collision point

		Point intersectedCollision = null; //eject coordinate of collision 
		Vector2d EPA_normal = null; //normal du coté intersecté

		List<Collidable> collidableEffects = null;
		if(computeWorld && considerEffects)
			collidableEffects=Collidable.getAllCollidableEffectOnScreen(partie);
		
		ModelPrincipal.debugTime.elapsed("got all collidable effects");
		Point prevtotalInterDist = new Point(-1,-1);
		boolean xlimExceeded = false;
		boolean ylimExceeded = false;
		Point maxInterDist=null;

		if(object2!=null)
		{
			object2Hitbox=object2.getHitbox(partie.INIT_RECT, partie.getScreenDisp()).copy();
		}
		ModelPrincipal.debugTime.elapsed("got object 2 hitbox");
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

			object1Hitbox=  object1.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy().translate(p);
			ModelPrincipal.debugTime.elapsed("While: objectHitbox");
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
				if(object1.checkCollideWithWorld()){
					mondeBlocs = getMondeBlocs(partie.monde,object1Hitbox, partie.INIT_RECT,partie.getScreenDisp(),
							partie.TAILLE_BLOC);
					allCollidables.addAll(mondeBlocs);
					ModelPrincipal.debugTime.elapsed("While: get monde blocs");
				}

				if(collidableEffects!=null && object1.checkCollideWithEffect())
				{
					allCollidables.addAll(collidableEffects);
					allCollidables.remove(object1);
				}

			}

			for(Collidable col : allCollidables)
			{
				if(computeWorld)
					object2Hitbox = col.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();
				Vector2d supp1 = GJK_EPA.support(object2Hitbox.polygon,minEjectDeplacement );//fixed one
				Vector2d supp2 = GJK_EPA.support(object1Hitbox.polygon, ejectDeplacement);//mobile one
				Vector2d firstDir = new Vector2d(supp1.x-supp2.x, supp1.y-supp2.y);

				List<Vector2d> simplex = GJK_EPA.intersects(object2Hitbox.polygon,object1Hitbox.polygon ,firstDir);
				ModelPrincipal.debugTime.elapsed("While/AllColli: gjk intersects");
				List<Vector2d> normals = new ArrayList<Vector2d>();

				int collision_type=-1;
				boolean dNull= true;
				Double dInter=0.0d;

				if(simplex!=null)
				{
					dNull=false;
					if(ejectClosest || (Math.abs(minEjectDeplacement.x) == 0 && Math.abs(minEjectDeplacement.y)==0 ) )
						dInter = GJK_EPA.EPA(object2Hitbox.polygon,object1Hitbox.polygon, simplex, normals);
					else
						dInter= GJK_EPA.directionalEPA(object2Hitbox.polygon,object1Hitbox.polygon, simplex, new Vector2d(minEjectDeplacement.x,minEjectDeplacement.y), normals);
					
					ModelPrincipal.debugTime.elapsed("While/AllColli: epa");
				}

				collision_type =  GJK_EPA.isIntersect(dInter,dNull);

				boolean inTouch= collision_type == GJK_EPA.TOUCH && touchingIsColliding;
				boolean inCollision = collision_type==GJK_EPA.INTER; 
				
				if(inTouch || inCollision)
				{
					//Object is stuck in environment. Note: for eject closest, ejectDeplacement is set to (0,0) by default 
					if(ejectDeplacement.x==0 && ejectDeplacement.y==0 && !ejectClosest){
						return false; //stuck
					}

					Vector2d vectOut = new Vector2d(minEjectDeplacement.x,minEjectDeplacement.y);
					//the closest direction to eject is the normal of the edge containing the collision point (computed in EPA)
					if(ejectClosest)
						vectOut = new Vector2d(normals.get(0)); //WARNING: do not change that as it is tied to the GJK_EPA getIntersectInformation function
					vectOut.normalize();
					vectOut.scale(dInter);

					//avoid that the ejected object is exactly on the collided object
					Point correctEject = new Point();
					if(touchingIsColliding){
						//In the case of eject closest, ejectDeplacement is (0,0). The true eject direction is actually the normal (vectOut here)
						//This means that the movement direction is actually the opposite of the normal
						Vector2d mouvementDirection = ejectClosest ?new Vector2d(-vectOut.x,-vectOut.y) : ejectDeplacement;
						correctEject = ejectFromTouch(mouvementDirection, vectOut);
					}
					ModelPrincipal.debugTime.elapsed("While/AllColli: eject from touch");
					Vector2d vectOut_corrected= new Vector2d(Math.floor(round(vectOut.x))+correctEject.x,Math.floor(round(vectOut.y))+correctEject.y);
					//Only remember the biggest vect out 
					if((Math.abs(maxInterDist.x))<Math.abs((int)vectOut_corrected.x) || 
							((Math.abs(maxInterDist.y))<Math.abs((int)vectOut_corrected.y) ))
					{
						if(normals.size()>0)
						{
							EPA_normal=normals.get(0);
						}
						if(computeWorld && setColliInfo)
						{
							intersectedCol=col;
							Point[] res = computeCollisionPoint(new Vector2d(ejectDeplacement.x,ejectDeplacement.y),dInter,object1Hitbox,object2Hitbox);
							try{intersectedCollision= res[0];}
							catch(Exception e){
								e.printStackTrace(); 
								//Recompute collision point with log for debugging purpose
								computeCollisionPoint(new Vector2d(ejectDeplacement.x,ejectDeplacement.y),dInter,object1Hitbox,object2Hitbox,true);
							}
							Vector2d projectedEPA = GJK_EPA.projectVectorTo90(EPA_normal,false,0);
							ejectFromCollisionPoint=PointHelper.VecToPoint(projectedEPA);
							ModelPrincipal.debugTime.elapsed("While/AllColli: compute collision point");
						}
						maxInterDist.x =(int)vectOut_corrected.x;
						maxInterDist.y =(int)vectOut_corrected.y;
					}
					noIntersection=false;
				}

			}
			ModelPrincipal.debugTime.elapsed("While: allColli ended");
			totalInterDist.x += maxInterDist.x;
			totalInterDist.y += maxInterDist.y;

			if(ejectClosest){
				xlimExceeded = (Math.abs(totalInterDist.x)> Math.abs(closestEjectMaxDist.x));
				ylimExceeded = (Math.abs(totalInterDist.y)> Math.abs(closestEjectMaxDist.y));
			}
			else{
				xlimExceeded = (Math.abs(totalInterDist.x)> Math.abs(desired_dep.x));
				ylimExceeded = (Math.abs(totalInterDist.y)> Math.abs(desired_dep.y));
			}

		}
		ModelPrincipal.debugTime.elapsed("While ended");
		int final_x_dep = xlimExceeded ? 0 : (desired_dep.x+totalInterDist.x);
		int final_y_dep = ylimExceeded ? 0 : (desired_dep.y+totalInterDist.y);	
		
		if(shouldApplyMotion){
			object1.addXpos_sync(final_x_dep);
			object1.addYpos_sync(final_y_dep);
		}
		else{
			appliedMotion.setLocation(new Point(xlimExceeded ?0 : (totalInterDist.x) ,
					ylimExceeded ? 0 : (totalInterDist.y)));
		}


		if(computeWorld && setColliInfo)
		{
			if(intersectedCol!=null)
			{
				object1.setCollisionInformation(EPA_normal, intersectedCollision,  ejectFromCollisionPoint);
				if(warnCollision && resCollidedObject!=null){
					resCollidedObject[0]=intersectedCol;
				}
			}
			else
				object1.setCollisionInformation(null, null, null);//erase previous information to avoid misleading information
				
		}
		ModelPrincipal.debugTime.elapsed("Collision information");
		
		if(shouldApplyMotion && isWorldCollision(partie, object1,touchingIsColliding)){
			ModelPrincipal.debugTime.elapsed("Check world collision");
			object1.addXpos_sync(-final_x_dep);
			object1.addYpos_sync(-final_y_dep);
			return false; // object is stuck
		}
		ModelPrincipal.debugTime.elapsed("Check world collision 2");
		if(computeWorld && (intersectedCol!=null) && warnCollision)
			object1.handleWorldCollision(EPA_normal,partie,intersectedCol,false);
		
		ModelPrincipal.debugTime.elapsed("Handle world collision");
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
		return collisionObjects(partie, null,null,objectHitbox1,objectHitbox2,false,false);
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
			Vector2d deltaSpeed = new Vector2d(object1.getGlobalVit().x-object2.getGlobalVit().x,object1.getGlobalVit().y-object2.getGlobalVit().y);
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
			GJK_EPA.directionalEPA(objectHitbox1.polygon, objectHitbox2.polygon, simplex, firstDir, normals);
			

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
			allCollidables.addAll(Collidable.getAllCollidableEffectOnScreen(partie));
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

			if(simplex!=null)
				colliders.add(col);
		}

		return colliders;
	}
}
