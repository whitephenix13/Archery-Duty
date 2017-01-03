package editeur;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import principal.InterfaceConstantes;
import types.Bloc;
import types.Monde;
import types.StockageMonstre;
import Affichage.Affichage;

@SuppressWarnings("serial")
public class DessinEditeur extends JPanel implements InterfaceConstantes{
	
	//{{ variables
	private java.awt.Image image=null;
//permet de connaitre le pas , utile pour l'utilisation de la loupe
	public int pas = 100;
	public int pasRapport=100/20;
	//abscisse et ordonnée pour parcourir 
	
		int abs =0;
		int ord =0; 
	
	//indice pour savoir de combien on s'est déplacé avec le drag
	public int xdrag=0;
	public int ydrag=0;
	
	//entier permettant de définir ou se situe le rectangle qu'on regarde 
	//attention l'axe des y pointe vers le bas 
	
	//on commence par tracer au centre 
	
	public int absRect = ABS_MAX*100/2;
	public int ordRect = ORD_MAX*100/2; 
	
	//on creer une variable decallage qui permet d'afficher les drag 
	public int xdecal =0;
	public int ydecal=0;
	//ces variables permettent de savoir quand est ce que x/ydecal a augmenté/diminiué de 100
	//il faut alors modifier l'affichage: on change de rectangle 
	public int xdecalcumul =0;
	public int ydecalcumul =0;
	
	//booleen pour savoir quand l'image est draguée
	public boolean drag=false;
	//booleen pour savoir si on a atteint un bord, dans ce cas on arrete le drag 
	public boolean limiteDrag =false;
	
	// taille de la fenetre 
	
	/*Pour obtenir la taille de l'ecran, on fait 
	  Dimension tailleEcran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	 (int)tailleEcran.getWidth();
	 (int)tailleEcran.getWidth();
	 */
	
	
	public int hauteurFenetre = HAUTEUR_FENETRE;
	public int largeurFenetre = LARGEUR_FENETRE;
	
	//on crée les variables qui vont être modifiées par les boutons 
	public boolean bloquant=false;
	public boolean background=false;
	public boolean loupe = false;
	//texture speciale: 
	public boolean perso = false;
	public int[] persoPos = {-1,-1};
	public boolean start = false;
	public int[] startPos = {-1,-1};
	public boolean end = false;
	public int[] endPos = {-1,-1};
	public boolean monstreActive=false;
	public String texture ="";
//variable de parcours
	public int x= -100;
	public int y=-100 ;
	public int iter=0;
	//création et initialisation du monde 
	public Monde monde = new Monde();
	
	//création d'un tableau de monstre 
	public List<StockageMonstre> tabEditeurMonstre= new ArrayList <StockageMonstre> ();
	
	//Utilisé pour charger les images 
	Monde m=new Monde("");
	
	Affichage affich;
	
	//variables pour la suppression des monstres
	public int[] infoCase = {0,0};
	public List<StockageMonstre> monstreDansCase= new ArrayList<StockageMonstre>();
	
	//variables pour le comportement:
	public boolean doitStaticSpirel =false;
	
