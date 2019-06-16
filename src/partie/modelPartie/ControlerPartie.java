package partie.modelPartie;

import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import option.Touches;

public class ControlerPartie extends AbstractControlerPartie{

	public ControlerPartie(AbstractModelPartie _partie)
	{
		partie=_partie;
	}


	public void controlMousePressed(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e))
			partie.inputPartie.applyMouseInput(partie.touches.LEFT_MOUSE);
		if(SwingUtilities.isRightMouseButton(e))
			partie.inputPartie.applyMouseInput( partie.touches.RIGHT_MOUSE);
		if(SwingUtilities.isMiddleMouseButton(e))
			partie.inputPartie.applyMouseInput( partie.touches.MIDDLE_MOUSE);
	}
	public void controlMouseReleased(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e))
			partie.inputPartie.applyMouseInput(partie.inputPartie.buildReleaseKeyStroke(partie.touches.LEFT_MOUSE));
		if(SwingUtilities.isRightMouseButton(e))
			partie.inputPartie.applyMouseInput( partie.inputPartie.buildReleaseKeyStroke(partie.touches.RIGHT_MOUSE));
		if(SwingUtilities.isMiddleMouseButton(e))
			partie.inputPartie.applyMouseInput( partie.inputPartie.buildReleaseKeyStroke(partie.touches.MIDDLE_MOUSE));
	}

	public void controlBoutonsPressed(JButton button) {
		partie.HandleBoutonsPressed(button);
	}
}
