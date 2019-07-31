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
import menu.menuPrincipal.GameHandler.GameModeType;
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
	private int slotOpened = -1;
	
	protected boolean doitRevalidate=false;
	private ArrowSlotListener arrowSlotListener;
	private SourisListener sourisListener;
	private SourisMotionListener sourisMotionListener;
	private BoutonsPrincipalListener boutonPrincipalListener;
	AbstractControlerPartie controlerPartie;

	public AffichagePartie(AbstractControlerPartie _controlerPartie)
	{
		super();
		isSelfClearingBackBuffer=true;
		controlerPartie=_controlerPartie;
		arrowSlotListener = new ArrowSlotListener();
		sourisListener = new SourisListener();
		sourisMotionListener = new SourisMotionListener();
		boutonPrincipalListener = new BoutonsPrincipalListener();
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
				allbSlots[num][i] = new ArrowSlotButton(controlerPartie.partie.touches,num);
				if(i>0)
				{
					allbSlots[num][i].removeMouseListener(arrowSlotListener);
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

			disableOrEnableAllSlotButton(true);
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

			disableOrEnableAllSlotButton(false);
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

	private void showOrHideSlotButton(boolean show,ArrowSlotButton button)
	{
		if(show!=button.isEnabled())
			if(show){
				button.addMouseListener(arrowSlotListener);
			}
			else{
				button.removeMouseListener(arrowSlotListener);
			}
		button.setEnabled(show);				
		button.setVisible(show);
	}

	/**
	 * 
	 * @param enable
	 * @param button
	 * Used to open or close a slot 
	 */
	public void showOrHideSlot(boolean show,ArrowSlotButton button)
	{
		ArrowSlotButton[][] allbSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		for(int i =1; i<4; ++i){
			showOrHideSlotButton(show,allbSlots[button.slot][i]);
		}		
	}

	/**
	 * Used when a click is done outside of any buttons 
	 */
	public void CloseAllSlots()
	{
		boolean choosingArrow = slotOpened != -1;
		ArrowSlotButton[][] allbSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		if(choosingArrow){
			//set the chose arrow at pos 0 equippedArrowInSlot
			ArrowSlotButton.switchButtonType(allbSlots[slotOpened][equippedArrowInSlot(slotOpened)],allbSlots[slotOpened][0]);
			for(int i =1; i<4; ++i){
				allbSlots[slotOpened][i].setEnabled(false);
				allbSlots[slotOpened][i].setVisible(false);
			}	
		}
		slotOpened=-1;
	}

	/**
	 * 
	 * @param disable
	 * Used when in pause
	 */
	public void disableOrEnableAllSlotButton(boolean disable)
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
		mainPanel.addMouseListener(sourisListener);
		mainPanel.addMouseMotionListener(sourisMotionListener);

		MenuJButton[] addMouse = {bRejouer,bOption,bReprendre,bQuitter,bMenuPrincipal,bMenuPrincipal2};
		for(MenuJButton mjb : addMouse)
			mjb.addMouseListener(boutonPrincipalListener);

		ArrowSlotButton[][] addSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		for(int i = 0; i< addSlots.length;++i)
			addSlots[i][0].addMouseListener(arrowSlotListener);
	}
	public void removeListenerPartie()
	{
		controlerPartie.partie.inputPartie.reset();
		mainPanel.removeMouseListener(sourisListener);
		mainPanel.removeMouseMotionListener(sourisMotionListener);

		MenuJButton[] removeMouse = {bRejouer,bOption,bReprendre,bQuitter,bMenuPrincipal,bMenuPrincipal2};
		for(MenuJButton mjb : removeMouse){
			mjb.removeMouseListener(boutonPrincipalListener);
		}

		ArrowSlotButton[][] addSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		for(int i = 0; i< addSlots.length;++i)
			for(int j = 0; j<addSlots[0].length;++j)
			{
				addSlots[i][j].removeMouseListener(arrowSlotListener);
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

	public class BoutonsPrincipalListener implements MouseListener 
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
	
	private int equippedArrowInSlot(int slotIndex)
	{
		ArrowSlotButton[][] allSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		ObjectType targetArrowType = controlerPartie.partie.heros.getSlots()[slotIndex];
		for(int i=0; i<allSlots[slotIndex].length;++i){
			if(allSlots[slotIndex][i].arrowType.equals(targetArrowType))
				return i;
		}
		return -1;
	}
	
	private void changeArrowSlot(ArrowSlotButton source_but)
	{
		if( (controlerPartie.partie.finPartie) || (!controlerPartie.partie.finPartie &&controlerPartie.partie.inPause))
			return;

		ArrowSlotButton[][] allSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
		int clickedSlot = source_but.slot;

		boolean isSlotOpened = slotOpened != -1;
		//case 1: no slot opened and clicked on button corresponding to equippedArrow in slot i. If the slot is closed, equipped arrow should be in position 0
		if(!isSlotOpened)
		{
			//open slot 
			showOrHideSlot(true,source_but);
			//Reorganize the list so that the icons always show in the same order when opened
			for(int i=0; i<allSlots[clickedSlot].length;++i){
				int but_original_position = allSlots[clickedSlot][i].original_position;
				if( but_original_position!= i)
					ArrowSlotButton.switchButtonType(allSlots[clickedSlot][i],allSlots[clickedSlot][but_original_position]);
			}
			
			slotOpened=clickedSlot;
		}
		//case 2: slot i opened and clicked on button corresponding to equippedArrow in slot i
		else if(isSlotOpened && clickedSlot==slotOpened)
		{			
			//Only change the arrow of the hero if it is different 
			int equippedArrowInSlot = equippedArrowInSlot(clickedSlot);
			if(allSlots[clickedSlot][equippedArrowInSlot]!=source_but){
				//change the arrow for the heros
				controlerPartie.partie.heros.changeSlot(controlerPartie.partie, clickedSlot, source_but.arrowType);
				allSlots[clickedSlot][equippedArrowInSlot].isSelected=false;
				source_but.isSelected=true;
			}
			//Put back the correct icon (it was switch to show consistent list )
			ArrowSlotButton.switchButtonType(allSlots[clickedSlot][0],source_but);
			//hide the slot
			showOrHideSlot(false,source_but);
			
			slotOpened=-1;
		}
		//case 3: slot i opened and clicked on button from other slot 
		else{
			//close current slot
			showOrHideSlot(false,allSlots[slotOpened][0]);
			//open other slot
			showOrHideSlot(true,source_but);
			
			slotOpened=clickedSlot;
		}
	
		AffichagePartie.this.getActiveJFrame().pack();
		mainPanel.validate();
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

			ArrowSlotButton source_but = (ArrowSlotButton)e.getSource();
			if(!source_but.isEnabled()){ //apparently something broke the default behaviour of JButton in ArrowSlotButton so we have to check for enabled
				//dispatch the event to the main panel 
				mainPanel.dispatchEvent(e);
				return;
			}
			
			changeArrowSlot(source_but);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			ArrowSlotButton source_but = (ArrowSlotButton)e.getSource();
			if(!source_but.isEnabled()){ //apparently something broke the default behaviour of JButton in ArrowSlotButton so we have to check for enabled
				//dispatch the event to the main panel 
				mainPanel.dispatchEvent(e);
				return;
			}
		}

	}


	private void onGameRestart()
	{
		mainPanel.removeAll();
		mainPanel.add(panelSlots);
		CloseAllSlots();
		disableOrEnableAllSlotButton(false);
		initFlecheIcon=true;
	}

	public void update() {	
		if(controlerPartie.partie.arrowSlotKey != -1)
		{
			ArrowSlotButton source_but;
			ArrowSlotButton[][] allSlots = {bSlot1,bSlot2,bSlot3,bSlot4};
			//slot opened 
			if(slotOpened!=-1)
				source_but = allSlots[slotOpened][controlerPartie.partie.arrowSlotKey];
			//slot closed => get first button of the slot 
			else
				source_but = allSlots[controlerPartie.partie.arrowSlotKey][0];
			changeArrowSlot(source_but);
			controlerPartie.partie.arrowSlotKey=-1;
		}
		
		else if(controlerPartie.partie.getDisableBoutonsFin()){
			EnableBoutonsFin(false);
			onGameRestart();
			controlerPartie.partie.resetVariablesAffichage();
		}
		else{
			if(controlerPartie.partie.setAffichageOption)
			{
				controlerPartie.partie.shoudResumeGame=true;
				controlerPartie.partie.gameHandler.setGameMode(GameModeType.OPTION);
				controlerPartie.partie.setAffichageOption=false;

			}
			//Main update 
			updateSwingPartie();
			ModelPrincipal.debugTime.elapsed("repaint");

		}
	}
}


