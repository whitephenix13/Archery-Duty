package images;

import java.awt.Image;
import java.util.Arrays;

import gameConfig.TypeObject;
import loading.LoaderItem;

public class ImagesFlecheIcon extends LoaderItem{	
	
	private static String path ="resources/projectile/fleches/icons/";
	
	Image im_feu= null;
	Image im_electrique= null;
	Image im_glace= null;
	Image im_roche= null;

	Image im_lumiere= null;
	Image im_ombre= null;
	Image im_vent= null;
	Image im_grappin= null;

	Image im_foudre= null;
	Image im_explosive= null;
	Image im_trou_noir= null;
	Image im_bogue= null;

	Image im_auto_teleguidee= null;
	Image im_retard= null;
	Image im_v_fleche= null;
	Image im_cac= null;

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

		im_foudre =(getIm(path+"foudre.png",true));
		im_explosive=(getIm(path+"explosive.png",true));
		im_trou_noir=(getIm(path+"trou_noir.png",true));
		im_bogue=(getIm(path+"bogue.png",true));	

		percentage = (int)(12*100.0/nb);

		im_auto_teleguidee=(getIm(path+"auto_teleguidee.png",true));
		im_retard=(getIm(path+"retard.png",true));
		im_v_fleche=(getIm(path+"v_fleche.png",true));
		im_cac=(getIm(path+"corps_a_corps.png",true));

		percentage=100;
		alreadyLoaded=true;

	}

	public Image getImage(String name)
	{
		Image im = null;		

		if(name.equals(TypeObject.ELECTRIQUE))
			im=(this.im_electrique);
		else if (name.equals(TypeObject.FEU))
			im=(this.im_feu);
		else if (name.equals(TypeObject.GLACE))
			im=(this.im_glace);
		else if (name.equals(TypeObject.ROCHE))
			im=(this.im_roche);

		else if (name.equals(TypeObject.LUMIERE))
			im=(this.im_lumiere);
		else if (name.equals(TypeObject.GRAPPIN))
			im=(this.im_grappin);
		else if (name.equals(TypeObject.OMBRE))
			im=(this.im_ombre);
		else if (name.equals(TypeObject.VENT))
			im=(this.im_vent);

		else if (name.equals(TypeObject.BOGUE))
			im=(this.im_bogue);
		else if (name.equals(TypeObject.FOUDRE))
			im=(this.im_foudre);
		else if (name.equals(TypeObject.EXPLOSIVE))
			im=(this.im_explosive);
		else if (name.equals(TypeObject.TROU_NOIR))
			im=(this.im_trou_noir);

		else if (name.equals(TypeObject.AUTO_TELEGUIDEE))
			im=(this.im_auto_teleguidee);
		else if (name.equals(TypeObject.CAC))
			im=(this.im_cac);
		else if (name.equals(TypeObject.RETARD))
			im=(this.im_retard);
		else if (name.equals(TypeObject.V_FLECHE))
			im=(this.im_v_fleche);

		return im;

	}

	/**
	 * 
	 * @param name: string corresponding to the arrow type (ie: feu) 
	 * @return Returns all images of the same class (ie: materielle) than the arrow 
	 */
	public Image[]  getAllImagesOfSameClass(String name,String[] outArrowType)
	{
		Image[] res = new Image[4];
		String[][] allArrowType = {TypeObject.DESTRUCTRICE_CLASS,TypeObject.MATERIELLE_CLASS,TypeObject.RUSEE_CLASS,TypeObject.SPRIRITUELLE_CLASS};
		for(int i = 0; i<4; ++i){
			if(Arrays.asList(allArrowType[i]).contains(name))
			{
				int index = 1;
				res[0]= getImage(name);
				outArrowType[0]=name;
				for(String s : allArrowType[i])
				{
					if(!s.equals(name))
					{
						res[index]= getImage(s);
						outArrowType[index]=s;
						index+=1;
					}
				}
				break;
			}
		}
		return res;
	}

}
