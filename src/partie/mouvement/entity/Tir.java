package partie.mouvement.entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.mouvement.Animation;
import partie.mouvement.Mouvement;
import partie.mouvement.Mouvement.DirSubTypeMouv;
import utils.Vitesse;

public class Tir extends Mouvement_entity
{
	public Tir(ObjectType objType, SubTypeMouv _sub_type_mouv,int current_frame) 
    {
		super();
		type_mouv=EntityTypeMouv.TIR;
		sub_type_mouv=_sub_type_mouv;
		this.objType=objType;
		if(objType.equals(ObjectType.HEROS))
		{
			xtaille =  Arrays.asList(50,63,75,75,63,50,57,75,75,57);
			ytaille =  Arrays.asList(105,97,86,86,97,105,112,101,101,112);
			
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			//List<Integer> xg = Arrays.asList(11,11,26,28,32,18,25,16,38,11);
			//List<Integer> xd = Arrays.asList(31,31,46,48,52,38,45,36,58,31);
			
			List<Integer> xg = Arrays.asList(5,5,20,28,32,18,25,16,30,5);
			List<Integer> xd = Arrays.asList(31,31,46,54,58,46,51,42,56,31);
			List<Integer> yh = Arrays.asList(19,11,0 ,0 ,11,19,27,15,15,27);
			List<Integer> yb = Arrays.asList(102,94,83,83,94,102,110,98,98,110);

			//Used to set the position of the anchor for drawing the heros when he is in Tir animation
			x_rot_pos =  Arrays.asList(23,12,38,40,43,27,40,32,45,19); //23,12,38,40,43,27,40,32,45,19
			y_rot_pos =  Arrays.asList(47,44,30,30,44,47,58,45,45,58); //47,44,30,30,44,47,58,45,45,58

			//Used to determined animation 
			for(int i=0;i<xg.size();++i)
			{
				// ASSUME THAT ALL TIR HITBOXES ARE THE SAME
				x_center_tir.add(xg.get(i) + (xd.get(i)-xg.get(i))/2);//middle of x hitbox 
				y_center_tir.add(yh.get(i)+ (yb.get(i)+yh.get(i))/5);//arm height

			}
			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			animation.start(Arrays.asList(2), current_frame, 0, 1);

		}
		else if(objType.equals(ObjectType.SPIREL))
		{
			xtaille =  Arrays.asList(56,56);
			ytaille =  Arrays.asList(75,75);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0,0);
			List<Integer> xd = Arrays.asList(56,56);
			List<Integer> yh = Arrays.asList(0,0);
			List<Integer> yb = Arrays.asList(74,74);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			int start_index =sub_type_mouv.equals(DirSubTypeMouv.GAUCHE) ? 0 : 1;
			int end_index =sub_type_mouv.equals(DirSubTypeMouv.GAUCHE) ? 1 : 2;
			//animation frame, current_frame, start_index, end_index
			animation.start(Arrays.asList(20,20), current_frame, start_index, end_index);

		}
	
	}
	public Tir(ObjectType objType, SubTypeMouv _sub_type_mouv, int current_frame,Animation _animation){
		this(objType,_sub_type_mouv,current_frame);
		animation = _animation;
	}
	
	/*@Override
	public int getMaxBoundingSquare(Object obj)
	{
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
			return 112;
		else
			return 0;
	}*/
	
	public Mouvement Copy() {
		return new Tir(objType,sub_type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int mouv_index) {
		if(ObjectTypeHelper.isTypeOf(object, ObjectType.HEROS))
		{
			return null;//nothing
		}
		else if(ObjectTypeHelper.isTypeOf(object, ObjectType.SPIREL))
			return null;
		
		return null;
	}
	@Override
	public DirSubTypeMouv droite_gauche(int mouv_index,double rotation) {
		if(objType.equals(ObjectType.HEROS))
			if(mouv_index>=3 && mouv_index <= 7 )
				return (DirSubTypeMouv.GAUCHE);
			else 
				return(DirSubTypeMouv.DROITE);
		else if(objType.equals(ObjectType.SPIREL))
			if(mouv_index<1)
				return (DirSubTypeMouv.GAUCHE);
			else 
				return(DirSubTypeMouv.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return DirSubTypeMouv.GAUCHE;
		}
	}
	/*@Override
	public int updateAnimation(int mouv_index,int current_frame,double speedFactor) {
		return animation.update(0,current_frame,speedFactor);
	}*/
	@Override
	public boolean isInterruptible(int currentAnim) {
		if(objType.equals(ObjectType.HEROS))
			return true;
		else if(objType.equals(ObjectType.SPIREL))
			return animEndedOnce(); //spirel can't do anything when charging a shot
		return true;
	}
}
