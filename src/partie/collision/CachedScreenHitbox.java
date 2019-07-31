package partie.collision;

import java.awt.Point;

import gameConfig.InterfaceConstantes;
import partie.deplacement.Mouvement;

public class CachedScreenHitbox extends CachedObject<Hitbox>{

	public CachedScreenHitbox() {
		super();
		valueChangedStrategy = new boolean[6];
		valueChangedStrategy[ANIM]=false;
		valueChangedStrategy[DEPLACEMENT]=false;
		valueChangedStrategy[HITBOX]=false;
		valueChangedStrategy[POS]=false;
		valueChangedStrategy[ROTATION]=false;
		valueChangedStrategy[SCREENDISP]=true;
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
	public Hitbox getObject(Point INIT_RECT, Point currentScreendisp,Mouvement mouv, int _anim)
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
