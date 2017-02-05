package deplacement;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import partie.AbstractModelPartie;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Hitbox;

public class Deplace implements InterfaceConstantes{
	public Collision colli;

	public Deplace() 
	{
		colli = new Collision();
	}
	
	public void DeplaceObject(Collidable object, Mouvement nouvMouv, AbstractModelPartie partie)
	{
		boolean isHeros = object instanceof Heros;
		boolean mouvDifferant=false;
		if(isHeros){
			Heros heros = (Heros) object;
			mouvDifferant = (! (heros.deplacement.IsDeplacement(nouvMouv))) && partie.changeMouv ;
		}
		if( mouvDifferant || (!mouvDifferant && object.reaffiche<=0 ) )
		{
			object.memorizeCurrentValue();

			//on change d'animation avant de deplacer si elle doit etre changee
			if(isHeros && partie.slowDown)
				partie.slowCount= (partie.slowCount+1) % (object.slowDownFactor);

			boolean shouldMove=object.deplace(partie, this);
			if(shouldMove)
			{
				boolean useGravity = object.useGravity &&( !partie.slowDown || (partie.slowDown && partie.slowCount==0));
				if(useGravity)
					Gravite.gravite(object, partie.slowDown);

				object.applyFriction(0);
				//deplacement à l'aide de la vitesse  si il n'y a pas collision 
				//on reset les dernières positions de collisions:
				object.resetVarBeforeCollision();
				boolean stuck = !colli.ejectWorldCollision(partie, this, object);
				if(stuck)
				{
					object.handleStuck(partie, this);
				}
				else
					object.handleDeplacementSuccess(partie, this);
				object.resetVarDeplace();

				if(isHeros)
					deplaceEcran(partie,object);
				object.reaffiche=object.setReaffiche();
			}
		}
		else
			object.reaffiche--;

	}
	/**
	 * Recentre l'ecran autour du heros
	 * 
	 * @param heros, le personnage 
	 * 
	 */	
	public void deplaceEcran(AbstractModelPartie partie, Collidable object) //{{
	{
		int tailleBloc = partie.TAILLE_BLOC;
		//les conditions limites sont aux 3/7
		//trop à gauche de l'ecran
		if(object.xpos<2*InterfaceConstantes.LARGEUR_FENETRE/7){
			//on calcul de combien on doit deplacer l'ecran
			partie.xdeplaceEcran+= 2*InterfaceConstantes.LARGEUR_FENETRE/7-object.xpos;
			object.xpos=2*InterfaceConstantes.LARGEUR_FENETRE/7; 
			//on reajuste par rapport à la position du rectangle à regarder 
			partie.xdeplaceEcranBloc+=partie.xdeplaceEcran/tailleBloc*tailleBloc;
			partie.absRect=partie.INIT_RECT.x - partie.xdeplaceEcranBloc;
			partie.xdeplaceEcran=partie.xdeplaceEcran%tailleBloc;
		}
		//trop à droite 
		else if((object.xpos+object.deplacement.xtaille.get(object.anim))>5*InterfaceConstantes.LARGEUR_FENETRE/7){
			partie.xdeplaceEcran-= object.xpos +object.deplacement.xtaille.get(object.anim)- 5*InterfaceConstantes.LARGEUR_FENETRE/7;
			object.xpos = 5*InterfaceConstantes.LARGEUR_FENETRE/7-object.deplacement.xtaille.get(object.anim);
			//on reajuste par rapport à la position du rectangle à regarder 
			partie.xdeplaceEcranBloc+=partie.xdeplaceEcran/tailleBloc*tailleBloc;
			partie.absRect=partie.INIT_RECT.x - partie.xdeplaceEcranBloc;
			partie.xdeplaceEcran=partie.xdeplaceEcran%tailleBloc;
		}
		//trop en haut
		if(object.ypos<2*InterfaceConstantes.HAUTEUR_FENETRE/5){
			//on calcul de combien on doit deplacer l'ecran
			partie.ydeplaceEcran+= 2*InterfaceConstantes.HAUTEUR_FENETRE/5-object.ypos;
			object.ypos=2*InterfaceConstantes.HAUTEUR_FENETRE/5; 
			//on reajuste par rapport à la position du rectangle à regarder 
			partie.ydeplaceEcranBloc+=partie.ydeplaceEcran/tailleBloc*tailleBloc;
			partie.ordRect=partie.INIT_RECT.y - partie.ydeplaceEcranBloc;
			partie.ydeplaceEcran=partie.ydeplaceEcran%tailleBloc;
		}
		else if((object.ypos+object.deplacement.ytaille.get(object.anim))>3*InterfaceConstantes.HAUTEUR_FENETRE/5)
		{
			//on calcul de combien on doit deplacer l'ecran
			partie.ydeplaceEcran-= (object.ypos+object.deplacement.ytaille.get(object.anim))-3*InterfaceConstantes.HAUTEUR_FENETRE/5;
			object.ypos=3*InterfaceConstantes.HAUTEUR_FENETRE/5-object.deplacement.ytaille.get(object.anim);
			//on reajuste par rapport à la position du rectangle à regarder 
			partie.ydeplaceEcranBloc+=partie.ydeplaceEcran/tailleBloc*tailleBloc;
			partie.ordRect=partie.INIT_RECT.y - partie.ydeplaceEcranBloc;
			partie.ydeplaceEcran=partie.ydeplaceEcran%tailleBloc;
		}

	}

