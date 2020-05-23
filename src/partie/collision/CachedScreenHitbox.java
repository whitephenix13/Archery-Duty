package partie.collision;

import java.awt.Point;

import gameConfig.InterfaceConstantes;
import partie.collision.CachedObject.CachedParameter;
import partie.mouvement.Mouvement;

public class CachedScreenHitbox extends CachedObject<Hitbox>{

	public CachedScreenHitbox() {
		super();
		valueChangedStrategy = new boolean[8];
		valueChangedStrategy[CachedParameter.FORCED.getIndex()]=false;
		valueChangedStrategy[CachedParameter.MOUV_INDEX.getIndex()]=false;
		valueChangedStrategy[CachedParameter.MOUVEMENT.getIndex()]=false;
		valueChangedStrategy[CachedParameter.HITBOX.getIndex()]=false;
		valueChangedStrategy[CachedParameter.POS.getIndex()]=false;
		valueChangedStrategy[CachedParameter.ROTATION.getIndex()]=false;
		valueChangedStrategy[CachedParameter.SCALING.getIndex()]=false;
		valueChangedStrategy[CachedParameter.SCREENDISP.getIndex()]=true;
	}
	public CachedScreenHitbox(Collidable _parent) {
		this();
	}
	public CachedScreenHitbox(CachedScreenHitbox obj)
	{
		super(obj);
		if(obj.cachedObject!=null)
			this.cachedObject = obj.cachedObject.copy();
	}
	
	
	//Create this so that getObject can get less parameters 
	public Hitbox getObject(Point currentScreendisp)
	{
		return _getObject(null,currentScreendisp);
	}
	public Hitbox getObject(Point INIT_RECT, Point currentScreendisp,Mouvement mouv, int mouv_index)
	{
		return null;
	}
	
	@Override
	protected Hitbox computeObject(Point INIT_RECT, Point currentScreendisp) {
		return InterfaceConstantes.SCREEN.copy().translate(-currentScreendisp.x,-currentScreendisp.y);
	}

	@Override
	public CachedScreenHitbox copy() {
		return new CachedScreenHitbox(this);
	}
	
}
