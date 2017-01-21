package partie;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JPanel;

import menuPrincipal.AbstractModelPrincipal;
import monstre.Monstre;
import monstre.Spirel;
import monstre.TirMonstre;
import music.Music;
import music.MusicBruitage;
import personnage.Fleche;
import principal.InterfaceConstantes;
import serialize.Serialize;
import types.Bloc;
import types.Hitbox;
import types.Touches;
import Affichage.Affichage;
import collision.Collidable;
import collision.Collision;
import collision.IntersectHitbox;
import deplacement.Attente;
import deplacement.Course;
import deplacement.Marche;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import deplacement.Saut;

public class ModelPartie extends AbstractModelPartie{

	/**
	 * g�re l'action dans le jeu li�e � l'appui d'une touche
	 */	

	public void startPartie(int typeDeSpawn,String nomPartie)
	{
		//on charge notre niveau
		charger(nomPartie);
		//on fait apparaitre les monstres 
		nombreMonstreRestant=100;
		spawnMonster(nombreMonstreRestant,typeDeSpawn);
	}

	/**
	 * Boucle de jeu
	 * 
	 * @param affich, la JFrame a afficher
	 */	
	@SuppressWarnings("unchecked")
	public void play(Affichage affich) 
	{
		try 
		{
			while(!affich.isFocused())
			{
				if(firstNonFocused)
				{
					resetTouchesFocus();
					inPause=true;
					Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);
					keyAction();
					AbstractModelPrincipal.changeFrame=true;
					affich.actuAffichage();

					firstNonFocused=false;
				}

			}
			firstNonFocused=true;
			Thread.sleep(InterfaceConstantes.T);//T=1ms, permet de faire des tours de boucle � intervalles r�gulier 

			//int x= heros.xPos + heros.deplacement.xdecallsprite[heros.anim]; //la vrai position du herosnnage necessite encore un - variablesPartieRapide.xdeplaceEcran
			//int y= heros.yPos+ heros.deplacement.ydecallsprite[heros.anim]; 

			//on efface les qui doivent �tre d�truit 

			//on desactive la touche cap au cas ou elle serait utilis�e
			Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);
			keyAction();

