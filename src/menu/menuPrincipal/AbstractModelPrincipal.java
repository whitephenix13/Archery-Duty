package menu.menuPrincipal;

import java.util.ArrayList;

import Affichage.Affichage;
import debug.DebugTime;
import editeur.AbstractControlerEditeur;
import editeur.AbstractModelEditeur;
import editeur.AffichageEditeur;
import images.ImagesPrincipal;
import loading.Loader;
import menu.choixNiveau.AbstractControlerChoixNiveau;
import menu.choixNiveau.AbstractModelChoixNiveau;
import menu.choixNiveau.AffichageChoixNiveau;
import menu.credit.AffichageCredit;
import option.AbstractControlerOption;
import option.AbstractModelOption;
import option.AffichageOption;
import option.Touches;
import partie.modelPartie.AbstractControlerPartie;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.AffichagePartie;
import utils.observer.Observable;
import utils.observer.Observer;

public abstract class AbstractModelPrincipal implements Observable,GameHandler,GameMode{
	
	//Time in order in order to display only every 1/fps seconds
	protected long last_update = 0;
	
	//touches de deplacement
	protected Touches touches ;
	
	public Loader loaderMenuPrincipal = null;
	public Loader loaderAllMedia = null;
	
	public ImagesPrincipal imPrincipal= new ImagesPrincipal();

	public GameModeType currentGameModeType;
	//public static String modeSuivant=""; //REMOVE
	//public static boolean changeMode=false;//REMOVE
	//public static boolean changeFrame =false;//REMOVE
	public static boolean test =false;
	public static DebugTime debugTime;
	public static DebugTime debugTimeAffichage;

	protected boolean forceActuAffichage; 
	
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
	
	protected GameMode currentGameMode; 
	protected GameModeType nextGameMode;
	protected boolean computationDone;
	
	protected boolean gameInit;
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
	public AbstractModelPrincipal()
	{
		currentGameModeType = GameModeType.MAIN_MENU;
		nextGameMode=GameModeType.MAIN_MENU;
		currentGameMode = this;
		computationDone=true;//set to false via listeners 
	}
	
	
	protected abstract void Init();
	//REMOVE protected abstract void ChangementMode();
	//REMOVE protected abstract void StartBoucleJeu();
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
			if(mainObserver!=null)
				mainObserver.update();
		}
		public void removeMainObserver() {
			mainObserver=null;
		}  
}
