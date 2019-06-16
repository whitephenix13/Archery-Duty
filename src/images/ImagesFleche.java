package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.TypeObject;
import loading.LoaderItem;
import partie.projectile.fleches.Fleche;

public class ImagesFleche extends LoaderItem{
	
	private static String path ="resources/projectile/fleches/";
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
	public void run()
	{
		if(alreadyLoaded)
			return;

		for(int i=0; i<4; ++i){
			im_fleche.add(getIm(path+"/"+i+".png",true));

			percentage = ((int)((1.0 + i*17)/0.68));
			im_electrique_aura.add(getIm(path+"auras/electrique/"+i+".png",true));
			im_glace_aura.add(getIm(path+"auras/glace/"+i+".png",true));
			im_roche_aura.add(getIm(path+"auras/roche/"+i+".png",true));
			im_feu_aura.add(getIm(path+"auras/feu/"+i+".png",true));

			percentage = ((int)((5.0 + i*17)/0.68));

			im_lumiere_aura.add(getIm(path+"auras/lumiere/"+i+".png",true));
			im_ombre_aura.add(getIm(path+"auras/ombre/"+i+".png",true));
			im_vent_aura.add(getIm(path+"auras/vent/"+i+".png",true));
			im_grappin_aura.add(getIm(path+"auras/grappin/"+i+".png",true));

			percentage = ((int)((9.0 + i*17)/0.68));

			im_bogue_aura.add(getIm(path+"auras/bogue/"+i+".png",true));
			im_explosive_aura.add(getIm(path+"auras/explosive/"+i+".png",true));
			im_trou_noir_aura.add(getIm(path+"auras/trou_noir/"+i+".png",true));
			im_foudre_aura.add(getIm(path+"auras/foudre/"+i+".png",true));

			percentage = ((int)((13.0 + i*17)/0.68));

			im_auto_teleguidee_aura.add(getIm(path+"auras/auto_teleguidee/"+i+".png",true));
			im_retard_aura.add(getIm(path+"auras/retard/"+i+".png",true));
			im_v_fleche_aura.add(getIm(path+"auras/v_fleche/"+i+".png",true));
			im_cac_aura.add(getIm(path+"auras/corps_a_corps/"+i+".png",true));
			percentage = ((int)((17.0 + i*17)/0.68));
		}
		percentage=100;
		alreadyLoaded=true;
	}

	public ArrayList<Image> getImage(Fleche fleche)
	{
		ArrayList<Image> im = new ArrayList<Image>();

		if (TypeObject.isTypeOf(fleche, TypeObject.ELECTRIQUE))
			im.add(this.im_electrique_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.FEU))
			im.add(this.im_feu_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.GLACE))
			im.add(this.im_glace_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.ROCHE))
			im.add(this.im_roche_aura.get(fleche.getAnim()));


		if (TypeObject.isTypeOf(fleche, TypeObject.GRAPPIN))
			im.add(this.im_grappin_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.OMBRE))
			im.add(this.im_ombre_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.LUMIERE))
			im.add(this.im_lumiere_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.VENT))
			im.add(this.im_vent_aura.get(fleche.getAnim()));

		if (TypeObject.isTypeOf(fleche, TypeObject.BOGUE))
			im.add(this.im_bogue_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.FOUDRE))
			im.add(this.im_foudre_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.EXPLOSIVE))
			im.add(this.im_explosive_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.TROU_NOIR))
			im.add(this.im_trou_noir_aura.get(fleche.getAnim()));

		if (TypeObject.isTypeOf(fleche, TypeObject.AUTO_TELEGUIDEE))
			im.add(this.im_auto_teleguidee_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.CAC))
			im.add(this.im_cac_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.RETARD))
			im.add(this.im_retard_aura.get(fleche.getAnim()));
		if (TypeObject.isTypeOf(fleche, TypeObject.V_FLECHE))
			im.add(this.im_v_fleche_aura.get(fleche.getAnim()));

		im.add(im_fleche.get(fleche.getAnim()));

		return im;

	}

}
