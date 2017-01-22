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
import deplacement.Attente;
import deplacement.Course;
import deplacement.Deplace;
import deplacement.Glissade;
import deplacement.Marche;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import deplacement.Saut;
import deplacement.Tir;
import music.Music;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Bloc;
import types.Hitbox;
import types.Vitesse;

public class Heros extends Collidable{

	public int nouvAnim= 0;
	public Mouvement nouvMouv = new Attente();
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

	//Variables pour mémoriser la direction de la dernière collision
	public boolean last_colli_left=false;
	public boolean last_colli_right=false;

	public boolean runBeforeJump=false;
	//booleen pour savoir si le heros vient de sauter
	public boolean debutSaut = false;
	//booleen pour savoir si on arrive à la fin du saut 
	public boolean finSaut = false;
	//booleen pour savoir si il est en saut/peut sauter 
	public boolean peutSauter = true;
	//booleen pour savoir si le personnage veut sauter alors qu'il glisse
	public boolean sautGlisse = false;
	public boolean glisse =false;

	//booleen pour savoir si on veut déplacer le personnage sur le côté quand il saut 
	public boolean deplaceSautDroit = false;
	public boolean deplaceSautGauche =false;

	//objet pour connaitre les valeurs comme la taille des sprites pour une action donnée
	protected Attente attente= new Attente();
	protected Marche marche = new Marche();
	protected Course course = new Course();
	protected Saut saut = new Saut();
	protected Glissade glissade = new Glissade();
	protected Tir tir = new Tir();

	protected ResetHandleCollision resetHandleCollision;

