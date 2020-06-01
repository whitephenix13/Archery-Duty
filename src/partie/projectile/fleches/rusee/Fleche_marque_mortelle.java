package partie.projectile.fleches.rusee;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import debug.DebugBreak;
import gameConfig.InterfaceConstantes;
import menu.menuPrincipal.ModelPrincipal;
import music.MusicBruitage;
import partie.AI.A_Star_Helper;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.effects.Effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.PartieTimer;
import partie.mouvement.Deplace;
import partie.projectile.Projectile;
import utils.PointHelper;
import utils.Vitesse;

public class Fleche_marque_mortelle extends Rusee{
	
	//IMPROVEMENT: A_Star can be threaded (using a pool list) and return result when it ends to allow parallel A_star running without too much lag
	
	// WARNING : effect moves with 
	//	-colliding entity        			NO 
	//  -colliding ground (ie roche_effect) NO
	
	//The trail is composed of polygons that will be filled in white to simulate a trail behind the arrow 
	//TRAIL_TIME represents the delay between two addition of trail
	//UPDATE_TRAIL_TIME is used in case of transparency to update the transparency value by delta_transparency
	public ArrayList<Polygon> trails = new ArrayList<Polygon>();
	public Point trail_last_arrow_middle = null;
	public ArrayList<Color> trailsColor = new ArrayList<Color>();
	private double TRAIL_TIME = 0.01*Math.pow(10, 9); // 0.01 sec (10ms)
	private double last_trail_time = 0;
	private double UPDATE_TRAIL_TIME = 0.01*Math.pow(10, 9); // 0.01 sec (10ms)
	private double last_update_trail_time = 0;
	private int delta_transparency = 5;//12
	private int MAX_NUMBER_TRAIL = 40;
	
	//Size of the trail
	private double trail_height = 30; // size of the ytaille of T_normal for Fleche , original size is 6
	private double trail_width = 3; 

	public double shootTime =0; //used to compute an increasing speed over time
	
	public A_Star_Helper pathAlgo = null;
	private Collidable objTargeted;
	private boolean firstTarget;
	private int MAX_NUMBER_TARGET = 3;//Number of target to consider to smooth the direction towards next target when moving the arrow 
	//A Star algorithm variables 
	double reevaluation_time= 0.20*Math.pow(10, 9); // 0.05 sec (200ms)
	int max_step_size = InterfaceConstantes.TAILLE_BLOC/4; //maximum value by which the object can move at each iteration
	Point explorationAngle = new Point(1,4); //Maximum value by which the object can turn as a fraction of Math.Pi 
	int redirection_l = 2* InterfaceConstantes.TAILLE_BLOC; //This is used to delete the last x points when Updating the path
	//This value should be equal to the estimated distance that the target can move within REEVALUATION_TIME
	float smoothStrength = 0.0f;

	boolean stopTargeting = false;
	boolean shouldSetSpeed = true;
	//Maximum number of trails (life duration of a trail)
	
	public Fleche_marque_mortelle(List<Projectile> tabFleche, int current_frame,Heros _shooter, boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		//damage=-35*damageMult;
		damage=0;
		seyeri_cost=0; //-35
		firstTarget=true;
		pathAlgo = new A_Star_Helper(reevaluation_time,max_step_size, explorationAngle, redirection_l,smoothStrength);

	}

	public void setDebug(boolean val)
	{
		if(pathAlgo!=null)
			pathAlgo.setDebug(val);
	}
	
	@Override
	public void OnShoot(AbstractModelPartie partie){
		shootTime = PartieTimer.me.getElapsedNano();
		super.OnShoot(partie);
	}
	
