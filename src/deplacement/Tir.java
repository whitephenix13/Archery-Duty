package deplacement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import collision.Collidable;

public class Tir extends Mouvement_perso
{
	/* 0: H
	 * 1: HD
	 * 2: D
	 * 3: BD
	 * 4: B
	 * 5: BG
	 * 6: G 
	 * 7: HG
	 * */
	public Tir() 
    {
		this(Mouvement_perso.heros);
	}
	//constructeur monstre
	public Tir(String type) 
    {
		super();
		if(type.equals(Mouvement_perso.heros))
		{
			xtaille =  Arrays.asList(82,88,80,88,82,76,86,76);
			ytaille =  Arrays.asList(94,90,90,90,94,94,92,94);
			
			List<List<Point>> hitboxCreation = new ArrayList<List<Point>>();
			//add for every edge, a list of point depending on the animation
			List<Integer> xg = Arrays.asList(18,20,19,35,33,28,27,15);
			List<Integer> xd = Arrays.asList(52,56,55,71,69,62,59,47);
			List<Integer> yh = Arrays.asList(8 ,10,14,6 ,10,10,10,12);
			List<Integer> yb = Arrays.asList(94,90,90,90,94,94,92,94);

			hitboxCreation.add(asListPoint(xg,yh));
			hitboxCreation.add(asListPoint(xd,yh));
			hitboxCreation.add(asListPoint(xd,yb));
			hitboxCreation.add(asListPoint(xg,yb));

			hitbox = createHitbox(hitboxCreation);
		}
	
	}

	public Mouvement Copy(String type) {
		return new Tir(type);
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
