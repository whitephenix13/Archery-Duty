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

public class Accroche extends Mouvement_entity{
	public static enum SubMouvAccrocheEnum implements SubTypeMouv {ACCROCHE_DROITE, ACCROCHE_GAUCHE,GRIMPE_DROITE,GRIMPE_GAUCHE};
	
	public Accroche(ObjectType objType,SubTypeMouv _sub_type_mouv, int current_frame){
		super();
		type_mouv=MouvEntityEnum.ACCROCHE;
		sub_type_mouv=_sub_type_mouv;
		this.objType=objType;
		if(objType.equals(ObjectType.HEROS))
		{
			xtaille =  Arrays.asList(83,44,83,44);
			ytaille =  Arrays.asList(99,82,99,82);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(42,4 ,11,13); //Arrays.asList(42,4 ,11,4);
			List<Integer> xd = Arrays.asList(71,28,40,39);
			List<Integer> yh = Arrays.asList(12,4,12,4); //  Arrays.asList(12,4,12,13)
			List<Integer> yb = Arrays.asList(94,67,94,67);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int start_index=0;int end_index=0;
			if(sub_type_mouv.equals(SubMouvAccrocheEnum.ACCROCHE_GAUCHE)){
				start_index= 0; end_index = 1;}
			else if(sub_type_mouv.equals(SubMouvAccrocheEnum.GRIMPE_GAUCHE)){
				start_index= 1; end_index = 2;}
			else if(sub_type_mouv.equals(SubMouvAccrocheEnum.ACCROCHE_DROITE)){
				start_index= 2; end_index = 3;}
			else if(sub_type_mouv.equals(SubMouvAccrocheEnum.GRIMPE_DROITE)){
				start_index= 3; end_index = 4;}
			
			animation.start(Arrays.asList(10,4,10,4), current_frame, start_index, end_index);
		}
	}
	public Accroche(ObjectType objType,SubTypeMouv _sub_type_mouv, int current_frame,Animation _animation){
		this(objType,_sub_type_mouv,current_frame);
		animation = _animation;
	}
	
	/*@Override
	public int getMaxBoundingSquare(Object obj)
	{
		return 100;
	}*/

	public Mouvement Copy() {
		return new Accroche(objType,sub_type_mouv,animation.getStartFrame(),animation);
	}
	
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		if(ObjectTypeHelper.isTypeOf(object, ObjectType.HEROS))
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
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return DirSubTypeMouv.GAUCHE;
		}
	}

}
