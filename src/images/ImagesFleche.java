package images;

import java.awt.Image;
import java.util.ArrayList;

import fleches.Fleche;
import loading.LoadMediaThread;
import loading.OnLoadingCallback;

public class ImagesFleche extends LoadMediaThread{

	ArrayList<Image> im_fleche= new ArrayList<Image>();
	ArrayList<Image> im_foudre_aura= new ArrayList<Image>();
	ArrayList<Image> im_electrique_aura= new ArrayList<Image>();
	ArrayList<Image> im_glace_aura= new ArrayList<Image>();
	ArrayList<Image> im_roche_aura= new ArrayList<Image>();

	ArrayList<Image> im_feu_aura= new ArrayList<Image>();
	ArrayList<Image> im_ombre_aura= new ArrayList<Image>();
	ArrayList<Image> im_vent_aura= new ArrayList<Image>();
	ArrayList<Image> im_grappin_aura= new ArrayList<Image>();

	ArrayList<Image> im_lumiere_aura= new ArrayList<Image>();
	ArrayList<Image> im_explosive_aura= new ArrayList<Image>();
	ArrayList<Image> im_trou_noir_aura= new ArrayList<Image>();
	ArrayList<Image> im_bogue_aura= new ArrayList<Image>();

	ArrayList<Image> im_auto_teleguidee_aura= new ArrayList<Image>();
	ArrayList<Image> im_retard_aura= new ArrayList<Image>();
	ArrayList<Image> im_v_fleche_aura= new ArrayList<Image>();
	ArrayList<Image> im_cac_aura= new ArrayList<Image>();

	public ImagesFleche()
	{
	}
	
	@Override
	public void loadMedia()
	{
		if(mediaLoaded)
			return;
		
		for(int i=0; i<4; ++i){
			im_fleche.add(getIm("resources/fleches/"+i+".png",true));
			
			setPercentage((int)((1.0 + i*17)/0.68));
			im_foudre_aura.add(getIm("resources/fleches/auras/foudre/"+i+".png",true));
			im_electrique_aura.add(getIm("resources/fleches/auras/electrique/"+i+".png",true));
			im_glace_aura.add(getIm("resources/fleches/auras/glace/"+i+".png",true));
			im_roche_aura.add(getIm("resources/fleches/auras/roche/"+i+".png",true));
			
			setPercentage((int)((5.0 + i*17)/0.68));

			im_feu_aura.add(getIm("resources/fleches/auras/feu/"+i+".png",true));
			im_ombre_aura.add(getIm("resources/fleches/auras/ombre/"+i+".png",true));
			im_vent_aura.add(getIm("resources/fleches/auras/vent/"+i+".png",true));
			im_grappin_aura.add(getIm("resources/fleches/auras/grappin/"+i+".png",true));
			
			setPercentage((int)((9.0 + i*17)/0.68));

			im_lumiere_aura.add(getIm("resources/fleches/auras/lumiere/"+i+".png",true));
			im_explosive_aura.add(getIm("resources/fleches/auras/explosive/"+i+".png",true));
			im_trou_noir_aura.add(getIm("resources/fleches/auras/trou_noir/"+i+".png",true));
			im_grappin_aura.add(getIm("resources/fleches/auras/grappin/"+i+".png",true));	
			
			setPercentage((int)((13.0 + i*17)/0.68));

			im_auto_teleguidee_aura.add(getIm("resources/fleches/auras/auto_teleguidee/"+i+".png",true));
			im_retard_aura.add(getIm("resources/fleches/auras/retard/"+i+".png",true));
			im_v_fleche_aura.add(getIm("resources/fleches/auras/v_fleche/"+i+".png",true));
			im_cac_aura.add(getIm("resources/fleches/auras/corps_a_corps/"+i+".png",true));
			setPercentage((int)((17.0 + i*17)/0.68));
		}
		setPercentage(100);
		mediaLoaded=true;
	}
	
	public ArrayList<Image> getImage(Fleche fleche)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(!fleche.type_fleche.equals(""))
		{
			if(fleche.type_fleche.equals(Fleche.MATERIELLE.ELECTRIQUE))
				im.add(this.im_electrique_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.MATERIELLE.FEU))
				im.add(this.im_feu_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.MATERIELLE.GLACE))
				im.add(this.im_glace_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.MATERIELLE.ROCHE))
				im.add(this.im_roche_aura.get(fleche.anim));
			
			
			else if (fleche.type_fleche.equals(Fleche.SPIRITUELLE.GRAPPIN))
				im.add(this.im_grappin_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.SPIRITUELLE.OMBRE))
				im.add(this.im_ombre_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.SPIRITUELLE.LUMIERE))
				im.add(this.im_lumiere_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.SPIRITUELLE.VENT))
				im.add(this.im_vent_aura.get(fleche.anim));
			
			else if (fleche.type_fleche.equals(Fleche.DESTRUCTRICE.BOGUE))
				im.add(this.im_bogue_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.DESTRUCTRICE.FOUDRE))
				im.add(this.im_foudre_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.DESTRUCTRICE.EXPLOSIVE))
				im.add(this.im_explosive_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.DESTRUCTRICE.TROU_NOIR))
				im.add(this.im_trou_noir_aura.get(fleche.anim));
			
			else if (fleche.type_fleche.equals(Fleche.RUSEE.AUTO_TELEGUIDEE))
				im.add(this.im_auto_teleguidee_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.RUSEE.CAC))
				im.add(this.im_cac_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.RUSEE.RETARD))
				im.add(this.im_retard_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.RUSEE.V_FLECHE))
				im.add(this.im_v_fleche_aura.get(fleche.anim));
		}
		im.add(im_fleche.get(fleche.anim));

		return im;
		
	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		// TODO Auto-generated method stub
		
	}

}
