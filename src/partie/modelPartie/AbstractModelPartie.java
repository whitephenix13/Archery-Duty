package partie.modelPartie;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ActiveJComponent.ActiveJButton;
import Affichage.DrawImageHandler;
import Affichage.GameRenderer;
import debug.DebugDraw;
import gameConfig.InterfaceConstantes;
import images.ImagesContainer;
import images.ImagesFlecheIcon;
import loading.Loader;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameMode;
import music.MusicBruitage;
import option.Touches;
import partie.AI.A_Star;
import partie.AI.A_Star_Helper;
import partie.bloc.Monde;
import partie.collision.Collidable;
import partie.deplacement.Deplace;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.projectile.Projectile;
import utils.observer.Observable;
import utils.observer.Observer;

public abstract class AbstractModelPartie  implements Observable, GameMode{

	protected GameHandler gameHandler;
	
	public Loader loaderPartie = null;
	protected int frame = 0;
	public void nextFrame(){frame+=1;}
	public int getFrame(){return frame;}
	//repaint flag 
	public boolean isFirstFrameReady=false;
	
	/**Override the draw method to debug with draw*/
	public DebugDraw debugDraw =null;

	
	protected Deplace deplace = new Deplace();
	public Monde monde = null;
	public Heros heros;

	//Action speciale pour ralentir le temps 
	public boolean slowDown=false;
	public int slowCount=0;

	public List<Projectile> tabFleche= new ArrayList<Projectile>();
	public List<Projectile> tabTirMonstre = new ArrayList<Projectile>();
	public List<Entity> tabMonstre= new ArrayList <Entity> ();
	
	public List<Collidable> arrowsEffects = new ArrayList<Collidable>();
	
	protected DrawImageHandler imageDrawer = new DrawImageHandler();
	
	protected int nombreMonstreRestant= 0;

	//Permet de stocker les éléments de la liste à supprimer.On les supprimera à la frame suivante
	protected List<Integer> lEffaceFleche= new ArrayList<Integer>();
	protected List<Integer> lEffaceMonstre= new ArrayList<Integer>();
	protected List<Integer> lEffaceTirMonstre= new ArrayList<Integer>();
	protected List<Integer> lEffaceEffect= new ArrayList<Integer>();

	//INPUT 
	Touches touches;
	public InputPartie inputPartie;

	//Variables de déplacement 
	//booleen pour savoir si on change de mouvement et donc qu'on doit reequilibre la hitbox du heros
	public boolean changeMouv=false;
	
	protected boolean finPartie =false;
	protected boolean inPause=false;
	public boolean getinPause(){return inPause;}


	protected boolean firstNonFocused=true;


	//INPUT INTERPRETE 
	protected int xPositionSouris = 0 ;
	public int getXPositionSouris(){return xPositionSouris;}
	protected int yPositionSouris = 0 ;
	public int getYPositionSouris(){return yPositionSouris;}

	//}}
	///AFFICHAGE 
	//pour pouvoir acceder aux images chargées 
	public ImagesContainer imBackground;
	public ImagesContainer imMonde;
	public ImagesContainer imMonstre;
	public ImagesContainer imHeros;
	public ImagesContainer imTirMonstre;
	public ImagesContainer imFleches;
	public ImagesContainer imEffect;
	public ImagesContainer imConditions;
	public ImagesFlecheIcon imFlecheIcon;
	
	public Point INIT_RECT= new Point(50000,50000); //(abs,ord)
	//public int absRect =INIT_RECT.x;
	//public int ordRect = INIT_RECT.y;
	public int TAILLE_BLOC=100;
	//variable to know to displacement of the screen
	public int xScreendisp = 0;
	public int yScreendisp = 0;
	public Point getScreenDisp()
	{return new Point(xScreendisp,yScreendisp);}
	/**
	 * 
	 * @param x return xScreendispBloc, false: return yScreendispBloc
	 * @return Screendisp grounded to the closest multiple of TAILLE_BLOC
	 */
	public int getXYScreendispBloc(boolean x)
	{return (x?xScreendisp:yScreendisp)/TAILLE_BLOC*TAILLE_BLOC;}
	/**
	 * 
	 * @param x return xScreendispBloc, false: return yScreendispBloc
	 * @return Screendisp modulo TAILLE_BLOC
	 */
	public int getXYScreendispMod(boolean x)
	{return (x?xScreendisp:yScreendisp)%TAILLE_BLOC;}
	
	/**
	 * 
	 * @param x: return xScreendispBloc, false: return yScreendispBloc
	 * @return x/y value of the viewport 
	 */
	public int getXYViewport(boolean x)
	{return (x?INIT_RECT.x:INIT_RECT.y)- getXYScreendispBloc(x);}

