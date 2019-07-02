package debug;

import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.google.monitoring.runtime.instrumentation.Sampler;

public class DebugObjectCreation implements Sampler{
	
	Debug_stack debug;
	private final String[] logWithTheseWords = {};//set to specific word for specific login 
	private final String[] logWithoutTheseWords = {"debug.Debug_stack.getLastTraceFromKnowSource",
			"java.security.AccessController.doPrivileged"};
	private String stack_trace;
	public static void start()
	{
		AllocationRecorder.addSampler(new DebugObjectCreation());
	}

	@Override
	public void sampleAllocation(int count, String desc, Object newObj, long size) {
			debug = new Debug_stack();
			stack_trace = debug.getLastTraceFromKnowSource(logWithTheseWords,logWithoutTheseWords);
			if(stack_trace!="")
			{
				System.out.println(stack_trace+" "+ (newObj != null? newObj.toString() : "null") + " ||| " + desc + " ||| size= " + size +(count != -1 ? " ||| array_size= " + count: "") );
			}
	}
}