	//}}
public DessinEditeur(JFrame _affich) 
	{
	_affich=affich;
	this.addMouseMotionListener(new DragListener());
	this.addMouseListener(new DragListener());
	this.addMouseListener(new AjoutListener());
	this.repaint();
	}

/**
 * Gère l'ensemble des événements lié au deplacement de la fleche 
 * 
 * @param positionVoulue, position de la case cliquée
 * @param listAChercher, liste de tous les monstres du niveau
 * 
 * @return liste des monstres dans la case cliquée
 */	
public List<StockageMonstre> FindMonstre(int[] positionVoulue, List<StockageMonstre> listAChercher )
{
	List<StockageMonstre> resultat = new ArrayList<StockageMonstre>();
	resultat.clear();
	for(int i=0; i< listAChercher.size(); i++)
	{
		if(listAChercher.get(i).pos.x == positionVoulue[0] && listAChercher.get(i).pos.y == positionVoulue[1] )
		{
			resultat.add(listAChercher.get(i));
		}
	}
	return(resultat);
}

/**
 * Permet de choisir les monstres a supprimer
 */
public class DeleteMonstrePopUp extends JDialog {
	  public DeleteMonstrePopUp(JFrame parent, String title, boolean modal){
	    //On appelle le construteur de JDialog correspondant
	    super(parent, title, modal);
	    //On specifie une taille
	    this.setSize(300, 400);
	    //La position
	    this.setLocationRelativeTo(null);
	    //La boite ne devra pas etre redimensionnable
	    this.setResizable(false);
	    JPanel affichage = new JPanel();
	    JButton ok = new JButton("OK");	
	    List<JLabel> listLabels = new ArrayList<JLabel>();
	    listLabels.add(new JLabel("Listes des monstres à supprimer\n\n\n"));
	   
	    for(int i=0; i<monstreDansCase.size();i++)
	    {
		    listLabels.add(new JLabel((i+1)+ " "+monstreDansCase.get(i).nom));
	    }	    
	    final List<JCheckBox> listChecks= new ArrayList<JCheckBox> (); 
	    for(int i=0; i<listLabels.size()-1;i++)
	    {
		    listChecks.add(new JCheckBox());
	    }	
	    
	    ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				List<StockageMonstre> monstreASupprimer= new ArrayList<StockageMonstre>();
				 for(int i=0; i<listChecks.size();i++)
				   {
					    if(listChecks.get(i).isSelected())
					    {
					    	monstreASupprimer.add(monstreDansCase.get(i));
					    }
				   }	
				//on les supprime
				 for(int i=0; i<monstreASupprimer.size();i++)
				  {
					 tabEditeurMonstre.remove(monstreASupprimer.get(i));
				  }	
				setVisible(false);}});
	   
	    affichage.add(listLabels.get(0),BorderLayout.NORTH);
	    for(int i=1; i<listLabels.size();i++)
	    {
		    affichage.add(listLabels.get(i),BorderLayout.WEST);
		    affichage.add(listChecks.get(i-1),BorderLayout.EAST);
	    }	
		affichage.add(ok,BorderLayout.SOUTH);
		
		this.getContentPane().add(affichage);
	  }
	}

/**
 * Permet de decaller l'image avec le clic droit 
 */
class DragListener extends MouseAdapter{
	public void mouseDragged(MouseEvent e){
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
		if(!drag){
			drag=true;
			xdrag=e.getX();
			ydrag=e.getY();
			repaint();
		}
		//on calcul la distance cumulée
		xdecalcumul+=(e.getX()-xdrag);
		ydecalcumul+=(e.getY()-ydrag);
		
		//on modifie les valeurs du rectangle d'affichage si on a bougé de plus d'une case
		//on réajuste la variable xdecal pour ne pas ajouter deux fois la meme valeur
		
		absRect-=100*(xdecalcumul/pas);
		//on depasse a droite
		if(absRect/100-1<0 || absRect/100>ABS_MAX-largeurFenetre/pas-1){
			absRect+=100*(xdecalcumul/pas);
			xdecalcumul-=(e.getX()-xdrag);
			JOptionPane.showMessageDialog(null, "Vous allez depasser la limite du terrain en x", "Attention", JOptionPane.WARNING_MESSAGE);
			limiteDrag=true;
		}
		//on depasse a gauche
		
		else{
		//on traite normalement
		xdecal-=pas*(xdecalcumul/pas);
		}
		
		ordRect-=100*(ydecalcumul/pas);
		//on depasse en haut
				if(ordRect/100-1<0 || ordRect/100>ORD_MAX-hauteurFenetre/pas-1){
					ordRect += 100*(ydecalcumul/pas);
					ydecalcumul -= (e.getY()-ydrag);
					JOptionPane.showMessageDialog(null, "Vous allez depasser la limite du terrain en y", "Attention", JOptionPane.WARNING_MESSAGE);
					limiteDrag=true;
				}
				//on depasse en bas
				//sinon on traite normalement
				else{
		ydecal-=pas*(ydecalcumul/pas);
				}
				
				if(!limiteDrag){
		//on a fait le nombre de decallage de case necessaire, on garde juste le reste
		xdecalcumul=xdecalcumul%pas;
		ydecalcumul=ydecalcumul%pas;
		
		//on ajoute le nouvel ecart Ã  l'ecart total
		xdecal=xdecal+((e.getX()-xdrag)%pas);
		ydecal=ydecal+((e.getY()-ydrag)%pas);
		xdrag=e.getX();
		ydrag=e.getY();
				}
				limiteDrag=false;
				repaint();
		
				

}
	}
	
	public void mouseReleased(MouseEvent e){
		if(drag)
		{
			drag=false;
			xdecal=0;
			absRect=(absRect/pas )*pas;
			ydecal=0;
			ordRect=(ordRect/pas )*pas;
			
			
		}
		repaint();
	}
}
/**
 * Permet de dessiner en faisant glisser la souris
 */

