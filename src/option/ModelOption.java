package option;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import menuPrincipal.AbstractModelPrincipal;
import music.Music;
import music.MusicBruitage;
import types.Touches;


public class ModelOption extends AbstractModelOption{


	public void retourMenuPrincipal() {
		AbstractModelPrincipal.changeFrame=true;
		AbstractModelPrincipal.modeSuivant="Principal";
		AbstractModelPrincipal.changeMode=true;
	}


	public void setVolumeMusique(ChangeEvent event) {
		Music.volumeControl((double)((JSlider)event.getSource()).getValue()/100);
	}

	public void setVolumeBruitage(ChangeEvent event) {
		MusicBruitage bruit= new MusicBruitage();
		bruit.volumeControl((double)((JSlider)event.getSource()).getValue()/100);
	}


	public void blinkCustomClickableLabel()
	{
		if(caseFocus)
		{
			//on fait clignoter la case selectionner 
			blinkField = new Timer ();
			final long rate = 200;
			timerFinish = false;
 
			blinkField.scheduleAtFixedRate(new TimerTask(){public void run() 
			{
				if(memCustomClickableLabel.getBackground().equals(Color.WHITE))
					memCustomClickableLabel.setBackground(Color.GRAY);
				else
				memCustomClickableLabel.setBackground(Color.WHITE);
				
			}},rate/2,(long) rate);
		}
		else
		{
			//on arrete le clignotement 
			if(blinkField!=null && !timerFinish)
			{
				timerFinish=true;
				blinkField.cancel();
				
				memCustomClickableLabel.setBackground(Color.GRAY);
			}

		}
	}
	

	public void setModifTouches(int touch)
	{
		if(memCustomClickableLabel.getName().equals("droite"))
		{
			Touches.t_droite=touch;
		}
		else if(memCustomClickableLabel.getName().equals("gauche"))
		{
			Touches.t_gauche=touch;
		}
		else if(memCustomClickableLabel.getName().equals("saut"))
		{
			Touches.t_saut=touch;
		}
		else if(memCustomClickableLabel.getName().equals("tir"))
		{
			Touches.t_tir=touch;
		}
		else if(memCustomClickableLabel.getName().equals("slow"))
		{
			Touches.t_slow=touch;
		}
		else if(memCustomClickableLabel.getName().equals("pause"))
		{
			Touches.t_pause=touch;
		}
		else 
		{
			throw new IllegalArgumentException("nom JTextField inconnu");
		}
		
		updateInputText=true;
		notifyObserver();
	}

}
