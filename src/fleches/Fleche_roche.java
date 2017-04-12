package fleches;

import java.util.List;

public class Fleche_roche extends Fleche {

	public Fleche_roche(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=MATERIELLE.ROCHE;
	}

}
