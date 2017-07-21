package conditions;

import partie.PartieTimer;

public class C_Vitesse extends Condition{
	/*
	 * Slow down the motion of the Collidable
	 * */

	public C_Vitesse()
	{
		name=VITESSE;
		DUREE= 50*Math.pow(10, 9); //sec 
		FACTOR= 2; //speed x2 
		startTime=PartieTimer.me.getElapsedNano();
	}
}
