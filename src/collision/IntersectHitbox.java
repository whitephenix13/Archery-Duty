package collision;

import java.awt.Point;
import java.util.List;

import types.Hitbox;
import types.Point_float;

public class IntersectHitbox { //separating axis test

	//public static boolean hitboxIntersect(Hitbox hit1, int angle1,Hitbox hit2,int angle2){return(false);}

	/*
	static float projectPointOnAxe(Point point, Point axe)
	{
		
		 // D : {x=tu, y=tv} avec (u,v) le vecteur directeur 
		 // {Px-x,Py-y} . {u,v} = 0 avec point={Px,Py} et P={x,y} le point recherché 
		 // Comme P appartient à D, on a aussi x=tu et y= tv, on cherche alors t
		 
		float t = ((float)(point.x*axe.x+point.y*axe.y))/(axe.x*axe.x+axe.y*axe.y);
		return(t);	
	}

	static Point_float extremumParametersOnAxe(Hitbox hit, Point axe)
	{
		float t = projectPointOnAxe(hit.hg,axe);
		Point_float res = new Point_float(t,t); //res = {tmin, tmax } avec t parametre de la droite définie par l'axe 

		//test for each points 

		t= projectPointOnAxe(hit.bg,axe);
		//memorise min
		if(res.x>t)
			res.x=t;
		//memorise max 
		if(res.y<t)
			res.y=t;

		t= projectPointOnAxe(hit.bd,axe);
		//memorise min
		if(res.x>t)
			res.x=t;
		//memorise max 
		if(res.y<t)
			res.y=t;

		t= projectPointOnAxe(hit.hd,axe);
		//memorise min
		if(res.x>t)
			res.x=t;
		//memorise max 
		if(res.y<t)
			res.y=t;

		return res;
	}
	
	public static boolean isPointInRectangle (Point test, Hitbox box)
	{
		Point axe1 = new Point (box.hg.x-box.bg.x, box.hg.y-box.bg.y);
		Point axe2 = new Point (box.bg.x-box.bd.x, box.bg.y-box.bd.y);

		boolean inRect;
		Point_float tBox = extremumParametersOnAxe(box,axe1);
		float tTest = projectPointOnAxe(test,axe1);
		
		inRect = (tBox.x<=tTest && tTest<tBox.y );
		
		tBox = extremumParametersOnAxe(box,axe2);
		tTest= projectPointOnAxe(test,axe2);
		
		inRect = inRect && (tBox.x<=tTest && tTest<tBox.y );
		
		return inRect;
	}
	
	static boolean overlapSegment(Point_float s1, Point_float s2)
	{
		//Vu que les images sont dessiné à partir du coin haut gauche, 
		//si les valeurs minimales sur x sont égales, on a visuellement une collision
		if(s1.x<s2.x)
			return (s2.x<=s1.y);
		else
			return((s1.x<=s2.y));
	}

	public static boolean hitboxIntersect(Hitbox hit1,
			Hitbox hit2)
	{
		//the axis to test for separating axis test are (a1,b1) (b1,c1) if orthogonal, and (a2,b2) (b2,c2)

		//TEST FOR (hit1.hg,hit1.bg) axis
		Point axe= new Point(hit1.bg.x-hit1.hg.x,hit1.bg.y-hit1.hg.y);
		Point_float s1 = extremumParametersOnAxe(hit1,axe); 
		Point_float s2 = extremumParametersOnAxe(hit2,axe); 
		if(!overlapSegment(s1,s2)) //we found a separating axis
			return false ; 

		//TEST FOR (hit1.bg,hit1.bd) axis
		axe= new Point(hit1.bd.x-hit1.bg.x,hit1.bd.y-hit1.bg.y);
		s1 = extremumParametersOnAxe(hit1,axe); 
		s2 = extremumParametersOnAxe(hit2,axe); 
		if(!overlapSegment(s1,s2)) //we found a separating axis
			return false ; 

		//TEST FOR (hit2.hg,hit2.bg) axis
		axe= new Point(hit2.bg.x-hit2.hg.x,hit2.bg.y-hit2.hg.y);
		s1 = extremumParametersOnAxe(hit1,axe); 
		s2 = extremumParametersOnAxe(hit2,axe); 
		if(!overlapSegment(s1,s2)) //we found a separating axis
			return false ; 

		//TEST FOR (hit2.bg,hit2.bd) axis
		axe= new Point(hit2.bd.x-hit2.bg.x,hit2.bd.y-hit2.bg.y);
		s1 = extremumParametersOnAxe(hit1,axe); 
		s2 = extremumParametersOnAxe(hit2,axe); 
		if(!overlapSegment(s1,s2)) //we found a separating axis
			return false ; 
 
		return true; //found no separating axes 
	}

	public static boolean hitboxIntersect(Hitbox hit1, int angle1,Hitbox hit2,int angle2)
	{
		//Methods are below 

		//rotate rectangle if necessary 
		if(angle1!= 0 && angle1 != 180)
			hit1 = Hitbox.rotateHitbox(hit1, angle1);
		

		if(angle2!= 0 && angle2 != 180)
			hit2 = Hitbox.rotateHitbox(hit2, angle2);

		
		return hitboxIntersect(hit1,hit2);
	}

	private static float getMinTStatic(float tStaticMin, float tStatic,List<Point> intersectedSegment, Point vit )
	{
		if(tStatic<tStaticMin)
		{
			tStaticMin=tStatic;
			intersectedSegment.clear();
			intersectedSegment.add(new Point((int)tStaticMin*vit.x,(int)tStaticMin*vit.y));
		}
		else if(tStatic==tStaticMin)
		{
			intersectedSegment.add(new Point((int)tStaticMin*vit.x,(int)tStaticMin*vit.y));

		}
		return tStaticMin ;
	}
	
/*	public static Point distanceOutHitboxes(Point vit,Hitbox mobileHit, Hitbox staticHit, List<Point> lPoints)
	{
		//lPoints : liste des points formant le segment de l'object static intersecté par l'objet mobile.
		
		//To calculate the interpenetration distance, we project the extremum points of each hitboxes on the speed vector.
		//The interpenetration distance is then obtained for the difference between the point that has the maximum 
		//parameter for the mobileHitbox et the point that has the minimum parameter for the other. 

		float tMobileMax = projectPointOnAxe(mobileHit.hg,vit);
		tMobileMax=Math.max(tMobileMax, projectPointOnAxe(mobileHit.bg,vit));
		tMobileMax=Math.max(tMobileMax, projectPointOnAxe(mobileHit.bd,vit));
		tMobileMax=Math.max(tMobileMax, projectPointOnAxe(mobileHit.hd,vit));
		
		float tStaticMin = projectPointOnAxe(mobileHit.hg,vit);
		tStaticMin=getMinTStatic(tStaticMin,projectPointOnAxe(mobileHit.bg,vit),lPoints,vit );
		tStaticMin=getMinTStatic(tStaticMin,projectPointOnAxe(mobileHit.bd,vit),lPoints,vit );
		tStaticMin=getMinTStatic(tStaticMin,projectPointOnAxe(mobileHit.hd,vit),lPoints,vit );

		//convert t from point by using {x=tu, y=tv} with (u,v) steering vector 
		Point pMobileMax= new Point((int)tMobileMax*vit.x,(int)tMobileMax*vit.y) ;
		Point pStaticMin= new Point((int)tStaticMin*vit.x,(int)tStaticMin*vit.y) ;
		//distance pour faire ressortir la hitbox d=pMobileMax-pStaticMin
		Point res= new Point(pMobileMax.x-pStaticMin.x,pMobileMax.y-pStaticMin.y);

		return res;
	}
	
	
	*/


}
