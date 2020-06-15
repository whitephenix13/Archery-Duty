package partie.projectile.fleches.destructrice;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.GJK_EPA;
import partie.collision.Hitbox;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.modelPartie.ModelPartie;
import partie.modelPartie.PartieTimer;
import partie.mouvement.Deplace;
import partie.mouvement.Mouvement;
import partie.projectile.Projectile;
import partie.projectile.fleches.Fleche;

public class Fleche_barrage  extends Destructrice{
	private boolean DESACTIVATED = true;
	// WARNING : effect moves with 
	//	-colliding entity        			NO 
	//  -colliding ground (ie roche_effect) NO
	
	Parameters_marque_mortelle params = new Parameters_marque_mortelle();
	//Used to created the 7 copy arrows
	public Fleche_barrage(Fleche_barrage mainF,int xpos, int ypos, double _rotation) {
		super(ModelPartie.me.tabFleche, ModelPartie.me.getFrame(),mainF.shooter,true,mainF.params.damageMult,mainF.speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=-10*mainF.params.damageMult;
		seyeri_cost=0; //20
		shooter=mainF.shooter;
		this.MAX_NUMBER_INSTANCE=1;
		this.shouldMove=false;
		this.encochee=false;
		setMouvIndex(mainF.getMouvIndex());
		this.params = mainF.params;
		setXpos_sync(xpos);
		setYpos_sync(ypos);
		setRotation(_rotation);

		this.setCollideWithout(Arrays.asList(ObjectType.BLOC,ObjectType.FLECHE,ObjectType.HEROS));
		
	}
	//Used to create the main arrow
	public Fleche_barrage(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=-20*damageMult;
		shooter=_shooter;
		this.MAX_NUMBER_INSTANCE=1;
		params.barrageArrows.add(this);
		params.nbarrow+=1;
		
	}
	
	
	private int[] computeBarragePos(double rot)
	{		
		//compute direction
		Vector2d direction = Deplace.angleToVector(rot);
		direction.normalize();
		direction.x*= this.params.current_distance;
		direction.y*= this.params.current_distance;

		Vector2d shooter_tl = getShooterTopLeft();
		int[] res = new int[2];
		res[0] = (int) Math.round (shooter_tl.x+params.center.x+direction.x); 
		res[1] = (int) Math.round(shooter_tl.y+params.center.y+direction.y); 
		return res;
	}
	private void addNewArrows()
	{
		double time = PartieTimer.me.getElapsedNano();
		if(params.nbarrow<params.NB_ARROW && (time-params.last_add_time)>params.ADD_ARROW_TIME*Math.pow(10, 9))
		{
			for(int j=0; j<2;j++)
			{
				int i = params.nbarrow-1;
				if(params.nbarrow<params.NB_ARROW)
				{
					//position is heros center + maxdistance in correct direction 
					double rot = params.creat_rot + ((i%2)==0? 2*Math.PI/params.NB_ARROW*(i+2)/2: -1 * 2*Math.PI/params.NB_ARROW*(i+1)/2);
					int[] xypos = computeBarragePos(rot);//,getHitbox(ModelPartie.me.INIT_RECT),deplacement.hitbox_rotated
					params.barrageArrows.add(new Fleche_barrage(this,xypos[0],xypos[1],rot));
					params.nbarrow+=1;
				}
				else
					break;
			}
			params.last_add_time=time;
		}

	}
	/**Top left with respect to human coordinates (y towards up)*/
	private Vector2d getShooterTopLeft()
	{
		//value to shift the rotation to get the two points from the tail
		double epsilon = Math.PI/10;

		double[] XY = Deplace.angleToXY(-3.0*Math.PI/4+epsilon);

		//get the opposite since the direction we found was the one towards the tip
		return  Hitbox.supportPoint(new Vector2d(XY[0],XY[1]),shooter.getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp()).polygon);//getHitbox(ModelPartie.me.INIT_RECT).polygon);
	}