class ImageListener implements MouseMotionListener {

	//lorsqu'on drag, on dessine sur chacune des cases sur lesquels on passe
public void mouseDragged(MouseEvent e) {
	
	if(!texture.equals("spirel"))
	{
		monstreActive=false;
	}
	
	if(texture != "" )
    {
	if(loupe)
	{
		if(perso)
		{
			persoPos[0]=(e.getX()/pas)*pas*pasRapport+absRect;
			persoPos[1]=(e.getY()/pas)*pas*pasRapport+ordRect;
			perso=false;
			texture="";//on remet la souris
		}
		else if(start)
		{
			startPos[0]=(e.getX()/pas)*pas*pasRapport+absRect;
			startPos[1]=(e.getY()/pas)*pas*pasRapport+ordRect;
			start=false;
			texture="";//on remet la souris
		}
		else if(end)
		{
			endPos[0]=(e.getX()/pas)*pas*pasRapport+absRect;
			endPos[1]=(e.getY()/pas)*pas*pasRapport+ordRect;
			end=false;
			texture="";//on remet la souris
		}	
		else if(monstreActive)
		{
			if(texture=="spirel")
			{
			tabEditeurMonstre.add(new StockageMonstre("spirel",new Point((e.getX()/pas)*pas*pasRapport+absRect,(e.getY()/pas)*pas*pasRapport+ordRect),doitStaticSpirel));
			}
		}
		else
		{
		final Bloc tempPict= new Bloc(texture,(e.getX()/pas)*pas*pasRapport+absRect,(e.getY()/pas)*pas*pasRapport+ordRect,bloquant,background);
		monde.niveau[e.getX()/pas+absRect/100][e.getY()/pas+ordRect/100]=tempPict;
		}
	}
	else if(monstreActive)
	{
		if(texture=="spirel")
		{
		tabEditeurMonstre.add(new StockageMonstre("spirel",new Point((e.getX()/100)*100+absRect,(e.getY()/100)*100+ordRect),doitStaticSpirel));
		}
	}
	else if(perso)
	{
		persoPos[0]=(e.getX()/100)*100+absRect;
		persoPos[1]=(e.getY()/100)*100+ordRect;
		perso=false;
		texture="";//on remet la souris
	}
	else if(start)
	{
		startPos[0]=(e.getX()/100)*100+absRect;
		startPos[1]=(e.getY()/100)*100+ordRect;
		start=false;
		texture="";//on remet la souris
	}
	else if(end)
	{
		endPos[0]=(e.getX()/100)*100+absRect;
		endPos[1]=(e.getY()/100)*100+ordRect;
		end=false;
		texture="";//on remet la souris
	}
	else{
	final Bloc tempPict= new Bloc(texture,(e.getX()/100)*100+absRect,(e.getY()/100)*100+ordRect,bloquant,background);
	monde.niveau[e.getX()/100+absRect/100][e.getY()/100+ordRect/100]=tempPict;
	}
	repaint();
    }
}

	@Override
	public void mouseMoved(MouseEvent e) {
		x=e.getX();
		y=e.getY();
		repaint();
	}
	
}

/**
 * Permet d'ajouter un élément à l'aide d'un clic
 */
class AjoutListener implements MouseListener{

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if(!texture.equals("spirel"))
		{
			monstreActive=false;
		}
		
