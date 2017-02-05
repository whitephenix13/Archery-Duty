package monstre;

import java.awt.Point;

import javax.vecmath.Vector2d;

import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement_tir.Mouvement_tir;
import deplacement_tir.T_normal;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Hitbox;
import types.Vitesse;

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
		nom_tir=Mouvement_tir.tir_spirel;
		xpos=_xpos;
		ypos=_ypos;
		anim=_anim;
		deplacement= new T_normal(Mouvement_tir.tir_spirel);

		needDestroy=false;
		vit = new Vitesse();
		slowDownFactor=3; 
		fixedWhenScreenMoves=false ; //true : not influenced by screen displacement (ie: use for the hero)

		useGravity=false;
		setSpeed(anim);

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
		case 0 : vit.x= 1*vitesse;break;
		case 1 : vit.x= -1*vitesse;break;
		case 2 : vit.y=-1*vitesse;break;
		}
	}

	@Override
	public boolean deplace(AbstractModelPartie partie,Deplace deplace){
		//nothing specific to do 
		return true;
	}
	
	@Override
	public void memorizeCurrentValue() {
		//nothing to memorize so far 
		/*currentValue=new CurrentValue(){		
			@Override
			public void res()
			{}};*/
	}
	@Override
	public void handleStuck(AbstractModelPartie partie,Deplace deplace) {
		handleWorldCollision(new Vector2d(0,0), partie, deplace);
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie,
			Deplace deplace) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void applyFriction(int minspeed) {
		//do nothing
	}
	@Override
	public void resetVarBeforeCollision()
	{
		//nothing
	}
	@Override
	public void resetVarDeplace() {
		//nothing
	}
	/**
	 * Ralenti les animations  
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le tir
	 */	
	@Override
	public int setReaffiche()
	{
		return 20;
	}
	/**
	 * Permet de déclencher des événements lorsque la fleche doit etre détruite   
	 * 
	 */
	@Override
	public void destroy() {
		//on ne fait rien à la destruction
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT) {
		return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos,ypos),true);
	}
	@Override
	public Hitbox getHitbox(Point INIT_RECT, Mouvement _dep, int _anim) {
		Mouvement_tir temp = (Mouvement_tir) _dep.Copy(Mouvement_tir.tir_spirel); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox.get(_anim), new Point(xpos,ypos),true);
	}
	public Hitbox getWorldHitbox(AbstractModelPartie partie) {
		Hitbox hit1  =Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos,ypos),true);
		return Hitbox.plusPoint(hit1, new Point(partie.xdeplaceEcran + partie.xdeplaceEcranBloc,
				partie.ydeplaceEcran + partie.ydeplaceEcranBloc),true);
	}
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,
			Deplace deplace) {
		//project speed to ground 
		//double coef= vit.vect2d().dot(normal)/normal.lengthSquared();
		//vit = new Vitesse((int)(vit.x-coef*normal.x),(int)(vit.y-coef*normal.y));

		//boolean collision_gauche = (vit.x<=0) && (normal.x>0);
		//boolean collision_droite = (vit.x>=0) && (normal.x<0);
		//boolean collision_haut = (vit.y<=0) && (normal.y>0);
		//boolean collision_bas = (vit.y>=0) && (normal.y<0);
		
		vit=new Vitesse(0,0);
		needDestroy=true;

	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Deplace deplace) {needDestroy=true;}



}