	private Vector2d getMiddleTailArrow()
	{
		//value to shift the rotation to get the two points from the tail
		double epsilon = Math.PI/10;

		double[] XY = Deplace.angleToXY(getRotation()+epsilon);
		double[] XY2 = Deplace.angleToXY(getRotation()-epsilon);

		//get the opposite since the direction we found was the one towards the tip
		Vector2d fleche_tail1 = Hitbox.supportPoint(new Vector2d(-XY[0],-XY[1]),getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp()).polygon);
		Vector2d fleche_tail2 = Hitbox.supportPoint(new Vector2d(-XY2[0],-XY2[1]),getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp()).polygon);

		return new Vector2d(((fleche_tail1.x+fleche_tail2.x)/2),((fleche_tail1.y+fleche_tail2.y)/2));
	}
	private double getDistanceToShooter()
	{
		if(shooter ==null)
			return 0;

		//get the tail of the arrow
		Vector2d middleTailArrow = getMiddleTailArrow();

		Vector2d shooterTopLeft = getShooterTopLeft();

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

	private void computeCenter()
	{
		//compute the intersection between the arrow direction and the hitbox 
		final Vector2d direction = Deplace.angleToVector(this.getRotation());
		direction.negate();

		final Vector2d origin = getMiddleTailArrow(); 
		final List<Vector2d> intersections = computeIntersectionWithHitbox(shooter.getHitbox(ModelPartie.me.INIT_RECT,ModelPartie.me.getScreenDisp()),direction,origin);

		//The middle of those points is the "center" that will be used to create the new arrows 
		this.params.center = new Vector2d();

		int nbInter = intersections.size();
		for(int i=0;i<nbInter;i++)
		{params.center.x+=intersections.get(i).x;params.center.y+=intersections.get(i).y;}
		params.center.x/=nbInter;
		params.center.y/=nbInter;

		//make it relative 
		Vector2d shooter_tl = getShooterTopLeft();
		params.center.x-=shooter_tl.x;
		params.center.y-=shooter_tl.y;

	}
	@Override
	public void OnShoot()
	{
		computeCenter();
		super.OnShoot();
		params.creat_rot=this.getRotation();
	}

	@Override
	public boolean OnArrowReshot(Fleche firstFleche)
	{
		params.shoot_arrows=true;
		return false;
	}
	public boolean getCanReshot()
	{
		return (this.params.reached_max_distance&&this.params.shoot_arrows );
	}
	
	@Override 
	public boolean updateMouvementBasedOnAnimation() {
		if(DESACTIVATED)
			return super.updateMouvementBasedOnAnimation();;
		if(this.needDestroy || this.tempsDetruit>0){
			shouldMove = false;
			return false;
		}
		
		//The arrows were shot, use regular deplace
		if(params.shoot_arrows){
			this.shouldMove=true;
			this.setCollideWithout(Arrays.asList(ObjectType.FLECHE,ObjectType.HEROS));
			return super.updateMouvementBasedOnAnimation();
		}
		boolean computeDist = !this.encochee; 
		if(this.params.lastFrameUpdate != ModelPartie.me.getFrame())
		{
			double dist=0;
			if(computeDist)
				dist = getDistanceToShooter();
			//The main arrow is shot (barrage still not created) but max distance is not reached yet: use regular deplace
			if(dist<params.MAX_DISTANCE && !params.reached_max_distance)
				return super.updateMouvementBasedOnAnimation();
			else
			{
				//Update the distance
				if(!params.reached_max_distance){
					this.setCollideWithout(Arrays.asList(ObjectType.BLOC,ObjectType.FLECHE,ObjectType.HEROS));
					params.reached_max_distance=true;
					params.last_add_time= PartieTimer.me.getElapsedNano();
					params.current_distance=dist; 
				}
				else
				{
					//The barrage is built but is not shot yet: synchronzize the arrows's position with the hero (computeBarragePos), update the pos, the transform and the hitbox
					int[] xypos = new int[2];
					for(int i=0; i<params.barrageArrows.size();i++)
					{
						Fleche_barrage fb = params.barrageArrows.get(i);
						xypos = computeBarragePos(fb.getRotation());
						fb.setXpos_sync(xypos[0]);
						fb.setYpos_sync(xypos[1]);
					}
				}
				addNewArrows();
				this.params.lastFrameUpdate = ModelPartie.me.getFrame();
				return updateMouvementBasedOnAnimation();
			}
		}
		else
		{
			return false;
		}

	}
	@Override
	protected AffineTransform _computeDrawTr(boolean screenReferential,Point screendisp)
	{
		final Point pos;
		final Point anchor;
		
		if(encochee)
			return super._computeDrawTr(screenReferential, screendisp);

		pos = getPos();
		anchor = null;

		if(screenReferential)
		{
			final AffineTransform res = new AffineTransform();
			res.translate(screendisp.x, screendisp.y);
			res.concatenate(AbstractModelPartie.getRotatedTransform(pos,anchor, getRotation(),getScaling()));
			return res;
		}
		else
			return AbstractModelPartie.getRotatedTransform(pos,anchor, getRotation(),getScaling());
	}
	@Override
	protected Hitbox computeRotatedHitbox(Point screenDisp,Mouvement dep, int mouv_index)
	{
		//Override this function since the rotation has to be done around (0,0). The correct position was already computed by computeBarragePos
		if(!encochee && !params.shoot_arrows)
		{
			AffineTransform tr =  new AffineTransform();
			tr.translate(getXpos(), getYpos());
			tr.rotate(getRotation());
			return dep.getScaledHitboxCopy(mouv_index,new Vector2d(1,1)).transformHitbox(tr, getPos(), new Point());
		}
		else
			return super.computeRotatedHitbox(screenDisp,dep, mouv_index);
	}
}
