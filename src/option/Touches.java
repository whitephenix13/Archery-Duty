package option;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class Touches {

	public final String ERROR = "-10";
	public final String RIGHT_MOUSE = "-1";	
	public final String LEFT_MOUSE = "-2";	
	public final String MIDDLE_MOUSE = "-3";	

	public String t_droite = "D";
	public String t_gauche = "Q";
	public String t_saut ="SPACE";
	public String t_dash = RIGHT_MOUSE;
	public String[] t_tir ={LEFT_MOUSE,"E","R","X","C"} ;
	public String t_slow="Z";
	public String t_pause= "ESCAPE";
	public String[] t_slot = {"1","2","3","4"};

	public LinkedHashMap<String,String> mapMouse = new LinkedHashMap<String,String>();
	
	
	public static <T> boolean inArray(T t, T[]arr)
	{
		for(int i=0; i<arr.length;++i)
			if(t.equals(arr[i]))
				return true;
		return false;
	}	
	public static <T> int indexOf(T t,T[] arr)
	{
		for(int i=0; i<arr.length;++i)
			if(t.equals(arr[i]))
				return i;
		return -1;
	}
	
	
	public String ToString(String _touche)
	{
		String[] touche_decomposed = _touche.split(" ");
		String res="";
		//shift | control | ctrl | meta | alt | altGraph
		if(touche_decomposed.length > 1)
		{
			if(touche_decomposed[0].equals("shift"))
				if(touche_decomposed[1].equals("SHIFT"))
					return "SHIFT";
				else
					return "SHIFT + "+ touche_decomposed[1];
			else if(touche_decomposed[0].equals("control"))
				if(touche_decomposed[1].equals("CONTROL"))
					return "CONTROLE";
				else
					return "CONTROLE + "+ touche_decomposed[1];
			else if(touche_decomposed[0].equals("ctrl"))
				if(touche_decomposed[1].equals("CONTROLE"))
					return "CONTROLE";
				else
					return "CONTROLE + "+ touche_decomposed[1];
			else if(touche_decomposed[0].equals("meta"))
				if(touche_decomposed[1].equals("META"))
					return "META";
				else
					return "META + "+ touche_decomposed[1];
			else if(touche_decomposed[0].equals("alt"))
				if(touche_decomposed[1].equals("ALT"))
					return "ALT";
				else
					return "ALT + "+ touche_decomposed[1];
			else if(touche_decomposed[0].equals("altGraph"))
				if(touche_decomposed[1].equals("ALT_GRAPH"))
					return "ALT GRAPH";
				else
					return "ALT GRAPH + "+ touche_decomposed[1];

		}
		if(touche_decomposed[0]=="UP")
			res+= "FLECHE HAUT";
		else if(touche_decomposed[0]=="DOWN")
			res+="FLECHE BAS";
		else if(touche_decomposed[0]=="RIGHT")
			res+="FLECHE DROIT";
		else if(touche_decomposed[0]=="LEFT")
			res+="FLECHE GAUCHE";
		else if(touche_decomposed[0]==RIGHT_MOUSE)
			res+="SOURIS DROITE";
		else if(touche_decomposed[0]==LEFT_MOUSE)
			res+="SOURIS GAUCHE";
		else if(touche_decomposed[0]==MIDDLE_MOUSE)
			res+="SOURIS MILIEU";
		else
			res+=touche_decomposed[0];
		return res;
	}


}
