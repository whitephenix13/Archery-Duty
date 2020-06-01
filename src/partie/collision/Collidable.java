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
import partie.effects.Effect;
import partie.effects.Vent_effect;
import partie.entitie.Entity;
import partie.entitie.monstre.Spirel;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Mouvement;
import partie.mouvement.Mouvement.SubTypeMouv;
import partie.mouvement.Mouvement.TypeMouv;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

//Specify that an object can enter in collision. 
//Store the information needed to manage a collision
public abstract class Collidable extends Destroyable{
	
	public ObjectType objType;

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

	private int mouv_index;
	private Mouvement mouvement;
	private Point pos;
	private double rotation=0;
	private Vector2d scaling = new Vector2d(1,1);//x,y scaling of the unrotated object

	//All types that the object will not be consider when colliding
	private List<ObjectType> immuneType =null;
	
	//protected boolean[] cachedParametersForHitbox;
	private CachedHitbox cachedHitbox;
	private CachedAffineTransform cachedDrawTr;
	
	//static cached hitbox for the screen used in isObjectOnScreen
	private final static CachedScreenHitbox cachedScreenHitbox = new CachedScreenHitbox();
	
	//Class used for memorizeCurrentValue

	public CachedHitbox getCacheHitboxCopy(){if(cachedHitbox==null) return null;return cachedHitbox.copy();}
	public void setCachedHit(CachedHitbox cachedHitboxCopy){if(cachedHitbox==null) return ;cachedHitbox.set(cachedHitboxCopy);}

	public CachedAffineTransform getCacheDrawTrCopy(){if(cachedDrawTr==null) return null;return cachedDrawTr.copy();}
	public void setCachedDrawTr(CachedAffineTransform cachedDrawTrCopy){if(cachedDrawTr==null) return; cachedDrawTr.set(cachedDrawTrCopy);}
	
	public List<ObjectType> getImmuneType() {return immuneType;}
	
	public int getMouvIndex(){return mouv_index;}
	public void setMouvIndex(int i){
		if(i!=mouv_index)
		{
			mouv_index=i;
			onChangedMouvIndex();
		}
	}
	public void onChangedMouvIndex(){if(cachedHitbox !=null) cachedHitbox.onChangedMouvIndex();if(cachedDrawTr !=null) cachedDrawTr.onChangedMouvIndex();}
	
	public void onChangedHitbox(){if(cachedHitbox !=null) cachedHitbox.onChangedHitbox();if(cachedDrawTr !=null) cachedDrawTr.onChangedHitbox();}
	
	public double getRotation(){return rotation;}
	public void setRotation(double rot){
		setRotation(rot,false);
	}
	public void setRotation(double rot, boolean ignoreOnChangedRotation){
		if(rotation != rot)
		{
			rotation=rot;
			if(!ignoreOnChangedRotation)
				onChangedRotation();
		}
	}
	public void onChangedRotation(){if(cachedHitbox !=null) cachedHitbox.onChangedRotation();if(cachedDrawTr !=null) cachedDrawTr.onChangedRotation();}

	public Vector2d getScaling(){return scaling;}
	public void setScaling(double xScaling,double yScaling){setScaling(new Vector2d(xScaling,yScaling));}
	public void setScaling(Vector2d new_scaling){if(scaling != new_scaling){scaling = new_scaling; onChangedScaling();}}
	public void onChangedScaling(){if(cachedHitbox !=null) cachedHitbox.onChangedScaling();if(cachedDrawTr !=null) cachedDrawTr.onChangedScaling();}
	
	public Mouvement getMouvement(){return mouvement;}
	public boolean isMouvement(TypeMouv t){return mouvement.isMouvement(t);}
	public boolean isMouvement(Mouvement m){return mouvement.isMouvement(m);}
	public boolean isMouvement(TypeMouv type,SubTypeMouv sub){return mouvement.isMouvement(type, sub);}
	public void setMouvement(Mouvement dep){
		mouvement = dep; //change since animation might change 
		if(!dep.isMouvement(mouvement))//on call on changed movement if movement changed (hitbox is fixed per mouvement) 
		{
			onChangedMouvement(mouvement==null?false: dep.isMouvement(mouvement));
		}
	}
	
