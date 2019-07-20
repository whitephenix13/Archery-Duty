package partie.deplacement.entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//il y a 4 animations de deux cotés 
import java.util.List;

import gameConfig.ObjectTypeHelper;
import gameConfig.ObjectTypeHelper.ObjectType;
import option.Config;
import partie.collision.Collidable;
import partie.collision.Hitbox;
import partie.deplacement.Animation;
import partie.deplacement.Mouvement;
import utils.Vitesse;

public class Marche extends Mouvement_entity{
	
	//REMOVE public enum TypeMarche implements TypeMouv {MarcheGauche,MarcheDroite };

	//constructeur 
	public Marche(ObjectType objType,SubTypeMouv _sub_type_mouv,int current_frame){
		super();
		type_mouv=MouvEntityEnum.MARCHE;
		sub_type_mouv=_sub_type_mouv;
		this.objType=objType;
		
		if(objType.equals(ObjectType.HEROS))
		{

			xtaille =  Arrays.asList(75,75,75,75,75,75,75,75);
			ytaille =  Arrays.asList(101,100,98,100,101,100,98,100);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(27,14,37,38,26,39,16,15);
			List<Integer> xd = Arrays.asList(48,35,58,59,47,60,37,36);
			List<Integer> yh = Arrays.asList(13,13,11,13,13,13,11,13); // Arrays.asList(10,9,7,9,10,9,7,9);
			List<Integer> yb = Arrays.asList(99,99,97,99,99,99,97,99);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int start_index =sub_type_mouv.equals(SubMouvEntityEnum.GAUCHE) ? 0 : 4;
			int end_index =sub_type_mouv.equals(SubMouvEntityEnum.GAUCHE)? 4 : 8;
			animation.start(Arrays.asList(10,20,30,40,10,20,30,40), current_frame, start_index, end_index);

		}
		else if(objType.equals(ObjectType.SPIREL))
		{
			xtaille =  Arrays.asList(56,56,56,56,-1,-1,-1,-1);
			ytaille =  Arrays.asList(75,75,75,75,-1,-1,-1,-1);
		
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(0 ,0 ,0 ,0 ,-1,-1,-1,-1);
			List<Integer> xd = Arrays.asList(56,56,56,56,-1,-1,-1,-1);
			List<Integer> yh = Arrays.asList(0 ,0 ,0 ,0 ,-1,-1,-1,-1);
			List<Integer> yb = Arrays.asList(74,74,74,74,-1,-1,-1,-1);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			
			//animation frame, current_frame, start_index, end_index
			int start_index =sub_type_mouv.equals(SubMouvEntityEnum.GAUCHE) ? 0 : 2;
			int end_index =sub_type_mouv.equals(SubMouvEntityEnum.GAUCHE)? 2 : 4;
			animation.start(Arrays.asList(5,10,5,10), current_frame, start_index, end_index);
		}
	}

	public Marche(ObjectType objType,SubTypeMouv _sub_type_mouv, int current_frame,Animation _animation){
		this(objType,_sub_type_mouv,current_frame);
		animation = _animation;
	}
	/*@Override
	public int getMaxBoundingSquare(Object obj)
	{
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
			return 101;
		else if(TypeObject.isTypeOf(obj, TypeObject.SPIREL))
			return 75;
		else
			return 0;
	}*/
	public Mouvement Copy() {
		return new Marche(objType,sub_type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		if(ObjectTypeHelper.isTypeOf(object, ObjectType.HEROS))
		{
			int speed_norm = (int)(3.0 / Config.ratio_fps());

			assert (anim>=0 && anim <8);
			return new Vitesse((speed_norm * ((anim<4)? -1 : 1 )),object.localVit.y);
		}
		else if(ObjectTypeHelper.isTypeOf(object, ObjectType.SPIREL))
		{
			int speed_norm = (int)(3.0 / Config.ratio_fps());
			if(anim<2)
				return new Vitesse(-1*speed_norm,object.localVit.y);
			
			else
				return new Vitesse(speed_norm,object.localVit.y);
		}
		return null;
	}
	@Override
	public String droite_gauche(Object obj,int anim) {
		if(ObjectTypeHelper.isTypeOf(obj, ObjectType.HEROS))
			if(anim<4)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else if(ObjectTypeHelper.isTypeOf(obj, ObjectType.SPIREL))
			if(anim<2)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}
}
