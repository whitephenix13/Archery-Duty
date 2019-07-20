package editeur.Menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ActiveJComponent.ActiveJButton;
import ActiveJComponent.PassiveJDialog;
import ActiveJComponent.ActiveJFrame;
import ActiveJComponent.ActiveJLabel;
import ActiveJComponent.ActiveJMenu;
import ActiveJComponent.ActiveJMenuBar;
import ActiveJComponent.ActiveJMenuItem;
import ActiveJComponent.ActiveJPanel;
import ActiveJComponent.ActiveJRadioButtonMenuItem;
import ActiveJComponent.ActiveJTextField;
import Affichage.MenuScroller;
import editeur.AffichageEditeur;
import menu.menuPrincipal.GameHandler.GameModeType;
import partie.bloc.Bloc.TypeBloc;


public class menuEditeur {
	//{{variables 
	AffichageEditeur affichageEditeur;
	
	protected String nomFichier ="";
	
	public ActiveJMenuBar menuBar = new ActiveJMenuBar();
	ActiveJMenu m_fichier = new ActiveJMenu(" Fichier   ");
	ActiveJMenu m_objet = new ActiveJMenu(" Objet   ");
	ActiveJMenu m_texture = new ActiveJMenu(" Texture ");
	ActiveJMenu m_bloc = new ActiveJMenu("Objet Bloquant");
	ActiveJMenu m_back = new ActiveJMenu("Objet en Background");
	
	ActiveJMenuItem m_charger = new ActiveJMenuItem("Charger");
	ActiveJMenuItem m_sauvegarder= new ActiveJMenuItem("Sauvegarder");
	
	ActiveJMenuItem m_informations = new ActiveJMenuItem(" Informations ");
	
	ActiveJMenuItem m_nouv = new ActiveJMenuItem("Nouveau monde");
	ActiveJMenuItem m_menuP = new ActiveJMenuItem("Menu principal");
	ActiveJMenuItem m_quit = new ActiveJMenuItem("Quitter");
	
	ActiveJMenuItem m_loupe= new ActiveJMenuItem("Loupe");
	ActiveJMenuItem m_souris= new ActiveJMenuItem("Souris");
	ActiveJMenuItem m_deleteItem= new ActiveJMenuItem("Delete");
	ActiveJMenuItem m_vide = new ActiveJMenuItem("Vide");
	ActiveJMenuItem m_sol = new ActiveJMenuItem("Sol");
	ActiveJMenuItem m_terre = new ActiveJMenuItem("Terre");
	ActiveJMenuItem m_ciel = new ActiveJMenuItem("Ciel");
	ActiveJMenuItem m_perso= new ActiveJMenuItem("Perso");
	ActiveJMenuItem m_start= new ActiveJMenuItem("Start");
	ActiveJMenuItem m_end= new ActiveJMenuItem("End");
	ActiveJMenuItem m_spirel= new ActiveJMenuItem("Spirel");
	
	ActiveJRadioButtonMenuItem r_bloquant =new ActiveJRadioButtonMenuItem("Objet bloquant");
	ActiveJRadioButtonMenuItem r_nonBloquant =new ActiveJRadioButtonMenuItem("Objet non bloquant");
	ButtonGroup gp1 =new ButtonGroup();
	
	
	ActiveJRadioButtonMenuItem r_isBackground =new ActiveJRadioButtonMenuItem("Objet a afficher en arriere plan");
	ActiveJRadioButtonMenuItem r_nonIsBackground =new ActiveJRadioButtonMenuItem("Objet a ne pas afficher en arriere plan");
	ButtonGroup gp2 =new ButtonGroup();
	//}}
	
	public menuEditeur(AffichageEditeur _affichageEditeur)
	{
		affichageEditeur=_affichageEditeur;
		initMenu();
	}
	
	@SuppressWarnings("serial")
	public class PopUpSauv_Charg extends PassiveJDialog {

