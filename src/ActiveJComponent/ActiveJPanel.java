package ActiveJComponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.Arrays;

import javax.swing.JPanel;

import Affichage.GameRenderer;
import gameConfig.InterfaceConstantes;

public class ActiveJPanel extends JPanel{
	private boolean activeVisible;
	public ActiveJPanel()
	{
		super();
		super.setVisible(true);
		setIgnoreRepaint(true);
		activeVisible=true;
		setOpaque(false);
		this.setDoubleBuffered(false);
		this.setBounds(0,0,InterfaceConstantes.tailleEcran.width,InterfaceConstantes.tailleEcran.height);//Set bounds as LayeredPane has null layout and that the structure of rendering is: frame/LayeredPane/(1)JPanel/(2)JPanel
	}
	public ActiveJPanel(LayoutManager layout)
	{
		super(layout);
		super.setVisible(true);
		setIgnoreRepaint(true);
		activeVisible=true;
		setOpaque(false);
		this.setDoubleBuffered(false);
		this.setBounds(0,0,InterfaceConstantes.tailleEcran.width,InterfaceConstantes.tailleEcran.height);//Set bounds as LayeredPane has null layout and that the structure of rendering is: frame/LayeredPane/(1)JPanel/(2)JPanel
	}
	@Override
	public void paintComponents(Graphics g)
	{
		if(!activeVisible)
			return;
		if(!GameRenderer.isRenderCalled())
			return;
		//Add painting code here to draw before children so that background is displayed behind buttons
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
	/*REMOVE @Override
	public void validate()
	{
		if(!GameRenderer.isRenderCalled())
			return;
		super.validate();
	}
	@Override
	public void revalidate()
	{
		if(!GameRenderer.isRenderCalled())
			return;
		super.revalidate();
	}*/
	
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
