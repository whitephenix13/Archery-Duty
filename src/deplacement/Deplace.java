package deplacement;

import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

import javax.vecmath.Vector2d;

import monstre.Monstre;
import monstre.TirMonstre;
import partie.AbstractModelPartie;
import personnage.Fleche;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Bloc;
import types.Hitbox;
import types.Monde;
import types.Vitesse;
import collision.Collision;
import collision.GJK_EPA;

public class Deplace implements InterfaceConstantes{

	//permet de savoir combien de fois on dois réafficher , à l'aide de la fonction setReaffiche(Heros, heros, int anim)
	protected int reaffiche = 0;
	//permet d"executer plusieurs sprites d'affilés SANS QUE LE JOUEUR PUISSE INTERVENIR
	protected int enchaineAnimation = 1;
	//booleen pour savoir si il est contre un mur et doit rentrer en glissade
	public boolean glisse =false;
	//booleen permettant de savoir si l'action qu'on a voulu réaliser a été faite, donc le cas contraire, ne rien faire 
	protected boolean actionReussite = false;
	//pemet de savoir si on a deja decaller le sprite du perso dans partie rapide 
	//booleens pour voir si le bloc dessous est bloquant
	//protected boolean dessousGauche =false;
	//protected boolean dessousDroite = false;

	//protected boolean droiteHaut = false;
	//protected boolean gaucheHaut = false;
	//on choisira le bas à 20 pixels du haut pour que le rebond ne se fasse bien qu'avec la main 
	//protected boolean droiteBas = false;
	//protected boolean gaucheBas = false;

	//objet pour connaitre les valeurs comme la taille des sprites pour une action donnée
	protected Attente attente= new Attente();
	protected Marche marche = new Marche();
	protected Course course = new Course();
	protected Saut saut = new Saut();
	protected Glissade glissade = new Glissade();
	protected Tir tir = new Tir();

	public Collision colli;

	//}}
	//{{ fonctions pour le heros

	/**
	 * Gère l'ensemble des événements lié au deplacement du heros 
	 * 
	 * @param heros, le personnage 
	 * @param nouvMouv, le nouveau mouvement donné par partieRapideActionListener
	 * @param nouvAnim, la nouvelle animation donnée par partieRapideActionListener
	 * @param Monde, le niveau en cours 
	 * 
	 */	
	public void DeplaceHeros(Heros heros, Mouvement nouvMouv,int nouvAnim, AbstractModelPartie partie) throws InterruptedException{
		//TODO: deplacer les fonctions marche(), saut() ... dans deplacement
		//TODO: passer change anim a void et renommer la methode
		boolean mouvDifferant = (! (heros.deplacement.getClass().getName()==nouvMouv.getClass().getName())) && partie.changeMouv ;
		if( mouvDifferant || (!mouvDifferant && reaffiche<=0 ) )
		{
			int memX= heros.xpos; 
			int memY= heros.ypos;
			Mouvement memDep = heros.deplacement.Copy();
			int memAnim = heros.anim;
			Vitesse memVit = heros.vit.Copy();

			//on change d'animation avant de deplacer si elle doit etre changee
			if(partie.slowDown)
				partie.slowCount= (partie.slowCount+1) % (heros.slowDownFactor);

			heros.anim=changeAnim(heros,nouvMouv, nouvAnim, partie);
			partie.changeMouv= false;
			System.out.println("test "+ heros.getWorldPosition(partie).toString());
			//boolean dontUseGravity = (partie.slowDown && partie.slowCount!=0) ;
			boolean useGravity = heros.useGravity &&( !partie.slowDown || (partie.slowDown && partie.slowCount==0));
			System.out.println("use gravity : " + useGravity);
			if(useGravity)
				Gravite.gravite(heros, partie.slowDown);

			//deplacement à l'aide de la vitesse  si il n'y a pas collision 
			//on reset les dernières positions de collisions:
			heros.last_colli_left=false;
			heros.last_colli_right=false;
			boolean stuck = !colli.collisionGenerale(partie, this, heros);
			if(stuck)
			{
				System.out.println("STUCK*****************************************");
				heros.xpos=memX;
				heros.ypos=memY;
				heros.deplacement=memDep;
				heros.anim=memAnim;
				heros.vit=memVit;
			}
			deplaceEcran(partie,heros);
			reaffiche=setReaffiche(heros,heros.anim);
		}
		else
			reaffiche--;

	}


