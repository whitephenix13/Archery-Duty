package partie.conditions;

import partie.modelPartie.PartieTimer;

public class C_Vitesse extends Condition{
	/*
	 * Slow down the motion of the Collidable
	 * */

	public C_Vitesse(double _duree)
	{
		name=VITESSE;
		DUREE= _duree*Math.pow(10, 9); //sec 
		FACTOR= 2; //speed x2 
		startTime=PartieTimer.me.getElapsedNano();
	}
}
