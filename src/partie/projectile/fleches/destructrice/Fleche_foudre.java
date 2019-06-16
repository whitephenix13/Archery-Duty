package partie.projectile.fleches.destructrice;

import java.util.List;

import partie.entitie.heros.Heros;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;

public class Fleche_foudre extends Destructrice {
	
	// WARNING : effect moves with 
	//	-colliding entity        			??? 
	//  -colliding ground (ie roche_effect) ???
	
	public Fleche_foudre(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);

	}


}
