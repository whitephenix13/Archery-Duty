package monstre;

import java.awt.Image;
import java.awt.Toolkit;

public class ImagesTirMonstre {

	
	Image tir0;
	Image tir4;
	Image tir6;
	
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
		case 4: return(tir4);
		case 6: return(tir6);
		default: return(null);
		}

	}

	/**
	 * Charge les images en début de jeu pour optimiser le temps de calcul 
	 */
	public void chargerImages()
	{
		 tir0= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/TirMonstre/monstre.TirSpirel/0.gif"));
		 tir4= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/TirMonstre/monstre.TirSpirel/4.gif"));
		 tir6= Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/TirMonstre/monstre.TirSpirel/6.gif"));

	}
}
