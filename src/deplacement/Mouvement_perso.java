package deplacement;

public abstract class Mouvement_perso extends Mouvement{

	public enum TypeMouvPerso implements TypeMouv {Attente,Marche,Course,Glissade,Accroche,Saut,Tir};
	
	@Override
	public boolean IsDeplacement(Mouvement m)
	{
		return (this.getClass().getName().equals(m.getClass().getName()));
	}
	@Override
	public boolean IsDeplacement(TypeMouv type)
	{
		return this.getClass().getName().equals("deplacement." + type.toString());
	}
	
	

}
