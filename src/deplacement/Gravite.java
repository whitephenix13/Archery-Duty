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
	public static double gravity_norm = 0.5; //1.0
	//g=10 m/s^2 = 0.3 wu / frame^2 with 1 wu = 1cm
	//vitesse max a 195km/h= 54,17 m/s = 90 wu/frame
	public float varVitesse = (float) (gravity_norm *Config.i_ratio_fps()*Config.i_ratio_fps());
	public float limVitesse = (float) (90.0 *Config.i_ratio_fps()*Config.i_ratio_fps()) ; // 7
	public void gravite(Collidable object, boolean slowDown) {
		float varVitesseGlissade = varVitesse;
		float limVitesseGlissade = limVitesse/2;
		if (object.deplacement.IsDeplacement(Mouvement_perso.glissade))
		{
			if(object.vit.y<(limVitesseGlissade - varVitesseGlissade))
				object.vit.y+= varVitesseGlissade;

			else 
				object.vit.y= limVitesseGlissade;
		}
		else 
		{
			if(object.vit.y<(limVitesse - varVitesse))
				object.vit.y+= varVitesse;

			else 
				object.vit.y= limVitesse;

		}
	}
}

