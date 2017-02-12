package monstre;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collision;
import collision.GJK_EPA;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Bloc;
import types.Hitbox;
import types.Vitesse;
import deplacement.Attente;
import deplacement.Deplace;
import deplacement.Marche;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import deplacement.Saut;
import music.MusicBruitage;

@SuppressWarnings("serial")
public class Spirel extends Monstre{
	//timer pour que le monstre agisse toutes les (maximum 300ms )

	private long tempsAncienMouv=0;
	private static long delaiMouv= 400;
	private long tempsAncienTir=0;
	private static long delaiTir= 2000;
	private int distanceAttaque= 400;
	private boolean cooldown=true;

	public boolean peutSauter = true;
	public boolean sautDroit= false;
	public boolean sautGauche= false;

	public boolean staticSpirel=false;

	/**
	 * constructeur
	 * 
	 * @param xPo, position originale en x
	 * @param yPo, position originale en y
	 * @param _staticSpirel, permet de rendre la spirel immobile (si =true)
	 */	
	public Spirel( int xPo,int yPo,boolean _staticSpirel){
		nom=Mouvement_perso.m_spirel;//spirel

		staticSpirel=_staticSpirel;

		xpos = xPo;
		ypos = yPo; 
		vit=new Vitesse(0,0);
		deplacement=new Attente(nom) ;
		anim=1;
		tempsAncienMouv= System.nanoTime();
		actionReussite=false;
		reaffiche=0;

		finSaut=false;
		peutSauter=false;
		glisse=false;
		reaffiche=0;

		//Param from Collidable
		slowDownFactor=3;
		fixedWhenScreenMoves=false;

		xDecallagePlacementTir= Arrays.asList(30,-60,20);
		yDecallagePlacementTir= Arrays.asList(0 ,0,-100);
	}
	/**
	 * Permet de savoir de quel cote est tourné le monstre
	 * 
	 * @param anim, l'animation du monstre
	 * 
	 * @return String , "Droite" ou "Gauche", direction dans laquelle le monstre est tourné
	 */
	public String droite_gauche (int anim)
	{
		if(deplacement.IsDeplacement(Mouvement_perso.marche))
		{
			if(anim <2)
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
			if(anim <1)
			{
				return ("Gauche");
			}
			else return("Droite");
		}

		else 
			throw new IllegalArgumentException("Spirel/droite_gauche: ERREUR deplacement inconnu");
	}
	/**
	 * Gère l'ensemble des événements lié au deplacement d'un monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours 
	 */		
	@Override
	public boolean deplace(AbstractModelPartie partie,Deplace deplace)
	{
		//compute the next desired movement 
		IA(partie.tabTirMonstre,partie.heros,partie);
		//compute the true next movement depending on landing, gravity, ... 
		changeMouv(partie,deplace);
		return true;//move the object
	}

