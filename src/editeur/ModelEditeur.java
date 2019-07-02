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

import Affichage.Affichage;
import Affichage.Drawable;
import gameConfig.InterfaceConstantes;
import images.ImagesHeros;
import images.ImagesMonstre;
import menu.menuPrincipal.GameHandler;
import menu.menuPrincipal.GameMode;
import partie.bloc.Bloc;
import partie.bloc.Bloc.TypeBloc;
import partie.bloc.Monde;
import serialize.Serialize;

public class ModelEditeur extends AbstractModelEditeur{

	//Variables initialisée dans la fonction dezoom et qui servent à dessiner la zone de zoom 
	private int xZoomArea ; 
	private int yZoomArea; 
	private int xLengthZoomArea;
	private int yLengthZoomArea;

	private int tailleMenu =90;
	
	public ModelEditeur(GameHandler gameHandler){
		super();
		this.gameHandler=gameHandler;
	}
	
	public void moveViewPort(int xpos, int ypos) {
		/*xViewPort est toujours relatif a la taile du bloc d'origine: xViewPort/tailleBlocOrigine = indice dans la matrice
		 * De meme, x delta a des coordonnées dans le meme repere que celle de x view port
		 * */

		int xdelta= (xpos-xStartDrag)*((int)(loupe? (1/dezoomFactor):1));
		int ydelta= (ypos-yStartDrag)*((int)(loupe? (1/dezoomFactor):1));
		int _xViewPort=xViewPort;
		int _yViewPort=yViewPort;

		int toRightXViewPort = ((InterfaceConstantes.WINDOW_WIDTH)*InterfaceConstantes.TAILLE_BLOC/tailleBloc);
		int toDownYViewPort = (InterfaceConstantes.WINDOW_HEIGHT*InterfaceConstantes.TAILLE_BLOC/tailleBloc);
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
		Bloc tempPict =null;
		if(!texture.equals(TypeBloc.NONE))
			tempPict= new Bloc(texture,xBlocPos,yBlocPos,bloquant,background);
		monde.niveau[xBlocPos/InterfaceConstantes.TAILLE_BLOC][yBlocPos/InterfaceConstantes.TAILLE_BLOC]=tempPict;

		repaint=true;
		notifyObserver();
	}
	public void drawMonster(int xpos, int ypos) 
	{		
		int xMonstrePos = calculateDrawPos(xpos,xViewPort);
		int yMonstrePos= calculateDrawPos(ypos,yViewPort);

		if(texture==TypeBloc.SPIREL)
		{
			tabEditeurMonstre.add(new StockageMonstre(TypeBloc.SPIREL,new Point(xMonstrePos,yMonstrePos),staticMonstre));
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

		texture=TypeBloc.NONE;//on remet la souris

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
		int xEndAff=((InterfaceConstantes.WINDOW_WIDTH/tailleBloc+xViewPort/InterfaceConstantes.TAILLE_BLOC)+2);
		xEndAff= ( xEndAff >= InterfaceConstantes.ABS_MAX )? InterfaceConstantes.ABS_MAX : xEndAff;

		int yStartAff = (( (yViewPort/InterfaceConstantes.TAILLE_BLOC-1)<=0)? 0: (yViewPort/InterfaceConstantes.TAILLE_BLOC -1) ); 
		int yEndAff=((InterfaceConstantes.WINDOW_HEIGHT/tailleBloc+yViewPort/InterfaceConstantes.TAILLE_BLOC)+2);
		yEndAff= ( yEndAff >= InterfaceConstantes.ORD_MAX )? InterfaceConstantes.ORD_MAX : yEndAff;

		if(monde.niveau==null){
			monde.niveau= new Bloc[InterfaceConstantes.ABS_MAX][InterfaceConstantes.ORD_MAX];
			//REMOVE monde.initMonde();
		}

		for(int abs=xStartAff;abs<xEndAff;abs++)
		{
			for(int ord=yStartAff;ord<yEndAff;ord++)
			{
				final Bloc tempPict= monde.niveau[abs][ord];
				if(tempPict==null)
					continue;
				//xViewPort= xViewPortNonDécallé - x decallage 
				int xdraw = (int) ((tempPict.getXpos() -xViewPort)*((loupe) ? dezoomFactor : 1 ));
				int ydraw=(int) ((tempPict.getYpos()-yViewPort)*((loupe) ? dezoomFactor : 1 ));
				g.drawImage(imMonde.getImages(tempPict.getType(),loupe),xdraw,ydraw, null);

				//on dessine les limites de la carte 
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.red);

				if((abs==0))
					g2.drawLine(0, 0, 0, InterfaceConstantes.WINDOW_HEIGHT);

				if(abs== (InterfaceConstantes.ABS_MAX-1))
					g2.drawLine(InterfaceConstantes.WINDOW_WIDTH-7, 0, InterfaceConstantes.WINDOW_WIDTH-7, InterfaceConstantes.WINDOW_HEIGHT);

				if( (ord==0))
					g2.drawLine(0, 4, InterfaceConstantes.WINDOW_WIDTH, 4);

				if((ord==(InterfaceConstantes.ORD_MAX-1)) )
				{
					g2.drawLine(0, InterfaceConstantes.WINDOW_HEIGHT-tailleMenu-4, InterfaceConstantes.WINDOW_WIDTH, InterfaceConstantes.WINDOW_HEIGHT-tailleMenu-4);
				}
				g2.setStroke(new BasicStroke(1));
				g2.setColor(Color.black);

			}
		}

		//on dessine l'image du bloc choisi si il existe une texture 
		if(!texture.equals(TypeBloc.NONE))
		{
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/Editeur/"+texture +((loupe) ? "_p" : "" )+".png"));
			if(texture.equals(TypeBloc.DELETE))
			{
				g.drawImage(image, xMousePos-10, yMousePos-7, null);
			}
			else
			{
				g.drawImage(image, xMousePos-tailleBloc/2, yMousePos-tailleBloc/2, null);
			}

		}
		//on affiche les lignes
		for(int abs=0;abs<InterfaceConstantes.WINDOW_WIDTH/tailleBloc+1;abs++)
		{
			for(int ord=0;ord<InterfaceConstantes.WINDOW_HEIGHT/tailleBloc+1;ord++)
			{
				//a la différence du bloc, la ligne n'est pas décallé de base, il faut donc la décaller pour la faire
				//correspondre à la vue 

				//ligne verticale 
				g.drawLine(abs*tailleBloc+xdecalLine,ord*tailleBloc,abs*tailleBloc+xdecalLine,InterfaceConstantes.WINDOW_HEIGHT+ord*tailleBloc+tailleBloc);
				//ligne horizontale  
				g.drawLine(abs*tailleBloc,ord*tailleBloc+ydecalLine,InterfaceConstantes.WINDOW_WIDTH+abs*tailleBloc+tailleBloc ,ord*tailleBloc+ydecalLine);
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
		String monsterName = "";
		for(int i=0; i<tabEditeurMonstre.size(); i++)
		{
			monsterName = tabEditeurMonstre.get(i).type.toString().toLowerCase();
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource(ImagesMonstre.path+ monsterName+ "/"+monsterName+((loupe) ? "_p" : "" )+".png"));
			g.drawImage(image, (int)((tabEditeurMonstre.get(i).pos.x -xViewPort)*((loupe) ? dezoomFactor : 1 )),(int)((tabEditeurMonstre.get(i).pos.y -yViewPort)*((loupe) ? dezoomFactor : 1 )), null);
		}

		//on dessine le personnage 
		if(persoPos[0]!=-1)
		{
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/editeur/heros"+((loupe) ? "_p" : "" )+".png"));
			g.drawImage(image, (int)((persoPos[0] -xViewPort)*((loupe) ? dezoomFactor : 1 )),(int)((persoPos[1] -yViewPort)*((loupe) ? dezoomFactor : 1 )), null);
		}
		//on dessine le début
		if(startPos[0]!=-1)
		{
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/editeur/start"+((loupe) ? "_p" : "" )+".png"));
			g.drawImage(image,(int)( (startPos[0] -xViewPort)*((loupe) ? dezoomFactor : 1 )),(int)((startPos[1] -yViewPort)*((loupe) ? dezoomFactor : 1) ), null);
		}
		//on dessine la fin
		if(endPos[0]!=-1)
		{
			image = pan.getToolkit().getImage(getClass().getClassLoader().getResource("resources/editeur/end"+((loupe) ? "_p" : "" )+".png"));
			g.drawImage(image, (int)((endPos[0] -xViewPort)*((loupe) ? dezoomFactor : 1 )),(int)((endPos[1] -yViewPort)*((loupe) ? dezoomFactor : 1 )), null);
		}

	}

	public void setTexture(TypeBloc _texture)
	{
		perso=false;
		start=false;
		end=false;
		monstreActive=false;
		System.out.println(_texture);
		texture=_texture;
		if(_texture.equals(""))
		{
			menuEdit.setBloquant(false);
			setBackground(false);
		}
		else if (_texture.equals(TypeBloc.DELETE))
		{
			menuEdit.setBloquant(false);
		}
		/*REMOVE else if (_texture.equals(TypeBloc.VIDE))
		{
			menuEdit.setBloquant(false);
		}*/
		else if (_texture.equals(TypeBloc.SOL))
		{
			menuEdit.setBloquant(true);
		}
		else if (_texture.equals(TypeBloc.TERRE))
		{
			menuEdit.setBloquant(true);
		}
		else if (_texture.equals(TypeBloc.CIEL))
		{
			menuEdit.setBloquant(false);
		}
		else if (_texture.equals(TypeBloc.PERSO))
		{
			perso=true;
		}
		else if (_texture.equals(TypeBloc.START))
		{
			start=true;
		}
		else if (_texture.equals(TypeBloc.END))
		{
			end=true;
		}
		else if (_texture.equals(TypeBloc.SPIREL))
		{
			monstreActive=true;
			showStaticMonsters=true;
		}

		repaint=true;
		notifyObserver();
	}

	public List<StockageMonstre> FindMonstre(Point targetPos, List<StockageMonstre> listToSearch )
	{
		List<StockageMonstre> res = new ArrayList<StockageMonstre>();
		res.clear();
		for(int i=0; i< listToSearch.size(); i++)
		{
			if(listToSearch.get(i).pos.equals(targetPos))
			{
				res.add(listToSearch.get(i));
			}
		}
		return(res);
	}


	public void deleteMonster(int x, int y)
	{
		//int xPos = (x/tailleBloc)*tailleBloc+xViewPort;
		//int yPos= (y/tailleBloc)*tailleBloc+yViewPort;
		int xPos=calculateDrawPos(x,xViewPort);
		int yPos=calculateDrawPos(y,yViewPort);

		//on chercher tout les monstres dedans et on stock leur nom dans la liste
		monstreDansCase=FindMonstre(new Point(xPos,yPos),tabEditeurMonstre)	;
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
	@SuppressWarnings("unused")
	public void charger(String nom) 
	{
		Monde _monde = Serialize.charger(nom,null);		
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
		info+= "xViewPort= " + xViewPort +","+(xViewPort+InterfaceConstantes.WINDOW_WIDTH );
		info+= "\nyViewPort= "+ yViewPort+","+(yViewPort+InterfaceConstantes.WINDOW_HEIGHT -tailleMenu);
		info+="\nAbscisse max= " +InterfaceConstantes.ABS_MAX*100;
		info+= "\nOrdonnee max= "+ InterfaceConstantes.ORD_MAX*100;
		info+="\nBloquant= "+bloquant;
		info+="\nBackground= "+ background ;
		info+="\nTexture= "+ texture;
		info+="\nLargeur fenetre= "+InterfaceConstantes.WINDOW_WIDTH ;
		info+="\nHauteur fenetre= "+InterfaceConstantes.WINDOW_HEIGHT;

		showMessageDialog=true;
		textMessageDialog[0]=info;
		textMessageDialog[1]="A propos";
		typeMessageDialog= JOptionPane.INFORMATION_MESSAGE;

		notifyObserver();
	}

	public void dezoom() {

		int futurTailleBloc=  (int)(tailleBloc  * (!loupe? dezoomFactor: (1.0f/dezoomFactor)));
		int futurNombreBlocL= InterfaceConstantes.WINDOW_WIDTH/futurTailleBloc;
		int futurNombreBlocH= InterfaceConstantes.WINDOW_HEIGHT/futurTailleBloc;

		int deltaXViewPort = (InterfaceConstantes.WINDOW_WIDTH/tailleBloc - futurNombreBlocL ) /2 * InterfaceConstantes.TAILLE_BLOC;
		int deltaYViewPort= (InterfaceConstantes.WINDOW_HEIGHT/tailleBloc - futurNombreBlocH ) /2 * InterfaceConstantes.TAILLE_BLOC; 

		boolean xMinLim= ((xViewPort+deltaXViewPort)<0) ;
		boolean xMaxLim=( ((xViewPort+deltaXViewPort)/InterfaceConstantes.TAILLE_BLOC+futurNombreBlocL) > InterfaceConstantes.ABS_MAX );
		boolean yMinLim=((yViewPort+deltaYViewPort)<0) ;
		boolean yMaxLim=( ((yViewPort+deltaYViewPort)/InterfaceConstantes.TAILLE_BLOC+futurNombreBlocH) > InterfaceConstantes.ORD_MAX );
		//on memorise les valeurs pour tracer la zone de dezoom 
		if(!loupe)//si on est en train de dezoomer 
		{
			xZoomArea=-1*deltaXViewPort; 
			yZoomArea=-1*deltaYViewPort; 
			xLengthZoomArea=(int) (InterfaceConstantes.WINDOW_WIDTH*dezoomFactor);
			yLengthZoomArea=(int) (InterfaceConstantes.WINDOW_HEIGHT*dezoomFactor);
		}

		if( (InterfaceConstantes.ABS_MAX < futurNombreBlocL) || (InterfaceConstantes.ORD_MAX<futurNombreBlocH)  )
		{
			showMessageDialog=true;
			textMessageDialog[0]="Dezoom impossible: taille du monde trop petit";
			textMessageDialog[1]="Erreur dezoom";
			typeMessageDialog=JOptionPane.ERROR_MESSAGE;
			notifyObserver();
			return;
		}

		else if(!loupe &&(xMinLim||yMinLim||xMaxLim ||yMaxLim))
		{			
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
	
	@Override
	public void doComputations(Affichage affich){
		//As this mode is controlled by listeners, the computationDone is set to false when a listener is triggered. This function is then left empty
	}
	@Override
	public void updateGraphics(){
		this.notifyMainObserver();
	}
	@Override
	public boolean isComputationDone(){
		return computationDone;
	}
	@Override
	public boolean isGameModeLoaded()
	{
		//loading not required 
		return true;
	}
	@Override
	public GameMode getLoaderGameMode(){
		//loading not required 
		return null;
	}
}
