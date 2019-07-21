package partie.deplacement.entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Animation;
import partie.deplacement.Mouvement;
import utils.Vitesse;

//il y a 1 animations de deux cotés 

public class Attente extends Mouvement_entity{

	//constructeur des monstres 
	public Attente(ObjectType objType,SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv=MouvEntityEnum.ATTENTE;
		sub_type_mouv=_sub_type_mouv;
		this.objType=objType;
		if(objType.equals(ObjectType.HEROS))
		{
			xtaille =  Arrays.asList(85,84,85,84);
			ytaille =  Arrays.asList(100,100,100,100);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(40,40,13,12);
			List<Integer> xd = Arrays.asList(71,71,44,43);
			List<Integer> yh = Arrays.asList(13,13,13,13);
			List<Integer> yb = Arrays.asList(99,99,99,99);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int start_index =sub_type_mouv.equals(DirSubTypeMouv.GAUCHE) ? 0 : 2;
			int end_index =sub_type_mouv.equals(DirSubTypeMouv.GAUCHE) ? 2 : 4;
			animation.start(Arrays.asList(80,160,80,160), current_frame, start_index, end_index);
		}
		else if(objType.equals(ObjectType.SPIREL))
		{
			xtaille =  Arrays.asList(56,56,-1,-1,-1,-1,-1,-1);
			ytaille =  Arrays.asList(75,75,-1,-1,-1,-1,-1,-1);

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

	public Attente(ObjectType objType,SubTypeMouv _sub_type_mouv, int current_frame,Animation _animation){
		this(objType,_sub_type_mouv,current_frame);
		animation = _animation;
	}
	
	/*@Override
	public int getMaxBoundingSquare(Object obj)
	{
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
			return 100;
		else if(TypeObject.isTypeOf(obj, TypeObject.SPIREL))
			return 75;
		else
			return 0;
	}*/
	public Mouvement Copy() {
		return new Attente(objType,sub_type_mouv,animation.getStartFrame(),animation);
	}
	
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		if(ObjectTypeHelper.isTypeOf(object, ObjectType.HEROS))
			return new Vitesse(0,0);
		
		else if(ObjectTypeHelper.isTypeOf(object, ObjectType.SPIREL))
			return new Vitesse(0,0);
		return null;
	}

	@Override
	public DirSubTypeMouv droite_gauche(int anim,double rotation) {
		if(objType.equals(ObjectType.HEROS))
			if(anim<2)
				return (DirSubTypeMouv.GAUCHE);
			else 
				return(DirSubTypeMouv.DROITE);
		else if(objType.equals(ObjectType.SPIREL))
			if(anim<1)
				return (DirSubTypeMouv.GAUCHE);
			else 
				return(DirSubTypeMouv.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return DirSubTypeMouv.GAUCHE;
		}
	}

}
