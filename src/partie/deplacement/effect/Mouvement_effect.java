package partie.deplacement.effect;

import java.awt.Point;
import java.util.Collections;

import partie.collision.Collidable;
import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;
import partie.effects.Effect;
import utils.Vitesse;

public abstract class Mouvement_effect extends Mouvement{
	public static enum TypeMouvEffect implements TypeMouv {ElectriqueGround,ElectriqueEntitie,FeuGround,FeuEntitie,GlaceGround,GlaceEntitie,RocheGround,RocheEntitie,
		Explosive,Grappin,Lumiere,Ombre,Trou_noir,Vent};
	private String className = null; 
	protected Vitesse vit;

	@Override
	public boolean IsDeplacement(Mouvement m)
	{
		return (this.getClass().getName().equals(m.getClass().getName()));
	}
	@Override
	public boolean IsDeplacement(TypeMouv type)
	{
		if(className ==null )
			className = this.getClass().getName().replaceFirst("partie.deplacement.effect.", "");
		return className.equals(type.toString());
	}
	
	@Override
	public Vitesse __getUncheckedSpeed(Collidable object, int anim) {
		return vit;
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
	@Override
	public String droite_gauche(Object obj, int anim) {
		if( (((Effect)obj).getRotation() <= Math.PI/2) && (((Effect)obj).getRotation() >= 3*Math.PI/2) )
			return Mouvement.GAUCHE; 
		else
			return Mouvement.DROITE;
	}

}
