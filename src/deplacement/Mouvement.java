package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import collision.Collidable;
import types.Hitbox;

public abstract class Mouvement{
	public static String DROITE = "DROITE";
	public static String GAUCHE = "GAUCHE";

	public List<Integer> xtaille= new ArrayList<Integer>() ;
	public List<Integer> ytaille= new ArrayList<Integer>() ;
	public List<Hitbox> hitbox = new ArrayList<Hitbox>();
	public List<Hitbox> hitbox_rotated = new ArrayList<Hitbox>();//used to transform the initial hitbox into a rotated one

	public List<Integer> x_center_tir = new ArrayList<Integer>();
	public List<Integer> y_center_tir = new ArrayList<Integer>();

	public List<Integer> x_rot_pos = new ArrayList<Integer>();
	public List<Integer> y_rot_pos = new ArrayList<Integer>();
	
	protected Animation animation = new Animation();
	public boolean animEndedOnce(){return animation.isEndedOnce();}
	public int type_mouv;
	
	public List<Point> asListPoint(List<Integer> x, List<Integer> y)
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
	public List<Hitbox> createHitbox(List<List<Point>> list)
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
	
	public abstract Mouvement Copy(String type);
	public abstract boolean IsDeplacement(Mouvement m);
	public abstract boolean IsDeplacement(String s);
	public abstract void setSpeed(String type,Collidable object, int anim);
	public abstract String droite_gauche(String type,int anim);
	public int updateAnimation(String type,int anim,int current_frame,double speedFactor) {
		return animation.update(anim,current_frame,speedFactor);
	}
	}
