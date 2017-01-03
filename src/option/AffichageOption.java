package option;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import observer.Observer;
import principal.InterfaceConstantes;
import types.Touches;

@SuppressWarnings("serial")
public class AffichageOption extends JFrame implements Observer{

	public CustomLabel lSon = new CustomLabel("SON");   

	public CustomLabel lReglageSon = new CustomLabel("Reglage son: ");  
	public JSlider lSonSlider =new JSlider();
	
	public CustomLabel lReglageBruitage = new CustomLabel("Reglage bruitage: ");  
	public JSlider lBruitageSlider =new JSlider();
	
	public CustomLabel lControle = new CustomLabel("CONTROLE"); 
	
	public CustomLabel lDepDroit = new CustomLabel("Droite: ") ; 
	public CustomClickableLabel tDepDroit= new CustomClickableLabel(Touches.ToString(Touches.t_droite),"droite");
	
	public CustomLabel lDepGauche = new CustomLabel("Gauche: ") ;  
	public CustomClickableLabel tDepGauche= new CustomClickableLabel(Touches.ToString(Touches.t_gauche),"gauche");
	
	public CustomLabel lSaut = new CustomLabel("Saut: ") ; 
	public CustomClickableLabel tSaut= new CustomClickableLabel(Touches.ToString(Touches.t_saut),"saut");
	
	public CustomLabel lTir = new CustomLabel("Tir: ") ; 
	public CustomClickableLabel tTir= new CustomClickableLabel(Touches.ToString(Touches.t_tir),"tir");
	
	public CustomLabel lSlow = new CustomLabel("Slow: ") ; 
	public CustomClickableLabel tSlow= new CustomClickableLabel(Touches.ToString(Touches.t_slow),"slow");
	
	public CustomLabel lPause = new CustomLabel("Pause: ") ; 
	public CustomClickableLabel tPause= new CustomClickableLabel(Touches.ToString(Touches.t_pause),"pause");
	
	
	public JButton retour = new JButton("Retour");
	
	public LinkedHashMap<String,JPanel> mapPanel = new LinkedHashMap<String,JPanel>();
	String[] nomMapPanel = {"SON","son","CONTROLE","droite","gauche","saut","tir","slow","pause","retour"};
	
	private AbstractControlerOption controler;

	//Utilisé pour les textes du menu option
	public class CustomLabel extends JLabel
	{
		public CustomLabel(String s)
		{
			super();
			this.setForeground(Color.white);
			this.setText(s);
		}
	}
	
	//utilisé pour le choix des touches dans le menu option
	public class CustomClickableLabel extends JLabel
	{
		public CustomClickableLabel(String text, String name)
		{
			super();
		    this.setPreferredSize(new Dimension(100,20));
		    this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			this.setOpaque(true);
			this.setBackground(Color.white);
			this.setForeground(Color.black);
			this.setText(text);
			this.setName(name);
		}
	}
		
	public AffichageOption(AbstractControlerOption _controler)
	{
		controler=_controler;
		initComposant();
	}
	
	public void setSlider(JSlider slider, int initValue)
	{
		slider.setMaximum(100);
		slider.setMinimum(0);
		slider.setValue(initValue);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(10);
		slider.setMajorTickSpacing(20);
		slider.setBackground(Color.BLACK);
		slider.setForeground(Color.WHITE);
	}
	
