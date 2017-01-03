package editeur.BarreOutil;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import editeur.AbstractModelEditeur;
import editeur.AffichageEditeur;


public class BarreOutil{
	AffichageEditeur affich;
	//Creation de la toolbar 
	public JToolBar tbar = new JToolBar();
	 public JScrollPane scroll = new JScrollPane(tbar);

	//JScrollPane jsp = new JScrollPane(tbar);
	 private JButton souris_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/souris_p.png")));
	 private JButton loupe_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/loupe_p.png")));
	 private JButton vide_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/vide_p.png")));
	 private JButton sol_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/sol_p.png")));
	 private JButton terre_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/terre_p.png")));
	 private JButton ciel_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/ciel_p.png")));
	 private JButton perso_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/perso_p.png")));
	 private JButton start_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/start_p.png")));
	 private JButton end_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/end_p.png")));
	 private JButton spirel_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/spirel_p.png")));
	 private JButton delete_p = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/delete_p.png")));


	 public BarreOutil(AffichageEditeur affichage) {
		 	affich=affichage;
			scroll.setPreferredSize(new Dimension(400,40));
			
			souris_p.addActionListener(new TextureListener());
			tbar.add(souris_p);
			delete_p.addActionListener(new TextureListener());
			tbar.add(delete_p);
			
			loupe_p.addActionListener(new TextureListener());
			tbar.add(loupe_p);
			
			vide_p.addActionListener(new TextureListener());
			tbar.add(vide_p);
			sol_p.addActionListener(new TextureListener());
			tbar.add(sol_p);
			terre_p.addActionListener(new TextureListener());
			tbar.add(terre_p);
			ciel_p.addActionListener(new TextureListener());
			tbar.add(ciel_p);
			perso_p.addActionListener(new TextureListener());
			tbar.add(perso_p);
			start_p.addActionListener(new TextureListener());
			tbar.add(start_p);
			end_p.addActionListener(new TextureListener());
			tbar.add(end_p);
			spirel_p.addActionListener(new TextureListener());
			tbar.add(spirel_p);
			
			}
	 /**
	  * Converti les clics sur les images des textures en clics sur l'élément de menu correspondant
	  * 
	  */	
	public class TextureListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			AbstractModelEditeur edit= affich.controlerEditeur.edit;
			if(e.getSource()==loupe_p){
				edit.dezoom();
			}
			else if (e.getSource()==souris_p){
				edit.setTexture("");
			}
			else if(e.getSource()==sol_p){
				edit.setTexture("sol");
			}
			else if(e.getSource()==vide_p){
				edit.setTexture("vide");
			}
			else if(e.getSource()==terre_p){
				edit.setTexture("terre");
			}
			else if(e.getSource()==ciel_p){
				edit.setTexture("ciel");
			}
			else if(e.getSource()==perso_p){
				edit.setTexture("perso");
			}
			else if(e.getSource()==start_p){
				edit.setTexture("start");
			}
			else if(e.getSource()==end_p){
				edit.setTexture("end");
			}
			else if(e.getSource()==spirel_p){
				edit.setTexture("spirel");
			}
			else if(e.getSource()==delete_p){
				edit.setTexture("Delete");
			}
		}
		
	}
}
