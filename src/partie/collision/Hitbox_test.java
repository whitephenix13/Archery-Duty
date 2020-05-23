package partie.collision;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import org.junit.Test;

import partie.collision.Collidable.XAlignmentType;
import partie.collision.Collidable.YAlignmentType;
import partie.collision.Hitbox.ExtendDirection;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Deplace;
public class Hitbox_test {
	@Test
	public void checkEqualsHitbox() {
		Polygon poly1 = new Polygon();
		poly1.addPoint(-2, 1);
		poly1.addPoint(2, 1);
		poly1.addPoint(2, -1);
		poly1.addPoint(-2, -1);
		
		Polygon poly2 = new Polygon();
		poly2.addPoint(2, 1);
		poly2.addPoint(-2, 1);
		poly2.addPoint(-2, -1);
		poly2.addPoint(2, -1);
		
		Polygon poly3 = new Polygon();
		poly3.addPoint(-2, 1);
		poly3.addPoint(2, 1);
		poly3.addPoint(-2, -1);
		poly3.addPoint(2, -1);
		Hitbox hit1 = new Hitbox(poly1);
		Hitbox hit2 = new Hitbox(poly2);
		Hitbox hit3 = new Hitbox(poly3);
		assertTrue(hit1.equalsHitbox(hit2));
		assertTrue(!hit1.equalsHitbox(hit3));
	}
	
