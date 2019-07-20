package partie.projectile.fleches.rusee;

import java.util.List;

import partie.entitie.heros.Heros;
import partie.projectile.Projectile;

public class Fleche_leurre extends Rusee{
	// WARNING : effect moves with 
	//	-colliding entity        			??? 
	//  -colliding ground (ie roche_effect) ???
	
	public Fleche_leurre(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
	}
	

}
