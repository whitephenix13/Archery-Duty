package partie.entitie.monstre;

import java.awt.Point;
import java.util.List;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper.ObjectType;
import menu.menuPrincipal.ModelPrincipal;
import music.MusicBruitage;
import partie.collision.CachedAffineTransform;
import partie.collision.CachedHitbox;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.collision.Collidable.XAlignmentType;
import partie.collision.Collidable.YAlignmentType;
import partie.entitie.heros.Heros;
import partie.input.InputPartie;
import partie.input.InputPartiePool.InputType;
import partie.input.InputPartiePool.InputTypeArray;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.modelPartie.PartieTimer;
import partie.mouvement.Deplace;
import partie.mouvement.Mouvement;
import partie.mouvement.Mouvement.DirSubTypeMouv;
import partie.mouvement.entity.Attente;
import partie.mouvement.entity.Marche;
import partie.mouvement.entity.Mouvement_entity;
import partie.mouvement.entity.Saut;
import partie.mouvement.entity.Mouvement_entity.EntityTypeMouv;
import partie.mouvement.entity.Saut.SubMouvSautEnum;
import partie.mouvement.entity.Tir;
import partie.projectile.Projectile;
import partie.projectile.tirMonstre.TirSpirel;
import utils.Vitesse;

@SuppressWarnings("serial")
public class Spirel extends Monstre{
	private double lastAIUpdate=0;
	private static double delaiMouv= 50;
	private double last_shoot_time=-1;
	private double last_update_shoot_time=-1;

	private static double delaiTir= 2000;//ms
	private double squaredAttackDistance= 160000; // 400^2
	public double getSquaredAttackDistance(){return squaredAttackDistance*getScaling().lengthSquared();} //need to multiply by scaling squared
	private boolean cooldown=true;

	public boolean sautDroit= false;
	public boolean sautGauche= false;
	private boolean prevDirectionWasRight = false;
	
	private boolean shouldUpdateSpeed = true;
	public enum AISpirelAction implements AIAction{WAIT,MOVE_TOWARDS_HERO,MOVE_AWAY_HERO,JUMP,JUMP_ABOVE_OBSTACLE,WAIT_CLOSE_TO_HOLE,JUMP_ABOVE_HOLE,AVOID_HOLE,MOVE_TOWARDS_HOLE,MOVE_AWAY_HOLE,SHOOT};

	/**
	 * constructeur
	 * 
	 * @param xPo, position originale en x
	 * @param yPo, position originale en y
	 * @param _staticSpirel, permet de rendre la spirel immobile (si =true)
	 */	
	public Spirel( int xPo,int yPo,boolean _isStatic,int current_frame,InputPartie inputPartie){
		super(inputPartie);
		isStatic=_isStatic;
		setXpos_sync(xPo);
		setYpos_sync(yPo); 
		localVit= new Vitesse(0,0);
		setMouvement(new Attente(ObjectType.SPIREL,DirSubTypeMouv.GAUCHE,current_frame));
		setMouvIndex(1);

		wasGrounded=false;

		//Param from Collidable
		fixedWhenScreenMoves=false;

		MAXLIFE = 100;
		MINLIFE = 0;
		life= MAXLIFE;
				
		//add randomness to the update time of the spirel so that they don't all update at the same time
		double rand = Math.random();
		lastAIUpdate = PartieTimer.me.getElapsedNano() - rand*delaiMouv*Math.pow(10, 6);
		last_shoot_time = PartieTimer.me.getElapsedNano() - Math.random()*delaiTir *Math.pow(10, 6);
	}
	

	
	
	public void onAddLife(){if(life==MINLIFE){needDestroy=true;}};
	/**
	 * Permet de savoir de quel cote est tourné le monstre
	 * 
	 * @param mouv_index, l'animation du monstre
	 * 
	 * @return String , Mouvement.DROITE ou Mouvement.GAUCHE, direction dans laquelle le monstre est tourné
	 */
	
	private void updateShootTime()
	{
		last_shoot_time -= (System.nanoTime() - last_update_shoot_time) * (conditions.getSpeedFactor()-1); //speed influences the shoot speed
		last_update_shoot_time=System.nanoTime();
	}
	
