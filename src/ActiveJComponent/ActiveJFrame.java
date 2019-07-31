package ActiveJComponent;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JFrame;

import Affichage.GameRenderer;
import gameConfig.InterfaceConstantes;

public class ActiveJFrame extends JFrame{
	private boolean activeVisible;
	public ActiveJFrame()
	{
		super.setVisible( true );
		activeVisible=true;
		setIgnoreRepaint(true);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setPreferredSize(new Dimension(InterfaceConstantes.screenSize.width,InterfaceConstantes.screenSize.height));
		setFocusable(true);
		requestFocus();
		setJMenuBar(null);
		setResizable(false);
		//setLocationRelativeTo(null);
		
	}
	@Override 
	public void paint(Graphics g)
	{
		if(!activeVisible)
			return;
		if(!GameRenderer.isRenderCalled())
			return;
		super.paint(g);
	}
	@Override 
	public void paintComponents(Graphics g)
	{
		if(!activeVisible)
			return;
		if(!GameRenderer.isRenderCalled())
			return;
		super.paintComponents(g);
	}
	@Override
	public void repaint()
	{
	}
	//Override repaint functions to avoid forced repaint (ie when using setBounds())
  	@Override
	public void repaint(long tm, int x, int y,int  w,int h)
	{
  		//ignore repaint
	}

	@Override
	public void setVisible(boolean b)
	{
		activeVisible = b;
	}
	
	//WARNING: do not override isVisible since it is used internally with swing (you actually always want your object to be visible according to swing)
	public boolean isActiveVisible()
	{
		return activeVisible;
	}
	

}
