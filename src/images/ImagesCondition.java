package images;

import java.awt.Image;

import conditions.Condition;
import loading.LoadMediaThread;

public class ImagesCondition extends LoadMediaThread{

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

	public ImagesCondition (){}

	@Override
	public void loadMedia() {
		if(mediaLoaded)
			return;

		int nb_condi = 10;
		loadMedia("",Condition.BRULURE);
		setPercentage(100/nb_condi);
		loadMedia("",Condition.LENTEUR);
		setPercentage(200/nb_condi);
		loadMedia("",Condition.VITESSE);
		setPercentage(300/nb_condi);
		loadMedia("",Condition.PARALYSIE);
		setPercentage(400/nb_condi);
		loadMedia("",Condition.DEFAILLANCE);
		setPercentage(500/nb_condi);
		loadMedia("",Condition.RESISTANCE);
		setPercentage(600/nb_condi);
		loadMedia("",Condition.FORCE);
		setPercentage(700/nb_condi);
		loadMedia("",Condition.FAIBLESSE);
		setPercentage(800/nb_condi);
		loadMedia("",Condition.REGENERATION);
		setPercentage(900/nb_condi);
		loadMedia("",Condition.PRECISION);
		setPercentage(100);
		mediaLoaded=true;


	}

	@Override
	public void loadMedia(String media_categorie, String filename) {
		if(filename.equals(Condition.BRULURE))
		{
			if(im_brulure==null)
				im_brulure= getIm("resources/conditions/brulure.png",true);		
		}
		else if(filename.equals(Condition.LENTEUR))
		{
			if(im_lenteur==null)
				im_lenteur= getIm("resources/conditions/lenteur.png",true);		
		}
		else if(filename.equals(Condition.VITESSE))
		{
			if(im_vitesse==null)
				im_vitesse= getIm("resources/conditions/vitesse.png",true);		
		}
		else if(filename.equals(Condition.PARALYSIE))
		{
			if(im_paralysie==null)
				im_paralysie= getIm("resources/conditions/paralysie.png",true);		
		}
		else if(filename.equals(Condition.DEFAILLANCE))
		{
			if(im_defaillance==null)
				im_defaillance= getIm("resources/conditions/defaillance.png",true);		
		}
		else if(filename.equals(Condition.FORCE))
		{
			if(im_force==null)
				im_force= getIm("resources/conditions/force.png",true);		
		}
		else if(filename.equals(Condition.RESISTANCE))
		{
			if(im_resistance==null)
				im_resistance= getIm("resources/conditions/resistance.png",true);		
		}
		else if(filename.equals(Condition.FAIBLESSE))
		{
			if(im_faiblesse==null)
				im_faiblesse= getIm("resources/conditions/faiblesse.png",true);		
		}	
		else if(filename.equals(Condition.REGENERATION))
		{
			if(im_regeneration==null)
				im_regeneration= getIm("resources/conditions/regeneration.png",true);		
		}	
		else if(filename.equals(Condition.PRECISION))
		{
			if(im_precision==null)
				im_precision= getIm("resources/conditions/precision.png",true);		
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
