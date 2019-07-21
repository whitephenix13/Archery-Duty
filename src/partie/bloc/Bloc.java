package partie.bloc;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import images.ImagesContainer.ImageInfo;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Deplace;
import partie.deplacement.Mouvement;
import partie.modelPartie.AbstractModelPartie;
import utils.Vitesse;

public class Bloc extends Collidable{

	public static enum TypeBloc implements ImageInfo{NONE,CIEL,DELETE,END,LOUPE,PERSO,SOL,SOURIS,SPIREL,START,TERRE };
	public static enum BlocImModifier implements ImageInfo{LOUPE};

	private TypeBloc type;
	private boolean bloquer = false;
	private boolean background=false;

	public Bloc (){
		this(TypeBloc.NONE,-1,-1,false,false);
	}
	public Bloc(TypeBloc type, int x, int y , boolean bl, boolean back) {
		super();
		
		this.type=type;
		this.setXpos_sync(x);
		this.setYpos_sync(y);
		this.setDeplacement(new partie.deplacement.bloc.Idle());
		localVit= new Vitesse(0,0);
		fixedWhenScreenMoves=false;
		this.bloquer=bl;
		this.background=back;
		this.setCollideWithAll();
		
	}

	//mutateur
	public void setType(TypeBloc type){
		this.type=type;
	}

	public void setPos(int x,int y){
		setXpos_sync(x);
		setYpos_sync(y);
	}

	public void setBloquer(boolean bloque){
		this.bloquer=bloque;
	}
	public void setBackground(boolean back){
		this.background=back;
	}


	//accesseur
	public TypeBloc getType(){
		return(this.type);
	}

	public boolean getBloquer(){
		return(this.bloquer);
	}
	public boolean getBack(){
		return(this.background);
	}

	public Vector2d getNormCollision()
	{
		return null;
	}
	
	@Override
	public int getMaxBoundingSquare()
	{
		return getDeplacement().getMaxBoundingSquare();
	}
	@Override
	public Point getMaxBoundingRect()
	{
		return getDeplacement().getMaxBoundingRect();
	}
	@Override
	public AffineTransform computeDrawTr(Point screendisp)
	{
		return null;
	}
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		if(!bloquer)
			return null;

		int xposlocal = (getXpos()-INIT_RECT.x);
		int yposlocal = (getYpos()-INIT_RECT.y);
		Hitbox hit = this.getDeplacementHitbox(0);
		hit = Hitbox.plusPoint(hit, new Point(xposlocal,yposlocal), true);
		return hit;

	}
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp, Mouvement mouv, int _anim) {
		return computeHitbox(INIT_RECT,screenDisp);
	}
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {
		//no collision handle: this bloc si currently static which means that the only objects that can collides with it are
		//mobile objects. We'll rather call the handleCollision of the mobile objects 
	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal) {
		try {
			throw new Exception("Calling handleObjectCollision for a bloc is forbidden");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void memorizeCurrentValue() {
		//do nothing
	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		//Do nothing
		boolean[] res = {false,false};
		return res;
	}
	@Override
	public void applyFriction(double minLocalspeed, double minEnvirSpeed) {
		//do nothing
	}
	@Override
	public void resetVarBeforeCollision() {
		//do nothing
	}
	@Override
	public void handleStuck(AbstractModelPartie partie) {
		//do nothing
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {

	}
	@Override
	public void resetVarDeplace(boolean speedUpdated) {
		//do nothing
	}
	@Override
	public void onDestroy(AbstractModelPartie partie) {
		//do nothing
	}
	@Override
	public Vitesse getGlobalVit(AbstractModelPartie partie) {
		return null;
	}
	@Override
	public Hitbox getNextEstimatedHitbox(AbstractModelPartie partie,double newRotation,int anim)
	{
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}
}
