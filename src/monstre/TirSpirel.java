package monstre;

import java.util.Arrays;

import collision.Collision;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;

public class TirSpirel extends TirMonstre implements InterfaceConstantes {
 MusicBruitage bruitage = new MusicBruitage("laser");
	/**
	 * Instancie un TirSpirel
	 * 
	 * @param _xpos, la position en x d'apparition du tir
	 * @param _ypos, la position en y d'apparition du tir
	 * @param _anim, l'animation de depart du tir
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le monstre
	 */	
	public TirSpirel(int _xpos, int _ypos,int _anim)
	{
		nomTir="spirel";
		xpos=_xpos;
		ypos=_ypos;
		anim=_anim;
		doitDetruire=false;
		setSpeed(anim);
		//						  G  ,BG ,B  ,BD ,D  ,HD ,H  ,HG 
		xtaille= Arrays.asList(      114,0  ,0  ,0  ,114,0 ,34 ,0  );
		xhitbox= Arrays.asList(      114,0  ,0  ,0  ,114,0 ,34 ,0  );
		xdecallsprite= Arrays.asList(0  ,0  ,0  ,0  ,0  ,0 ,0  ,0  );
		ytaille= Arrays.asList(      34 ,0  ,0  ,0  ,34 ,0 ,114 ,0  );
		yhitbox= Arrays.asList(      34 ,0  ,0  ,0  ,34 ,0 ,114 ,0  );
		ydecallsprite= Arrays.asList(0  ,0  ,0  ,0  ,0  ,0 ,0  ,0  );

		dommage= -25;
		
		//on active la musique car le tir part directement 
	bruitage.startBruitage(700);
	//permet "d'effacer" l'objet au bout de 2000ms

	}
	/**
	 * Règle la vitesse du tour   
	 * 
	 * @param anim, l'animation en cours du tir
	 */	
	public void setSpeed(int anim) {
		//0:gauche, 4:droite, 6:haut
		int vitesse=10000;
		switch(anim)
		{
		case 0 : vit.x=-1*vitesse;break;
		case 4 : vit.x= 1*vitesse;break;
		case 6 : vit.y=-1*vitesse;break;
		}
	}
	/**
	 * Ralenti les animations  
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le tir
	 */	
	public void setReaffiche()
	{
		reaffiche=20;
	}
	/**
	 * Gère l'ensemble des déplacements du tir  
	 * 
	 */
	public void deplaceTir(AbstractModelPartie partie) 
	{
		if(reaffiche<=0)
		{
		//pas de changement d'animation
		//pas de gravité 
		Collision colli = new Collision();
		colli.collision(partie, this);
		setReaffiche();
		}
		else
		{
			reaffiche--;
		}
	}
	/**
	 * Permet de déclencher des événements lorsque la fleche doit etre détruite   
	 * 
	 */
	public void detruire() {
		//on ne fait rien à la destruction
	}

}
