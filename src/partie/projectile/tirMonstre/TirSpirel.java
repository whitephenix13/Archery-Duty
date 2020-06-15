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
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.mouvement.Mouvement;
import partie.mouvement.projectile.Mouvement_projectile;
import partie.mouvement.projectile.t_normal.T_normal_creation;
import partie.mouvement.projectile.t_normal.T_normal_idle;
import utils.Vitesse;

public class TirSpirel extends TirMonstre implements InterfaceConstantes {
	/**
	 * Instancie un TirSpirel
	 * 
	 * @param _xpos, la position en x d'apparition du tir
	 * @param _ypos, la position en y d'apparition du tir
	 * @param _mouv_index, l'animation de depart du tir
	 * 
	 * @return le nombre de tour de boucle a attendre avant de redeplacer le monstre
	 */	
	public TirSpirel(Vector2d pos,int _mouv_index, double _rotation,Vector2d _scaling,int current_frame,float damageMultiplier,float _speedFactor)
	{
		super();
		
		setMouvement(new T_normal_idle(ObjectType.TIR_SPIREL,null,current_frame));
	
		//Set the back of the projectile at the center of the monstre 
		setMouvIndex(_mouv_index);
		setRotation(_rotation);
		setScaling(_scaling);
		
		Point proj_mid = getLeftOfTaille();
		int x_tir_pos =  (int) (pos.x - proj_mid.x);
		int y_tir_pos =  (int) (pos.y - proj_mid.y);
		
		setXpos_sync(x_tir_pos);
		setYpos_sync(y_tir_pos);
	
		needDestroy=false;
		localVit=new Vitesse(0,0);
		fixedWhenScreenMoves=false ; //true : not influenced by screen displacement (ie: use for the hero)

		useGravity=false;
		getMouvement().setSpeed(this,getMouvIndex());

		damage= -25*damageMultiplier;//-25
		speedFactor=_speedFactor;

		
		//on active la musique car le tir part directement 
		MusicBruitage.me.startBruitage("laser");
		
		//If collide, destroy it 
		if(Collision.isWorldCollision( this, true))
		{
			this.destroy( true);
		}
	}
	
	public Vector2d getNormCollision()
	{
		return null;
	}
	
	@Override
	protected void handleInputs() {}
	@Override
	protected boolean updateMouvementBasedOnPhysic() {
		return false;
	}
	@Override
	protected boolean updateNonInterruptibleMouvement() {
		return false;
	}
	@Override
	protected boolean updateMouvementBasedOnInput() {
		return false;
	}
	@Override
	public boolean updateMouvementBasedOnAnimation(){
		//update rotation : not needed 
		//switch mouv_index 
		
		if(getMouvement().animEndedOnce() && getMouvement() instanceof T_normal_creation)
		{
			setMouvement(new T_normal_idle(ObjectType.TIR_SPIREL,null,ModelPartie.me.getFrame()));
			setMouvIndex(0);
		}
		int prevMouvIndex = getMouvIndex();
		int nextMouvIndex = getMouvement().updateAnimation(getMouvIndex(), ModelPartie.me.getFrame(),speedFactor);
		if(prevMouvIndex != getMouvIndex())
		{
			try {
				this.alignNextMouvement(getMouvement(), nextMouvIndex, XAlignmentType.LEFT, YAlignmentType.CENTER, false, true);
			} catch (Exception e) {} //this happens if we couldn't align the movement. We don't care in that case
			
			setMouvIndex(nextMouvIndex);
		}
		//update hitbox: draw transform did not changed => not needed

		//set speed of deplacement
		getMouvement().setSpeed(this,getMouvIndex());

		//update draw transform: not needed
		return true;
	}
	@Override
	protected void resetInputState() {}
	@Override
	protected void onMouvementChanged(boolean animationChanged, boolean mouvementChanged) {}
	
	@Override
	public void memorizeCurrentValue() {
		//nothing to memorize so far 
		/*currentValue=new CurrentValue(){		
			@Override
			public void res()
			{}};*/
	}
	@Override
	public void handleStuck() {
		handleWorldCollision(new Vector2d(0,0), null,true);
	}
	@Override
	public void handleDeplacementSuccess() {
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
	public void onDestroy() {
		//on ne fait rien à la destruction
	}
	
	@Override
	protected AffineTransform _computeDrawTr(boolean screenReferential, Point screenDisp,double rotation,Vector2d scaling)
	{
		Point pos;
		if(screenReferential)
			pos= new Point(getPos().x+screenDisp.x, getPos().y+screenDisp.y);
		else
			pos= new Point(getPos().x, getPos().y);
		
		return AbstractModelPartie.getRotatedTransform(pos,null,rotation,scaling);
	}

	
	private Hitbox computeRotatedHitbox(Point screendisp,Mouvement dep, int mouv_index)
	{
		//Use a scaling of 1 as scaling is used in computeDrawTr
		return dep.getScaledHitboxCopy(mouv_index, new Vector2d(1,1)).transformHitbox(_computeDrawTr(false,screendisp),getPos(),new Point(0,0));
	}
	
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		AffineTransform trans = _computeDrawTr(false,screenDisp); //this takes into account the scaling the get hitbox with scaling once below
		return getMouvement().getHitboxCopy(getMouvIndex()).transformHitbox(trans,new Point(0,0),new Point(0,0));
	}
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp, Mouvement _dep, int _mouv_index) {
		Mouvement_projectile dep_copy = (Mouvement_projectile) _dep.Copy(); //create the mouvement
		Hitbox rotatedTempHit = computeRotatedHitbox(screenDisp,dep_copy,_mouv_index); //new hibox 
		
		return rotatedTempHit.translate(getXpos(),getYpos());
	}
	
	@Override
	public void handleWorldCollision(Vector2d normal, Collidable collidedObject,boolean stuck) {
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
	public void handleObjectCollision(Collidable collider,Vector2d normal) {
		if(ObjectTypeHelper.isTypeOf(collider, ObjectType.FLECHE))
			MusicBruitage.me.startBruitage("annulation tir");
		needDestroy=true;
	}

}
