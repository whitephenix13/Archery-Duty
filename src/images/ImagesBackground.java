package images;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import gameConfig.InterfaceConstantes;
import loading.LoaderItem;
import partie.conditions.Condition;

public class ImagesBackground extends LoaderItem{
	public ImagesBackground() {
		super("Image background");
	}

	private static String path ="resources/background/";
	BufferedImage im_white =null;
	
	@Override
	public void run() {
		percentage = 0;
		if(alreadyLoaded){
			percentage = 100;
			return;
		}
		
		im_white = new BufferedImage(InterfaceConstantes.tailleEcran.width,InterfaceConstantes.tailleEcran.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D    graphics = im_white.createGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect ( 0, 0, im_white.getWidth(), im_white.getHeight() );
		
		percentage=100;
		alreadyLoaded=true;
		
	}
	
	public Image getImage(String name)
	{
		//default background is white 
		return im_white;
	}
}
