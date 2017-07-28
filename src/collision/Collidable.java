package collision;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import effects.Effect;
import effects.Grappin_effect;
import effects.Vent_effect;
import fleches.Fleche;
import monstre.Monstre;
import monstre.TirMonstre;
import partie.AbstractModelPartie;
import types.Destroyable;
import types.Entitie;
import types.Hitbox;
import types.Projectile;
import types.TypeObject;
import types.Vitesse;

//Specify that an object can enter in collision. 
//Store the information needed to manage a collision
public abstract class Collidable extends Destroyable{

	//All types that the object will not be consider when colliding
	private List<String> immuneType =null;
	public List<String> getImmuneType() {return immuneType;}
	private int xpos; 
	private int ypos; 
	
	
	public int xpos(){return xpos;}
	public int ypos(){return ypos;}
	/**
	 * xpos+=x
	 * @param x
	 */
	public void pxpos(int x){xpos+=x; if(x!=0)this.synchroSpeedAll(x, 0,true);}
	public void pxpos(int x,boolean fixedWhenScreenMove){xpos+=x; if(x!=0)this.synchroSpeedAll(x, 0,true,fixedWhenScreenMove);}

	/**
	 * ypos+=y
	 * @param y
	 */
	public void pypos(int y){ypos+=y;if(y!=0)this.synchroSpeedAll(0, y,true);}
	public void pypos(int y,boolean fixedWhenScreenMove){ypos+=y; if(y!=0)this.synchroSpeedAll(0,y,true,fixedWhenScreenMove);}
	public void xpos(int x){xpos=x; if(xpos!=x)this.synchroSpeedAll(x, 0,false);}
	public void ypos(int y){ypos=y;if(ypos!=y)this.synchroSpeedAll(0, y,false);}
	public double rotation=0;
	public int anim; 
	public Mouvement deplacement;
	public Vitesse localVit;
	
	
	//public ConditionHandler conditions;
	//Every registered object here will see their speed synchronise with respect to this collidable 
	private ArrayList<Collidable> synchroSpeed ;
	public void addSynchroSpeed(Collidable objToSynchronize)
	{
		//this==synchronizer
		synchroSpeed.add(objToSynchronize);
	}
	
	private void synchroSpeedAll(int xdep, int ydep,boolean add)
	{
		synchroSpeedAll(xdep, ydep,add,null);
	}
	/**
	 * 
	 * @param xdep
	 * @param ydep
	 * @param add
	 * @param _fixedWhenScreenMoves: use to parameter to not synchronize deplaceEcran for an object that has _fixedWhenScreenMoves=false 
	 */
	private void synchroSpeedAll(int xdep, int ydep,boolean add,Boolean _fixedWhenScreenMoves)
	{
		int endLoop = this.synchroSpeed.size();
		for(int i=0;i<endLoop; ++i ){
			Collidable col = this.synchroSpeed.get(i);
			int objRemoved = 0;
			if(_fixedWhenScreenMoves==null || col.fixedWhenScreenMoves==_fixedWhenScreenMoves)
				objRemoved = col.OnSynchroSpeed(this, xdep, ydep,add);
			//if the object has to be removed from that list (because it is going to be destroyed)
			if(objRemoved==-1){
				i=i-1;
				endLoop=endLoop-1;
			}
		}
	}
	/**
	 * 
	 * @param synchronizer
	 * @param xdep
	 * @param ydep
	 * @return 0 if everything worked find, -1 if the object got removed from the list 
	 */
	private int OnSynchroSpeed(Collidable synchronizer, int xdep, int ydep,boolean add)
	{
		if(this.needDestroy){
			synchronizer.synchroSpeed.remove(this);
			return -1;
		}
		//WARNING: this can generate out of bounds: no collision check here
		if(add){
			this.xpos+=xdep;
			this.ypos+=ydep;
		}
		else
		{
			this.xpos=xdep;
			this.ypos=ydep;
		}
		return 0;
	}
	
