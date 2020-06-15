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

import javax.vecmath.Vector2d;

import ActiveJComponent.ActiveJButton;
import Affichage.DrawImageHandler;
import Affichage.DrawImageItem;
import Affichage.GameRenderer;
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
import partie.collision.Hitbox;
import partie.conditions.Condition;
import partie.effects.Effect;
import partie.effects.EffectConsequence;
import partie.effects.Trou_noir_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.entitie.monstre.Monstre;
import partie.entitie.monstre.Monstre.AIAction;
import partie.entitie.monstre.Spirel;
import partie.input.InputPartie;
import partie.input.InputPartiePool;
import partie.input.InputPartiePool.InputType;
import partie.input.InputPartiePool.KeyState;
import partie.mouvement.Mouvement;
import partie.mouvement.Mouvement.DirSubTypeMouv;
import partie.mouvement.Mouvement.SubTypeMouv;
import partie.mouvement.entity.Accroche;
import partie.mouvement.entity.Attente;
import partie.mouvement.entity.Course;
import partie.mouvement.entity.Marche;
import partie.mouvement.entity.Mouvement_entity;
import partie.mouvement.entity.Saut;
import partie.mouvement.entity.Tir;
import partie.mouvement.entity.Accroche.SubMouvAccrocheEnum;
import partie.mouvement.entity.Mouvement_entity.EntityTypeMouv;
import partie.mouvement.entity.Saut.SubMouvSautEnum;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;
import partie.projectile.fleches.rusee.Fleche_marque_mortelle;
import serialize.Serialize;
import java.text.DecimalFormat;
public class ModelPartie extends AbstractModelPartie{
	
	public static ModelPartie me=null; //singleton startegy
	public static ModelPartie Instantiate(Touches _touches,GameHandler gameHandler){
		if(me==null)
			me = new ModelPartie(_touches,gameHandler);
		return me;
	}
	private ModelPartie(Touches _touches,GameHandler gameHandler)
	{
		super();
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
		
		inputPartie = new InputPartie(touches);
		inputGamemodePool = new InputPartiePool(inputPartie);
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
		for(Entity ent : Collidable.getAllEntitiesCollidable(heros))
			ent.getHitbox(INIT_RECT, getScreenDisp());//force creation of hitbox		
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
				inputPartie.resetGameTouchesFocus();
				inPause=true;
				Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);
				//keyAction();
				affich.changeGameModeRendering();

