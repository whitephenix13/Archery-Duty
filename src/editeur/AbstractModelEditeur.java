package editeur;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import editeur.BarreOutil.BarreOutil;
import editeur.Menu.menuEditeur;
import gameConfig.InterfaceConstantes;
import images.ImagesMonde;
import partie.bloc.Bloc.TypeBloc;
import partie.bloc.Monde;
import utils.observer.Observable;
import utils.observer.Observer;

public abstract class AbstractModelEditeur implements Observable{

	protected BarreOutil barreOut;
	protected menuEditeur menuEdit;
	
	protected int tailleBloc;  //taille d'un sprite de décors 
	protected float dezoomFactor;
	protected int xViewPort;
	protected int yViewPort;
	
	protected Monde monde ;
	protected List<StockageMonstre> tabEditeurMonstre;
	
	//Utilisé pour charger les images 
	public ImagesMonde imMonde;
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

	/*//creation des J Menu
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenu m_fichier = new JMenu(" Fichier   ");
	protected JMenu m_objet = new JMenu(" Objet   ");
	protected JMenu m_texture = new JMenu(" Texture ");
	protected JMenu m_bloc = new JMenu("Objet Bloquant");
	protected JMenu m_back = new JMenu("Objet en Background");
	
	protected JMenuItem m_charger = new JMenuItem("Charger");
	protected JMenuItem m_sauvegarder= new JMenuItem("Sauvegarder");
	
	protected JMenuItem m_informations = new JMenuItem(" Informations ");
	
	protected JMenuItem m_nouv = new JMenuItem("Nouveau monde");
	protected JMenuItem m_menuP = new JMenuItem("Menu principal");
	protected JMenuItem m_quit = new JMenuItem("Quitter");
	
	protected JMenuItem m_loupe= new JMenuItem("Loupe");
	protected JMenuItem m_souris= new JMenuItem("Souris");
	protected JMenuItem m_deleteItem= new JMenuItem("Delete");
	protected JMenuItem m_vide = new JMenuItem("Vide");
	protected JMenuItem m_sol = new JMenuItem("Sol");
	protected JMenuItem m_terre = new JMenuItem("Terre");
	protected JMenuItem m_ciel = new JMenuItem("Ciel");
	protected JMenuItem m_perso= new JMenuItem("Perso");
	protected JMenuItem m_start= new JMenuItem("Start");
	protected JMenuItem m_end= new JMenuItem("End");
	protected JMenuItem m_spirel= new JMenuItem("Spirel");
	
	protected JRadioButtonMenuItem r_bloquant =new JRadioButtonMenuItem("Objet bloquant");
	protected JRadioButtonMenuItem r_nonBloquant =new JRadioButtonMenuItem("Objet non bloquant");
	protected ButtonGroup gp1 =new ButtonGroup();
	
	
	protected JRadioButtonMenuItem r_isBackground =new JRadioButtonMenuItem("Objet a afficher en arriere plan");
	protected JRadioButtonMenuItem r_nonIsBackground =new JRadioButtonMenuItem("Objet a ne pas afficher en arriere plan");
	protected ButtonGroup gp2 =new ButtonGroup();*/

	protected boolean loupe; 
	//variables pour l'affichage 
	protected boolean repaint;
	protected boolean showMonsters;
	protected boolean showStaticMonsters;
	protected boolean showMessageDialog;
	protected String[] textMessageDialog ={"",""};
	protected int typeMessageDialog=-1;
	
	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
		
	public boolean getRepaint(){return repaint;}
	public boolean getShowMonsters(){return showMonsters;}
	public boolean getShowStaticMonsters(){return showStaticMonsters;}
	
	public void setBloquant(boolean _bloquant){	
		bloquant=_bloquant;
		}	
	public void setBackground(boolean _background){	
		background=_background;
		}
	
	public void init ()
	{
		tailleBloc=100;
		dezoomFactor=0.2f;
		xViewPort = (InterfaceConstantes.ABS_MAX-InterfaceConstantes.WINDOW_WIDTH/tailleBloc)*100/2;
		yViewPort = (InterfaceConstantes.ORD_MAX-InterfaceConstantes.WINDOW_HEIGHT/tailleBloc)*100/2;

		monde=new Monde();
		tabEditeurMonstre= new ArrayList <StockageMonstre> ();
				
		if(imMonde==null)
			imMonde= new ImagesMonde();
		
		texture=TypeBloc.VIDE;
				
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
		//on ne réinitialise pas les éléments du menu parce que sinon on perd tous les listeners 

		/*
		menuBar = new JMenuBar();
		m_fichier = new JMenu(" Fichier   ");
		m_objet = new JMenu(" Objet   ");
		m_texture = new JMenu(" Texture ");
		m_bloc = new JMenu("Objet Bloquant");
		m_back = new JMenu("Objet en Background");
		
		m_charger = new JMenuItem("Charger");
		m_sauvegarder= new JMenuItem("Sauvegarder");
		
		m_informations = new JMenuItem(" Informations ");
		
		m_nouv = new JMenuItem("Nouveau monde");
		m_menuP = new JMenuItem("Menu principal");
		m_quit = new JMenuItem("Quitter");
		
		m_loupe= new JMenuItem("Loupe");
		m_souris= new JMenuItem("Souris");
		m_deleteItem= new JMenuItem("Delete");
		m_vide = new JMenuItem("Vide");
		m_sol = new JMenuItem("Sol");
		m_terre = new JMenuItem("Terre");
		m_ciel = new JMenuItem("Ciel");
		m_perso= new JMenuItem("Perso");
		m_start= new JMenuItem("Start");
		m_end= new JMenuItem("End");
		m_spirel= new JMenuItem("Spirel");
		
		r_bloquant =new JRadioButtonMenuItem("Objet bloquant");
		r_nonBloquant =new JRadioButtonMenuItem("Objet non bloquant");
		gp1 =new ButtonGroup();
		
		
		r_isBackground =new JRadioButtonMenuItem("Objet a afficher en arriere plan");
		r_nonIsBackground =new JRadioButtonMenuItem("Objet a ne pas afficher en arriere plan");
		gp2 =new ButtonGroup();*/

		loupe= false; 
		
		resetVariablesAffichage();
		
		repaint=true;
		notifyObserver();

	}
	public abstract void moveViewPort(int xpos, int ypos);
	public abstract void releaseMoveViewport();
	public abstract void drawTerrain(int xpos, int ypos);
	public abstract void drawMonster(int xpos, int ypos);
	public abstract void drawSpecial(int xpos, int ypos);
	public abstract void draw(Graphics g,JPanel pan);
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
		repaint=false;
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
