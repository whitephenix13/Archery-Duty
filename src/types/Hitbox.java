package types;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

public class Hitbox {

	public Polygon polygon;
	//De combien la hitbox a tournée depuis sa position d'origine, angle orienté sens direct
	public float angle;

	public Hitbox()
	{
		polygon= new Polygon();
	}
	public Hitbox(Point p1, Point p2, Point p3, Point p4)
	{
		polygon= new Polygon();
		polygon.addPoint(p1.x, p1.y);
		polygon.addPoint(p2.x, p2.y);
		polygon.addPoint(p3.x, p3.y);
		polygon.addPoint(p4.x, p4.y);

	}
	public Hitbox(Polygon _p )
	{
		polygon=_p;
	}

	public String toString()
	{
		String s="";
		for(int i=0; i<polygon.npoints; ++i)
		{
			Polygon poly = polygon;
			Point p = new Point(poly.xpoints[i],poly.ypoints[i]);
			s+=(i==0? "" : " ")+"("+p.x+","+p.y+")";
		}		
		return s;
	}

	public static Vector2d supportPoint(Vector2d dir, Polygon poly)
	{
		Vector2d res= new Vector2d(poly.xpoints[0],poly.ypoints[0]);
		double res_value = dir.dot(res);
		boolean isGreater=false;
		for(int n=1; n<poly.npoints; ++n)
		{
			Vector2d vect = new Vector2d(poly.xpoints[n],poly.ypoints[n]);
			double dot= dir.dot(vect);

			//for a polygon, once you go through all its vertices, the value of the dot product with a direction
			//increase until you reach the support point and then decrease.
			if(dot<=res_value)
				if(isGreater)//we were increasing: the previous point was the support point
					break;
				else
					continue;
			
			res_value=dot;
			res=vect;
			isGreater=true;
		}
		return res;
	}
	
	public static List<Vector2d> supportsPoint(Vector2d dir, Polygon poly)
	{
		List<Vector2d> res= new ArrayList<Vector2d>();
		Vector2d first_vect = new Vector2d(poly.xpoints[0],poly.ypoints[0]);
		res.add(first_vect);
		double last_mem_value = dir.dot(first_vect);
		boolean isGreater=false;
		for(int n=1; n<poly.npoints; ++n)
		{
			Vector2d vect = new Vector2d(poly.xpoints[n],poly.ypoints[n]);
			double dot= dir.dot(vect);

			//for a polygon, once you go through all its vertices, the value of the dot product with a direction
			//increase until you reach the support point and then decrease.
			if(dot<last_mem_value)
				if(isGreater)//we were increasing: the previous point was the support point
					break;
				else
					continue;
			//if dot==last_mem_value we may have several max value, otherwise, the previous memorised values were wrong
			if(dot>last_mem_value)
				res= new ArrayList<Vector2d>();
			
			last_mem_value=dot;
			res.add(vect);
			isGreater=true;
		}
		
		return res;
	}
	public static Hitbox rotateHitbox(Hitbox hit, int angle)
	{
		assert (hit.polygon.npoints==4);
		Hitbox res = new Hitbox();
		Point center = getHitboxCenter(hit);

		for(int i=0; i<hit.polygon.npoints; ++i)
		{
			Polygon poly = hit.polygon;
			Point p = rotatePoint(new Point(poly.xpoints[i],poly.ypoints[i]),center,angle);
			res.polygon.addPoint(p.x,p.y);
		}		
		return(res);
	}

