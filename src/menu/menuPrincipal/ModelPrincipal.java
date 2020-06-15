package menu.menuPrincipal;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.sun.management.GarbageCollectionNotificationInfo ;

import Affichage.GameRenderer;
import debug.DebugObjectCreation;
import debug.DebugTime;
import editeur.AffichageEditeur;
import editeur.ControlerEditeur;
import editeur.ModelEditeur;
import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper.ObjectType;
import images.ImagesBackground;
import images.ImagesCondition;
import images.ImagesContainer;
import images.ImagesContainer.ImageGroup;
import images.ImagesContainer.ImageInfo;
import images.ImagesEffect;
import images.ImagesFleche;
import images.ImagesFlecheIcon;
import images.ImagesHeros;
import images.ImagesMonde;
import images.ImagesMonstre;
import images.ImagesPrincipal;
import images.ImagesTirMonstre;
import loading.Loader;
import loading.LoaderItem;
import loading.LoaderUtils;
import menu.choixNiveau.AffichageChoixNiveau;
import menu.choixNiveau.ControlerChoixNiveau;
import menu.choixNiveau.ModelChoixNiveau;
import menu.credit.AffichageCredit;
import music.Music;
import option.AffichageOption;
import option.Config;
import option.ControlerOption;
import option.ModelOption;
import option.Touches;
import partie.modelPartie.AffichagePartie;
import partie.modelPartie.ControlerPartie;
import partie.modelPartie.ModelPartie;
import utils.TypeApplication;


