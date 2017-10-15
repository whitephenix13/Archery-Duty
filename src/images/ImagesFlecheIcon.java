package images;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;

import fleches.Fleche;
import loading.LoadMediaThread;
import types.TypeObject;

public class ImagesFlecheIcon extends LoadMediaThread{	
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

	public ImagesFlecheIcon(){}

	@Override
	public void loadMedia() {
		if(mediaLoaded)
			return;

		int nb = 16;
		im_electrique=(getIm("resources/fleches/icons/electrique.png",true));
		im_feu=(getIm("resources/fleches/icons/feu.png",true));
		im_glace=(getIm("resources/fleches/icons/glace.png",true));
		im_roche=(getIm("resources/fleches/icons/roche.png",true));

		setPercentage((int)(400.0/nb));

		im_lumiere=(getIm("resources/fleches/icons/lumiere.png",true));
		im_ombre=(getIm("resources/fleches/icons/ombre.png",true));
		im_vent=(getIm("resources/fleches/icons/vent.png",true));
		im_grappin=(getIm("resources/fleches/icons/grappin.png",true));

		setPercentage((int)(800.0/nb));

		im_foudre =(getIm("resources/fleches/icons/foudre.png",true));
		im_explosive=(getIm("resources/fleches/icons/explosive.png",true));
		im_trou_noir=(getIm("resources/fleches/icons/trou_noir.png",true));
		im_bogue=(getIm("resources/fleches/icons/bogue.png",true));	

		setPercentage((int)(1200.0/nb));

		im_auto_teleguidee=(getIm("resources/fleches/icons/auto_teleguidee.png",true));
		im_retard=(getIm("resources/fleches/icons/retard.png",true));
		im_v_fleche=(getIm("resources/fleches/icons/v_fleche.png",true));
		im_cac=(getIm("resources/fleches/icons/corps_a_corps.png",true));

		setPercentage(100);
		mediaLoaded=true;

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
		String[][] allArrowType = {TypeObject.DESTRUCTRICE_CLASS,TypeObject.MATERIELLE_CLASS,TypeObject.MATERIELLE_CLASS,TypeObject.SPRIRITUELLE_CLASS};
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

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub

	}

}
