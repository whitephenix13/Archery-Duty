package menu.menuPrincipal;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import images.ImagesPrincipal;
import utils.TypeApplication;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichagePrincipal extends JFrame implements Observer{

	@SuppressWarnings("unused")
	private AbstractControlerPrincipal controlerPrincipal;
	
	private	PanelPrincipal panelBoutons = new PanelPrincipal();
	private BoutonPrincipal bPartieRapide=new BoutonPrincipal("Partie Rapide");
	private BoutonPrincipal bCredit=new BoutonPrincipal("Credit");
	private BoutonPrincipal bEditeur=new BoutonPrincipal("Editeur");
	private BoutonPrincipal bOption=new BoutonPrincipal("Option");
	private BoutonPrincipal bQuitter=new BoutonPrincipal("Quitter");
	
	//@Override
	//public void execute(){panelBoutons.repaint();};
	
	public class BoutonPrincipal extends JButton
	{
		public BoutonPrincipal(String s)
		{
			super(s);
			this.setForeground(Color.WHITE);
			this.setBackground(Color.BLACK);
			this.setFont(new Font("Courrier",Font.PLAIN,44));
			this.setEnabled(true);
			this.setVisible(true);
		}
	}
	public class PanelPrincipal extends JPanel
	{
		public PanelPrincipal()
		{
			super();
		}
		//change paint to paintcomponent 27/05/17
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if(controlerPrincipal.principal.loaderMenuPrincipal ==null)
			{
				return;
			}
			if(controlerPrincipal.principal.loaderMenuPrincipal.isLoadingDone())
			{
				g.drawImage(controlerPrincipal.principal.imPrincipal.getImage(ImagesPrincipal.BACKGROUND),0,0,this);		
	
				//methode pour faire apparaitre les boutons au dessus de l'image 
				setOpaque(false);
				//change paint to paintcomponent 27/05/17
				super.paintComponent(g);
				setOpaque(true);
			}
			else
			{
				controlerPrincipal.principal.loaderMenuPrincipal.showLoading(g);
			}
		}
	}

	public AffichagePrincipal(AbstractControlerPrincipal _controlerPrincipal)
	{
		controlerPrincipal=_controlerPrincipal;
		Init();
	}
	
	public void setButtons()
	{
		panelBoutons.setLayout(null);
		panelBoutons.add(bPartieRapide);
		if(TypeApplication.isJar)
			panelBoutons.add(bCredit);
		else
			panelBoutons.add(bEditeur);
		panelBoutons.add(bOption);
		panelBoutons.add(bQuitter);
	}
	public void removeButtons()
	{
		panelBoutons.removeAll();
	}
	public void Init()
	{
		bPartieRapide.setBounds(90, 260, 320, 50);
		bCredit.setBounds(90, 560, 290, 50);
		bEditeur.setBounds(90, 560, 290, 50);
		bOption.setBounds(990, 260, 290, 50);
		bQuitter.setBounds(990, 560, 290, 50);
		
		panelBoutons.setBackground(Color.black);
		this.setContentPane(panelBoutons);
		
		controlerPrincipal.principal.imPrincipal.run();;
	}

	public class boutonsPrincipalListener implements MouseListener 
	{

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent e) 
		{

		}

		public void mouseReleased(MouseEvent e) {
			JButton button = (JButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				if(button.getText().equals("Partie Rapide"))
				{
					AbstractModelPrincipal.changeFrame=true; 
					AbstractModelPrincipal.modeSuivant="ChoixNiveau"; //modeSuivant="Partie";
					AbstractModelPrincipal.changeMode=true; 

				}
				else if(button.getText().equals("Editeur"))
				{
					AbstractModelPrincipal.changeFrame=true;
					AbstractModelPrincipal.modeSuivant="Editeur";
					AbstractModelPrincipal.changeMode=true; 
				}
				else if(button.getText().equals("Credit"))
				{
					AbstractModelPrincipal.changeFrame=true;
					AbstractModelPrincipal.modeSuivant="Credit";
					AbstractModelPrincipal.changeMode=true; 
				}
				else if(button.getText().equals("Option"))
				{
					AbstractModelPrincipal.changeFrame=true;
					AbstractModelPrincipal.modeSuivant="Option";
					AbstractModelPrincipal.changeMode=true; 
				}
				else if(button.getText().equals("Quitter"))
				{
					AbstractModelPrincipal.changeFrame=true; 
					AbstractModelPrincipal.modeSuivant="Quitter";

					AbstractModelPrincipal.changeMode=true; 
				}
			}
		}

	}
	public void addListenerPrincipal() 
	{
		bPartieRapide.addMouseListener(new boutonsPrincipalListener());
		if(TypeApplication.isJar)
			bCredit.addMouseListener(new boutonsPrincipalListener());
		else
			bEditeur.addMouseListener(new boutonsPrincipalListener());
		bOption.addMouseListener(new boutonsPrincipalListener());
		bQuitter.addMouseListener(new boutonsPrincipalListener());
	}

	public void removeListenerPrincipal() 
	{
		bPartieRapide.removeMouseListener(bPartieRapide.getMouseListeners()[bPartieRapide.getMouseListeners().length-1]);
		if(TypeApplication.isJar)
			bCredit.removeMouseListener(bEditeur.getMouseListeners()[bEditeur.getMouseListeners().length-1]);
		else
			bEditeur.removeMouseListener(bEditeur.getMouseListeners()[bEditeur.getMouseListeners().length-1]);
		bOption.removeMouseListener(bOption.getMouseListeners()[bOption.getMouseListeners().length-1]);
		bQuitter.removeMouseListener(bQuitter.getMouseListeners()[bQuitter.getMouseListeners().length-1]);
	}

	public void update() {
		this.repaint();
	}

}
