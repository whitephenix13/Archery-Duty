package effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import collision.GJK_EPA;
import conditions.Condition;
import debug.Debug_stack;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import fleches.Fleche;
import partie.AbstractModelPartie;
import partie.PartieTimer;
import personnage.Heros;
import types.Entitie;
import types.Hitbox;
import types.Vitesse;

public class Roche_effect extends Effect{

	boolean type0 ;

	double DUREE_DEFAILLANCE = 3;

	double UPDATE_LENGTH_TIME= 0.01; //10ms
	double MAX_UPDATE_LENGTH = 2;//2
	double update_length = 0;
	double lastLengthUpdate=-1;

	boolean stopGrowing=false;
	boolean lastStopGrowingUpdate = true;
	int pilar_length = 1;
	int MAX_LENGTH = 100;
	public boolean isGrowing(){return !stopGrowing;}

	public double PILAR_DURATION= 3; //3sec 
	double lastPilarFullLength = -1; //used with pilar duration to time the destruction
	public boolean startDestroyAnim =false;

	List<Collidable> accrochedCol = new ArrayList<Collidable>();

	public void registerAccrocheCol(Collidable col)
	{
		if(!type0)
			return;

		if(!accrochedCol.contains(col)){
			accrochedCol.add(col);
		}
	}
	public void unregisterAccrocheCol(Collidable col)
	{
		if(!type0)
			return;

		accrochedCol.remove(col);
	}

	private BufferedImage convertedIm=null;

