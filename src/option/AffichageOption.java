package option;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Affichage.Drawable;
import gameConfig.InterfaceConstantes;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichageOption extends Drawable implements Observer{

	public CustomLabel lSon = new CustomLabel("SON");   

	public CustomLabel lReglageSon = new CustomLabel("Reglage son: ");  
	public JSlider lSonSlider =new JSlider();

	public CustomLabel lReglageBruitage = new CustomLabel("Reglage bruitage: ");  
	public JSlider lBruitageSlider =new JSlider();

	public CustomLabel lControle = new CustomLabel("CONTROLE"); 

	public CustomLabel lDepDroit = new CustomLabel("Droite: ") ; 
	public CustomClickableLabel tDepDroit;

	public CustomLabel lDepGauche = new CustomLabel("Gauche: ") ;  
	public CustomClickableLabel tDepGauche;

	public CustomLabel lSaut = new CustomLabel("Saut: ") ; 
	public CustomClickableLabel tSaut;

	public CustomLabel lTir = new CustomLabel("Tir: ") ; 
	public CustomClickableLabel tTir;

	public CustomLabel l2Tir = new CustomLabel("Tir secondaire: ") ; 
	public CustomClickableLabel t2Tir;
	
	public CustomLabel lSlow = new CustomLabel("Slow: ") ; 
	public CustomClickableLabel tSlow;

	public CustomLabel lPause = new CustomLabel("Pause: ") ; 
	public CustomClickableLabel tPause;


	public JButton retour = new JButton("Retour");

	public LinkedHashMap<String,JPanel> mapPanel = new LinkedHashMap<String,JPanel>();
	String[] nomMapPanel = {"SON","son","CONTROLE","droite","gauche","saut","tir","tir secondaire","slow","pause","retour"};

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
		super();
		controler=_controler;
		initComposant();
	}

	public void setSlider(JSlider slider,String name, int initValue)
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
		slider.setName(name);
	}

	public void setInputText()
	{
		Touches touch = controler.opt.touches;
		tDepDroit.setText(touch.ToString(touch.t_droite));
		tDepGauche.setText(touch.ToString(touch.t_gauche));
		tSaut.setText(touch.ToString(touch.t_saut));
		tTir.setText(touch.ToString(touch.t_tir));
		t2Tir.setText(touch.ToString(touch.t_2tir));
		tSlow.setText(touch.ToString(touch.t_slow));
		tPause.setText(touch.ToString(touch.t_pause));
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
			controler.opt.inputOption.init(p);
			//p.addKeyListener(in);
			for(Component c : p.getComponents())
			{
				c.addMouseListener(in);
				//controler.opt.inputOption.init(c);
				//c.addKeyListener(in);
			}
		}

		//On ajoute les listeners pour selectionner la touche à changer
		CustomClickableLabel[] cls = {tDepDroit,tDepGauche,tSaut,tTir,t2Tir,tSlow,tPause};
		for(CustomClickableLabel cl : cls ){
			cl.addMouseListener(new optionCliqueListener());
		}
	}

	public void removeListenerOption()
	{
		retour.removeMouseListener( retour.getMouseListeners()[retour.getMouseListeners().length-1]);
		lSonSlider.removeChangeListener(lSonSlider.getChangeListeners()[lSonSlider.getChangeListeners().length-1]);
		lBruitageSlider.removeChangeListener(lBruitageSlider.getChangeListeners()[lBruitageSlider.getChangeListeners().length-1]);

		for(JPanel p : mapPanel.values())
		{
			MouseListener[] ml = p.getMouseListeners();
			//KeyListener[] kl = p.getKeyListeners();
			
			p.removeMouseListener(ml[ml.length-1]);
			//p.removeKeyListener(kl[kl.length-1]);
			controler.opt.inputOption.reset(p);
			for(Component c : p.getComponents())
			{
				MouseListener[] cml = c.getMouseListeners();
				KeyListener[] ckl = c.getKeyListeners();

				c.removeMouseListener(cml[cml.length-1]);
				//c.removeKeyListener(ckl[ckl.length-1]);
			}
		}
		CustomClickableLabel[] cls = {tDepDroit,tDepGauche,tSaut,tTir,t2Tir,tSlow,tPause};
		for(CustomClickableLabel cl : cls ){
			MouseListener[] mls = cl.getMouseListeners();
			cl.removeMouseListener(mls[mls.length-1]);
		}
	}		

	public void initComposant()
	{
		Touches touch = controler.opt.touches;
		controler.opt.inputOption = new InputOption(controler,controler.opt.inputPartie);
		tDepDroit= new CustomClickableLabel(touch.ToString(touch.t_droite),"droite");
		tDepGauche= new CustomClickableLabel(touch.ToString(touch.t_gauche),"gauche");
		tSaut= new CustomClickableLabel(touch.ToString(touch.t_saut),"saut");
		tTir= new CustomClickableLabel(touch.ToString(touch.t_tir),"tir");
		t2Tir= new CustomClickableLabel(touch.ToString(touch.t_2tir),"tir secondaire");
		tSlow= new CustomClickableLabel(touch.ToString(touch.t_slow),"slow");
		tPause= new CustomClickableLabel(touch.ToString(touch.t_pause),"pause");

		retour = new JButton("Retour");

		//on régle les slideur 
		setSlider(lSonSlider,"son slider",(int) (Config.musicVolume*100));
		setSlider(lBruitageSlider,"bruitage slider",(int) (Config.bruitageVolume*100));

		//on rempli les panels pour option
		for(String s : nomMapPanel)
		{
			JPanel pan = new JPanel();
			pan.setName(s);
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
		
		JPanel panel2Tir = mapPanel.get("tir secondaire");
		panel2Tir.add(l2Tir);
		panel2Tir.add(t2Tir);
		
		JPanel panelSlow = mapPanel.get("slow");
		panelSlow.add(lSlow);
		panelSlow.add(tSlow);

		JPanel panelPause = mapPanel.get("pause");
		panelPause.add(lPause);
		panelPause.add(tPause);

		retour.setForeground(Color.white);
		mapPanel.get("retour").add(retour);

		this.getContentPane().setLayout(new GridLayout(11,1));
		this.getContentPane().setBackground(InterfaceConstantes.BACKGROUND_COLOR);
		this.getContentPane().setOpaque(false);
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
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) 
		{
			controler.opt.computationDone=false;
			JButton button = (JButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controler.opt.resetVariables();
				controler.controlRetourMenuPrincipal();
			}
			controler.opt.computationDone=true;
		}
	}



	public class optionReglageSonListener implements ChangeListener 
	{
		public void stateChanged(ChangeEvent event) 
		{
			controler.opt.computationDone=false;
			controler.opt.setVolumeMusique(event);
			controler.opt.computationDone=true;
		}
	}


	public class optionReglageBruitageListener implements ChangeListener 
	{
		public void stateChanged(ChangeEvent event) 
		{
			controler.opt.computationDone=false;
			controler.opt.setVolumeBruitage(event);
			controler.opt.computationDone=true;
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
			controler.opt.computationDone=false;
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
			controler.opt.computationDone=true;
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
			controler.opt.computationDone=false;
			//on vérifie que le clic de la souris est valide et qu'il y a une case selectionne
			controler.controlMouseInput(e);
			//Comme la touche a ete modifiée, plus aucune case n'est selectionne
			controler.opt.setCaseFocus(false);
			//On arrete le clignotement de la case
			controler.opt.blinkCustomClickableLabel();
			//on retire la case memorise
			controler.controlCustomClickableLabel(null);
			controler.opt.computationDone=true;
		}
		public void mouseReleased(MouseEvent e) {}

		public void keyPressed(KeyEvent e) 
		{
			/*//on vérifie que la touche du clavier est valide et qu'il y a une case selectionne
			controler.controlKeyboardInput(e);
			//Comme la touche a ete modifiée, plus aucune case n'est selectionne
			controler.opt.setCaseFocus(false);
			//On arrete le clignotement de la case
			controler.opt.blinkCustomClickableLabel();
			//on retire la case memorise
			controler.controlCustomClickableLabel(null);*/

		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}

	}
	
	@Override
	public void draw(Graphics g)
	{
		//Nothing to draw
		mainFrame.warnFadeOutCanStart();
	}

	@Override
	public void update() {		

		//message d'erreur lors d'un input invalide
		if(controler.opt.getShowInputError())
			JOptionPane.showMessageDialog(this.getContentPane(), "Touches autorisées: A-Z, 0-9, F1-F12, ESPACE, CTRL, SHIFT, BACKSPACE, ENTER, FLECHES, SOURIS" , "Erreur Saisie", JOptionPane.ERROR_MESSAGE);

		//Met à jour les touches
		if(controler.opt.getUpdateInputText())
			setInputText();

		//Remet à 0 les variables demandant d'updater un composant
		controler.opt.resetVariablesAffichage();
		this.getContentPane().repaint();
	}


}
