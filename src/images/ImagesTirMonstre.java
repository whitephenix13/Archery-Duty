package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.mouvement.projectile.Mouvement_projectile.MouvProjectileEnum;

public class ImagesTirMonstre extends ImagesContainer{
	
	public static ImagesTirMonstre me=null; //singleton
	private static String path ="resources/projectile/tirMonstre/";
	ArrayList<Image> im_SP_normal_creation= new ArrayList<Image>();
	ArrayList<Image> im_SP_normal_idle= new ArrayList<Image>();
	
	public ImagesTirMonstre()
	{
		super("Image tir monstre");
		if(me==null)
			me=this;
	}
	
	@Override
	public void run()
	{
		percentage=0;
		if(alreadyLoaded){
			percentage=100;
			return;
		}
		
		for(int i=0; i<3; ++i)
			im_SP_normal_creation.add(getIm(path+"spirel/normal/creation/"+i+".png",true));
		
		for(int i=0; i<1; ++i)
			im_SP_normal_idle.add(getIm(path+"spirel/normal/idle/"+i+".png",true));
		percentage = 100;;
		alreadyLoaded=true;
	}
	
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		return null;
	}
	
	/***
	 * objType: {@link ObjectType#TIR_SPIREL}
	 * info1 : {@link MouvProjectileEnum}
	 * info2 : null
	 */
	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int mouv_index)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(objType.equals(ObjectType.TIR_SPIREL))
		{
			if(info1.equals(MouvProjectileEnum.T_normal_creation))
				im.add(im_SP_normal_creation.get(mouv_index));
			else if(info1.equals(MouvProjectileEnum.T_normal_idle))
				im.add(im_SP_normal_idle.get(mouv_index));
		}

		return im;
	}
}
