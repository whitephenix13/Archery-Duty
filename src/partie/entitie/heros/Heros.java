package partie.entitie.heros;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import menu.menuPrincipal.ModelPrincipal;
import music.Music;
import option.Config;
import partie.collision.CachedAffineTransform;
import partie.collision.CachedHitbox;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.GJK_EPA;
import partie.collision.Hitbox;
import partie.conditions.Condition.ConditionEnum;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.monstre.Monstre;
import partie.input.InputPartie;
import partie.input.InputPartiePool;
import partie.input.InputPartiePool.InputType;
import partie.input.InputPartiePool.InputTypeArray;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.PartieTimer;
import partie.mouvement.Deplace;
import partie.mouvement.Gravite;
import partie.mouvement.Mouvement;
import partie.mouvement.Mouvement.DirSubTypeMouv;
import partie.mouvement.Mouvement.SubTypeMouv;
import partie.mouvement.entity.Accroche;
import partie.mouvement.entity.Accroche.SubMouvAccrocheEnum;
import partie.mouvement.entity.Attente;
import partie.mouvement.entity.Course;
import partie.mouvement.entity.Glissade;
import partie.mouvement.entity.Marche;
import partie.mouvement.entity.Mouvement_entity;
import partie.mouvement.entity.Mouvement_entity.EntityTypeMouv;
import partie.mouvement.entity.Saut;
import partie.mouvement.entity.Saut.SubMouvSautEnum;
import partie.mouvement.entity.Tir;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;
import partie.projectile.fleches.destructrice.Fleche_barrage;
import partie.projectile.fleches.destructrice.Fleche_explosive;
import partie.projectile.fleches.destructrice.Fleche_faucon;
import partie.projectile.fleches.destructrice.Fleche_trou_noir;
import partie.projectile.fleches.materielle.Fleche_electrique;
import partie.projectile.fleches.materielle.Fleche_feu;
import partie.projectile.fleches.materielle.Fleche_glace;
import partie.projectile.fleches.materielle.Fleche_roche;
import partie.projectile.fleches.rusee.Fleche_absorption;
import partie.projectile.fleches.rusee.Fleche_leurre;
import partie.projectile.fleches.rusee.Fleche_marque_mortelle;
import partie.projectile.fleches.rusee.Fleche_ninja;
import partie.projectile.fleches.sprirituelle.Fleche_grappin;
import partie.projectile.fleches.sprirituelle.Fleche_lumiere;
import partie.projectile.fleches.sprirituelle.Fleche_ombre;
import partie.projectile.fleches.sprirituelle.Fleche_vent;
import partie.projectile.tirMonstre.TirMonstre;
import utils.Vitesse;

public class Heros extends Entity{
	
	private InputPartie inputPartie;
	public InputPartie getInputPartie(){return inputPartie;}
	private InputPartiePool inputPool;
	// ***REMOVE public int nouvMouvIndex= 0;
	// ***REMOVE public Mouvement nouvMouv =null;
	private boolean prevDirectionWasRight = false;


	private float seyeri=InterfaceConstantes.MAXSEYERI;
	private float not_enough_seyeri=0; // variable to keep track of the amount of seyeri that a rejected action required (used for visual effect)
	public void setNotEnoughSeyeri(float val){not_enough_seyeri=val;not_enough_seyeri_counter=10;}
	public float getNotEnoughSeyeri(){return not_enough_seyeri;}
	private int not_enough_seyeri_counter; // use this to let the red indicaters appear longer
	// ***REMOVE private double accrocheCooldownTimer=0;
	public void decreaseNotEnoughSeyeriCounter()
	{
		if(not_enough_seyeri<=0)
			return;
		not_enough_seyeri_counter=Math.max(not_enough_seyeri_counter-1,0);
		//reset counter
		if(not_enough_seyeri_counter==0)
		{
			not_enough_seyeri_counter=3;
			not_enough_seyeri=0;
		}
	}

	private double tempsTouche;
	private long tempsClignote;

	private double lastSeyeriUpdate;
	public boolean invincible=true;
	private boolean infiniteSeyeri = true;
	//lorsque le personnage est touche, on le fait clignoter, ce booleen permet de savoir si on l'affiche ou non
	public boolean afficheTouche=true; 
	
	
	//booleen pour savoir si on arrive � la fin du saut 
	//public boolean finSaut = false;
	//booleen pour savoir si il est en saut/peut sauter 
	
	//booleen pour savoir si le personnage veut sauter alors qu'il glisse/accroche
	
	public boolean canJump = true;
	public boolean airJumping = false;
	public boolean wallJumping = false;
	public boolean groundJumping = false;
	private boolean isMoveRightKeyDown = false;
	public boolean isMoveRightKeyDown(){return isMoveRightKeyDown;}
	private boolean isMoveLeftKeyDown = false;
	public boolean isMoveLeftKeyDown(){return isMoveLeftKeyDown;}
	private boolean runBeforeJump=false;
	
	private boolean shouldUpdateSpeed = true;
	private boolean wasGrounded =false;

	//public boolean doitEncocherFleche=false;
	//public boolean flecheEncochee=false;
	public int arrowArmedIndex = -1; //same as index from inputPartie#ShootAction
	private Fleche armedArrow = null;
	//Which arrow is currently armed: changed in model partie
	//public ObjectType tir_type = ObjectType.FLECHE;

	//In order to determine affinity: 1=Materiel, 2=Spirituel, 3=Destructeur 4=Ruse
	//public int current_slot = 0;
	//Which arrows are equiped per affinity
	private ObjectType[] slots = {ObjectType.ROCHE,ObjectType.BARRAGE,ObjectType.OMBRE,ObjectType.MARQUE_MORTELLE};
	//Index of the slot that was clicked using the hotkeys
	public int getPressedSlotForUI(){return inputPool.getInputFirstPressed(InputTypeArray.SLOT);}
	
	public ObjectType[] getSlots(){return slots;}
	public void changeSlot(AbstractModelPartie partie,int slotNum,ObjectType newArrow)
	{
		slots[slotNum]=newArrow;
		addSeyeri(partie, -25);
	}

	//last time an arrow was shot
	public long last_shoot_time = -1;
	private long last_update_shoot_time=-1;

	//last time I armed an arrow
	// ***REMOVE public long last_armed_time = -1;
	// ***REMOVE private long last_update_armed_time=-1;
	//last time the heros wall jump: use to disable keys 
	public double last_wall_jump_time = -1;
	
	//booleen pour savoir si on veut deplacer le personnage sur le c�t� quand il saut 
	//public boolean deplaceSautDroit = false;
	//public boolean deplaceSautGauche =false;

	//objet pour connaitre les valeurs comme la taille des sprites pour une action donn�e

	public double rotation_tir=0;
	protected ResetHandleCollision resetHandleCollision;

	//variable pour deplace ecran 
	private boolean last_align_left=true;
	public boolean getLast_align_l(){return last_align_left;}
	private boolean last_align_down=true;
	public boolean getLast_align_d(){return last_align_down;}
	
	//Note: for online gaming, input partie should be simulated with data received via online connexion
	public Heros( int xPo,int yPo, int _mouv_index, int current_frame, InputPartie inputPartie){
		super();
		MAXLIFE = 100;
		MINLIFE = 0;
		life= MAXLIFE;
		setXpos_sync(xPo);
		setYpos_sync(yPo); 
		localVit= new Vitesse(0,0);
		fixedWhenScreenMoves=true;
		setMouvement(new Attente(ObjectType.HEROS,DirSubTypeMouv.GAUCHE,current_frame));
		this.setMouvIndex(_mouv_index);
		// ***REMOVE nouvMouvIndex= 0;
		// ***REMOVE nouvMouv = new Attente(ObjectType.HEROS,DirSubTypeMouv.GAUCHE,current_frame);
		tempsTouche=PartieTimer.me.getElapsedNano();
		controlScreenMotion=true;
		last_update_shoot_time=-1;
		// ***REMOVE last_update_armed_time=-1;
		this.inputPartie = inputPartie;
		inputPool = new InputPartiePool(inputPartie);
		inputPool.setPlayerInputPool(true);
		this.setCollideWithout(Arrays.asList(ObjectType.HEROS,ObjectType.FLECHE));
	}

	public void onAddLife(){};

	public DirSubTypeMouv droite_gauche (int mouv_index){
		return getMouvement().droite_gauche(mouv_index,this.getRotation());
	}

	public void touche (float degat) 
	{
		tempsTouche=PartieTimer.me.getElapsedNano();
		afficheTouche=false;
		addLife(degat);
		invincible=true;
	}
	public void miseAjourTouche()
	{
		if((PartieTimer.me.getElapsedNano()-tempsTouche)*Math.pow(10, -6)<=InterfaceConstantes.INV_TOUCHE && invincible)//heros invincible
		{
			if((System.nanoTime()-tempsClignote)*Math.pow(10, -6)>InterfaceConstantes.CLIGNOTE)
			{
				afficheTouche=!afficheTouche;
				tempsClignote=System.nanoTime();
			}
		}
		else
		{
			afficheTouche=true;
			invincible=false;
		}
	}
	public void miseAJourSeyeri(AbstractModelPartie partie)
	{
		if((PartieTimer.me.getElapsedNano()-lastSeyeriUpdate)*Math.pow(10, -6)>InterfaceConstantes.TEMPS_VAR_SEYERI)
		{
			lastSeyeriUpdate=PartieTimer.me.getElapsedNano();
			if(partie.slowDown)
				addSeyeri(partie,-0.15f * InterfaceConstantes.SLOW_DOWN_FACTOR);
			
			else
				addSeyeri(partie,0.25f * InterfaceConstantes.SLOW_DOWN_FACTOR);
		}
	}

	public float getSeyeri()
	{
		return(seyeri);
	}

	/**
	 * 
	 * @param partie
	 * @param add
	 * @return false if reached minSeyeri
	 */
	public boolean seyeriActionPossible(float cost)
	{
		if((seyeri+cost)<InterfaceConstantes.MINSEYERI)
			return false;
		else 
			return true;

	}
	public void addSeyeri(AbstractModelPartie partie,float add)
	{
		if(infiniteSeyeri)
			return;
		boolean minimum_reached =false;
		seyeri += add;
		if(seyeri>InterfaceConstantes.MAXSEYERI){seyeri=InterfaceConstantes.MAXSEYERI;}
		if(seyeri<InterfaceConstantes.MINSEYERI)
		{
			seyeri=InterfaceConstantes.MINSEYERI;
			minimum_reached=true;
		}
		if(minimum_reached)
		{
			partie.slowDown=false;
			PartieTimer.me.changedSlowMotion(false);
			partie.slowCount=0;

			if(partie.slowDown)
				Music.me.slowDownMusic();
			else
				Music.me.endSlowDownMusic();

		}

	}
	


	public Vector2d getNormCollision()
	{
		if(wasGrounded)
			return new Vector2d(0,-1);
		else
			return normCollision;
	}

