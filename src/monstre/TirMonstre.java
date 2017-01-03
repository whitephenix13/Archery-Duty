package monstre;

import java.util.ArrayList;
import java.util.List;

import partie.AbstractModelPartie;
import types.Vitesse;

public abstract class TirMonstre {
	
	public int anim = 0; 
	public int xpos=0;
	public int ypos=0;
	String nomTir;
	public Vitesse vit = new Vitesse(0,0);
	public int dommage;
	public boolean doitDetruire= false;
	public int reaffiche=0; 
	
	/*0 gauche, 1: bas gauche ..... 7 haut gauche*/
	public List<Integer> xtaille= new ArrayList<Integer>(8) ;
	public List<Integer> ytaille= new ArrayList<Integer>(8) ;
	public List<Integer> xhitbox= new ArrayList<Integer>(8) ;
	public List<Integer> yhitbox= new ArrayList<Integer>(8) ;
	public List<Integer> xdecallsprite= new ArrayList<Integer>(8) ;
	public List<Integer> ydecallsprite= new ArrayList<Integer>(8) ;
	
	/**
	 * Règle la vitesse du tour   
	 * 
	 * @param anim, l'animation en cours du tir
	 */	
	public abstract void setSpeed(int anim) ;
	/**
	 * Gère l'ensemble des déplacements du tir  
	 * 
	 */
	public abstract void deplaceTir(AbstractModelPartie partie);
	/**
	 * Permet de déclencher des événements lorsque la fleche doit etre détruite (explosion, vent ...)   
	 * 
	 */
	public abstract void detruire();
	/**
	 * Ralenti les animations  
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le tir
	 */	
	public abstract void setReaffiche();

}
