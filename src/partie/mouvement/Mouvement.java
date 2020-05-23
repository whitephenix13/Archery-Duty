package partie.mouvement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import images.ImagesContainer.ImageInfo;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import utils.Vitesse;

public abstract class Mouvement{

	public static interface TypeMouv extends ImageInfo{}//used for polymorphism of all movements such that any enum of movement will implement it 
	public static interface SubTypeMouv extends ImageInfo{}
	public static enum DirSubTypeMouv implements SubTypeMouv{DROITE,GAUCHE}

	protected List<Integer> xtaille= new ArrayList<Integer>() ;
	protected List<Integer> ytaille= new ArrayList<Integer>() ;
	public int getXtaille(int mouv_index, double scaling){return (int)Math.round(xtaille.get(mouv_index)*scaling);}
	public int getYtaille(int mouv_index, double scaling){return (int)Math.round(ytaille.get(mouv_index)*scaling);}

	protected List<Hitbox> hitbox = new ArrayList<Hitbox>(); //warning this can be set without calling OnChangedHitbox from collidable due to "protected"
	public Hitbox getHitboxCopy(int mouv_index){return hitbox.get(mouv_index).copy();}
	public Hitbox getScaledHitboxCopy(int mouv_index,Vector2d scaling){
		if(scaling.x==1 && scaling.y==1)
			return getHitboxCopy(mouv_index);
		else
			return hitbox.get(mouv_index).copy().scale(scaling);
	}
	public List<Hitbox> getHitboxes(){return hitbox;}
	public void setHitbox(Collidable obj,List<Hitbox> hit){this.hitbox= hit; obj.onChangedHitbox();}

	protected Animation animation = new Animation();
	public boolean animEndedOnce(){return animation.isEndedOnce();}
	public boolean animEnded(){return animation.isEnded();}
	/***
	 * 
	 * @return true if the movement can be interrupted by inputs
	 */
	public abstract boolean isInterruptible(int currentAnim);
	
	protected TypeMouv type_mouv;
	protected SubTypeMouv sub_type_mouv;
	protected ObjectType objType;

	public boolean stopSpeed = false;

	private static Vitesse zeroVit = new Vitesse();

	//protected abstract void OnSetHitbox(boolean isRotated);
	public abstract Mouvement Copy();
	/**
	 * @return the maximum length or height that the hitbox can reached for this motion(often =max(xtaille,ytaille). This value will be multiplied by sqrt(2) to handle diagonals of squares
	 * */

	/***
	 * WARNING: you should never call this function except in getSpeed
	 * This speed is the speed of the object without considering stopSpeed. To get the true speed use getSpeed instead
	 * @param object
	 * @param mouv_index
	 * @return
	 */
	protected abstract Vitesse __getUncheckedSpeed(Collidable object, int mouv_index);

	public TypeMouv getTypeMouv(){return type_mouv;}
	public SubTypeMouv getSubTypeMouv(){return sub_type_mouv;}
	public ObjectType getObjType(){return objType;}

	public boolean isMouvement(Mouvement m)
	{
		if(m==null)
			return false;
		return m.type_mouv.equals(type_mouv);
	}
	public boolean isMouvement(TypeMouv type)
	{
		return type.equals(type_mouv);
	}
	public boolean isMouvement(TypeMouv type,SubTypeMouv sub)
	{
		if((sub==null && type_mouv!=null) ||(sub!=null && type_mouv==null) )
			return false;
		else if(sub==null && type_mouv==null)
			return type.equals(type_mouv);
		else
			return type.equals(type_mouv) && sub.equals(sub_type_mouv);
	}


	public int getMaxBoundingSquare()
	{
		return Math.max(Collections.max(xtaille), Collections.max(ytaille));
	}

	public Point getMaxBoundingRect()
	{
		return new Point(Collections.max(xtaille), Collections.max(ytaille));
	}

	public Vitesse getSpeed(Collidable object, int mouv_index)
	{
		if(stopSpeed)
			return zeroVit;
		else 
			return __getUncheckedSpeed(object, mouv_index);
	}

	public void setSpeed(Collidable object, int mouv_index)
	{
		Vitesse vit = getSpeed(object,mouv_index);
		if(vit==null)
			return;
		object.setLocalVit(vit);
	};

	public abstract DirSubTypeMouv droite_gauche(int mouv_index,double rotation);
	public void startAnimation(List<Integer> animTimes, int current_frame, int start_index, int end_index, int max_anim)
	{
		animation.start(animTimes, current_frame, start_index, end_index);
		animation.setMaxNumAnim(max_anim);
	}
	public int updateAnimation(int mouv_index,int current_frame,double speedFactor) {
		boolean log=false;
		return animation.update(mouv_index,current_frame,speedFactor,log);	
	}
}
