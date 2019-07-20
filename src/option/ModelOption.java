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
		
		//REMOVE AbstractModelPrincipal.changeFrame=true; //REMOVE
		gameHandler.setGameMode(GameModeType.MAIN_MENU);
		/*REMOVE AbstractModelPrincipal.modeSuivant="Principal";
		AbstractModelPrincipal.changeMode=true;*/
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
		if(memCustomClickableLabel.getName().equals("droite"))
		{
			inpPartie.rebindKey(touch, touches.t_droite);
			touches.t_droite=touch;
		}
		else if(memCustomClickableLabel.getName().equals("gauche"))
		{
			inpPartie.rebindKey(touch, touches.t_gauche);
			touches.t_gauche=touch;
		}
		else if(memCustomClickableLabel.getName().equals("saut"))
		{
			inpPartie.rebindKey(touch, touches.t_saut);
			touches.t_saut=touch;
		}
		else if(memCustomClickableLabel.getName().equals("tir"))
		{
			inpPartie.rebindKey(touch, touches.t_tir);
			touches.t_tir=touch;
		}
		else if(memCustomClickableLabel.getName().equals("tir secondaire"))
		{
			inpPartie.rebindKey(touch, touches.t_2tir);
			touches.t_2tir=touch;
		}
		else if(memCustomClickableLabel.getName().equals("slow"))
		{
			inpPartie.rebindKey(touch, touches.t_slow);
			touches.t_slow=touch;
		}
		else if(memCustomClickableLabel.getName().equals("pause"))
		{
			inpPartie.rebindKey(touch, touches.t_pause);
			touches.t_pause=touch;
		}
		else 
		{
			throw new IllegalArgumentException("nom JTextField inconnu");
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
	public boolean isComputationDone(){
		return computationDone;
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
