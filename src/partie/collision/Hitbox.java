package partie.collision;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2d;

import partie.deplacement.Deplace;
import partie.modelPartie.AbstractModelPartie;
import utils.PointHelper;

public class Hitbox {

	public Polygon polygon; //WARNING make sure that polygon values are never changed outside of Hitbox functions 
	
	//De combien la hitbox a tournée depuis sa position d'origine, angle orienté sens direct
	public float angle;

	//keep in memory the boundary of the polygon 
	private boolean polygonDirty;
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;
	
	public Hitbox()
	{
		polygon= new Polygon();
		polygonDirty=true;
	}
	public Hitbox(final Point p1,final Point p2,final Point p3,final Point p4)
	{
		polygon= new Polygon();
		polygon.addPoint(p1.x, p1.y);
		polygon.addPoint(p2.x, p2.y);
		polygon.addPoint(p3.x, p3.y);
		if(p4 != null)
			polygon.addPoint(p4.x, p4.y);
		polygonDirty=true;

	}
	public Hitbox(final Polygon _p )
	{
		polygon=_p;
		polygonDirty=true;
	}
	
	private void computePolygonBoundaries()
	{
		xmin = polygon.xpoints[0];
		ymin = polygon.ypoints[0];
		xmax = polygon.xpoints[0];
		ymax = polygon.ypoints[0];
		for(int i=1; i< polygon.npoints; ++i)
		{
			xmin = Math.min(xmin, polygon.xpoints[i]);
			ymin = Math.min(ymin, polygon.ypoints[i]);
			xmax = Math.max(xmax, polygon.xpoints[i]);
			ymax = Math.max(ymax, polygon.ypoints[i]);
		}
		polygonDirty=false;
	}
	private void setPolygonBoundaries(final boolean polygonDirty, final int xmin, final int xmax, final int ymin, final int ymax)
	{
		this.polygonDirty=polygonDirty;
		this.xmin=xmin;
		this.xmax=xmax;
		this.ymin=ymin;
		this.ymax=ymax;
	}
	
	public int getXmin()
	{
		if(polygonDirty)
		{
			computePolygonBoundaries();
		}
		return xmin;
	}
	public int getXmax()
	{
		if(polygonDirty)
		{
			computePolygonBoundaries();
		}
		return xmax;
	}
	
	public int getYmin()
	{
		if(polygonDirty)
		{
			computePolygonBoundaries();
		}
		return ymin;
	}
	
	public int getYmax()
	{
		if(polygonDirty)
		{
			computePolygonBoundaries();
		}
		return ymax;
	}
	
	
	public Hitbox copy()
	{
		Hitbox copy = new Hitbox();
		for(int i=0; i< this.polygon.npoints; i++)
		{
			copy.polygon.addPoint(this.polygon.xpoints[i], this.polygon.ypoints[i]);
		}
		if(!polygonDirty)//save computation time 
			copy.setPolygonBoundaries(false, xmin, xmax, ymin, ymax);
		return copy;
	}
	
	
	public Hitbox translate(final Point transl)
	{
		return translate(transl.x, transl.y);
	}
	public Hitbox translate(final int tx, final int ty)
	{
		for(int i=0; i< polygon.npoints; ++i)
		{
			polygon.xpoints[i]+=tx;
			polygon.ypoints[i]+=ty;
		}
		if(!polygonDirty)//save computation time 
			setPolygonBoundaries(false, xmin+tx, xmax+tx, ymin+ty, ymax+ty);
		else
			polygonDirty = true;
		return this;//allow for combined transformations
	}
	
	//================================= HELPER STATIC METHODS ======================================================================