			//Lors d'une pause, on ne veut pas r�initaliser la partie en cours mais juste y acc�der � nouveau
			if(!inPause)
			{
				//First action is to delete since we want the user to since the colliding object for at least 1 frame 
				deleteObject(lEffaceFleche,(List<Collidable>)(List<?>) tabFleche);
				deleteObject(lEffaceTirMonstre,(List<Collidable>)(List<?>)tabTirMonstre);
				deleteObject(lEffaceMonstre,(List<Collidable>)(List<?>)tabMonstre);

				//on vide les listes
				lEffaceFleche.clear();
				lEffaceTirMonstre.clear();
				lEffaceMonstre.clear();

				//DEPLACEMENT 
				//Heros
				deplace.DeplaceObject(heros,heros.nouvMouv,this);
				//Monstre

				for(int i=0 ; i< tabMonstre.size(); i++)
				{
					Monstre m = tabMonstre.get(i);
					//on ne deplace le monstre qui si il est visible
					boolean monsterOnScreen= InterfaceConstantes.SCREEN.polygon.contains(new Point 
							(m.xpos+xdeplaceEcran+xdeplaceEcranBloc,
									m.ypos+ydeplaceEcran+ydeplaceEcranBloc));
					if (monsterOnScreen)
						deplace.DeplaceObject(m,m.deplacement, this);

				}

				//TIRS
				//Fleche
				for(int i=0; i<tabFleche.size(); i++)
				{
					Fleche f=tabFleche.get(i);
					deplace.DeplaceObject(f, f.deplacement, this);
				}

				//Tir Monstre deplace et a effacer
				for(int i=0 ; i< tabTirMonstre.size(); i++)
				{
					TirMonstre tir = tabTirMonstre.get(i);
					deplace.DeplaceObject(tir,tir.deplacement, this);
				}

				//tir fleche

				for(int i=0; i<tabFleche.size(); i++)
				{
					Fleche fleche = tabFleche.get(i);
					//on d�clenche le timer des fleches qui sont dans le mur 
					if(fleche.isPlanted && fleche.tempsDetruit==0)
					{
						fleche.timer();
					}
					long tempsFleche=  System.nanoTime()-fleche.tempsDetruit;

					if(fleche.isPlanted && (tempsFleche >= InterfaceConstantes.TEMPS_DESTRUCTION_FLECHE))
					{
						fleche.needDestroy=true;
					}

				}


				//on gere la collision des tirs/monstre/heros
				gestionTir();

				//PREPARE OBJECTS FOR DESTRUCTION 

				for(int i =0; i <tabFleche.size();++i)
					if(tabFleche.get(i).needDestroy)
						lEffaceFleche.add(i);

				for(int i =0; i <tabTirMonstre.size();++i)
					if(tabTirMonstre.get(i).needDestroy)
						lEffaceTirMonstre.add(i);

				for(int i =0; i <tabMonstre.size();++i)
					if(tabMonstre.get(i).needDestroy)
						lEffaceMonstre.add(i);

				//on met a jour le heros si il est touch� avant de l'afficher
				heros.miseAjourTouche();
				heros.miseAJourSpe(this);


				//on test si la partie est finie 

				boolean finPartieAvant= finPartie;

				finPartie= (heros.getLife()==InterfaceConstantes.MINLIFE) || (nombreMonstreRestant==0);

				//on detecte la fin de la partie de la premi�re fois : 
				if(!finPartieAvant &&finPartie )
				{
					AbstractModelPrincipal.changeFrame=true;
				}

			}
			//else "pause"
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * Charge le niveau en initialiser en partie VariablesPartieRapide
	 * 
	 * @param nomFichier, le nom du niveau � charger
	 */	
	public void charger(String nomFichier)
	{
		monde= Serialize.charger(nomFichier);
		//le monde est initialis�, il reste � mettre � jour les coordon�es de d�part du heros

		INIT_RECT.x= (monde.xStartPerso-InterfaceConstantes.LARGEUR_FENETRE/2)/100*100;//49 900
		INIT_RECT.y= (monde.yStartPerso-InterfaceConstantes.HAUTEUR_FENETRE/2)/100*100;//49 700

		absRect=INIT_RECT.x;
		ordRect=INIT_RECT.y;

		heros.xpos=(InterfaceConstantes.LARGEUR_FENETRE/2+(INIT_RECT.x==monde.xStartPerso-InterfaceConstantes.LARGEUR_FENETRE/2? 0:100 ))/100*100;
		heros.ypos=(InterfaceConstantes.HAUTEUR_FENETRE/2+(INIT_RECT.y==monde.yStartPerso-InterfaceConstantes.HAUTEUR_FENETRE/2? 0:100 ))/100*100;

	}

	/**
	 * G�re les collisions de tir fleche/tirMonstre, fleche/Monstre, TirMonstre/heros
	 */	
	void gestionTir () 
	{
		//gestion fleche/TirMonstre 
		for(int i=0; i<tabFleche.size(); i++)
			for(int j=0; j<tabTirMonstre.size(); j++){
				Fleche fleche= tabFleche.get(i);
				if(!fleche.encochee && !(fleche.isPlanted &&(fleche.tempsDetruit>0)) && !fleche.needDestroy)
				{
					TirMonstre tirM= tabTirMonstre.get(j);

					if(!tirM.needDestroy && this.deplace.colli.collisionObjects(this, deplace, fleche, tirM))
					{
						fleche.handleObjectCollision(this, deplace);
						tirM.handleObjectCollision(this, deplace);
						//bruit de collision tir/tir 
						(new MusicBruitage("annulation tir")).startBruitage(100);
					}
				}
			}
		//gestion fleche/monstre 
		for(int i=0; i<tabFleche.size(); i++)
			for(int j=0; j<tabMonstre.size(); j++)
			{
				Fleche fleche= tabFleche.get(i);
				if(!fleche.encochee && !(fleche.isPlanted && (fleche.tempsDetruit>0)) && !fleche.needDestroy)
				{
					Monstre monstre = tabMonstre.get(j);

					if(!monstre.needDestroy && deplace.colli.collisionObjects(this, deplace, fleche, monstre))
					{
						tabMonstre.get(j).addLife(tabFleche.get(i).degat);

						fleche.handleObjectCollision(this, deplace);
						monstre.handleObjectCollision(this, deplace);

					}
				}
			}

		//Update nombre monstre restant
		nombreMonstreRestant=tabMonstre.size();

		//gestion tirMonstre/heros
		for(int j=0; j<tabTirMonstre.size(); j++)
		{
			TirMonstre tirM= tabTirMonstre.get(j);

			if(!tirM.needDestroy && !heros.needDestroy && deplace.colli.collisionObjects(this, deplace, heros, tirM))
			{
				if(!heros.invincible)
					heros.touche(tabTirMonstre.get(j).dommage);

				heros.handleObjectCollision(this, deplace);
				tirM.handleObjectCollision(this, deplace);					
			}
		}
	}

