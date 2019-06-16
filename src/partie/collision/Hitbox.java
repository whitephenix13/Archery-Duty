package partie.collision;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import partie.deplacement.Deplace;
import partie.modelPartie.AbstractModelPartie;
import utils.PointHelper;

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
		if(p4 != null)
			polygon.addPoint(p4.x, p4.y);

	}
	public Hitbox(Polygon _p )
	{
		polygon=_p;
	}

	public Hitbox copy()
	{
		Hitbox copy = new Hitbox();
		for(int i=0; i< this.polygon.npoints; i++)
		{
			copy.polygon.addPoint(this.polygon.xpoints[i], this.polygon.ypoints[i]);
		}
		return copy;
	}

	/**Create square hitboxes based on the list*/
	public static List<Hitbox> createQuadriHitboxes(List<Point> p1, List<Point> p2, List<Point> p3, List<Point> p4)
	{
		List<Hitbox> hitboxes = new ArrayList<Hitbox>();
		for(int i=0;i<p1.size(); ++i)
			hitboxes.add(new Hitbox(p1.get(i),p2.get(i),p3.get(i),p4.get(i)));
		return hitboxes;
	}
	/** Create a square hitbox whose bounds are xmin,xmax,ymin,ymax*/
	public static Hitbox createSquareHitbox(int xmin, int ymin, int xmax, int ymax)
	{
		return new Hitbox(new Point(xmin,ymin),new Point(xmax,ymin),new Point(xmax,ymax),new Point(xmin,ymax));
	}
	/**Create "nb" square hitboxes all identical*/
	public static List<Hitbox> createSquareHitboxes(int xmin, int ymin, int xmax, int ymax,int nb)
	{
		List<Hitbox> hitboxes = new ArrayList<Hitbox>();
		for(int i=0;i<nb; ++i)
			hitboxes.add(createSquareHitbox(xmin, ymin, xmax, ymax));
		return hitboxes;
	}
	/**Create square hitboxes based on the list*/
	public static List<Hitbox> createSquareHitboxes(List<Integer> xmin, List<Integer> ymin, List<Integer> xmax, List<Integer> ymax)
	{
		List<Hitbox> hitboxes = new ArrayList<Hitbox>();
		for(int i=0;i<xmin.size(); ++i)
			hitboxes.add(createSquareHitbox(xmin.get(i), ymin.get(i), xmax.get(i), ymax.get(i)));
		return hitboxes;
	}
	public static List<Point> asListPoint(List<Integer> x, List<Integer> y)
	{
		List<Point> l = new ArrayList<Point>();
		for(int i = 0 ; i<x.size(); ++i)
		{
			l.add(new Point(x.get(i),y.get(i)));
		}
		return l;
	}
	/**
	 * 
	 * @param list [A B C D] where A : list of an edge pos (x,y) depending on the anim A[anim] list[edge][anim]
	 * @return list [A B] where list[anim] return a hitbox A
	 */
	public static List<Hitbox> createHitbox(List<List<Point>> list)
	{
		List<Hitbox> hitboxes = new ArrayList<Hitbox>();
		//Generate one hitbox per animation
		for(int anim=0; anim<list.get(0).size(); ++anim)
		{
			hitboxes.add(new Hitbox());	
		}

		for(int edge=0; edge<list.size(); ++edge)
		{
			List<Point> edgelist= list.get(edge);
			for(int anim=0; anim<edgelist.size(); ++anim)
			{
				Point point = edgelist.get(anim);
				hitboxes.get(anim).polygon.addPoint(point.x,point.y);
			}
		}
		return hitboxes;
	}
	public static String polyToString(Polygon poly)
	{
		String s="";
		for(int i=0; i<poly.npoints; ++i)
		{
			Point p = new Point(poly.xpoints[i],poly.ypoints[i]);
			s+=(i==0? "" : " ")+"("+p.x+","+p.y+")";
		}		
		return s;
	}
	public static String simplexToString(List<Vector2d> simplex)
	{
		String s="";
		for(int i=0; i<simplex.size(); ++i)
		{
			Point p = PointHelper.VecToPoint(simplex.get(i));
			s+=(i==0? "" : " ")+"("+p.x+","+p.y+")";
		}		
		return s;
	}
	public String toString()
	{
		return polyToString(polygon);
	}
	
	public static Hitbox translate(Hitbox hit, Point transl)
	{
		Polygon polygon = new Polygon();
		for(int i=0; i< hit.polygon.npoints; ++i)
		{
			polygon.addPoint(hit.polygon.xpoints[i]+transl.x, hit.polygon.ypoints[i]+transl.y);
		}
		return new Hitbox(polygon);
	}
	
	/***
	 * Extend the hitbox in a symetric way. Ie: hitbox 0,0 1,0 1,1 0,1 extended by 1,2 gives : -1 -2 , 2,-2  2,3 -1,3
	 * Only works on unrotated square hitbox 
	 * @param hit
	 * @param extension
	 * @return
	 */
	public static Hitbox extend(Hitbox unrotated_hit, Point extension)
	{
		Point mins = new Point(unrotated_hit.polygon.xpoints[0],unrotated_hit.polygon.ypoints[0]);
		Point maxs = new Point(unrotated_hit.polygon.xpoints[0],unrotated_hit.polygon.ypoints[0]);
		for(int i=0; i< unrotated_hit.polygon.npoints; ++i)
		{
			int x = unrotated_hit.polygon.xpoints[i];
			int y = unrotated_hit.polygon.ypoints[i];
			if(x>maxs.x)
				maxs.x=x;
			else if(x<mins.x)
				mins.x=x;
			if(y>maxs.y)
				maxs.y=y;
			else if(y<mins.y)
				mins.y=y;
		}
		return Hitbox.createSquareHitbox(mins.x-extension.x, mins.y-extension.y, maxs.x+extension.x, maxs.y+extension.y);
	}
	public static Vector2d supportPoint(Vector2d dir, Polygon poly)
	{
		Vector2d res= new Vector2d(poly.xpoints[0],poly.ypoints[0]);
		double res_value = dir.dot(res);
		for(int n=1; n<poly.npoints; ++n)
		{
			Vector2d vect = new Vector2d(poly.xpoints[n],poly.ypoints[n]);
			double dot= dir.dot(vect);
			//for a polygon, once you go through all its vertices, the value of the dot product with a direction
			//increase until you reach the support point and then decrease.
			if(dot>res_value)
			{
				res_value=dot;
				res=vect;
			}
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
	public static Hitbox convertHitbox(Hitbox current, Point INIT_RECT,AffineTransform tr) {

		Polygon current_pol = current.polygon; 
		Polygon new_pol = new Polygon();
		for(int j = 0; j<current_pol.npoints; ++j)
		{
			Point2D temp = tr.transform(new Point(current_pol.xpoints[j],current_pol.ypoints[j]), null);
			new_pol.addPoint((int)Math.round(temp.getX()),(int)Math.round(temp.getY()));

		}
		return new Hitbox(new_pol);

	}
	public static List<Hitbox> convertHitbox(List<Hitbox> current, Point INIT_RECT,AffineTransform tr) {
		List<Hitbox> new_rotated_hit = new ArrayList<Hitbox>();

		for (int i = 0; i<current.size(); ++i)
		{
			new_rotated_hit.add(convertHitbox(current.get(i),INIT_RECT,tr));
		}

		return new_rotated_hit;
	}
	
	/**
	 * Apply transform to the polygons and untranslate it with respect to position and screendisp (to get a coordinates in local scale)
	 * @param current: the hitbox to tranform
	 * @param tr: the transform to apply
	 * @param pos: the position to remove to have local coordinates (if current were in global coordinates)
	 * @param screendisp: the screen displacement to remove to have local coordinates (if current were in global coordinates with screen displacement)
	 * @return
	 */
	public static Hitbox convertHitbox(Hitbox current, AffineTransform tr,Point pos,Point screendisp) {

		Polygon current_pol = current.polygon; 
		Polygon new_pol = new Polygon();
		for(int j = 0; j<current_pol.npoints; ++j)
		{
			Point2D temp = tr.transform(new Point(current_pol.xpoints[j],current_pol.ypoints[j]), null);
			new_pol.addPoint((int)Math.round(temp.getX())-pos.x-screendisp.x,(int)Math.round(temp.getY())-pos.y-screendisp.y);
		}
		return new Hitbox(new_pol);

	}
	
	/**
	 * Apply transform to the polygons and untranslate it with respect to position and screendisp (to get a coordinates in local scale)
	 * @param current: the hitboxes to tranform
	 * @param tr: the transform to apply
	 * @param pos: the position to remove to have local coordinates (if current were in global coordinates)
	 * @param screendisp: the screen displacement to remove to have local coordinates (if current were in global coordinates with screen displacement)
	 * @return
	 */
	public static List<Hitbox> convertHitbox(List<Hitbox> current, AffineTransform tr,Point pos,Point screendisp) {
		List<Hitbox> new_rotated_hit = new ArrayList<Hitbox>();

		for (int i = 0; i<current.size(); ++i)
		{
			new_rotated_hit.add(convertHitbox(current.get(i),tr,pos,screendisp));
		}

		return new_rotated_hit;
	}

	/***
	 * Rotate the hitbox by the given angle (in rad) where the angle is given by Deplace.XYtoAngle
	 * @param hit
	 * @param angle
	 * @return
	 */
	public static Hitbox rotateHitbox(Hitbox hit, double rad_angle)
	{
		assert (hit.polygon.npoints==4);
		Hitbox res = new Hitbox();
		Vector2d center = getHitboxCenter(hit);
		for(int i=0; i<hit.polygon.npoints; ++i)
		{
			Polygon poly = hit.polygon;
			Vector2d p = rotatePoint(new Vector2d(poly.xpoints[i],poly.ypoints[i]),center,rad_angle);
			res.polygon.addPoint((int)Math.round(p.x),(int)Math.round(p.y));
		}		
		return(res);
	}
	
	private static Vector2d rotatePoint(Vector2d point,Vector2d centre,double rad_angle)
	{
		//
		
		// center the hitbox to 0,0 (remove centre from the point) 
		//Exemple with angle = Pi/4 , Point - centre = (sqrt(2),sqrt(2)), after rotation, should be (0,4)
		//   point after rotation //  point 
		//     ^  ^    
		//     |/                 
		//     center 
		//let alpha be the angle between v_point and (1,0) and beta the angle between v_point_rotation and (1,0)
		//We have tan(alpha) = point.y / point.x
		//and cos(alpha + rad_angle) = point_rotation.x / point.length
		//and sin(alpha + rad_angle) = point_rotation.y / point.length
		
		Vector2d res = new Vector2d();
		
		Vector2d point_centered = new Vector2d(point.x-centre.x,point.y-centre.y);
		double point_length = point_centered.length();
		double alpha = Deplace.XYtoAngle(point_centered.x, point_centered.y);
		
		res.x = centre.x + point_length * Math.cos(alpha + rad_angle);
		res.y = centre.y + point_length * Math.sin(alpha + rad_angle);
		return(res);
	}
	
	/**
	 * Get the area (hitbox) generated when the object moves from point A to point B with a given rotation
	 * Considers that the center of the hitbox is at point A and the center of the moved hitbox is at point B  
	 */
	//Function tested in A_Star_test
	public static Hitbox getSlidedHitbox(Hitbox unrotated_hitbox, Point origin, Point destination)
	{	
		Vector2d v_hitboxMiddle = Hitbox.getHitboxCenter(unrotated_hitbox);
		Point hitboxMiddle= PointHelper.RountVecToPoint(v_hitboxMiddle);
		origin = new Point(origin.x-hitboxMiddle.x,origin.y-hitboxMiddle.y);//place the center at origin
		destination = new Point(destination.x-hitboxMiddle.x,destination.y-hitboxMiddle.y);//place the center at destination

		//origin is where the middle of the hitbox start
		Point deltaPos = new Point(destination.x-origin.x,destination.y-origin.y);
		double rad_angle = Deplace.XYtoAngle(deltaPos.x, deltaPos.y);
		Hitbox rotated_hit = Hitbox.rotateHitbox(unrotated_hitbox,rad_angle);

		//                          |               ^
		//   v(x,y) ->  v_90(-y,x)  V  v_270 (y,-x) | 
		
		Polygon origin_poly = Hitbox.rotateHitbox(Hitbox.translate(unrotated_hitbox, origin),rad_angle).polygon; 

		Vector2d origin_support_dir_90 = new Vector2d(-deltaPos.y- 0.01 * deltaPos.x, deltaPos.x- 0.01 * deltaPos.y); //see v_90 above, the 0.01 is to lean it to the back of the direction
		Vector2d origin_support_dir_270 = new Vector2d(deltaPos.y- 0.01 * deltaPos.x, -deltaPos.x- 0.01 * deltaPos.y); //see v_270 above, the 0.01 is to lean it to the back of the direction

		//based on the drawing below, we want from G to C hence from v_90 to v_270
		Vector2d origin_p1 = Hitbox.supportPoint(origin_support_dir_90, origin_poly);
		Vector2d origin_p2 = Hitbox.supportPoint(origin_support_dir_270, origin_poly);

		
		Polygon dest_poly = Hitbox.rotateHitbox(Hitbox.translate(unrotated_hitbox, destination),rad_angle).polygon; 

		Vector2d dest_support_dir_90 = new Vector2d(-deltaPos.y+ 0.01 * deltaPos.x, deltaPos.x+ 0.01 * deltaPos.y); //see v_90 above, the 0.01 is to lean it to the front of the direction
		Vector2d dest_support_dir_270 = new Vector2d(deltaPos.y+ 0.01 * deltaPos.x, -deltaPos.x+ 0.01 * deltaPos.y); //see v_270 above, the 0.01 is to lean it to the front of the direction

		//based on the drawing below, we want from C' to F' hence from v_270 to v_90
		Vector2d dest_p1 = Hitbox.supportPoint(dest_support_dir_270, dest_poly);
		Vector2d dest_p2 = Hitbox.supportPoint(dest_support_dir_90, dest_poly);
		
		
		//Note: polygons are the same but translated. Hence they have the same number of sides 
		/*
		 *   Hit origin               Hit dest 
		 *    B C D                    B' C' D'
		 *  A__/\__E       ->       A' __/\__ E'
		 * G|______|F               G'|______| F'
		 *  
		 *  Slided hit 
		 *     B C                        C' D'  
		 *      __________________________
		 *  A__/                          \__ E'
		 * G|________________________________| F' 
		 *  
		 *  Algo: take the highest point (direction perpendiculat to movement direction) a bit to the left (to handle draw) 
		 *  and the lowest point a bit to the left (left since the object moves to the right).
		 *  In that case: C and G. Add all point between G and C (included)
		 *  Do the same with the hitbox at destination (considering the points to the right this time)
		 *  In that case C' and F'. Add all points between C' and F' 
		 *  This should form the slided hitbox 
		 * */
		Polygon res = new Polygon();
		
		boolean origin_p1_found = false;
		boolean origin_p2_found = false;

		boolean dest_p1_found = false;
		boolean dest_p2_found = false;
		
		int i=0;
		while(!origin_p1_found || !origin_p2_found || !dest_p1_found ||!dest_p2_found)
		{
			Point origin_p = new Point(origin_poly.xpoints[i], origin_poly.ypoints[i]);
			Point dest_p = new Point(dest_poly.xpoints[i], dest_poly.ypoints[i]);
			
			//Look for the first point of the polygon
			if((origin_p.x == origin_p1.x) && (origin_p.y == origin_p1.y))
			{
				origin_p1_found = true;
			}
			
			if(origin_p1_found){
				//Add all points between origin_p1 and origin_p2
				if(!origin_p2_found){
					res.addPoint(origin_p.x,origin_p.y);
					if((origin_p.x == origin_p2.x) && (origin_p.y == origin_p2.y))
						origin_p2_found = true;
				}
				//Add points from destination polygon
				else
				{
					//look for first point to add from destination
					if((dest_p.x == dest_p1.x) && (dest_p.y == dest_p1.y))
					{
						dest_p1_found = true;
					}
					//If first point to add is found, add all points until second is found 
					if(dest_p1_found)
					{
						if(!dest_p2_found)
						{
							res.addPoint(dest_p.x,dest_p.y);
							if((dest_p.x == dest_p2.x) && (dest_p.y == dest_p2.y))
								dest_p2_found = true;
						}
					}
				}
			}
			
			i+=1;
			if(i>=origin_poly.npoints)
				i=0;
		}
		
		return new Hitbox(res);
	}
	
	/**
	 * Get the middle of an object in world coordinate
	 * @param partie
	 * @param obj
	 * @return
	 */
	public static Vector2d getObjMid(AbstractModelPartie partie, Collidable obj)
	{
		Point _pos = new Point(obj.getXpos(),obj.getYpos());
		if(obj.fixedWhenScreenMoves)
		{
			_pos.x-=partie.xScreendisp;_pos.y-=partie.yScreendisp;
		}

		//find where object is precisely using the middle of the hitbox
		return Hitbox.getHitboxCenter(obj.getHitbox(partie.INIT_RECT,partie.getScreenDisp()));
	}
	/**
	 * Get the middle of the Hitbox*/
	public static Vector2d getHitboxCenter(Hitbox hitbox )
	{
		assert (hitbox.polygon.npoints==4);
		///on trace les deux diagonales, l'intersection des deux droites donnent le centre
		// Les droites sont de la forme ( a(x-x1) +y1  )
		Polygon poly = hitbox.polygon;
		Vector2d p0 = new Vector2d(poly.xpoints[0],poly.ypoints[0]);
		Vector2d p1 = new Vector2d(poly.xpoints[1],poly.ypoints[1]);
		Vector2d p2 = new Vector2d(poly.xpoints[2],poly.ypoints[2]);
		Vector2d p3 = new Vector2d(poly.xpoints[3],poly.ypoints[3]);
		
		Vector2d dir = new Vector2d(p1.x-p3.x,p1.y-p3.y);
		Vector2d middle = GJK_EPA.projection(p0, p2, dir, p3, true); // [751-762, 414-384] [0,-32] [756,415]
		return(middle);
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

	static Vector2d extremumParametersOnAxe(Hitbox hit, Point axe)
	{
		Polygon poly= hit.polygon;
		assert poly.npoints>0;
		float t = projectPointOnAxe(new Point(poly.xpoints[0],poly.ypoints[0]),axe);
		Vector2d res = new Vector2d(t,t); //res = {tmin, tmax } avec t parametre de la droite définie par l'axe 

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
		Vector2d tBox = extremumParametersOnAxe(box,axe1);
		float tTest = projectPointOnAxe(test,axe1);

		inRect = (tBox.x<=tTest && tTest<tBox.y );

		tBox = extremumParametersOnAxe(box,axe2);
		tTest= projectPointOnAxe(test,axe2);

		inRect = inRect && (tBox.x<=tTest && tTest<tBox.y );

		return inRect;
	}
	
	public static Vector2d projectOnHitbox(Hitbox hit, Vector2d A, Vector2d dir)
	{
		Polygon poly = hit.polygon;
		int size = poly.npoints;
		for(int i=0; i<size;i++)
		{
			int j = (i+1)%size;
			//test if p is on the segment poly[i] poly[j]
			Vector2d p1 = new Vector2d(poly.xpoints[i],poly.ypoints[i]);
			Vector2d p2 = new Vector2d(poly.xpoints[j],poly.ypoints[j]);
			Vector2d inter = GJK_EPA.projection(p1, p2, dir, A, false);
			if(inter != null)
				return inter;
		}
		return null;
	}
	
	public static boolean contains(Polygon poly, Point p)
	{
		return (poly.contains(p) || onBorder(poly,p));
	}
	/**
	 * Compensate the lack of information of the method poly.contains(point) by also checking the border 
	 * @param p
	 * @param poly
	 * @return
	 */
	public static boolean onBorder(Polygon poly, Point p)
	{
		int size = poly.npoints;
		double threshold = Math.pow(10, -9);
		for(int i=0; i<size;i++)
		{
			int j = (i+1)%size;
			//test if p is on the segment poly[i] poly[j]
			Point p1 = new Point(poly.xpoints[i],poly.ypoints[i]);
			Point p2 = new Point(poly.xpoints[j],poly.ypoints[j]);
			//for fast computation, asssume p1 != p2
			if(p.x == p1.x && (p.y == p1.y))
				return true;
			else if(p.x == p2.x && (p.y == p2.y))
				return true;
			//Here, the three points are different, test if they are aligned
			else if(  Math.abs((p.y-p1.y)* (p2.x-p.x) - (p2.y-p.y) *(p.x-p1.x)) < threshold)
			{
				//Test if p is between p1 and p2 : ie  (p-p1) . dot (p-p2) <0
				int dotProd = (p.x-p1.x) * (p.x-p2.x) + (p.y-p1.y) * (p.y-p2.y)  ;
				if( dotProd< 0 )
					return true;
			}
		}
		return false;
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
	
	//================================= TESTING ======================================================================
	protected static boolean TEST_getSlidedHitbox()
	{
		//getSlidedHitbox(Hitbox unrotated_hitbox, Point origin, Point destination)
		Polygon poly1 = new Polygon();
		poly1.addPoint(-2, 1);
		poly1.addPoint(2, 1);
		poly1.addPoint(2, -1);
		poly1.addPoint(-2, -1);
		Polygon res1 = Hitbox.getSlidedHitbox(new Hitbox(poly1), new Point(0,-1), new Point(0,1)).polygon;
		
		Polygon expected_poly1 = new Polygon();
		expected_poly1.addPoint(-1, -3);
		expected_poly1.addPoint(-1, 1);
		expected_poly1.addPoint(1, 1);
		expected_poly1.addPoint(1, -3);
		
		boolean success = true;
		for(int i=0; i<4;++i)
		{
			success = success && (res1.xpoints[i] ==expected_poly1.xpoints[i] ) && (res1.ypoints[i] ==expected_poly1.ypoints[i] );
			System.out.println(res1.xpoints[i] +" "+ res1.ypoints[i]);
		}
		return success;
	}
}