	/**Create square hitboxes based on the list*/
	public static List<Hitbox> createQuadriHitboxes(final List<Point> p1, final List<Point> p2, final List<Point> p3, final List<Point> p4)
	{
		List<Hitbox> hitboxes = new ArrayList<Hitbox>();
		for(int i=0;i<p1.size(); ++i)
			hitboxes.add(new Hitbox(p1.get(i),p2.get(i),p3.get(i),p4.get(i)));
		return hitboxes;
	}
	/** Create a square hitbox whose bounds are xmin,xmax,ymin,ymax*/
	public static Hitbox createSquareHitbox(final int xmin,final int ymin,final int xmax, final int ymax)
	{
		Hitbox box = new Hitbox(new Point(xmin,ymin),new Point(xmax,ymin),new Point(xmax,ymax),new Point(xmin,ymax));
		box.setPolygonBoundaries(false, xmin, xmax, ymin, ymax);
		return box;
	}
	/**Create "nb" square hitboxes all identical*/
	public static List<Hitbox> createSquareHitboxes(final int xmin, final int ymin, final int xmax, final int ymax,final int nb)
	{
		List<Hitbox> hitboxes = new ArrayList<Hitbox>();
		for(int i=0;i<nb; ++i)
			hitboxes.add(createSquareHitbox(xmin, ymin, xmax, ymax));
		return hitboxes;
	}
	/**Create square hitboxes based on the list*/
	public static List<Hitbox> createSquareHitboxes(final List<Integer> xmin, final List<Integer> ymin, final List<Integer> xmax, final List<Integer> ymax)
	{
		List<Hitbox> hitboxes = new ArrayList<Hitbox>();
		for(int i=0;i<xmin.size(); ++i)
			hitboxes.add(createSquareHitbox(xmin.get(i), ymin.get(i), xmax.get(i), ymax.get(i)));
		return hitboxes;
	}
	public static List<Point> asListPoint(final List<Integer> x, final List<Integer> y)
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
	public static List<Hitbox> createHitbox(final List<List<Point>> list)
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
	public static String polyToString(final Polygon poly)
	{
		String s="";
		for(int i=0; i<poly.npoints; ++i)
		{
			Point p = new Point(poly.xpoints[i],poly.ypoints[i]);
			s+=(i==0? "" : " ")+"("+p.x+","+p.y+")";
		}		
		return s;
	}
	public static String simplexToString(final List<Vector2d> simplex)
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
	
	
	/***
	 * Extend the hitbox in a symetric way. Ie: hitbox 0,0 1,0 1,1 0,1 extended by 1,2 gives : -1 -2 , 2,-2  2,3 -1,3
	 * Only works on unrotated square hitbox 
	 * @param hit
	 * @param extension
	 * @return
	 */
	public static Hitbox extend(final Hitbox unrotated_hit,final Point extension)
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
	
	public static Hitbox directionalExtend(final Hitbox rotated_hit,final double angle, final float extensionLength)
	{
		return directionalExtend(rotated_hit,Deplace.angleToVector(angle),extensionLength);
	}
	public static Hitbox directionalExtend(final Hitbox rotated_hit,Vector2d direction, final float extensionLength)
	{
		//WARNING: modifies the direction 
		direction.normalize();
		direction.scale(extensionLength);
		
		Hitbox res = rotated_hit.copy();
		//Get the segment in the direction (ie the segment that has its angle closest to the direction) 
		List<Vector2d> segmentInDirection = supportSegment(direction,rotated_hit.polygon);
		//Move those two points by extensionLength in that direction 
		for(int i=0; i<res.polygon.npoints;++i)
			if((res.polygon.xpoints[i] ==segmentInDirection.get(0).x) && (res.polygon.ypoints[i] ==segmentInDirection.get(0).y) 
					||(res.polygon.xpoints[i] ==segmentInDirection.get(1).x) && (res.polygon.ypoints[i] ==segmentInDirection.get(1).y)  )
			{
				res.polygon.xpoints[i]+= direction.x;
				res.polygon.ypoints[i]+= direction.y;
			}
		return res;
	}
	
	public static Vector2d supportPoint(final Vector2d dir,final Polygon poly)
	{
		return supportPoints(dir,poly,false).get(0);
	}
	public static List<Vector2d> supportPoints(final Vector2d dir,final Polygon poly)
	{
		return supportPoints(dir,poly,false);
	}
	public static List<Vector2d> supportSegment(final Vector2d dir,final Polygon poly)
	{
		return supportPoints(dir,poly,true);
	}
	
