//JFrame peut contenir plusieurs JPanel
package Affichage;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import ActiveJComponent.ActiveJFrame;
import ActiveJComponent.ActiveJLayeredPane;
import ActiveJComponent.ActiveJMenuBar;
import debug.DebugStack;
import editeur.AffichageEditeur;
import gameConfig.InterfaceConstantes;
import menu.choixNiveau.AffichageChoixNiveau;
import menu.credit.AffichageCredit;
import menu.menuPrincipal.AffichagePrincipal;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameHandler.GameModeType;
import menu.menuPrincipal.ModelPrincipal;
import option.AffichageOption;
import partie.modelPartie.AffichagePartie;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class GameRenderer extends ActiveJFrame implements InterfaceConstantes, Observer
{	
	private static boolean renderCalled = false;
	public static boolean isRenderCalled(){return renderCalled;}
	
	public boolean changeVariable= false;
	public ActiveJLayeredPane mainLayeredPane;//JFrame/LayeredPane/(1)FadingJPanel/(2)NextJPanel
	//private JLayeredPane layeredPane; //layer 0: current current screen OR Layer 0: old screen fading out; Layer -1: new screen to come 
	private Drawable componentToFadeOut;
	private Drawable currentMainComponent; //the component that should be shown on screen. When transitioning, this corresponds to the component that does not fade out
	
	private GameHandler gameHandler;
	private boolean inTransition;
	public boolean isInTransition(){return inTransition;}
	
	//Variables for active rendering
	private BufferStrategy buffer;
	private BufferedImage bi;
	private Graphics graphics;
	private Graphics2D g2d;

	private Font fpsFont=null;
	
	AffichageOption affichageOption;
	AffichagePrincipal affichagePrincipal;
	AffichageChoixNiveau affichageChoix;
	AffichagePartie affichagePartie;
	AffichageEditeur affichageEditeur;
	AffichageCredit affichageCredit;
	
	//private boolean fadeOutCanStart = false;
	/**
	 * Initialise Affichage
	 */  
	public GameRenderer(GameHandler gameHandler)
	{				
		this.gameHandler =gameHandler;
		setTitle("Menu principal");

		
		List<Image> icons = new ArrayList<Image>();
		icons.add(getImage("16x16.gif"));
		icons.add(getImage("32x32.gif"));
		icons.add(getImage("64x64.gif"));
		this.setIconImages(icons);

				
		mainLayeredPane= new ActiveJLayeredPane();
		this.setContentPane(mainLayeredPane);

		// Create BackBuffer...
		createBufferStrategy( 2 );
		buffer = getBufferStrategy();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		// Create off-screen drawing surface
		bi =  gc.createCompatibleImage( InterfaceConstantes.screenSize.width, InterfaceConstantes.screenSize.height );

		// Objects needed for rendering...
		graphics = null;
		
		this.pack();
	}
	
	public void setAffichagePrincipal(AffichagePrincipal _affichagePrincipal)
	{
		affichagePrincipal=_affichagePrincipal;
		affichagePrincipal.addListenerPrincipal();
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
		affichageOption.initFromGameRenderer(this);
		affichageEditeur=_affichageEditeur;
		affichageEditeur.initFromGameRenderer(this);
		affichageCredit=_affichageCredit;
		affichageCredit.initFromGameRenderer(this);
		affichageChoix = _affichageChoix;
		affichageChoix.initFromGameRenderer(this);
		affichagePartie=_affichagePartie;
		affichagePartie.initFromGameRenderer(this);
		affichagePrincipal.initFromGameRenderer(this);
	}
	
	public static void executeInEDT(Runnable r)
	{
		_executeInEdt(r,false);
	}
	public static void executeInEDTAndWait(Runnable r)
	{
		_executeInEdt(r,true);
	}
	/***
	 * Do not call this function. Instead use executeInEDT or executeInEDTAndWait
	 * @param r
	 * @param wait
	 */
	private static void _executeInEdt(Runnable r, boolean wait)
	{
		if (EventQueue.isDispatchThread()) {
	        r.run();
	    } else {
	    	if(wait){
	    		try {SwingUtilities.invokeAndWait(r);}  
	    		catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// should not happen
					e.printStackTrace();
				}
	    	}
	    	else
	    		SwingUtilities.invokeLater(r);
	    }
	}
	public void render(final boolean forceRepaint)
	{
		try{
			//Invoke that in the EDT thread as it is safer 
			executeInEDTAndWait(new Runnable(){
				@Override
				public void run() {
					draw(forceRepaint);
					if( !buffer.contentsLost() ){
						buffer.show();
					}
					else{
						System.out.println("Buffer content lost ");
					}
					Toolkit.getDefaultToolkit().sync();
				}});
		}
		catch(Exception e){e.printStackTrace();}
		finally {
			// release resources
			if( graphics != null ) 
				graphics.dispose();
			if( g2d != null ) 
				g2d.dispose();
		}
	}
	private void draw(boolean forceRepaint)
	{
		ModelPrincipal.debugTimeAffichage.print();
		ModelPrincipal.debugTimeAffichage.init(InterfaceConstantes.DEBUG_TIME_AFFICHAGE_PRINT_MODE,-1);
		ModelPrincipal.debugTimeAffichage.startElapsedForVerbose();
					
		// clear back buffer...
		g2d = bi.createGraphics();
		ModelPrincipal.debugTimeAffichage.elapsed("create graphics");
		if(currentMainComponent== null || !currentMainComponent.isSelfClearingBackBuffer){
			g2d.setColor( Color.black );
			g2d.fillRect( 0, 0, InterfaceConstantes.screenSize.width, InterfaceConstantes.screenSize.height );
		}
		ModelPrincipal.debugTimeAffichage.elapsed("cleared buffer");
		
		
		//Draw main component
	
		if(currentMainComponent!= null){
			currentMainComponent.setTransparency(g2d);
			currentMainComponent.drawOnGraphics(g2d,forceRepaint);
		}
		ModelPrincipal.debugTimeAffichage.elapsed("draw main component");	
		
		//Draw fading component 
			if(componentToFadeOut!=null){
				componentToFadeOut.setTransparency(g2d);
				componentToFadeOut.drawOnGraphics(g2d,forceRepaint);
			}
			ModelPrincipal.debugTimeAffichage.elapsed("draw fade out component");
			
			if(SHOW_FPS)
			{
				if(fpsFont==null)
					fpsFont = new Font(g2d.getFont().getFontName(),Font.BOLD, 15 );
				g2d.setColor(Color.red);
				g2d.setFont(fpsFont);
				g2d.drawString("FPS: "+gameHandler.getFps(), 10, 20);
				ModelPrincipal.debugTimeAffichage.elapsed("show fps");
			}
			
			//Get graphics and translate them so that we start drawing inside the decorated window 
			graphics = buffer.getDrawGraphics();
			Insets insets = getInsets();
			
			ModelPrincipal.debugTimeAffichage.elapsed("get graphics and insets");
			
			//If there is a jmenu bar, translate the graphics a bit more 
			int bar_height = getJMenuBar()!= null ? getJMenuBar().getBounds().height : 0;
			graphics.translate(insets.left, insets.top+bar_height);
			
			ModelPrincipal.debugTimeAffichage.elapsed("translate graphics");
			
			//Draw the image from the graphics (paintComponents/non swing components)
			graphics.drawImage( bi, 0,0, null );
			ModelPrincipal.debugTimeAffichage.elapsed("draw image on graphics");
			//Temporarely disable the painting lock of components by setting renderCalled to true
			//Call paintComponents on the two main panels to actually call the painting methods
			renderCalled =true;
			
			float fadeOutTransparency = 0;
			if(componentToFadeOut!=null){
				fadeOutTransparency = componentToFadeOut.getTransparency();
			}
			
			if(currentMainComponent!= null){
				currentMainComponent.setTransparency((Graphics2D)graphics,1-fadeOutTransparency);
				currentMainComponent.mainPanel.paintComponents(graphics);
			}
			ModelPrincipal.debugTimeAffichage.elapsed("draw main swing");
			
			if(componentToFadeOut!=null){
				componentToFadeOut.setTransparency((Graphics2D)graphics,fadeOutTransparency);
				componentToFadeOut.mainPanel.paintComponents(graphics);
			}
			ModelPrincipal.debugTimeAffichage.elapsed("draw fade out swing");
			
			
		//Finally draw the menu bar if it exists. Draw it at the end so that pop up is above everything else
		if(getJMenuBar() != null){
			graphics.translate(0, -bar_height);//get back to top of the screen, just below the insets 
			((ActiveJMenuBar)getJMenuBar()).paintBar(graphics);
			ModelPrincipal.debugTimeAffichage.elapsed("draw j menu bar");
		}
		
		renderCalled=false;
		
		
		//WARNING: do not forget to call warnFadeOutEnded/startFadeOut if the componentToFadeOut is not longer fading
		//This is called after the render phase to avoid any flash
		if(componentToFadeOut!=null)
			if(!componentToFadeOut.isFading){
				if(inTransition){
					warnFadeOutEnded();
					ModelPrincipal.debugTimeAffichage.elapsed("warn fade out end");
				}
			}
		
	}
	
	/**
	 * Actualise le contenu de la frame (this)
	 *
	 */
	public void changeGameModeRendering()
	{
		changeGameModeRendering(null);
	}
	/***
	 * 
	 * @param affichageLoader used to execute the transition from a loading screen to the gamemode
	 */
	public void changeGameModeRendering(Drawable affichageLoader)
	{
		if(affichageOption != null)
			affichageOption.retour.setContentAreaFilled(false);

		if(this.getJMenuBar()!= null){this.setJMenuBar(null);}
		if(gameHandler.currentGameMode().equals(GameModeType.MAIN_MENU))
		{	
			if(affichagePrincipal.getLoader() ==null){
				return;
			}
			beginTransition(affichagePrincipal);
			this.setTitle("Menu principal"); 
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.OPTION))
		{
			beginTransition(affichageOption);
			this.setTitle("Options"); 	   
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.EDITOR))
		{	
			beginTransition(affichageEditeur);
			this.setJMenuBar(affichageEditeur.getJMenuBar());
			this.setTitle("Editeur"); 
			this.revalidate();
			this.repaint();
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.CREDIT))
		{	
			beginTransition(affichageCredit);
			this.setTitle("Credit"); 
			this.revalidate();
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.LEVEL_SELECTION))
		{
			beginTransition(affichageChoix);
			this.setTitle("Choix niveau");
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.GAME))
		{
			beginTransition(affichagePartie);
			this.setTitle("Partie rapide"); 
			affichagePartie.requestGameFocus();
							
		}
		else if (gameHandler.currentGameMode().equals(GameModeType.LOADER))
		{
			//affichageLoader should not be null
			beginTransition(affichageLoader);//get affichage loader to show 
			this.setTitle("Loading"); 
							
		}	
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
		//this.repaint();
	}
	
	public boolean isScreenFading()
	{
		if(componentToFadeOut==null)
			return false;
		return componentToFadeOut.isFading;
	}
	/***
	 * Call this method whenever the screen from the next mode that we want to show is ready (ie: loaders ended, ...). The transitions starts directly.
	 */
	/*public void warnFadeOutCanStart()
	{
		fadeOutCanStart = true;
	}*/
	/***
	 * Called from drawable whenever the transparency from the screen hide is 0. This functions reorganize the layeredPane by removing the hidden component 
	 * @param whoEnded
	 */
	private void warnFadeOutEnded()
	{
			//See the comment next to layeredPane variable definition
			if(componentToFadeOut!=null){
				componentToFadeOut.resetFadingState();//Reset the fading for future use 
				mainLayeredPane.remove(componentToFadeOut.mainPanel);//remove the component at Position 0 (front)
			}
			componentToFadeOut=null;
			//move the component from Layer -1 to Layer 0
			inTransition=false;
	}
	

	public void beginTransition(final Drawable newDrawable)
	{
			//Handle case where we transition to itself => directly return
			if(currentMainComponent!= null && currentMainComponent.equals(newDrawable))
			{
				warnFadeOutEnded();//also moves currentMainComponent from layer -1 to 0
				return;
			}

			//Handle the case where we are in the middle of a transition
			if(componentToFadeOut!=null)
			{
				//force the transition to end
				warnFadeOutEnded();//also moves currentMainComponent from layer -1 to 0
			}
			//At this point, layeredPane should only have one component
			componentToFadeOut = currentMainComponent;
			mainLayeredPane.add(newDrawable.mainPanel); 
			currentMainComponent=newDrawable; //the current main component is newDrawable as it is the latest to be drawn 

			//Special case when there are no previous elements to fade => directly call warnFadeOutEnded()
			if(componentToFadeOut==null){
				warnFadeOutEnded();
				return;
			}
			
			//Start fading out 
			componentToFadeOut.startFadeOut(InterfaceConstantes.SCREEN_FADE_OUT_TIME);
			inTransition=true;
	}
	
	protected Image getImage(String name)
	{
		return Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/icons/"+name));
	}
}