	public void onChangedMouvement(boolean sameTypeOfMouvement){
		if(cachedHitbox !=null) cachedHitbox.onChangedMouvement(sameTypeOfMouvement);
		if(cachedDrawTr !=null) cachedDrawTr.onChangedMouvement(sameTypeOfMouvement);
	}
		//as hitbox are protected; we assume that they are changed whenever the deplacement is changed
	public int getXtaille(int mouv_index){return mouvement.getXtaille(mouv_index, scaling.x);}
	public int getYtaille(int mouv_index){return mouvement.getYtaille(mouv_index, scaling.y);}
	public int getCurrentXtaille(){return getXtaille(mouv_index);}
	public int getCurrentYtaille(){return getYtaille(mouv_index);}

	public Point getPointOfTaille(final float xdivider, final float ydivider,final double rotation,final Vector2d scaling,final int mouv_index){
		assert 0<=xdivider && xdivider<=1;
		assert 0<=ydivider && ydivider<=1;
		double unrot_x = mouvement.getXtaille(mouv_index, scaling.x)*xdivider;
		double unrot_y = mouvement.getYtaille(mouv_index, scaling.y)*ydivider;

		return new Point((int)Math.round(unrot_x* Math.cos(rotation) - unrot_y * Math.sin(rotation)),(int)Math.round(unrot_x * Math.sin(rotation) + unrot_y * Math.cos(rotation)));
	}
	public Point getPointOfTaille(final float xdivider, final float ydivider){
		return getPointOfTaille(xdivider,ydivider,rotation,scaling,mouv_index);
	}
	public Point getLeftOfTaille(final double rotation,final Vector2d scaling,final int mouv_index){
		return getPointOfTaille(0,0.5f,rotation,scaling,mouv_index);
	}
	public Point getCenterOfTaille(){return getPointOfTaille(0.5f,0.5f);}
	public Point getBottomOfTaille(){return getPointOfTaille(0.5f,1);}
	public Point getLeftOfTaille(){return getPointOfTaille(0,0.5f);}
	public Point getRightOfTaille(){return getPointOfTaille(1,0.5f);}
	
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
	
	public void OnPosChanged(){if(cachedHitbox !=null) cachedHitbox.onChangedPos();if(cachedDrawTr !=null) cachedDrawTr.onChangedPos();}
	
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

	public abstract Vitesse getGlobalVit();
	//Last norm of colliding object (most of the time: world). Null if none. The colliding object must be unpentrable, otherwise its norm is not registered as "last"
	//WARNING: only works with single object collision. WARNING strange things might happen if the object can move 
	
	public void setCollisionInformation(Vector2d _norm, Point _pointCollision, Point _correctedPointCollision)
	{normCollision=_norm;pointCollision = _pointCollision;correctedPointCollision=_correctedPointCollision; }
	public void setNormCollision(Vector2d _norm){normCollision=_norm;}
	//return the last norm of colliding object. This object has to be unpenetrable.
	public abstract Vector2d getNormCollision();
	
	public static enum XAlignmentType{NONE,CENTER,LEFT,RIGHT}
	public static enum YAlignmentType{NONE,CENTER,TOP,BOTTOM}

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

	public TypeMouv getTypeMouv(){return mouvement.getTypeMouv();}
	
	public void forceDrawTrDirty(){
		if(cachedDrawTr!=null)
			cachedDrawTr.forceDirty();
	}
	public AffineTransform getDrawTr(Point currentScreendisp)
	{	if(cachedDrawTr==null)
			cachedDrawTr = new CachedAffineTransform(this);
		return cachedDrawTr.getObject(currentScreendisp);
	}