	/***
	 * 
	 * @param dir
	 * @param poly
	 * @param getSupportSegment
	 * @return
	 */
	private static ArrayList<Vector2d> supportPoints(final Vector2d dir,final Polygon poly,boolean getSupportSegment)
	{
		HashMap<Vector2d,Double> dotValuePerVertex = new HashMap<Vector2d,Double>();
				
		double second_best_value = -Double.MAX_VALUE;
		Vector2d v0 = new Vector2d(poly.xpoints[0],poly.ypoints[0]);
		double best_value = dir.dot(v0);//retrieve all vertices such that dot > threshold_value
		dotValuePerVertex.put(v0, best_value);
		for(int n=1; n<poly.npoints; ++n)
		{
			Vector2d vect = new Vector2d(poly.xpoints[n],poly.ypoints[n]);
			double dot= dir.dot(vect);
			dotValuePerVertex.put(vect, dot);
			//for a polygon, once you go through all its vertices, the value of the dot product with a direction
			//increase until you reach the support point and then decrease.
			
			if(dot>best_value)
			{
				second_best_value = best_value;
				best_value = dot;
			}
			else if(getSupportSegment && (dot> second_best_value))
			{
				second_best_value = dot;
			}
		}
		
		ArrayList<Vector2d> res= new ArrayList<Vector2d>();
		Vector2d secondBest = null;
		for (Map.Entry<Vector2d, Double> entry : dotValuePerVertex.entrySet()) {
			if(entry.getValue()==best_value){
				res.add(entry.getKey());
			}
			else if(getSupportSegment && (entry.getValue()==second_best_value))
				secondBest = entry.getKey();
		}
		//Do this to avoid iterating 2 times over the object since we only want the 2 best points and that their can be 2 points that have a value of best_value
		if(getSupportSegment && res.size()<2)
			res.add(secondBest);
		return res;
	}


