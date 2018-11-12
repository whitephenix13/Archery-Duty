package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//il y a 3 animations de deux cotés 
import java.util.List;

import collision.Collidable;
import monstre.Spirel;
import option.Config;
import personnage.Heros;
import types.Hitbox;
import types.TypeObject;
import types.Vitesse;

public class Saut extends Mouvement_perso{

	public enum TypeSaut implements TypeMouv {JumpGauche,FallGauche,LandGauche,JumpDroite,FallDroite,LandDroite };


	//constructeur
	public Saut(Object obj,TypeMouv _type_mouv,int current_frame) {
		super();
		type_mouv=_type_mouv;
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
		{
			xtaille =  Arrays.asList(85,84,74,85,84,74);
			ytaille =  Arrays.asList(97,105,91,97,105,91); 
			
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(35,27,27,10,12,4);
			List<Integer> xd = Arrays.asList(74,71,69,49,56,46);
			List<Integer> yh = Arrays.asList(10,12,0,10,12,0);
			List<Integer> yb = Arrays.asList(96,98,86,96,98,86);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int start_index=0;int end_index=0;
			if(type_mouv.equals(TypeSaut.JumpGauche)){
				start_index=0;end_index=1;}
			else if(type_mouv.equals(TypeSaut.FallGauche)){
				start_index=1;end_index=2;}
			else if(type_mouv.equals(TypeSaut.LandGauche)){
				start_index=2;end_index=3;}
			else if(type_mouv.equals(TypeSaut.JumpDroite)){
				start_index=3;end_index=4;}
			else if(type_mouv.equals(TypeSaut.FallDroite)){
				start_index=4;end_index=5;}
			else if(type_mouv.equals(TypeSaut.LandDroite)){
				start_index=5;end_index=6;}
			animation.start(Arrays.asList(1,1,2,1,1,2), current_frame, start_index, end_index);

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
			//animation frame, current_frame, start_index, end_index
			int start_index=0;int end_index=0;
			if(type_mouv.equals(TypeSaut.JumpGauche)){
				start_index=0;end_index=1;}
			else if(type_mouv.equals(TypeSaut.FallGauche)){
				start_index=1;end_index=2;}
			else if(type_mouv.equals(TypeSaut.LandGauche)){
				start_index=2;end_index=3;}
			else if(type_mouv.equals(TypeSaut.JumpDroite)){
				start_index=3;end_index=4;}
			else if(type_mouv.equals(TypeSaut.FallDroite)){
				start_index=4;end_index=5;}
			else if(type_mouv.equals(TypeSaut.LandDroite)){
				start_index=5;end_index=6;}
			animation.start(Arrays.asList(5,5), current_frame, start_index, end_index);
		}
	}
	public Saut(Object obj,TypeMouv _type_mouv, int current_frame,Animation _animation){
		this(obj,_type_mouv,current_frame);
		animation = _animation;
	}
	@Override
	public int getMaxBoundingSquare(Object obj)
	{
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
			return 105;
		else if(TypeObject.isTypeOf(obj, TypeObject.SPIREL))
			return 75;
		else
			return 0;
	}
	public Mouvement Copy(Object obj) {
		return new Saut(obj,type_mouv,animation.getStartFrame(),animation);
	}
	
	@Override
	public void setSpeed(Collidable object, int anim) {
		
		Heros heros = null; 
		Vitesse vit = getSpeed(object,anim);
		
		//reset variables saut to ensure coherency
		if(TypeObject.isTypeOf(object, TypeObject.HEROS))
		{
			if (object instanceof Heros) 
				heros = (Heros) object;
			
			if(heros.debutSaut)
				heros.debutSaut=false;
			
			if(heros.sautGlisse)
				heros.sautGlisse=false;
			
			else if(heros.sautAccroche)
				heros.sautAccroche=false;
			
			else
			{

				if (heros.deplaceSautDroit  && !heros.last_colli_right && !heros.sautGlisse)
				{
					if(object.localVit.x<0)//change direction in air
						heros.runBeforeJump=false;
					//on attend que le joueur réappui sur la touche de direction pour redeplacer
					heros.deplaceSautDroit= false;
				}
				else if (heros.deplaceSautGauche && ! heros.last_colli_left  && !heros.sautGlisse)
				{
					if(object.localVit.x>0)//change direction in air
						heros.runBeforeJump=false;
					//on attend que le joueur réappui sur la touche de direction pour redeplacer
					heros.deplaceSautGauche= false;
				}
			}
		}

		if(vit==null)
			return;
		object.localVit.x=(vit.x);
		object.localVit.y=(vit.y);
	}
	@Override
	public Vitesse getSpeed(Collidable object, int anim) {

		if(TypeObject.isTypeOf(object, TypeObject.HEROS))
		{
			Heros heros = null; 
			if (object instanceof Heros) {
				heros = (Heros) object;
			}
			//permet de déplacer le héros sur le cote 
			final double vitMax = (object.localVit.x == 0) ? (8.0 / Config.ratio_fps()):  Math.abs(object.localVit.x) ; 
			final double varVit = (8.0 / Config.ratio_fps()) ; 
			//bit more complicated due to gravity, make sure that jump speed and gravity compensate the same way 
			//jump speed ~ 4.5 m/s = 7.5 wu / frame 
			final double vitSaut = (Config.i_ratio_fps()*(-11) + Config.i_ratio_fps()*(Config.i_ratio_fps()+1)/2 * Gravite.gravity_norm ); //-16 normalement 
			
			if(heros.sautGlisse)
				return new Vitesse((varVit * (heros.droite_gauche(anim).equals(Mouvement.GAUCHE) ? -1 : 1)),vitSaut);
			
			else if(heros.sautAccroche)
				return new Vitesse(object.localVit.x,vitSaut);
			
			else
			{
				if(heros.debutSaut) 
					return new Vitesse(object.localVit.x,vitSaut);
				else if(heros.finSaut || (anim == 2 || anim == 5))
					return new Vitesse(object.localVit.x,0);
				
				if (heros.deplaceSautDroit  && !heros.last_colli_right && !heros.sautGlisse)
				{
					if(object.localVit.x<(vitMax- varVit))
						return new Vitesse(object.localVit.x+varVit,object.localVit.y);
					else 
						return new Vitesse(vitMax,object.localVit.y);
				}
				else if (heros.deplaceSautGauche && ! heros.last_colli_left  && !heros.sautGlisse)
				{
					if(object.localVit.x>(-1*vitMax+ varVit))
						return new Vitesse(object.localVit.x-varVit,object.localVit.y);
					else 
						return new Vitesse(-1*vitMax,object.localVit.y);
				}
			}
		}
		else if(TypeObject.isTypeOf(object, TypeObject.SPIREL))
		{
			Spirel spirel=null;
			if(object instanceof Spirel)
				spirel=(Spirel) object;
			
			int xspeed=(int)(10.0 / Config.ratio_fps());//10000
			int yspeed=(int)(15.0 / Config.ratio_fps());//15000

			if(spirel.peutSauter)
				return new Vitesse(object.localVit.x,-1*yspeed);
			
			if(spirel.sautGauche && ! spirel.sautDroit)
				return new Vitesse(-1*xspeed,object.localVit.y);
			
			if(spirel.sautDroit && ! spirel.sautGauche)
				return new Vitesse(xspeed,object.localVit.y);
			
		}
		return null;
	}
	@Override
	public String droite_gauche(Object obj,int anim) {
		if(TypeObject.isTypeOf(obj, TypeObject.HEROS))
			if(anim<3)
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
