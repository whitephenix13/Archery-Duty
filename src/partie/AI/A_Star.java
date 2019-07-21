package partie.AI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Vector2d;

import org.junit.Test;

import debug.DebugBreak;
import debug.DebugDraw;
import gameConfig.InterfaceConstantes;
import partie.AI.A_Star.A_Star_Parameters;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.PartieTimer;
import utils.PointHelper;

public class A_Star {
	
	public enum ERROR {TOO_MUCH_EXPLORED,TOO_FAR,NOT_FOUND,NONE}
	public enum DEBUG_INVALID_CANDIDATES {ALL,INDICES_ANGLE,INDICES,ANGLE}

	public static int MAX_DISTANCE = InterfaceConstantes.WINDOW_WIDTH + 2* InterfaceConstantes.TAILLE_BLOC;//Maximum distance that the moving object can travel
	static int MAX_ITERATION = 300;
	public static class A_Star_Parameters
	{
		protected boolean DEBUG = false;
		public void setDebug(boolean val){DEBUG=val;}

		protected boolean LOG = false; //Manually set to true to get the logs 

		private ERROR lastError = ERROR.NONE;
		public ERROR getLastError(){return lastError;}
		
		protected double REEVALUATION_TIME;
		protected int REDIRECTION_LENGTH; //This is used to delete the last x points when Updating the path
		//This value should be equal to the estimated distance that the target can move within REEVALUATION_TIME
		protected Point exploration_angle; //Maximum value by which the object can turn as a fraction of Math.Pi
		protected int max_step_size; //maximum value by which the object can move at each iteration
		public A_Star_Parameters()
		{
			this(0.05*Math.pow(10, 9), 2* InterfaceConstantes.TAILLE_BLOC, new Point(1,4), InterfaceConstantes.TAILLE_BLOC);
		}
		public A_Star_Parameters(double reevaluation_time, int redirection_length, Point exploration_angle, int max_step_size)
		{
			REEVALUATION_TIME = reevaluation_time; //default  0.05*Math.pow(10, 9);
			REDIRECTION_LENGTH = redirection_length;//default 2* InterfaceConstantes.TAILLE_BLOC;
			this.exploration_angle = exploration_angle;//default new Point(1,4); 
			this.max_step_size = max_step_size;//default InterfaceConstantes.TAILLE_BLOC;
		}
		
		//Set only once
		protected double last_reevaluation_time=-1; //last time that the path was reevaluated 
		protected int grid_scale_divider = 1;// set 2 to have half distance between grid cells for more accuracy (1=> angle = Pi/4, 0, -Pi/4, 2=> -Pi/8, 0, Pi/8)) 
		protected int grid_max_index =-1;
		protected int grid_size = InterfaceConstantes.TAILLE_BLOC; 
		protected double limCosAngle = 0; //the maximum value of the cos. if val<limCosAngle then the point in not in the correct angle 
		protected int MAX_NUMBER_EXPLORED_CELLS = 0; //if the number of cells explored is greater than this, stop the algorithm 
		
		//local variables
		protected Point initialPos = new Point(); //position at which the object start moving. it determines the 0,0 of the grid
		protected Vector2d worldTarget = new Vector2d();
		protected A_Star_Candidate[][] grid = null; //0 if empty, 1 if visited 
		protected ArrayList<Point> listCandidates = new ArrayList<Point>(); // list of candidate cell index to visit 
		protected int number_explored_cells = 0; //if the number of cells explored is greater than MAX_NUMBER_EXPLORED_CELLS, stop the algorithm 

		//Debug variables
		protected ArrayList<Point> debugInvalidCandidates; // list of candidates that where invalid 
		protected ArrayList<Hitbox> debugSlidedHitbox ; //list of slided hitboxes that does not collide
		protected  DEBUG_INVALID_CANDIDATES debug_invalid = DEBUG_INVALID_CANDIDATES.ANGLE;
		
	}
	
	//WARNING: the algorithm always compute the distance from the original start point. As a consequence, will be biased and might tend to attack the enemy
	//by strange direction if he moved a lot from its original position

	//INPUT: All collidable object (as structured for the world, use Collidable.objectInBoundingSquare for others), object to move, target Point 
	//PARAMETERS: Re-evaluation time, grid_size 
	//OUTPUT: list of Points 

	/*boolean DEBUG = false;
	boolean LOG = false;

	public void setDebug(boolean val){DEBUG=val;}
	//Parameters to set 
	double REEVALUATION_TIME = 0.05*Math.pow(10, 9); // 0.05 sec (50ms)
	int REDIRECTION_LENGTH = 2* InterfaceConstantes.TAILLE_BLOC; //This is used to delete the last x points when Updating the path
	//This value should be equal to the estimated distance that the target can move within REEVALUATION_TIME
	Point explorationAngle = new Point(1,4); //Maximum value by which the object can turn as a fraction of Math.Pi
	int max_step_size = InterfaceConstantes.TAILLE_BLOC; //maximum value by which the object can move at each iteration

	//from its starting point. The algorithm will find no path if the object or target is more than MAX_DISTANCE from the starting point

	//Constants after initialization 
	double last_reevaluation_time=-1; //last time that the path was reevaluated 
	int grid_scale_divider = 1;// set 2 to have half distance between grid cells for more accuracy (1=> angle = Pi/4, 0, -Pi/4, 2=> -Pi/8, 0, Pi/8)) 
	int grid_size = InterfaceConstantes.TAILLE_BLOC; 
	double limCosAngle = 0; //the maximum value of the cos. if val<limCosAngle then the point in not in the correct angle 

	//local variables
	Point initialPos = new Point(); //position at which the object start moving. it determines the 0,0 of the grid
	Vector2d worldTarget = new Vector2d();
	A_Star_Candidate[][] grid = null; //0 if empty, 1 if visited 
	ArrayList<Point> listCandidates = new ArrayList<Point>(); // list of candidate cell index to visit 
	ArrayList<Point> debugInvalidCandidates; // list of candidates that where invalid 
	ArrayList<Hitbox> debugSlidedHitbox ; //list of slided hitboxes that does not collide
	private DEBUG_INVALID_CANDIDATES debug_invalid = DEBUG_INVALID_CANDIDATES.ANGLE;
	
	private ERROR lastError = ERROR.NONE;
	public ERROR getLastError(){return lastError;}*/

	public A_Star()
	{
	}


