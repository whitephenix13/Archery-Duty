package menuPrincipal;

import java.awt.Dimension;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import com.sun.management.GarbageCollectionNotificationInfo ;

import Affichage.Affichage;
import choixNiveau.AffichageChoixNiveau;
import choixNiveau.ControlerChoixNiveau;
import choixNiveau.ModelChoixNiveau;
import credit.AffichageCredit;
import debug.Debug_time;
import editeur.AffichageEditeur;
import editeur.ControlerEditeur;
import editeur.ModelEditeur;
import loading.LoadAllMedias;
import loading.LoadAllMedias.CustomLoad;
import music.Music;
import observer.Observer;
import option.AffichageOption;
import option.Config;
import option.ControlerOption;
import option.ModelOption;
import partie.AffichagePartie;
import partie.ControlerPartie;
import partie.ModelPartie;
import principal.InterfaceConstantes;
import principal.TypeApplication;
import types.Touches;


public class ModelPrincipal extends AbstractModelPrincipal{
	
	protected void Init() {
		debutBoucle=false;

		touches=new Touches();


		principal = this;
		controlerPrincipal = new ControlerPrincipal(principal);
		affichagePrincipal = new AffichagePrincipal(controlerPrincipal);
		principal.addObserver(affichagePrincipal);
		
		edit = new ModelEditeur();
		controlerEditeur = new ControlerEditeur(edit);
		affichageEditeur = new AffichageEditeur(controlerEditeur);
		edit.addObserver(affichageEditeur);

		affichageCredit= new AffichageCredit();

		choix = new ModelChoixNiveau();
		controlerChoix= new ControlerChoixNiveau(choix) ;
		affichageChoix = new AffichageChoixNiveau(controlerChoix);
		choix.addObserver(affichageChoix);
		affichageChoix.init();

		partie = new ModelPartie(touches);
		controlerPartie= new ControlerPartie(partie) ;
		affichagePartie = new AffichagePartie(controlerPartie);
		partie.addObserver(affichagePartie);
		partie.init();

		option = new ModelOption(touches,partie.inputPartie);
		controlerOption = new ControlerOption(option);
		affichageOption = new AffichageOption(controlerOption);
		option.addObserver(affichageOption);

		affich = new Affichage(affichagePrincipal,affichageOption,affichageEditeur,affichageCredit,affichageChoix,affichagePartie);
		//on met en place le conteneur 
		affich.setResizable(false);

		affich.getContentPane().setFocusable(true);
		affich.getContentPane().requestFocus();

		affich.setSize(new Dimension(InterfaceConstantes.LARGEUR_FENETRE,InterfaceConstantes.HAUTEUR_FENETRE));
		affich.setLocationRelativeTo(null);
		affich.setTitle("Menu principal");
		affich.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		affich.setVisible(true);		
		
		//add main observer used to ask for global screen update ie: when loading 
		principal.addMainObserver(affich);
		edit.addMainObserver(affich);
		choix.addMainObserver(affich);
		partie.addMainObserver(affich);
		option.addMainObserver(affich);

		Music.init();

		//Wait for background to be visible + music principal to be loaded
		LoadAllMedias loader = new LoadAllMedias();
		loader.load(LoadAllMedias.MT_IMAGE, LoadAllMedias.C_PRINCIPAL, "background", this, partie);
		loader.load(LoadAllMedias.MT_SOUND, LoadAllMedias.C_MUSIC, InterfaceConstantes.musiquePrincipal, this, partie);
		loader.start();
		loader.wait(principal, principal,true);

		affichagePrincipal.setButtons();
		affich.repaint();

		//start music 
		Music.me.setMusic(InterfaceConstantes.musiquePrincipal);
		Music.me.startMusic();

		//load all image + music + bruitage in different Thread 
		allMediaLoader = new LoadAllMedias();
		allMediaLoader.load(this,partie);
		allMediaLoader.start();
	}

	//TODO: TEST TO GET NOTIFICATION FROM GARABGE COLLECTOR 
	static
	{
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
					if(InterfaceConstantes.DEBUG_TIME_VERBOSE>=1)
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


			//affichage
			changeFrame=true;
			affich.actuAffichage();

			//on lance la partie rapide si elle n'est pas deja en cours ie: le jeu n'est pas en pause
			//And load level
			LoadAllMedias partieLoader = new LoadAllMedias();
			partieLoader.loadNiveau(choix.getNiveauSelectionne(), partie);
			partieLoader.start();
			partieLoader.wait(partie, partie,false);
			
			//make sure that all other media are loaded correctly
			allMediaLoader.wait(partie, partie,true);
			
			//Get rid of all the images and music loaded in cache 
			System.gc();
			partie.startPartie(InterfaceConstantes.SPAWN_PROGRAMME);//SPAWN_ALEATOIRE, SPAWN_PROGRAMME

			//listener
			affich.addListener(modeActuel);

			//définition du thread d'affichage qui fait tourner partie rapide.play() en continue 
			class ThreadAffichage implements Runnable
			{
				public void run() 
				{
					Debug_time debugTime = new Debug_time();

					do
					{
						debugTime.init();

						double deltaTime= (System.nanoTime()-last_update)/Math.pow(10, 6);//delta time in ms
						if(deltaTime>Config.getDeltaFrame(true)){
							if(InterfaceConstantes.DEBUG_TIME_VERBOSE>=1)
								System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

							debugTime.elapsed("partie", 1);

							last_update=System.nanoTime();
							partie.play(affich);

							debugTime.elapsed("repaint", 1);


							affichagePartie.repaintPartie();

							debugTime.elapsed("validate affichage", 1);

							affichagePartie.validateAffichagePartie(affich);
							if(!partie.getinPause() && (!partie.slowDown || (partie.slowDown && partie.slowCount==0)))
								partie.nextFrame();
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
	public static void main(String[] args) throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException, URISyntaxException 
	{

		TypeApplication.isJar= new TypeApplication().isJar();
		ModelPrincipal principal = new ModelPrincipal();
		principal.Init();
		principal.StartBoucleJeu();
		//Convertisseur conv = new Convertisseur();
		//conv.convertir();

	}
}