	@Test
	public void checkRotateHitbox(){
		Hitbox hit1 = Hitbox.createSquareHitbox(10, 60,20, 100); // center is 15,80; delta is: 10,40
		hit1 = Hitbox.rotateHitbox(hit1,Deplace.vectorToAngle(new Vector2d(0,-50)));
		//Center is kept : 15,80 
		//new delta is 40,10 (rotate by -90°)
		//new hitbox is:   xmin = -5, xmax = 35, ymin = 75, ymax = 85
		Hitbox expectedHit1 = Hitbox.createSquareHitbox(-5, 75, 35, 85);
		
		assertTrue("got "+hit1+" expected "+expectedHit1,hit1.equalsHitbox(expectedHit1));
		
		Hitbox hit2 = Hitbox.createSquareHitbox(6, 5, 37, 11);
		System.out.println("Center "+hit2.getCenter());

	}
	@Test
	public void checkSlidedHitbox() {
		Polygon poly1 = new Polygon();
		poly1.addPoint(-2, 1);
		poly1.addPoint(2, 1);
		poly1.addPoint(2, -1);
		poly1.addPoint(-2, -1);
		
		Polygon poly2 = new Polygon();
		poly2.addPoint(-2, 1);
		poly2.addPoint(-2, -1);
		poly2.addPoint(2, -1);
		poly2.addPoint(2, 1);

		Hitbox res1 = Hitbox.getSlidedHitbox(new Hitbox(poly1), new Point(-1,0), new Point(1,0));
		Hitbox res2 = Hitbox.getSlidedHitbox(new Hitbox(poly2), new Point(-1,0), new Point(1,0));
		
		System.out.println("slided: "+ res1);
		System.out.println("slided: "+ res2);
		
		Polygon expected_poly1 = new Polygon();
		expected_poly1.addPoint(-3, 1);
		expected_poly1.addPoint(-3, -1);
		expected_poly1.addPoint(3, -1);
		expected_poly1.addPoint(3, 1);
		
		Polygon expected_poly2 = new Polygon();
		expected_poly2.addPoint(-3, -1);
		expected_poly2.addPoint(3, -1);
		expected_poly2.addPoint(3, 1);
		expected_poly2.addPoint(-3, 1);
		
		assertTrue("got "+res1+" expected "+Hitbox.polyToString(expected_poly1),res1.equalsHitbox(new Hitbox(expected_poly1)));
		assertTrue("got "+res2+" expected "+Hitbox.polyToString(expected_poly2),res2.equalsHitbox(new Hitbox(expected_poly2)));
		
	}
	@Test
	public void checkGetPoint() {
		Polygon poly1 = new Polygon();
		poly1.addPoint(-2, -1);
		poly1.addPoint(-2, 1);
		poly1.addPoint(2, 1);
		poly1.addPoint(2, -1);
		
		Point pos = new Point(1,2);
		double rotation = Math.PI/4;
		AffineTransform tr = AbstractModelPartie.getRotatedTransform(pos, new Point(), rotation, new Vector2d(1,1));
		Hitbox hit = (new Hitbox(poly1)).transformHitbox(tr, new Point(), new Point());

		//Hitbox is    
		//         (0,0)
		//      (-1,1)
		//                   (3,3)
		//                (2,4)
		//
		Vector2d p1 = hit.getPoint(rotation, XAlignmentType.CENTER, YAlignmentType.CENTER);
		assertEquals(pos.x,p1.x,0);
		assertEquals(pos.y,p1.y,0);
		
		Vector2d p2 = hit.getPoint(rotation, XAlignmentType.LEFT, YAlignmentType.CENTER);
		assertEquals(-0.5,p2.x,0);
		assertEquals(0.5,p2.y,0);
		
		Vector2d p3 = hit.getPoint(rotation, XAlignmentType.RIGHT, YAlignmentType.CENTER);
		assertEquals(2.5,p3.x,0);
		assertEquals(3.5,p3.y,0);
		
		Vector2d p4 = hit.getPoint(rotation, XAlignmentType.CENTER, YAlignmentType.TOP);
		assertEquals(1.5,p4.x,0);
		assertEquals(1.5,p4.y,0);
		
		Vector2d p5 = hit.getPoint(rotation, XAlignmentType.CENTER, YAlignmentType.BOTTOM);
		assertEquals(0.5,p5.x,0);
		assertEquals(2.5,p5.y,0);
		
		Vector2d p6 = hit.getPoint(rotation, XAlignmentType.LEFT, YAlignmentType.BOTTOM);
		assertEquals(-1,p6.x,0);
		assertEquals(1,p6.y,0);
		
		Vector2d p7 = hit.getPoint(rotation, XAlignmentType.LEFT, YAlignmentType.TOP);
		assertEquals(0,p7.x,0);
		assertEquals(0,p7.y,0);
		
		Vector2d p8 = hit.getPoint(rotation, XAlignmentType.RIGHT, YAlignmentType.BOTTOM);
		assertEquals(2,p8.x,0);
		assertEquals(4,p8.y,0);
		
		Vector2d p9 = hit.getPoint(rotation, XAlignmentType.RIGHT, YAlignmentType.TOP);
		assertEquals(3,p9.x,0);
		assertEquals(3,p9.y,0);
	}
	@Test
	public void checkExtend() {

		Hitbox hit = new Hitbox(new Point(0,0),new Point(3,3), new Point(2,4), new Point(-1,1));
		Point extension = new Point(1,3);//this is extension for unrotated. rotated extension must take into account cos(angle). 
		//Also be carefull that hitbox have polygon( int) so be careful with rounding
		Vector2d direction = new Vector2d(1,1);
		
		//Hitbox is    
		//         (0,0)
		//      (-1,1)
		//                   (3,3)
		//                (2,4)
		//
		Hitbox forwardHit = Hitbox.extend(hit,extension, direction,ExtendDirection.FORWARD);
		Hitbox forwardHitExpected = new Hitbox(new Point(0,0),new Point(4,4), new Point(3,5), new Point(-1,1));
		assertTrue("expected " +forwardHitExpected +" got "+ forwardHit,forwardHitExpected.equalsHitbox(forwardHit));
		
		Hitbox backwardHit = Hitbox.extend(hit,extension, direction,ExtendDirection.BACKWARD);
		Hitbox backwardHitExpected = new Hitbox(new Point(-1,-1),new Point(3,3), new Point(2,4), new Point(-2,0));
		assertTrue("expected " +backwardHitExpected +" got "+ backwardHit,backwardHitExpected.equalsHitbox(backwardHit));
		
		//Watch out with rounding issues. direction is sqrt(2),sqrt(2) which is 0.7 so 3*0.7 = 2.1 
		Hitbox topHit = Hitbox.extend(hit,extension, direction,ExtendDirection.TOP);
		Hitbox topHitExpected = new Hitbox(new Point(2,-2),new Point(5,1), new Point(2,4), new Point(-1,1));
		assertTrue("expected " +topHitExpected +" got "+ topHit,topHitExpected.equalsHitbox(topHit));
		
		Hitbox bottomHit = Hitbox.extend(hit,extension, direction,ExtendDirection.BOTTOM);
		Hitbox bottomHitExpected = new Hitbox(new Point(0,0),new Point(3,3), new Point(0,6), new Point(-3,3));
		assertTrue("expected " +bottomHitExpected +" got "+ bottomHit,bottomHitExpected.equalsHitbox(bottomHit));
		
		Hitbox allHit = Hitbox.extend(hit,extension, direction,ExtendDirection.ALL);
		Hitbox allHitExpected = new Hitbox(new Point(1,-3),new Point(6,2), new Point(1,7), new Point(-4,2));
		assertTrue("expected " + allHitExpected +" got "+ allHit,allHitExpected.equalsHitbox(allHit));
	}
	
