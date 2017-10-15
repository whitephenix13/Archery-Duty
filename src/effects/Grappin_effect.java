package effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Deplace;
import fleches.Fleche;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

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
	Point middleHeros = new Point(); // correspond to the middle of the hero
	public Point getMiddleHeros(){return middleHeros;}

	//arrow 
	//use ref_fleche
	Point middleTailArrow = new Point();
	public Point getMiddleTailArrow(){return middleTailArrow;}

	public double getShooterToAnchorDistance(){return (Math.sqrt(Math.pow(middleTailArrow.x-middleHeros.x,2)+Math.pow(middleTailArrow.y-middleHeros.y,2)));}

	private int last_start_filter=-1;
	private BufferedImage convertedIm=null;
	double current_length = 0;
	public double getRemainingLength(){return (MAX_LENGTH-current_length);}

	private Vitesse modified_vitesse = null;
	public boolean reached_max_length=false;

	public double MIN_SHOOTER_ANCHOR_D = 50;
	int MAX_LENGTH = 1000; 
	private double DRAG_SPEED=15;//15
	public Grappin_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame, Collidable _shooter )
	{
		super.init();
		
		anim=_anim;
		ref_fleche=_ref_fleche;
		localVit= new Vitesse();
		rotation = 0;
		shooter=_shooter;
		
		xtaille =  Arrays.asList(1657);
		ytaille =  Arrays.asList(30);
		hitbox= Hitbox.createSquareHitboxes(1657-MAX_LENGTH,0,1657,30,1);


		int start_index =0;
		int end_index =1;
		animation.start(Arrays.asList(1), current_frame, start_index, end_index);
		//<=0 means endless loop
		maxnumberloops = -1;
		xplace=2;
		yplace=1;


		TEMPS_DESTRUCTION= _ref_fleche.TEMPS_DESTRUCTION;

		partie.arrowsEffects.add(this);
		setFirstPos(partie);
	}
	
	@Override
	public int getMaxBoundingSquare()
	{
		return MAX_LENGTH;
	}
	@Override
	public void onUpdate(AbstractModelPartie partie,boolean lastCompute) {		
		//destroy object when heros has collide
		//boolean stopCondi1 = !isDragging && ref_fleche.isPlanted && !reached_max_length && shooterDragged;
		boolean stopCondi = getShooterToAnchorDistance()<=MIN_SHOOTER_ANCHOR_D && isDragging;
		if(stopCondi && !lastCompute)
		{
			if(this.tempsDetruit>0)
				return;
			reached_max_length=true;
			ref_fleche.destroy(partie,false);
			this.destroy(partie,false);
			return;
		}
		if(reached_max_length && !lastCompute)
		{
			return;
		}
		
		if(ref_fleche==null )
			return;

		if((ref_fleche.getNeedDestroy() || this.needDestroy) && !lastCompute )
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

		Point prevMiddleHeros = (Point) middleHeros.clone();
		middleHeros=new Point((int) (topleftH.x + bottomrightH.x)/2,(int) (topleftH.y + bottomrightH.y)/2);

		//get the tail of the arrow

		//value to shift the rotation to get the two points from the tail
		double epsilon = Math.PI/10;

		double[] XY = Deplace.angleToXY(ref_fleche.rotation+epsilon);
		double[] XY2 = Deplace.angleToXY(ref_fleche.rotation-epsilon);

		//get the opposite since the direction we found was the one towards the tip
		Vector2d fleche_tail1 = Hitbox.supportPoint(new Vector2d(-XY[0],-XY[1]),ref_fleche.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);
		Vector2d fleche_tail2 = Hitbox.supportPoint(new Vector2d(-XY2[0],-XY2[1]),ref_fleche.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);
		Point prevMiddleTailArrow = (Point) middleTailArrow.clone();
		middleTailArrow = new Point((int)((fleche_tail1.x+fleche_tail2.x)/2),(int)((fleche_tail1.y+fleche_tail2.y)/2));

		double xPosRelative= middleTailArrow.x-middleHeros.x; 
		double yPosRelative= middleTailArrow.y-middleHeros.y;
		current_length= Math.sqrt(xPosRelative*xPosRelative+yPosRelative*yPosRelative);
		//if max length is reached: the effect has to be detroyed, no need to update the rotation
		if(Math.round(current_length)>MAX_LENGTH){
			reached_max_length=true;
			middleHeros=prevMiddleHeros;
			middleTailArrow=prevMiddleTailArrow;
			
			if(!lastCompute){
			ref_fleche.destroy(partie,false);
			destroy(partie,false);
			}
		}
		else
			rotation = Deplace.XYtoAngle(xPosRelative, yPosRelative);
		
		hitbox= Hitbox.createSquareHitboxes((int)(1657-current_length),0,1657,30,1);
		super.onUpdate(partie, lastCompute);	
	}

	
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		
	}
	
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,Collidable obj) {
		//if(modified_vitesse==null)
		//{
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS) && ! this.shooterDragged)
			return new Vitesse(); 

		if(!TypeObject.isTypeOf(obj, TypeObject.HEROS) &&  this.shooterDragged)
			return new Vitesse(); 

		double[] XY = Deplace.angleToXY(rotation);
		double normXY = Math.sqrt(Math.pow(XY[0],2)+Math.pow(XY[1],2));
		modified_vitesse= new Vitesse(XY[0]*DRAG_SPEED/normXY,XY[1]*DRAG_SPEED/normXY);
		if(!shooterDragged)
			modified_vitesse.negate();

		//}

		return modified_vitesse;	
	}
	
	@Override 
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		Point taille = new Point(xtaille.get(anim),ytaille.get(anim));

		int xshift =(int) ( taille.x /2 * xplace * Math.cos(0) - taille.y /2 * yplace * Math.sin(0));
		int yshift =(int) ( taille.x /2 * xplace * Math.sin(0) + taille.y /2 * yplace * Math.cos(0));


		Point pos = new Point(middleTailArrow.x-xshift+partie.xScreendisp,middleTailArrow.y-yshift+partie.yScreendisp);
		Point anchor = new Point(xshift,yshift);
		AffineTransform tr = partie.getRotatedTransform(pos,anchor, taille, rotation);
		return tr;

	}

	public void setFirstPos(AbstractModelPartie partie)
	{
		//get the middle right of the effect
		int x_eff = (int) (xtaille.get(anim) * Math.cos(ref_fleche.rotation) - ytaille.get(anim)/2 * Math.sin(ref_fleche.rotation));
		int y_eff =(int) (xtaille.get(anim) * Math.sin(ref_fleche.rotation) + ytaille.get(anim)/2 * Math.cos(ref_fleche.rotation));

		xpos_sync((int)(middleTailArrow.x)-x_eff+partie.xScreendisp);
		ypos_sync((int)(middleTailArrow.y)-y_eff+partie.yScreendisp);
	}

	@Override
	public Image applyFilter(AbstractModelPartie partie,Image im) {
		int width = im.getWidth(null);
		int height = im.getHeight(null);
		if(width==-1 || height == -1 )
			return im;

		int distance = (int) Math.round(Math.sqrt(Math.pow((middleTailArrow.x-middleHeros.x),2) + Math.pow((middleTailArrow.y-middleHeros.y),2) ));
		int start_filter = width - distance;
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
	


}
