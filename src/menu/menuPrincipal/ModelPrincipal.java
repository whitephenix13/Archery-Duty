package menu.menuPrincipal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.sun.management.GarbageCollectionNotificationInfo ;

import Affichage.GameRenderer;
import Affichage.Drawable;
import debug.DebugObjectCreation;
import debug.DebugTime;
import editeur.AffichageEditeur;
import editeur.ControlerEditeur;
import editeur.ModelEditeur;
import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper.ObjectType;
import images.ImagesContainer;
import images.ImagesContainer.ImageGroup;
import images.ImagesContainer.ImageInfo;
import images.ImagesContainer.ObjectSubType;
import images.ImagesPrincipal.ImPrincipalInfo;
import images.ImagesPrincipal;
import loading.Loader;
import loading.LoaderItem;
import loading.LoaderUtils;
import menu.choixNiveau.AffichageChoixNiveau;
import menu.choixNiveau.ControlerChoixNiveau;
import menu.choixNiveau.ModelChoixNiveau;
import menu.credit.AffichageCredit;
import menu.menuPrincipal.GameHandler.GameModeType;
import music.Music;
import option.AffichageOption;
import option.Config;
import option.ControlerOption;
import option.ModelOption;
import option.Touches;
import partie.bloc.Bloc;
import partie.collision.Hitbox;
import partie.modelPartie.AffichagePartie;
import partie.modelPartie.ControlerPartie;
import partie.modelPartie.ModelPartie;
import utils.TypeApplication;


@SuppressWarnings("unused")
public class ModelPrincipal extends AbstractModelPrincipal{
	
	
	public class MainLoop implements Runnable{
		@Override
		public void run()
		{
			try{
			double deltaTime;
			//Change game mode if needed 
			if(nextGameMode !=null){
				changeGameMode(nextGameMode);
				nextGameMode=null;
			}
			deltaTime=(System.nanoTime()-last_update)/Math.pow(10, 6);//delta time in ms

			//if((deltaTime>Config.getDeltaFrame(true)) &&currentGameMode.isComputationDone()){ 
			last_update=System.nanoTime();
			currentGameMode.doComputations(gameRenderer);//main game mode logic
			gameRenderer.render(false);
				//REMOVEcurrentGameMode.updateGraphics(); //update the screen based on the new computations
			//}
			}
			catch(Exception e){e.printStackTrace();}
			
		}
	}
	
