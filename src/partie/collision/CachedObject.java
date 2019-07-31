package partie.collision;

import java.awt.Point;
import java.util.Arrays;

import partie.projectile.fleches.Fleche;

public abstract class CachedObject<T> {
	
	protected Collidable parent;
	
	public final static int ANIM =0; 
	public final static int DEPLACEMENT =1;
	public final static int HITBOX =2;
	public final static int POS =3;
	public final static int ROTATION =4;
	public final static int SCREENDISP =5;

	public Point cachedScreenDisp; //screen disp when the object was cached
	protected boolean[] valueChanged;
	protected boolean[] valueChangedStrategy;//which parameters to use to determine if the object is dirty

	protected T cachedObject; // cached tranformation used to draw the object on screen. This is not the same as the on used to compute hitbox (it is translated by screendisp)
	
	protected CachedObject()
	{
		valueChanged = new boolean[5];
		cachedObject=null; //force computation on first time 
		
		//WARNING : initialize cachedParametersStrategy when heriting from that class
	}
	public CachedObject(Collidable _parent)
	{
		this();
		this.parent=_parent;
	}
	public CachedObject(CachedObject<T> obj)
	{
		parent = obj.parent;//don't need a deep copy here because in case of memorizeCurrentValue reverting to original, parent will also be reverted
		if(obj.cachedScreenDisp!=null)
			cachedScreenDisp = new Point(obj.cachedScreenDisp);
		valueChanged = obj.valueChanged;
		valueChangedStrategy = obj.valueChangedStrategy;
		cachedObject = obj.cachedObject;
	}
	public void set(CachedObject<T> obj)
	{
		parent = obj.parent;//don't need a deep copy here because in case of memorizeCurrentValue reverting to original, parent will also be reverted
		cachedScreenDisp = obj.cachedScreenDisp;
		valueChanged = obj.valueChanged;
		valueChangedStrategy = obj.valueChangedStrategy;
		cachedObject = obj.cachedObject;
	}
	
	public void OnChangedHitbox(){valueChanged[HITBOX]=true;}
	public void OnChangedAnim(){valueChanged[ANIM]=true;}
	public void OnChangedRotation(){valueChanged[ROTATION]=true;}
	public void OnChangedDeplacement(boolean sameTypeOfDeplacement){valueChanged[DEPLACEMENT] = true;valueChanged[HITBOX]=true;}//as hitbox are protected; we assume that they are changed whenever the deplacement is changed
	public void OnChangedPos(){valueChanged[POS] = true;}


	public boolean isObjectDirty(Point currentScreendisp)
	{
		//Do not update the cached hitbox if the hitbox is still under computation 
		if((valueChangedStrategy[ANIM] && valueChanged[ANIM]) 
				||(valueChangedStrategy[DEPLACEMENT] && valueChanged[DEPLACEMENT])
				||(valueChangedStrategy[POS] && valueChanged[POS]) 
				||(valueChangedStrategy[ROTATION] && valueChanged[ROTATION]) 
				||(valueChangedStrategy[SCREENDISP] && (cachedScreenDisp==null || !cachedScreenDisp.equals(currentScreendisp))) 
				|| cachedObject==null)
			return true;
		else
			return false;
	}
	
	public void setDirtyStrategy(boolean[] strategy)
	{
		valueChangedStrategy=strategy;
	}
	
	protected T _getObject(Point INIT_RECT, Point currentScreendisp)
	{

		if(isObjectDirty(currentScreendisp))
		{
			resetValueChanged();
			cachedScreenDisp = currentScreendisp;
			cachedObject = this.computeObject(INIT_RECT,currentScreendisp);

			return cachedObject;
		}
		else
			return cachedObject;
	}

	protected void resetValueChanged()
	{
		for(int i=0; i<valueChanged.length;++i)
			valueChanged[i]=false;
	}
	
	public abstract CachedObject<T> copy();
	protected abstract T computeObject(Point INIT_RECT, Point currentScreendisp);
	
}
