package partie.collision;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import org.junit.Test;

public class GJK_EPA_test {

	@Test
	public void basicCollision() {
		Polygon shapeA= new Polygon();
		shapeA.addPoint(0, 0);
		shapeA.addPoint(0, 100);
		shapeA.addPoint(100, 100);
		shapeA.addPoint(100, 0);

		
		Polygon shapeB= new Polygon();
		shapeB.addPoint(25, 25);
		shapeB.addPoint(25, 125);
		shapeB.addPoint(125, 125);
		shapeB.addPoint(125, 25);
		
		/*
		 * 
		 * 
		 * -75 125    25 125     125 125        
		 * 
		 *                  
		 * 
		 * -75  25    25  25     125  25
		 * 
		 *     0 0
		 * 
		 * -75 -75    25 -75   125 -75
		 * 
		 * */
		
		List<Vector2d> simplex = new ArrayList<Vector2d>();
		List<Vector2d> simplexCopy = new ArrayList<Vector2d>();
		Vector2d dir = new Vector2d(1,1);
		Vector2d minusDir = new Vector2d(dir);
		minusDir.negate();
		List<Vector2d> outNormal = new ArrayList<Vector2d>();
		List<Vector2d> outNormal2 = new ArrayList<Vector2d>();
		
		simplex=GJK_EPA.intersects(shapeB,shapeA, dir);
		for(Vector2d v2d:simplex)
			simplexCopy.add(new Vector2d(v2d));
				
		double closestEdgeDist = GJK_EPA.EPA(shapeB, shapeA, simplex, outNormal);
		assert closestEdgeDist == 75;
		assert ( outNormal.get(0).equals(new Vector2d(-1,0)) || outNormal.get(0).equals(new Vector2d(0,-1))) ;
		
		double closestEdgeDistInDirection = GJK_EPA.directionalEPA(shapeB, shapeA, simplexCopy,minusDir, outNormal2);
		assert Math.abs((closestEdgeDistInDirection - Math.sqrt(2) * 75)) <=0.000001;
		assert ( outNormal2.get(0).equals(new Vector2d(-1,0)) || outNormal2.get(0).equals(new Vector2d(0,-1))) ;
	}
	

