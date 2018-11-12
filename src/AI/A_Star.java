package AI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import debug.DebugDraw;
import partie.AbstractModelPartie;
import partie.PartieTimer;
import principal.InterfaceConstantes;
import types.Hitbox;

public class A_Star {

	//WARNING: the algorithm always compute the distance from the original start point. As a consequence, will be biased and might tend to attack the enemy
	//by strange direction if he moved a lot from its original position

	//INPUT: All collidable object (as structured for the world, use Collidable.objectInBoundingSquare for others), object to move, target Point 
	//PARAMETERS: Re-evaluation time, grid_size 
	//OUTPUT: list of Points 

	boolean DEBUG = false;
	public void setDebug(boolean val){DEBUG=val;}
	//Parameters to set 
	double REEVALUATION_TIME = 0.05*Math.pow(10, 9); // 0.05 sec (50ms)
	int REDIRECTION_LENGTH = 2* InterfaceConstantes.TAILLE_BLOC; //This is used to delete the last x points when Updating the path
	//This value should be equal to the estimated distance that the target can move within REEVALUATION_TIME
	Point explorationAngle = new Point(1,4); //Maximum value by which the object can turn as a fraction of Math.Pi
	int max_step_size = InterfaceConstantes.TAILLE_BLOC; //maximum value by which the object can move at each iteration

	//Constants
	int MAX_DISTANCE = InterfaceConstantes.WINDOW_WIDTH + 2* InterfaceConstantes.TAILLE_BLOC;//Maximum distance that the moving object can travel
	int MAX_ITERATION = 300;
	//from its starting point. The algorithm will find no path if the object or target is more than MAX_DISTANCE from the starting point

	//Constants after initialization 
	double last_reevaluation_time=-1; //last time that the path was reevaluated 
	int grid_scale_divider = 1;// set 2 to have half distance between grid cells for more accuracy (1=> angle = Pi/4, 0, -Pi/4, 2=> -Pi/8, 0, Pi/8)) 
	int grid_size = InterfaceConstantes.TAILLE_BLOC; 
	Point initialPos = new Point(); //position at which the object start moving. it determines the 0,0 of the grid
	double limCosAngle = 0; //the maximum value of the cos. if val<limCosAngle then the point in not in the correct angle 

	//local variables
	Point target = new Point();
	A_Star_Candidate[][] grid = null; //0 if empty, 1 if visited 
	ArrayList<Point> listCandidates = new ArrayList<Point>(); // list of candidate cell index to visit 
	ArrayList<Point> debugInvalidCandidates= new ArrayList<Point>(); // list of candidates that where invalid 
	public enum DEBUG_INVALID_CANDIDATES {ALL,INDICES_ANGLE,INDICES,ANGLE}
	private DEBUG_INVALID_CANDIDATES debug_invalid = DEBUG_INVALID_CANDIDATES.ANGLE;
	
	public enum ERROR {TOO_FAR,NOT_FOUND,NONE}
	private ERROR lastError = ERROR.NONE;
	public ERROR getLastError(){return lastError;}

	public A_Star()
	{
		init();
	}

	/**
	 * 
	 * @param _reevaluation_time how frequently should the path be recomputed 
	 * @param _max_step_size maximum value by which the object can move at each iteration
	 * @param _explorationAngle Maximum value by which the object can turn as a fraction of Math.Pi
	 * @param redirection_l This value should be equal to the estimated distance that the target can move within REEVALUATION_TIME
	 */
	public A_Star(double _reevaluation_time,int _max_step_size, Point _explorationAngle, int redirection_l)
	{
		REEVALUATION_TIME=_reevaluation_time;
		max_step_size=_max_step_size;
		explorationAngle=_explorationAngle;
		REDIRECTION_LENGTH = redirection_l;
		init();
	}

	int nextEven(double x)
	{
		int topInt = (int) Math.ceil(x);
		if(topInt%2 == 0)
			return topInt;
		else
			return topInt+1;
	}
	void init()
	{
		last_reevaluation_time=-1; 
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
		double limAngle = explorationAngle.x * Math.PI/explorationAngle.y;
		grid_scale_divider = limAngle>=Math.PI/2? 1 : nextEven(1.0/Math.tan(limAngle)); 
		grid_size = max_step_size / grid_scale_divider;
		limCosAngle = Math.cos(Math.min(limAngle + Math.PI/180,2*Math.PI));
	}

