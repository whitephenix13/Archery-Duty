package fleches;

import java.util.List;

public class Fleche_trou_noir extends Fleche {

	public Fleche_trou_noir(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=DESTRUCTRICE.TROU_NOIR;
	}

}
