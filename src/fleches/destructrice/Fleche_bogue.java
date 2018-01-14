package fleches.destructrice;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.GJK_EPA;
import deplacement.Deplace;
import effects.Roche_effect;
import fleches.Fleche;
import partie.AbstractModelPartie;
import partie.PartieTimer;
import personnage.Heros;
import types.Entitie;
import types.Hitbox;
import types.Projectile;
import types.TypeObject;

public class Fleche_bogue  extends Destructrice{

	Parameters_fleche_bogue params = new Parameters_fleche_bogue();
	//Used to created the 7 copy arrows
	public Fleche_bogue(AbstractModelPartie partie,Fleche_bogue mainF,int xpos, int ypos, double _rotation) {
		super(partie.tabFleche, partie.getFrame(),mainF.shooter,true,mainF.params.damageMult,mainF.speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=-10*mainF.params.damageMult;
		seyeri_cost= -20;
		shooter=mainF.shooter;
		this.MAX_NUMBER_INSTANCE=1;
		this.doitDeplace=false;
		this.encochee=false;
		this.anim=mainF.anim;
		this.params = mainF.params;
		//TODO: set correctly with xpos, ypos, rotation
		xpos_sync(xpos);
		ypos_sync(ypos);
		this.rotation=_rotation;

		this.draw_tr=new AffineTransform();
		draw_tr.setToIdentity();
		draw_tr.translate(xpos()+partie.xScreendisp, ypos()+partie.yScreendisp);
		draw_tr.rotate(rotation);
		this.deplacement.hitbox_rotated=Hitbox.convertHitbox(deplacement.hitbox,partie.INIT_RECT,draw_tr,new Point(xpos(),ypos()),new Point(partie.xScreendisp,partie.yScreendisp));

		this.setCollideWithout(Arrays.asList(TypeObject.BLOC,TypeObject.FLECHE,TypeObject.HEROS));

	}
	//Used to create the main arrow
	public Fleche_bogue(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=-20*damageMult;
		shooter=_shooter;
		this.MAX_NUMBER_INSTANCE=1;
		params.bogueArrows.add(this);
		params.nbarrow+=1;
	}
	
	
	private int[] computeBoguePos(AbstractModelPartie partie,int xp, int yp, double rot)
	{
		// Hitbox hit, List<Hitbox> hitbox_rot
		
		//compute direction
		Vector2d direction = Deplace.angleToVector(rot);
		direction.normalize();
		direction.x*= this.params.current_distance;
		direction.y*= this.params.current_distance;

		/*Vector2d middle = Hitbox.getHitboxCenter(hitbox_rot.get(anim)); // this.deplacement.hitbox_rotated.get(anim)
		Vector2d tailTopLeft = getTailTopArrow(partie,rot,hit);
		//compute the relative middle 
		Vector2d rmiddle= new Vector2d(middle.x-tailTopLeft.x,middle.y-tailTopLeft.y);
		rmiddle.x+=xp;
		rmiddle.y+=yp;*/
		Vector2d shooter_tl = getShooterTopLeft(partie);
		int[] res = new int[2];
		res[0] = (int) Math.round (shooter_tl.x+params.center.x+direction.x); //-rmiddle.x
		res[1] = (int) Math.round(shooter_tl.y+params.center.y+direction.y); //-rmiddle.y
		return res;
	}
	private void addNewArrows(AbstractModelPartie partie)
	{
		double time = PartieTimer.me.getElapsedNano();
		if(params.nbarrow<params.NB_ARROW && (time-params.last_add_time)>params.ADD_ARROW_TIME*Math.pow(10, 9))
		{
			//computeCenter(partie);
			for(int j=0; j<2;j++)
			{
				int i = params.nbarrow-1;
				if(params.nbarrow<params.NB_ARROW)
				{
					//position is heros center + maxdistance in correct direction 
					double rot = params.creat_rot + ((i%2)==0? 2*Math.PI/params.NB_ARROW*(i+2)/2: -1 * 2*Math.PI/params.NB_ARROW*(i+1)/2);
					int[] xypos = computeBoguePos(partie,xpos(),ypos(),rot);//,getHitbox(partie.INIT_RECT),deplacement.hitbox_rotated
					params.bogueArrows.add(new Fleche_bogue(partie,this,xypos[0],xypos[1],rot));
					params.nbarrow+=1;
				}
				else
					break;
			}
			params.last_add_time=time;
		}

	}
	/**Top left with respect to human coordinates (y towards up)*/
	private Vector2d getShooterTopLeft(AbstractModelPartie partie)
	{
		//value to shift the rotation to get the two points from the tail
		double epsilon = Math.PI/10;

		double[] XY = Deplace.angleToXY(-3.0*Math.PI/4+epsilon);

		//get the opposite since the direction we found was the one towards the tip
		return  Hitbox.supportPoint(new Vector2d(XY[0],XY[1]),shooter.getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);//getHitbox(partie.INIT_RECT).polygon);
	}
	private Vector2d getTailTopArrow(AbstractModelPartie partie,double rot,Hitbox hit)
	{
		//value to shift the rotation to get the two points from the tail
		double epsilon = Math.PI/10;

		double[] XY = Deplace.angleToXY(rot+epsilon);

		//get the opposite since the direction we found was the one towards the tip
		Vector2d fleche_tail1 = Hitbox.supportPoint(new Vector2d(-XY[0],-XY[1]),hit.polygon);//getHitbox(partie.INIT_RECT).polygon);

		return fleche_tail1;
	}
	private Vector2d getMiddleTailArrow(AbstractModelPartie partie)
	{
		//value to shift the rotation to get the two points from the tail
		double epsilon = Math.PI/10;

		double[] XY = Deplace.angleToXY(rotation+epsilon);
		double[] XY2 = Deplace.angleToXY(rotation-epsilon);

		//get the opposite since the direction we found was the one towards the tip
		Vector2d fleche_tail1 = Hitbox.supportPoint(new Vector2d(-XY[0],-XY[1]),getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);
		Vector2d fleche_tail2 = Hitbox.supportPoint(new Vector2d(-XY2[0],-XY2[1]),getHitbox(partie.INIT_RECT,partie.getScreenDisp()).polygon);

		return new Vector2d(((fleche_tail1.x+fleche_tail2.x)/2),((fleche_tail1.y+fleche_tail2.y)/2));
	}
	private double getDistanceToShooter(AbstractModelPartie partie)
	{
		if(shooter ==null)
			return 0;

		//get the tail of the arrow
		Vector2d middleTailArrow = getMiddleTailArrow(partie);

		Vector2d shooterTopLeft = getShooterTopLeft(partie);

		double xPosRelative= middleTailArrow.x-params.center.x-shooterTopLeft.x; 
		double yPosRelative= middleTailArrow.y-params.center.y-shooterTopLeft.y;
		
		return Math.sqrt(xPosRelative*xPosRelative+yPosRelative*yPosRelative);
	}

	public List<Vector2d> computeIntersectionWithHitbox(Hitbox hit, Vector2d direction, Vector2d origin)
	{
		List<Vector2d> res = new ArrayList<Vector2d>();
		//loop over all the polygon segments poly[i],poly[j]
		for(int i =0; i<hit.polygon.npoints; i++){
			int j=(i+1)%(hit.polygon.npoints);
			Vector2d p1 = new Vector2d(hit.polygon.xpoints[i],hit.polygon.ypoints[i]);
			Vector2d p2 = new Vector2d(hit.polygon.xpoints[j],hit.polygon.ypoints[j]);
			Vector2d projectedPoint =GJK_EPA.projection(p1, p2, direction, origin,true);
			if(projectedPoint!=null){
				res.add(projectedPoint);
			}
		}
		return res;
	}

	private void computeCenter(AbstractModelPartie partie)
	{
		//compute the intersection between the arrow direction and the hitbox 
		final Vector2d direction = Deplace.angleToVector(this.rotation);
		direction.negate();

		final Vector2d origin = getMiddleTailArrow(partie); 
		final List<Vector2d> intersections = computeIntersectionWithHitbox(shooter.getHitbox(partie.INIT_RECT,partie.getScreenDisp()),direction,origin);

		//The middle of those points is the "center" that will be used to create the new arrows 
		this.params.center = new Vector2d();

		int nbInter = intersections.size();
		for(int i=0;i<nbInter;i++)
		{params.center.x+=intersections.get(i).x;params.center.y+=intersections.get(i).y;}
		params.center.x/=nbInter;
		params.center.y/=nbInter;

		//make it relative 
		Vector2d shooter_tl = getShooterTopLeft(partie);
		params.center.x-=shooter_tl.x;
		params.center.y-=shooter_tl.y;

	}
	@Override
	public void flecheDecochee(AbstractModelPartie partie,Deplace deplace)
	{
		computeCenter(partie);
		super.flecheDecochee(partie, deplace);
		params.creat_rot=this.rotation;
	}

	@Override
	public boolean OnArrowReshot(AbstractModelPartie partie,Fleche firstFleche)
	{
		params.shoot_arrows=true;
		return false;
	}
	public boolean getCanReshot()
	{
		return (this.params.reached_max_distance&&this.params.shoot_arrows );
	}
	@Override
	public boolean[] deplace(AbstractModelPartie partie, Deplace deplace, boolean update_with_speed) {
		boolean[] res = {false,false};
		if(this.needDestroy || this.tempsDetruit>0)
			return res;
		if(encochee){
			deplacement.hitbox_rotated=Hitbox.convertHitbox(deplacement.hitbox,partie.INIT_RECT,draw_tr,new Point(xpos(),ypos()),new Point(partie.xScreendisp,partie.yScreendisp));
		}
		if(params.shoot_arrows){
			this.doitDeplace=true;
			deplacement.setSpeed(this, anim);
			this.setCollideWithout(Arrays.asList(TypeObject.FLECHE,TypeObject.HEROS));
			return super.deplace(partie, deplace, update_with_speed);
		}
		boolean computeDist = !this.encochee; 
		if(this.params.lastFrameUpdate != partie.getFrame())
		{
			double dist=0;
			if(computeDist)
				dist = getDistanceToShooter(partie);
			if(dist<params.MAX_DISTANCE && !params.reached_max_distance)
				return super.deplace(partie, deplace, update_with_speed);
			else
			{
				if(!params.reached_max_distance){
					this.setCollideWithout(Arrays.asList(TypeObject.BLOC,TypeObject.FLECHE,TypeObject.HEROS));
					params.reached_max_distance=true;
					params.last_add_time= PartieTimer.me.getElapsedNano();
					params.current_distance=dist; 
				}
				else
				{
					/*int[] xypos = computeBoguePos(partie,xpos(),ypos(),rotation,getHitbox(partie.INIT_RECT),deplacement.hitbox_rotated);
					xpos_sync(xypos[0]);
					ypos_sync(xypos[1]);*/
					int[] xypos = new int[2];
					for(int i=0; i<params.bogueArrows.size();i++)
					{
						Fleche_bogue fb = params.bogueArrows.get(i);
						xypos = computeBoguePos(partie,fb.xpos(),fb.ypos(),fb.rotation);//fb.getHitbox(partie.INIT_RECT),fb.deplacement.hitbox_rotated
						fb.xpos_sync(xypos[0]);
						fb.ypos_sync(xypos[1]);
					}
				}
				addNewArrows(partie);
				this.params.lastFrameUpdate = partie.getFrame();
				//return shouldMove, changedAnimation 
				return res;
			}
		}
		else
		{
			return res;
		}

	}
}
