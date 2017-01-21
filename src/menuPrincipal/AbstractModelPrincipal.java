package menuPrincipal;

import java.util.ArrayList;

import music.Music;
import music.ThreadMusique;
import observer.Observable;
import observer.Observer;
import option.AbstractControlerOption;
import option.AbstractModelOption;
import option.AffichageOption;
import partie.AbstractControlerPartie;
import partie.AbstractModelPartie;
import partie.AffichagePartie;
import types.Touches;
import Affichage.Affichage;
import choixNiveau.AbstractControlerChoixNiveau;
import choixNiveau.AbstractModelChoixNiveau;
import choixNiveau.AffichageChoixNiveau;
import credit.AffichageCredit;
import editeur.AbstractControlerEditeur;
import editeur.AbstractModelEditeur;
import editeur.AffichageEditeur;

public abstract class AbstractModelPrincipal implements Observable{
	
	//booleen permettant de s'assurer qu'un tour de boucle complet a été fait lorsqu'on change de mode 
	protected boolean debutBoucle;

	//touches de deplacement
	protected Touches touches ;
	
	//musique 
	protected Music music;
	protected ThreadMusique threadMusique;
	protected Thread t;
		
	public static String modeActuel="Principal";
	public static String modeSuivant="";
	public static boolean changeMode=false;
	public static boolean changeFrame =false;
	public static boolean test =false;
	
	protected AbstractModelPrincipal principal ;
	protected AbstractControlerPrincipal controlerPrincipal ;
	protected AffichagePrincipal affichagePrincipal ;
	
	protected AbstractModelOption option ;
	protected AbstractControlerOption controlerOption ;
	protected AffichageOption affichageOption ;
	
	protected AbstractModelEditeur edit ;
	protected AbstractControlerEditeur controlerEditeur ;
	protected AffichageEditeur affichageEditeur ;
	
	protected AffichageCredit affichageCredit;
	
	protected AbstractModelChoixNiveau choix ;
	protected AbstractControlerChoixNiveau controlerChoix ;
	protected AffichageChoixNiveau affichageChoix ;
	
	protected AbstractModelPartie partie ;
	protected AbstractControlerPartie controlerPartie ;
	protected AffichagePartie affichagePartie ;
	
	protected Affichage affich; 
	
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();

	protected abstract void Init();
	protected abstract void ChangementMode();
	protected abstract void StartBoucleJeu();
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
}
