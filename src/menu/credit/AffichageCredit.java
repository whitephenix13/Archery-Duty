package menu.credit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingConstants;

import ActiveJComponent.ActiveJButton;
import ActiveJComponent.ActiveJLabel;
import ActiveJComponent.ActiveJPanel;
import Affichage.GameRenderer;
import Affichage.Drawable;
import editeur.AffichageEditeur;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameHandler.GameModeType;
import menu.menuPrincipal.GameMode;

@SuppressWarnings("serial")
public class AffichageCredit extends Drawable implements GameMode{

	public CustomLabel lTitle = new CustomLabel("CREDITS",SwingConstants.CENTER);  
	public CustomLabel lCredit1 = new CustomLabel("Game creator:                                               Alexandre Lasbleis");  
	public CustomLabel lCredit2 = new CustomLabel("Developer:                                                    Alexandre Lasbleis");  
	public CustomLabel lCredit3 = new CustomLabel("Sound:                                                          Alexandre Lasbleis"); 
	public CustomLabel lCredit4 = new CustomLabel("Archer sprites:                       Tales of the World:Narikiri Dungeon 3 (GBA) (thanks to Grim)");  
	public CustomLabel lCredit5 = new CustomLabel("Other sprites:                                                Alexandre Lasbleis");  
	public CustomLabel lLicense= new CustomLabel("Archery Duty Copyright (C) 2017 Alexandre Lasbleis. "
			+ "This is free software, and you are welcome to redistribute it under certain conditions.  "
			+ "If you want to do so, contact me at: ArcheryDuty@protonmail.com");  

	public ActiveJButton retour = new ActiveJButton("Retour");
	
	private GameHandler gameHandler;
	public AffichageCredit(GameHandler gameHandler)
	{
		super();
		this.gameHandler = gameHandler;
		initComposant();
	}
	//Utilisé pour les textes du menu option
	public class CustomLabel extends ActiveJLabel
	{
		public CustomLabel(String s)
		{
			this(s,SwingConstants.LEFT);
		}
		public CustomLabel(String s,int constant)
		{
			super(s,constant);
			this.setForeground(Color.white);
			this.setText(s);
		}
	}
	public void addListenerCredit()
	{
	    retour.addMouseListener(new retourOptionListener());
	}
	public class retourOptionListener implements MouseListener
	{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) 
		{
			ActiveJButton button = (ActiveJButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				gameHandler.setGameMode(GameModeType.MAIN_MENU);
			}
		}
	}
	public void removeListenerCredit()
	{
		MouseListener[] ml = retour.getMouseListeners();
	    retour.removeMouseListener( ml[ml.length-1]);
	}
	public void initComposant()
	{
		ActiveJPanel panelNorth = new ActiveJPanel();
		ActiveJPanel panelCenter= new ActiveJPanel(new FlowLayout(FlowLayout.LEFT));
		ActiveJPanel panelSouth= new ActiveJPanel();
		
	    ActiveJPanel paneltitre = new ActiveJPanel();
	    ActiveJPanel panelcredit = new ActiveJPanel();
	    ActiveJPanel panelRetour = new ActiveJPanel();
	    ActiveJPanel panelLicense= new ActiveJPanel();
	    
	    mainPanel.setBackground(Color.BLACK);

	    panelNorth.setBackground(Color.BLACK);
	    panelCenter.setBackground(Color.BLACK);
	    panelSouth.setBackground(Color.BLACK);
	    paneltitre.setBackground(Color.BLACK);
	    panelcredit.setBackground(Color.BLACK);
	    panelRetour.setBackground(Color.BLACK);
	    panelLicense.setBackground(Color.BLACK);

	    lTitle.setPreferredSize(new Dimension(400,200));
	    lTitle.setFont(new Font(lTitle.getFont().getName(), Font.PLAIN, 50));
	    paneltitre.add(lTitle);
	    panelNorth.add(paneltitre);
	    
	    lCredit1.setPreferredSize(new Dimension(400,60));
	    lCredit1.setFont(new Font(lCredit1.getFont().getName(), Font.PLAIN, 30));
	    lCredit2.setFont(new Font(lCredit2.getFont().getName(), Font.PLAIN, 30));
	    lCredit3.setFont(new Font(lCredit3.getFont().getName(), Font.PLAIN, 30));
	    lCredit4.setFont(new Font(lCredit4.getFont().getName(), Font.PLAIN, 30));
	    lCredit5.setFont(new Font(lCredit5.getFont().getName(), Font.PLAIN, 30));
	    
	    panelcredit.add(lCredit1);
	    panelcredit.add(lCredit2);
	    panelcredit.add(lCredit3);
	    panelcredit.add(lCredit4);
	    panelcredit.add(lCredit5);
	    panelcredit.setLayout(new GridLayout(5,1));
	    panelCenter.add(panelcredit);
	    
	    retour.setPreferredSize(new Dimension(100, 30));
	    retour.setForeground(Color.white);
	    retour.setBackground(Color.BLACK);
	    
	    panelRetour.add(retour);
	    panelSouth.add(panelRetour);
	    
	    panelLicense.add(lLicense);
	    panelSouth.add(panelLicense);
	    panelSouth.setLayout(new GridLayout(2,1));

	    mainPanel.setLayout(new BorderLayout());
	    mainPanel.setLayout(new BorderLayout());
	    mainPanel.add(panelNorth,BorderLayout.NORTH);
	    mainPanel.add(panelCenter,BorderLayout.CENTER);
	    mainPanel.add(panelSouth,BorderLayout.SOUTH);

	}
	
	public void doComputations(GameRenderer affich){
		//As this mode is controlled by listeners, the computationDone is set to false when a listener is triggered. This function is then left empty
	}
	public void updateSwing(){
	}

	@Override
	public boolean isGameModeLoaded()
	{
		//loading not required 
		return true;
	}
	@Override
	public GameMode getLoaderGameMode(){
		//loading not required 
		return null;
	}
	
	@Override
	public void drawOnGraphics(Graphics g,boolean forceRepaint)
	{
		//Nothing to draw
	}
	
	public void update() {	
	}
}
