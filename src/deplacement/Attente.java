package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

//il y a 1 animations de deux cotés 

public class Attente extends Mouvement_perso{

	public enum TypeAttente implements TypeMouv {AttenteGauche,AttenteDroite };

	//constructeur des monstres 
	public Attente(Object obj,TypeMouv _type_mouv, int current_frame){
		super();
		type_mouv=_type_mouv;
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
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
			int start_index =type_mouv.equals(TypeAttente.AttenteGauche) ? 0 : 2;
			int end_index =type_mouv.equals(TypeAttente.AttenteGauche) ? 2 : 4;
			animation.start(Arrays.asList(80,160,80,160), current_frame, start_index, end_index);
		}
		else if(TypeObject.isTypeOf(obj, TypeObject.SPIREL))
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
			int start_index =type_mouv.equals(TypeAttente.AttenteGauche) ? 0 : 1;
			int end_index =type_mouv.equals(TypeAttente.AttenteGauche) ? 1 : 2;
			//animation frame, current_frame, start_index, end_index
			animation.start(Arrays.asList(20,20), current_frame, start_index, end_index);

		}
	}

	public Attente(Object obj,TypeMouv _type_mouv, int current_frame,Animation _animation){
		this(obj,_type_mouv,current_frame);
		animation = _animation;
	}
	
	@Override
	public int getMaxBoundingSquare(Object obj)
	{
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
			return 100;
		else if(TypeObject.isTypeOf(obj, TypeObject.SPIREL))
			return 75;
		else
			return 0;
	}
	public Mouvement Copy(Object obj) {
		return new Attente(obj,type_mouv,animation.getStartFrame(),animation);
	}
	
	@Override
	public Vitesse getSpeed(Collidable object, int anim) {
		if(TypeObject.isTypeOf(object, TypeObject.HEROS))
			return new Vitesse(0,0);
		
		else if(TypeObject.isTypeOf(object, TypeObject.SPIREL))
			return new Vitesse(0,0);
		return null;
	}

	@Override
	public String droite_gauche(Object obj,int anim) {
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
			if(anim<2)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else if(TypeObject.isTypeOf(obj, TypeObject.SPIREL))
			if(anim<1)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}

}
