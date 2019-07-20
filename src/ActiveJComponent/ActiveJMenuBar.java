package ActiveJComponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import Affichage.GameRenderer;

public class ActiveJMenuBar extends JMenuBar{
	private boolean activeVisible;
	public ActiveJMenuBar()
	{
		super();
		activeVisible=true;
		setIgnoreRepaint(true);
		this.setDoubleBuffered(false);
	}
	
	/**
	 * Auxiliary function to draw the JMenuBar 
	 * @param p1
	 * @param p2
	 */
	private void addPointToResult(Point p1,Point p2)
	{
		p1.x+=p2.x;
		p1.y+=p2.y;
	}
	/**
	 * Auxiliary function to draw the JMenuBar 
	 * @param g
	 * @param accumulated this value is changed by reference 
	 * @param xvalue
	 * @param yvalue
	 */
	private void translate(Graphics g, Point accumulated_out, int xvalue, int yvalue)
	{
		if(xvalue!=0){
			g.translate(xvalue, 0);
			accumulated_out.x+=xvalue;
		}
		if(yvalue != 0){
			g.translate(0, yvalue);
			accumulated_out.y+=yvalue;
		}
	}
	
	private Point testRecursiveDrawJMenu(JMenu menu,Graphics g)
	{
		Point accumulated_translate=new Point();
		if(menu != null && menu.isSelected())
		{
			//assumes that menu was draw by 1)Menu bar or 2) previous recursion 
			//Therefore only draw the pop menu 
			menu.getPopupMenu().paint(g);
			
			//JMenuItem are drawn by the previous command "menu.getPopupMenu().paint(g)". We only need to draw the pop up of the JMenu located in this popup menu (fiou!) 
			for(int i=0; i< menu.getPopupMenu().getComponentCount();++i){
				//Expend the Jmenu => translate the graphics to the right and call this function recursively to draw the menu 
				if(menu.getPopupMenu().getComponents()[i] instanceof JMenu){
					translate(g,accumulated_translate,menu.getPopupMenu().getComponents()[i].getBounds().width,0);
					testRecursiveDrawJMenu((JMenu)menu.getPopupMenu().getComponents()[i],g);
					translate(g,accumulated_translate,-menu.getPopupMenu().getComponents()[i].getBounds().width,menu.getPopupMenu().getComponents()[i].getBounds().height);
				}
				//Take into account that there is a JMenuItem and translate the graphics to the bottom 
				else if(menu.getPopupMenu().getComponents()[i] instanceof JMenuItem){
					translate(g,accumulated_translate,0,menu.getPopupMenu().getComponents()[i].getBounds().height);
				}
			}
				
		}
		return accumulated_translate;
	}
	public void paintBar(Graphics g)
	{
		Rectangle bound = this.getBounds();
		//Clear back buffer
		g.setColor(Color.WHITE);
		g.fillRect(bound.x,bound.y,bound.width,bound.height);
		//Draw menus in the bar 
		this.paintComponents(g);
		g.translate(0, getBounds().height);
		//draw popup menu
		Point accumulated_translate =new Point();
		JMenu menu;
		for (int i=0; i< getMenuCount(); ++i){
			menu = getMenu(i);
			//Draw all the popup menu/menu item from this top menu and accumulate the graphic shift to negate it afterward
			addPointToResult(accumulated_translate,testRecursiveDrawJMenu(menu,g));
			//Translate the next menu item (which is located in the menu bar) to the right 
			if(menu != null){
				translate(g,accumulated_translate,menu.getBounds().width,0);
			}
		}
		g.translate(-accumulated_translate.x, -getBounds().height-accumulated_translate.y);
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
	
	//WARNING: do not override isVisible since it is used internally with swing (you actually alaways want your object to be visible according to swing)
	public boolean isActiveVisible()
	{
		return activeVisible;
	}
}