	private ModelPrincipal()
	{
		super();
	}
	protected void Init() {
		
		debugTimeAffichage = new DebugTime(InterfaceConstantes.DEBUG_TIME_AFFICHAGE_LOOP_TO_SLOW,InterfaceConstantes.DEBUG_TIME_AFFICHAGE_ACTION_TO_SLOW,InterfaceConstantes.DEBUG_TIME_AFFICHAGE_VERBOSE);

		nextGameMode = null;
		gameRenderer = new GameRenderer(this);
		gameRenderer.setBackground(Color.black);
		//Specific case: for the transition to loading screen since we are going to wait for the loader to end
		//This shoud be done in the draw() method of ModelPrincipal but this init thread must be completed before being able the access the main loop hence the trick
		loaderMenuPrincipal = new Loader(gameRenderer,this);
		
		currentGameModeType=GameModeType.LOADER;
		currentGameMode = loaderMenuPrincipal; //get loader from partie 
		loaderMenuPrincipal.setCallback(new Runnable(){
			@Override
			public void run() {
				ModelPrincipal.this.setGameMode(GameModeType.MAIN_MENU); 
			}
		});
		gameRenderer.changeGameModeRendering(loaderMenuPrincipal.getAffichageLoader());//begin transition to loader
		
		
		//Enough elements have been created for the game to start (at this point, we only start rendering the loading screen)
		startGame();
		
		touches=new Touches();
		
		principal = this;
		controlerPrincipal = new ControlerPrincipal(principal);
		affichagePrincipal = new AffichagePrincipal(controlerPrincipal);//TODO: remove this behaviour in AffichagePrincipal/Init? Load the required media (background image)
		principal.addObserver(affichagePrincipal);

		
		gameRenderer.setAffichagePrincipal(affichagePrincipal);
		//on met en place le conteneur 
		//REMOVE gameRenderer.setResizable(false);

		//REMOVE gameRenderer.getContentPane().setFocusable(true);
		//REMOVE gameRenderer.getContentPane().requestFocus();

		//REMOVE gameRenderer.setSize(new Dimension(InterfaceConstantes.WINDOW_WIDTH,InterfaceConstantes.WINDOW_HEIGHT));
		//REMOVE gameRenderer.setLocationRelativeTo(null);
		//REMOVE gameRenderer.setTitle("Menu principal");
		//REMOVE gameRenderer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//REMOVE gameRenderer.setVisible(true);	
		

		//add main observer used to ask for global screen update ie: when loading 

		principal.addMainObserver(gameRenderer);
		
		//TODO: show loading screen here => first instantiate affichage, then give references to other affichages 
		LoaderItem mainObjectsCreation = new LoaderItem("Main objects creation"){
			@Override
			public void run()
			{
				edit = new ModelEditeur(ModelPrincipal.this);
				controlerEditeur = new ControlerEditeur(edit);
				affichageEditeur = new AffichageEditeur(controlerEditeur);
				edit.addObserver(affichageEditeur);
				percentage = 20;
				
				affichageCredit= new AffichageCredit(ModelPrincipal.this);

				choix = new ModelChoixNiveau(ModelPrincipal.this);
				controlerChoix= new ControlerChoixNiveau(choix) ;
				affichageChoix = new AffichageChoixNiveau(controlerChoix);
				choix.addObserver(affichageChoix);
				affichageChoix.init();
				percentage = 40;

				partie = new ModelPartie(touches,ModelPrincipal.this);
				controlerPartie= new ControlerPartie(partie) ;
				affichagePartie = new AffichagePartie(controlerPartie);
				partie.addObserver(affichagePartie);
				partie.init();
				percentage = 60;

				option = new ModelOption(touches,partie.inputPartie,ModelPrincipal.this);
				controlerOption = new ControlerOption(option);
				affichageOption = new AffichageOption(controlerOption);
				option.addObserver(affichageOption);
				percentage = 80;

				
				//Now that all other affichage are loaded, we can set the reference and end the loading screen 
				gameRenderer.setOtherAffichageReferences(affichageOption,affichageEditeur,affichageCredit,affichageChoix,affichagePartie);
				
				//add main observer used to ask for global screen update ie: when loading 
				edit.addMainObserver(gameRenderer);
				choix.addMainObserver(gameRenderer);
				partie.addMainObserver(gameRenderer);
				option.addMainObserver(gameRenderer);

				Music.init();
				percentage = 100;
			}
		};
		
		
		//Wait for background to be visible + music principal to be loaded
		
		loaderMenuPrincipal.addItem(mainObjectsCreation);
		loaderMenuPrincipal.addItem(LoaderUtils.load(LoaderUtils.MT_IMAGE, LoaderUtils.C_PRINCIPAL, null, this, partie));
		loaderMenuPrincipal.addItem(LoaderUtils.load(LoaderUtils.MT_SOUND, LoaderUtils.C_MUSIC, InterfaceConstantes.musiquePrincipal, this, partie));
		loaderMenuPrincipal.start();

		loaderMenuPrincipal.waitToEnd(principal);
		
		affichagePrincipal.setButtons();
		//REMOVE gameRenderer.repaint();

		//start music 
		Music.me.startNewMusic(InterfaceConstantes.musiquePrincipal);

		//load all image + music + bruitage in different Thread 
		loaderAllMedia = new Loader(gameRenderer,this);
		loaderAllMedia.addItem(LoaderUtils.loadAllImagesAndSounds(this, partie));
		loaderAllMedia.start();
	}

	//TO GET NOTIFICATION FROM GARABGE COLLECTOR 
	static
	{
		if(InterfaceConstantes.DEBUG_GC_VERBOSE>0){
			// notification listener. is notified whenever a gc finishes.
			NotificationListener notificationListener = new NotificationListener()
			{
				@Override
				public void handleNotification(Notification notification,Object handback)
				{
					if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION))
					{
						// extract garbage collection information from notification.
						final GarbageCollectionNotificationInfo gcInfo = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
	
						// access garbage collection information...
						//beautiful print: 
						String memPrint ="";
						if(InterfaceConstantes.DEBUG_GC_VERBOSE>1){
							memPrint ="\n\t";
							for(String key : gcInfo.getGcInfo().getMemoryUsageBeforeGc().keySet())
							{
								memPrint+=key+":init "+gcInfo.getGcInfo().getMemoryUsageBeforeGc().get(key).getInit()+" -> "+ gcInfo.getGcInfo().getMemoryUsageAfterGc().get(key).getInit()
											+ " used "+gcInfo.getGcInfo().getMemoryUsageBeforeGc().get(key).getUsed()+" -> "+ gcInfo.getGcInfo().getMemoryUsageAfterGc().get(key).getUsed()
											+ " commited "+gcInfo.getGcInfo().getMemoryUsageBeforeGc().get(key).getCommitted()+" -> "+ gcInfo.getGcInfo().getMemoryUsageAfterGc().get(key).getCommitted()
								+ " max "+gcInfo.getGcInfo().getMemoryUsageBeforeGc().get(key).getMax()+" -> "+ gcInfo.getGcInfo().getMemoryUsageAfterGc().get(key).getMax()+"\n\t";
							}
							memPrint.substring(0, memPrint.length()-4);//remove last \n\t 
						}
						System.out.println("*************"+gcInfo.getGcAction()+" because of "+ gcInfo.getGcCause() +" : "+gcInfo.getGcInfo().getDuration()+
								"ms."+memPrint+"**********");
					}
				}
			};
	