	public void forceHitboxDirty(){
		if(cachedHitbox!=null)
			cachedHitbox.forceDirty();
	}
	public Hitbox getHitbox(Point INIT_RECT, Point currentScreendisp)
	{
		if(cachedHitbox==null)
			cachedHitbox= new CachedHitbox(this);
		return cachedHitbox.getObject(INIT_RECT, currentScreendisp);
	}
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp,Mouvement mouv, int _mouv_index)
	{
		if(cachedHitbox==null)
			cachedHitbox= new CachedHitbox(this);
		return cachedHitbox.getObject(INIT_RECT, screenDisp, mouv, _mouv_index);
	}

	/***
	 * This is especially usefull in computeHitbox for effect that are based on a transformation that already takes into account the scaling
	 * @param i
	 * @return
	 */
	public Hitbox getUnscaledMouvementHitboxCopy(int i){
		return mouvement.getHitboxCopy(i);
	}
	public Hitbox getMouvementHitboxCopy(int i){
		return mouvement.getScaledHitboxCopy(i,scaling);
	}
	/*public void setMouvementHitbox(List<Hitbox> hitboxes){
		mouvement.setHitbox(this, hitboxes);
	}*/
	
	protected abstract void onStartDeplace();
	/***
	 * Handle non mouvement based inputs
	 * @param partie
	 */
	protected abstract void handleInputs(AbstractModelPartie partie);
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected abstract boolean updateMouvementBasedOnPhysic(AbstractModelPartie partie);
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected abstract boolean updateNonInterruptibleMouvement(AbstractModelPartie partie);
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected abstract boolean updateMouvementBasedOnInput(AbstractModelPartie partie);
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected abstract boolean updateMouvementBasedOnAnimation(AbstractModelPartie partie);
	/***
	 * Callback that is called before updating the animation in deplace() function
	 */
	protected abstract void resetInputState(AbstractModelPartie partie);
	/***
	 * Callback that is called before updating the animation in deplace() function
	 */
	protected abstract void onMouvementChanged(AbstractModelPartie partie,boolean animationChanged, boolean mouvementChanged);
	protected void onAnimationEnded(AbstractModelPartie partie){
		destroy(partie,true);
	}
	protected void updateTimers(){};
	protected boolean shouldUpdateSpeed(){
		return true;
	}
	/**
	 * 
	 * @param partie
	 * @param deplace
	 * @return shouldMove: true if the collision (hence movement) have to be applied to this object. 
	 */
	public boolean deplace(AbstractModelPartie partie) {
		onStartDeplace();
		
		int prev_mouv_index =getMouvIndex();
		TypeMouv prev_mouv_type = getMouvement().getTypeMouv();
		
		handleInputs(partie);
		
		boolean isUpdated = updateMouvementBasedOnPhysic(partie);
		String updateCause = "Physic";
		if(!isUpdated){
			isUpdated= updateNonInterruptibleMouvement(partie);
			updateCause = "Non interruptible mouvement";
		}
		if(!isUpdated){
			isUpdated= updateMouvementBasedOnInput(partie);
			updateCause = "Input";
		}
		if(!isUpdated){
			isUpdated =updateMouvementBasedOnAnimation(partie); //TODO: here update roche_effect hitbox (what is called in onDeplaceStart + call this at instantiation)
			updateCause = "Animation";
		}
		resetInputState(partie);
		
		if(isUpdated){
			onMouvementChanged( partie,prev_mouv_index != getMouvIndex(), !getMouvement().isMouvement(prev_mouv_type));
		}
				
		if(getMouvement().animEnded())
			onAnimationEnded(partie);
		
		updateTimers();
		
		//update speed that depends on mouvement 
		if(shouldUpdateSpeed())
			getMouvement().setSpeed(this, getMouvIndex());
		return !getNeedDestroy();
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
	public abstract Hitbox computeHitbox(Point INIT_RECT,Point screenDisp,Mouvement mouv, int _mouv_index);

	public abstract void handleWorldCollision(Vector2d normal,AbstractModelPartie partie,Collidable collidedObject,boolean stuck);
	public abstract void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal);
	public abstract void handleStuck(AbstractModelPartie partie);

	/**
	 * Function used in Deplace to get back to  the previous correct posistion if stuck
	 */
	public abstract void memorizeCurrentValue();

	public abstract void deplaceOutOfScreen(AbstractModelPartie partie);
	//Use the function trick to memorize the reset values
	protected class CurrentValue{public void res(){};}
	public abstract void applyFriction(double minlocalSpeed, double minEnvirSpeed);
	public abstract void resetVarBeforeCollision();
	public abstract void handleDeplacementSuccess(AbstractModelPartie partie);
	public abstract void resetVarDeplace(boolean speedUpdated);

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

	/***
	 * 
	 * @param partie
	 * @param nextMouv
	 * @param nextIndex
	 * @param xAlignment
	 * @param yAlignment
	 * @param avoidCollision
	 * @param useTouchCollision
	 * @return Only works for square hitbox
	 * @throws Exception
	 */
	protected boolean alignNextMouvement(AbstractModelPartie partie,Mouvement nextMouv, int nextIndex,XAlignmentType xAlignment,YAlignmentType yAlignment,boolean avoidCollision,boolean useTouchCollision) throws Exception{
		if(xAlignment.equals(XAlignmentType.NONE) && yAlignment.equals(YAlignmentType.NONE))
			return true;

		final Hitbox currentHit = getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), getMouvIndex());
		//Assumed that there are no change in scaling 
		final Hitbox nextHit = getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextIndex);
		
		final Vector2d currentAnchor = currentHit.getPoint(getRotation(), xAlignment, yAlignment);
		final Vector2d nextAnchor = nextHit.getPoint(getRotation(), xAlignment, yAlignment);
		
		//Example, current anchor is 0,0 next anchor is 2,2, we want to move by (-2,-2)
		addXpos_sync((int)Math.round(currentAnchor.x-nextAnchor.x));
		addYpos_sync((int)Math.round(currentAnchor.y-nextAnchor.y));

		int prevMouvIndex = getMouvIndex();
		Mouvement prev_mouv = getMouvement().Copy();
		mouv_index = nextIndex;
		this.mouvement=nextMouv;

		boolean valid = false;
		if(avoidCollision){
			//We make an estimation of the eject max dist using hitbox (not taille as it leads to incorrect result since two mouvement 
			//can have the same taille but different hitboxes). Anyway when changing mouvement, 
			//the next hitbox should either have multiple collision (mouv invalid since new hitbox too big in narrow space) 
			//or can be eject by at most the difference between the size of the hitbox => closestEjectMaxDist in't too relevant
			//closestEjectMaxDist is only use for early stopping in case of obvious failure			
			//Mouvement have unrotated hitboxes so hitbox size can be computed easily
			Hitbox prevMouvHit = prev_mouv.getHitboxes().get(prevMouvIndex);
			Hitbox nextMouvHit = nextMouv.getHitboxes().get(nextIndex);
			Point currentProjectedTaille = Hitbox.getProjectedSize(prevMouvHit.getXTaille(),prevMouvHit.getYTaille(),getRotation());
			Point nextProjectedTaille = Hitbox.getProjectedSize(nextMouvHit.getXTaille(),nextMouvHit.getYTaille(),getRotation());
			
			Vector2d closestEjectMaxDist = new Vector2d(Math.abs(currentProjectedTaille.x-nextProjectedTaille.x),
					Math.abs(currentProjectedTaille.y-nextProjectedTaille.y));
			valid = Collision.ejectFromCollision(partie, this, null, true,useTouchCollision, closestEjectMaxDist);
		}
		else{
			valid = !Collision.isWorldCollision(partie, this,useTouchCollision);
		}
		mouv_index = prevMouvIndex;
		this.mouvement=prev_mouv;
		
		if(!valid){
			throw new Exception("World/Animation design: could not align mouvement for "+ this.toString()+" current : "+ 
		getMouvement().getTypeMouv()+" "+getMouvIndex()+" next: "+ nextMouv.getTypeMouv()+" "+nextIndex);
		}
		return valid;
	}
	public boolean isNextMouvValid(Mouvement nextMouvement, int nextMouvIndex, AbstractModelPartie partie, boolean useTouchCollision)
	{
		int prevMouvIndex = mouv_index;
		Mouvement prev_mouv = mouvement.Copy();
		mouv_index = nextMouvIndex;
		this.mouvement=nextMouvement;

		boolean valid= !Collision.isWorldCollision(partie, this,useTouchCollision);
		mouv_index = prevMouvIndex;
		this.mouvement=prev_mouv;
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
	private static Polygon bounding_pol; //avoid creating object in memory every time
	private static Point getCollidableCenter(final AbstractModelPartie partie,final Collidable col,final boolean screenReferential)
	{
		if(col.fixedWhenScreenMoves)
			return new Point(col.getXpos() +(screenReferential?0:-partie.xScreendisp), col.getYpos()+(screenReferential?0: -partie.yScreendisp));
		else
			return new Point(col.getXpos() +(screenReferential?partie.xScreendisp:0), col.getYpos()+(screenReferential?partie.yScreendisp:0));
	}
	public static boolean isObjectInBoundingSquare(final AbstractModelPartie partie,final Collidable col,final Collidable refObject) 
	{
		if(refObject==null)
			return true;
		bounding_pol = polygonFromBoundingSquare(getCollidableCenter(partie,refObject,false),col.getMaxBoundingSquare(),refObject.getMaxBoundingSquare());
		//The condition is that the x,y of col is in the polygon centered in the x,y of the ref Object of size 3*(sum of max bouding square)
		return Hitbox.contains(bounding_pol, getCollidableCenter(partie,col,false));
	}
	
	/***
	 * returns true if the object is on the screen (plus/minus threshold)
	 */
	public static boolean isObjectOnScreen(final AbstractModelPartie partie,final Collidable c)
	{
		if(c instanceof Fleche && ((Fleche)c).encochee)//specific case for fleche encochee that are at the same location as the heros but have no pos
			return true;
		else{
			return Collision.testcollisionHitbox(partie, cachedScreenHitbox.getObject(partie.getScreenDisp()), c.getHitbox(partie.INIT_RECT, partie.getScreenDisp()));
			//return Hitbox.contains(InterfaceConstantes.SCREEN.polygon, getCollidableCenter(partie,c,true));
		}
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
		if(isObjectInBoundingSquare(partie,partie.heros,ref_object))
			objects.add(partie.heros);
		
		for(Entity m : partie.tabMonstre){
			if(isObjectInBoundingSquare(partie,m,ref_object))
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
		if(isObjectInBoundingSquare(partie,partie.heros,ref_object)){
			herosList.add(partie.heros);
			objects.add(herosList);
		}
		if(ref_object==null)
			objects.add(partie.tabMonstre);
		else
		{
			List<Entity> monstreList = new ArrayList<Entity>();
			for(Entity m : partie.tabMonstre){
				if(isObjectInBoundingSquare(partie,m,ref_object))
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
			if(isObjectInBoundingSquare(partie,f,ref_object))
				objects.add(f);
		}
		for(Projectile tirM : partie.tabTirMonstre){
			if(isObjectInBoundingSquare(partie,tirM,ref_object))
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
				if(isObjectInBoundingSquare(partie,f,ref_object))
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
				if(isObjectInBoundingSquare(partie,tirM,ref_object))
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
					if(isObjectInBoundingSquare(partie,col,ref_object))
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
		return getAllCollidableEffect(partie,null,false);
	}
	public static List<Collidable> getAllCollidableEffectOnScreen(AbstractModelPartie partie)
	{
		return getAllCollidableEffect(partie,null,true);
	}

	public static List<Collidable> getAllCollidableEffect(final AbstractModelPartie partie,final Collidable ref_object)
	{
		return getAllCollidableEffect(partie,ref_object,false);
	}
	private static List<Collidable> getAllCollidableEffect(final AbstractModelPartie partie,final Collidable ref_object,final boolean checkOnScreen)
	{
		List<Collidable> list = new ArrayList<Collidable>();
		for(Collidable col : partie.arrowsEffects)
		{
			Effect eff = (Effect) col;
			if(eff.isWorldCollider && !eff.checkCollideWithNone())
			{
				if(ref_object==null && !checkOnScreen)
					list.add(col);
				else if(ref_object!=null){
					if(isObjectInBoundingSquare(partie,col,ref_object)){
						list.add(col);
					}
				}
				else{ //checkOnScreen
					if(isObjectOnScreen(partie, col))
						list.add(col);				
				}
			}

		}
		return list;
	}

	
}
