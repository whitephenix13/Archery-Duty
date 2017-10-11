package fleches.destructrice;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import types.Hitbox;

public class Parameters_fleche_bogue {
	public double ADD_ARROW_TIME = 0.10; //add arrow every x sec
	public double MAX_DISTANCE = 80; //distance before the arrows stops 
	public int NB_ARROW = 32;
	public double current_distance=0;
	public boolean reached_max_distance = false; //state 0 -> state 1 
	public boolean shoot_arrows = false; //state 1 -> state 2

	public float damageMult =1;
	public List<Fleche_bogue> bogueArrows = new ArrayList<Fleche_bogue>();//contains the seven created arrows 

	public double last_add_time = 0;
	public int nbarrow=0;
	public Vector2d center = null;//center: it is used to compute the distance from the arrow to it and is also used as center to create new arrows, relative to shooter top left

	public int lastFrameUpdate = -1;
	
	//parameters for the creation of the arrow 
	public double creat_rot = 0;

}
