package monstre;

import java.awt.Image;
import java.awt.Toolkit;

public class ImagesTirMonstre {

	Image tir0;
	Image tir1;
	Image tir2;
	
	public ImagesTirMonstre()
	{
		chargerImages();
	}
	/**
	 * Renvoie la bonne image pour un tir de monstre donné
	 * 
	 * @param tir, le tir de monstre a afficher
	 * 
	 * @return l'image a afficher
	 */
	public Image getImage(TirMonstre tir)
	{

		switch(tir.anim)
		{
		case 0: return(tir0);
		case 1: return(tir1);
		case 2: return(tir2);
		default: return(null);
		}

	}

	/**
	 * Charge les images en début de jeu pour optimiser le temps de calcul 
	 */
	public void chargerImages()
	{
		 tir0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/TirMonstre/monstre.TirSpirel/0.gif"));
		 tir1= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/TirMonstre/monstre.TirSpirel/1.gif"));
		 tir2= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/TirMonstre/monstre.TirSpirel/2.gif"));

	}
}
