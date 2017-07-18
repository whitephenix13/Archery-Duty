package loading;

import java.awt.Image;
import java.awt.Toolkit;

import debug.Debug_stack;

public abstract class LoadMediaThread {

	protected boolean mediaLoaded=false;
	int percentage=0;
	public void setPercentage(int per){percentage=per;}
	public int getPercentage(){return percentage;}
	
	public abstract void loadMedia();
	public abstract void loadMedia(String media_categorie, String filename);
	protected Image getIm(String s, boolean waitForLoad)
	{
		Image im = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(s));
		if(waitForLoad){
			waitForLoaded(im);
		}
		return im; 
	}
	protected void waitForLoaded(Image im)
	{
		while(im.getHeight(null)== -1 || (im.getWidth(null)==-1))
		{
			continue;
		}
	}
}
