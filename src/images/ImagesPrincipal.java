package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import images.ImagesContainer.ImageInfo;
import partie.mouvement.entity.Mouvement_entity.EntityTypeMouv;

public class ImagesPrincipal extends ImagesContainer{
	public static ImagesPrincipal me=null; //singleton
	public static enum ImPrincipalInfo implements ImageInfo{BACKGROUND}
	
	private Image background = null;
	
	public ImagesPrincipal()
	{
		super("Image main menu");
		if(me==null)
			me=this;
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
	
	/***
	 * objType: null
	 * info1 : {@link ImPrincipalInfo}
	 * info2 : null
	 */
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		if(info1.equals(ImPrincipalInfo.BACKGROUND))
		{
			return background;
		}
		return null;
	}

	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int mouv_index)
	{
		return null;
	}
}
