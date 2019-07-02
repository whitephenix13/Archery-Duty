package Affichage;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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

	protected DrawablePanel mainPanel;//DO NOT OVERRIDE THE PAINT COMPONENT 
	protected Affichage mainFrame;
	
	private long fadeOutTime; //milli; time before mainPanel should be fully transparent 
	private long startFadeOutTime;//nano; start time when the fade out begun
	
	private final int fadingType;
	
	
	public class DrawablePanel extends JPanel{
				
		@Override
		public void paintComponent(Graphics g)
		{
			//System.out.println("draw");
			ModelPrincipal.debugTimeAffichage.init(InterfaceConstantes.DEBUG_TIME_AFFICHAGE_PRINT_MODE,-1);
			ModelPrincipal.debugTimeAffichage.startElapsedForVerbose();
			//System.out.println("Repaint " + Drawable.this);
			//Set transparency for smooth transitions
			super.paintComponent(g);
			ModelPrincipal.debugTimeAffichage.elapsed("Super paint component");
			if(isFading){
				((Graphics2D)g).setComposite(AlphaComposite.getInstance(fadingType, computeTransparency()));
			}
			else//Set transparency back to default
				((Graphics2D)g).setComposite(AlphaComposite.getInstance(fadingType, 1));
			ModelPrincipal.debugTimeAffichage.elapsed("Set composite");
			draw(g);
			ModelPrincipal.debugTimeAffichage.elapsed("draw");
			ModelPrincipal.debugTimeAffichage.print();
		}
	}
	
	
	public Drawable()
	{
		ModelPrincipal.debugTimeAffichage.init(InterfaceConstantes.DEBUG_TIME_AFFICHAGE_PRINT_MODE,-1);
		fadingType = AlphaComposite.SRC_OVER;
		isFading=false;
		mainPanel = new DrawablePanel();
		mainPanel.setSize(InterfaceConstantes.tailleEcran);
		mainPanel.setLocation(0, 0);
		mainPanel.setDoubleBuffered(true);
	}
	
	public JPanel getContentPane(){return mainPanel;}
	public void setFrameReference(Affichage frame){mainFrame=frame;}
	public void startFadeOut(float fadeOutTime_ms)
	{
		fadeOutTime=(long)(fadeOutTime_ms*Math.pow(10, 6));
		startFadeOutTime= System.nanoTime();
		isFading=true;
	}
	
	private float computeTransparency()
	{
		float elapsed = System.nanoTime()-startFadeOutTime;
		if( elapsed > fadeOutTime){
			mainFrame.warnFadeOutEnded(this);
			isFading=false;
			return 0;
		}
		else{
			return 1-((float)elapsed)/fadeOutTime;
		}
			
	}
	public abstract void draw(Graphics g);//what should be drawn in the mainPanel
}
