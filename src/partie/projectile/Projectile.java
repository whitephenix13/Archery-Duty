package partie.projectile;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.modelPartie.AbstractModelPartie;
import utils.Vitesse;

public abstract class Projectile extends Collidable{
	
	protected float speedFactor = 1;
	
	public Projectile()
	{
		super();
	}
	
	@Override 
	public void deplaceOutOfScreen(AbstractModelPartie partie)
	{
		destroy(partie,true);
	}
	@Override
	public AffineTransform computeDrawTr(Point screenDisp)
	{
		return _computeDrawTr(true,screenDisp);
	}
	@Override
	public Vitesse getGlobalVit(){
		Vitesse vit = localVit.Copy();
		return vit.times(speedFactor);
	}
	public Vitesse getGlobalVit(AbstractModelPartie partie,Vitesse _localVit){
		return _localVit.times(speedFactor);
	}
	
	/***
	 * 
	 * @param screenReferential: true if computation is to display the object on screen, false for world referential (ie: for hitbox computation)
	 * @param screenDisp
	 * @return
	 */
	protected AffineTransform _computeDrawTr(boolean screenReferential, Point screenDisp){
		return _computeDrawTr(screenReferential,screenDisp,getRotation(),getScaling());
	}
	
	protected abstract AffineTransform _computeDrawTr(boolean screenReferential, Point screenDisp,double rotation,Vector2d scaling);
}
