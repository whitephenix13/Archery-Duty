package choixNiveau;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import menuPrincipal.AbstractModelPrincipal;
import observer.Observer;
import principal.InterfaceConstantes;

@SuppressWarnings("serial")
public class AffichageChoixNiveau extends JFrame implements Observer{

	protected AbstractControlerChoixNiveau controlerChoix;
	
	protected JButton boutonJouer = new JButton();
	protected JButton boutonRetour = new JButton();

	
	protected JPanel panelBoutons= new JPanel();
	protected JScrollPane panelBoutonScroll;

	protected JPanel panelInterraction = new JPanel();
	
	public AffichageChoixNiveau(AbstractControlerChoixNiveau _controlerChoix)
	{
		controlerChoix=_controlerChoix;

	}
	public void init()
	{
		initAffichage();
		
		controlerChoix.choix.getAllNiveaux();//fill listNomNiveaux
		
		this.getContentPane().setLayout(new GridLayout(2,1));
		this.getContentPane().add(panelBoutonScroll);
		this.getContentPane().add(panelInterraction);
	}
	
	public void initAffichage()
	{
		panelBoutonScroll = new JScrollPane(panelBoutons);
		panelBoutonScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		panelBoutonScroll.setBounds(50, 30, 300, 50);
		
		panelInterraction.setLayout(null);
		//panelInterraction.setLayout(new GridLayout(1,2));
		controlerChoix.choix.resetBouton(boutonJouer,"Jouer");
		boutonJouer.setBounds(50,InterfaceConstantes.HAUTEUR_FENETRE/4,300,50);
		boutonJouer.setVisible(true);
		boutonJouer.setEnabled(true);
		
		
		controlerChoix.choix.resetBouton(boutonRetour,"Retour");
		boutonRetour.setBounds(InterfaceConstantes.LARGEUR_FENETRE -350 ,InterfaceConstantes.HAUTEUR_FENETRE/4,300,50);
		boutonRetour.setVisible(true);
		boutonRetour.setEnabled(true);
		
		panelInterraction.add(boutonJouer);
		panelInterraction.add(boutonRetour);
		panelInterraction.setBackground(Color.BLACK);
	}
	
	public void fillPanelBoutons()
	{
		List<String> listNomNiveaux = controlerChoix.choix.listNomNiveaux;
		List<JButton> listNiveaux = new ArrayList<JButton>();
		//on créer les boutons
				panelBoutons.setLayout(new GridLayout(listNomNiveaux.size(),1));
				//panelBoutons.setLayout(new FlowLayout());
				for(int i=0; i <listNomNiveaux.size(); i++ )
				{
					listNiveaux.add(new JButton());
					
					controlerChoix.choix.resetBouton(listNiveaux.get(i),listNomNiveaux.get(i));
					listNiveaux.get(i).setEnabled(true);
					listNiveaux.get(i).setVisible(true);
					
					panelBoutons.add(listNiveaux.get(i));
				}
				panelBoutonScroll = new JScrollPane(panelBoutons);
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
			JButton button = (JButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controlerChoix.choix.selectLevel((JButton)e.getSource());
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
			JButton button = (JButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				AbstractModelPrincipal.changeFrame=true;
				AbstractModelPrincipal.modeSuivant="Principal";
				AbstractModelPrincipal.changeMode=true;
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
			JButton button = (JButton)e.getSource();
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
		List<JButton> listNiveaux = controlerChoix.choix.listNiveaux;
		for(int i=0; i <controlerChoix.choix.listNomNiveaux.size(); i++ )
		{
			JButton button = listNiveaux.get(i);
			MouseListener[] ml = button.getMouseListeners();
			button.removeMouseListener(ml[ml.length-1]);
		}
		MouseListener[] ml2 = boutonJouer.getMouseListeners();
		MouseListener[] ml3 = boutonRetour.getMouseListeners();

		boutonJouer.removeMouseListener(ml2[ml2.length-1]);
		boutonRetour.removeMouseListener(ml3[ml3.length-1]);

	}
	
	
	public void update() {

		if(controlerChoix.choix.getUpdateListLevels())
		{
			fillPanelBoutons();
		}	
		controlerChoix.choix.resetVariablesAffichages();
		this.repaint();
	}

}
