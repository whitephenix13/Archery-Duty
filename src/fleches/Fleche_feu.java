package fleches;

import java.util.List;

import personnage.Heros;

public class Fleche_feu extends Fleche {

	public Fleche_feu(List<Fleche> tabFleche, int current_frame,Heros _shooter,boolean add_to_list) {
		super(tabFleche, current_frame,_shooter,add_to_list);
		type_fleche=SPIRITUELLE.FEU;
	}

}
