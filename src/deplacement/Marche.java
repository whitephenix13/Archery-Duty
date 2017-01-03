package deplacement;

import java.util.Arrays;
//il y a 4 animations de deux cotés 

public class Marche extends Mouvement{
//pour le sprite tourné vers la droite: xdecallsprite=xtaille - xdecallsprite(gauche)-xhitbox
	//constructeur personnage
public Marche()
{
	super();
	xtaille =  Arrays.asList(62,82,72,68,62,82,72,68);
	xhitbox =  Arrays.asList(26,37,30,37,26,37,30,37);
	xdecallsprite =  Arrays.asList(14,13,28,22,22,32,14,9);
	ytaille =  Arrays.asList(94,92,92,94,94,92,92,94);
	yhitbox =  Arrays.asList(74,71,72,72,74,71,72,72);
	ydecallsprite =  Arrays.asList(20,21,20,22,20,21,20,22);
}
//constructeur monstre
public Marche(String typeMonstre){
	super();
	if(typeMonstre.equals("spirel"))
	{
		xtaille =  Arrays.asList(56,56,56,56,-1,-1,-1,-1);
		xhitbox =  Arrays.asList(56,56,56,56,-1,-1,-1,-1);
		xdecallsprite =  Arrays.asList(0 ,0 ,0 ,0 ,-1,-1,-1,-1);
		ytaille =  Arrays.asList(75,75,75,75,-1,-1,-1,-1);
		yhitbox =  Arrays.asList(75,75,75,75,-1,-1,-1,-1);
		ydecallsprite =  Arrays.asList(0 ,0 ,0 ,0 ,-1,-1,-1,-1);
	}
}

public Mouvement Copy() {
	return new Marche();
}
}
