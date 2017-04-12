package fleches;

import java.util.List;

public class Fleche_electrique extends Fleche {

	public Fleche_electrique(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=MATERIELLE.ELECTRIQUE;
	}

}
