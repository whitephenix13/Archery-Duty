package loading;

import java.awt.Color;
import java.awt.Graphics;

import Affichage.Drawable;
import images.ImagesBackground.ImBackgroundInfo;
import images.ImagesContainer.ImageGroup;

public class AffichageLoader extends Drawable{
	
	private Loader loader;
	public AffichageLoader(Loader loader)
	{
		super();
		this.loader=loader;
		mainPanel.setOpaque(false);
	}
	
	@Override 
	public void drawOnGraphics(Graphics g,boolean forceRepaint)
	{
		g.drawImage(loader.getGameHandler().getImage(ImageGroup.BACKGROUND, null, ImBackgroundInfo.BLACK, null), 0, 0, null);
		
		//show loading 
		loader.showLoading(g);
		
		//mainFrame.repaint();REMOVE
	}
	
	public void onUpdateGraphics()
	{
		//REMOVE getFrame().repaint();
	}
	
}