	public void setCollideWithAll()
	{
		this.immuneType=new ArrayList<String>();
	}
	public void setCollideWithout(List<String> list )
	{
		this.immuneType=list;
	}
	public void setCollideWithNone()
	{
		this.immuneType=Arrays.asList(TypeObject.COLLIDABLE);
	}
	public boolean checkCollideWithWorld()
	{
		return !TypeObject.isMemberOf(this, Arrays.asList(TypeObject.BLOC));
	}

	public abstract Vitesse getGlobalVit(AbstractModelPartie partie);
	//Last norm of colliding object (most of the time: world). Null if none. The colliding object must be unpentrable, otherwise its norm is not registered as "last"
	//WARNING: only works with single object collision. WARNING strange things might happen if the object can move 
	protected Vector2d normCollision = null;
	
	public void setNormCollision(Vector2d _norm){normCollision=_norm;}
	//return the last norm of colliding object. This object has to be unpenetrable.
	public abstract Vector2d getNormCollision();


	public boolean fixedWhenScreenMoves ; //true : not influenced by screen displacement (ie: use for the hero)
	public boolean controlScreenMotion=false;
	
	protected CurrentValue currentValue;
	public boolean useGravity=false;
	//Variables pour mémoriser la direction de la dernière collision
	public boolean last_colli_left=false;
	public boolean last_colli_right=false;
	//value to limit the maximum displacement. <0 means no limit 
	public double max_speed_norm = -1; 
	
	public void init()
	{
		synchroSpeed= new ArrayList<Collidable>();
		//Collide with everyt
		normCollision=null;
		last_colli_left=false;
		last_colli_right=false;
		max_speed_norm=-1;
	}
	
	public abstract Hitbox getHitbox(Point INIT_RECT);
	public abstract Hitbox getHitbox(Point INIT_RECT,Mouvement mouv, int _anim);

