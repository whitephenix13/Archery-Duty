package deplacement;

import java.util.Arrays;

//il y a 1 animations de deux cotés 

public class Attente extends Mouvement{
	//constructeur du personnage
	public Attente(){
		super();
		xtaille =  Arrays.asList(70,70,-1,-1,-1,-1,-1,-1);
		xhitbox =  Arrays.asList(33,33,-1,-1,-1,-1,-1,-1);
		xdecallsprite =  Arrays.asList(21,16,-1,-1,-1,-1,-1,-1);
		ytaille =  Arrays.asList(94,94,-1,-1,-1,-1,-1,-1);
		yhitbox =  Arrays.asList(69,69,-1,-1,-1,-1,-1,-1);
		ydecallsprite =  Arrays.asList(25,25,-1,-1,-1,-1,-1,-1);
	}
	//constructeur des monstres 
	public Attente(String typeMonstre ){
		super();
		if(typeMonstre.equals("spirel"))
		{
		xtaille =  Arrays.asList(56,56,-1,-1,-1,-1,-1,-1);
		xhitbox =  Arrays.asList(56,56,-1,-1,-1,-1,-1,-1);
		xdecallsprite =  Arrays.asList(0 ,0 ,-1,-1,-1,-1,-1,-1);
		ytaille =  Arrays.asList(75,75,-1,-1,-1,-1,-1,-1);
		yhitbox =  Arrays.asList(75,75,-1,-1,-1,-1,-1,-1);
		ydecallsprite =  Arrays.asList(0 ,0 ,-1,-1,-1,-1,-1,-1);

		}
	}
	public Mouvement Copy() {
		return new Attente();
	}
}
