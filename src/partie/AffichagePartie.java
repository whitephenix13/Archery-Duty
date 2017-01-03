package partie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import observer.Observer;
import option.AbstractControlerOption;
import option.AbstractModelOption;
import option.AffichageOption;
import option.ControlerOption;
import option.ModelOption;
import Affichage.Affichage;

@SuppressWarnings("serial")
public class AffichagePartie extends JFrame implements Observer{

	protected MenuJButton bRejouer=new MenuJButton("Rejouer");
	protected MenuJButton bMenuPrincipal=new MenuJButton("Menu Principal");
	protected MenuJButton bMenuPrincipal2=new MenuJButton("Menu Principal");

	protected MenuJButton bReprendre= new MenuJButton("Reprendre");
	//autres buttons aussi utilis� 
	protected MenuJButton bOption=new MenuJButton("Option");
	protected MenuJButton bQuitter=new MenuJButton("Quitter");
	
	
	protected PanelPartie panelPartie=new PanelPartie();
	protected JPanel panelPauseY = new JPanel();
	protected JPanel panelPauseX = new JPanel();
	protected boolean firstTimePause=false;
	
	protected JPanel panelFinX = new JPanel();
	protected JPanel panelFinY = new JPanel();

	protected boolean firstTimeFin=false;
	
	protected boolean doitRevalidate=false;
	AbstractControlerPartie controlerPartie;
	
	public AffichagePartie(AbstractControlerPartie _controlerPartie)
	{
		controlerPartie=_controlerPartie;
		initAffichage();
	}
	
	
	public class PanelPartie extends JPanel 
	{
		
		public void paint(Graphics g)
		{
			super.paintComponent(g);
			
			//on dessine le niveau
			controlerPartie.partie.drawPartie(g,this);
		
			//on affiche les boutons si necessaire 
			if(controlerPartie.partie.finPartie ||controlerPartie.partie.inPause)
			{
				super.paint(g);
				setOpaque(true); 
			}
		}
	}
	
	public class MenuJButton extends JButton
	{
		public MenuJButton(String s)
		{
			super(s);
			this.setForeground(Color.WHITE);
			this.setBackground(Color.BLACK);
			this.setFont(new Font("Courrier",Font.PLAIN,44));
		}
	}
	
	public void repaintPartie()
	{
		
		if(!controlerPartie.partie.finPartie && firstTimePause)
		{
			doitRevalidate=true;
			firstTimePause=false;
			requestGameFocus();
		}
		
		if(!controlerPartie.partie.inPause && firstTimeFin)
		{
			doitRevalidate=true;
			firstTimeFin=false;
			requestGameFocus();
		}
		
		if(controlerPartie.partie.finPartie && !firstTimePause)
		{
			doitRevalidate=true;
			firstTimePause=true;
			
			EnableBoutonsFin(true);

			panelPartie.removeAll();
			panelPartie.add(panelFinY);
		}
		else if(!controlerPartie.partie.finPartie &&controlerPartie.partie.inPause && !firstTimeFin)
		{
			System.out.println("FinPartie");
			doitRevalidate=true;
			firstTimeFin=true;

			EnableBoutonsPause(true);
			
			panelPartie.removeAll();
			panelPartie.add(panelPauseX);
			
		}

		panelPartie.repaint();
		//this.revalidate();

	}
	public void requestGameFocus()
	{
		panelPartie.requestFocusInWindow();

	}
	public void validateAffichagePartie(Affichage affich)
	{
		if(doitRevalidate)
		{
			affich.revalidate();
			doitRevalidate=false;
		}
		
	}
	public void initAffichage()
	{
		panelPauseY.setLayout(new BoxLayout(panelPauseY,BoxLayout.Y_AXIS));
		panelPauseX.setLayout(new BoxLayout(panelPauseX,BoxLayout.X_AXIS));

		panelFinX.setLayout(new BoxLayout(panelFinX,BoxLayout.X_AXIS));
		panelFinY.setLayout(new BoxLayout(panelFinY,BoxLayout.Y_AXIS));

		panelPauseY.setOpaque(false);
		panelPauseX.setOpaque(false);
		
		panelPauseY.add(Box.createVerticalGlue());
		panelPauseY.add(bReprendre);
		panelPauseY.add(Box.createVerticalGlue());
		panelPauseY.add(bOption);
		panelPauseY.add(Box.createVerticalGlue());
		panelPauseY.add(bMenuPrincipal);
		panelPauseY.add(Box.createVerticalGlue());
		panelPauseY.add(bQuitter);
		panelPauseY.add(Box.createVerticalGlue());
		
		panelPauseX.add(Box.createHorizontalGlue());
		panelPauseX.add(panelPauseY);
		panelPauseX.add(Box.createHorizontalGlue());
		
		panelFinY.setOpaque(false);
		panelFinX.setOpaque(false);
		
		panelFinX.add(Box.createHorizontalGlue());
		panelFinX.add(bRejouer);
		panelFinX.add(Box.createHorizontalGlue());
		panelFinX.add(bMenuPrincipal2);
		panelFinX.add(Box.createHorizontalGlue());
		
		panelFinY.add(Box.createVerticalGlue());
		panelFinY.add(Box.createVerticalGlue());
		panelFinY.add(panelFinX);
		panelFinY.add(Box.createVerticalGlue());
	    
		panelPartie.setLayout(new BorderLayout());		
		this.getContentPane().add(panelPartie);
		   


		//on utilise le content pane principal pour dessiner 
		panelPartie.setFocusable(true);
		panelPartie.requestFocusInWindow();
	}
	
