package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.mouvement.entity.Mouvement_entity.EntityTypeMouv;

public class ImagesMonstre extends ImagesContainer{
	
	public static ImagesMonstre me=null; //singleton
	public static String path ="resources/entitie/monstre/";
	ArrayList<Image> im_SP_attente= new ArrayList<Image>();
	ArrayList<Image> im_SP_marche= new ArrayList<Image>();
	ArrayList<Image> im_SP_saut= new ArrayList<Image>();
	ArrayList<Image> im_SP_tir= new ArrayList<Image>();
	public ImagesMonstre()
	{
		super("Image monstre");
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
		
		for(int i=0; i<2; ++i)
			im_SP_attente.add(getIm(path+"spirel/attente/"+i+".gif",true));
		percentage =(int)(2*100.0/8);

		for(int i=0; i<4; ++i)
			im_SP_marche.add(getIm(path+"spirel/marche/"+i+".gif",true));
		percentage =(int)(6*100.0/8);
				
		for(int i=0; i<2; ++i)
			im_SP_saut.add(getIm(path+"spirel/attente/"+i+".gif",true));
		for(int i=0; i<2; ++i)
			im_SP_tir.add(getIm(path+"spirel/tir/"+i+".gif",true));
		percentage =100;
		alreadyLoaded=true;
		
	}
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		return null;
	}
	
	/***
	 * objType: {@link ObjectType#SPIREL}
	 * info1 : {@link EntityTypeMouv}
	 * info2 : null
	 */
	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int mouv_index)
	{
		ArrayList<Image> im = new ArrayList<Image>();
//				im.add(this.im_electrique_aura.get(fleche.mouv_index));

		if (objType.equals(ObjectType.SPIREL))
		{
			if(info1.equals(EntityTypeMouv.ATTENTE))
				im.add(im_SP_attente.get(mouv_index));
			else if (info1.equals(EntityTypeMouv.MARCHE))
				im.add(im_SP_marche.get(mouv_index));
			else if(info1.equals(EntityTypeMouv.SAUT))
				im.add(im_SP_saut.get(mouv_index));
			else if(info1.equals(EntityTypeMouv.TIR))
				im.add(im_SP_tir.get(mouv_index));
		}
		return im;
	}


}
