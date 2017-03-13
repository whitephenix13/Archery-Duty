package deplacement;

import java.util.ArrayList;
import java.util.List;

import option.Config;

public class Animation {

	private int start_frame=0;
	private int start_index=0;//this index is used 
	private int end_index=0;//this is the strict limit, anim (= [start_index,end_index-1]
	private boolean animation_ended =false;
	public int getStartFrame(){return start_frame;};
	public int getStartIndex(){return start_index;};
	public int getEndIndex(){return end_index;};
	public boolean isEndedOnce(){return animation_ended;}
	
	private List<Integer> animationFrame = new ArrayList<Integer>();//pos i indicates max frame of animation i
	public int num_anim=0;
	public Animation()
	{
	}
	private void setAnimationFrame(List<Integer> anim)
	{
		animationFrame=anim;
		num_anim=anim.size();
	}
	public void start(List<Integer> anim,int current_frame,int _start_index,int _end_index)
	{
		if(anim !=null)
			setAnimationFrame(anim);
		start_frame= current_frame;
		start_index =_start_index;
		end_index =_end_index;
	}
	public double getTime(int current_frame)//return time in s
	{
		return ((current_frame-start_frame)*Config.getDeltaFrame(false));
	}
	/**
	 * Return the corresponding anim value
	 * */
	
	public int update(int anim,int current_frame)
	{
		if(start_frame==-1)
		{
			try {throw new Exception("animation not initialized");} catch (Exception e) {e.printStackTrace();}
			return -1;
		}
		//elapsed frame > change frame number
		boolean switch_anim = (current_frame-start_frame)>animationFrame.get(anim)*Config.ratio_fps();
		if(anim==(end_index-1))
		{
			if(switch_anim){
				//restart animation from beginning
				animation_ended=true;
				start(null,current_frame,start_index,end_index);
				return start_index;}
			else
				return anim;
		}
		else
		{
			if(switch_anim)
				return ( ((anim-start_index+1)%(end_index-start_index))+start_index);//if anim =7, between 6 and 9(excluded), 2%3 +  6 = 8 :)
			else
				return anim;
		}
	}
	public void end()
	{
		start_frame=-1;
	}
}