		public PopUpSauv_Charg(ActiveJFrame parent, String title, boolean modal,boolean savePopup){
			    //On appelle le construteur de JDialog correspondant
			    super(parent, title, modal);		
			    super.setComponentAndShowDialog(createContent(savePopup));

			  }
			private Component createContent(final boolean savePopup)
			{
				final JPanel mainPan = new JPanel();
				mainPan.setLayout(new BoxLayout(mainPan,BoxLayout.Y_AXIS));

			    JLabel info1 = new JLabel("Seul les caracteres a-z A-Z 0-9 et '_' sont autorises");
				
			    final JPanel filename = new JPanel();
			    JLabel info2 = new JLabel("Entrez un nom de fichier: ");
			    final JTextField text= new JTextField(15);

				JPanel centeredButton = new JPanel();
				centeredButton.setLayout(new BoxLayout(centeredButton,BoxLayout.X_AXIS));
			    JButton ok = new JButton("OK");	
			    ok.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent arg0) {
							String texteTape = text.getText();
							if( texteTape.matches("^[a-zA-Z0-9_]*$")){
								nomFichier=texteTape;
								PopUpSauv_Charg.this.getContentPane().remove(mainPan);
								if(savePopup)
									affichageEditeur.controlerEditeur.controlSauvegarde(nomFichier);
								else
									affichageEditeur.controlerEditeur.controlChargement(nomFichier);
								nomFichier="";
								PopUpSauv_Charg.this.dispose();
								//PopUpSauv_Charg.this.setVisible(false);
							}
							
						}});
			    
			    filename.add(info2);
			    filename.add(text);
			    
			    centeredButton.add(Box.createHorizontalGlue());
			    centeredButton.add(ok);
			    centeredButton.add(Box.createHorizontalGlue());
			    
			    mainPan.add(info1);
			    mainPan.add(filename);
			    mainPan.add(centeredButton);
				mainPan.setVisible(true);
				
				return mainPan;
			}
			
		}
	
	public void initMenu() {

		gp1.add(r_bloquant);
		gp1.add(r_nonBloquant);
		r_nonBloquant.setSelected(true);
		
		m_bloc.add(r_bloquant);
		m_bloc.add(r_nonBloquant);
		
		gp2.add(r_isBackground);
		gp2.add(r_nonIsBackground);
		r_nonIsBackground.setSelected(true);
		
		m_back.add(r_isBackground);
		m_back.add(r_nonIsBackground);
		
		m_texture.add(m_loupe);
		m_texture.add(m_souris);
		m_texture.add(m_deleteItem);
		m_texture.add(m_vide);
		m_texture.add(m_sol);
		m_texture.add(m_terre);
		m_texture.add(m_ciel);
		m_texture.add(m_perso);
		m_texture.add(m_start);
		m_texture.add(m_end);
		m_texture.add(m_spirel);
		
		
		MenuScroller.setScrollerFor(m_texture, 3, 125, 8, 3);
		
		m_objet.add(m_texture);
		m_objet.add(m_bloc);
		m_objet.add(m_back);

		
		m_fichier.add(m_nouv);
		m_fichier.add(m_menuP);
		m_fichier.add(m_quit);
		
		menuBar.add(m_fichier);
		menuBar.add(m_objet);
		menuBar.add(m_charger);
		menuBar.add(m_sauvegarder);
		menuBar.add(m_informations);
	}
	
	public void controlTexture(Object source) {
	if (source == m_souris ){
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.NONE);
	}
	else if (source == m_deleteItem ){
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.DELETE);
	}
	else if (source == m_vide ){
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.NONE);

	}
	else if(source == m_sol) {
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.SOL);
	}
	else if(source == m_terre) {
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.TERRE);
	}
	else if(source == m_ciel) {
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.CIEL);
	}
	else if(source == m_perso) {
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.PERSO);
	}
	else if(source == m_start) {
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.START);
	}
	else if(source == m_end) {
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.END);
	}
	else if(source == m_spirel) {
		affichageEditeur.controlerEditeur.edit.setTexture(TypeBloc.SPIREL);
	}
	
}
	
	public void setBloquant(boolean _bloquant){	
		affichageEditeur.controlerEditeur.edit.setBloquant(_bloquant);
		r_bloquant.setSelected(_bloquant);
		r_nonBloquant.setSelected(!_bloquant);

		}	
	public void setBackground(boolean _background){
		affichageEditeur.controlerEditeur.edit.setBackground(_background);
		r_isBackground.setSelected(_background);
		r_nonIsBackground.setSelected(!_background);
		}
	
	
	public class TextureListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
				controlTexture(e.getSource());	
			}
		}
	public class NouvListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//on reset les variables 
			affichageEditeur.controlerEditeur.edit.init();
			//REMOVE affichageEditeur.getContentPane().repaint();
			}
		}
	public class retourMenuListener implements ActionListener{

			public void actionPerformed(ActionEvent e) {
				
				//REMOVE AbstractModelPrincipal.modeSuivant="Principal";
				//REMOVE AbstractModelPrincipal.changeMode=true;				

				//REMOVE AbstractModelPrincipal.changeFrame=true; //REMOVE? 
				menuEditeur.this.affichageEditeur.controlerEditeur.edit.gameHandler.setGameMode(GameModeType.MAIN_MENU);
			}
			
		}
		
	public class QuitterListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {		
				System.exit(0);
				}
		}

	public class SauvegarderListener implements ActionListener{

			public void actionPerformed(ActionEvent e) {
				PopUpSauv_Charg popup = new PopUpSauv_Charg(affichageEditeur.getActiveJFrame(),"Sauvegarder un fichier",true,true);
				popup.setVisible(true);
			}
			
		}
	

	public class ChargerListener  implements ActionListener  {

			public void actionPerformed(ActionEvent e) {
				PopUpSauv_Charg popup = new PopUpSauv_Charg(affichageEditeur.getActiveJFrame(),"Charger un fichier",true,false);
				popup.setVisible(true);
		}
	}
	
	public class InformationsListener implements ActionListener{

			public void actionPerformed(ActionEvent arg0) {
				affichageEditeur.controlerEditeur.edit.information();
			}
			
		}
		
	public class LoupeListener implements ActionListener{

			public void actionPerformed(ActionEvent e) { 
				affichageEditeur.controlerEditeur.edit.dezoom();
			}
			}
		
	public class BloquantListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
		affichageEditeur.controlerEditeur.edit.setBloquant(r_bloquant.isSelected());
			}
		}
	public class BackgroundListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			setBackground(r_isBackground.isSelected());
		}
		
	}
	
	public void addListenerMenuEditeur()
	{
		
			m_nouv.addActionListener(new NouvListener());
			m_menuP.addActionListener(new retourMenuListener());
			m_quit.addActionListener(new QuitterListener());
			m_informations.addActionListener(new InformationsListener());
			
			for(Component item : m_texture.getMenuComponents())
			{
				ActiveJMenuItem jitem=null;
				if(item instanceof ActiveJMenuItem)
					jitem = (ActiveJMenuItem)item;
				if(jitem!=null)
					if(jitem==m_loupe)
						jitem.addActionListener(new LoupeListener());
					else
						jitem.addActionListener(new TextureListener());

			}

			r_bloquant.addActionListener(new BloquantListener());
			r_nonBloquant.addActionListener(new BloquantListener());
			
			r_isBackground.addActionListener(new BackgroundListener());
			r_nonIsBackground.addActionListener(new BackgroundListener());
			
			m_sauvegarder.addActionListener(new SauvegarderListener());
			m_charger.addActionListener(new ChargerListener());
	}
	public void removeListenerMenuEditeur() 
	{
		
		m_nouv.removeActionListener(m_nouv.getActionListeners()[m_nouv.getActionListeners().length-1]);
		m_menuP.removeActionListener(m_menuP.getActionListeners()[m_menuP.getActionListeners().length-1]);
		m_quit.removeActionListener(m_quit.getActionListeners()[m_quit.getActionListeners().length-1]);
		m_informations.removeActionListener(m_informations.getActionListeners()[m_informations.getActionListeners().length-1]);
		
		for(Component item : m_texture.getMenuComponents())
		{
			ActiveJMenuItem jitem=null;
			if(item instanceof ActiveJMenuItem)
				jitem = (ActiveJMenuItem)item;
			if(jitem!=null){
				ActionListener[] al = jitem.getActionListeners();
				jitem.removeActionListener(al[al.length-1]);
			}
		}

		r_bloquant.removeActionListener(r_bloquant.getActionListeners()[r_bloquant.getActionListeners().length-1]);
		r_nonBloquant.removeActionListener(r_nonBloquant.getActionListeners()[r_nonBloquant.getActionListeners().length-1]);
		
		r_isBackground.removeActionListener(r_isBackground.getActionListeners()[r_isBackground.getActionListeners().length-1]);
		r_nonIsBackground.removeActionListener(r_nonIsBackground.getActionListeners()[r_nonIsBackground.getActionListeners().length-1]);
		
		m_sauvegarder.removeActionListener(m_sauvegarder.getActionListeners()[m_sauvegarder.getActionListeners().length-1]);
	
		m_charger.removeActionListener(m_charger.getActionListeners()[m_charger.getActionListeners().length-1]);
	}
}
