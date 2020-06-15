package partie.effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import menu.menuPrincipal.ModelPrincipal;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.conditions.Condition.ConditionEnum;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.modelPartie.PartieTimer;
import partie.mouvement.Deplace;
import partie.mouvement.Mouvement.DirSubTypeMouv;
import partie.mouvement.effect.Roche_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Roche_effect extends Effect{
	
	double DUREE_DEFAILLANCE = 3;
	final double GROUND_PULSE_EVERY = 0.05;
	final double ENTITY_PULSE_EVERY = 0.5;

	double UPDATE_LENGTH_TIME= 0.01; //10ms
	int MAX_UPDATE_LENGTH = 5;//by how much the pilar grows each update
	int MAX_LENGTH = 100;
	
	double update_length = 0;//how much the pillar actually grown (since it can't be longer than MAX_LENGTH)

	double pilar_length = 1;
	private double getUnscaledPilarLength(){
		return pilar_length/getScaling().y;
	}
	
	double lastLengthUpdateTime=-1;

	boolean stopGrowing=false;
	boolean lastStopGrowingUpdate = true;
	
	public boolean isGrowing(){return !stopGrowing;}

	public double PILAR_DURATION= 3; //3sec 
	double lastTimePilarFullLength = -1; //used with pilar duration to time the destruction
	public boolean startDestroyAnim =false;

	List<Collidable> accrochedCol = new ArrayList<Collidable>();
	
	//One liner array instantiation: new Collidable[] {..., ..., ...};
	public final static void synchroniseMovementWithRocheEffectMovement(Collidable collidedObject,Collidable[] objectsToSynchronize)
	{
		if(collidedObject instanceof Roche_effect)
		{
			Roche_effect eff = (Roche_effect) collidedObject;
			if(eff.isWorldCollider){
				for(Collidable obj : objectsToSynchronize)
					eff.addSynchroSpeed(obj);
			}
		}
	}
	
	
	public void registerAccrocheCol(Collidable col)
	{
		if(!groundEffect)
			return;

		if(!accrochedCol.contains(col)){
			accrochedCol.add(col);
		}
	}
	public void unregisterAccrocheCol(Collidable col)
	{
		if(!groundEffect)
			return;

		accrochedCol.remove(col);
	}

	private BufferedImage convertedIm=null;

	public Roche_effect(Fleche _ref_fleche,int _mouv_index, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision, boolean _groundCollision)
	{
		super(_mouv_index,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,_groundCollision,_groundCollision);
		assert getScaling().x == getScaling().y;
		ModelPrincipal.debugTime.startElapsedForVerbose();
		ModelPrincipal.debugTime.elapsed("Roche effect: call super.init");
		this.setCollideWithAll();
		
		consequenceUpdateTime = groundEffect?GROUND_PULSE_EVERY:ENTITY_PULSE_EVERY;

		this.isWorldCollider= groundEffect? true :false; //groundEffect=_groundCollision
		
		subTypeMouv = groundEffect?EffectCollisionEnum.GROUND:EffectCollisionEnum.ENTITY;
		setMouvement(new Roche_idle(subTypeMouv,ModelPartie.me.getFrame()));
		
		MAX_LENGTH = (int)Math.round(MAX_LENGTH * getScaling().y);
		MAX_UPDATE_LENGTH = (int)Math.round(MAX_UPDATE_LENGTH * getScaling().y);
		
		setFirstPos();

		if(groundEffect)
			updateHitbox();
		
		ModelPrincipal.debugTime.elapsed("Roche effect: call update hitbox");
		ModelPartie.me.arrowsEffects.add(this);

		int initialEject = MAX_UPDATE_LENGTH+1;
		ModelPrincipal.debugTime.elapsed("Roche effect: call on update");
		//if the eject failed, the object is stuck (for example when arrow roche shot in corner) then destroy it
	}

	@Override
	public boolean requestMoveBy(Collidable ref_object,Point motion,List<Collidable> collidableToMove, List<Point> motionToApply)
	{

		if(!groundEffect)
			super.requestMoveBy( ref_object, motion, collidableToMove, motionToApply);
		
		if(this.checkCollideWithWorld())
			if(Collision.testcollisionObjects(this, ref_object, false))
			{
				//eject the object by only considering the world collision (not effect because they can be pushed)
				Point appliedMotion = new Point();//not set to null so that we can retrieve the desired motion
				boolean considerEffects = false;
				if(!Collision.ejectWorldCollision(this,ref_object,motion,appliedMotion,considerEffects)){
					return false; // ejection was not successful,  "this" is preventing ref_object to move, return false
				}

				//apply motion for this
				collidableToMove.add(this);
				motionToApply.add(appliedMotion);
				this.addXpos(appliedMotion.x);
				this.addYpos(appliedMotion.y);

				//Check if perpendicular is not a problem : 
				boolean noCollision = checkPerpendicularValid(collidableToMove,motionToApply);
				if(!noCollision)
					return false;
				
				noCollision = checkSynchronizeValid(collidableToMove,motionToApply);
				if(!noCollision)
					return false;
				
				//check all current collision with effectand entities 
				List<Collidable> effectEntitieColliding = Collision.getAllEffectEntitieCollision(this, motion);
				effectEntitieColliding.remove(this);
				//for all current collisions != ref_object 
				for(Collidable col : effectEntitieColliding)
				{
					if(col == ref_object){
						return false;
					}

					//call requestMoveBy (try to push it) and return false if fail
					if(!col.requestMoveBy(this, motion, collidableToMove, motionToApply))
					{
						return false;
					}

					//if success, continue (ie: collidableToMove increased) and object was moved		
				}

				//if reached here, return true
			}

		return true;
	}

	/**
	 * 
	 * @param partie
	 * @param collidableToMove
	 * @param motionToApply
	 * @return Check if the synchronized object does not collide with other collidable
	 */
	private boolean checkSynchronizeValid(List<Collidable> collidableToMove, List<Point> motionToApply)
	{
		boolean success=true;
		for(Collidable col : synchroSpeed)
		{
			if(col.checkCollideWithNone())
				continue;
			//If collider : check collision (true). If effect, only check collision if worldCollider
			boolean isEffect = ( col instanceof Effect);
			boolean isCollidable= isEffect ?  ((Effect)col).isWorldCollider : true ; 
			boolean collideWithWorld = Collision.isWorldCollision(col, true);
			boolean collideWithEntiteEffect = Collision.getAllEffectEntitieCollision(col, new Point(0,0)).size()>0;
			if(isCollidable && (collideWithWorld || collideWithEntiteEffect))
			{
				success=false;
				break;
			}

		}
		return success;
	}
	/**
	 * 
	 * @param partie
	 * @param collidableToMove
	 * @param motionToApply
	 * @return Check for all accroch objects (heros) and perpendicular (arrows that hit this pilar)  if they are not colliding in that current setting (and also adjust the 
	 * accroche to the top of the pilar). Returns true if there is no collision, returns false otherwise. 
	 */
	private boolean checkPerpendicularValid(List<Collidable> collidableToMove, List<Point> motionToApply)
	{
		boolean success=true;
		for(Collidable col : accrochedCol)
		{
			Vector2d xydir_effect = Deplace.angleToVector(getRotation()-Math.PI/2); 
			Vector2d xydir_col = Deplace.angleToVector(col.getMouvement().droite_gauche(col.getMouvIndex(),col.getRotation()).equals(DirSubTypeMouv.GAUCHE) ? 5.0*Math.PI/4: 7.0*Math.PI/4);

			Vector2d effect_sp = Hitbox.supportPoint(xydir_effect, getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp()).polygon);
			Vector2d col_sp = Hitbox.supportPoint(xydir_col, col.getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp()).polygon);
			
			boolean considerTouch =true;
			//pillar goes up
			if(getRotation() == 0)
			{
				double dy= effect_sp.y -col_sp.y;
				col.addYpos_sync((int) dy);
				collidableToMove.add(col);
				motionToApply.add(new Point(0,(int)dy));
			}
			else if(getRotation() == Math.PI/2 || (getRotation() == -Math.PI/2))
			{
				double ejectx = (getRotation() == Math.PI/2) ? 1 : -1;
				double dx= effect_sp.x -col_sp.x+ejectx;
				col.addXpos_sync((int) dx);
				collidableToMove.add(col);
				motionToApply.add(new Point((int)dx,0));
			}
			if(Collision.isWorldCollision( col, considerTouch))
			{
				success=false;
				break;
			}
		}
		
		if(success)
		{
			//loop over synchronized object and check if they do not collide 
			for(Collidable col : synchroSpeed)
			{
				if(Collision.isWorldCollision( col, true))
				{
					success=false;
					break;
				}
			}
			
		}
		return success;

	}
	
	@Override 
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		if(groundEffect){
			int shift = 1;
			int newyMax = (int)( (Math.round(getUnscaledPilarLength())-shift) * 1); //Don't forget to scale 

			Hitbox mouvementHitbox = getUnscaledMouvementHitboxCopy(getMouvIndex()).copy()
					.reshapeUnrotatedSquareHitbox(null, null, null, newyMax);
			return computeEffectHitbox(mouvementHitbox,INIT_RECT,screenDisp);
		}
		else 
			return super.computeHitbox(INIT_RECT, screenDisp);
	}

	private void updateHitbox()
	{
		if(groundEffect)
			forceHitboxDirty();
	}

	/**
	 * 
	 * @param current_frame
	 * @param force: force the destroy mouv_index: first check if it was not already called. Also make sure that the pilar stop growing 
	 */
	public void startDestroyAnim(int current_frame,boolean force)
	{
		if(groundEffect)
		{
			if(force)
			{
				//destruction was already called
				if(startDestroyAnim)
					return;
				//else make sure to stop growing before calling destroy 
				stopGrowing=true;
				lastTimePilarFullLength=UPDATE_LENGTH_TIME*Math.pow(10, 9)+1;
				startDestroyAnim=true;
			}
			this.setCollideWithNone();

			((Roche_idle)getMouvement()).setDestroyAnimation(current_frame);

		}
	}
	@Override
	public void applyConsequence(Entity attacher,boolean isFirstApplication)
	{
		if(!groundEffect)
			attacher.conditions.addNewCondition(ConditionEnum.DEFAILLANCE, DUREE_DEFAILLANCE,System.identityHashCode(this));
	}

	private void ejectCollidable(int initEject,boolean init)
	{
		if(!groundEffect)
			return;
		//Compute the value by which the length of the pilar has to be updated 
		if((pilar_length+ MAX_UPDATE_LENGTH) > this.MAX_LENGTH)
			update_length = MAX_LENGTH - pilar_length;
		else
			update_length = this.MAX_UPDATE_LENGTH;
		
		if(!init){
			growPilar(update_length);
			updateHitbox();
		}
		else
			update_length=1;
		


		boolean couldntGrow = false; // true if when growing pilar is stuck in ground 
		//Variables to handle the case when an object is stuck
		List<Collidable> collidableToMove = new ArrayList<Collidable>();  //Collidable that were moved
		List<Point> motionToApply = new ArrayList<Point>();  // motion that was applied

		if(Collision.isWorldCollision(this, true,false))
			couldntGrow=true;

		//check perpendicular 
		if(!couldntGrow)
		{
			couldntGrow=!checkPerpendicularValid(collidableToMove, motionToApply);
		}
		
		if(!couldntGrow)
		{
			couldntGrow=!checkSynchronizeValid(collidableToMove,motionToApply);
		}
		//check recursivity 
		if(!couldntGrow)
		{
			//Test if the movement doesn't stuck any other objects
			List<Entity> allEntities = Collidable.getAllEntitiesCollidable(this);
			List<Collidable>  allEffects = Collidable.getAllCollidableEffect( this);
			allEffects.remove(this);

			List<Collidable> allColli = new ArrayList<Collidable>();
			allColli.addAll(allEntities);
			allColli.addAll(allEffects);
			allColli.removeAll(accrochedCol);
			
			Point motion = alignWithPilar(init?initEject :  (int)update_length);
			//negate motion since the function gives the direction from the top of the pilar to the bottom (opposite of actual motion) 
			motion = new Point(-motion.x,-motion.y);

			for(int i=0; i<allColli.size(); i++){
				Collidable col = allColli.get(i);

				//In the case where the movement was no possible: revert growth + objects from collidableToMove
				if(!col.requestMoveBy(this, motion, collidableToMove, motionToApply)){
					//Stop pilar growth and revert its motion
					couldntGrow=true;
					break; 
				}

				//Otherwise, object has been moved in requestMoveBy, check if the screen has to be updated 
				if(col.controlScreenMotion){
					Point delta = Deplace.getdeplaceEcran((Heros)col,true);
					Deplace.deplaceEcran(delta,col);
				}

			}
		}
		//If there was a problem, revert motion
		if(couldntGrow)
		{
			//if couldn't eject objects on init, then it has to be destroyed (ie: stuck in world)
			if(init)
				this.destroy(true);
			this.revertGrowth(update_length,  collidableToMove, motionToApply);
		}

	}
	
	@Override
	protected boolean updateMouvementBasedOnAnimation() {
		
		if(groundEffect){
			if(!stopGrowing && (pilar_length < MAX_LENGTH) && (PartieTimer.me.getElapsedNano() - lastLengthUpdateTime)>UPDATE_LENGTH_TIME*Math.pow(10, 9))
			{
				ejectCollidable(0,false);
				if(pilar_length == MAX_LENGTH)
				{
					stopGrowing=true;
					lastTimePilarFullLength=PartieTimer.me.getElapsedNano();
				}
				lastLengthUpdateTime=PartieTimer.me.getElapsedNano();
			}			
			if(!startDestroyAnim && stopGrowing&& (PartieTimer.me.getElapsedNano()-lastTimePilarFullLength)>PILAR_DURATION*Math.pow(10, 9))
			{
				startDestroyAnim(ModelPartie.me.getFrame(),false);
				startDestroyAnim=true;
				return true;
			}
		}
		return super.updateMouvementBasedOnAnimation();
	}

	@Override
	protected void onMouvementChanged(boolean animationChanged, boolean mouvementChanged)
	{
		if(animationChanged)
			previousMaskedIm=null;
	}

	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		return new Vitesse();
	}

	@Override
	public Image applyFilter( Image im) {
		if(!groundEffect)
			return im;
		int width = im.getWidth(null);
		int height = im.getHeight(null);

		if(width==-1 || height == -1 )
			return im;

		boolean stopUpdate = stopGrowing && getMouvIndex() ==0;
		if( stopUpdate && !lastStopGrowingUpdate)
			return previousMaskedIm;

		int start_filter = (int)Math.round(getUnscaledPilarLength());
		if(previousMaskedIm==null){
			previousMaskedIm = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
			convertedIm=ModelPartie.me.toBufferedImage(im);

			//deep copy of converted image into previous masked im
			ColorModel cm = convertedIm.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = convertedIm.copyData(null);
			previousMaskedIm=new BufferedImage(cm, raster, isAlphaPremultiplied, null);

		}
		previousMaskedIm=ModelPartie.me.apply_height_mask(convertedIm,previousMaskedIm,start_filter,1);

		if(stopUpdate)
			lastStopGrowingUpdate=false;
		return previousMaskedIm;
	}


	public void setFirstPos() {

		int divider = groundEffect? 1:2;
		
		int pilar_correction = groundEffect? (int)Math.round(pilar_length) :0;//take into account that at the start, the pilar is already of size pilar_length
		//get the middle bottom of the effect
		int x_eff_center = (int) (getCurrentXtaille()/2 * Math.cos(getRotation()) - (getCurrentYtaille()/divider+pilar_correction) * Math.sin(getRotation()));
		int y_eff_center = (int) (getCurrentXtaille()/2 * Math.sin(getRotation()) + (getCurrentYtaille()/divider+pilar_correction) * Math.cos(getRotation()));
		Point firstPos = new Point();
		if(groundEffect)
		{
			firstPos=super.setFirstPos(new Point(x_eff_center,y_eff_center));
		} 
		else
		{
			Hitbox fHitbox = ref_fleche.getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp());

			Vector2d opposNormal = new Vector2d();
			opposNormal.negate(normalCollision);
			List<Vector2d> collidedPoints = Hitbox.supportPoints(opposNormal, fHitbox.polygon);
			Vector2d computedPointCollision = new Vector2d();
			int size = collidedPoints.size();

			for(int i=0; i<collidedPoints.size();++i){
				computedPointCollision.x+=collidedPoints.get(i).x;
				computedPointCollision.y+=collidedPoints.get(i).y;
			}
			computedPointCollision.x/=size;
			computedPointCollision.y/=size;

			firstPos = new Point((int)computedPointCollision.x-x_eff_center, (int)computedPointCollision.y-y_eff_center);
		}
		//Point to start with the top of the pilar at the impact point 
		Point correctInit = groundEffect? alignWithPilar(MAX_LENGTH) : new Point();
		setXpos_sync(firstPos.x+correctInit.x);
		setYpos_sync(firstPos.y+correctInit.y);

	}

	/**
	 * 
	 * @param value
	 * @return if pilar is rotated by 45° and we want to increase its size by value, use this function to get the deltaX and deltaY to apply
	 */
	protected Point alignWithPilar(double value)
	{
		return new Point((int)(-value*Math.sin(getRotation())),(int)(value*Math.cos(getRotation())));
	}

	protected void growPilar(double update_length)
	{
		pilar_length+=update_length;
		Point deltaGrow = groundEffect? alignWithPilar(update_length) : new Point();
		//if delta grow is 0,-2 , the pilar is growing towards the up direction so we want to lower the position by 0,-2 so that the bottom 
		//of the pilar stays on the ground
		addXpos_sync(-deltaGrow.x);
		addYpos_sync(-deltaGrow.y);
	}
	protected void revertGrowth(double update_length,List<Collidable> collidableToMove, List<Point> motionToApply)
	{
		growPilar(-update_length);
		updateHitbox();
		stopGrowing=true;
		lastTimePilarFullLength=PartieTimer.me.getElapsedNano();

		//revert objects position 
		for(int j=0; j<collidableToMove.size() ; ++j){
			Collidable colMove = collidableToMove.get(j);
			colMove.addXpos_sync(-motionToApply.get(j).x);
			colMove.addYpos_sync(-motionToApply.get(j).y);
		}
	}


}
