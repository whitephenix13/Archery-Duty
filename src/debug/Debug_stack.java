package debug;

import java.util.Map;

public class Debug_stack {
	/**
	 * 
	 * @param ind: -1 for all 
	 */
	private boolean isLogFiltered(String log,String[] logWithTheseWords,String[] logWithoutTheseWords)
	{
		if(logWithoutTheseWords!=null)
			for(String s : logWithoutTheseWords)
				if(log.contains(s))
					return true;
		if(logWithTheseWords!= null)
			for(String s : logWithTheseWords)
				if(log.contains(s))
					return false;
		return logWithTheseWords.length>0; //if a specific filter was set on what the log should contain, all log not containing that are filtered, otherwise no filter
	}
	
	public String getLastTraceFromKnowSource(String[] logWithTheseWords,String[] logWithoutTheseWords)
	{
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		String res = stacks[stacks.length-1].toString();
			
		boolean firstKnownSourceFound =false;
		for(int i=(stacks.length-1);i>=0;--i)
			if(stacks[i].toString().contains("(Unknown Source)") && firstKnownSourceFound)
				break;
			else if(stacks[i].toString().contains("(Unknown Source)") )
				continue;
			else
			{
				firstKnownSourceFound=true;
				res=stacks[i].toString();
			}
		if(!isLogFiltered(res,logWithTheseWords,logWithoutTheseWords))
			return res;
		else return "";
	}
	public void print(int ind)
	{
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		if(ind == -1)
			for(int i=(stacks.length-1);i>=2;--i)
				System.out.println(stacks[i]);
		else
			System.out.println(stacks[ind]);
	};
}
