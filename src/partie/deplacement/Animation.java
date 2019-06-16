package partie.deplacement;

import java.util.ArrayList;
import java.util.List;

import option.Config;

public class Animation {
	
	private int start_frame=0;
	private int start_index=0;//this index is used 
	private int end_index=0;//this is the strict limit, anim (= [start_index,end_index-1]
	private int num_animation_reset =0;
	private int max_num_animation = -1;//Negative => no maximum, loop indefinitely
	
	/***
	 * Set to -1 for infinite loop, otherwise set the number of desired loops 
	 * @param val
	 */
	public void setMaxNumAnim(int val){max_num_animation=val;}
	public int getStartFrame(){return start_frame;};
	public int getStartIndex(){return start_index;};
	public int getEndIndex(){return end_index;};
	public boolean isEndedOnce(){return num_animation_reset>0;}
	public boolean isEnded(){
		if(max_num_animation>0)
			return num_animation_reset>=max_num_animation;
		else
			return false;
	}

	private List<Integer> animationFrame = new ArrayList<Integer>();//pos i indicates max frame of animation i
	public List<Integer> getAnimationFrame(){return animationFrame;}
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
	private void init()
	{
		start_frame=0;
		start_index=0;//this index is used 
		end_index=0;//this is the strict limit, anim (= [start_index,end_index-1]
		num_animation_reset =0;
		max_num_animation = -1;
		animationFrame = new ArrayList<Integer>();//pos i indicates max frame of animation i
		num_anim=0;
	}
	public void restart(List<Integer> anim,int current_frame,int _start_index,int _end_index)
	{
		init();
		start(anim,current_frame,_start_index,_end_index);
	}
	public double getTime(int current_frame)//return time in s
	{
		return ((current_frame-start_frame)*Config.getDeltaFrame(false));
	}
	
	public int update(int anim,int current_frame,double speedFactor)
	{
		return update(anim,current_frame,speedFactor,false);
	}
	/**
	 * Return the corresponding anim value
	 * */
	public int update(int anim,int current_frame,double speedFactor,boolean log)
	{
		if(start_frame==-1)
		{
			try {throw new Exception("animation not initialized");} catch (Exception e) {e.printStackTrace();}
			return -1;
		}
		//elapsed frame > change frame number
		boolean switch_anim = (current_frame-start_frame)*speedFactor>(animationFrame.get(anim)*Config.ratio_fps());
		if(log)
			System.out.println(switch_anim);
		
		if(!switch_anim)
			return anim;
				
		if(log)
			System.out.println(num_animation_reset+" "+ max_num_animation);
		if(anim==(end_index-1))
		{
			//reached the end of the loop and should not restart since max_num_animation-1 iterations where already done (num_anim_resset starts at 0)
			if(num_animation_reset==(max_num_animation-1))
			{
				//set the num reset to notify that the animation ended (but it did not actually restarted)
				num_animation_reset+=1;
				return anim;
			}
			//same as above but do not set the num reset
			if(max_num_animation>0 && (num_animation_reset>=(max_num_animation-1)))
				return anim;
			
			//switch anim is true, the last index is reached and the animation should restart:  
			num_animation_reset+=1;
			start(null,current_frame,start_index,end_index);
			return start_index;
		}
		//increment the anim number 
		else
		{
			return ( ((anim-start_index+1)%(end_index-start_index))+start_index);//if anim =7, between 6 and 9(excluded), 2%3 +  6 = 8 :)
		}
	}

	public void end()
	{
		start_frame=-1;
	}
}
