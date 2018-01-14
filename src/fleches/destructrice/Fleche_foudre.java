package fleches.destructrice;

import java.util.List;

import fleches.Fleche;
import personnage.Heros;
import types.Projectile;

public class Fleche_foudre extends Destructrice {

	public Fleche_foudre(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);

	}


}