	@Override
	public DirSubTypeMouv droite_gauche (int mouv_index)
	{
		return getMouvement().droite_gauche(mouv_index, getRotation());
	}
	
	public Vector2d getNormCollision()
	{
		if(wasGrounded)
			return new Vector2d(0,-1);
		else
			return normCollision;
	}
	@Override
	protected void onStartDeplace(){shouldUpdateSpeed=true;}
	/***
	 * Handle non mouvement based inputs
	 * @param partie
	 */
	protected void handleInputs(){
		if(controlledBy== null)
			AI(ModelPartie.me.tabTirMonstre);
	}
	private Collidable selectTarget(){
		return ModelPartie.me.heros; //by default, should be more complex later including clones or enemies if this entity is controlled by heros
	}
	
	private void moveTo(boolean leftDirection){		
		//Only move if not already moving in that direction
		if(!getCurrentInputPool().isInputDown(leftDirection?InputType.LEFT:InputType.RIGHT))
			getCurrentInputPool().onMove(leftDirection?-1:1, false, false);
		//Only release if was moving in that direction
		if(getCurrentInputPool().isInputDown(leftDirection?InputType.RIGHT:InputType.LEFT))
			getCurrentInputPool().onMove(leftDirection?1:-1, false, true);
	}
	private void stopMoving(){
		if(getCurrentInputPool().isInputDown(InputType.RIGHT))
			getCurrentInputPool().onMove(1, false, true);
		if(getCurrentInputPool().isInputDown(InputType.LEFT))
			getCurrentInputPool().onMove(-1, false, true);
	}
	@Override
	public void AI (List<Projectile> tabTirMonstre)
	{	
		if(!getMouvement().isInterruptible(getMouvIndex()))
			return;
		
		//Release pressed input
		if(getCurrentInputPool().isInputDown(InputType.JUMP)){
			lastIAAction = AISpirelAction.JUMP;
			getCurrentInputPool().onJump(true);
		}

		//If shooting and shoot anim ended once, release the shoot key; 
		if(getMouvement().getTypeMouv().equals(EntityTypeMouv.TIR) && getMouvement().animEndedOnce()){
			lastIAAction = AISpirelAction.SHOOT;
			getCurrentInputPool().onShoot(0, true);
			return;
		}
		
		boolean targetAtMyLeft;
		boolean isFacingLeft = getMouvement().droite_gauche(getMouvIndex(), getRotation()).equals(DirSubTypeMouv.GAUCHE);
		boolean monsterOnScreen= InterfaceConstantes.SCREEN.polygon.contains(new Point (getXpos()+ModelPartie.me.xScreendisp,getYpos()+ModelPartie.me.yScreendisp));
		boolean canAction= (PartieTimer.me.getElapsedNano()-lastAIUpdate)*Math.pow(10, -6)>delaiMouv && monsterOnScreen;
		boolean canShoot = (PartieTimer.me.getElapsedNano()-last_shoot_time)*Math.pow(10, -6)>delaiTir ;
		//On test le cooldown de tir
		if(canShoot)
		{
			cooldown=false;
			last_shoot_time=PartieTimer.me.getElapsedNano();
		}
		//on test le cooldown de mouvement
		if(canAction)
		{
			Collidable target = selectTarget();
			Hitbox target_hit = target.getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp());
			Vector2d targetCenter = target_hit.getCenter();
	
			Hitbox monstre_hit = this.getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp());
			Vector2d monsterCenter = monstre_hit.getCenter();
			
			double deltaX= Math.abs(monsterCenter.x-targetCenter.x);
			double deltaY= Math.abs(monsterCenter.y-targetCenter.y);
			
			targetAtMyLeft= monsterCenter.x-targetCenter.x>=0;
			
			boolean closeEnoughToShoot =  deltaX*deltaX+deltaY*deltaY<getSquaredAttackDistance();
			
