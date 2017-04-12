package fleches;

import java.awt.Point;
import java.util.List;

import javax.vecmath.Vector2d;

import collision.Collidable;
import effects.Vent_effect;
import music.MusicBruitage;
import partie.AbstractModelPartie;
import types.Hitbox;
import types.Vitesse;

public class Fleche_vent extends Fleche{
	private double EJECT_SPEED=1400;//square of desired speed at distance < sqrt(EJECT_DISTANCE)
	private int EJECT_DISTANCE = 40000; // square of distance = 200^2 
	public Fleche_vent(List<Fleche> tabFleche, int current_frame)
	{
		super(tabFleche,current_frame);
		type_fleche=SPIRITUELLE.VENT;
		TEMPS_DESTRUCTION_FLECHE= (long) (2* Math.pow(10,8));//in nano sec = 0.2 sec 
		degat=0;
	}
	@Override
	protected void onPlanted(List<Collidable> objects, AbstractModelPartie partie)
	{
		for(Collidable obj : objects)
		{
			//get position of objects 
			Point _pos = new Point(obj.xpos,obj.ypos);
			if(obj.fixedWhenScreenMoves)
			{
				_pos.x-=partie.xScreendisp;_pos.y-=partie.yScreendisp;
			}
			double deltaX=_pos.x-xpos;
			double deltaY=_pos.y-ypos;
			double distance = deltaX*deltaX+deltaY*deltaY;
			//eliminate objects too far
			if(distance < 4*EJECT_DISTANCE)
			{
				//find where object is more precisely using the middle of the hitbox
				Hitbox obj_hit = obj.getHitbox(partie.INIT_RECT);
				if(obj.fixedWhenScreenMoves)
					obj_hit=Hitbox.minusPoint(obj.getHitbox(partie.INIT_RECT), new Point(partie.xScreendisp,partie.yScreendisp),false);
				Vector2d obj_left_up_hit = Hitbox.supportPoint(new Vector2d(-1,-1),obj_hit.polygon);
				Vector2d obj_right_down_hit = Hitbox.supportPoint(new Vector2d(1,1),obj_hit.polygon);
				double objXmiddle = (obj_left_up_hit.x + obj_right_down_hit.x)/2;
				double objYmiddle = (obj_left_up_hit.y + obj_right_down_hit.y)/2;

				Hitbox fleche_hit = this.getHitbox(partie.INIT_RECT);
				Vector2d fleche_left_up_hit = Hitbox.supportPoint(new Vector2d(-1,-1),fleche_hit.polygon);
				Vector2d fleche_right_down_hit = Hitbox.supportPoint(new Vector2d(1,1),fleche_hit.polygon);
				double flecheXmiddle = (fleche_left_up_hit.x + fleche_right_down_hit.x)/2;
				double flecheYmiddle = (fleche_left_up_hit.y + fleche_right_down_hit.y)/2;
				
				double deltaX2= (objXmiddle - flecheXmiddle);
				double deltaY2= (objYmiddle - flecheYmiddle);
				double distance2 = deltaX2*deltaX2+deltaY2*deltaY2;
				double x_vit=0;
				double y_vit=0;
				//calculate projected speed
				if(distance2<EJECT_DISTANCE)
				{
					double sqrt_eject_speed = Math.sqrt(EJECT_SPEED);
					double sqrt_eject_distance = Math.sqrt(EJECT_DISTANCE);
					double sqrt_distance2 = Math.sqrt(distance2);

					double normSpeed = sqrt_eject_speed * ((sqrt_eject_distance-sqrt_distance2) / sqrt_eject_distance);
					x_vit = deltaX2 * normSpeed/sqrt_distance2;
					y_vit = deltaY2 * normSpeed/sqrt_distance2;
				}
				//add projected speed to current speed
				if(x_vit != 0 || (y_vit != 0)){
					obj.envirVit=new Vitesse(x_vit,y_vit);
				}
			}
		}
		if(!generatedEffect){
			generatedEffect=true;
			new Vent_effect(partie,xpos,ypos,deplacement.xtaille.get(anim),deplacement.ytaille.get(anim)/2,rotation,0,partie.getFrame());
			MusicBruitage.me.startBruitage("vent_effect");
		}
	}
}