	public abstract void handleWorldCollision(Vector2d normal,AbstractModelPartie partie,boolean stuck);
	public abstract void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal);
	public abstract void handleStuck(AbstractModelPartie partie);

	/**
	 * Function used in Deplace to get back to  the previous correct posistion if stuck
	 */
	public abstract void memorizeCurrentValue();
	/**
	 * 
	 * @param partie
	 * @param deplace
	 * @return [shouldMove,changedAnimation] shouldMove: if the collision (hence movement) have to be applied to this object. 
	 * changedAnimation : if the animation changed due to a change of movement or a change in droite_gauche
	 */
	public abstract boolean[] deplace(AbstractModelPartie partie, Deplace deplace);
	//Use the function trick to memorize the reset values
	protected class CurrentValue{public void res(){};}
	public abstract void applyFriction(double minlocalSpeed, double minEnvirSpeed);
	public abstract void resetVarBeforeCollision();
	public abstract void handleDeplacementSuccess(AbstractModelPartie partie);
	public abstract void resetVarDeplace();

	/**
	 * Call this method to properly destroy an object (ie: destroy arrow and remove effects)
	 */
	@Override
	public void destroy(AbstractModelPartie partie,boolean destroyNow)
	{
		if(destroyNow)
			this.needDestroy=true;
		else
			timer();
	}


	public void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace, boolean left, boolean down,
			Object obj, boolean useTouchCollision)
	{
		//Collect the added motion 
		double[] res = new double[0];
		Mouvement depActu= deplacement;

		int xdir = left ? -1 :1;
		int ydir = down ? 1 :-1;
		int dx= (int) Math.round( (Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).x -
				Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).x));

		int dy= (int) Math.round((Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).y -
				Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).y));

		int m_dx=0; //-dx, computed if needed
		int m_dy=0; //-dy, computed if needed

		int xadded = dx; //remember how to get back to normal xpos
		int yadded = dy;//remember how to get back to normal ypos
		//xpos+=, ypos+=
		pxpos(dx);
		pypos(dy);

		String s ="";
		String s_x =left? "left":"right";
		String s_mx =!left? "left":"right";
		String s_y =down? " down":" up";
		String s_my =!down? " down":" up";

		boolean valid=alignTestValid(depSuiv, animSuiv, partie,deplace,obj,useTouchCollision);

		s+= (valid && s=="") ? s_x+s_y : "";
		boolean n_glisse = depSuiv.IsDeplacement(Mouvement_perso.glissade);
		//test the opposite y 
		if(!valid)
		{
			m_dy=(int) Math.round(Hitbox.supportPoint(new Vector2d(0,-ydir), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).y -
					Hitbox.supportPoint(new Vector2d(0,-ydir), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).y);
			
			pxpos(dx-xadded);
			pypos(m_dy-yadded);
			
			xadded=dx;
			yadded=m_dy;

			valid=alignTestValid(depSuiv, animSuiv, partie,deplace,obj,useTouchCollision);
			s+= (valid && s=="") ? s_x+s_my : "";
		}

		//test the opposite x with the first value of y
		if(!valid && !n_glisse)
		{
			m_dx=(int) Math.round(Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).x -
					Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).x);
			
			pxpos(m_dx-xadded);
			pypos(dy-yadded);
			
			xadded=m_dx;
			yadded=dy;

			valid=alignTestValid(depSuiv, animSuiv, partie,deplace,obj,useTouchCollision);
			s+= (valid && s=="") ? s_mx+s_y : "";

		}

		//test the opposite x with the opposite y
		if(!valid && !n_glisse)
		{			
			pxpos(m_dx-xadded);
			pypos(m_dy-yadded);
			xadded=m_dx;
			yadded=m_dy;
			valid=alignTestValid(depSuiv, animSuiv, partie,deplace,obj,useTouchCollision);
			s+= (valid && s=="") ? s_mx+s_my : "";

		}

		/*if(deplacement_type.equals(TypeObject.heros)){
			System.out.println(deplacement.getClass().getName() +animActu +" "+depSuiv.getClass().getName()+animSuiv);
			System.out.println(s);
		}*/
	}

	public boolean alignTestValid(Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace, 
			Object obj, boolean useTouchCollision)
	{
		int prev_anim = anim;
		Mouvement prev_mouv = deplacement.Copy(obj);
		this.anim=animSuiv;
		this.deplacement=depSuiv;

		boolean valid= !Collision.isWorldCollision(partie, this,useTouchCollision);
		this.anim=prev_anim;
		this.deplacement=prev_mouv;
		return valid;
	}

	public Vitesse convertSpeed(double norm_speed, double angle)
	{
		double cos_angle = Math.cos(angle);
		double sin_angle = Math.sin(angle);

		double x = norm_speed * cos_angle;
		double y = norm_speed * sin_angle;
		return new Vitesse((int)Math.round(x),(int)Math.round(y));
	}

	//For all the methods below: do not include the effects 
	

	public static ArrayList<Entitie> getAllEntitiesCollidable(AbstractModelPartie partie)
	{
		ArrayList<Entitie> objects = new ArrayList<Entitie>();
		objects.add(partie.heros);
		for(Entitie m : partie.tabMonstre)
			objects.add(m);
		return objects;
	}

	public static List<List<Entitie>> getAllEntitiesCollidableSeparately(AbstractModelPartie partie)
	{
		List<List<Entitie>> objects = new ArrayList<List<Entitie>>();
		List<Entitie> herosList = new ArrayList<Entitie>();
		herosList.add(partie.heros);
		objects.add(herosList);
		objects.add(partie.tabMonstre);
		return objects;
	}
	public static ArrayList<Projectile> getAllProjectileCollidable(AbstractModelPartie partie)
	{
		ArrayList<Projectile> objects = new ArrayList<Projectile>();
		for(Projectile f : partie.tabFleche)
			objects.add(f);
		for(Projectile tirM : partie.tabTirMonstre)
			objects.add(tirM);
		return objects;
	}
	
	public static List<List<Projectile>> getAllProjectileCollidableSeparately(AbstractModelPartie partie)
	{
		List<List<Projectile>> objects = new ArrayList<List<Projectile>>();
		objects.add(partie.tabFleche);
		objects.add(partie.tabTirMonstre);
		return objects;
	}
	
}
