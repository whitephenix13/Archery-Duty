package partie.collision;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import gameConfig.Destroyable;
import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.deplacement.Deplace;
import partie.deplacement.Mouvement;
import partie.deplacement.Mouvement.SubTypeMouv;
import partie.deplacement.Mouvement.TypeMouv;
import partie.deplacement.entity.Mouvement_entity.MouvEntityEnum;
import partie.effects.Effect;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.Projectile;
import utils.Vitesse;

//Specify that an object can enter in collision. 
//Store the information needed to manage a collision
public abstract class Collidable extends Destroyable{
	
	public ObjectType objType;
	
	//Used to determine the index of cachedParameters 
	public final static int ANIM_CHANGED =0; 
	

	public Vitesse localVit;
	//Every registered object here will see their speed synchronise with respect to this collidable 
	public ArrayList<Collidable> synchroSpeed ;
	public boolean fixedWhenScreenMoves ; //true : not influenced by screen displacement (ie: use for the hero)
	public boolean controlScreenMotion=false;
	public boolean isVisible = true;

	public boolean useGravity=false;
	//Variables pour mémoriser la direction de la dernière collision
	public boolean last_colli_left=false;
	public boolean last_colli_right=false;
	//value to limit the maximum displacement. <0 means no limit 
	public double max_speed_norm = -1; 
	
	
	private int xpos; 
	private int ypos; 
	protected Vector2d normCollision = null;
	//Other collision information 
	protected Point pointCollision = null;
	protected Point correctedPointCollision = null;
	protected CurrentValue currentValue;

	private int anim;
	private Mouvement deplacement;
	private Point pos;
	private double rotation=0;

	//All types that the object will not be consider when colliding
	private List<ObjectType> immuneType =null;
	
	//protected boolean[] cachedParametersForHitbox;
	private CachedHitbox cachedHitbox;
	private CachedAffineTransform cachedDrawTr;
	//Class used for memorizeCurrentValue

	public CachedHitbox getCacheHitboxCopy(){if(cachedHitbox==null) return null;return cachedHitbox.copy();}
	public void setCachedHit(CachedHitbox cachedHitboxCopy){if(cachedHitbox==null) return ;cachedHitbox.set(cachedHitboxCopy);}

	public CachedAffineTransform getCacheDrawTrCopy(){if(cachedDrawTr==null) return null;return cachedDrawTr.copy();}
	public void setCachedDrawTr(CachedAffineTransform cachedDrawTrCopy){if(cachedDrawTr==null) return; cachedDrawTr.set(cachedDrawTrCopy);}
	
	public List<ObjectType> getImmuneType() {return immuneType;}
	
	public int getAnim(){return anim;}
	public void setAnim(int i){
		if(i!=anim)
		{
			anim=i;
			OnChangedAnim();
		}
	}
	public void OnChangedAnim(){if(cachedHitbox !=null) cachedHitbox.OnChangedAnim();if(cachedDrawTr !=null) cachedDrawTr.OnChangedAnim();}
	
	public void OnChangedHitbox(){if(cachedHitbox !=null) cachedHitbox.OnChangedHitbox();if(cachedDrawTr !=null) cachedDrawTr.OnChangedHitbox();}
	
	public double getRotation(){return rotation;}
	public void setRotation(double rot){
		setRotation(rot,false);
	}
	public void setRotation(double rot, boolean ignoreOnChangedRotation){
		if(rotation != rot)
		{
			rotation=rot;
			if(!ignoreOnChangedRotation)
				OnChangedRotation();
		}
	}
	public void OnChangedRotation(){if(cachedHitbox !=null) cachedHitbox.OnChangedRotation();if(cachedDrawTr !=null) cachedDrawTr.OnChangedRotation();}

