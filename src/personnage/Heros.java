package personnage;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import collision.CustomBoundingSquare;
import collision.GJK_EPA;
import debug.Debug_time;
import deplacement.Accroche;
import deplacement.Attente;
import deplacement.Course;
import deplacement.Deplace;
import deplacement.Glissade;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import deplacement.Saut;
import deplacement.Tir;
import effects.Roche_effect;
import fleches.Fleche;
import fleches.destructrice.Fleche_bogue;
import fleches.destructrice.Fleche_explosive;
import fleches.destructrice.Fleche_foudre;
import fleches.destructrice.Fleche_trou_noir;
import fleches.materielle.Fleche_electrique;
import fleches.materielle.Fleche_feu;
import fleches.materielle.Fleche_glace;
import fleches.materielle.Fleche_roche;
import fleches.rusee.Fleche_auto_teleguidee;
import fleches.rusee.Fleche_cac;
import fleches.rusee.Fleche_retard;
import fleches.rusee.Fleche_v_fleche;
import fleches.sprirituelle.Fleche_grappin;
import fleches.sprirituelle.Fleche_lumiere;
import fleches.sprirituelle.Fleche_ombre;
import fleches.sprirituelle.Fleche_vent;
import monstre.TirMonstre;
import music.Music;
import partie.AbstractModelPartie;
import partie.PartieTimer;
import principal.InterfaceConstantes;
import types.Entitie;
import types.Hitbox;
import types.Projectile;
import types.TypeObject;
import types.Vitesse;

public class Heros extends Entitie{

	public int nouvAnim= 0;
	public Mouvement nouvMouv =null;
	//on definit son type de deplacement 


	private float seyeri=InterfaceConstantes.MAXSEYERI;
	private float not_enough_seyeri=0; // variable to keep track of the amount of seyeri that a rejected action required (used for visual effect)
	public void setNotEnoughSeyeri(float val){not_enough_seyeri=val;not_enough_seyeri_counter=10;}
	public float getNotEnoughSeyeri(){return not_enough_seyeri;}
	private int not_enough_seyeri_counter; // use this to let the red indicaters appear longer
	private double accrocheCooldownTimer=0;
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
	//derniere fois que la spe a �t� baiss� � cause du slow down ou augment� hors du slow down
	private double tempsSeyeri;
	public boolean invincible=false;

	//lorsque le personnage est touche, on le fait clignoter, ce booleen permet de savoir si on l'affiche ou non
	public boolean afficheTouche=true; 

	//variable for ChangMouv to indicates if the animation was changed or not 
	private boolean animationChanged = true;

	public boolean runBeforeJump=false;
	//booleen pour savoir si le heros vient de sauter
	public boolean debutSaut = false;
	//booleen pour savoir si on arrive � la fin du saut 
	public boolean finSaut = false;
	//booleen pour savoir si il est en saut/peut sauter 
	public boolean peutSauter = true;
	//booleen pour savoir si le personnage veut sauter alors qu'il glisse/accroche
	public boolean sautGlisse = false;
	public boolean sautAccroche = false;
	private boolean wasGrounded =false;

	public boolean doitEncocherFleche=false;
	public boolean flecheEncochee=false;
	//Which arrow is currently armed: changed in model partie
	public String tir_type = TypeObject.FLECHE;

	//In order to determine affinity: 1=Materiel, 2=Spirituel, 3=Destructeur 4=Ruse
	public int current_slot = 0;
	//Which arrows are equiped per affinity
	//private String[] slots = {TypeObject.BOGUE,TypeObject.EXPLOSIVE,TypeObject.FOUDRE,TypeObject.TROU_NOIR};
	//private String[] slots = {TypeObject.ROCHE,TypeObject.FEU,TypeObject.GLACE,TypeObject.ELECTRIQUE};
	//private String[] slots = {TypeObject.VENT,TypeObject.GRAPPIN,TypeObject.OMBRE,TypeObject.LUMIERE};
	//private String[] slots = {TypeObject.AUTO_TELEGUIDEE,TypeObject.CAC,TypeObject.RETARD,TypeObject.V_FLECHE};

	//private String[] slots = {TypeObject.ROCHE,TypeObject.VENT,TypeObject.OMBRE,TypeObject.LUMIERE};
	//private String[] slots = {TypeObject.ELECTRIQUE,TypeObject.EXPLOSIVE,TypeObject.FEU,TypeObject.GLACE};
	//private String[] slots = {TypeObject.GRAPPIN,TypeObject.LUMIERE,TypeObject.OMBRE,TypeObject.ROCHE};
	private String[] slots = {TypeObject.ROCHE,TypeObject.BOGUE,TypeObject.OMBRE,TypeObject.FEU};


	public String[] getSlots(){return slots;}
	public void changeSlot(AbstractModelPartie partie,int slotNum,String newArrow)
	{
		slots[slotNum]=newArrow;
		addSeyeri(partie, -25);
	}
	/**
	 * 
	 * @param special is the arrow special or regular
	 */
	public void set_tir_type(boolean special){
		if(special)
			tir_type=slots[current_slot];
		else
			tir_type=TypeObject.FLECHE;
	} 
	/**
	 * 
	 * @param special: true to return the special arrow name 
	 * @return
	 */
	public String get_tir_type(boolean special){
		if(special)
			return slots[current_slot];
		else
			return TypeObject.FLECHE;
	} 
	//last time an arrow was shot
	public long last_shoot_time = -1;
	//last time I armed an arrow
	public long last_armed_time = -1;
	//last time the heros wall jump: use to disable keys 
	public double last_wall_jump_time = -1;

