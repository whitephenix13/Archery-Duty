//JFrame peut contenir plusieurs JPanel
package Affichage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
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
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import editeur.AffichageEditeur;
import gameConfig.InterfaceConstantes;
import menu.choixNiveau.AffichageChoixNiveau;
import menu.credit.AffichageCredit;
import menu.menuPrincipal.AbstractModelPrincipal;
import menu.menuPrincipal.AffichagePrincipal;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameHandler.GameModeType;
import option.AffichageOption;
import partie.modelPartie.AffichagePartie;
import serialize.Serialize;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class Affichage extends JFrame implements InterfaceConstantes, Observer
{	
	public boolean changeVariable= false;
	public JPanel mainPanel;
	private JLayeredPane layeredPane; //layer 0: current current screen OR Layer 0: old screen fading out; Layer -1: new screen to come 
	private Drawable componentToFadeOut;
	private Drawable currentMainComponent; //the component that should be shown on screen. When transitioning, this corresponds to the component that does not fade out
	
	private boolean waitForLoading; //Indicates that the loading is not done so we should wait for it to end before displaying the screen of the next mode
	private GameHandler gameHandler;
	private boolean inTransition;
	public boolean isInTransition(){return inTransition;}
	
	AffichageOption affichageOption;
	AffichagePrincipal affichagePrincipal;
	AffichageChoixNiveau affichageChoix;
	AffichagePartie affichagePartie;
	AffichageEditeur affichageEditeur;
	AffichageCredit affichageCredit;
		

	/**
	 * Initialise Affichage
	 */  
	public Affichage(AffichagePrincipal _affichagePrincipal, GameHandler gameHandler)
	{		
		affichagePrincipal=_affichagePrincipal;

		this.gameHandler =gameHandler;
		this.setFocusable(true);
		this.setJMenuBar(null);

		List<Image> icons = new ArrayList<Image>();
		icons.add(getImage("16x16.gif"));
		icons.add(getImage("32x32.gif"));
		icons.add(getImage("64x64.gif"));
		this.setIconImages(icons);

		affichagePrincipal.addListenerPrincipal();
		//REMOVE AbstractModelPrincipal.changeFrame=true;
		
		mainPanel= new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setDoubleBuffered(true);

		layeredPane= new JLayeredPane();
		layeredPane.setPreferredSize(InterfaceConstantes.tailleEcran);
		layeredPane.setOpaque(true);//Force this to opaque otherwise background is not drawn
		layeredPane.setBackground(InterfaceConstantes.BACKGROUND_COLOR);
		layeredPane.setDoubleBuffered(true);
		
		mainPanel.add(layeredPane, BorderLayout.CENTER);
		this.setContentPane(mainPanel);
		waitForLoading=false;
		
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
		affichageOption.setFrameReference(this);
		affichageEditeur=_affichageEditeur;
		affichageEditeur.setFrameReference(this);
		affichageCredit=_affichageCredit;
		affichageCredit.setFrameReference(this);
		affichageChoix = _affichageChoix;
		affichageChoix.setFrameReference(this);
		affichagePartie=_affichagePartie;
		affichagePartie.setFrameReference(this);
		affichagePrincipal.setFrameReference(this);
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
		actuAffichage(null);
	}
	/***
	 * 
	 * @param affichageLoader used to execute the transition from a loading screen to the gamemode
	 */
	public void actuAffichage(Drawable affichageLoader)
	{
		if(affichageOption != null)
			affichageOption.retour.setContentAreaFilled(false);

		if(this.getJMenuBar()!= null){this.setJMenuBar(null);}
		if(gameHandler.currentGameMode().equals(GameModeType.MAIN_MENU))
		{	
			if(affichagePrincipal.getLoader() ==null){
				return;
			}
			/*REMOVEif(!affichagePrincipal.getLoader().isLoadingDone())
			{
				System.out.println("2");
				if(!waitForLoading)
					beginTransition(affichagePrincipal.getLoader().getAffichageLoader());
				
				forceActuAffichage=true;
				waitForLoading=true;
			}*/
			waitForLoading=false;
			beginTransition(affichagePrincipal);
			//REMOVEthis.setContentPane(affichagePrincipal.getContentPane());
			this.setTitle("Menu principal"); 
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.OPTION))
		{
			beginTransition(affichageOption);
			//this.setContentPane(affichageOption.getContentPane());
			this.setTitle("Options"); 	   
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.EDITOR))
		{	
			beginTransition(affichageEditeur);
			//REMOVEthis.setContentPane(affichageEditeur.getContentPane());
			this.setJMenuBar(affichageEditeur.getJMenuBar());
			this.setTitle("Editeur"); 
			this.revalidate();
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.CREDIT))
		{	
			beginTransition(affichageCredit);
			//REMOVEthis.setContentPane(affichageCredit.getContentPane());
			this.setTitle("Credit"); 
			this.revalidate();
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.LEVEL_SELECTION))
		{
			beginTransition(affichageChoix);
			//REMOVEthis.setContentPane(affichageChoix.getContentPane());
			this.setTitle("Choix niveau");
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.GAME))
		{
			beginTransition(affichagePartie);
			this.setTitle("Partie rapide"); 
			//this.revalidate();
			affichagePartie.requestGameFocus();
							
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.LOADER))
		{
			//affichageLoader should not be null
			beginTransition(affichageLoader);//get affichage loader to show 
			this.setTitle("Loading"); 
							
		}
		//REMOVE this.repaint();
	
	}


	/**
	 * Ajoute les listeners selon le mode de jeu
	 */
	public void addListener(GameModeType mode)
	{
		if(mode.equals(GameModeType.EDITOR))
		{
			affichageEditeur.addListenerEditeur();
		}
		else if(mode.equals(GameModeType.CREDIT))
		{
			affichageCredit.addListenerCredit();
		}
		else if (mode.equals(GameModeType.OPTION))
		{
			affichageOption.addListenerOption();
		}
		else if (mode.equals(GameModeType.LEVEL_SELECTION))
		{
			affichageChoix.addListener();
		}
		else if (mode.equals(GameModeType.GAME))
		{
			affichagePartie.addListenerPartie();
		}
		else if (mode.equals(GameModeType.MAIN_MENU))
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
	public void removeListener (GameModeType mode)
	{
		if(mode.equals(GameModeType.EDITOR))
		{
			affichageEditeur.removeListenerEditeur();
		}
		else if(mode.equals(GameModeType.CREDIT))
		{
			affichageCredit.removeListenerCredit();
		}
		else if (mode.equals(GameModeType.OPTION))
		{
			affichageOption.removeListenerOption();
		}
		else if (mode.equals(GameModeType.GAME))
		{
			affichagePartie.removeListenerPartie();
		}
		else if (mode.equals(GameModeType.LEVEL_SELECTION))
		{
			affichageChoix.removeListener();
		}
		else if (mode.equals(GameModeType.MAIN_MENU))
		{
			affichagePrincipal.removeListenerPrincipal();
		}
		else if (mode.equals(GameModeType.LOADER))
		{
			//nothing to do
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
	
	public boolean isScreenFading()
	{
		if(componentToFadeOut==null)
			return false;
		return componentToFadeOut.isFading;
	}
	public void warnFadeOutCanStart()
	{
		if(componentToFadeOut!=null && !componentToFadeOut.isFading)
			componentToFadeOut.startFadeOut(InterfaceConstantes.SCREEN_FADE_OUT_TIME);
		inTransition=true;
	}
	public void warnFadeOutEnded(Drawable whoEnded)
	{
		System.out.println("====FadeOutEnded"  + componentToFadeOut+" "+currentMainComponent );

		//See the comment next to layeredPane variable definition
		if(componentToFadeOut!=null)
			layeredPane.remove(componentToFadeOut.mainPanel);//remove the component at Layer 0
		componentToFadeOut=null;
		//move the component from Layer -1 to Layer 0
		layeredPane.moveToFront(layeredPane.getComponent(0));//assumes there is only 1 component 
		inTransition=false;
	}
	
	/***
	 * Warning: The transition actually begins when warnFadeOutCanStart is called. This function is called by the drawable to has to be shown on screen (so that 
	 * this component manages the moment when it is ready to be displayed)
	 */
	public void beginTransition(Drawable newDrawable)
	{
		System.out.println("====Begin transition (fade,current) new => ("  + componentToFadeOut+","+currentMainComponent+") "+newDrawable );
		//Handle case where we transition to itself => directly return
		if(currentMainComponent!= null && currentMainComponent.equals(newDrawable))
		{
			warnFadeOutEnded(componentToFadeOut);//also moves currentMainComponent from layer -1 to 0
			return;
		}
		
		//Handle the case where we are in the middle of a transition
		if(componentToFadeOut!=null)
		{
			//force the transition to end
			warnFadeOutEnded(componentToFadeOut);//also moves currentMainComponent from layer -1 to 0
		}
		//At this point, layeredPane should only have one component
		componentToFadeOut = currentMainComponent;
		layeredPane.add(newDrawable.mainPanel,new Integer(-1)); //hide the component to show behind the one to fade		
		currentMainComponent=newDrawable; //the current main component is newDrawable as it is the latest to be drawn 
		
		//Special case when there are no previous elements to fade => directly call warnFadeOutEnded()
		if(componentToFadeOut==null)
			warnFadeOutEnded(null);
		System.out.println("\t==== => " + componentToFadeOut+" "+currentMainComponent );
	}
}

