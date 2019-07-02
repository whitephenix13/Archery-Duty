package images;

import java.awt.Image;

import loading.LoaderItem;

public class ImagesPrincipal extends LoaderItem{
	public static String BACKGROUND = "background";
	
	private Image background = null;
	
	public ImagesPrincipal()
	{
		super("Image main menu");
	}
	@Override
	public void run() {
		percentage = 0;
		if(alreadyLoaded){
			percentage = 100;
			return;
		}
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
