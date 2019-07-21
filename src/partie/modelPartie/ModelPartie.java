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

import ActiveJComponent.ActiveJButton;
import Affichage.DrawImageHandler;
import Affichage.DrawImageItem;
import Affichage.GameRenderer;
import debug.DebugTime;
import gameConfig.Destroyable;
import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import images.ImagesBackground.ImBackgroundInfo;
import images.ImagesContainer.ImageGroup;
import images.ImagesEffect.ImEffectInfo;
import images.ImagesFlecheIcon;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameHandler.GameModeType;
import menu.menuPrincipal.GameMode;
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
import partie.deplacement.Mouvement.DirSubTypeMouv;
import partie.deplacement.Mouvement.SubTypeMouv;
import partie.deplacement.entity.Accroche;
import partie.deplacement.entity.Accroche.SubMouvAccrocheEnum;
import partie.deplacement.entity.Attente;
import partie.deplacement.entity.Course;
import partie.deplacement.entity.Marche;
import partie.deplacement.entity.Mouvement_entity;
import partie.deplacement.entity.Mouvement_entity.MouvEntityEnum;
import partie.deplacement.entity.Saut;
import partie.deplacement.entity.Saut.SubMouvSautEnum;
import partie.deplacement.entity.Tir;
import partie.effects.Effect;
import partie.effects.Trou_noir_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.entitie.monstre.Spirel;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;
import partie.projectile.fleches.rusee.Fleche_marque_mortelle;
import serialize.Serialize;

public class ModelPartie extends AbstractModelPartie{

	public ModelPartie(Touches _touches,GameHandler gameHandler)
	{
		this.gameHandler=gameHandler;
		touches = _touches;
		
		imBackground = gameHandler.getImageGroup(ImageGroup.BACKGROUND);
		imMonde= gameHandler.getImageGroup(ImageGroup.MONDE);
		imMonstre= gameHandler.getImageGroup(ImageGroup.MONSTRE);
		imHeros= gameHandler.getImageGroup(ImageGroup.HEROS);
		imTirMonstre= gameHandler.getImageGroup(ImageGroup.TIRMONSTRE);
		imFleches= gameHandler.getImageGroup(ImageGroup.FLECHE);
		imEffect= gameHandler.getImageGroup(ImageGroup.EFFECT);
		imConditions= gameHandler.getImageGroup(ImageGroup.CONDITION);
		imFlecheIcon= (ImagesFlecheIcon) gameHandler.getImageGroup(ImageGroup.FLECHEICON); //need cast to access specific functions
		
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
		
		//Force creation of heros hitbox
		final Hitbox herosHit = heros.getHitbox(INIT_RECT, getScreenDisp());
		//Force creation of bloc hitbox
		for(Collidable bloc: Collision.getMondeBlocs(monde, herosHit, INIT_RECT, getScreenDisp(), InterfaceConstantes.TAILLE_BLOC))
			bloc.getHitbox(INIT_RECT, getScreenDisp());//force creation of hitbox
		//Force creation of entity hitbox
		for(Entity ent : Collidable.getAllEntitiesCollidable(this,heros))
			ent.getHitbox(INIT_RECT, getScreenDisp());//force creation of hitbox
		
		ModelPrincipal.debugTime = new DebugTime(InterfaceConstantes.DEBUG_TIME_LOOP_TO_SLOW,InterfaceConstantes.DEBUG_TIME_ACTION_TO_SLOW,InterfaceConstantes.DEBUG_TIME_VERBOSE);
		
	}

	/**
	 * Boucle de jeu
	 * 
	 * @param affich, la JFrame a afficher
	 */	
	public void play(GameRenderer affich) 
	{
		while(!affich.isFocused() && Config.pauseWhenLooseFocus)
		{
			if(firstNonFocused)
			{
				inputPartie.resetTouchesFocus();
				inPause=true;
				Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);
				keyAction();
				//REMOVE AbstractModelPrincipal.changeFrame=true;
				affich.changeGameModeRendering();

				firstNonFocused=false;
			}

		}
		firstNonFocused=true;
		ModelPrincipal.debugTime.startElapsedForVerbose();
		//int x= heros.xPos + heros.deplacement.xdecallsprite[heros.anim]; //la vrai position du heros necessite encore un - variablesPartieRapide.xdeplaceEcran
		//int y= heros.yPos+ heros.deplacement.ydecallsprite[heros.anim]; 

		//on efface les qui doivent �tre d�truit 

