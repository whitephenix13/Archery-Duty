package partie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
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
	//autres buttons aussi utilisé 
	protected MenuJButton bOption=new MenuJButton("Option");
	protected MenuJButton bQuitter=new MenuJButton("Quitter");


	protected PanelPartie panelPartie=new PanelPartie();
	protected JPanel panelPauseY = new JPanel();
	protected JPanel panelPauseX = new JPanel();
	protected boolean firstTimePause=false;
	protected boolean firstTimeFin=false;

	protected JPanel panelFinX = new JPanel();
	protected JPanel panelFinY = new JPanel();


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
		//End game 
		if(controlerPartie.partie.finPartie && !firstTimePause)
		{
			doitRevalidate=true;
			firstTimePause=true;

			EnableBoutonsFin(true);

			panelPartie.removeAll();
			panelPartie.add(panelFinY);
		}
		//Start pause
		else if(!controlerPartie.partie.finPartie &&controlerPartie.partie.inPause && !firstTimeFin)
		{
			doitRevalidate=true;
			firstTimeFin=true;

			EnableBoutonsPause(true);

			panelPartie.removeAll();
			panelPartie.add(panelPauseX);

		}
		//End pause


		//reset var 
		if(!controlerPartie.partie.finPartie && firstTimePause)
		{
			doitRevalidate=true;
			firstTimePause=false;
			requestGameFocus();
		}

		if(!controlerPartie.partie.finPartie && firstTimePause)
		{
			doitRevalidate=true;
			firstTimePause=false;
			requestGameFocus();
		}
		//End pause 
		if(!controlerPartie.partie.inPause && firstTimeFin)
		{
			doitRevalidate=true;
			firstTimeFin=false;
			requestGameFocus();

			EnableBoutonsPause(false);
			panelPartie.removeAll();
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

		MenuJButton[] addMouse = {bRejouer,bOption,bReprendre,bQuitter,bMenuPrincipal,bMenuPrincipal2};
		for(MenuJButton mjb : addMouse)
			mjb.addMouseListener(new boutonsPrincipalListener());

	}
	public void removeListenerPartie()
	{
		panelPartie.removeKeyListener(panelPartie.getKeyListeners()[0]);
		panelPartie.removeMouseListener(panelPartie.getMouseListeners()[0]);
		panelPartie.removeMouseMotionListener(panelPartie.getMouseMotionListeners()[0]);

		MenuJButton[] removeMouse = {bRejouer,bOption,bReprendre,bQuitter,bMenuPrincipal,bMenuPrincipal2};
		for(MenuJButton mjb : removeMouse){
			MouseListener[] listeners = mjb.getMouseListeners();
			mjb.removeMouseListener(listeners[listeners.length-1]);
		}
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
		}

		public void mouseReleased(MouseEvent e) 
		{
			JButton button = (JButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controlerPartie.controlBoutonsPressed(((JButton)e.getSource()));
			}
		}

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
			}
			public void mouseReleased(MouseEvent e) {
				JButton button = (JButton)e.getSource();
				Rectangle r = button.getBounds();
				//Apply pressed only if the release is on the pressed button
				if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
					(AffichagePartie.this).getContentPane().removeAll();
					for(Component c : components)
					{
						(AffichagePartie.this).getContentPane().add(c);
					}
					(AffichagePartie.this).repaint();
					(AffichagePartie.this).revalidate();
					//doitRevalidate=true;
				}
			}
		});

		this.repaint();
		this.revalidate();
		doitRevalidate=true;
	}

	public void update() {	
		if(controlerPartie.partie.getDisableBoutonsFin()){
			EnableBoutonsFin(false);
			panelPartie.removeAll();
		}

		if(controlerPartie.partie.setAffichageOption)
		{
			createOption();

		}
		controlerPartie.partie. resetVariablesAffichage();


	}

}
