package ActiveJComponent;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

public class PassiveJDialog extends JDialog{
	//WARNING: Active rendering is not activated for those components as they are not part of the main frame (they are separated windows)
	//WARNING2: Use swing components instead of active one since this window is not actively rendered (no buffer for it)
	
	//Call dispose() to close this dialog 
	public PassiveJDialog(ActiveJFrame frame, String title, boolean modal)
	{
		super(frame,title,modal);
		this.setLocationRelativeTo(frame);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() 
		{
			  public void windowClosed(WindowEvent e)
			  {
			    PassiveJDialog.this.dispose();
			  }

			  public void windowClosing(WindowEvent e)
			  {
			  }
			});
		
	}
	public void setComponentAndShowDialog(Component content)
	{
		this.getContentPane().add(content);//need to set this before calling set visible or it won't draw components
		this.pack();
	    super.setVisible(true);
	}
}
