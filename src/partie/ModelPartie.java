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
import collision.Collision;
import collision.IntersectHitbox;
import deplacement.Attente;
import deplacement.Course;
import deplacement.Marche;
import deplacement.Mouvement;
import deplacement.Saut;

public class ModelPartie extends AbstractModelPartie{

	/**
	 * gère l'action dans le jeu liée à l'appui d'une touche
	 */	


	public void startPartie(int typeDeSpawn,String nomPartie)
	{
		//on charge notre niveau
		charger(nomPartie);
		//on fait apparaitre les monstres 
		nombreMonstreRestant=1000;
		spawnMonster(nombreMonstreRestant,typeDeSpawn);
	}

	/**
	 * Boucle de jeu
	 * 
	 * @param affich, la JFrame a afficher
	 */	
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
			Thread.sleep(InterfaceConstantes.T);//T=1ms, permet de faire des tours de boucle à intervalles régulier 

			//int x= heros.xPos + heros.deplacement.xdecallsprite[heros.anim]; //la vrai position du herosnnage necessite encore un - variablesPartieRapide.xdeplaceEcran
			//int y= heros.yPos+ heros.deplacement.ydecallsprite[heros.anim]; 

			//on efface les qui doivent être détruit 
			effaceTout();

			//on vide les listes
			lEffaceFleche.clear();
			lEffaceTirMonstre.clear();
			lEffaceMonstre.clear();

