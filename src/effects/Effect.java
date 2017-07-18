package effects;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import deplacement.Animation;
import fleches.Fleche;
import partie.AbstractModelPartie;
import types.Destroyable;
import types.Vitesse;

public abstract class Effect extends Destroyable{
	
	public static String VENT = "vent";
	public static String GRAPPIN = "grappin";

	public String name;
	public int xpos; 
	public int ypos;
	public Fleche ref_fleche=null;
	public void onRemoveRefFleche(AbstractModelPartie partie,boolean destroyNow){this.onUpdate(partie,true);ref_fleche=null;this.destroy(partie, destroyNow);};
	public List<Integer> xtaille= new ArrayList<Integer>() ;
	public List<Integer> ytaille= new ArrayList<Integer>() ;
	public List<Integer> xanchor= new ArrayList<Integer>() ;
	public List<Integer> yanchor= new ArrayList<Integer>() ;
	public double rotation=0;
	public int anim; 
	public Animation animation= new Animation(); 
	public int maxnumberloops =1;
	private int numberloops =0;
	
	public int xplace=1;	//0 left 1 center 2 right
	public int yplace=1;	//0 up 1 center 2 down

	//normal value of the surface to reproject the desired speed in order to slide 
	public Vector2d normal = null;

	protected BufferedImage previousMaskedIm = null;//used for grappin

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
	public abstract void onUpdate(AbstractModelPartie partie,boolean last);
	
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
		List<Collidable> allCollidable = Collidable.getAllCollidable(partie);
		//loop through all the Collidable and remove itself as reference
		boolean remove_ref_fleche = (ref_fleche!=null);
		for(Collidable c : allCollidable)
		{
			c.currentEffects.remove(this);
			if(remove_ref_fleche && ref_fleche==c)
			{
				ref_fleche.OnFlecheEffectDestroy(partie, destroyNow);
			}
		}
		partie.arrowsEffects.remove(this);
	}

	public abstract Point getTranslationFromTranformDraw(AbstractModelPartie partie);
	public AffineTransform getTransformDraw(AbstractModelPartie partie) {
		Point transl = getTranslationFromTranformDraw(partie);
		AffineTransform tr=new AffineTransform(ref_fleche.draw_tr);
		double[] flatmat = new double[6];
		tr.getMatrix(flatmat);
		tr.setTransform(flatmat[0], flatmat[1], flatmat[2], flatmat[3], transl.x, transl.y);
		return tr;
	}
	public abstract Image applyFilter(AbstractModelPartie partie, Image im);
	/**
	 * return the speed to add to the agents concerned by the effect 
	 * */
	public abstract Vitesse getModifiedVitesse(AbstractModelPartie partie, Collidable obj);
}
