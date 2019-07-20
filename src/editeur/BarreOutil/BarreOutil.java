package editeur.BarreOutil;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import ActiveJComponent.ActiveJButton;
import ActiveJComponent.ActiveJScrollPane;
import ActiveJComponent.ActiveJToolBar;
import editeur.AbstractModelEditeur;
import editeur.AffichageEditeur;
import images.ImagesContainer;
import images.ImagesContainer.ImageGroup;
import partie.bloc.Bloc.BlocImModifier;
import partie.bloc.Bloc.TypeBloc;


public class BarreOutil{
	AffichageEditeur affich;
	//Creation de la toolbar 
	public ActiveJToolBar tbar = new ActiveJToolBar();
	public ActiveJScrollPane scroll = new ActiveJScrollPane(tbar);

	//JScrollPane jsp = new JScrollPane(tbar);
	private ActiveJButton souris_p;
	private ActiveJButton loupe_p;
	private ActiveJButton sol_p;
	private ActiveJButton terre_p;
	private ActiveJButton ciel_p;
	private ActiveJButton perso_p;
	private ActiveJButton start_p;
	private ActiveJButton end_p;
	private ActiveJButton spirel_p;
	private ActiveJButton delete_p;

	private boolean isInit;
	

	public BarreOutil(AffichageEditeur affichage) {
		affich=affichage;
		isInit=false;
	}
	
	public void InitWhenImageLoaded()
	{
		souris_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.SOURIS, BlocImModifier.LOUPE)));
		loupe_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.LOUPE, BlocImModifier.LOUPE)));
		//REMOVE vide_p = new ActiveJButton(new ImageIcon(getClass().getClassLoader().getResource("resources/Editeur/vide_p.png")));
		sol_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.SOL, BlocImModifier.LOUPE)));
		terre_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.TERRE, BlocImModifier.LOUPE)));
		ciel_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.CIEL, BlocImModifier.LOUPE)));
		perso_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.PERSO, BlocImModifier.LOUPE)));
		start_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.START, BlocImModifier.LOUPE)));
		end_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.END, BlocImModifier.LOUPE)));
		spirel_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.SPIREL, BlocImModifier.LOUPE)));
		delete_p = new ActiveJButton(new ImageIcon(affich.controlerEditeur.edit.imagesMonde.getImage(null, TypeBloc.DELETE, BlocImModifier.LOUPE)));


		scroll.setPreferredSize(new Dimension(400,40));

		souris_p.addActionListener(new TextureListener());
		tbar.add(souris_p);
		delete_p.addActionListener(new TextureListener());
		tbar.add(delete_p);

		loupe_p.addActionListener(new TextureListener());
		tbar.add(loupe_p);

		//vide_p.addActionListener(new TextureListener());
		//tbar.add(vide_p);
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
		isInit=true;
	}
	
	public boolean isInit()
	{
		return isInit;
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
				edit.setTexture(TypeBloc.NONE);
			}
			else if(e.getSource()==sol_p){
				edit.setTexture(TypeBloc.SOL);
			}
			/*else if(e.getSource()==vide_p){
				edit.setTexture(TypeBloc.NONE);
			}*/
			else if(e.getSource()==terre_p){
				edit.setTexture(TypeBloc.TERRE);
			}
			else if(e.getSource()==ciel_p){
				edit.setTexture(TypeBloc.CIEL);
			}
			else if(e.getSource()==perso_p){
				edit.setTexture(TypeBloc.PERSO);
			}
			else if(e.getSource()==start_p){
				edit.setTexture(TypeBloc.START);
			}
			else if(e.getSource()==end_p){
				edit.setTexture(TypeBloc.END);
			}
			else if(e.getSource()==spirel_p){
				edit.setTexture(TypeBloc.SPIREL);
			}
			else if(e.getSource()==delete_p){
				edit.setTexture(TypeBloc.DELETE);
			}
		}

	}
}