	public Roche_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision, int typeEffect)
	{
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,typeEffect,typeEffect == 0);
		this.setCollideWithAll();
		type0 = typeEffect==0;

		this.isWorldCollider= type0? true :false;

		if(type0){
			xtaille =  Arrays.asList(100,100,100,100,100);
			ytaille =  Arrays.asList(99,100,100,100,100);
			maxnumberloops = -1;
		}
		else{
			xtaille =  Arrays.asList(100,100,100,100);
			ytaille =  Arrays.asList(100,100,100,100);
			maxnumberloops = 1;
			hitbox= Hitbox.createSquareHitboxes(0,0,99,99,4);
		}

		setFirstPos(partie);

		if(type0)
			updateHitbox(partie);
		else
			this.onUpdate(partie, false); //update rotated hitbox and drawtr

		partie.arrowsEffects.add(this);

		int start_index =0;
		int end_index = type0? 1 : 4;
		List<Integer> animTimes= type0? Arrays.asList(10,4,8,12,16):Arrays.asList(4,8,12,16);
		animation.start(animTimes, current_frame, start_index, end_index);
		localVit= new Vitesse();

		int initialEject = 2;
		ejectCollidable(partie,initialEject,true);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr

		//if the eject failed, the object is stuck (for example when arrow roche shot in corner) then destroy it
	}

	@Override
	public boolean requestMoveBy(AbstractModelPartie partie,Collidable ref_object,Point motion,List<Collidable> collidableToMove, List<Point> motionToApply)
	{

		if(!type0)
			super.requestMoveBy(partie, ref_object, motion, collidableToMove, motionToApply);
		
		if(this.checkCollideWithWorld())
			if(Collision.testcollisionObjects(partie, this, ref_object, false))
			{
				//eject the object by only considering the world collision (not effect because they can be pushed)
				Point appliedMotion = new Point();//not set to null so that we can retrieve the desired motion
				boolean considerEffects = false;
				if(!Collision.ejectWorldCollision(partie, this,ref_object,motion,appliedMotion,considerEffects)){
					return false; // ejection was not successful,  "this" is preventing ref_object to move, return false
				}

				//apply motion for this
				collidableToMove.add(this);
				motionToApply.add(appliedMotion);
				this.pxpos(appliedMotion.x);
				this.pypos(appliedMotion.y);

				//Check if perpendicular is not a problem : 
				boolean noCollision = checkPerpendicularValid(partie, collidableToMove,motionToApply);
				if(!noCollision)
					return false;
				
				noCollision = checkSynchronizeValid(partie,collidableToMove,motionToApply);
				if(!noCollision)
					return false;
				
				//check all current collision with effectand entities 
				List<Collidable> effectEntitieColliding = Collision.getAllEffectEntitieCollision(partie, this, motion);
				effectEntitieColliding.remove(this);
				//for all current collisions != ref_object 
				for(Collidable col : effectEntitieColliding)
				{
					if(col == ref_object){
						return false;
					}

					//call requestMoveBy (try to push it) and return false if fail
					if(!col.requestMoveBy(partie, this, motion, collidableToMove, motionToApply))
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
	private boolean checkSynchronizeValid(AbstractModelPartie partie,List<Collidable> collidableToMove, List<Point> motionToApply)
	{
		boolean success=true;
		for(Collidable col : synchroSpeed)
		{
			if(col.checkCollideWithNone())
				continue;
			//If collider : check collision (true). If effect, only check collision if worldCollider
			boolean isEffect = ( col instanceof Effect);
			boolean isCollidable= isEffect ?  ((Effect)col).isWorldCollider : true ; 
			boolean collideWithWorld = Collision.isWorldCollision(partie, col, true);
			boolean collideWithEntiteEffect = Collision.getAllEffectEntitieCollision(partie, col, new Point(0,0)).size()>0;
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
	private boolean checkPerpendicularValid(AbstractModelPartie partie,List<Collidable> collidableToMove, List<Point> motionToApply)
	{
		boolean success=true;
		for(Collidable col : accrochedCol)
		{
			Vector2d xydir_effect = Deplace.angleToVector(rotation-Math.PI/2); 
			Vector2d xydir_col = Deplace.angleToVector(col.deplacement.droite_gauche(col, col.anim).equals(Mouvement.GAUCHE) ? 5.0*Math.PI/4: 7.0*Math.PI/4);

			Vector2d effect_sp = Hitbox.supportPoint(xydir_effect, getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);
			Vector2d col_sp = Hitbox.supportPoint(xydir_col, col.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);
			
			boolean considerTouch =true;
			//pillar goes up
			if(rotation == 0)
			{
				double dy= effect_sp.y -col_sp.y;
				col.pypos_sync((int) dy);
				collidableToMove.add(col);
				motionToApply.add(new Point(0,(int)dy));
			}
			else if(rotation == Math.PI/2 || (rotation == -Math.PI/2))
			{
				double ejectx = (rotation == Math.PI/2) ? 1 : -1;
				double dx= effect_sp.x -col_sp.x+ejectx;
				col.pxpos_sync((int) dx);
				collidableToMove.add(col);
				motionToApply.add(new Point((int)dx,0));
			}
			if(Collision.isWorldCollision(partie, col, considerTouch))
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
				if(Collision.isWorldCollision(partie, col, true))
				{
					success=false;
					break;
				}
			}
			
		}
		return success;

	}
	@Override
	public int getMaxBoundingSquare()
	{
		if(type0)
			return 100;
		else
			return 100;
	}

	private void updateHitbox(AbstractModelPartie partie)
	{
		if(type0)
		{
			int shift = 1; // make the pilar look nicer
			hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(35 ,35 ,27 ,10 ,0),
					Arrays.asList(0,0,0,0,0),
					Arrays.asList(63 ,63 ,76 ,91 ,99),
					Arrays.asList(pilar_length-shift,pilar_length-shift,pilar_length-shift,pilar_length-shift,pilar_length-shift));
			this.onUpdate(partie, false); //update rotated hitbox and drawtr

		}
	}

	/**
	 * 
	 * @param current_frame
	 * @param force: force the destroy anim: first check if it was not already called. Also make sure that the pilar stop growing 
	 */
	public void startDestroyAnim(int current_frame,boolean force)
	{
		if(type0)
		{
			if(force)
			{
				//destruction was already called
				if(startDestroyAnim)
					return;
				
				//else make sure to stop growing before calling destroy 
				stopGrowing=true;
				lastPilarFullLength=UPDATE_LENGTH_TIME*Math.pow(10, 9)+1;
				startDestroyAnim=true;
			}
			this.setCollideWithNone();
			int start_index =1;
			int end_index = 5;
			animation.resetAnimationEnded();
			animation.start(animation.getAnimationFrame(), current_frame, start_index, end_index);
			maxnumberloops = 1;
		}
	}
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(!type0)
			if(Collision.testcollisionObjects(partie, this, attacher,true))
				attacher.conditions.addNewCondition(Condition.DEFAILLANCE, DUREE_DEFAILLANCE,System.identityHashCode(this));
	}

	private void ejectCollidable(AbstractModelPartie partie,int initEject,boolean init)
	{
		if(!type0)
			return;
		//Compute the value by which the length of the pilar has to be updated 
		if((pilar_length+ MAX_UPDATE_LENGTH) > this.MAX_LENGTH)
			update_length = MAX_LENGTH - pilar_length;
		else
			update_length = this.MAX_UPDATE_LENGTH;

		if(init)
			update_length=0;
		growPilar(update_length);
		updateHitbox(partie);


		boolean couldntGrow = false; // true if when growing pilar is stuck in ground 
		//Variables to handle the case when an object is stuck
		List<Collidable> collidableToMove = new ArrayList<Collidable>();  //Collidable that were moved
		List<Point> motionToApply = new ArrayList<Point>();  // motion that was applied

		if(Collision.isWorldCollision(partie, this, true,false))
			couldntGrow=true;

		//check perpendicular 
		if(!couldntGrow)
		{
			couldntGrow=!checkPerpendicularValid(partie, collidableToMove, motionToApply);
		}
		
		if(!couldntGrow)
		{
			couldntGrow=!checkSynchronizeValid(partie,collidableToMove,motionToApply);
		}
		//check recursivity 
		if(!couldntGrow)
		{
			//Test if the movement doesn't stuck any other objects
			List<Entitie> allEntities = Collidable.getAllEntitiesCollidable(partie,this);
			List<Collidable>  allEffects = Collidable.getAllCollidableEffect(partie, this);
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
				if(!col.requestMoveBy(partie, this, motion, collidableToMove, motionToApply)){
					//Stop pilar growth and revert its motion
					couldntGrow=true;
					break; 
				}

				//Otherwise, object has been moved in requestMoveBy, check if the screen has to be updated 
				if(col.controlScreenMotion){
					Point delta = Deplace.getdeplaceEcran(partie,(Heros)col,true);
					Deplace.deplaceEcran(delta,partie,col);
				}

			}
		}

		//If there was a problem, revert motion
		if(couldntGrow)
		{
			//if couldn't eject objects on init, then it has to be destroyed (ie: stuck in world)
			if(init)
				this.destroy(partie, true);
			this.revertGrowth(update_length, partie, collidableToMove, motionToApply);
		}

	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace, boolean update_with_speed) {

		//update the hitbox : 
		if(type0){
			if(!stopGrowing && (pilar_length < MAX_LENGTH) && (PartieTimer.me.getElapsedNano() - lastLengthUpdate)>UPDATE_LENGTH_TIME*Math.pow(10, 9))
			{
				ejectCollidable(partie,0,false);
				if(pilar_length == MAX_LENGTH)
				{
					stopGrowing=true;
					lastPilarFullLength=PartieTimer.me.getElapsedNano();
				}
				lastLengthUpdate=PartieTimer.me.getElapsedNano();
			}
			
			if(!startDestroyAnim && stopGrowing&& (PartieTimer.me.getElapsedNano()-lastPilarFullLength)>PILAR_DURATION*Math.pow(10, 9))
			{
				startDestroyAnim(partie.getFrame(),false);
				startDestroyAnim=true;
			}
		}
		//updatePos(partie);

		int prevAnim = anim;
		anim=animation.update(anim,partie.getFrame(),1);
		if(prevAnim!=anim)
			previousMaskedIm=null;
		//doit deplace, change anim
		boolean[] res = {true,false};
		return res;
	}

	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}

	@Override
	public Image applyFilter(AbstractModelPartie partie, Image im) {
		if(!type0)
			return im;
		int width = im.getWidth(null);
		int height = im.getHeight(null);

		if(width==-1 || height == -1 )
			return im;

		boolean stopUpdate = stopGrowing && anim ==0;
		if( stopUpdate && !lastStopGrowingUpdate)
			return previousMaskedIm;

		int start_filter = pilar_length;
		if(previousMaskedIm==null){
			previousMaskedIm = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
			convertedIm=partie.toBufferedImage(im);

			//deep copy of converted image into previous masked im
			ColorModel cm = convertedIm.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = convertedIm.copyData(null);
			previousMaskedIm=new BufferedImage(cm, raster, isAlphaPremultiplied, null);

		}
		previousMaskedIm=partie.apply_height_mask(convertedIm,previousMaskedIm,start_filter,1);

		if(stopUpdate)
			lastStopGrowingUpdate=false;
		return previousMaskedIm;
	}


	public void setFirstPos(AbstractModelPartie partie) {

		int divider = type0? 1:2;
		int pilar_correction = type0? pilar_length :0;//take into account that at the start, the pilar is already of size pilar_length
		//get the middle bottom of the effect
		int x_eff_center = (int) ((xtaille.get(anim))/2 * Math.cos(rotation) - ((ytaille.get(anim))/divider+pilar_correction) * Math.sin(rotation));
		int y_eff_center = (int) ((xtaille.get(anim))/2 * Math.sin(rotation) + ((ytaille.get(anim))/divider+pilar_correction) * Math.cos(rotation));
		Point firstPos = new Point();
		if(type0)
		{
			firstPos=super.setFirstPos(new Point(x_eff_center,y_eff_center));
		} 
		else
		{
			Hitbox fHitbox = ref_fleche.getHitbox(partie.INIT_RECT,partie.getScreenDisp());

			Vector2d opposNormal = new Vector2d();
			opposNormal.negate(normalCollision);
			List<Vector2d> collidedPoints = Hitbox.supportsPoint(opposNormal, fHitbox.polygon);
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
		Point correctInit = type0? alignWithPilar(MAX_LENGTH) : new Point();
		xpos_sync(firstPos.x+correctInit.x);
		ypos_sync(firstPos.y+correctInit.y);

	}

	/**
	 * 
	 * @param value
	 * @return if pilar is rotated by 45° and we want to increase its size by value, use this function to get the deltaX and deltaY to apply
	 */
	protected Point alignWithPilar(double value)
	{
		return new Point((int)(-value*Math.sin(rotation)),(int)(value*Math.cos(rotation)));
	}

	protected void growPilar(double update_length)
	{
		pilar_length+=update_length;
		Point deltaGrow = type0? alignWithPilar(update_length) : new Point();
		//if delta grow is 0,2 , the pilar is growing towards the up direction so we want to lower the position by 0,2 so that the bottom 
		//of the pilar stays on the ground
		pxpos_sync(-deltaGrow.x);
		pypos_sync(-deltaGrow.y);
	}
	protected void revertGrowth(double update_length,AbstractModelPartie partie,List<Collidable> collidableToMove, List<Point> motionToApply)
	{
		growPilar(-update_length);
		updateHitbox(partie);
		stopGrowing=true;
		lastPilarFullLength=PartieTimer.me.getElapsedNano();

		//revert objects position 
		for(int j=0; j<collidableToMove.size() ; ++j){
			Collidable colMove = collidableToMove.get(j);
			colMove.pxpos_sync(-motionToApply.get(j).x);
			colMove.pypos_sync(-motionToApply.get(j).y);
		}
	}
	@Override
	protected void updatePos(AbstractModelPartie partie)
	{
		//nothing
	}

	@Override
	public Point getTranslationFromTranformDraw(AbstractModelPartie partie)
	{
		return new Point(xpos()+partie.xScreendisp,ypos()+partie.yScreendisp);
	}

	@Override
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		if(!type0)
			return super.computeTransformDraw(partie);
		else
			return super.computeTransformDrawRotated(partie);
	}

}
