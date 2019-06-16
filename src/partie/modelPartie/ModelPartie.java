package partie.modelPartie;

import java.awt.Color;
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

import Affichage.Affichage;
import Affichage.DrawImageHandler;
import Affichage.DrawImageItem;
import gameConfig.Destroyable;
import gameConfig.InterfaceConstantes;
import gameConfig.TypeObject;
import images.ImagesEffect;
import menu.menuPrincipal.AbstractModelPrincipal;
import menu.menuPrincipal.ModelPrincipal;
import music.Music;
import option.Config;
import option.Touches;
import partie.AI.A_Star_Helper;
import partie.bloc.Bloc;
import partie.bloc.Bloc.TypeBloc;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.CustomBoundingSquare;
import partie.collision.Hitbox;
import partie.conditions.Condition;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.deplacement.entity.Accroche;
import partie.deplacement.entity.Accroche.TypeAccroche;
import partie.deplacement.entity.Attente;
import partie.deplacement.entity.Attente.TypeAttente;
import partie.deplacement.entity.Course;
import partie.deplacement.entity.Course.TypeCourse;
import partie.deplacement.entity.Marche;
import partie.deplacement.entity.Marche.TypeMarche;
import partie.deplacement.entity.Mouvement_entity;
import partie.deplacement.entity.Mouvement_entity.TypeMouvEntitie;
import partie.deplacement.entity.Saut;
import partie.deplacement.entity.Saut.TypeSaut;
import partie.deplacement.entity.Tir;
import partie.deplacement.entity.Tir.TypeTirPerso;
import partie.effects.Effect;
import partie.effects.Trou_noir_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.entitie.monstre.Spirel;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;
import partie.projectile.fleches.rusee.Fleche_auto_teleguidee;
import partie.projectile.tirMonstre.TirSpirel;

public class ModelPartie extends AbstractModelPartie{

	public ModelPartie(Touches _touches)
	{
		touches = _touches;
		inputPartie = new InputPartie(this);
	}

	public void startPartie(int typeDeSpawn)
	{
		//world has been loaded 
		INIT_RECT.x= (monde.xStartPerso-InterfaceConstantes.WINDOW_WIDTH/2)/100*100;//49 900
		INIT_RECT.y= (monde.yStartPerso-InterfaceConstantes.WINDOW_HEIGHT/2)/100*100;//49 700

		heros.setXpos_sync((InterfaceConstantes.WINDOW_WIDTH/2+(INIT_RECT.x==monde.xStartPerso-InterfaceConstantes.WINDOW_WIDTH/2? 0:100 ))/100*100);
		heros.setYpos_sync((InterfaceConstantes.WINDOW_HEIGHT/2+(INIT_RECT.y==monde.yStartPerso-InterfaceConstantes.WINDOW_HEIGHT/2? 0:100 ))/100*100);
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
		int currentVerbose = 2;
		ModelPrincipal.debugTime.startElapsedForVerbose(currentVerbose);
		//int x= heros.xPos + heros.deplacement.xdecallsprite[heros.anim]; //la vrai position du heros necessite encore un - variablesPartieRapide.xdeplaceEcran
		//int y= heros.yPos+ heros.deplacement.ydecallsprite[heros.anim]; 

		//on efface les qui doivent être détruit 

		//on desactive la touche cap au cas ou elle serait utilisée
		Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);

		keyAction();

		ModelPrincipal.debugTime.elapsed("action", currentVerbose);