	/**
	 * IA pour le deplacement du monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours  
	 */	
	public void IA (List<TirMonstre> tabTirMonstre, Heros heros,AbstractModelPartie partie)
	{

		boolean herosAGauche;
		boolean canShoot = (System.nanoTime()-tempsAncienTir)*Math.pow(10, -6)>delaiTir * (partie.slowDown ? 2:1);
		boolean canMove= (System.nanoTime()-tempsAncienMouv)*Math.pow(10, -6)>delaiMouv * (partie.slowDown ? 2:1);
		//On test le cooldown de tir
		if(canShoot)
		{
			cooldown=false;
			tempsAncienTir=System.nanoTime();
		}
		//on test le cooldown de mouvement
		if(canMove)
		{
			herosAGauche= xpos-(heros.xpos-partie.xScreendisp)>=0;

			int herosXCentre= heros.xpos + heros.deplacement.xtaille.get(heros.anim)/2 -partie.xScreendisp;
			int herosYCentre= heros.ypos + heros.deplacement.ytaille.get(heros.anim)/2 -partie.xScreendisp;

			int monstreXCentre = xpos+deplacement.xtaille.get(anim)/2;
			int monstreYCentre = ypos+deplacement.ytaille.get(anim)/2;

			int deltaX= Math.abs(monstreXCentre-herosXCentre);
			int deltaY= Math.abs(monstreYCentre-herosYCentre);

			boolean herosInRange= (deltaX+deltaY)<distanceAttaque && ! cooldown;

			//on test si le heros est dans le cercle d'attaque
			if(herosInRange)
			{
				//animation d'attente
				doitChangMouv= !((deplacement.IsDeplacement(Mouvement_perso.attente)) 
						&& (herosAGauche? anim==0 : anim==1));
				nouvAnim=herosAGauche? 0 : 1;
				nouvMouv= new Attente(this.nom);

				//changeMouv (monstre,partie);
				//envoie du projectile
				if(droite_gauche(anim).equals("Gauche"))
				{
					//tir à gauche, anim=1
					tabTirMonstre.add(new TirSpirel((xpos+xDecallagePlacementTir.get(1)),(ypos+yDecallagePlacementTir.get(1)),1));	
				}
				else
				{
					//tir à droite, anim=0
					tabTirMonstre.add(new TirSpirel((xpos+xDecallagePlacementTir.get(0)),(ypos+yDecallagePlacementTir.get(0)),0));	

				}
				//tir en haut, anim= 2
				tabTirMonstre.add(new TirSpirel((xpos+xDecallagePlacementTir.get(2)),(ypos+yDecallagePlacementTir.get(2)),2));	
				cooldown=true;

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
				
				boolean jumpAbove= (droite_gauche(anim).equals("Gauche")? (blocLeft && !blocLeftUp) : (blocRight && !blocRightUp) ) && peutSauter;
				boolean inAir= deplacement.IsDeplacement(Mouvement_perso.saut);
				boolean moveInAir=droite_gauche(anim).equals("Gauche")?(!blocLeft) :(!blocRight); 
				boolean holeClose= (droite_gauche(anim).equals("Gauche")? (!blocGaucheBas) : (!blocDroitBas) );
				//on saute au dessus d'un obstacle si possible
				if( jumpAbove)
				{
					doitChangMouv=!deplacement.IsDeplacement(Mouvement_perso.saut);
					nouvAnim=(herosAGauche? 0 : 1);
					nouvMouv= new Saut(this.nom);
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
						doitChangMouv=!(herosAGauche? anim==0 : anim==1);
						nouvAnim=(herosAGauche? 0 : 1);
						nouvMouv= new Saut(this.nom);
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
						doitChangMouv= !((deplacement.IsDeplacement(Mouvement_perso.attente)) 
								&& (herosAGauche? anim==0 : anim==1));
						nouvAnim=herosAGauche? 0 : 1;
						nouvMouv= new Attente(this.nom);

					}
					//on se deplace dans l'autre direction
					else if(decision <= 90)
					{
						doitChangMouv=true;
						//on change de direction
						nouvAnim=(herosAGauche? 2 : 0);
						nouvMouv= new Marche(this.nom);
					}
					//le monstre tombe en marchant
					else 
					{
						doitChangMouv=!((deplacement.IsDeplacement(Mouvement_perso.marche)) 
								&& (herosAGauche? anim<2 : anim>=2));
						nouvAnim=(herosAGauche? 0 : 2);
						nouvMouv= new Marche(this.nom);
					}
				}
				//sinon on se deplace
				else
				{
					int decision = (int) (Math.random()*100);
					//deplacement
					if(decision <= 90)
					{
						doitChangMouv=!((deplacement.IsDeplacement(Mouvement_perso.marche)) 
								&& (herosAGauche? anim<2 : anim>=2));
						nouvAnim=(herosAGauche? 0 : 2);
						nouvMouv= new Marche(this.nom);
					}
					//attente
					else
					{
						//animation d'attente
						doitChangMouv= !((deplacement.IsDeplacement(Mouvement_perso.attente)) 
								&& (herosAGauche? anim==0 : anim==1));
						nouvAnim=herosAGauche? 0 : 1;
						nouvMouv= new Attente(this.nom);
					}
				}

			}
			else //spirel is static
			{
				//animation d'attente
				doitChangMouv= !((deplacement.IsDeplacement(Mouvement_perso.attente)) 
						&& (herosAGauche? anim==0 : anim==1));
				nouvAnim=herosAGauche? 0 : 1;
				nouvMouv= new Attente(this.nom);
			}	

			tempsAncienMouv=System.nanoTime();
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
		boolean herosAGauche= xpos-(partie.heros.xpos-partie.xScreendisp)>=0;
		boolean falling= !isGrounded(partie);
		boolean landing= (finSaut||!falling) && deplacement.IsDeplacement(Mouvement_perso.saut);
		if(falling)
			useGravity=falling;
		//chute
		if(falling)
		{
			peutSauter=false;
			int animSuiv = herosAGauche? 0 : 1;
			Mouvement_perso depSuiv=new Saut(this.nom);
			alignHitbox(anim,depSuiv,animSuiv,partie,deplace);

			//le monstre tombe, on met donc son animation de saut
			anim=animSuiv;
			deplacement= depSuiv;
			setSpeed();

		}
		
		//atterrissage
		if(landing)
		{
			int animSuiv = herosAGauche? 0 : 1;
			Mouvement_perso depSuiv=new Attente(this.nom);
			alignHitbox(anim,depSuiv,animSuiv,partie,deplace);
			anim=animSuiv;
			deplacement= depSuiv;
			setSpeed();
			useGravity=false;
			peutSauter=true;
			sautDroit=false;
			sautGauche=false;
			finSaut=false;
		}
		//on execute l'action voulue
		else
		{
			if(doitChangMouv)
			{
				//monstre.actionReussite= (decallageMonstre(monstre,monstre.nouvMouv,monstre.anim,monstre.nouvAnim,false,false,partie));
				alignHitbox(anim,nouvMouv,nouvAnim,partie,deplace);

				deplacement= nouvMouv;
				anim=nouvAnim;
				setSpeed();

			}
			else 
			{
				if(deplacement.IsDeplacement(Mouvement_perso.marche))
				{
					int animSuiv = droite_gauche(anim).equals("Gauche") ? (anim+1)%2 :(anim+1)%2+2 ;
					alignHitbox(anim,deplacement,animSuiv,partie,deplace);

					nouvAnim=animSuiv;
					anim=nouvAnim;
					setSpeed();

				}
				else if (deplacement.IsDeplacement(Mouvement_perso.saut))
				{
					setSpeed();
				}
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
		boolean going_left = vit.x<0;
		boolean facing_left_still= vit.x==0 &&(droite_gauche(animActu)=="Gauche"|| last_colli_left);
		boolean sliding_left_wall = (droite_gauche(animActu)=="Droite") ;
		boolean left = ( going_left|| facing_left_still ||sliding_left_wall) ; 
		boolean down = vit.y>=0; 

		super.alignHitbox(animActu,depSuiv, animSuiv, partie,deplace,left, down,Mouvement_perso.m_spirel,true);

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
		Hitbox hit = getHitbox(partie.INIT_RECT);
		assert hit.polygon.npoints==4;
		//get world hitboxes with Collision
		Point p = new Point(partie.xScreendisp,partie.yScreendisp);
		Hitbox objectHitboxL= fixedWhenScreenMoves? Hitbox.minusPoint(hit,p,false): hit;
		//Shift all points towards right/left
		for(int i=0; i<objectHitboxL.polygon.npoints; ++i){
			objectHitboxL.polygon.xpoints[i]+=right;
			objectHitboxL.polygon.ypoints[i]-=height;
		}
		//get all hitboxes: it can be slower 
		List<Bloc> mondeHitboxes=Collision.getMondeBlocs(partie.monde, objectHitboxL, partie.INIT_RECT, partie.TAILLE_BLOC);
		//if there is a collision between mondeHitboxes and objectHitbox, it means that lower the hitbox by 1 leads to a 
		//collision: the object is likely to be on the ground (otherwise, it is in a bloc).
		for(Bloc b : mondeHitboxes)
			if(GJK_EPA.intersectsB(objectHitboxL.polygon, b.getHitbox(partie.INIT_RECT).polygon, new Vector2d(right,-height))==GJK_EPA.TOUCH)
				return true;
		return false;
	}

	@Override
	public void memorizeCurrentValue()
	{
		final Point memPos= new Point(xpos,ypos); 
		final Mouvement_perso memDep = (Mouvement_perso) deplacement.Copy(Mouvement_perso.m_spirel);
		final int memAnim = anim;
		final Vitesse memVit = vit.Copy();
		
		currentValue=new CurrentValue(){		
			@Override
			public void res()
			{xpos=memPos.x;ypos=memPos.y;deplacement=memDep;anim=memAnim;vit=memVit;}};
	}
	@Override
	public void handleStuck(AbstractModelPartie partie,Deplace deplace)
	{
		if(currentValue!=null)
			currentValue.res();
		
		if(resetHandleCollision != null)
			resetHandleCollision.reset();
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie,
			Deplace deplace) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void applyFriction(int minspeed) {
		//do nothing
	}
	@Override
	public void resetVarBeforeCollision()
	{
		last_colli_left=false;
		last_colli_right=false;
	}
	@Override
	public void resetVarDeplace()
	{
		resetHandleCollision=null;
	}
	
	/**
	 * Ralenti les animations  
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le monstre
	 */	
	@Override
	public int setReaffiche(){

		if(deplacement.IsDeplacement(Mouvement_perso.attente)){
			return(2);
		}
		else if(deplacement.IsDeplacement(Mouvement_perso.marche)){
			return(5);
		}

		else if(deplacement.IsDeplacement(Mouvement_perso.saut)){
			return(5);
		}
		else if(deplacement.IsDeplacement(Mouvement_perso.tir)){
			return(5);
		}
		else {
			throw new IllegalArgumentException("ERREUR setReaffiche monstre, ACTION INCONNUE  "  +this.deplacement.getClass().getName());
		}
	}
	@Override
	public void destroy()
	{
		(new MusicBruitage("destruction robot")).startBruitage(1000);
	}
	/**
	 * Regle la vitesse du monstre en fonction de son mouvement et de son animation
	 * 
	 * @param monstre, le monstre a deplacer 
	 */	
	public void setSpeed()
	{
		if(deplacement.IsDeplacement(Mouvement_perso.attente))
		{
			vit.x=0;
			vit.y=0;
		}
		else if(deplacement.IsDeplacement(Mouvement_perso.marche))
		{
			int speed=10;//4000
			if(anim<2)
			{
				vit.x=-1*speed;
			}
			else
			{
				vit.x= speed;
			}
		}
		else if(deplacement.IsDeplacement(Mouvement_perso.saut))
		{
			int xspeed=10;
			int yspeed=15;

			if(peutSauter)
			{
				vit.y=-1*yspeed;
			}
			if(sautGauche && ! sautDroit)
			{
				vit.x= -1*xspeed;
			}
			if(sautDroit && ! sautGauche)
			{
				vit.x= xspeed;
			}

		}

	}	




}