		if(texture!="") // si le bloc choisi n'est pas "celui de la souri"
		{
			//On ajoute notre bloc au tableau on le translatant pour le remettre a la bonne place
			if(loupe)
			{
				if(perso)
				{
					persoPos[0]=(e.getX()/pas)*pas*pasRapport+absRect;
					persoPos[1]=(e.getY()/pas)*pas*pasRapport+ordRect;
					perso=false;
					texture="";//on remet la souris
				}
				else if(start)
				{
					startPos[0]=(e.getX()/pas)*pas*pasRapport+absRect;
					startPos[1]=(e.getY()/pas)*pas*pasRapport+ordRect;
					start=false;
					texture="";//on remet la souris
				}
				else if(end)
				{
					endPos[0]=(e.getX()/pas)*pas*pasRapport+absRect;
					endPos[1]=(e.getY()/pas)*pas*pasRapport+ordRect;
					end=false;
					texture="";//on remet la souris
				}
				else if(monstreActive)
				{
					if(texture=="spirel")
					{
						tabEditeurMonstre.add(new StockageMonstre("spirel",new Point((e.getX()/pas)*pas*pasRapport+absRect,(e.getY()/pas)*pas*pasRapport+ordRect),doitStaticSpirel));
					}
				}
				else
				{
					final Bloc tempPict= new Bloc(texture,(e.getX()/pas)*pas*pasRapport+absRect,(e.getY()/pas)*pas*pasRapport+ordRect,bloquant,background);
					monde.niveau[e.getX()/pas+absRect/100][e.getY()/pas+ordRect/100]=tempPict;
				}
			}
			else if(perso)
			{
				persoPos[0]=(e.getX()/100)*100+absRect;
				persoPos[1]=(e.getY()/100)*100+ordRect;
				perso=false;
				texture="";//on remet la souris
			}
			else if(start)
			{
				startPos[0]=(e.getX()/100)*100+absRect;
				startPos[1]=(e.getY()/100)*100+ordRect;
				start=false;
				texture="";//on remet la souris
			}
			else if(end)
			{
				endPos[0]=(e.getX()/100)*100+absRect;
				endPos[1]=(e.getY()/100)*100+ordRect;
				end=false;
				texture="";//on remet la souris
			}
			else if(monstreActive)
			{
				if(texture.equals("spirel"))
				{
					tabEditeurMonstre.add(new StockageMonstre("spirel",new Point((e.getX()/100)*100+absRect,(e.getY()/100)*100+ordRect),doitStaticSpirel));
				}
			}
			else
			{
				final Bloc tempPict= new Bloc(texture,(e.getX()/100)*100+absRect,(e.getY()/100)*100+ordRect,bloquant,background);
				monde.niveau[e.getX()/100+absRect/100][e.getY()/100+ordRect/100]=tempPict;
			}
		}
		repaint();
	}

	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	 if (texture.equals("Delete"))
		{
			//on memorise la case
			infoCase[0]= (e.getX()/(loupe ? pas: 100 ) )*(loupe ? pas: 100 )* (loupe? pasRapport: 1) +absRect;
			infoCase[1]= (e.getY()/(loupe ? pas: 100 ) )*(loupe ? pas: 100 )* (loupe? pasRapport: 1) +ordRect;
			//on chercher tout les monstres dedans et on stock leur nom dans la liste
			monstreDansCase=FindMonstre(infoCase,tabEditeurMonstre)	;
			//on lance le popup
			DeleteMonstrePopUp popUp= new DeleteMonstrePopUp(affich,"test",true);
			popUp.setVisible(true);
		}
	}
	public void mouseReleased(MouseEvent e) {
	
	}
	
}

/**
 * Reinitialise le niveau
 */
public void reset() {
	//abscisse et ordonnée pour parcourir 
	 abs =0;
	ord =0;
	
	//indice pour savoir de combien on s'est déplacé avec le drag
	 xdrag=0;
	ydrag=0;
	
	//entier permettant de définir ou se situe le rectangle qu'on regarde 
	//attention l'axe des y pointe vers le bas 
	
	//on commence par tracer au centre 
	 absRect = ABS_MAX*100/2;
	ordRect = ORD_MAX*100/2;
	//on creer une variable decallage qui permet d'afficher les drag 
	 xdecal =0;
	 ydecal=0;
	//ces variables permettent de savoir quand est ce que x/ydecal a augmenté/diminiué de 100
	//il faut alors modifier l'affichage: on change de rectangle 
	 xdecalcumul =0;
	ydecalcumul =0;
	
	//booleen pour savoir quand l'image est draguée
	 drag=false;
	//booleen pour savoir si on a atteint un bord, dans ce cas on arrete le drag 
	limiteDrag =false;
	
	// taille de l'ecran 
	Dimension tailleEcran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	 hauteurFenetre = (int)tailleEcran.getHeight();
	 largeurFenetre = (int)tailleEcran.getWidth();
	
	//on crée les variables qui vont être modifiées par les boutons 
	 bloquant=false;
	 background=false;
	 texture ="";
//variable de parcours
	 x= -100;
	 y=-100 ;
	 iter=0;
	//création et initialisation du monde 
	 monde = new Monde();
	 
	 persoPos[0]= -1; 
	 startPos[0]= -1; 
	 endPos[0]= -1; 
	 
	 tabEditeurMonstre.clear();
}

