package partie.projectile.fleches;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import gameConfig.InterfaceConstantes;
import gameConfig.TypeObject;
import menu.menuPrincipal.ModelPrincipal;
import music.MusicBruitage;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.Hitbox;
import partie.deplacement.Deplace;
import partie.deplacement.Mouvement;
import partie.deplacement.entity.Mouvement_entity;
import partie.deplacement.projectile.T_normal;
import partie.deplacement.projectile.T_normal.TypeTirNormal;
import partie.effects.Effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.Projectile;
import utils.PointHelper;
import utils.Vitesse;

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

	//relative to heros position
	public List<Integer> xanchor=Arrays.asList(22,18,40,42,38,24,52,50,65,32);
	public List<Integer> yanchor=Arrays.asList(30,20,22,24,44,48,62,42,30,36);

	public float damage;
	public float seyeri_cost;
	protected boolean animationChanged=false;
		
	public Effect flecheEffect;

	public Fleche(List<Projectile> tabFleche,int current_frame,Heros _shooter,boolean add_to_list,float damageMultiplier,float _speedFactor)
	{
		super.init();
		shooter=_shooter;
		setAnim(0);
		doitDeplace=false;
		tempsDetruit = 0;
		fixedWhenScreenMoves=false;
		localVit= new Vitesse(0,0);

		speedFactor=_speedFactor;

		setDeplacement(new T_normal(this,TypeTirNormal.Tir,current_frame));

		nulle=false;
		encochee=true;
		this.setCollideWithNone();
		if(add_to_list)
			tabFleche.add(this);

		TEMPS_DESTRUCTION = (long) Math.pow(10, 9);//nanos, 1sec 

		damage = -5 * damageMultiplier;
		seyeri_cost=-5;//-5

	}
	public Fleche(List<Projectile> tabFleche,int current_frame,Heros _shooter,float damageMultiplier,float speedFactor)
	{
		this(tabFleche,current_frame,_shooter,true,damageMultiplier,speedFactor);
	}

	public void setPosition(int x, int y)
	{
		setXpos_sync(x);
		setYpos_sync(y);
	}

	public void OnShoot(AbstractModelPartie partie,Deplace deplace)
	{
		AffineTransform current_draw_tr = _computeDrawTr(false,partie.getScreenDisp());
		
		doitDeplace=true;
		encochee=false;
		afterDecochee=true;
		this.setCollideWithout(Arrays.asList(TypeObject.FLECHE,TypeObject.HEROS));

		Vector2d middle = Hitbox.getHitboxCenter(getDeplacementHitbox(getAnim()));
		Point2D newpos= current_draw_tr.transform(PointHelper.VecToPoint(middle), null);

		//the goal is to set xpos and ypos correctly knowing that the new transform will follow this formula (cf: partie.getRotatedTransform)  
		//new transform = Rotation (translate ("new pos"), "around middle in global coordinates") -> translating arrow + rotating around center
		//We therefore want that the unrotated hitbox arond its center to match hitbox + pos 
		//Therefore the position is the position of the tranformed middle (as middle pos is unchanged by rotation) - middle hitbox (as positions are top left)
		setXpos_sync((int) newpos.getX()-(int)middle.x); 
		setYpos_sync((int) newpos.getY()-(int)middle.y);

		MusicBruitage.me.startBruitage("arc");

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
		this.doitDeplace=false;
		this.setCollideWithNone();
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
	protected void onPlanted(List<Entity> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed, boolean stuck)
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
	protected boolean OnObjectsCollision (List<Entity> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
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
		return getDeplacement().getMaxBoundingSquare(this);
	}
	

	@Override
	public Point getMaxBoundingRect() {	
		return getDeplacement().getMaxBoundingRect(this);
	}
	
	@Override
	protected AffineTransform _computeDrawTr(boolean screenReferential,Point screendisp)
	{
		final Point pos;
		final Point anchor;
		if(encochee)
		{
			//Rotate the arrow around the hero based on the hero anchor (arround what shoult the arrow rotate) and the arrow anchor (which part of the arrow should rotate)
			//Update the transform
			int hanim = shooter.getAnim();
			Point anchorPos = new Point(xanchor.get(hanim),yanchor.get(hanim));//xanchor relative to heros pos
			Point herosAnchor = new Point(((Mouvement_entity)shooter.getDeplacement()).x_rot_pos.get(shooter.getAnim()),((Mouvement_entity)shooter.getDeplacement()).y_rot_pos.get(shooter.getAnim()));

			//anchor should be the same as for the hero body parts 
			anchor = new Point((int)(herosAnchor.x-anchorPos.x),(int)(herosAnchor.y-anchorPos.y));
			pos=new Point(shooter.getXpos()+anchorPos.x- screendisp.x,shooter.getYpos()+anchorPos.y-screendisp.y); 
		}
		else
		{
			//Update the transform
			Vector2d middle = Hitbox.getHitboxCenter(getDeplacementHitbox(getAnim()));
			pos = new Point(getXpos(),getYpos());
			anchor = PointHelper.VecToPoint(middle);
		}
		if(screenReferential)
		{
			final AffineTransform res = new AffineTransform();
			res.translate(screendisp.x, screendisp.y);
			res.concatenate(AbstractModelPartie.getRotatedTransform(pos,anchor, getRotation()));
			return res;
		}
		else
			return AbstractModelPartie.getRotatedTransform(pos,anchor, getRotation());
	}
	
	@Override
	public AffineTransform computeDrawTr(Point screenDisp)
	{
		return _computeDrawTr(true,screenDisp);
	}
	
	protected Hitbox computeRotatedHitbox(Point screenDisp,Mouvement dep, int anim)
	{
		return Hitbox.convertHitbox(dep.getHitbox().get(anim),_computeDrawTr(false,screenDisp),new Point(getXpos(),getYpos()),new Point(0,0));
	}	
	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp) {
		Hitbox rotatedHit = computeRotatedHitbox(screenDisp,getDeplacement(),getAnim());
		return  Hitbox.plusPoint(rotatedHit, new Point(getXpos(),getYpos()),true);	
	}

	@Override
	public Hitbox computeHitbox(Point INIT_RECT,Point screenDisp, Mouvement _dep, int _anim) {
		Hitbox rotatedHit = computeRotatedHitbox(screenDisp,_dep,_anim);
		return Hitbox.plusPoint(rotatedHit, new Point(getXpos(),getYpos()),true);
	}



	@Override
	public void handleWorldCollision(Vector2d normal, AbstractModelPartie partie,Collidable collidedObject,boolean stuck) {
		ModelPrincipal.debugTime.startElapsedForVerbose();
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		//boolean collision_bas = normal.y<0;	
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		Vector2d prevLocalVit = this.getGlobalVit(partie);
		localVit= new Vitesse(0,0);
		this.doitDeplace=false;
		this.setCollideWithNone();
		ArrayList<Entity> objects = Collidable.getAllEntitiesCollidable(partie);

		onPlanted(objects,partie,collidedObject,prevLocalVit,stuck);
		ModelPrincipal.debugTime.elapsed("End of fleche handle world collision ");

	}
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,
			Collidable collider,Vector2d normal) 
	{


		ArrayList<Entity> objects = Collidable.getAllEntitiesCollidable(partie);

		needDestroy = OnObjectsCollision(objects,partie,collider,this.getGlobalVit(partie),normal);
		if(needDestroy)
			this.destroy(partie, true); // stop collision and other stuff 
	}

	@Override
	public void memorizeCurrentValue() {
	}
	
	private boolean updateRotation(AbstractModelPartie partie)
	{
		if(targetedPoint==null)
			return false;

		targetedPoint = new Point(targetedPoint.x,targetedPoint.y); 
		//get arrow tip
		Hitbox fHitbox = getHitbox(partie.INIT_RECT,partie.getScreenDisp());

		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(getRotation()-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(getRotation()+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		final Vector2d arrowTip = new Vector2d(((v1.x+v2.x)/2),((v1.y+v2.y)/2));
		Vector2d deltaPos = new Vector2d(targetedPoint.x-arrowTip.x,targetedPoint.y-arrowTip.y);
		double dist = deltaPos.length();
		int threshold = 100;

		if(dist > threshold && !targetReached)
		{
			//0° points to the right 
			//90° points to the bottom
			setRotation(Deplace.XYtoAngle(deltaPos.x, deltaPos.y));
			return true;
		}
		else if(dist <= threshold)
			targetReached=true;

		return false;
	}
	
	
	
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		boolean[] res = {doitDeplace,animationChanged};
		if(encochee)
		{
			//the arrow shouldn't move, so we call handleDeplacementSuccess to make sure that the transform is updated
			//updateTransformAndHitbox(partie);
			try {setAnim(changeAnim(partie,deplace));} catch (InterruptedException e) {e.printStackTrace();}
		}
		else
		{
			if(doitDeplace){
				updateRotation(partie);
				try {setAnim(changeAnim(partie,deplace));} catch (InterruptedException e) {e.printStackTrace();}
				getDeplacement().setSpeed(this, getAnim()); 
				//updateTransformAndHitbox(partie); // 
			}
		}
		return res;
	}
	
	@Override
	public void handleDeplacementSuccess(AbstractModelPartie partie) {
	}
	public int changeAnim(AbstractModelPartie partie,Deplace deplace) throws InterruptedException//{{
	{
		if(encochee && !doitDeplace)
		{
			//memorize world desired position 
			targetedPoint = new Point(partie.getXPositionSouris()-partie.xScreendisp,partie.getYPositionSouris()-partie.yScreendisp); 
			//set the anim 
			double[] anim_rot = deplace.getAnimRotationTir(partie,true);
			int animFleche = getDeplacement().updateAnimation(this,getAnim(), partie.getFrame(),speedFactor);
			setRotation(anim_rot[1]);
			if(animFleche==getAnim())
				animationChanged=false;
			return animFleche;
		}

		else if(doitDeplace)
		{
				int animFleche = getDeplacement().updateAnimation(this,getAnim(), partie.getFrame(),speedFactor);
				if(animFleche==getAnim())
					animationChanged=false;
				return animFleche;
		}
		else
			return getDeplacement().updateAnimation(this,getAnim(), partie.getFrame(),speedFactor);
	}

	public void decallageFleche(int animSuivante, AbstractModelPartie partie)
	{
		// on veut que le centre bas des flèches coincident 
		Point positionFinal=placerCentreBasFleche(this,animSuivante,partie.INIT_RECT,partie.getScreenDisp());

		// on effectue le decallage
		setXpos_sync(positionFinal.x);
		setYpos_sync(positionFinal.y);

		//si il y a collision lors du changement d'getAnim()ation, on doit arreter la fleche
		if (!Collision.ejectWorldCollision(partie , this))
		{
			//handleWorldCollision(new Vector2d(), partie,false); called by ejectWorldCollision
		}

	}

	/**
	 * Permet d'obtenir le centre en bas de la fleche (endroit ou on l'encoche)
	 * 
	 * @param xpos, position x de la fleche
	 * @param ypos, position y de la fleche
	 * @param anim, animation de la fleche
	 * @return le centre bas de la fleche 
	 */	

	@Override
	public void handleStuck(AbstractModelPartie partie) {
		handleWorldCollision( new Vector2d(), partie,null,true );
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

	@Override
	public Hitbox getNextEstimatedHitbox(AbstractModelPartie partie, double newRotation,int anim)
	{
		if(encochee)
			return null;
		AffineTransform tr = AbstractModelPartie.getRotatedTransform(new Point(getXpos(),getYpos()), 
				new Point(getDeplacement().xtaille.get(anim)/2,getDeplacement().ytaille.get(anim)/2), newRotation);
		List<Hitbox> lHit= Hitbox.convertHitbox(getDeplacement().getHitbox(),tr,new Point(0,0),new Point());

		double oldRota = this.getRotation();
		setRotation(newRotation,true);
		Vitesse vit = getDeplacement().getSpeed(this, anim);
		setRotation(oldRota,true);
		return Hitbox.plusPoint(lHit.get(anim),getGlobalVit(partie,vit).point(),false);
	}
	
	public Point centreBasFleche(Hitbox flecheHit,int anim)
	{
		//In order to avoid that by taking the perfect direction we find only one points, we will calculate for two shifted angles
		double noise = 0.05; //~5°
		double angle = anim * Math.PI / 8;
		Vector2d flecheDir1 = Deplace.angleToVector(angle+noise);
		Vector2d flecheDir2 = Deplace.angleToVector(angle-noise);

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
		Point currentCenter = centreBasFleche(fleche.getHitbox(INIT_RECT,screenDisp),fleche.getAnim());
		Point nextCenter = centreBasFleche(fleche.getHitbox(INIT_RECT,screenDisp, fleche.getDeplacement(), nouvAnim),nouvAnim);

		return new Point(currentCenter.x-nextCenter.x,currentCenter.y-nextCenter.y);

	}

	public static Vector2d getArrowTip(AbstractModelPartie partie,Fleche f)
	{
		Hitbox fHitbox = f.getHitbox(partie.INIT_RECT,partie.getScreenDisp());

		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(f.getRotation()-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(f.getRotation()+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		int x_tip_fleche =  (int) ((v1.x+v2.x)/2);
		int y_tip_fleche= (int) ((v1.y+v2.y)/2);
		return new Vector2d(x_tip_fleche,y_tip_fleche);
	}
	public static Point getArrowTip(AbstractModelPartie partie,Fleche f,boolean point)
	{
		Vector2d v =  getArrowTip(partie,f);
		return PointHelper.VecToPoint(v);
	}
	



}
