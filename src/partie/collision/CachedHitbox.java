package partie.collision;

import java.awt.Point;

import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Mouvement;

public class CachedHitbox extends CachedObject<Hitbox>{

	public CachedHitbox(Collidable _parent) {
		super(_parent);
		valueChangedStrategy = new boolean[8];
		valueChangedStrategy[CachedParameter.FORCED.getIndex()]=true;
		valueChangedStrategy[CachedParameter.MOUV_INDEX.getIndex()]=true;
		valueChangedStrategy[CachedParameter.MOUVEMENT.getIndex()]=true;
		valueChangedStrategy[CachedParameter.HITBOX.getIndex()]=true;
		valueChangedStrategy[CachedParameter.POS.getIndex()]=true;
		valueChangedStrategy[CachedParameter.ROTATION.getIndex()]=true;
		valueChangedStrategy[CachedParameter.SCALING.getIndex()]=true;
		valueChangedStrategy[CachedParameter.SCREENDISP.getIndex()]=true;

	}
	public CachedHitbox(CachedHitbox obj)
	{
		super(obj);
		if(obj.cachedObject!=null)
			this.cachedObject = obj.cachedObject.copy();
	}
	
	
	//Create this so that getObject can get less parameters 
	public Hitbox getObject(Point INIT_RECT, Point currentScreendisp)
	{
		return _getObject(INIT_RECT,currentScreendisp);
	}
	public Hitbox getObject(Point INIT_RECT, Point currentScreendisp,Mouvement mouv, int mouv_index)
	{
		return parent.computeHitbox(INIT_RECT, currentScreendisp, mouv, mouv_index);
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
