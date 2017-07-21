package deplacement;

import collision.Collidable;
import fleches.Fleche;
import monstre.Monstre;
import monstre.TirMonstre;
import option.Config;
import principal.InterfaceConstantes;

public class Gravite implements InterfaceConstantes
{
	//since gravity takes into account previous speed, the actual division factor is sum_1^{ratio_fps} 1 
	public static double gravity_norm = 0.5; //1.0
	//g=10 m/s^2 = 0.3 wu / frame^2 with 1 wu = 1cm
	//vitesse max a 195km/h= 54,17 m/s = 90 wu/frame
	public float varVitesse = (float) (gravity_norm *Config.i_ratio_fps()*Config.i_ratio_fps());
	public float limVitesse = (float) (10.0 *Config.i_ratio_fps()*Config.i_ratio_fps()) ; // 7
	public void gravite(Collidable object) {
		float varVitesseGlissade = varVitesse;
		float limVitesseGlissade = limVitesse/2;
		if (object.deplacement.IsDeplacement(Mouvement_perso.glissade))
		{
			if(object.localVit.y<(limVitesseGlissade - varVitesseGlissade))
				object.localVit.y += varVitesseGlissade;

			else 
				object.localVit.y=limVitesseGlissade;
		}
		else 
		{
			if(object.localVit.y<(limVitesse - varVitesse))
				object.localVit.y += varVitesse;
			else 
				object.localVit.y=limVitesse;

		}
	}
}

