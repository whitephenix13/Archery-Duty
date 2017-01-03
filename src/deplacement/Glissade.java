package deplacement;

import java.util.Arrays;

public class Glissade extends Mouvement
{
//constructeur personnage
public Glissade() 
		{
	//pour glissade: 61*74, 52*65, -1,-9 droite
	//pour glissade: 61*74, 52*65, -7,-9 gauche
	super();
	xtaille =  Arrays.asList( 61,61,0,0,0,0,0,0);
	xhitbox =  Arrays.asList(52,52,0,0,0,0,0,0);
	xdecallsprite =  Arrays.asList(7,1,0,0,0,0,0,0);
	ytaille =  Arrays.asList(74,74,0,0,0,0,0,0);
	yhitbox =  Arrays.asList( 65,65,0,0,0,0,0,0);
	ydecallsprite =  Arrays.asList(9,9,0,0,0,0,0,0);
		}
//constructeur monstre
public Glissade(String typeMonstre) 
{
	super();
	if(typeMonstre=="")
		{
			xtaille =  Arrays.asList( 61,61,0,0,0,0,0,0);
			xhitbox =  Arrays.asList(52,52,0,0,0,0,0,0);
			xdecallsprite =  Arrays.asList(7,1,0,0,0,0,0,0);
			ytaille =  Arrays.asList(74,74,0,0,0,0,0,0);
			yhitbox =  Arrays.asList( 65,65,0,0,0,0,0,0);
			ydecallsprite =  Arrays.asList(9,9,0,0,0,0,0,0);
		}
}

public Mouvement Copy() {
	return new Glissade();
}
}