			// register our listener with all gc beans
			for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans())
			{
				NotificationEmitter emitter = (NotificationEmitter) gcBean;
				emitter.addNotificationListener(notificationListener,null,null);
			}
		}
	}
	
	public void startGame()
	{
		//Very important: This method tries to keep up: 
		//if one game tick took long enough to delay the next game tick, the executor service will consider this in the calculation for the next sleep duration.
		executor = Executors
                .newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new MainLoop(), 0, (int)Math.round(Config.getDeltaFrame(true)), TimeUnit.MILLISECONDS);//call scheduleAtFixedRate.shutdown() to stop this
	}
	
	@Override 
	public void forceRepaint()
	{
		gameRenderer.render(true);
	}
	
	public GameModeType currentGameMode() {return currentGameModeType;}
	public void setGameMode(GameModeType newMode) 
	{
		nextGameMode = newMode;
	}
	private void changeGameMode(GameModeType newMode) 
	{
		if (newMode.equals(GameModeType.QUIT))
		{
			currentGameModeType=newMode;
			System.exit(0);
		}
		else if (newMode.equals(GameModeType.OPTION))
		{			
			//musique 
			String nextMusic = InterfaceConstantes.musiqueOption;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener 
			gameRenderer.removeListener(currentGameModeType);

			//changement de mode
			currentGameModeType=newMode;
			currentGameMode = option;

			//affichage
			//REMOVE changeFrame=true;
			gameRenderer.changeGameModeRendering();

			//listener
			gameRenderer.addListener(currentGameModeType);

		}
		else if (newMode.equals(GameModeType.EDITOR))
		{
			if(!currentGameModeType.equals(GameModeType.LOADER)){
				//listener
				gameRenderer.removeListener(currentGameModeType);
			
				edit.loaderEditeur = new Loader(gameRenderer,this);
				edit.loaderEditeur.addItem(LoaderUtils.waitForLoaderToEnd(loaderAllMedia));
				edit.loaderEditeur.addItem(LoaderUtils.waitForGarbageCollectorToEndInALoader());
				edit.loaderEditeur.start();
			}
			//changement de mode
			if(!edit.isGameModeLoaded()){//editeur loader not done 
				currentGameModeType=GameModeType.LOADER;
				currentGameMode = edit.getLoaderGameMode();  
				((Loader)edit.getLoaderGameMode()).setCallback(new Runnable(){
					@Override
					public void run() {
						ModelPrincipal.this.setGameMode(GameModeType.EDITOR); //return back to this function to finish initialization of partie 
					}
				});
				gameRenderer.changeGameModeRendering(((Loader)edit.getLoaderGameMode()).getAffichageLoader());//begin transition to loader
			}
			else{
				//musique 
				String nextMusic = InterfaceConstantes.musiqueEditeur;
	
				if(!Music.musiqueEnCours.equals(nextMusic))
				{
					Music.me.startNewMusic(nextMusic);
				}
				//listener
				gameRenderer.removeListener(currentGameModeType);
				
				//changement de mode
				currentGameModeType=newMode;
				currentGameMode = edit;
				//listener
				gameRenderer.addListener(currentGameModeType);
	
				//affichage
				//REMOVE changeFrame=true;
				gameRenderer.changeGameModeRendering();
			}
		}
		else if (newMode.equals(GameModeType.CREDIT))
		{

			//musique 
			String nextMusic = InterfaceConstantes.musiqueEditeur;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener
			gameRenderer.removeListener(currentGameModeType);

			//changement de mode
			currentGameModeType=newMode;
			currentGameMode = affichageCredit;
			//listener
			gameRenderer.addListener(currentGameModeType);

			//affichage
			//REMOVE changeFrame=true;
			gameRenderer.changeGameModeRendering();
		}
		else if (newMode.equals(GameModeType.LEVEL_SELECTION))
		{
			//musique 
			String nextMusic = InterfaceConstantes.musiquePrincipal;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener
			gameRenderer.removeListener(currentGameModeType);

			//changement de mode
			currentGameModeType=newMode;
			currentGameMode = choix;
			//affichage
			//REMOVE changeFrame=true;

			gameRenderer.changeGameModeRendering();

			//listener
			gameRenderer.addListener(currentGameModeType);

		}
		else if (newMode.equals(GameModeType.GAME))
		{
			//not drawing at first 
			partie.computationDone=false;
			
			//If we come from the partie loader, don't init the partie again 
			if(!currentGameModeType.equals(GameModeType.LOADER)){
				//listener
				gameRenderer.removeListener(currentGameModeType);
	
				//on reinitialiser les variables pour pouvoir rejouer plusieurs fois
	
				partie.init();
	
				//on lance la partie rapide si elle n'est pas deja en cours ie: le jeu n'est pas en pause
				//And load level
				partie.loaderPartie = new Loader(gameRenderer,this);
				partie.loaderPartie.addItem(LoaderUtils.loadWorld(choix.getNiveauSelectionne(), partie));
				partie.loaderPartie.addItem(LoaderUtils.waitForLoaderToEnd(loaderAllMedia));
				partie.loaderPartie.addItem(LoaderUtils.waitForGarbageCollectorToEndInALoader());
				partie.loaderPartie.start();
			}
			//changement de mode
			if(!partie.isGameModeLoaded()){//partie loader not done 
				currentGameModeType=GameModeType.LOADER;
				currentGameMode = partie.getLoaderGameMode(); //get loader from partie 
				((Loader)partie.getLoaderGameMode()).setCallback(new Runnable(){
					@Override
					public void run() {
						ModelPrincipal.this.setGameMode(GameModeType.GAME); //return back to this function to finish initialization of partie 
					}
				});
				gameRenderer.changeGameModeRendering(((Loader)partie.getLoaderGameMode()).getAffichageLoader());//begin transition to loader
			}
			else{
				currentGameModeType=newMode;
				currentGameMode = partie;
				
				//musique 
				int numMus = (int) (Math.random()*InterfaceConstantes.musiquePartie.length);
				String nextMusic = InterfaceConstantes.musiquePartie[numMus];

				if(!Music.musiqueEnCours.equals(nextMusic))
					Music.me.startNewMusic(nextMusic);			
				else
					//make sure that we start with the non slow version
					Music.me.endSlowDownMusic();
				
				//affichage
				//REMOVE changeFrame=true;
				gameRenderer.changeGameModeRendering();
				
				//REMOVE partie.loaderPartie.wait(partie);
				//make sure that all other media are loaded correctly
				loaderAllMedia.waitToEnd(partie);
				if(InterfaceConstantes.DEBUG_OBJECT_CREATION)
					DebugObjectCreation.start();
				
				//LoaderUtils.waitForGarbageCollectorToEnd();//REMOVE ? collect the loading thread to avoid a major GC of 3000ms at the start of the game
		
				//listener
				gameRenderer.addListener(currentGameModeType);
	
				//d�finition du thread d'affichage qui fait tourner partie rapide.play() en continue 
				/*REMOVE class ThreadAffichage implements Runnable
				{
					public void run() 
					{
						debugTime = new DebugTime();
						do
						{
							double deltaTime= (System.nanoTime()-last_update)/Math.pow(10, 6);//delta time in ms
							if((deltaTime>Config.getDeltaFrame(true)) &&partie.computationDone){ //TODO: only execute the loop if the previous iteration (partie + draw) was done? If partie done alone ?
								//TODO: currently computationDone is used as an indicator that the screen can be drawn
								//TODO: -> split that between "Calculation done" and "Can be drawn" 
								//TODO: only enter a new loop if the calculation is done (not the drawing so that the in game time is respected and player can 
								//still anticipate dodge/actions. Otherwise everything is slow down)
								//TODO: make sure that physics is not impacted if deltaTime is twice bigger than usual (should be "fixed in game time" between two graphics update)
								debugTime.print();
								debugTime.init(InterfaceConstantes.DEBUG_TIME_PRINT_MODE,partie.getFrame());
	
								partie.computationDone=false;
								
								ModelPrincipal.debugTime.startElapsedForVerbose();
							
								last_update=System.nanoTime();
	
								partie.play(affich);
								debugTime.elapsed("partie");
								
								affichagePartie.repaintPartie();
								debugTime.elapsed("repaint");
	
	
								affichagePartie.validateAffichagePartie(affich);
								if(!partie.getinPause() && (!partie.slowDown || (partie.slowDown && partie.slowCount==0)))
									partie.nextFrame();
								debugTime.elapsed("validate affichage");
	
							}
						}
						while(!partie.getFinPartie());//condition de fin
						//last draw when partie ends
						partie.computationDone=true;
					}
	
				}
				Thread t2= new Thread(new ThreadAffichage());*/
	
				//on lance la partie
				partie.computationDone=true;
				//REMOVE t2.start();
				//affich.actuAffichage();
			}

		}
		else if (newMode.equals(GameModeType.MAIN_MENU))
		{
			//musique 
			String nextMusic = InterfaceConstantes.musiquePrincipal;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener 
			gameRenderer.removeListener(currentGameModeType);

			//changement de mode
			currentGameModeType=newMode;
			currentGameMode = this;
			
			//listeners
			gameRenderer.addListener(currentGameModeType);


			//affichage
			//REMOVE changeFrame=true;
			gameRenderer.changeGameModeRendering();


		}
		//REMOVE gameRenderer.repaint();
		gameRenderer.validate();
	}
	

	
	/*remove public void updateGraphics()
	{
		affich.actuAffichage();
	}*/
	
	public static double r_ceil(double val, int dec)
	{
		double fact = Math.pow(10, dec);
		return Math.ceil(val *fact)/fact;
		
	}
	public static void test()
	{
	}
	
	public void doComputations(GameRenderer affich){
		//As this mode is controlled by listeners, the computationDone is set to false when a listener is triggered. This function is then left empty
	}
	public void updateSwing(){
		//REMOVE gameRenderer.repaint();
	}
	public boolean isComputationDone(){
		return computationDone;
	}
	
	@Override
	public boolean isGameModeLoaded()
	{
		//Specific loading in that case, it is called manually when initializating all the classes
		return true;
	}
	@Override
	public GameMode getLoaderGameMode(){
		//Specific loading in that case, it is called manually when initializating all the classes
		return null;
	}
	@Override
	public Image getImage(ImageGroup group,ObjectType typeObj, ImageInfo info1,ImageInfo info2)
	{
		return getImageGroup(group).getImage(typeObj,  info1, info2);
	}
	@Override
	public ArrayList<Image> getImages(ImageGroup group,ObjectType typeObj, ImageInfo info1,ImageInfo info2,int anim)
	{
		return getImageGroup(group).getImages(typeObj, info1, info2,anim);
	}
	@Override
	public ImagesContainer getImageGroup(ImageGroup group) {
		if(group.equals(ImageGroup.BACKGROUND))
			return this.imBackground;
		else if(group.equals(ImageGroup.CONDITION))
			return this.imConditions;
		else if(group.equals(ImageGroup.EFFECT))
			return this.imEffect;
		else if(group.equals(ImageGroup.FLECHE))
			return this.imFleches;
		else if(group.equals(ImageGroup.FLECHEICON))
			return this.imFlecheIcon;
		else if(group.equals(ImageGroup.HEROS))
			return this.imHeros;
		else if(group.equals(ImageGroup.MONDE))
			return this.imMonde;
		else if(group.equals(ImageGroup.MONSTRE))
			return this.imMonstre;
		else if(group.equals(ImageGroup.PRINCIPAL))
			return this.imPrincipal;
		else if(group.equals(ImageGroup.TIRMONSTRE))
			return this.imTirMonstre;
		else {
			try {throw new Exception("No image group for "+ group);} catch (Exception e) {e.printStackTrace();}
			return null;
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException, URISyntaxException 
	{	
		TypeApplication.isJar= new TypeApplication().isJar();
		/*if(InterfaceConstantes.DEBUG_OBJECT_CREATION)
			DebugObjectCreation.start();*/
		
		ModelPrincipal principal = new ModelPrincipal();
		principal.Init();
		//REMOVEprincipal.StartBoucleJeu();

		//test();
		//Convertisseur conv = new Convertisseur();
		//conv.convertir();

	}

}