	private Polygon generateTrail(AbstractModelPartie partie)
	{
		Vector2d arrow_middle = Hitbox.getObjMid(partie, this);
		if(trail_last_arrow_middle==null){
			
			trail_last_arrow_middle =PointHelper.VecToPoint(arrow_middle);
			return null;
		}

		Vector2d heightDir = new Vector2d(arrow_middle.x-trail_last_arrow_middle.x,arrow_middle.y-trail_last_arrow_middle.y);
		heightDir.normalize();
		heightDir.scale(trail_height/2);
		Vector2d widthDir = new Vector2d(heightDir.y,-heightDir.x);
		widthDir.normalize();
		widthDir.scale(trail_width/2);
		
		Point A = new Point((int)(trail_last_arrow_middle.x-widthDir.x),(int) (trail_last_arrow_middle.y-widthDir.y));
		Point B = new Point((int)(trail_last_arrow_middle.x+widthDir.x),(int) (trail_last_arrow_middle.y+widthDir.y));
		Point C = new Point((int)(B.x + heightDir.x),(int) (B.y + heightDir.y));
		Point D = new Point((int)(A.x + heightDir.x),(int) (A.y + heightDir.y));

		Polygon p = new Polygon();
		p.addPoint(A.x,A.y);
		p.addPoint(B.x,B.y);
		p.addPoint(C.x,C.y);
		p.addPoint(D.x,D.y);
		trail_last_arrow_middle = PointHelper.VecToPoint(arrow_middle);;
		return p;
	}

	private void updateTrail(AbstractModelPartie partie)
	{
		if(encochee)
			return;
		double time = PartieTimer.me.getElapsedNano() ;
		if((time - last_trail_time) > TRAIL_TIME )
		{
			Polygon trail = generateTrail(partie);
			if(trail !=null)
				trails.add(trail);
			trailsColor.add(Color.WHITE);
			while(trails.size()>MAX_NUMBER_TRAIL)
			{
				trails.remove(0);
				trailsColor.remove(0);
			}
			last_trail_time=time;
		}

		if(delta_transparency>0 && ((time - last_update_trail_time) > UPDATE_TRAIL_TIME) )
		{
			for(int i=0;i<trailsColor.size(); ++i)
			{
				Color c = trailsColor.get(i);
				int alpha = c.getAlpha();
				alpha = (alpha - delta_transparency) >0 ? alpha - delta_transparency : alpha;
				trailsColor.set(i, new Color(c.getRed(),c.getGreen(),c.getBlue(),alpha));
			}
			last_update_trail_time=time;
		}
	}

	Collidable FindTarget(AbstractModelPartie partie,Vector2d thisMid,Point mousePos)
	{
		double minDist = -1;
		Entity target = null;
		Point worldMousePos = new Point(mousePos.x-partie.getScreenDisp().x,mousePos.y-partie.getScreenDisp().y);
		for(Entity ent: partie.tabMonstre) //only take enemies visible on screen 
		{
			if(Collidable.isObjectOnScreen(partie, ent)){
				Vector2d objMid = Hitbox.getObjMid(partie, ent);
				//Consider scalar product of vector : fleche/mouse fleche/montre as fleche pos = heros pos at the start.
				//The target should minimize the angle and the distance to the mouse and be >0
				Vector2d thisToMouse = new Vector2d(worldMousePos.x-thisMid.x,worldMousePos.y-thisMid.y);
				thisToMouse.normalize();
				Vector2d thisToObj = new Vector2d(objMid.x-thisMid.x,objMid.y-thisMid.y);
				thisToObj.normalize();
				double cosAngle = thisToMouse.x*thisToObj.x+ thisToMouse.y*thisToObj.y;
				double distObjMouse = Math.sqrt(Math.pow(worldMousePos.x-objMid.x, 2) + Math.pow(worldMousePos.y-objMid.y, 2) );
				//Assume that the angle can have an imprecision of 10° (cosAngle = 0.984) 
				//Assume that the mouse click can have an imprecision of 500 (staying on the line fleche/click is more important)
				//Therefore we want 10° error <=> 500 dist error 
				//We consider the cosEquivalentDistance to be (1-cosAngle) * equivDist = 500 => equivDist ~ 33000 
				
				double dist = (1-cosAngle) *33000 + distObjMouse;
				if(dist<minDist || minDist<0)
				{
					minDist = dist;
					target= ent;
				}
			}
		}
		return target;
	}
	
	@Override
	protected boolean shouldUpdateSpeed(){
		return shouldSetSpeed;
	}
	

