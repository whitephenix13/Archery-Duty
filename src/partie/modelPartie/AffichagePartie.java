package partie.modelPartie;

import java.awt.BorderLayout;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Affichage.Affichage;
import gameConfig.InterfaceConstantes;
import option.AbstractControlerOption;
import option.AbstractModelOption;
import option.AffichageOption;
import option.ControlerOption;
import option.ModelOption;
import option.Touches;
import serialize.Serialize;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichagePartie extends JFrame implements Observer{

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

	protected PanelPartie panelPartie=new PanelPartie();
	protected JPanel panelPauseY = new JPanel();
	protected JPanel panelPauseX = new JPanel();
	protected boolean firstTimePause=false;
	protected boolean firstTimeFin=false;

	protected JPanel panelFinX = new JPanel();
	protected JPanel panelFinY = new JPanel();

	private final int BARS_HEIGHT = 70;

	public class PositionPanel extends JPanel
	{
		int xsize; 
		int ysize;
		public PositionPanel(int x, int y, boolean visible)
		{
			xsize=x;
			ysize=y;
			this.setOpaque(false);
			this.setVisible(visible);
		}
		public Dimension getPreferredSize() {
			return new Dimension(xsize, ysize);
		}
	}

	public class AbsoluateLayoutManager implements LayoutManager2 {

		@Override
		public void addLayoutComponent(Component comp, Object constraints) {
		}

		@Override
		public Dimension maximumLayoutSize(Container target) {
			return preferredLayoutSize(target);
		}

		@Override
		public float getLayoutAlignmentX(Container target) {
			return 0.5f;
		}

		@Override
		public float getLayoutAlignmentY(Container target) {
			return 0.5f;
		}

		@Override
		public void invalidateLayout(Container target) {
		}

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			int maxX = 0;
			int maxY = 0;
			for (Component comp : parent.getComponents()) {
				Dimension size = comp.getPreferredSize();
				maxX = Math.max(comp.getX() + size.width, maxX);
				maxY = Math.max(comp.getY() + size.height, maxY);
			}

			return new Dimension(maxX, maxY);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return preferredLayoutSize(parent);
		}

		@Override
		public void layoutContainer(Container parent) {
			for (Component comp : parent.getComponents()) {
				Dimension size = comp.getPreferredSize();
				comp.setSize(size);
			}
		}

	}


	protected JPanel panelSlots = new JPanel();
	protected JPanel panelSlot1 = new JPanel();
	protected JPanel panelSlot2 = new JPanel();
	protected JPanel panelSlot3 = new JPanel();
	protected JPanel panelSlot4 = new JPanel();
	boolean initFlecheIcon = true;

	protected int SHIFT_VAL = 10; //value by which the slotPanel has to be lower to indicate that it was selected 
	protected int last_shifted = -1; //index of the selected slot 

	protected boolean doitRevalidate=false;
	AbstractControlerPartie controlerPartie;

	public AffichagePartie(AbstractControlerPartie _controlerPartie)
	{
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
		panelSlots.setOpaque(true);

		JPanel[] allpanelSlot = {panelSlot1,panelSlot2,panelSlot3,panelSlot4};

		int alignWithBar =10;

		for(int i=0; i<4;++i)
		{
			allpanelSlot[i].setLayout(new BoxLayout(allpanelSlot[i],BoxLayout.Y_AXIS));
			allpanelSlot[i].setAlignmentY( Component.TOP_ALIGNMENT );
			allpanelSlot[i].setOpaque(false); 
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

		panelPartie.setLayout(new BoxLayout(panelPartie,BoxLayout.X_AXIS));

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
		panelPartie.add(panelSlots);

		panelPartie.setOpaque(true); 
		initFlecheIcon=true;

		//on utilise le content pane principal pour dessiner 
		panelPartie.setFocusable(true);
		panelPartie.requestFocusInWindow();

		//initialize input in order for them to be modifier by option
		controlerPartie.partie.inputPartie.init(panelPartie);

		this.getContentPane().add(panelPartie);
		this.pack();

	}
	public class PanelPartie extends JPanel 
	{

		public PanelPartie ()
		{
			this.setOpaque(false);
		}
		public void paintComponent(Graphics g)
		{
			if(!Serialize.niveauLoaded || !controlerPartie.partie.loaderPartie.isLoadingDone())
			{
				this.getRootPane().setBackground(Color.BLACK);

				controlerPartie.partie.loaderPartie.showLoading(g);
				return;
			}

			if(!controlerPartie.partie.computationDone && !controlerPartie.partie.getFinPartie() && !controlerPartie.partie.getForceRepaint()){
				return;
			}

			if(controlerPartie.partie.getForceRepaint())
				controlerPartie.partie.resetForceRepaint();

			super.paintComponent(g);

			//on dessine le niveau

			controlerPartie.partie.drawPartie(g);
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
			
			JPanel layerPan = new JPanel();
			layerPan.setOpaque(false);
			layerPan.setLayout(null);		
			panelFinY.setSize(InterfaceConstantes.WINDOW_WIDTH,InterfaceConstantes.WINDOW_HEIGHT);
			layerPan.add(panelFinY);
			layerPan.add(panelSlots);
			panelPartie.add(layerPan);

		}
		//Start pause
		else if(!controlerPartie.partie.finPartie &&controlerPartie.partie.inPause && !firstTimeFin)
		{
			doitRevalidate=true;
			firstTimeFin=true;

			EnableBoutonsPause(true);

			panelPartie.removeAll();
			
			JPanel layerPan = new JPanel();
			layerPan.setOpaque(false);
			layerPan.setLayout(null);		
			panelPauseX.setSize(InterfaceConstantes.WINDOW_WIDTH,InterfaceConstantes.WINDOW_HEIGHT);
			layerPan.add(panelPauseX);//panelPauseX
			layerPan.add(panelSlots);

			DisableAllSlotButton(true);
			panelPartie.add(layerPan);

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
			EnableBoutonsPause(false);
			panelPartie.removeAll();
			panelPartie.add(panelSlots);

			DisableAllSlotButton(false);
		}


		//if icon changed 
		if(controlerPartie.partie.arrowSlotIconChanged)
		{	
			if(initFlecheIcon){
				//retrieved all the image for the slots in correct order 
				String[] arrowsType1 = new String[4];  // name of the arrows in the slot 1 
				String[] arrowsType2 = new String[4];
				String[] arrowsType3 = new String[4];
				String[] arrowsType4 = new String[4];
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

			JPanel[] allpanelSlot = {panelSlot1,panelSlot2,panelSlot3,panelSlot4};
			int new_last_shited = -1;
			for(int i=0;i<4;i++)
			{
				if(last_shifted >=0 && (i==last_shifted))
				{
					if(allpanelSlot[i].getComponentCount()>0){
						ArrowSlotButton firstButton = (ArrowSlotButton)allpanelSlot[i].getComponents()[0];
						if(firstButton != null){
							firstButton.AddBorderSize(-SHIFT_VAL,0,0,0);
						}
					}
				}
				if(controlerPartie.partie.heros.current_slot == i)
				{
					if(allpanelSlot[i].getComponentCount()>0){
						ArrowSlotButton firstButton = (ArrowSlotButton)allpanelSlot[i].getComponents()[0];
						if(firstButton != null)
							firstButton.AddBorderSize(SHIFT_VAL,0,0,0);
						new_last_shited=i;
					}
				}
			}
			last_shifted = new_last_shited;
			controlerPartie.partie.arrowSlotIconChanged=false;
		}

		panelPartie.requestFocus();
		panelPartie.repaint();


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
		controlerPartie.partie.inputPartie.init(panelPartie);
		panelPartie.addMouseListener(new SourisListener());
		panelPartie.addMouseMotionListener(new SourisMotionListener());

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
		panelPartie.removeMouseListener(panelPartie.getMouseListeners()[0]);
		panelPartie.removeMouseMotionListener(panelPartie.getMouseMotionListeners()[0]);

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
			JButton button = (JButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				controlerPartie.controlBoutonsPressed(((JButton)e.getSource()));
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
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}

	}

	public void createOption(Touches _touches)
	{
		AbstractModelOption option = new ModelOption(_touches,controlerPartie.partie.inputPartie);
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

	private void onGameRestart()
	{
		panelPartie.removeAll();
		panelPartie.add(panelSlots);
		DisableAllSlotButton(false);
		initFlecheIcon=true;
	}

	public void update() {	
		if(controlerPartie.partie.getDisableBoutonsFin()){
			EnableBoutonsFin(false);
			onGameRestart();
		}

		if(controlerPartie.partie.setAffichageOption)
		{
			createOption(controlerPartie.partie.touches);

		}
		if(controlerPartie.partie.getForceRepaint()){
			panelPartie.repaint();
		}
		controlerPartie.partie.resetVariablesAffichage();
	}
}


