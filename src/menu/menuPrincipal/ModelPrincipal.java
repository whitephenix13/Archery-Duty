package menu.menuPrincipal;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import com.sun.management.GarbageCollectionNotificationInfo ;

import Affichage.Affichage;
import debug.DebugObjectCreation;
import debug.DebugTime;
import editeur.AffichageEditeur;
import editeur.ControlerEditeur;
import editeur.ModelEditeur;
import gameConfig.InterfaceConstantes;
import images.ImagesPrincipal;
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
import partie.bloc.Bloc;
import partie.collision.Hitbox;
import partie.modelPartie.AffichagePartie;
import partie.modelPartie.ControlerPartie;
import partie.modelPartie.ModelPartie;
import utils.TypeApplication;


@SuppressWarnings("unused")
public class ModelPrincipal extends AbstractModelPrincipal{

	protected void Init() {
		
		
		debutBoucle=false;

		touches=new Touches();

		principal = this;
		controlerPrincipal = new ControlerPrincipal(principal);
		affichagePrincipal = new AffichagePrincipal(controlerPrincipal);//TODO: remove this behaviour in AffichagePrincipal/Init? Load the required media (background image)
		principal.addObserver(affichagePrincipal);

		
		affich = new Affichage(affichagePrincipal);

		//on met en place le conteneur 
		affich.setResizable(false);

		affich.getContentPane().setFocusable(true);
		affich.getContentPane().requestFocus();

		affich.setSize(new Dimension(InterfaceConstantes.WINDOW_WIDTH,InterfaceConstantes.WINDOW_HEIGHT));
		affich.setLocationRelativeTo(null);
		affich.setTitle("Menu principal");
		affich.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		affich.setVisible(true);	
		

		//add main observer used to ask for global screen update ie: when loading 

		principal.addMainObserver(affich);

		//TODO: show loading screen here => first instantiate affichage, then give references to other affichages 
		LoaderItem mainObjectsCreation = new LoaderItem(){
			@Override
			public void run()
			{
				edit = new ModelEditeur();
				controlerEditeur = new ControlerEditeur(edit);
				affichageEditeur = new AffichageEditeur(controlerEditeur);
				edit.addObserver(affichageEditeur);
				percentage = 20;
				
				affichageCredit= new AffichageCredit();

				choix = new ModelChoixNiveau();
				controlerChoix= new ControlerChoixNiveau(choix) ;
				affichageChoix = new AffichageChoixNiveau(controlerChoix);
				choix.addObserver(affichageChoix);
				affichageChoix.init();
				percentage = 40;

				partie = new ModelPartie(touches);
				controlerPartie= new ControlerPartie(partie) ;
				affichagePartie = new AffichagePartie(controlerPartie);
				partie.addObserver(affichagePartie);
				partie.init();
				percentage = 60;

				option = new ModelOption(touches,partie.inputPartie);
				controlerOption = new ControlerOption(option);
				affichageOption = new AffichageOption(controlerOption);
				option.addObserver(affichageOption);
				percentage = 80;

				
				//Now that all other affichage are loaded, we can set the reference and end the loading screen 
				affich.setOtherAffichageReferences(affichageOption,affichageEditeur,affichageCredit,affichageChoix,affichagePartie);
				
				//add main observer used to ask for global screen update ie: when loading 
				edit.addMainObserver(affich);
				choix.addMainObserver(affich);
				partie.addMainObserver(affich);
				option.addMainObserver(affich);

				Music.init();
			}
		};
		
		
		//Wait for background to be visible + music principal to be loaded
		loaderMenuPrincipal = new Loader();
		loaderMenuPrincipal.addItem(mainObjectsCreation);
		loaderMenuPrincipal.addItem(LoaderUtils.load(LoaderUtils.MT_IMAGE, LoaderUtils.C_PRINCIPAL, ImagesPrincipal.BACKGROUND, this, partie));
		loaderMenuPrincipal.addItem(LoaderUtils.load(LoaderUtils.MT_SOUND, LoaderUtils.C_MUSIC, InterfaceConstantes.musiquePrincipal, this, partie));
		loaderMenuPrincipal.start();
		loaderMenuPrincipal.wait(principal);

		affichagePrincipal.setButtons();
		affich.repaint();

		//start music 
		Music.me.setMusic(InterfaceConstantes.musiquePrincipal);
		Music.me.startMusic();

		//load all image + music + bruitage in different Thread 
		loaderAllMedia = new Loader();
		loaderAllMedia.addItem(LoaderUtils.loadAllImagesAndSounds(this, partie));
		loaderAllMedia.start();
	}

