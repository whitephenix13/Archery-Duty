package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.Hitbox;
import types.TypeObject;

public class Tir extends Mouvement_perso
{
	public static int tir = 0;

	public Tir(String type, int _type_mouv,int current_frame) 
    {
		super();
		type_mouv=_type_mouv;
		if(type.equals(TypeObject.heros))
		{
			xtaille =  Arrays.asList(50,63,75,75,63,50,57,75,75,57);
			ytaille =  Arrays.asList(105,97,86,86,97,105,112,101,101,112);
			
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(11,11,26,28,32,18,25,16,38,11);
			List<Integer> xd = Arrays.asList(31,31,46,48,52,38,45,36,58,31);
			List<Integer> yh = Arrays.asList(19,11,0 ,0 ,11,19,27,15,15,27);
			List<Integer> yb = Arrays.asList(102,94,83,83,94,102,110,98,98,110);

			x_rot_pos =  Arrays.asList(23,12,38,40,43,27,40,32,45,19);
			y_rot_pos =  Arrays.asList(47,44,30,30,44,47,58,45,45,58);

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
	
	}
	public Tir(String type,int _type_mouv, int current_frame,Animation _animation){
		this(type,_type_mouv,current_frame);
		animation = _animation;
	}
	public Mouvement Copy(String type) {
		return new Tir(type,type_mouv,animation.getStartFrame(),animation);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim) {
		if(type.equals(TypeObject.heros))
		{
			//nothing
		}
		else if(type.equals(TypeObject.m_spirel))
		{
			//nothing
		}
	}
	@Override
	public String droite_gauche(String type,int anim) {
		if(type.equals(TypeObject.heros))
			if(anim>=3 && anim <= 7 )
				return (Mouvement.GAUCHE);
			else 
				return(Mouvement.DROITE);
		else{
			try {throw new Exception("String droite gauche: type unknown");} catch (Exception e) {e.printStackTrace();}
			return ("");
		}
	}
	@Override
	public int updateAnimation(String type,int anim,int current_frame,double speedFactor) {
		return animation.update(0,current_frame,speedFactor);
	}
}
