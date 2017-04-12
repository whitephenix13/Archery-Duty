package effects;

import java.util.ArrayList;
import java.util.List;

import deplacement.Animation;

public abstract class Effect {
	public String name;
	public int xpos; 
	public int ypos; 
	public List<Integer> xtaille= new ArrayList<Integer>() ;
	public List<Integer> ytaille= new ArrayList<Integer>() ;
	public List<Integer> xanchor= new ArrayList<Integer>() ;
	public List<Integer> yanchor= new ArrayList<Integer>() ;
	public double rotation=0;
	public int anim; 
	public Animation animation= new Animation(); 
	public int maxnumberloops =1;
	private int numberloops =0;
	public boolean isEnded()
	{
		if(animation.isEndedOnce())
		{
			numberloops+=1;
			animation.resetEndedOnce();
		}
		return numberloops>=maxnumberloops;
	}
	public abstract void onDestroy();
}
