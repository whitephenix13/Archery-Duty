package principal;
import java.awt.Dimension;
import java.awt.Point;

import types.Hitbox;


public interface InterfaceConstantes {
	static String VERSION ="0.0.0";
	//taille maximum de la matrice 
	static int ABS_MAX = 1000;
	static int ORD_MAX = 1000;
	static int TAILLE_BLOC=100; // taille de référence d'un bloc
	
	//nombre de fleches max: 
	static int NB_FLECHES_MAX = 50;
	static long TEMPS_DESTRUCTION_FLECHE = (long) Math.pow(10, 9);//nanos, 1sec 
	
	static int NB_MONSTRES_MAX=100;
	
	//type de spawn
	int SPAWN_ALEATOIRE = 0; 
	int SPAWN_PROGRAMME = 1;
	
	static int loopTime = 3600000; // 1 heure
	 Dimension tailleEcran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	
	
	static int LARGEUR_FENETRE =  (int)tailleEcran.getWidth();//1366
	static int HAUTEUR_FENETRE =  (int)tailleEcran.getHeight();//768
	
	//limites à partir desquelles on ne s'interesse plus aux objets:
	
	static Point BD_FENETRE = new Point(LARGEUR_FENETRE + 200,HAUTEUR_FENETRE+200);
	static Point HG_FENETRE= new Point(-200,-200);
	static Point BG_FENETRE= new Point(-200,HAUTEUR_FENETRE+200);
	static Point HD_FENETRE= new Point(LARGEUR_FENETRE + 200,-200);
	static Hitbox SCREEN =new Hitbox(InterfaceConstantes.HG_FENETRE,InterfaceConstantes.BG_FENETRE,
									InterfaceConstantes.BD_FENETRE,InterfaceConstantes.HD_FENETRE);
		
	
	//permet de determiner les vitesses de chute, de deplacement et de saut 
	long T= 17; // temps de raffraichissement en milliseconde
	long TDash= 150;
	//durée d'invincibilité lorsque le héros est touché 
	static long INV_TOUCHE = 3000;
	//fréquence à laquelle il clignote
	static long CLIGNOTE= 50;
	
	static int MAXLIFE= 100;
	static int MINLIFE= 0;
	static int MAXSPE= 100;
	static int MINSPE= 0;
	static double FRICTION = 0.1;
	
	//vitesse a laquelle la spe diminue de 1 lors du slow down//augmente de 1 en dehors du slow down
	static long TEMPS_VAR_SPE=100; 
	
	int NOMBRE_BRUITAGE_MAX= 100;
	
	//choix des musiques 
	static String musiqueOption= "jvdLevel1";
	static String musiquePrincipal= "jvdLevel1";
	static String[] musiquePartie= {"rockItBaby","bossRush2"};
	static String musiqueEditeur = "bossRush";
	
	static String[] musiqueSlow={"rockItBaby_s","bossRush2_s"};
	//Ensemble des bruitages
	static String[] bruitagesArray = {"annulation tir","arc","laser","destruction robot"};
	
}
