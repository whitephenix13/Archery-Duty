package editeur;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import principal.InterfaceConstantes;
import serialize.Serialize;
import types.Bloc;
import types.Monde;
import types.StockageMonstre;

public class ModelEditeur extends AbstractModelEditeur{

	//Variables initialisée dans la fonction dezoom et qui servent à dessiner la zone de zoom 
	private int xZoomArea ; 
	private int yZoomArea; 
	private int xLengthZoomArea;
	private int yLengthZoomArea;

	private int tailleMenu =90;
	public void moveViewPort(int xpos, int ypos) {
		/*xViewPort est toujours relatif a la taile du bloc d'origine: xViewPort/tailleBlocOrigine = indice dans la matrice
		 * De meme, x delta a des coordonnées dans le meme repere que celle de x view port
		 * */

		int xdelta= (xpos-xStartDrag)*((int)(loupe? (1/dezoomFactor):1));
		int ydelta= (ypos-yStartDrag)*((int)(loupe? (1/dezoomFactor):1));
		int _xViewPort=xViewPort;
		int _yViewPort=yViewPort;

		int toRightXViewPort = ((InterfaceConstantes.LARGEUR_FENETRE)*InterfaceConstantes.TAILLE_BLOC/tailleBloc);
		int toDownYViewPort = (InterfaceConstantes.HAUTEUR_FENETRE*InterfaceConstantes.TAILLE_BLOC/tailleBloc);
		if(!drag){
			drag=true;
			xStartDrag=xpos;
			yStartDrag=ypos;
			repaint=true;
			notifyObserver();
			return ;
		}
		_xViewPort-= xdelta;
		_yViewPort-= ydelta;
		
		xStartDrag=xpos;
		yStartDrag=ypos;
		
		//on va depasser a gauche : on divise et on ajoute pour decaller a droite 
		if((_xViewPort)<0){
			int newXViewPort=0;
			_xViewPort= newXViewPort;
		}
		//on va depasser droite: on divise pour decaller à gauche 
		else if( (_xViewPort+toRightXViewPort) > (InterfaceConstantes.ABS_MAX*InterfaceConstantes.TAILLE_BLOC) ){
			int newXViewPort=InterfaceConstantes.ABS_MAX*InterfaceConstantes.TAILLE_BLOC-toRightXViewPort ;
			_xViewPort= newXViewPort;
		}
		//on va depasser en haut : on divise et on ajoute pour decaller en bas
		if((_yViewPort)<0){
			int newYViewPort=0;
			_yViewPort= newYViewPort;
		}
		//on va depasser en bas: on divise pour decaller en haut
		else if( (_yViewPort+toDownYViewPort) > (InterfaceConstantes.ORD_MAX*InterfaceConstantes.TAILLE_BLOC) ){
			int newYViewPort=(InterfaceConstantes.ORD_MAX*InterfaceConstantes.TAILLE_BLOC)-toDownYViewPort;
			_yViewPort= newYViewPort;
		}
				xViewPort=_xViewPort;
				yViewPort=_yViewPort;
				repaint=true;
				notifyObserver();
	}
	public void releaseMoveViewport()
	{
		drag=false;
	}
	
