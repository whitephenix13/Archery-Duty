package collision;

import java.awt.Point;

import javax.vecmath.Vector2d;

import partie.AbstractModelPartie;
import types.Hitbox;
import types.Vitesse;
import deplacement.Deplace;
import deplacement.Mouvement;

//Specify that an object can enter in collision. 
//Store the information needed to manage a collision
public abstract class Collidable {
	
	public int xpos; 
	public int ypos; 
	public int anim; 
	public Mouvement deplacement;
	public int reaffiche;
	
	public boolean needDestroy=false;
	public Vitesse vit;
	
	public int slowDownFactor; 
	public boolean fixedWhenScreenMoves ; //true : not influenced by screen displacement (ie: use for the hero)
	
	protected CurrentValue currentValue;
	public boolean useGravity=false;
	
	public abstract Hitbox getHitbox(Point INIT_RECT);
	public abstract Hitbox getHitbox(Point INIT_RECT,Mouvement mouv, int _anim);

	public abstract void handleWorldCollision(Vector2d normal,AbstractModelPartie partie,Deplace deplace);
	public abstract void handleObjectCollision(AbstractModelPartie partie,Deplace deplace);
	/**
	 * Function used in Deplace to get back to  the previous correct posistion if stuck
	 */
	public abstract void memorizeCurrentValue();
	/**
	 * 
	 * @param partie
	 * @param deplace
	 * @return True if the object needs to be moved
	 */
	public abstract boolean deplace(AbstractModelPartie partie, Deplace deplace);
	//Use the function trick to memorize the reset values
	protected class CurrentValue{public void res(){};}
	public abstract void applyFriction(int minSpeed);
	public abstract void resetVarBeforeCollision();
	public abstract void handleStuck(AbstractModelPartie partie,Deplace deplace);
	public abstract void resetVarDeplace();
	public abstract int setReaffiche();

	public abstract void destroy();
	
		
	
}
