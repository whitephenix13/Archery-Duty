package debug;

/***
 * 
 * @author alexandre
 * Class used to log important information for debugging purpose
 */
public class DebugLog {
	boolean isActive = false;
	public DebugLog(){
	}
	public void setActive(boolean state){
		isActive = state;
	}

	public void log(String mess){
		if(!isActive)
			return;
		
		System.out.println(mess);
	}
	public void log(String mess, int numTab){
		if(!isActive)
			return;
		
		StringBuilder logs = new StringBuilder();
		for(int i=0; i<numTab; ++i)
			logs.append("\t");
		logs.append(mess);
		System.out.println(logs.toString());
		
	}
}
