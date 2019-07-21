package partie.projectile.fleches.sprirituelle;

import java.awt.Point;
import java.util.List;

import javax.vecmath.Vector2d;

import music.MusicBruitage;
import partie.collision.Collidable;
import partie.collision.Collision;
import partie.collision.GJK_EPA;
import partie.collision.Hitbox;
import partie.deplacement.Deplace;
import partie.effects.Effect;
import partie.effects.Ombre_effect;
import partie.effects.Roche_effect;
import partie.entitie.Entity;
import partie.entitie.heros.Heros;
import partie.modelPartie.AbstractModelPartie;
import partie.projectile.Projectile;

public class Fleche_ombre extends Spirituelle {
	
	// WARNING : effect moves with 
	//	-colliding entity        			NO 
	//  -colliding ground (ie roche_effect) YES
	
	public Fleche_ombre(List<Projectile> tabFleche, int current_frame,Heros _shooter,boolean add_to_list,float damageMult,float speedFactor) {
		super(tabFleche, current_frame,_shooter,add_to_list,damageMult,speedFactor);
		TEMPS_DESTRUCTION= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		damage=0*damageMult;
		seyeri_cost=-30;
	}

	/**Switch the two position of the objects following this rule: try switch by mathing bottom first and top last */
	void teleportSwitch(Collidable object1,Collidable object2,AbstractModelPartie partie,Vector2d normal)
	{
		//Get hitbox from object 1
		Hitbox object1Hitbox = object1.getHitbox(partie.INIT_RECT,partie.getScreenDisp());

		//Get points to match 
		Vector2d v1 = Hitbox.supportPoint(new Vector2d(1,1), object1Hitbox.polygon);
		Vector2d v2 = Hitbox.supportPoint(new Vector2d(-1,1), object1Hitbox.polygon);

		/*Direction of normal first, opposite after*/
		double x1 = normal.x>0 ? v1.x :  v2.x;
		double x2 = normal.x>0 ? v2.x :  v1.x;

		/*bottom first, top last*/
		double y1 = Hitbox.supportPoint(new Vector2d(1,-1), object1Hitbox.polygon).y;
		double y2 = v1.y;

		//Get hitbox from object 2
		Hitbox object2Hitbox = object2.getHitbox(partie.INIT_RECT,partie.getScreenDisp());

		//Get points to match 
		Vector2d v1_2 = Hitbox.supportPoint(new Vector2d(1,1), object2Hitbox.polygon);
		Vector2d v2_2 = Hitbox.supportPoint(new Vector2d(-1,1), object2Hitbox.polygon);

		/*Direction of normal first, opposite after*/
		double x1_2 = normal.x>0 ? v1_2.x :  v2_2.x;
		double x2_2 = normal.x>0 ? v2_2.x :  v1_2.x;

		/*bottom first, top last*/
		double y1_2 = Hitbox.supportPoint(new Vector2d(1,-1), object2Hitbox.polygon).y;
		double y2_2 = v1_2.y;

		//Compute all possible matches 
		Vector2d[] allDeltaPos = {new Vector2d(x1_2-x1,y1_2-y1),new Vector2d(x2_2-x2,y1_2-y1),new Vector2d(x1_2-x1,y2_2-y2),new Vector2d(x2_2-x2,y2_2-y2)} ;
		boolean object1TP =false;
		boolean object2TP = false;
		for(int i=0; i< allDeltaPos.length; ++i)
		{
			if(!object1TP){
				//teleport the object
				object1.addXpos_sync((int) allDeltaPos[i].x);
				object1.addYpos_sync((int) allDeltaPos[i].y);
				//test stuck
				if(Collision.isWorldCollision(partie, object1, true)){
					//revert position
					object1.addXpos_sync((int) -allDeltaPos[i].x);
					object1.addYpos_sync((int) -allDeltaPos[i].y);
				}
				else
					object1TP=true;
			}
			if(!object2TP){
				//teleport the object
				object2.addXpos_sync((int) -allDeltaPos[i].x);
				object2.addYpos_sync((int) -allDeltaPos[i].y);
				//test stuck
				if(Collision.isWorldCollision(partie, object2, true)){
					//revert position
					object2.addXpos_sync((int) allDeltaPos[i].x);
					object2.addYpos_sync((int) allDeltaPos[i].y);
				}
				else
					object2TP=true;
			}
			if(object1TP && object2TP)
				break;
		}
		//correct screen : 
		if(object1.controlScreenMotion)
		{
			Point delta = Deplace.getdeplaceEcran(partie,object1,true);
			Deplace.deplaceEcran(delta,partie,object1);
		}
		if(object2.controlScreenMotion)
		{
			Point delta = Deplace.getdeplaceEcran(partie,object2,true);
			Deplace.deplaceEcran(delta,partie,object2);
		}
	}

