package deplacement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MouvementFleche 
{
	//comme la hit box peut être de travers (mais quelle reste rectangulaire) , il faut différentier les positions hautes et basses
	//les animations partent du haut et tourne dans le sens des aiguilles d'une montre :
	/**
	 * 0: H
	 * 1: HD
	 * 2: D
	 * 3: BD
	 * 4: B
	 * 5: BG
	 * 6: G 
	 * 7: HG
	 * 
	 * 
	 * à partir de la position x et y de la fleches, on obtient les 4 points délimitant la hitbox de la manière suivante: 
	 *  Les valeurs peuvent donc être négatives en fonction de l'orientation de la flèche 
	 *  
	 *HG : xHG = x + xHdecallsprite ; 				yHG= y + yHDecallSprite
	 *HD : xHD = x + xHdecallsprite + xHdecall2 ; 	yHD= y + yHDecallSprite + yHdecall2
	 *BG : xBG = x + xBdecallsprite ;			 	yBG= y + yBDecallSprite
	 *BD : xBD = x + xBdecallsprite + xBdecall2 ; 	yBD= y + yBDecallSprite + yBdecall2
	 * */
	
	public List<Integer> xtaille= new ArrayList<Integer>(8) ;
	public List<Integer> ytaille= new ArrayList<Integer>(8) ;
	
	public List<Integer> xHdecallsprite= new ArrayList<Integer>(8) ;
	public List<Integer> xBdecallsprite= new ArrayList<Integer>(8) ;
	public List<Integer> yHdecallsprite= new ArrayList<Integer>(8) ;
	public List<Integer> yBdecallsprite= new ArrayList<Integer>(8) ;
	
	public List<Integer> xHdecall2= new ArrayList<Integer>(8) ;
	public List<Integer> xBdecall2= new ArrayList<Integer>(8) ;
	public List<Integer> yHdecall2= new ArrayList<Integer>(8) ;
	public List<Integer> yBdecall2= new ArrayList<Integer>(8) ;
	

	public int xHdecallFleche = 38;
	public int yHdecallFleche = -18;
	
	public int xHDdecallFleche = 32; 
	public int yHDdecallFleche = 4; 
	
	public int xDdecallFleche = 37; 
	public int yDdecallFleche = 29; 
	
	public int xBDdecallFleche =45; 
	public int yBDdecallFleche =23; 
	
	public int xBdecallFleche =33; 
	public int yBdecallFleche =60; 
	
	public int xBGdecallFleche =6; 
	public int yBGdecallFleche =22;
	
	public int xGdecallFleche =-1; 
	public int yGdecallFleche =27; 
	
	public int xHGdecallFleche =4; 
	public int yHGdecallFleche =2;
	public void initTableau(int[] tab, int v1, int v2, int v3, int v4, int v5, int v6, int v7, int v8){
		tab[0]=v1;
		tab[1]=v2;
		tab[2]=v3;
		tab[3]=v4;
		tab[4]=v5;
		tab[5]=v6;
		tab[6]=v7;
		tab[7]=v8;
	}
	
	public MouvementFleche() 
	{

		//GRANDE FLECHE 
		/*
		int xHG =fleche.xpos+ fleche.deplacement.xHdecallsprite[fleche.anim]
		int xHD =fleche.xpos + fleche.deplacement.xHdecallsprite[fleche.anim] + fleche.deplacement.xHdecall2[fleche.anim]
		int xBG =fleche.xpos + fleche.deplacement.xBdecallsprite[fleche.anim]
		int xBD= fleche.xpos + fleche.deplacement.xBdecallsprite[fleche.anim] + fleche.deplacement.xBdecall2[fleche.anim]
		int yHG =fleche.ypos + fleche.deplacement.yHdecallsprite[fleche.anim]
		int yHD=fleche.ypos + fleche.deplacement.yHdecallsprite[fleche.anim] + fleche.deplacement.yHdecall2[fleche.anim]
		int yBG =fleche.ypos + fleche.deplacement.yBdecallsprite[fleche.anim]
		int yBD=fleche.ypos + fleche.deplacement.yBdecallsprite[fleche.anim] + fleche.deplacement.yBdecall2[fleche.anim]
	*/
		xtaille=Arrays.asList(10,37,46,37,10,37,46,37);
		xHdecallsprite=Arrays.asList(0 ,35,0 ,6 ,0 ,27,0 ,6 );
		xHdecall2=Arrays.asList(10,7 ,46,30,10,7 ,46,30);
		xBdecallsprite=Arrays.asList(0 ,0 ,0 ,0 ,0 ,-6 ,0 ,0);
		xBdecall2=Arrays.asList(10,7 ,46,30,10,7 ,46,30);
		ytaille=Arrays.asList(46,37,10,37,46,37,10,37);
		yHdecallsprite=Arrays.asList(0 ,-5,0 ,0 ,0 ,0 ,0 ,0);
		yHdecall2=Arrays.asList(0 ,7 ,0 ,30,0 ,7 ,0 ,30);
		yBdecallsprite=Arrays.asList(46,30,10,6 ,46,33,10,6);
		yBdecall2=Arrays.asList( 0 ,7 ,0 ,30,0 ,7 ,0 ,30);
	}
}
