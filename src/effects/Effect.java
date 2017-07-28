package effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
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
	public void onUpdate(AbstractModelPartie partie, boolean last) {
		draw_tr=computeTransformDraw(partie);
		hitbox_rotated = Hitbox.convertHitbox(hitbox, partie.INIT_RECT, draw_tr, new Point(xpos(),ypos()), new Point(partie.xScreendisp,partie.yScreendisp));

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

	public abstract Point getTranslationFromTranformDraw(AbstractModelPartie partie);
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr=new AffineTransform(ref_fleche.draw_tr);
		double[] flatmat = new double[6];
		tr.getMatrix(flatmat);
		tr.setTransform(flatmat[0], flatmat[1], flatmat[2], flatmat[3], transl.x, transl.y);
		return tr;
	}

	
	@Override
	public Hitbox getHitbox(Point INIT_RECT) {
		if(draw_tr==null || hitbox_rotated==null)
			return hitbox.get(anim);
		return hitbox_rotated.get(anim);
	}


	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		anim=animation.update(anim,partie.getFrame(),1);
		//doit deplace, change anim
		boolean[] res = {true,false};
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
	public Hitbox getHitbox(Point INIT_RECT, Mouvement mouv, int _anim) {
		return getHitbox(INIT_RECT);
	}
	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,boolean stuck) {	
	}
	
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Collidable collider, Vector2d normal) {		
	}
	@Override
	public void handleStuck(AbstractModelPartie partie) {	
		handleWorldCollision( new Vector2d(), partie,true );
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
	public void resetVarDeplace() {		
	}

}
