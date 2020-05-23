package partie.mouvement.entity;

import java.util.ArrayList;
import java.util.List;

import partie.mouvement.Mouvement;

public abstract class Mouvement_entity extends Mouvement{

	public static enum EntityTypeMouv implements TypeMouv {ATTENTE,MARCHE,COURSE,GLISSADE,ACCROCHE,SAUT,TIR};
	//protected MouvEntityEnum mouvType;
	public List<Integer> x_center_tir = new ArrayList<Integer>();
	public List<Integer> y_center_tir = new ArrayList<Integer>();

	public List<Integer> x_rot_pos = new ArrayList<Integer>();
	public List<Integer> y_rot_pos = new ArrayList<Integer>();
	private String className = null; 
	
	protected Mouvement_entity()
	{
		
	}


}
