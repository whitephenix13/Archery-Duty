package partie.collision;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

public abstract class GJK_EPA {
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static float FLOAT_MAX = 3.402823466e+38F;
	public static double TOLERANCE = 1.0e-10F;
	public static int NOT_INTER=-1;
	public static int TOUCH=0;
	public static int INTER=1;
	/**
	 * @param a the object polygon
	 * @param b the monde polygon
	 */
	public static int intersectsB( final Polygon a, final Polygon b, final Vector2d firstDir )
	{
		List<Vector2d> simplex = intersects(a, b,firstDir );
		List<Vector2d> normals = new ArrayList<Vector2d>();
		double dInter=0.0d;
		boolean dNull=true;
		if(simplex!=null)
		{
			dNull=false;
			GJK_EPA.EPA(b, a, simplex, firstDir , normals);
		}
		return isIntersect(dInter,dNull);
	}
	public static int isIntersect(double dInter,boolean dNull)
	{
		if(dNull)
			return NOT_INTER;
		else if(Math.abs(dInter)<0.001) 
			return TOUCH;
		else 
			return INTER;
	}
	public static int isIntersect(List<Vector2d> simplex)
	{
		boolean touching=false; //0 belong to one of the side of the simplex
		if(simplex==null)
			return NOT_INTER;
		for(int i=0; i<simplex.size();i++)
		{
			int j = (i==simplex.size()-1)? 0 : (i+1);
			Vector2d A= simplex.get(i);
			Vector2d B= simplex.get(j);
			boolean touchingNow = false;
			if(A.x==B.x)
				touchingNow= (Math.abs(A.x)<0.71f); // ~sqrt(2)/2
			else
			{

				//Find C such that AB . CO = (0,0) (O being vector(0,0))
				// (B.x-A.x) C.x + (B.y-A.y) C.y =0 <=>  C.x + a* C.y = 0
				//C belong to AB : C.y = a * C.x + b
				//C.y = a * -1 * a * C.y + b
				double _a = (B.y-A.y)/(B.x-A.x);
				double _b= A.y-_a*A.x;
				double Y= _b/(1+_a*_a);
				double X= -1*_a*Y;
				Vector2d v = new Vector2d(X,Y);
				touchingNow=(v.length() < 0.71);
			}

			touching = touching || touchingNow;
			if(touching)
				break;
		}
		if(touching)
			return TOUCH; 
		else
			return (simplex==null?NOT_INTER:INTER);
	}
	public static List<Vector2d> intersects( final Polygon a, final Polygon b, final Vector2d firstDir )
	{
		assert a != null;
		assert b != null;

		// Create a simplex which is basically a dynamic polygon:
		final List< Vector2d > simplex = new ArrayList< Vector2d >( );

		// Pick an arbitrary starting direction for simplicity:
		final Vector2d direction = firstDir;
		if(direction.x==0 && direction.y==0)
			direction.x=1.0f;// default direction to (1,0)

		// Initialize the simplex list with our first direction:
		Vector2d vect =  minkowski( a, b, direction );
		simplex.add( vect );

		// Negate the direction so we search in the opposite direction for the next Minkowski point
		// since the origin obviously won't be in the current direction:
		direction.negate( );

		while ( true )
		{
			// Search for a new point along the direction:
			Vector2d point = minkowski( a, b, direction );

			// For a potential collision, new point must go past the origin which means that the
			// dot product must be positive (ie. the angle between 'point' and 'direction' must
			// be less than 90 degrees ), otherwise the objects don't intersect.
			if ( point.dot( direction ) < 0 )
				return null;

			// Point is valid so update the simplex:
			simplex.add( point );
			// Evaluate and update the simplex while checking for an early exit:
			if ( evaluate( simplex, direction ) == true )
				return simplex;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the Minkowski difference point (ie. the point further along the edge of the Minkowski
	 * difference polygon in the direction of the vector).
	 *
	 * @param   a - First polygon.
	 * @param   b - Section polygon.
	 * @param   direction - Direction vector.
	 * @return  Point along the edge of the Minkowski different in the passed direction.
	 */
	private static Vector2d minkowski( final Polygon a, final Polygon b, final Vector2d direction )
	{
		/*
		 * Minkowski difference is simply the support point from polygon A minus the
		 * support point from polygon B in the opposite direction.
		 */

		final Vector2d result = support( a, direction );
		result.sub( support( b, new Vector2d( -direction.x, -direction.y ) ) );
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the furthest point along the edge of the polygon in the director of the vector.
	 *
	 * @param   polygon - Polygon to evaluate.
	 * @param   direction - Direction vector.
	 * @return  Point along the edge of polygon in the passed direction.
	 */

	static Vector2d support( final Polygon polygon, final Vector2d direction )
	{
		/*
		 * The further point in any direction in the polygon must be a vertex point,
		 * so iterate over each point in the polygon and compare the dot product (ie. scalar
		 * evaluation of a point along a vector) and take the point with the highest value.
		 */

		double  max   = -Double.MAX_VALUE;  // Maximum dot product value
		int     index = -1;                 // Index of furthest point in the direction

		for ( int i = 0; i != polygon.npoints; ++i )
		{
			double dot = direction.x * polygon.xpoints[ i ] + direction.y * polygon.ypoints[ i ];

			if ( dot > max )
			{
				max   = dot;
				index = i;
			}
		}
		return new Vector2d( polygon.xpoints[ index ], polygon.ypoints[ index ] );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns true if the two vectors are pointing in the same direction.  Two vectors are
	 * considered to be in the same direction if the angle between them is less than 90
	 * degrees.
	 *
	 * @param   a - Vector a.
	 * @param   b - Vector b.
	 * @return  True if the two vectors are pointing in the same direction, otherwise false.
	 */
	private static boolean sameDirection( final Vector2d a, final Vector2d b )
	{
		/*
		 * For two vectors to be in the same direction, the angle between them
		 * must be less than 90 degrees, ie. the cos(angle) will be greater
		 * than zero.  Since dot(a,b) = |a| * |b| * cos(angle), and |?| is always
		 * positive, we only need to compare the sign of the dot product.
		 */

		return a.dot( b ) > 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new vector AB.  Resulting vector is equivalent to:
	 * <pre>B - A</pre>
	 *
	 * @param   a - First point.
	 * @param   b - Second point.
	 * @return  Vector AB.
	 */
	private static Vector2d createVector( final Vector2d a, final Vector2d b )
	{
		return new Vector2d( b.x - a.x, b.y - a.y );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Evaluates a simplex object.  Simplex is expected to contain 2-3 points for
	 * a 2D implementation.  Both the simplex and direction will be updated.
	 *
	 * @param   simplex   - Simplex object.  This will be modified.
	 * @param   direction - Direction to search in.  This will be modified.
	 * @return  True if the simplex contains the origin, otherwise false.
	 * @throws  IllegalArgumentException If simplex does not contain 2-3 points.
	 */
	private static boolean evaluate( List< Vector2d > simplex, Vector2d direction )
	{
		switch ( simplex.size( ) )
		{
		// Line segment:
		case 2:
		{
			// Pull the points from the simplex list:
			final Vector2d a  = simplex.get( 1 );
			final Vector2d b  = simplex.get( 0 );

			// Compute helper vectors:
			final Vector2d ao = createVector( a, new Vector2d( ) );
			final Vector2d ab = createVector( a, b );

			// Adjust the direction to be perpendicular to AB, pointing towards the origin:
			direction.set( -ab.y, ab.x );
			if ( sameDirection( direction, ao ) == false )
				direction.negate( );

			// Continue building the simplex:
			return false;
		}

		// Triangle:
		case 3:
		{
			// Pull the points from the simplex list:
			final Vector2d a  = simplex.get( 2 );
			final Vector2d b  = simplex.get( 1 );
			final Vector2d c  = simplex.get( 0 );

			// Compute helper vectors:
			final Vector2d ao = createVector( a, new Vector2d( ) );
			final Vector2d ab = createVector( a, b );
			final Vector2d ac = createVector( a, c );

			// Adjust the direction to be perpendicular to AB, pointing away from C:
			direction.set( -ab.y, ab.x );
			if ( sameDirection( direction, c ) == true )
				direction.negate( );

			// If the perpendicular vector from the edge AB is heading towards the origin,
			// then we know that C is furthest from the origin and we can safely
			// remove to create a new simplex away from C:
			if ( sameDirection( direction, ao ) == true )
			{
				simplex.remove( 0 );
				return false;
			}

			// Adjust the direction to be perpendicular to AC, pointing away from B:
			direction.set( -ac.y, ac.x );
			if ( sameDirection( direction, b ) == true )
				direction.negate( );

			// If the perpendicular vector from the edge AC is heading towards the origin,
			// then we know that B is furthest from the origin and we can safely
			// remove to create a new simplex away from B:
			if ( sameDirection( direction, ao ) == true )
			{
				simplex.remove( 1 );
				return false;
			}

			// If the perendicular vectors generated from the edges of the triangle
			// do not point in the direction of the origin, then the origin must be
			// contained inside of the triangle:
			return true;
		}

		default:
			throw new IllegalArgumentException( "Invalid number of points in the GJK simplex: " + simplex.size( ) );
		}
	}

	public static class IntersectPoint
	{
		public double distance; //penetration distance , penetration vector is speed.normalize * distance

		public double distanceNormal; //distance from to origine to the intersect point along the normal
		public Vector2d normal; //normal of the simplex edge containing the intersect point

		public int index; //index of where to add the point in the simplex list
		
		public Vector2d intersect = null;
		public IntersectPoint(double d,double nd, Vector2d n, Vector2d _inter)
		{
			distance=d; 
			distanceNormal= nd;
			normal=n;
			intersect=_inter;
		}

	}

	/**Expansion algorithm which iteratively adds a point in order to expand the simplex. Stop when the distance to the closest edge is close to the one of the point added */
	public static double EPA(Polygon shapeA, Polygon shapeB, List<Vector2d> simplex, Vector2d dir, List<Vector2d> outNormal)
	{
		while(true)
		{
			
			double distance = FLOAT_MAX;

			IntersectPoint edgeInDirection = getEdgeInDirection(simplex, dir,shapeA, shapeB);
			if(edgeInDirection==null)
				return 0;

			
			final Vector2d edgeNormal = edgeInDirection.normal;
			//get support point in direction of edge's normal
			final Vector2d sup = support( shapeA,edgeNormal  );
			sup.sub( support( shapeB, new Vector2d( -edgeNormal.x, -edgeNormal.y ) ) ); 
		
			distance = edgeInDirection.distanceNormal;
			double d = sup.dot(edgeNormal);
			
			
			if( (d - distance) <= TOLERANCE)
			{
				edgeInDirection.normal= correctNormal(edgeInDirection.normal);
				outNormal.add(edgeInDirection.normal);
				return edgeInDirection.distance;
			}
			else{

				simplex.add(edgeInDirection.index,sup);
			}

		}
	}
	/*Project a vector to the closest vector that makes a 90° with the axis.
	 * Positif perturbation makes the vector turn toward the positif angle*/
	public static Vector2d projectVectorTo90(Vector2d v,boolean negate,double perturbation)
	{
		boolean biggestX = Math.abs(v.x)>=Math.abs(v.y);
		int neg =negate?-1:1;
		if(biggestX)
		{
			int sign = v.x>0?1:-1;
			double other = perturbation==0 ? 0 : (perturbation*sign*neg);
			return new Vector2d(1*sign*neg,other);
		}
		else
		{
			int sign = v.y>0?1:-1;
			double other = perturbation==0 ? 0 : (perturbation*sign*neg);
			return new Vector2d(other,1*sign*neg);
		}

	}
	public static Vector2d correctNormal(Vector2d v)
	{
		//correct -0 to 0 
		if(Math.abs(v.x)==0)
			v.x=0;
		if(Math.abs(v.y)==0)
			v.y=0;

		return v;
	}
	/**
	 * Check if point is between min(p1,p2) and max(p1,p2)
	 * @param p1
	 * @param p2
	 * @param point
	 * @return
	 */
	public static boolean containValue(double p1, double p2,double point)
	{
		if( ( (Math.min(p1, p2)-TOLERANCE)<= point) && ( (Math.max(p1, p2)+TOLERANCE)>= point) )
			return true;
		else
			return false;
	}
	/**
	 * Test if ptest.x is between min(p1.x,p2.x) and max(p1.x,p2.x) and same for ptest.y
	 * @param px
	 * @param py
	 * @param ptest
	 * @return
	 */
	public static boolean containValue(Vector2d p1, Vector2d p2,Vector2d ptest)
	{
		return (containValue(p1.x,p2.x,ptest.x) && containValue(p1.y,p2.y,ptest.y));
	}
	public static Vector2d supportPoint(Vector2d dir, List<Vector2d> l)
	{
		Vector2d res= l.get(0);
		double res_value = dir.dot(res);
		for(int n=1; n<l.size(); ++n)
		{
			Vector2d vect = l.get(n);
			double dot= dir.dot(vect);

			if(dot>res_value)
			{
				res_value=dot;
				res=vect;
			}
		}
		return res;
	}

	public static IntersectPoint getEdgeInDirection(List<Vector2d> simplex, Vector2d dir,Polygon shA, Polygon shB)
	{
		IntersectPoint intersectTest=null;
		IntersectPoint memIntersectTest=null;

		for(int i = 0; i < simplex.size(); i++)
		{
			int j;
			if(i+1 == simplex.size())
				j = 0;
			else
				j = i+1;
			Vector2d origin = new Vector2d(0,0);
			intersectTest = getProjectionDistance(simplex.get(i),simplex.get(j),dir,origin);
			//System.out.println("Projection: " +simplex.get(i) +" "+simplex.get(j)+ " "+dir);
			if(intersectTest!=null)
			{
				//check if the next one can't be better ie normal distance shorter
				if((i+1)<simplex.size())
				{
					int i2 = i+1;
					int j2 = (i2+1 == simplex.size())? 0 : i2+1;
					IntersectPoint intersectTest2 = getProjectionDistance(simplex.get(i2),simplex.get(j2),dir,origin);
					if(intersectTest2!=null && (intersectTest2.distanceNormal<intersectTest.distanceNormal)){
						intersectTest=intersectTest2;
						i=i2;
						j=j2;
					}
				}
				//in the case where i=0 also check the last one 
				if(i==0 && ((i+1)< simplex.size()))
				{
					int i2 = simplex.size()-1;
					int j2 = 0;
					IntersectPoint intersectTest2 = getProjectionDistance(simplex.get(i2),simplex.get(j2),dir,origin);
					if(intersectTest2!=null && (intersectTest2.distanceNormal<intersectTest.distanceNormal)){
						intersectTest=intersectTest2;
						i=i2;
						j=j2;
					}
				}
				
				//System.out.println(intersectTest.normal);
				intersectTest.index=j;
				if(intersectTest.normal==null)
				{
					Vector2d n = new Vector2d(simplex.get(i).y-simplex.get(j).y,simplex.get(j).x-simplex.get(i).x); 
					n.normalize();
					//we compare the dot product of the normal and each points. 
					//if the dot products is positif, it means that simplex.get(i) and simplex.get(j) are not the support points
					//for n and thus, the normal is oriented towards the center of the polygon
					//if it is negative, the normal is correct.


					outerloop: 
						for(int indA =0; indA<shA.npoints; indA++)
							for(int indB =0; indB<shB.npoints; indB++)
							{
								Vector2d v = new Vector2d(shA.xpoints[indA]-shB.xpoints[indB],shA.ypoints[indA]-shB.ypoints[indB]);
								double dot = v.dot(n);
								if(dot<TOLERANCE)
								{
									break outerloop;
								}

								else if (dot>TOLERANCE)
									n = new Vector2d(-n.x,-n.y);

								//else: continue
							}
					intersectTest.normal=n;

				}
				//if(intersectTest.distanceNormal>TOLERANCE )){
				return intersectTest;

				//else
				//memIntersectTest=intersectTest; //continue to look for a better point if exists 

			}
		}

		return memIntersectTest;
	}


	/**calculate the projection of the point A in the direction dir on the segment [p1,p2]
	 * allDirection: if true check intersection in dir AND -dir */
	public static Vector2d projection(Vector2d p1, Vector2d p2, Vector2d dir, Vector2d A,boolean allDirection)
	{
		//(1)y=a1x+b1 avec pt intercection P={X,Y} et X[min(p1.x,p2.x),max(p1.x,p2.x)], Y[min(p1.y,p2.y),max(p1.y,p2.y)]
		//(2)D : y= a2 * x + b2 with a2 = dir.y/dir.x and b2 = (A.y - a2 * A.x)  

		//a2 = dir.y/dir.x and b2 = (A.y - a2 * A.x)  

		//By subsitution of y from 2 to 1 a2 x + b2 = a1 x + b1 : x = (b1-b2)/(a2-a1) exist if a1 != inf, dir.x !=0 , dir.y/dir.x != a1
		//Y=aX+b
		Vector2d intersectP=null;
		//Check if the input are relevants: direction non zero and two distinct points for the segment 
		if( (dir.x==0 && dir.y==0) || (p1.x==p2.x && p1.y==p2.y))
			return null;

		if(p1.x==p2.x && dir.x==0)//the segment and direction are vertical lines : a1 is infinite : (1)x=constante=p1.x, (2): x=p1.x=p2.x=A.x
		{
			if(p1.x!=A.x)
				return null;
			else
			{
				Vector2d p1_p2 = new Vector2d(p2.x-p1.x,p2.y-p1.y);
				if(dir.dot(p1_p2)>=0) //dir is in the same direction the the vector starting in p1 and ending in p2
					intersectP=p2;
				else
					intersectP=p1;
			}



		}
		else if(p1.x==p2.x)//a is infinite : (1)x=p1.x, (2) y= a2 * x + b2
		{
			double a2 = dir.y/dir.x; 
			double Y= a2 * p1.x + (A.y - a2 * A.x);
			intersectP= new Vector2d(p1.x,Y);

			//check if point is in the segment and that the projection was done with respect to the correct direction
			Vector2d projVect = new Vector2d(intersectP.x-A.x,intersectP.y-A.y);

			if(!containValue(p1.y,p2.y, Y) || (!allDirection && projVect.dot(dir)<0) )
				return null;
			

		}
		else if(dir.x==0)// (1) y=ax+b, (2)x=dir.x
		{
			double a= (p2.y-p1.y)/(p2.x-p1.x);
			double b = p1.y-a*p1.x;
			double Y= a * A.x + b; 
			intersectP=new Vector2d(A.x,Y);
			if(!containValue(p1.x,p2.x, intersectP.x) || !containValue(p1.y,p2.y, intersectP.y) || (!allDirection && intersectP.dot(dir)<0))// check if the point is in the opposite direction
				return null;

		}
		else 
		{
			//(1) y=a1x+b1, (2) y=a2x+b2
			//a2 = dir.y/dir.x and b2 = (A.y - a2 * A.x)  

			double a1= (p2.y-p1.y)/(p2.x-p1.x);
			double b1 = p1.y-a1*p1.x;
			double a2 =dir.y/dir.x; 
			double b2 = A.y - a2 * A.x;
			if(a1== a2)//(1) y=a1x+b1, (2) y=a2x+b2
			{
				if(b1!=b2)
					return null;
				else 
				{
					Vector2d p1_p2 = new Vector2d(p2.x-p1.x,p2.y-p1.y);
					if(dir.dot(p1_p2)>=0) //dir is in the same direction the the vector starting in p1 and ending in p2
						intersectP=p2;
					else
						intersectP=p1;
				}
			}
			//X = (b1-b2)/(a2-a1)
			//Y= a2 * X + b2
			else //(1) y=ax+b, (2) y= dir.y/dir.x* x
			{
				double X=(b1-b2)/(a2-a1);
				double Y=0;
				if(a2>Math.pow(10, 12))//a2 is close to infinite, compte Y with 1
					Y=a1*X+ b1;
				else
					Y=a2*X+ b2;
				
				intersectP=new Vector2d(X,Y);
				//compute the vector [A,intersectP]
				Vector2d projVect = new Vector2d(intersectP.x-A.x,intersectP.y-A.y);

				if ( (!containValue(p1.x,p2.x, X)) || (!containValue(p1.y,p2.y, Y)) || (!allDirection && projVect.dot(dir)<0) )
					return null;
			}
		}
		return intersectP;
	}

	//Calculate the distance from A to segment, the normal to the segment, (or return null) and store those information in IntresectPoint
	public static IntersectPoint getProjectionDistance(Vector2d p1, Vector2d p2, Vector2d dir, Vector2d A)
	{
		Vector2d projectedP=projection(p1,p2,dir,A,false);
		if(projectedP==null)
			return null;
		//Compute the normal to the segment 

		//normal to segment [p2.x-p1.x,p2.y-p1.y] is [-1,1].[p2.y-p1.y,p2.x-p1.x](left 90°) or [1,-1].[p2.y-p1.y,p2.x-p1.x](right 90°)
		//plus we want the normal n to verify: n . intersectP >0
		Vector2d n = new Vector2d(p1.y-p2.y,p2.x-p1.x); // *[-1,1]
		n.normalize();

		Vector2d v = new Vector2d(dir.x,dir.y);
		v.normalize();

		Vector2d projVect = new Vector2d(projectedP.x-A.x,projectedP.y-A.y);
		if(n.dot(projVect)<0) //compute the normal such that the A point is considered to be INSIDE
			n=new Vector2d(-n.x, -n.y);
		else if (n.dot(projVect)==0) // if the direction and the segment where parallel
			if(n.dot(dir)<0) //check the normal direction using the original dir 
				n=new Vector2d(-n.x, -n.y);
			else if(n.dot(dir)==0)
			{
				//special case, handled in getEdgeInDirection
				return new IntersectPoint(projectedP.dot(v),0,null,projectedP);
			}
		double distance = projVect.dot(v);
		double distanceNormal= projVect.dot(n);
		//double distance = intersectP.length();
		return new IntersectPoint(distance,distanceNormal,n,projectedP);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
