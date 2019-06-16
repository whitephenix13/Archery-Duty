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

public abstract class AbstractModelPrincipal implements Observable{
	
	//Time in order in order to display only every 1/fps seconds
	protected long last_update = 0;
	
	//booleen permettant de s'assurer qu'un tour de boucle complet a été fait lorsqu'on change de mode 
	protected boolean debutBoucle;

	//touches de deplacement
	protected Touches touches ;
	
	public Loader loaderMenuPrincipal = null;
	public Loader loaderAllMedia = null;
	
	public ImagesPrincipal imPrincipal= new ImagesPrincipal();

	public static String modeActuel="Principal";
	public static String modeSuivant="";
	public static boolean changeMode=false;
	public static boolean changeFrame =false;
	public static boolean test =false;
	public static DebugTime debugTime;
	
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
