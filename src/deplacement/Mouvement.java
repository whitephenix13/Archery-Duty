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
	
	public abstract Mouvement Copy(Object obj);
	/**
	 * @return the maximum length or height that the hitbox can reached for this motion(often =max(xtaille,ytaille). This value will be multiplied by sqrt(2) to handle diagonals of squares
	 * */
	public abstract int getMaxBoundingSquare(Object obj);
	public abstract boolean IsDeplacement(Mouvement m);
	public abstract boolean IsDeplacement(String s);
	public abstract void setSpeed(Collidable object, int anim);
	public abstract String droite_gauche(Object obj,int anim);
	public int updateAnimation(Object obj,int anim,int current_frame,double speedFactor) {
		return updateAnimation(obj,anim,current_frame,speedFactor,false);
	}
	public int updateAnimation(Object obj,int anim,int current_frame,double speedFactor,boolean log) {
		return animation.update(anim,current_frame,speedFactor,log);
	}
	}
