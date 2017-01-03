package deplacement;

import java.util.Arrays;
//il y a 4 animations de deux cotés 

public class Course extends Mouvement{
	//constructeur personnage
	public Course(){
		super();
		xtaille =  Arrays.asList(64,100,74,108,64,100,74,108);
		xhitbox =  Arrays.asList(39,38,31,38,39,38,31,38);
		xdecallsprite =  Arrays.asList(10,30,10,16,15,32,33,52);
		ytaille =  Arrays.asList(78,78,76,72,78,78,76,72);
		yhitbox =  Arrays.asList(58,58,57,56,58,58,57,56);
		ydecallsprite =  Arrays.asList(20,20,19,16,20,20,19,16);
	}
	//constructeur monstre
	public Course(String typeMonstre){
		super();
		if(typeMonstre=="")
		{
		xtaille =  Arrays.asList(64,100,74,108,64,100,74,108);
		xhitbox =  Arrays.asList(39,38,31,38,39,38,31,38);
		xdecallsprite =  Arrays.asList(10,30,10,16,15,32,33,22);
		ytaille =  Arrays.asList(78,78,76,72,78,78,76,72);
		yhitbox =  Arrays.asList(58,58,57,56,58,58,57,56);
		ydecallsprite =  Arrays.asList(20,20,19,16,20,20,19,16);
		}
	}
	public Mouvement Copy() {
		return new Course();
	}
}
