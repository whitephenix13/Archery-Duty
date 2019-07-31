package editeur;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ActiveJComponent.ActiveJCheckBox;
import ActiveJComponent.ActiveJFrame;
import ActiveJComponent.ActiveJMenuBar;
import ActiveJComponent.ActiveJOptionPane;
import ActiveJComponent.PassiveJDialog;
import Affichage.Drawable;
import editeur.BarreOutil.BarreOutil;
import editeur.Menu.menuEditeur;
import images.ImagesBackground.ImBackgroundInfo;
import images.ImagesContainer.ImageGroup;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichageEditeur extends Drawable implements Observer{

	public AbstractControlerEditeur controlerEditeur;
	private boolean isLayoutInit;


	public AffichageEditeur(AbstractControlerEditeur _controlerEditeur)
	{
		super();
		controlerEditeur=_controlerEditeur;
		isLayoutInit=false;//wait for image to be loaded in BarreOutil to activate the layout

		controlerEditeur.edit.barreOut= new BarreOutil(this);

		controlerEditeur.edit.menuEdit= new menuEditeur(this);
		controlerEditeur.edit.menuEdit.initMenu();

		controlerEditeur.edit.init();

		mainPanel.setLayout(new BorderLayout());
		mainPanel.setOpaque(false);

	}

	public ActiveJMenuBar getJMenuBar()
	{
		return controlerEditeur.edit.menuEdit.menuBar;
	}
	public class DeleteMonstrePopUp extends PassiveJDialog {
		public DeleteMonstrePopUp(ActiveJFrame parent, String title, boolean modal){
			//On appelle le construteur de JDialog correspondant
			super(parent, title, modal);
			super.setComponentAndShowDialog(createContent());
		}
		private Component createContent()
		{
			JPanel affichage = new JPanel();
			JButton ok = new JButton("OK");	
			List<JLabel> listLabels = new ArrayList<JLabel>();
			listLabels.add(new JLabel("Listes des monstres à supprimer\n\n\n"));

			for(int i=0; i<controlerEditeur.edit.monstreDansCase.size();i++)
			{
				listLabels.add(new JLabel((i+1)+ " "+controlerEditeur.edit.monstreDansCase.get(i).type.toString()));
			}	    
			final List<JCheckBox> listChecks= new ArrayList<JCheckBox> (); 
			for(int i=0; i<listLabels.size()-1;i++)
			{
				listChecks.add(new JCheckBox());
			}	

			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					List<StockageMonstre> monstreASupprimer= new ArrayList<StockageMonstre>();
					for(int i=0; i<listChecks.size();i++)
					{
						if(listChecks.get(i).isSelected())
						{
							monstreASupprimer.add(controlerEditeur.edit.monstreDansCase.get(i));
						}
					}	
					//on les supprime
					for(int i=0; i<monstreASupprimer.size();i++)
					{
						controlerEditeur.edit.tabEditeurMonstre.remove(monstreASupprimer.get(i));
					}	
					setVisible(false);
				}});

			affichage.add(listLabels.get(0),BorderLayout.NORTH);
			for(int i=1; i<listLabels.size();i++)
			{
				affichage.add(listLabels.get(i),BorderLayout.WEST);
				affichage.add(listChecks.get(i-1),BorderLayout.EAST);
			}	
			affichage.add(ok,BorderLayout.SOUTH);
			return affichage;
		}
	}

	public class ComportementSpirelPopUp extends PassiveJDialog 
	{
		public ComportementSpirelPopUp(ActiveJFrame parent, String title, boolean modal){
			//On appelle le construteur de JDialog correspondant
			super(parent, title, modal);
			this.setDefaultCloseOperation(ActiveJFrame.DO_NOTHING_ON_CLOSE);
			super.setComponentAndShowDialog(createContent());
			
		}
		
		private Component createContent()
		{
			JPanel affichage = new JPanel();
			JPanel panelStaticSpirel= new JPanel();
			JButton ok = new JButton("OK");	

			JLabel lStaticSpirel= new JLabel("Spirel immobile ");
			final JCheckBox checkStatic= new JCheckBox();

			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) 
				{
					if(checkStatic.isSelected())
					{
						controlerEditeur.edit.staticMonstre=true;
					}
					else
					{
						controlerEditeur.edit.staticMonstre=false;
					}
					setVisible(false);
				}	});
			affichage.setLayout(new BorderLayout());
			panelStaticSpirel.setLayout(new GridLayout(1,2));
			panelStaticSpirel.add(lStaticSpirel);
			panelStaticSpirel.add(checkStatic);
			affichage.add(panelStaticSpirel,BorderLayout.CENTER);
			affichage.add(ok,BorderLayout.SOUTH);
			return affichage;
		}
	}


	class DragListener extends MouseAdapter{
		public void mouseDragged(MouseEvent e){
			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			{
				controlerEditeur.edit.moveViewPort(e.getX(),e.getY());
			}
		}

		public void mouseReleased(MouseEvent e){
			controlerEditeur.edit.releaseMoveViewport();	}
	}

	class ImageListener implements MouseMotionListener {

		//lorsqu'on drag, on dessine sur chacune des cases sur lesquels on passe
		public void mouseDragged(MouseEvent e) {
			if(!controlerEditeur.edit.drag && ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0))
				controlerEditeur.controlDraw(e.getX(), e.getY());
			controlerEditeur.edit.xMousePos=e.getX();
			controlerEditeur.edit.yMousePos=e.getY();
		}
		public void mouseMoved(MouseEvent e) 
		{
			controlerEditeur.edit.xMousePos=e.getX();
			controlerEditeur.edit.yMousePos=e.getY();
		}
	}

	class AjoutListener implements MouseListener{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			if(!controlerEditeur.edit.drag && ((e.getModifiers() & InputEvent.BUTTON1_MASK)!=0) )
				controlerEditeur.controlDraw(e.getX(), e.getY());
		}
		public void mouseReleased(MouseEvent e) {}
	}

	public void addListenerEditeur()
	{

		controlerEditeur.edit.menuEdit.addListenerMenuEditeur();

		mainPanel.addMouseListener(new DragListener());
		mainPanel.addMouseMotionListener(new DragListener());
		mainPanel.addMouseMotionListener(new ImageListener());
		mainPanel.addMouseListener(new AjoutListener());

	}
	public void removeListenerEditeur() 
	{

		controlerEditeur.edit.menuEdit.removeListenerMenuEditeur();

		//remove the two last elements 
		mainPanel.removeMouseMotionListener(mainPanel.getMouseMotionListeners()[mainPanel.getMouseMotionListeners().length-2]);
		mainPanel.removeMouseMotionListener(mainPanel.getMouseMotionListeners()[mainPanel.getMouseMotionListeners().length-1]);
		//remove the two last elements 
		mainPanel.removeMouseListener(mainPanel.getMouseListeners()[mainPanel.getMouseListeners().length-2]);
		mainPanel.removeMouseListener(mainPanel.getMouseListeners()[mainPanel.getMouseListeners().length-1]);
	}


	@Override
	public void drawOnGraphics(Graphics g,boolean forceRepaint)
	{
		//draw white background 
		g.drawImage(controlerEditeur.edit.gameHandler.getImage(ImageGroup.BACKGROUND, null, ImBackgroundInfo.WHITE, null), 0, 0, null);
		controlerEditeur.edit.draw(g, mainPanel);
	}

	public void update() 
	{
		if(!isLayoutInit)
		{
			mainPanel.add(controlerEditeur.edit.barreOut.scroll,BorderLayout.NORTH);
			mainPanel.doLayout();
			isLayoutInit=true;
		}
		if(controlerEditeur.edit.getShowMonsters())
		{
			DeleteMonstrePopUp popUp= new DeleteMonstrePopUp(getActiveJFrame(),"test",true);
			popUp.setVisible(true);
		}

		if(controlerEditeur.edit.getShowStaticMonsters())
		{
			ComportementSpirelPopUp comportement = new ComportementSpirelPopUp(getActiveJFrame(),"Choix comportement spirel",true);
			comportement.setVisible(true);
		}

		if(controlerEditeur.edit.showMessageDialog)
		{
			ActiveJOptionPane.showMessageDialog(mainPanel, controlerEditeur.edit.textMessageDialog[0], 
					controlerEditeur.edit.textMessageDialog[1],controlerEditeur.edit.typeMessageDialog);
		}


		controlerEditeur.edit.resetVariablesAffichage();
	}

}
