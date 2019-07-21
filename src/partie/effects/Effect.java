package partie.effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.vecmath.Vector2d;

import images.ImagesContainer.ImageInfo;
import images.ImagesContainer.ObjectSubType;
import partie.collision.Collidable;
import partie.collision.GJK_EPA;
import partie.collision.Hitbox;
import partie.deplacement.Deplace;
import partie.deplacement.Mouvement;
import partie.deplacement.Mouvement.SubTypeMouv;
import partie.deplacement.Mouvement.TypeMouv;
import partie.entitie.Entity;
import partie.modelPartie.AbstractModelPartie;
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
	public void onRemoveRefFleche(AbstractModelPartie partie,boolean destroyNow){this.onUpdate(partie,true);ref_fleche=null;};//TODO: changed to not destroy effect this.destroy(partie, destroyNow);
	
	public int xplace=1;	//Determine what is the key point defining the draw position 0 left 1 center 2 right
	public int yplace=1;	//Determine what is the key point defining the draw position 0 up 1 center 2 down

	//normal value of the surface to reproject the desired speed in order to slide 
	public Vector2d normal = null;

	protected BufferedImage previousMaskedIm = null;//used for grappin

	public boolean isWorldCollider =false; //set to true if this object should be consider as a bloc for collision. Used in roche_effect
	public boolean isProjectile = false; //set to true if this object should be consider as a projectile for projectile/projectile and projectile/heros collision
	
	
	public Effect(int _anim,Fleche _ref_fleche)
	{
		this(_anim,_ref_fleche,null,null,null,false,false);
	}

	/**
	 * 
	 * @param _normalCollision
	 * @param useRefArrowRotation false: compute the projected rotation to main x/y axis (angle = -Pi:Pi, -Pi/2 0 Pi/2 -
	 */
	public Effect(int _anim,Fleche _ref_fleche,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,boolean groundEffect,boolean useProjectedRot)
	{
		super();
		
		setAnim(_anim);
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
		return getDeplacement().animEnded();
	}
	/** call each step by the main loop*/
	public void onUpdate(AbstractModelPartie partie, boolean last) {
	}
	/** call in deplace, apply relative effects to collidable to which the effect is attached to. For speed modifiers, use getModifiedSpeed*/
	public abstract void updateOnCollidable(AbstractModelPartie partie,Entity attacher);
	/**
	 * return the speed to add to the agents concerned by the effect 
	 * */
	public abstract Vitesse getModifiedVitesse(AbstractModelPartie partie, Collidable obj);
	@Override
	public void onDestroy(AbstractModelPartie partie)
	{
		
	}

	@Override
	public void destroy(AbstractModelPartie partie,boolean destroyNow)
	{
		if(destroyNow)
			needDestroy=true;
		else
			timer();
		List<Entity> allCollidable = Collidable.getAllEntitiesCollidable(partie);
		//loop through all the Collidable and remove itself as reference
		boolean remove_ref_fleche = (ref_fleche!=null);
		for(Entity c : allCollidable)
		{
			c.unregisterEffect(partie, this);

		}
		if(remove_ref_fleche)
		{
			ref_fleche.OnFlecheEffectDestroy(partie, destroyNow);
		}
	}
	

	protected void updatePos(AbstractModelPartie partie)
	{
	}
	protected Point setFirstPos(AbstractModelPartie partie,Point effCenter)
	{
		if(pointCollision!=null)
			return new Point((int)pointCollision.x+correctedPointCollision.x-effCenter.x, (int)pointCollision.y+correctedPointCollision.y-effCenter.y);
		else
		{
			//get the tip of the arrow
			Point arrowTip = getArrowTip(partie);
			return new Point(arrowTip.x-effCenter.x,arrowTip.y-effCenter.y);
		}

	}
	protected Point getArrowTip(AbstractModelPartie partie)
	{
		return Fleche.getArrowTip(partie, ref_fleche, true);
	}
	
	
	
	@Override
	public AffineTransform computeDrawTr(Point screenDisp)
	{
		return AbstractModelPartie.getRotatedTransform(new Point(getXpos()+screenDisp.x, getYpos()+screenDisp.y),null,getRotation());
	}
	
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		AffineTransform tr = computeDrawTr(screenDisp);
		if(tr==null)
			return Hitbox.plusPoint(this.getDeplacementHitbox(getAnim()).copy(),new Point(getXpos(),getYpos()),true);
		else{
			Hitbox rotatedHitbox = Hitbox.convertHitbox(getDeplacementHitbox(getAnim()), tr,new Point(getXpos(),getYpos()), screenDisp); 
			return Hitbox.plusPoint(rotatedHitbox,new Point(getXpos(),getYpos()),true);
		}	
	}


	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		updatePos(partie);
		
		setAnim(getDeplacement().updateAnimation(getAnim(), partie.getFrame(), 1));
		getDeplacement().setSpeed(this, getAnim());
		//doit deplace, change anim
		boolean[] res = {!getNeedDestroy(),false};
		return res;
	}
	
	
	public Image applyFilter(AbstractModelPartie partie, Image im) {
		return im;
	}
	
	@Override
	public Vitesse getGlobalVit(AbstractModelPartie partie){
		return localVit.Copy();
	}
	
	@Override
	public Vector2d getNormCollision() {
		return null;
	}
	
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp, Mouvement mouv, int _anim) {
		return computeHitbox(INIT_RECT,screenDisp);
	}
	
	public void handleEntitieEffectCollision(Entity ent)
	{
		ent.registerEffect(this);
	}
	
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {	
	}
	
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Collidable collider, Vector2d normal) {		
	}
	@Override
	public void handleStuck(AbstractModelPartie partie) {	
		handleWorldCollision( new Vector2d(), partie,null,true );
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
	public void handleDeplacementSuccess(AbstractModelPartie partie) {		
	}

	@Override
	public void resetVarDeplace(boolean speedUpdated) {		
	}
	
	@Override
	public Hitbox getNextEstimatedHitbox(AbstractModelPartie partie,double newRotation,int anim)
	{
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}
	
	
	@Override
	public int getMaxBoundingSquare()
	{
		return getDeplacement().getMaxBoundingSquare();
	}
	@Override
	public Point getMaxBoundingRect() {
		return getDeplacement().getMaxBoundingRect();
	}

}
