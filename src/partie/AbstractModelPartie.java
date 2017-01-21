package partie;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import monstre.ImagesMonstre;
import monstre.ImagesTirMonstre;
import monstre.Monstre;
import monstre.TirMonstre;
import music.MusicBruitage;
import observer.Observable;
import observer.Observer;
import personnage.Fleche;
import personnage.Heros;
import personnage.ImagesHeros;
import principal.InterfaceConstantes;
import types.Monde;
import Affichage.Affichage;
import deplacement.Attente;
import deplacement.Course;
import deplacement.Deplace;
import deplacement.Glissade;
import deplacement.Marche;
import deplacement.Mouvement;
import deplacement.Saut;
import deplacement.Tir;

public abstract class AbstractModelPartie implements Observable {
	//{{Variables	

	protected Deplace deplace = new Deplace();
	public Monde monde = new Monde();

	public Heros heros;

	//Action speciale pour ralentir le temps 
	public boolean slowDown=false;
	//public int slowDownFactor= 3;//6
	public int slowDownTir= 10;//6
	public int slowCount=0;

	protected List<Fleche> tabFleche= new ArrayList<Fleche>();
	public List<TirMonstre> tabTirMonstre = new ArrayList<TirMonstre>();
	protected List<Monstre> tabMonstre= new ArrayList <Monstre> ();
	protected int nombreMonstreRestant= 0;

	//Permet de stocker les éléments de la liste à supprimer.On les supprimera à la frame suivante
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

	//Variables de déplacement 
	//booleen pour savoir si on change de mouvement et donc qu'on doit reequilibre la hitbox du heros
	public boolean changeMouv=false;
	public boolean flecheEncochee = false;
	
	protected boolean finPartie =false;
	protected boolean inPause=false;

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
	//pour pouvoir acceder aux images chargées 
	protected Monde m= new Monde("default");
	protected ImagesMonstre imMonstre =new ImagesMonstre();
	protected ImagesHeros imHeros = new ImagesHeros();
	protected ImagesTirMonstre imTirMonstre= new ImagesTirMonstre();
	protected Fleche defaultFleche = new Fleche(true);

	public Point INIT_RECT= new Point(50000,50000); //(abs,ord)
	public int absRect =INIT_RECT.x;
	public int ordRect = INIT_RECT.y;
	public int TAILLE_BLOC=100;
	//entier permettant de déplacer légérement l'ecran pour pas qu'il ne bouge par bloc(<100) 
	public int xdeplaceEcran = 0;
	public int ydeplaceEcran = 0;
	//entier stockant les centaines de décallage (>100) 
	public int xdeplaceEcranBloc = 0;
	public int ydeplaceEcranBloc = 0;

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

		heros= new Heros(InterfaceConstantes.LARGEUR_FENETRE/2,InterfaceConstantes.HAUTEUR_FENETRE/2,1,new Attente());

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
		flecheEncochee = false;
		
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
		//pour pouvoir acceder aux images chargées 
		m= new Monde("default");
		imMonstre =new ImagesMonstre();
		imTirMonstre= new ImagesTirMonstre();
		defaultFleche = new Fleche(true);

		INIT_RECT= new Point(50000,50000); //(abs,ord)
		absRect =INIT_RECT.x;
		ordRect = INIT_RECT.y;
		xdeplaceEcran = 0;
		ydeplaceEcran = 0;
		xdeplaceEcranBloc = 0;
		ydeplaceEcranBloc = 0;
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
	public abstract void drawPerso(Graphics g,boolean drawHitbox);
	public abstract void drawFleches(Graphics g,boolean drawHitbox);
	public abstract void drawTirMonstres(Graphics g,boolean drawHitbox);
	public abstract void drawEffects(Graphics g,JPanel pan,boolean drawHitbox);
	public abstract void drawInterface(Graphics g);
	public abstract void drawHitbox(Graphics g,int xdraw, int draw,int width, int height);
	public abstract void drawHitbox(Graphics g,int xdraw, int draw,int width, int height,Integer angle);
	public abstract void drawHitbox(Graphics g,int xdraw, int draw,int width, int height,Integer angle,Point origine);
	public abstract void drawBar(Graphics g,int x, int y, int width, int height, int value ,Color background, Color foreground);
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