			//on desactive la touche cap au cas ou elle serait utilisée
			Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);
			keyAction();

			//Lors d'une pause, on ne veut pas réinitaliser la partie en cours mais juste y accéder à nouveau
			if(!inPause)
			{
				//DEPLACEMENT 
				//Heros
				deplace.DeplaceHeros(heros,nouvMouv ,nouvAnim,this);
				//Monstre

				for(int i=0 ; i< tabMonstre.size(); i++)
				{

					//on ne deplace le monstre qui si il est visible
					Hitbox ecran =  new Hitbox(	InterfaceConstantes.HG_FENETRE,InterfaceConstantes.BG_FENETRE,
							InterfaceConstantes.BD_FENETRE,InterfaceConstantes.HD_FENETRE);
					if (Hitbox.isPointInRectangle(new Point (tabMonstre.get(i).xPos+xdeplaceEcran+xdeplaceEcranBloc,
							tabMonstre.get(i).yPos+ydeplaceEcran+ydeplaceEcranBloc),ecran))

						deplace.DeplaceMonstre(tabTirMonstre,tabMonstre.get(i), this);

				}

				//TIRS
				//Fleche
				for(int i=0; i<tabFleche.size(); i++)
				{
					deplace.DeplaceFleche(tabFleche.get(i), tabFleche.get(i).anim, this,heros);
				}

				//Tir Monstre deplace et a effacer
				for(int i=0 ; i< tabTirMonstre.size(); i++)
				{
					tabTirMonstre.get(i).deplaceTir(this);

					if(tabTirMonstre.get(i).doitDetruire)
					{
						lEffaceTirMonstre.add(i);
					}

				}

				//DESTRUCTION
				//tir monstre
				effaceTirMonstre();

				lEffaceTirMonstre.clear();
				//tir fleche

				for(int i=0; i<tabFleche.size(); i++)
				{

					long tempsFleche= System.nanoTime()-tabFleche.get(i).tempsDetruit;
					if(tabFleche.get(i).doitDetruire && (tempsFleche >= InterfaceConstantes.TEMPS_DESTRUCTION_FLECHE))
					{
						System.out.println("t1 "+ System.nanoTime() + " t2 "+ tabFleche.get(i).tempsDetruit + " t1-t2 "+ tempsFleche);
						lEffaceFleche.add(i);
					}
					//on déclenche le timer des fleches qui sont dans le mur 
					if(tabFleche.get(i).tempsDetruit==0)
					{
						tabFleche.get(i).timer();
					}
				}
				effaceFleche();
				lEffaceFleche.clear();


				//on gere la collision des tirs/monstre/heros
				gestionTir();


				//on met a jour le heros si il est touché avant de l'afficher
				heros.miseAjourTouche();
				heros.miseAJourSpe(this);


				//on test si la partie est finie 

				boolean finPartieAvant= finPartie;

				finPartie= (heros.getLife()==InterfaceConstantes.MINLIFE) || (nombreMonstreRestant==0);

				//on detecte la fin de la partie de la première fois : 
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
	 * @param nomFichier, le nom du niveau à charger
	 */	
	public void charger(String nomFichier)
	{
		// InputStream input = getClass().getClassLoader().getResourceAsStream("resources/Levels/"+nomFichier);
		//ois=new ObjectInputStream(new BufferedInputStream(input));

		//monde=(Monde)ois.readObject();
		//ois.close();
		monde= Serialize.charger(nomFichier);
		//le monde est initialisé, il reste à mettre à jour les coordonées de départ du heros

		INIT_RECT.x= (monde.xStartPerso-InterfaceConstantes.LARGEUR_FENETRE/2)/100*100;//49 900
		INIT_RECT.y= (monde.yStartPerso-InterfaceConstantes.HAUTEUR_FENETRE/2)/100*100;//49 700

		absRect=INIT_RECT.x;
		ordRect=INIT_RECT.y;

		heros.xpos=(InterfaceConstantes.LARGEUR_FENETRE/2+(INIT_RECT.x==monde.xStartPerso-InterfaceConstantes.LARGEUR_FENETRE/2? 0:100 ))/100*100;
		heros.ypos=(InterfaceConstantes.HAUTEUR_FENETRE/2+(INIT_RECT.y==monde.yStartPerso-InterfaceConstantes.HAUTEUR_FENETRE/2? 0:100 ))/100*100;



		//isFound=false;
		//JOptionPane.showMessageDialog(null, "Fichier inexistant" , "Echec Chargement", JOptionPane.ERROR_MESSAGE);

	}

	/**
	 * Gère les collisions de tir fleche/tirMonstre, fleche/Monstre, TirMonstre/heros
	 */	
	void gestionTir () 
	{

		//gestion fleche/TirMonstre 
		for(int i=0; i<tabFleche.size(); i++)
		{
			for(int j=0; j<tabTirMonstre.size(); j++)
			{
				if(!tabFleche.get(i).encochee && !(tabFleche.get(i).doitDetruire&&(tabFleche.get(i).tempsDetruit>0)))
				{
					int angle1= 0;
					//TODO: changement
					/*switch (tabFleche.get(i).anim)
			{
			case 1: angle1=45;break;
			case 3: angle1=-45;break;
			case 5: angle1=45;break;
			case 7: angle1=45;break;
			default: angle1=0;break;
			}*/
					Fleche fleche= tabFleche.get(i);
					//variables pour localiser la fleche
					//{{
					int xHG =fleche.xpos+ fleche.deplacement.xHdecallsprite.get(fleche.anim);
					int xHD =fleche.xpos + fleche.deplacement.xHdecallsprite.get(fleche.anim) + fleche.deplacement.xHdecall2.get(fleche.anim);
					int xBG =fleche.xpos + fleche.deplacement.xBdecallsprite.get(fleche.anim);
					int  xBD= fleche.xpos + fleche.deplacement.xBdecallsprite.get(fleche.anim) + fleche.deplacement.xBdecall2.get(fleche.anim);
					int yHG =fleche.ypos + fleche.deplacement.yHdecallsprite.get(fleche.anim);
					int yHD=fleche.ypos + fleche.deplacement.yHdecallsprite.get(fleche.anim) + fleche.deplacement.yHdecall2.get(fleche.anim);
					int  yBG =fleche.ypos + fleche.deplacement.yBdecallsprite.get(fleche.anim);
					int yBD=fleche.ypos + fleche.deplacement.yBdecallsprite.get(fleche.anim) + fleche.deplacement.yBdecall2.get(fleche.anim);

					//}}
					//variables pour localiser le TirMonstre 
					//{{
					int length= tabTirMonstre.get(j).xhitbox.get(tabTirMonstre.get(j).anim);
					int width= tabTirMonstre.get(j).yhitbox.get(tabTirMonstre.get(j).anim);

					int xM= tabTirMonstre.get(j).xpos+tabTirMonstre.get(j).xdecallsprite.get(tabTirMonstre.get(j).anim);
					int yM=tabTirMonstre.get(j).ypos+tabTirMonstre.get(j).ydecallsprite.get(tabTirMonstre.get(j).anim);



					int angle2=0;
					//}}

					Hitbox hit1 = new Hitbox(new Point(xHG,yHG),new Point(xBG,yBG),new Point(xBD,yBD),new Point(xHD,yHD));
					Hitbox hit2 = new Hitbox(new Point(xM,yM),new Point(xM,yM+width),new Point(xM+length,yM+width),new Point(xM+length,yM));

					if(IntersectHitbox.hitboxIntersect(hit1,angle1,hit2,angle2))
					{

						if(!lEffaceFleche.contains(i)&& !lEffaceTirMonstre.contains(j))
						{
							lEffaceFleche.add(i);
							lEffaceTirMonstre.add(j);
							//bruit de collision tir/tir 
							(new MusicBruitage("annulation tir")).startBruitage(100);
						}


						/*
				if(!lEffaceFleche.contains(i)){lEffaceFleche.add(i);}
				if(!lEffaceTirMonstre.contains(j)){lEffaceTirMonstre.add(j);}
				//bruit de collision tir/tir 
				 (new MusicBruitage("annulation tir")).startBruitage(100);
						 */

					}
				}
			}
		}
		//gestion fleche/monstre 
		for(int i=0; i<tabFleche.size(); i++)
		{
			for(int j=0; j<tabMonstre.size(); j++)
			{
				if(!tabFleche.get(i).encochee && !(tabFleche.get(i).doitDetruire && (tabFleche.get(i).tempsDetruit>0)))
				{
					int angle1= 0;
					//TODO: changement
					/*
			switch (tabFleche.get(i).anim)
			{
			case 1: angle1=45;break;
			case 3: angle1=-45;break;
			case 5: angle1=45;break;
			case 7: angle1=45;break;
			default: angle1=0;break;
			}*/
					Fleche fleche= tabFleche.get(i);
					//variables pour localiser la fleche
					//{{
					int xHG =fleche.xpos+ fleche.deplacement.xHdecallsprite.get(fleche.anim);
					int xHD =fleche.xpos + fleche.deplacement.xHdecallsprite.get(fleche.anim) + fleche.deplacement.xHdecall2.get(fleche.anim);
					int xBG =fleche.xpos + fleche.deplacement.xBdecallsprite.get(fleche.anim);
					int  xBD= fleche.xpos + fleche.deplacement.xBdecallsprite.get(fleche.anim) + fleche.deplacement.xBdecall2.get(fleche.anim);
					int yHG =fleche.ypos + fleche.deplacement.yHdecallsprite.get(fleche.anim);
					int yHD=fleche.ypos + fleche.deplacement.yHdecallsprite.get(fleche.anim) + fleche.deplacement.yHdecall2.get(fleche.anim);
					int  yBG =fleche.ypos + fleche.deplacement.yBdecallsprite.get(fleche.anim);
					int yBD=fleche.ypos + fleche.deplacement.yBdecallsprite.get(fleche.anim) + fleche.deplacement.yBdecall2.get(fleche.anim);

					//}}
					//variables pour localiser le monstre 
					//{{

					int length= tabMonstre.get(j).deplacement.xhitbox.get(tabMonstre.get(j).anim);
					int width= tabMonstre.get(j).deplacement.yhitbox.get(tabMonstre.get(j).anim);

					int xM= tabMonstre.get(j).xPos+tabMonstre.get(j).deplacement.xdecallsprite.get(tabMonstre.get(j).anim);
					int yM=tabMonstre.get(j).yPos+tabMonstre.get(j).deplacement.ydecallsprite.get(tabMonstre.get(j).anim);



					int angle2=0;

					//}}

					Hitbox hit1 = new Hitbox(new Point(xHG,yHG),new Point(xBG,yBG),new Point(xBD,yBD),new Point(xHD,yHD));
					Hitbox hit2 = new Hitbox(new Point(xM,yM),new Point(xM,yM+width),new Point(xM+length,yM+width),new Point(xM+length,yM));


					if(IntersectHitbox.hitboxIntersect(hit1,angle1,hit2,angle2))
					{
						if(!lEffaceFleche.contains(i)&& !lEffaceMonstre.contains(j))
						{
							lEffaceFleche.add(i);
							tabMonstre.get(j).addLife(tabFleche.get(i).degat);

							if(tabMonstre.get(j).getLife()==InterfaceConstantes.MINLIFE)
							{
								lEffaceMonstre.add(j);
							}
						}


						/*
				if(!lEffaceFleche.contains(i))
				{
					lEffaceFleche.add(i);
					tabMonstre.get(j).addLife(tabFleche.get(i).degat);
				}
				if(!lEffaceMonstre.contains(j)&& tabMonstre.get(j).getLife()==MINLIFE)
				{
					lEffaceMonstre.add(j);
				}
						 */
					}
				}
			}
		}


		//gestion tirMonstre/heros
		//variables pour localiser le heros 
		//{{

		int x= heros.xpos + heros.deplacement.xdecallsprite.get(heros.anim)-xdeplaceEcran-xdeplaceEcranBloc;
		int y= heros.ypos + heros.deplacement.ydecallsprite.get(heros.anim)-ydeplaceEcran-ydeplaceEcranBloc;
		int length = heros.deplacement.xhitbox.get(heros.anim);
		int width = heros.deplacement.yhitbox.get(heros.anim);

		int angle=0;
		//}}
		for(int j=0; j<tabTirMonstre.size(); j++)
		{
			//variables pour localiser le TirMonstre 
			//{{
			int xM= tabTirMonstre.get(j).xpos+tabTirMonstre.get(j).xdecallsprite.get(tabTirMonstre.get(j).anim);
			int yM=tabTirMonstre.get(j).ypos+tabTirMonstre.get(j).ydecallsprite.get(tabTirMonstre.get(j).anim);
			int lengthM=tabTirMonstre.get(j).xhitbox.get(tabTirMonstre.get(j).anim);
			int widthM= tabTirMonstre.get(j).yhitbox.get(tabTirMonstre.get(j).anim);
			int angle2=0;
			//}}

			Hitbox hit1 = new Hitbox(new Point(x,y),new Point(x,y+width),new Point(x+length,y+width),new Point(x+length,y));
			Hitbox hit2 = new Hitbox(new Point(xM,yM),new Point(xM,yM+width),new Point(xM+length,yM+width),new Point(xM+length,yM));


			if(IntersectHitbox.hitboxIntersect(hit1,angle,hit2,angle2))

			{
				if(!lEffaceTirMonstre.contains(j))
				{
					lEffaceTirMonstre.add(j);
				}
				if(!heros.invincible)
				{
					heros.touche(tabTirMonstre.get(j).dommage);
				}
			}

		}

	}

	//fonctions pour effacer 
	/**
	 * efface les monstres mort
	 */	
	void effaceMonstre()
	{
		Collections.sort(lEffaceMonstre);

		//efface les monstres
		for(int i=0; i<lEffaceMonstre.size(); i++)
		{

			tabMonstre.remove(lEffaceMonstre.get(i)-i);
			nombreMonstreRestant--;
			//bruit de destruction du robot
			(new MusicBruitage("destruction robot")).startBruitage(1000);

		}

	}
	/**
	 * efface les tirs à détruire
	 */	
	void effaceTirMonstre() 
	{
		Collections.sort(lEffaceTirMonstre);
		//efface les tirs de monstre
		for(int i=0; i<lEffaceTirMonstre.size(); i++)
		{
			//on execute l'action à la destruction

			tabTirMonstre.get(lEffaceTirMonstre.get(i)-i).detruire();
			tabTirMonstre.remove(lEffaceTirMonstre.get(i)-i);

		}
	}
	/**
	 * efface les fleches a detruire
	 */	
	void effaceFleche() 
	{
		Collections.sort(lEffaceFleche);
		//efface les fleches
		for(int i=0; i<lEffaceFleche.size(); i++)
		{
			tabFleche.remove(lEffaceFleche.get(i)-i);
		}


	}
	/**
	 * efface les monstres et l'ensemble des tirs 
	 */	
	void effaceTout()
	{
		effaceMonstre();
		effaceTirMonstre();
		effaceFleche();
	}

	/*
boolean intersectRectangle(Point HG1, Point HD1, Point BG1, Point BD1,int angle1,Point HG2, Point HD2, Point BG2, Point BD2,int angle2)
{

	Point centre1 = getRectCenter(HG1,HD1,BG1,BD1);
	Point centre2 = getRectCenter(HG2,HD2,BG2,BD2);
	boolean resultat= false;
	Point rHG1 = rotatePoint(HG1,centre1,angle1);
	Point rHD1 = rotatePoint(HD1,centre1,angle1);
	Point rBG1 = rotatePoint(BG1,centre1,angle1);
	Point rBD1 = rotatePoint(BD1,centre1,angle1);
	Point rHG2 = rotatePoint(HG2,centre2,angle2);
	Point rHD2 = rotatePoint(HD2,centre2,angle2);
	Point rBG2 = rotatePoint(BG2,centre2,angle2);
	Point rBD2 = rotatePoint(BD2,centre2,angle2);

	resultat= resultat || isPointInRectangle(rHG1,rHG2,rHD2,rBG2);
	resultat= resultat || isPointInRectangle(rHD1,rHG2,rHD2,rBG2);
	resultat= resultat || isPointInRectangle(rBG1,rHG2,rHD2,rBG2);
	resultat= resultat || isPointInRectangle(rBD1,rHG2,rHD2,rBG2);

	resultat= resultat || isPointInRectangle(rHG2,rHG1,rHD1,rBG1);
	resultat= resultat || isPointInRectangle(rHD2,rHG1,rHD1,rBG1);
	resultat= resultat || isPointInRectangle(rBG2,rHG1,rHD1,rBG1);
	resultat= resultat || isPointInRectangle(rBD2,rHG1,rHD1,rBG1);
	return(resultat);
}
	 */
	/**
	 * Initialise les monstres dans le niveau
	 * 
	 * @param nombre, nombre de monstre à faire spawner aléatoirement
	 * @param typeDeSpawn, SPAWN_ALEATOIRE: spawn dans le rectangle défini par l'éditeur, SPAWN_PROGRAMME: spawn la ou les mosntres sont placés
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

				//on convertit les monstres stockés en monstres 
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
					//coordonées aléatoire pour placer le monstre
					x=(int) (Math.random()*(monde.xEndMap-monde.xStartMap));
					y=(int) (Math.random()*(monde.yEndMap-monde.yStartMap));

					x= monde.xStartMap- INIT_RECT.x +x;
					y= monde.yStartMap- INIT_RECT.y +y;
					//on créer le monstre à faire apparaitre
					spirel = new Spirel(x,y,false);


					int BASDROITE = 6;
					int HAUTGAUCHE= 8;

					//pour le test de blocage d'un monstre dans un mur, on utilise l'ancienne direction dans laquelle il s'est déplacé
					//On test les deux bords extreme. Si les tests réussient, le monstre n'est pas dans un mur 
					int ancienDirection=HAUTGAUCHE;

					int xS = 0; 
					int yS= 0;	

					//Test du blocage du coin HAUT GAUCHE du monstre
					for (int j=0 ; j<3 ; j ++)
					{
						xS=spirel.xPos+spirel.deplacement.xdecallsprite.get(spirel.anim)+ (j==0 ? 0 : spirel.deplacement.xhitbox.get(spirel.anim));
						yS=spirel.yPos+spirel.deplacement.ydecallsprite.get(spirel.anim) + (j==2 ? 0 : spirel.deplacement.yhitbox.get(spirel.anim)); 
						correct= correct && !Collision.isBloque(this, spirel,xS,yS,ancienDirection);
					}

					ancienDirection=BASDROITE;
					//Test du blocage du coin BAS DROITE du monstre
					for (int j=0 ; j<3 ; j ++)
					{
						xS = spirel.xPos+spirel.deplacement.xdecallsprite.get(spirel.anim) + (j<=1 ? 0 : spirel.deplacement.xhitbox.get(spirel.anim));
						yS= spirel.yPos+spirel.deplacement.ydecallsprite.get(spirel.anim)  + (j>=1 ? 0 : spirel.deplacement.yhitbox.get(spirel.anim));
					}
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
					//on regle la position et la vitesse
					deplace.setParamFleche(this,fleche, heros);
					tabFleche.add(fleche);
				}

				//COURSE DROITE
				if(courseDroiteDown && !(heros.IsDeplacement(Mouvement.glissade))&& !flecheEncochee)
				{
					changeMouv=true;
					//si on ne courrait pas vers la droite avant
					if(! (heros.IsDeplacement(Mouvement.course) && heros.anim>=4))
					{
						nouvAnim= 4; 
						nouvMouv= new Course(); 
					}
					else //on courrait deja avant , il suffit juste de changer de sprite et de le decaller 
					{

						nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche"? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						nouvMouv= heros.deplacement; 
					}
				}

				//MARCHE DROITE 
				else if(marcheDroiteDown&& !flecheEncochee)
				{
					changeMouv=true;

					if(heros.deplacement.getClass().getName()=="deplacement.Glissade"&& heros.anim==1)
					{
						changeMouv=true;

						nouvAnim= (heros.vit.y>=0 ? 4 : 3); 
						nouvMouv= new Saut(); 
					}
					else if (heros.deplacement.getClass().getName().equals("deplacement.Glissade"))
					{
						//on change rien 				
						nouvMouv= heros.deplacement;
						nouvAnim=heros.anim;
					}
					//si on courrait vers la droite en l'air ou non 
					else if((heros.deplacement.getClass().getName()=="deplacement.Course" && heros.anim>=4))
					{		
						nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						nouvMouv= heros.deplacement; 
					}

					//si on ne marchait pas vers la droite et qu'on est pas en l'air 
					else if(! (heros.deplacement.getClass().getName()=="deplacement.Marche" && heros.anim>=4)&& peutSauter)
					{
						nouvAnim= 4; 
						nouvMouv=new Marche();

					}

					//si on veut marcher en l'air (donc vers la droite) 
					else if (!peutSauter&& !heros.last_colli_right)
					{
						deplaceSautDroit=true; // on fait bouger le heros
						nouvAnim= heros.anim %3 +3;
						nouvMouv=new Saut();
					}
					else // si le heros est au sol et veux continuer à marcher vers la droite
					{

						nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						nouvMouv= heros.deplacement; 
					}
				}
				//COURSE GAUCHE
				if(courseGaucheDown && !(heros.deplacement.getClass().getName()=="deplacement.Glissade")&& !flecheEncochee)
				{
					changeMouv=true;
					//si on ne courrait pas vers la gauche avant 
					if(! (heros.deplacement.getClass().getName()=="deplacement.Course" && heros.anim<4))
					{

						nouvAnim=0;
						nouvMouv= new Course();
					}
					else // si on courrait vers la gauche avant 
					{

						nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						nouvMouv= heros.deplacement; 
					}
				}
				//MARCHE GAUCHE 
				else if(marcheGaucheDown&& !flecheEncochee )
				{
					changeMouv=true;

					if(heros.deplacement.getClass().getName()=="deplacement.Glissade"&& heros.anim==0)
					{
						changeMouv=true;

						nouvAnim= (heros.vit.y>=0 ? 1 : 0); 
						nouvMouv= new Saut(); 
					}
					else if (heros.deplacement.getClass().getName().equals("deplacement.Glissade"))
					{
						//on change rien 
						nouvMouv=  heros.deplacement;
						nouvAnim= heros.anim;
					}

					//si on courrait vers la gauche en l'air ou non 
					else if((heros.deplacement.getClass().getName()=="deplacement.Course" && heros.anim<4))
					{

						nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						nouvMouv= heros.deplacement; 
					}

					//si on ne marchait pas vers la gauche et qu'on est pas en l'air 
					else if(! (heros.deplacement.getClass().getName()=="deplacement.Marche" && heros.anim<4)&& peutSauter)
					{
						nouvAnim=0;
						nouvMouv= new Marche();
					}

					//si on veut marcher en l'air (donc vers la gauche) 
					else if (!peutSauter&& !heros.last_colli_left)
					{
						deplaceSautGauche=true; // on fait bouger le herosnnage
						nouvAnim= heros.anim %3;
						nouvMouv=new Saut(); 
					}
					else // si le heros est au sol et veux continuer à marcher vers la gauche
					{
						nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche"? (heros.anim+1)%4 :(heros.anim+1)%4+4 ); 
						nouvMouv= heros.deplacement; 
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
				//si le herosnnage saute pour la première fois et qu'il peut sauter et qu'il ne glisse pas
				if(sautDown && peutSauter && !(heros.deplacement.getClass().getName()=="deplacement.Glissade")&& !flecheEncochee)
				{
					courseDroiteDown=false;
					courseGaucheDown=false;

					changeMouv=true;

					peutSauter=false;


					//le herosnnage saute donc finSaut est faux
					debutSaut=true;
					finSaut=false;

					nouvAnim=heros.droite_gauche(heros.anim)=="Gauche" ? 0 : 3 ;
					nouvMouv= new Saut();


				}
				//on glisse et on veut sauter 
				else if(sautDown && heros.deplacement.getClass().getName()=="deplacement.Glissade") 
				{
					changeMouv=true;	
					sautGlisse=true;

					nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche"? 0 : 3);
					nouvMouv=new Saut();

				}

				//touches pour lesquels maintenir appuyé ne change rien
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
				//on variablesPartieRapide.affiche l'animation d'attente

				nouvAnim= (heros.droite_gauche(heros.anim)=="Gauche" ? 0 : 1) ;
				nouvMouv= new Attente();


				tabFleche.get(tabFleche.size()-1).flecheDecochee();
			}

		}

		//MARCHE
		if ((marcheDroiteReleased||marcheGaucheReleased
				||courseDroiteReleased||courseGaucheReleased))
		{
			if(marcheDroiteReleased)
			{
				marcheDroiteDown=false;marcheDroiteReleased=false;
			}
			if(courseDroiteReleased)
			{
				courseDroiteDown=false;courseDroiteReleased=false;
			}
			if(marcheGaucheReleased)
			{
				marcheGaucheDown=false;marcheGaucheReleased=false;
			}
			if(courseGaucheReleased)
			{
				courseGaucheDown=false;courseGaucheReleased=false;
			}


			if( !heros.deplacement.getClass().getName().equals("deplacement.Glissade") && !flecheEncochee )
			{
				changeMouv=true;

				//pas de decallage de sprite 

				//au sol
				if((heros.deplacement.getClass().getName()=="deplacement.Marche"|| heros.deplacement.getClass().getName()=="deplacement.Course"||heros.deplacement.getClass().getName()=="deplacement.Dash") && peutSauter)
				{
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					nouvAnim= (heros.droite_gauche(heros.anim)=="Droite" ? 1: 0 );
					nouvMouv= new Attente();

					//on met sa vitesse à 0:  
					heros.vit.x=0;

				}

				else if (heros.deplacement.getClass().getName()=="deplacement.Attente")
				{
					//on arrete quand meme le herosnnage (exemple si il relache la touche de deplacement sur laquelle il avait appuyé en l'air)
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					nouvAnim= heros.droite_gauche(heros.anim)=="Droite" ? 1: 0 ;
					nouvMouv= new Attente();

					//on met sa vitesse à 0:  
					heros.vit.x=0;
				}
				//en l'air et glisse pas
				else if(!peutSauter)
				{

					heros.vit.x=0;
					changeMouv=true;
					// tout dépend si le herosnnage tombe ou non 
					if (heros.vit.y<0)
					{
						//il ne tombe pas donc on met les premières animations de saut

						nouvAnim= heros.droite_gauche(heros.anim)=="Gauche" ? 0: 3 ;

						nouvMouv=new Saut();
					}

					else // le herosnnage tombe 
					{
						nouvAnim=heros.droite_gauche(heros.anim)=="Gauche" ? 1: 4 ;
						nouvMouv=new Saut();
					}
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
			boolean dash = delta<InterfaceConstantes.TDash && delta>InterfaceConstantes.TminDash;
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
			deplaceSautDroit=false;

			marcheDroiteDown=false;
			marcheDroiteReleased=true;

			courseDroiteDown=false;
			courseDroiteReleased=true;
			//on retient le temps de relachement de la touche
			clickTime1 = System.nanoTime();
		}
		if(input==Touches.t_gauche)
		{
			deplaceSautGauche=false;

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
				//on choisit de ne pas afficher la fin de la partie(elle n'est en réalité pas finie
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

		drawMonde(g,true);
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
			int xDraw= m.xPos+xdeplaceEcran+xdeplaceEcranBloc;
			int yDraw= m.yPos+ydeplaceEcran+ydeplaceEcranBloc;

			g.drawImage(imMonstre.getImage(m), xDraw ,yDraw ,null);

			if(drawHitbox)
			{
				int width= m.deplacement.xtaille.get(m.anim);
				int height= m.deplacement.ytaille.get(m.anim);
				drawHitbox(g,xDraw, yDraw,width,height);
			}
		}

	}

	public void drawPerso(Graphics g,boolean drawHitbox) {

		if(heros.afficheTouche)
		{
			g.drawImage(heros.getImages(), heros.xpos ,heros.ypos,null);
		}

		if(drawHitbox)
		{
			Hitbox hitbox= heros.getHitbox(INIT_RECT);
			/*int xDraw = heros.xpos+heros.deplacement.xdecallsprite.get(heros.anim);
			int yDraw= heros.ypos+heros.deplacement.ydecallsprite.get(heros.anim);
			int width= heros.deplacement.xhitbox.get(heros.anim);
			int height= heros.deplacement.yhitbox.get(heros.anim); */
			drawPolygon(g,hitbox.polygon);
		}

	}

	public void drawFleches(Graphics g,boolean drawHitbox) {
		//Affichage des flèches
		if(tabFleche == null)
		{
			return ;	
		}
		try{
			for(Fleche fleche :tabFleche )
			{
				int xdraw=  fleche.xpos+xdeplaceEcran+xdeplaceEcranBloc;
				int ydraw= fleche.ypos+ydeplaceEcran+ydeplaceEcranBloc;
				g.drawImage(defaultFleche.getImage(fleche),xdraw ,ydraw,null);

			}
		}
		catch(ConcurrentModificationException e1)
		{
			System.out.println("ERROR: ConcurrentModificationException");
		}



	}

	public void drawTirMonstres(Graphics g,boolean drawHitbox) {
		//Affichage des tirs de monstres 
		for(TirMonstre tir : tabTirMonstre)
		{
			int xdraw=  tir.xpos +xdeplaceEcran+xdeplaceEcranBloc;
			int ydraw= tir.ypos +ydeplaceEcran+ydeplaceEcranBloc;
			g.drawImage(imTirMonstre.getImage(tir),xdraw,ydraw,null);

			if(drawHitbox)
			{
				int width= tir.xtaille.get(tir.anim);
				int height=tir.ytaille.get(tir.anim);
				drawHitbox(g,xdraw,ydraw,width,height);
			}

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
			int xG= m.xPos+xdeplaceEcran+xdeplaceEcranBloc;
			int xD= xG +m.deplacement.xtaille.get(m.anim);
			int yH= m.yPos+ydeplaceEcran+ydeplaceEcranBloc;

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
