package partie.entitie.monstre;

import java.awt.Point;
import java.util.List;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import menu.menuPrincipal.ModelPrincipal;
import music.MusicBruitage;
import partie.collision.CachedAffineTransform;
import partie.collision.CachedHitbox;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.deplacement.Deplace;
import partie.deplacement.Mouvement;
import partie.deplacement.Mouvement.DirSubTypeMouv;
import partie.deplacement.entity.Attente;
import partie.deplacement.entity.Marche;
import partie.deplacement.entity.Mouvement_entity;
import partie.deplacement.entity.Mouvement_entity.MouvEntityEnum;
import partie.deplacement.entity.Saut;
import partie.deplacement.entity.Saut.SubMouvSautEnum;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.PartieTimer;
import partie.projectile.Projectile;
import partie.projectile.tirMonstre.TirSpirel;
import utils.Vitesse;

@SuppressWarnings("serial")
public class Spirel extends Monstre{
	//timer pour que le monstre agisse toutes les (maximum 300ms )

	//variable for ChangMouv to indicates if the animation was changed or not 
	private boolean animationChanged = true;
	
	private double tempsAncienMouv=0;
	private static double delaiMouv= 50;
	private double last_shoot_time=-1;
	private double last_update_shoot_time=-1;

	private static double delaiTir= 2000;//ms
	private double distanceAttaque= 160000; // 400^2
	private boolean cooldown=true;

	public boolean peutSauter = true;
	public boolean sautDroit= false;
	public boolean sautGauche= false;
	private boolean wasGrounded=false;
	
	public boolean staticSpirel=false;

	/**
	 * constructeur
	 * 
	 * @param xPo, position originale en x
	 * @param yPo, position originale en y
	 * @param _staticSpirel, permet de rendre la spirel immobile (si =true)
	 */	
	public Spirel( int xPo,int yPo,boolean _staticSpirel,int current_frame){
		super();
		staticSpirel=_staticSpirel;

		setXpos_sync(xPo);
		setYpos_sync(yPo); 
		localVit= new Vitesse(0,0);
		setDeplacement(new Attente(ObjectType.SPIREL,DirSubTypeMouv.GAUCHE,current_frame));
		setAnim(1);
		tempsAncienMouv= PartieTimer.me.getElapsedNano();
		actionReussite=false;

		finSaut=false;
		peutSauter=false;
		glisse=false;

		//Param from Collidable
		fixedWhenScreenMoves=false;

		MAXLIFE = 100;
		MINLIFE = 0;
		life= MAXLIFE;
	}
	

	
	
	public void onAddLife(){if(life==MINLIFE){needDestroy=true;}};
	/**
	 * Permet de savoir de quel cote est tourné le monstre
	 * 
	 * @param anim, l'animation du monstre
	 * 
	 * @return String , Mouvement.DROITE ou Mouvement.GAUCHE, direction dans laquelle le monstre est tourné
	 */
	
	private void updateShootTime()
	{
		double mult = conditions.getSpeedFactor();
		double deltaShoot = (System.nanoTime() - last_update_shoot_time) * (mult-1);	
		last_shoot_time -= deltaShoot;
		last_update_shoot_time=System.nanoTime();
	}
	
	@Override
	public DirSubTypeMouv droite_gauche (int anim)
	{
		if(getDeplacement().IsDeplacement(MouvEntityEnum.MARCHE))
		{
			if(anim <2)
			{
				return (DirSubTypeMouv.GAUCHE);
			}
			else return(DirSubTypeMouv.DROITE);
		}
		else if(getDeplacement().IsDeplacement(MouvEntityEnum.ATTENTE))
		{
			if(anim <1)
			{
				return (DirSubTypeMouv.GAUCHE);
			}
			else return(DirSubTypeMouv.DROITE);
		}
		else if(getDeplacement().IsDeplacement(MouvEntityEnum.SAUT))
		{
			if(anim <1)
			{
				return (DirSubTypeMouv.GAUCHE);
			}
			else return(DirSubTypeMouv.DROITE);
		}

		else 
			throw new IllegalArgumentException("Spirel/droite_gauche: ERREUR deplacement inconnu");
	}
	
	public Vector2d getNormCollision()
	{
		if(wasGrounded)
			return new Vector2d(0,-1);
		else
			return normCollision;
	}
	
