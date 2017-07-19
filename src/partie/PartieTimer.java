package partie;

import principal.InterfaceConstantes;

public class PartieTimer {
	
	public static PartieTimer me=null;
	private double totalElapsedTime=0;
	private double lastSystemTime;
	private boolean timeSlowedDown = false;
	private PartieTimer(){}
	public static void init()
	{
		if(me==null)
			me=new PartieTimer();
		me.totalElapsedTime=0;
		me.lastSystemTime= System.nanoTime();
	}

	public void changedSlowMotion(boolean slowMotionActivated)
	{
		totalElapsedTime+= (System.nanoTime()-lastSystemTime) / (timeSlowedDown?InterfaceConstantes.SLOW_DOWN_FACTOR : 1);
		lastSystemTime=System.nanoTime();
		timeSlowedDown=slowMotionActivated;
	}
	public double getElapsedNano()
	{
		double elapsed = totalElapsedTime + (System.nanoTime()-lastSystemTime) / (timeSlowedDown?InterfaceConstantes.SLOW_DOWN_FACTOR : 1);
		return elapsed;
	}
}
