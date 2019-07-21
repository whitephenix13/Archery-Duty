package partie.projectile.tirMonstre;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import music.MusicBruitage;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.deplacement.Deplace;
import partie.deplacement.Mouvement;
import partie.deplacement.projectile.Mouvement_projectile;
import partie.deplacement.projectile.T_normal;
import partie.modelPartie.AbstractModelPartie;
import utils.Vitesse;

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
	public TirSpirel(AbstractModelPartie partie,int _x_mid_pos, int _y_mid_pos,int _anim, double _rotation,int current_frame,float damageMultiplier,float _speedFactor)
	{
		super();
		
		setDeplacement(new T_normal(ObjectType.TIR_SPIREL,null,current_frame));
	
		//Desired location for the projectile: spirel pos + middle of spirel = projectile pos + middle back of projectile
		//projectile pos = (xpos,ypos) + (xtaille/2,ytaille/2) - (-ytailleproj/2 * sin(angle), ytailleproj/2 * cos(angle)) 
		int x_tir_pos =  (int) (_x_mid_pos + getDeplacement().ytaille.get(0) * 0.5f  * Math.sin(_rotation));
		int y_tir_pos =  (int) (_y_mid_pos - getDeplacement().ytaille.get(0) * 0.5f  * Math.cos(_rotation));
		
		setXpos_sync(x_tir_pos);
		setYpos_sync(y_tir_pos);
		setAnim(_anim);
		setRotation(_rotation);
		needDestroy=false;
		localVit=new Vitesse(0,0);
		fixedWhenScreenMoves=false ; //true : not influenced by screen displacement (ie: use for the hero)

		useGravity=false;
		getDeplacement().setSpeed(this,getAnim());

		damage= -25*damageMultiplier;//-25
		speedFactor=_speedFactor;

		
		//on active la musique car le tir part directement 
		MusicBruitage.me.startBruitage("laser");

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
	public boolean[] deplace(AbstractModelPartie partie,Deplace deplace){
		boolean animationChanged = false;
		//update rotation : not needed 
		//switch anim 
		int prev_anim = getAnim();
		setAnim(getDeplacement().updateAnimation(getAnim(), partie.getFrame(),speedFactor,false));
		animationChanged = (prev_anim != getAnim());
		if(animationChanged)
		{
			//align mid right of tir with previous mid right
			int x_current_mid = (int) (getDeplacement().xtaille.get(getAnim())* 1 * Math.cos(getRotation()) - (getDeplacement().ytaille.get(getAnim())*0.5f) * Math.sin(getRotation()));
			int y_current_mid = (int) (getDeplacement().xtaille.get(getAnim())* 1 * Math.sin(getRotation()) + (getDeplacement().ytaille.get(getAnim())*0.5f) * Math.cos(getRotation()));
		
			int x_prev_mid = (int) (getDeplacement().xtaille.get(prev_anim)* 1 * Math.cos(getRotation()) - (getDeplacement().ytaille.get(prev_anim)*0.5f) * Math.sin(getRotation()));
			int y_prev_mid = (int) (getDeplacement().xtaille.get(prev_anim)* 1 * Math.sin(getRotation()) + (getDeplacement().ytaille.get(prev_anim)*0.5f) * Math.cos(getRotation()));
		
			addXpos(x_prev_mid-x_current_mid);
			addYpos(y_prev_mid-y_current_mid);

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
	protected AffineTransform _computeDrawTr(boolean screenReferential, Point screenDisp)
	{
		AffineTransform trans = new AffineTransform();
		//Translate the object to its position
		if(screenReferential)
			trans.translate(getPos().x+screenDisp.x, getPos().y+screenDisp.y);
		else
			trans.translate(getPos().x, getPos().y);
		//rotate it around its center
		trans.rotate(getRotation(), 0, 0);
		return trans;
	}

	
	private Hitbox computeRotatedHitbox(Point screendisp,Mouvement dep, int anim)
	{
		return Hitbox.convertHitbox(dep.getHitbox().get(anim),_computeDrawTr(false,screendisp),new Point(getXpos(),getYpos()),new Point(0,0));
	}
	
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		AffineTransform trans = _computeDrawTr(false,screenDisp);
		Hitbox hit =  Hitbox.convertHitbox(getDeplacement().getHitbox().get(getAnim()),trans,new Point(0,0),new Point(0,0));
		return hit;
	}
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp, Mouvement _dep, int _anim) {
		Mouvement_projectile dep_copy = (Mouvement_projectile) _dep.Copy(); //create the mouvement
		Hitbox rotatedTempHit = computeRotatedHitbox(screenDisp,dep_copy,_anim);

		return Hitbox.plusPoint(rotatedTempHit, new Point(getXpos(),getYpos()),true);
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
		if(ObjectTypeHelper.isTypeOf(collider, ObjectType.FLECHE))
			MusicBruitage.me.startBruitage("annulation tir");
		needDestroy=true;
	}


}
