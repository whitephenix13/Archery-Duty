package images;

import java.awt.Image;
import java.util.ArrayList;

import fleches.Fleche;
import loading.LoadMediaThread;

public class ImagesPrincipal extends LoadMediaThread{
	private Image background = null;
	@Override
	public void loadMedia() {
		if(mediaLoaded)
			return;
		loadMedia("", "background");
		setPercentage(100);
		mediaLoaded=true;
		
	}
	@Override
	public void loadMedia(String media_categorie, String filename) {
		if(filename.equals("background"))
		{
			if(background==null)
				background=getIm("resources/Principal.png",true);
		}
	} 
	
	public Image getImage(String name)
	{
		if(name.equals("background"))
		{
			return background;
		}
		return null;
	}
}