	/**
	 * Apply transform to the polygons and untranslate it with respect to position and screendisp (to get a coordinates in local scale)
	 * @param current: the hitbox to tranform
	 * @param tr: the transform to apply
	 * @param pos: the position to remove to have local coordinates (if current were in global coordinates)
	 * @param screendisp: the screen displacement to remove to have local coordinates (if current were in global coordinates with screen displacement)
	 * @return
	 */
	public static Hitbox convertHitbox(final Hitbox current, final AffineTransform tr,final Point pos,final Point screendisp) {

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
	public static List<Hitbox> convertHitbox(final List<Hitbox> current, final AffineTransform tr,final Point pos,final Point screendisp) {
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
	public static Hitbox rotateHitbox(final Hitbox hit, final double rad_angle)
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
	
	private static Vector2d rotatePoint(final Vector2d point,final Vector2d centre,final double rad_angle)
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
	public static Hitbox getSlidedHitbox(final Hitbox unrotated_hitbox, final Point ref_origin, final Point ref_destination)
	{	
		//Note: origin and destination are final as we don't want their references to be modified
		Vector2d v_hitboxMiddle = Hitbox.getHitboxCenter(unrotated_hitbox);
		Point hitboxMiddle= PointHelper.RountVecToPoint(v_hitboxMiddle);
		
		//place the center at origin
		Point centeredOrigin = new Point(ref_origin.x-hitboxMiddle.x,ref_origin.y-hitboxMiddle.y);
		
		//place the center at destination
		Point centeredDestination = new Point(ref_destination.x-hitboxMiddle.x,ref_destination.y-hitboxMiddle.y);

		//origin is where the middle of the hitbox start
		Point deltaPos = new Point(centeredDestination.x-centeredOrigin.x,centeredDestination.y-centeredOrigin.y);
		double rad_angle = Deplace.XYtoAngle(deltaPos.x, deltaPos.y);

		//                          |               ^
		//   v(x,y) ->  v_90(-y,x)  V  v_270 (y,-x) | 
		Polygon origin_poly = Hitbox.rotateHitbox(unrotated_hitbox.copy().translate(centeredOrigin),rad_angle).polygon; //copy
		
		Vector2d origin_support_dir_90 = new Vector2d(-deltaPos.y- 0.01 * deltaPos.x, deltaPos.x- 0.01 * deltaPos.y); //see v_90 above, the 0.01 is to lean it to the back of the direction
		Vector2d origin_support_dir_270 = new Vector2d(deltaPos.y- 0.01 * deltaPos.x, -deltaPos.x- 0.01 * deltaPos.y); //see v_270 above, the 0.01 is to lean it to the back of the direction

		//based on the drawing below, we want from G to C hence from v_90 to v_270
		Vector2d origin_p1 = Hitbox.supportPoint(origin_support_dir_90, origin_poly);
		Vector2d origin_p2 = Hitbox.supportPoint(origin_support_dir_270, origin_poly);

		
		Polygon dest_poly = Hitbox.rotateHitbox(unrotated_hitbox.copy().translate(centeredDestination),rad_angle).polygon; 

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
		
		int x_origin_p =0;
		int y_origin_p =0;
		
		int x_dest_p =0;
		int y_dest_p =0;
		while(!origin_p1_found || !origin_p2_found || !dest_p1_found ||!dest_p2_found)
		{
			x_origin_p = origin_poly.xpoints[i];
			y_origin_p = origin_poly.ypoints[i];
			
			x_dest_p = dest_poly.xpoints[i];
			y_dest_p = dest_poly.ypoints[i];
			
			//Look for the first point of the polygon
			if((x_origin_p == origin_p1.x) && (y_origin_p == origin_p1.y))
			{
				origin_p1_found = true;
			}
			
			if(origin_p1_found){
				//Add all points between origin_p1 and origin_p2
				if(!origin_p2_found){
					res.addPoint(x_origin_p,y_origin_p);
					if((x_origin_p == origin_p2.x) && (y_origin_p == origin_p2.y))
						origin_p2_found = true;
				}
				//Add points from destination polygon
				else
				{
					//look for first point to add from destination
					if((x_dest_p == dest_p1.x) && (y_dest_p == dest_p1.y))
					{
						dest_p1_found = true;
					}
					//If first point to add is found, add all points until second is found 
					if(dest_p1_found)
					{
						if(!dest_p2_found)
						{
							res.addPoint(x_dest_p,y_dest_p);
							if((x_dest_p == dest_p2.x) && (y_dest_p == dest_p2.y))
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
	public static Vector2d getObjMid(final AbstractModelPartie partie, final Collidable obj)
	{
		//find where object is precisely using the middle of the hitbox
		return Hitbox.getHitboxCenter(obj.getHitbox(partie.INIT_RECT,partie.getScreenDisp()));
	}
	/**
	 * Get the middle of the Hitbox*/
	public static Vector2d getHitboxCenter(final Hitbox hitbox )
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

	public static Vector2d projectOnHitbox(final Hitbox hit, final Vector2d A, final Vector2d dir)
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
	
	public static boolean contains(final Polygon poly, final Point p)
	{
		return (poly.contains(p) || onBorder(poly,p));
	}
	/**
	 * Compensate the lack of information of the method poly.contains(point) by also checking the border 
	 * @param p
	 * @param poly
	 * @return
	 */
	public static boolean onBorder(final Polygon poly, final Point p)
	{
		int size = poly.npoints;
		double threshold = Math.pow(10, -9);
		
		int j; 
		int dotProd;
		for(int i=0; i<size;i++)
		{
			j = (i+1)%size;
			//test if p is on the segment poly[i] poly[j]			
			//for fast computation, assume poly[i] != poly[j]
			if(p.x == poly.xpoints[i] && (p.y == poly.ypoints[i]))
				return true;
			else if(p.x == poly.xpoints[j] && (p.y == poly.ypoints[j]))
				return true;
			//Here, the three points are different, test if they are aligned
			else if(  Math.abs((p.y-poly.ypoints[i])* (poly.xpoints[j]-p.x) - (poly.ypoints[j]-p.y) *(p.x-poly.xpoints[i])) < threshold)
			{
				//Test if p is between poly[i] and poly[j] : ie  (p-poly[j]) . dot (p-poly[j]) <0
				dotProd = (p.x-poly.xpoints[i]) * (p.x-poly.xpoints[j]) + (p.y-poly.ypoints[i]) * (p.y-poly.ypoints[j])  ;
				if( dotProd< 0 )
					return true;
			}
		}
		return false;
	}

	
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
		}
		return success;
	}
}