@SuppressWarnings("unused")
public class ModelPrincipal extends AbstractModelPrincipal{
	
	
	private ModelPrincipal()
	{
		super();
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
	
	public class MainLoop implements Runnable{
		@Override
		public void run()
		{
			while(true){
			try{
				if((System.nanoTime()-last_fps_update_time)>=1000000000) //more than 1 sec: update fps
				{
					fps = num_frame_since_last_fps;
					num_frame_since_last_fps=0;
					last_fps_update_time=System.nanoTime();
				}
				double begin_time = System.nanoTime();
				ModelPrincipal.debugTime.print();
				ModelPrincipal.debugTime.init(InterfaceConstantes.DEBUG_TIME_PRINT_MODE,frame);
				
				ModelPrincipal.debugTime.startElapsedForVerbose();
				//Change game mode if needed 
				if(nextGameMode !=null){
					changeGameMode(nextGameMode);
					nextGameMode=null;
				}
				ModelPrincipal.debugTime.elapsed("change game mode");

				currentGameMode.doComputations(gameRenderer);//main game mode logic
				ModelPrincipal.debugTime.elapsed("do computations");
				gameRenderer.render(false);
				ModelPrincipal.debugTime.elapsed("renders");
				
				num_frame_since_last_fps+=1;
				frame+=1;

				double elapsed = ( System.nanoTime() - begin_time )*Math.pow(10, -6);//ms
				long sleeptime = Math.round(Config.getDeltaFrame(true) - elapsed);
				if(sleeptime>=0 ){
					Thread.sleep(sleeptime);//not super accurate but good enough
				}
				//No catchup if computation is too long
				//Catchup would drop rendering and execute more computation 

			}
			catch(Exception e){e.printStackTrace();}
			}

		}
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
		affichagePrincipal = new AffichagePrincipal(controlerPrincipal);
		principal.addObserver(affichagePrincipal);

		
		gameRenderer.setAffichagePrincipal(affichagePrincipal);

		//add main observer used to ask for global screen update ie: when loading 
		principal.addMainObserver(gameRenderer);
		
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

				ModelPartie partie = ModelPartie.Instantiate(touches, ModelPrincipal.this);
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
		loaderMenuPrincipal.addItem(LoaderUtils.load(LoaderUtils.MT_IMAGE, LoaderUtils.C_PRINCIPAL, null, this, ModelPartie.me));
		if(!InterfaceConstantes.IGNORE_SOUND)
			loaderMenuPrincipal.addItem(LoaderUtils.load(LoaderUtils.MT_SOUND, LoaderUtils.C_MUSIC, InterfaceConstantes.musiquePrincipal, this, ModelPartie.me));
		loaderMenuPrincipal.start();

		loaderMenuPrincipal.waitToEnd(principal);
		
		affichagePrincipal.setButtons();

		//start music 
		Music.me.startNewMusic(InterfaceConstantes.musiquePrincipal);

		//load all image + music + bruitage in different Thread 
		loaderAllMedia = new Loader(gameRenderer,this);
		loaderAllMedia.addItem(LoaderUtils.loadAllImagesAndSounds(ModelPartie.me));
		loaderAllMedia.start();
	}
	
	public int getFps()
	{
		return fps;
	}
	
	public void startGame()
	{
		Thread mainThread = new Thread(new MainLoop());
		mainThread.start();
		//Very important: This method tries to keep up: 
		//if one game tick took long enough to delay the next game tick, the executor service will consider this in the calculation for the next sleep duration.
		/*executor = Executors
                .newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new MainLoop(), 0, (int)Math.round(Config.getDeltaFrame(true)), TimeUnit.MILLISECONDS);//call scheduleAtFixedRate.shutdown() to stop this
		*/
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
			boolean comesFromPartie = (ModelPartie.me != null && ModelPartie.me.shoudResumeGame());
			if(!comesFromPartie){
				//musique 
				String nextMusic = InterfaceConstantes.musiqueOption;
				if(!Music.musiqueEnCours.equals(nextMusic))
				{
					Music.me.startNewMusic(nextMusic);
				}
			}
			else
				option.setSpecificReturn(GameModeType.GAME);
			//listener 
			gameRenderer.removeListener(currentGameModeType);

			//changement de mode
			currentGameModeType=newMode;
			currentGameMode = option;

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

			gameRenderer.changeGameModeRendering();

			//listener
			gameRenderer.addListener(currentGameModeType);

		}
		else if (newMode.equals(GameModeType.GAME))
		{
			//not drawing at first 			
			//If we come from the partie loader, don't init the partie again 
			if(ModelPartie.me.shoudResumeGame())
				gameRenderer.removeListener(currentGameModeType);
			else if(!currentGameModeType.equals(GameModeType.LOADER)){
				//listener
				gameRenderer.removeListener(currentGameModeType);
	
				//on reinitialiser les variables pour pouvoir rejouer plusieurs fois
				ModelPartie.me.init();
	
				//on lance la partie rapide si elle n'est pas deja en cours ie: le jeu n'est pas en pause
				//And load level
				ModelPartie.me.loaderPartie = new Loader(gameRenderer,this);
				ModelPartie.me.loaderPartie.addItem(LoaderUtils.loadWorld(choix.getNiveauSelectionne(), ModelPartie.me));
				ModelPartie.me.loaderPartie.addItem(LoaderUtils.waitForLoaderToEnd(loaderAllMedia));
				ModelPartie.me.loaderPartie.addItem(LoaderUtils.waitForGarbageCollectorToEndInALoader());
				ModelPartie.me.loaderPartie.start();
			}
			//changement de mode
			if(!ModelPartie.me.isGameModeLoaded()){//partie loader not done 
				currentGameModeType=GameModeType.LOADER;
				currentGameMode = ModelPartie.me.getLoaderGameMode(); //get loader from partie 
				((Loader)ModelPartie.me.getLoaderGameMode()).setCallback(new Runnable(){
					@Override
					public void run() {
						ModelPrincipal.this.setGameMode(GameModeType.GAME); //return back to this function to finish initialization of partie 
					}
				});
				gameRenderer.changeGameModeRendering(((Loader)ModelPartie.me.getLoaderGameMode()).getAffichageLoader());//begin transition to loader
			}
			else{
				currentGameModeType=newMode;
				currentGameMode = ModelPartie.me;
				
				//musique 
				if(!ModelPartie.me.shoudResumeGame()){
					int numMus = (int) (Math.random()*InterfaceConstantes.musiquePartie.length);
					String nextMusic = InterfaceConstantes.musiquePartie[numMus];
	
					if(!Music.musiqueEnCours.equals(nextMusic))
						Music.me.startNewMusic(nextMusic);			
					else
						//make sure that we start with the non slow version
						Music.me.endSlowDownMusic();
				}
				
				//affichage
				gameRenderer.changeGameModeRendering();
				
				//make sure that all other media are loaded correctly
				if(InterfaceConstantes.DEBUG_OBJECT_CREATION && !ModelPartie.me.shoudResumeGame())
					DebugObjectCreation.start();
						
				//listener
				gameRenderer.addListener(currentGameModeType);
	
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
			gameRenderer.changeGameModeRendering();


		}
		gameRenderer.validate();
	}
	
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
	public ArrayList<Image> getImages(ImageGroup group,ObjectType typeObj, ImageInfo info1,ImageInfo info2,int mouv_index)
	{
		return getImageGroup(group).getImages(typeObj, info1, info2,mouv_index);
	}
	@Override
	public ImagesContainer getImageGroup(ImageGroup group) {
		if(group.equals(ImageGroup.BACKGROUND))
			return ImagesBackground.me;
		else if(group.equals(ImageGroup.CONDITION))
			return ImagesCondition.me;
		else if(group.equals(ImageGroup.EFFECT))
			return ImagesEffect.me;
		else if(group.equals(ImageGroup.FLECHE))
			return ImagesFleche.me;
		else if(group.equals(ImageGroup.FLECHEICON))
			return ImagesFlecheIcon.me;
		else if(group.equals(ImageGroup.HEROS))
			return ImagesHeros.me;
		else if(group.equals(ImageGroup.MONDE))
			return ImagesMonde.me;
		else if(group.equals(ImageGroup.MONSTRE))
			return ImagesMonstre.me;
		else if(group.equals(ImageGroup.PRINCIPAL))
			return ImagesPrincipal.me;
		else if(group.equals(ImageGroup.TIRMONSTRE))
			return ImagesTirMonstre.me;
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
		
		if(InterfaceConstantes.IGNORE_SOUND)
			System.out.println("/!\\ START GAME WITHOUT SOUND /!\\");
		
		ModelPrincipal principal = new ModelPrincipal();
		principal.Init();

		//test();
		//Convertisseur conv = new Convertisseur();
		//conv.convertir();

	}

}
