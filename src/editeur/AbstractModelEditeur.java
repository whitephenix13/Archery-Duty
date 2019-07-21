package editeur;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import ActiveJComponent.ActiveJPanel;
import editeur.BarreOutil.BarreOutil;
import editeur.Menu.menuEditeur;
import gameConfig.InterfaceConstantes;
import images.ImagesContainer;
import images.ImagesMonde;
import loading.Loader;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameMode;
import partie.bloc.Bloc.TypeBloc;
import partie.bloc.Monde;
import utils.observer.Observable;
import utils.observer.Observer;

public abstract class AbstractModelEditeur implements Observable,GameMode{

	public GameHandler gameHandler;
	public Loader loaderEditeur;
	public ImagesContainer imagesMonde;
	
	protected BarreOutil barreOut;
	protected menuEditeur menuEdit;
	
	protected int tailleBloc;  //taille d'un sprite de décors 
	protected float dezoomFactor;
	protected int xViewPort;
	protected int yViewPort;
	
	protected Monde monde ;
	protected List<StockageMonstre> tabEditeurMonstre;
	
	//Utilisé pour charger les images 
	protected TypeBloc texture;
	
	//monstres
	protected boolean monstreActive;
	protected boolean staticMonstre;
	protected List<StockageMonstre> monstreDansCase;

	//texture speciale: 
	protected boolean perso;
	protected int[] persoPos;
	protected boolean start;
	protected int[] startPos;
	protected boolean end;
	protected int[] endPos;
		
	protected boolean bloquant ;
	protected boolean background;
	
	//variables pour le drag 
	protected boolean drag; 
	protected int xStartDrag;
	protected int yStartDrag;
	protected int xMousePos;
	protected int yMousePos;

	protected boolean loupe; 
	//variables pour l'affichage 
	protected boolean showMonsters;
	protected boolean showStaticMonsters;
	protected boolean showMessageDialog;
	protected String[] textMessageDialog ={"",""};
	protected int typeMessageDialog=-1;
		
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
		
	public boolean getShowMonsters(){return showMonsters;}
	public boolean getShowStaticMonsters(){return showStaticMonsters;}
	
	public void setBloquant(boolean _bloquant){	
		bloquant=_bloquant;
		}	
	public void setBackground(boolean _background){	
		background=_background;
		}
	
	public AbstractModelEditeur()
	{
	}
	
	public void init ()
	{
		tailleBloc=100;
		dezoomFactor=0.2f;
		xViewPort = (InterfaceConstantes.ABS_MAX-InterfaceConstantes.WINDOW_WIDTH/tailleBloc)*100/2;
		yViewPort = (InterfaceConstantes.ORD_MAX-InterfaceConstantes.WINDOW_HEIGHT/tailleBloc)*100/2;

		monde=new Monde();
		tabEditeurMonstre= new ArrayList <StockageMonstre> ();
		
		texture=TypeBloc.NONE;
				
		monstreActive=false;
		staticMonstre=false;
		monstreDansCase= new ArrayList<StockageMonstre>();
				 
		perso = false;
		persoPos= new int[2];
		persoPos[0]=-1;
		persoPos[1]=-1;
		start = false;
		startPos= new int[2];
		startPos[0]=-1;
		startPos[1]=-1;
		end = false;		
		endPos= new int[2];
		endPos[0]=-1;
		endPos[1]=-1;
		
		bloquant=false;
		background=false;
				
		drag=false;
		xStartDrag=0;
		yStartDrag=0;
		xMousePos=0;
		yMousePos=0;

		loupe= false; 
		
		resetVariablesAffichage();
		
		notifyObserver();

	}
	public abstract void moveViewPort(int xpos, int ypos);
	public abstract void releaseMoveViewport();
	public abstract void drawTerrain(int xpos, int ypos);
	public abstract void drawMonster(int xpos, int ypos);
	public abstract void drawSpecial(int xpos, int ypos);
	public abstract void draw(Graphics g,ActiveJPanel pan);
	public abstract void setTexture(TypeBloc texture);
	
	public abstract List<StockageMonstre> FindMonstre(Point targetPos, List<StockageMonstre> listToSearch );
	public abstract void deleteMonster(int x, int y);

	//fonctions pour le menu 
	public abstract String sauver(String nom);
	public abstract void charger(String nom);
	public abstract void information();
	public abstract void dezoom();
	
	public void resetVariablesAffichage()
	{
		showMonsters=false;
		showStaticMonsters=false;
		showMessageDialog=false;
		textMessageDialog = new String[2];
		typeMessageDialog=-1;

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