	//booleen pour savoir si on veut deplacer le personnage sur le c�t� quand il saut 
	public boolean deplaceSautDroit = false;
	public boolean deplaceSautGauche =false;

	//objet pour connaitre les valeurs comme la taille des sprites pour une action donn�e
	/*protected Attente attente= new Attente();
	protected Marche marche = new Marche();
	protected Course course = new Course();
	protected Saut saut = new Saut();
	protected Glissade glissade = new Glissade();
	protected Tir tir = new Tir();*/

	public double rotation_tir=0;
	protected ResetHandleCollision resetHandleCollision;

	//variable pour deplace ecran 
	private boolean last_align_left=true;
	public boolean getLast_align_l(){return last_align_left;}
	private boolean last_align_down=true;
	public boolean getLast_align_d(){return last_align_down;}

	public Heros( int xPo,int yPo, int _anim, int current_frame){
		super.init();
		MAXLIFE = 100;
		MINLIFE = 0;
		life= MAXLIFE;
		xpos_sync(xPo);
		ypos_sync(yPo); 
		localVit= new Vitesse(0,0);
		fixedWhenScreenMoves=true;
		deplacement=new Attente(this,Attente.attente_gauche,current_frame);
		anim=_anim;
		nouvAnim= 0;
		nouvMouv = new Attente(this,Attente.attente_gauche,current_frame);
		tempsTouche=PartieTimer.me.getElapsedNano();
		controlScreenMotion=true;
		this.setCollideWithout(Arrays.asList(TypeObject.HEROS,TypeObject.FLECHE));
	}

	public void onAddLife(){};