	public boolean computeBeginSliding(Heros heros,boolean blocDroitGlisse, boolean blocGaucheGlisse,boolean falling)
	{
		boolean begin_sliding = (heros.IsDeplacement(Mouvement.saut)||heros.IsDeplacement(Mouvement.course)) 
				&& ((blocDroitGlisse && (heros.last_colli_right||heros.vit.x>0))
						||(blocGaucheGlisse&&(heros.last_colli_left||heros.vit.x<0))) && falling; 
		return begin_sliding;
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
	public int changeAnim(Heros heros,Mouvement nouvMouv, int nouvAnim, AbstractModelPartie partie) throws InterruptedException
	{
		System.out.println("\n===================== CHANGE ANIM =====================");
		System.out.println("Deplacement: "+ heros.deplacement.toString() + " "+ heros.anim);
		System.out.println("Position "+ heros.getWorldPosition(partie).toString());
		System.out.println("Speed: " + heros.vit.x + ","+heros.vit.y);
		int ydeplaceEcran = partie.ydeplaceEcran;
		boolean blocDroitGlisse=false;
		boolean blocGaucheGlisse=false;
		int animHeros=heros.anim;
		//translate all object hitboxes, see collision to get full formula
		Point deplaceEcran =new Point(partie.xdeplaceEcran+partie.xdeplaceEcranBloc,
				partie.ydeplaceEcran+partie.ydeplaceEcranBloc);
		Hitbox herosHitbox= Hitbox.minusPoint(heros.getHitbox(partie.INIT_RECT),deplaceEcran);

		List<Bloc> mondeBlocs = Collision.getMondeBlocs(partie.monde,herosHitbox, partie.INIT_RECT,partie.TAILLE_BLOC);

		for(Bloc mondeBloc : mondeBlocs)
		{
			Hitbox mondeBox = mondeBloc.getHitbox(partie.INIT_RECT);
			Polygon p_glissade_d= Hitbox.minusPoint(heros.getShiftedHitbox(1),deplaceEcran).polygon;
			Polygon p_glissade_g= Hitbox.minusPoint(heros.getShiftedHitbox(-1),deplaceEcran).polygon;
			Polygon p_monde= mondeBox.polygon;
			int a = GJK_EPA.intersectsB(p_glissade_d,p_monde, heros.vit.vect2d());
			int b = GJK_EPA.intersectsB(p_glissade_g,p_monde, heros.vit.vect2d());
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
		boolean falling = !heros.isGrounded(partie);
		if(falling)
			heros.useGravity=falling;
		//le heros atteri alors qu'il était en chute libre
		boolean landing = (partie.finSaut||!falling) && heros.IsDeplacement(Mouvement.saut) && (animHeros ==1 || animHeros ==4);
		//le heros touche le sol en glissant
		boolean landSliding = partie.finSaut && heros.IsDeplacement(Mouvement.glissade);
		//le heros chute ou cours vers un mur: il commence à glisser sur le mur 
		boolean beginSliding= computeBeginSliding(heros,blocDroitGlisse,blocGaucheGlisse,falling); //TODO: Erreur: droit est vrai/vrai quand colli gauche

		//le heros décroche du mur
		boolean endSliding = heros.IsDeplacement(Mouvement.glissade) && 
				((!blocDroitGlisse && heros.droite_gauche(animHeros)==("Gauche")) ||
						(!blocGaucheGlisse && heros.droite_gauche(animHeros)==("Droite")));

		int anim=animHeros;
		System.out.println("falling: "+ falling + " landing: "+ landing + " landSliding: "+ landSliding + " begin sliding: "+ beginSliding +
				" end sliding"+ endSliding + " change mouv "+ partie.changeMouv+ " firing "+ isFiring);
		if(isFiring)//cas différent puisqu'on ne veut pas que l'avatar chute en l'air
		{
			int animSuivante = animFlecheEncochee(partie.getXPositionSouris(), partie.getYPositionSouris() , heros);
			//on decalle
			switchDeplacement(heros,animHeros,tir,animSuivante ,partie );
			if(partie.peutSauter){heros.vit.x=0;}//on empeche le heros de deraper si il tir au sol
			heros.deplacement= new Tir();
			setReaffiche(heros,animSuivante);
			return(animSuivante);

		}
		else
		{

			//attention, falling est le seul bloc de code à ne pas avoir de return 
			if(falling)
			{
				partie.peutSauter=false;
				if(!(heros.IsDeplacement(Mouvement.glissade)||heros.IsDeplacement(Mouvement.course)))
				{
					switchDeplacement(heros,animHeros,saut,(heros.droite_gauche(animHeros)=="Gauche" ? 1 :4),partie );		
					animHeros = (heros.droite_gauche(animHeros)=="Gauche" ? 1 :4);
					anim=animHeros;
					heros.deplacement= new Saut();
					saut(anim,heros,partie);

					//landing = partie.finSaut;
					beginSliding= computeBeginSliding(heros,blocDroitGlisse,blocGaucheGlisse,(falling&&!landing));

				}
			}

			if(landing) //atterrissage: accroupi 
			{

				System.out.println("atterrissage 1");
				//on ajuste la position du personnage pour qu'il soit centré 
				switchDeplacement(heros,animHeros,heros.deplacement,(heros.droite_gauche(animHeros)=="Gauche" ? 2 : 5 ),partie );
				glisse=false;
				partie.finSaut=false;//set landing to false
				anim= (heros.droite_gauche(animHeros)=="Gauche"? 2 : 5 );
				saut(anim, heros,partie);

				return(anim);


			}
			else if(heros.IsDeplacement(Mouvement.saut) && (animHeros ==2 || animHeros ==5))//atterissage: se relève
			{
				System.out.println("atterrissage 2");

				//on ajuste la position du personnage pour qu'il soit centré 
				switchDeplacement(heros,animHeros,attente,(heros.droite_gauche(animHeros)=="Gauche" ? 0 : 1 ),partie );
				glisse=false;
				//on choisit la direction d'attente			
				heros.vit.x=0;	
				partie.finSaut=false;
				partie.peutSauter=true;
				anim= (heros.droite_gauche(animHeros)=="Gauche" ? 0 : 1 );
				heros.deplacement=new Attente();
				return(anim);

			}
			else if(landSliding)
			{
				switchDeplacement(heros,animHeros,attente,(heros.droite_gauche(animHeros)=="Gauche" ? 0 : 1 ),partie );
				glisse=false;
				partie.finSaut=false;
				//on ajuste la position du personnage pour qu'il soit centré 
				anim= (heros.droite_gauche(animHeros)=="Gauche" ? 0 : 1 );
				heros.deplacement=attente;
				heros.vit.y=0;
				return(anim);

			}
			else if(beginSliding)
			{
				switchDeplacement(heros,animHeros,glissade,(blocDroitGlisse ? 0 :1),partie );
				glisse=false;
				anim = (blocDroitGlisse ? 0 :1);
				heros.deplacement= new Glissade();
				glissade(anim,heros);

				return(anim);
			}
			else if(endSliding)
			{
				if(heros.vit.y<=0) //TP au dessus du bloc
				{
					//TODO: Remplacer TP par anim accrochage
					int x_decall = 3;//4
					heros.xpos+=(heros.droite_gauche(animHeros)=="Gauche"? x_decall : (-x_decall) );

					int yTailleHeros= heros.deplacement.ydecallsprite.get(animHeros)+heros.deplacement.yhitbox.get(animHeros);
					heros.ypos= (int)((heros.ypos+yTailleHeros-ydeplaceEcran))/100*100-yTailleHeros+ydeplaceEcran-4;

					switchDeplacement(heros,animHeros,attente,(heros.droite_gauche(animHeros)=="Gauche" ? 0 :1),partie );
					anim= (heros.droite_gauche(animHeros)=="Gauche" ? 0 :1);
					heros.deplacement=new Attente();
					heros.vit= new Vitesse(0,0);
					nouvAnim=anim;
					//on reinitialise les variables de saut 
					partie.debutSaut = false;
					partie.finSaut = false;
					partie.peutSauter = true;
					glisse =false;
					partie.sautGlisse = false;
					return(anim);
				}
				else // le heros tombe 
				{
					switchDeplacement(heros,animHeros,saut,(heros.droite_gauche(animHeros)=="Gauche" ? 1 :4),partie);
					anim = (heros.droite_gauche(animHeros)=="Gauche" ? 1 :4);
					heros.deplacement= new Saut();
					saut(anim,heros,partie);
					heros.vit.x=0;
					nouvAnim=anim;	
					return(anim);
				}

			}

			//Call function to check if move is allowed
			boolean allowed = moveAllowed(heros,nouvMouv);
			//CHANGEMENT DE MOUVEMENT
			if(partie.changeMouv && allowed)
			{
				System.out.println("Change move");
				switchDeplacement(heros,animHeros,nouvMouv,nouvAnim,partie );

				anim=nouvAnim;
				heros.deplacement=nouvMouv;

				if(nouvMouv.getClass().getName().equals("deplacement.Marche"))
				{
					marche(anim,heros);	
				}
				else if(nouvMouv.getClass().getName().equals("deplacement.Course"))
				{
					course(anim,heros);
				}
				else if(nouvMouv.getClass().getName().equals("deplacement.Saut"))
				{
					saut(anim,heros,partie);
				}
				else if(nouvMouv.getClass().getName().equals("deplacement.Glissade"))
				{
					glissade(anim,heros);
				}
				else if(nouvMouv.getClass().getName().equals("deplacement.Attente"))
				{
					heros.vit.x=0;
				}
				else
				{
					partie.changeMouv=false;
					//throw new IllegalArgumentException("Deplace/changeAnim: Changement mouvement inconnu");
				}
			}

			if(!partie.changeMouv) // MEME MOUVEMENT QUE PRECEDEMMENT 
			{

				if( heros.IsDeplacement(Mouvement.marche))
				{	
					switchDeplacement(heros,animHeros,marche,(heros.droite_gauche(animHeros)=="Gauche" ? (animHeros+1)%4 :(animHeros+1)%4+4 ),partie );
					anim= (heros.droite_gauche(animHeros)=="Gauche" ? (animHeros+1)%4 :(animHeros+1)%4+4 );
					marche(anim,heros);
				}
				else if(heros.IsDeplacement(Mouvement.course))
				{			
					switchDeplacement(heros,animHeros,course,(heros.droite_gauche(animHeros)=="Gauche" ? (animHeros+1)%4 :(animHeros+1)%4+4 ),partie );
					anim= (heros.droite_gauche(animHeros)=="Gauche" ? (animHeros+1)%4 :(animHeros+1)%4+4 );
					course(anim,heros);
				}
				else if(heros.IsDeplacement(Mouvement.saut))
				{
					saut(animHeros,heros,partie);
				}
				else if( heros.IsDeplacement(Mouvement.glissade))
				{
					//pas de changement d'animation
				}
				else if(heros.IsDeplacement(Mouvement.attente))
				{
					// pas de changement d'animation
				}
				else if(heros.IsDeplacement(Mouvement.tir))
				{
					// pas de changement d'animation
				}
				else 
				{
					throw new IllegalArgumentException("Deplace/changeAnim: meme mouvement inconnu: "+ heros.deplacement.getClass().getName());
				}
			}
			return(anim);
		}
	}

	private boolean moveAllowed(Heros heros, Mouvement nextMove)
	{
		String currentM = heros.deplacement.getClass().getName();
		String nextM = nextMove.getClass().getName();

		boolean allowed=true;
		
		//Three possible move so far : attente,marche, course, saut
		//Unexpected behavious: attente/marche while being in the air(ie current move being saut/glissade )
		
		allowed = allowed && !( (currentM.equals("deplacement.Saut") || currentM.equals("deplacement.Glissade")) &&
				(nextM.equals("deplacement.Attente") || nextM.equals("deplacement.Marche") ));
		return allowed; 
	}
	/**
	 * Regle la vitesse de marche en fonction de l'animation 
	 * 
	 * @param anim, animation actuelle du personnage  
	 * @param heros, le personnage 
	 * 
	 */	
	public void marche(int anim,Heros heros) {
		assert (anim>=0 && anim <8);
		heros.vit.x= 20000 * ((anim<4)? -1 : 1 );
	}
	/**
	 * Regle la vitesse de course en fonction de l'animation 
	 * 
	 * @param anim, animation actuelle du personnage  
	 * @param heros, le personnage 
	 * 
	 */	
	public void course (int anim,Heros heros) {
		assert (anim>=0 && anim <8);
		heros.vit.x= 40000 * ((anim<4)? -1 : 1 );
	}
	/**
	 * Regle la vitesse de glissade en fonction de l'animation 
	 * 
	 * @param anim, animation actuelle du personnage  
	 * @param heros, le personnage 
	 * 
	 */	
	public void glissade (int anim,Heros heros) {
		//on ne change pas la vitesse en y : on diminuera juste la gravitée
	}
	/**
	 * Regle la vitesse de saut en fonction de l'animation 
	 * 
	 * @param anim, animation actuelle du personnage  
	 * @param heros, le personnage 
	 * 
	 */	
	public void saut(int anim,Heros heros,AbstractModelPartie partie){
		//permet de déplacer le héros sur le cote 
		final int vitMax = (heros.vit.x == 0) ? 8000:  Math.abs(heros.vit.x) ; 
		final int varVit = 8000 ; 
		final int vitSaut = -15000; //10000 normalement 

		if(partie.sautGlisse)
		{
			partie.sautGlisse=false;
			heros.vit.x=varVit * ((heros.droite_gauche(anim)=="Gauche") ? -1 : 1);
			heros.vit.y=vitSaut;
		}
		else
		{
			if(partie.debutSaut) 
			{
				heros.vit.y=vitSaut;
				partie.debutSaut =false;
			}
			else if(partie.finSaut)
			{
				heros.vit.y=0;
			}
			if (partie.deplaceSautDroit )
			{
				if(heros.vit.x<(vitMax- varVit))
					heros.vit.x+= varVit;
				else 
					heros.vit.x= vitMax;

				//on attend que le joueur réappui sur la touche de direction pour redeplacer
				partie.deplaceSautDroit= false;
				return;
			}
			if (partie.deplaceSautGauche )
			{
				if(heros.vit.x>(-1*vitMax+ varVit))
					heros.vit.x-= varVit;
				else 
					heros.vit.x= -1*vitMax;

				//on attend que le joueur réappui sur la touche de direction pour redeplacer
				partie.deplaceSautGauche= false;
				return;
			}
		}

	}

	/**
	 * Regle le nombre de tour de boucle a attendre avant de réappeler la fonction DeplaceHeros
	 * 
	 * @param anim, animation actuelle du personnage  
	 * @param heros, le personnage 
	 * 
	 * @return le nombre de tour de boucle
	 */	
	public int setReaffiche(Heros heros,int anim) //{{
	{
		int reaffiche=0;
		if(heros.IsDeplacement(Mouvement.attente))
			reaffiche=50;//50

		else if(heros.IsDeplacement(Mouvement.marche))
			reaffiche=100;//100

		else if(heros.IsDeplacement(Mouvement.course))
			reaffiche=50;//50

		else if(heros.IsDeplacement(Mouvement.glissade))
			reaffiche=20;//20

		else if(heros.IsDeplacement(Mouvement.saut))
			reaffiche=20;//20

		else if(heros.IsDeplacement(Mouvement.tir))
			reaffiche=20;//20
		else 
			throw new IllegalArgumentException("ERREUR setReaffiche, ACTION INCONNUE  "  +heros.deplacement.getClass().getName());

		return(reaffiche);
	}
	//}}

	/**
	 * Recentre l'ecran autour du heros
	 * 
	 * @param heros, le personnage 
	 * 
	 */	
	public void deplaceEcran(AbstractModelPartie partie, Heros heros) //{{
	{
		//les conditions limites sont aux 3/7
		//trop à gauche de l'ecran
		if(heros.xpos<2*InterfaceConstantes.LARGEUR_FENETRE/7){
			//on calcul de combien on doit deplacer l'ecran
			partie.xdeplaceEcran+= 2*InterfaceConstantes.LARGEUR_FENETRE/7-heros.xpos;
			heros.xpos=2*InterfaceConstantes.LARGEUR_FENETRE/7; 
			//on reajuste par rapport à la position du rectangle à regarder 
			partie.xdeplaceEcranBloc+=partie.xdeplaceEcran/100*100;
			//enlever le -= et le remplacer par = absRect0 + deplaceecranbloc
			//absRect-=xdeplaceEcran/100*100; // absRect est en pixel
			partie.absRect=partie.INIT_RECT.x - partie.xdeplaceEcranBloc;
			partie.xdeplaceEcran=partie.xdeplaceEcran%100;
		}
		//trop à droite 
		else if((heros.xpos+heros.deplacement.xtaille.get(heros.anim))>5*InterfaceConstantes.LARGEUR_FENETRE/7){
			partie.xdeplaceEcran-= heros.xpos +heros.deplacement.xtaille.get(heros.anim)- 5*InterfaceConstantes.LARGEUR_FENETRE/7;
			heros.xpos = 5*InterfaceConstantes.LARGEUR_FENETRE/7-heros.deplacement.xtaille.get(heros.anim);
			//on reajuste par rapport à la position du rectangle à regarder 
			partie.xdeplaceEcranBloc+=partie.xdeplaceEcran/100*100;
			//absRect-=xdeplaceEcran/100*100;
			partie.absRect=partie.INIT_RECT.x - partie.xdeplaceEcranBloc;
			partie.xdeplaceEcran=partie.xdeplaceEcran%100;
		}
		//trop en haut
		if(heros.ypos<2*InterfaceConstantes.HAUTEUR_FENETRE/5){
			//on calcul de combien on doit deplacer l'ecran
			partie.ydeplaceEcran+= 2*InterfaceConstantes.HAUTEUR_FENETRE/5-heros.ypos;
			heros.ypos=2*InterfaceConstantes.HAUTEUR_FENETRE/5; 
			//on reajuste par rapport à la position du rectangle à regarder 
			partie.ydeplaceEcranBloc+=partie.ydeplaceEcran/100*100;
			partie.ordRect=partie.INIT_RECT.y - partie.ydeplaceEcranBloc;
			partie.ydeplaceEcran=partie.ydeplaceEcran%100;
		}
		else if((heros.ypos+heros.deplacement.ytaille.get(heros.anim))>3*InterfaceConstantes.HAUTEUR_FENETRE/5)
		{
			//on calcul de combien on doit deplacer l'ecran
			partie.ydeplaceEcran-= (heros.ypos+heros.deplacement.ytaille.get(heros.anim))-3*InterfaceConstantes.HAUTEUR_FENETRE/5;
			heros.ypos=3*InterfaceConstantes.HAUTEUR_FENETRE/5-heros.deplacement.ytaille.get(heros.anim);
			//on reajuste par rapport à la position du rectangle à regarder 
			partie.ydeplaceEcranBloc+=partie.ydeplaceEcran/100*100;
			//	ordRect-=ydeplaceEcran/100*100; // absRect est en pixel
			partie.ordRect=partie.INIT_RECT.y - partie.ydeplaceEcranBloc;
			partie.ydeplaceEcran=partie.ydeplaceEcran%100;
		}

	}
	//}}

	//Move the character to center it before the animation change.
	public void switchDeplacement(Heros heros,int animActu,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie)
	{
		String sDepActu = heros.deplacement.getClass().getName();
		System.out.println("SWITCH DEPLACEMENT: "+ sDepActu +" => " +depSuiv.toString()+ " // "+ animActu + " => "+ animSuiv);
		System.out.println("\tBefore switch position "+ heros.getWorldPosition(partie,heros.getHitbox(partie.INIT_RECT,heros.deplacement,animActu)));

		/*
		  normal -> normal : sens de la vitesse
		 ***-> glissade sens de la vitesse: -> [| 
		  glissade -> *** opposé au regard |[ -> (on évite de rentrer dans le mur où on était)
		  Par défaut si la vitesse est nulle: BAS DROITE
		 * */
		Mouvement depActu= heros.deplacement;
		boolean isGlissade = heros.IsDeplacement(Mouvement.glissade);
		boolean going_left = heros.vit.x<0;
		boolean facing_left_still= heros.vit.x==0 &&(heros.droite_gauche(animActu)=="Gauche"|| heros.last_colli_left)&& !isGlissade;
		boolean sliding_left_wall = (heros.droite_gauche(animActu)=="Droite") && isGlissade;
		boolean left = ( going_left|| facing_left_still ||sliding_left_wall) ; 
		boolean right = !left;
		boolean up = heros.vit.y<0; 
		boolean down = heros.vit.y>=0; 
		Vector2d rightActu=null;Vector2d rightSuiv=null;
		if(right)
		{
			rightActu= Hitbox.supportPoint(new Vector2d(1,0), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
			rightSuiv= Hitbox.supportPoint(new Vector2d(1,0), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);
		}

		Vector2d leftActu=null;Vector2d leftSuiv=null;
		if(left)
		{
			leftActu= Hitbox.supportPoint(new Vector2d(-1,0), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
			leftSuiv= Hitbox.supportPoint(new Vector2d(-1,0), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);
		}

		Vector2d upActu=null;Vector2d upSuiv=null;
		if(up)
		{
			upActu= Hitbox.supportPoint(new Vector2d(0,-1), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
			upSuiv= Hitbox.supportPoint(new Vector2d(0,-1), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);
		}

		Vector2d downActu=null;Vector2d downSuiv=null;
		if(down)
		{
			downActu= Hitbox.supportPoint(new Vector2d(0,1), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
			downSuiv= Hitbox.supportPoint(new Vector2d(0,1), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);
		}
		//TODO: debug message 
		String s= "\t";
		s+= (left? "left" : (right? "right": "err"));
		s+= (up? " up" : (down? " down": " err"));
		System.out.println(s);

		if(left && up)
		{
			heros.xpos+= leftActu.x-leftSuiv.x;
			heros.ypos+= upActu.y-upSuiv.y;
		}
		else if(left && down)
		{
			heros.xpos+= leftActu.x-leftSuiv.x;
			heros.ypos+= downActu.y-downSuiv.y;
		}
		else if(right && up)
		{
			heros.xpos+= rightActu.x-rightSuiv.x;
			heros.ypos+= upActu.y-upSuiv.y;
		}
		else if(right && down)
		{
			heros.xpos+= rightActu.x-rightSuiv.x;
			heros.ypos+= downActu.y-downSuiv.y;
		} 
		else
			try {throw new Exception("\tswitch Deplacement: unknow case");} catch (Exception e) {e.printStackTrace();}
		System.out.println("\tAfter switch position "+ heros.getWorldPosition(partie,heros.getHitbox(partie.INIT_RECT,depSuiv,animSuiv)));

	}
	public boolean decallageHeros(Heros heros,Mouvement depSuiv, int animActu, int animSuiv, AbstractModelPartie partie) {

		Mouvement depActu= heros.deplacement;

		if(depActu.getClass().getName()=="deplacement.Glissade" || depSuiv.getClass().getName()=="deplacement.Glissade")
		{
			if(heros.droite_gauche(heros.anim).equals("Gauche"))
			{
				//memoriser les points les plus à droite et les plus à gauche pour depActu,animActu et depSuiv, animSuiv
				Vector2d rightActu= Hitbox.supportPoint(new Vector2d(1,0), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
				Vector2d rightSuiv= Hitbox.supportPoint(new Vector2d(1,0), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);

				if(herosBloque(heros,depSuiv,animSuiv,partie,(int)(rightActu.x-rightSuiv.x),(int)(rightActu.y-rightSuiv.y)) 
						|| herosBloque(heros,depSuiv,animSuiv,partie, 0,0))
					return true;

				Vector2d leftActu= Hitbox.supportPoint(new Vector2d(-1,0), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
				Vector2d leftSuiv= Hitbox.supportPoint(new Vector2d(-1,0), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);
				if(herosBloque(heros,depSuiv,animSuiv,partie,(int)(leftActu.x-leftSuiv.x),(int)(leftActu.y-leftSuiv.y)))
					return true;
				return false;
			}
			else if(heros.droite_gauche(heros.anim).equals("Droite"))
			{
				//memoriser les points les plus à droite et les plus à gauche pour depActu,animActu et depSuiv, animSuiv
				Vector2d leftActu= Hitbox.supportPoint(new Vector2d(-1,0), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
				Vector2d leftSuiv= Hitbox.supportPoint(new Vector2d(-1,0), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);


				if(herosBloque(heros,depSuiv,animSuiv,partie,(int)(leftActu.x-leftSuiv.x),(int)(leftActu.y-leftSuiv.y)) 
						|| herosBloque(heros,depSuiv,animSuiv,partie, 0,0))
					return true;

				Vector2d rightActu= Hitbox.supportPoint(new Vector2d(1,0), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
				Vector2d rightSuiv= Hitbox.supportPoint(new Vector2d(1,0), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);

				if(herosBloque(heros,depSuiv,animSuiv,partie,(int)(rightActu.x-rightSuiv.x),(int)(rightActu.y-rightSuiv.y)))
					return true;
				return false;
			}
			else
				return false;
		}
		else
		{
			Vector2d downActu= Hitbox.supportPoint(new Vector2d(0,1), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
			Vector2d downSuiv= Hitbox.supportPoint(new Vector2d(0,1), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);

			if(herosBloque(heros,depSuiv,animSuiv,partie,(int)(downActu.x-downSuiv.x),(int)(downActu.y-downSuiv.y)) 
					|| herosBloque(heros,depSuiv,animSuiv,partie, 0,0))
				return true;

			Vector2d upActu= Hitbox.supportPoint(new Vector2d(0,-1), heros.getHitbox(partie.INIT_RECT, depActu, animActu).polygon);
			Vector2d upSuiv= Hitbox.supportPoint(new Vector2d(0,-1), heros.getHitbox(partie.INIT_RECT, depSuiv, animSuiv).polygon);

			if(herosBloque(heros,depSuiv,animSuiv,partie,(int)(upActu.x-upSuiv.x),(int)(upActu.y-upSuiv.y)))
				return true;
			return false;
		}
	}


	public boolean herosBloque(Heros heros,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie, int depX, int depY){
		Polygon poly = heros.getHitbox(partie.INIT_RECT).polygon;
		boolean isBloque=false;
		for(int i=0; i<poly.npoints; ++i)
			isBloque=isBloque || (partie.monde.niveau[poly.xpoints[i]+depX][poly.xpoints[i]+depY].getBloquer());

		return isBloque;
	}

	//}}
	//{{ fonctions pour la fleche
	/**
	 * Gère l'ensemble des événements lié au deplacement de la fleche 
	 * 
	 * @param fleche, la fleche a deplacer 
	 * @param anim, l'animation actuelle de la fleche
	 * @param Monde, le niveau en cours 
	 * @param heros, le personnage , utilisé pour connaitre sa position
	 * 
	 */	
	public void DeplaceFleche(Fleche fleche ,int anim, AbstractModelPartie partie,Heros heros) throws InterruptedException{
		if(fleche.reaffiche<=0)
		{
			if(fleche.encochee)
			{
				setParamFleche(partie,fleche,heros);
				fleche.anim=changeAnim(fleche,partie,heros);

			}

			//une fleche encochee ne peut pas se deplacer 
			else if(fleche.doitDeplace)
			{
				fleche.anim=changeAnim(fleche,partie,heros);
				//Gravite.gravite(fleche,variablesPartieRapide.slowDown);
				colli.collision(partie, fleche);
				//on redessine autant de fois l'image qu'il le faut pour que l'animation soit assez lente
				fleche.reaffiche=setReaffiche(fleche,heros.anim);



			}
			//la fleche n'est pas encochée et ne doit pas se deplacer: elle est plantée quelque part 
		}
		else
		{
			fleche.reaffiche--;
		}
	}

	/**
	 * Indique en fonction de la vitesse de la fleche, son animation
	 * 
	 * @param fleche, la fleche a deplacer 
	 * @return l'animation de la fleche
	 */	
	public int quelCadran(Fleche fleche)
	{
		//tan (45°/2) = 0,41421356
		// les cadrans sont numerotés de 0 à 7 en tournant (cadrant 0 entre le fleche d'anim 7 et 1 )
		// dans le sens horaire
		//on fait les cas limites 

		if(fleche.vit.y ==0 && fleche.vit.x==0)
		{
			return(fleche.anim);//on garde la même animation
		}
		else if(fleche.vit.y ==0 && fleche.vit.x>0)
		{
			return(2);//droite
		}
		else if(fleche.vit.y ==0 && fleche.vit.x<0)
		{
			return(6);//gauche
		}
		else if(fleche.vit.y >0 && fleche.vit.x == 0)
		{
			return(4);//bas
		}
		else if(fleche.vit.y < 0 && fleche.vit.x == 0)
		{
			return(0);//haut
		}
		else if(fleche.vit.y <0 && Math.abs((float)fleche.vit.y/fleche.vit.x)>=Math.abs(Math.tan(67.5* Math.PI/ 180)))
		{
			return(0);
		}
		else if(fleche.vit.y <0 && fleche.vit.x>0 && Math.abs((float)fleche.vit.y/fleche.vit.x)>=Math.abs(Math.tan(22.5* Math.PI/ 180)))
		{
			return(1);
		}
		else if(fleche.vit.x>0 && Math.abs((float)fleche.vit.y/fleche.vit.x)<=Math.abs(Math.tan(22.5* Math.PI/ 180)))
		{
			return(2);
		}
		else if(fleche.vit.y > 0 && fleche.vit.x>0 && Math.abs((float)fleche.vit.y/fleche.vit.x)>=Math.abs(Math.tan(22.5* Math.PI/ 180)))
		{
			return(3);
		}
		else if(fleche.vit.y > 0  && Math.abs((float)fleche.vit.y/fleche.vit.x)>=Math.abs(Math.tan(67.5* Math.PI/ 180)))
		{
			return(4);
		}
		else if(fleche.vit.y > 0 && fleche.vit.x<0 && Math.abs((float)fleche.vit.y/fleche.vit.x)>=Math.abs(Math.tan(22.5* Math.PI/ 180)))
		{
			return(5);
		}
		else if(fleche.vit.x<0 && Math.abs((float)fleche.vit.y/fleche.vit.x)<= Math.abs(Math.tan(22.5* Math.PI/ 180)))
		{
			return(6);
		}
		else if(fleche.vit.y <0 && fleche.vit.x<0 && Math.abs((float)fleche.vit.y/fleche.vit.x)>=Math.abs(Math.tan(22.5* Math.PI/ 180)))
		{
			return(7);
		}
		else {
			throw new IllegalArgumentException("ERREUR: quelCadran");
		}
	}
	/**
	 * Permet de savoir si on doit changer l'animation de la fleche
	 * 
	 * @param fleche, la fleche a deplacer 
	 * @param cadran, entier entre 0 et 7 indiquant la direction de la fleche
	 * @return si la fleche a la bonne animation ou non 
	 */	
	public boolean doitChangerAnim(Fleche fleche, int cadran)
	{
		if(cadran==fleche.anim )
		{
			return(false);

		}
		else 
		{
			return(true);
		}
	}
	/**
	 * Change l'animation de la fleche si necessaire 
	 * 
	 * @param fleche, la fleche a deplacer 
	 * @param anim, l'animation actuelle de la fleche
	 * @param Monde, le niveau en cours 
	 * @return l'animation de la fleche
	 */	
	public int changeAnim(Fleche fleche,AbstractModelPartie partie,Heros heros) throws InterruptedException//{{
	{
		if(!fleche.encochee)
		{
			int animSuivante= quelCadran(fleche);
			if (doitChangerAnim(fleche,animSuivante))
			{

				decallageFleche (fleche, animSuivante, partie );
				return(animSuivante);

			}
			else 
			{
				return(fleche.anim);
			}
		}
		else //fleche encochee
		{
			return(animFlecheEncochee(partie.getXPositionSouris(),partie.getYPositionSouris(), heros));
		}
	}
	//}}

	/**
	 * Permet de passer a l'animation de fleche suivante et de la detruire si il y a collision
	 * 
	 * @param fleche, la fleche a deplacer 
	 * @param animSuivante, l'animation suivante de la fleche
	 * @param Monde, le niveau en cours 
	 */	
	public void decallageFleche(Fleche fleche, int animSuivante, AbstractModelPartie partie)
	{

		Monde monde = partie.monde;
		int INIT_ABS_RECT = partie.INIT_RECT.x;
		int INIT_ORD_RECT = partie.INIT_RECT.y;


		// on veut que le centre bas des flèches coincident 
		Point centreFleche = new Point();
		Point positionFinal = new Point();
		centreFleche=centreBasFleche(fleche.xpos, fleche.ypos,fleche.anim);
		positionFinal=placerCentreBasFleche(centreFleche.x,centreFleche.y,animSuivante);

		// on effectue le decallage
		fleche.xpos=positionFinal.x;
		fleche.ypos=positionFinal.y;

		int xHG =fleche.xpos+ fleche.deplacement.xHdecallsprite.get(animSuivante);
		int xHD =fleche.xpos + fleche.deplacement.xHdecallsprite.get(animSuivante) + fleche.deplacement.xHdecall2.get(animSuivante);
		int  xBG =fleche.xpos + fleche.deplacement.xBdecallsprite.get(animSuivante);
		int  xBD= fleche.xpos + fleche.deplacement.xBdecallsprite.get(animSuivante) + fleche.deplacement.xHdecall2.get(animSuivante);
		int  yHG =fleche.ypos + fleche.deplacement.yHdecallsprite.get(animSuivante);
		int  yHD=fleche.ypos + fleche.deplacement.yHdecallsprite.get(animSuivante) + fleche.deplacement.yHdecall2.get(animSuivante);
		int  yBG =fleche.ypos + fleche.deplacement.yBdecallsprite.get(animSuivante);
		int yBD=fleche.ypos + fleche.deplacement.yBdecallsprite.get(animSuivante) + fleche.deplacement.yBdecall2.get(animSuivante);

		//si il y a collision lors du changement d'animation, on doit arreter la fleche
		if (  monde.niveau[(xHG +INIT_ABS_RECT)/100][(yHG+INIT_ORD_RECT)/100].getBloquer()
				|| monde.niveau[(xHD +INIT_ABS_RECT)/100][(yHD+INIT_ORD_RECT)/100].getBloquer()
				|| monde.niveau[(xBG +INIT_ABS_RECT)/100][(yBG+INIT_ORD_RECT)/100].getBloquer()
				|| monde.niveau[(xBD+INIT_ABS_RECT)/100][(yBD+INIT_ORD_RECT)/100].getBloquer()
				)
		{
			fleche.doitDeplace=false;
			fleche.doitDetruire=true;
			//fleche.timer();
		}

	}
	/**
	 * Permet d'obtenir le centre en bas de la fleche (endroit ou on l'encoche)
	 * 
	 * @param xpos, position x de la fleche
	 * @param ypos, position y de la fleche
	 * @param anim, animation de la fleche
	 * @return le centre bas de la fleche 
	 */	
	public Point centreBasFleche(int xpos, int ypos, int anim)
	{
		Point point = new Point();
		MouvementFleche mouv= new MouvementFleche();
		int xHG =xpos+ mouv.xHdecallsprite.get(anim);
		int xHD =xpos + mouv.xHdecallsprite.get(anim) + mouv.xHdecall2.get(anim);
		int  xBG =xpos + mouv.xBdecallsprite.get(anim);
		int  xBD= xpos + mouv.xBdecallsprite.get(anim) + mouv.xBdecall2.get(anim);
		int  yHG =ypos + mouv.yHdecallsprite.get(anim);
		int  yHD=ypos + mouv.yHdecallsprite.get(anim) + mouv.yHdecall2.get(anim);
		int  yBG =ypos + mouv.yBdecallsprite.get(anim);
		int yBD=ypos + mouv.yBdecallsprite.get(anim) + mouv.yBdecall2.get(anim);

		if(anim==7 ||anim==0|| anim ==1)
		{
			point.x=(xBG+xBD)/2;
			point.y=(yBG+yBD)/2;
		}
		else if (anim==2)
		{
			point.x=(xHG+xBG)/2;
			point.y=(yHG+yBG)/2;
		}
		else if (anim==3||anim==4|| anim==5)
		{
			point.x=(xHG+xHD)/2;
			point.y=(yHG+yHD)/2;
		}
		else if (anim==6)
		{
			point.x=(xHD+xBD)/2;
			point.y=(yHD+yBD)/2;
		}
		return(point);
	}
	/**
	 * Permet de placer le centre bas d'une fleche a un point donné
	 * 
	 * @param xValeurVoulue, position en x voulue pour le bas de la fleche
	 * @param yValeurVoulue,  position en y voulue pour le bas de la fleche
	 * @param anim, animation de la fleche
	 * @return la valeur de xpos et ypos pour la fleche
	 */	
	public Point placerCentreBasFleche(int xValeurVoulue, int yValeurVoulue, int anim)
	{
		Point point = new Point();
		MouvementFleche mouv= new MouvementFleche();

		// on place la fleche a la bonne position position (le coin haut gauche de la fleche est place)
		// on fait le decallage pour que ce soit le bas milieu qui soit a cet endroit
		if(anim==7||anim==0|| anim ==1)
		{
			point.x = xValeurVoulue- mouv.xBdecallsprite.get(anim)-mouv.xBdecall2.get(anim)/2;
			point.y = yValeurVoulue- mouv.yBdecallsprite.get(anim)-mouv.yBdecall2.get(anim)/2;
		}
		else if (anim==2)
		{
			point.x = xValeurVoulue- mouv.xHdecallsprite.get(anim)/2-mouv.xBdecallsprite.get(anim)/2;
			point.y = yValeurVoulue- mouv.yHdecallsprite.get(anim)/2-mouv.yBdecallsprite.get(anim)/2;
		}
		else if (anim==3||anim==4|| anim==5)
		{
			point.x = xValeurVoulue- mouv.xHdecallsprite.get(anim)-mouv.xHdecall2.get(anim)/2;
			point.y = yValeurVoulue- mouv.yHdecallsprite.get(anim)-mouv.yHdecall2.get(anim)/2;
		}
		else if (anim==6)
		{
			point.x = xValeurVoulue- mouv.xHdecallsprite.get(anim)/2-mouv.xBdecallsprite.get(anim)/2-mouv.xBdecall2.get(anim)/2 -mouv.xHdecall2.get(anim)/2;
			point.y = yValeurVoulue- mouv.yHdecallsprite.get(anim)/2-mouv.yBdecallsprite.get(anim)/2-mouv.yBdecall2.get(anim)/2 -mouv.yHdecall2.get(anim)/2;
		}
		return(point);
	}
	/**
	 * Regle le nombre de tour de boucle a attendre avant de réappeler la fonction DeplaceFleche
	 * 
	 * @param fleche, la fleche a deplacer
	 * @param anim, animation actuelle de la fleche
	 *
	 * @return le nombre de tour de boucle
	 */	
	public int setReaffiche(Fleche fleche,int anim) 
	{
		if(fleche.deplacement.getClass().getName().equals("deplacement.MouvementFleche")){
			return(20);
		}
		else {
			throw new IllegalArgumentException("ERREUR setReaffiche, ACTION INCONNUE  "  +fleche.deplacement.getClass().getName());
		}
	}

	/**
	 * Regle la vitesse de la fleche
	 * 
	 * @param fleche, la fleche a deplacer
	 * @param heros, le personnage jouable
	 *
	 */	
	public void setParamFleche (AbstractModelPartie partie, Fleche fleche,Heros heros) 
	{
		int xdeplaceEcran = partie.xdeplaceEcran;
		int ydeplaceEcran = partie.ydeplaceEcran;
		int xdeplaceEcranBloc = partie.xdeplaceEcranBloc;
		int ydeplaceEcranBloc = partie.ydeplaceEcranBloc;

		int animFleche = animFlecheEncochee(partie.getXPositionSouris(),partie.getYPositionSouris(),heros);
		//on veut que la norme du vecteur vitesse vaille "vitesse"
		int vitesse = 40000;
		int vitesseReduite= (int) (40000f/(2));
		fleche.anim= animFleche;
		switch(animFleche)
		{
		case 0 : 
			fleche.xpos= (heros.xpos-xdeplaceEcran-xdeplaceEcranBloc)+fleche.deplacement.xHdecallFleche;
			fleche.ypos= (heros.ypos-ydeplaceEcran-ydeplaceEcranBloc)+fleche.deplacement.yHdecallFleche;
			fleche.vit.x=0;
			fleche.vit.y= -1 *vitesse;
			break;
		case 1 : 
			fleche.xpos= (heros.xpos-xdeplaceEcran-xdeplaceEcranBloc)+fleche.deplacement.xHDdecallFleche;
			fleche.ypos= (heros.ypos-ydeplaceEcran-ydeplaceEcranBloc)+fleche.deplacement.yHDdecallFleche;
			fleche.vit.x= vitesseReduite;
			fleche.vit.y= -1 *vitesseReduite;
			break;
		case 2 : 
			fleche.xpos= (heros.xpos-xdeplaceEcran-xdeplaceEcranBloc)+fleche.deplacement.xDdecallFleche;
			fleche.ypos= (heros.ypos-ydeplaceEcran-ydeplaceEcranBloc)+fleche.deplacement.yDdecallFleche;
			fleche.vit.x= vitesse;
			fleche.vit.y=0;
			break;
		case 3 : 
			fleche.xpos= (heros.xpos-xdeplaceEcran-xdeplaceEcranBloc)+fleche.deplacement.xBDdecallFleche;
			fleche.ypos= (heros.ypos-ydeplaceEcran-ydeplaceEcranBloc)+fleche.deplacement.yBDdecallFleche;
			fleche.vit.x= vitesseReduite;
			fleche.vit.y= vitesseReduite;
			break;
		case 4 : 
			fleche.xpos= (heros.xpos-xdeplaceEcran-xdeplaceEcranBloc)+fleche.deplacement.xBdecallFleche;
			fleche.ypos= (heros.ypos-ydeplaceEcran-ydeplaceEcranBloc)+fleche.deplacement.yBdecallFleche;
			fleche.vit.x=0;
			fleche.vit.y=vitesse;
			break;
		case 5 : 
			fleche.xpos= (heros.xpos-xdeplaceEcran-xdeplaceEcranBloc)+fleche.deplacement.xBGdecallFleche;
			fleche.ypos= (heros.ypos-ydeplaceEcran-ydeplaceEcranBloc)+fleche.deplacement.yBGdecallFleche;
			fleche.vit.x= -1 * vitesseReduite;
			fleche.vit.y= vitesseReduite;
			break;
		case 6 : 
			fleche.xpos= (heros.xpos-xdeplaceEcran-xdeplaceEcranBloc)+fleche.deplacement.xGdecallFleche;
			fleche.ypos= (heros.ypos-ydeplaceEcran-ydeplaceEcranBloc)+fleche.deplacement.yGdecallFleche;
			fleche.vit.x= -1 * vitesse;
			fleche.vit.y= 0;
			break;
		case 7 : 
			fleche.xpos= (heros.xpos-xdeplaceEcran-xdeplaceEcranBloc)+fleche.deplacement.xHGdecallFleche;
			fleche.ypos= (heros.ypos-ydeplaceEcran-ydeplaceEcranBloc)+fleche.deplacement.yHGdecallFleche;
			fleche.vit.x= -1 * vitesseReduite;
			fleche.vit.y= -1 * vitesseReduite;
			break;
		default : 	throw new IllegalArgumentException("ERREUR: set position fleche: anim inconnue ");
		}
	}

	//}}
	//{{ fonctions pour le monstre

	/**
	 * Gère l'ensemble des événements lié au deplacement d'un monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param Monde, le niveau en cours 
	 * 
	 */	
	public void DeplaceMonstre(List<TirMonstre> tabTirMonstre,Monstre monstre, AbstractModelPartie partie) 
	{
		if(monstre.reaffiche<=0)
		{
			if(partie.slowDown && partie.slowCount!=0)
			{
				//Deplacement par IA, choix de l'animation et de la vitesse;
				monstre.deplace(tabTirMonstre,monstre,partie.heros,  partie);

				//on gere les collisions
				colli.collision(partie, monstre);

				//on redessine autant de fois l'image qu'il le faut pour que l'animation soit assez lente
				monstre.reaffiche=monstre.setReaffiche();
			}
			else
			{
				//Gravite
				Gravite.gravite(monstre);
				//Deplacement par IA, choix de l'animation et de la vitesse;
				monstre.deplace(tabTirMonstre,monstre,partie.heros,  partie);

				//on gere les collisions
				colli.collision(partie, monstre);

				//on redessine autant de fois l'image qu'il le faut pour que l'animation soit assez lente
				monstre.reaffiche=monstre.setReaffiche();
			}
		}
		else
		{
			monstre.reaffiche--;
		}
	}

	//}}
	//{{ fonctions communes 
	/**
	 * Initialise 
	 */	
	public Deplace() 
	{
		colli = new Collision();
	}

	/**
	 * Renvoie l'animation d'un fleche encochée en fonction de la position de la souris 
	 * 
	 * @param xPosSouris, position en x de la souris
	 * @param yPosSouris, position en y de la souris
	 * @param heros, le personnage jouable
	 * 
	 * @return l'animation de la fleche
	 */	
	public int animFlecheEncochee( int xPosSouris, int yPosSouris, Heros heros)
	{
		int anim = heros.anim;
		int xPosRelative = xPosSouris - (heros.xpos+heros.deplacement.xdecallsprite.get(anim)+heros.deplacement.xhitbox.get(anim)/2);
		int yPosRelative = yPosSouris - (heros.ypos+heros.deplacement.ydecallsprite.get(anim)+heros.deplacement.yhitbox.get(anim)/2);
		int xTolerance = 100 ;
		int yTolerance = 100 ;
		if(xPosRelative> (-1 * xTolerance / 2) && xPosRelative < (xTolerance /2 ) )
		{
			if(yPosRelative <0 )
			{
				//animation en haut 
				return(0);
			}
			else 
			{
				//animation en bas 
				return(4);
			}
		}
		else if(yPosRelative> (-1 * yTolerance / 2) && yPosRelative < (yTolerance /2 ))
		{
			if(xPosRelative <0 )
			{
				//animation gauche
				return(6);
			}
			else 
			{
				//animation droite
				return(2);
			}
		}
		else 
		{
			if(xPosRelative >0 && yPosRelative <0)
			{
				return(1);
			}
			else if(xPosRelative >0 && yPosRelative >=0)
			{
				return(3);
			}
			else if(xPosRelative <=0 && yPosRelative >=0)
			{
				return(5);
			}
			else if(xPosRelative <=0 && yPosRelative < 0)
			{
				return(7);
			}
			else 
			{
				throw new IllegalArgumentException("ERREUR: animFlecheEncochee ");
			}
		}
	}
	//}}
}


