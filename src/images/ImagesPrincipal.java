package images;

import java.awt.Image;
import java.util.ArrayList;

import fleches.Fleche;
import loading.LoadMediaThread;

public class ImagesPrincipal extends LoadMediaThread{
	public static String BACKGROUND = "background";
	
	private Image background = null;
	@Override
	public void loadMedia() {
		if(mediaLoaded)
			return;
		loadMedia("", BACKGROUND);
		setPercentage(100);
		mediaLoaded=true;
		
	}
	@Override
	public void loadMedia(String media_categorie, String filename) {
		if(filename.equals(BACKGROUND))
		{
			if(background==null)
				background=getIm("resources/Principal.png",true);
		}
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
