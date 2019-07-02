package editeur;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Affichage.Drawable;
import editeur.BarreOutil.BarreOutil;
import editeur.Menu.menuEditeur;
import gameConfig.InterfaceConstantes;
import menu.menuPrincipal.GameHandler;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichageEditeur extends Drawable implements Observer{
	
	public AbstractControlerEditeur controlerEditeur;
	private BufferedImage im_white =null;
	public JMenuBar getJMenuBar()
	{
		return controlerEditeur.edit.menuEdit.menuBar;
	}
	public AffichageEditeur(AbstractControlerEditeur _controlerEditeur)
	{
		super();
		controlerEditeur=_controlerEditeur;
		
		this.getContentPane().removeAll();
		
		controlerEditeur.edit.barreOut= new BarreOutil(this);
		
		controlerEditeur.edit.menuEdit= new menuEditeur(this);
		controlerEditeur.edit.menuEdit.initMenu();
		
		controlerEditeur.edit.init();
		
		im_white = new BufferedImage(InterfaceConstantes.tailleEcran.width,InterfaceConstantes.tailleEcran.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D    graphics = im_white.createGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect ( 0, 0, im_white.getWidth(), im_white.getHeight() );

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(controlerEditeur.edit.barreOut.scroll,BorderLayout.NORTH);
		this.getContentPane().setOpaque(false);
		//mainPanel.setBackground(Color.white);
	   //REMOVE  this.getContentPane().add(dessin,BorderLayout.CENTER);
	}
	
	
	public class DeleteMonstrePopUp extends JDialog {
		  public DeleteMonstrePopUp(JFrame parent, String title, boolean modal){
		    //On appelle le construteur de JDialog correspondant
		    super(parent, title, modal);
		    //On specifie une taille
		    this.setSize(300, 400);
		    //La position
		    this.setLocationRelativeTo(null);
		    //La boite ne devra pas etre redimensionnable
		    this.setResizable(false);
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
					AffichageEditeur.this.controlerEditeur.edit.computationDone=false; 
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
					AffichageEditeur.this.controlerEditeur.edit.computationDone=true; 
					}});
		   
		    affichage.add(listLabels.get(0),BorderLayout.NORTH);
		    for(int i=1; i<listLabels.size();i++)
		    {
			    affichage.add(listLabels.get(i),BorderLayout.WEST);
			    affichage.add(listChecks.get(i-1),BorderLayout.EAST);
		    }	
			affichage.add(ok,BorderLayout.SOUTH);
			
			this.getContentPane().add(affichage);
		  }
		}

	public class ComportementSpirelPopUp extends JDialog 
	{
		public ComportementSpirelPopUp(JFrame parent, String title, boolean modal){
		    //On appelle le construteur de JDialog correspondant
		    super(parent, title, modal);
		    //On specifie une taille
		    this.setSize(200, 100);
		    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		    //La position
		    this.setLocationRelativeTo(null);
		    //La boite ne devra pas etre redimensionnable
		    this.setResizable(false);
		    JPanel affichage = new JPanel();
		    JPanel panelStaticSpirel= new JPanel();
		    JButton ok = new JButton("OK");	
		    
		    JLabel lStaticSpirel= new JLabel("Spirel immobile ");
		    final JCheckBox checkStatic= new JCheckBox();
		    
		    ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) 
				{
					AffichageEditeur.this.controlerEditeur.edit.computationDone=false; 
					if(checkStatic.isSelected())
					{
						controlerEditeur.edit.staticMonstre=true;
					}
					else
					{
						controlerEditeur.edit.staticMonstre=false;
					}
					setVisible(false);
					AffichageEditeur.this.controlerEditeur.edit.computationDone=true; 
				}	});
		    affichage.setLayout(new BorderLayout());
		    panelStaticSpirel.setLayout(new GridLayout(1,2));
		    panelStaticSpirel.add(lStaticSpirel);
		    panelStaticSpirel.add(checkStatic);
			affichage.add(panelStaticSpirel,BorderLayout.CENTER);
		    affichage.add(ok,BorderLayout.SOUTH);
			this.getContentPane().add(affichage);
		  }
	}
	
	
	class DragListener extends MouseAdapter{
		public void mouseDragged(MouseEvent e){
			AffichageEditeur.this.controlerEditeur.edit.computationDone=false; 
			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			{
				controlerEditeur.edit.moveViewPort(e.getX(),e.getY());
			}
			AffichageEditeur.this.controlerEditeur.edit.computationDone=true; 
		}
		
		public void mouseReleased(MouseEvent e){
			controlerEditeur.edit.releaseMoveViewport();	}
	}
	
	class ImageListener implements MouseMotionListener {
	
		//lorsqu'on drag, on dessine sur chacune des cases sur lesquels on passe
		public void mouseDragged(MouseEvent e) {
			AffichageEditeur.this.controlerEditeur.edit.computationDone=false; 
			if(!controlerEditeur.edit.drag && ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0))
			controlerEditeur.controlDraw(e.getX(), e.getY());
			controlerEditeur.edit.xMousePos=e.getX();
			controlerEditeur.edit.yMousePos=e.getY();
			AffichageEditeur.this.controlerEditeur.edit.computationDone=true; 
			//REMOVE mainPanel.repaint();
		}
		public void mouseMoved(MouseEvent e) 
		{
			AffichageEditeur.this.controlerEditeur.edit.computationDone=false; 
			controlerEditeur.edit.xMousePos=e.getX();
			controlerEditeur.edit.yMousePos=e.getY();
			AffichageEditeur.this.controlerEditeur.edit.computationDone=true; 
			//REMOVE mainPanel.repaint();
		}
	}

	class AjoutListener implements MouseListener{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			AffichageEditeur.this.controlerEditeur.edit.computationDone=false; 
			if(!controlerEditeur.edit.drag && ((e.getModifiers() & InputEvent.BUTTON1_MASK)!=0) )
				controlerEditeur.controlDraw(e.getX(), e.getY());
			AffichageEditeur.this.controlerEditeur.edit.computationDone=true; 
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
	public void draw(Graphics g)
	{
		//Nothing to draw
		//draw white background 
		g.drawImage(im_white, 0, 0, null);
		controlerEditeur.edit.draw(g, mainPanel);
		mainFrame.warnFadeOutCanStart();
	}
	
	public JFrame getMainFrame()
	{
		return mainFrame;
	}
	
	public void update() 
	{
			if(controlerEditeur.edit.getRepaint())
			{
				mainPanel.repaint();
			}
			
			if(controlerEditeur.edit.getShowMonsters())
			{
				DeleteMonstrePopUp popUp= new DeleteMonstrePopUp(mainFrame,"test",true);
				popUp.setVisible(true);
			}
			
			if(controlerEditeur.edit.getShowStaticMonsters())
			{
				ComportementSpirelPopUp comportement = new ComportementSpirelPopUp(mainFrame,"Choix comportement spirel",true);
				comportement.setVisible(true);
			}
			
			if(controlerEditeur.edit.showMessageDialog)
			{
				JOptionPane.showMessageDialog(mainPanel, controlerEditeur.edit.textMessageDialog[0], 
						controlerEditeur.edit.textMessageDialog[1],controlerEditeur.edit.typeMessageDialog);
			}

			
			controlerEditeur.edit.resetVariablesAffichage();
		}

}
