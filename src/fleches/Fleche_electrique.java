package fleches;

import java.util.List;

import personnage.Heros;

public class Fleche_electrique extends Fleche {

	public Fleche_electrique(List<Fleche> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		type_fleche=MATERIELLE.ELECTRIQUE;
	}

}
