package partie.collision;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Mouvement;
import utils.Vitesse;

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
		super();
		setXpos(center.x);
		setYpos(center.y);
		fixedWhenScreenMoves = relative;
		maxBound=_maxBound;
	}
	/*public static CustomBoundingSquare getScreen()
	{
		return getScreen(new Point(0,0));
	}*/
	/*public static CustomBoundingSquare getScreen(Point screenDisp)
	{
		return new CustomBoundingSquare(
				new Point( screenDisp.x+(InterfaceConstantes.BD_FENETRE.x + InterfaceConstantes.HG_FENETRE.x)/2, 
						screenDisp.y+(InterfaceConstantes.BD_FENETRE.y + InterfaceConstantes.HG_FENETRE.y)/2 ),
				true, 
				 ((InterfaceConstantes.BD_FENETRE.x - InterfaceConstantes.HG_FENETRE.x)/2));
	}*/
	@Override
	public Vitesse getGlobalVit() {
		
		return null;
	}

	@Override
	public Vector2d getNormCollision() {
		
		return null;
	}

	@Override
	public int getMaxBoundingSquare() {
		return maxBound;
	}
	@Override
	public Point getMaxBoundingRect() {
		return new Point(maxBound,maxBound);
	}
	
	@Override
	public AffineTransform computeDrawTr(Point screendisp)
	{
		return null;
	}
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		
		return null;
	}

	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp, Mouvement mouv, int mouv_index) {
		
		return null;
	}

	@Override
	public void handleWorldCollision(Vector2d normal,Collidable collidedObject,	boolean stuck) {
		
		
	}

	@Override
	public void handleObjectCollision(Collidable collider, Vector2d normal) {
		
		
	}

	@Override
	public void handleStuck() {
		
		
	}

	@Override
	public void memorizeCurrentValue() {
		
		
	}

	@Override 
	public void deplaceOutOfScreen()
	{
		//do nothing
	}
	@Override
	public void applyFriction(double minlocalSpeed, double minEnvirSpeed) {
		
		
	}

	@Override
	public void resetVarBeforeCollision() {
		
		
	}

	@Override
	public void handleDeplacementSuccess() {
		
		
	}

	@Override
	public void resetVarDeplace(boolean speedUpdated) {
		
		
	}

	@Override
	public void onDestroy() {
		
		
	}
	@Override
	protected void onStartDeplace(){}
	@Override
	protected void handleInputs() {
		
	}
	@Override
	protected boolean updateMouvementBasedOnPhysic() {
		return false;
	}
	@Override
	protected boolean updateNonInterruptibleMouvement() {
		return false;
	}
	@Override
	protected boolean updateMouvementBasedOnInput() {
		return false;
	}
	@Override
	protected boolean updateMouvementBasedOnAnimation() {
		return false;
	}
	@Override
	protected void resetInputState() {
		
	}
	@Override
	protected void onMouvementChanged(boolean animationChanged, boolean mouvementChanged) {
		
	}
}
