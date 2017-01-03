package deplacement;

import monstre.Monstre;
import personnage.Fleche;
import personnage.Heros;
import principal.InterfaceConstantes;

public class Gravite implements InterfaceConstantes
{
	public static void gravite(Heros heros, boolean slowDown) {
		//vitesse max a 195km/h= 54,17 m/s
		//variation de vitesse : Vnouv= - g * t  
		// ici g= 10 et t= 17 * 10^-3 s 
		int coefCorrecteurVar =1000 ; //1000
		int coefCorrecteurLim =15000 ;//15000
		float varVitesse = (float) (coefCorrecteurVar);
		float limVitesse = (float) (coefCorrecteurLim) ;
		float varVitesseGlissade = varVitesse;
		float limVitesseGlissade = limVitesse/2;


		if (heros.deplacement.getClass().getName()==("deplacement.Glissade"))
		{
			if(heros.vit.y<(limVitesseGlissade - varVitesseGlissade))
				heros.vit.y+= varVitesseGlissade;
			
			else 
				heros.vit.y= (int) limVitesseGlissade;
		}
		else 
		{
			if(heros.vit.y<(limVitesse - varVitesse))
				heros.vit.y+= varVitesse;
			
			else 
				heros.vit.y= (int) limVitesse;
			
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

