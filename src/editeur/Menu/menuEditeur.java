package editeur.Menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import Affichage.MenuScroller;
import editeur.AffichageEditeur;
import menu.menuPrincipal.AbstractModelPrincipal;
import menu.menuPrincipal.GameHandler.GameModeType;
import partie.bloc.Bloc.TypeBloc;


public class menuEditeur {
	//{{variables 
	AffichageEditeur affichageEditeur;
	
	protected String nomFichier ="";
	
	public JMenuBar menuBar = new JMenuBar();
	JMenu m_fichier = new JMenu(" Fichier   ");
	JMenu m_objet = new JMenu(" Objet   ");
	JMenu m_texture = new JMenu(" Texture ");
	JMenu m_bloc = new JMenu("Objet Bloquant");
	JMenu m_back = new JMenu("Objet en Background");
	
	JMenuItem m_charger = new JMenuItem("Charger");
	JMenuItem m_sauvegarder= new JMenuItem("Sauvegarder");
	
	JMenuItem m_informations = new JMenuItem(" Informations ");
	
	JMenuItem m_nouv = new JMenuItem("Nouveau monde");
	JMenuItem m_menuP = new JMenuItem("Menu principal");
	JMenuItem m_quit = new JMenuItem("Quitter");
	
	JMenuItem m_loupe= new JMenuItem("Loupe");
	JMenuItem m_souris= new JMenuItem("Souris");
	JMenuItem m_deleteItem= new JMenuItem("Delete");
	JMenuItem m_vide = new JMenuItem("Vide");
	JMenuItem m_sol = new JMenuItem("Sol");
	JMenuItem m_terre = new JMenuItem("Terre");
	JMenuItem m_ciel = new JMenuItem("Ciel");
	JMenuItem m_perso= new JMenuItem("Perso");
	JMenuItem m_start= new JMenuItem("Start");
	JMenuItem m_end= new JMenuItem("End");
	JMenuItem m_spirel= new JMenuItem("Spirel");
	
	JRadioButtonMenuItem r_bloquant =new JRadioButtonMenuItem("Objet bloquant");
	JRadioButtonMenuItem r_nonBloquant =new JRadioButtonMenuItem("Objet non bloquant");
	ButtonGroup gp1 =new ButtonGroup();
	
	
	JRadioButtonMenuItem r_isBackground =new JRadioButtonMenuItem("Objet a afficher en arriere plan");
	JRadioButtonMenuItem r_nonIsBackground =new JRadioButtonMenuItem("Objet a ne pas afficher en arriere plan");
	ButtonGroup gp2 =new ButtonGroup();
	//}}
	
	public menuEditeur(AffichageEditeur _affichageEditeur)
	{
		affichageEditeur=_affichageEditeur;
		initMenu();
	}
	
	@SuppressWarnings("serial")
	public class PopUpSauv_Charg extends JDialog {

		public PopUpSauv_Charg(JFrame parent, String title, boolean modal){
			    //On appelle le construteur de JDialog correspondant
			    super(parent, title, modal);
			    //On specifie une taille
			    this.setSize(400, 120);
			    //La position
			    this.setLocationRelativeTo(null);
			    //La boite ne devra pas etre redimensionnable
			    this.setResizable(false);
			    JPanel affichage = new JPanel();
			    final JTextField texte= new JTextField("alpha");
			    JButton ok = new JButton("OK");	
			    JLabel info1 = new JLabel("Seul les caracteres a-z A-Z 0-9 et '_' sont autorises ");
			    JLabel info2 = new JLabel("Entrez un nom de fichier ");
			    ok.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						String texteTape = texte.getText();
						if( texteTape.matches("^[a-zA-Z0-9_]*$")){
							nomFichier=texteTape;
							setVisible(false);
						}}});
			    affichage.add(info1,BorderLayout.NORTH);
			    affichage.add(info2,BorderLayout.CENTER);
				affichage.add(texte,BorderLayout.CENTER);
				affichage.add(ok,BorderLayout.SOUTH);
				this.getContentPane().add(affichage);
			    //Tout ceci ressemble à ce que nous faisons depuis le debut avec notre JFrame.
			    
			 
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
		System.out.println("set bloquant "+ _bloquant );
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
			affichageEditeur.getContentPane().repaint();
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
				PopUpSauv_Charg popup = new PopUpSauv_Charg(affichageEditeur.getMainFrame(),"Sauvegarder un fichier",true);
				popup.setVisible(true);
				affichageEditeur.controlerEditeur.controlSauvegarde(nomFichier);
				nomFichier="";
			}
			
		}
	

	public class ChargerListener  implements ActionListener  {

			public void actionPerformed(ActionEvent e) {
				PopUpSauv_Charg popup = new PopUpSauv_Charg(affichageEditeur.getMainFrame(),"Charger un fichier",true);
				popup.setVisible(true);
				affichageEditeur.controlerEditeur.controlChargement(nomFichier);
				nomFichier="";
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
				JMenuItem jitem=null;
				if(item instanceof JMenuItem)
					jitem = (JMenuItem)item;
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
			JMenuItem jitem=null;
			if(item instanceof JMenuItem)
				jitem = (JMenuItem)item;
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
