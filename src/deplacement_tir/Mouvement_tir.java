package deplacement_tir;

import deplacement.Mouvement;
import deplacement.TypeMouv;

public abstract class Mouvement_tir extends Mouvement{

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
		return this.getClass().getName().equals("deplacement_tir." + type.toString());
	}
	

}
