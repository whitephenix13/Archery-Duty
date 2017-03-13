package personnage;

import java.awt.Point;
import java.awt.Polygon;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import collision.GJK_EPA;
import deplacement.Accroche;
import deplacement.Attente;
import deplacement.Course;
import deplacement.Deplace;
import deplacement.Glissade;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import deplacement.Saut;
import deplacement.Tir;
import music.Music;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Bloc;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Heros extends Collidable{

	public int nouvAnim= 0;
	public Mouvement nouvMouv = new Attente(TypeObject.heros,Attente.attente_gauche,0);
	//on définit son type de déplacement 

	private int life=InterfaceConstantes.MAXLIFE;
	private int spe=InterfaceConstantes.MAXSPE;

	private long tempsTouche;
	private long tempsClignote;
	//derniere fois que la spe a été baissé à cause du slow down ou augmenté hors du slow down
	private long tempsSpe;
	public boolean invincible=true;

	//lorsque le personnage est touche, on le fait clignoter, ce booleen permet de savoir si on l'affiche ou non
	public boolean afficheTouche=true; 

	//variable for ChangMouv to indicates if the animation was changed or not 
	private boolean animationChanged = true;

	public boolean runBeforeJump=false;
	//booleen pour savoir si le heros vient de sauter
	public boolean debutSaut = false;
	//booleen pour savoir si on arrive à la fin du saut 
	public boolean finSaut = false;
	//booleen pour savoir si il est en saut/peut sauter 
	public boolean peutSauter = true;
	//booleen pour savoir si le personnage veut sauter alors qu'il glisse/accroche
	public boolean sautGlisse = false;
	public boolean sautAccroche = false;

	//public boolean glisse =false;

	public boolean doitEncocherFleche=false;
	public boolean flecheEncochee=false;

	//booleen pour savoir si on veut déplacer le personnage sur le côté quand il saut 
	public boolean deplaceSautDroit = false;
	public boolean deplaceSautGauche =false;

	//objet pour connaitre les valeurs comme la taille des sprites pour une action donnée
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

	/*public Heros(){
		xpos = 0;
		ypos = 0;
		vit = new Vitesse(0,0);
		slowDownFactor=3;//6
		fixedWhenScreenMoves=true;
		anim=0;
		nouvAnim= 0;
		deplacement=new Attente(TypeObject.heros,Attente.attente_gauche,0);
		nouvMouv = new Attente(TypeObject.heros,Attente.attente_gauche,0);
		tempsTouche=System.nanoTime();
	};*/
	public Heros( int xPo,int yPo, int _anim, Mouvement_perso dep,int current_frame){
		type=TypeObject.heros;
		xpos = xPo;
		ypos = yPo; 
		vit = new Vitesse(0,0);
		slowDownFactor=3;//6
		fixedWhenScreenMoves=true;
		deplacement=dep ;
		anim=_anim;
		nouvAnim= 0;
		nouvMouv = new Attente(TypeObject.heros,Attente.attente_gauche,current_frame);
		tempsTouche=System.nanoTime();
	}
	public String droite_gauche (int anim){
		return deplacement.droite_gauche(TypeObject.heros, anim);
	}

	public void touche (int degat) 
	{
		tempsTouche=System.nanoTime();
		afficheTouche=false;
		addLife(degat);
		invincible=true;
	}
	public void miseAjourTouche()
	{
		if((System.nanoTime()-tempsTouche)*Math.pow(10, -6)<=InterfaceConstantes.INV_TOUCHE)//heros invincible
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
	public void miseAJourSpe(AbstractModelPartie partie)
	{
		if((System.nanoTime()-tempsSpe)*Math.pow(10, -6)>InterfaceConstantes.TEMPS_VAR_SPE)
		{
			tempsSpe=System.nanoTime();
			if(partie.slowDown)
			{
				addSpe(partie,-1);
			}
			else
			{
				addSpe(partie,1);
			}
		}
	}
	public int getLife()
	{
		return(life);
	}
	public void addLife(int add)
	{
		life += add;
		if(life>InterfaceConstantes.MAXLIFE){life=InterfaceConstantes.MAXLIFE;}
		if(life<InterfaceConstantes.MINLIFE){life=InterfaceConstantes.MINLIFE;}

	}
	public int getSpe()
	{
		return(spe);
	}

	public void addSpe(AbstractModelPartie partie,int add)
	{
		spe += add;
		if(spe>InterfaceConstantes.MAXSPE){spe=InterfaceConstantes.MAXSPE;}
		if(spe<InterfaceConstantes.MINSPE)
		{
			spe=InterfaceConstantes.MINSPE;
			partie.slowDown=false;
			partie.slowCount=0;
			Music music = new Music();

			try {
				if(partie.slowDown)
					music.slowDownMusic();
				else
					music.endSlowDownMusic();
			} catch (UnsupportedAudioFileException | IOException
					| LineUnavailableException e) {
				e.printStackTrace();
			}

		}

	}

	public Hitbox getHitbox(Point INIT_RECT) {
		try{
			return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos,ypos),true);}
		catch(IndexOutOfBoundsException e)
		{
			System.out.println(deplacement.getClass().getName()+" "+anim);
			return new Hitbox();
		}
	}
	public Hitbox getHitbox(Point INIT_RECT, Mouvement _dep, int _anim) {
		try{
		Mouvement_perso temp = (Mouvement_perso) _dep.Copy(TypeObject.heros); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox.get(_anim), new Point(xpos,ypos),true);
		}
		catch(IndexOutOfBoundsException e)
		{
			System.out.println(_dep.getClass().getName()+" "+_anim);
			return new Hitbox();
		}
	}

	/**
	 * 
	 * @param right True: get the right slide hitbox False: the left one
	 * @return
	 */
	public Hitbox getGliss_Accroch_Hitbox(Point INIT_RECT, boolean gliss, boolean right)//used to get slide hitbox
	{
		//Hand coded, only two possible deplacement: Course or Saut 
		int up = vit.y<0? 0 : (deplacement.IsDeplacement(Mouvement_perso.saut)?4 : -3); //if gliss: up of hand
		int down = vit.y<0? 27 : (deplacement.IsDeplacement(Mouvement_perso.saut)?31 : 24);//if gliss: down of hand
		if(!gliss)
		{
			//small square below hand: if it is in collision while the hand is not: heros should change to Accroche
			up=down+1;
			down = down + 15;
		}
		//assume the Heros hitbox is a square/rectangle not rotated 
		Hitbox herosHit = this.getHitbox(INIT_RECT);

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

	public Hitbox getWorldPosition(AbstractModelPartie partie)
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
	}
	public boolean isGrounded(AbstractModelPartie partie)
	{
		Hitbox hit = getHitbox(partie.INIT_RECT);
		assert hit.polygon.npoints==4;
		//get world hitboxes with Collision
		Point p = new Point(partie.xScreendisp,partie.yScreendisp);
		Hitbox objectHitboxL= fixedWhenScreenMoves? Hitbox.minusPoint(hit,p,false): hit;
		//lowers all points by 1 at most 
		for(int i=0; i<objectHitboxL.polygon.npoints; ++i)
			objectHitboxL.polygon.ypoints[i]+=1;
		//get all hitboxes: it can be slower 
		List<Bloc> mondeHitboxes=Collision.getMondeBlocs(partie.monde, objectHitboxL, partie.INIT_RECT, partie.TAILLE_BLOC);
		//if there is a collision between mondeHitboxes and objectHitbox, it means that lower the hitbox by 1 leads to a 
		//collision: the object is likely to be on the ground (otherwise, it is in a bloc).
		for(Bloc b : mondeHitboxes){
			if(GJK_EPA.intersectsB(objectHitboxL.polygon, b.getHitbox(partie.INIT_RECT).polygon, new Vector2d(0,1))==GJK_EPA.TOUCH)
			{
				return true;
			}}
		return false;
	}

	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,
			Deplace deplace) {
		//project speed to ground 
		double coef= vit.vect2d().dot(normal)/normal.lengthSquared();
		vit = new Vitesse((int)(vit.x-coef*normal.x),(int)(vit.y-coef*normal.y));

		boolean collision_gauche = (vit.x<=0) && (normal.x>0);
		boolean collision_droite = (vit.x>=0) && (normal.x<0);
		boolean collision_haut = (vit.y<=0) && (normal.y>0);
		boolean collision_bas = (vit.y>=0) && (normal.y<0);
		//System.out.println("collision "+ (collision_gauche?"gauche ": "" )+(collision_droite?"droite ": "")+(collision_bas?"bas ": "")+(collision_haut?"haut ": "") + 
		//		vit.x +" "+vit.y);
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;

		final boolean mem_finSaut = finSaut;
		final boolean mem_peutSauter=peutSauter;
		final boolean mem_useGravity=useGravity;

		resetHandleCollision = new ResetHandleCollision(){
			@Override
			public void reset(Deplace deplace,AbstractModelPartie partie)
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
		public void reset(Deplace deplace,AbstractModelPartie partie)
		{};
	}

	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Deplace deplace) {}

	@Override
	public void memorizeCurrentValue() {

		final Point memPos= new Point(xpos,ypos); 
		final Mouvement_perso memDep = (Mouvement_perso) deplacement.Copy(TypeObject.heros);
		final int memAnim = anim;
		final Vitesse memVit = vit.Copy();
		currentValue=new CurrentValue(){		
			@Override
			public void res()
			{xpos=memPos.x;ypos=memPos.y;deplacement=memDep;anim=memAnim;vit=memVit;}};
	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		boolean doitDeplace=false;
		try {
			animationChanged=true;
			anim=changeMouv(nouvMouv, nouvAnim, partie,deplace);
			partie.changeMouv= false;
			doitDeplace=true;

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean[] res = {doitDeplace,animationChanged};
		return res;
	}


	/**
	 * Donne l'animation suivante, en fonction du mouvement en cours et de son animation 
	 * 
	 * @param animHeros animation actuelle du personnage  
	 * @param heros le personnage 
	 * @param nouvMouv le nouveau mouvement donné par partieRapideActionListener
	 * @param nouvAnim la nouvelle animation donnée par partieRapideActionListener
	 * @param blocDessous savoir si le bloc en dessous du sprite est bloquant
	 * @param blocDroitGlisse savoir si le bloc a droite du sprite est bloquant
	 * @param blocGaucheGlisse savoir si le bloc a gauche du sprite est bloquant
	 * @param Monde le niveau en cours
	 * @return la nouvelle animation 
	 * 
	 */	
	public int changeMouv(Mouvement nouvMouv, int nouvAnim, AbstractModelPartie partie,Deplace deplace) throws InterruptedException
	{
		System.out.println(deplacement.getClass().getName() +anim+" "+nouvMouv.getClass().getName()+" "+partie.changeMouv);
		boolean blocDroitGlisse=false;
		boolean blocGaucheGlisse=false;

		boolean blocDroitAccroche=false;//true if accroche special hitbox is colliding with world 
		boolean blocGaucheAccroche=false;

		int animHeros=anim;
		//translate all object hitboxes, see collision to get full formula
		Point deplaceEcran =new Point(partie.xScreendisp,partie.yScreendisp);
		Hitbox herosHitbox= Hitbox.minusPoint(getHitbox(partie.INIT_RECT),deplaceEcran,false);

		List<Bloc> mondeBlocs = Collision.getMondeBlocs(partie.monde,herosHitbox, partie.INIT_RECT,partie.TAILLE_BLOC);

		for(Bloc mondeBloc : mondeBlocs)
		{
			Hitbox mondeBox = mondeBloc.getHitbox(partie.INIT_RECT);
			Polygon p_glissade_d= Hitbox.minusPoint(getGliss_Accroch_Hitbox(partie.INIT_RECT,true,true),deplaceEcran,false).polygon;
			Polygon p_glissade_g= Hitbox.minusPoint(getGliss_Accroch_Hitbox(partie.INIT_RECT,true,false),deplaceEcran,false).polygon;
			Polygon p_accroche_d= Hitbox.minusPoint(getGliss_Accroch_Hitbox(partie.INIT_RECT,false,true),deplaceEcran,false).polygon;
			Polygon p_accroche_g= Hitbox.minusPoint(getGliss_Accroch_Hitbox(partie.INIT_RECT,false,false),deplaceEcran,false).polygon;

			Polygon p_monde= mondeBox.polygon;
			int a = GJK_EPA.intersectsB(p_glissade_d,p_monde, vit.vect2d());
			int b = GJK_EPA.intersectsB(p_glissade_g,p_monde, vit.vect2d());
			int c = GJK_EPA.intersectsB(p_accroche_d,p_monde, vit.vect2d());
			int d = GJK_EPA.intersectsB(p_accroche_g,p_monde, vit.vect2d());

			blocDroitGlisse=blocDroitGlisse||
					((a==GJK_EPA.TOUCH) || (a==GJK_EPA.INTER ));

			blocGaucheGlisse=blocGaucheGlisse||
					((b==GJK_EPA.TOUCH) || (b==GJK_EPA.INTER));

			blocDroitAccroche= blocDroitAccroche ||
					((c==GJK_EPA.TOUCH) || (c==GJK_EPA.INTER ));

			blocGaucheAccroche= blocGaucheAccroche ||
					((d==GJK_EPA.TOUCH) || (d==GJK_EPA.INTER ));

			if( (blocDroitGlisse || blocGaucheGlisse) && (blocDroitAccroche && blocGaucheAccroche))
			{
				break;
			}
		}
		// le heros est en chute libre
		boolean falling = !isGrounded(partie);
		if(falling)
			useGravity=falling && !this.deplacement.IsDeplacement(Mouvement_perso.accroche);
		//le heros atteri alors qu'il était en chute libre,
		boolean landing = (finSaut||!falling) && deplacement.IsDeplacement(Mouvement_perso.saut) && (animHeros ==1 || animHeros ==4);
		//le heros touche le sol en glissant
		boolean landSliding = finSaut && deplacement.IsDeplacement(Mouvement_perso.glissade);
		//le heros chute ou cours vers un mur: il commence à glisser sur le mur 
		boolean[] beginSliding= computeBeginSliding(blocDroitGlisse,blocGaucheGlisse,falling); 
		boolean beginSliding_r= beginSliding[0];
		boolean beginSliding_l= beginSliding[1];

		boolean[] beginAccroche= computeAccroche(blocDroitGlisse,blocGaucheGlisse,blocDroitAccroche,blocGaucheAccroche,falling);
		boolean beginAccroche_r = beginAccroche[1];
		boolean beginAccroche_l = beginAccroche[0];
		//le heros décroche du mur
		boolean endSliding = deplacement.IsDeplacement(Mouvement_perso.glissade) && 
				((!blocDroitGlisse && droite_gauche(animHeros)==("Gauche")) ||
						(!blocGaucheGlisse && droite_gauche(animHeros)==("Droite")));

		int anim=animHeros;
		if(doitEncocherFleche || flecheEncochee)//cas différent puisqu'on ne veut pas que l'avatar ait l'animation de chute en l'air
		{
			double[] anim_rotation = deplace.getAnimRotationTir(partie,false);
			int animSuivante = (int)anim_rotation[0];
			rotation_tir=anim_rotation[1];
			//on decalle
			Mouvement mouvSuivant = new Tir(TypeObject.heros,Tir.tir,partie.getFrame());
			alignHitbox(animHeros,mouvSuivant,animSuivante ,partie,deplace,blocGaucheGlisse);
			deplacement= mouvSuivant;
			deplacement.setSpeed(TypeObject.heros, this, anim);
			return(animSuivante);

		}

		//SPECIAL CASE that comes from computation on the current Mouvement 
		if( (beginSliding_r||beginSliding_l) && !deplacement.IsDeplacement(Mouvement_perso.glissade))
		{
			Mouvement nextMouv = new Glissade(TypeObject.heros,beginSliding_l?Glissade.glissade_gauche:Glissade.glissade_droite,partie.getFrame());
			int nextAnim = (beginSliding_l ? 0 :1);

			alignHitbox(animHeros,nextMouv,nextAnim,partie ,deplace,blocGaucheGlisse);
			anim = nextAnim;
			deplacement= nextMouv;
			deplacement.setSpeed(TypeObject.heros, this, anim);

			return(anim);
		}

		if((beginAccroche_r || beginAccroche_l) && !deplacement.IsDeplacement(Mouvement_perso.accroche))
		{
			Mouvement nextMouv= new Accroche(TypeObject.heros, beginAccroche_l? Accroche.accroche_gauche:Accroche.accroche_droite,partie.getFrame());
			int nextAnim = (beginAccroche_l?0:2);

			//Manually align hitbox 
			int xdir = beginAccroche_l ? -1 :1;
			int ydir = -1; //get upper part of hitbox

			double ycurrentup = Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,deplacement, animHeros).polygon).y;
			//get value to align hitbox 
			double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,deplacement, animHeros).polygon).x -
					Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,nextMouv, nextAnim).polygon).x;

			double dy= ycurrentup -
					Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,nextMouv, nextAnim).polygon).y;

			xpos+= dx;
			ypos+= dy;
			
			//align to lower bloc
			double yScreenDispMod= partie.getXYScreendispMod(false);
			double align_to_ground =(int) (ycurrentup- yScreenDispMod)/ InterfaceConstantes.TAILLE_BLOC * InterfaceConstantes.TAILLE_BLOC + InterfaceConstantes.TAILLE_BLOC
					-(ycurrentup -yScreenDispMod) ;

			ypos += align_to_ground;
			useGravity=false;

			deplacement=nextMouv;
			anim=nextAnim;
			deplacement.setSpeed(TypeObject.heros, this, nextAnim);
			return anim;
		}

		//attention, falling est le seul bloc de code à ne pas avoir de return 
		if(falling)
		{
			peutSauter=false;
			if(!(deplacement.IsDeplacement(Mouvement_perso.glissade)||deplacement.IsDeplacement(Mouvement_perso.course)||deplacement.IsDeplacement(Mouvement_perso.accroche)))
			{
				int up = vit.y>=0 ? 1 : 0;
				int type_mouv = droite_gauche(animHeros)=="Gauche" ? (up==0?Saut.jump_gauche:Saut.fall_gauche) :
					(up==0?Saut.jump_droite:Saut.fall_droite);
				Mouvement mouvSuivant = new Saut(TypeObject.heros,type_mouv,partie.getFrame());
				int animSuivant = (droite_gauche(animHeros)=="Gauche" ? 0+up :3+up);
				alignHitbox(animHeros,mouvSuivant,animSuivant,partie,deplace,blocGaucheGlisse );		
				//TODO: why?
				animHeros = animSuivant;
				anim=animSuivant;
				deplacement=mouvSuivant;
				deplacement.setSpeed(TypeObject.heros, this, anim);

				//landing = partie.finSaut;
				beginSliding= computeBeginSliding(blocDroitGlisse,blocGaucheGlisse,(falling&&!landing));

			}
		}
		
		if(deplacement.IsDeplacement(Mouvement_perso.accroche))
		{
			if( (anim == 1 || anim == 3) && deplacement.animEndedOnce())
			{
				int next_anim = (droite_gauche(animHeros)=="Gauche" ? 0 :2);
				Mouvement nextMouv = new Attente(TypeObject.heros,droite_gauche(animHeros)=="Gauche" ?Attente.attente_gauche: Attente.attente_droite,partie.getFrame());

				alignHitbox(animHeros,nextMouv,next_anim,partie,deplace,blocGaucheGlisse);
				anim= next_anim;
				deplacement=nextMouv;
				deplacement.setSpeed(TypeObject.heros, this, anim);
				
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
			Mouvement mouvSuiv = new Saut(TypeObject.heros,droite_gauche(animHeros)=="Gauche" ?Saut.land_gauche:Saut.land_droite,partie.getFrame());
			int animSuiv = (droite_gauche(animHeros)=="Gauche"? 2 : 5 );
			//on ajuste la position du personnage pour qu'il soit centré 
			alignHitbox(animHeros,mouvSuiv,animSuiv,partie,deplace,blocGaucheGlisse );
			finSaut=false;//set landing to false
			deplacement=mouvSuiv;
			anim= animSuiv;
			deplacement.setSpeed(TypeObject.heros, this, anim);

			return(anim);


		}
		else if(deplacement.IsDeplacement(Mouvement_perso.saut) && (animHeros ==2 || animHeros ==5)  && deplacement.animEndedOnce())//atterissage: se relève
		{
			int nextAnim = runBeforeJump? (droite_gauche(animHeros)=="Gauche" ? 0 : 4 ) : (droite_gauche(animHeros)=="Gauche" ? 0 : 2 );
			Mouvement_perso nextDep=  runBeforeJump? new Course(TypeObject.heros,droite_gauche(animHeros)=="Gauche" ?Course.course_gauche:Course.course_droite,partie.getFrame()) 
					: new Attente(TypeObject.heros,droite_gauche(animHeros)=="Gauche" ?Attente.attente_gauche:Attente.attente_droite,partie.getFrame());
			//on ajuste la position du personnage pour qu'il soit centré 
			alignHitbox(animHeros,nextDep,nextAnim,partie,deplace,blocGaucheGlisse);
			//on choisit la direction d'attente			
			vit.x=0;	
			finSaut=false;
			peutSauter=true;
			anim=nextAnim;
			deplacement=nextDep;

			return(anim);

		}
		else if(landSliding)
		{
			Mouvement nextMouv = new Attente(TypeObject.heros,droite_gauche(animHeros)=="Gauche" ?Attente.attente_gauche: Attente.attente_droite,partie.getFrame());
			int nextAnim = (droite_gauche(animHeros)=="Gauche" ? 0 : 2 );
			alignHitbox(animHeros,nextMouv,nextAnim,partie ,deplace,blocGaucheGlisse);
			finSaut=false;
			//on ajuste la position du personnage pour qu'il soit centré 
			anim= nextAnim;
			deplacement=nextMouv;
			vit.y=0;
			return(anim);

		}

		else if(endSliding)
		{
			int nextAnim= (droite_gauche(animHeros)=="Gauche" ? 1 :4);
			Mouvement nextMouv = new Saut(TypeObject.heros,droite_gauche(animHeros)=="Gauche" ?Saut.fall_gauche: Saut.fall_droite,partie.getFrame());
			alignHitbox(animHeros,nextMouv,nextAnim,partie,deplace,blocGaucheGlisse);
			anim = nextAnim;
			deplacement= nextMouv;
			deplacement.setSpeed(TypeObject.heros, this, anim);
			vit.x=0;
			nouvAnim=anim;	
			return(anim);

		}
		//Call function to check if move is allowed
		boolean allowed = moveAllowed(nouvMouv,nouvAnim);
		//CHANGEMENT DE MOUVEMENT
		if(partie.changeMouv && allowed)
		{
			if(nouvMouv.IsDeplacement(Mouvement_perso.accroche) && ( (nouvAnim == 1) || (nouvAnim == 3)))
			{
				//manually align hitbox 
				int xdir = (nouvAnim == 1) ? -1 :1;
				int ydir_up = -1; //get upper part of hitbox
				int ydir_down = 1; //get lower part of hitbox

				//top of hitbox should now be bottom of next hitbox 
				//Manually align hitbox 
	

				double ycurrentup = Hitbox.supportPoint(new Vector2d(0,ydir_up), getHitbox(partie.INIT_RECT,deplacement, animHeros).polygon).y;
				//get value to align hitbox 
				double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,deplacement, animHeros).polygon).x -
					Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,nouvMouv, nouvAnim).polygon).x;

				
				double dy= ycurrentup -
						Hitbox.supportPoint(new Vector2d(0,ydir_down), getHitbox(partie.INIT_RECT,nouvMouv, nouvAnim).polygon).y;

				int xdecall = 12 *  ((nouvAnim==1)? -1:1);
				xpos+= dx + xdecall;
				ypos+= dy-1;
				
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

			anim=nouvAnim;
			deplacement=nouvMouv;
			deplacement.setSpeed(TypeObject.heros, this, anim);
			
		}
		else
		//if(!partie.changeMouv ) // MEME MOUVEMENT QUE PRECEDEMMENT , otherwise problem with landing 
		{

			animationChanged=false;
			int nextAnim = deplacement.updateAnimation(TypeObject.heros, animHeros, partie.getFrame());
			alignHitbox(animHeros,deplacement,nextAnim,partie,deplace,blocGaucheGlisse);
			anim= nextAnim;
			deplacement.setSpeed(TypeObject.heros, this, anim);

		}
		partie.changeMouv=false;

		return(anim);

	}

	private boolean[] computeSlide_Accroche(boolean slide, boolean blocDroitGlisse, boolean blocGaucheGlisse, boolean blocDroitAccroche, boolean blocGaucheAccroche, boolean falling)
	{
		boolean blocGauche = slide? blocGaucheGlisse : (!blocGaucheGlisse && blocGaucheAccroche);
		boolean blocDroit = slide? blocDroitGlisse:  (!blocDroitGlisse && blocDroitAccroche);
		boolean res_d = (deplacement.IsDeplacement(Mouvement_perso.saut)||deplacement.IsDeplacement(Mouvement_perso.course)) 
				&& (blocGauche&&(last_colli_left||vit.x<0)) && falling; 
		boolean res_g = (deplacement.IsDeplacement(Mouvement_perso.saut)||deplacement.IsDeplacement(Mouvement_perso.course)) 
				&& (blocDroit && (last_colli_right||vit.x>0)) && falling; 
		boolean[] res ={res_d,res_g};
		//caution for accroche, res_d is actually res_l, and res_l res_d
		return res;
	}
	public boolean[] computeBeginSliding(boolean blocDroitGlisse, boolean blocGaucheGlisse,boolean falling)
	{
		return computeSlide_Accroche(true,blocDroitGlisse,blocGaucheGlisse,false,false,falling);
	}

	public boolean[] computeAccroche(boolean blocDroitGlisse, boolean blocGaucheGlisse, boolean blocDroitAccroche, boolean blocGaucheAccroche, boolean falling)
	{
		return computeSlide_Accroche(false,blocDroitGlisse,blocGaucheGlisse,blocDroitAccroche,blocGaucheAccroche,falling);
	}

	private boolean moveAllowed(Mouvement nextMove, int nextAnim)
	{
		Mouvement currentM = deplacement;

		boolean allowed=true;

		//Unexpected behavious: attente/marche while being in the air(ie current move being saut/glissade )
		//Unexpected behavious: going right/left in the air while landing

		boolean inAirAllowed = !( (currentM.IsDeplacement(Mouvement_perso.saut) || currentM.IsDeplacement(Mouvement_perso.glissade)) &&
				(nextMove.IsDeplacement(Mouvement_perso.attente) || nextMove.IsDeplacement(Mouvement_perso.marche) ));

		boolean airLandingAllowed= ! (currentM.IsDeplacement(Mouvement_perso.saut) && nextMove.IsDeplacement(Mouvement_perso.saut) && 
				((anim==2) || (anim==5) )) ; //movement in air allowed only if not landing

		allowed = allowed && airLandingAllowed && inAirAllowed;
		return allowed; 
	}

	//Move the character to center it before the animation change.
	public void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace,boolean blocGaucheGlisse )
	{
		boolean isGlissade = deplacement.IsDeplacement(Mouvement_perso.glissade);
		boolean going_left = vit.x<0;

		boolean facing_left_still= vit.x==0 &&(droite_gauche(animActu)=="Gauche"|| last_colli_left)&& !isGlissade;
		boolean sliding_left_wall = (droite_gauche(animActu)=="Droite") && isGlissade;
		boolean start_falling_face_right = (!deplacement.IsDeplacement(Mouvement_perso.saut) && depSuiv.IsDeplacement(Mouvement_perso.saut)) 
				&& (droite_gauche(animActu)=="Droite");
		boolean start_falling_face_left = (!deplacement.IsDeplacement(Mouvement_perso.saut) && depSuiv.IsDeplacement(Mouvement_perso.saut)) 
				&& (droite_gauche(animActu)=="Gauche");
		boolean left = ! start_falling_face_left && ( going_left|| facing_left_still ||sliding_left_wall || blocGaucheGlisse || start_falling_face_right) ; 
		boolean down = vit.y>=0; 

		last_align_left=left;
		last_align_down=down;
		super.alignHitbox(animActu,depSuiv, animSuiv, partie,deplace,left, down,TypeObject.heros,!depSuiv.IsDeplacement(Mouvement_perso.glissade));
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
	public void applyFriction(int minSpeed)
	{
		if(deplacement.IsDeplacement(Mouvement_perso.tir))
		{
			boolean neg = vit.x<0;
			double newVitX= vit.x - (vit.x* InterfaceConstantes.FRICTION);
			if( (!neg && newVitX<minSpeed) || (neg && newVitX>-1*minSpeed) )
				vit.x=minSpeed;
			else
				vit.x=newVitX;
		}
	}
	@Override
	public void handleStuck(AbstractModelPartie partie, Deplace deplace) {
		System.out.println("heros stuck");
		if(currentValue!=null)
			currentValue.res();

		if(resetHandleCollision != null){
			resetHandleCollision.reset(deplace, partie);
		}
		doitEncocherFleche=false;
	}

	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie,
			Deplace deplace) {
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
	public void resetVarDeplace() {
		resetHandleCollision=null;
	}

	public void shootArrow(AbstractModelPartie partie)
	{
		new Fleche(partie.tabFleche,partie.getFrame());
	}
	@Override
	public void destroy() {
		//Do nothing
	}



}

