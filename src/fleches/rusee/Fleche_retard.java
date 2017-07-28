package fleches.rusee;

import java.util.List;

import personnage.Heros;
import types.Projectile;

public class Fleche_retard extends Rusee{

	public Fleche_retard(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
	}

}
