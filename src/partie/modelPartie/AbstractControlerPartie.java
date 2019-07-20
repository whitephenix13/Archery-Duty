package partie.modelPartie;

import java.awt.event.MouseEvent;

import ActiveJComponent.ActiveJButton;

public abstract class AbstractControlerPartie {
	
	protected AbstractModelPartie partie;
	
	//public abstract void controlPressedInput(int input);
	//public abstract void controlReleasedInput(int input);
	public abstract void controlMousePressed(MouseEvent e);
	public abstract void controlMouseReleased(MouseEvent e);
	
	public abstract void controlBoutonsPressed(ActiveJButton button);

}