	public void setInputText()
	{
	
		    tDepDroit.setText(Touches.ToString(Touches.t_droite));
		    tDepGauche.setText(Touches.ToString(Touches.t_gauche));
		    tSaut.setText(Touches.ToString(Touches.t_saut));
		    tTir.setText(Touches.ToString(Touches.t_tir));
		    tSlow.setText(Touches.ToString(Touches.t_slow));
		    tPause.setText(Touches.ToString(Touches.t_pause));
	}
	public void addListenerOption()
	{

		//on règle le boutton retour 
	    retour.addMouseListener(new retourOptionListener());
	    lSonSlider.addChangeListener(new optionReglageSonListener());
	    lBruitageSlider.addChangeListener(new optionReglageBruitageListener());
	    
	    //On ajoute les listeners pour changer les touches 
	    inputListeners in = new inputListeners();
	    for(JPanel p : mapPanel.values())
	    {
	    	p.addMouseListener(in);
	    	p.addKeyListener(in);
	    	for(Component c : p.getComponents())
	    	{
	    		c.addMouseListener(in);
	    		c.addKeyListener(in);
	    	}
	    }
	    
	    //On ajoute les listeners pour selectionner la touche à changer
	    tDepDroit.addMouseListener(new optionCliqueListener());
	    tDepGauche.addMouseListener(new optionCliqueListener());
	    
	    tSaut.addMouseListener(new optionCliqueListener());
	    
	    tTir.addMouseListener(new optionCliqueListener());
	    
	    tSlow.addMouseListener(new optionCliqueListener());
	    
	    tPause.addMouseListener(new optionCliqueListener());
	    

	}
	
	public void removeListenerOption()
	{
	    retour.removeMouseListener( retour.getMouseListeners()[1]);
	    lSonSlider.removeChangeListener(lSonSlider.getChangeListeners()[0]);
	    lBruitageSlider.removeChangeListener(lBruitageSlider.getChangeListeners()[0]);
	    
	    for(JPanel p : mapPanel.values())
	    {
	    	p.removeMouseListener(p.getMouseListeners()[0]);
	    	p.removeKeyListener(p.getKeyListeners()[0]);
	    	for(Component c : p.getComponents())
	    	{
		    	c.removeMouseListener(c.getMouseListeners()[0]);
		    	c.removeKeyListener(c.getKeyListeners()[0]);
	    	}
	    }
	    	tDepDroit.removeMouseListener(tDepDroit.getMouseListeners()[0]);
		    tDepGauche.removeMouseListener(tDepGauche.getMouseListeners()[0]);
		    tSaut.removeMouseListener(tSaut.getMouseListeners()[0]);
		    tTir.removeMouseListener(tTir.getMouseListeners()[0]);
		    tSlow.removeMouseListener(tSlow.getMouseListeners()[0]);
		    tPause.removeMouseListener(tPause.getMouseListeners()[0]);
	}		
		
	public void initComposant()
	{
		

		//on régle les slideur 
		setSlider(lSonSlider,(int) (InterfaceConstantes.valeurSonInit*100));
		setSlider(lBruitageSlider,(int) (InterfaceConstantes.valeurBruitageInit*100));
	    
	  //on rempli les panels pour option
	    for(String s : nomMapPanel)
	    {
	    	JPanel pan = new JPanel();
		    pan.setBackground(Color.BLACK);
	    	mapPanel.put(s, pan);
	    }
	    
	     mapPanel.get("SON").add(lSon);

	     JPanel panelSon = mapPanel.get("son");
	     panelSon.add(lReglageSon);
	     panelSon.add(lSonSlider);
	     panelSon.add(lReglageBruitage);
	     panelSon.add(lBruitageSlider);
	     
	     mapPanel.get("CONTROLE").add(lControle);
	     
	     JPanel panelDroit = mapPanel.get("droite");
	     panelDroit.add(lDepDroit);
	     panelDroit.add(tDepDroit);
	     
	     JPanel panelGauche = mapPanel.get("gauche");
	     panelGauche.add(lDepGauche);
	     panelGauche.add(tDepGauche);
	     
	     JPanel panelSaut = mapPanel.get("saut");
	     panelSaut.add(lSaut);
	     panelSaut.add(tSaut);
	     
	     JPanel panelTir = mapPanel.get("tir");
	     panelTir.add(lTir);
	     panelTir.add(tTir);
	     
	     JPanel panelSlow = mapPanel.get("slow");
	     panelSlow.add(lSlow);
	     panelSlow.add(tSlow);
	     
	     JPanel panelPause = mapPanel.get("pause");
	     panelPause.add(lPause);
	     panelPause.add(tPause);
	     
	     retour.setForeground(Color.white);
	     mapPanel.get("retour").add(retour);
	     
	     this.getContentPane().setLayout(new GridLayout(10,1));
	     for(JPanel pan : mapPanel.values())
		   {
			   this.getContentPane().add(pan);
		   }
	     
	}
 
