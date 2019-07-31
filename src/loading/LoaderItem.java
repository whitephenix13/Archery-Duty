package loading;

import java.awt.Image;
import java.awt.Toolkit;

public class LoaderItem implements Runnable {
	
	protected boolean alreadyLoaded = false;
	protected int percentage;
	private String name;
	
	public LoaderItem(String name)
	{
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	public int getProgress()
	{
		return percentage;
	}
	
	//Override this method to add elements to load 
	public void run()
	{
		percentage=100;
	}
	/*Exemple: 
	 * 
	 *	LoaderItem item = new LoaderItem(){
	 *	@Override
	 *	public void run()
	 *	{
	 *		//some code with local variables from the class
	 *		percentage = 100;
	 *	}
	 *};
	 * 
	 */
	
	////////////////////// UTILS /////////////////////////////
	protected Image getIm(String s, boolean waitForLoad)
	{
		Image im = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(s));
		if(waitForLoad){
			//Wait until image is load 
			while(im.getHeight(null)== -1 || (im.getWidth(null)==-1))
			{
				continue;
			}
		}
		return im; 
	}
}
