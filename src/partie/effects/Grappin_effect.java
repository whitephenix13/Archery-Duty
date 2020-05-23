package partie.effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.mouvement.Deplace;
import partie.mouvement.effect.Grappin_idle;
import partie.projectile.fleches.Fleche;
import utils.Vitesse;

public class Grappin_effect extends Effect{
	/**
	 * shooter ----> arrow (when shot) 
	 * Case 1: shooter ---> anchor_collidable == bloc (when heros is dragged: boolean shooterDragged = true)
	 * Case 2 : shooter <---- anchor_collidable == object (when object is dragged: boolean shooterDragged = false)
	 */

	
	public Collidable shooter=null;
	public boolean shooterDragged=true;
	public boolean isDragging = false;
	//shooter
	//use shooter
	Vector2d middleHeros = new Vector2d(); // correspond to the middle of the hero
	public Vector2d getMiddleHeros(){return middleHeros;}

	//arrow 
	//use ref_flecheVector2d
	Vector2d middleTailArrow = new Vector2d();
	public Vector2d getMiddleTailArrow(){return middleTailArrow;}

	public double getShooterToAnchorDistance(){return (Math.sqrt(Math.pow(middleTailArrow.x-middleHeros.x,2)+Math.pow(middleTailArrow.y-middleHeros.y,2)));}

	private int last_start_filter=-1;
	private BufferedImage convertedIm=null;
	
	private double _current_length; //Never access this directly
	private double getCurrentLength(){
		return _current_length;
	}
	private void setCurrentLength(double newLength){
		if(_current_length != newLength){
			forceHitboxDirty(); //force hitbox recomputation
			_current_length = newLength;
		}
	}
	
	//double current_length = 0;
	public double getRemainingLength(){return (MAX_LENGTH-getCurrentLength());}

	private Vitesse modified_vitesse = null;
	public boolean reached_max_length=false;

	public double MIN_SHOOTER_ANCHOR_D = 50;
	int MAX_LENGTH = 1000; 
	private double DRAG_SPEED=15;//15
	
