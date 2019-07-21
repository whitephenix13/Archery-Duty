package partie.deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gameConfig.ObjectTypeHelper.ObjectType;
import images.ImagesContainer.ImageInfo;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import utils.Vitesse;

public abstract class Mouvement{
	
	public static interface TypeMouv extends ImageInfo{}//used for polymorphism of all movements such that any enum of movement will implement it 
	public static interface SubTypeMouv extends ImageInfo{}
	public static enum DirSubTypeMouv implements SubTypeMouv{DROITE,GAUCHE}
	
	public List<Integer> xtaille= new ArrayList<Integer>() ;
	public List<Integer> ytaille= new ArrayList<Integer>() ;
	
	protected List<Hitbox> hitbox = new ArrayList<Hitbox>(); //warning this can be set without calling OnChangedHitbox from collidable due to "protected"
	public List<Hitbox> getHitbox(){return hitbox;}
	public void setHitbox(Collidable obj,List<Hitbox> hit){this.hitbox= hit; obj.OnChangedHitbox();}
	
	protected Animation animation = new Animation();
	public boolean animEndedOnce(){return animation.isEndedOnce();}
	public boolean animEnded(){return animation.isEnded();}
	
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
	 * @param anim
	 * @return
	 */
	protected abstract Vitesse __getUncheckedSpeed(Collidable object, int anim);
	
	public TypeMouv getTypeMouv(){return type_mouv;}
	public SubTypeMouv getSubTypeMouv(){return sub_type_mouv;}
	public ObjectType getObjType(){return objType;}
	
	public boolean IsDeplacement(Mouvement m)
	{
		if(m==null)
			return false;
		return m.type_mouv.equals(type_mouv);
	}
	public boolean IsDeplacement(TypeMouv type)
	{
		return type.equals(type_mouv);
	}
	public boolean IsDeplacement(TypeMouv type,SubTypeMouv sub)
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
	
	public Vitesse getSpeed(Collidable object, int anim)
	{
		if(stopSpeed)
			return zeroVit;
		else 
			return __getUncheckedSpeed(object, anim);
	}

	public void setSpeed(Collidable object, int anim)
	{
		Vitesse vit = getSpeed(object,anim);
		if(vit==null)
			return;
		object.setLocalVit(vit);
	};
	
	public abstract DirSubTypeMouv droite_gauche(int anim,double rotation);
	public int updateAnimation(int anim,int current_frame,double speedFactor) {
		return updateAnimation(anim,current_frame,speedFactor,false);
	}
	public int updateAnimation(int anim,int current_frame,double speedFactor,boolean log) {
		return animation.update(anim,current_frame,speedFactor,log);
	}
	}
