package option;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Timer;

import javax.swing.event.ChangeEvent;

import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameHandler.GameModeType;
import menu.menuPrincipal.GameMode;
import option.AffichageOption.CustomClickableLabel;
import partie.input.InputPartie;
import utils.observer.Observable;
import utils.observer.Observer;

public abstract class AbstractModelOption implements Observable,GameMode{
	
	protected GameHandler gameHandler;
	
	protected Touches touches;

	//add Option Model variables 
	protected Timer blinkField;
	protected boolean timerFinish =true;
	protected boolean caseFocus = false;
	protected CustomClickableLabel memCustomClickableLabel;
	public InputOption inputOption;
	protected InputPartie inputPartie;
	//variables pour l'affichage 
	protected boolean showInputError=false;
	protected boolean updateInputText=false;
	
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
	protected GameModeType specificReturn =null;
	
	public AbstractModelOption()
	{
	}
	
	public void setSpecificReturn(GameModeType type){specificReturn = type;}
	public void setCaseFocus(boolean _caseFocus){caseFocus=_caseFocus;}
	public void setShowInputError(boolean value){showInputError=value;}
	public void setMemCustomClickableLabel(CustomClickableLabel _mem)
	{
		//On reset l'ancien label 
		if(memCustomClickableLabel!=null)
			memCustomClickableLabel.setBackground(Color.WHITE);

		//on memorise le nouveau
		memCustomClickableLabel=_mem;

		//on request le focus pour le nouveau pour etre sur que les inputs claviers soient bien écoutés
		if(memCustomClickableLabel!=null)
		{
			memCustomClickableLabel.requestFocus();
		}

	}


	public boolean getCaseFocus(){return caseFocus;}
	public boolean getShowInputError(){return showInputError;}
	public boolean getUpdateInputText(){return updateInputText;}
	public CustomClickableLabel getMemCustomClickableLabel(){return memCustomClickableLabel;}

	public void resetVariablesAffichage()
	{
		showInputError=false;
		updateInputText=false;
	}
	public void resetVariables()
	{
		if(blinkField!=null)
			blinkField.cancel();blinkField=null;

			timerFinish =true;
			caseFocus=false;
			memCustomClickableLabel=null;

			resetVariablesAffichage();
	}
	//Implémentation du pattern observer
	public void addObserver(Observer obs) {
		this.listObserver.add(obs);
	}
	public void notifyObserver() {
		for(Observer obs : listObserver)
			obs.update();
	}
	public void removeObserver() {
		listObserver = new ArrayList<Observer>();
	}  

	private Observer mainObserver;

	public void addMainObserver(Observer obs) {
		mainObserver=obs;
	}
	public void notifyMainObserver() {
		mainObserver.update();
	}
	public void removeMainObserver() {
		mainObserver=null;
	}  
	

	public abstract void retourMenuPrincipal();
	public abstract void blinkCustomClickableLabel();
	public abstract void setVolumeMusique(ChangeEvent event);
	public abstract void setVolumeBruitage(ChangeEvent event);
	public abstract void setModifTouches(String value, InputPartie inpPartie);

}
