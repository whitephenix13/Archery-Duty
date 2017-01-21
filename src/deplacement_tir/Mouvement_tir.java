package deplacement_tir;

import deplacement.Mouvement;
import deplacement.Mouvement_perso;

public abstract class Mouvement_tir extends Mouvement{

	
	 public static String tir_normal= "T_normal";
	 
	 /*public static String tir_explosif= "T_explosif";
	 *public static String tir_vent= "T_vent"; 
	 * */
	
	//deplacement pour Tir
	public static String fleche= "fleche";//identifier
	//deplacement pour Tir Monstre
	public static String tir_spirel= "t_spirel";//identifier
	
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
	
	public abstract Mouvement Copy(String type);

}
