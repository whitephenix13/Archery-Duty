package debug;

public class Debug_stack {
	public Debug_stack(){};
	/**
	 * 
	 * @param ind: -1 for all 
	 */
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
