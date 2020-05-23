package partie.collision;

import java.awt.Point;
import java.util.Arrays;

import gameConfig.InterfaceConstantes;
import partie.projectile.fleches.Fleche;

public abstract class CachedObject<T> {
	
	protected Collidable parent;
	
	public static enum CachedParameter{
		FORCED(0),MOUV_INDEX(1),MOUVEMENT(2),HITBOX(3),POS(4),ROTATION(5),SCALING(6),SCREENDISP(7);
		private final int index;
		private CachedParameter(int val){
			index = val;
		}
		public int getIndex(){return index;}
	}
	
	public Point cachedScreenDisp; //screen disp when the object was cached
	protected boolean[] valueChanged;
	protected boolean[] valueChangedStrategy;//which parameters to use to determine if the object is dirty

	protected T cachedObject; // cached tranformation used to draw the object on screen. This is not the same as the on used to compute hitbox (it is translated by screendisp)
	
	protected CachedObject()
	{
		valueChanged = new boolean[7]; //screendisp is not part of it 
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
	
	public void onChangedHitbox(){valueChanged[CachedParameter.HITBOX.getIndex()]=true;}
	public void onChangedMouvIndex(){valueChanged[CachedParameter.MOUV_INDEX.getIndex()]=true;}
	public void onChangedRotation(){valueChanged[CachedParameter.ROTATION.getIndex()]=true;}
	public void onChangedScaling(){valueChanged[CachedParameter.SCALING.getIndex()]=true;}
	public void onChangedMouvement(boolean sameTypeOfMouvement){valueChanged[CachedParameter.MOUVEMENT.getIndex()] = true;
	valueChanged[CachedParameter.HITBOX.getIndex()]=true;}//as hitbox are protected; we assume that they are changed whenever the mouvement is changed
	public void onChangedPos(){valueChanged[CachedParameter.POS.getIndex()] = true;}

	public void forceDirty(){valueChanged[CachedParameter.FORCED.getIndex()] = true;}
	public boolean isObjectDirty(Point currentScreendisp)
	{
		
		
		if(InterfaceConstantes.DEBUG_CACHED_OBJECT){
			String debug_dirty_cause = "";
			if((valueChangedStrategy[CachedParameter.MOUV_INDEX.getIndex()] && valueChanged[CachedParameter.MOUV_INDEX.getIndex()]))
				debug_dirty_cause += " Mouv index changed";
			if((valueChangedStrategy[CachedParameter.MOUVEMENT.getIndex()] && valueChanged[CachedParameter.MOUVEMENT.getIndex()]))
				debug_dirty_cause += "; Mouvement changed";
			if((valueChangedStrategy[CachedParameter.POS.getIndex()] && valueChanged[CachedParameter.POS.getIndex()]) )
				debug_dirty_cause += "; Position changed";
			if((valueChangedStrategy[CachedParameter.ROTATION.getIndex()] && valueChanged[CachedParameter.ROTATION.getIndex()]) )
				debug_dirty_cause += "; Rotation changed";
			if((valueChangedStrategy[CachedParameter.SCALING.getIndex()] && valueChanged[CachedParameter.SCALING.getIndex()]) )
				debug_dirty_cause += "; Scaling changed";
			if((valueChangedStrategy[CachedParameter.SCREENDISP.getIndex()] && (cachedScreenDisp==null || !cachedScreenDisp.equals(currentScreendisp))) )
				debug_dirty_cause += "; Screendisp changed";
			if((valueChangedStrategy[CachedParameter.FORCED.getIndex()] && valueChanged[CachedParameter.FORCED.getIndex()]))
				debug_dirty_cause += "; Forced";
			if(cachedObject==null)
				debug_dirty_cause += "; Cached object is null";
			if(!debug_dirty_cause.equals(""))
				System.out.println(parent+"<"+this+"> dirty because "+debug_dirty_cause);
		}
		//if(InterfaceConstantes)
		//Do not update the cached hitbox if the hitbox is still under computation 
		if((valueChangedStrategy[CachedParameter.MOUV_INDEX.getIndex()] && valueChanged[CachedParameter.MOUV_INDEX.getIndex()]) 
				||(valueChangedStrategy[CachedParameter.MOUVEMENT.getIndex()] && valueChanged[CachedParameter.MOUVEMENT.getIndex()])
				||(valueChangedStrategy[CachedParameter.POS.getIndex()] && valueChanged[CachedParameter.POS.getIndex()]) 
				||(valueChangedStrategy[CachedParameter.ROTATION.getIndex()] && valueChanged[CachedParameter.ROTATION.getIndex()]) 
				||(valueChangedStrategy[CachedParameter.SCALING.getIndex()] && valueChanged[CachedParameter.SCALING.getIndex()]) 
				||(valueChangedStrategy[CachedParameter.SCREENDISP.getIndex()] && (cachedScreenDisp==null || !cachedScreenDisp.equals(currentScreendisp))) 
				||(valueChangedStrategy[CachedParameter.FORCED.getIndex()] && valueChanged[CachedParameter.FORCED.getIndex()])
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
