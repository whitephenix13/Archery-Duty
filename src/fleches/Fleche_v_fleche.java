package fleches;

import java.util.List;

public class Fleche_v_fleche extends Fleche{

	public Fleche_v_fleche(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=RUSEE.V_FLECHE;
	}

}
