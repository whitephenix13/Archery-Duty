package deplacement_tir;

import deplacement.Mouvement;

public abstract class Mouvement_tir extends Mouvement{

	
	 public static String tir_normal= "T_normal";
	 
	 /*public static String tir_explosif= "T_explosif";
	 *public static String tir_vent= "T_vent"; 
	 * */
	

	
	@Override
	public boolean IsDeplacement(Mouvement m)
	{
		return (this.getClass().getName().equals(m.getClass().getName()));
	}
	
	@Override
	public boolean IsDeplacement(String s)
	{
		return this.getClass().getName().equals("deplacement_tir." + s);
	}
	

}
