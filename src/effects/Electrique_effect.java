package effects;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import collision.Collision;
import conditions.Condition;
import fleches.Fleche;
import partie.AbstractModelPartie;
import personnage.Heros;
import types.Entitie;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Electrique_effect extends Effect{

	boolean type0 ;
	double DUREE_PARALYSIE= 2;
	
	int numberExplosion = 0;
	int lifeGained = 10;
	int damage = -5;
	
	Collidable previousCollider = null;
	public Electrique_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,int typeEffect,int _numberExplosion)
	{
		this(partie,_ref_fleche,_anim, current_frame,_normalCollision,_pointCollision,
				_correctedPointCollision,typeEffect,_numberExplosion,null);
	}
	public Electrique_effect(AbstractModelPartie partie,Fleche _ref_fleche,int _anim, int current_frame,Vector2d _normalCollision,Point _pointCollision,
			Point _correctedPointCollision,int typeEffect,int _numberExplosion,Collidable prevCollider)
	{
		super.init(_anim,_ref_fleche,_normalCollision,_pointCollision,_correctedPointCollision,typeEffect,typeEffect == 0);
		type0 = typeEffect==0;
		TEMPS_DESTRUCTION = 3*(long) Math.pow(10, 8);//nanos, 0.3sec 
		isProjectile =true; //to allow for collision with other projectile and entities
		this.setCollideWithout(Arrays.asList(TypeObject.ELECTRIQUE_EFF,TypeObject.FLECHE));
		numberExplosion=_numberExplosion;
		previousCollider=prevCollider;
		if(type0){
			xtaille =  Arrays.asList(200,200,200,200,200,200);
			ytaille =  Arrays.asList(200,200,200,200,200,200);
			hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(43 ,0  ,0  ,0  ,0  ,0  ),
					Arrays.asList(145,72 ,17 ,0  ,0  ,0  ),
					Arrays.asList(159,175,200,200,200,200),
					Arrays.asList(200,200,200,200,200,200));
		}
		else{
			xtaille =  Arrays.asList(120,120,120,120);
			ytaille =  Arrays.asList(60,60,60,60);
			hitbox= Hitbox.createSquareHitboxes(
					Arrays.asList(60 ,0,0,0),
					Arrays.asList(0,0,0,0),
					Arrays.asList(120,120,120,120),
					Arrays.asList(60,60,60,60));
		}

		//(new Float(0.0)).equals(new Float(-0.0))
		int start_index =0;
		int end_index = type0? 6 : 4;
		List<Integer> animTimes= type0? Arrays.asList(6,12,18,24,30,36):Arrays.asList(4,8,12,16);
		animation.start(animTimes, current_frame, start_index, end_index);
		if(type0){
			animation.setLoop(true);
			maxnumberloops = 3;
		}
		else
		{
			animation.setLoop(true);
			maxnumberloops = -1;
		}
		partie.arrowsEffects.add(this);
		if(!type0)
			rotation = Math.random() * 2* Math.PI;
		setFirstPos(partie);
		this.onUpdate(partie, false); //update rotated hitbox and drawtr
		
		setSpeed();
	}

	@Override
	public int getMaxBoundingSquare()
	{
		if(type0)
			return 200;
		else
			return 120;
	}
	private void setSpeed()
	{
		if(type0)
			return;
		int speedNorm = 30;
		Vitesse vit = convertSpeed(speedNorm,rotation);
		localVit.x=(vit.x);
		localVit.y=(vit.y);
	}
	@Override
	public Vitesse getModifiedVitesse(AbstractModelPartie partie,
			Collidable obj) {
		return new Vitesse();
	}
	@Override
	public void updateOnCollidable(AbstractModelPartie partie,Entitie attacher)
	{
		if(type0)
			if(Collision.testcollisionObjects(partie, this, attacher,true))
				attacher.conditions.addNewCondition(Condition.PARALYSIE, DUREE_PARALYSIE,System.identityHashCode(this));
	}

	private void setFirstPos(AbstractModelPartie partie) {

		//get the middle bottom of the effect
		int xdivider = type0? 2:1;

		int x_eff_center = (int) (xtaille.get(anim)/xdivider * Math.cos(rotation) - (ytaille.get(anim)) * Math.sin(rotation));
		int y_eff_center = (int) (xtaille.get(anim)/xdivider * Math.sin(rotation) + (ytaille.get(anim)) * Math.cos(rotation));

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
			return super.computeTransformDrawRotated(partie);
		else
			return super.computeTransformDrawRotated(partie);
	}
	
	@Override
	public void handleWorldCollision(Vector2d normal,AbstractModelPartie partie,Collidable collidedObject,boolean stuck)
	{
		if(type0)
			return;
		boolean collision_gauche = normal.x>0;
		boolean collision_droite = normal.x<0;
		//boolean collision_haut = normal.y>0;
		//boolean collision_bas = normal.y<0;	
		last_colli_left=collision_gauche;
		last_colli_right=collision_droite;
		localVit= new Vitesse(0,0);
		this.setCollideWithNone();
		this.destroy(partie, stuck);
	}
	
	@Override
	public void handleObjectCollision(AbstractModelPartie partie,Collidable collider,Vector2d normal)
	{
		if(type0)
			return;
		if(collider == previousCollider)
			return;
		//If collide with the heros, gain seyeri
		if(collider instanceof Heros)
			((Heros)collider).addLife(lifeGained);
		//else exploded in other electrique effect 
		else 
		{
			for(int i=0; i<numberExplosion;++i)
			{
				new Electrique_effect(partie,ref_fleche,0,partie.getFrame(),normal,null,null,1,numberExplosion-1,collider);
			}
			if(collider instanceof Entitie)
				((Entitie)collider).addLife(damage);
		}
		destroy(partie,true);
	}
	@Override
	public void handleStuck(AbstractModelPartie partie) {
		if(type0)
			return;
		handleWorldCollision( new Vector2d(), partie,null,true );
	}
}
