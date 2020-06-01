package menu.menuPrincipal;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ActiveJComponent.ActiveJButton;
import Affichage.Drawable;
import images.ImagesPrincipal;
import images.ImagesPrincipal.ImPrincipalInfo;
import loading.Loader;
import menu.credit.AffichageCredit;
import menu.menuPrincipal.GameHandler.GameModeType;
import utils.TypeApplication;
import utils.observer.Observer;

@SuppressWarnings("serial")
public class AffichagePrincipal extends Drawable implements Observer{

	@SuppressWarnings("unused")
	private AbstractControlerPrincipal controlerPrincipal;
	
	//private	PanelPrincipal panelBoutons = new PanelPrincipal();
	private BoutonPrincipal bPartieRapide=new BoutonPrincipal("Partie Rapide");
	private BoutonPrincipal bCredit=new BoutonPrincipal("Credit");
	private BoutonPrincipal bEditeur=new BoutonPrincipal("Editeur");
	private BoutonPrincipal bOption=new BoutonPrincipal("Option");
	private BoutonPrincipal bQuitter=new BoutonPrincipal("Quitter");
	
	//@Override
	//public void execute(){panelBoutons.repaint();};
	
	public class BoutonPrincipal extends ActiveJButton
	{
		public BoutonPrincipal(String s)
		{
			super(s);
			this.setForeground(Color.WHITE);
			this.setBackground(Color.BLACK);
			this.setFont(new Font("Courrier",Font.PLAIN,44));
			this.setEnabled(true);
			this.setVisible(true);
		}
	}

	public AffichagePrincipal(AbstractControlerPrincipal _controlerPrincipal)
	{
		super();
		controlerPrincipal=_controlerPrincipal;
		Init();
	}
	
	public void setButtons()
	{
		mainPanel.setLayout(null);
		mainPanel.add(bPartieRapide);
		if(TypeApplication.isJar)
			mainPanel.add(bCredit);
		else
			mainPanel.add(bEditeur);
		mainPanel.add(bOption);
		mainPanel.add(bQuitter);
	}
	public void removeButtons()
	{
		mainPanel.removeAll();
	}
	public void Init()
	{
		bPartieRapide.setBounds(90, 260, 320, 50);
		bCredit.setBounds(90, 560, 290, 50);
		bEditeur.setBounds(90, 560, 290, 50);
		bOption.setBounds(990, 260, 290, 50);
		bQuitter.setBounds(990, 560, 290, 50);
		
		mainPanel.setBackground(Color.black);
		mainPanel.setOpaque(false);
		
		ImagesPrincipal.me.run();
	}

	public class boutonsPrincipalListener implements MouseListener 
	{

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {
			ActiveJButton button = (ActiveJButton)e.getSource();
			Rectangle r = button.getBounds();
			//Apply pressed only if the release is on the pressed button
			if(r.contains(new Point(r.x+e.getX(),r.y+e.getY()))){
				if(button.getText().equals("Partie Rapide"))
				{
					controlerPrincipal.principal.setGameMode(GameModeType.LEVEL_SELECTION);
				}
				else if(button.getText().equals("Editeur"))
				{
					controlerPrincipal.principal.setGameMode(GameModeType.EDITOR);
				}
				else if(button.getText().equals("Credit"))
				{
					controlerPrincipal.principal.setGameMode(GameModeType.CREDIT);
				}
				else if(button.getText().equals("Option"))
				{
					controlerPrincipal.principal.setGameMode(GameModeType.OPTION);
				}
				else if(button.getText().equals("Quitter"))
				{
					controlerPrincipal.principal.setGameMode(GameModeType.QUIT);
				}
			}
		}

	}
	public void addListenerPrincipal() 
	{
		bPartieRapide.addMouseListener(new boutonsPrincipalListener());
		if(TypeApplication.isJar)
			bCredit.addMouseListener(new boutonsPrincipalListener());
		else
			bEditeur.addMouseListener(new boutonsPrincipalListener());
		bOption.addMouseListener(new boutonsPrincipalListener());
		bQuitter.addMouseListener(new boutonsPrincipalListener());
	}

	public void removeListenerPrincipal() 
	{
		bPartieRapide.removeMouseListener(bPartieRapide.getMouseListeners()[bPartieRapide.getMouseListeners().length-1]);
		if(TypeApplication.isJar)
			bCredit.removeMouseListener(bEditeur.getMouseListeners()[bEditeur.getMouseListeners().length-1]);
		else
			bEditeur.removeMouseListener(bEditeur.getMouseListeners()[bEditeur.getMouseListeners().length-1]);
		bOption.removeMouseListener(bOption.getMouseListeners()[bOption.getMouseListeners().length-1]);
		bQuitter.removeMouseListener(bQuitter.getMouseListeners()[bQuitter.getMouseListeners().length-1]);
	}
	
	
	@Override
	public void drawOnGraphics(Graphics g,boolean forceRepaint)
	{
		g.drawImage(ImagesPrincipal.me.getImage(null,ImPrincipalInfo.BACKGROUND,null),0,0,mainPanel);		
	}
	
	public Loader getLoader()
	{
		return controlerPrincipal.principal.loaderMenuPrincipal;
	}
	public void update() {
	}

}
