package collision;

import java.awt.Point;

import javax.vecmath.Vector2d;

import deplacement.Deplace;
import deplacement.Mouvement;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Hitbox;
import types.Vitesse;

/**
 * Class used for the getCollidable functions. Maily use to test if a collidable is within a specific area 
 * */
public class CustomBoundingSquare extends Collidable{

	private int maxBound;
	/**
	 * @param center: center of the desired square box 
	 * @param relative: true if center is given in screen coordinate (between 0 and ~1400 ...) false otherwise
	 * @param _maxBound: length of the square box 
	 * */
	public CustomBoundingSquare(Point center, boolean relative, int _maxBound)
	{
		xpos = center.x;
		ypos = center.y;
		fixedWhenScreenMoves = relative;
		maxBound=_maxBound;
	}
	public static CustomBoundingSquare getScreen()
	{
		return getScreen(new Point(0,0));
	}
	public static CustomBoundingSquare getScreen(Point screenDisp)
	{
		return new CustomBoundingSquare(
				new Point( screenDisp.x+(InterfaceConstantes.BD_FENETRE.x + InterfaceConstantes.HG_FENETRE.x)/2, 
						screenDisp.y+(InterfaceConstantes.BD_FENETRE.y + InterfaceConstantes.HG_FENETRE.y)/2 ),
				true, 
				 ((InterfaceConstantes.BD_FENETRE.x - InterfaceConstantes.HG_FENETRE.x)/2));
	}
	@Override
	public Vitesse getGlobalVit(AbstractModelPartie partie) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector2d getNormCollision() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxBoundingSquare() {
		return maxBound;
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp, Mouvement mouv, int _anim) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleWorldCollision(Vector2d normal,
			AbstractModelPartie partie, Collidable collidedObject,
			boolean stuck) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Collidable collider, Vector2d normal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleStuck(AbstractModelPartie partie) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void memorizeCurrentValue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace, boolean update_with_speed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyFriction(double minlocalSpeed, double minEnvirSpeed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetVarBeforeCollision() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetVarDeplace(boolean speedUpdated) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy(AbstractModelPartie partie) {
		// TODO Auto-generated method stub
		
	}

}
