package choixNiveau;

import java.awt.Color;
import java.awt.GridLayout;
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
				System.out.println(listNiveaux.size());
				controlerChoix.choix.listNiveaux=listNiveaux;
	}
	
	public class niveauListener implements MouseListener
	{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {	
			controlerChoix.choix.selectLevel((JButton)e.getSource());
		}
		public void mouseReleased(MouseEvent e) {}
	}
	
	  /**
	   * Permet le retour vers le menu principal
	   */
	public class retourListener implements MouseListener 
	{
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {
			AbstractModelPrincipal.changeFrame=true;
			AbstractModelPrincipal.modeSuivant="Principal";
			AbstractModelPrincipal.changeMode=true;
		}
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	  /**
	   * Lance le chargement du niveau
	   */
	public class jouerListener implements MouseListener 
	{
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {
			
				controlerChoix.controlPlayLevel();
			
		}

		public void mouseReleased(MouseEvent arg0) {}
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
			listNiveaux.get(i).removeMouseListener(listNiveaux.get(i).getMouseListeners()[1]);
		}
		boutonJouer.addMouseListener(boutonJouer.getMouseListeners()[1]);
		boutonRetour.addMouseListener(boutonRetour.getMouseListeners()[1]);

	}
	
	
	public void update() {

		if(controlerChoix.choix.getUpdateListLevels())
		{
			fillPanelBoutons();
			System.out.println("levels upd");
		}	
		controlerChoix.choix.resetVariablesAffichages();

	}

}