	@Override
	public boolean updateMouvementBasedOnAnimation(AbstractModelPartie partie) {
		
		ModelPrincipal.debugTime.startElapsedForVerbose();
		boolean res = shouldMove;		
		shouldSetSpeed = true; //set to false if speed is set in a custom way 
		if(!encochee && !stopTargeting)
		{
			OnTargetActive(partie);
			ModelPrincipal.debugTime.elapsed("change mouv_index based on target");	
		}
		else
		{
			res = super.updateMouvementBasedOnAnimation(partie);
			ModelPrincipal.debugTime.elapsed("change mouv_index based on regular update");	
		}
		ModelPrincipal.debugTime.elapsed("update transform and hitbox");
		updateTrail(partie);
		ModelPrincipal.debugTime.elapsed("update trail");

		return res;
	}
	/***
	 * Keep moving towards the target
	 */
	private void OnTargetActive(AbstractModelPartie partie){
		Vector2d dir = Deplace.angleToVector(getRotation());
		Vector2d thisMid = Hitbox.getObjMid(partie, this);
		Vector2d target=null;
		//If we have not found the first target => find closest target to mouse
		if(firstTarget)
		{
			objTargeted = FindTarget(partie,thisMid,shooter.getMousePositionWhenReleased());
			if(objTargeted != null)
				target = Hitbox.getObjMid(partie, objTargeted);
			firstTarget=false;
		}
		//Else if we found first target and it is not destroyed yet  
		if(!firstTarget && objTargeted!= null && !objTargeted.getNeedDestroy())
		{
			target = Hitbox.getObjMid(partie, objTargeted);
		}
		//target destroyed => move straight
		else if(!firstTarget)
		{
			stopTargeting =true;
			target = null;
		}
		ModelPrincipal.debugTime.elapsed("find target");
		if(target!=null){
			ArrayList<Point> nextPathPoints = pathAlgo.GetNextTargets(partie, this, dir, target,MAX_NUMBER_TARGET);
			ModelPrincipal.debugTime.elapsed("get next targets");
			if(nextPathPoints != null && nextPathPoints.size()>0){
				//Point to the next target and move to exactly end up at the target point.
				//This is due to avoid trajectory imprecision 
				Point nextPoint = nextPathPoints.get(0);
				Vector2d final_direction = new Vector2d(nextPoint.x-thisMid.x,nextPoint.y-thisMid.y);
				
				if(!final_direction.equals(new Vector2d(0,0))){
					setRotation(Deplace.XYtoAngle(final_direction.x, final_direction.y));
					ModelPrincipal.debugTime.elapsed("rotation");

					//Set speed to reach the target. Make sure that this speed is not bigger than the normal one 
					Vector2d vit1 = final_direction;
					double scaler = this.getMouvement().getSpeed(this, getMouvIndex()).length() / vit1.length();
					if(scaler<1){
						vit1.scale(scaler);
					}
					this.setLocalVit(new Vitesse(vit1));
					shouldSetSpeed=false;
					ModelPrincipal.debugTime.elapsed("set speed with targeting");
				}
			}
			else if(!stopTargeting)
			{
				Vector2d final_direction = new Vector2d(target.x-thisMid.x,target.y-thisMid.y);
				setRotation(Deplace.XYtoAngle(final_direction.x, final_direction.y));
				ModelPrincipal.debugTime.elapsed("rotation");
				partie.forceRepaint();
				ModelPrincipal.debugTime.elapsed("force repaint");
				
				double scaler = this.getMouvement().getSpeed(this, getMouvIndex()).length() / final_direction.length();
				if(scaler<1){
					final_direction.scale(scaler);
				}
				this.setLocalVit(new Vitesse(final_direction));
				
				shouldSetSpeed=false;
				stopTargeting=true;
				ModelPrincipal.debugTime.elapsed("set speed without targeting");
			}
		}
		else
			stopTargeting=true;
	}


	@Override
	public void destroy(AbstractModelPartie partie,boolean destroyNow)
	{
		stopTargeting=true;
		pathAlgo.OnDestroy(partie);
		super.destroy(partie, destroyNow);
	}
	@Override
	protected void onPlanted(List<Entity> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck){
			destroy(partie,false);
			return;
		}
		if(!generatedEffect){
			generatedEffect=true;
			MusicBruitage.me.startBruitage("arc");

			if(collidedObject instanceof Roche_effect)
			{
				Roche_effect eff = (Roche_effect) collidedObject;
				if(eff.isWorldCollider){
					eff.addSynchroSpeed(this);
					eff.addSynchroSpeed(flecheEffect);
				}
			}
			this.simulateDestroy();
			this.isVisible=true;
		}
		destroy(partie,false);
	}

	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if((collider instanceof Projectile))
			return false;
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);
		if(!generatedEffect){
			generatedEffect=true;
			MusicBruitage.me.startBruitage("arc");

		}
		return true;
	}

}
