package ActiveJComponent;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JLayeredPane;

import Affichage.GameRenderer;

public class ActiveJLayeredPane extends JLayeredPane{
	private boolean activeVisible;
	//Usefull http://fuseyism.com/classpath/doc/javax/swing/JLayeredPane-source.html
	public ActiveJLayeredPane()
	{
		super.setVisible(true);
		activeVisible=true;
		setOpaque(false);
		this.setIgnoreRepaint(true);
		this.setDoubleBuffered(false);
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
	public void paint(Graphics g)
	{
		if(!activeVisible)
			return;
		if(!GameRenderer.isRenderCalled())
			return;
		super.paint(g);
	}
	@Override
	public void repaint()
	{
		//ignore repaint
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
	
	//WARNING: do not override isVisible since it is used internally with swing (you actually alaways want your object to be visible according to swing)
	public boolean isActiveVisible()
	{
		return activeVisible;
	}
}
