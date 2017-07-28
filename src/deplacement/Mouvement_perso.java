package deplacement;

public abstract class Mouvement_perso extends Mouvement{

	public static String attente= "Attente";
	public static String marche= "Marche";
	public static String course= "Course";
	public static String glissade= "Glissade";
	public static String accroche= "Accroche";
	public static String saut= "Saut";
	public static String tir= "Tir";
	
	@Override
	public boolean IsDeplacement(Mouvement m)
	{
		return (this.getClass().getName().equals(m.getClass().getName()));
	}
	@Override
	public boolean IsDeplacement(String s)
	{
		return this.getClass().getName().equals("deplacement." + s);
	}
	
	

}