	public void EnableBoutonsPause(boolean enable)
	{
		bReprendre.setEnabled(enable);
		bReprendre.setVisible(enable);
		bOption.setEnabled(enable);
		bOption.setVisible(enable);
		bMenuPrincipal.setEnabled(enable);
		bMenuPrincipal.setVisible(enable);
		bQuitter.setEnabled(enable);
		bQuitter.setVisible(enable);
	}
	public void EnableBoutonsFin(boolean enable)
	{
		bRejouer.setEnabled(true);
		bRejouer.setVisible(true);
		
		bMenuPrincipal2.setEnabled(true);
		bMenuPrincipal2.setVisible(true);
	}
	
	/**
	 * ajoute les listeners de PartieRapide
	 * 
	 * @param Affichage: la JFrame a afficher
	 */	

	public void addListenerPartie()
	{
		panelPartie.addKeyListener(new ClavierListener());
		panelPartie.addMouseListener(new SourisListener());
		panelPartie.addMouseMotionListener(new SourisMotionListener());
		
		bRejouer.addMouseListener(new boutonsPrincipalListener());
		bOption.addMouseListener(new boutonsPrincipalListener());
		bReprendre.addMouseListener(new boutonsPrincipalListener());
		bQuitter.addMouseListener(new boutonsPrincipalListener());
		bMenuPrincipal.addMouseListener(new boutonsPrincipalListener());
		bMenuPrincipal2.addMouseListener(new boutonsPrincipalListener());

	}
	public void removeListenerPartie()
	{
		panelPartie.removeKeyListener(panelPartie.getKeyListeners()[0]);
		panelPartie.removeMouseListener(panelPartie.getMouseListeners()[0]);
		panelPartie.removeMouseMotionListener(panelPartie.getMouseMotionListeners()[0]);
		
		bRejouer.removeMouseListener(bRejouer.getMouseListeners()[1]);
		bMenuPrincipal.removeMouseListener(bMenuPrincipal.getMouseListeners()[1]);
		bMenuPrincipal2.removeMouseListener(bMenuPrincipal2.getMouseListeners()[1]);
		bOption.removeMouseListener(bOption.getMouseListeners()[1]);
		bReprendre.removeMouseListener(bReprendre.getMouseListeners()[1]);;
		bQuitter.removeMouseListener(bQuitter.getMouseListeners()[1]);
	}

	

	public class ClavierListener implements KeyListener
	  {
	    public void keyPressed(KeyEvent event) 
	    {
	    	controlerPartie.controlPressedInput(event.getKeyCode());
	    }
	    public void keyReleased(KeyEvent event) 
	    {
	    	controlerPartie.controlReleasedInput(event.getKeyCode());
	    }
	    
	    public void keyTyped(KeyEvent event) 
	    {
	    }       
	 }

	public class SourisListener implements MouseListener
	{
		public void mouseClicked(MouseEvent arg0) {	
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {	
		}
		public void mousePressed(MouseEvent e) {
			controlerPartie.controlMousePressed(e);
		}
		public void mouseReleased(MouseEvent e) {
			controlerPartie.controlMouseReleased(e);
		}
		
	}

	public class SourisMotionListener implements MouseMotionListener 
	{
		public void mouseDragged(MouseEvent e) 
		{
			controlerPartie.partie.xPositionSouris=e.getX();
			controlerPartie.partie.yPositionSouris=e.getY();
		}
		public void mouseMoved(MouseEvent e) 
		{
			controlerPartie.partie.xPositionSouris=e.getX();
			controlerPartie.partie.yPositionSouris=e.getY();

		}
	}

	public class boutonsPrincipalListener implements MouseListener 
	{

		public void mouseClicked(MouseEvent e) {
	}

		public void mouseEntered(MouseEvent arg0) {}

		public void mouseExited(MouseEvent arg0) {}

		public void mousePressed(MouseEvent e) 
		{
			controlerPartie.controlBoutonsPressed(((JButton)e.getSource()));
		}

		public void mouseReleased(MouseEvent arg0) {}
		
	}
	
	public void createOption()
	{
		AbstractModelOption option = new ModelOption();
		AbstractControlerOption controlerOption = new ControlerOption(option);
		final AffichageOption affichageOption = new AffichageOption(controlerOption);
		affichageOption.addListenerOption();
		affichageOption.retour.setContentAreaFilled(false);
		affichageOption.retour.removeMouseListener( affichageOption.retour.getMouseListeners()[1]);
		option.addObserver(affichageOption);
		final Component[] components =this.getContentPane().getComponents();
		
		this.getContentPane().removeAll();
		this.getContentPane().add(affichageOption.getContentPane());
		
		affichageOption.retour.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0) {}			
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) 
			{
				(AffichagePartie.this).getContentPane().removeAll();
				for(Component c : components)
				{
					(AffichagePartie.this).getContentPane().add(c);
				}
				(AffichagePartie.this).repaint();
				(AffichagePartie.this).revalidate();
				//doitRevalidate=true;
			}
			public void mouseReleased(MouseEvent e) {}
		});

		this.repaint();
		this.revalidate();
		doitRevalidate=true;
	}
	
	public void update() {	
		if(controlerPartie.partie.getDisableBoutonsFin())
			EnableBoutonsFin(false);
		
		if(controlerPartie.partie.setAffichageOption)
		{
			createOption();
			
		}
		controlerPartie.partie. resetVariablesAffichage();
		
		
	}

}
