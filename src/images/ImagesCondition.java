package images;

import java.awt.Image;
import java.util.ArrayList;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.conditions.Condition.ConditionEnum;

public class ImagesCondition extends ImagesContainer{

	private static String path ="resources/conditions/";
	Image im_brulure =null;
	Image im_regeneration =null;
	Image im_lenteur =null;
	Image im_vitesse =null;
	Image im_paralysie =null;
	Image im_precision =null;
	Image im_defaillance =null;
	Image im_resistance =null;
	Image im_force =null;
	Image im_faiblesse =null;

	public ImagesCondition (){super("Image conditions");}

	@Override
	public void run() {
		percentage = 0;
		if(alreadyLoaded){
			percentage = 100;
			return;
		}

		int nb_condi = 10;
		run("",ConditionEnum.BRULURE);
		percentage = (int)(1*100.0/nb_condi);
		run("",ConditionEnum.LENTEUR);
		percentage = (int)(2*100.0/nb_condi);
		run("",ConditionEnum.VITESSE);
		percentage = (int)(3*100.0/nb_condi);
		run("",ConditionEnum.PARALYSIE);
		percentage = (int)(4*100.0/nb_condi);
		run("",ConditionEnum.DEFAILLANCE);
		percentage = (int)(5*100.0/nb_condi);
		run("",ConditionEnum.RESISTANCE);
		percentage = (int)(6*100.0/nb_condi);
		run("",ConditionEnum.FORCE);
		percentage = (int)(7*100.0/nb_condi);
		run("",ConditionEnum.FAIBLESSE);
		percentage = (int)(8*100.0/nb_condi);
		run("",ConditionEnum.REGENERATION);
		percentage = (int)(9*100.0/nb_condi);
		run("",ConditionEnum.PRECISION);
		percentage=100;
		alreadyLoaded=true;


	}

	public void run(String media_categorie, ConditionEnum type) {
		if(type.equals(ConditionEnum.BRULURE))
		{
			if(im_brulure==null)
				im_brulure= getIm(path+"brulure.png",true);		
		}
		else if(type.equals(ConditionEnum.LENTEUR))
		{
			if(im_lenteur==null)
				im_lenteur= getIm(path+"lenteur.png",true);		
		}
		else if(type.equals(ConditionEnum.VITESSE))
		{
			if(im_vitesse==null)
				im_vitesse= getIm(path+"vitesse.png",true);		
		}
		else if(type.equals(ConditionEnum.PARALYSIE))
		{
			if(im_paralysie==null)
				im_paralysie= getIm(path+"paralysie.png",true);		
		}
		else if(type.equals(ConditionEnum.DEFAILLANCE))
		{
			if(im_defaillance==null)
				im_defaillance= getIm(path+"defaillance.png",true);		
		}
		else if(type.equals(ConditionEnum.FORCE))
		{
			if(im_force==null)
				im_force= getIm(path+"force.png",true);		
		}
		else if(type.equals(ConditionEnum.RESISTANCE))
		{
			if(im_resistance==null)
				im_resistance= getIm(path+"resistance.png",true);		
		}
		else if(type.equals(ConditionEnum.FAIBLESSE))
		{
			if(im_faiblesse==null)
				im_faiblesse= getIm(path+"faiblesse.png",true);		
		}	
		else if(type.equals(ConditionEnum.REGENERATION))
		{
			if(im_regeneration==null)
				im_regeneration= getIm(path+"regeneration.png",true);		
		}	
		else if(type.equals(ConditionEnum.PRECISION))
		{
			if(im_precision==null)
				im_precision= getIm(path+"precision.png",true);		
		}	
		else{
			try {
				throw(new Exception("Condition not known: "+ type));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * objType: null
	 * info1 : {@link ConditionEnum}
	 * info2 : null
	 */
	@Override
	public Image getImage(ObjectType objType, ImageInfo info1,ImageInfo info2)
	{
		if(info1.equals(ConditionEnum.BRULURE))
			return im_brulure;

		else if(info1.equals(ConditionEnum.LENTEUR))
			return im_lenteur;	

		else if(info1.equals(ConditionEnum.VITESSE))
			return im_vitesse;

		else if(info1.equals(ConditionEnum.PARALYSIE))
			return im_paralysie;		

		else if(info1.equals(ConditionEnum.DEFAILLANCE))
			return im_defaillance;		

		else if(info1.equals(ConditionEnum.FORCE))
			return im_force;		

		else if(info1.equals(ConditionEnum.RESISTANCE))
			return im_resistance;		

		else if(info1.equals(ConditionEnum.FAIBLESSE))
			return im_faiblesse;	
		
		else if(info1.equals(ConditionEnum.REGENERATION))
			return im_regeneration;	
		
		else if(info1.equals(ConditionEnum.PRECISION))
			return im_precision;	
		else{
			try {
				throw(new Exception("Condition not known: "+ info1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	@Override 
	public ArrayList<Image> getImages(ObjectType objType, ImageInfo info1,ImageInfo info2, int mouv_index)
	{
		return null;
	}
}
