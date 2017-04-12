package fleches;

import java.util.List;

import fleches.Fleche.RUSEE;

public class Fleche_cac extends Fleche{

	public Fleche_cac(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=RUSEE.CAC;

	}

}
