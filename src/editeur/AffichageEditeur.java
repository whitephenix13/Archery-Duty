package editeur;

import java.awt.BorderLayout;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import observer.Observer;
import types.StockageMonstre;
import editeur.BarreOutil.BarreOutil;
import editeur.Menu.menuEditeur;

@SuppressWarnings("serial")
public class AffichageEditeur extends JFrame implements Observer{

	protected PanelDraw dessin = new PanelDraw();
	
	public AbstractControlerEditeur controlerEditeur;
	public JMenuBar getJMenuBar()
	{
		return controlerEditeur.edit.menuEdit.menuBar;
	}
	public AffichageEditeur(AbstractControlerEditeur _controlerEditeur)
	{
		controlerEditeur=_controlerEditeur;
		
		this.getContentPane().removeAll();
		
		controlerEditeur.edit.barreOut= new BarreOutil(this);
		
		controlerEditeur.edit.menuEdit= new menuEditeur(this);
		controlerEditeur.edit.menuEdit.initMenu();
		
		controlerEditeur.edit.init();

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(controlerEditeur.edit.barreOut.scroll,BorderLayout.NORTH);
	    this.getContentPane().add(dessin,BorderLayout.CENTER);
	}

	public class PanelDraw extends JPanel
	{
		public void paint(Graphics g)
		{
			super.paintComponent(g);
			controlerEditeur.edit.draw(g, this);
		}
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
			    listLabels.add(new JLabel((i+1)+ " "+controlerEditeur.edit.monstreDansCase.get(i).nom));
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
					setVisible(false);}});
		   
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
			this.getContentPane().add(affichage);
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
			dessin.repaint();
		}
		public void mouseMoved(MouseEvent e) 
		{
			controlerEditeur.edit.xMousePos=e.getX();
			controlerEditeur.edit.yMousePos=e.getY();
			dessin.repaint();
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
			
			dessin.addMouseListener(new DragListener());
			dessin.addMouseMotionListener(new DragListener());
			dessin.addMouseMotionListener(new ImageListener());
			dessin.addMouseListener(new AjoutListener());
		 	 
	}
	public void removeListenerEditeur() 
	{
		
		controlerEditeur.edit.menuEdit.removeListenerMenuEditeur();
		
		//remove the two last elements 
		dessin.removeMouseMotionListener(dessin.getMouseMotionListeners()[dessin.getMouseMotionListeners().length-2]);
		dessin.removeMouseMotionListener(dessin.getMouseMotionListeners()[dessin.getMouseMotionListeners().length-1]);
		//remove the two last elements 
		dessin.removeMouseListener(dessin.getMouseListeners()[dessin.getMouseListeners().length-2]);
		dessin.removeMouseListener(dessin.getMouseListeners()[dessin.getMouseListeners().length-1]);
	}
	
	
	
	public void update() 
	{
			if(controlerEditeur.edit.getRepaint())
			{
				dessin.repaint();
			}
			
			if(controlerEditeur.edit.getShowMonsters())
			{
				DeleteMonstrePopUp popUp= new DeleteMonstrePopUp(this,"test",true);
				popUp.setVisible(true);
			}
			
			if(controlerEditeur.edit.getShowStaticMonsters())
			{
				ComportementSpirelPopUp comportement = new ComportementSpirelPopUp(this,"Choix comportement spirel",true);
				comportement.setVisible(true);
			}
			
			if(controlerEditeur.edit.showMessageDialog)
			{
				JOptionPane.showMessageDialog(this, controlerEditeur.edit.textMessageDialog[0], 
						controlerEditeur.edit.textMessageDialog[1],controlerEditeur.edit.typeMessageDialog);
			}

			
			controlerEditeur.edit.resetVariablesAffichage();
		}

}
