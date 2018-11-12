package AI;

import java.awt.Point;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Deplace;
import partie.AbstractModelPartie;
import types.Hitbox;

public class A_Star_Helper {
	
	//BEFORE USING IT, MAKE SURE THAT GETNEXTPOS IS IMPLEMENTED FOR THE COLLIDABLE
	
	private A_Star algo;
	ArrayList<Point> path = null;
	int current_index = 0;
	double REACHED_DISTANCE = 30; //unused
	float smoothStrength = 0;
	double REEVALUATION_TIME = 0.05*Math.pow(10, 9); // 0.05 sec (50ms)

	public A_Star_Helper()
	{
		algo = new A_Star();
	}

	/**
	 * 
	 * @param _reevaluation_time how frequently should the path be recomputed 
	 * @param _max_step_size maximum value by which the object can move at each iteration
	 * @param _explorationAngle Maximum value by which the object can turn as a fraction of Math.Pi
	 * @param redirection_l This value should be equal to the estimated distance that the target can move within REEVALUATION_TIME
	 */
	public A_Star_Helper(double _reevaluation_time,int _max_step_size, Point _explorationAngle, int redirection_l,float _smoothStrength)
	{
		algo = new A_Star(_reevaluation_time,_max_step_size,_explorationAngle,redirection_l);
		smoothStrength=_smoothStrength;
	}
	
	public void setDebug(boolean val)
	{
		algo.setDebug(val);
	}
	
	public boolean PointReached(AbstractModelPartie partie,Collidable objectToMove,Point target,double speednorm)
	{
		Vector2d objMid = Hitbox.getObjMid(partie, objectToMove);
		Vector2d dir = new Vector2d(target.x-objMid.x,target.y-objMid.y);
		dir.normalize();
		dir.scale(speednorm);
		Vector2d nextObjMid = Hitbox.getHitboxCenter(objectToMove.getNextEstimatedHitbox(partie, Deplace.XYtoAngle(dir.x, dir.y),objectToMove.anim)); // see where the object will be at next frame 
		if(nextObjMid ==null)
			nextObjMid = objMid;
		
		double dist  = Math.sqrt(Math.pow(objMid.x-target.x,2) + Math.pow(objMid.y - target.y,2));
		double nextDist = Math.sqrt(Math.pow(nextObjMid.x-target.x,2) + Math.pow(nextObjMid.y - target.y,2));
		
		if(dist < nextDist)
			return true;

		else 
			return false;
	}

	public Point GetNextTarget(AbstractModelPartie partie,Collidable objectToMove,Vector2d dir, Point _target)
	{
		if(path ==null){
			path = algo.FindPath(partie,objectToMove, dir, _target, smoothStrength);
		}
		else{
			int[] newCurrentIndex = {-1};
			Point prevNextTarget = path.get(current_index);
			path = algo.UpdatePath(partie,path, dir, objectToMove, _target, smoothStrength,false,prevNextTarget,newCurrentIndex);
			if(newCurrentIndex[0] != -1)
				current_index=newCurrentIndex[0];

		}
		if(path == null)
			return null;
		//Check distance between objectToMove and path.get(current_index). If object is close (ie target reached), go to next point 
		double speednorm = objectToMove.getGlobalVit(partie).vect2d().length();
		boolean pointReached = PointReached(partie,objectToMove,path.get(current_index),speednorm);
		while(pointReached)
		{
			current_index++;
			if(current_index>= path.size())
				break;
			pointReached = PointReached(partie,objectToMove,path.get(current_index),speednorm);
			//TODO: handle reachable
		}
		
		
		if(pointReached || (current_index>= path.size()))
			return null;
		else
			return path.get(current_index);
	}
	
	public ArrayList<Point> getPath()
	{
		return path;
	}
	public String pathToString()
	{
		if(path==null)
			return "";
		return algo.PathToString(path);
	}
	
	public void OnDestroy(AbstractModelPartie partie)
	{
		algo.OnDestroy(partie);
	}
	
	
}
