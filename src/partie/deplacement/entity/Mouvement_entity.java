package partie.deplacement.entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import partie.deplacement.Mouvement;
import partie.deplacement.TypeMouv;

public abstract class Mouvement_entity extends Mouvement{

	public static enum TypeMouvEntitie implements TypeMouv {Attente,Marche,Course,Glissade,Accroche,Saut,Tir};
	public List<Integer> x_center_tir = new ArrayList<Integer>();
	public List<Integer> y_center_tir = new ArrayList<Integer>();

	public List<Integer> x_rot_pos = new ArrayList<Integer>();
	public List<Integer> y_rot_pos = new ArrayList<Integer>();
	private String className = null; 
	
	protected Mouvement_entity()
	{
		
	}

	@Override
	public boolean IsDeplacement(Mouvement m)
	{
		return (this.getClass().getName().equals(m.getClass().getName()));
	}
	@Override
	public boolean IsDeplacement(TypeMouv type)
	{
		if(className ==null )
			className = this.getClass().getName().replaceFirst("partie.deplacement.entity.", "");
		return className.equals(type.toString());
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
