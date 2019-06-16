package images;

import java.awt.Image;

import loading.LoaderItem;

public class ImagesPrincipal extends LoaderItem{
	public static String BACKGROUND = "background";
	
	private Image background = null;
	@Override
	public void run() {
		if(alreadyLoaded)
			return;
		background=getIm("resources/principal.png",true);
		percentage = 100;		
		alreadyLoaded=true;
	}

	public Image getImage(String name)
	{
		if(name.equals(BACKGROUND))
		{
			return background;
		}
		return null;
	}
}
