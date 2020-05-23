package debug;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import partie.collision.Hitbox;

public class DebugMain {
	private int test; 
	
	public static void main(String[] args) throws Exception 
	{	
		Point p1 = new Point(1+1,3);
		Point p2 = new Point(2,3);
		System.out.println(p1.equals(p2));
		System.out.println(p1 == (p2));
		Hitbox untr_hit = Hitbox.createSquareHitbox(0, 0, 8, 4);
		Hitbox untr_newhit = Hitbox.createSquareHitbox(0, 0, 24, 20);

		Point pos = new Point(10,20);
		Point topLeftAnchor = new Point(4,2); //assume hitbox of 6,2
		double rotation = Math.PI/2;
		float scaling = 0.5f;
		
		AffineTransform tr = getRotatedTransform(pos,topLeftAnchor,rotation,scaling);

		AffineTransform tr_plus1 = getRotatedTransform(new Point(pos.x+1,pos.y),topLeftAnchor,rotation,scaling);

		//Assume now that we have a hitbox of 12,6, that we want to align to middle right 
		
		Hitbox hit = untr_hit.copy().transformHitbox(tr, new Point(), new Point());
		//anchor translated is at (10,20)+(8,4)/2 = 14,22 
		//after rotation, width is 4 and heignt is 8 (90° rotation) 
		//hit is then 12,18 (top left) -> 16,26 (bottom left)
		//For scale of .5, => width after rotation : 2 and height is 4 
		//hit is then 13,20 (top left) -> 15,24 (bottom left) 
		System.out.println(hit.toString());
		
		AffineTransform newtr = getRotatedTransform(pos,new Point(12,10),rotation,scaling);
		
		//8,8 being hit2 anchor - hit1 anchor
		AffineTransform newtr_afterpos = getRotatedTransform(new Point(pos.x-8,pos.y-8),new Point(12,10),rotation,scaling);

		Hitbox newhit = untr_newhit.copy().transformHitbox(newtr, new Point(), new Point());
		Hitbox newhit_afterpos = untr_newhit.copy().transformHitbox(newtr_afterpos, new Point(), new Point());

		System.out.println(newhit.toString());
		System.out.println(newhit_afterpos.toString());
		
	}
	public static AffineTransform getRotatedTransform(Point pos,Point topLeftAnchor,double rotation, float scaling)
	{		
		AffineTransform trans = new AffineTransform();
		if(topLeftAnchor == null)
			topLeftAnchor = new Point();
		//Translate the object to its position
		trans.translate(pos.x + topLeftAnchor.x*(1-scaling), pos.y + topLeftAnchor.y*(1-scaling));
		//scale to object around its anchor (otherwise it is by default around its top left corner)
		trans.scale(scaling, scaling);
		//rotate it around its anchor (most of the time its center)
		trans.rotate(rotation, topLeftAnchor.x, topLeftAnchor.y);

		return trans;
	}
}
