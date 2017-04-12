package fleches;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;

public class ImagesFleche {

	ArrayList<Image> im_fleche= new ArrayList<Image>();
	ArrayList<Image> im_foudre_aura= new ArrayList<Image>();
	ArrayList<Image> im_electrique_aura= new ArrayList<Image>();
	ArrayList<Image> im_glace_aura= new ArrayList<Image>();
	ArrayList<Image> im_roche_aura= new ArrayList<Image>();

	ArrayList<Image> im_feu_aura= new ArrayList<Image>();
	ArrayList<Image> im_ombre_aura= new ArrayList<Image>();
	ArrayList<Image> im_vent_aura= new ArrayList<Image>();
	ArrayList<Image> im_grappin_aura= new ArrayList<Image>();

	ArrayList<Image> im_chargee_aura= new ArrayList<Image>();
	ArrayList<Image> im_explosive_aura= new ArrayList<Image>();
	ArrayList<Image> im_trou_noir_aura= new ArrayList<Image>();
	ArrayList<Image> im_bogue_aura= new ArrayList<Image>();

	ArrayList<Image> im_auto_teleguidee_aura= new ArrayList<Image>();
	ArrayList<Image> im_retard_aura= new ArrayList<Image>();
	ArrayList<Image> im_v_fleche_aura= new ArrayList<Image>();
	ArrayList<Image> im_cac_aura= new ArrayList<Image>();

	public ImagesFleche()
	{
		chargerImages();
	}
	
	public void chargerImages()
	{
		for(int i=0; i<4; ++i){
			im_fleche.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/"+i+".png")));
			im_foudre_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/foudre/"+i+".png")));
			im_electrique_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/electrique/"+i+".png")));
			im_glace_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/glace/"+i+".png")));
			im_roche_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/roche/"+i+".png")));
			
			im_feu_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/feu/"+i+".png")));
			im_ombre_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/ombre/"+i+".png")));
			im_vent_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/vent/"+i+".png")));
			im_grappin_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/grappin/"+i+".png")));
			
			im_chargee_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/chargee/"+i+".png")));
			im_explosive_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/explosive/"+i+".png")));
			im_trou_noir_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/trou_noir/"+i+".png")));
			im_grappin_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/grappin/"+i+".png")));
			
			im_auto_teleguidee_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/auto_teleguidee/"+i+".png")));
			im_retard_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/retard/"+i+".png")));
			im_v_fleche_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/v_fleche/"+i+".png")));
			im_cac_aura.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(
					"resources/fleches/auras/corps_a_corps/"+i+".png")));
		}
	}
	
	public ArrayList<Image> getImage(Fleche fleche)
	{
		ArrayList<Image> im = new ArrayList<Image>();
		if(!fleche.type_fleche.equals(""))
		{
			if(fleche.type_fleche.equals(Fleche.MATERIELLE.ELECTRIQUE))
				im.add(this.im_electrique_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.MATERIELLE.FOUDRE))
				im.add(this.im_foudre_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.MATERIELLE.GLACE))
				im.add(this.im_glace_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.MATERIELLE.ROCHE))
				im.add(this.im_roche_aura.get(fleche.anim));
			
			else if (fleche.type_fleche.equals(Fleche.SPIRITUELLE.FEU))
				im.add(this.im_feu_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.SPIRITUELLE.GRAPPIN))
				im.add(this.im_grappin_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.SPIRITUELLE.OMBRE))
				im.add(this.im_ombre_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.SPIRITUELLE.VENT))
				im.add(this.im_vent_aura.get(fleche.anim));
			
			else if (fleche.type_fleche.equals(Fleche.DESTRUCTRICE.BOGUE))
				im.add(this.im_bogue_aura.get(fleche.anim));
			else if (fleche.type_fleche.equals(Fleche.DESTRUCTRICE.CHARGEE))
				im.add(this.im_chargee_aura.get(fleche.anim));
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
}
