package AI;

import java.awt.Point;
import java.util.ArrayList;

public class A_Star_Candidate {
	public Point cellIndex = null;
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