	/**
	 * Renvoie l'animation d'une fleche encochée/du héros en fonction de la position de la souris 
	 * @return l'animation de la fleche/du heros
	 */	
	public int animFlecheEncochee(AbstractModelPartie partie )
	{
		int xPosSouris=partie.getXPositionSouris();
		int yPosSouris=partie.getYPositionSouris();
		Heros heros = partie.heros;
		Hitbox herosHit = heros.deplacement.hitbox.get(partie.heros.anim);
		Vector2d leftUpP = Hitbox.supportsPoint(new Vector2d(-1,-1), herosHit.polygon).get(0);
		Vector2d rightDownP = Hitbox.supportsPoint(new Vector2d(1,1), herosHit.polygon).get(0);
		int x=(int) leftUpP.x; 
		int y=(int) leftUpP.y; 
		int yUpArrow = -14; // because the arrow does not start at the center of the heros' hitbox
		int xhit = (int) (rightDownP.x-leftUpP.x);
		int yhit = (int) (rightDownP.y-leftUpP.y);

		double xPosRelative = xPosSouris - (heros.xpos+x+xhit/2);
		double yPosRelative = yPosSouris - (heros.ypos+y+yhit/2+yUpArrow);
		double angle = Math.atan(yPosRelative/xPosRelative);
		if(xPosRelative<0 && yPosRelative<=0 )
			angle = -Math.PI + angle;
		if(xPosRelative<0 && yPosRelative>0)
			angle= Math.PI + angle;
		double range = 2 * Math.PI / 8;


		if(-range/2 <= angle && angle < range/2)
			return(0);
		else if(range/2 <= angle && angle < 3*range/2)
			return(1);
		else if(3*range/2 <= angle && angle < 5*range/2 )
			return(2);
		else if(5*range/2 <= angle && angle < 7*range/2 )
			return(3);
		else if((7*range/2 <= angle && angle <=8*range/2) || (-8*range/2 <= angle && angle < -7*range/2))
			return(4);
		else if(-7*range/2 <= angle && angle < -5*range/2)
			return(5);
		else if(-5*range/2 <= angle && angle < -3*range/2)
			return(6);
		else if(-3*range/2 <= angle && angle < -range/2)
			return(7);
		else 
			throw new IllegalArgumentException("ERREUR: animFlecheEncochee ");
	}

}


