package partie.modelPartie;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import ActiveJComponent.ActiveEmptyBorder;
import ActiveJComponent.ActiveJButton;
import ActiveJComponent.ActiveJFrame;
import ActiveJComponent.ActiveJPanel;
import Affichage.Drawable;
import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper.ObjectType;
import menu.menuPrincipal.ModelPrincipal;
import option.AbstractControlerOption;
import option.AbstractModelOption;
import option.AffichageOption;
import option.ControlerOption;
import option.ModelOption;
import option.Touches;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichagePartie extends Drawable implements Observer{

	protected MenuJButton bRejouer=new MenuJButton("Rejouer");
	protected MenuJButton bMenuPrincipal=new MenuJButton("Menu Principal");
	protected MenuJButton bMenuPrincipal2=new MenuJButton("Menu Principal");

	protected MenuJButton bReprendre= new MenuJButton("Reprendre");
	//autres buttons aussi utilisé 
	protected MenuJButton bOption=new MenuJButton("Option");
	protected MenuJButton bQuitter=new MenuJButton("Quitter");

	protected ArrowSlotButton[] bSlot1 = new ArrowSlotButton[4];
	protected ArrowSlotButton[] bSlot2 = new ArrowSlotButton[4];
	protected ArrowSlotButton[] bSlot3 = new ArrowSlotButton[4];
	protected ArrowSlotButton[] bSlot4 = new ArrowSlotButton[4];

	protected ActiveJPanel panelPauseY = new ActiveJPanel();
	protected ActiveJPanel panelPauseX = new ActiveJPanel();
	protected boolean firstTimePause=false;
	protected boolean firstTimeFin=false;

	protected ActiveJPanel panelFinX = new ActiveJPanel();
	protected ActiveJPanel panelFinY = new ActiveJPanel();

	private final int BARS_HEIGHT = 70;

	protected ActiveJPanel panelSlots = new ActiveJPanel();
	protected ActiveJPanel panelSlot1 = new ActiveJPanel();
	protected ActiveJPanel panelSlot2 = new ActiveJPanel();
	protected ActiveJPanel panelSlot3 = new ActiveJPanel();
	protected ActiveJPanel panelSlot4 = new ActiveJPanel();
	boolean initFlecheIcon = true;

	protected int SHIFT_VAL = 10; //value by which the slotPanel has to be lower to indicate that it was selected 
	protected int last_shifted = -1; //index of the selected slot 

	protected boolean doitRevalidate=false;
	AbstractControlerPartie controlerPartie;

	public AffichagePartie(AbstractControlerPartie _controlerPartie)
	{
		super();
		controlerPartie=_controlerPartie;
		initAffichage();
	}

	public void initAffichage()
	{

		panelPauseY.setLayout(new BoxLayout(panelPauseY,BoxLayout.Y_AXIS));
		panelPauseX.setLayout(new BoxLayout(panelPauseX,BoxLayout.X_AXIS));

		panelFinX.setLayout(new BoxLayout(panelFinX,BoxLayout.X_AXIS));
		panelFinY.setLayout(new BoxLayout(panelFinY,BoxLayout.Y_AXIS));

		panelSlots.setLayout(new BoxLayout(panelSlots,BoxLayout.X_AXIS));

		ActiveJPanel[] allpanelSlot = {panelSlot1,panelSlot2,panelSlot3,panelSlot4};
		
		int alignWithBar =10;

		for(int i=0; i<4;++i)
		{
			allpanelSlot[i].setLayout(new BoxLayout(allpanelSlot[i],BoxLayout.Y_AXIS));
			allpanelSlot[i].setAlignmentY( Component.TOP_ALIGNMENT );
			allpanelSlot[i].setBorder(new EmptyBorder(BARS_HEIGHT,i == 0 ? alignWithBar :0,0,0));
		}

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

		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		ArrowSlotButton[][] allbSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		
		for(int num=0; num<4; ++num)
			for(int i=0; i<bSlot1.length; ++i)
			{
				allbSlots[num][i] = new ArrowSlotButton(num);
				if(i>0)
				{
					allbSlots[num][i].setEnabled(false);
					allbSlots[num][i].setVisible(false);
				}
				allpanelSlot[num].add(allbSlots[num][i]);
			}

		panelSlots.add(panelSlot1);
		panelSlots.add(panelSlot2);
		panelSlots.add(panelSlot3);
		panelSlots.add(panelSlot4);
		panelSlots.add(Box.createHorizontalGlue());

		panelSlots.setOpaque(false);

		panelSlots.setFocusable(false);
		mainPanel.add(panelSlots);

		initFlecheIcon=true;

		//on utilise le content pane principal pour dessiner 
		mainPanel.setFocusable(true);
		mainPanel.requestFocusInWindow();
		mainPanel.setOpaque(false);
		
		//initialize input in order for them to be modifier by option
		controlerPartie.partie.inputPartie.init(mainPanel);

	}
	
	public int getFrame()
	{
		return controlerPartie.partie.getFrame();
	}
	
	@Override
	public void drawOnGraphics(Graphics g,boolean forceRepaint){
		//draw main game
		controlerPartie.partie.drawPartie(g);
	}


	public class MenuJButton extends ActiveJButton
	{
		public MenuJButton(String s)
		{
			super(s);
			this.setForeground(Color.WHITE);
			this.setBackground(Color.BLACK);
			this.setFont(new Font("Courrier",Font.PLAIN,44));
		}
	}

	public void updateSwingPartie()
	{
		
		//End game 
		if(controlerPartie.partie.finPartie && !firstTimePause)
		{
			doitRevalidate=true;
			firstTimePause=true;

			EnableBoutonsFin(true);

			mainPanel.removeAll();
			
			ActiveJPanel layerPan = new ActiveJPanel();
			layerPan.setOpaque(false);
			layerPan.setLayout(null);		
			panelFinY.setSize(InterfaceConstantes.WINDOW_WIDTH,InterfaceConstantes.WINDOW_HEIGHT);
			layerPan.add(panelFinY);
			layerPan.add(panelSlots);
			mainPanel.add(layerPan);

		}
		//Start pause
		else if(!controlerPartie.partie.finPartie &&controlerPartie.partie.inPause && !firstTimeFin)
		{
			doitRevalidate=true;
			firstTimeFin=true;

			EnableBoutonsPause(true);

			mainPanel.removeAll();
			
			ActiveJPanel layerPan = new ActiveJPanel();
			layerPan.setOpaque(false);
			layerPan.setLayout(null);		
			panelPauseX.setSize(InterfaceConstantes.WINDOW_WIDTH,InterfaceConstantes.WINDOW_HEIGHT);
			layerPan.add(panelPauseX);//panelPauseX
			layerPan.add(panelSlots);

			DisableAllSlotButton(true);
			mainPanel.add(layerPan);

		}
		//End pause


		//reset var 
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
			EnableBoutonsPause(false);
			mainPanel.removeAll();
			mainPanel.add(panelSlots);

			DisableAllSlotButton(false);
		}

		//if icon changed 
		if(controlerPartie.partie.arrowSlotIconChanged)
		{	
			if(initFlecheIcon){
				//retrieved all the image for the slots in correct order 
				ObjectType[] arrowsType1 = new ObjectType[4];  // name of the arrows in the slot 1 
				ObjectType[] arrowsType2 = new ObjectType[4];
				ObjectType[] arrowsType3 = new ObjectType[4];
				ObjectType[] arrowsType4 = new ObjectType[4];
				bSlot1=ArrowSlotButton.setIcons(bSlot1, controlerPartie.partie.imFlecheIcon.getAllImagesOfSameClass(controlerPartie.partie.heros.getSlots()[0],arrowsType1));
				bSlot2=ArrowSlotButton.setIcons(bSlot2, controlerPartie.partie.imFlecheIcon.getAllImagesOfSameClass(controlerPartie.partie.heros.getSlots()[1],arrowsType2));
				bSlot3=ArrowSlotButton.setIcons(bSlot3, controlerPartie.partie.imFlecheIcon.getAllImagesOfSameClass(controlerPartie.partie.heros.getSlots()[2],arrowsType3));
				bSlot4=ArrowSlotButton.setIcons(bSlot4, controlerPartie.partie.imFlecheIcon.getAllImagesOfSameClass(controlerPartie.partie.heros.getSlots()[3],arrowsType4));
				
				ArrowSlotButton.setArrowType(bSlot1, arrowsType1);
				ArrowSlotButton.setArrowType(bSlot2, arrowsType2);
				ArrowSlotButton.setArrowType(bSlot3, arrowsType3);
				ArrowSlotButton.setArrowType(bSlot4, arrowsType4);
				initFlecheIcon=false;
			}

			ActiveJPanel[] allpanelSlot = {panelSlot1,panelSlot2,panelSlot3,panelSlot4};
			int new_last_shited = -1;
			for(int i=0;i<4;i++)
			{
				if(last_shifted >=0 && (i==last_shifted))
				{
					if(allpanelSlot[i].getComponentCount()>0){
						allpanelSlot[i].remove(0);
					}
				}
				if(controlerPartie.partie.heros.current_slot == i)
				{
					if(allpanelSlot[i].getComponentCount()>0){
						allpanelSlot[i].add(Box.createRigidArea(new Dimension(0,SHIFT_VAL)),0);
						new_last_shited=i;
					}
				}
			}
			last_shifted = new_last_shited;
			controlerPartie.partie.arrowSlotIconChanged=false;
			//mainPanel.getLayout().layoutContainer(mainPanel);
		}

		mainPanel.requestFocus();


	}
	
	public boolean isLoadingDone()
	{
		return controlerPartie.partie.loaderPartie.isGameModeLoaded();
	}
	public Drawable getAffichageLoader()
	{
		return controlerPartie.partie.loaderPartie.getAffichageLoader();
	}
	
	public void requestGameFocus()
	{
		mainPanel.requestFocusInWindow();

	}
	public void validateAffichagePartie(ActiveJFrame affich)
	{
		if(doitRevalidate)
		{
			doitRevalidate=false;
			affich.revalidate();
		}

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
		bRejouer.setEnabled(enable);
		bRejouer.setVisible(enable);

		bMenuPrincipal2.setEnabled(enable);
		bMenuPrincipal2.setVisible(enable);
	}

	/**
	 * 
	 * @param enable
	 * @param button
	 * Used to open or close a slot 
	 */
	public void EnableSlotButton(boolean enable,ArrowSlotButton button)
	{
		ArrowSlotButton[][] allbSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		for(int i =1; i<4; ++i){
			allbSlots[button.slot][i].setEnabled(enable);
			allbSlots[button.slot][i].setVisible(enable);
		}		
	}

	/**
	 * Used when a click is done outside of any buttons 
	 */
	public void CloseAllSlots()
	{
		ArrowSlotButton[][] allbSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		for(int j=0; j<4;j++)
			if(allbSlots[j][0].choosingArrow){
				allbSlots[j][0].choosingArrow=false;
				for(int i =1; i<4; ++i){
					allbSlots[j][i].setEnabled(false);
					allbSlots[j][i].setVisible(false);
				}	
			}
	}

	/**
	 * 
	 * @param disable
	 * Used when in pause
	 */
	public void DisableAllSlotButton(boolean disable)
	{
		ArrowSlotButton[][] allbSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		for(int i =0; i<4; ++i){
			for(int j=0; j<bSlot1.length;j++)
			{
				allbSlots[i][j].setEnabled(!disable);
			}
		}
	}

	/**
	 * ajoute les listeners de PartieRapide
	 * 
	 * @param Affichage: la JFrame a afficher
	 */	

	public void addListenerPartie()
	{
		controlerPartie.partie.inputPartie.init(mainPanel);
		mainPanel.addMouseListener(new SourisListener());
		mainPanel.addMouseMotionListener(new SourisMotionListener());

		MenuJButton[] addMouse = {bRejouer,bOption,bReprendre,bQuitter,bMenuPrincipal,bMenuPrincipal2};
		for(MenuJButton mjb : addMouse)
			mjb.addMouseListener(new boutonsPrincipalListener());

		ArrowSlotButton[][] addSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		for(int i = 0; i< addSlots.length;++i)
			for(int j = 0; j<addSlots[0].length;++j)
			{
				addSlots[i][j].addMouseListener(new ArrowSlotListener());
			}
	}
	public void removeListenerPartie()
	{
		controlerPartie.partie.inputPartie.reset();
		mainPanel.removeMouseListener(mainPanel.getMouseListeners()[0]);
		mainPanel.removeMouseMotionListener(mainPanel.getMouseMotionListeners()[0]);

		MenuJButton[] removeMouse = {bRejouer,bOption,bReprendre,bQuitter,bMenuPrincipal,bMenuPrincipal2};
		for(MenuJButton mjb : removeMouse){
			MouseListener[] listeners = mjb.getMouseListeners();
			mjb.removeMouseListener(listeners[listeners.length-1]);
		}

		ArrowSlotButton[][] addSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		for(int i = 0; i< addSlots.length;++i)
			for(int j = 0; j<addSlots[0].length;++j)
			{
				MouseListener[] listeners = addSlots[i][j].getMouseListeners();
				addSlots[i][j].removeMouseListener(listeners[listeners.length-1]);
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
			
			CloseAllSlots();
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
			
			ActiveJButton button = (ActiveJButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controlerPartie.controlBoutonsPressed(((ActiveJButton)e.getSource()));
			}
			
		}

	}

	public class ArrowSlotListener implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent arg0) {
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if( (controlerPartie.partie.finPartie) || (!controlerPartie.partie.finPartie &&controlerPartie.partie.inPause))
				return;

			
			ArrowSlotButton[][] allSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
			ArrowSlotButton source_but = (ArrowSlotButton)e.getSource();

			int clickedSlot = source_but.slot;
			//If an arrow was clicked, switch it in the slot 
			//if not cliked on arrow 0 
			if(allSlots[clickedSlot][0] != source_but)
			{
				int source_but_index = -1; 
				for(int i=1; i<4;i++)
				{
					if(allSlots[clickedSlot][i] == source_but){
						source_but_index=i;
						break;
					}
				}

				//switch buttons properties
				ArrowSlotButton.switchButtonType(allSlots[clickedSlot][0],allSlots[clickedSlot][source_but_index]);

				//switch arrow for heros
				controlerPartie.partie.heros.changeSlot(controlerPartie.partie, clickedSlot, allSlots[clickedSlot][0].arrowType);
			}

			//if arrow i, set arrow i in the slot and then switch 0 and i in bSlot


			//Always consider the first arrow of the slot 
			ArrowSlotButton but = allSlots[clickedSlot][0];

			but.choosingArrow=!but.choosingArrow;
			EnableSlotButton(but.choosingArrow,but);


			//close all the other opened ones 
			if(but.choosingArrow)
			{
				for(int i=0;i<4;++i)
				{
					if(i != but.slot)
					{
						if(allSlots[i][0].choosingArrow)
						{
							allSlots[i][0].choosingArrow=false;
							EnableSlotButton(false,allSlots[i][0]);
						}
					}
				}
			}
			AffichagePartie.this.getActiveJFrame().pack();
			mainPanel.validate();
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}

	}

	public void createOption(Touches _touches)
	{
		AbstractModelOption option = new ModelOption(_touches,controlerPartie.partie.inputPartie,controlerPartie.partie.gameHandler);
		AbstractControlerOption controlerOption = new ControlerOption(option);
		final AffichageOption affichageOption = new AffichageOption(controlerOption);
		affichageOption.addListenerOption();
		affichageOption.retour.setContentAreaFilled(false);
		affichageOption.retour.removeMouseListener( affichageOption.retour.getMouseListeners()[1]);
		affichageOption.initFromOtherDrawable(this);
		option.addObserver(affichageOption);
		final Component[] components =this.getContentPane().getComponents();

		//this.getContentPane().removeAll();
		affichageOption.requestBeginTransition();//manually force the begin transition as we don't want to unload the active game 
		affichageOption.retour.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0) {}			
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) 
			{
			}
			public void mouseReleased(MouseEvent e) {
				ActiveJButton button = (ActiveJButton)e.getSource();
				Rectangle r = button.getBounds();
				//Apply pressed only if the release is on the pressed button
				if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
					(AffichagePartie.this).getContentPane().removeAll();
					for(Component c : components)
					{
						(AffichagePartie.this).getContentPane().add(c);
					}
					AffichagePartie.this.requestBeginTransition();
				}
			}
		});
		doitRevalidate=true;
	}

	private void onGameRestart()
	{
		mainPanel.removeAll();
		mainPanel.add(panelSlots);
		DisableAllSlotButton(false);
		initFlecheIcon=true;
	}

	public void update() {	

		boolean specialCase =false;
		if(controlerPartie.partie.getDisableBoutonsFin()){
			specialCase=true;
			EnableBoutonsFin(false);
			onGameRestart();
		}

		if(controlerPartie.partie.setAffichageOption)
		{
			specialCase=true;
			createOption(controlerPartie.partie.touches);

		}
		if(specialCase)
			controlerPartie.partie.resetVariablesAffichage();
		else
		{
			//Main update 
			updateSwingPartie();
			ModelPrincipal.debugTime.elapsed("repaint");


			//validateAffichagePartie(getFrame());
			
		}
	}
}


