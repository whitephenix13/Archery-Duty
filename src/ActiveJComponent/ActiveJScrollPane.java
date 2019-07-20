package ActiveJComponent;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JScrollPane;

import Affichage.GameRenderer;

public class ActiveJScrollPane extends JScrollPane{
	private boolean activeVisible;
	public ActiveJScrollPane(ActiveJToolBar toolbar)
	{
		super(toolbar);
		activeVisible=true;
		this.setOpaque(false);
		this.getViewport().setOpaque(false);
		setIgnoreRepaint(true);
	}
	public ActiveJScrollPane(ActiveJPanel pan)
	{
		super(pan);
		activeVisible=true;
		this.setOpaque(false);
		this.getViewport().setOpaque(false);
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
}
