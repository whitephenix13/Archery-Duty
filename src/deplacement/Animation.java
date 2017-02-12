package deplacement;

import java.util.ArrayList;
import java.util.List;

public class Animation {

	/*private long frame=0;
	private long startTime=0;
	private List<Integer> animationFrame = new ArrayList<Integer>();//pos i indicates max frame of animation i

	public Animation()
	{
	}
	public Animation(List<Integer> anim)
	{
		setAnimationFrame(anim);
	}
	public void setAnimationFrame(List<Integer> anim)
	{
		animationFrame=anim;
	}
	public void start()
	{
		startTime= System.nanoTime();
	}
	public double getTime()//return time in seconds
	{
		return ((double)(System.nanoTime()-startTime))/Math.pow(10, 9);
	}
	/**
	 * Return the corresponding anim value
	 * */
	/*
	public int update(int anim)
	{
		if(startTime==-1)
			start();
		double time =getTime();
		if(anim==(animationTime.size()-1))
		{
			if(time>animationTime.get(anim))
				return 0;
			else
				return anim;
		}
		else
		{
			if(time>animationTime.get(anim))
				return (anim+1);
			else
				return anim;
		}
	}
	public void end()
	{
		startTime=-1;
	}*/
}
