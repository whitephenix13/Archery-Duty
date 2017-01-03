package monstre;

import java.util.Arrays;
import java.util.List;

import partie.AbstractModelPartie;
import personnage.Heros;
import types.Vitesse;
import deplacement.Attente;
import deplacement.Marche;
import deplacement.Saut;

@SuppressWarnings("serial")
public class Spirel extends Monstre{
	//timer pour que le monstre agisse toutes les (maximum 300ms )
	
	private long tempsAncienMouv=0;
	private static long delaiMouv= 400;
	private long tempsAncienTir=0;
	private static long delaiTir= 2000;
	private int distanceAttaque= 400;
	private boolean cooldown=true;
	
	private boolean peutSauter = true;
	private boolean sautDroit= false;
	private boolean sautGauche= false;

	public boolean staticSpirel=false;
	
	/**
	 * constructeur
	 * 
	 * @param xPo, position originale en x
	 * @param yPo, position originale en y
	 * @param _staticSpirel, permet de rendre la spirel immobile (si =true)
	 */	
	public Spirel( int xPo,int yPo,boolean _staticSpirel){
		nom="spirel";
		
		staticSpirel=_staticSpirel;
		
		xPos = xPo;
		yPos = yPo; 
		vit=new Vitesse(0,0);
		deplacement=new Attente(nom) ;
		anim=1;
		tempsAncienMouv= System.nanoTime();
		actionReussite=false;
		reaffiche=0;
		
		finSaut=false;
		peutSauter=false;
		glisse=false;
		reaffiche=0;
		//									 G ,BG,B ,BD,D ,HD,H ,HG
		xDecallagePlacementTir= Arrays.asList(-60,0 ,0 ,0 ,30,0 ,20,0);
		yDecallagePlacementTir= Arrays.asList(0 ,0 ,0 ,0 ,0,0 ,-100,0);
	}
	/**
	 * Permet de savoir de quel cote est tourné le monstre
	 * 
	 * @param anim, l'animation du monstre
	 * 
	 * @return String , "Droite" ou "Gauche", direction dans laquelle le monstre est tourné
	 */
	public String droite_gauche (int anim)
	{
		if(this.deplacement.getClass().getName().equals("deplacement.Marche"))
		{
		if(anim <2)
		{
			return ("Gauche");
		}
		else return("Droite");
		}
	else if(this.deplacement.getClass().getName().equals("deplacement.Attente"))
	{
		if(anim <1)
		{
			return ("Gauche");
		}
		else return("Droite");
	}
	else if(this.deplacement.getClass().getName().equals("deplacement.Saut"))
	{
		if(anim <1)
		{
			return ("Gauche");
		}
		else return("Droite");
	}

	else 
		throw new IllegalArgumentException("Spirel/droite_gauche: ERREUR deplacement inconnu");
	}
	/**
	 * Gère l'ensemble des événements lié au deplacement d'un monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param heros, le personnage jouable
	 * @param Monde, le niveau en cours 
	 */		
	public  void deplace ( List<TirMonstre> tabTirMonstre,Monstre monstre, Heros heros,AbstractModelPartie partie)
	{
	
		boolean herosAGauche;
		//On test le cooldown de tir
		if((System.nanoTime()-tempsAncienTir)*Math.pow(10, -6)>delaiTir * (partie.slowDown ? 2:1) )
		{
			cooldown=false;
			tempsAncienTir=System.nanoTime();
		}
		//on test le cooldown de mouvement
		if((System.nanoTime()-tempsAncienMouv)*Math.pow(10, -6)>delaiMouv * (partie.slowDown ? 2:1))
		{
			herosAGauche= monstre.xPos-(heros.xpos-partie.xdeplaceEcran-partie.xdeplaceEcranBloc)>=0;
			
			int herosXCentre= heros.xpos + heros.deplacement.xtaille.get(heros.anim)/2 -partie.xdeplaceEcran-partie.xdeplaceEcranBloc;
			int herosYCentre= heros.ypos + heros.deplacement.ytaille.get(heros.anim)/2 -partie.ydeplaceEcran-partie.ydeplaceEcranBloc;

			int monstreXCentre = monstre.xPos+monstre.deplacement.xtaille.get(anim)/2;
			int monstreYCentre = monstre.yPos+monstre.deplacement.ytaille.get(anim)/2;
			
			int deltaX= Math.abs(monstreXCentre-herosXCentre);
			int deltaY= Math.abs(monstreYCentre-herosYCentre);
			
			//on test si le heros est dans le cercle d'attaque
			if( (deltaX+deltaY)<distanceAttaque && ! cooldown)
			{
					//animation d'attente
					monstre.doitChangMouv= !((monstre.deplacement.getClass().getName().equals("deplacement.Attente")) 
							&& (herosAGauche? monstre.anim==0 : monstre.anim==1));
					monstre.nouvAnim=herosAGauche? 0 : 1;
					monstre.nouvMouv= new Attente(this.nom);
					
				changeMouv (monstre,partie);
					//envoie du projectile
				if(monstre.droite_gauche(monstre.anim).equals("Gauche"))
				{
					//tir à gauche, anim=0
					tabTirMonstre.add(new TirSpirel((monstre.xPos+xDecallagePlacementTir.get(0)),(monstre.yPos+yDecallagePlacementTir.get(0)),0));	
				}
				else
				{
					//tir à droite, anim=4
					tabTirMonstre.add(new TirSpirel((monstre.xPos+xDecallagePlacementTir.get(4)),(monstre.yPos+yDecallagePlacementTir.get(4)),4));	

				}
				//tir en haut, anim= 6
				tabTirMonstre.add(new TirSpirel((monstre.xPos+xDecallagePlacementTir.get(6)),(monstre.yPos+yDecallagePlacementTir.get(6)),6));	
				cooldown=true;
					
			}
			else
			{
				if(!staticSpirel)
				{
					//sinon on se rapproche ou on reste proche 
					int xG= monstre.xPos+ monstre.deplacement.xdecallsprite.get(monstre.anim);
				  	int xD=xG+ monstre.deplacement.xhitbox.get(monstre.anim);
				    int yH= monstre.yPos+ monstre.deplacement.ydecallsprite.get(monstre.anim);
				  	int yB=yH+monstre.deplacement.yhitbox.get(monstre.anim);
				  	int INIT_ABS_RECT = partie.INIT_RECT.x;
				  	int INIT_ORD_RECT = partie.INIT_RECT.y;

					boolean blocGauche= partie.monde.niveau[(int) (xG-1+INIT_ABS_RECT)/100][(int) (yB+INIT_ORD_RECT)/100].getBloquer()==true;
					boolean blocDroit= partie.monde.niveau[(int) (xD+1+INIT_ABS_RECT)/100][(int) (yB+INIT_ORD_RECT)/100].getBloquer()==true;
					boolean blocGaucheHaut= partie.monde.niveau[(int) (xG-1+INIT_ABS_RECT)/100][(int) (yB+INIT_ORD_RECT)/100-1].getBloquer()==true;
					boolean blocDroitHaut= partie.monde.niveau[(int) (xD+1+INIT_ABS_RECT)/100][(int) (yB+INIT_ORD_RECT)/100-1].getBloquer()==true;
					boolean blocGaucheBas= partie.monde.niveau[(int) (xG-1+INIT_ABS_RECT)/100][(int) (yB+INIT_ORD_RECT)/100+1].getBloquer()==true;
					boolean blocDroitBas= partie.monde.niveau[(int) (xD+1+INIT_ABS_RECT)/100][(int) (yB+INIT_ORD_RECT)/100+1].getBloquer()==true;
	
					
					boolean blocGauchePied= blocGauche;
					boolean blocGaucheTete= partie.monde.niveau[(int) (xG-1+INIT_ABS_RECT)/100][(int) (yH+INIT_ORD_RECT)/100].getBloquer()==true;;
					boolean blocDroitPied= blocDroit;
					boolean blocDroitTete= partie.monde.niveau[(int) (xD+1+INIT_ABS_RECT)/100][(int) (yH+INIT_ORD_RECT)/100].getBloquer()==true;;
					
					//on saute au dessus d'un obstacle si possible
					if( (monstre.droite_gauche(anim).equals("Gauche")? (blocGauche && !blocGaucheHaut) : (blocDroit && !blocDroitHaut) ) && peutSauter)
					{
						
						monstre.doitChangMouv=!monstre.deplacement.getClass().getName().equals("deplacement.Saut");
						monstre.nouvAnim=(herosAGauche? 0 : 1);
						monstre.nouvMouv= new Saut(this.nom);
						changeMouv (monstre,partie);
					}
					//si on est en l'air, on se deplace
					else if (monstre.deplacement.getClass().getName().equals("deplacement.Saut"))
					{
						//si on peut se deplacer sur le coté
						if(monstre.droite_gauche(anim).equals("Gauche")?(!blocGauchePied && !blocGaucheTete) :(!blocDroitPied && !blocDroitTete))
						{
							
							
							if(herosAGauche)
							{
								sautGauche=true;
								sautDroit=false;
							}
							else
							{
								sautGauche=false;
								sautDroit=true;
							}
							monstre.doitChangMouv=!(herosAGauche? monstre.anim==0 : monstre.anim==1);
							monstre.nouvAnim=(herosAGauche? 0 : 1);
							monstre.nouvMouv= new Saut(this.nom);
							changeMouv (monstre,partie);
						}
						else
						{
							sautGauche=false;
							sautDroit=false;
						}
						
					}
					//Si il y a un trou a coté du monstre
					else if((monstre.droite_gauche(anim).equals("Gauche")? (!blocGaucheBas) : (!blocDroitBas) ))
					{
						int decision = (int) (Math.random()*100);
						//on attend 
						if(decision <= 70)
						{
							//animation d'attente
							monstre.doitChangMouv= !((monstre.deplacement.getClass().getName().equals("deplacement.Attente")) 
									&& (herosAGauche? monstre.anim==0 : monstre.anim==1));
							monstre.nouvAnim=herosAGauche? 0 : 1;
							monstre.nouvMouv= new Attente(this.nom);
							
						changeMouv (monstre,partie);
						}
						//on se deplace dans l'autre direction
						else if(decision <= 90)
						{
							monstre.doitChangMouv=true;
							//on change de direction
							monstre.nouvAnim=(herosAGauche? 2 : 0);
							monstre.nouvMouv= new Marche(this.nom);
							changeMouv (monstre,partie);
						}
						//le monstre tombe en marchant
						else 
						{
							monstre.doitChangMouv=!((monstre.deplacement.getClass().getName().equals("deplacement.Marche")) 
									&& (herosAGauche? monstre.anim<2 : monstre.anim>=2));
							monstre.nouvAnim=(herosAGauche? 0 : 2);
							monstre.nouvMouv= new Marche(this.nom);
							changeMouv (monstre,partie);
						}
					}
					//sinon on se deplace
					else
					{
					int decision = (int) (Math.random()*100);
					//deplacement
					if(decision <= 90)
					{
						monstre.doitChangMouv=!((monstre.deplacement.getClass().getName().equals("deplacement.Marche")) 
								&& (herosAGauche? monstre.anim<2 : monstre.anim>=2));
						monstre.nouvAnim=(herosAGauche? 0 : 2);
						monstre.nouvMouv= new Marche(this.nom);
						changeMouv (monstre,partie);
					}
					//attente
					else
					{
						//animation d'attente
						monstre.doitChangMouv= !((monstre.deplacement.getClass().getName().equals("deplacement.Attente")) 
								&& (herosAGauche? monstre.anim==0 : monstre.anim==1));
						monstre.nouvAnim=herosAGauche? 0 : 1;
						monstre.nouvMouv= new Attente(this.nom);
					}
				}
		
				}
				else
				{
					//animation d'attente
					monstre.doitChangMouv= !((monstre.deplacement.getClass().getName().equals("deplacement.Attente")) 
							&& (herosAGauche? monstre.anim==0 : monstre.anim==1));
					monstre.nouvAnim=herosAGauche? 0 : 1;
					monstre.nouvMouv= new Attente(this.nom);
				}	
			}
			tempsAncienMouv=System.nanoTime();
		}
		else
		{
			//on continue le mouvement précédant
			monstre.doitChangMouv=false;
			changeMouv (monstre,partie);
		}
 
	}

