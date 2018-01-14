package fleches;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import debug.Debug_stack;
import deplacement.Deplace;
import deplacement.Mouvement;
import deplacement_tir.Mouvement_tir;
import deplacement_tir.T_normal;
import effects.Effect;
import effects.Roche_effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Entitie;
import types.Hitbox;
import types.Projectile;
import types.TypeObject;
import types.Vitesse;

public class Fleche extends Projectile implements InterfaceConstantes{

	public Heros shooter;

	public boolean doitDeplace=false;
	public boolean generatedEffect = false;
	public boolean nulle =false;
	public boolean encochee =false;
	public boolean afterDecochee = false; //set to true after first decochee and before second deplace.
	private Point targetedPoint = null; // The point in world coordinate that was cliked when the arrow was shot. Used to get more accurate direction
	private boolean targetReached = false; // set to true when close to target to avoid that the arrow goes back and forth around the target
	
	public int MAX_NUMBER_INSTANCE=-1;//<0 for no restriction 

	public AffineTransform draw_tr=null;
	//relative to heros position
	public List<Integer> xanchor=Arrays.asList(28,20,45,45,40,30,55,52,70,35);
	public List<Integer> yanchor=Arrays.asList(30,20,22,25,45,50,65,42,30,40);

	public float damage;
	public float seyeri_cost;
	private boolean animationChanged=false;

	public Effect flecheEffect;

	public Fleche(List<Projectile> tabFleche,int current_frame,Heros _shooter,boolean add_to_list,float damageMultiplier,float _speedFactor)
	{
		super.init();
		shooter=_shooter;
		anim=0;
		doitDeplace=false;
		tempsDetruit = 0;
		fixedWhenScreenMoves=false;
		localVit= new Vitesse(0,0);

		speedFactor=_speedFactor;

		deplacement = new T_normal(this,T_normal.tir,current_frame);

		nulle=false;
		encochee=true;
		this.setCollideWithNone();
		if(add_to_list)
			tabFleche.add(this);

		TEMPS_DESTRUCTION = (long) Math.pow(10, 9);//nanos, 1sec 

		damage = -5 * damageMultiplier;
		seyeri_cost=-8;//-5

		draw_tr=new AffineTransform();

	}
	public Fleche(List<Projectile> tabFleche,int current_frame,Heros _shooter,float damageMultiplier,float speedFactor)
	{
		this(tabFleche,current_frame,_shooter,true,damageMultiplier,speedFactor);
	}

	@Override
	public void setNeedDestroy()
	{
		needDestroy=true;
		this.doitDeplace=false;
		this.setCollideWithNone();
	}

	public void setPosition(int x, int y)
	{
		xpos_sync(x);
		ypos_sync(y);
	}

	public void flecheDecochee(AbstractModelPartie partie,Deplace deplace)
	{
		doitDeplace=true;
		encochee=false;
		afterDecochee=true;
		this.setCollideWithout(Arrays.asList(TypeObject.FLECHE,TypeObject.HEROS));
		//get current position
		Point2D newpos= draw_tr.transform(new Point(0,0), null);
		xpos_sync((int) newpos.getX()-partie.xScreendisp);
		ypos_sync((int) newpos.getY()-partie.yScreendisp);

		deplacement.hitbox_rotated  = Hitbox.convertHitbox(deplacement.hitbox,partie.INIT_RECT,draw_tr,new Point(xpos(),ypos()),new Point(partie.xScreendisp,partie.yScreendisp));

		//reset the 0 of the transformation. usefull since the position changed from 0 to its drawing position (~700,400)
		draw_tr.translate(-xpos(), -ypos());


		//deplacement.setSpeed(this, anim);
		MusicBruitage.startBruitage("arc");

	}
	public boolean getCanReshot()
	{
		return false;
	}
	/**
	 * 
	 * @param f
	 * @return In the canShootArrow function for Heros; Determine if this arrow should count for the total number of arrows that are involved for canShootArrow
	 */
	public boolean shouldCountArrow(Fleche f)
	{
		return true;
	}
	/**
	 * 
	 * @param partie
	 * @param firstFleche
	 * @return true if allow that arrow is rehoot, false otherwise 
	 */

	public boolean OnArrowReshot(AbstractModelPartie partie,Fleche firstFleche)
	{
		return false;
	}
	/**
	 * Called when the effect created by this arrow ends
	 */
	public void OnFlecheEffectDestroy(AbstractModelPartie partie,boolean destroyNow)
	{
		//Default behaviour is to destroy the arrow if the related effect is detroyed 
		this.destroy(partie, destroyNow);
	}

