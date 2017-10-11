package partie;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import Affichage.Affichage;
import collision.Collidable;
import debug.DebugDraw;
import deplacement.Deplace;
import effects.Effect;
import images.ImagesCondition;
import images.ImagesEffect;
import images.ImagesFleche;
import images.ImagesFlecheIcon;
import images.ImagesHeros;
import images.ImagesMonde;
import images.ImagesMonstre;
import images.ImagesTirMonstre;
import loading.DisplayLoader;
import music.MusicBruitage;
import observer.Observable;
import observer.Observer;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Entitie;
import types.Monde;
import types.Projectile;
import types.Touches;

public abstract class AbstractModelPartie extends DisplayLoader implements Observable {
	//frame flag 
	protected int frame = 0;
	public void nextFrame(){frame+=1;}
	public int getFrame(){return frame;}
	//repaint flag 
	public boolean computationDone=false;
	
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
	public List<Entitie> tabMonstre= new ArrayList <Entitie> ();
	
	public List<Collidable> arrowsEffects = new ArrayList<Collidable>();
	
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
	public ImagesMonde imMonde= new ImagesMonde();
	public ImagesMonstre imMonstre= new ImagesMonstre();
	public ImagesHeros imHeros= new ImagesHeros();
	public ImagesTirMonstre imTirMonstre= new ImagesTirMonstre();
	public ImagesFleche imFleches = new ImagesFleche();
	public ImagesEffect imEffect= new ImagesEffect();
	public ImagesCondition imConditions= new ImagesCondition();
	public ImagesFlecheIcon imFlecheIcon = new ImagesFlecheIcon();
	
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
	//entier permettant de déplacer légérement l'ecran pour pas qu'il ne bouge par bloc(<100) 
	//public int xdeplaceEcran = 0;
	//public int ydeplaceEcran = 0;
	//entier stockant les centaines de décallage (>100) 
	//public int xdeplaceEcranBloc = 0;
	//public int ydeplaceEcranBloc = 0;

	//variables pour l'affichage 
	protected boolean disableBoutonsFin=false;
	protected boolean setAffichageOption=false; 

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
		super.reset();
		PartieTimer.init();

		deplace = new Deplace();
		if(monde==null)
			monde = new Monde();
		frame= 0;
		heros= new Heros(InterfaceConstantes.LARGEUR_FENETRE/2,InterfaceConstantes.HAUTEUR_FENETRE/2,1,frame);

		//Action speciale pour ralentir le temps 
		slowDown=false;
		slowCount=0;

		tabFleche= new ArrayList<Projectile>();
		tabTirMonstre = new ArrayList<Projectile>();
		tabMonstre= new ArrayList <Entitie> ();
		arrowsEffects = new ArrayList<Collidable>();
		nombreMonstreRestant= 0;

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
		//pour pouvoir acceder aux images chargées 
		//loadImages();

		INIT_RECT= new Point(50000,50000); //(abs,ord)
		xScreendisp = 0;
		yScreendisp = 0;
				
	}
	public void resetVariablesAffichage()
	{
		disableBoutonsFin=false;
		setAffichageOption=false;
	}

	//public abstract void HandlePressedInput(int input);
	//public abstract void HandleReleasedInput(int input);
	public abstract void HandleBoutonsPressed(JButton button);

	public abstract void startPartie(int typeDeSpawn);
	public abstract void play(Affichage affich) ;

	public abstract void drawPartie(Graphics g,JPanel pan);

	public abstract void drawMonde(Graphics g,boolean drawHitbox);
	public abstract void drawMonstres(Graphics g,boolean drawHitbox);
	/**
	 * @param pos : position of hitbox
	 * @param anchor: position of center of rotation relative to top left of hitbox
	 * @param taille: size of hitbox
	 * @param rotation
	 * @return
	 */
	public abstract AffineTransform getRotatedTransform(Point pos, Point anchor, Point taille, double rotation);
	public abstract void drawPerso(Graphics g,boolean drawHitbox);
	public abstract void drawFleches(Graphics g,boolean drawHitbox);
	public abstract void drawTirMonstres(Graphics g,boolean drawHitbox);
	public abstract void drawEffects(Graphics g,JPanel pan,boolean drawHitbox);
	public abstract void drawInterface(Graphics g);
	public abstract void drawHitbox(Graphics g,int xdraw, int draw,int width, int height);
	public abstract void drawHitbox(Graphics g,int xdraw, int draw,int width, int height,Integer angle);
	public abstract void drawHitbox(Graphics g,int xdraw, int draw,int width, int height,Integer angle,Point origine);
	public abstract void drawBar(Graphics g,int number_rectangles, int[] x, int[] y, int[] width, int[] height,Color[] colors);
	
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