	public String droite_gauche (int anim){
		return deplacement.droite_gauche(this, anim);
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
		if((PartieTimer.me.getElapsedNano()-tempsSeyeri)*Math.pow(10, -6)>InterfaceConstantes.TEMPS_VAR_SEYERI)
		{
			tempsSeyeri=PartieTimer.me.getElapsedNano();
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
		return deplacement.getMaxBoundingSquare(this);
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp) {
		try{
			//As the heros is fixed when the screen move (position relative to screen), need to substract ScreenDisp
			return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos()-screenDisp.x,ypos()-screenDisp.y),true);}
		catch(IndexOutOfBoundsException e)
		{
			return new Hitbox();
		}
	}
	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp, Mouvement _dep, int _anim) {
		try{
			Mouvement_perso temp = (Mouvement_perso) _dep.Copy(this); //create the mouvement
			return Hitbox.plusPoint(temp.hitbox.get(_anim), new Point(xpos()-screenDisp.x,ypos()-screenDisp.y),true);
		}
		catch(IndexOutOfBoundsException e)
		{
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
		int up = getGlobalVit(partie).y<0? 0 : (deplacement.IsDeplacement(Mouvement_perso.saut)?4 : 0); //if gliss: up of hand
		int down = getGlobalVit(partie).y<0? 27 : (deplacement.IsDeplacement(Mouvement_perso.saut)?31 : 24);//if gliss: down of hand
		if(!gliss)
		{
			//small square below hand: if it is in collision while the hand is not: heros should change to Accroche
			up=   down+1;
			down = down + 15;
		}
		//WARNING assume the Heros hitbox is a square/rectangle not rotated 
		Hitbox herosHit = this.getHitbox(partie.getScreenDisp(),partie.getScreenDisp());

		List<Vector2d> upLeftP = Hitbox.supportsPoint(new Vector2d(-1,-1), herosHit.polygon);
		List<Vector2d> downLeftP = Hitbox.supportsPoint(new Vector2d(-1,1), herosHit.polygon);
		List<Vector2d> downRightP = Hitbox.supportsPoint(new Vector2d(1,1), herosHit.polygon);
		List<Vector2d> upRightP = Hitbox.supportsPoint(new Vector2d(1,-1), herosHit.polygon);

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
		//Debug_stack stack = new Debug_stack(); 
		//stack.print(-1);
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
					this.pxpos(appliedMotion.x);
					this.pypos(appliedMotion.y);
				}

			}
		//In this case, the object does not consider collision with the world(or is not colliding with ref_object), hence it should no be moved by the ref_object.
		//However as it is not preventing the ref_object from moving, the function returns true (but the object is not registred in collidableToMove)
		return true;
	}

	/*public Hitbox getWorldPosition(AbstractModelPartie partie)
	{
		int xCompScreenMove=0; 
		int yCompScreenMove=0;
		Point deplaceEcran =new Point(partie.xScreendisp,partie.yScreendisp);
		if(fixedWhenScreenMoves)
		{
			xCompScreenMove=deplaceEcran.x;
			yCompScreenMove=deplaceEcran.y;
		}

		Point p = new Point(xCompScreenMove,
				yCompScreenMove);
		return Hitbox.minusPoint(getHitbox(partie.INIT_RECT),p,false);
	}
	public Hitbox getWorldPosition(AbstractModelPartie partie,Hitbox hit)
	{
		int xCompScreenMove=0; 
		int yCompScreenMove=0;
		Point deplaceEcran =new Point(partie.xScreendisp,partie.yScreendisp);
		if(fixedWhenScreenMoves)
		{
			xCompScreenMove=deplaceEcran.x;
			yCompScreenMove=deplaceEcran.y;
		}

		Point p = new Point(xCompScreenMove,
				yCompScreenMove);
		return Hitbox.minusPoint(hit,p,false);
	}*/
	public boolean isGrounded(AbstractModelPartie partie)
	{
		Hitbox hit = getHitbox(partie.INIT_RECT,partie.getScreenDisp());
		assert hit.polygon.npoints==4;
		//get world hitboxes with Collision
		//lowers all points by 1 at most 
		for(int i=0; i<hit.polygon.npoints; ++i)
			hit.polygon.ypoints[i]+=1;
		//get all hitboxes: it can be slower 
		boolean res =  Collision.isWorldCollision(partie,hit,true);
		return res;
		/*List<Collidable> mondeHitboxes=Collision.getMondeBlocs(partie.monde, objectHitboxL, partie.INIT_RECT, partie.TAILLE_BLOC);
		//if there is a collision between mondeHitboxes and objectHitbox, it means that lower the hitbox by 1 leads to a 
		//collision: the object is likely to be on the ground (otherwise, it is in a bloc).
		for(Collidable b : mondeHitboxes){
			if(GJK_EPA.intersectsB(objectHitboxL.polygon, b.getHitbox(partie.INIT_RECT).polygon, new Vector2d(0,1))==GJK_EPA.TOUCH)
			{
				return true;
			}}
		return false;*/
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

		final boolean mem_finSaut = finSaut;
		final boolean mem_peutSauter=peutSauter;
		final boolean mem_useGravity=useGravity;

		resetHandleCollision = new ResetHandleCollision(){
			@Override
			public void reset()
			{
				finSaut=mem_finSaut;
				peutSauter=mem_peutSauter;
				useGravity= mem_useGravity;
			}};

			if(collision_bas)
			{
				finSaut=true;
				peutSauter=true;
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
		if(TypeObject.isTypeOf(collider, TypeObject.TIR_MONSTRE))
		{
			if(!invincible){
				TirMonstre tm = (TirMonstre)collider;
				touche(((TirMonstre)collider).damage);
			}
		}
	}

	@Override
	public void memorizeCurrentValue() {

		final Point memPos= new Point(xpos(),ypos()); 
		final Mouvement_perso memDep = (Mouvement_perso) deplacement.Copy(this);
		final int memAnim = anim;
		final Vitesse memVitloca = localVit.Copy();
		currentValue=new CurrentValue(){		
			@Override
			public void res()
			{xpos_sync(memPos.x);ypos_sync(memPos.y);deplacement=memDep;anim=memAnim;localVit=(memVitloca);}};
	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace, boolean update_with_speed) {
		boolean doitDeplace=false;
		try {
			animationChanged=true;
			anim=changeMouv(nouvMouv, nouvAnim, partie,deplace, update_with_speed);
			afterChangeMouv(partie);
			partie.changeMouv= false;
			doitDeplace=true;

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean[] res = {doitDeplace,animationChanged};
		return res;
	}

	private Collidable accrocheCol=null;
	/**
	 * Donne l'animation suivante, en fonction du mouvement en cours et de son animation 
	 * 
	 * @param animHeros animation actuelle du personnage  
	 * @param heros le personnage 
	 * @param nouvMouv le nouveau mouvement donn� par partieRapideActionListener
	 * @param nouvAnim la nouvelle animation donn�e par partieRapideActionListener
	 * @param blocDessous savoir si le bloc en dessous du sprite est bloquant
	 * @param blocDroitGlisse savoir si le bloc a droite du sprite est bloquant
	 * @param blocGaucheGlisse savoir si le bloc a gauche du sprite est bloquant
	 * @param Monde le niveau en cours
	 * @return la nouvelle animation 
	 * 
	 */	
	public int changeMouv(Mouvement nouvMouv, int nouvAnim, AbstractModelPartie partie,Deplace deplace, boolean update_with_speed) throws InterruptedException
	{
		Debug_time debugTime = new Debug_time();
		debugTime.init();

		Hitbox blocDroitGlisseHit = null;
		Hitbox blocGaucheGlisseHit = null;

		Hitbox blocDroitAccrocheHit = null;//not null if accroche special hitbox is colliding with world 
		Hitbox blocGaucheAccrocheHit = null;

		int animHeros=anim;
		//translate all object hitboxes, see collision to get full formula
		Hitbox herosHitbox= getHitbox(partie.getScreenDisp(),partie.getScreenDisp());
		debugTime.elapsed("monde bloc", 4);

		List<Collidable> mondeBlocs = Collision.getMondeBlocs(partie.monde,herosHitbox, partie.INIT_RECT,partie.getScreenDisp(),partie.TAILLE_BLOC);
		List<Collidable> effectColl = Collidable.getAllCollidableEffect(partie, CustomBoundingSquare.getScreen());
		List<Collidable> allColli = new ArrayList<Collidable>();
		allColli.addAll(mondeBlocs);
		allColli.addAll(effectColl);

		debugTime.elapsed("loop collision", 4);

		for(Collidable coll : allColli)
		{
			debugTime.elapsed("get hitbox", 5);

			Hitbox box = coll.getHitbox(partie.INIT_RECT,partie.getScreenDisp());
			Polygon p_glissade_d= getGliss_Accroch_Hitbox(partie,true,true).polygon;
			Polygon p_glissade_g= getGliss_Accroch_Hitbox(partie,true,false).polygon;
			Polygon p_accroche_d= getGliss_Accroch_Hitbox(partie,false,true).polygon;
			Polygon p_accroche_g= getGliss_Accroch_Hitbox(partie,false,false).polygon;


			Polygon p_coll= box.polygon;
			int a = GJK_EPA.intersectsB(p_glissade_d,p_coll, getGlobalVit(partie).vect2d());
			int b = GJK_EPA.intersectsB(p_glissade_g,p_coll, getGlobalVit(partie).vect2d());
			int c = GJK_EPA.intersectsB(p_accroche_d,p_coll, getGlobalVit(partie).vect2d());
			int d = GJK_EPA.intersectsB(p_accroche_g,p_coll, getGlobalVit(partie).vect2d());

			accrocheCol = coll;

			blocDroitGlisseHit=((a==GJK_EPA.TOUCH) || (a==GJK_EPA.INTER ))?box:blocDroitGlisseHit;

			blocGaucheGlisseHit=((b==GJK_EPA.TOUCH) || (b==GJK_EPA.INTER))?box :blocGaucheGlisseHit ;

			blocDroitAccrocheHit= ((c==GJK_EPA.TOUCH) || (c==GJK_EPA.INTER ))?box : blocDroitAccrocheHit;

			blocGaucheAccrocheHit= ((d==GJK_EPA.TOUCH) || (d==GJK_EPA.INTER ))?box : blocGaucheAccrocheHit ;

			if( ( (blocDroitGlisseHit!=null) || (blocGaucheGlisseHit!=null)) && ((blocDroitAccrocheHit !=null) && (blocGaucheAccrocheHit!=null)))
			{
				break;
			}
		}
		debugTime.elapsed("after init 1", 4);

		boolean blocDroitGlisse=blocDroitGlisseHit!=null;
		boolean blocGaucheGlisse=blocGaucheGlisseHit!=null;
		boolean blocDroitAccroche=blocDroitAccrocheHit!=null;
		boolean blocGaucheAccroche=blocGaucheAccrocheHit!=null;

		// le heros est en chute libre
		boolean falling = !isGrounded(partie);
		wasGrounded=!falling;
		if(falling)
			useGravity=falling && !this.deplacement.IsDeplacement(Mouvement_perso.accroche) && !isDragged();
		else if(update_with_speed)
			useGravity=false;
		//le heros chute ou cours vers un mur: il commence � glisser sur le mur 
		boolean[] beginSliding= computeBeginSliding(partie,blocDroitGlisse,blocGaucheGlisse,falling); 
		boolean beginSliding_r= beginSliding[0] ;
		boolean beginSliding_l= beginSliding[1] ;

		boolean[] beginAccroche= computeAccroche(partie,blocDroitGlisse,blocGaucheGlisse,blocDroitAccroche,blocGaucheAccroche,falling);
		boolean beginAccroche_r = beginAccroche[1];
		boolean beginAccroche_l = beginAccroche[0];

		//update some values because player might have been ejected due to a fleche vent
		updateVarSaut(falling, beginAccroche_r || beginAccroche_l, beginSliding_r || beginSliding_l);

		//le heros atteri alors qu'il �tait en chute libre,
		boolean landing = (!falling) && deplacement.IsDeplacement(Mouvement_perso.saut) && (animHeros == 1 || animHeros == 4 ||
				( (animHeros == 0 || animHeros ==  3) && this.getGlobalVit(partie).y>=0 ));
		boolean standup = deplacement.IsDeplacement(Mouvement_perso.saut) && (animHeros ==2 || animHeros ==5)  && deplacement.animEndedOnce();
		//deal with the case where the heros was ejected while standing up
		if(standup && falling)
		{
			standup=false;
			finSaut=false;
		}

		//special case, dealing with stop of accroche 
		boolean stopAccrocheD = deplacement.IsDeplacement(Mouvement_perso.accroche) && !(blocDroitGlisse && blocDroitAccroche) 
				&& (droite_gauche(animHeros).equals(Mouvement.DROITE)) && (anim != 1) && (anim != 3);
		boolean stopAccrocheG = deplacement.IsDeplacement(Mouvement_perso.accroche) && !(blocGaucheGlisse && blocGaucheAccroche) 
				&& (droite_gauche(animHeros).equals(Mouvement.GAUCHE))&& (anim != 1) && (anim != 3);
		if(stopAccrocheD || stopAccrocheG)
		{
			Mouvement nextMouv = new Attente(this,droite_gauche(animHeros).equals(Mouvement.GAUCHE) ?Attente.attente_gauche: Attente.attente_droite,partie.getFrame());
			int nextAnim = (droite_gauche(animHeros).equals(Mouvement.GAUCHE) ? 0 : 2 );
			alignHitbox(animHeros,nextMouv,nextAnim,partie ,deplace,blocGaucheGlisse);
			//on ajuste la position du personnage pour qu'il soit centrÃ© 
			anim= nextAnim;
			deplacement=nextMouv;
			localVit.y=(0);

			return(anim);

		}
		//le heros touche le sol en glissant
		boolean landSliding = finSaut && deplacement.IsDeplacement(Mouvement_perso.glissade);

		//le heros d�croche du mur
		boolean endSliding = deplacement.IsDeplacement(Mouvement_perso.glissade) && 
				((!blocDroitGlisse && droite_gauche(animHeros).equals(Mouvement.GAUCHE)) ||
						(!blocGaucheGlisse && droite_gauche(animHeros)==(Mouvement.DROITE)) || !falling) ;
		
		int anim=animHeros;

		debugTime.elapsed("after init 2, fleche", 4);

		if( (doitEncocherFleche || flecheEncochee))//cas diff�rent puisqu'on ne veut pas que l'avatar ait l'animation de chute en l'air
		{
			double[] anim_rotation = deplace.getAnimRotationTir(partie,false);
			int animSuivante = (int)anim_rotation[0];
			rotation_tir=anim_rotation[1];
			//on decalle
			Mouvement mouvSuivant = new Tir(this,Tir.tir,partie.getFrame());
			alignHitbox(animHeros,mouvSuivant,animSuivante ,partie,deplace,blocGaucheGlisse);
			deplacement= mouvSuivant;
			deplacement.setSpeed(this, anim);
			if(!falling)
			{
				finSaut=false;
				peutSauter=true;
				useGravity=false;
			}
			return(animSuivante);

		}
		debugTime.elapsed("slide", 4);

		//SPECIAL CASE that comes from computation on the current Mouvement 
		if( (beginSliding_r||beginSliding_l) && !deplacement.IsDeplacement(Mouvement_perso.glissade))
		{
			Mouvement nextMouv = new Glissade(this,beginSliding_l?Glissade.glissade_gauche:Glissade.glissade_droite,partie.getFrame());
			int nextAnim = (beginSliding_l ? 0 :1);

			alignHitbox(animHeros,nextMouv,nextAnim,partie ,deplace,blocGaucheGlisse);
			anim = nextAnim;
			deplacement= nextMouv;
			deplacement.setSpeed(this, anim);
			deplaceSautGauche=false;
			deplaceSautDroit=false;
			localVit.x=(0);
			return(anim);
		}
		debugTime.elapsed("accroche", 4);

		if((beginAccroche_r || beginAccroche_l) && !deplacement.IsDeplacement(Mouvement_perso.accroche))
		{
			//TODO: 
			Mouvement nextMouv= new Accroche(this, beginAccroche_l? Accroche.accroche_gauche:Accroche.accroche_droite,partie.getFrame());
			int nextAnim = (beginAccroche_l?0:2);

			//Manually align hitbox 
			int xdir = beginAccroche_l ? -1 :1;
			int ydir = -1; //get upper part of hitbox

			double ycurrentup = Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),deplacement, animHeros).polygon).y;
			//get value to align hitbox 
			double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),deplacement, animHeros).polygon).x -
					Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextAnim).polygon).x;

			double dy= ycurrentup -
					Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv, nextAnim).polygon).y;

			//align to lower bloc: make ycurrentup align with top of collider hitbox 

			double colliderTopY=0;
			if(beginAccroche_r)
			{
				colliderTopY= Hitbox.supportPoint(new Vector2d(0,-1), blocDroitAccrocheHit.polygon).y;
			}
			else
			{
				colliderTopY= Hitbox.supportPoint(new Vector2d(0,-1), blocGaucheAccrocheHit.polygon).y;
			}
			double align_to_ground = colliderTopY-ycurrentup;

			pxpos_sync((int)dx);
			pypos_sync((int)dy);
			pypos_sync((int) align_to_ground);


			boolean motionSuccess = true;
			motionSuccess= alignTestValid(nextMouv,nextAnim, partie,deplace, this, true);
			//WARNING: problem if the action is not succeeded 
			accrocheCooldownTimer=PartieTimer.me.getElapsedNano();

			if(motionSuccess){
				for(Collidable eff : partie.arrowsEffects){
					if(eff instanceof Roche_effect){
						Roche_effect r_eff = (Roche_effect) eff;
						if((r_eff.typeEffect==0) && (eff == accrocheCol))
						{
							r_eff.registerAccrocheCol(this);
						}
					}
				}
				useGravity=false;
				
				deplacement=nextMouv;
				anim=nextAnim;
				deplacement.setSpeed(this, nextAnim);

				return anim;
			}
			else
			{
				pxpos_sync((int)-dx);
				pypos_sync((int)-dy);
				pypos_sync((int)-align_to_ground);
			}

		}

		debugTime.elapsed("fall", 4);

		//attention, falling est le seul bloc de code � ne pas avoir de return 
		if(falling)
		{
			peutSauter=false;
			if(!(deplacement.IsDeplacement(Mouvement_perso.glissade)||deplacement.IsDeplacement(Mouvement_perso.course)
					||deplacement.IsDeplacement(Mouvement_perso.accroche)))
			{
				int up = getGlobalVit(partie).y>=0 ? 1 : 0;
				int type_mouv = droite_gauche(animHeros).equals(Mouvement.GAUCHE) ? (up==0?Saut.jump_gauche:Saut.fall_gauche) :
					(up==0?Saut.jump_droite:Saut.fall_droite);
				Mouvement mouvSuivant = new Saut(this,type_mouv,partie.getFrame());
				int animSuivant = (droite_gauche(animHeros).equals(Mouvement.GAUCHE) ? 0+up :3+up);
				alignHitbox(animHeros,mouvSuivant,animSuivant,partie,deplace,blocGaucheGlisse );		
				animHeros = animSuivant;
				anim=animSuivant;
				deplacement=mouvSuivant;
				deplacement.setSpeed(this, anim);
				//landing = partie.finSaut;
				beginSliding= computeBeginSliding(partie,blocDroitGlisse,blocGaucheGlisse,(falling&&!landing));
			}
		}

		if(deplacement.IsDeplacement(Mouvement_perso.accroche))
		{			
			if( (anim == 1 || anim == 3) && deplacement.animEndedOnce())
			{
				int next_anim = (droite_gauche(animHeros).equals(Mouvement.GAUCHE) ? 0 :2);
				Mouvement nextMouv = new Attente(this,droite_gauche(animHeros).equals(Mouvement.GAUCHE) ?Attente.attente_gauche: Attente.attente_droite,partie.getFrame());

				alignHitbox(animHeros,nextMouv,next_anim,partie,deplace,blocGaucheGlisse);
				anim= next_anim;
				deplacement=nextMouv;
				deplacement.setSpeed(this, anim);



				//on reinitialise les variables de saut 
				debutSaut = false;
				finSaut = false;
				useGravity=false;
				peutSauter = true;
				sautGlisse = false;
				sautAccroche=false;
				return(anim);
			}
		}
		else if(landing) //atterrissage: accroupi 
		{
			Mouvement mouvSuiv = new Saut(this,droite_gauche(animHeros).equals(Mouvement.GAUCHE) ?Saut.land_gauche:Saut.land_droite,partie.getFrame());
			int animSuiv = (droite_gauche(animHeros).equals(Mouvement.GAUCHE)? 2 : 5 );
			//on ajuste la position du personnage pour qu'il soit centr� 
			alignHitbox(animHeros,mouvSuiv,animSuiv,partie,deplace,blocGaucheGlisse );
			finSaut=false;//set landing to false
			deplacement=mouvSuiv;
			anim= animSuiv;
			deplacement.setSpeed(this, anim);

			return(anim);


		}
		else if(standup)//atterissage: se rel�ve
		{

			int nextAnim = runBeforeJump? (droite_gauche(animHeros).equals(Mouvement.GAUCHE) ? 0 : 4 ) : (droite_gauche(animHeros).equals(Mouvement.GAUCHE) ? 0 : 2 );
			Mouvement_perso nextDep=  runBeforeJump? new Course(this,droite_gauche(animHeros).equals(Mouvement.GAUCHE) ?Course.course_gauche:Course.course_droite,partie.getFrame()) 
					: new Attente(this,droite_gauche(animHeros).equals(Mouvement.GAUCHE) ?Attente.attente_gauche:Attente.attente_droite,partie.getFrame());
			//on ajuste la position du personnage pour qu'il soit centr� 
			alignHitbox(animHeros,nextDep,nextAnim,partie,deplace,blocGaucheGlisse);
			//on choisit la direction d'attente			
			localVit.x=(0);
			finSaut=false;
			peutSauter=true;
			anim=nextAnim;
			deplacement=nextDep;

			return(anim);

		}
		else if(landSliding)
		{

			Mouvement nextMouv = new Attente(this,droite_gauche(animHeros).equals(Mouvement.GAUCHE) ?Attente.attente_gauche: Attente.attente_droite,partie.getFrame());
			int nextAnim = (droite_gauche(animHeros).equals(Mouvement.GAUCHE) ? 0 : 2 );
			alignHitbox(animHeros,nextMouv,nextAnim,partie ,deplace,blocGaucheGlisse);
			finSaut=false;
			//on ajuste la position du personnage pour qu'il soit centr� 
			anim= nextAnim;
			deplacement=nextMouv;
			localVit.y=(0);
			return(anim);

		}

		else if(endSliding)
		{

			int nextAnim= (droite_gauche(animHeros).equals(Mouvement.GAUCHE) ? 1 :4);
			Mouvement nextMouv = new Saut(this,droite_gauche(animHeros).equals(Mouvement.GAUCHE) ?Saut.fall_gauche: Saut.fall_droite,partie.getFrame());
			alignHitbox(animHeros,nextMouv,nextAnim,partie,deplace,blocGaucheGlisse);
			anim = nextAnim;
			deplacement= nextMouv;
			deplacement.setSpeed(this, anim);
			localVit.x=(0);
			nouvAnim=anim;	
			return(anim);

		}
		//Call function to check if move is allowed
		boolean allowed = moveAllowed(nouvMouv,nouvAnim);
		//CHANGEMENT DE MOUVEMENT
		debugTime.elapsed("move allow", 4);

		if(partie.changeMouv && allowed)
		{
			boolean accroche_not_enough_space = false;

			if(nouvMouv.IsDeplacement(Mouvement_perso.accroche) && ( (nouvAnim == 1) || (nouvAnim == 3)))
			{				
				//manually align hitbox 
				int xdir = (nouvAnim == 1) ? -1 :1;
				int ydir_up = -1; //get upper part of hitbox
				int ydir_down = 1; //get lower part of hitbox

				//top of hitbox should now be bottom of next hitbox 
				//Manually align hitbox 

				double ycurrentup = Hitbox.supportPoint(new Vector2d(0,ydir_up), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),deplacement, animHeros).polygon).y;
				//get value to align hitbox 
				double next_x = Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nouvMouv, nouvAnim).polygon).x;
				double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),deplacement, animHeros).polygon).x -
						next_x;

				double next_y_bottom = Hitbox.supportPoint(new Vector2d(0,ydir_down), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nouvMouv, nouvAnim).polygon).y;
				double dy= ycurrentup -next_y_bottom;

				//Variables to test  if the heros would have enough space to stand up
				Mouvement nextMouv_space = new Attente(this,droite_gauche(nouvAnim).equals(Mouvement.GAUCHE) ?Attente.attente_gauche: Attente.attente_droite,
						partie.getFrame());
				int nextAnim_space = (droite_gauche(nouvAnim).equals(Mouvement.GAUCHE) ? 0 : 2 );
				double x_space= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextAnim_space).polygon).x;
				double y_bottom_space= Hitbox.supportPoint(new Vector2d(0,ydir_down), getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextAnim_space).polygon).y;

				//Normal match 
				int xdecall = 12 *  ((nouvAnim==1)? -1:1);
				pxpos_sync((int) (dx + xdecall));
				pypos_sync( (int) (dy-1));

				//test if the heros would have enough space to stand up
				double dx_space= next_x -x_space;
				double dy_space= next_y_bottom -y_bottom_space;
				//test if heros collide when stand up 
				pxpos_sync( (int) (dx_space));
				pypos_sync( (int) (dy_space));

				Hitbox attenteHit = getHitbox(partie.INIT_RECT,partie.getScreenDisp(),nextMouv_space, nextAnim_space).copy();
				accroche_not_enough_space=Collision.isWorldCollision(partie,attenteHit , true);
				pxpos_sync( (int) (-dx_space));
				pypos_sync( (int) (-dy_space));
				//revert motion if stuck
				if(accroche_not_enough_space)
				{
					pxpos_sync((int) (-dx - xdecall));
					pypos_sync( (int) (1-dy));

				}

			}

			else{
				alignHitbox(animHeros,nouvMouv,nouvAnim,partie,deplace,blocGaucheGlisse);
			}
			if(nouvMouv.IsDeplacement(Mouvement_perso.saut) && debutSaut)
			{
				if(deplacement.IsDeplacement(Mouvement_perso.course))
					runBeforeJump=true;
				else
					runBeforeJump=false;
			}

			if(!accroche_not_enough_space)
			{
				anim=nouvAnim;
				deplacement=nouvMouv;
				deplacement.setSpeed(this, anim);
			}

		}
		else
			//if(!partie.changeMouv ) // MEME MOUVEMENT QUE PRECEDEMMENT , otherwise problem with landing 
		{
			animationChanged=false;
			int nextAnim = deplacement.updateAnimation(this, animHeros, partie.getFrame(),conditions.getSpeedFactor());
			alignHitbox(animHeros,deplacement,nextAnim,partie,deplace,blocGaucheGlisse);
			anim= nextAnim;
			deplacement.setSpeed(this, anim);

		}
		debugTime.elapsed("end", 4);

		partie.changeMouv=false;

		return(anim);

	}

	public void afterChangeMouv(AbstractModelPartie partie)
	{
		if(!deplacement.IsDeplacement(Mouvement_perso.accroche)){
			//unregister accroche from roche_effect
			for(Collidable eff : partie.arrowsEffects){
				if(eff instanceof Roche_effect)
				{
					Roche_effect r_eff = (Roche_effect) eff;
					if(r_eff.typeEffect==0)
						r_eff.unregisterAccrocheCol(this);
				}
			}
			accrocheCol=null;
		}
	}

	private boolean[] computeSlide_Accroche(AbstractModelPartie partie,boolean slide, boolean blocDroitGlisse, boolean blocGaucheGlisse, boolean blocDroitAccroche, boolean blocGaucheAccroche, boolean falling)
	{
		boolean blocGauche = slide? blocGaucheGlisse : (!blocGaucheGlisse && blocGaucheAccroche);
		boolean blocDroit = slide? blocDroitGlisse:  (!blocDroitGlisse && blocDroitAccroche);
		boolean falling_running = ( falling && deplacement.IsDeplacement(Mouvement_perso.course)) || deplacement.IsDeplacement(Mouvement_perso.saut);
		boolean accrocheCooldownDone = slide? true : (PartieTimer.me.getElapsedNano() - accrocheCooldownTimer) > InterfaceConstantes.ACCROCHE_COOLDOWN;
		//Special case in which accroche should not happen : if object is dragged or if wind arrow stick to it 
		boolean no_accroche= slide? false  : this.isDragged(); // (this.isDragged()||this.isWindProjected());
		boolean res_d =  (blocGauche&&(last_colli_left||getGlobalVit(partie).x<0)) && falling_running && !no_accroche && accrocheCooldownDone; 
		boolean res_g =  (blocDroit && (last_colli_right||getGlobalVit(partie).x>0))&& falling_running  && !no_accroche && accrocheCooldownDone; 

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

	private boolean moveAllowed(Mouvement nextMove, int nextAnim)
	{
		Mouvement currentM = deplacement;

		boolean allowed=true;

		//Unexpected behaviour: attente/marche while being in the air(ie current move being saut/glissade )
		//Unexpected behaviour: going right/left in the air while landing

		boolean inAirAllowed = !( (currentM.IsDeplacement(Mouvement_perso.saut) || currentM.IsDeplacement(Mouvement_perso.glissade)) &&
				(nextMove.IsDeplacement(Mouvement_perso.attente) || nextMove.IsDeplacement(Mouvement_perso.marche) ));

		boolean airLandingAllowed= ! (currentM.IsDeplacement(Mouvement_perso.saut) && nextMove.IsDeplacement(Mouvement_perso.saut) && 
				((anim==2) || (anim==5) )) ; //movement in air allowed only if not landing

		allowed = allowed && airLandingAllowed && inAirAllowed;
		return allowed; 
	}

	//Move the character to center it before the animation change.
	public void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace,boolean blocGaucheGlisse)
	{
		alignHitbox(animActu, depSuiv, animSuiv, partie, deplace, blocGaucheGlisse,null,null);
	}
	public void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace,boolean blocGaucheGlisse, Boolean forcedleft,
			Boolean forcedDown )
	{
		boolean isGlissade = deplacement.IsDeplacement(Mouvement_perso.glissade);
		boolean going_left = getGlobalVit(partie).x<0;

		boolean facing_left_still= getGlobalVit(partie).x==0 &&(droite_gauche(animActu).equals(Mouvement.GAUCHE)|| last_colli_left)&& !isGlissade;
		boolean sliding_left_wall = (droite_gauche(animActu)==Mouvement.DROITE) && isGlissade;
		boolean start_falling_face_right = (!deplacement.IsDeplacement(Mouvement_perso.saut) && depSuiv.IsDeplacement(Mouvement_perso.saut)) 
				&& (droite_gauche(animActu).equals(Mouvement.DROITE));
		boolean start_falling_face_left = (!deplacement.IsDeplacement(Mouvement_perso.saut) && depSuiv.IsDeplacement(Mouvement_perso.saut)) 
				&& (droite_gauche(animActu).equals(Mouvement.GAUCHE));
		boolean left = ! start_falling_face_left && ( going_left|| facing_left_still ||sliding_left_wall || blocGaucheGlisse || start_falling_face_right) ; 
		boolean down = getGlobalVit(partie).y>=0; 

		if(forcedleft!=null)
			left=forcedleft;
		if(forcedDown!=null)
			down=forcedDown;
		last_align_left=left;
		last_align_down=down;
		super.alignHitbox(animActu,depSuiv, animSuiv, partie,deplace,left, down,this,!depSuiv.IsDeplacement(Mouvement_perso.glissade));
	}

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
		if(deplacement.IsDeplacement(Mouvement_perso.tir) && !useGravity)
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
		System.out.println("heros stuck "+ deplacement.getClass().getName()+" "+ anim);
		if(currentValue!=null)
			currentValue.res();

		if(resetHandleCollision != null){
			resetHandleCollision.reset();
		}
		doitEncocherFleche=false;
	}

	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {
		//The animation change is successful: create arrow
		if(doitEncocherFleche && deplacement.IsDeplacement(Mouvement_perso.tir))
		{
			this.shootArrow(partie);
			doitEncocherFleche=false;
			flecheEncochee=true;
		}
		doitEncocherFleche=false;

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
	public Fleche getArrowInstance(AbstractModelPartie partie, boolean special,boolean add_to_list)
	{
		Fleche fleche = null;
		String tir_type_ =get_tir_type(special);
		//Only give the shooter information in the fleche constructor if it is needed 

		//MATERIEL
		if(tir_type_.equals(TypeObject.FEU))
			fleche =new Fleche_feu(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO
		else if(tir_type_.equals(TypeObject.ELECTRIQUE))
			fleche =new Fleche_electrique(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO
		else if(tir_type_.equals(TypeObject.GLACE))
			fleche =new Fleche_glace(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO
		else if(tir_type_.equals(TypeObject.ROCHE))
			fleche =new Fleche_roche(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO

		//SPIRITUELLE
		else if(tir_type_.equals(TypeObject.LUMIERE))
			fleche =new Fleche_lumiere(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type_.equals(TypeObject.GRAPPIN))
			fleche =new Fleche_grappin(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type_.equals(TypeObject.OMBRE))
			fleche =new Fleche_ombre(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type_.equals(TypeObject.VENT))
			fleche =new Fleche_vent(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());

		//DESTRUCTEUR
		else if(tir_type_.equals(TypeObject.BOGUE))
			fleche =new Fleche_bogue(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());
		else if(tir_type_.equals(TypeObject.FOUDRE))
			fleche =new Fleche_foudre(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO
		else if(tir_type_.equals(TypeObject.EXPLOSIVE))
			fleche =new Fleche_explosive(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO
		else if(tir_type_.equals(TypeObject.TROU_NOIR))
			fleche =new Fleche_trou_noir(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO

		//RUSE
		else if(tir_type_.equals(TypeObject.AUTO_TELEGUIDEE))
			fleche =new Fleche_auto_teleguidee(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO
		else if(tir_type_.equals(TypeObject.CAC))
			fleche =new Fleche_cac(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO
		else if(tir_type_.equals(TypeObject.RETARD))
			fleche =new Fleche_retard(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO
		else if(tir_type_.equals(TypeObject.V_FLECHE))
			fleche =new Fleche_v_fleche(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());//TODO

		else if(tir_type_.equals(TypeObject.FLECHE))
			fleche =new Fleche(partie.tabFleche,partie.getFrame(),this,add_to_list,conditions.getDamageFactor(),conditions.getShotSpeedFactor());

		return fleche;
	}

	/**
	 * Test if the Heros can shoot an arrow (enough seyeri, can shoot several instance of this arrow...) 
	 */
	public boolean canShootArrow(AbstractModelPartie partie)
	{
		List<Projectile> tabFleche= partie.tabFleche;
		Fleche f = getArrowInstance(partie,!tir_type.equals(TypeObject.FLECHE),false);
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
				// canReshoot is to handle the case of flecheBogue were arrows are instantiated and shot but the heros can create new ones 
				if(TypeObject.isTypeOf(fl, f, true) && fl.shooter==this && !fl.getCanReshot())
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
				//TODO: check if this is safe to remove this 
				/*else if(!fl.getCanReshot()) 
				{
					arrow_already_shot=false; // allow reshoot
				}*/
			}
		}


		return (enough_seyeri && notParalyzed && can_shoot_one_more);
	}

	public void shootArrow(AbstractModelPartie partie)
	{	
		Fleche fleche =getArrowInstance(partie,!tir_type.equals(TypeObject.FLECHE),true);
		addSeyeri(partie, fleche.seyeri_cost);

	}
	@Override
	public void onDestroy(AbstractModelPartie partie) {
		//Do nothing
	}

	private void updateVarSaut(boolean falling, boolean accroche, boolean glisse) {
		this.peutSauter=!falling;
		//this.sautAccroche=this.sautAccroche && accroche;
		//this.sautGlisse=this.sautGlisse;
		this.debutSaut=this.debutSaut && ! falling;
		this.finSaut=this.finSaut && !falling;
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

