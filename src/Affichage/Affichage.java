//JFrame peut contenir plusieurs JPanel
package Affichage;
/*
	 |------------> x
	 |
	 |
	 V
	 y 
 */
import javax.swing.JFrame;

import menuPrincipal.AbstractModelPrincipal;
import menuPrincipal.AffichagePrincipal;
import option.AffichageOption;
import partie.AffichagePartie;
import principal.InterfaceConstantes;
import choixNiveau.AffichageChoixNiveau;
import editeur.AffichageEditeur;

@SuppressWarnings("serial")
public class Affichage extends JFrame implements InterfaceConstantes
{	
	public boolean changeVariable= false;
	
	AffichageOption affichageOption;
	AffichagePrincipal affichagePrincipal;
	AffichageChoixNiveau affichageChoix;
	AffichagePartie affichagePartie;
	AffichageEditeur affichageEditeur;


	/**
	 * Initialise Affichage
	 */  
	public Affichage(AffichagePrincipal _affichagePrincipal,AffichageOption _affichageOption, 
			AffichageEditeur _affichageEditeur,AffichageChoixNiveau _affichageChoix,AffichagePartie _affichagePartie)
	{		
		affichagePrincipal=_affichagePrincipal;
		affichageOption = _affichageOption;
		affichageEditeur=_affichageEditeur;
		affichageChoix = _affichageChoix;
		affichagePartie=_affichagePartie;

		this.setFocusable(true);
		this.setJMenuBar(null);

		affichagePrincipal.addListenerPrincipal();
		AbstractModelPrincipal.changeFrame=true;
		actuAffichage();

	}


	/**
	 * Actualise le contenu de la frame (this)
	 *
	 */
	public void actuAffichage()
	{
		if(AbstractModelPrincipal.changeFrame)
		{
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
		}
		this.repaint();
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
			//addListenerPartie(partieRap);
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
		else if (mode=="Option")
		{
			affichageOption.removeListenerOption();
		}
		else if (mode=="Partie")
		{
			affichagePartie.removeListenerPartie();
			//removeListenerPartie(partieRap);
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

}

