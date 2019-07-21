package menu.choixNiveau;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

import ActiveJComponent.ActiveJButton;
import ActiveJComponent.ActiveJPanel;
import ActiveJComponent.ActiveJScrollPane;
import Affichage.Drawable;
import gameConfig.InterfaceConstantes;
import images.ImagesBackground.ImBackgroundInfo;
import images.ImagesContainer.ImageGroup;
import menu.menuPrincipal.GameHandler.GameModeType;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichageChoixNiveau extends Drawable implements Observer{

	protected AbstractControlerChoixNiveau controlerChoix;
	
	protected ActiveJButton boutonJouer = new ActiveJButton();
	protected ActiveJButton boutonRetour = new ActiveJButton();

	
	protected ActiveJPanel panelBoutons= new ActiveJPanel();
	protected ActiveJScrollPane panelBoutonScroll;

	protected ActiveJPanel panelInterraction = new ActiveJPanel();
	
	public AffichageChoixNiveau(AbstractControlerChoixNiveau _controlerChoix)
	{
		super();
		controlerChoix=_controlerChoix;

	}
	public void init()
	{
		initAffichage();
		
		controlerChoix.choix.getAllNiveaux();//fill listNomNiveaux
		
		mainPanel.setLayout(new GridLayout(2,1));
		mainPanel.add(panelBoutonScroll);
		mainPanel.add(panelInterraction);
		mainPanel.setOpaque(false);
	}
	
	public void initAffichage()
	{
		panelBoutonScroll = new ActiveJScrollPane(panelBoutons);
		panelBoutonScroll.setVerticalScrollBarPolicy(ActiveJScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panelBoutonScroll.getVerticalScrollBar().setUnitIncrement(16);
		//panelBoutonScroll.setVerticalScrollBarPolicy(ActiveJScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		panelInterraction.setLayout(null);
		//panelInterraction.setLayout(new GridLayout(1,2));
		controlerChoix.choix.resetBouton(boutonJouer,"Jouer");
		boutonJouer.setBounds(50,InterfaceConstantes.WINDOW_HEIGHT/4,300,50);
		boutonJouer.setVisible(true);
		boutonJouer.setEnabled(true);
		
		
		controlerChoix.choix.resetBouton(boutonRetour,"Retour");
		boutonRetour.setBounds(InterfaceConstantes.WINDOW_WIDTH -350 ,InterfaceConstantes.WINDOW_HEIGHT/4,300,50);
		boutonRetour.setVisible(true);
		boutonRetour.setEnabled(true);
		
		panelInterraction.add(boutonJouer);
		panelInterraction.add(boutonRetour);
		panelInterraction.setOpaque(false);
		panelInterraction.setBackground(InterfaceConstantes.BACKGROUND_COLOR);
	}
	
	public void fillPanelBoutons()
	{
		List<String> listNomNiveaux = controlerChoix.choix.listNomNiveaux;
		List<ActiveJButton> listNiveaux = new ArrayList<ActiveJButton>();
		//on créer les boutons
		panelBoutons.setLayout(new GridLayout(listNomNiveaux.size(),1));
		panelBoutons.setBackground(InterfaceConstantes.BACKGROUND_COLOR);
		//panelBoutons.setLayout(new FlowLayout());
		ActiveJButton but;
		for(int i=0; i <listNomNiveaux.size(); i++ )
		{
			but=new ActiveJButton();
			listNiveaux.add(but);
			
			controlerChoix.choix.resetBouton(but,listNomNiveaux.get(i));
			but.setEnabled(true);
			but.setVisible(true);
			//but.setOpaque(false);
				
			but.setBackground(InterfaceConstantes.BACKGROUND_COLOR);
			
			panelBoutons.add(listNiveaux.get(i));
		}
		//panelBoutonScroll = new ActiveJScrollPane(panelBoutons);
		//panelBoutonScroll.setBackground(InterfaceConstantes.BACKGROUND_COLOR);
		controlerChoix.choix.listNiveaux=listNiveaux;
	}
	
	public class niveauListener implements MouseListener
	{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) 
		{
			ActiveJButton button = (ActiveJButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controlerChoix.choix.selectLevel((ActiveJButton)e.getSource());
			}
		}
	}
	
	  /**
	   * Permet le retour vers le menu principal
	   */
	public class retourListener implements MouseListener 
	{
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent e) 
		{
			ActiveJButton button = (ActiveJButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controlerChoix.choix.gameHandler.setGameMode(GameModeType.MAIN_MENU);
			}
		}
	}
	
	  /**
	   * Lance le chargement du niveau
	   */
	public class jouerListener implements MouseListener 
	{
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {}
		public void mouseReleased(MouseEvent e) 
		{
			ActiveJButton button = (ActiveJButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controlerChoix.controlPlayLevel();
			}
		}
	}
	
	public void addListener()
	{
		for(int i=0; i <controlerChoix.choix.listNomNiveaux.size(); i++ )
		{
			controlerChoix.choix.listNiveaux.get(i).addMouseListener(new niveauListener());
		}
		boutonJouer.addMouseListener(new jouerListener());
		boutonRetour.addMouseListener(new retourListener());

	}

	public void removeListener()
	{
		List<ActiveJButton> listNiveaux = controlerChoix.choix.listNiveaux;
		for(int i=0; i <controlerChoix.choix.listNomNiveaux.size(); i++ )
		{
			ActiveJButton button = listNiveaux.get(i);
			MouseListener[] ml = button.getMouseListeners();
			button.removeMouseListener(ml[ml.length-1]);
		}
		MouseListener[] ml2 = boutonJouer.getMouseListeners();
		MouseListener[] ml3 = boutonRetour.getMouseListeners();

		boutonJouer.removeMouseListener(ml2[ml2.length-1]);
		boutonRetour.removeMouseListener(ml3[ml3.length-1]);

	}
	@Override
	public void drawOnGraphics(Graphics g,boolean forceRepaint) 
	{
		g.drawImage(controlerChoix.choix.gameHandler.getImage(ImageGroup.BACKGROUND, null, ImBackgroundInfo.BLACK, null),0,0,null);
	}
	
	public void update() {

		if(controlerChoix.choix.getUpdateListLevels())
		{
			fillPanelBoutons();
		}	
		controlerChoix.choix.resetVariablesAffichages();
	}

}
