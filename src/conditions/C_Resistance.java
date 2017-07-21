package conditions;

import partie.PartieTimer;

public class C_Resistance extends Condition{
	/*
	 * Reduce damage received
	 * */

	public C_Resistance()
	{
		name=RESISTANCE;
		DUREE= 30*Math.pow(10, 9); //sec 
		FACTOR= 0.5; //speed x0.5  
		startTime=PartieTimer.me.getElapsedNano();
	}
}
