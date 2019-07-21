package menu.menuPrincipal;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

import Affichage.GameRenderer;
import debug.DebugTime;
import editeur.AbstractControlerEditeur;
import editeur.AbstractModelEditeur;
import editeur.AffichageEditeur;
import gameConfig.InterfaceConstantes;
import images.ImagesBackground;
import images.ImagesCondition;
import images.ImagesEffect;
import images.ImagesFleche;
import images.ImagesFlecheIcon;
import images.ImagesHeros;
import images.ImagesMonde;
import images.ImagesMonstre;
import images.ImagesPrincipal;
import images.ImagesTirMonstre;
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
	public ImagesBackground imBackground= new ImagesBackground();
	public ImagesMonde imMonde= new ImagesMonde();
	public ImagesMonstre imMonstre= new ImagesMonstre();
	public ImagesHeros imHeros= new ImagesHeros();
	public ImagesTirMonstre imTirMonstre= new ImagesTirMonstre();
	public ImagesFleche imFleches = new ImagesFleche();
	public ImagesEffect imEffect= new ImagesEffect();
	public ImagesCondition imConditions= new ImagesCondition();
	public ImagesFlecheIcon imFlecheIcon = new ImagesFlecheIcon();
	
	public GameModeType currentGameModeType;
	public static boolean test =false;
	public static DebugTime debugTime;
	public static DebugTime debugTimeAffichage;

	protected boolean forceActuAffichage; 
	
	protected ScheduledExecutorService executor;
	
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
	
	protected GameRenderer gameRenderer; 
	
	protected GameMode currentGameMode; 
	protected GameModeType nextGameMode;
	
	//protected boolean gameInit;
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
	public AbstractModelPrincipal()
	{
		currentGameModeType = GameModeType.MAIN_MENU;
		//nextGameMode=GameModeType.MAIN_MENU;
		currentGameMode = this;
		ModelPrincipal.debugTime = new DebugTime(InterfaceConstantes.DEBUG_TIME_LOOP_TO_SLOW,InterfaceConstantes.DEBUG_TIME_ACTION_TO_SLOW,InterfaceConstantes.DEBUG_TIME_VERBOSE);

	}
	
	
	protected abstract void Init();
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
