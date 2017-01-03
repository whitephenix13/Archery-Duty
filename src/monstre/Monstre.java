package monstre;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import partie.AbstractModelPartie;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Monde;
import types.Vitesse;
import deplacement.Mouvement;


@SuppressWarnings("serial")
public abstract class Monstre implements InterfaceConstantes, Serializable{
	//on définit la position du coin en haut à  gauche de la hitbox
	public int xPos ;
	public int yPos ;
	public int anim;
	public String nom ;
	
	
	public Mouvement deplacement ;
	public Vitesse vit;
	public int slowDownFactor= 3;//6

	public boolean finSaut;
	public boolean peutSauter;
	public boolean glisse;
	
	public boolean actionReussite;
	public int reaffiche;
	public boolean doitChangMouv;
	public Mouvement nouvMouv;
	public int nouvAnim;
	
	public List<Integer> xDecallagePlacementTir= new ArrayList<Integer>(8);
	public List<Integer> yDecallagePlacementTir=  new ArrayList<Integer>(8);
	
	Image SPattente0; 
	Image SPattente1;
	
	Image SPmarche0;
	Image SPmarche1;
	Image SPmarche2;
	Image SPmarche3; 
	
	private int life=MAXLIFE;
	/**
	 * Permet de savoir de quel cote est tourné le monstre
	 * 
	 * @param anim, l'animation du monstre
	 * 
	 * @return String , "Droite" ou "Gauche", direction dans laquelle le monstre est tourné
	 */
	public abstract String droite_gauche (int anim);  
	/**
	 * Gère l'ensemble des événements lié au deplacement d'un monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours 
	 */	
	public abstract void deplace (List<TirMonstre> tabTirMonstre,Monstre monstre, Heros heros,AbstractModelPartie partie);
	/**
	 * Ralenti les animations  
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le monstre
	 */	
	public abstract int setReaffiche();
	/**
	 * IA pour le deplacement du monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param Monde, le niveau en cours 
	 */	
	public abstract void changeMouv (Monstre monstre,AbstractModelPartie partie);
	/**
	 * Regle la vitesse du monstre en fonction de son mouvement et de son animation
	 * 
	 * @param monstre, le monstre a deplacer 
	 */	
	public abstract void setSpeed(Monstre monstre);
	/**
	 * Effectue les decallages entre l'ancien sprite et le nouveau sprite pour que les deux images concordent niveau positionnement
	 * 
	 * @param monstre, le monstre a deplacer
	 * @param depSuiv, le futur mouvement
	 * @param animActuel, l'animation du heros actuellement
	 * @param animSuiv, la future animation
	 * @param blocDroitGlisse, savoir si le bloc a droite du sprite est bloquant
	 * @param blocGaucheGlisse, savoir si le bloc a gauche du sprite est bloquant
	 * @param Monde, le niveau en cours 
	 * 
	 * @return si le decallage est possible ou non 
	 * 
	 */	
	public boolean decallageMonstre(Monstre monstre,Mouvement depSuiv, int animActuel, int animSuiv, boolean blocDroitGlisse, boolean blocGaucheGlisse, AbstractModelPartie partie) {
		
		Mouvement depPrec= monstre.deplacement;
		boolean autorise;
		int depXgauche=0;
		int depXdroit=0;
		int depYbas=0;
		int depYhaut=0;

		depYbas= depPrec.ydecallsprite.get(animActuel) - depSuiv.ydecallsprite.get(animSuiv)  +depPrec.yhitbox.get(animActuel)-depSuiv.yhitbox.get(animSuiv);
		depYhaut=depPrec.ydecallsprite.get(animActuel) -depSuiv.ydecallsprite.get(animSuiv);
		depXgauche= depPrec.xdecallsprite.get(animActuel) -depSuiv.xdecallsprite.get(animSuiv);
		depXdroit = depPrec.xdecallsprite.get(animActuel) - depSuiv.xdecallsprite.get(animSuiv)  +depPrec.xhitbox.get(animActuel)-depSuiv.xhitbox.get(animSuiv);
		
		
		/*==========================  ***-> GLISSADE ==============================*/
		if(!depPrec.getClass().getName().equals("deplacement.Glissade") && depSuiv.getClass().getName().equals("deplacement.Glissade"))
		{ 
			if(blocGaucheGlisse)
			{
				
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie,depXgauche, depYbas);
				if(autorise)
					{
					monstre.xPos+=depXgauche;
					monstre.yPos+=depYbas; 
					return(autorise);
					}

				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie,depXgauche, depYhaut);
				if(autorise)
				{
				monstre.xPos+=depXgauche;monstre.yPos+=depYhaut; 
				return(autorise);
				}
					
				//on place le sprite en faisant concorder les bords haut droit
				autorise=monstreBloque(monstre,depSuiv,animSuiv,partie, depXdroit, depYhaut);
				if(autorise)
				{
				monstre.xPos+=depXdroit;monstre.yPos+=depYhaut;
				return(autorise);
				}
				
				//on place le sprite en faisant concorder les bords bas droit
				autorise=monstreBloque(monstre,depSuiv,animSuiv,partie, depXdroit, depYbas);
				if(autorise)
				{
				monstre.xPos+=depXdroit;monstre.yPos+=depYbas;
					return(autorise);
					}
				
				else {return(false);}
				
				
			}
			else  
			{
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie,depXdroit, depYbas);
				if(autorise)
				{
					monstre.xPos+=depXdroit;monstre.yPos+=depYbas;
					return(autorise);
					}
				
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie,depXdroit, depYhaut);
				if(autorise)
				{
				monstre.xPos+=depXdroit;monstre.yPos+=depYhaut;
				return(autorise);
				}
				
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie, depXgauche, depYbas);
				if(autorise)
				{
					monstre.xPos+=depXgauche;monstre.yPos+=depYbas;
					return(autorise);
					}
				
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie,  depXgauche, depYhaut);
				if(autorise)
				{
					monstre.xPos+=depXgauche;monstre.yPos+=depYhaut;
					return(autorise);
					}
				
				else {return(false);}
				
			}
		}
		/*==========================  GLISSADE-> ***  ==============================*/
		else if(depPrec.getClass().getName().equals("deplacement.Glissade") && !depSuiv.getClass().getName().equals("deplacement.Glissade" ))
		{
			if(monstre.droite_gauche(animActuel).equals("Gauche"))
			{
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie, depXdroit, depYbas);
				if(autorise)
				{
					monstre.xPos+=depXdroit;monstre.yPos+=depYbas;
					return(autorise);
				}
				
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie, depXdroit, depYhaut);
				if(autorise)
				{
					monstre.xPos+=depXdroit;monstre.yPos+=depYhaut;
					return(autorise);
					}
				
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie, depXgauche, depYbas);
				if(autorise)
				{
					monstre.xPos+=depXgauche;monstre.yPos+=depYbas;
					return(autorise);
					}
				
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie,  depXgauche, depYhaut);
				if(autorise)
				{
					monstre.xPos+=depXgauche;monstre.yPos+=depYhaut;
					return(autorise);
					}
				
				else {return(false);}
				
				
			}
			else  
			{
				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie, depXgauche, depYbas);
				if(autorise)
				{
					monstre.xPos+=depXgauche;monstre.yPos+=depYbas;
					return(autorise);
					}

				autorise =monstreBloque(monstre,depSuiv,animSuiv,partie, depXgauche, depYhaut);
				if(autorise)
				{
					monstre.xPos+=depXgauche;monstre.yPos+=depYhaut;
					return(autorise);
					}
				
				autorise=monstreBloque(monstre,depSuiv,animSuiv,partie,  depXdroit, depYhaut);
				if(autorise)
				{
					monstre.xPos+=depXdroit;
					monstre.yPos+=depYhaut;
					return(autorise);
					}
				
				autorise=monstreBloque(monstre,depSuiv,animSuiv,partie,  depXdroit, depYbas);
				if(autorise)
				{
					monstre.xPos+=depXdroit;
					monstre.yPos+=depYbas;
					return(autorise);
					}
				
				else {return(false);}
				
				
			}
		}
		else {
		//on place le sprite en faisant concorder les bords bas gauche
		autorise =monstreBloque(monstre,depSuiv,animSuiv,partie, depXgauche, depYbas);
		if(autorise)
		{
			monstre.xPos+=depXgauche;monstre.yPos+=depYbas;
			return(autorise);
			}
		
		//on place le sprite en faisant concorder les bords bas droit
		autorise=monstreBloque(monstre,depSuiv,animSuiv,partie,  depXdroit, depYbas);
		if(autorise)
		{
			monstre.xPos+=depXdroit;
			monstre.yPos+=depYbas;
			return(autorise);
			}
			
		//on place le sprite en faisant concorder les bords haut gauche
		autorise =monstreBloque(monstre,depSuiv,animSuiv,partie,  depXgauche, depYhaut);
		if(autorise)
		{
			monstre.xPos+=depXgauche;
			monstre.yPos+=depYhaut;
			return(autorise);
			}
		
		//on place le sprite en faisant concorder les bords haut droit
		autorise=monstreBloque(monstre,depSuiv,animSuiv,partie,  depXdroit, depYhaut);
		if(autorise)
		{
			monstre.xPos+=depXdroit;
			monstre.yPos+=depYhaut;
			return(autorise);
			}
		
		
		
		else {return(false);}
		}

	}
	/**
	 * Permet de savoir si, étant donné une animation et un mouvement, un décallage bloque le heros ou non
	 * 
	 * @param monstre, le monstre a deplacer
	 * @param depSuiv, le futur mouvement
	 * @param animSuiv, la future animation
	 * @param Monde, le niveau en cours 
	 * @param depX, decallage du sprite selon X
	 * @param depY, decallage du sprite selon Y
	 * 
	 * @return etant donné un sprite et un decallage effectué, renvoie vrai si le sprite n'est pas bloqué après decallage
	 * 
	 */	
	public boolean monstreBloque(Monstre monstre,Mouvement depSuiv, int animSuiv, AbstractModelPartie partie, int depX, int depY){
		Monde monde = partie.monde;
		int INIT_ABS_RECT = partie.INIT_RECT.x;
		int INIT_ORD_RECT= partie.INIT_RECT.y;
		boolean db= monde.niveau[(int) ((monstre.xPos + depX + depSuiv.xdecallsprite.get(animSuiv)+ depSuiv.xhitbox.get(animSuiv)    +INIT_ABS_RECT)/100)][(int) ((monstre.yPos + depY+depSuiv.ydecallsprite.get(animSuiv)+     depSuiv.yhitbox.get(animSuiv)+INIT_ORD_RECT)/100)].getBloquer();
		boolean gb= monde.niveau[(int) ((monstre.xPos + depX + depSuiv.xdecallsprite.get(animSuiv)                               +INIT_ABS_RECT)/100)][(int) ((monstre.yPos + depY+depSuiv.ydecallsprite.get(animSuiv)+     depSuiv.yhitbox.get(animSuiv)+INIT_ORD_RECT)/100)].getBloquer();
		
		boolean dh= monde.niveau[(int) ((monstre.xPos + depX + depSuiv.xdecallsprite.get(animSuiv)+ depSuiv.xhitbox.get(animSuiv)    +INIT_ABS_RECT)/100)][(int) ((monstre.yPos + depY+depSuiv.ydecallsprite.get(animSuiv)                               +INIT_ORD_RECT)/100)].getBloquer();
		boolean gh= monde.niveau[(int) ((monstre.xPos + depX + depSuiv.xdecallsprite.get(animSuiv)                               +INIT_ABS_RECT)/100)][(int) ((monstre.yPos + depY+depSuiv.ydecallsprite.get(animSuiv)                               +INIT_ORD_RECT)/100)].getBloquer();
		return(!db && !gb && !dh && !gh);
	}
	
	/**
	 * getter
	 */	
	public int getLife(){return(life);};
	/**
	 * accesseur permettant de gérer la vie max et la vie mimimale
	 */	
	public void addLife(int degats)
	{
		if((life+degats)<MINLIFE)
		{
			life=MINLIFE;
		}
		else if ((life+degats)>MAXLIFE)
		{
			life=MAXLIFE;
		}
		else
		{
			life+=degats;
		}
	}
}


