package fleches;

import java.util.List;

import personnage.Heros;

public class Fleche_retard extends Fleche{

	public Fleche_retard(List<Fleche> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		type_fleche=RUSEE.RETARD;
	}

}
