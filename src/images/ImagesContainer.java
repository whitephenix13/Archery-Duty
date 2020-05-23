package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import loading.LoaderItem;

public abstract class ImagesContainer extends LoaderItem{
	public static enum ImageGroup {BACKGROUND,CONDITION,EFFECT,FLECHE,FLECHEICON,HEROS,MONDE,MONSTRE,PRINCIPAL,TIRMONSTRE};
	public interface ImageInfo{}; //TO BE IMPLEMENTED BY CLASS IMPLEMENTING IMAGE CONTAINER 
	public interface ObjectSubType{}; //TO BE IMPLEMENTED BY CLASS IMPLEMENTING IMAGE CONTAINER. Gives more precision about the image. Ex; Electrique/Ground (often matches subTypeMouv)
	public ImagesContainer(String name) {
		super(name);
	}
	public abstract Image getImage(ObjectType typeObj, ImageInfo info1,ImageInfo info2);
	public abstract ArrayList<Image> getImages(ObjectType typeObj, ImageInfo info1,ImageInfo info2,int mouv_index);
}
