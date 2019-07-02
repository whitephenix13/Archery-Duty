package images;

import java.awt.Image;

import loading.LoaderItem;
import partie.conditions.Condition;

public class ImagesCondition extends LoaderItem{

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
		run("",Condition.BRULURE);
		percentage = (int)(1*100.0/nb_condi);
		run("",Condition.LENTEUR);
		percentage = (int)(2*100.0/nb_condi);
		run("",Condition.VITESSE);
		percentage = (int)(3*100.0/nb_condi);
		run("",Condition.PARALYSIE);
		percentage = (int)(4*100.0/nb_condi);
		run("",Condition.DEFAILLANCE);
		percentage = (int)(5*100.0/nb_condi);
		run("",Condition.RESISTANCE);
		percentage = (int)(6*100.0/nb_condi);
		run("",Condition.FORCE);
		percentage = (int)(7*100.0/nb_condi);
		run("",Condition.FAIBLESSE);
		percentage = (int)(8*100.0/nb_condi);
		run("",Condition.REGENERATION);
		percentage = (int)(9*100.0/nb_condi);
		run("",Condition.PRECISION);
		percentage=100;
		alreadyLoaded=true;


	}

	public void run(String media_categorie, String filename) {
		if(filename.equals(Condition.BRULURE))
		{
			if(im_brulure==null)
				im_brulure= getIm(path+"brulure.png",true);		
		}
		else if(filename.equals(Condition.LENTEUR))
		{
			if(im_lenteur==null)
				im_lenteur= getIm(path+"lenteur.png",true);		
		}
		else if(filename.equals(Condition.VITESSE))
		{
			if(im_vitesse==null)
				im_vitesse= getIm(path+"vitesse.png",true);		
		}
		else if(filename.equals(Condition.PARALYSIE))
		{
			if(im_paralysie==null)
				im_paralysie= getIm(path+"paralysie.png",true);		
		}
		else if(filename.equals(Condition.DEFAILLANCE))
		{
			if(im_defaillance==null)
				im_defaillance= getIm(path+"defaillance.png",true);		
		}
		else if(filename.equals(Condition.FORCE))
		{
			if(im_force==null)
				im_force= getIm(path+"force.png",true);		
		}
		else if(filename.equals(Condition.RESISTANCE))
		{
			if(im_resistance==null)
				im_resistance= getIm(path+"resistance.png",true);		
		}
		else if(filename.equals(Condition.FAIBLESSE))
		{
			if(im_faiblesse==null)
				im_faiblesse= getIm(path+"faiblesse.png",true);		
		}	
		else if(filename.equals(Condition.REGENERATION))
		{
			if(im_regeneration==null)
				im_regeneration= getIm(path+"regeneration.png",true);		
		}	
		else if(filename.equals(Condition.PRECISION))
		{
			if(im_precision==null)
				im_precision= getIm(path+"precision.png",true);		
		}	
		else{
			try {
				throw(new Exception("Condition not known: "+ filename));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public Image getImage(String name)
	{
		if(name.equals(Condition.BRULURE))
			return im_brulure;

		else if(name.equals(Condition.LENTEUR))
			return im_lenteur;	

		else if(name.equals(Condition.VITESSE))
			return im_vitesse;

		else if(name.equals(Condition.PARALYSIE))
			return im_paralysie;		

		else if(name.equals(Condition.DEFAILLANCE))
			return im_defaillance;		

		else if(name.equals(Condition.FORCE))
			return im_force;		

		else if(name.equals(Condition.RESISTANCE))
			return im_resistance;		

		else if(name.equals(Condition.FAIBLESSE))
			return im_faiblesse;	
		
		else if(name.equals(Condition.REGENERATION))
			return im_regeneration;	
		
		else if(name.equals(Condition.PRECISION))
			return im_precision;	
		else{
			try {
				throw(new Exception("Condition not known: "+ name));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
