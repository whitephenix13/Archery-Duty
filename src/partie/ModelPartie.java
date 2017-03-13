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
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JPanel;

import Affichage.Affichage;
import collision.Collidable;
import collision.Collision;
import deplacement.Accroche;
import deplacement.Attente;
import deplacement.Course;
import deplacement.Marche;
import deplacement.Mouvement_perso;
import deplacement.Saut;
import deplacement.Tir;
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
import types.TypeObject;

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

		//int x= heros.xPos + heros.deplacement.xdecallsprite[heros.anim]; //la vrai position du heros necessite encore un - variablesPartieRapide.xdeplaceEcran
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
						(m.xpos+xScreendisp,m.ypos+yScreendisp));
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
		computationDone=true;
		//else "pause"

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
							monde.listMonstreOriginal.get(i).pos.y,monde.listMonstreOriginal.get(i).immobile,frame));
			}
			//on optimise la memoire
			monde.listMonstreOriginal.clear();
		}
		else if(typeDeSpawn==InterfaceConstantes.SPAWN_ALEATOIRE)
		{
			int x=0;
			int y=0;
			boolean correct= true;
			Spirel spirel= new Spirel(0, 0,false,frame);

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
					spirel = new Spirel(x,y,false,frame);
					Collision colli = new Collision();
					correct = !colli.isWorldCollision(this, deplace, spirel,false);
				}
				while (!correct); //on attend d'avoir une position correct avant de placer le monstre 

				//on place le monstre
				tabMonstre.add(new Spirel(x,y,false,frame));

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
				if(toucheTirDown && !heros.flecheEncochee && !heros.doitEncocherFleche)
				{
					changeMouv=true;
					//on ne tir qu'une fleche
					toucheTirDown=false;
					heros.doitEncocherFleche=true;
					heros.nouvMouv= new Tir(TypeObject.heros,Tir.tir,frame); 
				}
				boolean heros_shoots = heros.flecheEncochee||heros.doitEncocherFleche;
				boolean heros_accroche = heros.deplacement.IsDeplacement(Mouvement_perso.accroche);
				boolean heros_glisse = heros.deplacement.IsDeplacement(Mouvement_perso.glissade);
				//COURSE DROITE
				if(courseDroiteDown && !heros_glisse && !heros_accroche && !heros_shoots)
				{
					//si on ne courrait pas vers la droite avant
					if(! (heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim>=4))
					{
						changeMouv=true;
						heros.nouvAnim= 4; 
						heros.nouvMouv= new Course(TypeObject.heros,Course.course_droite,frame); 
					}
				}

				//MARCHE DROITE 
				else if(marcheDroiteDown&& !heros_shoots)
				{
					if(heros_glisse)
					{
						if(heros.anim == 1)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.vit.y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(TypeObject.heros,heros.vit.y>=0? Saut.fall_droite:Saut.jump_droite,frame); 
						}
					}
					else if(heros_accroche)
					{
						//leave the border

						if(heros.anim == 0)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.vit.y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(TypeObject.heros,heros.vit.y>=0? Saut.fall_droite:Saut.jump_droite,frame); 
						}
						//climb the border
						else if (heros.anim == 2 && heros.deplacement.animEndedOnce())
						{
							changeMouv=true;

							heros.nouvAnim= 3; 
							heros.nouvMouv= new Accroche(TypeObject.heros,Accroche.grimpe_droite,frame); 						
						}
					}
					//si on courrait vers la droite en l'air ou non 
					else if((heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim>=4))
					{		
						//no change mouv
					}

					//si on ne marchait pas vers la droite et qu'on est pas en l'air 
					else if(! (heros.deplacement.IsDeplacement(Mouvement_perso.marche) && heros.anim>=4)&& heros.peutSauter)
					{
						changeMouv=true;
						heros.nouvAnim= 4; 
						heros.nouvMouv=new Marche(TypeObject.heros,Marche.marche_droite,frame);
					}

					//si on veut marcher en l'air (donc vers la droite) 
					else if (!heros.peutSauter) 
					{
						if(heros.deplacement.IsDeplacement(Mouvement_perso.saut) && (heros.deplacement.type_mouv != Saut.land_gauche ) && (heros.deplacement.type_mouv != Saut.land_droite ))
						{
							changeMouv=true;
							heros.deplaceSautDroit=true; // on fait bouger le heros
							boolean fall = heros.vit.y >=0 ;
							heros.nouvAnim= fall? 4 : 3 ; 
							heros.nouvMouv=new Saut(TypeObject.heros,fall?Saut.fall_droite:Saut.jump_droite,frame);
						}
					}
					else if(heros.peutSauter)// si le heros est au sol et veux continuer � marcher vers la droite
					{
						//no change mouv
					}
					else
					{
						try {throw new Exception("Unhandled input right in KeyDownAction ");} catch (Exception e) {e.printStackTrace();}
					}
				}
				//COURSE GAUCHE
				if(courseGaucheDown && !heros_glisse && !heros_accroche && !heros_shoots)
				{
					//si on ne courrait pas vers la gauche avant 
					if(! (heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim<4))
					{
						changeMouv=true;
						heros.nouvAnim=0;
						heros.nouvMouv= new Course(TypeObject.heros,Course.course_gauche,frame);
					}
				}
				//MARCHE GAUCHE 
				else if(marcheGaucheDown&& !heros_shoots )
				{

					if(heros_glisse)
					{
						if(heros.anim==0){
							changeMouv=true;

							heros.nouvAnim= (heros.vit.y>=0 ? 1 : 0); 
							heros.nouvMouv= new Saut(TypeObject.heros,heros.vit.y>=0? Saut.fall_gauche:Saut.jump_gauche,frame); 
						}
					}
					else if (heros_accroche)
					{
						//leave the border

						if(heros.anim == 2)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.vit.y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(TypeObject.heros,heros.vit.y>=0? Saut.fall_gauche:Saut.jump_gauche,frame); 
						}
						//climb the border
						else if (heros.anim == 0 && heros.deplacement.animEndedOnce())
						{
							changeMouv=true;

							heros.nouvAnim= 1; 
							heros.nouvMouv= new Accroche(TypeObject.heros,Accroche.grimpe_gauche,frame); 						
						}
					}

					//si on courrait vers la gauche en l'air ou non 
					else if((heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim<4))
					{
						//no change
					}

					//si on ne marchait pas vers la gauche et qu'on est pas en l'air 
					else if(! (heros.deplacement.IsDeplacement(Mouvement_perso.marche) && heros.anim<4)&& heros.peutSauter)
					{
						changeMouv=true;

						heros.nouvAnim=0;
						heros.nouvMouv= new Marche(TypeObject.heros,Marche.marche_gauche,frame);
					}

					//si on veut marcher en l'air (donc vers la gauche) 
					else if (!heros.peutSauter)
					{
						changeMouv=true;

						heros.deplaceSautGauche=true; // on fait bouger le heros
						boolean fall = heros.vit.y >=0 ;
						heros.nouvAnim=fall? 1 : 0 ; 
						heros.nouvMouv=new Saut(TypeObject.heros,fall? Saut.fall_gauche:Saut.jump_gauche,frame); 
					}
					else if(heros.peutSauter) // si le heros est au sol et veux continuer � marcher vers la gauche
					{
						//no change
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
				//si le heros saute pour la premi�re fois et qu'il peut sauter et qu'il ne glisse pas
				if(sautDown &&  !heros_shoots)
				{
					if(heros_glisse)
					{
						changeMouv=true;	
						heros.sautGlisse=true;

						heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche"? 0 : 3);
						heros.nouvMouv=new Saut(TypeObject.heros,heros.nouvAnim==0?Saut.jump_gauche:Saut.jump_droite,frame );
					}
					else if(heros_accroche && ( (heros.anim==0) || (heros.anim==2)))
					{
						changeMouv=true;	
						heros.sautAccroche=true;
						heros.useGravity=true;
						heros.nouvAnim= ((heros.anim == 0)? 0 : 3);
						heros.nouvMouv=new Saut(TypeObject.heros,heros.nouvAnim==0?Saut.jump_gauche:Saut.jump_droite,frame );
					}
					else if(heros.peutSauter){
						courseDroiteDown=false;
						courseGaucheDown=false;

						changeMouv=true;

						heros.peutSauter=false;


						//le heros saute donc finSaut est faux
						heros.debutSaut=true;
						heros.finSaut=false;

						heros.nouvAnim=heros.droite_gauche(heros.anim)=="Gauche" ? 0 : 3 ;
						heros.nouvMouv= new Saut(TypeObject.heros,heros.nouvAnim==0?Saut.jump_gauche:Saut.jump_droite,frame );
					}
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
		//on arrete de deplacer le heros qui saute: 
		//TIR 
		if(toucheTirReleased)
		{
			toucheTirDown=false;
			toucheTirReleased=false;

			if(heros.flecheEncochee)
			{
				heros.flecheEncochee=false;
				changeMouv=true;

				heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? 0 : 2) ;
				heros.nouvMouv= new Attente(TypeObject.heros,heros.nouvAnim==0? Attente.attente_gauche:Attente.attente_droite,frame);
				tabFleche.get(tabFleche.size()-1).flecheDecochee(this,deplace);
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

			boolean heros_glisse = heros.deplacement.IsDeplacement(Mouvement_perso.glissade);
			boolean heros_accroche = heros.deplacement.IsDeplacement(Mouvement_perso.accroche);

			if( !heros_glisse && !heros_accroche && !heros.flecheEncochee )
			{
				changeMouv=true;

				//pas de decallage de sprite 

				//au sol
				if((heros.deplacement.IsDeplacement(Mouvement_perso.marche)|| heros.deplacement.IsDeplacement(Mouvement_perso.course)) && heros.peutSauter)
				{
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= (heros.droite_gauche(heros.anim)=="Droite" ? 2: 0 );
					heros.nouvMouv= new Attente(TypeObject.heros,heros.nouvAnim==0? Attente.attente_gauche:Attente.attente_droite,frame);

					//on met sa vitesse � 0:  
					heros.vit.x=0;

				}

				else if (heros.deplacement.IsDeplacement(Mouvement_perso.attente))
				{
					//on arrete quand meme le heros (exemple si il relache la touche de deplacement sur laquelle il avait appuy� en l'air)
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= heros.droite_gauche(heros.anim)=="Droite" ? 2: 0 ;
					heros.nouvMouv= new Attente(TypeObject.heros,heros.nouvAnim==0? Attente.attente_gauche:Attente.attente_droite,frame);

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

					int type_mouv=heros.nouvAnim==0? Saut.jump_gauche: (heros.nouvAnim==3?Saut.jump_droite:  (heros.nouvAnim==1?Saut.fall_gauche:Saut.fall_droite));
					heros.nouvMouv=new Saut(TypeObject.heros,type_mouv,frame);

				}
			}
		}
		//SLOW DOWN
		if(toucheSlowReleased)
		{
			toucheSlowReleased=false;
		}
		//SAUT 
		if(sautReleased&& !heros.flecheEncochee )
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
		drawPerso(g,false);
		drawFleches(g,false);
		drawTirMonstres(g,false);
		drawEffects(g,pan,false);
		drawInterface(g);

	}

	public void drawMonde(Graphics g,boolean drawHitbox) 
	{
		int xviewport = getXYViewport(true);
		int yviewport = getXYViewport(false);

		int xStartAff = xviewport/TAILLE_BLOC-2;
		int xEndAff = (InterfaceConstantes.LARGEUR_FENETRE/TAILLE_BLOC+xviewport/TAILLE_BLOC)+2;

		int yStartAff = yviewport/TAILLE_BLOC-2;
		int yEndAff = (InterfaceConstantes.HAUTEUR_FENETRE/TAILLE_BLOC+yviewport/TAILLE_BLOC)+2;

		for(int abs=xStartAff;abs<xEndAff;abs++)
			for(int ord=yStartAff;ord<yEndAff;ord++)
			{
				Bloc tempPict= monde.niveau[abs][ord];
				if(tempPict != null)
				{
					int xDraw=tempPict.getXpos()+ xScreendisp- INIT_RECT.x;
					int yDraw=tempPict.getYpos()+ yScreendisp- INIT_RECT.y;
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
			int xDraw= m.xpos+xScreendisp;
			int yDraw= m.ypos+yScreendisp;

			g.drawImage(imMonstre.getImage(m), xDraw ,yDraw ,null);
			int xDraw_d= xDraw +m.deplacement.xtaille.get(m.anim);
			//draw monster lifebar
			drawBar(g,xDraw/2+xDraw_d/2-InterfaceConstantes.MAXLIFE/2,yDraw-10,InterfaceConstantes.MAXLIFE,5,m.getLife(),Color.BLACK,Color.GREEN);
			if(drawHitbox)
			{
				Hitbox hitbox= m.getWorldHitbox(this);
				drawPolygon(g,hitbox.polygon);
			}
		}

	}

	/**
	 * @param pos : position of hitbox
	 * @param anchor: position of center of rotation relative to top left of hitbox
	 * @param taille: size of hitbox
	 * @param rotation
	 * @return
	 */
	public AffineTransform getRotatedTransform(Point pos, Point anchor, Point taille, double rotation)
	{
		AffineTransform trans = new AffineTransform();
		//set to anchor point 
		//distance of anchor to actual center of image 
		Point c_anchor=new Point(pos.x+anchor.x-taille.x/2,pos.y+anchor.y-taille.y/2);//due to the fact that position is top left
		trans.translate(c_anchor.x, c_anchor.y);
		//set to rotation point  and rotate by desired angle
		trans.rotate(rotation, taille.x/2, taille.y/2);

		//set to draw position 
		Point d_pos=new Point(pos.x-c_anchor.x,pos.y-c_anchor.y);
		trans.translate(d_pos.x, d_pos.y);
		return trans;
	}
	public Point drawPersoTir(Graphics g)
	{
		int anim = heros.anim;
		Point anchor = new Point(heros.xpos+heros.deplacement.x_rot_pos.get(anim), 
				heros.ypos+heros.deplacement.y_rot_pos.get(anim));
		Graphics2D g2d = (Graphics2D)g;


		AffineTransform tr = getRotatedTransform(new Point(heros.xpos,heros.ypos), 
				new Point(heros.deplacement.x_rot_pos.get(anim),heros.deplacement.y_rot_pos.get(anim)),
				new Point(heros.deplacement.xtaille.get(anim),heros.deplacement.ytaille.get(anim)),
				heros.rotation_tir);


		ArrayList<Image> l_image = imHeros.getImages(heros);
		for(int i=0; i<l_image.size(); ++i)
		{
			//im_body, im_back, im_head, im_front
			if(i==0||i==2)
				g.drawImage(l_image.get(i), heros.xpos,heros.ypos,null);

			else
				g2d.drawImage(l_image.get(i), tr,null);

		}
		return anchor;
	}
	public void drawPerso(Graphics g,boolean drawHitbox) {

		Point anchor = null;
		if(heros.afficheTouche)
		{
			if(heros.deplacement.IsDeplacement(Mouvement_perso.tir))
				anchor=drawPersoTir(g);
			else
			{
				ArrayList<Image> l_image = imHeros.getImages(heros);
				for(int i=0; i<l_image.size(); ++i)
					g.drawImage(l_image.get(i), heros.xpos,heros.ypos,null);

			}
		}

		if(drawHitbox)
		{
			Hitbox hitbox= heros.getHitbox(INIT_RECT);
			drawPolygon(g,hitbox.polygon);
			if(anchor!=null){
				g.setColor(Color.red);
				g.drawString("o", anchor.x, anchor.y);
				g.setColor(Color.black);
			}

		}

	}


	public void drawFleches(Graphics g,boolean drawHitbox) {
		//Affichage des fl�ches
		if(tabFleche == null)
		{
			return ;	
		}
		try{
			Graphics2D g2d = (Graphics2D)g;

			for(int i=0;i<tabFleche.size();++i) 
			{
				Fleche fleche = tabFleche.get(i);
				int anim=fleche.anim;
				int hanim = heros.anim;
				Point pos = new Point();
				Point anchor = new Point(0,0); //only arrows towards its center
				Point taille = new Point(fleche.deplacement.xtaille.get(anim),fleche.deplacement.ytaille.get(anim));
				AffineTransform tr;
				if(fleche.encochee)
				{
					Point f_anchor = new Point(fleche.xanchor.get(hanim),fleche.yanchor.get(hanim));
					pos=new Point(heros.xpos+f_anchor.x,heros.ypos+f_anchor.y);
					//Anchor is relative to position: true_anchor = world anchor - mypos
					//world anchor = heros pos + heros anchor, mypos = heros  pos + fleche anchor
					anchor=new Point(heros.deplacement.x_rot_pos.get(hanim)-f_anchor.x,
							heros.deplacement.y_rot_pos.get(hanim)-f_anchor.y);
					tr = getRotatedTransform(pos,anchor, taille, fleche.rotation);

				}
				else
				{
					//Point d_pos=new Point(pos.x-c_anchor.x,pos.y-c_anchor.y);
					//trans.translate(d_pos.x, d_pos.y);
					AffineTransform tr_pos = new AffineTransform();
					tr_pos.translate(fleche.xpos+xScreendisp, fleche.ypos+yScreendisp);
					tr=new AffineTransform(fleche.draw_tr);
					double[] flatmat = new double[6];
					tr.getMatrix(flatmat);
					tr.setTransform(flatmat[0], flatmat[1], flatmat[2], flatmat[3], fleche.xpos+xScreendisp, fleche.ypos+yScreendisp);
				}
				if(fleche.encochee)
					fleche.draw_tr=tr;
				ArrayList<Image> images = imFleches.getImage(fleche);
				for(Image im : images)
					g2d.drawImage(im,tr,null);

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
				int xdraw=  tir.xpos +xScreendisp;
				int ydraw= tir.ypos +yScreendisp;
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