	@Test
	public void checkSlidedHitboxFromPosition(){
		Hitbox hit = new Hitbox(new Point(0,0),new Point(4,0), new Point(4,2), new Point(0,2));
		Point extension = new Point(0,0);
		Point originalPosition = new Point(48,99);
		Point targetPosition = new Point(48,199);
		/*
		 * Rotated is expected to be  3 -1; 3 3; 1 3; 1 -1 
		 * Middle of the hitbox is (2,1) 
		 *
		 * */
		Hitbox slidedFromPositionsWithoutExtensionWithoutTip = Hitbox.getSlidedHitboxFromPosition(hit,originalPosition,targetPosition,extension,false);
		//Ori center: (50,100)
		//Ori poly: (49,98),(51,98),(51,102),(49,102)
		//Dest center: (50,200)
		//Dest poly: (49,198),(51,198),(51,202),(49,202)
		Hitbox expectedWithoutExtensionWithoutTipHit = new Hitbox(new Point(49,98),new Point(51,98), new Point(51,202), new Point(49,202));
		assertTrue("expected " + expectedWithoutExtensionWithoutTipHit +" got "+ slidedFromPositionsWithoutExtensionWithoutTip,
				expectedWithoutExtensionWithoutTipHit.equalsHitbox(slidedFromPositionsWithoutExtensionWithoutTip));

		//with extension
		extension = new Point(1,2); //for the unrotated so it becomes (2,1) for the rotated
		//Ori center: (50,100)
		//Ori poly: (47,97),(53,97),(53,103),(47,103)
		//Dest center: (50,200)
		//Dest poly: (47,197),(53,197),(53,203),(47,203)
		Hitbox slidedFromPositionsWithExtensionWithoutTip = Hitbox.getSlidedHitboxFromPosition(hit,originalPosition,targetPosition,extension,false);
		Hitbox expectedWithExtensionWithoutTipHit = new Hitbox(new Point(47,97),new Point(53,97), new Point(53,203), new Point(47,203));
		assertTrue("expected " + expectedWithExtensionWithoutTipHit +" got "+ slidedFromPositionsWithExtensionWithoutTip,
				expectedWithExtensionWithoutTipHit.equalsHitbox(slidedFromPositionsWithExtensionWithoutTip));
		//with tip
		extension = new Point(0,0); 
		//Ori center: (50,100)
		//Ori poly: (49,98),(51,98),(51,102),(49,102)
		//Tip is 0,2
		//Dest center: (50,198)
		//Dest poly: (49,196),(51,196),(51,200),(49,200)
		Hitbox slidedFromPositionsWithoutExtensionWithTip = Hitbox.getSlidedHitboxFromPosition(hit,originalPosition,targetPosition,extension,true);
		Hitbox expectedWithoutExtensionWithTipHit =new Hitbox(new Point(49,98),new Point(51,98), new Point(51,200), new Point(49,200));
		assertTrue("expected " + expectedWithoutExtensionWithTipHit +" got "+ slidedFromPositionsWithoutExtensionWithTip,
				expectedWithoutExtensionWithTipHit.equalsHitbox(slidedFromPositionsWithoutExtensionWithTip));
		
		//with extension and tip 
		extension = new Point(1,2); //for the unrotated so it becomes (2,1) for the rotated
		//Ori center: (50,100)
		//Ori poly: (47,97),(53,97),(53,103),(47,103)
		//Tip is 0,2
		//Dest center: (50,198)
		//Dest poly: (47,195),(53,195),(53,201),(47,201)
		Hitbox slidedFromPositionsWithExtensionWithTip = Hitbox.getSlidedHitboxFromPosition(hit,originalPosition,targetPosition,extension,true);
		Hitbox expectedWithExtensionWithTipHit = new Hitbox(new Point(47,97),new Point(53,97), new Point(53,201), new Point(47,201));
		assertTrue("expected " + expectedWithExtensionWithTipHit +" got "+ slidedFromPositionsWithExtensionWithTip,
				expectedWithExtensionWithTipHit.equalsHitbox(slidedFromPositionsWithExtensionWithTip));
	}
}