	int calculateDrawPos(int pos, int ViewPort)
	{
		//Il faut faire attention au repere dans lequel on est et en fonction de ce repere, on doit multiplier par le
		//facteur de dezoom ou non.
		//on calcul de combien est decallé le viewport dans le niveau taille normal(zoomé)
		int decalReel= (int) ((InterfaceConstantes.TAILLE_BLOC-ViewPort%InterfaceConstantes.TAILLE_BLOC));
		//on calcul le facteur de dezoom
		float factor = loupe?dezoomFactor:1;
		//on exprime la différence de position entre la souris et le decallage du viewport(exprimé dans le repere dezoomé)
		//Cela permet d'obtenir la position de la souris si le decallage du viewport était nul, ce qui permet de faire  
		//correspondre la vue avec l'endroit où l'utilisateur a cliqué 
		int delta= (pos-(int)(decalReel*factor))+ ( ((pos-(int)(decalReel*factor))<0) ? -tailleBloc : 0 );
		//on calcul la position du bloc dans le repere normal: viewport+ distance au bord gauche de l'ecran 
		//+ decalage pour annuler l'offset de l'affichage 
		return( (int) ( ViewPort+ ((delta)/tailleBloc)*tailleBloc*(1.0f/factor) + decalReel ) );
	}
	public void drawTerrain(int xpos, int ypos) 
	{
		int xBlocPos=calculateDrawPos(xpos,xViewPort);
		int yBlocPos=calculateDrawPos(ypos,yViewPort);
		final Bloc tempPict= new Bloc(texture,xBlocPos,yBlocPos,bloquant,background);
		monde.niveau[xBlocPos/InterfaceConstantes.TAILLE_BLOC][yBlocPos/InterfaceConstantes.TAILLE_BLOC]=tempPict;
		
		repaint=true;
		notifyObserver();
	}
	public void drawMonster(int xpos, int ypos) 
	{		
		int xMonstrePos = calculateDrawPos(xpos,xViewPort);
		int yMonstrePos= calculateDrawPos(ypos,yViewPort);
		
		if(texture=="spirel")
		{
			tabEditeurMonstre.add(new StockageMonstre("spirel",new Point(xMonstrePos,yMonstrePos),staticMonstre));
		}
		
		repaint=true;
		notifyObserver();
	}
	public void drawSpecial(int x, int y) 
	{		

		int xPos = calculateDrawPos(x,xViewPort);
		int yPos = calculateDrawPos(y,yViewPort);
		if(perso)
		{
			persoPos[0]=xPos;
			persoPos[1]=yPos;
			perso=false;
		}
		else if(start)
		{
			startPos[0]=xPos;
			startPos[1]=yPos;
			start=false;
		}
		else if(end)
		{
			endPos[0]=xPos;
			endPos[1]=yPos;
			end=false;
		}
		
		texture="";//on remet la souris

		repaint=true;
		notifyObserver();
	}
	
