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

import partie.collision.Collidable.XAlignmentType;
import partie.collision.Collidable.YAlignmentType;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Deplace;
import utils.PointHelper;

public class Hitbox {

	public Polygon polygon; //WARNING make sure that polygon values are never changed outside of Hitbox functions 
	
	//keep in memory the boundary of the polygon 
	private boolean polygonDirty;
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;
	
	private boolean isNull =false;
	public boolean isNull(){return isNull;}
	public Hitbox()
	{
		polygon= new Polygon();
		polygonDirty=true;
	}
	public static Hitbox createEmptyHitbox(){
		Hitbox res = new Hitbox();
		res.isNull = true;
		return res;
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
	
	public int getXTaille()
	{
		return getXmax()-getXmin();
	}
	public int getYTaille()
	{
		return getYmax()-getYmin();
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
	public Hitbox scale(final Vector2d scaling)
	{
		for(int i=0; i< polygon.npoints; ++i)
		{
			polygon.xpoints[i]=(int)Math.round(polygon.xpoints[i]*scaling.x);
			polygon.ypoints[i]=(int)Math.round(polygon.ypoints[i]*scaling.y);
		}
		if(!polygonDirty)//save computation time 
			setPolygonBoundaries(false, (int)Math.round(xmin*scaling.x), (int)Math.round(xmax*scaling.x), (int)Math.round(ymin*scaling.y), (int)Math.round(ymax*scaling.y));
		else
			polygonDirty = true;
		return this;
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
	public Vector2d getCenter()
	{
		assert (polygon.npoints==4);
		///on trace les deux diagonales, l'intersection des deux droites donnent le centre
		// Les droites sont de la forme ( a(x-x1) +y1  )
		Polygon poly = polygon;
		Vector2d p0 = new Vector2d(poly.xpoints[0],poly.ypoints[0]);
		Vector2d p1 = new Vector2d(poly.xpoints[1],poly.ypoints[1]);
		Vector2d p2 = new Vector2d(poly.xpoints[2],poly.ypoints[2]);
		Vector2d p3 = new Vector2d(poly.xpoints[3],poly.ypoints[3]);
		
		Vector2d dir = new Vector2d(p1.x-p3.x,p1.y-p3.y);
		Vector2d middle = GJK_EPA.projection(p3,p0, p2,dir, true); // [751-762, 414-384] [0,-32] [756,415]
		return(middle);
	}
	
	public Hitbox transformHitbox(final AffineTransform tr,final Point pos,final Point screendisp) {

		for(int j = 0; j<this.polygon.npoints; ++j)
		{
			Point2D temp = tr.transform(new Point(this.polygon.xpoints[j],this.polygon.ypoints[j]), null);
			this.polygon.xpoints[j] = (int)Math.round(temp.getX()-pos.x-screendisp.x);
			this.polygon.ypoints[j] = (int)Math.round(temp.getY()-pos.y-screendisp.y);
		}
		polygonDirty = true;
		return this;

	}
	
	/***
	 * 
	 * @param newXMin
	 * @param newXMax
	 * @param newYMin
	 * @param newYMax
	 * @return assuming the hitbox is unrotated and square, change the polygon so that the new min/max corresponds to the values passed as parameters.
	 */
	public Hitbox reshapeUnrotatedSquareHitbox(Integer newXMin, Integer newXMax, Integer newYMin, Integer newYMax){
		boolean updateXMin = newXMin != null;
		boolean updateXMax = newXMax != null;
		boolean updateYMin = newYMin != null;
		boolean updateYMax = newYMax != null;
		for(int j = 0; j<polygon.npoints; ++j)
		{
			if(updateXMin && (polygon.xpoints[j] == getXmin())){
				polygon.xpoints[j] = newXMin;
			}
			else if(updateXMax && (polygon.xpoints[j] == getXmax())){
				polygon.xpoints[j] = newXMax;
			}
			if(updateYMin && (polygon.ypoints[j] == getYmin())){
				polygon.ypoints[j] = newYMin;
			}
			else if(updateYMax && (polygon.ypoints[j] == getYmax())){
				polygon.ypoints[j] = newYMax;
			}
				
		}
		this.setPolygonBoundaries(false, updateXMin?newXMin: getXmin(), updateXMax?newXMax:getXmax(), updateYMin?newYMin:getYmin(), updateYMax?newYMax:getYmax());
		return this;
	}
	
	/***
	 * 
	 * @param xAlignment see {@link XAlignmentType}
	 * @param yAlignment see {@link YAlignmentType} consider the natural Y axis (this means that BOTTOM is for y>0 as x/y axis are inverted)
	 * @return the point of the hitbox that corresponds to both alignment, or null if at least one alignment is NONE
	 */
	public Vector2d getPoint(double hitboxRotation,XAlignmentType xAlignment,YAlignmentType yAlignment){
		assert polygon.npoints == 4;
		
		if(xAlignment.equals(XAlignmentType.NONE) || yAlignment.equals(YAlignmentType.NONE))
			return null; //need the two alignments to get the point 
		
		//Vector2d rotationVect = Deplace.angleToVector(hitboxRotation);
		
		//Center center => compute both center
		//Center Top => compute top left and top right, take the middle
		//Right Center => compute the right top and right bottom,take the middle 
		//Top Right => get directly 
		
		if(xAlignment.equals(XAlignmentType.CENTER) && yAlignment.equals(YAlignmentType.CENTER))
		{
			return getCenter();			
		}
		else if(xAlignment.equals(XAlignmentType.CENTER)){
			//yAlignment is either BOTTOM or TOP 
			int ydir = yAlignment.equals(YAlignmentType.BOTTOM)? 1 : -1;
			
			Vector2d left_dir = Deplace.angleToVector(hitboxRotation+ydir*3*Math.PI/4);
			Vector2d right_dir = Deplace.angleToVector(hitboxRotation+ydir*Math.PI/4);
			
			Vector2d left = Hitbox.supportPoint(left_dir, polygon);
			Vector2d right = Hitbox.supportPoint(right_dir, polygon);
			return new Vector2d((left.x+right.x)/2,(left.y+right.y)/2);
		}
		else if(yAlignment.equals(YAlignmentType.CENTER)){
			//xAlignment is either LEFT or RIGHT 
			double angleOffset = xAlignment.equals(XAlignmentType.LEFT)? Math.PI/2 : 0;
			
			Vector2d up_dir = Deplace.angleToVector(hitboxRotation-Math.PI/4-angleOffset);
			Vector2d bottom_dir = Deplace.angleToVector(hitboxRotation+Math.PI/4+angleOffset);
			
			Vector2d up = Hitbox.supportPoint(up_dir, polygon);
			Vector2d bottom = Hitbox.supportPoint(bottom_dir, polygon);
			return new Vector2d((up.x+bottom.x)/2,(up.y+bottom.y)/2);
		}
		else{
			int ydir = yAlignment.equals(YAlignmentType.BOTTOM)? 1 : -1;
			double angleOffset = xAlignment.equals(XAlignmentType.LEFT)? Math.PI/2 : 0;
			
			return Hitbox.supportPoint(Deplace.angleToVector(hitboxRotation+ydir*(Math.PI/4 +angleOffset)),polygon);
		}
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
	 * @param list [A B C D] where A : list of an edge pos (x,y) depending on the mouv_index A[mouv_index] list[edge][mouv_index]
	 * @return list [A B] where list[mouv_index] return a hitbox A
	 */
	public static List<Hitbox> createHitbox(final List<List<Point>> list)
	{
		List<Hitbox> hitboxes = new ArrayList<Hitbox>();
		//Generate one hitbox per animation
		for(int mouv_index=0; mouv_index<list.get(0).size(); ++mouv_index)
		{
			hitboxes.add(new Hitbox());	
		}

		for(int edge=0; edge<list.size(); ++edge)
		{
			List<Point> edgelist= list.get(edge);
			for(int mouv_index=0; mouv_index<edgelist.size(); ++mouv_index)
			{
				Point point = edgelist.get(mouv_index);
				hitboxes.get(mouv_index).polygon.addPoint(point.x,point.y);
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
	
	public static enum ExtendDirection{
		FORWARD,BOTTOM,BACKWARD,TOP,ALL; //unrotated hitbox, forward is right (x>0, y=0)
	}

	/***
	 * 
	 * @param rotated_hitbox
	 * @param unrotated_extension: Point(px,py), where px represents by how much [for the unrotated hitbox] the font/back should be extended, py for the top/bottom
	 * @param direction: direction of the "front" of the hitbox. This vector represents the angle. Or in other words, this vector points to the right when th hitbox is not rotated
	 * @return
	 */
	public static Hitbox uniformExtend(final Hitbox rotated_hitbox,final Point unrotated_extension, Vector2d direction){
		return extend(rotated_hitbox,unrotated_extension,direction,ExtendDirection.ALL);
	}
	
	public static Hitbox directionalExtend(final Hitbox rotated_hit,final double angle, final int extensionLength)
	{
		return directionalExtend(rotated_hit,Deplace.angleToVector(angle),extensionLength);
	}
	public static Hitbox directionalExtend(final Hitbox rotated_hit,Vector2d direction, final int extensionLength)
	{
		return extend(rotated_hit,new Point(extensionLength,0),direction,ExtendDirection.FORWARD);
	}
	/***
	 * 
	 * @param rotated_hitbox
	 * @param unrotated_extension: Point(px,py), where px represents by how much [for the unrotated hitbox] the font/back should be extended, py for the top/bottom
	 * @param direction: direction of the "front" of the hitbox. This vector represents the angle. Or in other words, this vector points to the right when th hitbox is not rotated
	 * @param extendDirection: Direction to be extended
	 * @return
	 */
	public static Hitbox extend(final Hitbox rotated_hitbox,final Point unrotated_extension, Vector2d direction,ExtendDirection extendDirection)
	{
		assert rotated_hitbox.polygon.npoints == 4 ;
		//WARNING: modifies the direction 		
		Hitbox res = rotated_hitbox.copy();
		
		//                      ||unrot_extension|| * cos(angle)
		//                                  ----
		//                                  \  |
		// ||unrot_extension||
		//  								 \ |  ||unrot_extension|| * sin(angle)
		//                                    \|
		direction.normalize();
		Vector2d forward_backward_extension = new Vector2d(direction);
		forward_backward_extension.scale(unrotated_extension.x);
		
		Vector2d up_down_extension = new Vector2d(-direction.y,direction.x);
		up_down_extension.scale(unrotated_extension.y);
		
		Vector2d bottomDirection = new Vector2d(-direction.y,direction.x);
		
		//Get the segment in the direction (ie the segment that has its angle closest to the direction) 
		List<Vector2d> forwardSegmentInDirection = supportSegment(direction,rotated_hitbox.polygon);
		List<Vector2d> bottomSegmentInDirection = supportSegment(bottomDirection,rotated_hitbox.polygon);

		
		for(int i=0; i<rotated_hitbox.polygon.npoints;++i){
			int j = (i+1)%rotated_hitbox.polygon.npoints;
			Vector2d p1 = new Vector2d(rotated_hitbox.polygon.xpoints[i],rotated_hitbox.polygon.ypoints[i]);
			Vector2d p2 = new Vector2d(rotated_hitbox.polygon.xpoints[j],rotated_hitbox.polygon.ypoints[j]);
			Vector2d extensionToAdd = null;
			ExtendDirection currentDirection; //direction of the current segment
			//check if forward
			if((p1.equals(forwardSegmentInDirection.get(0)) && p2.equals(forwardSegmentInDirection.get(1)))
					|| (p1.equals(forwardSegmentInDirection.get(1)) && p2.equals(forwardSegmentInDirection.get(0))))
			{
				extensionToAdd =forward_backward_extension;
				currentDirection = ExtendDirection.FORWARD;
			}
			else 
			{
				boolean p1_is_bottom0 = p1.equals(bottomSegmentInDirection.get(0));
				boolean p1_is_bottom1 = p1.equals(bottomSegmentInDirection.get(1));
				boolean p2_is_bottom0 = p2.equals(bottomSegmentInDirection.get(0));
				boolean p2_is_bottom1 = p2.equals(bottomSegmentInDirection.get(1));
				//else check if bottom
				if((p1_is_bottom0 && p2_is_bottom1) ||(p1_is_bottom1 && p2_is_bottom0) )
				{
					extensionToAdd =up_down_extension;
					currentDirection = ExtendDirection.BOTTOM;
				}
				//else if one point is common with bottom (and that it is not forward) => backward
				else if(p1_is_bottom0||p1_is_bottom1||p2_is_bottom0||p2_is_bottom1){
					extensionToAdd =new Vector2d(-forward_backward_extension.x,-forward_backward_extension.y);
					currentDirection = ExtendDirection.BACKWARD;
				}
				//else top
				else{
					extensionToAdd =new Vector2d(-up_down_extension.x,-up_down_extension.y);
					currentDirection = ExtendDirection.TOP;
				}
			}

			if(extendDirection.equals(ExtendDirection.ALL) || extendDirection.equals(currentDirection)){

				res.polygon.xpoints[i]+= Math.round(extensionToAdd.x);
				res.polygon.ypoints[i]+= Math.round(extensionToAdd.y);

				res.polygon.xpoints[j]+= Math.round(extensionToAdd.x);
				res.polygon.ypoints[j]+= Math.round(extensionToAdd.y) ;
			}
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
	/*public static Hitbox convertHitbox(final Hitbox current, final AffineTransform tr,final Point pos,final Point screendisp) {

		Polygon current_pol = current.polygon; 
		Polygon new_pol = new Polygon();
		for(int j = 0; j<current_pol.npoints; ++j)
		{
			Point2D temp = tr.transform(new Point(current_pol.xpoints[j],current_pol.ypoints[j]), null);
			new_pol.addPoint((int)Math.round(temp.getX())-pos.x-screendisp.x,(int)Math.round(temp.getY())-pos.y-screendisp.y);
		}
		return new Hitbox(new_pol);

	}*/
	
	/**
	 * Apply transform to the polygons and untranslate it with respect to position and screendisp (to get a coordinates in local scale)
	 * @param current: the hitboxes to tranform
	 * @param tr: the transform to apply
	 * @param pos: the position to remove to have local coordinates (if current were in global coordinates)
	 * @param screendisp: the screen displacement to remove to have local coordinates (if current were in global coordinates with screen displacement)
	 * @return
	 */
	/*public static List<Hitbox> convertHitbox(final List<Hitbox> current, final AffineTransform tr,final Point pos,final Point screendisp) {
		List<Hitbox> new_rotated_hit = new ArrayList<Hitbox>();

		for (int i = 0; i<current.size(); ++i)
		{
			new_rotated_hit.add(convertHitbox(current.get(i),tr,pos,screendisp));
		}

		return new_rotated_hit;
	}*/
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
		Vector2d center = hit.getCenter();
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
	
	/***
	 * Helper function for {@link #getSlidedHitbox(Hitbox, Point, Point)}: returns the sequence of points that have to be added in the result.
	 * This is the sequence in the sense that the order of the points respects the one from the polygon. for exemple for a polygon p1 p2 p3 p4 
	 * if p1 p3 p4 needs to be added, the result will be p4 p1 p2 to preserve the sequence 
	 * @param poly
	 * @param direction
	 * @param discriminator
	 * @param takePositiveDot
	 * @return
	 */
	private static ArrayList<Point> getSequenceOfPointsBasedOnDotSign(Polygon poly, Vector2d separatorCenter, Vector2d discriminator, boolean takePositiveDot){
		int ending_point_index = -1; //index of the last point of the sequence added to res (and not index of polygon !!!!)
		ArrayList<Point> res = new ArrayList<Point>();
		boolean previousPointAdded = false;
		for(int i=0; i<poly.npoints;++i){
			Point p = new Point(poly.xpoints[i],poly.ypoints[i]);
			Vector2d pDir = new Vector2d(p.x-separatorCenter.x,p.y-separatorCenter.y);
			double dot = pDir.dot(discriminator);
			//if we need to add the point (based on its dot production with the direction) 
			if( (dot<=0 && !takePositiveDot) || (dot>=0 && takePositiveDot)){
				//add the points
				res.add(p);
				//handle case where the point added is the last of the sequence 
				if( ending_point_index==-1 && i==(poly.npoints-1))
					ending_point_index = res.size()-1;
				previousPointAdded = true;
			}
			else{
				//if the point is not added, check if the previous point was not the last of the sequence
				if(previousPointAdded)
					ending_point_index = res.size()-1;
				previousPointAdded = false;
			}	
		}
		
		//Handle exception 
		if(ending_point_index==-1)
			try{throw new Exception("The ending point of the sequence was not determined correctly");}
			catch(Exception e){e.printStackTrace();}
		
		
		//All points are present in res. Now they have to be reordered
		ArrayList<Point> permutated_res = new ArrayList<Point>();
		int res_size = res.size();
		int startPointIndex = (ending_point_index+1)%res_size;
		
		for(int i=0; i<res_size;++i){
			int indexToAdd = (i+startPointIndex)%res_size;
			permutated_res.add(res.get(indexToAdd));
		}
		return permutated_res;
	}
	
	/**
	 * Get the area (hitbox) generated when the object moves from point A to point B with a given rotation
	 * WARNING: only works with CONVEX polygons 
	 * @param rotated_hitbox hitbox in (0,0) referential that has correct rotation
	 * @param ref_origin value by which to translate the hitbox to get it to its world original position
	 * @param ref_destination value by which to translate the hitbox to get it to its world target position
	 */
	//Function tested in A_Star_test
	public static Hitbox getSlidedHitbox(final Hitbox rotated_hitbox, final Point ref_origin, final Point ref_destination)
	{	
		//Note: origin and destination are final as we don't want their references to be modified
		
		//Note: polygons are the same but translated. Hence they have the same number of sides 
		/*
		 *   origin  poly             dest poly
		 *    B _ C                     B'_ C'
		 *  A/    \D       ->       A' /    \ D'
		 * F|______|E               F'|______| E'
		 *  
		 *  Slided poly 
		 *     B                         C'  
		 *      __________________________
		 *  A/                              \ D'
		 * F|________________________________| E' 
		 *  
		 *  The algorithm works in 4 steps: 
		 *  
		 *  1) Determine supports points for the original and destination polygon in the direction perpendicular to the direction
		 *  This is used to determine the "top" and "bottom" points that will draw most of the slided shape.
		 *  Break ties by taking points a bit in the back(/front) of the direction for the original(destination) poly
		 *  
		 *  ie: F and B for ori; C' and E' for dest
		 *  
		 *   2) Determine which points from ori to keep and which points from dest to keep. 
		 *   The idea here is to use the line created by the support points as a separator: every point from the ori that is left from the separator should be kept
		 *   Consider the vector that is perpendicular to the separator and has a positive dot product with the direction: 
		 *   this vector that we are going to call "discriminator"is going to determine whether we keep a point or not.
		 *   If the vector from the center of the separator to the points has a negative dot product with the determinator: 
		 *   -keep the point (for ori) 
		 *   -drop it (for dest)
		 *   (and the opposite for positive dot)
		 *  
		 *  ie: 
		 *             
		 *    B _ C          
		 *  A/|O    \D       
		 * F|/_____|E         
		 *   The separator (BF) has O has its center. the vector perpendicular to BF that points towards the right is the discriminator
		 *   OA.dot(discriminator) <= 0 so keep this point
 		 *   OC.dot(discriminator) >0 so drop this point 
 		 *   
 		 *   3) Once you know which points to keep, reorganise them so that they are consecutive points from their polygons
 		 *   
 		 *   ie: 
 		 *   ori will keep A,B,F; dest will keep C',D',E'
 		 *   The points from ori have to be reorganised as F,A,B
 		 *   
 		 *   4) Concatenate each list to get the slided polygon: 
 		 *   FABC'D'E'
		 * */
		
		Point deltaPos = new Point(ref_destination.x-ref_origin.x,ref_destination.y-ref_origin.y); 
		Vector2d direction = new Vector2d(deltaPos.x,deltaPos.y);
		//Note: we want the center of the hibox to be at the positions
		Vector2d center = rotated_hitbox.getCenter();
		Point point_center = new Point((int)Math.round(center.x),(int)Math.round(center.y));
		Point delte_ref_origin = new Point(ref_origin.x-point_center.x,ref_origin.y-point_center.y);
		Point delte_ref_destination = new Point(ref_destination.x-point_center.x,ref_destination.y-point_center.y);

		Polygon origin_poly = rotated_hitbox.copy().translate(delte_ref_origin).polygon;
		Polygon dest_poly = rotated_hitbox.copy().translate(delte_ref_destination).polygon;
		
		//Step 1: Determine the support points 
		//Perpendicular of v = (x,y) are (-y,x) and (y,-x)
		//add backward direction to break ties for ori, add forward direction to break tie for dest
		Vector2d oriSupportTieBreak = new Vector2d(direction);
		oriSupportTieBreak.scale(-0.01);
		Vector2d destSupportTieBreak = new Vector2d(direction);
		destSupportTieBreak.scale(0.01);
		
		Vector2d ori_sup1 = Hitbox.supportPoint(new Vector2d(-direction.y+oriSupportTieBreak.x,direction.x+oriSupportTieBreak.y), origin_poly);
		Vector2d ori_sup2 = Hitbox.supportPoint(new Vector2d(direction.y+oriSupportTieBreak.x,-direction.x+oriSupportTieBreak.y), origin_poly);
		Vector2d dest_sup1 = Hitbox.supportPoint(new Vector2d(-direction.y+destSupportTieBreak.x,direction.x+destSupportTieBreak.y), dest_poly);
		Vector2d dest_sup2 = Hitbox.supportPoint(new Vector2d(direction.y+destSupportTieBreak.x,-direction.x+destSupportTieBreak.y), dest_poly);
		
		//Step 2: Compute the discriminator and use it to determine which points to keep 
		Vector2d ori_O = new Vector2d((ori_sup1.x+ori_sup2.x)/2,(ori_sup1.y+ori_sup2.y)/2); //center of separator
		Vector2d dest_O = new Vector2d((dest_sup1.x+dest_sup2.x)/2,(dest_sup1.y+dest_sup2.y)/2);//center of separator
		Vector2d ori_discriminator = new Vector2d(-1 * (ori_sup2.y-ori_sup1.y),ori_sup2.x-ori_sup1.x);
		if(ori_discriminator.dot(direction)<0){
			ori_discriminator.negate();
		}
		Vector2d dest_discriminator = new Vector2d(-1 * (dest_sup2.y-dest_sup1.y),dest_sup2.x-dest_sup1.x);
		if(dest_discriminator.dot(direction)<0){
			dest_discriminator.negate();
		}

		//Step 2 & 3 Use the discriminator to get the list of points and reorganize them
		ArrayList<Point> oriPointsToAdd = getSequenceOfPointsBasedOnDotSign(origin_poly,ori_O, ori_discriminator, false);
		ArrayList<Point> destPointsToAdd = getSequenceOfPointsBasedOnDotSign(dest_poly,dest_O, dest_discriminator, true);
		
		//Step 4 Concatenate each list of points (as they have been correctly re-organized in the previous step)
		//As origin_poly and dest_poly comes from the same polygon (but translated), they have the same rotation size (either clockwise or anti clockwise)
		//Hence the ori and dest points to add can be directly concatenated
		Polygon res = new Polygon();
		for(Point p : oriPointsToAdd)
			res.addPoint(p.x, p.y);
		for(Point p : destPointsToAdd)
			res.addPoint(p.x, p.y);

		return new Hitbox(res);
	}
	
	/***
	 * Compute the slided hitbox from an unrotated_hitbox at position current_pos that goes to position target_pos
	 * @param objectUnrotatedHitbox
	 * @param current_pos
	 * @param target_pos
	 * @param extension if not null, the value by which the rotated hitbox should be expended (can be use to have so margin for the collision)
	 * @param moveTipToTargetPosition if true move the front of the hitbox to the target position, otherwise move the center of the hitbox
	 * @return
	 */
	public static Hitbox getSlidedHitboxFromPosition(Hitbox objectUnrotatedHitbox,Point current_pos,Point target_pos,Point extension, boolean moveTipToTargetPosition){
		Vector2d direction = new Vector2d(target_pos.x-current_pos.x,target_pos.y-current_pos.y);
		double rotation = Deplace.vectorToAngle(direction);
		Hitbox rotated_hit = Hitbox.rotateHitbox(objectUnrotatedHitbox, rotation);
		
		Vector2d worldHitTipVect = rotated_hit.getPoint(rotation, XAlignmentType.RIGHT, YAlignmentType.CENTER);
		Vector2d centerRotated = rotated_hit.getCenter();
		Vector2d centerRelativeHitTipVect = new Vector2d(worldHitTipVect.x-centerRotated.x,worldHitTipVect.y-centerRotated.y);

		Hitbox extendedHitbox = rotated_hit;
		if(extension != null)
			extendedHitbox = Hitbox.uniformExtend(rotated_hit,extension,direction); 

		//get slided collision from tip of current hitbox to target pos (we want the tip to be at target_pos)
		Point corrected_target_pos = moveTipToTargetPosition? new Point(target_pos.x-(int)Math.round(centerRelativeHitTipVect.x),target_pos.y-(int)Math.round(centerRelativeHitTipVect.y)) : target_pos;
		return Hitbox.getSlidedHitbox(extendedHitbox, current_pos, corrected_target_pos);
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
		return obj.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).getCenter();
	}
	/**
	 * Get the middle of the Hitbox*/

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

	public static Point getProjectedSize(final int xsize, final int ysize, final double angle){
			int x_projected = (int) (xsize * Math.cos(angle) - ysize * Math.sin(angle));
			int y_projected = (int) (xsize * Math.sin(angle) + ysize * Math.cos(angle));
			return new Point(x_projected,y_projected);
	}
	
	/**
	 * Check equality of polygon of hitbox. Does not take into account if the points from the polygon are not in the same order
	 * @param test
	 * @return
	 */
	public boolean equalsHitbox(Hitbox test){		
		if(polygon.npoints != test.polygon.npoints)
			return false;
		
		//Find a matching point
		Point p_to_match = new Point(polygon.xpoints[0],polygon.ypoints[0]);
		int match_index =-1;
		for(int i=0; i<test.polygon.npoints; ++i){
			if(test.polygon.xpoints[i] == p_to_match.x)
				if(test.polygon.ypoints[i] == p_to_match.y)
				{
					match_index=i;
					break;
				}
		}

		if(match_index == -1)
			return false;
		if(polygon.npoints == 1)
			return true;
		
		//Find the rotation direction (clockwise or anticlockwise)
		int previous_match_index = (match_index+test.polygon.npoints-1)%test.polygon.npoints;
		int next_match_index = (match_index+1)%test.polygon.npoints;
		boolean clockwiseRotation = true;
		if( (test.polygon.xpoints[previous_match_index] == polygon.xpoints[1]) 
				&& (test.polygon.ypoints[previous_match_index] ==polygon.ypoints[1])){
			clockwiseRotation = false;
		}
		else if((test.polygon.xpoints[next_match_index] == polygon.xpoints[1]) 
			&& (test.polygon.ypoints[next_match_index] ==polygon.ypoints[1])){
			clockwiseRotation = true;
		}
		else
			return false;
			
		//Check equality of other points 
		for(int i=2; i<polygon.npoints; ++i){
			int j=0;
			if(clockwiseRotation)
				j = (match_index +i)%test.polygon.npoints;
			else
				j = (match_index - i + test.polygon.npoints)%test.polygon.npoints;
			
			if(polygon.xpoints[i] != test.polygon.xpoints[j])
				return false;
			if(polygon.ypoints[i] != test.polygon.ypoints[j])
				return false;
		
		}
		return true;
	}
}
