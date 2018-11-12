package effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.GJK_EPA;
import debug.Debug_stack;
import deplacement.Animation;
import deplacement.Deplace;
import deplacement.Mouvement;
import fleches.Fleche;
import partie.AbstractModelPartie;
import types.Destroyable;
import types.Entitie;
import types.Hitbox;
import types.Vitesse;

public abstract class Effect extends Collidable{
	
	protected Vector2d normalCollision=null;
	protected Point pointCollision = null;
	protected Point correctedPointCollision = null;
	
	public int typeEffect = -1; //-1 for default, 0 if special animation for the ground, 1 if special animation for ennemi/shoot collision

	public Fleche ref_fleche=null;
	public void onRemoveRefFleche(AbstractModelPartie partie,boolean destroyNow){this.onUpdate(partie,true);ref_fleche=null;this.destroy(partie, destroyNow);};
	public List<Integer> xtaille= new ArrayList<Integer>() ;
	public List<Integer> ytaille= new ArrayList<Integer>() ;
	
	public Animation animation= new Animation(); 
	public int maxnumberloops =1;
	private int numberloops =0;
	
	public int xplace=1;	//Determine what is the key point defining the draw position 0 left 1 center 2 right
	public int yplace=1;	//Determine what is the key point defining the draw position 0 up 1 center 2 down

	//normal value of the surface to reproject the desired speed in order to slide 
	public Vector2d normal = null;

	protected BufferedImage previousMaskedIm = null;//used for grappin

	public AffineTransform draw_tr=null;

	public List<Hitbox> hitbox = new ArrayList<Hitbox>();
	public List<Hitbox> hitbox_rotated = new ArrayList<Hitbox>();//used to transform the initial hitbox into a rotated one

	public boolean isWorldCollider =false; //set to true if this object should be consider as a bloc for collision. Used in roche_effect
	public boolean isProjectile = false; //set to true if this object should be consider as a projectile for projectile/projectile and projectile/heros collision
	/**
	 * 
	 * @param _normalCollision
	 * @param useRefArrowRotation false: compute the projected rotation to main x/y axis (angle = -Pi:Pi, -Pi/2 0 Pi/2 -
	 */
	public void init(int _anim,Fleche _ref_fleche,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,int _typeEffect,boolean useProjectedRot)
	{
		super.init();
		
		anim=_anim;
		ref_fleche = _ref_fleche;
		normalCollision=_normalCollision;
		pointCollision=_pointCollision;
		correctedPointCollision=_correctedPointCollision;
		typeEffect=_typeEffect;
		localVit= new Vitesse();

		//AXIS FOR ANGLE IS (1,0) 
		_normalCollision = GJK_EPA.projectVectorTo90(_normalCollision,false,0);
		double crossProdNorm = _normalCollision.y; // axis.x * _normalCollision.y  -axis.y * _normalCollision.x
		double dotProd = _normalCollision.x;
		double effectRotation = Math.atan(crossProdNorm/dotProd) ;
		boolean minus_zero = ((Double)effectRotation).equals(new Double(-0.0));
		effectRotation += minus_zero? -Math.PI/2 : Math.PI/2;

		rotation =  useProjectedRot?effectRotation:ref_fleche.rotation;
	}
	public boolean isEnded()
	{
		//endless loop until some event stop it 
		if(maxnumberloops<=0)
			return false;
		
		if(animation.isEndedOnce())
		{
			numberloops+=1;
			animation.resetEndedOnce();
		}
		return numberloops>=maxnumberloops;
	}
	/** call each step by the main loop*/
	/*public void onUpdate(AbstractModelPartie partie, boolean last) {
		draw_tr=computeTransformDraw(partie);
		hitbox_rotated = Hitbox.convertHitbox(hitbox, partie.INIT_RECT, draw_tr, new Point(xpos(),ypos()), new Point(partie.xScreendisp,partie.yScreendisp));

	}*/
	public void onUpdate(AbstractModelPartie partie, boolean last) {
		draw_tr=computeTransformDraw(partie);
		hitbox_rotated = Hitbox.convertHitbox(hitbox, draw_tr,new Point(xpos(),ypos()), partie.getScreenDisp());
	}
	/** call in deplace, apply relative effects to collidable to which the effect is attached to. For speed modifiers, use getModifiedSpeed*/
	public abstract void updateOnCollidable(AbstractModelPartie partie,Entitie attacher);
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
		List<Entitie> allCollidable = Collidable.getAllEntitiesCollidable(partie);
		//loop through all the Collidable and remove itself as reference
		boolean remove_ref_fleche = (ref_fleche!=null);
		for(Entitie c : allCollidable)
		{
			c.currentEffects.remove(this);
		}
		if(remove_ref_fleche)
		{
			ref_fleche.OnFlecheEffectDestroy(partie, destroyNow);
		}
		//partie.arrowsEffects.remove(this); DO NOT DO THIS OTHERWISE THE EFFECT DISAPPEAR IMMEDIATLY 
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
	
	public Point getTranslationFromTranformDraw(AbstractModelPartie partie)
	{
		return new Point(xpos()+partie.xScreendisp,ypos()+partie.yScreendisp);
	}
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr=new AffineTransform(ref_fleche.draw_tr);
		double[] flatmat = new double[6];
		tr.getMatrix(flatmat);
		tr.setTransform(flatmat[0], flatmat[1], flatmat[2], flatmat[3], transl.x, transl.y);
		return tr;
	}
	public AffineTransform computeTransformDrawRotated(AbstractModelPartie partie)
	{
		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr2 = new AffineTransform();
		tr2.translate(transl.x, transl.y);
		tr2.rotate(rotation);
		return tr2;
	}
	
	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp) {
		if(draw_tr==null || hitbox_rotated==null){
			return Hitbox.plusPoint(hitbox.get(anim).copy(),new Point(xpos(),ypos()),true);
		}
		return Hitbox.plusPoint(hitbox_rotated.get(anim).copy(),new Point(xpos(),ypos()),true);
	}


	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		updatePos(partie);
		anim=animation.update(anim,partie.getFrame(),1);
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
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp, Mouvement mouv, int _anim) {
		return getHitbox(INIT_RECT,screenDisp);
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


}
