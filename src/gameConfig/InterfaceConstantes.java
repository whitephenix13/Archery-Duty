package gameConfig;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import debug.DebugTime.PrintMode;
import partie.collision.Hitbox;


public interface InterfaceConstantes {
	static final String VERSION ="0.2.5"; //0.3 when all arrows implemented
	
	static final boolean SHOW_FPS = true;
	
	static final boolean IGNORE_SOUND = true;
	
	static final boolean DEBUG_OBJECT_CREATION = false;
	
	static final int DEBUG_GC_VERBOSE=0;
	
	static final int DEBUG_TIME_LOOP_TO_SLOW = 20;//ms
	static final int DEBUG_TIME_ACTION_TO_SLOW = 2;//ms
	static final int DEBUG_TIME_VERBOSE=1;
	static final PrintMode DEBUG_TIME_PRINT_MODE = PrintMode.NONE;
	
	static final int DEBUG_TIME_AFFICHAGE_LOOP_TO_SLOW = 4;//ms
	static final int DEBUG_TIME_AFFICHAGE_ACTION_TO_SLOW = 1;//ms
	static final int DEBUG_TIME_AFFICHAGE_VERBOSE=5;
	static final PrintMode DEBUG_TIME_AFFICHAGE_PRINT_MODE = PrintMode.NONE;
	
	static final boolean DEBUG_CACHED_OBJECT = false;
	
	static final boolean DRAW_HITBOX_MONDE = false;
	static final boolean DRAW_HITBOX_PERSO = true;
	static final boolean DRAW_HITBOX_MONSTRES = false;
	static final boolean DRAW_HITBOX_FLECHES = true;
	static final boolean DRAW_HITBOX_TIR_MONSTRES = true;
	static final boolean DRAW_HITBOX_EFFECTS = false;
	
	//taille maximum de la matrice 
	static final int ABS_MAX = 1000;
	static final int ORD_MAX = 1000;
	static final int TAILLE_BLOC=100; // taille de référence d'un bloc
	
	//nombre de fleches max: 
	static final int NB_FLECHES_MAX = 50;
	static final long FLECHE_TIR_COOLDOWN = (long) ( 25* Math.pow(10, 6));//nanos, 50ms

	
	static final int NB_MONSTRES_MAX=100;
	static final int SLOW_DOWN_FACTOR = 4;//10 looks fun :p
	//type de spawn
	int SPAWN_ALEATOIRE = 0; 
	int SPAWN_PROGRAMME = 1;
	
	Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	
	static int WINDOW_WIDTH =  (int)screenSize.getWidth();//1366
	static int WINDOW_HEIGHT =  (int)screenSize.getHeight();//768
	
	//limites à partir desquelles on ne s'interesse plus aux objets:
	final int onScreenTolerance = 250;	
	static Point BD_FENETRE = new Point(WINDOW_WIDTH + onScreenTolerance,WINDOW_HEIGHT+onScreenTolerance);
	static Point HG_FENETRE= new Point(-onScreenTolerance,-onScreenTolerance);
	static Point BG_FENETRE= new Point(-onScreenTolerance,WINDOW_HEIGHT+onScreenTolerance);
	static Point HD_FENETRE= new Point(WINDOW_WIDTH + onScreenTolerance,-onScreenTolerance);
	static Hitbox SCREEN =new Hitbox(InterfaceConstantes.HG_FENETRE,InterfaceConstantes.BG_FENETRE,
									InterfaceConstantes.BD_FENETRE,InterfaceConstantes.HD_FENETRE);
	
	static final boolean MOVE_SCREEN_WHEN_HEROS_MOVES = false;
	
	static final Color BACKGROUND_COLOR = Color.BLACK;
	static final int SCREEN_FADE_OUT_TIME = 204; //204ms

	final static long T_DOUBLE_TAP= 150;
	
	//durée d'invincibilité lorsque le héros est touché 
	static final long INV_TOUCHE = 1000;
	//fréquence à laquelle il clignote
	static final long CLIGNOTE= 50;
	
	final float MAXSEYERI= 100;
	static final float MINSEYERI= 0;
	
	static final double FRICTION = 0.1;
	static final double AIRFRICTION = 0.05;

	//vitesse a laquelle le seyeri varie: toute les 100ms
	static long TEMPS_VAR_SEYERI=100; 
	
	static final int NOMBRE_BRUITAGE_MAX= 100;
	
	//choix des musiques 
	static final String musiqueOption= "jvdLevel1";
	static final String musiquePrincipal= "jvdLevel1";
	static final String[] musiquePartie= {"rockItBaby","bossRush2"};
	static final String musiqueEditeur = "bossRush";
	
	static final String[] musiqueSlow={"rockItBaby_s","bossRush2_s"};
	//Ensemble des bruitages
	static final String[] bruitagesArray = {"annulation tir","arc","laser","destruction robot","vent_effect"};
	
}