/**
 * Dessine les éléments 
 */
public void paint(Graphics g)
{
	super.paintComponent(g);
		
	for(abs=absRect/100-1;abs<(largeurFenetre/pas+absRect/100)+1;abs++)
	{
		for(ord=ordRect/100-1;ord<(hauteurFenetre/pas+ordRect/100)+1;ord++)
		{
			final Bloc tempPict= monde.niveau[abs][ord];
		//on translate les blocs pour les afficher
			//Pour avoir les images en petit : + ((loupe) ? "_p": "")
			int xdraw = (tempPict.getXpos() +xdecal-absRect)/((loupe) ? pasRapport : 1 );
			int ydraw=(tempPict.getYpos()+ydecal-ordRect)/((loupe) ? pasRapport : 1 );
			
			g.drawImage(m.getImage(tempPict,loupe),xdraw,ydraw, null);
		}
	}
	///((loupe) ? pasRapport : 1 )
			//on affiche les lignes
	for(abs=0;abs<largeurFenetre/pas+1;abs++)
	{
		for(ord=0;ord<hauteurFenetre/pas+1;ord++)
		{
		g.drawLine(abs*pas,ord*pas,abs*pas,hauteurFenetre+ord*pas+pas);
		g.drawLine(abs*pas,ord*pas,largeurFenetre+abs*pas+pas ,ord*pas);
		}
	}
	
	
	for(int i=0; i<tabEditeurMonstre.size(); i++)
	{
		image = getToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/"+tabEditeurMonstre.get(i).nom + ""+((loupe) ? "_p" : "" )+".png"));
		g.drawImage(image, (tabEditeurMonstre.get(i).pos.x +xdecal-absRect)/((loupe) ? pasRapport : 1 ),(tabEditeurMonstre.get(i).pos.y +ydecal-ordRect)/((loupe) ? pasRapport : 1 ), null);
	}
	
	//on dessine le personnage 
	if(persoPos[0]!=-1)
	{
		image = getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/perso"+((loupe) ? "_p" : "" )+".png"));
		g.drawImage(image, (persoPos[0] +xdecal-absRect)/((loupe) ? pasRapport : 1 ),(persoPos[1] +ydecal-ordRect)/((loupe) ? pasRapport : 1 ), null);
	}
	//on dessine le début
	if(startPos[0]!=-1)
	{
		image = getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/start"+((loupe) ? "_p" : "" )+".png"));
		g.drawImage(image, (startPos[0] +xdecal-absRect)/((loupe) ? pasRapport : 1 ),(startPos[1] +ydecal-ordRect)/((loupe) ? pasRapport : 1 ), null);
	}
	//on dessine la fin
	if(endPos[0]!=-1)
	{
		image = getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/end"+((loupe) ? "_p" : "" )+".png"));
		g.drawImage(image, (endPos[0] +xdecal-absRect)/((loupe) ? pasRapport : 1 ),(endPos[1] +ydecal-ordRect)/((loupe) ? pasRapport : 1 ), null);
	}
	//on dessine l'image du bloc choisi si il existe une texture 
		if(texture != "")
		{
		image = getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/"+texture +((loupe) ? "_p" : "" )+".png"));
		
		if(texture.equals("Delete"))
		{
			g.drawImage(image, x-10, y-7, null);
		}
		else
		{
			g.drawImage(image, x-pas/2, y-pas/2, null);
		}
		
		}
}
}