	void deleteObject(List<Integer> indexList, List<Collidable> objectList )
	{
		Collections.sort(indexList);

		for(int i=0; i<indexList.size(); i++)
		{
			objectList.get(indexList.get(i)-i).destroy();
			objectList.remove(indexList.get(i)-i);
		}
	}


	/**
	 * Initialise les monstres dans le niveau
	 * 
	 * @param nombre, nombre de monstre � faire spawner al�atoirement
	 * @param typeDeSpawn, SPAWN_ALEATOIRE: spawn dans le rectangle d�fini par l'�diteur, SPAWN_PROGRAMME: spawn la ou les mosntres sont plac�s
	 */	
	void spawnMonster(int nombre, int typeDeSpawn)
	{

		if(typeDeSpawn==InterfaceConstantes.SPAWN_PROGRAMME)
		{
			nombreMonstreRestant=monde.listMonstreOriginal.size();
			tabMonstre.clear();
			for(int i=0; i<monde.listMonstreOriginal.size(); i++ )
			{
				//xpos= pos- position spawn original 
				monde.listMonstreOriginal.get(i).pos.x-=INIT_RECT.x;
				monde.listMonstreOriginal.get(i).pos.y-=INIT_RECT.y;

				//on convertit les monstres stock�s en monstres 
				if(monde.listMonstreOriginal.get(i).nom.equals("spirel"))
					tabMonstre.add(new Spirel(monde.listMonstreOriginal.get(i).pos.x,
							monde.listMonstreOriginal.get(i).pos.y,monde.listMonstreOriginal.get(i).immobile));
			}
			//on optimise la memoire
			monde.listMonstreOriginal.clear();
		}
		else if(typeDeSpawn==InterfaceConstantes.SPAWN_ALEATOIRE)
		{
			int x=0;
			int y=0;
			boolean correct= true;
			Spirel spirel= new Spirel(0, 0,false);

			for (int i=0; i< nombre; i++ )
			{

				do
				{
					correct=true;
					//coordon�es al�atoire pour placer le monstre
					x=(int) (Math.random()*(monde.xEndMap-monde.xStartMap));
					y=(int) (Math.random()*(monde.yEndMap-monde.yStartMap));

					x= monde.xStartMap- INIT_RECT.x +x;
					y= monde.yStartMap- INIT_RECT.y +y;
					//on cr�er le monstre � faire apparaitre
					spirel = new Spirel(x,y,false);
					Collision colli = new Collision();
					correct = !colli.isWorldCollision(this, deplace, spirel);
				}
				while (!correct); //on attend d'avoir une position correct avant de placer le monstre 

				//on place le monstre
				tabMonstre.add(new Spirel(x,y,false));

			}
		}
	}