	//Method to redefined. Action to execute before an arrow is detroyed. Mainly, stop the effect before all references are lost 
	public void beforeFlecheDestroyed(AbstractModelPartie partie){};
	@Override
	public void destroy(AbstractModelPartie partie,boolean destroyNow)
	{
		beforeFlecheDestroyed(partie);
		super.destroy(partie, destroyNow);
		//remove itself from created effect 
		if(flecheEffect !=null)
			flecheEffect.onRemoveRefFleche(partie,destroyNow);
		flecheEffect=null;
	}
	@Override
	public void onDestroy(AbstractModelPartie partie){
		//do nothing when detroyed 
	}

	/**
	 * Eject arrow out of the colliding objects only if afterDecochee is set to true
	 * @return false if stuck
	 */
	protected boolean ejectArrow(AbstractModelPartie partie,Vector2d unprojectedSpeed)
	{
		return ejectArrow(partie,unprojectedSpeed,null);
	}
	/**
	 * Eject arrow out of the colliding objects only if afterDecochee is set to true
	 * @return false if stuck
	 */
	protected boolean ejectArrow(AbstractModelPartie partie,Vector2d unprojectedSpeed,Collidable[] resCollidableObject)
	{
		int MAX_EJECT = 40;
		boolean success = true;
		if(afterDecochee)
		{
			afterDecochee=false;
			Vector2d ejectVect = new Vector2d(unprojectedSpeed.x,unprojectedSpeed.y);
			ejectVect.normalize();
			ejectVect=new Vector2d(ejectVect.x*MAX_EJECT,ejectVect.y*MAX_EJECT);
			boolean setColliInfo = true;
			boolean warnColli = false;
			success = Collision.ejectWorldCollision(partie, this, ejectVect,true,setColliInfo,warnColli,resCollidableObject);
		}
		return success;
	}
	protected void onPlanted(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed, boolean stuck)
	{
		this.doitDeplace=false;

		if(this.afterDecochee&&stuck)
			ejectArrow(partie,unprojectedSpeed,null);
		if(collidedObject instanceof Roche_effect)
		{
			Roche_effect eff = (Roche_effect) collidedObject;
			if(eff.isWorldCollider){
				eff.addSynchroSpeed(this);
				if(flecheEffect!=null)
					eff.addSynchroSpeed(flecheEffect);
			}
		}
		this.destroy(partie, false);
	}
	/**
	 * 
	 * @param objects
	 * @param partie
	 * @param collider
	 * @return true if need immediate destroy
	 */
	protected boolean OnObjectsCollision (List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);
		return true;
	}

	public Vector2d getNormCollision()
	{
		return null;
	}

	@Override
	public int getMaxBoundingSquare()
	{
		return deplacement.getMaxBoundingSquare(this);
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp) {
		if(!encochee)
			return  Hitbox.plusPoint(deplacement.hitbox_rotated.get(anim), new Point(xpos(),ypos()),true);	
		else
			return  Hitbox.plusPoint(deplacement.hitbox_rotated.get(anim), new Point(xpos(),ypos()),true);	
	}

	@Override
	public Hitbox getHitbox(Point INIT_RECT,Point screenDisp, Mouvement _dep, int _anim) {
		//ASSUME WE ALWAYS USE THIS WHEN THE ARROW IS SHOT (!encochee)
		Mouvement_tir temp = (Mouvement_tir) _dep.Copy(this); //create the mouvement
		return Hitbox.plusPoint(temp.hitbox_rotated.get(_anim), new Point(xpos(),ypos()),true);
	}



	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {

		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		//boolean collision_bas = normal.y<0;	
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		Vector2d prevLocalVit = this.getGlobalVit(partie).vect2d();
		localVit= new Vitesse(0,0);
		this.doitDeplace=false;
		this.setCollideWithNone();
		ArrayList<Entitie> objects = Collidable.getAllEntitiesCollidable(partie);

		onPlanted(objects,partie,collidedObject,prevLocalVit,stuck);


	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Collidable collider,Vector2d normal) 
	{


		ArrayList<Entitie> objects = Collidable.getAllEntitiesCollidable(partie);

		needDestroy = OnObjectsCollision(objects,partie,collider,this.getGlobalVit(partie).vect2d(),normal);
		if(needDestroy)
			this.setNeedDestroy(); // stop collision and other stuff 
	}

	@Override
	public void memorizeCurrentValue() {
		//nothing to memorize so far 
		/*currentValue=new CurrentValue(){		
			@Override
			public void res()
			{}};*/
	}
	
	public AffineTransform updateTransform(AbstractModelPartie partie)
	{
		if(encochee)
		{
			int hanim = partie.heros.anim;
			Point pos = new Point();
			Point anchor = new Point(0,0); //only arrows towards its center
			Point taille = new Point(deplacement.xtaille.get(anim),deplacement.ytaille.get(anim));
			Point f_anchor = new Point(xanchor.get(hanim),yanchor.get(hanim));
			pos=new Point(partie.heros.xpos()+f_anchor.x,partie.heros.ypos()+f_anchor.y);
			//Anchor is relative to position: true_anchor = world anchor - mypos
			//world anchor = heros pos + heros anchor, mypos = heros  pos + fleche anchor
			anchor=new Point(partie.heros.deplacement.x_rot_pos.get(hanim)-f_anchor.x,
					partie.heros.deplacement.y_rot_pos.get(hanim)-f_anchor.y);					
			draw_tr = partie.getRotatedTransform(pos,anchor, taille, rotation);
		}
		return draw_tr;
	}
	private boolean updateRotation(AbstractModelPartie partie)
	{
		if(targetedPoint==null)
			return false;
		//get arrow tip
		Hitbox fHitbox = getHitbox(partie.INIT_RECT,partie.getScreenDisp());

		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(rotation-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(rotation+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		Vector2d arrowTip = new Vector2d(((v1.x+v2.x)/2),((v1.y+v2.y)/2));
		
		Vector2d deltaPos = new Vector2d(targetedPoint.x-arrowTip.x,targetedPoint.y-arrowTip.y);
		double dist = deltaPos.length();
		int threshold = 100;
		
		if(dist > threshold && !targetReached)
		{
			//0° points to the right 
			//90° points to the bottom
			rotation = Deplace.XYtoAngle(deltaPos.x, deltaPos.y);
			return true;
		}
		else if(dist <= threshold)
			targetReached=true;

		return false;
	}
	
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace, boolean update_with_speed) {
		if(encochee){
			deplacement.hitbox_rotated=Hitbox.convertHitbox(deplacement.hitbox,partie.INIT_RECT,draw_tr,new Point(xpos(),ypos()),partie.getScreenDisp());
		}
		else{
			updateRotation(partie);
			deplacement.setSpeed(this, anim);
		}

		try {
			anim=changeAnim(partie,deplace);} 
		catch (InterruptedException e) {e.printStackTrace();}
		
		updateTransform(partie);
		
		boolean[] res = {doitDeplace,animationChanged};
		return res;
	}
	public int changeAnim(AbstractModelPartie partie,Deplace deplace) throws InterruptedException//{{
	{
		if(encochee && !doitDeplace)
		{
			//memorize world desired position 
			targetedPoint = new Point(partie.getXPositionSouris()-partie.xScreendisp,partie.getYPositionSouris()-partie.yScreendisp);
			//set the anim 
			double[] anim_rot = deplace.getAnimRotationTir(partie,true);
			int animFleche = deplacement.updateAnimation(this, anim, partie.getFrame(),speedFactor);
			rotation = anim_rot[1];
			if(animFleche==anim)
				animationChanged=false;
			return animFleche;
		}

		else if(doitDeplace)
		{
			if (useGravity)
			{
				int animSuivante= gravityAnim(partie);
				if(animSuivante==anim)
					animationChanged=false;
				decallageFleche (animSuivante, partie );
				return(animSuivante);
			}
			else {
				int animFleche = deplacement.updateAnimation(this, anim, partie.getFrame(),speedFactor);
				if(animFleche==anim)
					animationChanged=false;
				return animFleche;
			}

		}
		else
			return deplacement.updateAnimation(this, anim, partie.getFrame(),speedFactor);
	}
	public int gravityAnim(AbstractModelPartie partie)
	{
		Vitesse gvit = getGlobalVit(partie);
		if(gvit.y ==0 && gvit.x==0)
		{
			return(anim);//on garde la même animation
		}
		else if(gvit.x>0 && Math.abs((float)gvit.y/gvit.x)<=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(0);
		}
		else if(gvit.y > 0 && gvit.x>0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(1);
		}
		else if(gvit.y > 0  && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(3* Math.PI/ 8)))
		{
			return(2);
		}
		else if(gvit.y > 0 && gvit.x<0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(3);
		}
		else if(gvit.x<0 && Math.abs((float)gvit.y/gvit.x)<= Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(4);
		}
		else if(gvit.y <0 && gvit.x<0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(5);
		}
		else if(gvit.y <0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(3* Math.PI/ 8)))
		{
			return(6);
		}
		else if(gvit.y <0 && gvit.x>0 && Math.abs((float)gvit.y/gvit.x)>=Math.abs(Math.tan(Math.PI/ 8)))
		{
			return(7);
		}
		else {
			throw new IllegalArgumentException("Fleche/gravityAnim: Unknown values");
		}
	}

	public void decallageFleche(int animSuivante, AbstractModelPartie partie)
	{
		// on veut que le centre bas des flèches coincident 
		Point positionFinal=placerCentreBasFleche(this,animSuivante,partie.INIT_RECT,partie.getScreenDisp());

		// on effectue le decallage
		xpos_sync(positionFinal.x);
		ypos_sync(positionFinal.y);

		//si il y a collision lors du changement d'animation, on doit arreter la fleche
		if (!Collision.ejectWorldCollision(partie , this))
		{
			//handleWorldCollision(new Vector2d(), partie,false); called by ejectWorldCollision
		}

	}
	private Vector2d angleToVector(double angle)
	{
		Vector2d res = new Vector2d(0,0);
		//Minimize chance of computing big numbers by taking the inverse 
		if(Math.abs(angle-Math.PI/2)< 0.001 * Math.PI || Math.abs(angle-3*Math.PI/2)< 0.001 * Math.PI)
			res=new Vector2d(1/Math.tan(angle),1);
		else
			res=new Vector2d(1,Math.tan(angle));

		res.normalize();
		return res;
	}
	/**
	 * Permet d'obtenir le centre en bas de la fleche (endroit ou on l'encoche)
	 * 
	 * @param xpos, position x de la fleche
	 * @param ypos, position y de la fleche
	 * @param anim, animation de la fleche
	 * @return le centre bas de la fleche 
	 */	
	public Point centreBasFleche(Hitbox flecheHit,int anim)
	{
		//In order to avoid that by taking the perfect direction we find only one points, we will calculate for two shifted angles
		double noise = 0.05; //~5°
		double angle = anim * Math.PI / 8; 
		Vector2d flecheDir1 = angleToVector(angle+noise);
		Vector2d flecheDir2 = angleToVector(angle-noise);

		//Look for the bottom center which is in the opposite way
		flecheDir1.negate();
		flecheDir2.negate();

		Vector2d p1 = Hitbox.supportPoint(flecheDir1, flecheHit.polygon);
		Vector2d p2 = Hitbox.supportPoint(flecheDir2, flecheHit.polygon);

		return new Point((int)(p1.x+p2.x),(int)(p1.y+p2.y));
	}
	/**
	 * Permet de placer le centre bas d'une fleche a un point donné
	 * 
	 * @param xValeurVoulue, position en x voulue pour le bas de la fleche
	 * @param yValeurVoulue,  position en y voulue pour le bas de la fleche
	 * @param anim, animation de la fleche
	 * @return la valeur de xpos et ypos pour la fleche
	 */	
	public Point placerCentreBasFleche(Fleche fleche,int nouvAnim,Point INIT_RECT,Point screenDisp)
	{
		Point currentCenter = centreBasFleche(fleche.getHitbox(INIT_RECT,screenDisp),fleche.anim);
		Point nextCenter = centreBasFleche(fleche.getHitbox(INIT_RECT,screenDisp, fleche.deplacement, nouvAnim),nouvAnim);

		return new Point(currentCenter.x-nextCenter.x,currentCenter.y-nextCenter.y);

	}
	
	public static Vector2d getArrowTip(AbstractModelPartie partie,Fleche f)
	{
		Hitbox fHitbox = f.getHitbox(partie.INIT_RECT,partie.getScreenDisp());

		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(f.rotation-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(f.rotation+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		int x_tip_fleche =  (int) ((v1.x+v2.x)/2);
		int y_tip_fleche= (int) ((v1.y+v2.y)/2);
		return new Vector2d(x_tip_fleche,y_tip_fleche);
	}
	public static Point getArrowTip(AbstractModelPartie partie,Fleche f,boolean point)
	{
		Vector2d v =  getArrowTip(partie,f);
		return new Point((int)v.x,(int)v.y);
	}
	
	@Override
	public void handleStuck(AbstractModelPartie partie) {
		handleWorldCollision( new Vector2d(), partie,null,true );
	}
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {
		// TODO Auto-generated method stub

	}
	@Override
	public void resetVarBeforeCollision()
	{
		//nothing
	}
	@Override
	public void resetVarDeplace(boolean speedUpdated) {
		if(afterDecochee && speedUpdated){
			afterDecochee=false;
		}
	}

	@Override
	public void applyFriction(double minlocalSpeed, double minEnvirSpeed) {
		//nothing
	}




}
