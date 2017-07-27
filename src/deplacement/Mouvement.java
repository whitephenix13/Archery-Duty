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
	
	public abstract Mouvement Copy(String type);
	public abstract boolean IsDeplacement(Mouvement m);
	public abstract boolean IsDeplacement(String s);
	public abstract void setSpeed(String type,Collidable object, int anim);
	public abstract String droite_gauche(String type,int anim);
	public int updateAnimation(String type,int anim,int current_frame,double speedFactor) {
		return animation.update(anim,current_frame,speedFactor);
	}
	}