	public void draw(Graphics g,JPanel pan)
	{
		 java.awt.Image image=null;
		 Graphics2D g2 = (Graphics2D) g;
		int xdecalLine= (int) ((InterfaceConstantes.TAILLE_BLOC-xViewPort%InterfaceConstantes.TAILLE_BLOC)*(loupe?dezoomFactor:1));
		int ydecalLine= (int) ((InterfaceConstantes.TAILLE_BLOC-yViewPort%InterfaceConstantes.TAILLE_BLOC)*(loupe?dezoomFactor:1));
			
		//l'indice de début d'affichage : viewPort/Taille_Bloc
		 int xStartAff = (( (xViewPort/InterfaceConstantes.TAILLE_BLOC-1)<=0)? 0: (xViewPort/InterfaceConstantes.TAILLE_BLOC -1) ); 
		 int xEndAff=((InterfaceConstantes.LARGEUR_FENETRE/tailleBloc+xViewPort/InterfaceConstantes.TAILLE_BLOC)+2);
		 xEndAff= ( xEndAff >= InterfaceConstantes.ABS_MAX )? InterfaceConstantes.ABS_MAX : xEndAff;

		 int yStartAff = (( (yViewPort/InterfaceConstantes.TAILLE_BLOC-1)<=0)? 0: (yViewPort/InterfaceConstantes.TAILLE_BLOC -1) ); 
		 int yEndAff=((InterfaceConstantes.HAUTEUR_FENETRE/tailleBloc+yViewPort/InterfaceConstantes.TAILLE_BLOC)+2);
		 yEndAff= ( yEndAff >= InterfaceConstantes.ORD_MAX )? InterfaceConstantes.ORD_MAX : yEndAff;

		for(int abs=xStartAff;abs<xEndAff;abs++)
		{
			for(int ord=yStartAff;ord<yEndAff;ord++)
			{
				final Bloc tempPict= monde.niveau[abs][ord];
				//xViewPort= xViewPortNonDécallé - x decallage 
				int xdraw = (int) ((tempPict.getXpos() -xViewPort)*((loupe) ? dezoomFactor : 1 ));
				int ydraw=(int) ((tempPict.getYpos()-yViewPort)*((loupe) ? dezoomFactor : 1 ));
				g.drawImage(m.getImage(tempPict,loupe),xdraw,ydraw, null);
				
				//on dessine les limites de la carte 
				 g2.setStroke(new BasicStroke(2));
				 g2.setColor(Color.red);

				 if((abs==0))
					g2.drawLine(0, 0, 0, InterfaceConstantes.HAUTEUR_FENETRE);
				
				 if(abs== (InterfaceConstantes.ABS_MAX-1))
					g2.drawLine(InterfaceConstantes.LARGEUR_FENETRE-7, 0, InterfaceConstantes.LARGEUR_FENETRE-7, InterfaceConstantes.HAUTEUR_FENETRE);
				
				 if( (ord==0))
					g2.drawLine(0, 4, InterfaceConstantes.LARGEUR_FENETRE, 4);
			
				 if((ord==(InterfaceConstantes.ORD_MAX-1)) )
				 {
					g2.drawLine(0, InterfaceConstantes.HAUTEUR_FENETRE-tailleMenu-4, InterfaceConstantes.LARGEUR_FENETRE, InterfaceConstantes.HAUTEUR_FENETRE-tailleMenu-4);
				 }
					g2.setStroke(new BasicStroke(1));
					 g2.setColor(Color.black);

			}
		}
		
		//on dessine l'image du bloc choisi si il existe une texture 
		if(texture != "")
		{
		image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/"+texture +((loupe) ? "_p" : "" )+".png"));
		if(texture.equals("Delete"))
		{
			g.drawImage(image, xMousePos-10, yMousePos-7, null);
		}
		else
		{
			g.drawImage(image, xMousePos-tailleBloc/2, yMousePos-tailleBloc/2, null);
		}
		
		}
		//on affiche les lignes
		for(int abs=0;abs<InterfaceConstantes.LARGEUR_FENETRE/tailleBloc+1;abs++)
		{
			for(int ord=0;ord<InterfaceConstantes.HAUTEUR_FENETRE/tailleBloc+1;ord++)
			{
				//a la différence du bloc, la ligne n'est pas décallé de base, il faut donc la décaller pour la faire
				//correspondre à la vue 
				
				//ligne verticale 
				g.drawLine(abs*tailleBloc+xdecalLine,ord*tailleBloc,abs*tailleBloc+xdecalLine,InterfaceConstantes.HAUTEUR_FENETRE+ord*tailleBloc+tailleBloc);
				//ligne horizontale  
				g.drawLine(abs*tailleBloc,ord*tailleBloc+ydecalLine,InterfaceConstantes.LARGEUR_FENETRE+abs*tailleBloc+tailleBloc ,ord*tailleBloc+ydecalLine);
			}
		}
		//on affiche la zone de zoom 
		if(loupe)
		{
			int x1 = (int) ((xZoomArea)*dezoomFactor);
			int y1 =  (int) ((yZoomArea)*dezoomFactor);
			int x2 = x1+xLengthZoomArea;
			int y2 = (int) (y1+yLengthZoomArea-tailleMenu*dezoomFactor); // on decalle de la taille du menu 

			 g2.setStroke(new BasicStroke(2));
             g2.drawLine(x1,y1,x1,y2);
             g2.drawLine(x2,y1,x2,y2);
             g2.drawLine(x1,y1,x2,y1);
             g2.drawLine(x1,y2,x2,y2);
			 g2.setStroke(new BasicStroke(1));

		}
		
		//on affiche la zone de spawn si elle existe
		if( (startPos[0]!=-1) && (endPos[0]!=-1) )
		{
			int xstart = (int) ((startPos[0] -xViewPort)*((loupe) ? dezoomFactor : 1 ));
			int ystart = (int) ((startPos[1]-yViewPort)*((loupe) ? dezoomFactor : 1 ));
			int xend = (int) ((endPos[0] -xViewPort)*((loupe) ? dezoomFactor : 1 ))+tailleBloc;
			int yend = (int) ((endPos[1]-yViewPort)*((loupe) ? dezoomFactor : 1 )+tailleBloc);
			
			g2.setColor(Color.red);
			g2.setStroke(new BasicStroke(2));
			g2.drawLine(xstart, ystart, xstart, yend);
			g2.drawLine(xend, ystart, xend, yend);
			g2.drawLine(xstart, ystart, xend, ystart);
			g2.drawLine(xstart, yend, xend, yend);
			g2.setColor(Color.black);
			g2.setStroke(new BasicStroke(1));
		}
		for(int i=0; i<tabEditeurMonstre.size(); i++)
		{
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/monstres/"+tabEditeurMonstre.get(i).nom + ""+((loupe) ? "_p" : "" )+".png"));
			g.drawImage(image, (int)((tabEditeurMonstre.get(i).pos.x -xViewPort)*((loupe) ? dezoomFactor : 1 )),(int)((tabEditeurMonstre.get(i).pos.y -yViewPort)*((loupe) ? dezoomFactor : 1 )), null);
		}
		
		//on dessine le personnage 
		if(persoPos[0]!=-1)
		{
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/perso"+((loupe) ? "_p" : "" )+".png"));
			g.drawImage(image, (int)((persoPos[0] -xViewPort)*((loupe) ? dezoomFactor : 1 )),(int)((persoPos[1] -yViewPort)*((loupe) ? dezoomFactor : 1 )), null);
		}
		//on dessine le début
		if(startPos[0]!=-1)
		{
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/start"+((loupe) ? "_p" : "" )+".png"));
			g.drawImage(image,(int)( (startPos[0] -xViewPort)*((loupe) ? dezoomFactor : 1 )),(int)((startPos[1] -yViewPort)*((loupe) ? dezoomFactor : 1) ), null);
		}
		//on dessine la fin
		if(endPos[0]!=-1)
		{
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/end"+((loupe) ? "_p" : "" )+".png"));
			g.drawImage(image, (int)((endPos[0] -xViewPort)*((loupe) ? dezoomFactor : 1 )),(int)((endPos[1] -yViewPort)*((loupe) ? dezoomFactor : 1 )), null);
		}
	
	}

