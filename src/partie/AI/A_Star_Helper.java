package partie.AI;

import java.awt.Point;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import partie.AI.A_Star.A_Star_Parameters;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Deplace;
import partie.modelPartie.AbstractModelPartie;

public class A_Star_Helper {

	//BEFORE USING IT, MAKE SURE THAT GETNEXTPOS IS IMPLEMENTED FOR THE COLLIDABLE

	private A_Star_Parameters params;
	ArrayList<Point> path = null;
	int current_index = 0;
	float smoothStrength = 0;
	double REEVALUATION_TIME = 0.05*Math.pow(10, 9); // 0.05 sec (50ms)

	/**
	 * 
	 * @param _reevaluation_time how frequently should the path be recomputed 
	 * @param _max_step_size maximum value by which the object can move at each iteration
	 * @param _explorationAngle Maximum value by which the object can turn as a fraction of Math.Pi
	 * @param redirection_l This value should be equal to the estimated distance that the target can move within REEVALUATION_TIME
	 */
	public A_Star_Helper(double _reevaluation_time,int _max_step_size, Point _explorationAngle, int redirection_l,float _smoothStrength)
	{
		initAStarParameters(_reevaluation_time, redirection_l, _explorationAngle, _max_step_size);
		smoothStrength=_smoothStrength;
	}
	
	public void initAStarParameters(double reevaluation_time, int redirection_length, Point exploration_angle, int max_step_size)
	{
		params = new A_Star_Parameters(reevaluation_time, redirection_length, exploration_angle, max_step_size);
		
		if(params.DEBUG)
		{
			params.debugInvalidCandidates= new ArrayList<Point>();
			params.debugSlidedHitbox = new ArrayList<Hitbox>(); 
		}
		params.last_reevaluation_time=-1; 

		//if we consider that the direction is (1,0), we want the point above in the grid (and below) to be within the correct angle hence 
		//let grid_scale_divider = n , we search n such that  (100,100/n).normalize().dot((1,0)) >= limCosAngle 
		//(100,100/n).normalize() =( 1/Norm, 1/(n * Norm) ) with Norm = sqrt(1 + 1/n^2} )
		//Solving 1/sqrt(1 + 1/n^2 ) >= lim 
		//always true if lim<=0 hence angle >= Pi/2 
		// 1 >= lim^2 (1+ 1/(n^2) )
		// 1/lim^2 -1 >= 1/(n^2)
		//n^2 >= lim^2 / (1-lim^2 )
		// with lim = cos(angle) 
		//n^2 >= cotan^2(angle)
		//n >= cotan(angle))
		//We take n as the next int that is a multiple of 2 to ensure that the direction left right top down is still possible 
		double limAngle = params.exploration_angle.x * Math.PI/params.exploration_angle.y;
		params.grid_scale_divider = limAngle>=Math.PI/2? 1 : nextEven(1.0/Math.tan(limAngle)); 
		params.grid_size = max_step_size / params.grid_scale_divider;
		params.grid_max_index= (int)(params.grid_scale_divider*1.5* A_Star.MAX_DISTANCE/params.grid_size+1);//*1.5 since we need to be able to go at least 0.75 * to the left
		params.limCosAngle = Math.cos(Math.min(limAngle + Math.PI/180,2*Math.PI));
		params.MAX_NUMBER_EXPLORED_CELLS= (int) (0.25 * params.grid_max_index*params.grid_max_index);
		
		params.debugInvalidCandidates = new ArrayList<Point>();
		params.debugSlidedHitbox = new ArrayList<Hitbox>();
	}
	
	int nextEven(double x)
	{
		int topInt = (int) Math.ceil(x);
		if(topInt%2 == 0)
			return topInt;
		else
			return topInt+1;
	}
	public void setDebug(boolean val)
	{
		if(params!=null)
			params.setDebug(val);
	}

	public boolean PointReached(AbstractModelPartie partie,Collidable objectToMove,Vector2d prevDir, Point target,Point nextTarget,double speednorm)
	{
		//TODO: 
		//Case 0 When the center is at less than 10% of the max_step_size, consider that the point is reached. 
		//This is to to handle the imprecision of the direction at short distance (ie: when arrow is shot)  
		//Case 1: compare with the previous dir: if the new one is opposite to the previous: the object was crossed 
		
		double reach_distance =0.1 * params.max_step_size;
		Vector2d objMid = Hitbox.getObjMid(partie, objectToMove);
		Vector2d dir = new Vector2d(target.x-objMid.x,target.y-objMid.y);
		
		if(dir.length() <= reach_distance)
			return true;
		/*else if(prevDir.dot(dir)>0)
			return false;*/
		else
			return false;//true

	}

	public ArrayList<Point> GetNextTargets(AbstractModelPartie partie,Collidable objectToMove,Vector2d dir, Vector2d _target, int max_num_target)
	{
		if(path ==null){
			path = A_Star.FindPath(params,partie,objectToMove, dir, _target, smoothStrength);
		}
		else{
			int[] newCurrentIndex = {-1};
			Point prevNextTarget = path.get(current_index);
			path = A_Star.UpdatePath(params,partie,path, dir, objectToMove, _target, smoothStrength,false,prevNextTarget,newCurrentIndex);
			
			if(newCurrentIndex[0] != -1)
				current_index=newCurrentIndex[0];

		}
		if(path == null || path.size()==0)
			return null;
		//Check distance between objectToMove and path.get(current_index). If object is close (ie target reached), go to next point 
		double speednorm = objectToMove.getGlobalVit(partie).length();
		int path_size = path.size();
		boolean pointReached = PointReached(partie,objectToMove,dir,path.get(current_index),(current_index+1<path_size)?path.get(current_index+1):null, speednorm);
		while(pointReached)
		{
			current_index++;
			if(current_index>= path_size)
				break;
			pointReached = PointReached(partie,objectToMove,dir,path.get(current_index),(current_index+1<path_size)?path.get(current_index+1):null,speednorm);
		}

		if(current_index>= path.size())
			return new ArrayList<Point>();
		else
		{
			ArrayList<Point> res = new ArrayList<Point>();
			for(int i=current_index; i<Math.min(current_index+max_num_target,path.size()); ++i)
				res.add(path.get(i));
			return res;
		}
	}

	public ArrayList<Point> getPath()
	{
		return path;
	}
	public String pathToString()
	{
		if(path==null)
			return "";
		return A_Star.PathToString(path);
	}

	public void OnDestroy(AbstractModelPartie partie)
	{
		A_Star.OnDestroy(params,partie);
		params=null;
	}


}
