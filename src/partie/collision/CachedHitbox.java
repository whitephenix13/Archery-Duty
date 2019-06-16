package partie.collision;

import java.awt.Point;

import partie.deplacement.Mouvement;
import partie.modelPartie.AbstractModelPartie;

public class CachedHitbox extends CachedObject<Hitbox>{

	public CachedHitbox(Collidable _parent) {
		super(_parent);
		valueChangedStrategy = new boolean[6];
		valueChangedStrategy[ANIM]=true;
		valueChangedStrategy[DEPLACEMENT]=true;
		valueChangedStrategy[HITBOX]=true;
		valueChangedStrategy[POS]=true;
		valueChangedStrategy[ROTATION]=true;
		valueChangedStrategy[SCREENDISP]=true;

	}
	public CachedHitbox(CachedHitbox obj)
	{
		super(obj);
		this.cachedObject = obj.cachedObject.copy();
	}
	
	
	//Create this so that getObject can get less parameters 
	public Hitbox getObject(Point INIT_RECT, Point currentScreendisp)
	{
		return _getObject(INIT_RECT,currentScreendisp);
	}
	public Hitbox getObject(Point INIT_RECT, Point currentScreendisp,Mouvement mouv, int _anim)
	{
		return parent.computeHitbox(INIT_RECT, currentScreendisp, mouv, _anim);
	}
	
	@Override
	protected Hitbox computeObject(Point INIT_RECT, Point currentScreendisp) {
		return parent.computeHitbox(INIT_RECT, currentScreendisp);
	}

	@Override
	public CachedHitbox copy() {
		return new CachedHitbox(this);
	}
	


}