	private static Point rotatePoint(Point point,Point centre, int angle)
	{
		//
		//en posant X'= X-xc et Y'= Y-yc, on a (translation au centre) 
		//X'= l * cos (alpha + 45 ) 
		//Y'= l* sin (alpha + 45) 
		//
		//donc alpha = atan (Y'/X') -45 
		//111111111111111111
		//	l= X'/cos (alpha + 45 )
		//
		//finalement x'= l* cos (alpha)
		//			y'= l * sin (alpha) 
		//
		//on obtient ainsi x et y 
		// 
		///
		Point res = new Point();
		float alpha = (float) (Math.atan((float)(point.y-centre.y)/(point.x-centre.x))- Math.toRadians(angle));
		float l = (float) ((float)(point.x-centre.x)/(Math.cos(alpha + Math.toRadians(angle) )));

		res.x= (int) (centre.x + l * Math.cos (alpha));
		res.y= (int) (centre.y + l * Math.sin (alpha));
		return(res);
	}

	private static Point getHitboxCenter(Hitbox hitbox )
	{
		assert (hitbox.polygon.npoints==4);
		///on trace les deux diagonales, l'intersection des deux droites donnent le centre
		// Les droites sont de la forme ( a(x-x1) +y1  )
		Polygon poly = hitbox.polygon;
		Point p0 = new Point(poly.xpoints[0],poly.ypoints[0]);
		Point p1 = new Point(poly.xpoints[1],poly.ypoints[1]);
		Point p2 = new Point(poly.xpoints[2],poly.ypoints[2]);
		Point p3 = new Point(poly.xpoints[3],poly.ypoints[3]);

		float a1= (float)(p0.y-p2.y)/(p0.x-p2.x);
		float a2= (float)(p1.y-p3.y)/(p1.x-p3.x);
		Point res = new Point();
		res.x= (int)((float)(a1*p0.x-a2*p1.x+p1.y-p0.y)/(a1-a2));
		res.y=(int)(a1*(res.x-p0.x)+p0.y);

		return(res);
	}

	public static Hitbox minusPoint(Hitbox hit, Point p, boolean copy)
	{
		Hitbox copyHit = null;
		if(copy)
			copyHit= new Hitbox();
		for(int i=0; i<hit.polygon.npoints;i++)
		{
			if(copy)
			{
				copyHit.polygon.addPoint(hit.polygon.xpoints[i]-p.x, hit.polygon.ypoints[i]-p.y);
			}
			else
			{
				hit.polygon.xpoints[i]-= p.x;
				hit.polygon.ypoints[i]-= p.y;
			}
		}
		return copy? copyHit: hit;
	}
	
	public static Hitbox plusPoint(Hitbox hit, Point p, boolean copy)
	{
		Point p2 = new Point(-p.x,-p.y);
		return minusPoint(hit,p2,copy);
	}

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
		Polygon poly= hit.polygon;
		assert poly.npoints>0;
		float t = projectPointOnAxe(new Point(poly.xpoints[0],poly.ypoints[0]),axe);
		Point_float res = new Point_float(t,t); //res = {tmin, tmax } avec t parametre de la droite définie par l'axe 

