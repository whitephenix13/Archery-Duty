package deplacement;

import collision.Collidable;
import monstre.Monstre;
import monstre.TirMonstre;
import option.Config;
import personnage.Fleche;
import principal.InterfaceConstantes;

public class Gravite implements InterfaceConstantes
{
	//since gravity takes into account previous speed, the actual division factor is sum_1^{ratio_fps} 1 
	public static double gravity_norm = 1.0;
	public int varVitesse = (int)(gravity_norm *Config.i_ratio_fps()*Config.i_ratio_fps());
	public int limVitesse = (int)(7.0 *Config.i_ratio_fps()*Config.i_ratio_fps()) ;
	public void gravite(Collidable object, boolean slowDown) {
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		float varVitesseGlissade = varVitesse;
		float limVitesseGlissade = limVitesse/2;
		if (object.deplacement.IsDeplacement(Mouvement_perso.glissade))
		{
			if(object.vit.y<(limVitesseGlissade - varVitesseGlissade))
				object.vit.y+= varVitesseGlissade;

			else 
				object.vit.y= (int) limVitesseGlissade;
		}
		else 
		{
			if(object.vit.y<(limVitesse - varVitesse))
				object.vit.y+= varVitesse;

			else 
				object.vit.y= (int) limVitesse;

		}
	}
	public void gravite(Fleche fleche, boolean slowDown) 
	{
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		if(!slowDown)
		{
			if(fleche.vit.y<(limVitesse - varVitesse))
			{
				fleche.vit.y+= varVitesse;
			}
			else 
			{
				fleche.vit.y= (int) limVitesse;
			}

		}
	}

	public void gravite(TirMonstre tir) 
	{
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		
		if(tir.vit.y<(limVitesse - varVitesse))
		{
			tir.vit.y+= varVitesse;
		}
		else 
		{
			tir.vit.y= (int) limVitesse;
		}

	}

	public void gravite(Monstre monstre) 
	{
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		if(monstre.vit.y<(limVitesse - varVitesse))
		{
			monstre.vit.y+= varVitesse;
		}
		else 
		{
			monstre.vit.y= (int) limVitesse;
		}
	}

}

