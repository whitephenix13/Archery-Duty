package deplacement;

import collision.Collidable;
import partie.AbstractModelPartie;
import types.TypeObject;

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
	
	public boolean alignTestValid(Collidable object, Mouvement depSuiv, int animSuiv, AbstractModelPartie partie,Deplace deplace)
	{
		int prev_anim = object.anim;
		Mouvement prev_mouv = object.deplacement.Copy(TypeObject.heros);
		object.anim=animSuiv;
		object.deplacement=depSuiv;

		boolean valid = false;
		if(object.deplacement.IsDeplacement(Mouvement_perso.glissade))
			valid= !deplace.colli.isWorldCollision(partie, deplace, object,false);
		else
			valid= !deplace.colli.isWorldCollision(partie, deplace, object,true);
		object.anim=prev_anim;
		object.deplacement=prev_mouv;
		return valid;
	}
	public abstract Mouvement Copy(String type);

}