			boolean shootAllowed=closeEnoughToShoot && ! cooldown && (conditions.getShotSpeedFactor()>0) && !getCurrentInputPool().isInputDown(InputTypeArray.SHOOT,0);
			//If drag and can't shoot, exit 
			if(this.isDragged() && !shootAllowed)
				return;
			//Shoot towards heros
			if(shootAllowed)
			{	
				if((isFacingLeft && targetAtMyLeft) || (!isFacingLeft && !targetAtMyLeft)){
					int decision = (int) (Math.random()*100);
					if(decision<=50){
						lastIAAction = AISpirelAction.SHOOT;
						stopMoving();
						getCurrentInputPool().onShoot(0, false);
					}
				}
				else{
					lastIAAction = AISpirelAction.MOVE_TOWARDS_HERO;
					moveTo(targetAtMyLeft);
				}

			}
			else if (!isStatic) // Target is not in range, try to move towards him
			{
				
				//sinon on se rapproche ou on reste proche 
				boolean blocLeftDown= nearObstacle(-1,-1);
				boolean blocRightDown= nearObstacle(1,-1);

				boolean blocRight= nearObstacle(1,0);
				boolean blocLeft= nearObstacle(-1,0);
				
				boolean blocRightUp= nearObstacle(1,InterfaceConstantes.TAILLE_BLOC);
				boolean blocLeftUp= nearObstacle(-1,InterfaceConstantes.TAILLE_BLOC);
				
				boolean jumpAbove= (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)? (blocLeft && !blocLeftUp) : (blocRight && !blocRightUp) ) && wasGrounded;
				boolean inAir= getMouvement().isMouvement(EntityTypeMouv.SAUT);
				boolean holeClose= (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)? (!blocLeftDown) : (!blocRightDown) );

				//on saute au dessus d'un obstacle si possible
				if( jumpAbove){
					lastIAAction = AISpirelAction.JUMP_ABOVE_OBSTACLE;
					getCurrentInputPool().onJump(false);
				}
				//si on est en l'air, on se deplace
				else if (inAir){
					lastIAAction = AISpirelAction.MOVE_TOWARDS_HERO;
					moveTo(targetAtMyLeft);
				}
				//Si il y a un trou a coté du monstre
				else if(holeClose)
				{
					int decision = (int) (Math.random()*100);
					//on attend 
					if(decision <= 70){
						lastIAAction = AISpirelAction.WAIT_CLOSE_TO_HOLE;
						stopMoving();
					}
					//WARNING need refactor, hole close does not mean that the spirel is facing the hole, therefore the decision makes no sense 
					//on se deplace dans l'autre direction
					else if(decision <= 90){
						lastIAAction = AISpirelAction.MOVE_AWAY_HOLE;
						moveTo(!targetAtMyLeft);
					}
					//Move in the same direction and fall into the hole 
					else{
						lastIAAction = AISpirelAction.MOVE_TOWARDS_HOLE;
						moveTo(targetAtMyLeft);
					}
				}
				//sinon on se deplace
				else
				{
					int decision = (int) (Math.random()*100);
					//deplacement
					if(decision <= 90){
						lastIAAction = AISpirelAction.MOVE_TOWARDS_HERO;
						moveTo(targetAtMyLeft);
					}
					//attente
					else{
						lastIAAction = AISpirelAction.WAIT;
						stopMoving();
					}
				}
			}
			else{ //spirel is static
				lastIAAction = AISpirelAction.WAIT;
				stopMoving();
			}

			lastAIUpdate=PartieTimer.me.getElapsedNano();
		}
	}
	
	private void setMouvement(Mouvement newMouv, int newMouvIndex){
		if(wasGrounded){
			useGravity=false;
			sautDroit=false;
			sautGauche=false;
		}
		setMouvement(newMouv);
		setMouvIndex(newMouvIndex);
	}
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected boolean updateMouvementBasedOnPhysic(){
		ModelPrincipal.debugTime.startElapsedForVerbose();
		boolean isFacingLeft = getMouvement().droite_gauche(getMouvIndex(), getRotation()).equals(DirSubTypeMouv.GAUCHE);
		boolean falling= !isGrounded();
		wasGrounded = !falling;
		boolean landing= !falling && getMouvement().isMouvement(EntityTypeMouv.SAUT);
		if(falling)
			useGravity=falling;
		
		ModelPrincipal.debugTime.elapsed("Spirel chang mouv: init var");
		//chute
		if(falling && !getMouvement().getTypeMouv().equals(EntityTypeMouv.SAUT))
		{
			int nextMouvIndex = isFacingLeft? 0 : 1;
			//no fall animation, put the jump instead
			Mouvement_entity nextMouv=new Saut(ObjectType.SPIREL,isFacingLeft?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE, ModelPartie.me.getFrame());
			
			boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
			if(success){
				setMouvement(nextMouv,nextMouvIndex);
				return true;
			}
		}
		ModelPrincipal.debugTime.elapsed("Spirel chang mouv: falling");
		//atterrissage
		if(landing)
		{
			int nextMouvIndex = isFacingLeft? 0 : 1;
			Mouvement_entity nextMouv=new Attente(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,ModelPartie.me.getFrame());
			boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
			if(success){
				setMouvement(nextMouv,nextMouvIndex);
				return true;
			}
			
			ModelPrincipal.debugTime.elapsed("Spirel chang mouv: landing");
			return true;
		}
		return false;
	}
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected boolean updateNonInterruptibleMouvement(){
		return false;
	}
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected boolean updateMouvementBasedOnInput(){
		//First handle released (shoot & move) then handle press (shoot,jump,move)
		boolean isFacingLeft = getMouvement().droite_gauche(getMouvIndex(), getRotation()).equals(DirSubTypeMouv.GAUCHE);
		boolean falling = !isGrounded();
		if(getCurrentInputPool().isInputReleased(InputTypeArray.SHOOT,0)){
			
			int nextMouvIndex = isFacingLeft?0:1;
			Mouvement nextMouv = new Attente(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,ModelPartie.me.getFrame());
			
			boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
			if(success){
				setMouvement(nextMouv,nextMouvIndex);
			}
			else
				return false;
			//shoot projectile
			double tir1_rotation = droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)?Math.PI : 0;
			double tir2_rotation = 3*Math.PI/2;
			
			//middle of the spirel hitbox
			Vector2d spirelMid = Hitbox.getObjMid( this);
			
			ModelPartie.me.tabTirMonstre.add(createProjectile(spirelMid,tir1_rotation,getScaling()));	
			ModelPartie.me.tabTirMonstre.add(createProjectile(spirelMid,tir2_rotation,getScaling()));	
			
			cooldown=true;
			return true;
		}
		else if(getCurrentInputPool().isInputReleased(InputType.LEFT) || getCurrentInputPool().isInputReleased(InputType.RIGHT)){
			boolean shouldStop = (getCurrentInputPool().isInputReleased(InputType.LEFT) && isFacingLeft) || (getCurrentInputPool().isInputReleased(InputType.RIGHT) &&!isFacingLeft);
			if(shouldStop){
				if(falling){
					int nextMouvIndex = isFacingLeft?0:1;
					Mouvement nextMouv = new Saut(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,ModelPartie.me.getFrame());
					
					boolean isNextDirectionLeft = nextMouv.droite_gauche(nextMouvIndex,0).equals(DirSubTypeMouv.GAUCHE);
					boolean isSameMouvement = getMouvement().isMouvement(EntityTypeMouv.SAUT) && ((isFacingLeft&& isNextDirectionLeft)||(!isFacingLeft && !isNextDirectionLeft));
					
					if(!isSameMouvement){
						boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
						if(success){
							setMouvement(nextMouv,nextMouvIndex);
							return true;
						}
					}
				}
				else{
					int nextMouvIndex = isFacingLeft?0:1;
					Mouvement nextMouv = new Attente(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,ModelPartie.me.getFrame());
					
					boolean isNextDirectionLeft = nextMouv.droite_gauche(nextMouvIndex,0).equals(DirSubTypeMouv.GAUCHE);
					boolean isSameMouvement = getMouvement().isMouvement(EntityTypeMouv.ATTENTE) && ((isFacingLeft&& isNextDirectionLeft)||(!isFacingLeft && !isNextDirectionLeft));
					
					if(!isSameMouvement){
						boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
						if(success){
							setMouvement(nextMouv,nextMouvIndex);
							return true;
						}
					}
				}
			}
		}
		//When shooting is down, either starts shooting or do nothing. But make sure not to trigger move left/right-> 2nd if condition is within this loop
		if(getCurrentInputPool().isInputDown(InputTypeArray.SHOOT,0)){
			if(!getMouvement().isMouvement(EntityTypeMouv.TIR)){
				int nextMouvIndex = isFacingLeft?0:1;
				Mouvement nextMouv = new Tir(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,ModelPartie.me.getFrame());
				boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
				if(success){
					setMouvement(nextMouv,nextMouvIndex);
					return true;
				}
			}
		}
		else if(getCurrentInputPool().isInputDown(InputType.JUMP) && wasGrounded){
			int nextMouvIndex = isFacingLeft?0:1;
			Mouvement nextMouv = new Saut(ObjectType.SPIREL,isFacingLeft?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,ModelPartie.me.getFrame());
			boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
			if(success){
				setMouvement(nextMouv,nextMouvIndex);
				shouldUpdateSpeed = false;
				setJumpSpeed(false,false,true);
				return true;
			}
		}
		else if(getCurrentInputPool().isInputDown(InputType.LEFT) || getCurrentInputPool().isInputDown(InputType.RIGHT)){
			boolean rightAndLeftPressedTogether = getCurrentInputPool().isInputDown(InputType.LEFT) &&  getCurrentInputPool().isInputDown(InputType.RIGHT);
			boolean shouldMoveRight = rightAndLeftPressedTogether? !prevDirectionWasRight:getCurrentInputPool().isInputDown(InputType.RIGHT); 
			if(falling){
				//if(!getMouvement().isMouvement(EntityTypeMouv.SAUT)){
				int nextMouvIndex = !shouldMoveRight?0:1;
				Mouvement nextMouv = new Saut(ObjectType.SPIREL,!shouldMoveRight?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,ModelPartie.me.getFrame());
				
				boolean isSameMouvement = getMouvement().isMouvement(EntityTypeMouv.SAUT) && ((!shouldMoveRight&& isFacingLeft)||(shouldMoveRight && !isFacingLeft));
				if(!isSameMouvement){
					boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
					if(success){
						sautDroit = shouldMoveRight;
						sautGauche = !shouldMoveRight;
						setMouvement(nextMouv,nextMouvIndex);
						return true;
					}
				}else{
					sautDroit = shouldMoveRight;
					sautGauche = !shouldMoveRight;
				}
			}
			else{
				int nextMouvIndex = !shouldMoveRight?0:2;
				Mouvement nextMouv = new Marche(ObjectType.SPIREL,!shouldMoveRight?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,ModelPartie.me.getFrame());
				
				boolean isSameMouvement = getMouvement().isMouvement(EntityTypeMouv.MARCHE) && ((!shouldMoveRight&& isFacingLeft)||(shouldMoveRight && !isFacingLeft));
				
				if(!isSameMouvement){
					boolean success = alignNextMouvement(nextMouv, nextMouvIndex);
					if(success){
						setMouvement(nextMouv,nextMouvIndex);
						return true;
					}
				}
			}
			prevDirectionWasRight  = rightAndLeftPressedTogether?prevDirectionWasRight :   getCurrentInputPool().isInputDown(InputType.RIGHT);
		}
		return false;
	}
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected boolean updateMouvementBasedOnAnimation(){
		int nextMouvIndex = getMouvement().updateAnimation(getMouvIndex(), ModelPartie.me.getFrame(),conditions.getSpeedFactor());
		if(getMouvIndex() != nextMouvIndex){
			boolean success = alignNextMouvement(getMouvement(), nextMouvIndex);
			if(success){
				setMouvement(getMouvement(),nextMouvIndex);
				return true;
			}
		}
		return false;
	}
	/***
	 * Callback that is called before updating the animation in deplace() function
	 */
	protected void resetInputState(){
		getCurrentInputPool().updateInputState();
	}
	/***
	 * Callback that is called before updating the animation in deplace() function
	 */
	protected void onMouvementChanged(boolean animationChanged, boolean mouvementChanged){
	}
	protected void onAnimationEnded(){
		destroy(true);
	}
	protected void updateTimers(){
		updateShootTime();
	};

	/**
	 * Align to the rigth/left/up/down the next movement/hitbox to the previous one
	 * @param monstre
	 * @param currentMouvIndex
	 * @param depSuiv
	 * @param nextMouvIndex
	 * @param partie
	 */
	public boolean alignNextMouvement(Mouvement nextMouv, int nextMouvIndex)
	{
		boolean going_left = getGlobalVit().x<0;
		boolean facing_left_still= getGlobalVit().x==0 &&(droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)|| last_colli_left);
		boolean sliding_left_wall = (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.DROITE)) ;
		
		boolean left = ( going_left|| facing_left_still ||sliding_left_wall) ; 
		boolean down = getGlobalVit().y>=0; 
		
		boolean success = false;
		try{
			success = super.alignNextMouvement( nextMouv, nextMouvIndex, left? XAlignmentType.LEFT : XAlignmentType.RIGHT,
					down?YAlignmentType.BOTTOM : YAlignmentType.TOP , true, !nextMouv.isMouvement(EntityTypeMouv.GLISSADE));
		} catch(Exception e){e.printStackTrace();}
		
		return success;

	}

	private TirSpirel createProjectile( Vector2d pos,double rotation,Vector2d scaling){
		return new TirSpirel(pos,
				0,rotation,scaling,ModelPartie.me.getFrame(),conditions.getDamageFactor(),conditions.getShotSpeedFactor());
	}
	public boolean isGrounded()
	{
		return nearObstacle(0,-1);
	}

	/**
	 * Test if there is a bloc at a specific height to the right or left 
	 * @param partie
	 * @param right value from which the hitbox is shifted to the right (negative for left)
	 * @param height the height to shift the hitbox, positive is towards the top of the screen
	 * @return
	 */
	public boolean nearObstacle(int right,int height)
	{
		Hitbox hit = getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp()).copy();
		assert hit.polygon.npoints==4;
		
		hit.translate(right, -1*height);
		return Collision.isWorldCollision( hit, true);
	}

	@Override
	public void memorizeCurrentValue()
	{
		final Point memPos= new Point(getXpos(),getYpos()); 
		final Mouvement_entity memDep = (Mouvement_entity) getMouvement().Copy();
		final int memMouvIndex = getMouvIndex();
		final Vitesse memVitloca = localVit.Copy();
		final CachedHitbox cachedHit = this.getCacheHitboxCopy();
		final CachedAffineTransform cachedDrawTr = this.getCacheDrawTrCopy();
		currentValue=new CurrentValue(){		
			@Override
			public void res()
			{setXpos_sync(memPos.x);setYpos_sync(memPos.y);setMouvement(memDep);setMouvIndex(memMouvIndex);localVit=memVitloca;
			setCachedHit(cachedHit);setCachedDrawTr(cachedDrawTr);}};
	}
	@Override
	public void handleStuck()
	{
		if(currentValue!=null)
			currentValue.res();
		
		if(resetHandleCollision != null)
			resetHandleCollision.reset();
	}
	@Override
	public void handleDeplacementSuccess() {
	}
	@Override
	protected boolean shouldUpdateSpeed(){
		return shouldUpdateSpeed;
	}
	@Override
	public void resetVarBeforeCollision()
	{
		last_colli_left=false;
		last_colli_right=false;
	}
	@Override
	public void resetVarDeplace(boolean speedUpdated)
	{
		resetHandleCollision=null;
	}
	
	
	@Override
	public void onDestroy()
	{
		MusicBruitage.me.startBruitage("destruction robot");
	}
	@Override
	public void applyFriction(double minlocalSpeed, double minEnvirSpeed) {

		
	}

}