	public class retourOptionListener implements MouseListener
	{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			controler.opt.resetVariables();
			controler.controlRetourMenuPrincipal();
		}
		public void mouseReleased(MouseEvent e) {}
	}
		
	   

	public class optionReglageSonListener implements ChangeListener 
	{
		public void stateChanged(ChangeEvent event) 
		{
			controler.opt.setVolumeMusique(event);
		}
	}
		
	
	public class optionReglageBruitageListener implements ChangeListener 
	{
		public void stateChanged(ChangeEvent event) 
		{
			controler.opt.setVolumeBruitage(event);

		}
	}
		
	/*
	 * Listener permettant de selectionner une case et de la faire clignoter
	 * */
	public class optionCliqueListener implements MouseListener
	{
		public void mouseClicked(MouseEvent e) {	
		}
		public void mouseEntered(MouseEvent e) {	
		}
		public void mouseExited(MouseEvent e) {	
		}
		public void mousePressed(MouseEvent e) {
			//Aucune case selectionnee
			if(!controler.opt.getCaseFocus())
			{
				//Une informe qu'une case est selectionne
				controler.opt.setCaseFocus(!controler.opt.getCaseFocus());
				//On mémorise la case
				controler.controlCustomClickableLabel((CustomClickableLabel)e.getSource());
				//on la fait clignoter
				controler.opt.blinkCustomClickableLabel();

			}
			//CHOIX: si l'utilisateur reclic sur la case en question, deux événements sont lancés: 
			// inputListener et optionCliqueListener (dans cet ordre) ce qui fait que la case est reselectionnée.
		}
		public void mouseReleased(MouseEvent e) {	
		}
		
	}
		
	/*
	 * Listener permettant de mémoriser la touche du clavier ou le clic souris correspondant à la touche qu'on veut modifier
	 * */
	public class inputListeners implements KeyListener, MouseListener
	{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) 
		{
			//on vérifie que le clic de la souris est valide et qu'il y a une case selectionne
			controler.controlMouseInput(e);
			//Comme la touche a ete modifiée, plus aucune case n'est selectionne
			controler.opt.setCaseFocus(false);
			//On arrete le clignotement de la case
			controler.opt.blinkCustomClickableLabel();
			//on retire la case memorise
			controler.controlCustomClickableLabel(null);

		}
		public void mouseReleased(MouseEvent e) {}

		public void keyPressed(KeyEvent e) 
		{
			//on vérifie que la touche du clavier est valide et qu'il y a une case selectionne
			controler.controlKeyboardInput(e);
			//Comme la touche a ete modifiée, plus aucune case n'est selectionne
			controler.opt.setCaseFocus(false);
			//On arrete le clignotement de la case
			controler.opt.blinkCustomClickableLabel();
			//on retire la case memorise
			controler.controlCustomClickableLabel(null);

		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		
	}
		
		
	@Override
	public void update() {		
	
		//message d'erreur lors d'un input invalide
		if(controler.opt.getShowInputError())
			JOptionPane.showMessageDialog(this, "Touches autorisées: A-Z, 0-9, F1-F12, ESPACE, CTRL, SHIFT, BACKSPACE, ENTER, FLECHES, SOURIS" , "Erreur Saisie", JOptionPane.ERROR_MESSAGE);
	
		//Met à jour les touches
		if(controler.opt.getUpdateInputText())
			setInputText();
		
		//Remet à 0 les variables demandant d'updater un composant
		controler.opt.resetVariablesAffichage();
	}


}