	@Test
	public void collisionWithMatchingVertex() {
		Polygon shapeA= new Polygon();
		shapeA.addPoint(0, 0);
		shapeA.addPoint(0, 100);
		shapeA.addPoint(100, 100);
		shapeA.addPoint(100, 0);

		
		Polygon shapeB= new Polygon();
		shapeB.addPoint(100, 100);
		shapeB.addPoint(100, 200);
		shapeB.addPoint(200, 200);
		shapeB.addPoint(200, 100);
		List<Vector2d> simplex = new ArrayList<Vector2d>();
		List<Vector2d> simplexCopy = new ArrayList<Vector2d>();
		Vector2d dir = new Vector2d(0,1);
		Vector2d minusDir = new Vector2d(dir);
		minusDir.negate();
		List<Vector2d> outNormal = new ArrayList<Vector2d>();
		List<Vector2d> outNormal2 = new ArrayList<Vector2d>();
		
		simplex=GJK_EPA.intersects(shapeB,shapeA, dir);
		for(Vector2d v2d:simplex)
			simplexCopy.add(new Vector2d(v2d));
				
		double closestEdgeDist = GJK_EPA.EPA(shapeB, shapeA, simplex, outNormal);
		assert closestEdgeDist == 0:"Fail: "+closestEdgeDist;
		
		double closestEdgeDistInDirection = GJK_EPA.directionalEPA(shapeB, shapeA, simplexCopy,minusDir, outNormal2);
		assert closestEdgeDistInDirection == 0:"Fail "+closestEdgeDistInDirection;
	}
	@Test
	public void collisionWithMatchingEdge() {
		Polygon shapeA= new Polygon();
		shapeA.addPoint(0, 0);
		shapeA.addPoint(0, 100);
		shapeA.addPoint(100, 100);
		shapeA.addPoint(100, 0);

		
		Polygon shapeB= new Polygon();
		shapeB.addPoint(50, 25);
		shapeB.addPoint(50, 75);
		shapeB.addPoint(100, 75);
		shapeB.addPoint(100, 25);
		
		List<Vector2d> simplex = new ArrayList<Vector2d>();
		List<Vector2d> simplexCopy = new ArrayList<Vector2d>();
		Vector2d dir = new Vector2d(0,1);
		Vector2d minusDir = new Vector2d(dir);
		minusDir.negate();
		List<Vector2d> outNormal = new ArrayList<Vector2d>();
		List<Vector2d> outNormal2 = new ArrayList<Vector2d>();
		
		simplex=GJK_EPA.intersects(shapeB,shapeA, dir);
		for(Vector2d v2d:simplex)
			simplexCopy.add(new Vector2d(v2d));
				
		double closestEdgeDist = GJK_EPA.EPA(shapeB, shapeA, simplex, outNormal);
		assert closestEdgeDist == 50;
		
		double closestEdgeDistInDirection = GJK_EPA.directionalEPA(shapeB, shapeA, simplexCopy,minusDir, outNormal2);
		assert closestEdgeDistInDirection == 75;
	}
	@Test
	public void collisionWithMatchingPolygon() {
		Polygon shapeA= new Polygon();
		shapeA.addPoint(0, 0);
		shapeA.addPoint(0, 100);
		shapeA.addPoint(100, 100);
		shapeA.addPoint(100, 0);

		
		Polygon shapeB= new Polygon();
		shapeB.addPoint(0, 0);
		shapeB.addPoint(0, 100);
		shapeB.addPoint(100, 100);
		shapeB.addPoint(100, 0);
		
		List<Vector2d> simplex = new ArrayList<Vector2d>();
		List<Vector2d> simplexCopy = new ArrayList<Vector2d>();
		Vector2d dir = new Vector2d(1,1);
		Vector2d minusDir = new Vector2d(dir);
		minusDir.negate();
		List<Vector2d> outNormal = new ArrayList<Vector2d>();
		List<Vector2d> outNormal2 = new ArrayList<Vector2d>();
		
		simplex=GJK_EPA.intersects(shapeB,shapeA, dir);
		for(Vector2d v2d:simplex)
			simplexCopy.add(new Vector2d(v2d));
				
		double closestEdgeDist = GJK_EPA.EPA(shapeB, shapeA, simplex, outNormal);
		assert closestEdgeDist == 100;
		
		double closestEdgeDistInDirection = GJK_EPA.directionalEPA(shapeB, shapeA, simplexCopy,minusDir, outNormal2);
		assert Math.abs(closestEdgeDistInDirection - 100*Math.sqrt(2)) <0.000001;
	}
	@Test
	public void collisionWithNonOverlapingPolygon() {
		Polygon shapeA= new Polygon();
		shapeA.addPoint(0, 0);
		shapeA.addPoint(0, 100);
		shapeA.addPoint(100, 100);
		shapeA.addPoint(100, 0);

		
		Polygon shapeB= new Polygon();
		shapeB.addPoint(-50, -50);
		shapeB.addPoint(-50, -55);
		shapeB.addPoint(-55, -55);
		shapeB.addPoint(-55, -50);
		
		List<Vector2d> simplex = new ArrayList<Vector2d>();
		Vector2d dir = new Vector2d(1,1);
		Vector2d minusDir = new Vector2d(dir);
		minusDir.negate();

		simplex=GJK_EPA.intersects(shapeB,shapeA, dir);
		assert simplex == null;

	}
	@Test
	public void collisionWithRotatedPolygon() {
		Polygon shapeA= new Polygon();
		shapeA.addPoint(0, 0);
		shapeA.addPoint(0, 100);
		shapeA.addPoint(100, 100);
		shapeA.addPoint(100, 0);

		
		Polygon shapeB= new Polygon();
		shapeB.addPoint(50, 50);
		shapeB.addPoint(75, 75);
		shapeB.addPoint(65, 75);
		shapeB.addPoint(40, 50);
		
		List<Vector2d> simplex = new ArrayList<Vector2d>();
		List<Vector2d> simplexCopy = new ArrayList<Vector2d>();
		Vector2d dir = new Vector2d(1,1);
		Vector2d minusDir = new Vector2d(dir);
		minusDir.negate();
		List<Vector2d> outNormal = new ArrayList<Vector2d>();
		List<Vector2d> outNormal2 = new ArrayList<Vector2d>();
		
		simplex=GJK_EPA.intersects(shapeB,shapeA, dir);
		for(Vector2d v2d:simplex)
			simplexCopy.add(new Vector2d(v2d));
				
		double closestEdgeDist = GJK_EPA.EPA(shapeB, shapeA, simplex, outNormal);
		assert closestEdgeDist == 50;
		
		double closestEdgeDistInDirection = GJK_EPA.directionalEPA(shapeB, shapeA, simplexCopy,minusDir, outNormal2);

		assert closestEdgeDistInDirection >0;
	}
	
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
		int xpos = (direction.x>0 && Collision.round(outVector.x)==(int)Collision.round(outVector.x))? -1 : 0;
		int ypos = (direction.y>0 && Collision.round(outVector.y)==(int)Collision.round(outVector.y))? -1 : 0;
		//RES: vectOut= new Vector2d(Math.floor(outVector.x)+x+xequ,Math.floor(outVector.y)+y+yequ);
		return new Point(xneg+xpos,yneg+ypos);
	}
	
	@Test
	public void testColli(){
		//Not a real test, used for debugging purpose
		Polygon shapeB= new Polygon();
		shapeB.addPoint(400, 400);
		shapeB.addPoint(499, 400);
		shapeB.addPoint(499, 499);
		shapeB.addPoint(400, 499);
		
		Polygon shapeA= new Polygon();
		shapeA.addPoint(492, 413);
		shapeA.addPoint(531, 413);
		shapeA.addPoint(531, 499);
		shapeA.addPoint(492, 499);
		
		
		List<Vector2d> simplex = new ArrayList<Vector2d>();
		List<Vector2d> simplexCopy = new ArrayList<Vector2d>();
		Vector2d dir = new Vector2d(-1,0);
		Vector2d minusDir = new Vector2d(dir);
		minusDir.negate();
		List<Vector2d> outNormal = new ArrayList<Vector2d>();
		List<Vector2d> outNormal2 = new ArrayList<Vector2d>();
		
		simplex=GJK_EPA.intersects(shapeB,shapeA, dir);
		for(Vector2d v2d:simplex)
			simplexCopy.add(new Vector2d(v2d));
				
		double closestEdgeDist = GJK_EPA.EPA(shapeB, shapeA, simplex, outNormal);		
		double closestEdgeDistInDirection = GJK_EPA.directionalEPA(shapeB, shapeA, simplexCopy,minusDir, outNormal2);
		
		System.out.println("closestEdgeDist "+ closestEdgeDist +" closestEdgeDistInDirection "+ closestEdgeDistInDirection);
		
	}
	@Test
	public void specificCase1(){
		Polygon shapeB= new Polygon();
		shapeB.addPoint(800, 300);
		shapeB.addPoint(899, 300);
		shapeB.addPoint(899, 399);
		shapeB.addPoint(800, 399);
		
		Polygon shapeA= new Polygon();
		shapeA.addPoint(865, 396);
		shapeA.addPoint(904, 396);
		shapeA.addPoint(904, 482);
		shapeA.addPoint(865, 482);
		
		List<Vector2d> simplex = new ArrayList<Vector2d>();
		List<Vector2d> simplexCopy = new ArrayList<Vector2d>();
		Vector2d dir = new Vector2d(8,-4);
		Vector2d minusDir = new Vector2d(dir);
		minusDir.negate();
		List<Vector2d> outNormal = new ArrayList<Vector2d>();
		simplex=GJK_EPA.intersects(shapeB,shapeA, dir);
		for(Vector2d v2d:simplex)
			simplexCopy.add(new Vector2d(v2d));
		

		double closestEdgeDistInDirection = GJK_EPA.directionalEPA(shapeB, shapeA, simplexCopy,minusDir, outNormal);
		System.out.println("closestEdgeDistInDirection "+closestEdgeDistInDirection);
		assert closestEdgeDistInDirection <=100; 
	}
}
