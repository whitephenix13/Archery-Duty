package partie.AI;

import java.awt.Point;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import debug.DebugLog;
import gameConfig.InterfaceConstantes;
import partie.AI.A_Star.A_Star_Parameters;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Deplace;

public class A_Star_Helper {

	//BEFORE USING IT, MAKE SURE THAT GETNEXTPOS IS IMPLEMENTED FOR THE COLLIDABLE
	DebugLog aStarDebugLog = new DebugLog();
	
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
	public A_Star_Helper(double reevaluation_time,int max_step_size, Point exploration_angle, int redirection_length,float _smoothStrength)
	{
		aStarDebugLog.setActive(true);
		
		params = new A_Star_Parameters(reevaluation_time, redirection_length, exploration_angle, max_step_size,aStarDebugLog);
		smoothStrength=_smoothStrength;
	}
	

	public void setDebug(boolean val)
	{
		if(params!=null)
			params.setDebug(val);
	}

	public boolean PointReached(Collidable objectToMove,Vector2d prevDir, Point target,Point nextTarget,double speednorm)
	{
		//When the center is at less than 10% of the max_step_size, consider that the point is reached. 
		//This is to account for the fact that we actually want to hit the hitbox and not the target (hitbox is bigger than target)
		//Hitting the target is harder and required last minute rotation (which can cause collisions)
		
		double reach_distance =0.1 * params.max_step_size;
		Vector2d objMid = Hitbox.getObjMid(objectToMove);
		Vector2d dir = new Vector2d(target.x-objMid.x,target.y-objMid.y);
		
		if(dir.length() <= reach_distance)
			return true;
		else
			return false;

	}

	public ArrayList<Point> GetNextTargets(Collidable objectToMove,Vector2d dir, Vector2d _target, int max_num_target)
	{
		System.out.println("path null " + path==null);
		aStarDebugLog.log("Begin get next targets");
		if(path ==null){
			path = A_Star.FindPath(params,objectToMove, dir, _target, smoothStrength);
		}
		else{
			int[] newCurrentIndex = {-1};
			Point prevNextTarget = path.get(current_index);
			System.out.println("before update path");
			path = A_Star.UpdatePath(params,path, dir, objectToMove, _target, smoothStrength,false,prevNextTarget,newCurrentIndex);
			System.out.println("after update path");
			if(newCurrentIndex[0] != -1)
				current_index=newCurrentIndex[0];

		}
		if(path == null || path.size()==0)
			return null;
		//Check distance between objectToMove and path.get(current_index). If object is close (ie target reached), go to next point 
		double speednorm = objectToMove.getGlobalVit().length();
		int path_size = path.size();
		
		for(; current_index<path_size; ++current_index)
		{
			boolean pointReached = PointReached(objectToMove,dir,path.get(current_index),(current_index+1<path_size)?path.get(current_index+1):null, speednorm);
			if(!pointReached)
				break;
		}
		
		aStarDebugLog.log("End get next targets");
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

	public void OnDestroy()
	{
		A_Star.OnDestroy(params);
		params=null;
	}


}
