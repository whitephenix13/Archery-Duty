package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;
import types.Hitbox;

public class Glissade extends Mouvement_perso
{
	//pour glissade: 61*74, 52*65, -1,-9 droite
	//pour glissade: 61*74, 52*65, -7,-9 gauche
	public List<Hitbox> leftSlideHitbox;
	public List<Hitbox> rightSlideHitbox;

	public Glissade() 
	{
		this(Mouvement_perso.heros);
	}
	//constructeur monstre
	public Glissade(String type) 
	{
		super();
		if(type==Mouvement_perso.heros)
		{
			xtaille =  Arrays.asList(49,49);
			ytaille =  Arrays.asList(89,89);

			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(9,0);
			List<Integer> xd = Arrays.asList(48,39);
			List<Integer> yh = Arrays.asList(5,5);
			List<Integer> yb = Arrays.asList(88,88);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
			/*		
			 *  boolean right= (i==1);
				boolean left = (i==-1);
				int decall_x_hit = 1; //need sqrt(2) to go out of the wall
				int xg=xpos +deplacement.xdecallsprite.get(anim) + (left ? -decall_x_hit : 0);
				int xd=xg+deplacement.xhitbox.get(anim)+ (right ? decall_x_hit : 0);
				int yh=ypos +deplacement.ydecallsprite.get(anim)+7;
				int yb=yh+36; 
			 * */
			
			List<List<Point>> hitboxCreation_l_slide = new ArrayList<List<Point>>();
			List<List<Point>> hitboxCreation_r_slide = new ArrayList<List<Point>>();

			//add for every edge, a list of point depending on the animation
			int decall_x_hit = 1;

			List<Integer> xg_l_slide = Arrays.asList(7-decall_x_hit,1-decall_x_hit);
			List<Integer> xd_l_slide = Arrays.asList(59-decall_x_hit,53-decall_x_hit);
			List<Integer> xg_r_slide = Arrays.asList(7+decall_x_hit,1+decall_x_hit);
			List<Integer> xd_r_slide = Arrays.asList(59+decall_x_hit,53+decall_x_hit);
			
			List<Integer> yh_slide = Arrays.asList(16,16);
			List<Integer> yb_slide = Arrays.asList(52,52);
			
			hitboxCreation_l_slide.add(asListPoint(xg_l_slide,yh_slide));
			hitboxCreation_l_slide.add(asListPoint(xd_l_slide,yh_slide));
			hitboxCreation_l_slide.add(asListPoint(xd_l_slide,yb_slide));
			hitboxCreation_l_slide.add(asListPoint(xg_l_slide,yb_slide));

			hitboxCreation_r_slide.add(asListPoint(xg_r_slide,yh_slide));
			hitboxCreation_r_slide.add(asListPoint(xd_r_slide,yh_slide));
			hitboxCreation_r_slide.add(asListPoint(xd_r_slide,yb_slide));
			hitboxCreation_r_slide.add(asListPoint(xg_r_slide,yb_slide));
			
			leftSlideHitbox= createHitbox(hitboxCreation_l_slide);
			rightSlideHitbox= createHitbox(hitboxCreation_r_slide);

		}
	}

	public Mouvement Copy(String type) {
		return new Glissade(type);
	}
	@Override
	public void setSpeed(String type, Collidable object, int anim,Deplace deplace) {
		if(type.equals(Mouvement_perso.heros))
		{
			//nothing
		}
		else if(type.equals(Mouvement_perso.m_spirel))
		{
			//nothing
		}
	}
}