	public Grappin_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _mouv_index, int current_frame, Collidable _shooter )
	{
		super(_mouv_index,_ref_fleche);
		setScaling(1,getScaling().y);//Never change the x scaling or it will screw the arrow (since grappin is from hero to arrow disregarding the scale).
		this.setCollideWithNone();
	
		ref_fleche=_ref_fleche;
		localVit= new Vitesse();
		setRotation(0);
		shooter=_shooter;
		
		subTypeMouv = null;
		setMouvement(new Grappin_idle(subTypeMouv,partie.getFrame()));

		TEMPS_DESTRUCTION= _ref_fleche.TEMPS_DESTRUCTION;
				
		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		
		this.onUpdate(partie); 
		
	}

	
	@Override
	protected boolean updateMouvementBasedOnAnimation(AbstractModelPartie partie) {
		onUpdate(partie);
		return super.updateMouvementBasedOnAnimation(partie);
	}
	
	@Override 
	public void onRemoveRefFleche(AbstractModelPartie partie,boolean destroyNow){
		this.needDestroy=true;
		super.onRemoveRefFleche(partie, destroyNow);
	}
	

	
	private void onUpdate(AbstractModelPartie partie) {		
		//destroy object when heros has collide
		boolean stopCondi = getShooterToAnchorDistance()<=MIN_SHOOTER_ANCHOR_D && isDragging;
		if(stopCondi)
		{
			if(this.tempsDetruit>0)
				return;
			reached_max_length=true;
			ref_fleche.destroy(partie,false);
			this.destroy(partie,false);
			return;
		}
		if(reached_max_length)
		{
			return;
		}
		
		if(ref_fleche==null )
			return;

		if((ref_fleche.getNeedDestroy() || this.needDestroy))
		{
			this.destroy(partie,true);
			return;
		}

		//update the xpos, ypos parameter with respect to the heros position
		//update the parameter for the mask 

		Heros her = ref_fleche.shooter;
		Hitbox herHit = her.getHitbox(partie.INIT_RECT, partie.getScreenDisp());
		
		Vector2d topleftH = Hitbox.supportPoint(new Vector2d(-1,-1), herHit.polygon);
		Vector2d bottomrightH = Hitbox.supportPoint(new Vector2d(1,1), herHit.polygon);

		Vector2d prevMiddleHeros = new Vector2d(middleHeros);
		middleHeros=herHit.getCenter();

		//get the tail of the arrow

		//value to shift the rotation to get the two points from the tail
		double epsilon = Math.PI/10;

		double[] XY = Deplace.angleToXY(ref_fleche.getRotation()+epsilon);
		double[] XY2 = Deplace.angleToXY(ref_fleche.getRotation()-epsilon);

		//get the opposite since the direction we found was the one towards the tip
		Vector2d fleche_tail1 = Hitbox.supportPoint(new Vector2d(-XY[0],-XY[1]),ref_fleche.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);
		Vector2d fleche_tail2 = Hitbox.supportPoint(new Vector2d(-XY2[0],-XY2[1]),ref_fleche.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);
		Vector2d prevMiddleTailArrow = new Vector2d(middleTailArrow);
		//compute the middle of the tail
		middleTailArrow.set(fleche_tail1);
		middleTailArrow.add(fleche_tail2);
		middleTailArrow.scale(0.5f);

		double xPosRelative= middleTailArrow.x-middleHeros.x; 
		double yPosRelative= middleTailArrow.y-middleHeros.y;
		setCurrentLength(Math.sqrt(xPosRelative*xPosRelative+yPosRelative*yPosRelative));
		//if max length is reached: the effect has to be detroyed, no need to update the rotation
		if(Math.round(getCurrentLength())>MAX_LENGTH){
			reached_max_length=true;
			middleHeros=prevMiddleHeros;
			middleTailArrow=prevMiddleTailArrow;
			
			ref_fleche.destroy(partie,false);
			destroy(partie,false);
		}
		else{
			setRotation(Deplace.XYtoAngle(xPosRelative, yPosRelative));	
			System.out.println(middleTailArrow +" "+ middleHeros);
			System.out.println("Fleche grappin rotation "+ (getRotation()*180/Math.PI) + "for relative ("+xPosRelative+","+yPosRelative+")");
		}
	}

	
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entity attacher)
	{
		
	}
	
	@Override
	public Vitesse getModifiedVitesse(Collidable obj) {
		if(ObjectTypeHelper.isTypeOf(obj, ObjectType.HEROS) && ! this.shooterDragged)
			return new Vitesse(); 

		if(!ObjectTypeHelper.isTypeOf(obj, ObjectType.HEROS) &&  this.shooterDragged)
			return new Vitesse(); 

		double[] XY = Deplace.angleToXY(getRotation());
		double normXY = Math.sqrt(Math.pow(XY[0],2)+Math.pow(XY[1],2));
		modified_vitesse= new Vitesse(XY[0]*DRAG_SPEED/normXY,XY[1]*DRAG_SPEED/normXY);
		if(!shooterDragged)
			modified_vitesse.negate();

		return modified_vitesse;	
	}
	
	@Override
	public AffineTransform computeDrawTr(Point screenDisp)
	{
		Point middle_right = getPointOfTaille(1,0.5f,0,getScaling(),getMouvIndex()); //for grappin effect

		//position is top left 
		Point pos = new Point((int)Math.round(middleTailArrow.x-middle_right.x+screenDisp.x),(int)Math.round(middleTailArrow.y-middle_right.y+screenDisp.y));
		Point anchor = middle_right;//rotate around middle right 
		AffineTransform tr = AbstractModelPartie.getRotatedTransform(pos,anchor, getRotation(),getScaling());
		return tr;
	}
	@Override 
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {

		//Get the mouvement hitbox and reshape the xmin so that they fit with the current length
		int newXMin = (int)( (getCurrentXtaille()-getCurrentLength())); //Don't forget to get the unscale value 
		Hitbox unscaledMouvementHitbox = getUnscaledMouvementHitboxCopy(getMouvIndex()).copy()
				.reshapeUnrotatedSquareHitbox(newXMin, null, null, null);
		return computeEffectHitbox(unscaledMouvementHitbox,INIT_RECT,screenDisp);
	}

	public void setFirstPos(AbstractModelPartie partie)
	{
		//get the middle right of the effect
		Point middle_right = getRightOfTaille();

		setXpos_sync((int)(middleTailArrow.x)-middle_right.x+partie.xScreendisp);
		setYpos_sync((int)(middleTailArrow.y)-middle_right.y+partie.yScreendisp);
	}

	@Override
	public Image applyFilter(AbstractModelPartie partie,Image im) {
		int width = im.getWidth(null);
		int height = im.getHeight(null);
		if(width==-1 || height == -1 )
			return im;

		int distance = (int) Math.round(Math.sqrt(Math.pow((middleTailArrow.x-middleHeros.x),2) + Math.pow((middleTailArrow.y-middleHeros.y),2) ));
		int start_filter = width - distance ; 
		if(previousMaskedIm==null){
			previousMaskedIm = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
			convertedIm=partie.toBufferedImage(im);

			//deep copy of converted image into previous masked im
			ColorModel cm = convertedIm.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = convertedIm.copyData(null);
			previousMaskedIm=new BufferedImage(cm, raster, isAlphaPremultiplied, null);

			last_start_filter=0;
		}
		previousMaskedIm=partie.apply_width_mask(convertedIm,previousMaskedIm,start_filter,last_start_filter,1);
		last_start_filter=start_filter;

		return previousMaskedIm;
	}
	
	@Override
	public void handleEntitieEffectCollision(Entity ent)
	{
		//Do not register entitie as the effect really starts if the arrow collides with an Entite or the ground 
	}
	


}