	public Mouvement getDeplacement(){return deplacement;}
	public boolean isDeplacement(TypeMouv t){return deplacement.IsDeplacement(t);}
	public boolean isDeplacement(Mouvement m){return deplacement.IsDeplacement(m);}
	public boolean isDeplacement(TypeMouv type,SubTypeMouv sub){return deplacement.IsDeplacement(type, sub);}
	public void setDeplacement(Mouvement dep){
		if(!dep.equals(deplacement))
		{
			deplacement = dep;
			OnDeplacementChanged(deplacement==null?false: dep.IsDeplacement(deplacement));
		}
	}
	
	public void OnDeplacementChanged(boolean sameTypeOfDeplacement){
		if(cachedHitbox !=null) cachedHitbox.OnChangedDeplacement(sameTypeOfDeplacement);
		if(cachedDrawTr !=null) cachedDrawTr.OnChangedDeplacement(sameTypeOfDeplacement);
	}
		//as hitbox are protected; we assume that they are changed whenever the deplacement is changed
	
	public int getXpos(){return xpos;}
	public int getYpos(){return ypos;}
	
	protected void setXpos(int x){if(x!=xpos){xpos=x; OnPosChanged();}}
	protected void setYpos(int y){if(y!=ypos){ypos=y; OnPosChanged();}}
	
	protected void addXpos(int x){if(x!=0){xpos+=x; OnPosChanged();}}
	protected void addYpos(int y){if(y!=0){ypos+=y; OnPosChanged();}}

	public void addXpos_sync(int x){if(x!=0){xpos+=x; this.synchroSpeedAll(x, 0,true); OnPosChanged();}}
	public void addXpos_sync(int x,boolean fixedWhenScreenMove){if(x!=0){xpos+=x; this.synchroSpeedAll(x, 0,true,fixedWhenScreenMove); OnPosChanged();}}

	/**
	 * ypos+=y
	 * @param y
	 */
	public void addYpos_sync(int y){if(y!=0){ypos+=y;this.synchroSpeedAll(0, y,true);OnPosChanged();}}
	public void addYpos_sync(int y,boolean fixedWhenScreenMove){if(y!=0){ypos+=y; this.synchroSpeedAll(0,y,true,fixedWhenScreenMove);OnPosChanged();}}
	public void setXpos_sync(int x){if(xpos!=x){xpos=x; this.synchroSpeedAll(x, 0,false);OnPosChanged();}}
	public void setYpos_sync(int y){if(ypos!=y){ypos=y;this.synchroSpeedAll(0, y,false);OnPosChanged();}}
	
	public void OnPosChanged(){if(cachedHitbox !=null) cachedHitbox.OnChangedPos();if(cachedDrawTr !=null) cachedDrawTr.OnChangedPos();}
	
	public Point getPos(){pos.x =xpos; pos.y=ypos;return pos;}
	/**
	 * 
	 * @return the difference between the current position and the position given as parameter
	 */
	public Point getDeltaPos(Point previousP){return new Point(xpos-previousP.x,ypos-previousP.y);}
	
