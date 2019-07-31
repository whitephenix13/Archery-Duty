package option;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ActiveJComponent.ActiveJButton;
import ActiveJComponent.ActiveJLabel;
import ActiveJComponent.ActiveJOptionPane;
import ActiveJComponent.ActiveJPanel;
import ActiveJComponent.ActiveJScrollPane;
import ActiveJComponent.ActiveJSlider;
import Affichage.Drawable;
import gameConfig.InterfaceConstantes;
import images.ImagesBackground.ImBackgroundInfo;
import images.ImagesContainer.ImageGroup;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichageOption extends Drawable implements Observer{

	public CustomLabel lSon = new CustomLabel("SON");   

	public CustomLabel lReglageSon = new CustomLabel("Reglage son: ");  
	public ActiveJSlider lSonSlider =new ActiveJSlider();

	public CustomLabel lReglageBruitage = new CustomLabel("Reglage bruitage: ");  
	public ActiveJSlider lBruitageSlider =new ActiveJSlider();

	protected ActiveJScrollPane keysScrollPane;
	protected ActiveJPanel keysPanel;

	public CustomLabel lControle = new CustomLabel("CONTROLE"); 

	public CustomLabel lDepDroit = new CustomLabel("Droite: ") ; 
	public CustomClickableLabel tDepDroit;

	public CustomLabel lDepGauche = new CustomLabel("Gauche: ") ;  
	public CustomClickableLabel tDepGauche;

	public CustomLabel lSaut = new CustomLabel("Saut: ") ; 
	public CustomClickableLabel tSaut;

	public CustomLabel[] lTir = {new CustomLabel("Tir: "),new CustomLabel("Special tir 1: "),new CustomLabel("Special tir 2: "),
			new CustomLabel("Special tir 3: "),new CustomLabel("Special tir 4: ")}; 
	public CustomClickableLabel[] tTir;

	public CustomLabel[] lSlot = {new CustomLabel("Slot 1: "),new CustomLabel("Slot 2: "),
			new CustomLabel("Slot 3: "),new CustomLabel("Slot 4: ")}; 
	public CustomClickableLabel[] tSlot;
	
	public CustomLabel lDash = new CustomLabel("Dash: ") ; 
	public CustomClickableLabel tDash;

	public CustomLabel lSlow = new CustomLabel("Slow: ") ; 
	public CustomClickableLabel tSlow;

	public CustomLabel lPause = new CustomLabel("Pause: ") ; 
	public CustomClickableLabel tPause;


	public ActiveJButton retour = new ActiveJButton("Retour");

	public LinkedHashMap<String,JComponent> mapPanel = new LinkedHashMap<String,JComponent>();
	String[] keysMapName = {"SON","son","CONTROLE","keys","retour"};
	private AbstractControlerOption controler;

	//Utilisé pour les textes du menu option
	public class CustomLabel extends ActiveJLabel
	{
		public CustomLabel(String s)
		{
			super();
			this.setForeground(Color.white);
			this.setText(s);
		}
	}

	//utilisé pour le choix des touches dans le menu option
	public class CustomClickableLabel extends ActiveJLabel
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

	public void setSlider(ActiveJSlider slider,String name, int initValue)
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
		for(int i=0;i<tTir.length;++i)
			tTir[i].setText(touch.ToString(touch.t_tir[i]));
		for(int i=0;i<tSlot.length;++i)
			tSlot[i].setText(touch.ToString(touch.t_slot[i]));
		tDash.setText(touch.ToString(touch.t_dash));
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
		for(JComponent p : mapPanel.values())
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
		ArrayList<CustomClickableLabel> cls = new ArrayList<CustomClickableLabel>();
		cls.addAll(Arrays.asList(new CustomClickableLabel[]{tDepDroit,tDepGauche,tSaut,tDash}));
		cls.addAll(Arrays.asList(tTir));
		cls.addAll(Arrays.asList(tSlot));
		cls.addAll(Arrays.asList(new CustomClickableLabel[]{tSlow,tPause}));
		
		for(CustomClickableLabel cl : cls ){
			cl.addMouseListener(new optionCliqueListener());
		}
	}

	public void removeListenerOption()
	{
		retour.removeMouseListener( retour.getMouseListeners()[retour.getMouseListeners().length-1]);
		lSonSlider.removeChangeListener(lSonSlider.getChangeListeners()[lSonSlider.getChangeListeners().length-1]);
		lBruitageSlider.removeChangeListener(lBruitageSlider.getChangeListeners()[lBruitageSlider.getChangeListeners().length-1]);

		for(JComponent p : mapPanel.values())
		{
			MouseListener[] ml = p.getMouseListeners();
			
			p.removeMouseListener(ml[ml.length-1]);
			controler.opt.inputOption.reset(p);
			for(Component c : p.getComponents())
			{
				MouseListener[] cml = c.getMouseListeners();
				KeyListener[] ckl = c.getKeyListeners();

				c.removeMouseListener(cml[cml.length-1]);
			}
		}
		ArrayList<CustomClickableLabel> cls = new ArrayList<CustomClickableLabel>();
		cls.addAll(Arrays.asList(new CustomClickableLabel[]{tDepDroit,tDepGauche,tSaut,tDash}));
		cls.addAll(Arrays.asList(tTir));
		cls.addAll(Arrays.asList(tSlot));
		cls.addAll(Arrays.asList(new CustomClickableLabel[]{tSlow,tPause}));
		for(CustomClickableLabel cl : cls ){
			MouseListener[] mls = cl.getMouseListeners();
			cl.removeMouseListener(mls[mls.length-1]);
		}
	}		
	
	private ActiveJPanel createComponentPanel()
	{
		return createComponentPanel("");
	}
	private ActiveJPanel createComponentPanel(String s)
	{
		ActiveJPanel pan = new ActiveJPanel();
		pan.setName(s);
		pan.setBackground(Color.BLACK);
		return pan;
	}
	
	public void initComposant()
	{
		Touches touch = controler.opt.touches;
		controler.opt.inputOption = new InputOption(controler,controler.opt.inputPartie);
		tDepDroit= new CustomClickableLabel(touch.ToString(touch.t_droite),"droite");
		tDepGauche= new CustomClickableLabel(touch.ToString(touch.t_gauche),"gauche");
		tSaut= new CustomClickableLabel(touch.ToString(touch.t_saut),"saut");
		tTir = new CustomClickableLabel[5];
		for(int i=0; i<lTir.length;++i)
			tTir[i]= new CustomClickableLabel(touch.ToString(touch.t_tir[i]),i==0?"tir":("special tir "+i));
		tSlot = new CustomClickableLabel[4];
		for(int i=0; i<lSlot.length;++i)
			tSlot[i]= new CustomClickableLabel(touch.ToString(touch.t_slot[i]),"slot "+i);
		tDash= new CustomClickableLabel(touch.ToString(touch.t_dash),"dash");
		tSlow= new CustomClickableLabel(touch.ToString(touch.t_slow),"slow");
		tPause= new CustomClickableLabel(touch.ToString(touch.t_pause),"pause");

		retour = new ActiveJButton("Retour");

		//on régle les slideur 
		setSlider(lSonSlider,"son slider",(int) (Config.musicVolume*100));
		setSlider(lBruitageSlider,"bruitage slider",(int) (Config.bruitageVolume*100));

		//on rempli les panels pour option
		for(String s : keysMapName)
		{
			if(s.equals("keys")){
				keysPanel = new ActiveJPanel();
				keysPanel.setLayout(new BoxLayout(keysPanel,BoxLayout.Y_AXIS));
				keysPanel.setBackground(Color.black);
				keysScrollPane = new ActiveJScrollPane(keysPanel);
				keysScrollPane.setName(s);
				mapPanel.put(s, keysScrollPane);
			}
			else{
				mapPanel.put(s, createComponentPanel(s));
			}
		}

		mapPanel.get("SON").add(lSon);

		ActiveJPanel panelSon = (ActiveJPanel)mapPanel.get("son");
		panelSon.add(lReglageSon);
		panelSon.add(lSonSlider);
		panelSon.add(lReglageBruitage);
		panelSon.add(lBruitageSlider);

		mapPanel.get("CONTROLE").add(lControle);
		
		ArrayList<CustomLabel> keysLabel = new ArrayList<CustomLabel>();
		keysLabel.addAll(Arrays.asList(new CustomLabel[]{lDepDroit,lDepGauche,lSaut,lDash}));
		keysLabel.addAll(Arrays.asList(lTir));
		keysLabel.addAll(Arrays.asList(lSlot));
		keysLabel.addAll(Arrays.asList(new CustomLabel[]{lSlow,lPause}));

		ArrayList<CustomClickableLabel> keysClickableLabel = new ArrayList<CustomClickableLabel>();
		keysClickableLabel.addAll(Arrays.asList(new CustomClickableLabel[]{tDepDroit,tDepGauche,tSaut,tDash}));
		keysClickableLabel.addAll(Arrays.asList(tTir));
		keysClickableLabel.addAll(Arrays.asList(tSlot));
		keysClickableLabel.addAll(Arrays.asList(new CustomClickableLabel[]{tSlow,tPause}));
		
		for(int i=0; i< keysLabel.size(); ++i)
		{
			ActiveJPanel panelKey = createComponentPanel();
			panelKey.add(keysLabel.get(i));
			panelKey.add(keysClickableLabel.get(i));
			keysPanel.add(panelKey);
		}
		

		retour.setForeground(Color.white);
		mapPanel.get("retour").add(retour);

		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		this.getContentPane().setBackground(InterfaceConstantes.BACKGROUND_COLOR);
		this.getContentPane().setOpaque(false);
		for(Container pan : mapPanel.values())
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
			ActiveJButton button = (ActiveJButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controler.opt.resetVariables();
				controler.controlRetourMenuPrincipal();
			}
		}
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
	public void drawOnGraphics(Graphics g,boolean forceRepaint)
	{
		g.drawImage(controler.opt.gameHandler.getImage(ImageGroup.BACKGROUND, null, ImBackgroundInfo.BLACK, null),0,0,null);
	}

	@Override
	public void update() {		

		//message d'erreur lors d'un input invalide
		if(controler.opt.getShowInputError())
			ActiveJOptionPane.showMessageDialog(this.getContentPane(), "Touches autorisées: A-Z, 0-9, F1-F12, ESPACE, CTRL, SHIFT, BACKSPACE, ENTER, FLECHES, SOURIS" , "Erreur Saisie", ActiveJOptionPane.ERROR_MESSAGE);

		//Met à jour les touches
		if(controler.opt.getUpdateInputText())
			setInputText();

		//Remet à 0 les variables demandant d'updater un composant
		controler.opt.resetVariablesAffichage();
	}


}
