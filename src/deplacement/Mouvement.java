package deplacement;

import java.util.ArrayList;
import java.util.List;

public abstract class Mouvement{
	
	public List<Integer> xtaille= new ArrayList<Integer>(8) ;
	public List<Integer> ytaille= new ArrayList<Integer>(8) ;
	public List<Integer> xhitbox= new ArrayList<Integer>(8) ;
	public List<Integer> yhitbox= new ArrayList<Integer>(8) ;
	public List<Integer> xdecallsprite= new ArrayList<Integer>(8) ;
	public List<Integer> ydecallsprite= new ArrayList<Integer>(8) ;
	
	//deplacement pour heros
	public static String attente= "Attente";
	public static String marche= "Marche";
	public static String course= "Course";
	public static String glissade= "Glissade";
	public static String saut= "Saut";
	public static String tir= "Tir";
	
	public boolean IsDeplacement(String s)
	{
		return this.getClass().getName().equals("deplacement." + s);
	}
	public abstract Mouvement Copy();

}
