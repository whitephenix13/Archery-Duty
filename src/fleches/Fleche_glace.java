package fleches;

import java.util.List;

public class Fleche_glace extends Fleche {

	public Fleche_glace(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=MATERIELLE.GLACE;
	}

}
