package fleches;

import java.util.List;

public class Fleche_feu extends Fleche {

	public Fleche_feu(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=SPIRITUELLE.FEU;
	}

}