		//Lors d'une pause, on ne veut pas réinitaliser la partie en cours mais juste y accéder à nouveau
		if(!inPause)
		{

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

			ModelPrincipal.debugTime.elapsed("delete", currentVerbose);

			//DEPLACEMENT ENTITIES

			for(Entity ent : Collidable.getAllEntitiesCollidable(this))
			{
				ModelPrincipal.debugTime.startElapsedForVerbose(currentVerbose+1);
				Mouvement mouv = ent.getDeplacement();
				if(ent instanceof Heros){
					Heros h = (Heros)ent;
					mouv=h.nouvMouv;
				}
				boolean shouldBeDestroyed = ent.getNeedDestroy() || ((PartieTimer.me.getElapsedNano()-ent.tempsDetruit) > ent.TEMPS_DESTRUCTION) && ent.tempsDetruit>0;
				if(!shouldBeDestroyed)
					deplace.DeplaceObject(ent, mouv, this,currentVerbose+2);
				//Handle case where deplace set need destroy to true 
				if(shouldBeDestroyed )
					ent.destroy(this, true);
				ModelPrincipal.debugTime.elapsed("deplace entitie: ", ent.toString(), currentVerbose+1);
			}
			
			ModelPrincipal.debugTime.elapsed("deplaceEntities", currentVerbose);

			//DEPLACEMENT PROJECTILES 
			for(Projectile proj : Collidable.getAllProjectileCollidable(this))
			{
				ModelPrincipal.debugTime.startElapsedForVerbose(currentVerbose+1);
				//Pos is incorrect for Fleche when encochee hence never destroy it in that case.
				//Otherwise destroy projectile if too far out of screen 
				boolean destroyTooFar = (proj instanceof Fleche? !((Fleche)proj).encochee : true) &&
						!Collidable.objectInBoundingSquare(this,proj,CustomBoundingSquare.getScreen()); 
				boolean shouldBeDestroyed =  destroyTooFar || proj.getNeedDestroy() || ((PartieTimer.me.getElapsedNano()-proj.tempsDetruit) > proj.TEMPS_DESTRUCTION) && proj.tempsDetruit>0;
				if(!shouldBeDestroyed)
					deplace.DeplaceObject(proj, proj.getDeplacement(), this,currentVerbose+2);
				//Handle case where deplace set need destroy to true 
				if(shouldBeDestroyed )
					proj.destroy(this, true);
				ModelPrincipal.debugTime.elapsed("deplace projectile: "+ proj.toString(), currentVerbose+1);
			}

			ModelPrincipal.debugTime.elapsed("deplaceProjectiles", currentVerbose);

			//UPDATE AND DEPLACEMENT OF EFFECTS
			for(Collidable col : arrowsEffects)
			{
				ModelPrincipal.debugTime.startElapsedForVerbose(currentVerbose+1);
				Effect eff = (Effect) col;
				boolean shouldBeDestroyed = eff.getNeedDestroy() || (((PartieTimer.me.getElapsedNano()-eff.tempsDetruit) > eff.TEMPS_DESTRUCTION) && eff.tempsDetruit>0);
				boolean ended = eff.isEnded();
				if(!shouldBeDestroyed && !ended){
					deplace.DeplaceObject(eff, eff.getDeplacement(), this,currentVerbose+2);
					eff.onUpdate(this, false);
				}
				else
					eff.destroy(this, true);
				ModelPrincipal.debugTime.elapsed("deplace and update effects: "+ col.toString(), currentVerbose+1);
			}
			
			ModelPrincipal.debugTime.elapsed("update and deplacement of effects", currentVerbose);

			//COLLISION ENTITIES/PROJECTILE/EFFECTS
			List<List<Entity>> allEntities = Collidable.getAllEntitiesCollidableSeparately(this);
			List<List<Collidable>> allProjectiles = Collidable.getAllProjectileCollidableSeparately(this);

			//Compare groups of projectile between them (ie: fleche and tirMonstre)
			for(int i=0; i<(allProjectiles.size()-1);i++)
				for(int j=i+1; j<(allProjectiles.size());j++)
				{
					ModelPrincipal.debugTime.startElapsedForVerbose(currentVerbose+1);
					List<Collidable> groupI = allProjectiles.get(i);
					List<Collidable> groupJ = allProjectiles.get(j);


					for(int i_index =0; i_index<groupI.size(); i_index++)
						for(int j_index =0; j_index<groupJ.size(); j_index++){
							Collidable proj_i = groupI.get(i_index);
							Collidable proj_j = groupJ.get(j_index);
							
							if(proj_i.getNeedDestroy() || proj_j.getNeedDestroy())
								continue; 
							if(TypeObject.isMemberOf(proj_i, proj_j.getImmuneType()) || 
									(TypeObject.isMemberOf(proj_j, proj_i.getImmuneType())))
								continue;
							Collision.collisionObjects(this, proj_i, proj_j,true);//Object are warn if collision in this function
							ModelPrincipal.debugTime.elapsed("collision between projectile: ", proj_i.toString()," " , proj_i.toString(), currentVerbose+1);
						}
				}
			ModelPrincipal.debugTime.elapsed("collision between projectiles", currentVerbose);

			//Compare groups of entities and projectiles between them (ie: fleche and monstre)
			for(int i=0; i<(allEntities.size());i++)
				for(int j=0; j<(allProjectiles.size());j++)
				{
					ModelPrincipal.debugTime.startElapsedForVerbose(currentVerbose+1);
					List<Entity> groupEntitie = allEntities.get(i);
					List<Collidable> groupProjectile = allProjectiles.get(j);

					for(int i_index =0; i_index<groupEntitie.size(); i_index++){
						Entity ent = groupEntitie.get(i_index);
						for(int j_index =0; j_index<groupProjectile.size(); j_index++){
							Collidable proj = groupProjectile.get(j_index);
							
							if(ent.getNeedDestroy() || proj.getNeedDestroy())
								continue; 
							
							if((TypeObject.isMemberOf(ent,proj.getImmuneType())) 
									|| (TypeObject.isMemberOf(proj,ent.getImmuneType() )))
								continue;
							Collision.collisionObjects(this, ent, proj,true);//Object are warn if collision in this function
							ModelPrincipal.debugTime.elapsed("collision between entitie/projectile: ", ent.toString()," " , proj.toString(), currentVerbose+1);
						}
				}
				}
			ModelPrincipal.debugTime.elapsed("collision entitie/projectiles", currentVerbose);
			//COLLISION ENTITIE EFFECTS 
			for(int i=0; i<(allEntities.size());i++){
				List<Entity> groupEntitie = allEntities.get(i);
				for(int i_index =0; i_index<groupEntitie.size(); i_index++){
					ModelPrincipal.debugTime.startElapsedForVerbose(currentVerbose+1);
					Entity ent = groupEntitie.get(i_index);
					for(Collidable col : arrowsEffects)
					{
						Effect eff = (Effect) col;
						if(eff.getNeedDestroy())
							continue;
						if(!ent.currentEffects.contains(eff))
							if(Collision.testcollisionObjects(this, eff, ent,true))
							{
								eff.handleEntitieEffectCollision(ent);
							}
						//if col does not have effect and collide with it: add it 
						//Effect eff = (Effect) col;
						ModelPrincipal.debugTime.elapsed("collision between entitie/effect: ", ent.toString()," " , eff.toString(), currentVerbose+1);
					}
				}
			}
			ModelPrincipal.debugTime.elapsed("collision entitie/effects", currentVerbose);

			//Update Variables after collisions 
			nombreMonstreRestant=tabMonstre.size();
			//on met a jour le heros si il est touché avant de l'afficher
			heros.miseAjourTouche();
			heros.miseAJourSeyeri(this);


			//update conditions for all entities. Only do it at the end to be sure that all entities received the same information on the conditions
			for(Entity c : Collidable.getAllEntitiesCollidable(this))
				c.conditions.updateConditionState();

			ModelPrincipal.debugTime.elapsed("effect updates", currentVerbose);

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

			ModelPrincipal.debugTime.elapsed("object destruction preparation", currentVerbose);

			//on test si la partie est finie 

			boolean finPartieAvant= finPartie;

			finPartie= (heros.getLife()==heros.MINLIFE) || (nombreMonstreRestant==0);

			//on detecte la fin de la partie de la première fois : 
			if(!finPartieAvant &&finPartie )
			{
				AbstractModelPrincipal.changeFrame=true;
			}

		}