	//TO GET NOTIFICATION FROM GARABGE COLLECTOR 
	static
	{
		if(InterfaceConstantes.DEBUG_TIME_VERBOSE>=1){
			// notification listener. is notified whenever a gc finishes.
			NotificationListener notificationListener = new NotificationListener()
			{
				@Override
				public void handleNotification(Notification notification,Object handback)
				{
					if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION))
					{
						// extract garbage collection information from notification.
						GarbageCollectionNotificationInfo gcInfo = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
	
						// access garbage collection information...
						
							System.out.println("*************Garbage Collector Call**********");
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

	protected void ChangementMode () 
	{
		changeMode=false;

		if (modeSuivant == "Quitter")
		{
			modeActuel="Quitter";
			System.exit(0);
		}
		else if (modeSuivant=="Option")
		{			
			//musique 
			String nextMusic = InterfaceConstantes.musiqueOption;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener 
			affich.removeListener(modeActuel);

			//changement de mode
			modeActuel="Option";

			//affichage
			changeFrame=true;
			affich.actuAffichage();

			//listener
			affich.addListener(modeActuel);

		}
		else if (modeSuivant=="Editeur")
		{

			//musique 
			String nextMusic = InterfaceConstantes.musiqueEditeur;

			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener
			affich.removeListener(modeActuel);
			
			loaderAllMedia.wait(partie); //Make sure that all media are loader before running editeur
			edit.imMonde = partie.imMonde;
			
			//changement de mode
			modeActuel="Editeur";

			//listener
			affich.addListener(modeActuel);

			//affichage
			changeFrame=true;
			affich.actuAffichage();
		}
		else if (modeSuivant=="Credit")
		{

			//musique 
			String nextMusic = InterfaceConstantes.musiqueEditeur;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener
			affich.removeListener(modeActuel);

			//changement de mode
			modeActuel="Credit";

			//listener
			affich.addListener(modeActuel);

			//affichage
			changeFrame=true;
			affich.actuAffichage();
		}
		else if (modeSuivant=="ChoixNiveau")
		{
			//musique 
			String nextMusic = InterfaceConstantes.musiquePrincipal;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener
			affich.removeListener(modeActuel);

			//changement de mode
			modeActuel="ChoixNiveau";

			//affichage
			changeFrame=true;

			affich.actuAffichage();

			//listener
			affich.addListener(modeActuel);

		}
		else if (modeSuivant=="Partie")
		{
			//not drawing at first 
			partie.computationDone=false;

			//musique 
			int numMus = (int) (Math.random()*InterfaceConstantes.musiquePartie.length);
			String nextMusic = InterfaceConstantes.musiquePartie[numMus];

			if(!Music.musiqueEnCours.equals(nextMusic))
				Music.me.startNewMusic(nextMusic);			
			else
				//make sure that we start with the non slow version
				Music.me.endSlowDownMusic();
			//listener
			affich.removeListener(modeActuel);

			//on reinitialiser les variables pour pouvoir rejouer plusieurs fois

			partie.init();


			//changement de mode
			modeActuel="Partie";



			//on lance la partie rapide si elle n'est pas deja en cours ie: le jeu n'est pas en pause
			//And load level
			partie.loaderPartie = new Loader();
			partie.loaderPartie.addItem(LoaderUtils.loadWorld(choix.getNiveauSelectionne(), partie));
			partie.loaderPartie.start();
			
			//affichage
			changeFrame=true;
			affich.actuAffichage();
			
			partie.loaderPartie.wait(partie);

			//make sure that all other media are loaded correctly
			loaderAllMedia.wait(partie);
			if(InterfaceConstantes.DEBUG_OBJECT_CREATION)
				DebugObjectCreation.start();
			//Get rid of all the images and music loaded in cache 
			//System.gc();
			partie.startPartie(InterfaceConstantes.SPAWN_PROGRAMME);//SPAWN_ALEATOIRE, SPAWN_PROGRAMME

			//listener
			affich.addListener(modeActuel);

			//définition du thread d'affichage qui fait tourner partie rapide.play() en continue 
			class ThreadAffichage implements Runnable
			{
				public void run() 
				{
					debugTime = new DebugTime();
					final int currentVerbose = 1;
					do
					{
						debugTime.init();

						double deltaTime= (System.nanoTime()-last_update)/Math.pow(10, 6);//delta time in ms
						if((deltaTime>Config.getDeltaFrame(true)) &&partie.computationDone){ //TODO: only execute the loop if the previous iteration (partie + draw) was done? If partie done alone ?
							//TODO: currently computationDone is used as an indicator that the screen can be drawn
							//TODO: -> split that between "Calculation done" and "Can be drawn" 
							//TODO: only enter a new loop if the calculation is done (not the drawing so that the in game time is respected and player can 
							//still anticipate dodge/actions. Otherwise everything is slow down)
							//TODO: make sure that physics is not impacted if deltaTime is twice bigger than usual (should be "fixed in game time" between two graphics update)
							
							partie.computationDone=false;
							
							if(InterfaceConstantes.DEBUG_TIME_VERBOSE>=1)
								System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
							ModelPrincipal.debugTime.startElapsedForVerbose(currentVerbose);
						
							last_update=System.nanoTime();
							partie.play(affich);
							debugTime.elapsed("partie", currentVerbose);

							affichagePartie.repaintPartie();
							debugTime.elapsed("repaint", currentVerbose);


							affichagePartie.validateAffichagePartie(affich);
							if(!partie.getinPause() && (!partie.slowDown || (partie.slowDown && partie.slowCount==0)))
								partie.nextFrame();
							debugTime.elapsed("validate affichage", currentVerbose);

						}
					}
					while(!partie.getFinPartie());//condition de fin
					//last draw when partie ends
					partie.computationDone=true;
				}

			}
			Thread t2= new Thread(new ThreadAffichage());

			//on lance la partie
			partie.computationDone=true;
			t2.start();
			//affich.actuAffichage();


		}
		else if (modeSuivant=="Principal")
		{
			//musique 
			String nextMusic = InterfaceConstantes.musiquePrincipal;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				Music.me.startNewMusic(nextMusic);
			}
			//listener 
			affich.removeListener(modeActuel);

			//changement de mode
			modeActuel="Principal";

			//listeners
			affich.addListener(modeActuel);


			//affichage
			changeFrame=true;
			affich.actuAffichage();


		}
		affich.repaint();
		affich.validate();
	}
	protected void StartBoucleJeu()
	{
		//Coeur du programme 
		while (true)
		{
			if(changeMode)
			{				
				ChangementMode();	
			}
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}//Need to slow down the loop or all others action are ignored
		}
	}
	
	public static double r_ceil(double val, int dec)
	{
		double fact = Math.pow(10, dec);
		return Math.ceil(val *fact)/fact;
		
	}
	public static void test()
	{
	}
	
	static int count = 0;

	  int x;
	  ModelPrincipal() {
	    x = count;
	  }
	public static void main(String[] args) throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException, URISyntaxException 
	{	
		TypeApplication.isJar= new TypeApplication().isJar();
		/*if(InterfaceConstantes.DEBUG_OBJECT_CREATION)
			DebugObjectCreation.start();*/
		
		ModelPrincipal principal = new ModelPrincipal();
		principal.Init();
		principal.StartBoucleJeu();

		//test();
		//Convertisseur conv = new Convertisseur();
		//conv.convertir();

	}
}
