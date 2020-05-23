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

	public boolean peutSauter = true;
	public boolean sautDroit= false;
	public boolean sautGauche= false;
	private boolean wasGrounded=false;
	private boolean prevDirectionWasRight = false;
		
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

		finSaut=false;
		peutSauter=false;
		glisse=false;

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
	/***
	 * Handle non mouvement based inputs
	 * @param partie
	 */
	protected void handleInputs(AbstractModelPartie partie){
		if(controlledBy== null)
			AI(partie.tabTirMonstre,partie);
	}
	private Collidable selectTarget(AbstractModelPartie partie){
		return partie.heros; //by default, should be more complex later including clones or enemies if this entity is controlled by heros
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
	public void AI (List<Projectile> tabTirMonstre,AbstractModelPartie partie)
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
		boolean monsterOnScreen= InterfaceConstantes.SCREEN.polygon.contains(new Point (getXpos()+partie.xScreendisp,getYpos()+partie.yScreendisp));
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
			Collidable target = selectTarget(partie);
			Hitbox target_hit = target.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
			Vector2d targetCenter = target_hit.getCenter();
	
			Hitbox monstre_hit = this.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
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
				boolean blocLeftDown= nearObstacle(partie,-1,-1);
				boolean blocRightDown= nearObstacle(partie,1,-1);

				boolean blocRight= nearObstacle(partie,1,0);
				boolean blocLeft= nearObstacle(partie,-1,0);
				
				boolean blocRightUp= nearObstacle(partie,1,InterfaceConstantes.TAILLE_BLOC);
				boolean blocLeftUp= nearObstacle(partie,-1,InterfaceConstantes.TAILLE_BLOC);
				
				boolean jumpAbove= (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)? (blocLeft && !blocLeftUp) : (blocRight && !blocRightUp) ) && peutSauter;
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
	
	private void setMouvement(AbstractModelPartie partie,Mouvement newMouv, int newMouvIndex){
		System.out.println("Set mouvement "+ newMouv+" "+newMouvIndex);
		if(!getMouvement().getTypeMouv().equals(EntityTypeMouv.SAUT) && newMouv.getTypeMouv().equals(EntityTypeMouv.SAUT)){
			peutSauter = false;
		}
		if(wasGrounded){
			useGravity=false;
			peutSauter=true;
			sautDroit=false;
			sautGauche=false;
			finSaut=false;
		}
		setMouvement(newMouv);
		setMouvIndex(newMouvIndex);
	}
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected boolean updateMouvementBasedOnPhysic(AbstractModelPartie partie){
		ModelPrincipal.debugTime.startElapsedForVerbose();
		boolean isFacingLeft = getMouvement().droite_gauche(getMouvIndex(), getRotation()).equals(DirSubTypeMouv.GAUCHE);
		boolean falling= !isGrounded(partie);
		wasGrounded = !falling;
		boolean landing= (finSaut||!falling) && getMouvement().isMouvement(EntityTypeMouv.SAUT);
		if(falling)
			useGravity=falling;
		//update variable since the spirel can be ejected 
		this.peutSauter=!falling;
		this.finSaut=this.finSaut && !falling;
		
		ModelPrincipal.debugTime.elapsed("Spirel chang mouv: init var");
		//chute
		if(falling && !getMouvement().getTypeMouv().equals(EntityTypeMouv.SAUT))
		{
			int nextMouvIndex = isFacingLeft? 0 : 1;
			//no fall animation, put the jump instead
			Mouvement_entity nextMouv=new Saut(ObjectType.SPIREL,isFacingLeft?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE, partie.getFrame());
			
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		ModelPrincipal.debugTime.elapsed("Spirel chang mouv: falling");
		//atterrissage
		if(landing)
		{
			int nextMouvIndex = isFacingLeft? 0 : 1;
			Mouvement_entity nextMouv=new Attente(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
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
	protected boolean updateNonInterruptibleMouvement(AbstractModelPartie partie){
		return false;
	}
	/***
	 * 
	 * @return true if mouvement updated
	 */
	protected boolean updateMouvementBasedOnInput(AbstractModelPartie partie){
		//First handle released (shoot & move) then handle press (shoot,jump,move)
		boolean isFacingLeft = getMouvement().droite_gauche(getMouvIndex(), getRotation()).equals(DirSubTypeMouv.GAUCHE);
		boolean falling = !isGrounded(partie);
		if(getCurrentInputPool().isInputReleased(InputTypeArray.SHOOT,0)){
			
			int nextMouvIndex = isFacingLeft?0:1;
			Mouvement nextMouv = new Attente(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
			}
			else
				return false;
			//shoot projectile
			double tir1_rotation = droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)?Math.PI : 0;
			double tir2_rotation = 3*Math.PI/2;
			
			//middle of the spirel hitbox
			Vector2d spirelMid = Hitbox.getObjMid(partie, this);
			
			partie.tabTirMonstre.add(createProjectile(partie,spirelMid,tir1_rotation,getScaling()));	
			partie.tabTirMonstre.add(createProjectile(partie,spirelMid,tir2_rotation,getScaling()));	
			
			cooldown=true;
			return true;
		}
		else if(getCurrentInputPool().isInputReleased(InputType.LEFT) || getCurrentInputPool().isInputReleased(InputType.RIGHT)){
			boolean shouldStop = (getCurrentInputPool().isInputReleased(InputType.LEFT) && isFacingLeft) || (getCurrentInputPool().isInputReleased(InputType.RIGHT) &&!isFacingLeft);
			if(shouldStop){
				if(falling){
					int nextMouvIndex = isFacingLeft?0:1;
					Mouvement nextMouv = new Saut(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
					
					boolean isNextDirectionLeft = nextMouv.droite_gauche(nextMouvIndex,0).equals(DirSubTypeMouv.GAUCHE);
					boolean isSameMouvement = getMouvement().isMouvement(EntityTypeMouv.SAUT) && ((isFacingLeft&& isNextDirectionLeft)||(!isFacingLeft && !isNextDirectionLeft));
					
					if(!isSameMouvement){
						boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
						if(success){
							setMouvement(partie,nextMouv,nextMouvIndex);
							return true;
						}
					}
				}
				else{
					int nextMouvIndex = isFacingLeft?0:1;
					Mouvement nextMouv = new Attente(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
					
					boolean isNextDirectionLeft = nextMouv.droite_gauche(nextMouvIndex,0).equals(DirSubTypeMouv.GAUCHE);
					boolean isSameMouvement = getMouvement().isMouvement(EntityTypeMouv.ATTENTE) && ((isFacingLeft&& isNextDirectionLeft)||(!isFacingLeft && !isNextDirectionLeft));
					
					if(!isSameMouvement){
						boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
						if(success){
							setMouvement(partie,nextMouv,nextMouvIndex);
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
				Mouvement nextMouv = new Tir(ObjectType.SPIREL,isFacingLeft?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
				boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					setMouvement(partie,nextMouv,nextMouvIndex);
					return true;
				}
			}
		}
		else if(getCurrentInputPool().isInputDown(InputType.JUMP)){
			int nextMouvIndex = isFacingLeft?0:1;
			Mouvement nextMouv = new Saut(ObjectType.SPIREL,isFacingLeft?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame());
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		else if(getCurrentInputPool().isInputDown(InputType.LEFT) || getCurrentInputPool().isInputDown(InputType.RIGHT)){
			boolean rightAndLeftPressedTogether = getCurrentInputPool().isInputDown(InputType.LEFT) &&  getCurrentInputPool().isInputDown(InputType.RIGHT);
			boolean shouldMoveRight = rightAndLeftPressedTogether? !prevDirectionWasRight:getCurrentInputPool().isInputDown(InputType.RIGHT); 
			if(falling){
				//if(!getMouvement().isMouvement(EntityTypeMouv.SAUT)){
				int nextMouvIndex = !shouldMoveRight?0:1;
				Mouvement nextMouv = new Saut(ObjectType.SPIREL,!shouldMoveRight?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame());
				
				boolean isSameMouvement = getMouvement().isMouvement(EntityTypeMouv.SAUT) && ((!shouldMoveRight&& isFacingLeft)||(shouldMoveRight && !isFacingLeft));
				System.out.println("shouldMoveright "+ shouldMoveRight +" isFacingRight "+ !isFacingLeft+" same mouv "+ isSameMouvement);
				if(!isSameMouvement){
					boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
					if(success){
						sautDroit = shouldMoveRight;
						sautGauche = !shouldMoveRight;
						setMouvement(partie,nextMouv,nextMouvIndex);
						return true;
					}
				}else{
					sautDroit = shouldMoveRight;
					sautGauche = !shouldMoveRight;
				}
			}
			else{
				int nextMouvIndex = !shouldMoveRight?0:2;
				Mouvement nextMouv = new Marche(ObjectType.SPIREL,!shouldMoveRight?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
				
				boolean isSameMouvement = getMouvement().isMouvement(EntityTypeMouv.MARCHE) && ((!shouldMoveRight&& isFacingLeft)||(shouldMoveRight && !isFacingLeft));
				
				if(!isSameMouvement){
					boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
					if(success){
						setMouvement(partie,nextMouv,nextMouvIndex);
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
	protected boolean updateMouvementBasedOnAnimation(AbstractModelPartie partie){
		int nextMouvIndex = getMouvement().updateAnimation(getMouvIndex(), partie.getFrame(),conditions.getSpeedFactor());
		if(getMouvIndex() != nextMouvIndex){
			boolean success = alignNextMouvement(partie,getMouvement(), nextMouvIndex);
			if(success){
				setMouvement(partie,getMouvement(),nextMouvIndex);
				return true;
			}
		}
		return false;
	}
	/***
	 * Callback that is called before updating the animation in deplace() function
	 */
	protected void resetInputState(AbstractModelPartie partie){
		getCurrentInputPool().updateInputState();
	}
	/***
	 * Callback that is called before updating the animation in deplace() function
	 */
	protected void onMouvementChanged(AbstractModelPartie partie,boolean animationChanged, boolean mouvementChanged){
	}
	protected void onAnimationEnded(AbstractModelPartie partie){
		destroy(partie,true);
	}
	protected void updateTimers(){
		updateShootTime();
	};

	/**
	 * Gère l'ensemble des événements lié au deplacement d'un monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours 
	 */		
	//@Override
	/* ***REMOVEpublic boolean deplac(AbstractModelPartie partie)
	{
		//ModelPrincipal.debugTime.startElapsedForVerbose();
		//updateShootTime();
		//ModelPrincipal.debugTime.elapsed("Spirel update shoot time" );
		//compute the next desired movement 
		IA(partie.tabTirMonstre,partie.heros,partie);
		ModelPrincipal.debugTime.elapsed("Spirel IA");
		//compute the true next movement depending on landing, gravity, ... 
		changeMouv(partie);
		ModelPrincipal.debugTime.elapsed("Spirel changeMouv");
		return true;//move the object
	}*/
	
	/**
	 * IA pour le deplacement du monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours  
	 */	
	
	// ***REMOVE @Override
	/**
	 * 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param Monde, le niveau en cours 
	 */	
	/* ***REMOVE public void changeMouv (AbstractModelPartie partie)
	{
		ModelPrincipal.debugTime.startElapsedForVerbose();
		boolean herosAGauche= getXpos()-(partie.heros.getXpos()-partie.xScreendisp)>=0;
		boolean falling= !isGrounded(partie);
		wasGrounded = !falling;
		boolean landing= (finSaut||!falling) && getMouvement().isMouvement(MouvEntityEnum.SAUT);
		if(falling)
			useGravity=falling;
		//update variable since the spirel can be ejected 
		this.peutSauter=!falling;
		this.finSaut=this.finSaut && !falling;
		
		ModelPrincipal.debugTime.elapsed("Spirel chang mouv: init var");
		//chute
		if(falling)
		{
			peutSauter=false;
			int nextMouvIndex = herosAGauche? 0 : 1;
			//no fall animation, put the jump instead
			Mouvement_entity depSuiv=new Saut(ObjectType.SPIREL,herosAGauche?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE, partie.getFrame());
			alignHitbox(getMouvIndex(),depSuiv,nextMouvIndex,partie);

			//le monstre tombe, on met donc son animation de saut
			setMouvIndex(nextMouvIndex);
			setMouvement(depSuiv);
			getMouvement().setSpeed(this,getMouvIndex());

		}
		ModelPrincipal.debugTime.elapsed("Spirel chang mouv: falling");
		//atterrissage
		if(landing)
		{
			int nextMouvIndex = herosAGauche? 0 : 1;
			Mouvement_entity depSuiv=new Attente(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			alignHitbox(getMouvIndex(),depSuiv,nextMouvIndex,partie);
			setMouvIndex(nextMouvIndex);
			setMouvement(depSuiv);
			getMouvement().setSpeed(this,getMouvIndex());
			useGravity=false;
			peutSauter=true;
			sautDroit=false;
			sautGauche=false;
			finSaut=false;
			ModelPrincipal.debugTime.elapsed("Spirel chang mouv: landing");
		}
		//on execute l'action voulue
		else
		{
			if(doitChangMouv)
			{
				//monstre.actionReussite= (decallageMonstre(monstre,monstre.nouvMouv,monstre.mouv_index,monstre.nouvAnim,false,false,partie));
				alignHitbox(getMouvIndex(),nouvMouv,newMouvIndex,partie);

				setMouvement(nouvMouv);
				setMouvIndex(newMouvIndex);
				getMouvement().setSpeed(this,getMouvIndex());
				ModelPrincipal.debugTime.elapsed("Spirel chang mouv: doit changer mouv");

			}
			else 
			{
				int nextMouvIndex = getMouvement().updateAnimation(getMouvIndex(), partie.getFrame(),conditions.getSpeedFactor());
				if(getMouvIndex() != nextMouvIndex){
					alignHitbox(getMouvIndex(),getMouvement(),nextMouvIndex,partie);
					setMouvIndex(nextMouvIndex);
				}
				getMouvement().setSpeed(this, getMouvIndex());
				ModelPrincipal.debugTime.elapsed("Spirel chang mouv: meme mouv");
			}
		}
	}*/
	/**
	 * Align to the rigth/left/up/down the next movement/hitbox to the previous one
	 * @param monstre
	 * @param currentMouvIndex
	 * @param depSuiv
	 * @param nextMouvIndex
	 * @param partie
	 */
	public boolean alignNextMouvement(AbstractModelPartie partie,Mouvement nextMouv, int nextMouvIndex)
	{
		boolean going_left = getGlobalVit().x<0;
		boolean facing_left_still= getGlobalVit().x==0 &&(droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)|| last_colli_left);
		boolean sliding_left_wall = (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.DROITE)) ;
		
		boolean left = ( going_left|| facing_left_still ||sliding_left_wall) ; 
		boolean down = getGlobalVit().y>=0; 
		
		boolean success = false;
		try{
			System.out.println("Align "+ nextMouv +" "+nextMouvIndex);
			success = super.alignNextMouvement(partie, nextMouv, nextMouvIndex, left? XAlignmentType.LEFT : XAlignmentType.RIGHT,
					down?YAlignmentType.BOTTOM : YAlignmentType.TOP , true, !nextMouv.isMouvement(EntityTypeMouv.GLISSADE));
		} catch(Exception e){e.printStackTrace();}
		
		return success;

	}

	private TirSpirel createProjectile(AbstractModelPartie partie, Vector2d pos,double rotation,Vector2d scaling){
		return new TirSpirel(partie,pos,
				0,rotation,scaling,partie.getFrame(),conditions.getDamageFactor(),conditions.getShotSpeedFactor());
	}
	public boolean isGrounded(AbstractModelPartie partie)
	{
		return nearObstacle(partie,0,-1);
	}

	/**
	 * Test if there is a bloc at a specific height to the right or left 
	 * @param partie
	 * @param right value from which the hitbox is shifted to the right (negative for left)
	 * @param height the height to shift the hitbox, positive is towards the top of the screen
	 * @return
	 */
	public boolean nearObstacle(AbstractModelPartie partie,int right,int height)
	{
		Hitbox hit = getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();
		assert hit.polygon.npoints==4;
		
		hit.translate(right, -1*height);
		return Collision.isWorldCollision(partie, hit, true);
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
	public void handleStuck(AbstractModelPartie partie)
	{
		if(currentValue!=null)
			currentValue.res();
		
		if(resetHandleCollision != null)
			resetHandleCollision.reset();
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {
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
	public void onDestroy(AbstractModelPartie partie)
	{
		MusicBruitage.me.startBruitage("destruction robot");
	}
	@Override
	public void applyFriction(double minlocalSpeed, double minEnvirSpeed) {

		
	}

}