		computationDone=true;
		//else "pause"
		ModelPrincipal.debugTime.elapsed("end of play loop", currentVerbose);

	}



	/*void gestionEffect()
	 * NOT USED 
	{
		for(int i=0;i<arrowsEffects.size();++i)
		{
			Effect eff = (Effect) arrowsEffects.get(i);
			//if anim ended: delete
			double tempsEffect=  PartieTimer.me.getElapsedNano()-eff.tempsDetruit;

			if(!eff.isEnded() && tempsEffect>eff.TEMPS_DESTRUCTION && eff.tempsDetruit>0)
			{
				eff.destroy(this, true);
			}	
			//increment anim
			else if(!eff.isEnded() && !eff.getNeedDestroy() && (eff.tempsDetruit==0) )
			{
				eff.onUpdate(this,false);
				eff.anim=eff.animation.update(eff.anim, this.getFrame(),1);
			}
			if(eff.isEnded())
			{
				//eff.ref_fleche.OnFlecheEffectDestroy(this, true);
				eff.destroy(this, true);
			}
		}


	}*/
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
				if(monde.listMonstreOriginal.get(i).type.equals(TypeBloc.SPIREL))
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

	/*
	 * INFORMATIVE FUNCTION 
	 * public Point convertToWorld(Point pos, boolean fixedWhenScreenMoves)
	{
		if(fixedWhenScreenMoves)
			return new Point(pos.x-xScreendisp,pos.y-yScreendisp);
		else
			return pos;
	}*/
	//INPUTS 
	private boolean nextDirectionRightInBothCase = true; 
	public void keyDownAction () 
	{
		if(!finPartie)
		{
			boolean bothDirection = (inputPartie.courseDroiteDown || inputPartie.marcheDroiteDown) &&  (inputPartie.courseGaucheDown || inputPartie.marcheGaucheDown);
			boolean courseDroiteDown = inputPartie.courseDroiteDown  && (bothDirection? nextDirectionRightInBothCase : true );
			boolean marcheDroiteDown = inputPartie.marcheDroiteDown  && (bothDirection? nextDirectionRightInBothCase : true );
			boolean marcheGaucheDown = inputPartie.marcheGaucheDown  && (bothDirection? !nextDirectionRightInBothCase : true );
			boolean courseGaucheDown = inputPartie.courseGaucheDown  && (bothDirection? !nextDirectionRightInBothCase : true );

			if(inputPartie.pauseDown )
			{
				inPause=!inPause;
				inputPartie.pauseDown=false;
				if(inPause)
					inputPartie.resetTouchesFocus();
			}
			if(!inPause )
			{
				if(inputPartie.arrowDown>-1)
				{
					if(heros.current_slot != inputPartie.arrowDown)
						arrowSlotIconChanged=true;

					heros.current_slot=inputPartie.arrowDown;
				}
				boolean isDragged = this.heros.isDragged();
				//TIR 
				if( (inputPartie.toucheTirDown|| inputPartie.touche2TirDown) && !heros.flecheEncochee && !heros.doitEncocherFleche 
						&& ((System.nanoTime()-heros.last_shoot_time)>InterfaceConstantes.FLECHE_TIR_COOLDOWN))
				{
					//if not just wall jump 
					if(!(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Saut) && ((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<=InterfaceConstantes.WALL_JUMP_DISABLE_TIME)))
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
							heros.nouvMouv= new Tir(heros,TypeTirPerso.Tir,frame); 
							heros.last_armed_time=System.nanoTime();
						}
					}
				}

				boolean heros_shoots = heros.flecheEncochee||heros.doitEncocherFleche;
				boolean heros_accroche = heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Accroche);
				boolean heros_glisse = heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Glissade);
				//COURSE DROITE
				if(courseDroiteDown && !heros_glisse && !heros_accroche && !heros_shoots && !isDragged)
				{
					//si on ne courrait pas vers la droite avant
					if(! (heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Course) && heros.getAnim()>=4))
					{
						//do not run if we just wall jump
						if(! (heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Saut) && (PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<= InterfaceConstantes.WALL_JUMP_DISABLE_TIME ))
						{
							changeMouv=true;
							heros.nouvAnim= 4; 
							heros.nouvMouv= new Course(heros,TypeCourse.CourseDroite,frame); 
						}
					}
				}

				//MARCHE DROITE 
				else if(marcheDroiteDown&& !heros_shoots && !isDragged)
				{
					if(heros_glisse)
					{
						if(heros.getAnim() == 1)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(heros,heros.getGlobalVit(this).y>=0? TypeSaut.FallDroite:TypeSaut.JumpDroite,frame); 
						}
					}
					else if(heros_accroche)
					{
						//leave the border

						if(heros.getAnim() == 0)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(heros,heros.getGlobalVit(this).y>=0? TypeSaut.FallDroite:TypeSaut.JumpDroite,frame); 
						}
						//climb the border
						else if (heros.getAnim() == 2 && heros.getDeplacement().animEndedOnce())
						{
							changeMouv=true;

							heros.nouvAnim= 3; 
							heros.nouvMouv= new Accroche(heros,TypeAccroche.GrimpeDroite,frame); 						
						}
					}
					//si on courrait vers la droite en l'air ou non 
					else if((heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Course) && heros.getAnim()>=4))
					{		
						//no change mouv
					}

					//si on ne marchait pas vers la droite et qu'on est pas en l'air 
					else if(! (heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Marche) && heros.getAnim()>=4)&& heros.peutSauter)
					{
						changeMouv=true;
						heros.nouvAnim= 4; 
						heros.nouvMouv=new Marche(heros,TypeMarche.MarcheDroite,frame);
					}

					//si on veut marcher en l'air (donc vers la droite) 
					else if (!heros.peutSauter) 
					{
						//do not move if we just wall jump
						if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Saut)&& 
								(heros.getDeplacement().type_mouv != TypeSaut.LandGauche ) && (heros.getDeplacement().type_mouv != TypeSaut.LandDroite ) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)> InterfaceConstantes.WALL_JUMP_DISABLE_TIME))
						{
							changeMouv=true;
							heros.deplaceSautDroit=true; // on fait bouger le heros
							boolean fall = heros.getGlobalVit(this).y >=0 ;
							heros.nouvAnim= fall? 4 : 3 ; 
							heros.nouvMouv=new Saut(heros,fall?TypeSaut.FallDroite:TypeSaut.JumpDroite,frame);
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
				if(courseGaucheDown && !heros_glisse && !heros_accroche && !heros_shoots && !isDragged)
				{
					//si on ne courrait pas vers la gauche avant 
					if(! (heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Course) && heros.getAnim()<4))
					{
						//do not run if we just wall jump
						if(! (heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Saut) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<= InterfaceConstantes.WALL_JUMP_DISABLE_TIME)))
						{
							changeMouv=true;
							heros.nouvAnim=0;
							heros.nouvMouv= new Course(heros,TypeCourse.CourseGauche,frame);
						}
					}
				}
				//MARCHE GAUCHE 
				else if(marcheGaucheDown&& !heros_shoots && !isDragged)
				{

					if(heros_glisse)
					{
						if(heros.getAnim()==0){
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 1 : 0); 
							heros.nouvMouv= new Saut(heros,heros.getGlobalVit(this).y>=0? TypeSaut.FallGauche:TypeSaut.JumpGauche,frame); 
						}
					}
					else if (heros_accroche)
					{
						//leave the border

						if(heros.getAnim() == 2)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(heros,heros.getGlobalVit(this).y>=0? TypeSaut.FallGauche:TypeSaut.JumpGauche,frame); 
						}
						//climb the border
						else if (heros.getAnim() == 0 && heros.getDeplacement().animEndedOnce())
						{
							changeMouv=true;

							heros.nouvAnim= 1; 
							heros.nouvMouv= new Accroche(heros,TypeAccroche.GrimpeGauche,frame); 						
						}
					}

					//si on courrait vers la gauche en l'air ou non 
					else if((heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Course) && heros.getAnim()<4))
					{
						//no change
					}

					//si on ne marchait pas vers la gauche et qu'on est pas en l'air 
					else if(! (heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Marche) && heros.getAnim()<4)&& heros.peutSauter)
					{

						changeMouv=true;

						heros.nouvAnim=0;
						heros.nouvMouv= new Marche(heros,TypeMarche.MarcheGauche,frame);
					}

					//si on veut marcher en l'air (donc vers la gauche) 
					else if (!heros.peutSauter)
					{
						//do not move if we just wall jump
						if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Saut)&& 
								(heros.getDeplacement().type_mouv != TypeSaut.LandGauche ) && (heros.getDeplacement().type_mouv != TypeSaut.LandDroite ) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)> InterfaceConstantes.WALL_JUMP_DISABLE_TIME))
						{
							changeMouv=true;

							heros.deplaceSautGauche=true; // on fait bouger le heros
							boolean fall = heros.getGlobalVit(this).y >=0 ;
							heros.nouvAnim=fall? 1 : 0 ; 
							heros.nouvMouv=new Saut(heros,fall? TypeSaut.FallGauche:TypeSaut.JumpGauche,frame); 
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

						heros.nouvAnim= (heros.droite_gauche(heros.getAnim()).equals(Mouvement.GAUCHE)? 0 : 3);
						heros.nouvMouv=new Saut(heros,heros.nouvAnim==0?TypeSaut.JumpGauche:TypeSaut.JumpDroite,frame );
						heros.last_wall_jump_time=PartieTimer.me.getElapsedNano();
					}
					else if(heros_accroche && ( (heros.getAnim()==0) || (heros.getAnim()==2)))
					{
						changeMouv=true;	
						heros.sautAccroche=true;
						heros.useGravity=true;
						heros.nouvAnim= ((heros.getAnim() == 0)? 0 : 3);
						heros.nouvMouv=new Saut(heros,heros.nouvAnim==0?TypeSaut.JumpGauche:TypeSaut.JumpDroite,frame );
					}
					else if(heros.peutSauter){
						inputPartie.courseDroiteDown=false;
						inputPartie.courseGaucheDown=false;

						changeMouv=true;

						heros.peutSauter=false;


						//le heros saute donc finSaut est faux
						heros.debutSaut=true;
						heros.finSaut=false;

						heros.nouvAnim=heros.droite_gauche(heros.getAnim()).equals(Mouvement.GAUCHE) ? 0 : 3 ;
						heros.nouvMouv= new Saut(heros,heros.nouvAnim==0?TypeSaut.JumpGauche:TypeSaut.JumpDroite,frame );
					}
				}
				nextDirectionRightInBothCase = bothDirection?nextDirectionRightInBothCase : ((inputPartie.marcheDroiteDown || inputPartie.courseDroiteDown)? false : true );
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

				for(int i=0; i<tabFleche.size();i++)
				{
					Fleche f = (Fleche)tabFleche.get(i);
					if(f.encochee)
						f.OnShoot(this,deplace);
				}


				heros.nouvAnim= (heros.droite_gauche(heros.getAnim()).equals(Mouvement.GAUCHE) ? 0 : 2) ;
				heros.nouvMouv= new Attente(heros,heros.nouvAnim==0? TypeAttente.AttenteGauche:TypeAttente.AttenteDroite,frame);
				if(normal_2tir_R)
				{}
				heros.last_shoot_time= System.nanoTime();

			}

		}

		boolean just_wall_jump= heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Saut) && (((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<=InterfaceConstantes.WALL_JUMP_DISABLE_TIME));
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

			boolean heros_glisse = heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Glissade);
			boolean heros_accroche = heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Accroche);

			if( !heros_glisse && !heros_accroche && !heros.flecheEncochee )
			{
				changeMouv=true;

				//pas de decallage de sprite 

				//au sol
				if((heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Marche)|| heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Course)) && heros.peutSauter)
				{
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= (heros.droite_gauche(heros.getAnim()).equals(Mouvement.DROITE) ? 2: 0 );
					heros.nouvMouv= new Attente(heros,heros.nouvAnim==0? TypeAttente.AttenteGauche:TypeAttente.AttenteDroite,frame);

					//on met sa vitesse à 0:  
					heros.localVit.x=0;

				}

				else if (heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Attente))
				{
					//on arrete quand meme le heros (exemple si il relache la touche de deplacement sur laquelle il avait appuyé en l'air)
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= heros.droite_gauche(heros.getAnim()).equals(Mouvement.DROITE) ? 2: 0 ;
					heros.nouvMouv= new Attente(heros,heros.nouvAnim==0? TypeAttente.AttenteGauche:TypeAttente.AttenteDroite,frame);

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
						heros.nouvAnim= heros.droite_gauche(heros.getAnim()).equals(Mouvement.GAUCHE) ? 0: 3 ;

					else // le heros tombe 
						heros.nouvAnim=heros.droite_gauche(heros.getAnim()).equals(Mouvement.GAUCHE) ? 1: 4 ;

					TypeMouv type_mouv=heros.nouvAnim==0? TypeSaut.JumpGauche: (heros.nouvAnim==3?TypeSaut.JumpDroite:  (heros.nouvAnim==1?TypeSaut.FallGauche:TypeSaut.FallDroite));
					heros.nouvMouv=new Saut(heros,type_mouv,frame);

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

		//SWITCH ARROW 
		if(inputPartie.arrowReleased>-1)
			inputPartie.arrowReleased=-1;
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
	public void drawPartie(Graphics g) {

		drawMonde(g,false);
		drawMonstres(g,false);
		drawPerso(g,true);
		drawFleches(g,true);
		drawTirMonstres(g,false);
		drawEffects(g,false );
		drawInterface(g);
		
		imageDrawer.drawAll(g);
		if(debugDraw != null)
			debugDraw.draw(g);
	}


	public void drawMonde(Graphics g,boolean drawHitbox) 
	{
		int xviewport = getXYViewport(true);
		int yviewport = getXYViewport(false);

		int xStartAff = xviewport/TAILLE_BLOC-2;
		int xEndAff = (InterfaceConstantes.WINDOW_WIDTH/TAILLE_BLOC+xviewport/TAILLE_BLOC)+2;

		int yStartAff = yviewport/TAILLE_BLOC-2;
		int yEndAff = (InterfaceConstantes.WINDOW_HEIGHT/TAILLE_BLOC+yviewport/TAILLE_BLOC)+2;

		for(int abs=xStartAff;abs<xEndAff;abs++)
			for(int ord=yStartAff;ord<yEndAff;ord++)
			{
				Bloc tempPict =  monde.niveau[abs][ord];
				if(tempPict != null)
				{
					int xDraw=tempPict.getXpos()+ xScreendisp- INIT_RECT.x;
					int yDraw=tempPict.getYpos()+ yScreendisp- INIT_RECT.y;
					imageDrawer.addImage(new DrawImageItem(imMonde.getImages(tempPict.getType(),false),xDraw,yDraw, null,DrawImageHandler.MONDE));

					if(drawHitbox && tempPict.getBloquer())
					{
						Hitbox hitbox= tempPict.getHitbox(INIT_RECT,getScreenDisp());
						imageDrawer.addImage(new DrawImageItem(hitbox,getScreenDisp(),Color.RED,Color.BLACK,DrawImageHandler.MONDE));

						imageDrawer.addImage(new DrawImageItem("("+tempPict.getXpos()/TAILLE_BLOC+","+tempPict.getYpos()/TAILLE_BLOC+")",
								xDraw, yDraw+TAILLE_BLOC/2,Color.BLACK,Color.BLACK,20,DrawImageHandler.MONDE));

						
					}
				}
			}
	}

	public void drawMonstres(Graphics g,boolean drawHitbox) {

		for(Entity m : tabMonstre )
		{
			int xDraw= m.getXpos()+xScreendisp;
			int yDraw= m.getYpos()+yScreendisp;

			ArrayList<Image> images = imMonstre.getImage(m);
			for(Image im : images){
				imageDrawer.addImage(new DrawImageItem(im,xDraw,yDraw,null,DrawImageHandler.MONSTRE));
			}

			int xDraw_d= xDraw +m.getDeplacement().xtaille.get(m.getAnim());
			//draw monster lifebar
			int[] x= {(int) (xDraw/2+xDraw_d/2-m.MAXLIFE/2),(int) (xDraw/2+xDraw_d/2-m.MAXLIFE/2)};
			int[] y= {yDraw-10,yDraw-10};
			int[] width={(int) m.MAXLIFE,(int) m.getLife()};
			int[] height={5,5};
			Color[] colors = {Color.BLACK,Color.GREEN};
			imageDrawer.addImage(new DrawImageItem(x,y,width,height,colors,DrawImageHandler.MONSTRE));


			ArrayList<Condition> allcondis = m.conditions.getAllConditions();
			int xMiddle = (int) (Hitbox.getHitboxCenter(m.getHitbox(INIT_RECT,getScreenDisp())).x-m.getXpos());
			int xStartDrawCondi = m.conditions.getXStartDraw(xDraw, xMiddle, allcondis.size());
			int yStartDrawCondi = m.conditions.getYStartDraw(y[0]-25);

			for(int i =0; i<allcondis.size();++i){
				Condition condi = allcondis.get(i); 
				if(condi.blinkDisplay){
					imageDrawer.addImage(new DrawImageItem(imConditions.getImage(condi.name), xStartDrawCondi+25*i ,yStartDrawCondi ,null
							,DrawImageHandler.MONSTRE));
				}
			}


			//drawBar(g,xDraw/2+xDraw_d/2-InterfaceConstantes.MAXLIFE/2,yDraw-10,InterfaceConstantes.MAXLIFE,5,m.getLife(),Color.BLACK,Color.GREEN);
			if(drawHitbox)
			{
				Hitbox hitbox= m.getHitbox(INIT_RECT,getScreenDisp());
				imageDrawer.addImage(new DrawImageItem(hitbox,getScreenDisp(),Color.RED,Color.BLACK,DrawImageHandler.MONSTRE));
			}
		}

	}

	public Point drawPersoTir(Graphics g)
	{
		int anim = heros.getAnim();
		Point anchor = new Point(((Mouvement_entity)heros.getDeplacement()).x_rot_pos.get(anim),((Mouvement_entity)heros.getDeplacement()).y_rot_pos.get(anim));
		Point returnedAnchor = new Point(heros.getXpos()+anchor.x,heros.getYpos()+anchor.y);
		
		AffineTransform tr = getRotatedTransform(new Point(heros.getXpos(),heros.getYpos()),anchor,heros.rotation_tir);//TODO: changed

		ArrayList<Image> l_image = imHeros.getImages(heros);
		for(int i=0; i<l_image.size(); ++i)
		{
			//im_body, im_back, im_head, im_front
			if(i==0||i==2){
				imageDrawer.addImage(new DrawImageItem(l_image.get(i), heros.getXpos(),heros.getYpos(),null,DrawImageHandler.PERSO));
			}

			else{
				imageDrawer.addImage(new DrawImageItem(l_image.get(i), tr,null,DrawImageHandler.PERSO));
			}

		}
		return returnedAnchor;
	}
	public void drawPerso(Graphics g,boolean drawHitbox) {

		Point anchor = null;
		if(heros.afficheTouche)
		{
			if(heros.getDeplacement().IsDeplacement(TypeMouvEntitie.Tir))
				anchor=drawPersoTir(g);
			else
			{
				ArrayList<Image> l_image = imHeros.getImages(heros);
				for(int i=0; i<l_image.size(); ++i){
					imageDrawer.addImage(new DrawImageItem(l_image.get(i), heros.getXpos(),heros.getYpos(),null,DrawImageHandler.PERSO));
				}
			}
			ArrayList<Condition> allcondis = heros.conditions.getAllConditions();
			int xMiddle = (int) (Hitbox.getHitboxCenter(heros.getHitbox(INIT_RECT,getScreenDisp())).x-heros.getXpos())+xScreendisp;
			int xStartDrawCondi = heros.conditions.getXStartDraw(heros.getXpos(), xMiddle, allcondis.size());
			int yStartDrawCondi = heros.conditions.getYStartDraw(heros.getYpos()-25);
			for(int i =0; i<allcondis.size();++i){
				Condition condi = allcondis.get(i); 
				if(condi.blinkDisplay){
					imageDrawer.addImage(new DrawImageItem(imConditions.getImage(condi.name), xStartDrawCondi+25*i ,yStartDrawCondi ,null
							,DrawImageHandler.PERSO));
				}
			}
		}

		if(drawHitbox)
		{
			Hitbox hitbox= heros.getHitbox(INIT_RECT,getScreenDisp());
			imageDrawer.addImage(new DrawImageItem(hitbox,getScreenDisp(),Color.RED,Color.BLACK,DrawImageHandler.PERSO));
			if(anchor!=null){
				imageDrawer.addImage(new DrawImageItem("o", anchor.x, anchor.y,Color.RED,Color.BLACK,20,DrawImageHandler.PERSO));

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
			for(int i=0;i<tabFleche.size();++i) 
			{
				Fleche fleche = (Fleche) tabFleche.get(i);
				if(!fleche.isVisible)
					continue;
				
				AffineTransform tr = fleche.getDrawTr(getScreenDisp());

				ArrayList<Image> images = imFleches.getImage(fleche);
				for(Image im : images){
					imageDrawer.addImage(new DrawImageItem(im,tr,null,DrawImageHandler.FLECHE));
				}

				//Draw trail if the fleche is auto_teleguidee
				if(fleche instanceof Fleche_auto_teleguidee)
				{
					Fleche_auto_teleguidee f_auto = (Fleche_auto_teleguidee) fleche;
					f_auto.setDebug(drawHitbox);
					
					for(int f_index = 0; f_index< f_auto.trails.size();++f_index){
						Polygon trail = f_auto.trails.get(f_index);
						Polygon toDraw = new Polygon();
						for(int poly_i=0; poly_i<trail.npoints;poly_i++){
							toDraw.addPoint(trail.xpoints[poly_i]+xScreendisp, trail.ypoints[poly_i]+yScreendisp);
						}
						imageDrawer.addImage(new DrawImageItem(toDraw,f_auto.trailsColor.get(f_index),
								Color.BLACK,true,DrawImageHandler.FLECHE));
					}
					if(drawHitbox)
					{
						A_Star_Helper algo = f_auto.pathAlgo;
						if(algo != null)
						{
							ArrayList<Point> path = algo.getPath();
							if(path != null)
							{
								Point p1 = null;
								Point p2 = null;

								for(int k=-1; k<path.size()-1; ++k)
								{
									if(k==-1)
										p1 = Fleche.getArrowTip(this, f_auto, true);
									else 
										p1 = path.get(k);
	
									p2 = path.get(k+1);
									imageDrawer.addImage(new DrawImageItem(
											new Point(p1.x+xScreendisp,p1.y+yScreendisp), new Point(p2.x+xScreendisp,p2.y+yScreendisp),
											Color.RED,Color.BLACK,DrawImageHandler.INTERFACE));
								}
							
							}
						}
					}

				}
				
				if(drawHitbox)
				{
					Hitbox hitbox= fleche.getHitbox(INIT_RECT,getScreenDisp());
					imageDrawer.addImage(new DrawImageItem(hitbox,getScreenDisp(),Color.RED,Color.BLACK,DrawImageHandler.FLECHE));
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
			for(Projectile tir : tabTirMonstre)
			{
				AffineTransform tr = tir.getDrawTr(getScreenDisp());
				
				ArrayList<Image> images = imTirMonstre.getImage(tir);
				for(Image im : images){
					imageDrawer.addImage(new DrawImageItem(im,tr,null,DrawImageHandler.TIRMONSTRE));
				}

				if(drawHitbox)
				{
					Hitbox hitbox= tir.getHitbox(INIT_RECT,getScreenDisp());
					imageDrawer.addImage(new DrawImageItem(hitbox,getScreenDisp(),Color.RED,Color.BLACK,DrawImageHandler.TIRMONSTRE));
				}

			}
		}
		catch(ConcurrentModificationException e1)
		{
			e1.printStackTrace();
		}

	}

	public void drawEffects(Graphics g,boolean drawHitbox) {
		//Draw arrow effect
		for(int i =0; i< arrowsEffects.size(); ++i)
		{
			Effect eff = (Effect)arrowsEffects.get(i);


			if(eff.getNeedDestroy() || eff.isEnded())
				continue;

			AffineTransform tr = eff.getDrawTr(getScreenDisp());
			
			ArrayList<Image> images = imEffect.getImage(eff);
			for(Image im : images){
				Image im2draw = eff.applyFilter(this, im);
				int layer =  eff instanceof Trou_noir_effect ?DrawImageHandler.EFFECT_FRONT : DrawImageHandler.EFFECT;
				imageDrawer.addImage(new DrawImageItem(im2draw,tr,null,layer));
			}
			if(drawHitbox)
			{
				Hitbox hitbox= eff.getHitbox(INIT_RECT,getScreenDisp());
				imageDrawer.addImage(new DrawImageItem(hitbox,getScreenDisp(),Color.RED,Color.BLACK,DrawImageHandler.EFFECT));
			}
		}
		//Affichage du slow motion 
		if(slowDown )
		{
			//la taille de l'image fait 1500
			Image im = imEffect.getImage(ImagesEffect.SLOWDOWN);
			imageDrawer.addImage(new DrawImageItem(im, (InterfaceConstantes.WINDOW_WIDTH-im.getWidth(null))/2 ,
					(InterfaceConstantes.WINDOW_HEIGHT-im.getHeight(null))/2,null,DrawImageHandler.EFFECT));

		}


	}
	@Override
	public void drawInterface(Graphics g) {
		//AFFICHAGE DE L'INTERFACE 
		//life
		int[] x_l= {10,10};
		int[] y_l= {10,10};
		int[] width_l={(int) heros.MAXLIFE,(int) heros.getLife()};
		int[] height_l={20,20};
		Color[] colors_l = {Color.BLACK,Color.GREEN};
		imageDrawer.addImage(new DrawImageItem(x_l,y_l,width_l,height_l,colors_l,DrawImageHandler.INTERFACE));

		//seyeri
		int[] x_s= {10,10,10};
		int[] y_s= {40,40,40};
		int[] width_s={(int) InterfaceConstantes.MAXSEYERI,(int) heros.getNotEnoughSeyeri(),(int) heros.getSeyeri()};
		int[] height_s={20,20,20};
		Color[] colors_s = {Color.BLACK,Color.RED,Color.BLUE};
		imageDrawer.addImage(new DrawImageItem(x_s,y_s,width_s,height_s,colors_s,DrawImageHandler.INTERFACE));

		heros.decreaseNotEnoughSeyeriCounter();
	
		//nombre de monstre restant
		imageDrawer.addImage(new DrawImageItem("Monstres restant: "+ nombreMonstreRestant, InterfaceConstantes.WINDOW_WIDTH - 220, 20,
				Color.BLACK,Color.BLACK,20,DrawImageHandler.INTERFACE));

		//jeu en pause 
		if(inPause)
		{
			imageDrawer.addImage(new DrawImageItem("Pause",1250, 40,
					Color.BLACK,new Color(0,0,0,125),20,DrawImageHandler.INTERFACE));
			
			int[] x_p= {0};
			int[] y_p= {0};
			int[] width_p={InterfaceConstantes.WINDOW_WIDTH};
			int[] height_p={InterfaceConstantes.WINDOW_HEIGHT};
			Color[] colors_p = {new Color(0,0,0,125)};
			imageDrawer.addImage(new DrawImageItem(x_p,y_p,width_p,height_p,colors_p,DrawImageHandler.INTERFACE));
		}

		if(finPartie)
		{
			if(heros.getLife()==heros.MINLIFE)
			{
				imageDrawer.addImage(new DrawImageItem("DEFAITE",InterfaceConstantes.WINDOW_WIDTH/2-20, InterfaceConstantes.WINDOW_HEIGHT/4,
						Color.BLACK,Color.BLACK,40,DrawImageHandler.INTERFACE));
			}
			else if(nombreMonstreRestant==0)
			{
				imageDrawer.addImage(new DrawImageItem("VICTOIRE",InterfaceConstantes.WINDOW_WIDTH/2-20, InterfaceConstantes.WINDOW_HEIGHT/4,
						Color.BLACK,Color.BLACK,40,DrawImageHandler.INTERFACE));
			}
		}

	}



	/*public AffineTransform getRotatedTransform(Point pos, Point anchor, Point taille, double rotation)
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
	}*/


	//used for grappin effect 
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

	//used for roche effect 
	public BufferedImage apply_height_mask(BufferedImage original,BufferedImage previousMaskedIm, int h_start_mask,float transparency)
	{
		int width = original.getWidth();
		int height = original.getHeight();
		if(width==-1 || height == -1)
			return original;

		int desired_alpha = (int)(255* transparency);
		Color c = new Color(desired_alpha,desired_alpha,desired_alpha);
		Color nullC = new Color(255-desired_alpha,255-desired_alpha,255-desired_alpha);


		//make the first part transparent (ie visible)
		for(int i =0; i<width;i++){
			for(int j=0;j<h_start_mask;j++){
				int current_rgb = original.getRGB(i, j);
				int color = current_rgb & 0x00ffffff; // Mask preexisting alpha
				int prev_alpha = current_rgb & 0xff000000;
				int alpha = prev_alpha & (c.getRGB() << 24); // Shift blue to alpha
				previousMaskedIm.setRGB(i, j, (color | alpha));
			}
		}
		
		//make the second part invisible 
		for(int i =0; i<width;i++){
			for(int j=h_start_mask;j<height;j++){
				int current_rgb = original.getRGB(i, j);
				int color = current_rgb & 0x00ffffff; // Mask preexisting alpha
				int prev_alpha = current_rgb & 0xff000000;
				int alpha = prev_alpha & (nullC.getRGB() << 24); // Shift blue to alpha
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