		//on desactive la touche cap au cas ou elle serait utilis�e
		Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);

		keyAction();

		ModelPrincipal.debugTime.elapsed("action");

		//Lors d'une pause, on ne veut pas r�initaliser la partie en cours mais juste y acc�der � nouveau
		if(!inPause && !finPartie)
		{
			//First action is to delete since we want the user to see the colliding objects for at least 1 frame 
			if(!lEffaceFleche.isEmpty()){
				deleteObject(lEffaceFleche,tabFleche);
				lEffaceFleche.clear();
			}
			if(!lEffaceTirMonstre.isEmpty()){
				deleteObject(lEffaceTirMonstre,tabTirMonstre);
				lEffaceTirMonstre.clear();
			}
			if(!lEffaceMonstre.isEmpty()){
				deleteObject(lEffaceMonstre,tabMonstre);
				lEffaceMonstre.clear();
			}
			if(!lEffaceEffect.isEmpty()){
				deleteObject(lEffaceEffect,arrowsEffects);
				lEffaceEffect.clear();
			}
			
			ModelPrincipal.debugTime.elapsed("delete");

			//DEPLACEMENT ENTITIES

			for(Entity ent : Collidable.getAllEntitiesCollidable(this))
			{
				ModelPrincipal.debugTime.startElapsedForVerbose();
				Mouvement mouv = ent.getDeplacement();
				if(ent instanceof Heros){
					Heros h = (Heros)ent;
					mouv=h.nouvMouv;
				}
				boolean shouldBeDestroyed = ent.getNeedDestroy() || ((PartieTimer.me.getElapsedNano()-ent.tempsDetruit) > ent.TEMPS_DESTRUCTION) && ent.tempsDetruit>0;
				if(!shouldBeDestroyed)
					deplace.DeplaceObject(ent, mouv, this);
				//Handle case where deplace set need destroy to true 
				if(shouldBeDestroyed )
					ent.destroy(this, true);
				ModelPrincipal.debugTime.elapsed("deplace entitie: ", ent.toString());
			}
			
			ModelPrincipal.debugTime.elapsed("deplaceEntities");

			//DEPLACEMENT PROJECTILES 
			for(Projectile proj : Collidable.getAllProjectileCollidable(this))
			{
				ModelPrincipal.debugTime.startElapsedForVerbose();
				//Pos is incorrect for Fleche when encochee hence never destroy it in that case.
				//Otherwise destroy projectile if too far out of screen 
				boolean destroyTooFar = (proj instanceof Fleche? !((Fleche)proj).encochee : true) &&
						!Collidable.objectInBoundingSquare(this,proj,CustomBoundingSquare.getScreen()); 
				boolean shouldBeDestroyed =  destroyTooFar || proj.getNeedDestroy() || ((PartieTimer.me.getElapsedNano()-proj.tempsDetruit) > proj.TEMPS_DESTRUCTION) && proj.tempsDetruit>0;
				if(!shouldBeDestroyed)
					deplace.DeplaceObject(proj, proj.getDeplacement(), this);
				//Handle case where deplace set need destroy to true 
				if(shouldBeDestroyed )
					proj.destroy(this, true);
				ModelPrincipal.debugTime.elapsed("deplace projectile: "+ proj.toString());
			}

			ModelPrincipal.debugTime.elapsed("deplaceProjectiles");

			//UPDATE AND DEPLACEMENT OF EFFECTS
			for(Collidable col : arrowsEffects)
			{
				ModelPrincipal.debugTime.startElapsedForVerbose();
				Effect eff = (Effect) col;
				boolean shouldBeDestroyed = eff.getNeedDestroy() || (((PartieTimer.me.getElapsedNano()-eff.tempsDetruit) > eff.TEMPS_DESTRUCTION) && eff.tempsDetruit>0);
				boolean ended = eff.isEnded();
				if(!shouldBeDestroyed && !ended){
					deplace.DeplaceObject(eff, eff.getDeplacement(), this);
					eff.onUpdate(this, false);
				}
				else
					eff.destroy(this, true);
				ModelPrincipal.debugTime.elapsed("deplace and update effects: "+ col.toString() );
			}
			
			ModelPrincipal.debugTime.elapsed("update and deplacement of effects");

			//COLLISION ENTITIES/PROJECTILE/EFFECTS
			List<List<Entity>> allEntities = Collidable.getAllEntitiesCollidableSeparately(this);
			List<List<Collidable>> allProjectiles = Collidable.getAllProjectileCollidableSeparately(this);

			//Compare groups of projectile between them (ie: fleche and tirMonstre)
			for(int i=0; i<(allProjectiles.size()-1);i++)
				for(int j=i+1; j<(allProjectiles.size());j++)
				{
					ModelPrincipal.debugTime.startElapsedForVerbose();
					List<Collidable> groupI = allProjectiles.get(i);
					List<Collidable> groupJ = allProjectiles.get(j);


					for(int i_index =0; i_index<groupI.size(); i_index++)
						for(int j_index =0; j_index<groupJ.size(); j_index++){
							Collidable proj_i = groupI.get(i_index);
							Collidable proj_j = groupJ.get(j_index);
							
							if(proj_i.getNeedDestroy() || proj_j.getNeedDestroy())
								continue; 
							if(ObjectTypeHelper.isMemberOf(proj_i, proj_j.getImmuneType()) || 
									(ObjectTypeHelper.isMemberOf(proj_j, proj_i.getImmuneType())))
								continue;
							Collision.collisionObjects(this, proj_i, proj_j,true);//Object are warn if collision in this function
							ModelPrincipal.debugTime.elapsed("collision between projectile: ", proj_i.toString()," " , proj_i.toString());
						}
				}
			ModelPrincipal.debugTime.elapsed("collision between projectiles");

			//Compare groups of entities and projectiles between them (ie: fleche and monstre)
			for(int i=0; i<(allEntities.size());i++)
				for(int j=0; j<(allProjectiles.size());j++)
				{
					ModelPrincipal.debugTime.startElapsedForVerbose();
					List<Entity> groupEntitie = allEntities.get(i);
					List<Collidable> groupProjectile = allProjectiles.get(j);

					for(int i_index =0; i_index<groupEntitie.size(); i_index++){
						Entity ent = groupEntitie.get(i_index);
						for(int j_index =0; j_index<groupProjectile.size(); j_index++){
							Collidable proj = groupProjectile.get(j_index);
							
							if(ent.getNeedDestroy() || proj.getNeedDestroy())
								continue; 
							
							if((ObjectTypeHelper.isMemberOf(ent,proj.getImmuneType())) 
									|| (ObjectTypeHelper.isMemberOf(proj,ent.getImmuneType() )))
								continue;
							Collision.collisionObjects(this, ent, proj,true);//Object are warn if collision in this function
							ModelPrincipal.debugTime.elapsed("collision between entitie/projectile: ", ent.toString()," " , proj.toString() );
						}
				}
				}
			ModelPrincipal.debugTime.elapsed("collision entitie/projectiles");
			//COLLISION ENTITIE EFFECTS 
			for(int i=0; i<(allEntities.size());i++){
				List<Entity> groupEntitie = allEntities.get(i);
				for(int i_index =0; i_index<groupEntitie.size(); i_index++){
					ModelPrincipal.debugTime.startElapsedForVerbose();
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
						ModelPrincipal.debugTime.elapsed("collision between entitie/effect: ", ent.toString()," " , eff.toString());
					}
				}
			}
			ModelPrincipal.debugTime.elapsed("collision entitie/effects");

			//Update Variables after collisions 
			nombreMonstreRestant=tabMonstre.size();
			//on met a jour le heros si il est touch� avant de l'afficher
			heros.miseAjourTouche();
			heros.miseAJourSeyeri(this);


			//update conditions for all entities. Only do it at the end to be sure that all entities received the same information on the conditions
			for(Entity c : Collidable.getAllEntitiesCollidable(this))
				c.conditions.updateConditionState();

			ModelPrincipal.debugTime.elapsed("effect updates");

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

			ModelPrincipal.debugTime.elapsed("object destruction preparation");

			//on test si la partie est finie 

			boolean finPartieAvant= finPartie;

			finPartie= (heros.getLife()==heros.MINLIFE) || (nombreMonstreRestant==0);

			//on detecte la fin de la partie de la premi�re fois : 
			if(!finPartieAvant &&finPartie )
			{
				//REMOVE gameHandler.updateGraphics();
				//REMOVE AbstractModelPrincipal.changeFrame=true;
			}

		}
		
		isFirstFrameReady=true;
		//else "pause"
		ModelPrincipal.debugTime.elapsed("end of play loop");
	}



	
	<T extends Destroyable> void deleteObject(List<Integer> indexList, List<T> objectList )
	{
		ModelPrincipal.debugTime.startElapsedForVerbose();
		Collections.sort(indexList);
		ModelPrincipal.debugTime.elapsed("deleteObject: sort");
		for(int i=0; i<indexList.size(); i++)
		{
			objectList.get(indexList.get(i)-i).onDestroy(this);
			ModelPrincipal.debugTime.elapsed("deleteObject: on destroy");
			objectList.remove(indexList.get(i)-i);
			ModelPrincipal.debugTime.elapsed("deleteObject: remove");
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
					//coordon�es al�atoire pour placer le monstre
					x=(int) (Math.random()*(monde.xEndMap-monde.xStartMap));
					y=(int) (Math.random()*(monde.yEndMap-monde.yStartMap));

					x= monde.xStartMap- INIT_RECT.x +x;
					y= monde.yStartMap- INIT_RECT.y +y;
					//on cr�er le monstre � faire apparaitre
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
			final boolean bothDirection = (inputPartie.courseDroiteDown || inputPartie.marcheDroiteDown) &&  (inputPartie.courseGaucheDown || inputPartie.marcheGaucheDown);
			final boolean courseDroiteDown = inputPartie.courseDroiteDown  && (bothDirection? nextDirectionRightInBothCase : true );
			final boolean marcheDroiteDown = inputPartie.marcheDroiteDown  && (bothDirection? nextDirectionRightInBothCase : true );
			final boolean marcheGaucheDown = inputPartie.marcheGaucheDown  && (bothDirection? !nextDirectionRightInBothCase : true );
			final boolean courseGaucheDown = inputPartie.courseGaucheDown  && (bothDirection? !nextDirectionRightInBothCase : true );

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
					if(!(heros.getDeplacement().IsDeplacement(MouvEntityEnum.SAUT) && ((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<=InterfaceConstantes.WALL_JUMP_DISABLE_TIME)))
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
							heros.nouvMouv= new Tir(ObjectType.HEROS,null,frame); 
							heros.last_armed_time=System.nanoTime();
						}
					}
				}

				boolean heros_shoots = heros.flecheEncochee||heros.doitEncocherFleche;
				boolean heros_accroche = heros.getDeplacement().IsDeplacement(MouvEntityEnum.ACCROCHE);
				boolean heros_glisse = heros.getDeplacement().IsDeplacement(MouvEntityEnum.GLISSADE);
				//COURSE DROITE
				if(courseDroiteDown && !heros_glisse && !heros_accroche && !heros_shoots && !isDragged)
				{
					//si on ne courrait pas vers la droite avant
					if(! (heros.getDeplacement().IsDeplacement(MouvEntityEnum.COURSE) && heros.getAnim()>=4))
					{
						//do not run if we just wall jump
						if(! (heros.getDeplacement().IsDeplacement(MouvEntityEnum.SAUT) && (PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<= InterfaceConstantes.WALL_JUMP_DISABLE_TIME ))
						{
							changeMouv=true;
							heros.nouvAnim= 4; 
							heros.nouvMouv= new Course(ObjectType.HEROS,DirSubTypeMouv.DROITE,frame); 
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
							heros.nouvMouv= new Saut(ObjectType.HEROS,heros.getGlobalVit(this).y>=0? SubMouvSautEnum.FALL_DROITE:SubMouvSautEnum.JUMP_DROITE,frame); 
						}
					}
					else if(heros_accroche)
					{
						//leave the border

						if(heros.getAnim() == 0)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(ObjectType.HEROS,heros.getGlobalVit(this).y>=0? SubMouvSautEnum.FALL_DROITE:SubMouvSautEnum.JUMP_DROITE,frame); 
						}
						//climb the border
						else if (heros.getAnim() == 2 && heros.getDeplacement().animEndedOnce())
						{
							changeMouv=true;

							heros.nouvAnim= 3; 
							heros.nouvMouv= new Accroche(ObjectType.HEROS,SubMouvAccrocheEnum.GRIMPE_DROITE,frame); 						
						}
					}
					//si on courrait vers la droite en l'air ou non 
					else if((heros.getDeplacement().IsDeplacement(MouvEntityEnum.COURSE) && heros.getAnim()>=4))
					{		
						//no change mouv
					}

					//si on ne marchait pas vers la droite et qu'on est pas en l'air 
					else if(! (heros.getDeplacement().IsDeplacement(MouvEntityEnum.MARCHE) && heros.getAnim()>=4)&& heros.peutSauter)
					{
						changeMouv=true;
						heros.nouvAnim= 4; 
						heros.nouvMouv=new Marche(ObjectType.HEROS,DirSubTypeMouv.DROITE,frame);
					}

					//si on veut marcher en l'air (donc vers la droite) 
					else if (!heros.peutSauter) 
					{
						//do not move if we just wall jump
						if(heros.getDeplacement().IsDeplacement(MouvEntityEnum.SAUT)&& 
								(!heros.isDeplacement(MouvEntityEnum.SAUT, SubMouvSautEnum.LAND_GAUCHE)) && (!heros.isDeplacement(MouvEntityEnum.SAUT, SubMouvSautEnum.LAND_DROITE)) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)> InterfaceConstantes.WALL_JUMP_DISABLE_TIME))
						{
							changeMouv=true;
							heros.deplaceSautDroit=true; // on fait bouger le heros
							boolean fall = heros.getGlobalVit(this).y >=0 ;
							heros.nouvAnim= fall? 4 : 3 ; 
							heros.nouvMouv=new Saut(ObjectType.HEROS,fall?SubMouvSautEnum.FALL_DROITE:SubMouvSautEnum.JUMP_DROITE,frame);
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
				if(courseGaucheDown && !heros_glisse && !heros_accroche && !heros_shoots && !isDragged)
				{
					//si on ne courrait pas vers la gauche avant 
					if(! (heros.getDeplacement().IsDeplacement(MouvEntityEnum.COURSE) && heros.getAnim()<4))
					{
						//do not run if we just wall jump
						if(! (heros.getDeplacement().IsDeplacement(MouvEntityEnum.SAUT) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<= InterfaceConstantes.WALL_JUMP_DISABLE_TIME)))
						{
							changeMouv=true;
							heros.nouvAnim=0;
							heros.nouvMouv= new Course(ObjectType.HEROS,DirSubTypeMouv.GAUCHE,frame);
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
							heros.nouvMouv= new Saut(ObjectType.HEROS,heros.getGlobalVit(this).y>=0? SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.JUMP_GAUCHE,frame); 
						}
					}
					else if (heros_accroche)
					{
						//leave the border

						if(heros.getAnim() == 2)
						{
							changeMouv=true;

							heros.nouvAnim= (heros.getGlobalVit(this).y>=0 ? 4 : 3); 
							heros.nouvMouv= new Saut(ObjectType.HEROS,heros.getGlobalVit(this).y>=0? SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.JUMP_GAUCHE,frame); 
						}
						//climb the border
						else if (heros.getAnim() == 0 && heros.getDeplacement().animEndedOnce())
						{
							changeMouv=true;

							heros.nouvAnim= 1; 
							heros.nouvMouv= new Accroche(ObjectType.HEROS,SubMouvAccrocheEnum.GRIMPE_GAUCHE,frame); 						
						}
					}

					//si on courrait vers la gauche en l'air ou non 
					else if((heros.getDeplacement().IsDeplacement(MouvEntityEnum.COURSE) && heros.getAnim()<4))
					{
						//no change
					}

					//si on ne marchait pas vers la gauche et qu'on est pas en l'air 
					else if(! (heros.getDeplacement().IsDeplacement(MouvEntityEnum.MARCHE) && heros.getAnim()<4)&& heros.peutSauter)
					{

						changeMouv=true;

						heros.nouvAnim=0;
						heros.nouvMouv= new Marche(ObjectType.HEROS,DirSubTypeMouv.GAUCHE,frame);
					}

					//si on veut marcher en l'air (donc vers la gauche) 
					else if (!heros.peutSauter)
					{
						//do not move if we just wall jump
						if(heros.getDeplacement().IsDeplacement(MouvEntityEnum.SAUT)&& 
								(!heros.isDeplacement(MouvEntityEnum.SAUT,SubMouvSautEnum.LAND_GAUCHE)) && (!heros.isDeplacement(MouvEntityEnum.SAUT,SubMouvSautEnum.LAND_DROITE)) && 
								((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)> InterfaceConstantes.WALL_JUMP_DISABLE_TIME))
						{
							changeMouv=true;

							heros.deplaceSautGauche=true; // on fait bouger le heros
							boolean fall = heros.getGlobalVit(this).y >=0 ;
							heros.nouvAnim=fall? 1 : 0 ; 
							heros.nouvMouv=new Saut(ObjectType.HEROS,fall? SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.JUMP_GAUCHE,frame); 
						}
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
				//si le heros saute pour la premi�re fois et qu'il peut sauter et qu'il ne glisse pas
				if(inputPartie.sautDown &&  !heros_shoots && !isDragged)
				{
					if(heros_glisse)
					{
						changeMouv=true;	
						heros.sautGlisse=true;

						heros.nouvAnim= (heros.droite_gauche(heros.getAnim()).equals(DirSubTypeMouv.GAUCHE)? 0 : 3);
						heros.nouvMouv=new Saut(ObjectType.HEROS,heros.nouvAnim==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,frame );
						heros.last_wall_jump_time=PartieTimer.me.getElapsedNano();
					}
					else if(heros_accroche && ( (heros.getAnim()==0) || (heros.getAnim()==2)))
					{
						changeMouv=true;	
						heros.sautAccroche=true;
						heros.useGravity=true;
						heros.nouvAnim= ((heros.getAnim() == 0)? 0 : 3);
						heros.nouvMouv=new Saut(ObjectType.HEROS,heros.nouvAnim==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,frame );
					}
					else if(heros.peutSauter){
						inputPartie.courseDroiteDown=false;
						inputPartie.courseGaucheDown=false;

						changeMouv=true;

						heros.peutSauter=false;


						//le heros saute donc finSaut est faux
						heros.debutSaut=true;
						heros.finSaut=false;

						heros.nouvAnim=heros.droite_gauche(heros.getAnim()).equals(DirSubTypeMouv.GAUCHE) ? 0 : 3 ;
						heros.nouvMouv= new Saut(ObjectType.HEROS,heros.nouvAnim==0?SubMouvSautEnum.JUMP_GAUCHE:SubMouvSautEnum.JUMP_DROITE,frame );
					}
				}
				nextDirectionRightInBothCase = bothDirection?nextDirectionRightInBothCase : ((inputPartie.marcheDroiteDown || inputPartie.courseDroiteDown)? false : true );
				//touches pour lesquels maintenir appuy� ne change rien
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
		final boolean normal_tir_R= inputPartie.toucheTirReleased;//left click
		final boolean normal_2tir_R= inputPartie.touche2TirReleased; //right click

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


				heros.nouvAnim= (heros.droite_gauche(heros.getAnim()).equals(DirSubTypeMouv.GAUCHE) ? 0 : 2) ;
				heros.nouvMouv= new Attente(ObjectType.HEROS,heros.nouvAnim==0? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,frame);
				if(normal_2tir_R)
				{}
				heros.last_shoot_time= System.nanoTime();

			}

		}

		final boolean just_wall_jump= heros.getDeplacement().IsDeplacement(MouvEntityEnum.SAUT) && (((PartieTimer.me.getElapsedNano()-heros.last_wall_jump_time)<=InterfaceConstantes.WALL_JUMP_DISABLE_TIME));
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

			final boolean heros_glisse = heros.getDeplacement().IsDeplacement(MouvEntityEnum.GLISSADE);
			final boolean heros_accroche = heros.getDeplacement().IsDeplacement(MouvEntityEnum.ACCROCHE);

			if( !heros_glisse && !heros_accroche && !heros.flecheEncochee )
			{
				changeMouv=true;

				//pas de decallage de sprite 

				//au sol
				if((heros.getDeplacement().IsDeplacement(MouvEntityEnum.MARCHE)|| heros.getDeplacement().IsDeplacement(MouvEntityEnum.COURSE)) && heros.peutSauter)
				{
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= (heros.droite_gauche(heros.getAnim()).equals(DirSubTypeMouv.DROITE) ? 2: 0 );
					heros.nouvMouv= new Attente(ObjectType.HEROS,heros.nouvAnim==0? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,frame);

					//on met sa vitesse � 0:  
					heros.localVit.x=0;

				}

				else if (heros.getDeplacement().IsDeplacement(MouvEntityEnum.ATTENTE))
				{
					//on arrete quand meme le heros (exemple si il relache la touche de deplacement sur laquelle il avait appuy� en l'air)
					changeMouv=true;
					//on variablesPartieRapide.affiche l'animation d'attente

					heros.nouvAnim= heros.droite_gauche(heros.getAnim()).equals(DirSubTypeMouv.DROITE) ? 2: 0 ;
					heros.nouvMouv= new Attente(ObjectType.HEROS,heros.nouvAnim==0? DirSubTypeMouv.GAUCHE:DirSubTypeMouv.DROITE,frame);

					//on met sa vitesse � 0:  
					heros.localVit.x=0;
				}
				//en l'air et glisse pas
				else if(!heros.peutSauter)
				{

					heros.localVit.x=0;
					changeMouv=true;
					// tout d�pend si le heros tombe ou non 

					if (heros.getGlobalVit(this).y<0)//il ne tombe pas donc on met les premi�res animations de saut
						heros.nouvAnim= heros.droite_gauche(heros.getAnim()).equals(DirSubTypeMouv.GAUCHE) ? 0: 3 ;

					else // le heros tombe 
						heros.nouvAnim=heros.droite_gauche(heros.getAnim()).equals(DirSubTypeMouv.GAUCHE) ? 1: 4 ;

					SubTypeMouv type_mouv=heros.nouvAnim==0? SubMouvSautEnum.JUMP_GAUCHE: (heros.nouvAnim==3?SubMouvSautEnum.JUMP_DROITE:  (heros.nouvAnim==1?SubMouvSautEnum.FALL_GAUCHE:SubMouvSautEnum.FALL_DROITE));
					heros.nouvMouv=new Saut(ObjectType.HEROS,type_mouv,frame);

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

	public void HandleBoutonsPressed(ActiveJButton button) {
		if(button.getText().equals("Option"))
		{
			setAffichageOption=true;
			notifyObserver();

		}
		else if(button.getText().equals("Quitter"))
		{
			//REMOVE AbstractModelPrincipal.changeFrame=true; //REMOVE
			//REMOVE AbstractModelPrincipal.modeSuivant="Quitter";

			if(inPause)
			{
				//on arrete la partie pour sortir du Thread d'affichage de partie rapide
				finPartie=true;
				//on choisit de ne pas afficher la fin de la partie(elle n'est en r�alit� pas finie
				//REMOVE AbstractModelPrincipal.changeFrame=false;
			}
			//REMOVE sAbstractModelPrincipal.changeMode=true; 
			gameHandler.setGameMode(GameModeType.QUIT);
		}
		else if(button.getText().equals("Rejouer"))
		{
			
			//REMOVE AbstractModelPrincipal.modeSuivant="Partie";
			finPartie=false;
			//REMOVE AbstractModelPrincipal.changeFrame=true;
			//REMOVE AbstractModelPrincipal.changeMode=true; 

			disableBoutonsFin=true;
			notifyObserver();
			gameHandler.setGameMode(GameModeType.GAME);

		}
		else if(button.getText().equals("Menu Principal"))
		{
			//REMOVE AbstractModelPrincipal.modeSuivant="Principal";
			//REMOVE AbstractModelPrincipal.changeFrame=true;//REMOVE 
			finPartie=true;
			//REMOVE AbstractModelPrincipal.changeMode=true; 

			disableBoutonsFin=true;
			notifyObserver();
			gameHandler.setGameMode(GameModeType.MAIN_MENU);

		}

		else if(button.getText().equals("Reprendre"))
		{
			inPause=false;
			//REMOVE gameHandler.updateGraphics();
			//REMOVE AbstractModelPrincipal.changeFrame=true;
		}
	}

	/**
	 * Permet de reinitialiser tout les appuis de touches, utile notamment lors de la perte de focus de la fenetre
	 */	

	public void precomputeDraw()
	{
		imageDrawer.clearImages();
		drawBackground();
		drawMonde(false);
		drawMonstres(true);
		drawPerso(false);
		drawFleches(true);
		drawTirMonstres(false);
		drawEffects(false);
		drawInterface();
		imageDrawer.sortImages();
		
		updateSwing();
	}

	//DRAW
	public void drawPartie(Graphics g) {

		imageDrawer.drawAll(g);
		if(debugDraw != null)
			debugDraw.draw(g);

	}
	public void drawBackground()
	{
		imageDrawer.addImage(new DrawImageItem(imBackground.getImage(null,ImBackgroundInfo.WHITE,null), 0 ,0 ,null,DrawImageHandler.BACKGROUND));
	}
	
	public void drawMonde(boolean drawHitbox) 
	{
		int xviewport = getXYViewport(true);
		int yviewport = getXYViewport(false);

		int xStartAff = xviewport/TAILLE_BLOC-2;
		int xEndAff = (InterfaceConstantes.WINDOW_WIDTH/TAILLE_BLOC+xviewport/TAILLE_BLOC)+2;

		int yStartAff = yviewport/TAILLE_BLOC-2;
		int yEndAff = (InterfaceConstantes.WINDOW_HEIGHT/TAILLE_BLOC+yviewport/TAILLE_BLOC)+2;
		
		int xDraw;
		int yDraw;
		for(int abs=xStartAff;abs<xEndAff;abs++)
			for(int ord=yStartAff;ord<yEndAff;ord++)
			{
				Bloc tempPict =  monde.niveau[abs][ord];
				if(tempPict != null)
				{
					xDraw=tempPict.getXpos()+ xScreendisp- INIT_RECT.x;
					yDraw=tempPict.getYpos()+ yScreendisp- INIT_RECT.y;
					imageDrawer.addImage(new DrawImageItem(imMonde.getImage(null,tempPict.getType(),null),xDraw,yDraw, null,DrawImageHandler.MONDE));

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

	public void drawMonstres(boolean drawHitbox) {

		for(Entity m : tabMonstre )
		{
			int xDraw= m.getXpos()+xScreendisp;
			int yDraw= m.getYpos()+yScreendisp;

			ArrayList<Image> images = imMonstre.getImages(m.objType,m.getTypeMouv(),null,m.getAnim());
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
					imageDrawer.addImage(new DrawImageItem(imConditions.getImage(null,condi.type,null), xStartDrawCondi+25*i ,yStartDrawCondi ,null
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

	public Point drawPersoTir()
	{
		int anim = heros.getAnim();
		Point anchor = new Point(((Mouvement_entity)heros.getDeplacement()).x_rot_pos.get(anim),((Mouvement_entity)heros.getDeplacement()).y_rot_pos.get(anim));
		Point returnedAnchor = new Point(heros.getXpos()+anchor.x,heros.getYpos()+anchor.y);
		
		AffineTransform tr = getRotatedTransform(new Point(heros.getXpos(),heros.getYpos()),anchor,heros.rotation_tir);//TODO: changed

		ArrayList<Image> l_image = imHeros.getImages(null,heros.getTypeMouv(),null,heros.getAnim());
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
	public void drawPerso(boolean drawHitbox) {

		Point anchor = null;
		if(heros.afficheTouche)
		{
			if(heros.getDeplacement().IsDeplacement(MouvEntityEnum.TIR))
				anchor=drawPersoTir();
			else
			{
				ArrayList<Image> l_image = imHeros.getImages(null,heros.getTypeMouv(),null,heros.getAnim());
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
					imageDrawer.addImage(new DrawImageItem(imConditions.getImage(null,condi.type,null), xStartDrawCondi+25*i ,yStartDrawCondi ,null
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


	public void drawFleches(boolean drawHitbox) {
		//Affichage des fl�ches
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

				ArrayList<Image> images = imFleches.getImages(fleche.objType,null,null,fleche.getAnim());
				for(Image im : images){
					imageDrawer.addImage(new DrawImageItem(im,tr,null,DrawImageHandler.FLECHE));
				}

				//Draw trail if the fleche is marque_mortelle
				if(fleche instanceof Fleche_marque_mortelle)
				{
					Fleche_marque_mortelle f_auto = (Fleche_marque_mortelle) fleche;
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

	public void drawTirMonstres(boolean drawHitbox) {
		//Affichage des tirs de monstres 
		try{
			for(Projectile tir : tabTirMonstre)
			{
				AffineTransform tr = tir.getDrawTr(getScreenDisp());
				
				ArrayList<Image> images = imTirMonstre.getImages(tir.objType,tir.getTypeMouv(),null,tir.getAnim());
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

	public void drawEffects(boolean drawHitbox) {
		//Draw arrow effect
		for(int i =0; i< arrowsEffects.size(); ++i)
		{
			Effect eff = (Effect)arrowsEffects.get(i);


			if(eff.getNeedDestroy() || eff.isEnded())
				continue;

			AffineTransform tr = eff.getDrawTr(getScreenDisp());
			
			ArrayList<Image> images = imEffect.getImages(null,eff.getDeplacement().getTypeMouv(),eff.subTypeMouv,eff.getAnim());
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
			Image im = imEffect.getImage(null,ImEffectInfo.SLOWDOWN,null);
			imageDrawer.addImage(new DrawImageItem(im, (InterfaceConstantes.WINDOW_WIDTH-im.getWidth(null))/2 ,
					(InterfaceConstantes.WINDOW_HEIGHT-im.getHeight(null))/2,null,DrawImageHandler.EFFECT));

		}


	}
	@Override
	public void drawInterface() {
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
	@Override
	public boolean isComputationDone(){
		return computationDone && listenersComputationDone;
	}
	@Override
	public void doComputations(GameRenderer affich){
		//wait for loader to end
		if(!isGameModeLoaded())
			return;
		
		ModelPrincipal.debugTime.print();
		ModelPrincipal.debugTime.init(InterfaceConstantes.DEBUG_TIME_PRINT_MODE,getFrame());

		computationDone=false;
		
		ModelPrincipal.debugTime.startElapsedForVerbose();
		play(affich);
		
		ModelPrincipal.debugTime.elapsed("partie");
		precomputeDraw();
		ModelPrincipal.debugTime.elapsed("precomute draw");
		if(!getinPause() && (!slowDown || (slowDown && slowCount==0)))
			nextFrame();
		computationDone=true;
	}
	@Override
	public void updateSwing(){
		notifyObserver();
		ModelPrincipal.debugTime.elapsed("validate affichage");
	}
	@Override
	public boolean isGameModeLoaded()
	{
		return Serialize.niveauLoaded && loaderPartie.isLoadingDone();
	}
	@Override
	public GameMode getLoaderGameMode(){
		return loaderPartie;
	}

}