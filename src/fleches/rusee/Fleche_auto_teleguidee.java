package fleches.rusee;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Vector2d;

import AI.A_Star_Helper;
import collision.Collidable;
import debug.Debug_stack;
import deplacement.Deplace;
import effects.Effect;
import effects.Roche_effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import partie.PartieTimer;
import personnage.Heros;
import principal.InterfaceConstantes;
import types.Entitie;
import types.Hitbox;
import types.Projectile;

public class Fleche_auto_teleguidee extends Rusee{

	//The trail is composed of polygons that will be filled in white to simulate a trail behind the arrow 
	//TRAIL_TIME represents the delay between two addition of trail
	//UPDATE_TRAIL_TIME is used in case of transparency to update the transparency value by delta_transparency
	public ArrayList<Polygon> trails = new ArrayList<Polygon>();
	public ArrayList<Color> trailsColor = new ArrayList<Color>();
	private double TRAIL_TIME = 0.01*Math.pow(10, 9); // 0.01 sec (10ms)
	private double last_trail_time = 0;
	private double UPDATE_TRAIL_TIME = 0.01*Math.pow(10, 9); // 0.01 sec (10ms)
	private double last_update_trail_time = 0;
	private int delta_transparency = 12;

	//Size of the trail
	private double trail_height = 4; // size of the ytaille of T_normal for Fleche , original size is 6
	private double trail_width = 50; 

	public A_Star_Helper pathAlgo = null;
	//A Star algorithm variables 
	double reevaluation_time= 0.15*Math.pow(10, 9); // 0.05 sec (150ms)
	int max_step_size = InterfaceConstantes.TAILLE_BLOC; //maximum value by which the object can move at each iteration
	Point explorationAngle = new Point(1,4); //Maximum value by which the object can turn as a fraction of Math.Pi
	int redirection_l = 2* InterfaceConstantes.TAILLE_BLOC; //This is used to delete the last x points when Updating the path
	//This value should be equal to the estimated distance that the target can move within REEVALUATION_TIME
	float smoothStrength = 0.0f;

	boolean stopTargeting = false;

	//Maximum number of trails (life duration of a trail)
	private int MAX_NUMBER_TRAIL = 20;
	public Fleche_auto_teleguidee(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=-35*damageMult;
		seyeri_cost=0; //-35

		pathAlgo = new A_Star_Helper(reevaluation_time,max_step_size, explorationAngle, redirection_l,smoothStrength);
	}

	public void setDebug(boolean val)
	{
		pathAlgo.setDebug(val);
	}

