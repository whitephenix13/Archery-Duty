package effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import collision.GJK_EPA;
import conditions.Condition;
import deplacement.Deplace;
import fleches.Fleche;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Bloc;
import types.Entitie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Glace_effect extends Effect{
	boolean type0 ;
	double DUREE_LENTEUR=3;
	double damage = 0;
	double DUREE_EJECT = -1;
	int movement_speed = 5;
	double eject_vit_norm = 20;
	
	public Glace_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,int typeEffect, int _damage)
	{
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,typeEffect,typeEffect == 0);
		type0 = typeEffect==0;
		damage = _damage;
		isProjectile=true;
		this.setCollideWithout(Arrays.asList(TypeObject.ELECTRIQUE_EFF,TypeObject.GLACE_EFF,TypeObject.FLECHE));

		if(type0){
			xtaille =  Arrays.asList(90,90,90,90,90);
			ytaille =  Arrays.asList(72,72,72,72,72);
			hitbox = Hitbox.createQuadriHitboxes(
					Arrays.asList(new Point(10,70),new Point(10,70),new Point(2,62),new Point(36,56),new Point(38,42)),
					Arrays.asList(new Point(40,0),new Point(40,0),new Point(44,1),new Point(7,3),new Point(4,5)),
					Arrays.asList(new Point(54,0),new Point(54,0),new Point(89,25),new Point(89,6),new Point(89,5)),
					Arrays.asList(new Point(80,70),new Point(80,70),new Point(89,55),new Point(89,33),new Point(89,29))
					);
			/*hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(15 ,15  ,15  ,15 ,15 ),
					Arrays.asList(0 ,0  ,0  ,0 ,0 ),
					Arrays.asList(75,75,75,75,75),
					Arrays.asList(72,72,70,57,43));*/
		}
		else{
			xtaille =  Arrays.asList(100,100,100,100);
			ytaille =  Arrays.asList(100,100,100,100);
			hitbox= Hitbox.createSquareHitboxes(0,0,100,100,4);
		}


		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index = type0? 1 : 4; //5:4
		List<Integer> animTimes= type0? Arrays.asList(6,12,18,24,30):Arrays.asList(4,8,12,16);
		if(!type0){
			maxnumberloops = 1;
		}
		else{
			maxnumberloops = -1;
		}
		animation.start(animTimes, current_frame, start_index, end_index);

		partie.arrowsEffects.add(this);
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
		
		setSpeed();
	}
	private void setSpeed()
	{
		if(!type0)
			return;
		Vitesse vit = convertSpeed(movement_speed,rotation-Math.PI/2);
		localVit.x=(vit.x);
		localVit.y=(vit.y);
	}
	
	private void setDestroy(AbstractModelPartie partie)
	{
		if(!type0)
			return;
		List<Integer> animTimes= Arrays.asList(6,12,18,24,30);
		int start_index =1;
		int end_index =5;
		maxnumberloops = 1;
		animation.restart(animTimes, partie.getFrame(), start_index, end_index);
	}
	
	@Override
	public int getMaxBoundingSquare()
	{
		if(type0)
			return 90;
		else
			return 100;
	}

	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(!type0)
			if(Collision.testcollisionObjects(partie, this, attacher,true))
				attacher.conditions.addNewCondition(Condition.LENTEUR, DUREE_LENTEUR,System.identityHashCode(this));
	}

	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}

	public void setFirstPos(AbstractModelPartie partie) {
		//get the middle bottom of the effect
		int ydivider = type0? 1:2;

		int x_eff_center = (int) (xtaille.get(anim)/2 * Math.cos(rotation) - (ytaille.get(anim)/ydivider) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/2 * Math.sin(rotation) + (ytaille.get(anim)/ydivider) * Math.cos(rotation));

		Point firstPos = new Point();
		if(type0)
			firstPos = super.setFirstPos(partie,new Point(x_eff_center,y_eff_center));
		else{
			//get the tip of the arrow
			Point arrowTip = super.getArrowTip(partie);

			firstPos=new Point(arrowTip.x-x_eff_center,arrowTip.y-y_eff_center);

		}
		xpos_sync(firstPos.x);
		ypos_sync(firstPos.y);
	}
	@Override
	public AffineTransform computeTransformDraw(AbstractModelPartie partie) {
		if(!type0)
			return super.computeTransformDraw(partie);
		else
			return super.computeTransformDrawRotated(partie);
	}

	@Override
	public void handleWorldCollision(Vector2d normal,AbstractModelPartie partie,Collidable collidedObject,boolean stuck)
	{
		if(!type0)
			return;
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		//boolean collision_bas = normal.y<0;	
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		localVit= new Vitesse(0,0);
		this.setCollideWithNone();
		setDestroy(partie);
	}
	
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal)
	{
		if(!type0)
			return;
		//If collide with the heros, gain seyeri
		if(collider instanceof Entitie){
			//get middle of collider
			Vector2d colliderMid = Hitbox.getObjMid(partie, collider);
			//get middle of effect 
			Vector2d effectMid = Hitbox.getObjMid(partie, this);
			// get rotation based on that previous value 
			double deltaX= (colliderMid.x - effectMid.x);
			double deltaY= (colliderMid.y - effectMid.y);

			double distance = Math.sqrt(deltaX*deltaX+deltaY*deltaY);
			Vitesse init_vit = new Vitesse(deltaX*eject_vit_norm/distance,deltaY*eject_vit_norm/distance);
			Entitie ent = (Entitie)collider;
			ent.conditions.addNewCondition(Condition.MOTION, DUREE_EJECT,init_vit,System.identityHashCode(this));
			ent.addLife(damage);
			localVit= new Vitesse(0,0);
			this.setCollideWithNone();
			setDestroy(partie);
		}
	}
	@Override
	public void handleStuck(AbstractModelPartie partie) {
		if(!type0)
			return;
		handleWorldCollision( new Vector2d(), partie,null,true );
	}


}