	//variables pour l'affichage 
	protected boolean disableBoutonsFin=false;
	protected boolean setAffichageOption=false; 
	protected boolean arrowSlotIconChanged =true;
	//private boolean forceRepaint = false;
	
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
	//}}
	public boolean getDisableBoutonsFin(){return disableBoutonsFin ;}
	public boolean getFinPartie(){return finPartie;}

	public void init()
	{
		//pour initaliser variables partie rapide dans music bruitage
		MusicBruitage.init();
		
		reset();
		resetVariablesAffichage();
	}
	
	public void reset() 
	{
		if(loaderPartie != null)
			loaderPartie.reset();
		PartieTimer.init();

		deplace = new Deplace();
		if(monde==null)
			monde = new Monde();
		frame= 0;
		heros= new Heros(InterfaceConstantes.WINDOW_WIDTH/2,InterfaceConstantes.WINDOW_HEIGHT/2,1,frame);

		//Action speciale pour ralentir le temps 
		slowDown=false;
		slowCount=0;

		tabFleche= new ArrayList<Projectile>();
		tabTirMonstre = new ArrayList<Projectile>();
		tabMonstre= new ArrayList <Entity> ();
		arrowsEffects = new ArrayList<Collidable>();
		nombreMonstreRestant= 0;

		imageDrawer = new DrawImageHandler();
		
		lEffaceFleche= new ArrayList<Integer>();
		lEffaceMonstre= new ArrayList<Integer>();
		lEffaceTirMonstre= new ArrayList<Integer>();
		lEffaceEffect= new ArrayList<Integer>();
		//INPUT 
		changeMouv=false;
		
		finPartie =false;
		inPause=false;


		firstNonFocused=true;

		xPositionSouris = 0 ;
		yPositionSouris = 0 ;

		///AFFICHAGE 

		INIT_RECT= new Point(50000,50000); //(abs,ord)
		xScreendisp = 0;
		yScreendisp = 0;
		
		//Reference A_Star and A_Star_Helper for the first time to load the class once so that referencing them later is faster (~6 ms saved)
		A_Star astar = new A_Star();
		A_Star_Helper astarHelper = new A_Star_Helper(1,1,new Point(1,1),1,1);
				
	}
	public void resetVariablesAffichage()
	{
		disableBoutonsFin=false;
		setAffichageOption=false;
		arrowSlotIconChanged =true;
	}
	/**
	 * Translate an object to the position and rotate it around its center
	 * @param pos : position of hitbox (global coordinates)
	 * @param anchor: position of center of rotation relative to top left of hitbox
	 * @param taille: size of hitbox
	 * @param rotation
	 * @return
	 */
	public static AffineTransform getRotatedTransform(Point pos,Point topLeftAnchor,double rotation)
	{
		AffineTransform trans = new AffineTransform();
		//Translate the object to its position
		trans.translate(pos.x, pos.y);
		//rotate it around its center
		if(topLeftAnchor==null)
			trans.rotate(rotation);
		else
			trans.rotate(rotation, topLeftAnchor.x, topLeftAnchor.y);

		return trans;
	}

	public abstract void HandleBoutonsPressed(ActiveJButton button);

	public abstract void startPartie(int typeDeSpawn);
	public abstract void play(GameRenderer affich) ;
	
	public abstract void precomputeDraw();
	public abstract void drawPartie(Graphics g);

	public abstract void drawMonde(boolean drawHitbox);
	public abstract void drawMonstres(boolean drawHitbox);
	/**
	 * @param pos : position of hitbox
	 * @param anchor: position of center of rotation relative to top left of hitbox
	 * @param taille: size of hitbox
	 * @param rotation
	 * @return
	 */
	
	//public static abstract AffineTransform getRotatedTransform(Point pos, Point topLeftAnchor, double rotation);
	public abstract void drawPerso(boolean drawHitbox);
	public abstract void drawFleches(boolean drawHitbox);
	public abstract void drawTirMonstres(boolean drawHitbox);
	public abstract void drawEffects(boolean drawHitbox);
	public abstract void drawInterface();
	
	public abstract BufferedImage apply_width_mask(BufferedImage original,BufferedImage previousMaskedIm, int w_start, int last_start,float transparency);
	public abstract BufferedImage apply_height_mask(BufferedImage original,BufferedImage previousMaskedIm, int h_start_mask,float transparency);

	public abstract BufferedImage toBufferedImage(Image img);

	//Implémentation du pattern observer
	public void addObserver(Observer obs) {
		this.listObserver.add(obs);
	}
	public void notifyObserver() {
		for(Observer obs : listObserver)
			obs.update();
	}
	public void forceRepaint() {
		gameHandler.forceRepaint();
		notifyObserver();
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
