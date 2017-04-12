package fleches;

import java.util.List;

import fleches.Fleche.RUSEE;

public class Fleche_bogue  extends Fleche{

	public Fleche_bogue(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=DESTRUCTRICE.BOGUE;
	}

}