	/**
	 * 
	 * @param index: cellIndex of the new candidate 
	 * @param prevNeighbor: A_Star_Candidate that causes the addition of this as a new candidate (null if initialization)
	 */
	private void AddCandidate(Point index, A_Star_Candidate prevNeighbor)
	{
		listCandidates.add(index);
		grid[index.x][index.y]= new A_Star_Candidate(index,prevNeighbor);
		grid[index.x][index.y].isCandidate=true;
	}

	/**
	 * 
	 * @param index cellIndex of the candidate to select
	 */
	private void SelectCandidate(Point index)
	{
		listCandidates.remove(index);
		grid[index.x][index.y].isCandidate=false;
	}

	/**
	 * 
	 * @param index cellIndex of the candidate to remove
	 * @return returns the removed candidate 
	 */
	private A_Star_Candidate RemoveCell(Point index)
	{
		listCandidates.remove(index);
		A_Star_Candidate cand = grid[index.x][index.y];
		grid[index.x][index.y]= null;
		return cand;
	}
	/**
	 * @param pos: world position
	 * @return cell index corresponding to a world position
	 */
	private Point PosToCell(Vector2d pos)
	{
		//the grid is of size 2*grid_scale_divider*MAX_DISTANCE+1.
		//The initialPos is at the center of the grid (index (MAX_DISTANCE,MAX_DISTANCE) ) 
		return new Point((int)((pos.x-initialPos.x)/grid_size + MAX_DISTANCE),(int)((pos.y-initialPos.y)/grid_size + MAX_DISTANCE));
	}
	/**
	 * @param pos: world position
	 * @return cell index corresponding to a world position
	 */
	private Point PosToCell(Point pos)
	{
		return PosToCell(new Vector2d(pos.x,pos.y));
	}
	private Point CellToPos(Point cell)
	{
		return new Point((cell.x - MAX_DISTANCE) *grid_size + initialPos.x, (cell.y - MAX_DISTANCE) *grid_size + initialPos.y );
	}
	/**
	 * Specific distance function that returns in how many moves we can reach B from A (diagonal is a legal move) 
	 * @param A
	 * @param B
	 * @return
	 */
	private int Dist(Point A, Point B)
	{
		return Math.max(Math.abs(B.x-A.x), Math.abs(B.y-A.y));
	}
	/**
	 * 
	 * @param index: index of the element in the grid
	 * @return false if the index is incorrect, true otherwise 
	 */
	private boolean checkIndex(Point index)
	{
		int size = 2*grid_scale_divider*MAX_DISTANCE+1;
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
	private boolean checkGridIndex(Point index,A_Star_Candidate[][] grid,boolean testEmpty)
	{
		if(!checkIndex(index))
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
	public boolean CheckNeighborsAngle(Point candidate,Point elem,Vector2d dir)
	{
		//dir should be normalized
		Vector2d v = new Vector2d(candidate.x-elem.x,candidate.y-elem.y);
		v.normalize();
		return v.dot(dir) >=limCosAngle;
	}

	/**
	 * Return true if there is no collision
	 * @param partie
	 * @param elem
	 * @param objectToMove
	 * @return
	 */
	public boolean CheckCollision(AbstractModelPartie partie,Point elem,int max_dist_object)
	{
		Point worldCoordinate = CellToPos(elem);
		Point p1 = new Point(worldCoordinate.x-max_dist_object/2,worldCoordinate.y-max_dist_object/2);
		Point p2 = new Point(worldCoordinate.x+max_dist_object/2,worldCoordinate.y-max_dist_object/2);
		Point p3 = new Point(worldCoordinate.x+max_dist_object/2,worldCoordinate.y+max_dist_object/2);
		Point p4 = new Point(worldCoordinate.x-max_dist_object/2,worldCoordinate.y+max_dist_object/2);
		Hitbox estimatedHit = new Hitbox(p1,p2,p3,p4);
		boolean res = !Collision.isWorldCollision(partie, estimatedHit, true);
		return res;
	}
	/**
	 * Find the next neighbors to explore for the A* algorithm next step
	 * @param elem: the current position
	 * @param dir: direction to which the element is aiming
	 * @param grid: grid of empty/explored area 
	 * @return List of point representing the valid neighbors
	 */
	public void AddNeightbors(AbstractModelPartie partie,A_Star_Candidate elem,int max_dist_object,
			Vector2d dir, A_Star_Candidate[][]grid)
	{
		if(DEBUG)
			debugInvalidCandidates.clear();

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
		int xstart = elem.cellIndex.x-grid_scale_divider;
		int xend = elem.cellIndex.x+grid_scale_divider;
		int ystart = elem.cellIndex.y-grid_scale_divider;
		int yend = elem.cellIndex.y+grid_scale_divider;
		
		//First of all, consider the case where the target is a point within the neighbors but not in the border. 
		//Correction: always add target when found otherwise it might happen that the angle is wrong so the target is missed
		if(grid_scale_divider>1)
		{
			if( (xstart+1 <= target.x) && (target.x <= xend -1) && (ystart+1 <= target.y) && (target.y <= yend -1) )
			{
				/*if(checkGridIndex(target,grid,true) && CheckNeighborsAngle(target,elem.cellIndex,dir) &&
						CheckCollision(partie,target,max_dist_object))*/
					AddCandidate(target,elem);
				/*else if(DEBUG)
				{
					if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ALL))
						debugInvalidCandidates.add(target);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && CheckNeighborsAngle(target,elem.cellIndex,dir) )
						debugInvalidCandidates.add(target);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.INDICES) && checkGridIndex(target,grid,true) )
						debugInvalidCandidates.add(target);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && checkGridIndex(target,grid,true) && CheckNeighborsAngle(target,elem.cellIndex,dir))
						debugInvalidCandidates.add(target);
				}*/
			}
		}
		//double loop to loop over border elements
		//if the grid is empty + the indices are in the grid + the angle is correct + there is no collision : add this point as a candidate 
		for(int x = xstart; x<=xend; x+=1 )
		{
			if(x==xstart || (x==xend) )
			{
				for(int y = ystart; y<=yend; y+=1 )
				{
					Point p = new Point(x,y);

					if(checkGridIndex(p,grid,true) && CheckNeighborsAngle(p,elem.cellIndex,dir) &&
							CheckCollision(partie,p,max_dist_object)){
						AddCandidate(p,elem);
					}
					else if(DEBUG)
					{
						if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ALL))
							debugInvalidCandidates.add(p);
						else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && CheckNeighborsAngle(p,elem.cellIndex,dir) )
							debugInvalidCandidates.add(p);
						else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.INDICES) && checkGridIndex(p,grid,true) )
							debugInvalidCandidates.add(p);
						else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && checkGridIndex(p,grid,true) && CheckNeighborsAngle(p,elem.cellIndex,dir))
							debugInvalidCandidates.add(p);
					}
				}
			}
			else
			{
				Point p1 = new Point(x,ystart);
				Point p2 = new Point(x,yend);

				if(checkGridIndex(p1,grid,true) && CheckNeighborsAngle(p1,elem.cellIndex,dir)&&
						CheckCollision(partie,p1,max_dist_object)){
					AddCandidate(p1,elem);
				}
				else if(DEBUG)
				{
					if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ALL))
						debugInvalidCandidates.add(p1);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && CheckNeighborsAngle(p1,elem.cellIndex,dir) )
						debugInvalidCandidates.add(p1);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.INDICES) && checkGridIndex(p1,grid,true) )
						debugInvalidCandidates.add(p1);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && checkGridIndex(p1,grid,true) && CheckNeighborsAngle(p1,elem.cellIndex,dir))
						debugInvalidCandidates.add(p1);
				}
				if(checkGridIndex(p2,grid,true) && CheckNeighborsAngle(p2,elem.cellIndex,dir)&&
						CheckCollision(partie,p2,max_dist_object)){
					AddCandidate(p2,elem);
				}
				else if(DEBUG)
				{
					if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ALL))
						debugInvalidCandidates.add(p2);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && CheckNeighborsAngle(p2,elem.cellIndex,dir) )
						debugInvalidCandidates.add(p2);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.INDICES) && checkGridIndex(p2,grid,true) )
						debugInvalidCandidates.add(p2);
					else if(debug_invalid.equals(DEBUG_INVALID_CANDIDATES.ANGLE) && checkGridIndex(p2,grid,true) && CheckNeighborsAngle(p2,elem.cellIndex,dir))
						debugInvalidCandidates.add(p2);
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
	private double score(A_Star_Candidate prevBest, A_Star_Candidate elem, Point target)
	{
		double score =  elem.distance + Dist(elem.cellIndex,target);
		//add penalty if the vector prevBest,elem does not point toward target
		if(prevBest!= null)
		{
			Vector2d v1 = new Vector2d(elem.cellIndex.x-prevBest.cellIndex.x,elem.cellIndex.y-prevBest.cellIndex.y);
			Vector2d v2 = new Vector2d(target.x-prevBest.cellIndex.x,target.y-prevBest.cellIndex.y);
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
	public ArrayList<Point> FindPath(AbstractModelPartie partie,Collidable objectToMove,Vector2d dir, Point _target, float smoothStrength)
	{
		return FindPath(partie,objectToMove,dir,_target,smoothStrength,null);
	}
	
	void debugDraw(final AbstractModelPartie partie,final int max_dist_object)
	{
		if(partie.debugDraw==null){
			partie.debugDraw = new DebugDraw(){
				@Override
				public void draw(Graphics g){

					for(int xInd=0; xInd<grid.length;++xInd)
					{
						for(int yInd=0; yInd<grid[0].length;++yInd)
						{
							A_Star_Candidate debugCand = grid[xInd][yInd];
							if(debugCand != null){
								if(debugCand.isCandidate)
								{
									g.setColor(Color.RED);
								}
								else
								{
									g.setColor(Color.GREEN);
								}
								Point p = CellToPos(debugCand.cellIndex);
								g.fillRect(p.x+partie.xScreendisp,p.y +partie.yScreendisp, 5, 5);
								g.drawRect(p.x+partie.xScreendisp -max_dist_object/2, p.y+partie.yScreendisp -max_dist_object/2
										, max_dist_object, max_dist_object);
							}
						}
					}
					for(int i=0; i<debugInvalidCandidates.size();++i)
					{
						g.setColor(Color.BLACK);
						Point p = CellToPos(debugInvalidCandidates.get(i));
						g.fillRect(p.x+partie.xScreendisp,p.y +partie.yScreendisp, 5, 5);
						g.drawRect(p.x+partie.xScreendisp -max_dist_object/2, p.y+partie.yScreendisp -max_dist_object/2
								, max_dist_object, max_dist_object);
					}
					g.setColor(Color.BLUE);
					Point p = CellToPos(target);
					g.fillRect(p.x+partie.xScreendisp,p.y +partie.yScreendisp, 5, 5);

					g.setColor(Color.BLACK);
				}

			};
		}
	}
	void debugBreak(AbstractModelPartie partie)
	{
		partie.forceRepaint();
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			System.out.print(""); //set break point here
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

	public ArrayList<Point> FindPath(final AbstractModelPartie partie,Collidable objectToMove,Vector2d dir, Point _target, float smoothStrength,
			Point initPos)
	{
	
		if(last_reevaluation_time == -1)
		{
			last_reevaluation_time = PartieTimer.me.getElapsedNano();
		}
		lastError= ERROR.NONE;

		Vector2d objWorldPos = Hitbox.getObjMid(partie,objectToMove);
		if(objWorldPos==null)
			System.out.println(objectToMove.getHitbox(partie.INIT_RECT, partie.getScreenDisp()));
		//as the hitbox can turn while moving, we consider the worst case with the diagonal of a square
		int max_dist_object = (int) Math.ceil( Math.sqrt(2) * objectToMove.deplacement.getMaxBoundingSquare(objectToMove));

		if(DEBUG){
			debugDraw(partie,max_dist_object);
		}
		//Only happen when generating the path for the first time. Shouldn't trigger when recomputing path 
		if(grid ==null)
		{
			//generate grid: generate thinner grid depending on exploration angle
			grid = new A_Star_Candidate[2*grid_scale_divider*MAX_DISTANCE+1][2*grid_scale_divider*MAX_DISTANCE+1] ; 
			//Arrays.fill(grid, null);
			//Remember the initial Pos, see the warning at the beginning for more information 
			initialPos = new Point((int)objWorldPos.x,(int)objWorldPos.y);
		}
		//Set object and target to grid coordinates
		Point targetIndex = PosToCell(_target);
		Point objectIndex = PosToCell(objWorldPos);
		//In the case where the path is recomputed, we start from a different position
		if(initPos != null)
			objectIndex = PosToCell(initPos);
		target = targetIndex;

		//Check if the initialPos and the object/initIndex or target are not too far one from the other 
		if(Dist(objectIndex,initialPos)> MAX_DISTANCE || Dist(targetIndex,initialPos)> MAX_DISTANCE)
		{
			lastError = ERROR.TOO_FAR;
			return null;
		}

		//In case where the path is recomputed, the listCandidates date from last update
		AddCandidate(objectIndex,null);
		//Iterate until the target is reached or the list of neighbors to explore (listCandidates) is empty 
		boolean targetReached = false;
		A_Star_Candidate last_candidate = null;
		int iter = 0; 
		do{
			if(DEBUG){
				debugBreak(partie);
			}
			System.out.println("Find path" + iter+" dir " + dir +" candidates "+ listCandidates); 

			/*String s="";
			for(int iter = 0; iter<listCandidates.size(); ++i)
			{
				s+= " " listCandidates.get(iter);
			}*/
			//Look for the best candidate (initialize with candidate 0 )
			Point ind = listCandidates.get(0);
			double minScore = score(grid[ind.x][ind.y].prevNeighbor,grid[ind.x][ind.y],target);
			int minCandIndex = 0;
			for(int i=1; i<listCandidates.size(); ++i)
			{
				ind = listCandidates.get(i);
				double score = score(grid[ind.x][ind.y].prevNeighbor,grid[ind.x][ind.y],target);
				if(score < minScore)
				{
					minScore=score;	
					minCandIndex=i;
				}
			}
			//Get the best candidate and remove it from the list
			Point minIndex= listCandidates.get(minCandIndex);
			SelectCandidate(minIndex);
			A_Star_Candidate bestCand = grid[minIndex.x][minIndex.y];
			System.out.println("\tbest cand"  + bestCand.cellIndex+ " target "+ target +" world target "+ CellToPos(target)); ;
			
			// update the direction 
			A_Star_Candidate bestCandPrev = bestCand.prevNeighbor;
			if( bestCandPrev !=null){
				dir = new Vector2d(bestCand.cellIndex.x-bestCandPrev.cellIndex.x,bestCand.cellIndex.y-bestCandPrev.cellIndex.y);
				dir.normalize(); 
			}
			
			//Check if target is reached 
			if(Dist(bestCand.cellIndex,target) ==0){
				last_candidate= bestCand;
				targetReached=true;
			}
			//Add neighbors to candidate 
			else
				//update the list of candidates 
				AddNeightbors(partie,bestCand,max_dist_object,dir,grid);

			iter++;
		}
		while(!targetReached && (listCandidates.size()>0 ) && iter <=MAX_ITERATION);

		ArrayList<Point> path = new ArrayList<Point>();

		//no path found
		if(!targetReached)
		{
			lastError = ERROR.NOT_FOUND;
			return null;
		}
		//path found: rebuild it by iterating from last candidate 
		else
		{
			A_Star_Candidate path_elem = last_candidate;
			while(path_elem != null)
			{
				path.add(CellToPos(path_elem.cellIndex));
				path_elem = path_elem.prevNeighbor;
			}
			Collections.reverse(path);
		}
		path = SmoothPath(path, smoothStrength,0);
		return path;
	}
	
	private void OnRemovePathPoint(Point removedIndex,A_Star_Candidate neighborRequestingRemove)
	{
		//remove element at index and update the grid
		A_Star_Candidate removed_cand  = RemoveCell(removedIndex);

		//check for its neighbors if the removed element was the prevNeighbor of the A_Star_Candidate
		//if so, remove this candidate from the list 
		boolean addAsCandidate = false; 
		Point addAsCandidateNeighborIndex = null;

		//iterate over neighbors
		for(int x=-grid_scale_divider;x<=grid_scale_divider;x+=1)
		{
			for(int y=-grid_scale_divider;y<=grid_scale_divider;y+=1)
			{
				if(x==0 && y ==0)
					continue;
				Point neighbor_index = new Point(removedIndex.x+x,removedIndex.y+y);

				//If index is valid and cell is not empty, remove neighbor if prevNeighbor is removed_cand
				if(checkGridIndex(neighbor_index,grid,false)){
					A_Star_Candidate removedPrevNeighbor = grid[neighbor_index.x][neighbor_index.y].prevNeighbor;
					if(removedPrevNeighbor != null)
					{
						if(removedPrevNeighbor.equals(removed_cand))
							OnRemovePathPoint(neighbor_index,removed_cand);
					}
					//Add the removed candidate in the listCandidates if there is a neighbor that is not in the listCandidate 
					else if(!grid[neighbor_index.x][neighbor_index.y].isCandidate)
					{
						addAsCandidate=true;
						addAsCandidateNeighborIndex=neighbor_index;
					}
				}
			}
		}
		if(addAsCandidate)
			AddCandidate(removedIndex,grid[addAsCandidateNeighborIndex.x][addAsCandidateNeighborIndex.y]);
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
	public ArrayList<Point> UpdatePath(AbstractModelPartie partie,ArrayList<Point> path, Vector2d orginalDir, Collidable objectToMove, Point _target,
			float smoothStrength, boolean force, Point prevNextTarget, int[] outCurrentIndex)
	{
		double time = PartieTimer.me.getElapsedNano();
		if(force || ((time - last_reevaluation_time) > REEVALUATION_TIME) )
		{
			last_reevaluation_time=time;

			lastError= ERROR.NONE;
			
			Point targetIndex = PosToCell(_target);

			if(target.equals(targetIndex)){
				System.out.println("same target");
				return path;
			}
			else
				target=targetIndex;
			//   if path contains target delete item (and refill list candidate,...) until target is reached
			//		else delete last points and rerun algorithm between the last point and the target
			int i=0;
			boolean perfectMatch = false;
			for(i=0; i<path.size(); ++i)
			{
				if(Dist(path.get(i),_target)==0)
				{
					perfectMatch=true;
					break;
				}
			}
			int startIndex = i+1;//index from which the deletion should start 
			//Case in which the target is not in the path 
			if(i>=path.size())
			{
				int num_point_deleted = REDIRECTION_LENGTH/grid_size + 1;
				startIndex = Math.max(path.size()-num_point_deleted,0) ;
			}
			//Delete the last x points 
			for(int j=path.size()-1;j>=startIndex;--j)
			{
				Point removedP = PosToCell(path.remove(j));
				//update listCandidates + grid 
				OnRemovePathPoint(removedP,null);
				if(DEBUG){
					debugBreak(partie);
				}
			}
			if(perfectMatch){
				outCurrentIndex[0] = path.indexOf(prevNextTarget);
				System.out.println("perfect macth");
				if(outCurrentIndex[0]==-1)//arrow already crossed the road of the object
					return null;
				else
					return path;
			}
			//recompute the end of the path and concatenate it 
			else
			{
				//Compute the correct direction 
				int originalPathSize = path.size();
				Vector2d dir = orginalDir;
				if(originalPathSize>=1)
				{
					Point lastIndex = PosToCell(path.get(path.size()-1));
					A_Star_Candidate last_cand = grid[lastIndex.x][lastIndex.y];
					A_Star_Candidate last_cand_prev = last_cand.prevNeighbor;
					if(last_cand_prev !=null){
						dir = new Vector2d(last_cand.cellIndex.x-last_cand_prev.cellIndex.x,last_cand.cellIndex.y-last_cand_prev.cellIndex.y);
						dir.normalize(); 
					}
				}
				
				ArrayList<Point> endPath = null;
				//If the nextTarget is still in the path, compute the end path and keep the outCurrentIndex
				//otherwise, compute the path from scratch (starting at the object pos !!! ) and set the outCurrentIndex to 0 
				System.out.println("prevNextTarget " +prevNextTarget);
				boolean builtFromScratch = !path.contains(prevNextTarget);
				if(!builtFromScratch)
				{
					//delete all candidate and update grid to force to start exploring from that point
					int numCandidates = listCandidates.size();
					for(int listInd=0; listInd<numCandidates;++listInd)
					{
						Point pIndex = listCandidates.remove(0);
						grid[pIndex.x][pIndex.y] = null;
					}
					endPath = FindPath(partie,objectToMove,dir, _target,smoothStrength,path.get(originalPathSize-1));
					System.out.println("path containing destination " +path +" |||\n " +endPath );

				}
				// recompute from scratch 
				else
				{
					for(int xInd=0; xInd<grid.length;++xInd)
					{
						for(int yInd=0; yInd<grid[0].length;++yInd)
						{
							grid[xInd][yInd] = null;
						}
					}
					listCandidates.clear();
					endPath = FindPath(partie,objectToMove,dir, _target, smoothStrength);
					System.out.println("path not containing destination");
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
					for(int concI =1; concI<endPath.size();++concI)
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
	public ArrayList<Point> SmoothPath(ArrayList<Point> path, float smoothStrength,int startIndex)
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
	public void OnDestroy(AbstractModelPartie partie)
	{
		if(DEBUG)
		{
			partie.debugDraw=null;
		}
	}
	
}
