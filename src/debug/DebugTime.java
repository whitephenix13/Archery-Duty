package debug;

import gameConfig.InterfaceConstantes;

public class DebugTime {
	private double MAX_TIME = 17;//ms
	
	private double starttime;
	private double[] times;

	public DebugTime(){};
	public void init()
	{
		starttime = System.nanoTime();

		times = new double[10];
		startElapsedForVerbose(1);
	}
	
	
	public String tab(int verbose, boolean isActionTooSlow,boolean isLoopTooSlow )
	{
		
		String tabs = "";
		for(int i=0; i<verbose; i++)
			if(isActionTooSlow)
				tabs+="..";
			else if(isLoopTooSlow)
				tabs+="--";
			else
				tabs+="  ";
		return tabs;
	}
	
	public void startElapsedForVerbose(int verbose)
	{
		times[verbose-1]= System.nanoTime();
	}
	
	/***
	 * Use this to avoid concanating string and ease the garbage collector
	 */
	public void elapsed(String mess1,String mess2,String mess3,String mess4, int verbose)
	{
		if(verbose<=InterfaceConstantes.DEBUG_TIME_VERBOSE)
		{
			elapsed(mess1+mess2+mess3+mess4, verbose);
		}
	}
	/***
	 * Use this to avoid concanating string and ease the garbage collector
	 */
	public void elapsed(String mess1,String mess2,String mess3, int verbose)
	{
		if(verbose<=InterfaceConstantes.DEBUG_TIME_VERBOSE)
		{
			elapsed(mess1+mess2+mess3, verbose);
		}
	}
	/***
	 * Use this to avoid concanating string and ease the garbage collector
	 */
	public void elapsed(String mess1,String mess2, int verbose)
	{
		if(verbose<=InterfaceConstantes.DEBUG_TIME_VERBOSE)
		{
			elapsed(mess1+mess2, verbose);
		}
	}
	public void elapsed(String mess, int verbose)
	{
		if(verbose<=InterfaceConstantes.DEBUG_TIME_VERBOSE)
		{
			boolean printIffSlow =InterfaceConstantes.DEBUG_TIME_ONLY_LOG_SLOW_STEPS;
			double deltaAction = System.nanoTime()-times[verbose-1];
			double deltaLoop = System.nanoTime()-starttime;
			
			boolean isActionTooSlow = (deltaLoop)*Math.pow(10, -6)>MAX_TIME ;
			boolean isLoopTooSlow = ((deltaAction)*Math.pow(10, -6))>=10;

			String tabs = tab(verbose,isActionTooSlow,isLoopTooSlow);
			
			String s = verbose+": "+"Time: "+mess+ ": "+ ((deltaAction)*Math.pow(10, -6)) + "ms / "+ (deltaLoop)*Math.pow(10, -6)+"ms";
			
			if(!printIffSlow || (printIffSlow &&(isActionTooSlow||isLoopTooSlow) ))
				System.out.println(tabs+s);
						
			times[verbose-1]= System.nanoTime();
		}
	}

}
