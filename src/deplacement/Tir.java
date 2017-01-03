package deplacement;

import java.util.Arrays;

public class Tir extends Mouvement
{
	//constructeur personnage
	public Tir() 
    {
		/* 0: H
		 * 1: HD
		 * 2: D
		 * 3: BD
		 * 4: B
		 * 5: BG
		 * 6: G 
		 * 7: HG
		 * */
		super();
		xtaille =  Arrays.asList(86,76,82,88,80,88,82,76);
		xhitbox =  Arrays.asList(32,32,34,36,36,36,36,34);
		xdecallsprite =  Arrays.asList(27,15,18,20,19,35,33,28);
		ytaille =  Arrays.asList(92,94,94,90,90,90,94,94);
		yhitbox =  Arrays.asList(82,82,86,80,76,84,84,84);
		ydecallsprite =  Arrays.asList(10,12,8 ,10,14,6 ,10,10);

		//position point haut gauche de la fleche avec x (vers la droit) et y (vers le bas) par rapport à l'anim correspondante
		//ces valeurs sont directement ajoutée dans deplace dans public void setParamFleche (Fleche fleche,Heros heros,Affichage affich)
		/* 0: H :  +40 -3
		 * 1: HD   +37 +10
		 * 2: D    +40 +29
		 * 3: BD   +45 +25
		 * 4: B	   +32 +61
		 * 5: BG   +19 +26
		 * 6: G    +10 +29
		 * 7: HG   +17 +16 
		 * */
	}
	//constructeur monstre
	public Tir(String typeMonstre) 
    {
		/* 0: H
		 * 1: HD
		 * 2: D
		 * 3: BD
		 * 4: B
		 * 5: BG
		 * 6: G 
		 * 7: HG
		 * */
		super();
		if(typeMonstre == "")
		{
			xtaille =  Arrays.asList(86,76,82,88,80,88,82,76);
			xhitbox =  Arrays.asList(32,32,34,36,36,36,36,34);
			xdecallsprite =  Arrays.asList(27,15,18,20,19,35,33,28);
			ytaille =  Arrays.asList(92,94,94,90,90,90,94,94);
			yhitbox =  Arrays.asList(82,82,86,80,76,84,84,84);
			ydecallsprite =  Arrays.asList(10,12,8 ,10,14,6 ,10,10);
		}
	
	}

	public Mouvement Copy() {
		return new Tir();
	}
}
