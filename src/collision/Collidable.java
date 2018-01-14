package collision;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import effects.Effect;
import partie.AbstractModelPartie;
import personnage.Heros;
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
	protected int xpos; 
	protected int ypos; 


	public int xpos(){return xpos;}
	public int ypos(){return ypos;}
	/**
	 * xpos+=x
	 * @param x
	 */
	public void pxpos(int x){xpos+=x;}
	public void pypos(int y){ypos+=y;}

	public void pxpos_sync(int x){if(x==0) return;xpos+=x; this.synchroSpeedAll(x, 0,true);}
	public void pxpos_sync(int x,boolean fixedWhenScreenMove){xpos+=x; if(x!=0)this.synchroSpeedAll(x, 0,true,fixedWhenScreenMove);}

	/**
	 * ypos+=y
	 * @param y
	 */
	public void pypos_sync(int y){if(y==0) return;ypos+=y;this.synchroSpeedAll(0, y,true);}
	public void pypos_sync(int y,boolean fixedWhenScreenMove){ypos+=y; if(y!=0)this.synchroSpeedAll(0,y,true,fixedWhenScreenMove);}
	public void xpos_sync(int x){xpos=x; if(xpos!=x)this.synchroSpeedAll(x, 0,false);}
	public void ypos_sync(int y){ypos=y;if(ypos!=y)this.synchroSpeedAll(0, y,false);}
	
	public Point getPos(){return new Point(xpos,ypos);}
	/**
	 * 
	 * @return the difference between the current position and the position given as parameter
	 */
	public Point getDeltaPos(Point previousP){return new Point(xpos-previousP.x,ypos-previousP.y);}
	public double rotation=0;
	public int anim; 
	public Mouvement deplacement;
	public Vitesse localVit;


	//public ConditionHandler conditions;
	//Every registered object here will see their speed synchronise with respect to this collidable 
	public ArrayList<Collidable> synchroSpeed ;
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
		if(synchroSpeed ==null)
			return;
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
	protected int OnSynchroSpeed(Collidable synchronizer, int xdep, int ydep,boolean add)
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
		return !TypeObject.isMemberOf(Arrays.asList(TypeObject.BLOC), immuneType);
	}
	public boolean checkCollideWithEntitie()
	{
		return !TypeObject.isMemberOf(Arrays.asList(TypeObject.ENTITIE), immuneType);
	}
	public boolean checkCollideWithEffect()
	{
		return !TypeObject.isMemberOf(Arrays.asList(TypeObject.EFFECT), immuneType);
	}
	public boolean checkCollideWithNone()
	{
		return immuneType.contains(TypeObject.COLLIDABLE);
	}

	public abstract Vitesse getGlobalVit(AbstractModelPartie partie);
	//Last norm of colliding object (most of the time: world). Null if none. The colliding object must be unpentrable, otherwise its norm is not registered as "last"
	//WARNING: only works with single object collision. WARNING strange things might happen if the object can move 
	protected Vector2d normCollision = null;
	//Other collision information 
	protected Point pointCollision = null;
	protected Point correctedPointCollision = null;

	public void setCollisionInformation(Vector2d _norm, Point _pointCollision, Point _correctedPointCollision)
	{normCollision=_norm;pointCollision = _pointCollision;correctedPointCollision=_correctedPointCollision; }
	public void setNormCollision(Vector2d _norm){normCollision=_norm;}
	//return the last norm of colliding object. This object has to be unpenetrable.
	public abstract Vector2d getNormCollision();


	public boolean fixedWhenScreenMoves ; //true : not influenced by screen displacement (ie: use for the hero)
	public boolean controlScreenMotion=false;
	public boolean isVisible = true;

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
		setCollideWithNone();
	}

	public abstract int getMaxBoundingSquare();
	public abstract Hitbox getHitbox(Point INIT_RECT,Point screenDisp);
	public abstract Hitbox getHitbox(Point INIT_RECT,Point screenDisp,Mouvement mouv, int _anim);

	public abstract void handleWorldCollision(Vector2d normal,AbstractModelPartie partie,Collidable collidedObject,boolean stuck);
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
	public abstract boolean[] deplace(AbstractModelPartie partie, Deplace deplace, boolean update_with_speed);
	//Use the function trick to memorize the reset values
	protected class CurrentValue{public void res(){};}
	public abstract void applyFriction(double minlocalSpeed, double minEnvirSpeed);
	public abstract void resetVarBeforeCollision();
	public abstract void handleDeplacementSuccess(AbstractModelPartie partie);
	public abstract void resetVarDeplace(boolean speedUpdated);

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

	static boolean TEST = false;
	/**
	 * @param ref_object: the object that pushes "this" by motion
	 * @param motion: value by which the collidable has to be moved for the test
	 * @param collidableToMove: collidable to have to be ejected
	 * @param motionToApply: motion to apply to the collidable that have to be ejected
	 * @return false if moving this by motion is not possible(collision with world or effects) or if ref_object and this are not colliding ,true otherwise (AND APPLY THE MOTION)
	 */
	public boolean requestMoveBy(AbstractModelPartie partie,Collidable ref_object,Point motion,List<Collidable> collidableToMove, List<Point> motionToApply)
	{
		if(this.checkCollideWithWorld())
			if(Collision.testcollisionObjects(partie, this, ref_object, false))
			{
				
				Point appliedMotion = new Point();//not set to null so that we can retrieve the desired motion
				boolean considerEffects = true;
				if(!Collision.ejectWorldCollision(partie, this,ref_object,motion,appliedMotion,considerEffects))
					return false; // ejection was not successful,  "this" is preventing ref_object to move, return false
				else
				{
					//"this" is not preventing ref_object to move: return true (done later) and add it to the collidableToMove list
					collidableToMove.add(this);
					motionToApply.add(appliedMotion);
					//apply motion
					this.pxpos(appliedMotion.x);
					this.pypos(appliedMotion.y);
				}
			}
		//In this case, the object does not consider collision with the world(or is not colliding with ref_object), hence it should no be moved by the ref_object.
		//However as it is not preventing the ref_object from moving, the function returns true (but the object is not registred in collidableToMove)
		return true;
	}
	public void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace, boolean left, boolean down,
			Object obj, boolean useTouchCollision)
	{
		//Collect the added motion 
		Mouvement depActu= deplacement;

		int xdir = left ? -1 :1;
		int ydir = down ? 1 :-1;
		int dx= (int) Math.round( (Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depActu, animActu).polygon).x -
				Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depSuiv, animSuiv).polygon).x));

		int dy= (int) Math.round((Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depActu, animActu).polygon).y -
				Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depSuiv, animSuiv).polygon).y));

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
			m_dy=(int) Math.round(Hitbox.supportPoint(new Vector2d(0,-ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depActu, animActu).polygon).y -
					Hitbox.supportPoint(new Vector2d(0,-ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depSuiv, animSuiv).polygon).y);

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
			m_dx=(int) Math.round(Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depActu, animActu).polygon).x -
					Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depSuiv, animSuiv).polygon).x);

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
		//TODO: changed, removed (int) 
		//CHANGED return new Vitesse(Math.round(x),Math.round(y));
		return new Vitesse(x,y);
	}

	//For all the methods below: do not include the effects 

	private static Polygon polygonFromBoundingSquare(Point center, Integer maxBouding1, Integer maxBouding2)
	{
		//Knowing the center (position of obj 2 ) and the bounding square max value of obj1 and obj2 
		//the length of the square is sqrt(2) * (maxBouding1 + maxBouding2) * 2 ) ~= 3 * (maxBouding1 + maxBouding2)
		Polygon p = new Polygon();
		int half_L = (int) Math.ceil(1.5 * (maxBouding1+maxBouding2));
		p.addPoint(center.x-half_L, center.y-half_L);
		p.addPoint(center.x+half_L, center.y-half_L);
		p.addPoint(center.x+half_L, center.y+half_L);
		p.addPoint(center.x-half_L, center.y+half_L);
		return p;
	}
	public static boolean objectInBoundingSquare(AbstractModelPartie partie,Collidable col, Collidable refObject) 
	{
		if(refObject==null)
			return true;
		Point col_pos = new Point(col.xpos() + (col.fixedWhenScreenMoves? -partie.xScreendisp:0), col.ypos() + (col.fixedWhenScreenMoves? -partie.yScreendisp:0));
		Point ref_center = new Point(refObject.xpos() + (refObject.fixedWhenScreenMoves? -partie.xScreendisp:0), refObject.ypos() + (refObject.fixedWhenScreenMoves? -partie.yScreendisp:0));
		Polygon bounding_pol = polygonFromBoundingSquare(ref_center,col.getMaxBoundingSquare(),refObject.getMaxBoundingSquare());
		//The condition is that the x,y of col is in the polygon centered in the x,y of the ref Object of size 3*(sum of max bouding square)
		return Hitbox.contains(bounding_pol, col_pos);
	}
	public static ArrayList<Entitie> getAllEntitiesCollidable(AbstractModelPartie partie)
	{
		return getAllEntitiesCollidable(partie,null);
	}
	/**
	 * 
	 * @param partie
	 * @param center: center of the hitbox to consider (x y pos of the object to test collision with) 
	 * @param maxBoundingSquare: max bounding squaez if the previous object 
	 * @return
	 */
	public static ArrayList<Entitie> getAllEntitiesCollidable(AbstractModelPartie partie,Collidable ref_object)
	{
		ArrayList<Entitie> objects = new ArrayList<Entitie>();
		if(objectInBoundingSquare(partie,partie.heros,ref_object))
			objects.add(partie.heros);
		
		for(Entitie m : partie.tabMonstre){
			if(objectInBoundingSquare(partie,m,ref_object))
				objects.add(m);
		}
		return objects;
	}
	
	public static List<List<Entitie>> getAllEntitiesCollidableSeparately(AbstractModelPartie partie)
	{
		return getAllEntitiesCollidableSeparately(partie,null);
	}
	public static List<List<Entitie>> getAllEntitiesCollidableSeparately(AbstractModelPartie partie,Collidable ref_object)
	{
		List<List<Entitie>> objects = new ArrayList<List<Entitie>>();
		List<Entitie> herosList = new ArrayList<Entitie>();
		if(objectInBoundingSquare(partie,partie.heros,ref_object)){
			herosList.add(partie.heros);
			objects.add(herosList);
		}
		if(ref_object==null)
			objects.add(partie.tabMonstre);
		else
		{
			List<Entitie> monstreList = new ArrayList<Entitie>();
			for(Entitie m : partie.tabMonstre){
				if(objectInBoundingSquare(partie,m,ref_object))
					monstreList.add(m);
			}
			objects.add(monstreList);
		}
		return objects;
	}
	public static ArrayList<Projectile> getAllProjectileCollidable(AbstractModelPartie partie)
	{
		return getAllProjectileCollidable(partie,null);
	}
	public static ArrayList<Projectile> getAllProjectileCollidable(AbstractModelPartie partie,Collidable ref_object)
	{
		ArrayList<Projectile> objects = new ArrayList<Projectile>();
		for(Projectile f : partie.tabFleche){
			if(objectInBoundingSquare(partie,f,ref_object))
				objects.add(f);
		}
		for(Projectile tirM : partie.tabTirMonstre){
			if(objectInBoundingSquare(partie,tirM,ref_object))
				objects.add(tirM);
		}
		return objects;
	}

	public static List<List<Projectile>> getAllProjectileCollidableSeparately(AbstractModelPartie partie)
	{
		return getAllProjectileCollidableSeparately(partie,null);
	}
	
	public static List<List<Projectile>> getAllProjectileCollidableSeparately(AbstractModelPartie partie,Collidable ref_object)
	{
		List<List<Projectile>> objects = new ArrayList<List<Projectile>>();
		if(ref_object==null)
			objects.add(partie.tabFleche);
		else
		{
			List<Projectile> flecheList = new ArrayList<Projectile>();
			for(Projectile f : partie.tabFleche){
				if(objectInBoundingSquare(partie,f,ref_object))
					flecheList.add(f);
			}
			objects.add(flecheList);
		}
		
		if(ref_object==null)
			objects.add(partie.tabTirMonstre);
		else
		{
			List<Projectile> tirMList = new ArrayList<Projectile>();
			for(Projectile tirM : partie.tabTirMonstre){
				if(objectInBoundingSquare(partie,tirM,ref_object))
					tirMList.add(tirM);
			}
			objects.add(tirMList);
		}
		return objects;
	}
	
	/**Get all effects that are collidable (such as bloc)*/
	public static List<Collidable> getAllCollidableEffect(AbstractModelPartie partie)
	{
		return getAllCollidableEffect(partie,null);
	}
	/**
	 * 
	 * @param partie
	 * @param ref_object: for Screen test, use CustomBoundingSquare.getScreen()
	 * @return
	 */
	public static List<Collidable> getAllCollidableEffect(AbstractModelPartie partie,Collidable ref_object)
	{
		List<Collidable> list = new ArrayList<Collidable>();
		for(Collidable col : partie.arrowsEffects)
		{
			Effect eff = (Effect) col;
			if(eff.isWorldCollider && !eff.checkCollideWithNone())
			{
				if(ref_object==null)
					list.add(col);
				else
				{
					
					if(objectInBoundingSquare(partie,col,ref_object))
					{
						list.add(col);
					}
				}
			}

		}
		return list;
	}
	
	/*public static List<Collidable> getAllCollidableWithEffect(AbstractModelPartie partie,Effect eff)
	{
		List<Collidable> list = new ArrayList<Collidable>();
		for(Entitie ent: getAllEntitiesCollidable(partie))
		{
			for(Effect ef : ent.currentEffects)
				if(eff == ef)
					list.add(ent);
					
		}
		return list;
	}*/

}