	@Override
	public int getMaxBoundingSquare()
	{
		return getMouvement().getMaxBoundingSquare();
	}
	@Override
	public Point getMaxBoundingRect()
	{
		return getMouvement().getMaxBoundingRect();
	}
	public Point getMousePositionWhenReleased(){
		return inputPool.getMouseReleasedPos();
	}
	@Override
	public AffineTransform computeDrawTr(Point screenDisp)
	{
		return null;
	}
	
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		try{
			//As the heros is fixed when the screen move (position relative to screen), need to substract ScreenDisp
			return getMouvementHitboxCopy(getMouvIndex()).copy().translate(getXpos()-screenDisp.x,getYpos()-screenDisp.y);
		}
		catch(IndexOutOfBoundsException e)
		{
			return new Hitbox();
		}
	}
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp, Mouvement _dep, int _mouv_index) {
		try{
			Mouvement_entity temp = (Mouvement_entity) _dep.Copy(); //create the mouvement
			return temp.getScaledHitboxCopy(_mouv_index,getScaling()).translate(getXpos()-screenDisp.x,getYpos()-screenDisp.y);//no need to copy hitbox as it comes from a copied mouvement
		}
		catch(IndexOutOfBoundsException e)
		{
			assert false: "Index out of bound when computing hitbox";
			return new Hitbox();
		}
	}

	/**
	 * 
	 * @param right True: get the right slide hitbox False: the left one
	 * @return
	 */
	public Hitbox getGliss_Accroch_Hitbox(AbstractModelPartie partie, boolean gliss, boolean right)//used to get slide hitbox
	{
		//Hand coded, only two possible deplacement: Course or Saut 
		int up = getGlobalVit().y<0? 0 : (getMouvement().isMouvement(EntityTypeMouv.SAUT)?4 : 0); //if gliss: up of hand
		int down = getGlobalVit().y<0? 27 : (getMouvement().isMouvement(EntityTypeMouv.SAUT)?31 : 24);//if gliss: down of hand
		if(!gliss)
		{
			//small square below hand: if it is in collision while the hand is not: heros should change to Accroche
			up=   down+1;
			down = down + 15;
		}
		//WARNING assume the Heros hitbox is a square/rectangle not rotated 
		assert getRotation()==0;
		Hitbox herosHit = this.getHitbox(partie.getScreenDisp(),partie.getScreenDisp());

		List<Vector2d> upLeftP = Hitbox.supportPoints(new Vector2d(-1,-1), herosHit.polygon);
		List<Vector2d> downLeftP = Hitbox.supportPoints(new Vector2d(-1,1), herosHit.polygon);
		List<Vector2d> downRightP = Hitbox.supportPoints(new Vector2d(1,1), herosHit.polygon);
		List<Vector2d> upRightP = Hitbox.supportPoints(new Vector2d(1,-1), herosHit.polygon);

		Polygon p = new Polygon();

		int decall_x_hit = 2;
		for(int i=0; i<upLeftP.size();++i)
		{
			Vector2d v= upLeftP.get(i);
			p.addPoint((int)(v.x+ (right? decall_x_hit : -decall_x_hit)), (int)(v.y+up));
		}
		for(int i=0; i<downLeftP.size();++i)
		{
			Vector2d v= downLeftP.get(i);
			p.addPoint((int)(v.x+ (right? decall_x_hit : -decall_x_hit)), (int)(upLeftP.get(0).y+down));
		}
		for(int i=0; i<downRightP.size();++i)
		{
			Vector2d v= downRightP.get(i);
			p.addPoint((int)(v.x+ (right? decall_x_hit : -decall_x_hit)), (int)(upLeftP.get(0).y+down));
		}
		for(int i=0; i<upRightP.size();++i)
		{
			Vector2d v= upRightP.get(i);
			p.addPoint((int)(v.x+ (right? decall_x_hit : -decall_x_hit)), (int)(v.y+up));
		}
		return new Hitbox(p);	
	}

	/**
	 * @param ref_object: the object that pushes "this" by motion
	 * @param motion: value by which the collidable has to be moved for the test
	 * @param collidableToMove: collidable to have to be ejected
	 * @param motionToApply: motion to apply to the collidable that have to be ejected
	 * @return false if moving this by motion is not possible(collision with world or effects) or if ref_object and this are not colliding ,true otherwise (AND APPLY THE MOTION)
	 */
	@Override
	public boolean requestMoveBy(AbstractModelPartie partie,Collidable ref_object,Point motion,List<Collidable> collidableToMove, List<Point> motionToApply)
	{
		if(this.checkCollideWithWorld())
			if(Collision.testcollisionObjects(partie, this, ref_object, false))
			{
				Point appliedMotion = new Point();//not set to null so that we can retrieve the desired motion
				boolean considerEffects = true;
				if(!Collision.ejectWorldCollision(partie, this,ref_object,motion,appliedMotion,considerEffects) || (appliedMotion.x==0 && appliedMotion.y==0)){
					return false; // ejection was not successful,  "this" is preventing ref_object to move, return false
				}
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

	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {
		conditions.OnAttacherCollided();
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		boolean collision_bas = normal.y<0;
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;

		final boolean mem_peutSauter=canJump;
		final boolean mem_useGravity=useGravity;

		resetHandleCollision = new ResetHandleCollision(){
			@Override
			public void reset()
			{
				canJump=mem_peutSauter;
				useGravity= mem_useGravity;
			}};

			if(collision_bas)
			{
				wasGrounded = true;
				canJump=true;
				useGravity=false;
			}

	}
	public class ResetHandleCollision
	{
		public void reset()
		{};
	}

	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal) 
	{
		if(ObjectTypeHelper.isTypeOf(collider, ObjectType.TIR_MONSTRE))
		{
			if(!invincible){
				TirMonstre tm = (TirMonstre)collider;
				touche(((TirMonstre)collider).damage);
			}
		}
	}

	@Override
	public void memorizeCurrentValue() {

		final Point memPos= new Point(getXpos(),getYpos()); 
		final Mouvement_entity memDep = (Mouvement_entity) getMouvement().Copy();
		final int memMouvIndex = getMouvIndex();
		final Vitesse memVitloca = localVit.Copy();
		final CachedHitbox cachedHit = this.getCacheHitboxCopy();
		final CachedAffineTransform cachedDrawTr = this.getCacheDrawTrCopy();
		currentValue=new CurrentValue(){		
			@Override
			public void res()
			{setXpos_sync(memPos.x);setYpos_sync(memPos.y);setMouvement(memDep);setMouvIndex(memMouvIndex);localVit=(memVitloca);
			setCachedHit(cachedHit);setCachedDrawTr(cachedDrawTr);}};
	}
	
	
	private void setMouvement(AbstractModelPartie partie,Mouvement newMouv, int newMouvIndex){
		//Use this function to set specific variables when changing movement (ie: peutSauter if newMouv = saut)
		if(newMouv.isMouvement(EntityTypeMouv.ACCROCHE) && (newMouvIndex==0 || newMouvIndex==2)){
			// ***REMOVE accrocheCooldownTimer=PartieTimer.me.getElapsedNano();
			for(Collidable eff : partie.arrowsEffects){
				if(eff instanceof Roche_effect){
					Roche_effect r_eff = (Roche_effect) eff;
					if((r_eff.groundEffect) && (eff == accrocheCol))
						r_eff.registerAccrocheCol(this);
				}
			}				
			useGravity = false;
		}
		else if(newMouv.isMouvement(EntityTypeMouv.ATTENTE)){
			localVit.y=0;
			useGravity=false;
			canJump = true;
		}
		else if(newMouv.isMouvement(EntityTypeMouv.GLISSADE)){
			localVit.x=(0);
		}
		else if(newMouv.isMouvement(EntityTypeMouv.SAUT)){
			if(newMouvIndex==0||newMouvIndex==3){
				canJump = false;
			}
			else if(newMouv.isMouvement(EntityTypeMouv.SAUT)&& (newMouvIndex==1||newMouvIndex==4))
			{
				canJump = false; //handle case where heros was on ground and is now falling
			}
			if(getMouvement().isMouvement(EntityTypeMouv.GLISSADE)){
				this.last_wall_jump_time=PartieTimer.me.getElapsedNano();
			}
			else if(getMouvement().isMouvement(EntityTypeMouv.ACCROCHE)){
				useGravity=true;
			}
		}
		
		if(wasGrounded){
			canJump=true;
			useGravity=false;
		}
		
		if(getMouvement().isMouvement(EntityTypeMouv.ACCROCHE) && !newMouv.isMouvement(EntityTypeMouv.ACCROCHE)){
			//unregister accroche from roche_effect
			for(Collidable eff : partie.arrowsEffects){
				if(eff instanceof Roche_effect)
				{
					Roche_effect r_eff = (Roche_effect) eff;
					if(r_eff.groundEffect)
						r_eff.unregisterAccrocheCol(this);
				}
			}
			accrocheCol=null;
		}
		setMouvement(newMouv);
		setMouvIndex(newMouvIndex);
	}
	/* ***REMOVE @Override
	protected void handleKeyDownInput(AbstractModelPartie partie) {
		if(!partie.isPartieEnded())
		{
			final boolean currentDirectionIsRight = getMouvement().droite_gauche(getMouvIndex(), getRotation()).equals(DirSubTypeMouv.DROITE);

			final boolean isMoveRightDTapDown = inputPool.isMoveRightDoubleTapState(KeyState.DOWN);
			final boolean isMoveRightDown = inputPool.isMoveRightState(KeyState.DOWN);
			final boolean isMoveLeftDTapDown = inputPool.isMoveLeftDoubleTapState(KeyState.DOWN);
			final boolean isMoveLeftDown = inputPool.isMoveLeftState(KeyState.DOWN);
			final boolean isSlowDown = inputPool.isSlowState(KeyState.DOWN);
			final boolean isJumpDown = inputPool.isJumpState(KeyState.DOWN);
			final boolean isPauseDown = inputPool.isPauseState(KeyState.DOWN);
			
			final boolean rightAndLeftPressedTogether = (isMoveRightDTapDown || isMoveRightDown) &&  (isMoveLeftDTapDown || isMoveLeftDown);
			final boolean courseDroiteDown = isMoveRightDTapDown && (rightAndLeftPressedTogether? !prevDirectionWasRight : true );
			final boolean marcheDroiteDown = isMoveRightDown  && (rightAndLeftPressedTogether? !prevDirectionWasRight : true );
			final boolean marcheGaucheDown = isMoveLeftDown  && (rightAndLeftPressedTogether? prevDirectionWasRight : true );
			final boolean courseGaucheDown = isMoveLeftDTapDown  && (rightAndLeftPressedTogether? prevDirectionWasRight : true );

			if(isPauseDown)
			{
				partie.onPauseDown();
				inputPool.resetPauseState();
				if(partie.isInPause())
					inputPool.getReferenceInputPartie().resetTouchesFocus();
			}
			if(!partie.isInPause())
			{
				
				boolean isDragged = this.isDragged();
				int shootDownIndex = inputPool.hasShootState(KeyState.DOWN);
				//SHOOTING 
				if(shootDownIndex>-1 && !this.flecheEncochee && !this.doitEncocherFleche 
						&& ((System.nanoTime()-this.last_shoot_time)>InterfaceConstantes.FLECHE_TIR_COOLDOWN))
				{
					//if not wall jump recently
					if(!(this.getMouvement().isMouvement(MouvEntityEnum.SAUT) && ((PartieTimer.me.getElapsedNano()-this.last_wall_jump_time)<=InterfaceConstantes.WALL_JUMP_DISABLE_TIME)))
					{
						//If multiple input pressed => prioritize regular shoot
						inputPool.resetShootState(shootDownIndex);
						this.set_tir_type(shootDownIndex);				
						
						boolean can_shoot_arrow = canShootArrow(partie);
						if(can_shoot_arrow){
							//on ne tir qu'une fleche
							this.doitEncocherFleche=true;
							this.nouvMouv= new Tir(ObjectType.HEROS,null,partie.getFrame()); 
							this.last_armed_time=System.nanoTime();
						}
					}
				}

				boolean heros_shoots = this.flecheEncochee||this.doitEncocherFleche;
				boolean heros_accroche = this.getMouvement().isMouvement(MouvEntityEnum.ACCROCHE);
				boolean heros_glisse = this.getMouvement().isMouvement(MouvEntityEnum.GLISSADE);
				//COURSE
				if((courseDroiteDown||courseGaucheDown) && !heros_glisse && !heros_accroche && !heros_shoots && !isDragged)
				{

					boolean isRunningInOppositeDirection = (courseDroiteDown && !currentDirectionIsRight) || (courseGaucheDown && currentDirectionIsRight);
					//si on ne courrait pas vers la droite avant
					if(! (this.getMouvement().isMouvement(MouvEntityEnum.COURSE) && isRunningInOppositeDirection))
					{
						//do not run if we just wall jump
						if(! (this.getMouvement().isMouvement(MouvEntityEnum.SAUT) && (PartieTimer.me.getElapsedNano()-this.last_wall_jump_time)<= InterfaceConstantes.WALL_JUMP_DISABLE_TIME ))
						{
							this.nouvMouvIndex= courseDroiteDown?4:0; 
							this.nouvMouv= new Course(ObjectType.HEROS,courseDroiteDown?DirSubTypeMouv.DROITE:DirSubTypeMouv.GAUCHE,partie.getFrame()); 
						}
					}
				}
				//MARCHE 
				else if((marcheDroiteDown||marcheGaucheDown)&& !heros_shoots && !isDragged)
				{
					int marche_anim_offset = marcheDroiteDown?4:0;
					int saut_anim_offset = marcheDroiteDown?3:0;
					int nb_accroche_anim_offset = 2;
					
					final boolean moveInSameDirection =(currentDirectionIsRight && marcheDroiteDown) || (!currentDirectionIsRight && marcheGaucheDown) ;
					
					if(heros_glisse || heros_accroche)
					{
						boolean isFirstAccrocheAnim = (getMouvIndex() == 0 || getMouvIndex() ==nb_accroche_anim_offset);
						//fall from side/accroche
						if( (heros_glisse && !moveInSameDirection) || (heros_accroche && isFirstAccrocheAnim && !moveInSameDirection)) 
						{
							this.nouvMouvIndex= (getGlobalVit(partie).y>=0 ? saut_anim_offset+1 : saut_anim_offset); 
							SubMouvSautEnum subMouv = marcheDroiteDown?(getGlobalVit(partie).y>=0? SubMouvSautEnum.FALL_DROITE:SubMouvSautEnum.JUMP_DROITE) 
									: (getGlobalVit(partie).y>=0? SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.JUMP_GAUCHE);
							this.nouvMouv= new Saut(ObjectType.HEROS,subMouv,partie.getFrame()); 
						}
						//Move in same direction while accroche => climb
						else if(heros_accroche && isFirstAccrocheAnim && moveInSameDirection)
						{
							this.nouvMouvIndex= marcheDroiteDown?1+nb_accroche_anim_offset: 1; 
							this.nouvMouv= new Accroche(ObjectType.HEROS,marcheDroiteDown?SubMouvAccrocheEnum.GRIMPE_DROITE:SubMouvAccrocheEnum.GRIMPE_GAUCHE,partie.getFrame()); 						
						}
					}
					//Do nothing if we were running in this direction already
					else if(this.getMouvement().isMouvement(MouvEntityEnum.COURSE) && moveInSameDirection)
					{		
						//no change mouv
					}

					//Grounded and move in different direction  
					else if(! (this.getMouvement().isMouvement(MouvEntityEnum.MARCHE) && moveInSameDirection)&& this.peutSauter)
					{
						this.nouvMouvIndex= marche_anim_offset; 
						this.nouvMouv=new Marche(ObjectType.HEROS,DirSubTypeMouv.DROITE,partie.getFrame());
					}

					//Moving while being in air 
					else if (!this.peutSauter) 
					{
						//do not move if we just wall jump
						if(this.getMouvement().isMouvement(MouvEntityEnum.SAUT)&& 
								(!this.isMouvement(MouvEntityEnum.SAUT, SubMouvSautEnum.LAND_GAUCHE)) && (!this.isMouvement(MouvEntityEnum.SAUT, SubMouvSautEnum.LAND_DROITE)) && 
								((PartieTimer.me.getElapsedNano()-this.last_wall_jump_time)> InterfaceConstantes.WALL_JUMP_DISABLE_TIME))
						{
							if(marcheDroiteDown)
								this.deplaceSautDroit=true; //activates the speed while in air 
							else
								this.deplaceSautGauche=true; //activates the speed while in air 
							boolean fall = this.getGlobalVit(partie).y >=0 ;
							this.nouvMouvIndex= fall? 1+saut_anim_offset : saut_anim_offset ; 
							SubMouvSautEnum subMouv = marcheDroiteDown?(fall?SubMouvSautEnum.FALL_DROITE:SubMouvSautEnum.JUMP_DROITE) 
									: (fall?SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.JUMP_GAUCHE);
							this.nouvMouv=new Saut(ObjectType.HEROS,subMouv,partie.getFrame());
						}
					}
				}
				//SLOW DOWN 
				if(isSlowDown)
				{
					partie.onPartieSlowDown();
					inputPool.resetSlowState();
				}

				//SAUT 
				if(isJumpDown &&  !heros_shoots && !isDragged)
				{
					if(heros_glisse)
					{
						this.sautGlisse=true;

						this.nouvMouvIndex= (this.droite_gauche(this.getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)? 0 : 3);
						this.nouvMouv=new Saut(ObjectType.HEROS,this.nouvMouvIndex==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame() );
						this.last_wall_jump_time=PartieTimer.me.getElapsedNano();
					}
					else if(heros_accroche && ( (this.getMouvIndex()==0) || (this.getMouvIndex()==2)))
					{
						this.sautAccroche=true;
						this.useGravity=true;
						this.nouvMouvIndex= ((this.getMouvIndex() == 0)? 0 : 3);
						this.nouvMouv=new Saut(ObjectType.HEROS,this.nouvMouvIndex==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame() );
					}
					else if(this.peutSauter){
						inputPool.resetMoveLeftDoubleTapState();
						inputPool.resetMoveRightDoubleTapState();

						this.peutSauter=false;

						//Heros jumps so "finSaut" has to be false
						this.debutSaut=true;
						this.finSaut=false;

						this.nouvMouvIndex=this.droite_gauche(this.getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 0 : 3 ;
						this.nouvMouv= new Saut(ObjectType.HEROS,this.nouvMouvIndex==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame() );
					}
				}
				prevDirectionWasRight = rightAndLeftPressedTogether?prevDirectionWasRight :  (isMoveRightDown || isMoveRightDTapDown);
				//Reset pool for which holding down <=> pressing once 
				inputPool.resetJumpState();
			}
			
			int indexSlotDown = inputPool.hasSlotState(KeyState.DOWN);
			if(indexSlotDown >=0)
			{
				inputPool.resetSlotState(indexSlotDown);
				arrowSlotKeyPool = indexSlotDown;
			}
		}
	}
	@Override
	protected void handleKeyReleasedInput(AbstractModelPartie partie) {
		boolean isPauseReleased = inputPool.isPauseState(KeyState.RELEASED);
		int shoot_index = inputPool.hasShootState(KeyState.RELEASED);
		if(isPauseReleased)
		{
			inputPool.resetPauseState();
		}
		//TIR 
		if( shoot_index>=0 && ((System.nanoTime()-last_armed_time)>InterfaceConstantes.ARMED_MIN_TIME))
		{
			inputPool.resetShootState(shoot_index);		
			
			if(flecheEncochee)
			{
				flecheEncochee=false;
				
				partie.getFlecheEncochee(this).OnShoot(partie);

				nouvMouvIndex= (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2) ;
				nouvMouv= new Attente(ObjectType.HEROS,nouvMouvIndex==0? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
				last_shoot_time= System.nanoTime();

			}

		}
		
		final boolean isMoveRightDTapDown = inputPool.isMoveRightDoubleTapState(KeyState.DOWN);
		final boolean isMoveRightDown = inputPool.isMoveRightState(KeyState.DOWN);
		final boolean isMoveLeftDTapDown = inputPool.isMoveLeftDoubleTapState(KeyState.DOWN);
		final boolean isMoveLeftDown = inputPool.isMoveLeftState(KeyState.DOWN);
		final boolean just_wall_jump= getMouvement().isMouvement(MouvEntityEnum.SAUT) && (((PartieTimer.me.getElapsedNano()-last_wall_jump_time)<=InterfaceConstantes.WALL_JUMP_DISABLE_TIME));
		//MARCHE
		if ((isMoveRightDown||isMoveLeftDown||isMoveRightDTapDown||isMoveLeftDTapDown) && !just_wall_jump){
			runBeforeJump=false;
			if(isMoveRightDown)
				inputPool.resetMoveRightState();
			if(isMoveRightDTapDown)
				inputPool.resetMoveRightDoubleTapState();
			if(isMoveLeftDown)
				inputPool.resetMoveLeftState();
			if(isMoveLeftDTapDown)
				inputPool.resetMoveLeftDoubleTapState();

			final boolean heros_glisse = getMouvement().isMouvement(MouvEntityEnum.GLISSADE);
			final boolean heros_accroche = getMouvement().isMouvement(MouvEntityEnum.ACCROCHE);

			if( !heros_glisse && !heros_accroche && !flecheEncochee )
			{
				//pas de decallage de sprite 

				//au sol
				if((getMouvement().isMouvement(MouvEntityEnum.MARCHE)|| getMouvement().isMouvement(MouvEntityEnum.COURSE)) && peutSauter)
				{
					//on variablesPartieRapide.affiche l'animation d'attente

					nouvMouvIndex= (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.DROITE) ? 2: 0 );
					nouvMouv= new Attente(ObjectType.HEROS,nouvMouvIndex==0? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());

					//on met sa vitesse à 0:  
					localVit.x=0;

				}

				else if (getMouvement().isMouvement(MouvEntityEnum.ATTENTE))
				{
					//on arrete quand meme le heros (exemple si il relache la touche de deplacement sur laquelle il avait appuyé en l'air)
					//on variablesPartieRapide.affiche l'animation d'attente

					nouvMouvIndex= droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.DROITE) ? 2: 0 ;
					nouvMouv= new Attente(ObjectType.HEROS,nouvMouvIndex==0? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());

					//on met sa vitesse à 0:  
					localVit.x=0;
				}
				//en l'air et glisse pas
				else if(!peutSauter)
				{

					localVit.x=0;
					// tout dépend si le heros tombe ou non 

					if (getGlobalVit(partie).y<0)//il ne tombe pas donc on met les premières animations de saut
						nouvMouvIndex= droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 0: 3 ;

					else // le heros tombe 
						nouvMouvIndex=droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 1: 4 ;

					SubTypeMouv type_mouv=nouvMouvIndex==0? SubMouvSautEnum.JUMP_GAUCHE: (nouvMouvIndex==3?SubMouvSautEnum.JUMP_DROITE:  (nouvMouvIndex==1?SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.FALL_DROITE));
					nouvMouv=new Saut(ObjectType.HEROS,type_mouv,partie.getFrame());

				}
			}
		}
		//SLOW DOWN
		if(inputPool.isSlowState(KeyState.RELEASED))
			inputPool.resetSlowState();
		//SAUT 
		if(inputPool.isJumpState(KeyState.RELEASED)&& !flecheEncochee )
			inputPool.resetJumpState();
		//DASH
		if(inputPool.isDashState(KeyState.RELEASED))
			inputPool.resetDashState();
		
		int indexSlotReleased = inputPool.hasSlotState(KeyState.RELEASED);
		if(indexSlotReleased >=0)
		{
			inputPool.resetSlotState(indexSlotReleased);
			arrowSlotKeyPool = -1;
		}
	}*/
	@Override
	protected void onStartDeplace(){shouldUpdateSpeed=true;}
	@Override
	protected void handleInputs(AbstractModelPartie partie){
		isMoveRightKeyDown = inputPool.isInputDown(InputType.RIGHT);
		isMoveLeftKeyDown = inputPool.isInputDown(InputType.LEFT);
		//SLOW DOWN 
		if(inputPool.isInputFirstPressed(InputType.SLOW))
			partie.onPartieSlowDown();
		
		//Warn the UI so that it updates its UI for the slots
		partie.notifyObserver();
	};
	

	@Override
	protected boolean updateMouvementBasedOnPhysic(AbstractModelPartie partie){		
		int herosMouvIndex=getMouvIndex();
		
		Hitbox[] hitboxesToSlideAccrocheOn = computeHitboxToSlideAccrocheOn(partie);
		boolean blocGaucheGlisse =hitboxesToSlideAccrocheOn[0]!=null;
		boolean blocDroitGlisse =hitboxesToSlideAccrocheOn[1]!=null;
		boolean blocGaucheAccroche=hitboxesToSlideAccrocheOn[2]!=null;
		boolean blocDroitAccroche=hitboxesToSlideAccrocheOn[3]!=null;

		boolean falling = !isGrounded(partie);
		wasGrounded=!falling;
		if(falling)
			useGravity=falling && !this.getMouvement().isMouvement(EntityTypeMouv.ACCROCHE) && !isDragged();

		boolean[] beginSliding= computeBeginSliding(partie,blocDroitGlisse,blocGaucheGlisse,falling); 
		boolean beginSliding_r= beginSliding[0] ;
		boolean beginSliding_l= beginSliding[1] ;

		boolean[] beginAccroche= computeAccroche(partie,blocDroitGlisse,blocGaucheGlisse,blocDroitAccroche,blocGaucheAccroche,falling);
		boolean beginAccroche_r = beginAccroche[1];
		boolean beginAccroche_l = beginAccroche[0];

		//update some values because player might have been ejected due to a fleche vent
		updateVarSaut(falling, beginAccroche_r || beginAccroche_l, beginSliding_r || beginSliding_l);
		//special case, dealing with stop of accroche 
		boolean accrocheRightEarlyEnd = getMouvement().isMouvement(EntityTypeMouv.ACCROCHE) && !(blocDroitGlisse && blocDroitAccroche) 
				&& (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.DROITE)) && (getMouvIndex() != 1) && (getMouvIndex() != 3);
		boolean accrocheLeftEarlyEnd = getMouvement().isMouvement(EntityTypeMouv.ACCROCHE) && !(blocGaucheGlisse && blocGaucheAccroche) 
				&& (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE))&& (getMouvIndex() != 1) && (getMouvIndex() != 3);
		boolean endAccroche = getMouvement().isMouvement(EntityTypeMouv.ACCROCHE) && ((getMouvIndex()  == 1 || getMouvIndex()  == 3) 
				&& getMouvement().animEndedOnce());
		
		//If the heros is shooting, physic movement are not applied
		if(getMouvement().isMouvement(EntityTypeMouv.TIR))
		{
			return false;
		}
		
		//Handle accroche starts/ends 
		if(endAccroche){
			Mouvement nextMouv = new Attente(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame());
			int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2 );
			
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		else if(accrocheRightEarlyEnd || accrocheLeftEarlyEnd)
		{
			int nextMouvIndex= (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 1 :4);
			Mouvement nextMouv = new Saut(ObjectType.HEROS,droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ?SubMouvSautEnum.FALL_GAUCHE: SubMouvSautEnum.FALL_DROITE,partie.getFrame());
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		else if((beginAccroche_r || beginAccroche_l) && !getMouvement().isMouvement(EntityTypeMouv.ACCROCHE))
		{
			Mouvement nextMouv= new Accroche(ObjectType.HEROS, beginAccroche_l? SubMouvAccrocheEnum.ACCROCHE_GAUCHE:SubMouvAccrocheEnum.ACCROCHE_DROITE,partie.getFrame());
			int nextMouvIndex = (beginAccroche_l?0:2);
			
			double[] dx_dy = computeAlignAccroche(partie,beginAccroche_l,nextMouv,nextMouvIndex,hitboxesToSlideAccrocheOn);
			addXpos_sync((int)dx_dy[0]); addYpos_sync((int)dx_dy[1]);

			if(isNextMouvValid(nextMouv,nextMouvIndex, partie, true)){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
			else{
				addXpos_sync((int)-dx_dy[0]); addYpos_sync((int)-dx_dy[1]);
			}
		}
		//Handle sliding starts/ends 
		boolean fallingSautAnimation = getMouvement().isMouvement(EntityTypeMouv.SAUT)  && (herosMouvIndex == 0 || herosMouvIndex ==  3||herosMouvIndex == 1 || herosMouvIndex == 4);
		boolean canSwitchToFall = !getMouvement().isMouvement(EntityTypeMouv.ACCROCHE) && !getMouvement().isMouvement(EntityTypeMouv.GLISSADE)
				&& !fallingSautAnimation;
		boolean landing = (!falling) && getMouvement().isMouvement(EntityTypeMouv.SAUT) && fallingSautAnimation;

		boolean standup = getMouvement().isMouvement(EntityTypeMouv.SAUT) && (herosMouvIndex ==2 || herosMouvIndex ==5)  && getMouvement().animEndedOnce();
		//deal with the case where the heros was ejected while standing up
		if(standup && falling)
		{
			standup=false;
		}
		boolean landSliding = wasGrounded && getMouvement().isMouvement(EntityTypeMouv.GLISSADE);
		boolean endSliding = getMouvement().isMouvement(EntityTypeMouv.GLISSADE) && 
				((!blocDroitGlisse && droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE)) ||
						(!blocGaucheGlisse && droite_gauche(herosMouvIndex)==(DirSubTypeMouv.DROITE)) || !falling) ;
		
		if(landSliding)
		{
			Mouvement nextMouv = new Attente(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame());
			int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2 );
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}

		else if(endSliding)
		{
			int nextMouvIndex= (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 1 :4);
			Mouvement nextMouv = new Saut(ObjectType.HEROS,droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ?SubMouvSautEnum.FALL_GAUCHE: SubMouvSautEnum.FALL_DROITE,partie.getFrame());
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				//REMOVE localVit.x=(0);
				return true;
			}
		}
		else if( (beginSliding_r||beginSliding_l) && !getMouvement().isMouvement(EntityTypeMouv.GLISSADE))
		{
			Mouvement nextMouv = new Glissade(ObjectType.HEROS,beginSliding_l?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			int nextMouvIndex = (beginSliding_l ? 0 :1);
			
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
			
		}
		
		//Handle saut transition (falling or landing)
		if(landing) 
		{
			Mouvement nextMouv = new Saut(ObjectType.HEROS,droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ?SubMouvSautEnum.LAND_GAUCHE:SubMouvSautEnum.LAND_DROITE,partie.getFrame());
			int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE)? 2 : 5 );
			//on ajuste la position du personnage pour qu'il soit centre 
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		else if(standup){
			boolean keepRunning = runBeforeJump &&( inputPool.isInputDown(InputType.RIGHT) || inputPool.isInputDown(InputType.LEFT));
			int nextMouvIndex = keepRunning? (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 4 ) : (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2 );
			Mouvement_entity nextMouv=  keepRunning? new Course(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame()) 
					: new Attente(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame());
			//on ajuste la position du personnage pour qu'il soit centre
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		else if(falling&&canSwitchToFall)
		{
			int down = getGlobalVit().y>=0 ? 1 : 0;
			SubTypeMouv sub_type_mouv = droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? (down==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.FALL_GAUCHE) :
				(down==0?SubMouvSautEnum.JUMP_DROITE:SubMouvSautEnum.FALL_DROITE);
			Mouvement nextMouv = new Saut(ObjectType.HEROS,sub_type_mouv,partie.getFrame());
			int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0+down :3+down);
			
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		return false; 
	}
	@Override
	protected boolean updateNonInterruptibleMouvement(AbstractModelPartie partie){
		//Accroche anim 1 or 3
		//Saut anim 2 or 5 
		return false;
	}
	@Override
	protected boolean updateMouvementBasedOnInput(AbstractModelPartie partie){
		if(!getMouvement().isInterruptible(getMouvIndex()))
			return false;
		//Current move priority: dash>shooting arrow> jump> aiming arrow> move (right/left...) 

		final boolean heros_shoots = getMouvement().isMouvement(EntityTypeMouv.TIR);
		final boolean heros_accroche = getMouvement().isMouvement(EntityTypeMouv.ACCROCHE);
		final boolean heros_glisse = getMouvement().isMouvement(EntityTypeMouv.GLISSADE);
		//DASH 
		//TODO: 
		
		//SHOOTING
		//Shooting arrow or handling arrow shot but could not change mouvement
		boolean isArmedArrowReleased = arrowArmedIndex>-1 && inputPool.isInputReleased(InputTypeArray.SHOOT,arrowArmedIndex);
		if( isArmedArrowReleased || (arrowArmedIndex == -1 && getMouvement().isMouvement(EntityTypeMouv.TIR)))
		{	
			//shoot arrow whatever happens
			if(arrowArmedIndex>-1){
				this.shootArrow(partie);
			}
			int nextMouvIndex = (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2) ;
			Mouvement nextMouv = new Attente(ObjectType.HEROS,droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		
		//jumping
		if(inputPool.isInputFirstPressed(InputType.JUMP) &&  !heros_shoots && !isDragged())
		{
			int nextMouvIndex = 0;
			Mouvement nextMouv = null;
			boolean heros_hanging = heros_accroche && ( (this.getMouvIndex()==0) || (this.getMouvIndex()==2));
			if(heros_glisse)
			{
				nextMouvIndex= (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)? 0 : 3);
				nextMouv=new Saut(ObjectType.HEROS,nextMouvIndex==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame() );
			}
			else if(heros_hanging)
			{
				nextMouvIndex= ((getMouvIndex() == 0)? 0 : 3);
				nextMouv=new Saut(ObjectType.HEROS,nextMouvIndex==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame() );
			}
			else if(wasGrounded){
				nextMouvIndex=droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 0 : 3 ;
				nextMouv= new Saut(ObjectType.HEROS,nextMouvIndex==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame() );
			}
			if(nextMouv != null){
				boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					runBeforeJump = getMouvement().isMouvement(EntityTypeMouv.COURSE);
					setMouvement(partie,nextMouv,nextMouvIndex);
					//Handle speed there as it is set only one time (jump is not a continuous speed)
					shouldUpdateSpeed = false;
					setJumpSpeed(heros_glisse,false,!heros_glisse&&(heros_hanging||wasGrounded));
					return true;
				}
			}
		}
		
		//Arming arrow
		int shootDownIndex = inputPool.getInputFirstPressed(InputTypeArray.SHOOT);
		//Note: Prevent to shoot if we are in a Tir mouvement already (this means that the shot arrow didn't ended correctly)
		if(shootDownIndex>-1 && !getMouvement().isMouvement(EntityTypeMouv.TIR) && arrowArmedIndex==-1
				&& ((System.nanoTime()-this.last_shoot_time)>InterfaceConstantes.FLECHE_TIR_COOLDOWN) && canShootArrow(partie,shootDownIndex))
		{
			Mouvement nextMouv = new Tir(ObjectType.HEROS,null,partie.getFrame()); 
			double[] mouv_index_rotation = Deplace.getMouvIndexRotationTir(partie,false);
			int nextMouvIndex = (int)mouv_index_rotation[0];
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				armArrow(partie,shootDownIndex);
				rotation_tir=mouv_index_rotation[1];
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		
		//Releasing move input 

		final boolean releasedRightMove = inputPool.isInputReleased(InputType.RIGHT) || inputPool.isInputReleased(InputType.RIGHT_D_TAP) ;
		final boolean releasedLeftMove = inputPool.isInputReleased(InputType.LEFT) || inputPool.isInputReleased(InputType.LEFT_D_TAP) ;
		
		if((releasedRightMove || releasedLeftMove) && !heros_shoots &&!heros_accroche && !heros_glisse ){
			runBeforeJump = false;
			if(wasGrounded){
				int nextMouvIndex = (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2) ;
				Mouvement nextMouv = new Attente(ObjectType.HEROS,droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
				boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					setMouvement(partie,nextMouv,nextMouvIndex);
					return true;
				}
			}
			else{
				int up = getGlobalVit().y>=0 ? 1 : 0;
				SubTypeMouv sub_type_mouv = droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? (up==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.FALL_GAUCHE) :
					(up==0?SubMouvSautEnum.JUMP_DROITE:SubMouvSautEnum.FALL_DROITE);
				Mouvement nextMouv = new Saut(ObjectType.HEROS,sub_type_mouv,partie.getFrame());
				int nextMouvIndex = (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 0+up :3+up);
				
				boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					setMouvement(partie,nextMouv,nextMouvIndex);
					return true;
				}	
			}
		}
		
		//Move left/right 
		
		final boolean currentDirectionIsRight = getMouvement().droite_gauche(getMouvIndex(), getRotation()).equals(DirSubTypeMouv.DROITE);

		final boolean isMoveRightDTapDown = inputPool.isInputFirstPressed(InputType.RIGHT_D_TAP); //first pressed: prevents run->jump->keeps running
		final boolean isMoveRightDown = inputPool.isInputDown(InputType.RIGHT);
		final boolean isMoveLeftDTapDown = inputPool.isInputFirstPressed(InputType.LEFT_D_TAP);//first pressed: prevents run->jump->keeps running
		final boolean isMoveLeftDown = inputPool.isInputDown(InputType.LEFT);
		
		final boolean rightAndLeftPressedTogether = (isMoveRightDTapDown || isMoveRightDown) &&  (isMoveLeftDTapDown || isMoveLeftDown);
		final boolean isRunning = getMouvement().isMouvement(EntityTypeMouv.COURSE);
		final boolean courseDroiteDown = (isMoveRightDTapDown || (isRunning && isMoveRightDown)) && (rightAndLeftPressedTogether? !prevDirectionWasRight : true );
		final boolean courseGaucheDown = (isMoveLeftDTapDown || (isRunning && isMoveLeftDown))  && (rightAndLeftPressedTogether? prevDirectionWasRight : true );
		final boolean marcheDroiteDown = isMoveRightDown  && (rightAndLeftPressedTogether? !prevDirectionWasRight : true );
		final boolean marcheGaucheDown = isMoveLeftDown  && (rightAndLeftPressedTogether? prevDirectionWasRight : true );

		
		//Running
		if((courseDroiteDown||courseGaucheDown) && !heros_glisse && !heros_accroche && !heros_shoots && wasGrounded && !isDragged())
		{
			boolean isRunningInSameDirection = (courseDroiteDown && currentDirectionIsRight) || (courseGaucheDown && !currentDirectionIsRight);
			//New movement only if we run into a different direction
			if(! (isRunning && isRunningInSameDirection))
			{
				int nextMouvIndex= courseDroiteDown?4:0; 
				Mouvement nextMouv= new Course(ObjectType.HEROS,courseDroiteDown?DirSubTypeMouv.DROITE:DirSubTypeMouv.GAUCHE,partie.getFrame()); 
				boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					setMouvement(partie,nextMouv,nextMouvIndex);
					return true;
				}
			}
		}
		//walking 
		else if((marcheDroiteDown||marcheGaucheDown)&& !heros_shoots && !isDragged())
		{
			int marche_anim_offset = marcheDroiteDown?4:0;
			int saut_anim_offset = marcheDroiteDown?3:0;
			int nb_accroche_anim_offset = 2;
			
			final boolean moveInSameDirection =(currentDirectionIsRight && marcheDroiteDown) || (!currentDirectionIsRight && marcheGaucheDown) ;

			int nextMouvIndex =0; 
			Mouvement nextMouv = null;
			boolean accrocheAlign = false;
			if(heros_glisse || heros_accroche)
			{
				//Watch out, heros sliding with wall on its left is facing right 
				final boolean moveInSameDirectionGlisse =(currentDirectionIsRight && marcheGaucheDown) || (!currentDirectionIsRight && marcheDroiteDown) ;

				boolean isFirstAccrocheAnim = (getMouvIndex() == 0 || getMouvIndex() ==nb_accroche_anim_offset);
				//fall from side/accroche
				if( (heros_glisse && !moveInSameDirectionGlisse) || (heros_accroche && isFirstAccrocheAnim && !moveInSameDirection)) 
				{
					nextMouvIndex= (getGlobalVit().y>=0 ? saut_anim_offset+1 : saut_anim_offset); 
					SubMouvSautEnum subMouv = marcheDroiteDown?(getGlobalVit().y>=0? SubMouvSautEnum.FALL_DROITE:SubMouvSautEnum.JUMP_DROITE) 
							: (getGlobalVit().y>=0? SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.JUMP_GAUCHE);
					nextMouv= new Saut(ObjectType.HEROS,subMouv,partie.getFrame()); 
				}
				//Move in same direction while accroche => climb
				else if(heros_accroche && isFirstAccrocheAnim && moveInSameDirection)
				{
					nextMouvIndex= marcheDroiteDown?1+nb_accroche_anim_offset: 1; 
					nextMouv= new Accroche(ObjectType.HEROS,marcheDroiteDown?SubMouvAccrocheEnum.GRIMPE_DROITE:SubMouvAccrocheEnum.GRIMPE_GAUCHE,partie.getFrame()); 						
					accrocheAlign = true;
				}
			}
			//Grounded and move in different direction  
			else if(!(getMouvement().isMouvement(EntityTypeMouv.MARCHE) && moveInSameDirection)&& wasGrounded)
			{
				nextMouvIndex= marche_anim_offset; 
				nextMouv=new Marche(ObjectType.HEROS,marcheDroiteDown?DirSubTypeMouv.DROITE:DirSubTypeMouv.GAUCHE,partie.getFrame());
			}

			//Moving while being in air 
			else if (!wasGrounded) 
			{
				nextMouvIndex= (getGlobalVit().y>=0 ? saut_anim_offset+1 : saut_anim_offset); 
				SubMouvSautEnum subMouv = marcheDroiteDown?(getGlobalVit().y>=0? SubMouvSautEnum.FALL_DROITE:SubMouvSautEnum.JUMP_DROITE) 
						: (getGlobalVit().y>=0? SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.JUMP_GAUCHE);
				nextMouv= new Saut(ObjectType.HEROS,subMouv,partie.getFrame()); 
			}
			if(nextMouv != null){
				boolean success = false;
				if(accrocheAlign)
					success = alignAccrocheClimbMouvement(partie,nextMouv, nextMouvIndex);
				else
					success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					prevDirectionWasRight  = rightAndLeftPressedTogether?prevDirectionWasRight :  (isMoveRightDown || isMoveRightDTapDown);
					setMouvement(partie,nextMouv,nextMouvIndex);
					return true;
				}
			}
		} //end of if move
		return false;
	}//end of function
	private void setJumpSpeed(boolean isWallJumping,boolean isAirJumping, boolean isGroundJumping){
		if(isAirJumping)
			airJumping = true;
		else if(isWallJumping)
			wallJumping = true;
		else 
			groundJumping = true;
		getMouvement().setSpeed(this, getMouvIndex());
		if(isAirJumping)
			airJumping = false;
		else if(isWallJumping)
			wallJumping = false;
		else 
			groundJumping = false;
	}
	
	@Override
	protected boolean updateMouvementBasedOnAnimation(AbstractModelPartie partie){
		if(getMouvement().isMouvement(EntityTypeMouv.TIR)){
				double[] mouv_index_rotation = Deplace.getMouvIndexRotationTir(partie,false);
				int nextMouvIndex = (int)mouv_index_rotation[0];
				Mouvement nextMouv = new Tir(ObjectType.HEROS,null,partie.getFrame());
				boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					rotation_tir=mouv_index_rotation[1];
					setMouvement(partie,nextMouv,nextMouvIndex);
					return true;
			}
			 
		} 
		else if(getMouvement().isMouvement(EntityTypeMouv.ACCROCHE) && ( (getMouvIndex() == 1) || (getMouvIndex() == 3)) && getMouvement().animEndedOnce())
		{					
			int nextMouvIndex = (droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2) ;
			Mouvement nextMouv = new Attente(ObjectType.HEROS,droite_gauche(getMouvIndex()).equals(DirSubTypeMouv.GAUCHE)? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			
			boolean success = alignNextMouvement(partie,nextMouv,nextMouvIndex);
			if(success){
				setMouvement(partie,nextMouv,nextMouvIndex);
				return true;
			}
		}
		else// MEME MOUVEMENT QUE PRECEDEMMENT
		{
			int nextMouvIndex = getMouvement().updateAnimation(getMouvIndex(), partie.getFrame(),conditions.getSpeedFactor());
			if(getMouvIndex()!=nextMouvIndex){
				boolean success = alignNextMouvement(partie,getMouvement(), nextMouvIndex);
				if(success){
					setMouvement(partie,getMouvement(),nextMouvIndex);
				}
			}
			return true;
		}		
		return false;
	}
	@Override
	protected void resetInputState(AbstractModelPartie partie){
		inputPool.updateInputState();
	}
	@Override 
	protected void onMouvementChanged(AbstractModelPartie partie,boolean animationChanged, boolean mouvementChanged){
	}
	
	private Collidable accrocheCol=null;
	
	/**
	 * 
	 * @param partie
	 * @return [heros can slide on left bloc, heros can slide on right bloc, heros can accroche on left bloc, heros can accroche on right bloc]
	 */
	private Hitbox[] computeHitboxToSlideAccrocheOn(AbstractModelPartie partie){
		
		ModelPrincipal.debugTime.startElapsedForVerbose();
		
		Hitbox blocDroitGlisseHit = null;
		Hitbox blocGaucheGlisseHit = null;

		Hitbox blocDroitAccrocheHit = null;//not null if accroche special hitbox is colliding with world 
		Hitbox blocGaucheAccrocheHit = null;
		
		//translate all object hitboxes, see collision to get full formula
		Hitbox herosHitbox= getHitbox(partie.getScreenDisp(),partie.getScreenDisp());
		ModelPrincipal.debugTime.elapsed("get heros hitbox");
		List<Collidable> mondeBlocs = Collision.getMondeBlocs(partie.monde,herosHitbox, partie.INIT_RECT,partie.getScreenDisp(),partie.TAILLE_BLOC);
		ModelPrincipal.debugTime.elapsed("monde bloc");

		List<Collidable> effectColl = Collidable.getAllCollidableEffectOnScreen(partie);
		ModelPrincipal.debugTime.elapsed("get all collidable effect");
		
		List<Collidable> allColli = new ArrayList<Collidable>();
		allColli.addAll(mondeBlocs);//adding this first means that we focus on holding/sliding on world bloc first 
		allColli.addAll(effectColl);
		
		//Get the hitbox uses to detect glissade or accroche (hitbox around the hand)
		Polygon p_glissade_d= getGliss_Accroch_Hitbox(partie,true,true).polygon;
		Polygon p_glissade_g= getGliss_Accroch_Hitbox(partie,true,false).polygon;
		Polygon p_accroche_d= getGliss_Accroch_Hitbox(partie,false,true).polygon;
		Polygon p_accroche_g= getGliss_Accroch_Hitbox(partie,false,false).polygon;
		
		ModelPrincipal.debugTime.startElapsedForVerbose();
		for(Collidable coll : allColli)
		{
			Hitbox box = coll.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
			//Check the collision with this collidable
			Polygon p_coll= box.polygon;
			int a = GJK_EPA.intersectsB(p_glissade_d,p_coll, getGlobalVit());
			int b = GJK_EPA.intersectsB(p_glissade_g,p_coll, getGlobalVit());
			int c = GJK_EPA.intersectsB(p_accroche_d,p_coll, getGlobalVit());
			int d = GJK_EPA.intersectsB(p_accroche_g,p_coll, getGlobalVit());
			
			accrocheCol = coll;
			
			//memorize the collider hitbox if there was a collision, otherwise keep the same hitbox 
			if(blocDroitGlisseHit==null && (a==GJK_EPA.TOUCH) || (a==GJK_EPA.INTER ))
				blocDroitGlisseHit=box;
			if(blocGaucheGlisseHit==null && (b==GJK_EPA.TOUCH) || (b==GJK_EPA.INTER))
				blocGaucheGlisseHit=box;
			if(blocDroitAccrocheHit==null && (c==GJK_EPA.TOUCH) || (c==GJK_EPA.INTER ))
				blocDroitAccrocheHit=box;
			if(blocGaucheAccrocheHit==null && (d==GJK_EPA.TOUCH) || (d==GJK_EPA.INTER ))
				blocGaucheAccrocheHit=box;
			
			//Warning, this does not handle the case where multiple collidable are candidates for glisse/accroche. This will always take the bloc collidable first
			//Early stopping if every hitbox were found 
			if(  (blocDroitGlisseHit!=null) && (blocGaucheGlisseHit!=null) && (blocDroitAccrocheHit !=null) && (blocGaucheAccrocheHit!=null))
				break;
			ModelPrincipal.debugTime.elapsed("get hitbox");
		}
		ModelPrincipal.debugTime.elapsed("loop collision");
		
		Hitbox[] res = new Hitbox[4];
		
		res[0]=blocGaucheGlisseHit;
		res[1]=blocDroitGlisseHit;
		res[2]=blocGaucheAccrocheHit;
		res[3]=blocDroitAccrocheHit;
		return res;
	}
	/* ***REMOVE public void updateMouvement(AbstractModelPartie partie)
	{
		ModelPrincipal.debugTime.startElapsedForVerbose();
		
		int herosMouvIndex=getMouvIndex();
		
		Hitbox[] hitboxesToSlideAccrocheOn = computeHitboxToSlideAccrocheOn(partie);
		boolean blocGaucheGlisse =hitboxesToSlideAccrocheOn[0]!=null;
		boolean blocDroitGlisse =hitboxesToSlideAccrocheOn[1]!=null;
		boolean blocGaucheAccroche=hitboxesToSlideAccrocheOn[2]!=null;
		boolean blocDroitAccroche=hitboxesToSlideAccrocheOn[3]!=null;
		// le heros est en chute libre
		boolean falling = !isGrounded(partie);
		wasGrounded=!falling;
		if(falling)
			useGravity=falling && !this.getMouvement().isMouvement(MouvEntityEnum.ACCROCHE) && !isDragged();

		//le heros chute ou cours vers un mur: il commence � glisser sur le mur 
		boolean[] beginSliding= computeBeginSliding(partie,blocDroitGlisse,blocGaucheGlisse,falling); 
		boolean beginSliding_r= beginSliding[0] ;
		boolean beginSliding_l= beginSliding[1] ;

		boolean[] beginAccroche= computeAccroche(partie,blocDroitGlisse,blocGaucheGlisse,blocDroitAccroche,blocGaucheAccroche,falling);
		boolean beginAccroche_r = beginAccroche[1];
		boolean beginAccroche_l = beginAccroche[0];

		//update some values because player might have been ejected due to a fleche vent
		updateVarSaut(falling, beginAccroche_r || beginAccroche_l, beginSliding_r || beginSliding_l);

		//le heros atteri alors qu'il etait en chute libre,
		boolean landing = (!falling) && getMouvement().isMouvement(MouvEntityEnum.SAUT) && (herosMouvIndex == 1 || herosMouvIndex == 4 ||
				( (herosMouvIndex == 0 || herosMouvIndex ==  3) && this.getGlobalVit(partie).y>=0 ));

		boolean standup = getMouvement().isMouvement(MouvEntityEnum.SAUT) && (herosMouvIndex ==2 || herosMouvIndex ==5)  && getMouvement().animEndedOnce();
		//deal with the case where the heros was ejected while standing up
		if(standup && falling)
		{
			standup=false;
			finSaut=false;
		}

		//special case, dealing with stop of accroche 
		boolean stopAccrocheD = getMouvement().isMouvement(MouvEntityEnum.ACCROCHE) && !(blocDroitGlisse && blocDroitAccroche) 
				&& (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.DROITE)) && (getMouvIndex() != 1) && (getMouvIndex() != 3);
		boolean stopAccrocheG = getMouvement().isMouvement(MouvEntityEnum.ACCROCHE) && !(blocGaucheGlisse && blocGaucheAccroche) 
				&& (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE))&& (getMouvIndex() != 1) && (getMouvIndex() != 3);
		if(stopAccrocheD || stopAccrocheG)
		{
			Mouvement nextMouv = new Attente(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame());
			int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2 );
			
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				localVit.y=(0);
				setMouvement(nextMouv);
				setMouvIndex(nextMouvIndex);
			}
			return;
		}
		//le heros touche le sol en glissant
		boolean landSliding = finSaut && getMouvement().isMouvement(MouvEntityEnum.GLISSADE);

		//le heros d�croche du mur
		boolean endSliding = getMouvement().isMouvement(MouvEntityEnum.GLISSADE) && 
				((!blocDroitGlisse && droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE)) ||
						(!blocGaucheGlisse && droite_gauche(herosMouvIndex)==(DirSubTypeMouv.DROITE)) || !falling) ;
		
		int mouv_index=herosMouvIndex;

		ModelPrincipal.debugTime.elapsed("Init 2");

		if( (doitEncocherFleche || flecheEncochee))//cas different puisqu'on ne veut pas que l'avatar ait l'animation de chute en l'air
		{
			double[] mouv_index_rotation = Deplace.getMouvIndexRotationTir(partie,false);
			int nextMouvIndex = (int)mouv_index_rotation[0];
			rotation_tir=mouv_index_rotation[1];
			//on decalle
			Mouvement nextMouv = new Tir(ObjectType.HEROS,null,partie.getFrame());
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				setMouvement(nextMouv);
				getMouvement().setSpeed(this, nextMouvIndex);
				if(!falling)
				{
					finSaut=false;
					peutSauter=true;
					useGravity=false;
				}
				setMouvIndex(nextMouvIndex);
				return;
			}
		}
		ModelPrincipal.debugTime.elapsed("fleche");

		//SPECIAL CASE that comes from computation on the current Mouvement 
		if( (beginSliding_r||beginSliding_l) && !getMouvement().isMouvement(MouvEntityEnum.GLISSADE))
		{
			Mouvement nextMouv = new Glissade(ObjectType.HEROS,beginSliding_l?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			int nextMouvIndex = (beginSliding_l ? 0 :1);
			
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				mouv_index = nextMouvIndex;
				setMouvement(nextMouv);
				getMouvement().setSpeed(this, mouv_index);
				deplaceSautGauche=false;
				deplaceSautDroit=false;
				localVit.x=(0);
				setMouvIndex(mouv_index);
			}
			return;
		}
		ModelPrincipal.debugTime.elapsed("slide");

		if((beginAccroche_r || beginAccroche_l) && !getMouvement().isMouvement(MouvEntityEnum.ACCROCHE))
		{
			Mouvement nextMouv= new Accroche(ObjectType.HEROS, beginAccroche_l? SubMouvAccrocheEnum.ACCROCHE_GAUCHE:SubMouvAccrocheEnum.ACCROCHE_DROITE,partie.getFrame());
			int nextMouvIndex = (beginAccroche_l?0:2);

			//Manually align hitbox 
			int xdir = beginAccroche_l ? -1 :1;
			int ydir = -1; //get upper part of hitbox

			double ycurrentup = Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), herosMouvIndex,getScaling()).polygon).y;
			//get value to align hitbox 
			double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), herosMouvIndex,getScaling()).polygon).x -
					Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextMouvIndex,getScaling()).polygon).x;

			double dy= ycurrentup -
					Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextMouvIndex,getScaling()).polygon).y;

			//align to lower bloc: make ycurrentup align with top of collider hitbox 

			double colliderTopY=0;
			if(beginAccroche_r)
			{
				colliderTopY= Hitbox.supportPoint(new Vector2d(0,-1), hitboxesToSlideAccrocheOn[3].polygon).y; //right accroche hitbox
			}
			else
			{
				colliderTopY= Hitbox.supportPoint(new Vector2d(0,-1), hitboxesToSlideAccrocheOn[2].polygon).y;//left accroche hitbox
			}
			double align_to_ground = colliderTopY-ycurrentup;

			addXpos_sync((int)dx);
			addYpos_sync((int)dy);
			addYpos_sync((int) align_to_ground);


			boolean motionSuccess = true;
			
			motionSuccess= isNextMouvValid(nextMouv,nextMouvIndex, partie, true);
			accrocheCooldownTimer=PartieTimer.me.getElapsedNano();

			if(motionSuccess){
				for(Collidable eff : partie.arrowsEffects){
					if(eff instanceof Roche_effect){
						Roche_effect r_eff = (Roche_effect) eff;
						if((r_eff.groundEffect) && (eff == accrocheCol))
						{
							r_eff.registerAccrocheCol(this);
						}
					}
				}
				useGravity=false;
				
				setMouvement(nextMouv);
				mouv_index=nextMouvIndex;
				getMouvement().setSpeed(this, nextMouvIndex);

				setMouvIndex(mouv_index);
				return;
			}
			else
			{
				addXpos_sync((int)-dx);
				addYpos_sync((int)-dy);
				addYpos_sync((int)-align_to_ground);
			}

		}
		ModelPrincipal.debugTime.elapsed("accroche");

		if(falling)
		{
			peutSauter=false;
			if(!(getMouvement().isMouvement(MouvEntityEnum.GLISSADE)||getMouvement().isMouvement(MouvEntityEnum.COURSE)
					||getMouvement().isMouvement(MouvEntityEnum.ACCROCHE)))
			{
				int up = getGlobalVit(partie).y>=0 ? 1 : 0;
				SubTypeMouv sub_type_mouv = droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? (up==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.FALL_GAUCHE) :
					(up==0?SubMouvSautEnum.JUMP_DROITE:SubMouvSautEnum.FALL_DROITE);
				Mouvement nextMouv = new Saut(ObjectType.HEROS,sub_type_mouv,partie.getFrame());
				int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0+up :3+up);
				
				boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					herosMouvIndex = nextMouvIndex;
					mouv_index=nextMouvIndex;
					setMouvement(nextMouv);
					getMouvement().setSpeed(this, mouv_index);
					beginSliding= computeBeginSliding(partie,blocDroitGlisse,blocGaucheGlisse,(falling&&!landing));
				}
			}
		}

		if(getMouvement().isMouvement(MouvEntityEnum.ACCROCHE))
		{			
			if( (mouv_index == 1 || mouv_index == 3) && getMouvement().animEndedOnce())
			{
				int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 :2);
				Mouvement nextMouv = new Attente(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame());
				
				boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
				if(success){
					mouv_index= nextMouvIndex;
					setMouvement(nextMouv);
					getMouvement().setSpeed(this, mouv_index);
	
	
	
					//on reinitialise les variables de saut 
					debutSaut = false;
					finSaut = false;
					useGravity=false;
					peutSauter = true;
					sautGlisse = false;
					sautAccroche=false;
					setMouvIndex(mouv_index);
					return;
				}
			}
		}
		else if(landing) //atterrissage: accroupi 
		{
			Mouvement nextMouv = new Saut(ObjectType.HEROS,droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ?SubMouvSautEnum.LAND_GAUCHE:SubMouvSautEnum.LAND_DROITE,partie.getFrame());
			int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE)? 2 : 5 );
			//on ajuste la position du personnage pour qu'il soit centre 
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				finSaut=false;//set landing to false
				setMouvement(nextMouv);
				mouv_index= nextMouvIndex;
				getMouvement().setSpeed(this, mouv_index);
	
				setMouvIndex(mouv_index);
				return;
			}
		}
		else if(standup)//atterissage: se releve
		{

			int nextMouvIndex = runBeforeJump? (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 4 ) : (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2 );
			Mouvement_entity nextMouv=  runBeforeJump? new Course(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame()) 
					: new Attente(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame());
			//on ajuste la position du personnage pour qu'il soit centre
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				//on choisit la direction d'attente			
				localVit.x=(0);
				finSaut=false;
				peutSauter=true;
				mouv_index=nextMouvIndex;
				setMouvement(nextMouv);
	
				setMouvIndex(mouv_index);
				return;
			}

		}
		else if(landSliding)
		{

			Mouvement nextMouv = new Attente(ObjectType.HEROS,droite_gauche(herosMouvIndex),partie.getFrame());
			int nextMouvIndex = (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2 );
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				finSaut=false;
				//on ajuste la position du personnage pour qu'il soit centre
				mouv_index= nextMouvIndex;
				setMouvement(nextMouv);
				localVit.y=(0);
				setMouvIndex(mouv_index);
				return;
			}
		}

		else if(endSliding)
		{

			int nextMouvIndex= (droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 1 :4);
			Mouvement nextMouv = new Saut(ObjectType.HEROS,droite_gauche(herosMouvIndex).equals(DirSubTypeMouv.GAUCHE) ?SubMouvSautEnum.FALL_GAUCHE: SubMouvSautEnum.FALL_DROITE,partie.getFrame());
			boolean success = alignNextMouvement(partie,nextMouv, nextMouvIndex);
			if(success){
				mouv_index = nextMouvIndex;
				setMouvement(nextMouv);
				getMouvement().setSpeed(this, mouv_index);
				localVit.x=(0);
				nouvMouvIndex=mouv_index;	
				setMouvIndex(mouv_index);
				return;
			}
		}
		ModelPrincipal.debugTime.elapsed("fall");

		//Call function to check if move is allowed
		boolean allowed = moveAllowed(nouvMouv,nouvMouvIndex);
		//CHANGEMENT DE MOUVEMENT
		ModelPrincipal.debugTime.elapsed("move allow");

		if(nouvMouv!=null && allowed)
		{
			boolean accroche_not_enough_space = false;

			if(nouvMouv.isMouvement(MouvEntityEnum.ACCROCHE) && ( (nouvMouvIndex == 1) || (nouvMouvIndex == 3)))
			{				
				//manually align hitbox 
				int xdir = (nouvMouvIndex == 1) ? -1 :1;
				int ydir_up = -1; //get upper part of hitbox
				int ydir_down = 1; //get lower part of hitbox

				//top of hitbox should now be bottom of next hitbox 
				//Manually align hitbox 

				double ycurrentup = Hitbox.supportPoint(new Vector2d(0,ydir_up), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), herosMouvIndex,getScaling()).polygon).y;
				//get value to align hitbox 
				double next_x = Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nouvMouv, nouvMouvIndex,getScaling()).polygon).x;
				double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), herosMouvIndex,getScaling()).polygon).x -
						next_x;

				double next_y_bottom = Hitbox.supportPoint(new Vector2d(0,ydir_down), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nouvMouv, nouvMouvIndex,getScaling()).polygon).y;
				double dy= ycurrentup -next_y_bottom;

				//Variables to test  if the heros would have enough space to stand up
				Mouvement nextMouv_space = new Attente(ObjectType.HEROS,droite_gauche(nouvMouvIndex),partie.getFrame());
				int nextMouvIndex_space = (droite_gauche(nouvMouvIndex).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2 );
				double x_space= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextMouvIndex_space,getScaling()).polygon).x;
				double y_bottom_space= Hitbox.supportPoint(new Vector2d(0,ydir_down), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextMouvIndex_space,getScaling()).polygon).y;

				//Normal match 
				int xdecall = 12 *  ((nouvMouvIndex==1)? -1:1);
				addXpos_sync((int) (dx + xdecall));
				addYpos_sync( (int) (dy-1));

				//test if the heros would have enough space to stand up
				double dx_space= next_x -x_space;
				double dy_space= next_y_bottom -y_bottom_space;
				//test if heros collide when stand up 
				addXpos_sync( (int) (dx_space));
				addYpos_sync( (int) (dy_space));

				Hitbox attenteHit = getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextMouvIndex_space,getScaling()).copy();
				accroche_not_enough_space=Collision.isWorldCollision(partie,attenteHit , true);
				addXpos_sync( (int) (-dx_space));
				addYpos_sync( (int) (-dy_space));
				//revert motion if stuck
				if(accroche_not_enough_space)
				{
					addXpos_sync((int) (-dx - xdecall));
					addYpos_sync( (int) (1-dy));

				}

			}

			else{
				boolean success = alignNextMouvement(partie,nouvMouv, nouvMouvIndex);
			}
			if(nouvMouv.isMouvement(MouvEntityEnum.SAUT) && debutSaut)
			{
				if(getMouvement().isMouvement(MouvEntityEnum.COURSE))
					runBeforeJump=true;
				else
					runBeforeJump=false;
			}

			if(!accroche_not_enough_space)
			{
				mouv_index=nouvMouvIndex;
				setMouvement(nouvMouv);
			}
			getMouvement().setSpeed(this, mouv_index);
			nouvMouv = null;
			nouvMouvIndex = -1;
		}
		else
			//if(!partie.changeMouv ) // MEME MOUVEMENT QUE PRECEDEMMENT
		{
			int nextMouvIndex = getMouvement().updateAnimation(herosMouvIndex, partie.getFrame(),conditions.getSpeedFactor());
			if(mouv_index!=nextMouvIndex){
				boolean success = alignNextMouvement(partie,nouvMouv, nouvMouvIndex);
				if(success)
					mouv_index= nextMouvIndex;				
			}
			getMouvement().setSpeed(this, mouv_index);

		}
		ModelPrincipal.debugTime.elapsed("End changeMouvIndex");

		setMouvIndex(mouv_index);
	}*/

	/* ***REMOVE public void afterChangeMouv(AbstractModelPartie partie)
	{
		if(!getMouvement().isMouvement(MouvEntityEnum.ACCROCHE)){
			//unregister accroche from roche_effect
			for(Collidable eff : partie.arrowsEffects){
				if(eff instanceof Roche_effect)
				{
					Roche_effect r_eff = (Roche_effect) eff;
					if(r_eff.groundEffect)
						r_eff.unregisterAccrocheCol(this);
				}
			}
			accrocheCol=null;
		}
	}*/

	private boolean[] computeSlide_Accroche(AbstractModelPartie partie,boolean slide, boolean blocDroitGlisse, boolean blocGaucheGlisse, boolean blocDroitAccroche, boolean blocGaucheAccroche, boolean falling)
	{
		boolean blocGauche = slide? blocGaucheGlisse : (!blocGaucheGlisse && blocGaucheAccroche);
		boolean blocDroit = slide? blocDroitGlisse:  (!blocDroitGlisse && blocDroitAccroche);
		boolean falling_running = ( falling && getMouvement().isMouvement(EntityTypeMouv.COURSE)) || getMouvement().isMouvement(EntityTypeMouv.SAUT);
		// ***REMOVE? boolean accrocheCooldownDone = slide? true : (PartieTimer.me.getElapsedNano() - accrocheCooldownTimer) > InterfaceConstantes.ACCROCHE_COOLDOWN;
		//Special case in which accroche should not happen : if object is dragged or if wind arrow stick to it 
		boolean no_accroche= slide? false  : this.isDragged(); 
		boolean res_d =  (blocGauche&&(last_colli_left||getGlobalVit().x<0)) && falling_running && !no_accroche;// ***REMOVE && accrocheCooldownDone; 
		boolean res_g =  (blocDroit && (last_colli_right||getGlobalVit().x>0))&& falling_running  && !no_accroche;// ***REMOVE && accrocheCooldownDone; 

		boolean[] res ={res_d,res_g};
		//caution for accroche, res_d is actually res_l, and res_l res_d
		return res;
	}
	public boolean[] computeBeginSliding(AbstractModelPartie partie,boolean blocDroitGlisse, boolean blocGaucheGlisse,boolean falling)
	{
		return computeSlide_Accroche(partie,true,blocDroitGlisse,blocGaucheGlisse,false,false,falling);
	}

	public boolean[] computeAccroche(AbstractModelPartie partie,boolean blocDroitGlisse, boolean blocGaucheGlisse, boolean blocDroitAccroche, boolean blocGaucheAccroche, boolean falling)
	{
		return computeSlide_Accroche(partie,false,blocDroitGlisse,blocGaucheGlisse,blocDroitAccroche,blocGaucheAccroche,falling);
	}

	/* ***REMOVE private boolean moveAllowed(Mouvement nextMove, int nextMouvIndex)
	{
		if(nextMove==null)
			return true;
		Mouvement currentM = getMouvement();

		boolean allowed=true;

		//Unexpected behaviour: attente/marche while being in the air(ie current move being saut/glissade )
		//Unexpected behaviour: going right/left in the air while landing

		boolean inAirAllowed = !( (currentM.isMouvement(MouvEntityEnum.SAUT) || currentM.isMouvement(MouvEntityEnum.GLISSADE)) &&
				(nextMove.isMouvement(MouvEntityEnum.ATTENTE) || nextMove.isMouvement(MouvEntityEnum.MARCHE) ));

		boolean airLandingAllowed= ! (currentM.isMouvement(MouvEntityEnum.SAUT) && nextMove.isMouvement(MouvEntityEnum.SAUT) && 
				((getMouvIndex()==2) || (getMouvIndex()==5) )) ; //movement in air allowed only if not landing

		allowed = allowed && airLandingAllowed && inAirAllowed;
		return allowed; 
	}*/
	
	/***
	 * 
	 * @param partie
	 * @param beginAccroche_l
	 * @param nextMouv
	 * @param nextMouvIndex
	 * @param hitboxesToSlideAccrocheOn
	 * @return [dx,dy]
	 */
	public double[] computeAlignAccroche(AbstractModelPartie partie, boolean beginAccroche_l,Mouvement nextMouv, int nextMouvIndex, Hitbox[] hitboxesToSlideAccrocheOn){
		//Manually align hitbox 
		int xdir = beginAccroche_l ? -1 :1;
		int ydir = -1; //get upper part of hitbox

		double ycurrentup = Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), getMouvIndex()).polygon).y;
		//get value to align hitbox 
		double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), getMouvIndex()).polygon).x -
				Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextMouvIndex).polygon).x;
		double dy= ycurrentup -
				Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextMouvIndex).polygon).y;

		//align to lower bloc: make ycurrentup align with top of collider hitbox 
		double colliderTopY=0;
		if(beginAccroche_l)
			colliderTopY= Hitbox.supportPoint(new Vector2d(0,-1), hitboxesToSlideAccrocheOn[2].polygon).y;//left accroche hitbox
		else
			colliderTopY= Hitbox.supportPoint(new Vector2d(0,-1), hitboxesToSlideAccrocheOn[3].polygon).y; //right accroche hitbox

		double align_to_ground = colliderTopY-ycurrentup;
		double[] res = {dx,dy+align_to_ground};
		return res;
	}

	public boolean alignNextMouvement(AbstractModelPartie partie,Mouvement nextMouv, int nextIndex){
		
		int currentMouvIndex = getMouvIndex();
		/*REMOVE boolean isGlissade = getMouvement().isMouvement(MouvEntityEnum.GLISSADE);
		boolean going_left = getGlobalVit(partie).x<0;

		boolean facing_left_still= getGlobalVit(partie).x==0 &&(droite_gauche(currentMouvIndex).equals(DirSubTypeMouv.GAUCHE)|| last_colli_left)&& !isGlissade;
		boolean sliding_left_wall = (droite_gauche(currentMouvIndex)==DirSubTypeMouv.DROITE) && isGlissade;
		boolean start_falling_face_right = (!getMouvement().isMouvement(MouvEntityEnum.SAUT) && nextMouv.isMouvement(MouvEntityEnum.SAUT)) 
				&& (droite_gauche(currentMouvIndex).equals(DirSubTypeMouv.DROITE));
		boolean start_falling_face_left = (!getMouvement().isMouvement(MouvEntityEnum.SAUT) && nextMouv.isMouvement(MouvEntityEnum.SAUT)) 
				&& (droite_gauche(currentMouvIndex).equals(DirSubTypeMouv.GAUCHE));
		boolean left = ! start_falling_face_left && ( going_left|| facing_left_still ||sliding_left_wall || blocGaucheGlisse || start_falling_face_right) ; 
		boolean down = getGlobalVit(partie).y>=0; */
		// handle case where heros is near a cliff and moves above it, we want the heros to fall => align to the back of the heros (ie : move right, align left)
		
		//Start falling facing left or right
		boolean left= nextMouv.droite_gauche(nextIndex, getRotation()).equals(DirSubTypeMouv.GAUCHE);
		if((!getMouvement().isMouvement(EntityTypeMouv.SAUT) && nextMouv.isMouvement(EntityTypeMouv.SAUT)))
			left = droite_gauche(currentMouvIndex).equals(DirSubTypeMouv.DROITE); //if facing right, align the left side 
		//Start glissade
		else if(nextMouv.isMouvement(EntityTypeMouv.GLISSADE))
			left = droite_gauche(currentMouvIndex).equals(DirSubTypeMouv.GAUCHE); //if facing left, wall is on the left therefore align left
		
		boolean down = getGlobalVit().y>=0;
		boolean success = false;
		
		try{
			success = super.alignNextMouvement(partie, nextMouv, nextIndex, left? XAlignmentType.LEFT : XAlignmentType.RIGHT,
					down?YAlignmentType.BOTTOM : YAlignmentType.TOP , true, !nextMouv.isMouvement(EntityTypeMouv.GLISSADE));
		} catch(Exception e){e.printStackTrace();}
		return success;
	}
	/***
	 * Align the hitboxes when ending accroche mouvement (standup). This function does not use #Collidable.alignNextMouvement because it uses a custom alignment
	 * @param partie
	 * @param nextMouv
	 * @param nextMouvIndex
	 * @return true if alignment is valid
	 */
	public boolean alignAccrocheClimbMouvement(AbstractModelPartie partie,Mouvement nextMouv,int nextMouvIndex){
		boolean accroche_not_enough_space = false;
		
		DirSubTypeMouv nextDir = nextMouv.droite_gauche(nextMouvIndex,getRotation());
		
		//manually align hitbox 
		int xdir = (getMouvement().droite_gauche(getMouvIndex(), getRotation())).equals(DirSubTypeMouv.GAUCHE) ? -1 :1;
		int ydir_up = -1; //get upper part of hitbox
		int ydir_down = 1; //get lower part of hitbox

		//top of hitbox should now be bottom of next hitbox 
		//Manually align hitbox 

		double ycurrentup = Hitbox.supportPoint(new Vector2d(0,ydir_up), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), getMouvIndex()).polygon).y;
		//get value to align hitbox 
		double next_x = Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextMouvIndex).polygon).x;
		double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),getMouvement(), getMouvIndex()).polygon).x -
				next_x;

		double next_y_bottom = Hitbox.supportPoint(new Vector2d(0,ydir_down), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextMouvIndex).polygon).y;
		double dy= ycurrentup -next_y_bottom;

		//Variables to test  if the heros would have enough space to stand up
		Mouvement nextMouv_space = new Attente(ObjectType.HEROS,nextDir,partie.getFrame());
		int nextMouvIndex_space = (nextDir.equals(DirSubTypeMouv.GAUCHE) ? 0 : 2 );
		double x_space= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextMouvIndex_space).polygon).x;
		double y_bottom_space= Hitbox.supportPoint(new Vector2d(0,ydir_down), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextMouvIndex_space).polygon).y;

		//Normal match 
		int xdecall = 12 *  (nextDir.equals(DirSubTypeMouv.GAUCHE)? -1:1);
		addXpos_sync((int) (dx + xdecall));
		addYpos_sync( (int) (dy-1));

		//test if the heros would have enough space to stand up
		double dx_space= next_x -x_space;
		double dy_space= next_y_bottom -y_bottom_space;
		//test if heros collide when stand up 
		addXpos_sync( (int) (dx_space));
		addYpos_sync( (int) (dy_space));

		Hitbox attenteHit = getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextMouvIndex_space).copy();
		accroche_not_enough_space=Collision.isWorldCollision(partie,attenteHit , true);
		addXpos_sync( (int) (-dx_space));
		addYpos_sync( (int) (-dy_space));
		//revert motion if stuck
		if(accroche_not_enough_space)
		{
			addXpos_sync((int) (-dx - xdecall));
			addYpos_sync( (int) (1-dy));

		}
		return !accroche_not_enough_space;
	}
	//protected boolean alignNextMouvement(AbstractModelPartie partie,Mouvement nextMouv, int nextIndex,XAlignmentType xAlignment,YAlignmentType yAlignment,boolean avoidCollision,boolean useTouchCollision) throws Exception{
	
	@Override
	public void updateTimers()
	{
		//mult = 1 : no change
		//mult = 2: last_time -= 1*delta : elapsed time + delta  = 2*delta = mult * delta 
		//mult = 0.5 : last_time += 0.5 delta : elapsed time -0.5 * delta = 0.5 delta = mult
		double mult = conditions.getSpeedFactor();
		double deltaShoot = (System.nanoTime() - last_update_shoot_time) * (mult-1);
		// ***REMOVE double deltaArmed = (System.nanoTime() - last_update_armed_time) * (mult-1);
		
		last_shoot_time -= deltaShoot;
		// ***REMOVE last_armed_time -= deltaArmed;
		
		last_update_shoot_time=System.nanoTime();
		// ***REMOVE last_update_armed_time=System.nanoTime();
	}
	@Override
	protected boolean shouldUpdateSpeed(){
		return shouldUpdateSpeed;
	}
	//Move the character to center it before the animation change.
	/*REMOVE public void alignHitbox(int currentMouvIndex,Mouvement depSuiv, int nextMouvIndex, AbstractModelPartie partie,boolean blocGaucheGlisse)
	{
		alignHitbox(currentMouvIndex, depSuiv, nextMouvIndex, partie, blocGaucheGlisse,null,null);
	}
	public void alignHitbox(int currentMouvIndex,Mouvement depSuiv, int nextMouvIndex, AbstractModelPartie partie,boolean blocGaucheGlisse, Boolean forcedleft,
			Boolean forcedDown )
	{
		boolean isGlissade = getMouvement().isMouvement(MouvEntityEnum.GLISSADE);
		boolean going_left = getGlobalVit(partie).x<0;

		boolean facing_left_still= getGlobalVit(partie).x==0 &&(droite_gauche(currentMouvIndex).equals(DirSubTypeMouv.GAUCHE)|| last_colli_left)&& !isGlissade;
		boolean sliding_left_wall = (droite_gauche(currentMouvIndex)==DirSubTypeMouv.DROITE) && isGlissade;
		boolean start_falling_face_right = (!getMouvement().isMouvement(MouvEntityEnum.SAUT) && depSuiv.isMouvement(MouvEntityEnum.SAUT)) 
				&& (droite_gauche(currentMouvIndex).equals(DirSubTypeMouv.DROITE));
		boolean start_falling_face_left = (!getMouvement().isMouvement(MouvEntityEnum.SAUT) && depSuiv.isMouvement(MouvEntityEnum.SAUT)) 
				&& (droite_gauche(currentMouvIndex).equals(DirSubTypeMouv.GAUCHE));
		boolean left = ! start_falling_face_left && ( going_left|| facing_left_still ||sliding_left_wall || blocGaucheGlisse || start_falling_face_right) ; 
		boolean down = getGlobalVit(partie).y>=0; 

		if(forcedleft!=null)
			left=forcedleft;
		if(forcedDown!=null)
			down=forcedDown;
		last_align_left=left;
		last_align_down=down;
		super.alignHitbox(currentMouvIndex,depSuiv, nextMouvIndex, partie,left, down,this,!depSuiv.isMouvement(MouvEntityEnum.GLISSADE));
	}*/

	@Override
	public void resetVarBeforeCollision()
	{
		last_colli_left=false;
		last_colli_right=false;
	}
	/**
	 * Apply friction to slow down a heros that is shooting, does not influence the y speed
	 * @param minSpeed : minimum speed to which the hero can be slowed down
	 */
	@Override
	public void applyFriction(double minlocalSpeed,double minEnvirSpeed)
	{
		if(getMouvement().isMouvement(EntityTypeMouv.TIR) && !useGravity)
		{
			boolean neg = localVit.x<0;
			double frict = InterfaceConstantes.FRICTION;
			double newVitX= localVit.x - (localVit.x* frict);
			if( (!neg && newVitX<minlocalSpeed) || (neg && newVitX>-1*minlocalSpeed) )
				localVit.x=(minlocalSpeed);
			else
				localVit.x=(newVitX);
		}

	}
	@Override
	public void handleStuck(AbstractModelPartie partie) {
		System.out.println("heros stuck "+ getMouvement().getClass().getName()+" "+ getMouvIndex());
		if(currentValue!=null)
			currentValue.res();

		if(resetHandleCollision != null){
			resetHandleCollision.reset();
		}
	}

	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {

	}

	@Override
	public void resetVarDeplace(boolean speedUpdated) {
		resetHandleCollision=null;
	}
	/**
	 * 
	 * @param partie
	 * @param special
	 * @param add_to_list
	 * @return
	 */
	public Fleche getArrowInstance(AbstractModelPartie partie,int _arrowArmedIndex,boolean add_to_list)
	{
		Fleche fleche = null;
		if(_arrowArmedIndex==-1)
			return null;
		ObjectType tir_type = ObjectType.FLECHE;
		if(_arrowArmedIndex>0)
			tir_type= slots[_arrowArmedIndex-1];
		//ObjectType tir_type_ =get_tir_type(special);
		//Only give the shooter information in the fleche constructor if it is needed 
		//MATERIEL
		if(tir_type.equals(ObjectType.FEU))
			fleche =new Fleche_feu(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.ELECTRIQUE))
			fleche =new Fleche_electrique(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.GLACE))
			fleche =new Fleche_glace(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.ROCHE))
			fleche =new Fleche_roche(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());

		//SPIRITUELLE
		else if(tir_type.equals(ObjectType.LUMIERE))
			fleche =new Fleche_lumiere(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.GRAPPIN))
			fleche =new Fleche_grappin(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.OMBRE))
			fleche =new Fleche_ombre(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.VENT))
			fleche =new Fleche_vent(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());

		//DESTRUCTEUR
		else if(tir_type.equals(ObjectType.BARRAGE))
			fleche =new Fleche_barrage(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.FAUCON))
			fleche =new Fleche_faucon(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.EXPLOSIVE))
			fleche =new Fleche_explosive(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.TROU_NOIR))
			fleche =new Fleche_trou_noir(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());

		//RUSE
		else if(tir_type.equals(ObjectType.MARQUE_MORTELLE))
			fleche =new Fleche_marque_mortelle(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.ABSORPTION))
			fleche =new Fleche_absorption(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.LEURRE))
			fleche =new Fleche_leurre(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type.equals(ObjectType.NINJA))
			fleche =new Fleche_ninja(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());

		else if(tir_type.equals(ObjectType.FLECHE))
			fleche =new Fleche(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());

		return fleche;
	}

	/**
	 * Test if the Heros can shoot an arrow (enough seyeri, can shoot several instance of this arrow...) 
	 */
	public boolean canShootArrow(AbstractModelPartie partie,int _arrowArmedIndex)
	{
		List<Projectile> tabFleche= partie.tabFleche;
		Fleche f = getArrowInstance(partie,_arrowArmedIndex,false);
		int arrow_max_instance= f.MAX_NUMBER_INSTANCE;
		int current_instance = 0;
		boolean enough_seyeri=seyeriActionPossible(f.seyeri_cost);
		boolean notParalyzed = conditions.getShotSpeedFactor()>0;
		if(!enough_seyeri)
			setNotEnoughSeyeri(-1*f.seyeri_cost);
		boolean can_shoot_one_more = true;

		//for Fleche_roche 
		Fleche first_fleche = null;
		//Only create the arrow if none other arrows of this type were shot by shooter 
		if(arrow_max_instance>0){
			for(int i=tabFleche.size()-1; i>=0;--i)
			{
				Fleche fl = (Fleche)tabFleche.get(i);
				//If a similar arrow was shot           && I was the shooter
				// canReshoot is to handle the case of flecheBarrage were arrows are instantiated and shot but the heros can create new ones 
				if(ObjectTypeHelper.isTypeOf(fl, f, true) && fl.shooter==this && !fl.getCanReshot())
				{
					//In case of Fleche roche: do not count it if the effect is in destroy animation 
					boolean shouldCountArrow = f.shouldCountArrow(fl);
					if(shouldCountArrow)
						first_fleche= fl;
					
					if(shouldCountArrow){
						current_instance+=1;
						if(current_instance>=arrow_max_instance)
						{
							can_shoot_one_more=fl.OnArrowReshot(partie,first_fleche);
							break;
						}
					}
				}
			}
		}


		return (enough_seyeri && notParalyzed && can_shoot_one_more);
	}
	
	public void armArrow(AbstractModelPartie partie,int _arrowArmedIndex){
		armedArrow =getArrowInstance(partie,_arrowArmedIndex,true);
		arrowArmedIndex = _arrowArmedIndex;

		addSeyeri(partie, armedArrow.seyeri_cost);
	}
	
	public void shootArrow(AbstractModelPartie partie)
	{	
		last_shoot_time= System.nanoTime();
		armedArrow.OnShoot(partie);
		armedArrow=null; //remove reference to armed arrow 
		arrowArmedIndex = -1;
	}
	@Override
	public void onDestroy(AbstractModelPartie partie) {
		//Do nothing
	}

	private void updateVarSaut(boolean falling, boolean accroche, boolean glisse) {
		this.canJump=!falling;
	}
	@Override
	public void destroy(AbstractModelPartie partie,boolean destroyNow) {
		//Remove all related effects 
		if(destroyNow)
			this.needDestroy=true;
		else 
			timer();
		for(int i=currentEffects.size()-1;i>=0;--i)
		{
			unregisterEffect(partie,currentEffects.get(i));
		}

		//Remove all reference:
		//_shooter from fleche
		for(int j= partie.tabFleche.size()-1; j>=0;j--)
		{
			Heros _shooter = ((Fleche)partie.tabFleche.get(j)).shooter;
			if(_shooter==this)
			{
				_shooter=null;
			}
		}
		//from partie
		if(partie.heros==this)
			partie.heros=null;
	}

}

