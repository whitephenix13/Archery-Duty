package debug;

import principal.InterfaceConstantes;

public class Debug_time {
	private double MAX_TIME = 0.2;
	
	private double time;
	private double starttime;
	
	public Debug_time(){};
	public void init()
	{
		starttime= System.nanoTime();
		time = starttime;
	}
	
	public void elapsed(String mess, int verbose)
	{
		if(verbose<=InterfaceConstantes.DEBUG_TIME_VERBOSE)
		{
			String tabs = "";
			for(int i=0; i<verbose; i++)
				tabs+="\t";
			
			String s = "Time: "+mess+ ": "+ ((System.nanoTime()-time)*Math.pow(10, -9)) + " / "+ (System.nanoTime()-starttime)*Math.pow(10, -9);
					
			if((System.nanoTime()-starttime)*Math.pow(10, -9)>MAX_TIME)
				System.err.println(tabs+"W"+s);
			else
				System.out.println(tabs+s);
						
			time= System.nanoTime();
		}
	}
}
