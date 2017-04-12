package fleches;

import java.util.List;

public class Fleche_chargee extends Fleche {

	public Fleche_chargee(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=DESTRUCTRICE.CHARGEE;
	}

}
