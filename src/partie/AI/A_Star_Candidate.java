package partie.AI;

import java.awt.Point;
import java.util.ArrayList;

import partie.AI.A_Star.A_Star_Parameters;

public class A_Star_Candidate {
	public Point cellIndex = null;
	private Point worldPos =null;
	public Point getWorldPos(A_Star_Parameters params)
	{
		if(cellIndex==null)
			return null;
		if(worldPos==null)
			worldPos=A_Star.CellToPos(params,cellIndex);
		return worldPos;
	}
	public int distance = -1;
	public A_Star_Candidate prevNeighbor= null;
	public boolean isCandidate = true; //true of the element is in the list of candidate, false otherwise
	
	private boolean isEmpty = true;
	
	public A_Star_Candidate (Point _index,A_Star_Candidate _prevNeighbor )
	{
		cellIndex = _index;
		prevNeighbor=_prevNeighbor;
		if(prevNeighbor ==null)
			distance = 0;
		else
			distance = prevNeighbor.distance+1;
		
	}
}