		//test for each points 
		for(int i=0; i<(poly.npoints-1); ++i)
		{
			t= projectPointOnAxe(new Point(poly.xpoints[i],poly.ypoints[i]),axe);
			//memorise min
			if(res.x>t)
				res.x=t;
			//memorise max 
			if(res.y<t)
				res.y=t;
		}
		return res;
	}

	public static boolean isPointInRectangle (Point test, Hitbox box)
	{
		Polygon poly= box.polygon;
		assert (poly.npoints==4);

		Point p0 = new Point(poly.xpoints[0],poly.ypoints[0]);
		Point p1 = new Point(poly.xpoints[1],poly.ypoints[1]);
		Point p2 = new Point(poly.xpoints[2],poly.ypoints[2]);

		Point axe1 = new Point (p0.x-p1.x, p0.y-p1.y);
		Point axe2 = new Point (p1.x-p2.x, p1.y-p2.y);

		boolean inRect;
		Point_float tBox = extremumParametersOnAxe(box,axe1);
		float tTest = projectPointOnAxe(test,axe1);

		inRect = (tBox.x<=tTest && tTest<tBox.y );

		tBox = extremumParametersOnAxe(box,axe2);
		tTest= projectPointOnAxe(test,axe2);

		inRect = inRect && (tBox.x<=tTest && tTest<tBox.y );

		return inRect;
	}

	/*private boolean pEquals(Point p1, Point p2)
	{
		if(p1.x==p2.x && p1.y==p2.y)
			return true;
		else
			return false;
	}
	private boolean pInList(Point p, List<Point> l)
	{
		int i=l.size();
		for (Point p0 : l)
		{
			if(pEquals(p,p0))
				break;
			i--;
		}

		if(i==0)
			return false;
		else
			return true;
	}

	private Point[] getAdjacentsPoints(Point p)
	{
		Point[] res= new Point[2];
		if(pEquals(hg,p) || pEquals(bd,p))
		{
			res[0]=bg; 
			res[1]=hd;
		}
		else if(pEquals(bg,p)||pEquals(hd,p))
		{
			res[0]=hg; 
			res[1]=bd;
		}
		else
		{
			String error = "Erreur Hitbox\\getAdjacentsPoints : le point n'appartient pas à la hitbox";
			try {throw new Exception(error);} 
			catch (Exception e) {e.printStackTrace();}
		}

		return res;
	}
	private Point getOtherHitboxPoint(List<Point> l)
	{
		if(!pInList(hg,l))
			return hg;
		if(!pInList(bg,l))
			return bg;
		if(!pInList(bd,l))
			return bd;
		if(!pInList(hd,l))
			return hd;
		return null;

	}
	private Point getNormal(Point p1, Point p2)
	{
		//un vecteur normal à {a,b} est {-b,a}
		Point normal = new Point();
		normal.x = p2.y-p1.y;
		normal.y= p1.x-p2.x;
		return normal;
	}
	public Point getNormal(List<Point> intersectedSegment)
	{
		Point normal = null;

		if(intersectedSegment.size()==2)
		{
			normal= getNormal(intersectedSegment.get(0),intersectedSegment.get(1));
			//On oriente correctement la normale en calculant un produit scalaire avec un vecteur formé d'un point n'appartenant pas
			//à la liste et d'un point appartenant à la liste. res>0 : bien orienté.
			Point other = getOtherHitboxPoint(intersectedSegment);

			Point vect = intersectedSegment.get(0);
			vect.x-= other.x;
			vect.y-=other.y;

			boolean reverse=( (normal.x*vect.x + normal.y*vect.y) < 0);
			normal.x*= (reverse?-1:1);
			normal.y*= (reverse?-1:1);
		}
		else if (intersectedSegment.size()==1)
		{
			//on calcul les deux normales d'à côté
			Point[] adjacentsPoints = getAdjacentsPoints(intersectedSegment.get(0));

			Point normal0 = getNormal(intersectedSegment.get(0),adjacentsPoints[0]);
			Point normal1 = getNormal(intersectedSegment.get(0),adjacentsPoints[1]);

			Point vect0 = intersectedSegment.get(0);
			vect0.x-=adjacentsPoints[1].x;
			vect0.y-=adjacentsPoints[1].y;

			Point vect1 = intersectedSegment.get(0);
			vect1.x-=adjacentsPoints[0].x;
			vect1.y-=adjacentsPoints[0].y;

			boolean reverse = ( (normal0.x*vect0.x + normal0.y*vect0.y) < 0);
			normal0.x*= (reverse?-1:1);
			normal0.y*= (reverse?-1:1);

			reverse = ( (normal1.x*vect1.x + normal1.y*vect1.y) < 0);
			normal1.x*= (reverse?-1:1);
			normal1.y*= (reverse?-1:1);

			//la normale finale correspond à la moyenne de ces deux normales
			normal= normal0;
			normal.x+=normal1.x;
			normal.y+=normal1.y;
		}
		else
		{
			String error = "Erreur Hitbox\\getNormal : taille de laliste incorrecte";
			try {throw new Exception(error);} 
			catch (Exception e) {e.printStackTrace();}
		}
		return normal;

	}*/
}
