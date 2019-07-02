package loading;

import java.awt.Color;
import java.awt.Graphics;

import Affichage.Drawable;

public class AffichageLoader extends Drawable{
	
	private Loader loader;
	public AffichageLoader(Loader loader)
	{
		super();
		this.loader=loader;
		mainPanel.setOpaque(false);
		mainPanel.setBackground(Color.BLACK);
	}
	
	@Override 
	public void draw(Graphics g)
	{
		mainFrame.warnFadeOutCanStart();

		//show loading 
		loader.showLoading(g);
		//mainFrame.repaint();REMOVE
	}
	
	public void onUpdateGraphics()
	{
		mainFrame.repaint();
	}
	
}