	public  void setTexture(String _texture)
	{
		perso=false;
		start=false;
		end=false;
		monstreActive=false;
		
		texture=_texture;
		if(_texture.equals(""))
		{
			setBloquant(false);
			setBackground(false);
		}
		else if (_texture.equals("Delete"))
		{
			setBloquant(false);
		}
		else if (_texture.equals("vide"))
		{
			setBloquant(false);
		}
		else if (_texture.equals("sol"))
		{
			setBloquant(true);
		}
		else if (_texture.equals("terre"))
		{
			setBloquant(true);
		}
		else if (_texture.equals("ciel"))
		{
			setBloquant(false);
		}
		else if (_texture.equals("perso"))
		{
			perso=true;
		}
		else if (_texture.equals("start"))
		{
			start=true;
		}
		else if (_texture.equals("end"))
		{
			end=true;
		}
		else if (_texture.equals("spirel"))
		{
			monstreActive=true;
			showStaticMonsters=true;
		}
		
		repaint=true;
		notifyObserver();
	}
	
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

	
	public void deleteMonster(int x, int y)
	{
		//int xPos = (x/tailleBloc)*tailleBloc+xViewPort;
		//int yPos= (y/tailleBloc)*tailleBloc+yViewPort;
		int xPos=calculateDrawPos(x,xViewPort);
		int yPos=calculateDrawPos(y,yViewPort);

		int[] infoCase= new int[2];
		//on memorise la case
		infoCase[0]= xPos;
		infoCase[1]= yPos;
		//on chercher tout les monstres dedans et on stock leur nom dans la liste
		monstreDansCase=FindMonstre(infoCase,tabEditeurMonstre)	;
		//on lance le popup
		showMonsters=true;
		notifyObserver();
	}

	public String sauver(String nom) {
		monde.xStartMap=startPos[0];
		monde.yStartMap=startPos[1];
		
		monde.xEndMap=endPos[0];
		monde.yEndMap=endPos[1];
		
		monde.xStartPerso=persoPos[0];
		monde.yStartPerso=persoPos[1];

		monde.listMonstreOriginal= tabEditeurMonstre;

		String err = Serialize.sauver(nom, monde);
		return err;
	}
	public void charger(String nom) 
	{
		Monde _monde = Serialize.charger(nom);		
		if(_monde==null)
		{
			return;
		}
		startPos[0]=_monde.xStartMap;
		startPos[1]=_monde.yStartMap;
		
		endPos[0]=_monde.xEndMap;
		endPos[1]=_monde.yEndMap;
		
		persoPos[0]=_monde.xStartPerso;
		persoPos[1]=_monde.yStartPerso;
		
		tabEditeurMonstre=_monde.listMonstreOriginal;
		
		monde=_monde;
		
		repaint=true;
		notifyObserver();
	
	}
	
