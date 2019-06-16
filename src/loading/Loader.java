package loading;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import gameConfig.InterfaceConstantes;
import utils.observer.Observable;

public class Loader {
	
	/**
	 * To use this : 
	 * create the loader : Loader loader = new Loader();
	 * add custom LoaderItem or existing ones from LoaderUtils: loader.addItem(LoaderUtils.loadNiveau(...,...));
	 * when all items to load are ready, start the process: loader.start(); Progress can be obtained via getProgress()
	 * Use showLoading(g) in paintComponents(g) to display the loading screen 
	 */
	//display parameters
	private double UPDATE_LOAD = 0.5; //update every UPDATE_LOAD seconds the dots
	private double start_load = -1; //keep track of the start of the load to display load progress
	private int numberDot = 0;
	
	private ArrayList<LoaderItem> pendingItems = new ArrayList<LoaderItem>();
	private LoaderItem mainLoader = null;
	private boolean loadingDone=false;
	private boolean lastRound = true;
	
	public int getProgress()
	{
		if(mainLoader==null)
			return 100;
		else
			return mainLoader.getProgress();
	}
	public boolean isLoadingDone(){
		return loadingDone;
	}
	
	public void reset()
	{
		loadingDone=false;
		start_load = -1; 
		numberDot = 0;
	}
	
	public void addItem(LoaderItem item)
	{
		pendingItems.add(item);
	}
	
	public void start()
	{
		mainLoader = new LoaderItem(){
			@Override
			public void run()
			{
				for(int i =0; i< pendingItems.size(); ++i)
					pendingItems.get(i).run();
				loadingDone=true;
			}
			@Override
			public int getProgress()
			{
				int res = 0;
				int num = pendingItems.size();
				for(int i =0; i< num; ++i){
					res+=pendingItems.get(i).getProgress();
				}
				res= (int) (((float) res )/ num);
				
				return res;
			}
		};
		
		Thread t = new Thread(mainLoader);
		t.start();
	}
	public void wait(Observable modelToUpdateGraphics)
	{
		while(!this.loadingDone || lastRound)
		{
			if(this.loadingDone)
				lastRound=false;
			
			//slow down loop with sleep 
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(modelToUpdateGraphics!=null){
				modelToUpdateGraphics.notifyMainObserver();
			}
			
			continue;
		}
	}
	public void showLoading(Graphics g)
	{
		if(start_load==-1 )
			start_load=System.nanoTime();
		else if((System.nanoTime()- start_load)*Math.pow(10, -9)>UPDATE_LOAD){
			start_load=System.nanoTime();
			numberDot=(numberDot+1)%4;
		}
		
		String s  = "Chargement";
		switch(numberDot)
		{
			case 1: s+=" .";break; 
			case 2: s+=" . .";break; 
			case 3: s+=" . . .";break; 
			default: break; 
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(new Font("TimesRoman", Font.PLAIN, 50)); 
		g2.setColor(Color.WHITE);
		g2.drawString(s, InterfaceConstantes.WINDOW_WIDTH/2-120, InterfaceConstantes.WINDOW_HEIGHT/2-25);
		g2.drawString(getProgress()+"%", InterfaceConstantes.WINDOW_WIDTH/2-50, InterfaceConstantes.WINDOW_HEIGHT/2+40);
	}
}
