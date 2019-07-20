package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.deplacement.projectile.Mouvement_projectile.MouvProjectileEnum;

public class ImagesTirMonstre extends ImagesContainer{
	
	private static String path ="resources/projectile/tirMonstre/";
	ArrayList<Image> im_SP_tir= new ArrayList<Image>();
	
	public ImagesTirMonstre()
	{
		super("Image tir monstre");
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
			im_SP_tir.add(getIm(path+"spirel/"+i+".png",true));
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
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int anim)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(objType.equals(ObjectType.TIR_SPIREL))
		{
			if(info1.equals(MouvProjectileEnum.T_normal))
				im.add(im_SP_tir.get(anim));
		}

		return im;
	}
}