	//INPUTS 
	public void keyDownAction () 
	{
		if(!finPartie)
		{
			if(pauseDown )
			{
				AbstractModelPrincipal.changeFrame=true;
				inPause=!inPause;
				pauseDown=false;
			}
			if(!inPause )
			{
				//TIR 
				if(toucheTirDown && !flecheEncochee)
				{
					//on ne tir qu'une fleche
					toucheTirDown=false;

					flecheEncochee=true;
					Fleche fleche= new Fleche();
					fleche.nulle=false;
					fleche.encochee=true;
					tabFleche.add(fleche);
				}

				//COURSE DROITE
				if(courseDroiteDown && !(heros.deplacement.IsDeplacement(Mouvement_perso.glissade))&& !flecheEncochee)
				{
					changeMouv=true;
					//si on ne courrait pas vers la droite avant
					if(! (heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim>=4))
					{
						heros.nouvAnim= 4; 
						heros.nouvMouv= new Course(); 
					}
					else //on courrait deja avant , il suffit juste de changer de sprite et de le decaller 
					{
						heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche"? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						heros.nouvMouv= heros.deplacement; 
					}
				}

				//MARCHE DROITE 
				else if(marcheDroiteDown&& !flecheEncochee)
				{
					changeMouv=true;

					if(heros.deplacement.IsDeplacement(Mouvement_perso.glissade)&& heros.anim==1)
					{
						changeMouv=true;

						heros.nouvAnim= (heros.vit.y>=0 ? 4 : 3); 
						heros.nouvMouv= new Saut(); 
					}
					else if (heros.deplacement.IsDeplacement(Mouvement_perso.glissade))
					{
						//on change rien 				
						heros.nouvMouv= heros.deplacement;
						heros.nouvAnim=heros.anim;
					}
					//si on courrait vers la droite en l'air ou non 
					else if((heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim>=4))
					{		
						heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						heros.nouvMouv= heros.deplacement; 
					}

					//si on ne marchait pas vers la droite et qu'on est pas en l'air 
					else if(! (heros.deplacement.IsDeplacement(Mouvement_perso.marche) && heros.anim>=4)&& heros.peutSauter)
					{
						heros.nouvAnim= 4; 
						heros.nouvMouv=new Marche();
					}

					//si on veut marcher en l'air (donc vers la droite) 
					else if (!heros.peutSauter) 
					{
						heros.deplaceSautDroit=true; // on fait bouger le heros
						boolean fall = heros.vit.y >=0 ;
						heros.nouvAnim= fall? 4 : 3 ; 
						heros.nouvMouv=new Saut();
					}
					else if(heros.peutSauter)// si le heros est au sol et veux continuer � marcher vers la droite
					{
						heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						heros.nouvMouv= heros.deplacement; 
					}
					else
					{
						try {throw new Exception("Unhandled input right in KeyDownAction ");} catch (Exception e) {e.printStackTrace();}
					}
				}
				//COURSE GAUCHE
				if(courseGaucheDown && !(heros.deplacement.IsDeplacement(Mouvement_perso.glissade))&& !flecheEncochee)
				{
					changeMouv=true;
					//si on ne courrait pas vers la gauche avant 
					if(! (heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim<4))
					{

						heros.nouvAnim=0;
						heros.nouvMouv= new Course();
					}
					else // si on courrait vers la gauche avant 
					{

						heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						heros.nouvMouv= heros.deplacement; 
					}
				}
				//MARCHE GAUCHE 
				else if(marcheGaucheDown&& !flecheEncochee )
				{
					changeMouv=true;

					if(heros.deplacement.IsDeplacement(Mouvement_perso.glissade)&& heros.anim==0)
					{
						changeMouv=true;

						heros.nouvAnim= (heros.vit.y>=0 ? 1 : 0); 
						heros.nouvMouv= new Saut(); 
					}
					else if (heros.deplacement.IsDeplacement(Mouvement_perso.glissade))
					{
						//on change rien 
						heros.nouvMouv=  heros.deplacement;
						heros.nouvAnim= heros.anim;
					}

					//si on courrait vers la gauche en l'air ou non 
					else if((heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim<4))
					{

						heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						heros.nouvMouv= heros.deplacement; 
					}

					//si on ne marchait pas vers la gauche et qu'on est pas en l'air 
					else if(! (heros.deplacement.IsDeplacement(Mouvement_perso.marche) && heros.anim<4)&& heros.peutSauter)
					{
						heros.nouvAnim=0;
						heros.nouvMouv= new Marche();
					}

					//si on veut marcher en l'air (donc vers la gauche) 
					else if (!heros.peutSauter)
					{
						heros.deplaceSautGauche=true; // on fait bouger le herosnnage
						boolean fall = heros.vit.y >=0 ;
						heros.nouvAnim=fall? 1 : 0 ; 
						heros.nouvMouv=new Saut(); 
					}
					else if(heros.peutSauter) // si le heros est au sol et veux continuer � marcher vers la gauche
					{
						heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche"? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						heros.nouvMouv= heros.deplacement; 
					}
					else
					{
						try {throw new Exception("Unhandled input left in KeyDownAction ");} catch (Exception e) {e.printStackTrace();}
					}

				}

				//SLOW DOWN 

				if(toucheSlowDown)
				{
					slowCount=0;
					slowDown= ! slowDown;
					toucheSlowDown=false;

					Music music = new Music();
					try {
						if(slowDown)
							music.slowDownMusic();
						else
							music.endSlowDownMusic();
					} catch (UnsupportedAudioFileException | IOException
							| LineUnavailableException e) {
						e.printStackTrace();
					}
				}

				//SAUT 
				//si le herosnnage saute pour la premi�re fois et qu'il peut sauter et qu'il ne glisse pas
				if(sautDown && heros.peutSauter && !(heros.deplacement.IsDeplacement(Mouvement_perso.glissade))&& !flecheEncochee)
				{
					courseDroiteDown=false;
					courseGaucheDown=false;

					changeMouv=true;

					heros.peutSauter=false;


					//le heros saute donc finSaut est faux
					heros.debutSaut=true;
					heros.finSaut=false;

					heros.nouvAnim=heros.droite_gauche(heros.anim)=="Gauche" ? 0 : 3 ;
					heros.nouvMouv= new Saut();


				}
				//on glisse et on veut sauter 
				else if(sautDown && heros.deplacement.IsDeplacement(Mouvement_perso.glissade)) 
				{
					changeMouv=true;	
					heros.sautGlisse=true;

					heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche"? 0 : 3);
					heros.nouvMouv=new Saut();

				}

				//touches pour lesquels maintenir appuy� ne change rien
				sautDown =false;
				toucheTirDown =false;
			}
		}
	}