	/**
	 * Gère l'ensemble des événements lié au deplacement d'un monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours 
	 */		
	@Override
	public boolean[] deplace(AbstractModelPartie partie,Deplace deplace)
	{
		ModelPrincipal.debugTime.startElapsedForVerbose();
		updateShootTime();
		ModelPrincipal.debugTime.elapsed("Spirel update shoot time" );
		//compute the next desired movement 
		IA(partie.tabTirMonstre,partie.heros,partie);
		ModelPrincipal.debugTime.elapsed("Spirel IA");
		//compute the true next movement depending on landing, gravity, ... 
		animationChanged=true;
		changeMouv(partie,deplace);
		ModelPrincipal.debugTime.elapsed("Spirel changeMouv");
		boolean[] res= {true,animationChanged};
		return res;//move the object
	}

	/**
	 * IA pour le deplacement du monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours  
	 */	
	public void IA (List<Projectile> tabTirMonstre, Heros heros,AbstractModelPartie partie)
	{
		boolean herosAGauche;
		boolean monsterOnScreen= InterfaceConstantes.SCREEN.polygon.contains(new Point (getXpos()+partie.xScreendisp,getYpos()+partie.yScreendisp));
		boolean canAction= (PartieTimer.me.getElapsedNano()-tempsAncienMouv)*Math.pow(10, -6)>delaiMouv && monsterOnScreen;
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
			Hitbox heros_hit = heros.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
			Vector2d heros_left_up_hit  = Hitbox.supportPoint(new Vector2d(-1,-1), heros_hit.polygon) ;
			Vector2d heros_right_down_hit  = Hitbox.supportPoint(new Vector2d(1,1), heros_hit.polygon) ;
			double herosXmiddle = (heros_left_up_hit.x + heros_right_down_hit.x)/2;
			double herosYmiddle = (heros_left_up_hit.y + heros_right_down_hit.y)/2;

			Hitbox monstre_hit = this.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
			Vector2d monstre_left_up_hit  = Hitbox.supportPoint(new Vector2d(-1,-1), monstre_hit.polygon) ;
			Vector2d monstre_right_down_hit  = Hitbox.supportPoint(new Vector2d(1,1), monstre_hit.polygon) ;
			double monstreXmiddle=(monstre_left_up_hit.x + monstre_right_down_hit.x)/2;
			double monstreYmiddle=(monstre_left_up_hit.y + monstre_right_down_hit.y)/2;
			
			double deltaX= Math.abs(monstreXmiddle-herosXmiddle);
			double deltaY= Math.abs(monstreYmiddle-herosYmiddle);
			
			herosAGauche= monstreXmiddle-herosXmiddle>=0;

			boolean shootAllowed= deltaX*deltaX+deltaY*deltaY<distanceAttaque && ! cooldown && (conditions.getShotSpeedFactor()>0);
			//If drag and can't shoot, exit 
			if(this.isDragged() && !shootAllowed)
				return;
			//Shoot towards heros
			if(shootAllowed)
			{	
				int decision = (int) (Math.random()*100);
				if(decision<=50){
					//animation d'attente
					doitChangMouv= !((getDeplacement().IsDeplacement(MouvEntityEnum.ATTENTE)) 
							&& (herosAGauche? getAnim()==0 : getAnim()==1));
					nouvAnim=herosAGauche? 0 : 1;
					nouvMouv= new Attente(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
	
					//shoot projectile
					double tir1_rotation = droite_gauche(getAnim()).equals(DirSubTypeMouv.GAUCHE)?Math.PI : 0;
					double tir2_rotation = 3*Math.PI/2;
					
					//middle of the spirel hitbox
					int xmid = getXpos() + getDeplacement().xtaille.get(getAnim())/2;
					int ymid = getYpos() + getDeplacement().ytaille.get(getAnim())/2;
	
					tabTirMonstre.add(new TirSpirel(partie,xmid , ymid,
							0,tir1_rotation,partie.getFrame(),conditions.getDamageFactor(),conditions.getShotSpeedFactor()));	
					tabTirMonstre.add(new TirSpirel(partie, xmid , ymid ,
							0,tir2_rotation,partie.getFrame(),conditions.getDamageFactor(),conditions.getShotSpeedFactor()));	
	
					cooldown=true;
				}

			}
			else if (!staticSpirel) // Heros is not in range, try to move towards him
			{
				
				//sinon on se rapproche ou on reste proche 
				boolean blocGaucheBas= nearObstacle(partie,-1,-1);
				boolean blocDroitBas= nearObstacle(partie,1,-1);

				boolean blocRight= nearObstacle(partie,1,0);
				boolean blocLeft= nearObstacle(partie,-1,0);
				
				double jumpHeight= 100; //approx value
				boolean blocRightUp= nearObstacle(partie,1,jumpHeight);
				boolean blocLeftUp= nearObstacle(partie,-1,jumpHeight);
				
				boolean jumpAbove= (droite_gauche(getAnim()).equals(DirSubTypeMouv.GAUCHE)? (blocLeft && !blocLeftUp) : (blocRight && !blocRightUp) ) && peutSauter;
				boolean inAir= getDeplacement().IsDeplacement(MouvEntityEnum.SAUT);
				boolean moveInAir=droite_gauche(getAnim()).equals(DirSubTypeMouv.GAUCHE)?(!blocLeft) :(!blocRight); 
				boolean holeClose= (droite_gauche(getAnim()).equals(DirSubTypeMouv.GAUCHE)? (!blocGaucheBas) : (!blocDroitBas) );

				//on saute au dessus d'un obstacle si possible
				if( jumpAbove)
				{
					doitChangMouv=!getDeplacement().IsDeplacement(MouvEntityEnum.SAUT);
					nouvAnim=(herosAGauche? 0 : 1);
					nouvMouv= new Saut(ObjectType.SPIREL,herosAGauche?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame());
				}
				//si on est en l'air, on se deplace
				else if (inAir)
				{
					//si on peut se deplacer sur le coté
					if(moveInAir)
					{
						if(herosAGauche)
						{
							sautGauche=true;
							sautDroit=false;
						}
						else
						{
							sautGauche=false;
							sautDroit=true;
						}
						doitChangMouv=!(herosAGauche? getAnim()==0 : getAnim()==1);
						nouvAnim=(herosAGauche? 0 : 1);
						nouvMouv= new Saut(ObjectType.SPIREL,herosAGauche?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,partie.getFrame());
					}
					else
					{
						sautGauche=false;
						sautDroit=false;
					}

				}
				//Si il y a un trou a coté du monstre
				else if(holeClose)
				{
					int decision = (int) (Math.random()*100);
					//on attend 
					if(decision <= 70)
					{
						//animation d'attente
						doitChangMouv= !((getDeplacement().IsDeplacement(MouvEntityEnum.ATTENTE)) 
								&& (herosAGauche? getAnim()==0 : getAnim()==1));
						nouvAnim=herosAGauche? 0 : 1;
						nouvMouv= new Attente(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());

					}
					//on se deplace dans l'autre direction
					else if(decision <= 90)
					{
						doitChangMouv=true;
						//on change de direction
						nouvAnim=(herosAGauche? 2 : 0);
						nouvMouv= new Marche(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.DROITE:DirSubTypeMouv.GAUCHE,partie.getFrame());
					}
					//le monstre tombe en marchant
					else 
					{
						doitChangMouv=!((getDeplacement().IsDeplacement(MouvEntityEnum.MARCHE)) 
								&& (herosAGauche? getAnim()<2 : getAnim()>=2));
						nouvAnim=(herosAGauche? 0 : 2);
						nouvMouv= new Marche(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
					}
				}
				//sinon on se deplace
				else
				{
					int decision = (int) (Math.random()*100);

					//deplacement
					if(decision <= 90)
					{
						doitChangMouv=!((getDeplacement().IsDeplacement(MouvEntityEnum.MARCHE)) 
								&& (herosAGauche? getAnim()<2 : getAnim()>=2));
						nouvAnim=(herosAGauche? 0 : 2);
						nouvMouv= new Marche(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
					}
					//attente
					else
					{
						//animation d'attente
						doitChangMouv= !((getDeplacement().IsDeplacement(MouvEntityEnum.ATTENTE)) 
								&& (herosAGauche? getAnim()==0 : getAnim()==1));
						nouvAnim=herosAGauche? 0 : 1;
						nouvMouv= new Attente(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
					}
				}

			}
			else //spirel is static
			{
				//animation d'attente
				doitChangMouv= !((getDeplacement().IsDeplacement(MouvEntityEnum.ATTENTE)) 
						&& (herosAGauche? getAnim()==0 : getAnim()==1));
				nouvAnim=herosAGauche? 0 : 1;
				nouvMouv= new Attente(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			}	

			tempsAncienMouv=PartieTimer.me.getElapsedNano();
		}
		else
		{
			//on continue le mouvement précédant
			doitChangMouv=false;
		}

	}

	/**
	 * 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param Monde, le niveau en cours 
	 */	
	public void changeMouv (AbstractModelPartie partie,Deplace deplace)
	{
		ModelPrincipal.debugTime.startElapsedForVerbose();
		boolean herosAGauche= getXpos()-(partie.heros.getXpos()-partie.xScreendisp)>=0;
		boolean falling= !isGrounded(partie);
		wasGrounded = !falling;
		boolean landing= (finSaut||!falling) && getDeplacement().IsDeplacement(MouvEntityEnum.SAUT);
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
			int animSuiv = herosAGauche? 0 : 1;
			//no fall animation, put the jump instead
			Mouvement_entity depSuiv=new Saut(ObjectType.SPIREL,herosAGauche?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE, partie.getFrame());
			alignHitbox(getAnim(),depSuiv,animSuiv,partie,deplace);

			//le monstre tombe, on met donc son animation de saut
			setAnim(animSuiv);
			setDeplacement(depSuiv);
			getDeplacement().setSpeed(this,getAnim());

		}
		ModelPrincipal.debugTime.elapsed("Spirel chang mouv: falling");
		//atterrissage
		if(landing)
		{
			int animSuiv = herosAGauche? 0 : 1;
			Mouvement_entity depSuiv=new Attente(ObjectType.SPIREL,herosAGauche?DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,partie.getFrame());
			alignHitbox(getAnim(),depSuiv,animSuiv,partie,deplace);
			setAnim(animSuiv);
			setDeplacement(depSuiv);
			getDeplacement().setSpeed(this,getAnim());
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
				//monstre.actionReussite= (decallageMonstre(monstre,monstre.nouvMouv,monstre.anim,monstre.nouvAnim,false,false,partie));
				alignHitbox(getAnim(),nouvMouv,nouvAnim,partie,deplace);

				setDeplacement(nouvMouv);
				setAnim(nouvAnim);
				getDeplacement().setSpeed(this,getAnim());
				ModelPrincipal.debugTime.elapsed("Spirel chang mouv: doit changer mouv");

			}
			else 
			{
				animationChanged=false;
				int nextAnim = getDeplacement().updateAnimation(getAnim(), partie.getFrame(),conditions.getSpeedFactor());
				if(getAnim() != nextAnim){
					alignHitbox(getAnim(),getDeplacement(),nextAnim,partie,deplace);
					setAnim(nextAnim);
				}
				getDeplacement().setSpeed(this, getAnim());
				ModelPrincipal.debugTime.elapsed("Spirel chang mouv: meme mouv");
			}
		}
		doitChangMouv=false;
	}
	/**
	 * Align to the rigth/left/up/down the next movement/hitbox to the previous one
	 * @param monstre
	 * @param animActu
	 * @param depSuiv
	 * @param animSuiv
	 * @param partie
	 */
	public void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie, Deplace deplace)
	{
		boolean going_left = getGlobalVit(partie).x<0;
		boolean facing_left_still= getGlobalVit(partie).x==0 &&(droite_gauche(animActu).equals(DirSubTypeMouv.GAUCHE)|| last_colli_left);
		boolean sliding_left_wall = (droite_gauche(animActu).equals(DirSubTypeMouv.DROITE)) ;
		boolean left = ( going_left|| facing_left_still ||sliding_left_wall) ; 
		boolean down = getGlobalVit(partie).y>=0; 

		super.alignHitbox(animActu,depSuiv, animSuiv, partie,deplace,left, down,this,true);

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
	public boolean nearObstacle(AbstractModelPartie partie,double right,double height)
	{
		Hitbox hit = getHitbox(partie.INIT_RECT,partie.getScreenDisp()).copy();
		assert hit.polygon.npoints==4;
		//get world hitboxes with Collision
		//Shift all points towards right/left
		for(int i=0; i<hit.polygon.npoints; ++i){
			hit.polygon.xpoints[i]+=right;
			hit.polygon.ypoints[i]-=height;
		}
		return Collision.isWorldCollision(partie, hit, true);
		/*//get all hitboxes: it can be slower 
		List<Collidable> mondeHitboxes=Collision.getMondeBlocs(partie.monde, objectHitboxL, partie.INIT_RECT, partie.TAILLE_BLOC);
		//if there is a collision between mondeHitboxes and objectHitbox, it means that lower the hitbox by 1 leads to a 
		//collision: the object is likely to be on the ground (otherwise, it is in a bloc).
		for(Collidable b : mondeHitboxes)
			if(GJK_EPA.intersectsB(objectHitboxL.polygon, b.getHitbox(partie.INIT_RECT).polygon, new Vector2d(right,-height))==GJK_EPA.TOUCH)
				return true;
		return false;*/
	}

	@Override
	public void memorizeCurrentValue()
	{
		final Point memPos= new Point(getXpos(),getYpos()); 
		final Mouvement_entity memDep = (Mouvement_entity) getDeplacement().Copy();
		final int memAnim = getAnim();
		final Vitesse memVitloca = localVit.Copy();
		final CachedHitbox cachedHit = this.getCacheHitboxCopy();
		final CachedAffineTransform cachedDrawTr = this.getCacheDrawTrCopy();
		currentValue=new CurrentValue(){		
			@Override
			public void res()
			{setXpos_sync(memPos.x);setYpos_sync(memPos.y);setDeplacement(memDep);setAnim(memAnim);localVit=memVitloca;
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
		// TODO Auto-generated method stub
		
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
