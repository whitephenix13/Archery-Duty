package partie.deplacement.projectile;

import java.awt.Point;
import java.util.Collections;

import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Electrique_effect;

public abstract class Mouvement_projectile extends Mouvement{

	public enum TypeTir implements TypeMouv {T_normal};
	 
	 /*public static String tir_explosif= "T_explosif";
	 *public static String tir_vent= "T_vent"; 
	 * */
	

	
	@Override
	public boolean IsDeplacement(Mouvement m)
	{
		return (this.getClass().getName().equals(m.getClass().getName()));
	}
	
	@Override
	public boolean IsDeplacement(TypeMouv type)
	{
		return this.getClass().getName().equals("partie.deplacement.projectile." + type.toString());
	}
	@Override
	public int getMaxBoundingSquare(Object obj)
	{
		return Math.max(Collections.max(xtaille), Collections.max(ytaille));
	}
	@Override
	public Point getMaxBoundingRect(Object obj)
	{
		return new Point(Collections.max(xtaille), Collections.max(ytaille));
	}

}
