package partie.collision;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import partie.collision.CachedObject.CachedParameter;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Mouvement;

public class CachedAffineTransform extends CachedObject<AffineTransform>{

	public CachedAffineTransform(Collidable _parent) {
		super(_parent);
		valueChangedStrategy = new boolean[8];
		valueChangedStrategy[CachedParameter.FORCED.getIndex()]=true;
		valueChangedStrategy[CachedParameter.MOUV_INDEX.getIndex()]=true;
		valueChangedStrategy[CachedParameter.MOUVEMENT.getIndex()]=true;
		valueChangedStrategy[CachedParameter.HITBOX.getIndex()]=false;
		valueChangedStrategy[CachedParameter.POS.getIndex()]=true;
		valueChangedStrategy[CachedParameter.ROTATION.getIndex()]=true;
		valueChangedStrategy[CachedParameter.SCALING.getIndex()]=true;
		valueChangedStrategy[CachedParameter.SCREENDISP.getIndex()]=true;
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
