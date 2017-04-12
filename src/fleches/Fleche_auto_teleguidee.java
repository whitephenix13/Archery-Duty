package fleches;

import java.util.List;

public class Fleche_auto_teleguidee extends Fleche{

	public Fleche_auto_teleguidee(List<Fleche> tabFleche, int current_frame) {
		super(tabFleche, current_frame);
		type_fleche=RUSEE.AUTO_TELEGUIDEE;
	}

}
