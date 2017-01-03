package partie;

import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import types.Touches;

public class ControlerPartie extends AbstractControlerPartie{

	public ControlerPartie(AbstractModelPartie _partie)
	{
		partie=_partie;
	}

	public void controlPressedInput(int input) {
		partie.HandlePressedInput(input);
	}
	public void controlReleasedInput(int input) {
		partie.HandleReleasedInput(input);
	}

	public void controlMousePressed(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e))
			partie.HandlePressedInput(Touches.LEFT_MOUSE);
		if(SwingUtilities.isRightMouseButton(e))
			partie.HandlePressedInput(Touches.RIGHT_MOUSE);
		if(SwingUtilities.isMiddleMouseButton(e))
			partie.HandlePressedInput(Touches.MIDDLE_MOUSE);
	}
	public void controlMouseReleased(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e))
			partie.HandleReleasedInput(Touches.LEFT_MOUSE);
		if(SwingUtilities.isRightMouseButton(e))
			partie.HandleReleasedInput(Touches.RIGHT_MOUSE);
		if(SwingUtilities.isMiddleMouseButton(e))
			partie.HandleReleasedInput(Touches.MIDDLE_MOUSE);
	}

	public void controlBoutonsPressed(JButton button) {
		partie.HandleBoutonsPressed(button);
	}
}
