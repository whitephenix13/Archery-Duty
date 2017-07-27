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

public class Saut extends Mouvement_perso{

	public static int jump_gauche = 0;
	public static int fall_gauche = 1;
	public static int land_gauche = 2;
	public static int jump_droite = 3;
	public static int fall_droite = 4;
	public static int land_droite = 5;

	//constructeur
	public Saut(String type,int _type_mouv,int current_frame) {
		super();
		type_mouv=_type_mouv;
		if(type.equals(TypeObject.heros))
		{
			xtaille =  Arrays.asList(85,84,74,85,84,74);
			ytaille =  Arrays.asList(97,105,91,97,105,91); 
			
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(35,27,27,10,12,4);
			List<Integer> xd = Arrays.asList(74,71,69,49,56,46);
			List<Integer> yh = Arrays.asList(8,5,5,8,5,5);
			List<Integer> yb = Arrays.asList(96,98,86,96,98,86);

			hitboxCreation.add(Hitbox.asListPoint(xg,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yh));
			hitboxCreation.add(Hitbox.asListPoint(xd,yb));
			hitboxCreation.add(Hitbox.asListPoint(xg,yb));

			hitbox = Hitbox.createHitbox(hitboxCreation);
			//animation frame, current_frame, start_index, end_index
			int start_index=0;int end_index=0;
			if(type_mouv==jump_gauche){
				start_index=0;end_index=1;}
			else if(type_mouv==fall_gauche){
				start_index=1;end_index=2;}
			else if(type_mouv==land_gauche){
				start_index=2;end_index=3;}
			else if(type_mouv==jump_droite){
				start_index=3;end_index=4;}
			else if(type_mouv==fall_droite){
				start_index=4;end_index=5;}
			else if(type_mouv==land_droite){
				start_index=5;end_index=6;}
			animation.start(Arrays.asList(1,1,5,1,1,5), current_frame, start_index, end_index);

		}
		else if(type.equals(TypeObject.m_spirel))
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
			if(type_mouv==jump_gauche){
				start_index=0;end_index=1;}
			else if(type_mouv==fall_gauche){
				start_index=1;end_index=2;}
			else if(type_mouv==land_gauche){
				start_index=2;end_index=3;}
			else if(type_mouv==jump_droite){
				start_index=3;end_index=4;}
			else if(type_mouv==fall_droite){
				start_index=4;end_index=5;}
			else if(type_mouv==land_droite){
				start_index=5;end_index=6;}
			animation.start(Arrays.asList(5,5), current_frame, start_index, end_index);
		}
	}
	public Saut(String type,int _type_mouv, int current_frame,Animation _animation){
		this(type,_type_mouv,current_frame);
		animation = _animation;
	}
	public Mouvement Copy(String type) {
		return new Saut(type,type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim) {
		if(type.equals(TypeObject.heros))
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
			{
				object.localVit.x=(varVit * (heros.droite_gauche(anim).equals(Mouvement.GAUCHE) ? -1 : 1));
				heros.sautGlisse=false;
				object.localVit.y=(vitSaut);
			}
			else if(heros.sautAccroche)
			{
				heros.sautAccroche=false;
				object.localVit.y=(vitSaut);
			}
			else
			{
				if(heros.debutSaut) 
				{
					object.localVit.y=(vitSaut);
					heros.debutSaut =false;
				}
				else if(heros.finSaut)
				{
					object.localVit.y=(0);
				}
				if (heros.deplaceSautDroit  && !heros.last_colli_right && !heros.sautGlisse)
				{
					if(object.localVit.x<0)//change direction in air
						heros.runBeforeJump=false;

					if(object.localVit.x<(vitMax- varVit))
						object.localVit.x+=(varVit);
					else 
						object.localVit.x=(vitMax);

					//on attend que le joueur réappui sur la touche de direction pour redeplacer
					heros.deplaceSautDroit= false;
					return;
				}
				if (heros.deplaceSautGauche && ! heros.last_colli_left  && !heros.sautGlisse)
				{
					if(object.localVit.x>0)//change direction in air
						heros.runBeforeJump=false;

					if(object.localVit.x>(-1*vitMax+ varVit))
						object.localVit.x-=(varVit);
					else 
						object.localVit.x=(-1*vitMax);

					//on attend que le joueur réappui sur la touche de direction pour redeplacer
					heros.deplaceSautGauche= false;
					return;
				}
			}
		}
		else if(type.equals(TypeObject.m_spirel))
		{
			Spirel spirel=null;
			if(object instanceof Spirel)
				spirel=(Spirel) object;
			
			int xspeed=(int)(10.0 / Config.ratio_fps());//10000
			int yspeed=(int)(15.0 / Config.ratio_fps());//15000

			if(spirel.peutSauter)
			{
				object.localVit.y=(-1*yspeed);
			}
			if(spirel.sautGauche && ! spirel.sautDroit)
			{
				object.localVit.x=(-1*xspeed);
			}
			if(spirel.sautDroit && ! spirel.sautGauche)
			{
				object.localVit.x=(xspeed);
			}
		}
	}
	@Override
	public String droite_gauche(String type,int anim) {
		if(type.equals(TypeObject.heros))
			if(anim<3)
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else if(type.equals(TypeObject.m_spirel))
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
