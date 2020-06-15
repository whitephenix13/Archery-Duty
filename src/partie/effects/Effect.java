package partie.effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.vecmath.Vector2d;

import debug.DebugStack;
import partie.collision.Collidable;
import partie.collision.GJK_EPA;
import partie.collision.Hitbox;
import partie.entitie.Entity;
import partie.input.InputPartie;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.mouvement.Deplace;
import partie.mouvement.Mouvement;
import partie.mouvement.Mouvement.SubTypeMouv;
import partie.mouvement.Mouvement.TypeMouv;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public abstract class Effect extends Collidable{
	
	//public static enum EffectEnum implements TypeMouv {Electrique,Feu,Glace,Roche,Explosive,Grappin,Lumiere,Ombre,Trou_noir,Vent};
	public static enum EffectCollisionEnum implements SubTypeMouv{GROUND,ENTITY}

	public TypeMouv typeMouv;//for now, it is not relevant as all mouv are 'idle' but it might change in the future
	public SubTypeMouv subTypeMouv;

	protected Vector2d normalCollision=null;
	protected Point pointCollision = null;
	protected Point correctedPointCollision = null;
	
	public boolean groundEffect = false;

	public Fleche ref_fleche=null;
	public void onRemoveRefFleche(boolean destroyNow){ref_fleche=null;};
	
	//public int xplace=1;	//Determine what is the key point defining the draw position 0 left 1 center 2 right
	//public int yplace=1;	//Determine what is the key point defining the draw position 0 up 1 center 2 down

	//normal value of the surface to reproject the desired speed in order to slide 
	public Vector2d normal = null;

	protected BufferedImage previousMaskedIm = null;//used for grappin

	public boolean isWorldCollider =false; //set to true if this object should be consider as a bloc for collision. Used in roche_effect
	public boolean isProjectile = false; //set to true if this object should be consider as a projectile for projectile/projectile and projectile/heros collision
	
	protected double consequenceUpdateTime=-1; //in seconds, the frequence at which the consequence should be apply to the affected object
	public double getConsequenceUpdateTime(){return consequenceUpdateTime;}
	
	public Effect(int _mouv_index,Fleche _ref_fleche)
	{
		this(_mouv_index,_ref_fleche,null,null,null,false,false);
	}

	/**
	 * 
	 * @param _normalCollision
	 * @param useRefArrowRotation false: compute the projected rotation to main x/y axis (angle = -Pi:Pi, -Pi/2 0 Pi/2 -
	 */
	public Effect(int _mouv_index,Fleche _ref_fleche,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundEffect,boolean useProjectedRot)
	{
		super();
		
		setMouvIndex(_mouv_index);
		ref_fleche = _ref_fleche;
		normalCollision=_normalCollision;
		pointCollision=_pointCollision;
		correctedPointCollision=_correctedPointCollision;
		localVit= new Vitesse();
		this.groundEffect=groundEffect;
		
		//AXIS FOR ANGLE IS (1,0) 
		if(_normalCollision != null){
			_normalCollision = GJK_EPA.projectVectorTo90(_normalCollision,false,0);
			double crossProdNorm = _normalCollision.y; // axis.x * _normalCollision.y  -axis.y * _normalCollision.x
			double dotProd = _normalCollision.x;
			double effectRotation = Math.atan(crossProdNorm/dotProd) ;
			boolean minus_zero = ((Double)effectRotation).equals(new Double(-0.0));
			effectRotation += minus_zero? -Math.PI/2 : Math.PI/2;
			
			setRotation(useProjectedRot?effectRotation:ref_fleche.getRotation());
		}
		
	}
	public boolean isEnded()
	{
		return getMouvement().animEnded();
	}

	public abstract void applyConsequence(Entity attacher,boolean isFirstApplication);
	/**
	 * return the speed to add to the agents concerned by the effect 
	 * */
	public abstract Vitesse getModifiedVitesse(Collidable obj);
	@Override
	public void onDestroy()
	{
		
	}

	@Override
	public void destroy(boolean destroyNow)
	{
		if(destroyNow)
			needDestroy=true;
		else
			timer();
		List<Entity> allCollidable = Collidable.getAllEntitiesCollidable();
		//loop through all the Collidable and remove itself as reference
		boolean remove_ref_fleche = (ref_fleche!=null);
		for(Entity c : allCollidable)
		{
			c.unregisterEffect(this);

		}
		if(remove_ref_fleche)
		{
			ref_fleche.OnFlecheEffectDestroy(destroyNow);
		}
	}
	

	protected Point setFirstPos(Point effCenter)
	{
		if(pointCollision!=null)
			return new Point((int)pointCollision.x+correctedPointCollision.x-effCenter.x, (int)pointCollision.y+correctedPointCollision.y-effCenter.y);
		else
		{
			//get the tip of the arrow
			Point arrowTip = getArrowTip();
			return new Point(arrowTip.x-effCenter.x,arrowTip.y-effCenter.y);
		}

	}
	protected Point getArrowTip()
	{
		return Fleche.getArrowTip(ref_fleche, true);
	}
	
	
	
	@Override
	public AffineTransform computeDrawTr(Point screenDisp)
	{
		return AbstractModelPartie.getRotatedTransform(new Point(getXpos()+screenDisp.x, getYpos()+screenDisp.y),null,getRotation(),getScaling());
	}
	
	protected Hitbox computeEffectHitbox(Hitbox unscaledMouvementHitbox,Point INIT_RECT,Point screenDisp){
		//Give the unscale mouvement hitbox since the affineTransformation used to transform the hitbox already took the scaling into account
		AffineTransform tr = computeDrawTr(screenDisp);
		if(tr==null)
			return unscaledMouvementHitbox.translate(getXpos(),getYpos());
		else{
			Hitbox rotatedHitbox = unscaledMouvementHitbox.transformHitbox(tr, getPos(), screenDisp);
			return rotatedHitbox.translate(getXpos(),getYpos());
		}	
	}
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		return computeEffectHitbox(getUnscaledMouvementHitboxCopy(getMouvIndex()),INIT_RECT,screenDisp);
	}

	
	/***
	 * Callback that is called before updating position in deplace() function
	 */
	/*protected void onDeplaceStart()
	{
		
	}*/

	@Override
	protected void onStartDeplace(){}
	@Override
	protected void handleInputs() {
		
	}
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
	protected boolean updateMouvementBasedOnAnimation() {
		int nextMouvIndex = getMouvement().updateAnimation(getMouvIndex(), ModelPartie.me.getFrame(),1);
		if(getMouvIndex()!=nextMouvIndex){
			boolean success = true;//no need to hitbox alignment check 
			if(success){
				setMouvIndex(nextMouvIndex);
				return true;
			}
		}
		return false;
	}
	@Override
	protected void resetInputState() {
		
	}
	@Override
	protected void onMouvementChanged(boolean animationChanged, boolean mouvementChanged) {
	}
	/***
	 * Callback that is called before updating the animation in deplace() function
	 */
	@Override
	protected void onAnimationEnded()
	{
		destroy(true);
	}
	/*REMOVE @Override 
	/* REMOVE public boolean[] deplace( Deplace deplace) {
		//onDeplaceStart(partie);
		
		//if(shouldUpdatePos())
		//	updatePos(partie);
		
		//if(isEnded())
		//	onAnimationEnded(partie);
		
		//boolean changedAnim  =false;
		//int prev_mouv_index =getAnim();
		//int next_mouv_index = getDeplacement().updateAnimation(getAnim(), ModelPartie.me.getFrame(), 1);
		
		//setAnim(next_mouv_index);
		//if(prev_mouv_index != getAnim()){
		//	changedAnim=true;
		//	onAnimChanged(partie,prev_mouv_index,getAnim());
		//}
		
		//getDeplacement().setSpeed(this, getAnim());
		//doit deplace, change mouv_index
		boolean[] res = {!getNeedDestroy(),changedAnim};
		return res;
	}*/
	@Override 
	public void deplaceOutOfScreen()
	{
		//destroy itself
		System.out.println("Out of screen " + this +" "+ this.getPos() +" fixed "+fixedWhenScreenMoves +" screen disp "+ ModelPartie.me.getScreenDisp());
		this.destroy(true);
	}
	
	public Image applyFilter(Image im) {
		return im;
	}
	
	@Override
	public Vitesse getGlobalVit(){
		return localVit.Copy();
	}
	
	@Override
	public Vector2d getNormCollision() {
		return null;
	}
	
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp, Mouvement mouv, int _mouv_index) {
		//do not take into account scaling since it is taken into account in affine transformation
		return computeEffectHitbox(mouv.getHitboxCopy(_mouv_index),INIT_RECT,screenDisp);
	}
	
	public void handleEntitieEffectCollision(Entity ent)
	{
		ent.registerEffect(this);
	}
	
	@Override
	public void handleWorldCollision(Vector2d normal,Collidable collidedObject,boolean stuck) {	
	}
	
	@Override
	public void handleObjectCollision(Collidable collider, Vector2d normal) {		
	}
	@Override
	public void handleStuck() {	
		handleWorldCollision( new Vector2d(),null,true );
	}
	
	@Override
	public void memorizeCurrentValue() {		
	}
	
	@Override
	public void applyFriction(double minlocalSpeed, double minEnvirSpeed) {		
	}
	
	@Override
	public void resetVarBeforeCollision() {
	}

	@Override
	public void handleDeplacementSuccess() {		
	}

	@Override
	public void resetVarDeplace(boolean speedUpdated) {		
	}
	
	
	@Override
	public int getMaxBoundingSquare()
	{
		return getMouvement().getMaxBoundingSquare();
	}
	@Override
	public Point getMaxBoundingRect() {
		return getMouvement().getMaxBoundingRect();
	}

	
}
