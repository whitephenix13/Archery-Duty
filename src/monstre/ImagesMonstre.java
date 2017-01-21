package monstre;

import java.awt.Image;
import java.awt.Toolkit;

import deplacement.Mouvement_perso;

public class ImagesMonstre {

	Image SPattente0; 
	Image SPattente1;
	
	Image SPmarche0;
	Image SPmarche1;
	Image SPmarche2;
	Image SPmarche3; 
	
	Image SPsaut0;
	Image SPsaut1;
	
	public ImagesMonstre()
	{
		chargerImages();
	}
	
	/**
	 * Renvoie la bonne image pour un monstre donné
	 * 
	 * @param monstre, le monstre a afficher
	 * 
	 * @return l'image a afficher
	 */
	public Image getImage(Monstre monstre)
	{
		if (monstre.nom.equals("spirel"))
		{
			if(monstre.deplacement.IsDeplacement(Mouvement_perso.attente))
			{
				switch(monstre.anim)
				{
				case 0: return(SPattente0);
				case 1: return(SPattente1);
				default: return(null);
				}
			}
			else if (monstre.deplacement.IsDeplacement(Mouvement_perso.marche))
			{
				switch(monstre.anim)
				{
				case 0: return(SPmarche0);
				case 1: return(SPmarche1);
				case 2: return(SPmarche2);
				case 3: return(SPmarche3);
				default: return(null);
				}
			}
			else if(monstre.deplacement.IsDeplacement(Mouvement_perso.saut))
			{
				switch(monstre.anim)
				{
				case 0: return(SPsaut0);
				case 1: return(SPsaut1);
				default: return(null);
				}
			}
			else
			{
				return(null);
			}
		}
		else
		{
			return(null);
		}
	}

	/**
	 * Charge les images en début de jeu pour optimiser le temps de calcul 
	 */
	public void chargerImages()
	{
		 SPattente0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/spirel/deplacement.Attente/0.gif"));
		 SPattente1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/spirel/deplacement.Attente/1.gif"));
		
		 SPmarche0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/spirel/deplacement.Marche/0.gif"));
		 SPmarche1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/spirel/deplacement.Marche/1.gif"));
		 SPmarche2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/spirel/deplacement.Marche/2.gif"));
		 SPmarche3 = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/spirel/deplacement.Marche/3.gif"));
	
		 SPsaut0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/spirel/deplacement.Attente/0.gif"));
		 SPsaut1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/spirel/deplacement.Attente/1.gif"));
		
	}
}
