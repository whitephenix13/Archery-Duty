package monstre;

import collision.Collidable;
import partie.AbstractModelPartie;
import types.Hitbox;

public abstract class TirMonstre extends Collidable{
	
	String nom_tir;
	public int dommage;
		
	/**
	 * Règle la vitesse du tour   
	 * 
	 * @param anim, l'animation en cours du tir
	 */	
	public abstract void setSpeed(int anim) ;

	/**
	 * Ralenti les animations  
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le tir
	 */	
	public abstract int setReaffiche();
	public abstract Hitbox getWorldHitbox(AbstractModelPartie partie);

}
