package ActiveJComponent;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JMenuItem;

import Affichage.GameRenderer;

public class ActiveJMenuItem extends JMenuItem{
	private boolean activeVisible;
	public ActiveJMenuItem(String name)
	{
		super(name);
		activeVisible=true;
		setIgnoreRepaint(true);
	}
	@Override
	public void repaint()
	{
		//USEFULL: http://fuseyism.com/classpath/doc/javax/swing/ (from this code, we know that repaint is called when mouse rollover button)
		//ignore repaint
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
	
	//Override repaint functions to avoid forced repaint (ie when using setBounds())
  	@Override
	public void repaint(long tm, int x, int y,int  w,int h)
	{
  		//ignore repaint
	}
	@Override
	public void repaint(Rectangle r)
	{
		//ignore repaint
	}
	@Override
	public void setVisible(boolean b)
	{
		activeVisible = b;
	}
	
	@Override
	public boolean isVisible()
	{
		return activeVisible;
	}
	//WARNING: do not override isVisible since it is used internally with swing (you actually alaways want your object to be visible according to swing)
	public boolean isActiveVisible()
	{
		return activeVisible;
	}
}