	/**
	 * IA pour le deplacement du monstre 
	 * 
	 * @param monstre, le monstre a deplacer 
	 * @param Monde, le niveau en cours 
	 */	
	public void changeMouv (Monstre monstre,AbstractModelPartie partie)
	{
		//si le monstre est en l'air, en lui met l'animation d'attente
		int xG = monstre.xPos+monstre.deplacement.xdecallsprite.get(monstre.anim);
		int xD = monstre.xPos+monstre.deplacement.xdecallsprite.get(monstre.anim)+monstre.deplacement.xhitbox.get(monstre.anim);
		int yB = monstre.yPos+monstre.deplacement.ydecallsprite.get(monstre.anim)+monstre.deplacement.yhitbox.get(monstre.anim);
		
		boolean blocDessousGauche= partie.monde.niveau[(int) (xG+partie.INIT_RECT.x)/100][(int) (yB+1+partie.INIT_RECT.y)/100].getBloquer()==true;
		boolean blocDessousDroit= partie.monde.niveau[(int) (xD+partie.INIT_RECT.x)/100][(int) (yB+1+partie.INIT_RECT.y)/100].getBloquer()==true;
		
		boolean herosAGauche= monstre.xPos-(partie.heros.xpos-partie.xdeplaceEcran-partie.xdeplaceEcranBloc)>=0;
		
		//atterrissage
		if((blocDessousGauche || blocDessousDroit) && monstre.deplacement.getClass().getName().equals("deplacement.Saut"))
		{
			monstre.anim=herosAGauche? 0 : 1;
			monstre.deplacement= new Attente(this.nom);
			setSpeed(monstre);
			peutSauter=true;
			sautDroit=false;
			sautGauche=false;
		}
		//chute
		else if(!blocDessousGauche && ! blocDessousDroit && !(monstre.vit.y<0))
		{
			//le monstre tombe, on met donc son animation de saut
			monstre.anim=herosAGauche? 0 : 1;
			monstre.deplacement= new Saut(this.nom);
			monstre.vit.x=0;
		}
		//on execute l'action voulue
		else
		{
		if(doitChangMouv)
		{
			monstre.actionReussite= (decallageMonstre(monstre,monstre.nouvMouv,monstre.anim,monstre.nouvAnim,false,false,partie));
			if(monstre.actionReussite)
			{
				monstre.deplacement= monstre.nouvMouv;
				monstre.anim=nouvAnim;
				setSpeed(monstre);
				//si on commence le saut 
				if(monstre.nouvMouv.getClass().getName().equals("deplacement.Saut") )
				{
					peutSauter=false;
				}
				
			}
			else
			{
				monstre.deplacement= new Attente("spirel");
				monstre.anim=monstre.droite_gauche(anim).equals("Gauche") ? 0 :1;
				setSpeed(monstre);
				
			}
			
			
		}
		else 
		{
			if(monstre.deplacement.getClass().getName().equals("deplacement.Marche"))
			{
				monstre.nouvAnim=(monstre.droite_gauche(anim).equals("Gauche") ? (anim+1)%2 :(anim+1)%2+2 );
				monstre.actionReussite= (decallageMonstre(monstre,monstre.deplacement,monstre.anim,monstre.nouvAnim,false,false,partie));
				if(monstre.actionReussite)
				{
				monstre.anim=monstre.nouvAnim;
				setSpeed(monstre);
				}
				else
				{
					monstre.deplacement= new Attente("spirel");
					monstre.anim=monstre.droite_gauche(anim).equals("Gauche") ? 0 :1;
					setSpeed(monstre);
				}
			}
			else if (monstre.deplacement.getClass().getName().equals("deplacement.Saut"))
			{
				setSpeed(monstre);
			}
		}
		}
		monstre.doitChangMouv=false;
	}
	/**
	 * Regle la vitesse du monstre en fonction de son mouvement et de son animation
	 * 
	 * @param monstre, le monstre a deplacer 
	 */	
	public void setSpeed(Monstre monstre)
	{
		if(monstre.deplacement.getClass().getName().equals("deplacement.Attente"))
		{
			monstre.vit.x=0;
			monstre.vit.y=0;
		}
		else if(monstre.deplacement.getClass().getName().equals("deplacement.Marche"))
		{
			int speed=10000;//4000
			if(anim<2)
			{
				monstre.vit.x=-1*speed;
			}
			else
			{
				monstre.vit.x= speed;
			}
		}
		else if(monstre.deplacement.getClass().getName().equals("deplacement.Saut"))
		{
			int xspeed=10000;
			int yspeed=15000;
			
			if(peutSauter)
			{
				monstre.vit.y=-1*yspeed;
			}
			if(sautGauche && ! sautDroit)
			{
				monstre.vit.x= -1*xspeed;
			}
			if(sautDroit && ! sautGauche)
			{
				monstre.vit.x= xspeed;
			}

		}
		
	}	
	/**
	 * Ralenti les animations  
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le monstre
	 */	
	public int setReaffiche(){
	
		if(this.deplacement.getClass().getName().equals("deplacement.Attente")){
			return(20);
		}
		else if(this.deplacement.getClass().getName().equals("deplacement.Marche")){
			return(50);
		}

		else if(this.deplacement.getClass().getName().equals("deplacement.Saut")){
			return(50);
		}
		else if(this.deplacement.getClass().getName().equals("deplacement.Tir")){
			return(50);
		}
		else {
			throw new IllegalArgumentException("ERREUR setReaffiche monstre, ACTION INCONNUE  "  +this.deplacement.getClass().getName());
		}
	}

}
