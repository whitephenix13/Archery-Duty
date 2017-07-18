package fleches;

import java.util.List;

import personnage.Heros;

public class Fleche_cac extends Fleche{

	public Fleche_cac(List<Fleche> tabFleche, int current_frame,Heros _shooter,boolean add_to_list) {
		super(tabFleche, current_frame,_shooter,add_to_list);
		type_fleche=RUSEE.CAC;

	}

}
