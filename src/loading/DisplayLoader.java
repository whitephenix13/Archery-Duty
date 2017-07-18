package loading;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import principal.InterfaceConstantes;

public class DisplayLoader {
	protected double UPDATE_LOAD = 0.5; //update every UPDATE_LOAD seconds the dots

	public boolean all_media_loaded=false;
	protected double start_load = -1; //keep track of the start of the load to display load progress
	protected int numberDot = 0;
	public int loadPercentage = 0;
	
	public void reset()
	{
		all_media_loaded=false;
		start_load = -1; 
		numberDot = 0;
		loadPercentage = 0;
	}
	public void showLoading(Graphics g)
	{
		if(start_load==-1 )
			start_load=System.nanoTime();
		else if((System.nanoTime()- start_load)*Math.pow(10, -9)>UPDATE_LOAD){
			start_load=System.nanoTime();
			numberDot=(numberDot+1)%4;
		}
		
		String s  = "Chargement";
		switch(numberDot)
		{
			case 1: s+=" .";break; 
			case 2: s+=" . .";break; 
			case 3: s+=" . . .";break; 
			default: break; 
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(new Font("TimesRoman", Font.PLAIN, 50)); 
		g2.setColor(Color.WHITE);
		g2.drawString(s, InterfaceConstantes.LARGEUR_FENETRE/2-120, InterfaceConstantes.HAUTEUR_FENETRE/2-25);
		g2.drawString(loadPercentage+"%", InterfaceConstantes.LARGEUR_FENETRE/2-50, InterfaceConstantes.HAUTEUR_FENETRE/2+40);
	}
}