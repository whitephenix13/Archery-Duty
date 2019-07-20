package images;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper.ObjectType;

public class ImagesBackground extends ImagesContainer{
	
	private static String path ="resources/background/";
	public enum ImBackgroundInfo implements ImageInfo{BLACK,WHITE};
	
	BufferedImage im_white =null;
	BufferedImage im_black =null;
	
	public ImagesBackground() {
		super("Image background");
	}
	
	@Override
	public void run() {
		percentage = 0;
		if(alreadyLoaded){
			percentage = 100;
			return;
		}
		int buffered_image_type = BufferedImage.TYPE_INT_RGB;//use RGB instead of ARGB for performance reasons (full screen image takes 0.7 ms instead of 2ms)
		im_white = new BufferedImage(InterfaceConstantes.tailleEcran.width,InterfaceConstantes.tailleEcran.height, buffered_image_type);
		Graphics2D    graphics = im_white.createGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect ( 0, 0, im_white.getWidth(), im_white.getHeight() );
		
		im_black = new BufferedImage(InterfaceConstantes.tailleEcran.width,InterfaceConstantes.tailleEcran.height, buffered_image_type);
		graphics = im_black.createGraphics();
		graphics.setColor(Color.black);
		graphics.fillRect ( 0, 0, im_black.getWidth(), im_black.getHeight() );
		
		percentage=100;
		alreadyLoaded=true;
		
	}
	
	/***
	 * objType: null
	 * info1 : {@link ImBackgroundInfo}
	 * info2 : null
	 */
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		if(info1.equals(ImBackgroundInfo.WHITE))
			return im_white;
		else if(info1.equals(ImBackgroundInfo.BLACK))
			return im_black;
		else{
			try {throw new Exception("No background image with type "+info1 );} catch (Exception e) {e.printStackTrace();}
			return null;
		}
			
	}
	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2,int anim)
	{
		return null;
	}
}
