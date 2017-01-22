package deplacement;

import collision.Collidable;
import monstre.Monstre;
import monstre.TirMonstre;
import personnage.Fleche;
import principal.InterfaceConstantes;

public class Gravite implements InterfaceConstantes
{
	public static void gravite(Collidable object, boolean slowDown) {
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		int coefCorrecteurVar =1000 ; //1000
		int coefCorrecteurLim =15000 ;//15000
		float varVitesse = (float) (coefCorrecteurVar);
		float limVitesse = (float) (coefCorrecteurLim) ;
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
	public static void gravite(Fleche fleche, boolean slowDown) 
	{
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		int coefCorrecteurVar =1000 ;
		int coefCorrecteurLim =15000 ;
		float varVitesse = (float) (coefCorrecteurVar);
		float limVitesse = (float) (coefCorrecteurLim) ;
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

	public static void gravite(TirMonstre tir) 
	{
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		int coefCorrecteurVar =1000 ;
		int coefCorrecteurLim =15000 ;
		float varVitesse = (float) (coefCorrecteurVar);
		float limVitesse = (float) (coefCorrecteurLim) ;
		
		if(tir.vit.y<(limVitesse - varVitesse))
		{
			tir.vit.y+= varVitesse;
		}
		else 
		{
			tir.vit.y= (int) limVitesse;
		}

	}

	public static void gravite(Monstre monstre) 
	{
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		int coefCorrecteurVar =1000 ;
		int coefCorrecteurLim =15000 ;
		float varVitesse = (float) (coefCorrecteurVar);
		float limVitesse = (float) (coefCorrecteurLim) ;
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