	public void information() {
		String info ="";
		info+= "xViewPort= " + xViewPort +","+(xViewPort+InterfaceConstantes.LARGEUR_FENETRE );
		info+= "\nyViewPort= "+ yViewPort+","+(yViewPort+InterfaceConstantes.HAUTEUR_FENETRE -tailleMenu);
		info+="\nAbscisse max= " +InterfaceConstantes.ABS_MAX*100;
		info+= "\nOrdonnee max= "+ InterfaceConstantes.ORD_MAX*100;
		info+="\nBloquant= "+bloquant;
		info+="\nBackground= "+ background ;
		info+="\nTexture= "+ texture;
		info+="\nLargeur fenetre= "+InterfaceConstantes.LARGEUR_FENETRE ;
		info+="\nHauteur fenetre= "+InterfaceConstantes.HAUTEUR_FENETRE;
				
		showMessageDialog=true;
		textMessageDialog[0]=info;
		textMessageDialog[1]="A propos";
		typeMessageDialog= JOptionPane.INFORMATION_MESSAGE;
		
		notifyObserver();
	}

	public void dezoom() {

		int futurTailleBloc=  (int)(tailleBloc  * (!loupe? dezoomFactor: (1.0f/dezoomFactor)));
		int futurNombreBlocL= InterfaceConstantes.LARGEUR_FENETRE/futurTailleBloc;
		int futurNombreBlocH= InterfaceConstantes.HAUTEUR_FENETRE/futurTailleBloc;

		int deltaXViewPort = (InterfaceConstantes.LARGEUR_FENETRE/tailleBloc - futurNombreBlocL ) /2 * InterfaceConstantes.TAILLE_BLOC;
		int deltaYViewPort= (InterfaceConstantes.HAUTEUR_FENETRE/tailleBloc - futurNombreBlocH ) /2 * InterfaceConstantes.TAILLE_BLOC; 
		
		boolean xMinLim= ((xViewPort+deltaXViewPort)<0) ;
		boolean xMaxLim=( ((xViewPort+deltaXViewPort)/InterfaceConstantes.TAILLE_BLOC+futurNombreBlocL) > InterfaceConstantes.ABS_MAX );
		boolean yMinLim=((yViewPort+deltaYViewPort)<0) ;
		boolean yMaxLim=( ((yViewPort+deltaYViewPort)/InterfaceConstantes.TAILLE_BLOC+futurNombreBlocH) > InterfaceConstantes.ORD_MAX );
		//on memorise les valeurs pour tracer la zone de dezoom 
		if(!loupe)//si on est en train de dezoomer 
		{
			xZoomArea=-1*deltaXViewPort; 
			yZoomArea=-1*deltaYViewPort; 
			xLengthZoomArea=(int) (InterfaceConstantes.LARGEUR_FENETRE*dezoomFactor);
			yLengthZoomArea=(int) (InterfaceConstantes.HAUTEUR_FENETRE*dezoomFactor);
		}
		
		if( (InterfaceConstantes.ABS_MAX < futurNombreBlocL) || (InterfaceConstantes.ORD_MAX<futurNombreBlocH)  )
		{
			System.out.println("419 pas assez de place pour dezoomer");
			showMessageDialog=true;
			textMessageDialog[0]="Dezoom impossible: taille du monde trop petit";
			textMessageDialog[1]="Erreur dezoom";
			typeMessageDialog=JOptionPane.ERROR_MESSAGE;
			notifyObserver();
			return;
		}
		
		else if(!loupe &&(xMinLim||yMinLim||xMaxLim ||yMaxLim))
		{
			System.out.println("limite terrain " );
			
			if(xMinLim)
				xViewPort=0;
			if(yMinLim)
				yViewPort=0;
			if(xMaxLim )
				xViewPort= (InterfaceConstantes.ABS_MAX-futurNombreBlocL)*futurTailleBloc;
			
			if(yMaxLim)
				yViewPort= (InterfaceConstantes.ORD_MAX-futurNombreBlocH)*futurTailleBloc;
			
			if(!xMinLim && !xMaxLim)
				xViewPort+=deltaXViewPort;
			
			if(!yMinLim && !yMaxLim)
				yViewPort+=deltaYViewPort;
			
			tailleBloc = futurTailleBloc;
			loupe=!loupe;
			
			repaint=true;
			notifyObserver();
		}
		else 
		{

			xViewPort+= deltaXViewPort;
			yViewPort+=deltaYViewPort;
			tailleBloc=futurTailleBloc;
			loupe=!loupe;
			repaint=true;
			notifyObserver();
		}
		
		
	}

}
