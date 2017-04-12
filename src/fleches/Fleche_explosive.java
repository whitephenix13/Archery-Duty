package fleches;

import java.util.List;

public class Fleche_explosive extends Fleche {

	public Fleche_explosive(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=DESTRUCTRICE.EXPLOSIVE;
	}

}
