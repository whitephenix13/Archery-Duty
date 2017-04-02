package option;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import option.AffichageOption.CustomClickableLabel;

public abstract class AbstractControlerOption {

	protected AbstractModelOption opt;
	public AbstractControlerOption(AbstractModelOption _opt)
	{
		opt=_opt;
	}
	
	public abstract void controlRetourMenuPrincipal();
	public abstract void controlMouseInput(MouseEvent e);
	//public abstract void controlKeyboardInput(KeyEvent e);
	public abstract void controlCustomClickableLabel(CustomClickableLabel l);
}
