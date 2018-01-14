package monstre;

import java.awt.Point;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
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
	public TirSpirel(AbstractModelPartie partie,int _xpos, int _ypos,int _anim, double _rotation,int current_frame,float damageMultiplier,float _speedFactor)
	{
		super.init();
		xpos_sync(_xpos);
		ypos_sync(_ypos);
		anim=_anim;
		deplacement= new T_normal(this,Attente.attente_gauche,current_frame);
		rotation = _rotation;
		needDestroy=false;
		localVit=new Vitesse(0,0);
		fixedWhenScreenMoves=false ; //true : not influenced by screen displacement (ie: use for the hero)

		useGravity=false;
		deplacement.setSpeed(this,anim);

		damage= -25*damageMultiplier;//-25
		speedFactor=_speedFactor;

		
		//on active la musique car le tir part directement 
		MusicBruitage.startBruitage("laser");
		//set transform 
		draw_tr = partie.getRotatedTransform(new Point(xpos(),ypos()),new Point(0,0), 
				new Point(deplacement.xtaille.get(anim),deplacement.ytaille.get(anim)), rotation);

		//rotate hitbox
		deplacement.hitbox_rotated=Hitbox.convertHitbox(deplacement.hitbox,partie.INIT_RECT,draw_tr,new Point(xpos(),ypos()),new Point(0,0));

		//If collide, destroy it 
		if(Collision.isWorldCollision(partie, this, true))
		{
			this.destroy(partie, true);
		}
	}
	
	public Vector2d getNormCollision()
	{
		return null;
	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie,Deplace deplace, boolean update_with_speed){
		boolean animationChanged = false;
		//update rotation : not needed 
		//switch anim 
		int prev_anim = anim;
		anim=deplacement.updateAnimation(this, anim, partie.getFrame(),speedFactor,false);
		animationChanged = (prev_anim != anim);
		if(animationChanged)
		{
			//align mid right of tir with previous mid right
			int x_current_mid = (int) (deplacement.xtaille.get(anim)* 1 * Math.cos(rotation) - (deplacement.ytaille.get(anim)*0.5f) * Math.sin(rotation));
			int y_current_mid = (int) (deplacement.xtaille.get(anim)* 1 * Math.sin(rotation) + (deplacement.ytaille.get(anim)*0.5f) * Math.cos(rotation));
		
			int x_prev_mid = (int) (deplacement.xtaille.get(prev_anim)* 1 * Math.cos(rotation) - (deplacement.ytaille.get(prev_anim)*0.5f) * Math.sin(rotation));
			int y_prev_mid = (int) (deplacement.xtaille.get(prev_anim)* 1 * Math.sin(rotation) + (deplacement.ytaille.get(prev_anim)*0.5f) * Math.cos(rotation));
		
			pxpos(x_prev_mid-x_current_mid);
			pypos(y_prev_mid-y_current_mid);

		}
		//update hitbox: draw transform did not changed => not needed

		//set speed of deplacement
		//update draw transform: not needed
		boolean[] res = {true,animationChanged};
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
		handleWorldCollision(new Vector2d(0,0), partie,null,true);
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
	public void resetVarDeplace(boolean speedUpdated) {
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
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp) {
		return  Hitbox.plusPoint(deplacement.hitbox_rotated.get(anim), new Point(xpos(),ypos()),true);	
	}
	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp, Mouvement _dep, int _anim) {
		Mouvement_tir temp = (Mouvement_tir) _dep.Copy(this); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox_rotated.get(_anim), new Point(xpos(),ypos()),true);
	}
	
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {
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
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal) {
		if(TypeObject.isTypeOf(collider, TypeObject.FLECHE))
			MusicBruitage.startBruitage("annulation tir");
		needDestroy=true;
	}


}