	/**
	 * 
	 * @param index: cellIndex of the new candidate 
	 * @param prevNeighbor: A_Star_Candidate that causes the addition of this as a new candidate (null if initialization)
	 */
	private static void AddCandidate(A_Star_Parameters params,Point index, A_Star_Candidate prevNeighbor)
	{
		params.listCandidates.add(index);
		params.grid[index.x][index.y]= new A_Star_Candidate(index,prevNeighbor);
		params.grid[index.x][index.y].isCandidate=true;
	}

	/**
	 * 
	 * @param index cellIndex of the candidate to select
	 */
	private static void SelectCandidate(A_Star_Parameters params,Point index)
	{
		params.listCandidates.remove(index);
		params.grid[index.x][index.y].isCandidate=false;
		params.number_explored_cells+=1;
	}

	/**
	 * 
	 * @param index cellIndex of the candidate to remove
	 * @return returns the removed candidate 
	 */
	private static A_Star_Candidate RemoveCell(A_Star_Parameters params,Point index)
	{
		params.listCandidates.remove(index);
		A_Star_Candidate cand = params.grid[index.x][index.y];
		if(!cand.isCandidate)
			params.number_explored_cells-=1;
			
		params.grid[index.x][index.y]= null;
		return cand;
	}
	/**
	 * @param pos: world position
	 * @return cell index corresponding to a world position
	 */
	private static Point PosToCell(A_Star_Parameters params,Vector2d pos)
	{
		return new Point((int)Math.round((pos.x-params.initialPos.x)/params.grid_size + params.grid_max_index/2),(int)Math.round((pos.y-params.initialPos.y)/params.grid_size +  params.grid_max_index/2));
	}
	/**
	 * @param pos: world position
	 * @return cell index corresponding to a world position
	 */
	public static Point PosToCell(A_Star_Parameters params,Point pos)
	{
		return PosToCell(params,new Vector2d(pos.x,pos.y));
	}
	public static Point CellToPos(A_Star_Parameters params,Point cell)
	{
		return new Point((cell.x - params.grid_max_index/2) *params.grid_size + params.initialPos.x, (cell.y - params.grid_max_index/2) *params.grid_size + params.initialPos.y );
	}
	/**
	 * Specific distance function that returns in how many moves we can reach B from A (diagonal is a legal move) 
	 * @param A
	 * @param B
	 * @return
	 */
	private static double Dist(Point A, Point B)
	{
		return Dist(new Vector2d(A.x,A.y),B);
	}
	private static double Dist(Vector2d A, Point B)
	{
		return Math.max(Math.abs(B.x-A.x), Math.abs(B.y-A.y));
	}
	/**
	 * 
	 * @param index: index of the element in the grid
	 * @return false if the index is incorrect, true otherwise 
	 */
	private static boolean checkIndex(A_Star_Parameters params,Point index)
	{
		int size = 2*params.grid_scale_divider*MAX_DISTANCE+1;
		if(index.x<0 || (index.y<0) || (index.x>=size) || (index.y>=size))
			return false;
		else
			return true;
	}
	/**
	 * 
	 * @param index: index of the element in the grid
	 * @param A_Star_Candidate[][] grid the matrice of visited cells
	 * @param testEmpty set to true if the result should be true if the cell is empty, false otherwise
	 * @return false if the index is incorrect or that the grid is not empty, true otherwise 
	 * @return
	 */
	private static boolean checkGridIndex(A_Star_Parameters params,Point index,A_Star_Candidate[][] grid,boolean testEmpty)
	{
		if(!checkIndex(params,index))
			return false;
		if(grid != null){
			if(grid[index.x][index.y] == null)
				return testEmpty;
			else
				return !testEmpty;
		}
		else
			return true;
	}

	/**
	 * @param candidate
	 * @param elem
	 * @param dir
	 * @return true if the vector elem -> candidate is at most explorationAngle apart from dir
	 */
	public static boolean CheckNeighborsAngle(A_Star_Parameters params,Point candidate,Point elem,Vector2d dir)
	{
		//dir should be normalized
		Vector2d v = new Vector2d(candidate.x-elem.x,candidate.y-elem.y);
		v.normalize();
		return v.dot(dir) >=params.limCosAngle;
	}

	/**
	 * Return true if there is no collision
	 * @param partie
	 * @param elem
	 * @param objectToMove
	 * @return
	 */
	public static boolean CheckCollision(A_Star_Parameters params,AbstractModelPartie partie,Point elem,Point max_dist_object)
	{
		Point worldCoordinate = CellToPos(params,elem);
		Point p1 = new Point(worldCoordinate.x-max_dist_object.x/2,worldCoordinate.y-max_dist_object.y/2);
		Point p2 = new Point(worldCoordinate.x+max_dist_object.x/2,worldCoordinate.y-max_dist_object.y/2);
		Point p3 = new Point(worldCoordinate.x+max_dist_object.x/2,worldCoordinate.y+max_dist_object.y/2);
		Point p4 = new Point(worldCoordinate.x-max_dist_object.x/2,worldCoordinate.y+max_dist_object.y/2);
		Hitbox estimatedHit = new Hitbox(p1,p2,p3,p4);
		boolean res = !Collision.isWorldCollision(partie, estimatedHit, true);
		return res;
	}
	
