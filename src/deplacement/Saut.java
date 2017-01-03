package deplacement;

import java.util.Arrays;
//il y a 3 animations de deux cotés 

public class Saut extends Mouvement{
	//Constructor personnage
	public Saut() {
		super();
		xtaille =  Arrays.asList(74,90,76,74,90,76,-1,-1);
		xhitbox =  Arrays.asList(32,32,32,32,32,32,-1,-1);
		xdecallsprite =  Arrays.asList(31,33,27,15,19,11,-1,-1);
		ytaille =  Arrays.asList(94,98,80,94,98,80,-1,-1); 
		yhitbox =  Arrays.asList(83,76,58,83,76,58,-1,-1);
		ydecallsprite =  Arrays.asList(11,22,22,11,22,22,-1,-1);
	}
	//constructeur monstre
	public Saut(String typeMonstre) {
		super();
		if(typeMonstre.equals("spirel"))
		{
		xtaille =  Arrays.asList(56,56,-1,-1,-1,-1,-1,-1);
		xhitbox =  Arrays.asList(56,56,-1,-1,-1,-1,-1,-1);
		xdecallsprite =  Arrays.asList(0 ,0 ,-1 ,-1 ,-1,-1,-1,-1);
		ytaille =  Arrays.asList(75,75,-1,-1,-1,-1,-1,-1);
		yhitbox =  Arrays.asList(75,75,-1,-1,-1,-1,-1,-1);
		ydecallsprite =  Arrays.asList(0 ,0 ,-1 ,-1 ,-1,-1,-1,-1);
		}
	}
	
	public Mouvement Copy() {
		return new Saut();
	}
}