	public void setLocalVit(Vitesse vit){localVit=vit;}

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
			this.addXpos(xdep);
			this.addYpos(ydep);
		}
		else
		{
			this.setXpos(xdep);
			this.setYpos(ydep);
		}
		return 0;
	}

	public void setCollideWithAll()
	{
		this.immuneType=new ArrayList<ObjectType>();
	}
	public void setCollideWithout(List<ObjectType> list )
	{
		this.immuneType=list;
	}
	public void setCollideWithNone()
	{
		this.immuneType=Arrays.asList(ObjectType.COLLIDABLE);
	}
	public boolean checkCollideWithWorld()
	{
		return !ObjectTypeHelper.isMemberOf(ObjectType.BLOC, immuneType);
	}
	public boolean checkCollideWithEntitie()
	{
		return !ObjectTypeHelper.isMemberOf(ObjectType.ENTITIE, immuneType);
	}
	public boolean checkCollideWithEffect()
	{
		return !ObjectTypeHelper.isMemberOf(ObjectType.EFFECT, immuneType);
	}
	public boolean checkCollideWithNone()
	{
		return immuneType.contains(ObjectType.COLLIDABLE);
	}

	public abstract Vitesse getGlobalVit(AbstractModelPartie partie);
	//Last norm of colliding object (most of the time: world). Null if none. The colliding object must be unpentrable, otherwise its norm is not registered as "last"
	//WARNING: only works with single object collision. WARNING strange things might happen if the object can move 
	
	public void setCollisionInformation(Vector2d _norm, Point _pointCollision, Point _correctedPointCollision)
	{normCollision=_norm;pointCollision = _pointCollision;correctedPointCollision=_correctedPointCollision; }
	public void setNormCollision(Vector2d _norm){normCollision=_norm;}
	//return the last norm of colliding object. This object has to be unpenetrable.
	public abstract Vector2d getNormCollision();
	
	public Collidable()
	{
		objType = ObjectTypeHelper.getTypeObject(this);
		synchroSpeed= new ArrayList<Collidable>();
		normCollision=null;
		last_colli_left=false;
		last_colli_right=false;
		max_speed_norm=-1;
		pos = new Point();		
		setCollideWithNone();
	}

	public TypeMouv getTypeMouv(){return deplacement.getTypeMouv();}
	
	public AffineTransform getDrawTr(Point currentScreendisp)
	{	if(cachedDrawTr==null)
			cachedDrawTr = new CachedAffineTransform(this);
		return cachedDrawTr.getObject(currentScreendisp);
	}

	public Hitbox getHitbox(Point INIT_RECT, Point currentScreendisp)
	{
		if(cachedHitbox==null)
			cachedHitbox= new CachedHitbox(this);
		return cachedHitbox.getObject(INIT_RECT, currentScreendisp);
	}
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp,Mouvement mouv, int _anim)
	{
		if(cachedHitbox==null)
			cachedHitbox= new CachedHitbox(this);
		return cachedHitbox.getObject(INIT_RECT, screenDisp, mouv, _anim);
	}

	public Hitbox getDeplacementHitbox(int i){
		return deplacement.getHitbox().get(i);
	}
	public void setDeplacementHitbox(List<Hitbox> hitboxes){
		deplacement.setHitbox(this, hitboxes);
	}
	
	public abstract int getMaxBoundingSquare();
	public abstract Point getMaxBoundingRect(); //More accurate than bounding square. Use it if xhitbox is really different from yhitbox and you need precise calculations
	/***
	 * Compute the Affine transform used to display the object on screen 
	 * @param screenDisp
	 * @return
	 */
	public abstract AffineTransform computeDrawTr(Point screenDisp);
	public abstract Hitbox computeHitbox(Point INIT_RECT,Point screenDisp);
	public abstract Hitbox computeHitbox(Point INIT_RECT,Point screenDisp,Mouvement mouv, int _anim);

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
	public abstract boolean[] deplace(AbstractModelPartie partie, Deplace deplace);
	//Use the function trick to memorize the reset values
	protected class CurrentValue{public void res(){};}
	public abstract void applyFriction(double minlocalSpeed, double minEnvirSpeed);
	public abstract void resetVarBeforeCollision();
	public abstract void handleDeplacementSuccess(AbstractModelPartie partie);
	public abstract void resetVarDeplace(boolean speedUpdated);

	public abstract Hitbox getNextEstimatedHitbox(AbstractModelPartie partie, double newRotation,int anim);
	/*{
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}*/
	
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
					this.addXpos(appliedMotion.x);
					this.addYpos(appliedMotion.y);
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
		Mouvement depActu= getDeplacement();

		int xdir = left ? -1 :1;
		int ydir = down ? 1 :-1;
		final Polygon currentPol = getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depActu, animActu).polygon;
		final Polygon nextHit = getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depSuiv, animSuiv).polygon;
		
		int dx= (int) Math.round( (Hitbox.supportPoint(new Vector2d(xdir,0),currentPol ).x -
				Hitbox.supportPoint(new Vector2d(xdir,0), nextHit).x));

		int dy= (int) Math.round((Hitbox.supportPoint(new Vector2d(0,ydir), currentPol).y -
				Hitbox.supportPoint(new Vector2d(0,ydir), nextHit).y));

		int m_dx=0; //-dx, computed if needed
		int m_dy=0; //-dy, computed if needed

		int xadded = dx; //remember how to get back to normal xpos
		int yadded = dy;//remember how to get back to normal ypos
		//xpos+=, ypos+=
		addXpos(dx);
		addYpos(dy);

		//String s ="";
		//String s_x =left? "left":"right";
		//String s_mx =!left? "left":"right";
		//String s_y =down? " down":" up";
		//String s_my =!down? " down":" up";

		boolean valid=alignTestValid(depSuiv, animSuiv, partie,deplace,obj,useTouchCollision);

		//s+= (valid && s=="") ? s_x+s_y : "";
		boolean n_glisse = depSuiv.IsDeplacement(MouvEntityEnum.GLISSADE);
		//test the opposite y 
		if(!valid)
		{
			m_dy=(int) Math.round(Hitbox.supportPoint(new Vector2d(0,-ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depActu, animActu).polygon).y -
					Hitbox.supportPoint(new Vector2d(0,-ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depSuiv, animSuiv).polygon).y);

			addXpos(dx-xadded);
			addYpos(m_dy-yadded);

			xadded=dx;
			yadded=m_dy;

			valid=alignTestValid(depSuiv, animSuiv, partie,deplace,obj,useTouchCollision);
			//s+= (valid && s=="") ? s_x+s_my : "";
		}

		//test the opposite x with the first value of y
		if(!valid && !n_glisse)
		{
			m_dx=(int) Math.round(Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depActu, animActu).polygon).x -
					Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),depSuiv, animSuiv).polygon).x);

			addXpos(m_dx-xadded);
			addYpos(dy-yadded);

			xadded=m_dx;
			yadded=dy;

			valid=alignTestValid(depSuiv, animSuiv, partie,deplace,obj,useTouchCollision);
			//s+= (valid && s=="") ? s_mx+s_y : "";

		}

		//test the opposite x with the opposite y
		if(!valid && !n_glisse)
		{			
			addXpos(m_dx-xadded);
			addYpos(m_dy-yadded);
			xadded=m_dx;
			yadded=m_dy;
			valid=alignTestValid(depSuiv, animSuiv, partie,deplace,obj,useTouchCollision);
			//s+= (valid && s=="") ? s_mx+s_my : "";

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
		Mouvement prev_mouv = deplacement.Copy();
		anim = animSuiv;
		this.deplacement=depSuiv;

		boolean valid= !Collision.isWorldCollision(partie, this,useTouchCollision);
		anim = prev_anim;
		this.deplacement=prev_mouv;
		return valid;
	}

	public Vitesse convertSpeed(double norm_speed, double angle)
	{
		double cos_angle = Math.cos(angle);
		double sin_angle = Math.sin(angle);

		double x = norm_speed * cos_angle;
		double y = norm_speed * sin_angle;
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
		Point col_pos = new Point(col.getXpos() + (col.fixedWhenScreenMoves? -partie.xScreendisp:0), col.getYpos() + (col.fixedWhenScreenMoves? -partie.yScreendisp:0));
		Point ref_center = new Point(refObject.getXpos() + (refObject.fixedWhenScreenMoves? -partie.xScreendisp:0), refObject.getYpos() + (refObject.fixedWhenScreenMoves? -partie.yScreendisp:0));
		Polygon bounding_pol = polygonFromBoundingSquare(ref_center,col.getMaxBoundingSquare(),refObject.getMaxBoundingSquare());
		//The condition is that the x,y of col is in the polygon centered in the x,y of the ref Object of size 3*(sum of max bouding square)
		return Hitbox.contains(bounding_pol, col_pos);
	}
	public static ArrayList<Entity> getAllEntitiesCollidable(AbstractModelPartie partie)
	{
		return getAllEntitiesCollidable(partie,null);
	}
	/**
	 * 
	 * @param partie
	 * @param center: center of the hitbox to consider (x y pos of the object to test collision with) 
	 * @param maxBoundingSquare: max bounding square if the previous object 
	 * @return
	 */
	public static ArrayList<Entity> getAllEntitiesCollidable(AbstractModelPartie partie,Collidable ref_object)
	{
		ArrayList<Entity> objects = new ArrayList<Entity>();
		if(objectInBoundingSquare(partie,partie.heros,ref_object))
			objects.add(partie.heros);
		
		for(Entity m : partie.tabMonstre){
			if(objectInBoundingSquare(partie,m,ref_object))
				objects.add(m);
		}
		return objects;
	}
	
	public static List<List<Entity>> getAllEntitiesCollidableSeparately(AbstractModelPartie partie)
	{
		return getAllEntitiesCollidableSeparately(partie,null);
	}
	public static List<List<Entity>> getAllEntitiesCollidableSeparately(AbstractModelPartie partie,Collidable ref_object)
	{
		List<List<Entity>> objects = new ArrayList<List<Entity>>();
		List<Entity> herosList = new ArrayList<Entity>();
		if(objectInBoundingSquare(partie,partie.heros,ref_object)){
			herosList.add(partie.heros);
			objects.add(herosList);
		}
		if(ref_object==null)
			objects.add(partie.tabMonstre);
		else
		{
			List<Entity> monstreList = new ArrayList<Entity>();
			for(Entity m : partie.tabMonstre){
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

	public static List<List<Collidable>> getAllProjectileCollidableSeparately(AbstractModelPartie partie)
	{
		return getAllProjectileCollidableSeparately(partie,null);
	}
	private static void addList(List<List<Collidable>> objects,List<Projectile> toAdd )
	{
		List<Collidable> l = new ArrayList<Collidable>();
		for(Projectile p : toAdd)
			l.add(p);
		objects.add(l);
	}
	public static List<List<Collidable>> getAllProjectileCollidableSeparately(AbstractModelPartie partie,Collidable ref_object)
	{
		List<List<Collidable>> objects = new ArrayList<List<Collidable>>();
		if(ref_object==null)
			addList(objects,partie.tabFleche);
		else
		{
			List<Collidable> flecheList = new ArrayList<Collidable>();
			for(Collidable f : partie.tabFleche){
				if(objectInBoundingSquare(partie,f,ref_object))
					flecheList.add(f);
			}
			objects.add(flecheList);
		}
		
		if(ref_object==null)
			addList(objects,partie.tabTirMonstre);
		else
		{
			List<Collidable> tirMList = new ArrayList<Collidable>();
			for(Collidable tirM : partie.tabTirMonstre){
				if(objectInBoundingSquare(partie,tirM,ref_object))
					tirMList.add(tirM);
			}
			objects.add(tirMList);
		}
		objects.add(getAllProjectileEffect(partie,ref_object));
		
		return objects;
	}
	/**Get all effects that are collidable (such as bloc)*/
	public static List<Collidable> getAllProjectileEffect(AbstractModelPartie partie)
	{
		return getAllProjectileEffect(partie,null);
	}
	/**
	 * 
	 * @param partie
	 * @param ref_object: for Screen test, use CustomBoundingSquare.getScreen()
	 * @return
	 */
	public static List<Collidable> getAllProjectileEffect(AbstractModelPartie partie,Collidable ref_object)
	{
		List<Collidable> list = new ArrayList<Collidable>();
		for(Collidable col : partie.arrowsEffects)
		{
			Effect eff = (Effect) col;
			if(eff.isProjectile && !eff.checkCollideWithNone())
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
	
}