	public void keyReleasedAction ()
	{
		if(pauseReleased)
		{
			pauseReleased=false;
		}
		//on arrete de deplacer le herosnnage qui saute: 
		//TIR 
		if(toucheTirReleased)
		{
			toucheTirDown=false;
			toucheTirReleased=false;

			if(flecheEncochee)
			{
				flecheEncochee=false;
				changeMouv=true;

				heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? 0 : 1) ;
				heros.nouvMouv= new Attente();

				tabFleche.get(tabFleche.size()-1).flecheDecochee(deplace);
			}

		}

		//MARCHE
		if ((marcheDroiteReleased||marcheGaucheReleased
				||courseDroiteReleased||courseGaucheReleased))
		{
			if(marcheDroiteReleased)
			{
				marcheDroiteDown=false;marcheDroiteReleased=false;heros.runBeforeJump=false;
			}
			if(courseDroiteReleased)
			{
				courseDroiteDown=false;courseDroiteReleased=false;heros.runBeforeJump=false;
			}
			if(marcheGaucheReleased)
			{
				marcheGaucheDown=false;marcheGaucheReleased=false;heros.runBeforeJump=false;
			}
			if(courseGaucheReleased)
			{
				courseGaucheDown=false;courseGaucheReleased=false;heros.runBeforeJump=false;
			}


			if( !heros.deplacement.IsDeplacement(Mouvement_perso.glissade) && !flecheEncochee )
			{
				changeMouv=true;

				//pas de decallage de sprite 

				//au sol
				if((heros.deplacement.IsDeplacement(Mouvement_perso.marche)|| heros.deplacement.IsDeplacement(Mouvement_perso.course)) && heros.peutSauter)
				{
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Droite" ? 1: 0 );
					heros.nouvMouv= new Attente();

					//on met sa vitesse � 0:  
					heros.vit.x=0;

				}

				else if (heros.deplacement.IsDeplacement(Mouvement_perso.attente))
				{
					//on arrete quand meme le herosnnage (exemple si il relache la touche de deplacement sur laquelle il avait appuy� en l'air)
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= heros.droite_gauche(heros.anim)=="Droite" ? 1: 0 ;
					heros.nouvMouv= new Attente();

					//on met sa vitesse � 0:  
					heros.vit.x=0;
				}
				//en l'air et glisse pas
				else if(!heros.peutSauter)
				{

					heros.vit.x=0;
					changeMouv=true;
					// tout d�pend si le heros tombe ou non 

					if (heros.vit.y<0)//il ne tombe pas donc on met les premi�res animations de saut
						heros.nouvAnim= heros.droite_gauche(heros.anim)=="Gauche" ? 0: 3 ;

					else // le heros tombe 
						heros.nouvAnim=heros.droite_gauche(heros.anim)=="Gauche" ? 1: 4 ;

					heros.nouvMouv=new Saut();

				}
			}
		}
		//SLOW DOWN
		if(toucheSlowReleased)
		{
			toucheSlowReleased=false;
		}
		//SAUT 
		if(sautReleased&& !flecheEncochee )
		{
			sautDown=false;
			sautReleased=false;
		}
	}

	public void keyAction()
	{
		keyDownAction();
		keyReleasedAction();
	}

	public void HandlePressedInput(int input) {

		if( input==Touches.t_pause)
		{
			pauseDown=true;
		}
		if(input==Touches.t_tir)
		{
			toucheTirDown=true;
		}
		if(input==Touches.t_slow)
		{
			toucheSlowDown=true;
		}
		if(input==Touches.t_saut)
		{
			sautDown=true;
		}
		if(input==Touches.t_droite || input==Touches.t_gauche )
		{
			clickTime2 = System.nanoTime();
			float delta = (float) ((clickTime2-clickTime1)*Math.pow(10, -6));
			boolean dash = delta<InterfaceConstantes.TDash ; // no tmin dash 

			if(dash && clickRight==true && input==Touches.t_droite)
			{
				courseDroiteDown=true;
				marcheDroiteDown=true;
				clickRight=true;
				clickLeft=false;
			}
			else if(dash && clickLeft==true && input==Touches.t_gauche)
			{
				courseGaucheDown=true;
				marcheGaucheDown=true;
				clickLeft=true;
				clickRight=false;
			}
			else
			{
				if(input==Touches.t_droite)
				{
					//deplacement normal
					marcheDroiteDown=true;
					clickRight=true;
					clickLeft=false;
				}
				if(input==Touches.t_gauche)
				{
					//deplacement normal
					marcheGaucheDown=true;
					clickLeft=true;
					clickRight=false;
				}
			}
		}
	}

	public void HandleReleasedInput(int input) {
		if(input==Touches.t_pause)
		{
			pauseDown=false;
			pauseReleased=true;
		}
		if(input==Touches.t_tir)
		{
			toucheTirDown=false;
			toucheTirReleased=true;
		}
		if(input==Touches.t_slow)
		{
			toucheSlowDown=false;
			toucheSlowReleased=true;
		}
		if (input==Touches.t_droite)
		{
			heros.deplaceSautDroit=false;

			marcheDroiteDown=false;
			marcheDroiteReleased=true;

			courseDroiteDown=false;
			courseDroiteReleased=true;
			//on retient le temps de relachement de la touche
			clickTime1 = System.nanoTime();
		}
		if(input==Touches.t_gauche)
		{
			heros.deplaceSautGauche=false;

			marcheGaucheDown=false;
			marcheGaucheReleased=true;

			courseGaucheDown=false;
			courseGaucheReleased=true;
			//on retient le temps de relachement de la touche
			clickTime1 = System.nanoTime();
		}
	}

	public void HandleBoutonsPressed(JButton button) {
		if(button.getText().equals("Option"))
		{
			setAffichageOption=true;
			notifyObserver();

		}
		else if(button.getText().equals("Quitter"))
		{
			AbstractModelPrincipal.changeFrame=true; 
			AbstractModelPrincipal.modeSuivant="Quitter";

			if(inPause)
			{
				//on arrete la partie pour sortir du Thread d'affichage de partie rapide
				finPartie=true;
				//on choisit de ne pas afficher la fin de la partie(elle n'est en r�alit� pas finie
				AbstractModelPrincipal.changeFrame=false;
			}
			AbstractModelPrincipal.changeMode=true; 
		}
		else if(button.getText().equals("Rejouer"))
		{
			AbstractModelPrincipal.modeSuivant="Partie";
			finPartie=false;
			AbstractModelPrincipal.changeFrame=true;
			AbstractModelPrincipal.changeMode=true; 

			disableBoutonsFin=true;
			notifyObserver();

		}
		else if(button.getText().equals("Menu Principal"))
		{
			AbstractModelPrincipal.modeSuivant="Principal";
			AbstractModelPrincipal.changeFrame=true;
			finPartie=true;
			AbstractModelPrincipal.changeMode=true; 

			disableBoutonsFin=true;
			notifyObserver();

		}

		else if(button.getText().equals("Reprendre"))
		{
			inPause=false;
			AbstractModelPrincipal.changeFrame=true;
		}
	}

	/**
	 * Permet de reinitialiser tout les appuis de touches, utile notamment lors de la perte de focus de la fenetre
	 */	
	public void resetTouchesFocus()
	{

		if (marcheDroiteDown) { marcheDroiteReleased=true;};
		if (marcheGaucheDown) { marcheGaucheReleased=true;};
		if (sautDown) { sautReleased=true;};
		if (toucheTirDown) { toucheTirReleased=true;};
		if (courseDroiteDown) {courseDroiteReleased=true;};
		if (courseGaucheDown) { courseGaucheReleased=true;};
		if (toucheSlowDown) { toucheSlowReleased=true;};
		if (pauseDown) { pauseReleased=true;};

		marcheDroiteDown= false;
		marcheGaucheDown = false;
		sautDown = false;
		toucheTirDown = false;
		courseDroiteDown= false;
		courseGaucheDown= false;
		toucheSlowDown= false;
		pauseDown= false;
	}

	//DRAW
	public void drawPartie(Graphics g,JPanel pan) {

		pan.setOpaque(false);

		drawMonde(g,false);
		drawMonstres(g,false);
		drawPerso(g,true);
		drawFleches(g,false);
		drawTirMonstres(g,false);
		drawEffects(g,pan,false);
		drawInterface(g);

	}

	public void drawMonde(Graphics g,boolean drawHitbox) 
	{
		int xStartAff = absRect/100-2;
		int xEndAff = (InterfaceConstantes.LARGEUR_FENETRE/100+absRect/100)+2;

		int yStartAff = ordRect/100-2;
		int yEndAff = (InterfaceConstantes.HAUTEUR_FENETRE/100+ordRect/100)+2;

		for(int abs=xStartAff;abs<xEndAff;abs++)
			for(int ord=yStartAff;ord<yEndAff;ord++)
			{
				Bloc tempPict= monde.niveau[abs][ord];
				if(tempPict != null)
				{
					int xDraw=tempPict.getXpos()-absRect+xdeplaceEcran;
					int yDraw=tempPict.getYpos()-ordRect+ydeplaceEcran;
					g.drawImage(m.getImage(tempPict,false),xDraw,yDraw, null);

					if(drawHitbox && tempPict.getBloquer())
					{
						drawHitbox(g,xDraw, yDraw, InterfaceConstantes.TAILLE_BLOC-1, InterfaceConstantes.TAILLE_BLOC-1);
						g.setColor(Color.BLACK);
						g.setFont(new Font(g.getFont().getFontName(),g.getFont().getStyle(), 20 ));
						g.drawString("("+tempPict.getXpos()/TAILLE_BLOC+","+tempPict.getYpos()/TAILLE_BLOC+")", xDraw, yDraw+TAILLE_BLOC/2);
						g.setColor(Color.RED);
					}
				}
			}
	}

	public void drawMonstres(Graphics g,boolean drawHitbox) {

		for(Monstre m : tabMonstre )
		{
			int xDraw= m.xpos+xdeplaceEcran+xdeplaceEcranBloc;
			int yDraw= m.ypos+ydeplaceEcran+ydeplaceEcranBloc;

			g.drawImage(imMonstre.getImage(m), xDraw ,yDraw ,null);

			if(drawHitbox)
			{
				Hitbox hitbox= m.getWorldHitbox(this);
				drawPolygon(g,hitbox.polygon);
			}
		}

	}

	public void drawPerso(Graphics g,boolean drawHitbox) {

		if(heros.afficheTouche)
		{
			g.drawImage(imHeros.getImages(heros), heros.xpos ,heros.ypos,null);
		}

		if(drawHitbox)
		{
			Hitbox hitbox= heros.getHitbox(INIT_RECT);
			drawPolygon(g,hitbox.polygon);
		}

	}

	public void drawFleches(Graphics g,boolean drawHitbox) {
		//Affichage des fl�ches
		if(tabFleche == null)
		{
			return ;	
		}
		try{
			for(int i=0;i<tabFleche.size();++i) 
			{
				Fleche fleche = tabFleche.get(i);
				int xdraw=  fleche.xpos+xdeplaceEcran+xdeplaceEcranBloc;
				int ydraw= fleche.ypos+ydeplaceEcran+ydeplaceEcranBloc;
				g.drawImage(defaultFleche.getImage(fleche),xdraw ,ydraw,null);
				if(drawHitbox)
				{
					Hitbox hitbox= fleche.getWorldHitbox(this);
					drawPolygon(g,hitbox.polygon);
				}
			}
		}
		catch(ConcurrentModificationException e1)
		{
			e1.printStackTrace();
		}



	}

	public void drawTirMonstres(Graphics g,boolean drawHitbox) {
		//Affichage des tirs de monstres 
		try{
			for(int i=0; i<tabTirMonstre.size();++i)
			{
				TirMonstre tir= tabTirMonstre.get(i);
				int xdraw=  tir.xpos +xdeplaceEcran+xdeplaceEcranBloc;
				int ydraw= tir.ypos +ydeplaceEcran+ydeplaceEcranBloc;
				g.drawImage(imTirMonstre.getImage(tir),xdraw,ydraw,null);

				if(drawHitbox)
				{
					Hitbox hitbox= tir.getWorldHitbox(this);
					drawPolygon(g,hitbox.polygon);
				}

			}
		}
		catch(ConcurrentModificationException e1)
		{
			e1.printStackTrace();
		}

	}

	public void drawEffects(Graphics g, JPanel pan,boolean drawHitbox) {
		//Affichage du slow motion 
		if(slowDown )
		{
			//la taille de l'image fait 1500
			Image im = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/slowDownFX.png"));
			g.drawImage(im, (InterfaceConstantes.LARGEUR_FENETRE-im.getWidth(null))/2 ,(InterfaceConstantes.HAUTEUR_FENETRE-im.getHeight(null))/2,null);
		}	
	}
	@Override
	public void drawInterface(Graphics g) {
		//AFFICHAGE DE L'INTERFACE 
		//life
		drawBar(g,10,10,InterfaceConstantes.MAXLIFE,20,heros.getLife(),Color.BLACK,Color.GREEN);

		//spe
		drawBar(g,10,40,InterfaceConstantes.MAXSPE,20,heros.getSpe(),Color.BLACK,Color.BLUE);

		//affichage de la vie des monstres 
		for(Monstre m : tabMonstre )
		{
			int xG= m.xpos+xdeplaceEcran+xdeplaceEcranBloc;
			int xD= xG +m.deplacement.xtaille.get(m.anim);
			int yH= m.ypos+ydeplaceEcran+ydeplaceEcranBloc;

			drawBar(g,xG/2+xD/2-InterfaceConstantes.MAXLIFE/2,yH-10,InterfaceConstantes.MAXLIFE,5,m.getLife(),Color.BLACK,Color.GREEN);
		}
		//nombre de monstre restant
		g.setColor(Color.BLACK);
		g.setFont(new Font(g.getFont().getFontName(),g.getFont().getStyle(), 20 ));
		g.drawString("Nombre monstres restant: "+ nombreMonstreRestant, 10, 80);

		//jeu en pause 
		if(inPause)
		{
			g.setColor(Color.BLACK);
			g.setFont(new Font(g.getFont().getFontName(),g.getFont().getStyle(), 20 ));
			g.drawString("Pause", 1250, 40);
			g.setColor(new Color(0,0,0,125));
			g.fillRect(0, 0, InterfaceConstantes.LARGEUR_FENETRE, InterfaceConstantes.HAUTEUR_FENETRE);
		}

		if(finPartie)
		{
			if(heros.getLife()==InterfaceConstantes.MINLIFE)
			{
				g.setColor(Color.BLACK);
				g.setFont(new Font(g.getFont().getFontName(),g.getFont().getStyle(), 40 ));
				g.drawString("DEFAITE",InterfaceConstantes.LARGEUR_FENETRE/2-20, InterfaceConstantes.HAUTEUR_FENETRE/4);

			}
			else if(nombreMonstreRestant==0)
			{
				g.setColor(Color.BLACK);
				g.setFont(new Font(g.getFont().getFontName(),g.getFont().getStyle(), 40 ));
				g.drawString("VICTOIRE",InterfaceConstantes.LARGEUR_FENETRE/2-20, InterfaceConstantes.HAUTEUR_FENETRE/4);

			}
		}

	}

	public void drawHitbox(Graphics g, int xdraw, int ydraw, int width, int height) {
		drawHitbox(g,xdraw, ydraw, width, height,null,null);
	}
	public void drawHitbox(Graphics g, int xdraw, int ydraw, int width, int height,Integer angle) {
		drawHitbox(g,xdraw, ydraw, width, height,angle,null);
	}
	public void drawHitbox(Graphics g, int xdraw, int ydraw, int width, int height, Integer angle, Point origine) {
		g.setColor(Color.red);
		Graphics2D g2d = (Graphics2D)g;
		int xt=0;
		int yt=0;

		double radAngle=0 ;
		if(angle != null)
			radAngle= Math.toRadians(angle);

		if(origine!=null)
		{
			int xCenter= 0;
			int yCenter= 0;
			xt = origine.x-xCenter;
			yt = origine.y-yCenter;
		}

		g2d.translate(xt,yt);

		if(angle != null)
			g2d.rotate(radAngle);

		g2d.translate(-xt, -yt);

		g2d.drawRect(xdraw, ydraw, width, height);
		//reset
		g2d.translate(xt, yt);
		g2d.rotate(-radAngle);
		g2d.translate(-xt,-yt);
	}

	public void drawPolygon(Graphics g, Polygon p)
	{
		g.drawPolygon(p);
	}
	public void drawBar(Graphics g,int x, int y, int width, int height, int value ,Color background, Color foreground)
	{
		g.setColor(background);
		g.fillRect(x, y, width, height);
		g.setColor(foreground);
		g.fillRect(x, y, value, height);
	}


}
