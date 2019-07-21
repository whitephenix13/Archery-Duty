package menu.choixNiveau;

import java.util.ArrayList;
import java.util.List;

import ActiveJComponent.ActiveJButton;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameMode;
import utils.observer.Observable;
import utils.observer.Observer;

public abstract class AbstractModelChoixNiveau implements Observable,GameMode{
	
	protected GameHandler gameHandler;
	
	protected String niveauSelectionne="";
	protected List<String> listNomNiveaux = new ArrayList<String>();
	protected List<ActiveJButton> listNiveaux = new ArrayList<ActiveJButton>();
	
	protected boolean updateListLevels=false;
	
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();

	public abstract void resetBouton(ActiveJButton bouton, String nomNiveau);
	public abstract void getAllNiveaux();
	public abstract void selectLevel(ActiveJButton button);
	public abstract void playLevel();
	//public static String getPath();

	public abstract String getNiveauSelectionne();
	public abstract List<ActiveJButton> getListBoutonNiveau();
	
	public AbstractModelChoixNiveau()
	{
	}
	
	
	public boolean getUpdateListLevels()
	{
		return updateListLevels;
	}
	
	public void resetVariablesAffichages()
	{
		updateListLevels=false;
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
}
