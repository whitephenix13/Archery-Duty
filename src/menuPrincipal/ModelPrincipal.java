package menuPrincipal;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import Affichage.Affichage;
import choixNiveau.AffichageChoixNiveau;
import choixNiveau.ControlerChoixNiveau;
import choixNiveau.ModelChoixNiveau;
import credit.AffichageCredit;
import editeur.AffichageEditeur;
import editeur.ControlerEditeur;
import editeur.ModelEditeur;
import music.Music;
import music.ThreadMusique;
import option.AffichageOption;
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
		try 
		{
			threadMusique = new ThreadMusique(InterfaceConstantes.musiquePrincipal);
			t = new Thread(threadMusique);

		} 
		catch (UnsupportedAudioFileException | IOException| LineUnavailableException e) {e.printStackTrace();}
		touches=new Touches();
		music=threadMusique.getMusic();

		principal = this;
		controlerPrincipal = new ControlerPrincipal(principal);
		affichagePrincipal = new AffichagePrincipal(controlerPrincipal);
		principal.addObserver(affichagePrincipal);

		option = new ModelOption();
		controlerOption = new ControlerOption(option);
		affichageOption = new AffichageOption(controlerOption);
		option.addObserver(affichageOption);

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

		partie = new ModelPartie();
		controlerPartie= new ControlerPartie(partie) ;
		affichagePartie = new AffichagePartie(controlerPartie);
		partie.addObserver(affichagePartie);
		partie.init();

		//try 
		//{
		//	partieRap= new PartieRapide(touches,variablesAffichage, variablesDeplace);
		//} 
		//catch (InterruptedException | UnsupportedAudioFileException| IOException | LineUnavailableException e) {e.printStackTrace();}

		affich = new Affichage(affichagePrincipal,affichageOption,affichageEditeur,affichageCredit,affichageChoix,affichagePartie);

		threadMusique.musique.startMusique();
		//on met en place le conteneur 
		affich.setResizable(false);

		affich.getContentPane().setFocusable(true);
		affich.getContentPane().requestFocus();
		//on ajoute de quoi écouter notre clavier

		affich.setSize(new Dimension(InterfaceConstantes.LARGEUR_FENETRE,InterfaceConstantes.HAUTEUR_FENETRE));
		affich.setLocationRelativeTo(null);
		affich.setTitle("Menu principal");
		affich.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		affich.setVisible(true);
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
				threadMusique.musique.stopMusique();
				try 
				{
					threadMusique.musique.setMusic(nextMusic, music);
				} 
				catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {e.printStackTrace();}
				threadMusique.musique.startMusique();
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
				threadMusique.musique.stopMusique();
				try
				{
					threadMusique.musique.setMusic(nextMusic, music);
				} 
				catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {e.printStackTrace();}
				threadMusique.musique.startMusique();
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
				threadMusique.musique.stopMusique();
				try
				{
					threadMusique.musique.setMusic(nextMusic, music);
				} 
				catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {e.printStackTrace();}
				threadMusique.musique.startMusique();
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
				threadMusique.musique.stopMusique();
				try 
				{
					threadMusique.musique.setMusic(nextMusic, music);
				} catch (UnsupportedAudioFileException | IOException| LineUnavailableException e) {e.printStackTrace();}
				threadMusique.musique.startMusique();
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
			//musique 
			int numMus = (int) (Math.random()*InterfaceConstantes.musiquePartie.length);
			String nextMusic = InterfaceConstantes.musiquePartie[numMus];

			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				threadMusique.musique.stopMusique();
				try
				{
					threadMusique.musique.setMusic(nextMusic, music);
				} 
				catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {e.printStackTrace();}
				threadMusique.musique.startMusique();
			}
			//listener
			affich.removeListener(modeActuel);

			//on reinitialiser les variables pour pouvoir rejouer plusieurs fois

			partie.init();


			//changement de mode
			modeActuel="Partie";


			//affichage
			changeFrame=true;
			affich.actuAffichage();

			//listener
			affich.addListener(modeActuel);

			//on lance la partie rapide si elle n'est pas deja en cours ie: le jeu n'est pas en pause

			partie.startPartie(InterfaceConstantes.SPAWN_PROGRAMME,choix.getNiveauSelectionne());//SPAWN_ALEATOIRE, SPAWN_PROGRAMME

			//définition du thread d'affichage qui fait tourner partie rapide.play() en continue 
			class ThreadAffichage implements Runnable
			{
				public void run() 
				{
					do
					{
						partie.play(affich);
						affichagePartie.repaintPartie();
						affichagePartie.validateAffichagePartie(affich);

					}
					while(!partie.getFinPartie());//condition de fin
				}

			}
			ThreadAffichage t2= new ThreadAffichage();

			//on lance la partie
			t2.run();
			//affich.actuAffichage();


		}
		else if (modeSuivant=="Principal")
		{
			//musique 
			String nextMusic = InterfaceConstantes.musiquePrincipal;
			if(!Music.musiqueEnCours.equals(nextMusic))
			{
				threadMusique.musique.stopMusique();
				try 
				{
					threadMusique.musique.setMusic(nextMusic, music);
				} catch (UnsupportedAudioFileException | IOException| LineUnavailableException e) {e.printStackTrace();}
				threadMusique.musique.startMusique();
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
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}//Need to slow down the loop or all others action are ignored
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