	void teleportToArrow(Collidable object,AbstractModelPartie partie,Vector2d normal)
	{
		//get the teleportation point 
		Hitbox fHitbox = getHitbox(partie.INIT_RECT,partie.getScreenDisp());

		Vector2d v1 = Hitbox.supportPoint(Deplace.angleToVector(getRotation()-Math.PI/10), fHitbox.polygon); //top right of unrotated hitbox (with tip pointing right)
		Vector2d v2 = Hitbox.supportPoint(Deplace.angleToVector(getRotation()+Math.PI/10), fHitbox.polygon); //bottom right of unrotated hitbox (with tip pointing right)

		Vector2d teleportationPoint = new Vector2d(((v1.x+v2.x)/2),((v1.y+v2.y)/2));


		/*Point that will be mapped to the teleportation point */
		Vector2d dir1 = GJK_EPA.projectVectorTo90(normal,true,0.01);
		Vector2d dir2 = GJK_EPA.projectVectorTo90(normal,true,-0.01);

		Hitbox objectHitbox= object.getHitbox(partie.INIT_RECT, partie.getScreenDisp());

		/**Condider 3 reference points for the object: imagine the case where the arrow is shot in the ground. The three possible reference are : 
		 * bottom middle, bottom left and bottom right */
		Vector2d objectReferencePoint2 = Hitbox.supportPoint(dir1, objectHitbox.polygon);//dir + right rotation (top => top right, right => right bottom...)
		Vector2d objectReferencePoint3 = Hitbox.supportPoint(dir2, objectHitbox.polygon);//dir + left rotation
		Vector2d objectReferencePointMiddle = new Vector2d((objectReferencePoint2.x+objectReferencePoint3.x)/2 , (objectReferencePoint2.y+objectReferencePoint3.y)/2);

		Vector2d deltaPos1 = new Vector2d(teleportationPoint.x-objectReferencePointMiddle.x,teleportationPoint.y-objectReferencePointMiddle.y);
		Vector2d deltaPos2 = new Vector2d(teleportationPoint.x-objectReferencePoint2.x,teleportationPoint.y-objectReferencePoint2.y);
		Vector2d deltaPos3 = new Vector2d(teleportationPoint.x-objectReferencePoint3.x,teleportationPoint.y-objectReferencePoint3.y);

		Vector2d[] allDeltaPos = {deltaPos1,deltaPos2,deltaPos3} ;

		boolean tpSuccess = false;
		int i=0;
		for(i=0; i< 3; ++i)
		{
			//teleport the object
			object.addXpos_sync((int) allDeltaPos[i].x);
			object.addYpos_sync((int) allDeltaPos[i].y);
			//test stuck
			if(Collision.isWorldCollision(partie, object, true)){
				//revert position
				object.addXpos_sync((int) -allDeltaPos[i].x);
				object.addYpos_sync((int) -allDeltaPos[i].y);
			}
			//teleportation succeed : move screen if needed and exit loop
			else
			{
				if(object.controlScreenMotion)
				{
					Point delta = Deplace.getdeplaceEcran(partie,object,true);
					Deplace.deplaceEcran(delta,partie,object);
				}
				tpSuccess=true;
				break;
			}
		}
	}
	void applyArrowEffect(List<Entity> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d collisionNormal,Point pColli, Point correctedPColli)
	{
		if(generatedEffect)
			return;

		generatedEffect=true;

		flecheEffect=new Ombre_effect(partie,this,0,partie.getFrame(),collisionNormal,pColli,correctedPColli);
		MusicBruitage.me.startBruitage("arc");

		if(collidedObject == null|| !(collidedObject instanceof Entity))
			teleportToArrow(shooter,partie,collisionNormal);
		else
			teleportSwitch(shooter,collidedObject,partie,collisionNormal);
		for(Entity obj : objects)
		{
			obj.registerEffect(flecheEffect);
		}

		Roche_effect.synchroniseMovementWithRocheEffectMovement(collidedObject, new Collidable[] {this,flecheEffect});

		this.doitDeplace=false;
		this.setCollideWithNone();
	}
	@Override
	protected void onPlanted(List<Entity> objects,AbstractModelPartie partie,Collidable collidedObject,Vector2d unprojectedSpeed,boolean stuck)
	{
		if(this.afterDecochee && stuck)
			ejectArrow(partie,unprojectedSpeed);
		if(stuck)
			destroy(partie,false);
		else
			applyArrowEffect(objects,partie,collidedObject,normCollision,pointCollision,correctedPointCollision);
		this.isVisible=false;
	}
	@Override
	protected boolean OnObjectsCollision(List<Entity> objects,AbstractModelPartie partie,Collidable collider,Vector2d unprojectedSpeed,Vector2d normal)
	{
		if(this.afterDecochee && (collider instanceof Effect))
			if(((Effect)collider).isWorldCollider)
				ejectArrow(partie,unprojectedSpeed);
		
		this.isVisible=false;
		this.doitDeplace=false;
		this.setCollideWithNone();
		if(collider instanceof Entity){
			applyArrowEffect(objects,partie,collider,normal,null,null);
			return false;
		}
		return true;
	}
}