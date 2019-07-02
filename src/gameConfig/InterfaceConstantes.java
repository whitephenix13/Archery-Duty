package gameConfig;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import debug.DebugTime.PrintMode;
import partie.collision.Hitbox;


public interface InterfaceConstantes {
	static String VERSION ="0.2.4";
	
	static boolean DEBUG_OBJECT_CREATION = false;
	
	static int DEBUG_GC_VERBOSE=1;
	
	static int DEBUG_TIME_LOOP_TO_SLOW = 15;//ms
	static int DEBUG_TIME_ACTION_TO_SLOW = 2;//ms
	static int DEBUG_TIME_VERBOSE=1;
	static PrintMode DEBUG_TIME_PRINT_MODE = PrintMode.PRINT_ONLY_SLOW_LOOP;
	
	static int DEBUG_TIME_AFFICHAGE_LOOP_TO_SLOW = 4;//ms
	static int DEBUG_TIME_AFFICHAGE_ACTION_TO_SLOW = 1;//ms
	static int DEBUG_TIME_AFFICHAGE_VERBOSE=1;
	static PrintMode DEBUG_TIME_AFFICHAGE_PRINT_MODE = PrintMode.NONE;
	
	//taille maximum de la matrice 
	static int ABS_MAX = 1000;
	static int ORD_MAX = 1000;
	static int TAILLE_BLOC=100; // taille de r�f�rence d'un bloc
	
	//nombre de fleches max: 
	static int NB_FLECHES_MAX = 50;
	long FLECHE_TIR_COOLDOWN = (long) ( 1* Math.pow(10, 8));//nanos, 0.1 sec 
	static long ARMED_MIN_TIME = (long) (1* Math.pow(10, 8));//nanos, 0.1 sec 
	static long WALL_JUMP_DISABLE_TIME = (long) (2* Math.pow(10, 8));//nanos, 0.2 sec 
	static long ACCROCHE_COOLDOWN = (long) (2* Math.pow(10, 8));
	
	static int NB_MONSTRES_MAX=100;
	static int SLOW_DOWN_FACTOR = 4;//10 looks fun :p
	//type de spawn
	int SPAWN_ALEATOIRE = 0; 
	int SPAWN_PROGRAMME = 1;
	
	static int loopTime = 3600000; // 1 heure
	Dimension tailleEcran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	
	static int WINDOW_WIDTH =  (int)tailleEcran.getWidth();//1366
	static int WINDOW_HEIGHT =  (int)tailleEcran.getHeight();//768
	
	//limites � partir desquelles on ne s'interesse plus aux objets:
	
	static Point BD_FENETRE = new Point(WINDOW_WIDTH + 200,WINDOW_HEIGHT+200);
	static Point HG_FENETRE= new Point(-200,-200);
	static Point BG_FENETRE= new Point(-200,WINDOW_HEIGHT+200);
	static Point HD_FENETRE= new Point(WINDOW_WIDTH + 200,-200);
	static Hitbox SCREEN =new Hitbox(InterfaceConstantes.HG_FENETRE,InterfaceConstantes.BG_FENETRE,
									InterfaceConstantes.BD_FENETRE,InterfaceConstantes.HD_FENETRE);
		
	static Color BACKGROUND_COLOR = Color.BLACK;
	static int SCREEN_FADE_OUT_TIME = 200; //ms

	//permet de determiner les vitesses de chute, de deplacement et de saut 
	long TDash= 150;
	//dur�e d'invincibilit� lorsque le h�ros est touch� 
	static long INV_TOUCHE = 1000;
	//fr�quence � laquelle il clignote
	static long CLIGNOTE= 50;
	
	static float MAXSEYERI= 100;
	static float MINSEYERI= 0;
	
	static double FRICTION = 0.1;
	static double AIRFRICTION = 0.05;

	//vitesse a laquelle le seyeri varie: toute les 100ms
	static long TEMPS_VAR_SEYERI=100; 
	
	int NOMBRE_BRUITAGE_MAX= 100;
	
	//choix des musiques 
	static String musiqueOption= "jvdLevel1";
	static String musiquePrincipal= "jvdLevel1";
	static String[] musiquePartie= {"rockItBaby","bossRush2"};
	static String musiqueEditeur = "bossRush";
	
	static String[] musiqueSlow={"rockItBaby_s","bossRush2_s"};
	//Ensemble des bruitages
	static String[] bruitagesArray = {"annulation tir","arc","laser","destruction robot","vent_effect"};
	
}
