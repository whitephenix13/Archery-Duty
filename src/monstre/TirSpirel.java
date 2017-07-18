package monstre;

import java.awt.Point;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Attente;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement_tir.Mouvement_tir;
import deplacement_tir.T_normal;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import principal.InterfaceConstantes;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class TirSpirel extends TirMonstre implements InterfaceConstantes {
	/**
	 * Instancie un TirSpirel
	 * 
	 * @param _xpos, la position en x d'apparition du tir
	 * @param _ypos, la position en y d'apparition du tir
	 * @param _anim, l'animation de depart du tir
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le monstre
	 */	
	public TirSpirel(int _xpos, int _ypos,int _anim,int current_frame)
	{
		type=TypeObject.tir_spirel;
		xpos(_xpos);
		ypos(_ypos);
		anim=_anim;
		deplacement= new T_normal(type,Attente.attente_gauche,current_frame);

		needDestroy=false;
		localVit = new Vitesse();
		slowDownFactor=3; 
		fixedWhenScreenMoves=false ; //true : not influenced by screen displacement (ie: use for the hero)

		useGravity=false;
		deplacement.setSpeed(TypeObject.tir_spirel,this,anim);

		dommage= 0;//-25

		//effect properties
		this.draggable=false;
		
		//on active la musique car le tir part directement 
		MusicBruitage.startBruitage("laser");

		
	}
	
	public Vector2d getNormCollision()
	{
		return null;
	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie,Deplace deplace){
		//nothing specific to do 
		boolean[] res = {true,false};
		return res;
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
	public void handleStuck(AbstractModelPartie partie) {
		handleWorldCollision(new Vector2d(0,0), partie);
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void applyFriction(double minLocalspeed, double minEnvirSpeed) {
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
	 * Permet de déclencher des événements lorsque la fleche doit etre détruite   
	 * 
	 */
	@Override
	public void onDestroy(AbstractModelPartie partie) {
		//on ne fait rien à la destruction
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT) {
		return  Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos(),ypos()),true);
	}
	@Override
	public Hitbox getHitbox(Point INIT_RECT, Mouvement _dep, int _anim) {
		Mouvement_tir temp = (Mouvement_tir) _dep.Copy(TypeObject.tir_spirel); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox.get(_anim), new Point(xpos(),ypos()),true);
	}
	public Hitbox getWorldHitbox(AbstractModelPartie partie) {
		Hitbox hit1  =Hitbox.plusPoint(deplacement.hitbox.get(anim), new Point(xpos(),ypos()),true);
		return Hitbox.plusPoint(hit1, new Point(partie.xScreendisp,partie.yScreendisp),true);
	}
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie) {
		//project speed to ground 
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		//boolean collision_bas = normal.y<0;
		
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		localVit=new Vitesse(0,0);
		needDestroy=true;
		
	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider) {needDestroy=true;}


}
