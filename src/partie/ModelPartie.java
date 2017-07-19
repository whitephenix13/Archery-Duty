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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import Affichage.Affichage;
import collision.Collision;
import debug.Debug_time;
import deplacement.Accroche;
import deplacement.Attente;
import deplacement.Course;
import deplacement.Marche;
import deplacement.Mouvement;
import deplacement.Mouvement_perso;
import deplacement.Saut;
import deplacement.Tir;
import effects.Effect;
import fleches.Fleche;
import images.ImagesEffect;
import loading.LoadAllMedias;
import loading.LoadAllMedias.CustomLoad;
import menuPrincipal.AbstractModelPrincipal;
import monstre.Monstre;
import monstre.Spirel;
import monstre.TirMonstre;
import music.Music;
import music.MusicBruitage;
import option.Config;
import principal.InterfaceConstantes;
import serialize.Serialize;
import types.Bloc;
import types.Destroyable;
import types.Hitbox;
import types.Touches;
import types.TypeObject;

public class ModelPartie extends AbstractModelPartie{


	public ModelPartie(Touches _touches)
	{
		touches = _touches;
		inputPartie = new InputPartie(this);
	}

	public void startPartie(int typeDeSpawn)
	{
		//world has been loaded 
		INIT_RECT.x= (monde.xStartPerso-InterfaceConstantes.LARGEUR_FENETRE/2)/100*100;//49 900
		INIT_RECT.y= (monde.yStartPerso-InterfaceConstantes.HAUTEUR_FENETRE/2)/100*100;//49 700

		heros.xpos((InterfaceConstantes.LARGEUR_FENETRE/2+(INIT_RECT.x==monde.xStartPerso-InterfaceConstantes.LARGEUR_FENETRE/2? 0:100 ))/100*100);
		heros.ypos((InterfaceConstantes.HAUTEUR_FENETRE/2+(INIT_RECT.y==monde.yStartPerso-InterfaceConstantes.HAUTEUR_FENETRE/2? 0:100 ))/100*100);
		//on fait apparaitre les monstres 
		nombreMonstreRestant=100;
		spawnMonster(nombreMonstreRestant,typeDeSpawn);
	}

