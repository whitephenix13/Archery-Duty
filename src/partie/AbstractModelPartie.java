package partie;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import Affichage.Affichage;
import deplacement.Attente;
import deplacement.Deplace;
import monstre.ImagesMonstre;
import monstre.ImagesTirMonstre;
import monstre.Monstre;
import monstre.TirMonstre;
import music.MusicBruitage;
import observer.Observable;
import observer.Observer;
import personnage.Fleche;
import personnage.Heros;
import personnage.ImagesFleche;
import personnage.ImagesHeros;
import principal.InterfaceConstantes;
import types.Monde;
import types.TypeObject;

public abstract class AbstractModelPartie implements Observable {
	//frame flag 
	protected int frame = 0;
	public void nextFrame(){frame+=1;}
	public int getFrame(){return frame;}
	//repaint flag 
	public boolean computationDone=false;
	
	protected Deplace deplace = new Deplace();
	public Monde monde = new Monde();

	public Heros heros;

	//Action speciale pour ralentir le temps 
	public boolean slowDown=false;
	//public int slowDownFactor= 3;//6
	public int slowDownTir= 10;//6
	public int slowCount=0;

	public List<Fleche> tabFleche= new ArrayList<Fleche>();
	public List<TirMonstre> tabTirMonstre = new ArrayList<TirMonstre>();
	protected List<Monstre> tabMonstre= new ArrayList <Monstre> ();
	protected int nombreMonstreRestant= 0;

	//Permet de stocker les �l�ments de la liste � supprimer.On les supprimera � la frame suivante
	protected List<Integer> lEffaceFleche= new ArrayList<Integer>();
	protected List<Integer> lEffaceMonstre= new ArrayList<Integer>();
	protected List<Integer> lEffaceTirMonstre= new ArrayList<Integer>();

	//INPUT 
	//pressed
	protected boolean marcheDroiteDown ;
	protected boolean marcheGaucheDown ;
	protected boolean sautDown ;
	protected boolean toucheTirDown ;
	protected boolean courseDroiteDown;
	protected boolean courseGaucheDown;
	protected boolean toucheSlowDown;
	protected boolean pauseDown;
	//release
	protected boolean marcheDroiteReleased ;
	protected boolean marcheGaucheReleased ;
	protected boolean sautReleased ;
	protected boolean toucheTirReleased ;
	protected boolean courseDroiteReleased;
	protected boolean courseGaucheReleased;
	protected boolean toucheSlowReleased;
	protected boolean pauseReleased;

	//Variables de d�placement 
	//booleen pour savoir si on change de mouvement et donc qu'on doit reequilibre la hitbox du heros
	public boolean changeMouv=false;
	
	protected boolean finPartie =false;
	protected boolean inPause=false;
	public boolean getinPause(){return inPause;}
	protected float clickTime1;
	protected float clickTime2;

	protected boolean clickRight = false;
	protected boolean clickLeft=false;

	protected boolean firstNonFocused=true;


	//INPUT INTERPRETE 
	protected int xPositionSouris = 0 ;
	public int getXPositionSouris(){return xPositionSouris;}
	protected int yPositionSouris = 0 ;
	public int getYPositionSouris(){return yPositionSouris;}

	//}}
	///AFFICHAGE 
	//pour pouvoir acceder aux images charg�es 
	protected Monde m= new Monde("default");
	protected ImagesMonstre imMonstre =new ImagesMonstre();
	protected ImagesHeros imHeros = new ImagesHeros();
	protected ImagesTirMonstre imTirMonstre= new ImagesTirMonstre();
	protected ImagesFleche imFleches = new ImagesFleche();

	public Point INIT_RECT= new Point(50000,50000); //(abs,ord)
	//public int absRect =INIT_RECT.x;
	//public int ordRect = INIT_RECT.y;
	public int TAILLE_BLOC=100;
	//variable to know to displacement of the screen
	public int xScreendisp = 0;
	public int yScreendisp = 0;
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
	//entier permettant de d�placer l�g�rement l'ecran pour pas qu'il ne bouge par bloc(<100) 
	//public int xdeplaceEcran = 0;
	//public int ydeplaceEcran = 0;
	//entier stockant les centaines de d�callage (>100) 
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
		(new MusicBruitage()).initMusicBruitage();

		reset();
		resetVariablesAffichage();
	}

	public void reset() 
	{
		deplace = new Deplace();
		monde = new Monde();
		frame= 0;
		heros= new Heros(InterfaceConstantes.LARGEUR_FENETRE/2,InterfaceConstantes.HAUTEUR_FENETRE/2,1,new Attente(TypeObject.heros,Attente.attente_gauche,frame),frame);

		//Action speciale pour ralentir le temps 
		slowDown=false;
		slowCount=0;

		tabFleche= new ArrayList<Fleche>();
		tabTirMonstre = new ArrayList<TirMonstre>();
		tabMonstre= new ArrayList <Monstre> ();
		nombreMonstreRestant= 0;

		lEffaceFleche= new ArrayList<Integer>();
		lEffaceMonstre= new ArrayList<Integer>();
		lEffaceTirMonstre= new ArrayList<Integer>();

		//INPUT 
		//pressed
		marcheDroiteDown=false ;
		marcheGaucheDown =false ;
		sautDown =false ;
		toucheTirDown =false ;
		courseDroiteDown=false ;
		courseGaucheDown=false ;
		toucheSlowDown=false ;
		pauseDown=false ;
		//release
		marcheDroiteReleased =false ;
		marcheGaucheReleased =false ;
		sautReleased =false ;
		toucheTirReleased =false ;
		courseDroiteReleased=false ;
		courseGaucheReleased=false ;
		toucheSlowReleased=false ;
		pauseReleased=false ;

		changeMouv=false;
		
		finPartie =false;
		inPause=false;

		clickTime1=0;
		clickTime2=0;

		clickRight = false;
		clickLeft=false;

		firstNonFocused=true;

		xPositionSouris = 0 ;
		yPositionSouris = 0 ;

		///AFFICHAGE 
		//pour pouvoir acceder aux images charg�es 
		m= new Monde("default");
		if(imHeros==null)
			imHeros= new ImagesHeros();
		if(imMonstre==null)
			imMonstre =new ImagesMonstre();
		if(imTirMonstre==null)
			imTirMonstre= new ImagesTirMonstre();
		if(imFleches==null)
			imFleches = new ImagesFleche();

		INIT_RECT= new Point(50000,50000); //(abs,ord)
		xScreendisp = 0;
		yScreendisp = 0;
	}
	public void resetVariablesAffichage()
	{
		disableBoutonsFin=false;
		setAffichageOption=false;
	}

	public abstract void HandlePressedInput(int input);
	public abstract void HandleReleasedInput(int input);
	public abstract void HandleBoutonsPressed(JButton button);

	public abstract void keyDownAction ();
	public abstract void keyReleasedAction ();
	public abstract void keyAction();
	public abstract void startPartie(int typeDeSpawn,String nomPartie);
	public abstract void resetTouchesFocus();
	public abstract void play(Affichage affich) ;
	public abstract void charger(String nomFichier);

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
	public abstract void drawBar(Graphics g,int x, int y, int width, int height, int value ,Color background, Color foreground);
	//Impl�mentation du pattern observer
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
