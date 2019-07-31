package option;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.ChangeEvent;

import ActiveJComponent.ActiveJSlider;
import Affichage.GameRenderer;
import Affichage.Drawable;
import menu.menuPrincipal.AbstractModelPrincipal;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameHandler.GameModeType;
import menu.menuPrincipal.GameMode;
import music.Music;
import music.MusicBruitage;
import partie.modelPartie.InputPartie;


public class ModelOption extends AbstractModelOption{

	public ModelOption(Touches _touches, InputPartie _inputPartie,GameHandler gameHandler)
	{
		super();
		this.gameHandler=gameHandler;
		touches=_touches;
		inputPartie=_inputPartie;
	}
	public void retourMenuPrincipal() {
		if(specificReturn!= null){
			gameHandler.setGameMode(specificReturn);
			specificReturn=null;
		}
		else
			gameHandler.setGameMode(GameModeType.MAIN_MENU);
	}


	public void setVolumeMusique(ChangeEvent event) {
		Music.me.volumeControl((double)((ActiveJSlider)event.getSource()).getValue()/100);
	}

	public void setVolumeBruitage(ChangeEvent event) {
		MusicBruitage.me.volumeControl((double)((ActiveJSlider)event.getSource()).getValue()/100);
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
	

	public void setModifTouches(String touch,InputPartie inpPartie)
	{
		if(memCustomClickableLabel == null)
			return;
		String labelName = memCustomClickableLabel.getName();
		if(labelName.equals("droite"))
		{
			inpPartie.rebindKey(touch, touches.t_droite);
			touches.t_droite=touch;
		}
		else if(labelName.equals("gauche"))
		{
			inpPartie.rebindKey(touch, touches.t_gauche);
			touches.t_gauche=touch;
		}
		else if(labelName.equals("saut"))
		{
			inpPartie.rebindKey(touch, touches.t_saut);
			touches.t_saut=touch;
		}
		else if(labelName.contains("tir"))
		{
			int i = labelName.contains("special")?(Integer.parseInt(labelName.substring(labelName.length()-1,labelName.length()))) :0;
			inpPartie.rebindKey(touch, touches.t_tir[i]);
			touches.t_tir[i]=touch;
		}
		else if(labelName.contains("slot"))
		{
			int i = Integer.parseInt(labelName.substring(labelName.length()-1,labelName.length()));
			inpPartie.rebindKey(touch, touches.t_slot[i]);
			touches.t_slot[i]=touch;
		}
		else if(labelName.equals("dash"))
		{
			inpPartie.rebindKey(touch, touches.t_dash);
			touches.t_dash=touch;
		}
		else if(labelName.equals("slow"))
		{
			inpPartie.rebindKey(touch, touches.t_slow);
			touches.t_slow=touch;
		}
		else if(labelName.equals("pause"))
		{
			inpPartie.rebindKey(touch, touches.t_pause);
			touches.t_pause=touch;
		}
		else 
		{
			throw new IllegalArgumentException("nom JTextField inconnu: "+labelName);
		}
		
		updateInputText=true;
		notifyObserver();
	}
	
	public void doComputations(GameRenderer affich){
		//As this mode is controlled by listeners, the computationDone is set to false when a listener is triggered. This function is then left empty
	}
	public void updateSwing(){
		this.notifyMainObserver();
	}

	@Override
	public boolean isGameModeLoaded()
	{
		//loading not required 
		return true;
	}
	@Override
	public GameMode getLoaderGameMode(){
		//loading not required 
		return null;
	}
}
