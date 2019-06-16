package partie.collision;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import partie.deplacement.Mouvement;
import partie.modelPartie.AbstractModelPartie;

public class CachedAffineTransform extends CachedObject<AffineTransform>{

	public CachedAffineTransform(Collidable _parent) {
		super(_parent);
		valueChangedStrategy = new boolean[6];
		valueChangedStrategy[ANIM]=true;
		valueChangedStrategy[DEPLACEMENT]=true;
		valueChangedStrategy[HITBOX]=false;
		valueChangedStrategy[POS]=true;
		valueChangedStrategy[ROTATION]=true;
		valueChangedStrategy[SCREENDISP]=true;
	}
	public CachedAffineTransform(CachedAffineTransform obj)
	{
		super(obj);
		this.cachedObject = new AffineTransform(obj.cachedObject);
	}
	
	//Create this so that getObject can get less parameters 
	public AffineTransform getObject(Point currentScreendisp)
	{
		return _getObject(null,currentScreendisp);
	}
	
	@Override
	protected AffineTransform computeObject(Point INIT_RECT, Point currentScreendisp) {
		return parent.computeDrawTr(currentScreendisp);
	}
	@Override
	public CachedAffineTransform copy() {
		return new CachedAffineTransform(this);
	}
		

}
