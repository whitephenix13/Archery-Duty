package Affichage;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

import ActiveJComponent.ActiveJFrame;
import ActiveJComponent.ActiveJPanel;
import gameConfig.InterfaceConstantes;
import menu.menuPrincipal.ModelPrincipal;

public abstract class Drawable {
	/*
	 * When extending this class; make sure 
	 * 1) to call super() in the init method
	 * 2) to call mainFrame.warnFadeOutCanStart() in the draw() method to start the fading (or it will never happen)
	 * 3) to not override the paintComponent method of mainPanel
	 * 
	 * */
	public boolean isFading;

	protected ActiveJPanel mainPanel;
	private GameRenderer gameRenderer;
	
	private long fadeOutTime; //milli; time before mainPanel should be fully transparent 
	private long startFadeOutTime;//nano; start time when the fade out begun
	
	private final int fadingType;
	
	public Drawable()
	{
		fadingType = AlphaComposite.SRC_OVER;
		isFading=false;
		mainPanel = new ActiveJPanel();
		resetFadingState();
	}
	public void resetFadingState()
	{
		startFadeOutTime=-1;
	}
	/***
	 * Set the gameRender to finish the initialization of this drawable
	 * @param d
	 */
	public void initFromGameRenderer(GameRenderer g)
	{
		this.gameRenderer = g;
	}
	/***
	 * Use another drawable to init this one (mainly the gameRenderer)
	 * @param d
	 */
	public void initFromOtherDrawable(Drawable d)
	{
		this.gameRenderer= d.gameRenderer;
	}
	
	public ActiveJPanel getContentPane(){return mainPanel;}
	public ActiveJFrame getActiveJFrame(){return (ActiveJFrame) SwingUtilities.getWindowAncestor(mainPanel);}
	public void startFadeOut(float fadeOutTime_ms)
	{
		fadeOutTime=(long)(fadeOutTime_ms*Math.pow(10, 6));
		startFadeOutTime= System.nanoTime();
		isFading=true;
	}
	/*public void warnFadeOutCanStart()
	{
		gameRenderer.warnFadeOutCanStart();
	}*/
	
	public float getTransparency()
	{
		return computeTransparency();
	}

	public void setTransparency(Graphics2D g2d)
	{
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, computeTransparency()));
	}

	public void setTransparency(Graphics2D g2d, float transparency)
	{
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
	}
	public void requestBeginTransition()
	{
		gameRenderer.beginTransition(this);
	}
	private float computeTransparency()
	{
		if(startFadeOutTime==-1) //fading has not started
			return 1;
		float elapsed = System.nanoTime()-startFadeOutTime;
		if( elapsed > fadeOutTime){
			isFading=false;
			return 0;
		}
		else{
			return 1-elapsed/fadeOutTime;
		}
			
	}
	public abstract void drawOnGraphics(Graphics g,boolean forceRepaint);//what should be drawn in the mainPanel
}
