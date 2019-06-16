//JFrame peut contenir plusieurs JPanel
package Affichage;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

/*
	 |------------> x
	 |
	 |
	 V
	 y 
 */
import javax.swing.JFrame;

import editeur.AffichageEditeur;
import gameConfig.InterfaceConstantes;
import menu.choixNiveau.AffichageChoixNiveau;
import menu.credit.AffichageCredit;
import menu.menuPrincipal.AbstractModelPrincipal;
import menu.menuPrincipal.AffichagePrincipal;
import option.AffichageOption;
import partie.modelPartie.AffichagePartie;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class Affichage extends JFrame implements InterfaceConstantes, Observer
{	
	public boolean changeVariable= false;
	
	AffichageOption affichageOption;
	AffichagePrincipal affichagePrincipal;
	AffichageChoixNiveau affichageChoix;
	AffichagePartie affichagePartie;
	AffichageEditeur affichageEditeur;
	AffichageCredit affichageCredit;


	/**
	 * Initialise Affichage
	 */  
	public Affichage(AffichagePrincipal _affichagePrincipal)
	{		
		affichagePrincipal=_affichagePrincipal;


		this.setFocusable(true);
		this.setJMenuBar(null);

		List<Image> icons = new ArrayList<Image>();
		icons.add(getImage("16x16.gif"));
		icons.add(getImage("32x32.gif"));
		icons.add(getImage("64x64.gif"));
		this.setIconImages(icons);

		affichagePrincipal.addListenerPrincipal();
		AbstractModelPrincipal.changeFrame=true;
		actuAffichage();

	}
	
	/***
	 * Normally those references are set in the constructor. However in order to allow a loading screen (because model partie might take some time to load), we
	 * first create Affichage without those and then add them when they are loaded 
	 * @param _affichageOption
	 * @param _affichageEditeur
	 * @param _affichageCredit
	 * @param _affichageChoix
	 * @param _affichagePartie
	 */
	public void setOtherAffichageReferences(AffichageOption _affichageOption, 
			AffichageEditeur _affichageEditeur,AffichageCredit _affichageCredit,AffichageChoixNiveau _affichageChoix,AffichagePartie _affichagePartie)
	{
		affichageOption = _affichageOption;
		affichageEditeur=_affichageEditeur;
		affichageCredit=_affichageCredit;
		affichageChoix = _affichageChoix;
		affichagePartie=_affichagePartie;
	}
	
	protected Image getImage(String name)
	{
		return Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/icons/"+name));
	}
	/**
	 * Actualise le contenu de la frame (this)
	 *
	 */
	public void actuAffichage()
	{
		if(AbstractModelPrincipal.changeFrame)
		{
			if(affichageOption != null)
				affichageOption.retour.setContentAreaFilled(false);

			if(this.getJMenuBar()!= null){this.setJMenuBar(null);}
			if(AbstractModelPrincipal.modeActuel=="Principal")
			{	
				this.setContentPane(affichagePrincipal.getContentPane());
				this.setTitle("Menu principal"); 
			}
			else if (AbstractModelPrincipal.modeActuel=="Option")
			{
				this.setContentPane(affichageOption.getContentPane());
				this.setTitle("Options"); 	   
			}
			else if (AbstractModelPrincipal.modeActuel=="Editeur")
			{	
				this.setContentPane(affichageEditeur.getContentPane());
				this.setJMenuBar(affichageEditeur.getJMenuBar());
				this.setTitle("Editeur"); 
				this.revalidate();
			}
			else if (AbstractModelPrincipal.modeActuel=="Credit")
			{	
				this.setContentPane(affichageCredit.getContentPane());
				this.setTitle("Credit"); 
				this.revalidate();
			}
			else if (AbstractModelPrincipal.modeActuel=="ChoixNiveau")
			{
				this.setContentPane(affichageChoix.getContentPane());
				this.setTitle("Choix niveau");
			}
			else if (AbstractModelPrincipal.modeActuel=="Partie")
			{
				this.setContentPane(affichagePartie.getContentPane());
				this.setTitle("Partie rapide"); 
				this.revalidate();
				affichagePartie.requestGameFocus();
			}
			this.repaint();
		}
		
	}


	/**
	 * Ajoute les listeners selon le mode de jeu
	 */
	public void addListener(String mode)
	{
		if(mode=="Editeur")
		{
			affichageEditeur.addListenerEditeur();
		}
		else if(mode=="Credit")
		{
			affichageCredit.addListenerCredit();
		}
		else if (mode=="Option")
		{
			affichageOption.addListenerOption();
		}
		else if (mode=="ChoixNiveau")
		{
			affichageChoix.addListener();
		}
		else if (mode=="Partie")
		{
			affichagePartie.addListenerPartie();
		}
		else if (mode=="Principal")
		{
			affichagePrincipal.addListenerPrincipal();
		}
		else 
		{
			throw new IllegalArgumentException("Impossible d'ajouter les listerners pour ce mode");
		}
	}
	/**
	 * Retire les listeners selon le mode de jeu
	 */
	public void removeListener (String mode)
	{
		if(mode=="Editeur")
		{
			affichageEditeur.removeListenerEditeur();
		}
		else if(mode=="Credit")
		{
			affichageCredit.removeListenerCredit();
		}
		else if (mode=="Option")
		{
			affichageOption.removeListenerOption();
		}
		else if (mode=="Partie")
		{
			affichagePartie.removeListenerPartie();
		}
		else if (mode=="ChoixNiveau")
		{
			affichageChoix.removeListener();
		}
		else if (mode=="Principal")
		{
			affichagePrincipal.removeListenerPrincipal();
		}
		else 
		{
			throw new IllegalArgumentException("Impossible de retirer les listerners pour ce mode");
		}
	}
	//}}
	public void update() {
		this.repaint();
	}
}