	/**
	 * Boucle de jeu
	 * 
	 * @param affich, la JFrame a afficher
	 */	
	public void play(Affichage affich) 
	{
		while(!affich.isFocused() && Config.pauseWhenLooseFocus)
		{
			if(firstNonFocused)
			{
				inputPartie.resetTouchesFocus();
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

		Debug_time debugTime = new Debug_time();
		debugTime.init();
		//on efface les qui doivent être détruit 

		//on desactive la touche cap au cas ou elle serait utilisée
		Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);

		debugTime.elapsed("action", 2);

		keyAction();

		//Lors d'une pause, on ne veut pas réinitaliser la partie en cours mais juste y accéder à nouveau
		if(!inPause)
		{
			debugTime.elapsed("delete", 2);


			//First action is to delete since we want the user to see the colliding objects for at least 1 frame 
			deleteObject(lEffaceFleche,(List<Destroyable>)(List<?>) tabFleche);
			deleteObject(lEffaceTirMonstre,(List<Destroyable>)(List<?>)tabTirMonstre);
			deleteObject(lEffaceMonstre,(List<Destroyable>)(List<?>)tabMonstre);
			deleteObject(lEffaceEffect,(List<Destroyable>)(List<?>)arrowsEffects);


			//on vide les listes
			lEffaceFleche.clear();
			lEffaceTirMonstre.clear();
			lEffaceMonstre.clear();
			lEffaceEffect.clear();

			debugTime.elapsed("deplaceHeros", 2);

			//DEPLACEMENT 
			//Heros
			deplace.DeplaceObject(heros,heros.nouvMouv,this);

			debugTime.elapsed("deplaceMonstre", 2);


			//Monstre

			for(int i=0 ; i< tabMonstre.size(); i++)
			{
				Monstre m = tabMonstre.get(i);
				//on ne deplace le monstre qui si il est visible
				//boolean monsterOnScreen= InterfaceConstantes.SCREEN.polygon.contains(new Point 
						//(m.xpos()+xScreendisp,m.ypos()+yScreendisp));
				//if (monsterOnScreen)
					deplace.DeplaceObject(m,m.deplacement, this);

			}

			debugTime.elapsed("deplaceFleche", 2);


			//TIRS
			//Fleche
			for(int i=0; i<tabFleche.size(); i++)
			{
				Fleche f=tabFleche.get(i);
				deplace.DeplaceObject(f, f.deplacement, this);
			}

			debugTime.elapsed("tirMonstre", 2);


			//Tir Monstre deplace et a effacer
			for(int i=0 ; i< tabTirMonstre.size(); i++)
			{
				TirMonstre tir = tabTirMonstre.get(i);
				deplace.DeplaceObject(tir,tir.deplacement, this);
			}

			debugTime.elapsed("tirFleche", 2);


			//tir fleche

			for(int i=0; i<tabFleche.size(); i++)
			{
				Fleche fleche = tabFleche.get(i);

				double tempsFleche=  PartieTimer.me.getElapsedNano()-fleche.tempsDetruit;
				if((tempsFleche > fleche.TEMPS_DESTRUCTION) && fleche.tempsDetruit>0  )
				{
					fleche.setNeedDestroy(true);
				}

			}

			debugTime.elapsed("tir collision", 2);


			//on gere la collision des tirs/monstre/heros
			gestionTir();

			debugTime.elapsed("effect updates", 2);


			//update effects anim or delete them 
			gestionEffect();

			debugTime.elapsed("object destruction preparation", 2);

			//PREPARE OBJECTS FOR DESTRUCTION 

			for(int i =0; i <tabFleche.size();++i)
				if(tabFleche.get(i).getNeedDestroy())
					lEffaceFleche.add(i);

			for(int i =0; i <tabTirMonstre.size();++i)
				if(tabTirMonstre.get(i).getNeedDestroy())
					lEffaceTirMonstre.add(i);

			for(int i =0; i <tabMonstre.size();++i)
				if(tabMonstre.get(i).getNeedDestroy())
					lEffaceMonstre.add(i);

			for(int i =0; i <arrowsEffects.size();++i)
				if(arrowsEffects.get(i).getNeedDestroy()){
					lEffaceEffect.add(i);
				}

			debugTime.elapsed("heros update", 2);


			//on met a jour le heros si il est touché avant de l'afficher
			heros.miseAjourTouche();
			heros.miseAJourSeyeri(this);


			debugTime.elapsed("end of game test", 2);

			//on test si la partie est finie 

			boolean finPartieAvant= finPartie;

			finPartie= (heros.getLife()==InterfaceConstantes.MINLIFE) || (nombreMonstreRestant==0);

			//on detecte la fin de la partie de la première fois : 
			if(!finPartieAvant &&finPartie )
			{
				AbstractModelPrincipal.changeFrame=true;
			}

		}

		computationDone=true;
		//else "pause"
		debugTime.elapsed("end of play loop", 1);

	}

	/**
	 * Gère les collisions de tir fleche/tirMonstre, fleche/Monstre, TirMonstre/heros
	 */	
	void gestionTir () 
	{
		//gestion fleche/TirMonstre 
		for(int i=0; i<tabFleche.size(); i++)
			for(int j=0; j<tabTirMonstre.size(); j++){
				Fleche fleche= tabFleche.get(i);
				if(fleche.checkCollision && !fleche.encochee && !(fleche.isPlanted &&(fleche.tempsDetruit>0)) && !fleche.getNeedDestroy())
				{
					TirMonstre tirM= tabTirMonstre.get(j);

					if(tirM.checkCollision && !tirM.getNeedDestroy() && Collision.collisionObjects(this, fleche, tirM))
					{
						//bruit de collision tir/tir 
						MusicBruitage.startBruitage("annulation tir");
					}
				}
			}
		//gestion fleche/monstre 
		for(int i=0; i<tabFleche.size(); i++)
			for(int j=0; j<tabMonstre.size(); j++)
			{
				Fleche fleche= tabFleche.get(i);
				if(fleche.checkCollision&& !fleche.encochee && !(fleche.isPlanted && (fleche.tempsDetruit>0)) && !fleche.getNeedDestroy())
				{
					Monstre monstre = tabMonstre.get(j);

					if(monstre.checkCollision && !monstre.getNeedDestroy() && Collision.collisionObjects(this, fleche, monstre))
					{
						tabMonstre.get(j).addLife(tabFleche.get(i).degat);
					}
				}
			}

		//Update nombre monstre restant
		nombreMonstreRestant=tabMonstre.size();

		//gestion tirMonstre/heros
		for(int j=0; j<tabTirMonstre.size(); j++)
		{
			TirMonstre tirM= tabTirMonstre.get(j);

			if(tirM.checkCollision && !tirM.getNeedDestroy() && !heros.getNeedDestroy() && Collision.collisionObjects(this, heros, tirM))
			{
				if(!heros.invincible)
					heros.touche(tabTirMonstre.get(j).dommage);
			}
		}
	}

	void gestionEffect()
	{
		for(int i=0;i<arrowsEffects.size();++i)
		{
			Effect eff = arrowsEffects.get(i);
			//if anim ended: delete
			double tempsEffect=  PartieTimer.me.getElapsedNano()-eff.tempsDetruit;
			if(eff.isEnded())
			{
				eff.ref_fleche.OnFlecheEffectDestroy(this, true);
				eff.destroy(this, true);
			}
			else if(tempsEffect>eff.TEMPS_DESTRUCTION && eff.tempsDetruit>0)
			{
				eff.setNeedDestroy(true);
			}	
			//increment anim
			else if(!eff.getNeedDestroy() && (eff.tempsDetruit==0) )
			{
				eff.onUpdate(this,false);
				eff.anim=eff.animation.update(eff.anim, this.getFrame());
			}
		}


	}
	void deleteObject(List<Integer> indexList, List<Destroyable> objectList )
	{
		Collections.sort(indexList);

		for(int i=0; i<indexList.size(); i++)
		{
			objectList.get(indexList.get(i)-i).onDestroy(this);
			objectList.remove(indexList.get(i)-i);
		}
	}


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
					//coordonées aléatoire pour placer le monstre
					x=(int) (Math.random()*(monde.xEndMap-monde.xStartMap));
					y=(int) (Math.random()*(monde.yEndMap-monde.yStartMap));

					x= monde.xStartMap- INIT_RECT.x +x;
					y= monde.yStartMap- INIT_RECT.y +y;
					//on créer le monstre à faire apparaitre
					spirel = new Spirel(x,y,false,frame);
					correct = !Collision.isWorldCollision(this, spirel,false);
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
			if(inputPartie.pauseDown )
			{
				inPause=!inPause;
				inputPartie.pauseDown=false;
				if(inPause)
					inputPartie.resetTouchesFocus();
			}
			if(!inPause )
			{
				boolean isDragged = this.heros.isDragged();
				//TIR 
				if( (inputPartie.toucheTirDown|| inputPartie.touche2TirDown) && !heros.flecheEncochee && !heros.doitEncocherFleche 
						&& ((System.nanoTime()-heros.last_shoot_time)>InterfaceConstantes.FLECHE_TIR_COOLDOWN))
				{
					//if not just wall jump 
					if(!(heros.deplacement.IsDeplacement(Mouvement_perso.saut) && ((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<=InterfaceConstantes.WALL_JUMP_DISABLE_TIME)))
					{
						//Normal tir 
						if(inputPartie.toucheTirDown)
						{
							inputPartie.toucheTirDown=false;
							heros.set_tir_type(false);
						}
						//Special tir
						else
						{
							inputPartie.touche2TirDown=false;
							heros.set_tir_type(true);
						}
						boolean can_shoot_arrow = heros.canShootArrow(this);
						if(can_shoot_arrow){
							changeMouv=true;
							//on ne tir qu'une fleche
							heros.doitEncocherFleche=true;
							heros.nouvMouv= new Tir(TypeObject.heros,Tir.tir,frame); 
							heros.last_armed_time=System.nanoTime();
						}
					}
				}
					
				boolean heros_shoots = heros.flecheEncochee||heros.doitEncocherFleche;
				boolean heros_accroche = heros.deplacement.IsDeplacement(Mouvement_perso.accroche);
				boolean heros_glisse = heros.deplacement.IsDeplacement(Mouvement_perso.glissade);
				//COURSE DROITE
				if(inputPartie.courseDroiteDown && !heros_glisse && !heros_accroche && !heros_shoots && !isDragged)
				{
					//si on ne courrait pas vers la droite avant
					if(! (heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim>=4))
					{
						//do not run if we just wall jump
						if(! (heros.deplacement.IsDeplacement(Mouvement_perso.saut) && (PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<= InterfaceConstantes.WALL_JUMP_DISABLE_TIME ))
						{
							changeMouv=true;
							heros.nouvAnim= 4; 
							heros.nouvMouv= new Course(TypeObject.heros,Course.course_droite,frame); 
						}
					}
				}

				//MARCHE DROITE 
				else if(inputPartie.marcheDroiteDown&& !heros_shoots && !isDragged)
				{
					if(heros_glisse)
					{
						if(heros.anim == 1)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(TypeObject.heros,heros.getGlobalVit(this).y>=0? Saut.fall_droite:Saut.jump_droite,frame); 
						}
					}
					else if(heros_accroche)
					{
						//leave the border

						if(heros.anim == 0)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(TypeObject.heros,heros.getGlobalVit(this).y>=0? Saut.fall_droite:Saut.jump_droite,frame); 
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
						//do not move if we just wall jump
						if(heros.deplacement.IsDeplacement(Mouvement_perso.saut)&& 
								(heros.deplacement.type_mouv != Saut.land_gauche ) && (heros.deplacement.type_mouv != Saut.land_droite ) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)> InterfaceConstantes.WALL_JUMP_DISABLE_TIME))
						{
							changeMouv=true;
							heros.deplaceSautDroit=true; // on fait bouger le heros
							boolean fall = heros.getGlobalVit(this).y >=0 ;
							heros.nouvAnim= fall? 4 : 3 ; 
							heros.nouvMouv=new Saut(TypeObject.heros,fall?Saut.fall_droite:Saut.jump_droite,frame);
						}
					}
					else if(heros.peutSauter)// si le heros est au sol et veux continuer à marcher vers la droite
					{
						//no change mouv
					}
					else
					{
						try {throw new Exception("Unhandled input right in KeyDownAction ");} catch (Exception e) {e.printStackTrace();}
					}
				}
				//COURSE GAUCHE
				if(inputPartie.courseGaucheDown && !heros_glisse && !heros_accroche && !heros_shoots && !isDragged)
				{
					//si on ne courrait pas vers la gauche avant 
					if(! (heros.deplacement.IsDeplacement(Mouvement_perso.course) && heros.anim<4))
					{
						//do not run if we just wall jump
						if(! (heros.deplacement.IsDeplacement(Mouvement_perso.saut) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<= InterfaceConstantes.WALL_JUMP_DISABLE_TIME)))
						{
							changeMouv=true;
							heros.nouvAnim=0;
							heros.nouvMouv= new Course(TypeObject.heros,Course.course_gauche,frame);
						}
					}
				}
				//MARCHE GAUCHE 
				else if(inputPartie.marcheGaucheDown&& !heros_shoots && !isDragged)
				{

					if(heros_glisse)
					{
						if(heros.anim==0){
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 1 : 0); 
							heros.nouvMouv= new Saut(TypeObject.heros,heros.getGlobalVit(this).y>=0? Saut.fall_gauche:Saut.jump_gauche,frame); 
						}
					}
					else if (heros_accroche)
					{
						//leave the border

						if(heros.anim == 2)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(TypeObject.heros,heros.getGlobalVit(this).y>=0? Saut.fall_gauche:Saut.jump_gauche,frame); 
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
						//do not move if we just wall jump
						if(heros.deplacement.IsDeplacement(Mouvement_perso.saut)&& 
								(heros.deplacement.type_mouv != Saut.land_gauche ) && (heros.deplacement.type_mouv != Saut.land_droite ) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)> InterfaceConstantes.WALL_JUMP_DISABLE_TIME))
						{
							changeMouv=true;

							heros.deplaceSautGauche=true; // on fait bouger le heros
							boolean fall = heros.getGlobalVit(this).y >=0 ;
							heros.nouvAnim=fall? 1 : 0 ; 
							heros.nouvMouv=new Saut(TypeObject.heros,fall? Saut.fall_gauche:Saut.jump_gauche,frame); 
						}
					}
					else if(heros.peutSauter) // si le heros est au sol et veux continuer à marcher vers la gauche
					{
						//no change
					}
					else
					{
						try {throw new Exception("Unhandled input left in KeyDownAction ");} catch (Exception e) {e.printStackTrace();}
					}

				}

				//SLOW DOWN 

				if(inputPartie.toucheSlowDown)
				{
					slowCount=0;
					slowDown= ! slowDown;
					PartieTimer.me.changedSlowMotion(slowDown);
					inputPartie.toucheSlowDown=false;

					if(slowDown)
						Music.me.slowDownMusic();
					else
						Music.me.endSlowDownMusic();

				}

				//SAUT 
				//si le heros saute pour la première fois et qu'il peut sauter et qu'il ne glisse pas
				if(inputPartie.sautDown &&  !heros_shoots && !isDragged)
				{
					if(heros_glisse)
					{
						changeMouv=true;	
						heros.sautGlisse=true;

						heros.nouvAnim= (heros.droite_gauche(heros.anim).equals(Mouvement.GAUCHE)? 0 : 3);
						heros.nouvMouv=new Saut(TypeObject.heros,heros.nouvAnim==0?Saut.jump_gauche:Saut.jump_droite,frame );
						heros.last_wall_jump_time=PartieTimer.me.getElapsedNano();
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
						inputPartie.courseDroiteDown=false;
						inputPartie.courseGaucheDown=false;

						changeMouv=true;

						heros.peutSauter=false;


						//le heros saute donc finSaut est faux
						heros.debutSaut=true;
						heros.finSaut=false;

						heros.nouvAnim=heros.droite_gauche(heros.anim).equals(Mouvement.GAUCHE) ? 0 : 3 ;
						heros.nouvMouv= new Saut(TypeObject.heros,heros.nouvAnim==0?Saut.jump_gauche:Saut.jump_droite,frame );
					}
				}

				//touches pour lesquels maintenir appuyé ne change rien
				inputPartie.sautDown =false;
			}
		}
	}

	public void keyReleasedAction ()
	{
		if(inputPartie.pauseReleased)
		{
			inputPartie.pauseReleased=false;
		}
		//on arrete de deplacer le heros qui saute: 
		//TIR 
		boolean normal_tir_R= inputPartie.toucheTirReleased;//left click
		boolean normal_2tir_R= inputPartie.touche2TirReleased; //right click

		if( (normal_tir_R|| normal_2tir_R) && ((System.nanoTime()-heros.last_armed_time)>InterfaceConstantes.ARMED_MIN_TIME))
		{
			if(normal_tir_R){
				inputPartie.toucheTirDown=false;
				inputPartie.toucheTirReleased=false;
			}
			else{
				inputPartie.touche2TirDown=false;
				inputPartie.touche2TirReleased=false;
			}

			if(heros.flecheEncochee)
			{
				heros.flecheEncochee=false;
				changeMouv=true;

				heros.nouvAnim= (heros.droite_gauche(heros.anim).equals(Mouvement.GAUCHE) ? 0 : 2) ;
				heros.nouvMouv= new Attente(TypeObject.heros,heros.nouvAnim==0? Attente.attente_gauche:Attente.attente_droite,frame);
				tabFleche.get(tabFleche.size()-1).flecheDecochee(this,deplace);
				//TODO:
				if(normal_2tir_R)
				{}
				heros.last_shoot_time= System.nanoTime();

			}

		}

		boolean just_wall_jump= heros.deplacement.IsDeplacement(Mouvement_perso.saut) && (((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<=InterfaceConstantes.WALL_JUMP_DISABLE_TIME));
		//MARCHE
		if ((inputPartie.marcheDroiteReleased||inputPartie.marcheGaucheReleased
				||inputPartie.courseDroiteReleased||inputPartie.courseGaucheReleased) && !just_wall_jump)
		{
			if(inputPartie.marcheDroiteReleased)
			{
				inputPartie.marcheDroiteDown=false;inputPartie.marcheDroiteReleased=false;heros.runBeforeJump=false;
			}
			if(inputPartie.courseDroiteReleased)
			{
				inputPartie.courseDroiteDown=false;inputPartie.courseDroiteReleased=false;heros.runBeforeJump=false;
			}
			if(inputPartie.marcheGaucheReleased)
			{
				inputPartie.marcheGaucheDown=false;inputPartie.marcheGaucheReleased=false;heros.runBeforeJump=false;
			}
			if(inputPartie.courseGaucheReleased)
			{
				inputPartie.courseGaucheDown=false;inputPartie.courseGaucheReleased=false;heros.runBeforeJump=false;
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

					heros.nouvAnim= (heros.droite_gauche(heros.anim).equals(Mouvement.DROITE) ? 2: 0 );
					heros.nouvMouv= new Attente(TypeObject.heros,heros.nouvAnim==0? Attente.attente_gauche:Attente.attente_droite,frame);

					//on met sa vitesse à 0:  
					heros.localVit.x=0;

				}

				else if (heros.deplacement.IsDeplacement(Mouvement_perso.attente))
				{
					//on arrete quand meme le heros (exemple si il relache la touche de deplacement sur laquelle il avait appuyé en l'air)
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= heros.droite_gauche(heros.anim).equals(Mouvement.DROITE) ? 2: 0 ;
					heros.nouvMouv= new Attente(TypeObject.heros,heros.nouvAnim==0? Attente.attente_gauche:Attente.attente_droite,frame);

					//on met sa vitesse à 0:  
					heros.localVit.x=0;
				}
				//en l'air et glisse pas
				else if(!heros.peutSauter)
				{

					heros.localVit.x=0;
					changeMouv=true;
					// tout dépend si le heros tombe ou non 

					if (heros.getGlobalVit(this).y<0)//il ne tombe pas donc on met les premières animations de saut
						heros.nouvAnim= heros.droite_gauche(heros.anim).equals(Mouvement.GAUCHE) ? 0: 3 ;

					else // le heros tombe 
						heros.nouvAnim=heros.droite_gauche(heros.anim).equals(Mouvement.GAUCHE) ? 1: 4 ;

					int type_mouv=heros.nouvAnim==0? Saut.jump_gauche: (heros.nouvAnim==3?Saut.jump_droite:  (heros.nouvAnim==1?Saut.fall_gauche:Saut.fall_droite));
					heros.nouvMouv=new Saut(TypeObject.heros,type_mouv,frame);

				}
			}
		}
		//SLOW DOWN
		if(inputPartie.toucheSlowReleased)
		{
			inputPartie.toucheSlowReleased=false;
		}
		//SAUT 
		if(inputPartie.sautReleased&& !heros.flecheEncochee )
		{
			inputPartie.sautDown=false;
			inputPartie.sautReleased=false;
		}
	}

	public void keyAction()
	{
		keyDownAction();
		keyReleasedAction();
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
				Bloc tempPict =  monde.niveau[abs][ord];
				if(tempPict != null)
				{
					int xDraw=tempPict.getXpos()+ xScreendisp- INIT_RECT.x;
					int yDraw=tempPict.getYpos()+ yScreendisp- INIT_RECT.y;
					g.drawImage(imMonde.getImages(tempPict,false),xDraw,yDraw, null);

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
			int xDraw= m.xpos()+xScreendisp;
			int yDraw= m.ypos()+yScreendisp;

			ArrayList<Image> images = imMonstre.getImage(m);
			for(Image im : images)
				g.drawImage(im, xDraw ,yDraw ,null);

			int xDraw_d= xDraw +m.deplacement.xtaille.get(m.anim);
			//draw monster lifebar
			int[] x= {xDraw/2+xDraw_d/2-InterfaceConstantes.MAXLIFE/2,xDraw/2+xDraw_d/2-InterfaceConstantes.MAXLIFE/2};
			int[] y= {yDraw-10,yDraw-10};
			int[] width={InterfaceConstantes.MAXLIFE,m.getLife()};
			int[] height={5,5};
			Color[] colors = {Color.BLACK,Color.GREEN};
			drawBar(g,2,x,y,width,height,colors);

			//drawBar(g,xDraw/2+xDraw_d/2-InterfaceConstantes.MAXLIFE/2,yDraw-10,InterfaceConstantes.MAXLIFE,5,m.getLife(),Color.BLACK,Color.GREEN);
			if(drawHitbox)
			{
				Hitbox hitbox= m.getWorldHitbox(this);
				drawPolygon(g,hitbox.polygon);
			}
		}

	}

	public Point drawPersoTir(Graphics g)
	{
		int anim = heros.anim;
		Point anchor = new Point(heros.xpos()+heros.deplacement.x_rot_pos.get(anim), 
				heros.ypos()+heros.deplacement.y_rot_pos.get(anim));
		Graphics2D g2d = (Graphics2D)g;


		AffineTransform tr = getRotatedTransform(new Point(heros.xpos(),heros.ypos()), 
				new Point(heros.deplacement.x_rot_pos.get(anim),heros.deplacement.y_rot_pos.get(anim)),
				new Point(heros.deplacement.xtaille.get(anim),heros.deplacement.ytaille.get(anim)),
				heros.rotation_tir);


		ArrayList<Image> l_image = imHeros.getImages(heros);
		for(int i=0; i<l_image.size(); ++i)
		{
			//im_body, im_back, im_head, im_front
			if(i==0||i==2)
				g.drawImage(l_image.get(i), heros.xpos(),heros.ypos(),null);

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
					g.drawImage(l_image.get(i), heros.xpos(),heros.ypos(),null);

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
		//Affichage des flèches
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
					pos=new Point(heros.xpos()+f_anchor.x,heros.ypos()+f_anchor.y);
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
					Point transl = new Point(fleche.xpos()+xScreendisp, fleche.ypos()+yScreendisp);
					tr=new AffineTransform(fleche.draw_tr);
					double[] flatmat = new double[6];
					tr.getMatrix(flatmat);
					tr.setTransform(flatmat[0], flatmat[1], flatmat[2], flatmat[3], transl.x, transl.y);
				}
				if(fleche.encochee){
					fleche.draw_tr=tr;
				}
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
			for(TirMonstre tir : tabTirMonstre)
			{
				int xDraw=  tir.xpos() +xScreendisp;
				int yDraw= tir.ypos() +yScreendisp;

				ArrayList<Image> images = imTirMonstre.getImage(tir);
				for(Image im : images)
					g.drawImage(im, xDraw ,yDraw ,null);

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
		Graphics2D g2d = (Graphics2D)g;
		//Draw arrow effect
		for(int i =0; i< arrowsEffects.size(); ++i)
		{
			Effect eff = arrowsEffects.get(i);
			int anim = eff.anim;
			AffineTransform tr = eff.getTransformDraw(this);

			ArrayList<Image> images = imEffect.getImage(eff);
			for(Image im : images){
				Image im2draw = eff.applyFilter(this, im);

				g2d.drawImage(im2draw,tr,null);

			}

			if(drawHitbox)
			{
				Point transl = eff.getTranslationFromTranformDraw(this);
				int xgh=transl.x;
				int ygh=transl.y;
				int xdh=(int) (xgh+eff.xtaille.get(anim)*Math.cos(eff.ref_fleche.rotation));
				int ydh=(int) (ygh+eff.xtaille.get(anim)*Math.sin(eff.ref_fleche.rotation));
				int xdb= (int) (xdh-eff.ytaille.get(anim)*Math.sin(eff.ref_fleche.rotation));
				int ydb= (int) (ydh+eff.ytaille.get(anim)*Math.cos(eff.ref_fleche.rotation));
				int xgb= (int) (xgh-eff.ytaille.get(anim)*Math.sin(eff.ref_fleche.rotation));
				int ygb=(int) (ygh+eff.ytaille.get(anim)*Math.cos(eff.ref_fleche.rotation));

				Hitbox hitbox= new Hitbox(new Point(xgh,ygh),new Point(xdh,ydh),new Point(xdb,ydb),new Point(xgb,ygb));
				drawPolygon(g,hitbox.polygon);
			}
		}
		//Affichage du slow motion 
		if(slowDown )
		{
			//la taille de l'image fait 1500
			Image im = imEffect.getImage(ImagesEffect.SLOWDOWN);
			g.drawImage(im, (InterfaceConstantes.LARGEUR_FENETRE-im.getWidth(null))/2 ,(InterfaceConstantes.HAUTEUR_FENETRE-im.getHeight(null))/2,null);
		}


	}
	@Override
	public void drawInterface(Graphics g) {
		//AFFICHAGE DE L'INTERFACE 
		//life
		int[] x_l= {10,10};
		int[] y_l= {10,10};
		int[] width_l={InterfaceConstantes.MAXLIFE,heros.getLife()};
		int[] height_l={20,20};
		Color[] colors_l = {Color.BLACK,Color.GREEN};
		drawBar(g,2,x_l,y_l,width_l,height_l,colors_l);

		//seyeri
		int[] x_s= {10,10,10};
		int[] y_s= {40,40,40};
		int[] width_s={(int) InterfaceConstantes.MAXSEYERI,heros.getNotEnoughSeyeri(),(int) heros.getSeyeri()};
		int[] height_s={20,20,20};
		Color[] colors_s = {Color.BLACK,Color.RED,Color.BLUE};
		drawBar(g,3,x_s,y_s,width_s,height_s,colors_s);

		heros.decreaseNotEnoughSeyeriCounter();


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
	public void drawBar(Graphics g,int number_rectangles, int[] x, int[] y, int[] width, int[] height,Color[] colors)
	{
		for(int i=0; i< number_rectangles; i++)
		{
			if(width[i]>0){
				g.setColor(colors[i]);
				g.fillRect(x[i], y[i], width[i], height[i]);}
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


	public BufferedImage apply_width_mask(BufferedImage original,BufferedImage previousMaskedIm, int w_start, int last_start,float transparency)
	{
		int width = original.getWidth();
		int height = original.getHeight();
		if(width==-1 || height == -1)
			return original;

		int desired_alpha = (int)(255* transparency);
		Color c = new Color(desired_alpha,desired_alpha,desired_alpha);
		Color nullC = new Color(255-desired_alpha,255-desired_alpha,255-desired_alpha);

		//make the first part invisible 
		for(int i =last_start; i<w_start;i++){
			for(int j=0;j<height;j++){
				int current_rgb = original.getRGB(i, j);
				int color = current_rgb & 0x00ffffff; // Mask preexisting alpha
				int prev_alpha = current_rgb & 0xff000000;
				int alpha = prev_alpha & (nullC.getRGB() << 24); // Shift blue to alpha
				previousMaskedIm.setRGB(i, j, (color | alpha));
			}
		}

		//make the second part transparent 
		for(int i =w_start; i<last_start;i++){
			for(int j=0;j<height;j++){
				int current_rgb = original.getRGB(i, j);
				int color = current_rgb & 0x00ffffff; // Mask preexisting alpha
				int prev_alpha = current_rgb & 0xff000000;
				int alpha = prev_alpha & (c.getRGB() << 24); // Shift blue to alpha
				previousMaskedIm.setRGB(i, j, (color | alpha));
			}
		}
		return previousMaskedIm;
	}



	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	@Override
	public BufferedImage toBufferedImage(Image img)
	{
		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}


}