	public Heros(){
		xpos = 0;
		ypos = 0;
		vit = new Vitesse(0,0);
		slowDownFactor=3;//6
		fixedWhenScreenMoves=true;
		anim=0;
		nouvAnim= 0;
		deplacement=new Attente();
		nouvMouv = new Attente();
		deplacement = new Attente();
		tempsTouche=System.nanoTime();
	};
	public Heros( int xPo,int yPo, int _anim, Mouvement_perso dep){
		xpos = xPo;
		ypos = yPo; 
		vit = new Vitesse(0,0);
		slowDownFactor=3;//6
		fixedWhenScreenMoves=true;
		deplacement=dep ;
		anim=_anim;
		nouvAnim= 0;
		nouvMouv = new Attente();
		tempsTouche=System.nanoTime();
	}
	public String droite_gauche (int anim){
		if(deplacement.IsDeplacement(Mouvement_perso.marche) || deplacement.IsDeplacement(Mouvement_perso.course))
		{
			if(anim <4)
			{
				return ("Gauche");
			}
			else return("Droite");
		}
		else if(deplacement.IsDeplacement(Mouvement_perso.attente))
		{
			if(anim <1)
			{
				return ("Gauche");
			}
			else return("Droite");
		}
		else if(deplacement.IsDeplacement(Mouvement_perso.saut))
		{
			if(anim <3)
			{
				return ("Gauche");
			}
			else return("Droite");
		}
		else if(deplacement.IsDeplacement(Mouvement_perso.glissade))
		{
			if(anim==0)
			{
				return ("Gauche");
			} 
			else return("Droite");
		}
		else if(deplacement.IsDeplacement(Mouvement_perso.tir))
		{
			if(anim>=2 && anim<=5)
			{
				return ("Gauche");
			} 
			else return("Droite");
		}
		else{
			try {
				throw new Exception("Heros/droite_gauche: ERREUR deplacement inconnu");
			} catch (Exception e) {
				e.printStackTrace();
			}}
		return("Gauche");
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
		return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos,ypos),true);
	}
	public Hitbox getHitbox(Point INIT_RECT, Mouvement _dep, int _anim) {
		Mouvement_perso temp = (Mouvement_perso) _dep.Copy(Mouvement_perso.heros); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox.get(_anim), new Point(xpos,ypos),true);
	}

	/**
	 * 
	 * @param right True: get the right slide hitbox False: the left one
	 * @return
	 */
	public Hitbox getSlideHitbox(Point INIT_RECT, boolean right)//used to get slide hitbox
	{
		//assume the Heros hitbox is a square/rectangle not rotated 
		Hitbox herosHit = this.getHitbox(INIT_RECT);
		List<Vector2d> upLeftP = Hitbox.supportsPoint(new Vector2d(-1,-1), herosHit.polygon);
		List<Vector2d> downLeftP = Hitbox.supportsPoint(new Vector2d(-1,1), herosHit.polygon);
		List<Vector2d> downRightP = Hitbox.supportsPoint(new Vector2d(1,1), herosHit.polygon);
		List<Vector2d> upRightP = Hitbox.supportsPoint(new Vector2d(1,-1), herosHit.polygon);

		Polygon p = new Polygon();

		int decall_x_hit = 1;
		for(int i=0; i<upLeftP.size();++i)
		{
			Vector2d v= upLeftP.get(i);
			p.addPoint((int)(v.x+ (right? decall_x_hit : -decall_x_hit)), (int)(v.y+7));
		}
		for(int i=0; i<downLeftP.size();++i)
		{
			Vector2d v= downLeftP.get(i);
			p.addPoint((int)(v.x+ (right? decall_x_hit : -decall_x_hit)), (int)(upLeftP.get(0).y+43));
		}
		for(int i=0; i<downRightP.size();++i)
		{
			Vector2d v= downRightP.get(i);
			p.addPoint((int)(v.x+ (right? decall_x_hit : -decall_x_hit)), (int)(upLeftP.get(0).y+43));
		}
		for(int i=0; i<upRightP.size();++i)
		{
			Vector2d v= upRightP.get(i);
			p.addPoint((int)(v.x+ (right? decall_x_hit : -decall_x_hit)), (int)(v.y+7));
		}
		return new Hitbox(p);	
	}

	public Hitbox getWorldPosition(AbstractModelPartie partie)
	{
		int xCompScreenMove=0; 
		int yCompScreenMove=0;
		Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
				partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
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
		Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
				partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
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
		Point p = new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
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
		//boolean collision_haut = (vit.y<=0) && (normal.y>0);
		boolean collision_bas = (vit.y>=0) && (normal.y<0);
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;

		final boolean mem_glisse=glisse;
		final boolean mem_finSaut = finSaut;
		final boolean mem_peutSauter=peutSauter;
		final boolean mem_useGravity=useGravity;

		resetHandleCollision = new ResetHandleCollision(){
			@Override
			public void reset(Deplace deplace,AbstractModelPartie partie)
			{
				glisse=mem_glisse;
				finSaut=mem_finSaut;
				peutSauter=mem_peutSauter;
				useGravity= mem_useGravity;
			}};

			if(collision_gauche || collision_droite)
				glisse=true;
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
		final Mouvement_perso memDep = (Mouvement_perso) deplacement.Copy(Mouvement_perso.heros);
		final int memAnim = anim;
		final Vitesse memVit = vit.Copy();
		currentValue=new CurrentValue(){		
			@Override
			public void res()
			{xpos=memPos.x;ypos=memPos.y;deplacement=memDep;anim=memAnim;vit=memVit;}};
	}
	@Override
	public boolean deplace(AbstractModelPartie partie, Deplace deplace) {
		try {
			anim=changeMouv(nouvMouv, nouvAnim, partie,deplace);
			partie.changeMouv= false;
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
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
		int ydeplaceEcran = partie.ydeplaceEcran;
		boolean blocDroitGlisse=false;
		boolean blocGaucheGlisse=false;
		int animHeros=anim;
		//translate all object hitboxes, see collision to get full formula
		Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
				partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
		Hitbox herosHitbox= Hitbox.minusPoint(getHitbox(partie.INIT_RECT),deplaceEcran,false);

		List<Bloc> mondeBlocs = Collision.getMondeBlocs(partie.monde,herosHitbox, partie.INIT_RECT,partie.TAILLE_BLOC);

		for(Bloc mondeBloc : mondeBlocs)
		{
			Hitbox mondeBox = mondeBloc.getHitbox(partie.INIT_RECT);
			Polygon p_glissade_d= Hitbox.minusPoint(getSlideHitbox(partie.INIT_RECT,true),deplaceEcran,false).polygon;
			Polygon p_glissade_g= Hitbox.minusPoint(getSlideHitbox(partie.INIT_RECT,false),deplaceEcran,false).polygon;
			Polygon p_monde= mondeBox.polygon;
			int a = GJK_EPA.intersectsB(p_glissade_d,p_monde, vit.vect2d());
			int b = GJK_EPA.intersectsB(p_glissade_g,p_monde, vit.vect2d());
			blocDroitGlisse=blocDroitGlisse||
					(a==GJK_EPA.TOUCH ||a==GJK_EPA.INTER );

			blocGaucheGlisse=blocGaucheGlisse||
					(b==GJK_EPA.TOUCH || b==GJK_EPA.INTER);

			if(blocDroitGlisse || blocGaucheGlisse)
			{
				break;
			}
		}

		//le heros tir une fleche
		boolean isFiring = partie.flecheEncochee;
		// le heros est en chute libre
		boolean falling = !isGrounded(partie);
		if(falling)
			useGravity=falling;
		//le heros atteri alors qu'il était en chute libre
		boolean landing = (finSaut||!falling) && deplacement.IsDeplacement(Mouvement_perso.saut) && (animHeros ==1 || animHeros ==4);
		//le heros touche le sol en glissant
		boolean landSliding = finSaut && deplacement.IsDeplacement(Mouvement_perso.glissade);
		//le heros chute ou cours vers un mur: il commence à glisser sur le mur 
		boolean beginSliding= computeBeginSliding(blocDroitGlisse,blocGaucheGlisse,falling); 

		//le heros décroche du mur
		boolean endSliding = deplacement.IsDeplacement(Mouvement_perso.glissade) && 
				((!blocDroitGlisse && droite_gauche(animHeros)==("Gauche")) ||
						(!blocGaucheGlisse && droite_gauche(animHeros)==("Droite")));

		int anim=animHeros;
		if(isFiring)//cas différent puisqu'on ne veut pas que l'avatar chute en l'air
		{
			int animSuivante = deplace.animFlecheEncochee(partie);
			//on decalle
			alignHitbox(animHeros,tir,animSuivante ,partie,deplace);
			deplacement= new Tir();
			deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);
			return(animSuivante);

		}
		else
		{

			//attention, falling est le seul bloc de code à ne pas avoir de return 
			if(falling)
			{
				peutSauter=false;
				if(!(deplacement.IsDeplacement(Mouvement_perso.glissade)||deplacement.IsDeplacement(Mouvement_perso.course)))
				{
					alignHitbox(animHeros,saut,(droite_gauche(animHeros)=="Gauche" ? 1 :4),partie,deplace );		
					animHeros = (droite_gauche(animHeros)=="Gauche" ? 1 :4);
					anim=animHeros;
					deplacement= new Saut();
					deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);

					//landing = partie.finSaut;
					beginSliding= computeBeginSliding(blocDroitGlisse,blocGaucheGlisse,(falling&&!landing));

				}
			}

			if(landing) //atterrissage: accroupi 
			{

				//on ajuste la position du personnage pour qu'il soit centré 
				alignHitbox(animHeros,deplacement,(droite_gauche(animHeros)=="Gauche" ? 2 : 5 ),partie,deplace );
				glisse=false;
				finSaut=false;//set landing to false
				anim= (droite_gauche(animHeros)=="Gauche"? 2 : 5 );
				deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);

				return(anim);


			}
			else if(deplacement.IsDeplacement(Mouvement_perso.saut) && (animHeros ==2 || animHeros ==5))//atterissage: se relève
			{

				int nextAnim = runBeforeJump? (droite_gauche(animHeros)=="Gauche" ? 0 : 4 ) : (droite_gauche(animHeros)=="Gauche" ? 0 : 1 );
				Mouvement_perso nextDep=  runBeforeJump? new Course() : new Attente();
				//on ajuste la position du personnage pour qu'il soit centré 
				alignHitbox(animHeros,nextDep,nextAnim,partie,deplace);
				glisse=false;
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
				alignHitbox(animHeros,attente,(droite_gauche(animHeros)=="Gauche" ? 0 : 1 ),partie ,deplace);
				glisse=false;
				finSaut=false;
				//on ajuste la position du personnage pour qu'il soit centré 
				anim= (droite_gauche(animHeros)=="Gauche" ? 0 : 1 );
				deplacement=attente;
				vit.y=0;
				return(anim);

			}
			else if(beginSliding)
			{
				alignHitbox(animHeros,glissade,(blocDroitGlisse ? 0 :1),partie ,deplace);
				glisse=false;
				anim = (blocDroitGlisse ? 0 :1);
				deplacement= new Glissade();
				deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);

				return(anim);
			}
			else if(endSliding)
			{
				if(vit.y<=0) //TP au dessus du bloc
				{
					//TODO: Remplacer TP par anim accrochage
					int x_decall = 3;//4
					xpos+=(droite_gauche(animHeros)=="Gauche"? x_decall : (-x_decall) );

					Hitbox attenteHitbox = getHitbox(partie.INIT_RECT, new Attente(Mouvement_perso.heros), (droite_gauche(animHeros)=="Gauche" ? 0 :1));
					int yTailleHeros= (int) (Hitbox.supportPoint(new Vector2d(0,1), attenteHitbox.polygon).y-
							Hitbox.supportPoint(new Vector2d(0,-1), attenteHitbox.polygon).y);
					ypos= (int)((ypos+yTailleHeros-ydeplaceEcran))/100*100-yTailleHeros+ydeplaceEcran-10;//-10 due to slide hitboxbeing reduced

					alignHitbox(animHeros,attente,(droite_gauche(animHeros)=="Gauche" ? 0 :1),partie,deplace );
					anim= (droite_gauche(animHeros)=="Gauche" ? 0 :1);
					deplacement=new Attente();
					deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);
					//on reinitialise les variables de saut 
					debutSaut = false;
					finSaut = false;
					useGravity=false;
					peutSauter = true;
					glisse =false;
					sautGlisse = false;
					return(anim);
				}
				else // le heros tombe 
				{
					alignHitbox(animHeros,saut,(droite_gauche(animHeros)=="Gauche" ? 1 :4),partie,deplace);
					anim = (droite_gauche(animHeros)=="Gauche" ? 1 :4);
					deplacement= new Saut();
					deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);
					vit.x=0;
					nouvAnim=anim;	
					return(anim);
				}

			}

			//Call function to check if move is allowed
			boolean allowed = moveAllowed(nouvMouv,nouvAnim);
			//CHANGEMENT DE MOUVEMENT
			if(partie.changeMouv && allowed)
			{
				alignHitbox(animHeros,nouvMouv,nouvAnim,partie,deplace);

				if(nouvMouv.IsDeplacement(Mouvement_perso.saut) && debutSaut)
				{
					if(deplacement.IsDeplacement(Mouvement_perso.course))
						runBeforeJump=true;
					else
						runBeforeJump=false;
				}

				anim=nouvAnim;
				deplacement=nouvMouv;
				deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);

			}

			if(!partie.changeMouv) // MEME MOUVEMENT QUE PRECEDEMMENT 
			{

				if( deplacement.IsDeplacement(Mouvement_perso.marche))
				{	
					alignHitbox(animHeros,marche,(droite_gauche(animHeros)=="Gauche" ? (animHeros+1)%4 :(animHeros+1)%4+4 ),partie,deplace);
					anim= (droite_gauche(animHeros)=="Gauche" ? (animHeros+1)%4 :(animHeros+1)%4+4 );
					deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);
				}
				else if(deplacement.IsDeplacement(Mouvement_perso.course))
				{			
					alignHitbox(animHeros,course,(droite_gauche(animHeros)=="Gauche" ? (animHeros+1)%4 :(animHeros+1)%4+4 ),partie,deplace );
					anim= (droite_gauche(animHeros)=="Gauche" ? (animHeros+1)%4 :(animHeros+1)%4+4 );
					deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);
				}
				else if(deplacement.IsDeplacement(Mouvement_perso.saut))
				{
					deplacement.setSpeed(Mouvement_perso.heros, this, anim,deplace);
				}
				else if( deplacement.IsDeplacement(Mouvement_perso.glissade))
				{
					//pas de changement d'animation
				}
				else if(deplacement.IsDeplacement(Mouvement_perso.attente))
				{
					// pas de changement d'animation
				}
				else if(deplacement.IsDeplacement(Mouvement_perso.tir))
				{
					// pas de changement d'animation
				}
				else 
				{
					throw new IllegalArgumentException("Deplace/changeAnim: meme mouvement inconnu: "+ deplacement.getClass().getName());
				}
			}
			return(anim);
		}
	}

	public boolean computeBeginSliding(boolean blocDroitGlisse, boolean blocGaucheGlisse,boolean falling)
	{
		boolean begin_sliding = (deplacement.IsDeplacement(Mouvement_perso.saut)||deplacement.IsDeplacement(Mouvement_perso.course)) 
				&& ((blocDroitGlisse && (last_colli_right||vit.x>0))
						||(blocGaucheGlisse&&(last_colli_left||vit.x<0))) && falling; 
		return begin_sliding;
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
	public void alignHitbox(int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace)
	{
		/*
			  normal -> normal : sens de la vitesse
		 ***-> glissade sens de la vitesse: -> [| 
			  glissade -> *** opposé au regard |[ -> (on évite de rentrer dans le mur où on était)
			  Par défaut si la vitesse est nulle: BAS DROITE
		 * */
		Mouvement depActu= deplacement;
		boolean isGlissade = deplacement.IsDeplacement(Mouvement_perso.glissade);
		boolean going_left = vit.x<0;
		boolean facing_left_still= vit.x==0 &&(droite_gauche(animActu)=="Gauche"|| last_colli_left)&& !isGlissade;
		boolean sliding_left_wall = (droite_gauche(animActu)=="Droite") && isGlissade;
		boolean left = ( going_left|| facing_left_still ||sliding_left_wall) ; 
		boolean down = vit.y>=0; 

		int xdir = left ? -1 :1;
		int ydir = down ? 1 :-1;
		double dx= Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).x -
				Hitbox.supportPoint(new Vector2d(xdir,0), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).x;

		double dy= Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).y -
				Hitbox.supportPoint(new Vector2d(0,ydir), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).y;

		xpos+= dx;
		ypos+= dy;

		int prev_anim = anim;
		Mouvement prev_mouv = deplacement.Copy(Mouvement_perso.heros);
		this.anim=animSuiv;
		this.deplacement=depSuiv;
		
		boolean valid = !deplace.colli.isWorldCollision(partie, deplace, this);
		
		this.anim=prev_anim;
		this.deplacement=prev_mouv;
		
		
		boolean attente = deplacement.IsDeplacement(Mouvement_perso.attente);
		boolean shooting =deplacement.IsDeplacement(Mouvement_perso.tir);
		boolean landing= deplacement.IsDeplacement(Mouvement_perso.saut) && (animActu==2 || animActu==5);
		
		if(!valid && (attente||shooting||  landing))
		{
			dx= -dx  + Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,depActu, animActu).polygon).x -
					Hitbox.supportPoint(new Vector2d(-xdir,0), getHitbox(partie.INIT_RECT,depSuiv, animSuiv).polygon).x;
			xpos+=dx;

		}
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
			int newVitX= vit.x - (int) (vit.x* InterfaceConstantes.FRICTION);
			if( (!neg && newVitX<minSpeed) || (neg && newVitX>-1*minSpeed) )
				vit.x=minSpeed;
			else
				vit.x=newVitX;
		}
	}
	@Override
	public void handleStuck(AbstractModelPartie partie, Deplace deplace) {
		if(currentValue!=null)
			currentValue.res();

		if(resetHandleCollision != null){
			resetHandleCollision.reset(deplace, partie);
		}
	}

	@Override
	public void resetVarDeplace() {
		resetHandleCollision=null;
	}
	/**
	 * Regle le nombre de tour de boucle a attendre avant de réappeler la fonction DeplaceHeros
	 * 
	 * @param anim, animation actuelle du personnage  
	 * @param heros, le personnage 
	 * 
	 * @return le nombre de tour de boucle
	 */	
	@Override
	public int setReaffiche() {
		int reaffiche=0;
		if(deplacement.IsDeplacement(Mouvement_perso.attente))
			reaffiche=50;//50

		else if(deplacement.IsDeplacement(Mouvement_perso.marche))
			reaffiche=100;//100

		else if(deplacement.IsDeplacement(Mouvement_perso.course))
			reaffiche=50;//50

		else if(deplacement.IsDeplacement(Mouvement_perso.glissade))
			reaffiche=20;//20

		else if(deplacement.IsDeplacement(Mouvement_perso.saut))
			reaffiche=20;//20

		else if(deplacement.IsDeplacement(Mouvement_perso.tir))
			reaffiche=20;//20
		else 
			throw new IllegalArgumentException("ERREUR setReaffiche, ACTION INCONNUE  "  +deplacement.getClass().getName());

		return(reaffiche);

	}

	@Override
	public void destroy() {
		//Do nothing
	}



}