	/**
	 * Check whether the object would collide when moving from the current cell to the neighbor. For that we compute the slided hitbox (hitbox of the trajectory of the object)
	 * The objecthitbox is the hitbox of the object oriented towards the target_pos 
	 */
	public static boolean CheckSlidedCollision(A_Star_Parameters params,AbstractModelPartie partie,Point current_pos,Point target_pos,Hitbox objectUnrotatedHitbox)
	{
		//extend hitbox to avoid unanticipated collisions due to calculation errors (arrow might be 1 aside its path)
		Hitbox extendedUnrotatedHitbox = Hitbox.extend(objectUnrotatedHitbox,new Point(4,4));
		Hitbox slidedHitbox =Hitbox.getSlidedHitbox(extendedUnrotatedHitbox, current_pos, target_pos);
		boolean res = !Collision.isWorldCollision(partie, slidedHitbox, true);
		if(params.DEBUG)
			params.debugSlidedHitbox.add(slidedHitbox);
		return res;
	}
	/**
	 * Find the next neighbors to explore for the A* algorithm next step
	 * @param elem: the current position
	 * @param dir: direction to which the element is aiming
	 * @param grid: grid of empty/explored area 
	 * @return List of point representing the valid neighbors
	 */
	public static void AddNeightbors(A_Star_Parameters params,AbstractModelPartie partie,A_Star_Candidate elem,Hitbox objectUnrotatedHitbox,
			Vector2d dir, A_Star_Candidate[][]grid)
	{
		if(params.DEBUG){
			params.debugInvalidCandidates.clear();
			params.debugSlidedHitbox.clear();
		}

		/* X X 0 . 0
		 * X w . . .      A points toward top left
		 * 0 . A . 0      0 represents the original grid
		 * . . . . .      . represents the padded grid due to grid_scale_divider 
		 * 0 . 0 . 0      w reprensents the possible neighbors for an angle of Pi/8
		 *                X reprensents the neighbors to return for an angle of Pi/8
		 */
		dir.normalize();
		//Given that we resize the grid, we are guaranteed to have at least two external cell in range (Hypothesis)
		//Loop over all external cells and only keep the ones that fulfill the angle condition 
		
	
		//Get the external neighbors candidate. As we are in cell index, adding grid_scale_divider correspond to move by grid_size*grid_scale_divider pixels
		int xstart = elem.cellIndex.x-params.grid_scale_divider;
		int xend = elem.cellIndex.x+params.grid_scale_divider;
		int ystart = elem.cellIndex.y-params.grid_scale_divider;
		int yend = elem.cellIndex.y+params.grid_scale_divider;
		
		//First of all, consider the case where the target is a point within the neighbors but not in the border. 
		//Correction: always add target when found otherwise it might happen that the angle is wrong so the target is missed
		//TODO: this case should never happen since the algo should stop if |currentPos - target| < grid_size
		/*if(grid_scale_divider>1)
		{
			if( (xstart+1 <= target.x) && (target.x <= xend -1) && (ystart+1 <= target.y) && (target.y <= yend -1) )
			{
					AddCandidate(target,elem);
			}
		}*/
		//double loop to loop over border elements
		//if the grid is empty + the indices are in the grid + the angle is correct + there is no collision : add this point as a candidate 
		for(int x = xstart; x<=xend; x+=1 )
		{
			if(x==xstart || (x==xend) )
			{
				for(int y = ystart; y<=yend; y+=1 )
				{
					Point p = new Point(x,y);

					if(checkGridIndex(params,p,grid,true) && CheckNeighborsAngle(params,p,elem.cellIndex,dir) &&
							CheckSlidedCollision(params,partie,elem.getWorldPos(params),CellToPos(params,p),objectUnrotatedHitbox)){
						AddCandidate(params,p,elem);
					}
					else if(params.DEBUG)
					{
						if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ALL))
							params.debugInvalidCandidates.add(p);	
						else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && CheckNeighborsAngle(params,p,elem.cellIndex,dir) )
							params.debugInvalidCandidates.add(p);
						else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.INDICES) && checkGridIndex(params,p,grid,true) )
							params.debugInvalidCandidates.add(p);
						else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && checkGridIndex(params,p,grid,true) && CheckNeighborsAngle(params,p,elem.cellIndex,dir))
							params.debugInvalidCandidates.add(p);
					}
				}
			}
			else
			{
				Point p1 = new Point(x,ystart);
				Point p2 = new Point(x,yend);

				if(checkGridIndex(params,p1,grid,true) && CheckNeighborsAngle(params,p1,elem.cellIndex,dir)&&
						CheckSlidedCollision(params,partie,elem.getWorldPos(params),CellToPos(params,p1),objectUnrotatedHitbox)){
					AddCandidate(params,p1,elem);
				}
				else if(params.DEBUG)
				{
					if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ALL))
						params.debugInvalidCandidates.add(p1);
					else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && CheckNeighborsAngle(params,p1,elem.cellIndex,dir) )
						params.debugInvalidCandidates.add(p1);
					else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.INDICES) && checkGridIndex(params,p1,grid,true) )
						params.debugInvalidCandidates.add(p1);
					else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && checkGridIndex(params,p1,grid,true) && CheckNeighborsAngle(params,p1,elem.cellIndex,dir))
						params.debugInvalidCandidates.add(p1);
				}
				if(checkGridIndex(params,p2,grid,true) && CheckNeighborsAngle(params,p2,elem.cellIndex,dir)&&
						CheckSlidedCollision(params,partie,elem.getWorldPos(params),CellToPos(params,p2),objectUnrotatedHitbox)){
					AddCandidate(params,p2,elem);
				}
				else if(params.DEBUG)
				{
					if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ALL))
						params.debugInvalidCandidates.add(p2);
					else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && CheckNeighborsAngle(params,p2,elem.cellIndex,dir) )
						params.debugInvalidCandidates.add(p2);
					else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.INDICES) && checkGridIndex(params,p2,grid,true) )
						params.debugInvalidCandidates.add(p2);
					else if(params.debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && checkGridIndex(params,p2,grid,true) && CheckNeighborsAngle(params,p2,elem.cellIndex,dir))
						params.debugInvalidCandidates.add(p2);
				}
			}
		}
	}

	/**
	 * Return the heuristic value for the A* algorithm 
	 * @param elem: element to evaluate 
	 * @param start: start position of the object to move
	 * @param target: target of the path 
	 * @return
	 */
	private static double score(A_Star_Parameters params,A_Star_Candidate prevBest, A_Star_Candidate elem, Vector2d world_target)
	{
		double score =  elem.distance + Dist(world_target,elem.getWorldPos(params));
		//add penalty if the vector prevBest,elem does not point toward target
		if(prevBest!= null)
		{
			Vector2d v1 = new Vector2d(elem.getWorldPos(params).x-prevBest.getWorldPos(params).x,elem.getWorldPos(params).y-prevBest.getWorldPos(params).y);
			Vector2d v2 = new Vector2d(world_target.x-prevBest.getWorldPos(params).x,world_target.y-prevBest.getWorldPos(params).y);
			v1.normalize();
			v2.normalize();
			score += (1-(v1.dot(v2)))/2;
		}
		return score;
			
	}

	/**
	 * Find a path between objectToMove and target
	 * @param objectToMove: object that will move along the path
	 * @param dir: direction to which the object is moving/pointing
	 * @param target: targeted location 
	 * @param partie: used to get access to collidable objects 
	 * @return a list of point ordered by closest destination (target is at the end) or null if no path was found 
	 */
	public static ArrayList<Point> FindPath(A_Star_Parameters params,AbstractModelPartie partie,Collidable objectToMove,Vector2d dir, Vector2d _target, float smoothStrength)
	{
		return FindPath(params,partie,objectToMove,dir,_target,smoothStrength,null,false,false);
	}
	public static ArrayList<Point> FindPathFromUpdate(A_Star_Parameters params,AbstractModelPartie partie,Collidable objectToMove,Vector2d dir, Vector2d _target, float smoothStrength,Point initPos, boolean recomputePath)
	{
		return FindPath(params,partie,objectToMove,dir,_target,smoothStrength,initPos,recomputePath,true);
	}
	
	static void debugDraw(final A_Star_Parameters params,final AbstractModelPartie partie, final Point max_dist_object)
	{
		if(partie.debugDraw==null){
			partie.debugDraw = new DebugDraw(){
				@Override
				public void draw(Graphics g){

					for(int xInd=0; xInd<params.grid.length;++xInd)
					{
						for(int yInd=0; yInd<params.grid[0].length;++yInd)
						{
							A_Star_Candidate debugCand = params.grid[xInd][yInd];
							if(debugCand != null){
								if(debugCand.isCandidate)
								{
									g.setColor(Color.RED);
								}
								else
								{
									g.setColor(Color.GREEN);
								}
								Point p = debugCand.getWorldPos(params);
								g.fillRect(p.x+partie.xScreendisp-2,p.y +partie.yScreendisp-2, 5, 5);
								g.drawRect(p.x+partie.xScreendisp -max_dist_object.x/2, p.y+partie.yScreendisp -max_dist_object.y/2
										, max_dist_object.x, max_dist_object.y);
							}
						}
					}
					for(int i=0; i<params.debugInvalidCandidates.size();++i)
					{
						g.setColor(Color.BLACK);
						Point p = CellToPos(params,params.debugInvalidCandidates.get(i));
						g.fillRect(p.x+partie.xScreendisp-2,p.y +partie.yScreendisp-2, 5, 5);
						g.drawRect(p.x+partie.xScreendisp -max_dist_object.x/2, p.y+partie.yScreendisp -max_dist_object.y/2
								, max_dist_object.x, max_dist_object.y);
					}
					for(int i=0; i<params.debugSlidedHitbox.size();++i)
					{
						g.setColor(Color.DARK_GRAY);
						Hitbox hit = params.debugSlidedHitbox.get(i);
						Hitbox temp_hit = Hitbox.plusPoint(hit, partie.getScreenDisp(), true);
						for(int n=1; n<temp_hit.polygon.npoints+1; ++n){
							int n1 = n-1;
							int n2 = n;
							if(n==temp_hit.polygon.npoints)
								n2 = 0;
							g.drawLine(temp_hit.polygon.xpoints[n1], temp_hit.polygon.ypoints[n1] 
									, temp_hit.polygon.xpoints[n2], temp_hit.polygon.ypoints[n2] );
						}
					}
					
					g.setColor(Color.BLUE);
					Point p = PointHelper.RountVecToPoint(params.worldTarget);
					g.fillRect(p.x+partie.xScreendisp-2,p.y +partie.yScreendisp-2, 5, 5);

					g.setColor(Color.BLACK);
				}

			};
		}
	}

	/**
	 * Find a path between objectToMove and target
	 * @param objectToMove: object that will move along the path
	 * @param dir: direction to which the object is moving/pointing
	 * @param target: targeted location 
	 * @param partie: used to get access to collidable objects 
	 * @param initIndex: set this to the last explored value if the path is recomputed from an existing one 
	 * @return a list of point ordered by closest destination (target is at the end) or null if no path was found 
	 */

	public static ArrayList<Point> FindPath(A_Star_Parameters params,final AbstractModelPartie partie,Collidable objectToMove,Vector2d dir, Vector2d _target, float smoothStrength,
			Point initPos,boolean recomputePath,boolean calledFromUpdatePath)
	{
	
		if(params.last_reevaluation_time == -1)
		{
			params.last_reevaluation_time = PartieTimer.me.getElapsedNano();
		}
		params.lastError= ERROR.NONE;

		Vector2d objWorldPos = Hitbox.getObjMid(partie,objectToMove);
		if(objWorldPos==null && params.LOG)
			System.out.println(objectToMove.getHitbox(partie.INIT_RECT, partie.getScreenDisp()));
			//Manually tuned so that an arrow can move through two blocks. Although considering "(int) Math.ceil( Math.sqrt(2) * objectToMove.deplacement.getMaxBoundingSquare(objectToMove))"
			//is the safest to always find a way, it is also too restrictive. It is less frutrating to see a arrow fail a path because it wasn't shot perfectly
			//(player mistake) than because the algo decided that there is no enough space 
			//Point max_bounding_rect= objectToMove.deplacement.getMaxBoundingRect(objectToMove);
		Hitbox objectUnrotatedHitbox = objectToMove.getDeplacementHitbox(0); //WARNING: this might change 
		//The object can rotate by maximum explorationAngle. The bounding is then extended to take into account this rotation (square embedding the rotated hitbox)		
		//      _ x      / \   
		//   y |_|   =>  \ /
		//				  
		// y after rotation - y contribution to height + x contribution to height = y cos(ang) + x sin(ang) 
		// what we want is max_(ang) ( ycos(ang) + x sin(ang) ) =(Derivation)> -y sin(ang) + x cos(ang) =0  => ang = atan(x/y)
		//as the rotation is within [0,explorationAngle], the optimal is atan(x/y) is within interval, exploration angle otherwise  
		// x after rotation = xcos(ang) + y sin (ang)>  => ang = atan(y/x) 
		//TODO: y after rotation = y sin (ang) + , ycos(ang) = cos(ang)
		
			//assert(max_bounding_rect.x != 0);
			//assert(max_bounding_rect.y != 0);
			//double x_optimal_angle = Math.abs(Math.atan(max_bounding_rect.y/max_bounding_rect.x));
			//double y_optimal_angle = Math.PI/2 - x_optimal_angle;
		double d_exploration_angle =  params.exploration_angle.x * Math.PI/params.exploration_angle.y;
		
			//double x_max_angle = Math.min(x_optimal_angle, d_exploration_angle);
			//double y_max_angle = Math.min(y_optimal_angle, d_exploration_angle);

		//Point max_dist_object= new Point((int)Math.ceil(max_bounding_rect.x * Math.cos(x_max_angle)+ max_bounding_rect.y * Math.sin(x_max_angle)), 
		//		(int)Math.ceil(max_bounding_rect.y * Math.cos(y_max_angle)+ max_bounding_rect.x * Math.sin(y_max_angle)));
		//TODO: change to value above when all bugs are corrected. Remember to consider the oriented hitbox when evaluating whether there is a collision or not.
			//int i_max_dist_object = (int) Math.ceil( 0.75 * objectToMove.deplacement.getMaxBoundingSquare(objectToMove));
			//Point max_dist_object= new Point(i_max_dist_object,i_max_dist_object); //TODO: only for debugging 
		if(params.DEBUG){
			int max_dist = objectToMove.getDeplacement().getMaxBoundingSquare();
			debugDraw(params,partie,new Point(max_dist,max_dist));
		}
		//Only happen when generating the path for the first time. Shouldn't trigger when recomputing path 
		if(params.grid ==null)
		{
			//generate grid: generate thinner grid depending on exploration angle
			params.grid = new A_Star_Candidate[params.grid_max_index][params.grid_max_index] ; 
			//Arrays.fill(grid, null);
			//Remember the initial Pos, see the warning at the beginning for more information 
			params.initialPos =PointHelper.VecToPoint(objWorldPos);
		}
		//Dont set target to grid coordinate since it can be between to grid cells. 
		if(initPos == null && params.LOG)
			System.out.println("Object pos "+ ((objWorldPos.y-params.initialPos.y)/params.grid_size + MAX_DISTANCE) +" (int) " + (int)((objWorldPos.y-params.initialPos.y)/params.grid_size + MAX_DISTANCE));
		
		//Set object to grid coordinates
		Point startPos =PointHelper.RountVecToPoint(objWorldPos);
		//In the case where the path is recomputed, we start from a different position
		if(initPos != null)
			startPos = initPos;
		Point startPosIndex = PosToCell(params,startPos);

		params.worldTarget = _target;

		//Check if the initialPos and the object/initIndex or target are not too far one from the other 
		if(Dist(startPos,params.initialPos)> MAX_DISTANCE || Dist(params.worldTarget,params.initialPos)> MAX_DISTANCE)
		{
			if(params.LOG)
				System.out.println(Dist(startPos,params.initialPos)+" between pos "+ Dist(params.worldTarget,params.initialPos) +" between target "+ MAX_DISTANCE +" max distance");
			params.lastError = ERROR.TOO_FAR;
			return null;
		}
		
		//In case where the path is recomputed, the listCandidates date from last update
		if(initPos==null || recomputePath)//initPos !=null -> find path is used to extend a path. We assume that the starting point is already selected 
			AddCandidate(params,startPosIndex,null);
		//Iterate until the target is reached or the list of neighbors to explore (listCandidates) is empty 
		boolean targetReached = false;
		A_Star_Candidate last_candidate = null;
		int iter = 0; 
		do{
			if(params.DEBUG){
				DebugBreak.breakAndUpdateGraphic(partie);
			}
			if(params.LOG)
				System.out.println("Find path" + iter+" dir " + dir +" candidates "+ params.listCandidates); 

			/*String s="";
			for(int iter = 0; iter<listCandidates.size(); ++i)
			{
				s+= " " listCandidates.get(iter);
			}*/
			//Look for the best candidate (initialize with candidate 0 )
			Point ind = params.listCandidates.get(0);
			double minScore = score(params,params.grid[ind.x][ind.y].prevNeighbor,params.grid[ind.x][ind.y],params.worldTarget);
			int minCandIndex = 0;
			for(int i=1; i<params.listCandidates.size(); ++i)
			{
				ind = params.listCandidates.get(i);
				double score = score(params,params.grid[ind.x][ind.y].prevNeighbor,params.grid[ind.x][ind.y],params.worldTarget);
				if(score < minScore)
				{
					minScore=score;	
					minCandIndex=i;
				}
			}
			//Get the best candidate and remove it from the list
			Point minIndex= params.listCandidates.get(minCandIndex);
			SelectCandidate(params,minIndex);
			A_Star_Candidate bestCand = params.grid[minIndex.x][minIndex.y];
			A_Star_Candidate bestCandPrev = bestCand.prevNeighbor;
			
			String bestCandPrevPos = bestCandPrev==null? "null" : bestCandPrev.getWorldPos(params).toString();
			if(params.LOG)
				System.out.println("\tbest cand"  +bestCand.getWorldPos(params)+ " prev best " + bestCandPrevPos + " target "+ params.worldTarget);
			
			// update the direction 
			if( bestCandPrev !=null){
				dir = new Vector2d(bestCand.cellIndex.x-bestCandPrev.cellIndex.x,bestCand.cellIndex.y-bestCandPrev.cellIndex.y);
				dir.normalize(); 
			}

			
			//Check if target is reached 
			if(Dist(params.worldTarget,bestCand.getWorldPos(params)) <params.grid_size){
				Point pWorldTarget = PointHelper.RountVecToPoint(params.worldTarget);
				if(CheckSlidedCollision(params,partie,bestCand.getWorldPos(params),pWorldTarget,objectUnrotatedHitbox)){
					last_candidate= bestCand;
					targetReached=true;
				}
			}
			//Add neighbors to candidate 
			if(!targetReached){
				if(params.LOG)
					System.out.println("Dir before add candidates " + dir);
				//update the list of candidates 
				AddNeightbors(params,partie,bestCand,objectUnrotatedHitbox,dir,params.grid);
			}
		
			if(params.number_explored_cells >= params.MAX_NUMBER_EXPLORED_CELLS)
			{
				params.lastError = ERROR.TOO_MUCH_EXPLORED;
			}
			iter++;
		}
		while(!targetReached && (params.listCandidates.size()>0 ) && iter <=MAX_ITERATION && (params.number_explored_cells <= params.MAX_NUMBER_EXPLORED_CELLS));
		
		if(params.number_explored_cells > params.MAX_NUMBER_EXPLORED_CELLS)
			params.lastError = ERROR.TOO_MUCH_EXPLORED;
		ArrayList<Point> path = new ArrayList<Point>();

		//no path found
		if(!targetReached)
		{
			params.lastError = ERROR.NOT_FOUND;
			return null;
		}
		//path found: rebuild it by iterating from last candidate 
		else
		{
			A_Star_Candidate path_elem = last_candidate;
			while(path_elem != null)
			{
				//If we reach the starting point (if any was given), stop building the path since it will be concatenate in update path 
				if((initPos != null) && startPosIndex.equals(path_elem.cellIndex) )
					break;
				path.add(path_elem.getWorldPos(params));
				path_elem = path_elem.prevNeighbor;
			}
			Collections.reverse(path);
		}
		//Only smooth the path if it was call from find path and not update path 
		if(!calledFromUpdatePath)
			path = SmoothPath(path, smoothStrength,0);
		return path;
	}
	
	private static void OnRemovePathPoint(A_Star_Parameters params,Point removedIndex,A_Star_Candidate neighborRequestingRemove)
	{
		//remove element at index and update the grid
		A_Star_Candidate removed_cand  = RemoveCell(params,removedIndex);

		//check for its neighbors if the removed element was the prevNeighbor of the A_Star_Candidate
		//if so, remove this candidate from the list 
		boolean addAsCandidate = false; 
		ArrayList<Point> addAsCandidateNeighborIndexList = new ArrayList<Point>();

		//iterate over neighbors
		for(int x=-params.grid_scale_divider;x<=params.grid_scale_divider;x+=1)
		{
			for(int y=-params.grid_scale_divider;y<=params.grid_scale_divider;y+=1)
			{
				if(x==0 && y ==0)
					continue;
				Point neighbor_index = new Point(removedIndex.x+x,removedIndex.y+y);
				//If index is valid and cell is not empty, remove neighbor if prevNeighbor is removed_cand
				if(checkGridIndex(params,neighbor_index,params.grid,false)){
					A_Star_Candidate removedNeighbor = params.grid[neighbor_index.x][neighbor_index.y]; //guarenteed not null due to checkGridIndex
					A_Star_Candidate removedNeighborPrevNeighbor = removedNeighbor.prevNeighbor;
				
					if(removedNeighborPrevNeighbor != null)
					{
						if(removedNeighborPrevNeighbor.equals(removed_cand)){
							OnRemovePathPoint(params,neighbor_index,removed_cand);
						}
					}
					//Add the removed candidate in the listCandidates if there is a neighbor that is not in the listCandidate 
					if(!removedNeighbor.isCandidate)
					{
						addAsCandidate=true;
						addAsCandidateNeighborIndexList.add(neighbor_index);
					}
				}
			}
		}
		if(addAsCandidate){
			//Check if one of the neighbor is still an non empty/non candidate cell after reursive removal
			for(Point addAsCandidateNeighborIndex : addAsCandidateNeighborIndexList)
				if(checkGridIndex(params,addAsCandidateNeighborIndex,params.grid,false))
					if(!params.grid[addAsCandidateNeighborIndex.x][addAsCandidateNeighborIndex.y].isCandidate){
						AddCandidate(params,removedIndex,params.grid[addAsCandidateNeighborIndex.x][addAsCandidateNeighborIndex.y]);
						break; //add it only once
					}
		}
	}
	
	/**
	 * Update the path (after some collidable moved)
	 * @param path: a list of point ordered by closest destination from object to target
	 * @param objectToMove: object that will move along the path
	 * @param target: targeted location 
	 * @param partie: used to get access to collidable objects 
	 * @param orginalDir: direction to which the object is moving/pointing
	 * @param smoothStrength 0 for no smoothing, 1 for full smoothing 
	 * @param force set to true to ignore REEVALUATION_TIME and force an update 
	 * @param prevNextTarget current point toward which the object is moving to.
	 * @param outCurrentIndex new index such that path[outCurrentIndex] corresponds to the next point to move to. -1 to keep the current one
	 * @return the updated path with the same format 
	 */
	public static ArrayList<Point> UpdatePath(A_Star_Parameters params,AbstractModelPartie partie,ArrayList<Point> path, Vector2d orginalDir, Collidable objectToMove, Vector2d _target,
			float smoothStrength, boolean force, Point prevNextTarget, int[] outCurrentIndex)
	{
		double time = PartieTimer.me.getElapsedNano();
		if(force || ((time - params.last_reevaluation_time) > params.REEVALUATION_TIME) )
		{
			
			params.last_reevaluation_time=time;

			params.lastError= ERROR.NONE;
						
			//remove the point that where already crossed from the path, but not from the grid/listCandiates as we they were explored
			int current_path_index= path.indexOf(prevNextTarget);
			if(params.LOG)
				System.out.println("***Path before crossed deletion (0 -> " + (current_path_index-1)+")" + path);
			for(int j=0;j<current_path_index;j++)
			{
				path.remove(0);
			}
			outCurrentIndex[0] = 0;
			if(params.worldTarget.equals(_target)){
				return path; //path where crossed points where removed 
			}
			else
				params.worldTarget=_target;
			//   if path contains target delete item (and refill list candidate,...) until target is reached
			//		else delete last points and rerun algorithm between the last point and the target
			

			int i=0;
			boolean perfectMatch = false;

			for(i=0; i<path.size(); ++i)
			{
				if(Dist(_target,path.get(i))==0)
				{
					perfectMatch=true;
					break;
				}
			}
			int startIndex = i+1;//index from which the deletion should start
			//Case in which the target is not in the path 
			if(i>=path.size())
			{
				int num_point_deleted = params.REDIRECTION_LENGTH/params.grid_size + 1;
				startIndex = Math.max(path.size()-num_point_deleted,0) ;
			}
			
			if(params.LOG)
				System.out.println("***Path before last points deletion ("+startIndex+"->"+(path.size()-1)+")" + path);
			
			//Delete the last x points 
			for(int j=path.size()-1;j>=startIndex;--j)
			{
				Point removedP = PosToCell(params,path.remove(j));

				//update listCandidates + grid 
				OnRemovePathPoint(params,removedP,null);
				if(params.DEBUG){
					DebugBreak.breakAndUpdateGraphic(partie);
				}
			}

			if(perfectMatch){
				outCurrentIndex[0] = path.indexOf(prevNextTarget);
				if(outCurrentIndex[0]==-1)//arrow already crossed the road of the object
					return null;
				else
					return path;
			}
			//recompute the end of the path and concatenate it 
			else
			{
				if(params.LOG)
					System.out.println("\n==========Recomputation of A* Path========");
				//Compute the correct direction 
				int originalPathSize = path.size();
				Vector2d dir = orginalDir;
				if(params.LOG)
					System.out.println("***OriginalDir = "+dir+" for path "+ path);
				//As we deleted the last 5 points, we have to build the path from the point path_n-6 
				//as we know that the arrow will come from  point path_n-6 towards point path_n-5, this gives the direction to use for the exploration
				if(originalPathSize>=1)
				{
					Point lastIndex = PosToCell(params,path.get(path.size()-1));
					A_Star_Candidate last_cand = params.grid[lastIndex.x][lastIndex.y];
					A_Star_Candidate last_cand_prev = last_cand.prevNeighbor;					

					if(last_cand_prev !=null){
						dir = new Vector2d(last_cand.cellIndex.x-last_cand_prev.cellIndex.x,last_cand.cellIndex.y-last_cand_prev.cellIndex.y);
						dir.normalize(); 
						if(params.LOG)
							System.out.println("***Dir recomputed = "+dir +" between "+ last_cand_prev.getWorldPos(params) +" and "+last_cand.getWorldPos(params) +" for path \n"+path );
					}

				}
				
				ArrayList<Point> endPath = null;
				
				//As the points crossed have already been removed from the path, there are two possibilities:
				//1) the path still has at least 1 point (which is the next target) and so the explored cells and candidates can be kept and the path extended (hence only the end is recomputed)
				//2) the path is empty after the removal. This means that the object is close to the target. In that case recompute everything and delete all candidates since we create a new path 
				boolean builtFromScratch = path.size()==0;
				//Extend the existing path by recomputing the last points 
				if(!builtFromScratch)
				{
					if(params.LOG)
						System.out.println("path containing destination " +path +" |||\n... " );

					//Compute the path FROM THE LAST POINT IN THE PATH 
					endPath = FindPathFromUpdate(params,partie,objectToMove,dir, _target,smoothStrength,path.get(originalPathSize-1),false);
					if(params.LOG)
						System.out.println("\t\t endPath for pos "+(path.get(originalPathSize-1))+" " +endPath );
				}
				// recompute from scratch 
				else
				{
					if(params.LOG)
						System.out.println("path not containing destination");
					for(int xInd=0; xInd<params.grid.length;++xInd)
					{
						for(int yInd=0; yInd<params.grid[0].length;++yInd)
						{
							params.grid[xInd][yInd] = null;
						}
					}
					params.listCandidates.clear();
					//TODO: give an initPos so that the grid is always the same 
					//TODO: give the next path point to reach as init pos so that you are sure that the grid is the same (ie: arrow direction is smooth and doesn't need sharp turn)
					
					endPath = FindPathFromUpdate(params,partie,objectToMove,dir, _target, smoothStrength,prevNextTarget,true);//TODO:prevNextTarget replaced null
				}

				if(endPath==null)
					return null;
				//Concatenate paths only if not built from scratch
				int init_size = path.size();
				if(builtFromScratch){
					path=endPath;
					outCurrentIndex[0] = 0;
				}
				else
				{
					for(int concI =0; concI<endPath.size();++concI)
					{
						path.add(endPath.get(concI));
					}	
					outCurrentIndex[0] = endPath==null ? -1 : path.indexOf(prevNextTarget);
				}
				//[0,1,2]
				//[5,4,3]
				//=> [0,1,2,3,4,5]: we want 2 , 3 ,4 ,5 to be smoothed but SmoothPath doesn't smooth the first index given hence the -2
				path = SmoothPath(path, smoothStrength,Math.max(init_size-2,0));
				return path;
			}
		}
		return path;
	}

	/**
	 * Smooth the path so that the turn are less sharp. This might invalidate the path!
	 * @param path: a list of point ordered by closest destination from object to target
	 * @param smoothStrength: float between 0 (no change) and 1 (a point becomes the mean of the previous and next point of the original path)
	 * @return The smooth path (not guaranteed to be valid)
	 */
	public  static ArrayList<Point> SmoothPath(ArrayList<Point> path, float smoothStrength,int startIndex)
	{
		if(smoothStrength ==0)
			return path;
		ArrayList<Point> smoothedPath = new ArrayList<Point>();

		smoothedPath.add(path.get(path.size()-1));

		for(int i=path.size()-2; i>=startIndex+1; --i)
		{
			Point A = path.get(i-1);
			Point B = path.get(i+1);
			Point middleP = new Point((A.x+B.x)/2,(A.y+B.y)/2);
			Point currentP = path.get(i);

			float c = smoothStrength;
			smoothedPath.add( new Point((int)((1-c) * currentP.x + c * middleP.x),(int)((1-c) * currentP.y + c * middleP.y)) );
		}

		for(int i = startIndex; i >= 0; --i)
		{
			smoothedPath.add(path.get(i));
		}

		return smoothedPath;
	}

	public static String PathToString(ArrayList<Point> path)
	{
		String s = "";
		for(Point p : path)
			s+= p.toString()+" ";
		return s;
	}
	public static void OnDestroy(A_Star_Parameters params,AbstractModelPartie partie)
	{
		if(params==null)
			return;
		if(params.DEBUG)
		{
			partie.debugDraw=null;
		}
	}
