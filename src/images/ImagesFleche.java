package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;

public class ImagesFleche extends ImagesContainer{
	
	private static String path ="resources/projectile/fleches/";
	ArrayList<Image> im_fleche= new ArrayList<Image>();
	ArrayList<Image> im_faucon_aura= new ArrayList<Image>();
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
	ArrayList<Image> im_barrage_aura= new ArrayList<Image>();

	ArrayList<Image> im_marque_mortelle_aura= new ArrayList<Image>();
	ArrayList<Image> im_absorption_aura= new ArrayList<Image>();
	ArrayList<Image> im_ninja_aura= new ArrayList<Image>();
	ArrayList<Image> im_leurre_aura= new ArrayList<Image>();

	public ImagesFleche()
	{
		super("Image fleche");
	}

	@Override
	public void run()
	{
		percentage = 0;
		if(alreadyLoaded){
			percentage = 100;
			return;
		}
		float count=0;
		int num_tot = 68;
		for(int i=0; i<4; ++i){
			im_fleche.add(getIm(path+"/"+i+".png",true));
			
			count+=1;
			percentage = ((int)(100*count/num_tot));
			im_electrique_aura.add(getIm(path+"auras/electrique/"+i+".png",true));
			im_glace_aura.add(getIm(path+"auras/glace/"+i+".png",true));
			im_roche_aura.add(getIm(path+"auras/roche/"+i+".png",true));
			im_feu_aura.add(getIm(path+"auras/feu/"+i+".png",true));

			count+=4;
			percentage = ((int)(100*count/num_tot));

			im_lumiere_aura.add(getIm(path+"auras/lumiere/"+i+".png",true));
			im_ombre_aura.add(getIm(path+"auras/ombre/"+i+".png",true));
			im_vent_aura.add(getIm(path+"auras/vent/"+i+".png",true));
			im_grappin_aura.add(getIm(path+"auras/grappin/"+i+".png",true));

			count+=4;
			percentage = ((int)(100*count/num_tot));

			im_barrage_aura.add(getIm(path+"auras/barrage/"+i+".png",true));
			im_explosive_aura.add(getIm(path+"auras/explosive/"+i+".png",true));
			im_trou_noir_aura.add(getIm(path+"auras/trou_noir/"+i+".png",true));
			im_faucon_aura.add(getIm(path+"auras/faucon/"+i+".png",true));
			
			count+=4;
			percentage = ((int)(100*count/num_tot));

			im_marque_mortelle_aura.add(getIm(path+"auras/marque_mortelle/"+i+".png",true));
			im_absorption_aura.add(getIm(path+"auras/absorption/"+i+".png",true));
			im_ninja_aura.add(getIm(path+"auras/ninja/"+i+".png",true));
			im_leurre_aura.add(getIm(path+"auras/leurre/"+i+".png",true));
			
			count+=4;
			percentage = ((int)(100*count/num_tot));
		}
		percentage=100;
		alreadyLoaded=true;
	}
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		return null;
	}
	/***
	 * objType:{@link ObjectType#FLECHE} ... 
	 * info1 : null
	 * info2 : null
	 */
	@Override
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int mouv_index)
	{
		ArrayList<Image> im = new ArrayList<Image>();

		if (objType.equals(ObjectType.ELECTRIQUE))
			im.add(this.im_electrique_aura.get(mouv_index));
		if (objType.equals(ObjectType.FEU))
			im.add(this.im_feu_aura.get(mouv_index));
		if (objType.equals(ObjectType.GLACE))
			im.add(this.im_glace_aura.get(mouv_index));
		if (objType.equals(ObjectType.ROCHE))
			im.add(this.im_roche_aura.get(mouv_index));


		if (objType.equals(ObjectType.GRAPPIN))
			im.add(this.im_grappin_aura.get(mouv_index));
		if (objType.equals(ObjectType.OMBRE))
			im.add(this.im_ombre_aura.get(mouv_index));
		if (objType.equals(ObjectType.LUMIERE))
			im.add(this.im_lumiere_aura.get(mouv_index));
		if (objType.equals(ObjectType.VENT))
			im.add(this.im_vent_aura.get(mouv_index));

		if (objType.equals(ObjectType.BARRAGE))
			im.add(this.im_barrage_aura.get(mouv_index));
		if (objType.equals(ObjectType.FAUCON))
			im.add(this.im_faucon_aura.get(mouv_index));
		if (objType.equals(ObjectType.EXPLOSIVE))
			im.add(this.im_explosive_aura.get(mouv_index));
		if (objType.equals(ObjectType.TROU_NOIR))
			im.add(this.im_trou_noir_aura.get(mouv_index));

		if (objType.equals(ObjectType.MARQUE_MORTELLE))
			im.add(this.im_marque_mortelle_aura.get(mouv_index));
		if (objType.equals(ObjectType.LEURRE))
			im.add(this.im_leurre_aura.get(mouv_index));
		if (objType.equals(ObjectType.ABSORPTION))
			im.add(this.im_absorption_aura.get(mouv_index));
		if (objType.equals(ObjectType.NINJA))
			im.add(this.im_ninja_aura.get(mouv_index));

		im.add(im_fleche.get(mouv_index));

		return im;

	}

}
