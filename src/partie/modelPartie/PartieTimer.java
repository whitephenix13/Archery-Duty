package partie.modelPartie;

import gameConfig.InterfaceConstantes;

public class PartieTimer {
	
	public static PartieTimer me=null;
	private double totalElapsedTime=0;
	private double lastSystemTime;
	private boolean timeSlowedDown = false;
	private PartieTimer(){}
	private boolean isFreezed = false;
	public static void init()
	{
		if(me==null)
			me=new PartieTimer();
		me.totalElapsedTime=0;
		me.lastSystemTime= System.nanoTime();
		me.timeSlowedDown = false;
		me.isFreezed = false;
	}
	
	private void UpdateTimer()
	{
		//do not update timer when the time is freezed 
		if(isFreezed)
			return;
		totalElapsedTime+= (System.nanoTime()-lastSystemTime) / (timeSlowedDown?InterfaceConstantes.SLOW_DOWN_FACTOR : 1);
		lastSystemTime=System.nanoTime();
	}
	
	public void changedSlowMotion(boolean slowMotionActivated)
	{
		UpdateTimer();
		timeSlowedDown=slowMotionActivated;
	}
	public double getElapsedNano()
	{
		double elapsed = totalElapsedTime + (System.nanoTime()-lastSystemTime) / (timeSlowedDown?InterfaceConstantes.SLOW_DOWN_FACTOR : 1);
		return elapsed;
	}
	//Use this when you break for example to get the true in game time 
	public void freezeTime()
	{
		UpdateTimer();
		isFreezed = true;
	}
	public void unfreezeTime()
	{
		isFreezed = false;
		lastSystemTime=System.nanoTime();//ignored the elapsed time when the timer was freezed
	}
}
