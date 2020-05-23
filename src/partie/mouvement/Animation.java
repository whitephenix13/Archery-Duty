package partie.mouvement;

import java.util.ArrayList;
import java.util.List;

import option.Config;

public class Animation {
	
	private int start_frame=0;
	private int start_index=0;//this index is used 
	private int end_index=0;//this is the strict limit, mouv_index (= [start_index,end_index-1]
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
	private void setAnimationFrame(List<Integer> animationFrame)
	{
		this.animationFrame=animationFrame;
		num_anim=animationFrame.size();
	}
	public void start(List<Integer> animationFrame,int current_frame,int _start_index,int _end_index)
	{
		if(animationFrame !=null)
			setAnimationFrame(animationFrame);
		start_frame= current_frame;
		start_index =_start_index;
		end_index =_end_index;
	}
	private void init()
	{
		start_frame=0;
		start_index=0;//this index is used 
		end_index=0;//this is the strict limit, mouv_index (= [start_index,end_index-1]
		num_animation_reset =0;
		max_num_animation = -1;
		animationFrame = new ArrayList<Integer>();//pos i indicates max frame of animation i
		num_anim=0;
	}
	public void restart(List<Integer> animationFrame,int current_frame,int _start_index,int _end_index)
	{
		init();
		start(animationFrame,current_frame,_start_index,_end_index);
	}
	public double getTime(int current_frame)//return time in s
	{
		return ((current_frame-start_frame)*Config.getDeltaFrame(false));
	}
	
	public int update(int mouv_index,int current_frame,double speedFactor)
	{
		return update(mouv_index,current_frame,speedFactor,false);
	}
	/**
	 * Return the corresponding mouv_index value
	 * */
	public int update(int mouv_index,int current_frame,double speedFactor,boolean log)
	{
		if(start_frame==-1)
		{
			try {throw new Exception("animation not initialized");} catch (Exception e) {e.printStackTrace();}
			return -1;
		}
		if(log)
			System.out.println("start_frame "+ start_frame+ " current_frame " +current_frame+" delta "+animationFrame.get(mouv_index)*Config.ratio_fps());
		if(log)
			System.out.println("loop "+ max_num_animation+" ended once "+isEndedOnce() );
		boolean switch_mouv_index = (current_frame-start_frame)*speedFactor>(animationFrame.get(mouv_index)*Config.ratio_fps());
		
		if(!switch_mouv_index)
			return mouv_index;
				
		
		if(mouv_index==(end_index-1))
		{
			//reached the end of the loop and should not restart since max_num_animation-1 iterations where already done (num_anim_resset starts at 0)
			if(num_animation_reset==(max_num_animation-1))
			{
				//set the num reset to notify that the animation ended (but it did not actually restarted)
				num_animation_reset+=1;
				return mouv_index;
			}
			//same as above but do not set the num reset
			if(max_num_animation>0 && (num_animation_reset>=(max_num_animation-1)))
				return mouv_index;
						
			//switch mouv_index is true, the last index is reached and the animation should restart:  
			num_animation_reset+=1;
			if(log)
				System.out.println("restart "+ num_animation_reset);
			start(null,current_frame,start_index,end_index);
			return start_index;
		}
		//increment the mouv_index number 
		else
		{
			return ( ((mouv_index-start_index+1)%(end_index-start_index))+start_index);//if mouv_index =7, between 6 and 9(excluded), 2%3 +  6 = 8 :)
		}
	}

	public void end()
	{
		start_frame=-1;
	}
}
