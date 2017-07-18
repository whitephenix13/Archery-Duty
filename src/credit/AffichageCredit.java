package credit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import menuPrincipal.AbstractModelPrincipal;

@SuppressWarnings("serial")
public class AffichageCredit extends JFrame{

	public CustomLabel lTitle = new CustomLabel("CREDITS",SwingConstants.CENTER);  
	public CustomLabel lCredit1 = new CustomLabel("Game creator:                                               Alexandre Lasbleis");  
	public CustomLabel lCredit2 = new CustomLabel("Developer:                                                    Alexandre Lasbleis");  
	public CustomLabel lCredit3 = new CustomLabel("Sound:                                                          Alexandre Lasbleis"); 
	public CustomLabel lCredit4 = new CustomLabel("Archer sprites:                       Tales of the World:Narikiri Dungeon 3 (GBA) (thanks to Grim)");  
	public CustomLabel lCredit5 = new CustomLabel("Other sprites:                                                Alexandre Lasbleis");  
	public CustomLabel lLicense= new CustomLabel("Archery Duty Copyright (C) 2017 Alexandre Lasbleis. "
			+ "This is free software, and you are welcome to redistribute it under certain conditions.  "
			+ "If you want to do so, contact me at: ArcheryDuty@protonmail.com");  

	public JButton retour = new JButton("Retour");

	public AffichageCredit()
	{
		initComposant();
	}
	//Utilisé pour les textes du menu option
	public class CustomLabel extends JLabel
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
			JButton button = (JButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				AbstractModelPrincipal.changeFrame=true;
				AbstractModelPrincipal.modeSuivant="Principal";
				AbstractModelPrincipal.changeMode=true;
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
		JPanel panel = new JPanel();

		JPanel panelNorth = new JPanel();
		JPanel panelCenter= new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panelSouth= new JPanel();
		
	    JPanel paneltitre = new JPanel();
	    JPanel panelcredit = new JPanel();
	    JPanel panelRetour = new JPanel();
	    JPanel panelLicense= new JPanel();
	    
	    panel.setBackground(Color.BLACK);

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

	    panel.setLayout(new BorderLayout());
	    panel.setLayout(new BorderLayout());
	    panel.add(panelNorth,BorderLayout.NORTH);
	    panel.add(panelCenter,BorderLayout.CENTER);
	    panel.add(panelSouth,BorderLayout.SOUTH);
	    this.getContentPane().add(panel);

	}
	public void update() {
		this.repaint();
	
	}
}
