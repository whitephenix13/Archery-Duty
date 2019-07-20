package images;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.projectile.fleches.Fleche;

public class ImagesFlecheIcon extends ImagesContainer{	
	
	private static String path ="resources/projectile/fleches/icons/";
	
	Image im_feu= null;
	Image im_electrique= null;
	Image im_glace= null;
	Image im_roche= null;

	Image im_lumiere= null;
	Image im_ombre= null;
	Image im_vent= null;
	Image im_grappin= null;

	Image im_faucon= null;
	Image im_explosive= null;
	Image im_trou_noir= null;
	Image im_barrage= null;

	Image im_marque_mortelle= null;
	Image im_ninja= null;
	Image im_leurre= null;
	Image im_absorption= null;

	public ImagesFlecheIcon()
	{
		super("Image fleche icon");
	}

	@Override
	public void run() {
		percentage=0;
		if(alreadyLoaded){
			percentage=100;
			return;
		}

		int nb = 16;
		im_electrique=(getIm(path+"electrique.png",true));
		im_feu=(getIm(path+"feu.png",true));
		im_glace=(getIm(path+"glace.png",true));
		im_roche=(getIm(path+"roche.png",true));
		
		percentage = (int)(4*100.0/nb);

		im_lumiere=(getIm(path+"lumiere.png",true));
		im_ombre=(getIm(path+"ombre.png",true));
		im_vent=(getIm(path+"vent.png",true));
		im_grappin=(getIm(path+"grappin.png",true));

		percentage = (int)(8*100.0/nb);

		im_faucon =(getIm(path+"faucon.png",true));
		im_explosive=(getIm(path+"explosive.png",true));
		im_trou_noir=(getIm(path+"trou_noir.png",true));
		im_barrage=(getIm(path+"barrage.png",true));	

		percentage = (int)(12*100.0/nb);

		im_marque_mortelle=(getIm(path+"marque_mortelle.png",true));
		im_ninja=(getIm(path+"leurre.png",true));
		im_leurre=(getIm(path+"absorption.png",true));
		im_absorption=(getIm(path+"ninja.png",true));

		percentage=100;
		alreadyLoaded=true;

	}
	/***
	 * objType:{@link ObjectType#FLECHE} ... 
	 * info1 : null
	 * info2 : null
	 */
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		Image im = null;		

		if(objType.equals(ObjectType.ELECTRIQUE))
			im=(this.im_electrique);
		else if(objType.equals(ObjectType.FEU))
			im=(this.im_feu);
		else if(objType.equals(ObjectType.GLACE))
			im=(this.im_glace);
		else if(objType.equals(ObjectType.ROCHE))
			im=(this.im_roche);

		else if(objType.equals(ObjectType.LUMIERE))
			im=(this.im_lumiere);
		else if(objType.equals(ObjectType.GRAPPIN))
			im=(this.im_grappin);
		else if(objType.equals(ObjectType.OMBRE))
			im=(this.im_ombre);
		else if(objType.equals(ObjectType.VENT))
			im=(this.im_vent);

		else if(objType.equals(ObjectType.BARRAGE))
			im=(this.im_barrage);
		else if (objType.equals(ObjectType.FAUCON))
			im=(this.im_faucon);
		else if(objType.equals(ObjectType.EXPLOSIVE))
			im=(this.im_explosive);
		else if(objType.equals(ObjectType.TROU_NOIR))
			im=(this.im_trou_noir);

		else if(objType.equals(ObjectType.MARQUE_MORTELLE))
			im=(this.im_marque_mortelle);
		else if (objType.equals(ObjectType.ABSORPTION))
			im=(this.im_absorption);
		else if (objType.equals(ObjectType.NINJA))
			im=(this.im_ninja);
		else if (objType.equals(ObjectType.LEURRE))
			im=(this.im_leurre);

		return im;

	}
	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int anim)
	{
		return null;
	}
	/**
	 * 
	 * @param name: string corresponding to the arrow type (ie: feu) 
	 * @return Returns all images of the same class (ie: materielle) than the arrow 
	 */
	public Image[]  getAllImagesOfSameClass(ObjectType arrowType,ObjectType[] outArrowType)
	{
		Image[] res = new Image[4];
		ObjectType[][] allArrowType = {Fleche.DESTRUCTRICE_CLASS,Fleche.MATERIELLE_CLASS,Fleche.RUSEE_CLASS,Fleche.SPRIRITUELLE_CLASS};
		for(int i = 0; i<4; ++i){
			if(Arrays.asList(allArrowType[i]).contains(arrowType))
			{
				int index = 1;
				res[0]= getImage(arrowType,null,null);
				outArrowType[0]=arrowType;
				for(ObjectType type : allArrowType[i])
				{
					if(!type.equals(arrowType))
					{
						res[index]= getImage(type,null,null);
						outArrowType[index]=type;
						index+=1;
					}
				}
				break;
			}
		}
		return res;
	}

}
