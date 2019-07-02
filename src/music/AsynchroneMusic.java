package music;

import java.util.ArrayList;

public abstract class AsynchroneMusic {
	public abstract class Request
	{
		//override this method 
		public abstract void run();
	}
	protected ArrayList<Request> requests = null; //list of actions to execute. Override the run() method to write your action
	private final Object requestsLock = new Object(); //avoid concurrency issues with requests
	private ExecuteRequest executeRequests;//Runnable that executes all the request and then go to pause (to avoid taking too much memory)
	private Thread t;
	
	//TODO: see stack overflow "how pause and then resume a thread"
	private class ExecuteRequest implements Runnable
	{
		private volatile boolean running =true;
		private volatile boolean paused = false;
		private final Object pauseLock= new Object();
		@Override
		public void run() {
			while(running){
				synchronized(pauseLock){
					if(!running)
					{
						//may have changed while waiting to synchronize on pauseLock
						break;
					}
					if(paused)
					{
						try{
							synchronized(pauseLock){
								pauseLock.wait();//will cause this Thread to block until another Thread calls pauseLock.notifyAll()
								//Note that calling wait() will relinquish the synchronized lock that this thread holds on pauseLock so another Thread
								//can acquire the lock to call notifyAll()
							}
						}
						catch(InterruptedException ex){break;}
					}
					if(!running)//running might have changed since we paused 
						break;
				}
				//================ MAIN CODE STARTS HERE ==================
				boolean hasNewRequest =true;
				int start_index =0;
				int num_requests = 0;
				synchronized(requestsLock){
					num_requests = requests.size();
				}
				do{
					//execute each requests 
					for(int i=start_index; i<num_requests; ++i)
						requests.get(i).run();
					//check whether their were new requests while the previous ones were executed 
					synchronized(requestsLock){
						start_index=num_requests;
						if(num_requests != requests.size())//new requests added
						{
							num_requests=requests.size();
							hasNewRequest=true;
						}
						else{
							requests.clear();
							hasNewRequest=false;
							pauseRequests(); //stop execution within the lock to avoid that a new request is added between the end of the while and the exit of the function
						}
					}
				}
				while(hasNewRequest);

			}
		}
		public void stop()
		{
			running = false;
			//you might also want to interrupt the Thread that is running this runnable too or perhaps call
			resume();
			//to unblock
		}
		public void pause()
		{
			paused=true;
		}
		public void resume(){
			synchronized(pauseLock){
				paused=false;
				pauseLock.notifyAll();//Unblocks Thread
			}
		}
		
		
	}
	
	public AsynchroneMusic()
	{
		requests= new ArrayList<Request>();
		executeRequests = new ExecuteRequest();
		t = new Thread(executeRequests);
		t.setDaemon(true);
		t.start();
		//Pause the thread until notify is called
		pauseRequests();
	}
	
	protected void runRequests()
	{
		executeRequests.resume();
	}
	protected void pauseRequests()
	{
		executeRequests.pause();
	}
	
}
