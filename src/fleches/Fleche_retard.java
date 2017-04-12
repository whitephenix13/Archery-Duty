package fleches;

import java.util.List;

public class Fleche_retard extends Fleche{

	public Fleche_retard(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=RUSEE.RETARD;
	}

}
