package partie.deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import partie.collision.Collidable;
import partie.collision.Hitbox;
import utils.Vitesse;

public abstract class Mouvement{
	
	public static String DROITE = "DROITE";
	public static String GAUCHE = "GAUCHE";
	
	public List<Integer> xtaille= new ArrayList<Integer>() ;
	public List<Integer> ytaille= new ArrayList<Integer>() ;
	
	protected List<Hitbox> hitbox = new ArrayList<Hitbox>(); //warning this can be set without calling OnChangedHitbox from collidable due to "protected"
	public List<Hitbox> getHitbox(){return hitbox;}
	public void setHitbox(Collidable obj,List<Hitbox> hit){this.hitbox= hit; obj.OnChangedHitbox();}
	
	protected Animation animation = new Animation();
	public boolean animEndedOnce(){return animation.isEndedOnce();}
	public boolean animEnded(){return animation.isEnded();}
	public TypeMouv type_mouv;
	
	public boolean stopSpeed = false;
	
	private static Vitesse zeroVit = new Vitesse();

	//protected abstract void OnSetHitbox(boolean isRotated);
	public abstract Mouvement Copy(Object obj);
	/**
	 * @return the maximum length or height that the hitbox can reached for this motion(often =max(xtaille,ytaille). This value will be multiplied by sqrt(2) to handle diagonals of squares
	 * */
	public abstract int getMaxBoundingSquare(Object obj);
	public abstract Point getMaxBoundingRect(Object obj);

	public abstract boolean IsDeplacement(Mouvement m);
	public abstract boolean IsDeplacement(TypeMouv s);
	/***
	 * WARNING: you should never call this function except in getSpeed
	 * This speed is the speed of the object without considering stopSpeed. To get the true speed use getSpeed instead
	 * @param object
	 * @param anim
	 * @return
	 */
	protected abstract Vitesse __getUncheckedSpeed(Collidable object, int anim);
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
	
	public abstract String droite_gauche(Object obj,int anim);
	public int updateAnimation(Object obj, int anim,int current_frame,double speedFactor) {
		return updateAnimation(obj,anim,current_frame,speedFactor,false);
	}
	public int updateAnimation(Object obj, int anim,int current_frame,double speedFactor,boolean log) {
		return animation.update(anim,current_frame,speedFactor,log);
	}
	}