				firstNonFocused=false;
			}

		}
		firstNonFocused=true;
		ModelPrincipal.debugTime.startElapsedForVerbose();

		//on desactive la touche cap au cas ou elle serait utilisée
		Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, false);

		//Handle input, mainly to handle Pause
		if(inputGamemodePool.isInputFirstPressed(InputType.PAUSE)){
			inPause = !inPause;
			if(inPause)
				inputGamemodePool.getReferenceInputPartie().resetGameTouchesFocus();//TODO: => reset all but pause ? 
		}
		ModelPrincipal.debugTime.elapsed("action");

		//Lors d'une pause, on ne veut pas réinitaliser la partie en cours mais juste y accéder à nouveau
		if(!inPause && !isPartieEnded)
		{
			/*
			 * 	Move all objects
				Detect collisions between projectiles/projectiles, entity/projectile, entite/effect
				Update conditions and apply condition damage
				Remove useless effect consequences on object 
				Register objects to destroy
			 * */
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

			for(Entity ent : Collidable.getAllEntitiesCollidable())
			{
				ModelPrincipal.debugTime.startElapsedForVerbose();
				boolean shouldBeDestroyed = ent.getNeedDestroy() || ((PartieTimer.me.getElapsedNano()-ent.tempsDetruit) > ent.TEMPS_DESTRUCTION) && ent.tempsDetruit>0;
				if(!shouldBeDestroyed){
					if(Collidable.isObjectOnScreen( ent))
						deplace.deplaceObject(ent);
					else
						deplace.deplaceObjectOutOfScreen( ent);
				}
				else
					ent.destroy(true);
				ModelPrincipal.debugTime.elapsed("deplace entitie: ", ent.toString());
			}
			
			ModelPrincipal.debugTime.elapsed("deplaceEntities");

			//DEPLACEMENT PROJECTILES 
			for(Projectile proj : Collidable.getAllProjectileCollidable())
			{
				ModelPrincipal.debugTime.startElapsedForVerbose();
				//Pos is incorrect for Fleche when encochee hence never destroy it in that case.
				//Otherwise destroy projectile if too far out of screen 
				boolean isObjectOnScreen = Collidable.isObjectOnScreen(proj);
				boolean shouldBeDestroyed =  proj.getNeedDestroy() || ((PartieTimer.me.getElapsedNano()-proj.tempsDetruit) > proj.TEMPS_DESTRUCTION) && proj.tempsDetruit>0;
				if(!shouldBeDestroyed) {
					if(isObjectOnScreen)
						deplace.deplaceObject(proj);
					else
						deplace.deplaceObjectOutOfScreen(proj);
				}
				else
					proj.destroy(true);
				ModelPrincipal.debugTime.elapsed("deplace projectile: "+ proj.toString());
			}

			ModelPrincipal.debugTime.elapsed("deplaceProjectiles");

			//UPDATE AND DEPLACEMENT OF EFFECTS
			int currentNumEffects = arrowsEffects.size();
			//Warning, size might change in the loop (arrows can be added with electrique effect) 
			//so we ignore them for this iteration (keep same max index)
			for(int i=0; i< currentNumEffects;++i)
			{
				Collidable col = arrowsEffects.get(i);
				ModelPrincipal.debugTime.startElapsedForVerbose();
				Effect eff = (Effect) col;
				boolean shouldBeDestroyed = eff.getNeedDestroy() || (((PartieTimer.me.getElapsedNano()-eff.tempsDetruit) > eff.TEMPS_DESTRUCTION) && eff.tempsDetruit>0);
				//REMOVE boolean ended = eff.isEnded();
				boolean isObjectOnScreen = Collidable.isObjectOnScreen(eff);
				if(!shouldBeDestroyed){
					if(isObjectOnScreen){
						deplace.deplaceObject(eff);
					}
					else
						deplace.deplaceObjectOutOfScreen(eff);
				}
				else
					eff.destroy(true);
				ModelPrincipal.debugTime.elapsed("deplace and update effects: "+ col.toString() );
			}
			
			ModelPrincipal.debugTime.elapsed("update and deplacement of effects");

			//COLLISION ENTITIES/PROJECTILE/EFFECTS
			List<List<Entity>> allEntities = Collidable.getAllEntitiesCollidableSeparately();
			List<List<Collidable>> allProjectiles = Collidable.getAllProjectileCollidableSeparately();

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
							Collision.collisionObjects(proj_i, proj_j,true);//Object are warn if collision in this function
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
							Collision.collisionObjects( ent, proj,true);//Object are warn if collision in this function
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
						if(eff.checkCollideWithEntitie() && Collision.testcollisionObjects( eff, ent,true))
						{
							EffectConsequence existingEffectConsequence = ent.currentEffectsMap.get(eff);
							if(existingEffectConsequence==null){
								eff.handleEntitieEffectCollision(ent);
								existingEffectConsequence = ent.currentEffectsMap.get(eff);
							}
							existingEffectConsequence.applyConsequence();
							existingEffectConsequence.OnEffectColliding(ModelPartie.me.getFrame());
						}
						ModelPrincipal.debugTime.elapsed("collision between entitie/effect: ", ent.toString()," " , eff.toString());
					}
				}
			}
			ModelPrincipal.debugTime.elapsed("collision entitie/effects");

			//update conditions for all entities. Only do it at the end to be sure that all entities received the same information on the conditions
			//Also callback before graphical updates (ie: for the damage taken list to be updated before the graphic call)
			for(Entity c : Collidable.getAllEntitiesCollidable()){
				c.conditions.updateConditionState();
				c.beforeGraphicUpdate();
				//apply condition damages
				c.addLife(c.conditions.conditionDamageReceived());
				//Update Effect consequences list on entity (delete the ones that were not update that frame or that have a deleted effect)
				c.removeEffectsThatExpired(ModelPartie.me.getFrame());
			}
			//on met a jour le heros si il est touché avant de l'afficher
			heros.miseAjourTouche();
			heros.miseAJourSeyeri();
			ModelPrincipal.debugTime.elapsed("effect updates");
			
			//Update Variables after collisions 
			nombreMonstreRestant=tabMonstre.size();
			
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

			isPartieEnded= (heros.getLife()==heros.MINLIFE) || (nombreMonstreRestant==0);
			
		}
		//Reset first pressed inputs
		inputGamemodePool.updateInputState();
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
			objectList.get(indexList.get(i)-i).onDestroy();
			ModelPrincipal.debugTime.elapsed("deleteObject: on destroy");
			objectList.remove(indexList.get(i)-i);
			ModelPrincipal.debugTime.elapsed("deleteObject: remove");
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
							monde.listMonstreOriginal.get(i).pos.y,monde.listMonstreOriginal.get(i).immobile,frame,this.inputPartie));
			}
			//on optimise la memoire
			monde.listMonstreOriginal.clear();
		}
		else if(typeDeSpawn==InterfaceConstantes.SPAWN_ALEATOIRE)
		{
			int x=0;
			int y=0;
			boolean correct= true;
			Spirel spirel= new Spirel(0, 0,false,frame,this.inputPartie);

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
					spirel = new Spirel(x,y,false,frame,this.inputPartie);
					correct = !Collision.isWorldCollision(spirel,false);
				}
				while (!correct); //on attend d'avoir une position correct avant de placer le monstre 

				//on place le monstre
				tabMonstre.add(new Spirel(x,y,false,frame,this.inputPartie));

			}
		}
	}
	
	@Override
	public Fleche getFlecheEncochee(Heros shooter){
		for(int i=0; i<tabFleche.size();i++)
		{
			Fleche f = (Fleche)tabFleche.get(i);
			if(f.encochee && f.shooter==shooter)
				return f;
		}
		return null;
	}
	
	

	public void HandleBoutonsPressed(ActiveJButton button) {
		if(button.getText().equals("Option"))
		{
			setAffichageOption=true;
			notifyObserver();

		}
		else if(button.getText().equals("Quitter"))
		{
			if(inPause)
			{
				//on arrete la partie pour sortir du Thread d'affichage de partie rapide
				isPartieEnded=true;
				//on choisit de ne pas afficher la fin de la partie(elle n'est en réalité pas finie
			}
			gameHandler.setGameMode(GameModeType.QUIT);
		}
		else if(button.getText().equals("Rejouer"))
		{
			
			isPartieEnded=false;
			disableBoutonsFin=true;
			notifyObserver();
			gameHandler.setGameMode(GameModeType.GAME);

		}
		else if(button.getText().equals("Menu Principal"))
		{
			isPartieEnded=true;
			disableBoutonsFin=true;
			notifyObserver();
			gameHandler.setGameMode(GameModeType.MAIN_MENU);

		}

		else if(button.getText().equals("Reprendre"))
		{
			inPause=false;
		}
	}

	/**
	 * Permet de reinitialiser tout les appuis de touches, utile notamment lors de la perte de focus de la fenetre
	 */	

	public void precomputeDraw()
	{
		imageDrawer.clearImages();
		drawBackground();
		drawMonde(InterfaceConstantes.DRAW_HITBOX_MONDE);
		drawMonstres(InterfaceConstantes.DRAW_HITBOX_MONSTRES);
		drawPerso(InterfaceConstantes.DRAW_HITBOX_PERSO);
		drawFleches(InterfaceConstantes.DRAW_HITBOX_FLECHES);
		drawTirMonstres(InterfaceConstantes.DRAW_HITBOX_TIR_MONSTRES);
		drawEffects(InterfaceConstantes.DRAW_HITBOX_EFFECTS);
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

		final int extraBloc = 1; //draw more blocs to make sure that there is always one visible when the hero moves
		
		int xStartAff = xviewport/TAILLE_BLOC-extraBloc;
		int xEndAff = (InterfaceConstantes.WINDOW_WIDTH/TAILLE_BLOC+xviewport/TAILLE_BLOC)+extraBloc+1;

		int yStartAff = yviewport/TAILLE_BLOC-extraBloc;
		int yEndAff = (InterfaceConstantes.WINDOW_HEIGHT/TAILLE_BLOC+yviewport/TAILLE_BLOC)+extraBloc+1;
		
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

			ArrayList<Image> images = imMonstre.getImages(m.objType,m.getTypeMouv(),null,m.getMouvIndex());
			AffineTransform tr = getRotatedTransform(new Point(xDraw,yDraw),new Point(),m.getRotation(),m.getScaling());
			for(Image im : images){
				//imageDrawer.addImage(new DrawImageItem(im,xDraw,yDraw,null,DrawImageHandler.MONSTRE));
				imageDrawer.addImage(new DrawImageItem(im,tr,null,DrawImageHandler.MONSTRE));
			}

			int xDraw_d= xDraw +m.getCurrentXtaille();
			//draw monster lifebar
			int[] x= {(int) (xDraw/2+xDraw_d/2-m.MAXLIFE/2),(int) (xDraw/2+xDraw_d/2-m.MAXLIFE/2)};
			int[] y= {yDraw-10,yDraw-10};
			int[] width={(int) m.MAXLIFE,(int) m.getLife()};
			int[] height={5,5};
			Color[] colors = {Color.BLACK,Color.GREEN};
			imageDrawer.addImage(new DrawImageItem(x,y,width,height,colors,DrawImageHandler.MONSTRE));


			ArrayList<Condition> allcondis = m.conditions.getAllConditions();
			int xMiddle = (int) Hitbox.getObjMid(m).x-m.getXpos();
			int xStartDrawCondi = m.conditions.getXStartDraw(xDraw, xMiddle, allcondis.size());
			int yStartDrawCondi = m.conditions.getYStartDraw(y[0]-25);

			for(int i =0; i<allcondis.size();++i){
				Condition condi = allcondis.get(i); 
				if(condi.blinkDisplay || isPartieEnded){ //display all condis at the end of the game
					imageDrawer.addImage(new DrawImageItem(imConditions.getImage(null,condi.type,null), xStartDrawCondi+25*i ,yStartDrawCondi ,null
							,DrawImageHandler.MONSTRE));
				}
			}
			//draw the damage
			for(DamageDrawer damageDrawer : m.lastDamageTaken){
				if(damageDrawer.shouldDraw()){
					Point defaultPos = new Point(m.getXpos()+m.getCurrentXtaille()/2,m.getYpos()-35); 
					Point drawPos = damageDrawer.getWorldDrawPos(defaultPos);
					imageDrawer.addImage(new DrawImageItem(damageDrawer.getMessage(),drawPos.x+xScreendisp,drawPos.y+yScreendisp,damageDrawer.getColor(),Color.BLACK,
							damageDrawer.getFontSize(),DrawImageHandler.INTERFACE));
				} 
			}

			if(drawHitbox)
			{
				Hitbox hitbox= m.getHitbox(INIT_RECT,getScreenDisp());
				imageDrawer.addImage(new DrawImageItem(hitbox,getScreenDisp(),Color.RED,Color.BLACK,DrawImageHandler.MONSTRE));
				Monstre monstre = ((Monstre)m);
				AIAction lastIAAction = monstre.lastIAAction;
				if(lastIAAction!=null){
					int l = lastIAAction.toString().length();
					imageDrawer.addImage(new DrawImageItem(lastIAAction.toString(),xDraw+m.getCurrentXtaille()/2-l/2*15,yDraw-25,Color.RED,Color.BLACK,15,DrawImageHandler.MONSTRE));
				}
				if(monstre.isStatic)
					imageDrawer.addImage(new DrawImageItem("Static",xDraw+m.getCurrentXtaille()/2-20,yDraw-15,Color.RED,Color.BLACK,10,DrawImageHandler.MONSTRE));
				if(monstre instanceof Spirel){
					int distanceAttaque = (int)Math.round(Math.sqrt(((Spirel)monstre).getSquaredAttackDistance()));
					Vector2d hitboxCenter = hitbox.getCenter();
					int x_center_attaque = (int)Math.round(hitboxCenter.x+getScreenDisp().x);
					int y_center_attaque = (int)Math.round(hitboxCenter.y+getScreenDisp().y);
					imageDrawer.addImage(new DrawImageItem(x_center_attaque,y_center_attaque,new Point(2*distanceAttaque,2*distanceAttaque),Color.RED,Color.BLACK,DrawImageHandler.MONSTRE));
				}
			}
		}

	}

	public Point drawPersoTir()
	{
		int mouv_index = heros.getMouvIndex();
		Point anchor = new Point(((Mouvement_entity)heros.getMouvement()).x_rot_pos.get(mouv_index),((Mouvement_entity)heros.getMouvement()).y_rot_pos.get(mouv_index));
		Point returnedAnchor = new Point(heros.getXpos()+anchor.x,heros.getYpos()+anchor.y);
		
		AffineTransform tr = getRotatedTransform(new Point(heros.getXpos(),heros.getYpos()),anchor,heros.rotation_tir,heros.getScaling());

		ArrayList<Image> l_image = imHeros.getImages(null,heros.getTypeMouv(),null,heros.getMouvIndex());
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
			if(heros.getMouvement().isMouvement(EntityTypeMouv.TIR))
				anchor=drawPersoTir();
			else
			{
				ArrayList<Image> l_image = imHeros.getImages(null,heros.getTypeMouv(),null,heros.getMouvIndex());
				for(int i=0; i<l_image.size(); ++i){
					imageDrawer.addImage(new DrawImageItem(l_image.get(i), heros.getXpos(),heros.getYpos(),null,DrawImageHandler.PERSO));
				}
			}
			ArrayList<Condition> allcondis = heros.conditions.getAllConditions();
			int xMiddle = (int) Hitbox.getObjMid(heros).x-heros.getXpos()+xScreendisp;
			int xStartDrawCondi = heros.conditions.getXStartDraw(heros.getXpos(), xMiddle, allcondis.size());
			int yStartDrawCondi = heros.conditions.getYStartDraw(heros.getYpos()-25);
			for(int i =0; i<allcondis.size();++i){
				Condition condi = allcondis.get(i); 
				if(condi.blinkDisplay|| isPartieEnded){//display all condis at the end of the game
					imageDrawer.addImage(new DrawImageItem(imConditions.getImage(null,condi.type,null), xStartDrawCondi+25*i ,yStartDrawCondi ,null
							,DrawImageHandler.PERSO));
				}
			}
		}
		//draw the damage
		for(DamageDrawer damageDrawer : heros.lastDamageTaken){
			if(damageDrawer.shouldDraw()){
				Point defaultPos = new Point(heros.getXpos()+heros.getCurrentXtaille()/2,heros.getYpos()-35); 
				Point drawPos = damageDrawer.getWorldDrawPos(defaultPos);
				imageDrawer.addImage(new DrawImageItem(damageDrawer.getMessage(),drawPos.x,drawPos.y,damageDrawer.getColor(),Color.BLACK,
						damageDrawer.getFontSize(),DrawImageHandler.INTERFACE));
			} 
		}

		if(drawHitbox)
		{
			Hitbox hitbox= heros.getHitbox(INIT_RECT,getScreenDisp());
			imageDrawer.addImage(new DrawImageItem(hitbox,getScreenDisp(),Color.RED,Color.BLACK,DrawImageHandler.PERSO));
			if(anchor!=null){
				imageDrawer.addImage(new DrawImageItem("o", anchor.x, anchor.y,Color.RED,Color.BLACK,20,DrawImageHandler.PERSO));

			}
			imageDrawer.addImage(new DrawImageItem("("+heros.getXpos()+","+heros.getYpos()+")",
					heros.getXpos(),heros.getYpos()+20,Color.BLACK,Color.BLACK,20,DrawImageHandler.PERSO));

		}

	}


	public void drawFleches(boolean drawHitbox) {
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

				ArrayList<Image> images = imFleches.getImages(fleche.objType,null,null,fleche.getMouvIndex());
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
										p1 = Fleche.getArrowTip(f_auto, true);
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
				
				ArrayList<Image> images = imTirMonstre.getImages(tir.objType,tir.getTypeMouv(),null,tir.getMouvIndex());
				
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

			
			if(eff.getNeedDestroy()) //TODO: removed this  || eff.isEnded()
				continue;

			AffineTransform tr = eff.getDrawTr(getScreenDisp());
			
			ArrayList<Image> images = imEffect.getImages(null,eff.getMouvement().getTypeMouv(),eff.subTypeMouv,eff.getMouvIndex());
			for(Image im : images){
				Image im2draw = eff.applyFilter(im);
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
		DecimalFormat df = new DecimalFormat("0.0");
		String heros_life_str = df.format(heros.getLife());
		String heros_max_life_str = df.format(heros.MAXLIFE);
		String heros_seyeri_str = df.format(heros.getSeyeri());
		String heros_max_seyeri_str = df.format(InterfaceConstantes.MAXSEYERI);
		
		int[] x_l= {10,10};
		int[] y_l= {10,10};
		int[] width_l={(int) heros.MAXLIFE,(int) heros.getLife()};
		int[] height_l={20,20};
		Color[] colors_l = {Color.BLACK,Color.GREEN};
		imageDrawer.addImage(new DrawImageItem(x_l,y_l,width_l,height_l,colors_l,DrawImageHandler.INTERFACE));
		imageDrawer.addImage(new DrawImageItem(heros_life_str+ "/"+heros_max_life_str, x_l[0]+ width_l[0]/2-30 , y_l[0]+height_l[0]/2+5,
				Color.WHITE,Color.WHITE,10,DrawImageHandler.INTERFACE));
		//seyeri
		int[] x_s= {10,10,10};
		int[] y_s= {40,40,40};
		int[] width_s={(int) InterfaceConstantes.MAXSEYERI,(int) heros.getNotEnoughSeyeri(),(int) heros.getSeyeri()};
		int[] height_s={20,20,20};
		Color[] colors_s = {Color.BLACK,Color.RED,new Color(255,125,125)};
		imageDrawer.addImage(new DrawImageItem(x_s,y_s,width_s,height_s,colors_s,DrawImageHandler.INTERFACE));
		imageDrawer.addImage(new DrawImageItem( heros_seyeri_str+ "/"+heros_max_seyeri_str, x_s[0]+ width_s[0]/2-30 , y_s[0]+height_s[0]/2+5,
				Color.WHITE,Color.WHITE,10,DrawImageHandler.INTERFACE));
		
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

		if(isPartieEnded)
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
	public void doComputations(GameRenderer affich){
		//wait for loader to end
		if(!isGameModeLoaded())
			return;
		
		ModelPrincipal.debugTime.startElapsedForVerbose();
		play(affich);
		
		ModelPrincipal.debugTime.elapsed("partie");
		precomputeDraw();
		ModelPrincipal.debugTime.elapsed("precomute draw");
		if(!isInPause() && (!slowDown || (slowDown && slowCount==0)))
			nextFrame();
	}
	@Override
	public void updateSwing(){
		notifyObserver();
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
