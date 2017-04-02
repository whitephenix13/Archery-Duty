package option;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import option.AffichageOption.CustomClickableLabel;
import types.Touches;

public class ControlerOption extends AbstractControlerOption{

	public ControlerOption(AbstractModelOption _opt) {
		super(_opt);
	}

	public void controlRetourMenuPrincipal() {
		//if(VariablesPartieRapide.inPause)
		//	opt.retourPartie();
	//	else
			opt.retourMenuPrincipal();
	}

	public void controlMouseInput(MouseEvent e) 
	{
		if(opt.caseFocus)
		{
		String input=opt.touches.ERROR;
		
		if(SwingUtilities.isLeftMouseButton(e))
			input = opt.touches.LEFT_MOUSE;
		
		if(SwingUtilities.isRightMouseButton(e))
			input = opt.touches.RIGHT_MOUSE;
		
		if(SwingUtilities.isMiddleMouseButton(e))
			input = opt.touches.MIDDLE_MOUSE;
		
		if(input !=opt.touches.ERROR)
			opt.setModifTouches(input,opt.inputPartie);
		else
			opt.setShowInputError(true);
		}
	}

	/*public void controlKeyboardInput(KeyEvent e) 
	{
		if(opt.caseFocus)
		{
			String input = opt.touches.ERROR;
			if( ((e.getKeyCode() >= KeyEvent.VK_A) && (e.getKeyCode() <= KeyEvent.VK_Z)) || ((e.getKeyCode() >= KeyEvent.VK_0) && (e.getKeyCode() <= KeyEvent.VK_9)) )
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_SPACE)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_LEFT)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_RIGHT)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_UP)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_DOWN)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_SHIFT)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_CONTROL)
				input=e.getKeyCode();

			if(e.getKeyCode() == KeyEvent.VK_F1)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F2)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F3)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F4)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F5)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F6)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F7)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F8)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F9)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F10)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F11)
				input=e.getKeyCode();
			
			if(e.getKeyCode() == KeyEvent.VK_F12)
				input=e.getKeyCode();
			
			if(input !=opt.touches.ERROR)
				opt.setModifTouches(input);
			else
				opt.setShowInputError(true);
			}
		
		}*/

	public void controlCustomClickableLabel(CustomClickableLabel t) {
		opt.setMemCustomClickableLabel(t);

	}
		

}
