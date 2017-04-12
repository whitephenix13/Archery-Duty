package fleches;

import java.util.List;

public class Fleche_foudre extends Fleche {

	public Fleche_foudre(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=MATERIELLE.FOUDRE;
	}

}