	private Polygon generateTrail(AbstractModelPartie partie)
	{
		Hitbox hit = this.getHitbox(partie.INIT_RECT, partie.getScreenDisp());

		/* Hitbox
		 * p2  ______
		 *    |      |
		 *    |	     | => 
		 * p1 |______|
		 *      +PI/2 
		 *      
		 * Trail 
		 *     ______ p2
		 *   C|______|B
		 *    |______|       => 
		 *   D|______|A
		 *    		  p1
		 *      
		 */
		Vector2d p1 = Hitbox.supportPoint(Deplace.angleToVector(rotation+Math.PI-Math.PI/10), hit.polygon); //bottom left of unrotated hitbox (with tip pointing right)
		Vector2d p2 = Hitbox.supportPoint(Deplace.angleToVector(rotation+Math.PI+Math.PI/10), hit.polygon); //top left of unrotated hitbox (with tip pointing right)

		Vector2d widthDir = Deplace.angleToVector(rotation+Math.PI);
		widthDir.normalize();
		Vector2d p1_to_p2_dir = Deplace.angleToVector(rotation-Math.PI/2);
		p1_to_p2_dir.normalize();
		//Default height is 19 (based on T_normal of fleche)
		int deltaHeight = (int) ((6 - trail_height)/2);
		Point A = new Point((int)(p1.x + p1_to_p2_dir.x * deltaHeight),(int) (p1.y + p1_to_p2_dir.y * deltaHeight));
		Point B = new Point((int)(p2.x - p1_to_p2_dir.x * deltaHeight),(int) (p2.y - p1_to_p2_dir.y * deltaHeight));
		Point C = new Point((int)(B.x + widthDir.x * trail_width),(int) (B.y + widthDir.y * trail_width));
		Point D = new Point((int)(A.x + widthDir.x * trail_width),(int) (A.y + widthDir.y * trail_width));

		Polygon p = new Polygon();
		Point screenDisp = partie.getScreenDisp();
		p.addPoint(A.x+screenDisp.x,A.y+screenDisp.y);
		p.addPoint(B.x+screenDisp.x,B.y+screenDisp.y);
		p.addPoint(C.x+screenDisp.x,C.y+screenDisp.y);
		p.addPoint(D.x+screenDisp.x,D.y+screenDisp.y);
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

	Point FindTarget(AbstractModelPartie partie,Vector2d thisMid)
	{
		float minDist = -1;
		Point target = new Point();
		for(Entitie ent: partie.tabMonstre)
		{
			Vector2d objMid = Hitbox.getHitboxCenter(ent.getHitbox(partie.INIT_RECT, partie.getScreenDisp()));
			float dist = (float) Math.sqrt(Math.pow(thisMid.x-objMid.x, 2) + Math.pow(thisMid.y-objMid.y, 2) );
			if(dist<minDist || minDist<0)
			{
				minDist = dist;
				target= new Point((int)objMid.x,(int)objMid.y);
			}
		}
		return target;
	}

	Point tempTarget =null;
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace) {
		boolean[] res = {doitDeplace,animationChanged};

		if(!encochee)
		{
			System.out.print(""); //break to see arrow movement 
			if(!stopTargeting){
				Vector2d dir = Deplace.angleToVector(rotation);
				Vector2d thisMid = Hitbox.getHitboxCenter(getHitbox(partie.INIT_RECT, partie.getScreenDisp()));
				//TODO: Point target = FindTarget(partie,thisMid);
				if(tempTarget==null)
					tempTarget = new Point(100-partie.xScreendisp,100-partie.yScreendisp);
				else
					tempTarget = new Point(tempTarget.x+10,tempTarget.y);//10
				Point target = tempTarget;
				if(target!=null){
					Point nextPathPoint = pathAlgo.GetNextTarget(partie, this, dir, target);
					System.out.println("Next " + nextPathPoint +" /// " + pathAlgo.getPath() );
					if(nextPathPoint!= null){
						//update speed and rotation
						Vector2d direction = new Vector2d(nextPathPoint.x-thisMid.x,nextPathPoint.y-thisMid.y);
						if(!direction.equals(new Vector2d(0,0))){
							rotation = Deplace.XYtoAngle(direction.x, direction.y);
							partie.forceRepaint();
							try {
								TimeUnit.MILLISECONDS.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							System.out.print(""); //break to see arrow movement 
						}

					}
					else
						stopTargeting=true;
				}
				else
					stopTargeting=true;
			}
			deplacement.setSpeed(this, anim);
		}
		else
			updateTransformAndHitbox(partie);
		updateTrail(partie);

		try {
			anim=changeAnim(partie,deplace);} 
		catch (InterruptedException e) {e.printStackTrace();}

		res[0]=doitDeplace;res[1]=animationChanged;
		return res;
	}

	@Override
	public void destroy(AbstractModelPartie partie,boolean destroyNow)
	{
		stopTargeting=true;
		pathAlgo.OnDestroy(partie);
		super.destroy(partie, destroyNow);
	}
	@Override
	protected void onPlanted(List<Entitie> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck){
			destroy(partie,false);
			return;
		}
		if(!generatedEffect){
			generatedEffect=true;
			MusicBruitage.startBruitage("arc");

			if(collidedObject instanceof Roche_effect)
			{
				Roche_effect eff = (Roche_effect) collidedObject;
				if(eff.isWorldCollider){
					eff.addSynchroSpeed(this);
					eff.addSynchroSpeed(flecheEffect);
				}
			}
			this.doitDeplace=false;
			this.setCollideWithNone();
		}
		destroy(partie,false);
	}

	@Override
	protected boolean OnObjectsCollision(List<Entitie> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);
		if(!generatedEffect){
			generatedEffect=true;
			MusicBruitage.startBruitage("arc");

		}
		return true;
	}

}
