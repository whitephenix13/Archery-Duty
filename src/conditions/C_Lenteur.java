package conditions;

import partie.PartieTimer;

public class C_Lenteur extends Condition{
	/*
	 * Slow down the motion of the Collidable
	 * */

	public C_Lenteur()
	{
		name=LENTEUR;
		DUREE= 30*Math.pow(10, 9); //sec 
		FACTOR= 0.5; //speed x0.5  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
