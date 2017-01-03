package personnage;

import java.awt.Image;
import java.awt.Toolkit;

import music.MusicBruitage;
import principal.InterfaceConstantes;
import types.Vitesse;
import deplacement.MouvementFleche;

public class Fleche implements InterfaceConstantes{
	
	public int anim = 0; 
	public int xpos=0;
	public int ypos=0;
	
	public MouvementFleche deplacement = new MouvementFleche();
	public Vitesse vit = new Vitesse(0,0);
	
	public boolean doitDeplace=false;
	public boolean doitDetruire= false;
	public boolean nulle =false;
	public boolean encochee =false;
	public int reaffiche =0;
	
	Image fleche0;
	Image fleche1;
	Image fleche2;
	Image fleche3;
	Image fleche4;
	Image fleche5;
	Image fleche6;
	Image fleche7;
	//timer pour savoir quand est ce que la fleche doit disparaitre (voir interface constante)
	public long tempsDetruit = 0;
	
	public int degat= -50;
	 MusicBruitage bruitage;

	//constructeur pour charger les images des fleches 
	public Fleche(boolean b)
	{
		chargerFleches();
	}
		
	public Fleche()
	{
		nulle= true;
		doitDeplace=false;
		doitDetruire= false;
		tempsDetruit = 0;
		bruitage = new MusicBruitage("arc");
	}
	public Fleche(int xF, int yF, MouvementFleche mouv)
	{
		xpos=xF;
		ypos=yF;
		deplacement=mouv;
		nulle = false;
		tempsDetruit = 0;
		bruitage = new MusicBruitage("arc");
		
	}
	public void timer()
	{
		tempsDetruit=System.nanoTime();
	}
	public void setPosition(int x, int y)
	{
		xpos=x;
		ypos=y;
	}
	public void chargerFleches()
	{
		 fleche0=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/0.gif"));
		 fleche1=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/1.gif"));
		 fleche2=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/2.gif"));
		 fleche3=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/3.gif"));
		 fleche4=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/4.gif"));
		 fleche5=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/5.gif"));
		 fleche6=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/6.gif"));
		 fleche7=Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("resources/fleches/7.gif"));
	}
	public Image getImage(Fleche fleche)
	//{{
	{
		switch(fleche.anim)
		{
		case 0: return(fleche0);
		case 1: return(fleche1);
		case 2: return(fleche2);
		case 3: return(fleche3);
		case 4: return(fleche4);
		case 5: return(fleche5);
		case 6: return(fleche6);
		case 7: return(fleche7);
		default: return(fleche0);
		}
	}
	//}}
	public void flecheDecochee()
	{
		doitDeplace=true;
		encochee=false;
		bruitage.startBruitage(100);
		
	}
}
