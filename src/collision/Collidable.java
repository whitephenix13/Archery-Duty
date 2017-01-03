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

	public Vitesse vit;
	
	public int slowDownFactor; 
	public boolean fixedWhenScreenMoves ; //true : not influenced by screen displacement (ie: use for the hero)

	public abstract Hitbox getHitbox(Point INIT_RECT);
	public abstract Hitbox getHitbox(Point INIT_RECT,Mouvement mouv, int _anim);

	public abstract void handleCollision(Vector2d normal,AbstractModelPartie partie,Deplace deplace);
}
