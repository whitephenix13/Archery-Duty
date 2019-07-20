package debug;

import java.util.Arrays;

public class DebugTime {
	public enum PrintMode{NONE,PRINT_DIRECTLY, PRINT_ONLY_SLOW_ACTIONS, PRINT_ONLY_SLOW_LOOP, PRINT_SLOW_ACTION_OR_SLOW_LOOP,PRINT_SLOW_ACTION_AND_SLOW_LOOP, PRINT_ALL}
		
	private double starttime;
	private double[] times;
	private int currentStackLength =0;
	private PrintMode printMode;
	
	private boolean shouldPrintLog = false;
	private String log = "";
	
	private int loopToSlowTime;
	private int actionToSlowTime;
	private int max_verbose;
	
	public DebugTime(int _loopToSlowTime, int _actionToSlowTime, int _verbose){
		loopToSlowTime=_loopToSlowTime;
		actionToSlowTime=_actionToSlowTime;
		max_verbose=_verbose;
	};

	public void init(PrintMode _printMode,int frame)
	{
		starttime = System.nanoTime();
		currentStackLength=0;
		currentStackLength= computeVerbose(-1); //so that verbose index starts at 1 
		times = new double[100];
		times[0]= System.nanoTime();
		shouldPrintLog=false;
		printMode = _printMode;
		
		log = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+(frame>=0?"frame "+frame :"" )+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
	}
	
	
	public String tab(int verbose, boolean isActionTooSlow,boolean isLoopTooSlow )
	{
		String tabs = "";
		for(int i=0; i<verbose; i++)
			if(isActionTooSlow)
				tabs+="**";
			else if(isLoopTooSlow)
				tabs+="..";
			else
				tabs+="  ";
		return tabs;
	}
	
	public void startElapsedForVerbose()
	{
		if(printMode.equals(PrintMode.NONE))
			return;
		times[computeVerbose(-1)]= System.nanoTime();
	}
	
	/***
	 * Use this to avoid concanating string and ease the garbage collector
	 */
	public void elapsed(String mess1,String mess2,String mess3,String mess4)
	{
		elapsed(mess1+mess2+mess3+mess4,-1);//add -1 to verbose since this function calls elapsed
	}
	/***
	 * Use this to avoid concanating string and ease the garbage collector
	 */
	public void elapsed(String mess1,String mess2,String mess3)
	{
		elapsed(mess1+mess2+mess3,-1);//add -1 to verbose since this function calls elapsed
	}
	/***
	 * Use this to avoid concanating string and ease the garbage collector
	 */
	public void elapsed(String mess1,String mess2)
	{
		elapsed(mess1+mess2,-1);//add -1 to verbose since this function calls elapsed
	}
	
	public void elapsed(String mess)
	{
		elapsed(mess,-1);
	}
	private void elapsed(String mess,int plusVerbose)
	{
		if(printMode.equals(PrintMode.NONE))
			return;
		int verbose = computeVerbose(plusVerbose);
		if(verbose<=max_verbose)
		{
			//PRINT_DIRECTLY, PRINT_ONLY_SLOW_ACTIONS, PRINT_ONLY_SLOW_LOOP, PRINT_SLOW_ACTION_AND_SLOW_LOOP, PRINT_ALL
			boolean printSlowAction =printMode.equals(PrintMode.PRINT_ONLY_SLOW_ACTIONS) || printMode.equals(PrintMode.PRINT_SLOW_ACTION_OR_SLOW_LOOP) 
					|| printMode.equals(PrintMode.PRINT_ALL);
			boolean printSlowLoop = printMode.equals(PrintMode.PRINT_ONLY_SLOW_LOOP) || printMode.equals(PrintMode.PRINT_SLOW_ACTION_OR_SLOW_LOOP) 
					|| printMode.equals(PrintMode.PRINT_ALL);
			boolean printDirectly = printMode.equals(PrintMode.PRINT_DIRECTLY);
			
			boolean printSlowActionAndSlowLoop =printMode.equals(PrintMode.PRINT_SLOW_ACTION_AND_SLOW_LOOP);
			
			long nanoTime = System.nanoTime();
			double deltaAction = nanoTime-times[verbose-1];
			double deltaLoop = nanoTime-starttime;
			
			boolean isLoopTooSlow = (deltaLoop)*Math.pow(10, -6)> loopToSlowTime;
			boolean isActionTooSlow = ((deltaAction)*Math.pow(10, -6))>=actionToSlowTime;

			String tabs = tab(verbose,isActionTooSlow,isLoopTooSlow);
			
			String s = verbose+": "+"Time: "+mess+ ": "+ ((deltaAction)*Math.pow(10, -6)) + "ms / "+ (deltaLoop)*Math.pow(10, -6)+"ms";
			
			if(printDirectly)
				System.out.println(tabs+s);
			else
			{
				log+=tabs+s+"\n";
				if((printSlowAction && isActionTooSlow) || (printSlowLoop && isLoopTooSlow) || (printSlowActionAndSlowLoop &&isActionTooSlow &&isLoopTooSlow) ){
					shouldPrintLog = true;
				}
			}
			times[verbose-1]= nanoTime;
		}
	}
	
	public void print()
	{
		if(printMode==null || printMode.equals(PrintMode.NONE))
			return;
		if(shouldPrintLog){
			System.out.print(log);
		}
		log="~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
		shouldPrintLog=false;
	}
	
	private int computeVerbose(int plusVerbose)
	{
		return Thread.currentThread().getStackTrace().length - currentStackLength+plusVerbose;
	}

}