//================================= TESTING ======================================================================
	
	protected boolean TEST_RemovePathPoint_neighborTest()
	{
		// X explored cell , O candidate, . null
		// Init: X1-X2-X3-O4
		// Remove X3
		// Expect: X1-O2-O3
		A_Star_Parameters params = new A_Star_Parameters();
		A_Star_Candidate[][]  grid = new A_Star_Candidate[10][10] ;
		int grid_scale_divider=1;
		
		Point p1 = new Point(0,0); Point p2 = new Point(0,1); Point p3 = new Point(0,2);Point p4 = new Point(0,3);
		
		AddCandidate(params,p1,null);
		SelectCandidate(params,p1);
		
		AddCandidate(params,p2,grid[p1.x][p1.x]);
		SelectCandidate(params,p2);

		AddCandidate(params,p3,grid[p2.x][p2.y]);
		SelectCandidate(params,p3);
		
		AddCandidate(params,p4,grid[p3.x][p3.y]);

		OnRemovePathPoint(params,p3,null);
		
		boolean neighborAsDesired = (grid[p1.x][p1.y].prevNeighbor == null) && (grid[p2.x][p2.y].prevNeighbor.cellIndex.equals(p1)) && (grid[p3.x][p3.y].prevNeighbor.cellIndex.equals(p2));
		return neighborAsDesired;
	}
	
	protected boolean TEST_RemovePathPoint_removeInChain()
	{
		// X explored cell , O candidate, . null
		// Init: X1-X2-O3 (x=0, y=0..2)
		// Remove X2
		// Expect: X1-O2-.
		
		A_Star_Parameters params = new A_Star_Parameters();
		A_Star_Candidate[][]  grid = new A_Star_Candidate[10][10] ;
		int grid_scale_divider=1;
		
		Point p1 = new Point(0,0); Point p2 = new Point(0,1); Point p3 = new Point(0,2);
		
		AddCandidate(params,p1,null);
		SelectCandidate(params,p1);
		
		AddCandidate(params,p2,grid[p1.x][p1.x]);
		SelectCandidate(params,p2);

		AddCandidate(params,p3,grid[p2.x][p2.y]);
		
		OnRemovePathPoint(params,p2,null);
		
		boolean gridAsDesired = (!grid[p1.x][p1.y].isCandidate) && (grid[p2.x][p2.y].isCandidate)  && (grid[p3.x][p3.y]== null);
		boolean neighborAsDesired = (grid[p1.x][p1.y].prevNeighbor == null) && (grid[p2.x][p2.y].prevNeighbor.cellIndex.equals(p1));
		return gridAsDesired && neighborAsDesired;
	}
	protected boolean TEST_RemovePathPoint_removeTConfig()
	{
		// X explored cell , O candidate, . null
		//Init        o1<-x2->x3->o4
		//				 ^x5
		//				 ^x6
		//Remove x5
		//Desired     .  -. -. - .
		//				 ^o5
		//				 ^x6
		
		A_Star_Parameters params = new A_Star_Parameters();
		A_Star_Candidate[][]  grid = new A_Star_Candidate[10][10] ;
		int grid_scale_divider=1;
		
		Point p1 = new Point(0,2); Point p2 = new Point(1,2); Point p3 = new Point(2,2); Point p4 = new Point(3,2); Point p5 = new Point(1,1); Point p6 = new Point(1,0);
		
		AddCandidate(params,p6,null);
		SelectCandidate(params,p6);
		
		AddCandidate(params,p5,grid[p6.x][p6.y]);
		SelectCandidate(params,p5);
		
		AddCandidate(params,p2,grid[p5.x][p5.y]);
		SelectCandidate(params,p2);

		AddCandidate(params,p3,grid[p2.x][p2.y]);
		SelectCandidate(params,p3);
		
		AddCandidate(params,p1,grid[p2.x][p2.y]);
		AddCandidate(params,p4,grid[p3.x][p3.y]);
		
		OnRemovePathPoint(params,p5,null);
		
		boolean gridAsDesired = (!grid[p6.x][p6.y].isCandidate) && (grid[p5.x][p5.y].isCandidate) && (grid[p2.x][p2.y]==null)  && (grid[p1.x][p1.y]== null) && (grid[p3.x][p3.y]== null) && (grid[p4.x][p4.y]== null);
		boolean neighborAsDesired = grid[p5.x][p5.y].prevNeighbor.cellIndex.equals(p6);
		return gridAsDesired && neighborAsDesired;
	}
	protected boolean TEST_RemovePathPoint_removeSquareConfig()
	{
		// X explored cell , O candidate, . null
		//Init    o1<-x2<-x3<-x4
		//			  x5     ^x6
		//			  ^x7<-x8<^x9
		//Remove x6
		//Desired  o1 -o2 - o3 <-.  (due to diagonals)
		//			  ^x5      o6
		//			  ^x7<-x8<^x9
		
		A_Star_Parameters params = new A_Star_Parameters();
		A_Star_Candidate[][] grid = new A_Star_Candidate[10][10] ;
		int grid_scale_divider=1;
		
		Point p1 = new Point(0,2); Point p2 = new Point(1,2); Point p3 = new Point(2,2); Point p4 = new Point(3,2); Point p5 = new Point(1,1);
		Point p6 = new Point(3,1); Point p7 = new Point(1,0); Point p8 = new Point(2,0); Point p9 = new Point(3,0);
		
		AddCandidate(params,p9,null);
		SelectCandidate(params,p9);
		
		AddCandidate(params,p6,grid[p9.x][p9.y]);
		SelectCandidate(params,p6);
		
		AddCandidate(params,p4,grid[p6.x][p6.y]);
		SelectCandidate(params,p4);
		
		AddCandidate(params,p3,grid[p4.x][p4.y]);
		SelectCandidate(params,p3);
		
		AddCandidate(params,p2,grid[p3.x][p3.y]);
		SelectCandidate(params,p2);
		
		AddCandidate(params,p1,grid[p2.x][p2.y]);		
		AddCandidate(params,p8,grid[p9.x][p9.y]);
		SelectCandidate(params,p8);

		AddCandidate(params,p7,grid[p8.x][p8.y]);
		SelectCandidate(params,p7);
		
		AddCandidate(params,p5,grid[p7.x][p7.y]);
		SelectCandidate(params,p5);
		
		OnRemovePathPoint(params,p6,null);
		
		

		boolean gridAsDesired = (grid[p1.x][p1.y].isCandidate) && (grid[p2.x][p2.y].isCandidate) && (grid[p3.x][p3.y].isCandidate) && (grid[p4.x][p4.y]==null)  
				&& (!grid[p5.x][p5.y].isCandidate) && (grid[p6.x][p6.y].isCandidate) && (!grid[p7.x][p7.y].isCandidate) && (!grid[p8.x][p8.y].isCandidate)
				&& (!grid[p9.x][p9.y].isCandidate);
				
		boolean neighborAsDesired = (grid[p1.x][p1.y].prevNeighbor.cellIndex.equals(p5)) && (grid[p2.x][p2.y].prevNeighbor.cellIndex.equals(p5))
				&& (grid[p3.x][p3.y].prevNeighbor.cellIndex.equals(p5)) && (grid[p5.x][p5.y].prevNeighbor.cellIndex.equals(p7)) 
				&& (grid[p7.x][p7.y].prevNeighbor.cellIndex.equals(p8)) && (grid[p8.x][p8.y].prevNeighbor.cellIndex.equals(p9));

		return gridAsDesired && neighborAsDesired;
	}
	
	protected void displayGrid(A_Star_Candidate[][]  grid)
	{
		int x_size = grid.length;
		int y_size = grid[0].length;

		for(int y=y_size-1;y>=0;--y)
		{
			for(int x=0;x<x_size;++x)
				if(grid[x][y] ==null)
					System.out.print(".");
				else if(grid[x][y].isCandidate)
					System.out.print("O");
				else if(!grid[x][y].isCandidate)
					System.out.print("X");
			System.out.println("");
		}
	}
}